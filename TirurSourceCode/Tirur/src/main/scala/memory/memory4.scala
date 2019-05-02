package memory

import chisel3._
import chisel3.util._
import freechips.rocketchip.util._

class Memory4 extends Module{
  
    val io  = IO(new Bundle{
        val blockio = new memory_io(MemParams.blockPortWidth,MemParams.blockAddrPortWidth)
        val dataio = new memory_io(MemParams.dataPortWidth,MemParams.dataaddrPortWidth)
             
    })       
    val mem = SeqMem(MemParams.MemSizeInWord,Vec(4,UInt(8.W)))//SyncReadMem
    val wmask = "b1111".U         
      
    val wend = io.dataio.en && io.dataio.we 
    val rend = io.dataio.en && !io.dataio.we 
    
    
    val addr = io.dataio.addr  
    val datad = Vec.tabulate(4) { i => io.dataio.wdata(8*(i+1)-1, 8*i) }
    when(wend) //protect concurrent write to the same block
    {                            
       mem.write(addr, datad, wmask.toBools)                                     
    }           

    val dv = mem.readAndHold(addr,rend)
    io.dataio.rdata := dv.asUInt
    io.dataio.ready := io.dataio.en
                
    
    
    
    
    //block IO port    
    val wenb = io.blockio.en && io.blockio.we
    val renb = io.blockio.en && !io.blockio.we 
       
    val extraAddrWidth = log2Ceil(MemParams.BlockSize)
    val baseAddr = Cat(io.blockio.addr,0.U(extraAddrWidth.W))
    
    val datab =      Vec(Seq.fill(4)(0.U(32.W)))
    val addrb =      Vec(Seq.fill(4)(0.U(MemParams.dataaddrPortWidth.W)))
    //val dmb = "b1111".U
    
    when(wenb)
    {
      
      for(i <- 0 to 3)
      {
         addrb(i) := baseAddr+i.U
         datab(i) := io.blockio.wdata(32*(i+1)-1,32*i)                                         
         mem.write(addr(i), Vec.tabulate(4) { j => datab(i)(8*(j+1)-1, 8*j) }, wmask.toBools)
      }
       
    }
       
    val out  =  Vec(Seq.fill(4)(0.U(32.W)))

    for(i <- 0 to 3)
    {
        addrb(i) := baseAddr + i.U
    out(i) := mem.readAndHold(addrb(i), renb).asUInt
    }
    io.blockio.rdata := Cat(out(3),out(2),out(1),out(0))
    io.blockio.ready := io.blockio.en
             
          
}

object elaboratememory4 extends App{
  chisel3.Driver.execute(args,()=> new Memory4)
}
