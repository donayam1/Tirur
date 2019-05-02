package cpu

import chisel3._
import chisel3.util._

import common._

/*addr_width=15
data_width=32
**/
class memory_io(data_width:Int,addr_width:Int) extends Bundle{
  val add  = Input(UInt(addr_width.W)) // 32K memory 2^5 * 2^10 
  val wdata = Input(UInt(data_width.W))
  val we = Input(Bool())
  val en = Input(Bool())

  val rdata = Output(UInt(data_width.W))
  val ready = Output(Bool())
  
  override def cloneType = { new memory_io(data_width,addr_width).asInstanceOf[this.type] }

}



class Memory(data_width:Int,addr_width:Int) extends Module{
    val io = IO(
        new Bundle{
            val inst = new memory_io(data_width,addr_width)
            //val data = new memory_io
            val debug = new memory_io(data_width,addr_width)
          }
        )
    io <> DontCare
    
    val mem = SyncReadMem(32000,UInt(32.W))
    
    //instraction read and write
    io.inst.ready := false.B    
    when(io.inst.en && io.inst.we)
    {   
        mem.write(io.inst.add,io.inst.wdata)          
        io.inst.ready := true.B
    }.elsewhen(io.inst.en && ~io.inst.we)
    {
        io.inst.rdata := mem.read(io.inst.add)
        io.inst.ready := true.B
    }
    
    //debug read and write
    io.debug.ready := false.B    
    when(io.debug.en && io.debug.we)
    {   
        mem.write(io.debug.add,io.debug.wdata)          
        io.debug.ready := true.B
    }.elsewhen(io.debug.en && ~io.debug.we)
    {
        io.debug.rdata := mem.read(io.debug.add)
        io.debug.ready := true.B
    }
    
      
  
}