package memory

import chisel3._
import chisel3.util._


class Memorysp extends Module{
  
    val io  = IO(new Bundle{
        val blockio = new memory_io(MemParams.blockPortWidth,MemParams.blockAddrPortWidth)
        val wmask = Input(UInt(4.W))       
    })       
    val mem = Mem(MemParams.MemSizeInWord>>2,Vec(4,UInt(MemParams.MemWordWidth.W)))//SyncReadMem
    val out = Vec.fill(MemParams.BlockSize) {0.U(MemParams.MemWordWidth.W)}
            
    io.blockio.ready := false.B
    io.blockio.rdata := 0.U
    //block IO port
    when(io.blockio.en & io.blockio.valid)
    {      
//      when(io.blockio.valid === true.B)
//      {    
        val extraAddrWidth = log2Ceil(MemParams.BlockSize)
        val baseAddr = Cat(io.blockio.addr,0.U(extraAddrWidth.W))
        
        when(io.blockio.we === true.B)
        {
           val addr = io.blockio.addr
           val dv = Vec(io.blockio.wdata(32*1-1,32*0), 
                        io.blockio.wdata(32*2-1,32*1), 
                        io.blockio.wdata(32*3-1,32*2), 
                        io.blockio.wdata(32*4-1,32*3))
                         
           val dm = io.wmask.toBools
           mem.write(addr, dv, dm)                                     
           printf("Writting block dat\n")
           io.blockio.ready := true.B
        }.otherwise{
           
          val addr = io.blockio.addr
          val dv = Wire(Vec(4, UInt(32.W)))
          dv := mem.read(addr)
          io.blockio.rdata := dv.asUInt
          io.blockio.ready := true.B
        }        
//      }
    }
    
    when(reset.toBool())
    {
//      for(i <- 0 to 20)
//          mem.write(i.U,0.U)  
    }
    
}

object elaboratememorysp extends App{
  chisel3.Driver.execute(args,()=> new Memorysp)
}
