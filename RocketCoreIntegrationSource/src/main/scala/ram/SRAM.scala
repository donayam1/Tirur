// See LICENSE.SiFive for license details.
// See LICENSE.Berkeley for license details.

package ram

//import Chisel._
import chisel3._
import chisel3.util._
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.util._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.chip._

import memory._
import intgCheck._

/*class ModuleTop00 extends Module{
   val io = IO(new mt_io)
  
  
   val mem = SeqMem(MemParams.MemSizeInWord, Vec(4, Bits(width = 8)))

   val  memAddress = io.io.addr
    val wdata = Vec.tabulate(4) { i => io.io.wdata(8*(i+1)-1, 8*i) }
    val dm = "b1111".U
     
    // exactly this pattern is required to get a RWM memory
    when (io.io.we && io.io.en) {
      mem.write(memAddress, wdata, dm.toBools)
    }
    
    val ren = !io.io.we && io.io.en
    val rdata = mem.readAndHold(memAddress, ren)
    
    io.io.rdata := rdata.asUInt
    io.io.ready := Bool(true)
    

       
   
    
   io.done := Bool(false)
   io.err := Bool(false)
   io.currOutReady := Bool(false)
}

import memory._
import intgCheck._*/

class Tirur(address: AddressSet, executable: Boolean = true, beatBytes: Int = 4, name: Option[String] = None, errors: Seq[AddressSet] = Nil)(implicit p: Parameters) extends LazyModule
{
  private val resources =
    name.map(new SimpleDevice(_, Seq("sifive,sram0")).reg("mem")).getOrElse(new MemoryDevice().reg)

  val node = TLManagerNode(Seq(TLManagerPortParameters(
    Seq(TLManagerParameters(
      address            = List(address) ++ errors,
      resources          = resources,
      regionType         = RegionType.UNCACHED,
      executable         = executable,
      supportsGet        = TransferSizes(1, beatBytes),
      supportsPutPartial = TransferSizes(1, beatBytes),
      supportsPutFull    = TransferSizes(1, beatBytes),
      fifoId             = Some(0))), // requests are handled in order
    beatBytes  = beatBytes,
    minLatency = 1))) // no bypass needed for this device

  // We require the address range to include an entire beat (for the write mask)
  require ((address.mask & (beatBytes-1)) == beatBytes-1)

  lazy val module = new Implementation
    class Implementation extends LazyModuleImp(this) {
    val io = new Bundle {
      val in = node.bundleIn
    }

    def bigBits(x: BigInt, tail: List[Boolean] = List.empty[Boolean]): List[Boolean] =
      if (x == 0) tail.reverse else bigBits(x >> 1, ((x & 1) == 1) :: tail)
    val mask = bigBits(address.mask >> log2Ceil(beatBytes))

    val in = io.in(0)
    val edge = node.edgesIn(0)

    val addrBits = (mask zip edge.addr_hi(in.a.bits).toBools).filter(_._1).map(_._2)
    val a_legal = address.contains(in.a.bits.address)
    val memAddress = Cat(addrBits.reverse)
    //val mem = SeqMem(1 << addrBits.size, Vec(beatBytes, Bits(width = 8)))

    val d_full = RegInit(Bool(false))
    val d_read = Reg(Bool())
    val d_size = Reg(UInt())
    val d_source = Reg(UInt())
    val d_addr = Reg(UInt())
    val d_data = Wire(UInt())
    val d_legal = Reg(Bool())

    // Flow control
    when (in.d.fire()) { d_full := Bool(false) }
    when (in.a.fire()) { d_full := Bool(true)  }
    in.d.valid := d_full
    in.a.ready := in.d.ready || !d_full

    in.d.bits := edge.AccessAck(d_addr, UInt(0), d_source, d_size, !d_legal)
    // avoid data-bus Mux
    in.d.bits.data := d_data
    in.d.bits.opcode := Mux(d_read, TLMessages.AccessAckData, TLMessages.AccessAck)

    val read = in.a.bits.opcode === TLMessages.Get
    val rdata = Wire(Vec(beatBytes, Bits(width = 8)))
    val wdata = Vec.tabulate(beatBytes) { i => in.a.bits.data(8*(i+1)-1, 8*i) }
    d_data := Cat(rdata.reverse)
    when (in.a.fire()) {
      d_read   := read
      d_size   := in.a.bits.size
      d_source := in.a.bits.source
      d_addr   := edge.addr_lo(in.a.bits)
      d_legal  := a_legal
    }

    /*
     
    // exactly this pattern is required to get a RWM memory
    when (in.a.fire() && !read && a_legal) {
      mem.write(memAddress, wdata, in.a.bits.mask.toBools)
    }
    val ren = in.a.fire() && read
    rdata := mem.readAndHold(memAddress, ren)

    // Tie off unused channels
    in.b.valid := Bool(false)
    in.c.ready := Bool(true)
    in.e.ready := Bool(true)
    
    */
    
    
    val wright = Wire(Bool())
        wright := in.a.fire() && !read && a_legal
   
   val ren = in.a.fire() && read
        	
   //integrate connect tirur	
   val tirur = Module(new ModuleTop) 	
       tirur.io.io.addr := memAddress
       tirur.io.io.valid := wright || ren //in.a.valid
       //in.a.ready := tirur.io.ready 
       
       tirur.io.io.wdata := in.a.bits.data
       tirur.io.io.we := wright // !ren
       tirur.io.io.en := wright || ren 
       
       
       rdata := Vec.tabulate(beatBytes) { i => tirur.io.io.rdata(8*(i+1)-1, 8*i) }
       	

    
    
    // Tie off unused channelsconfigReg
    in.b.valid := Bool(false)
    in.c.ready := Bool(true)
    in.e.ready := Bool(true)
  }
}

trait HasPeripherySRAM extends HasSystemNetworks  {
  val Tirur_address = 0x52000000	//bootROMParams.address
  val Tirur_size    = MemParams.SOCAddressSize //0x3ff		//bootROMParams.size
  val msram = LazyModule(new Tirur(AddressSet(Tirur_address, Tirur_size),beatBytes=peripheryBusConfig.beatBytes,executable=false)(p)) 

  msram.node := TLFragmenter(peripheryBusConfig.beatBytes, cacheBlockBytes)(peripheryBus.node)
}

/** Coreplex will power-on running at 0x10040 (BootROM) */
trait HasPeripherySRAMModuleImp extends LazyMultiIOModuleImp {
  val outer: HasPeripherySRAM
  //outer.coreplex.module.io.resetVector := UInt(outer.bootrom_hang)
}

