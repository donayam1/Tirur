package memory

import chisel3._
import chisel3.util._


class Memory2 extends Module{
  
    val io  = IO(new Bundle{
        val blockio = new memory_io(MemParams.blockPortWidth,MemParams.blockAddrPortWidth)
        val dataio = new memory_io(MemParams.dataPortWidth,MemParams.dataaddrPortWidth)
             
    })       
    val mem = SeqMem(MemParams.MemSizeInWord,Vec(4,UInt(MemParams.MemWordWidth.W)))//SyncReadMem
    val wmask = 1.U<<io.dataio.addr(1,0)          
    

    io.dataio.ready := false.B
    io.dataio.rdata := 0.U
    val wtosameblock = (io.blockio.en & io.blockio.valid & io.blockio.we) & 
                        (io.dataio.en & io.dataio.valid & io.dataio.we) &
                        ((io.dataio.addr>>2.U) === (io.blockio.addr))
    //block IO port
    when(io.dataio.en & io.dataio.valid)
    {   
        val addr = io.dataio.addr>>2.U           
        when(io.dataio.we === true.B &  ~wtosameblock) //protect concurrent write to the same block
        {
           
           val dv = Vec(io.dataio.wdata, 
                        io.dataio.wdata, 
                        io.dataio.wdata, 
                        io.dataio.wdata)
                         
           val dm = wmask.toBools
           mem.write(addr, dv, dm)                                     
           //printf("Writting normal dat %x\n",io.dataio.wdata)
           io.dataio.ready := true.B
        }.elsewhen(io.dataio.we === false.B){           
          val dv = Wire(Vec(4, UInt(32.W)))
          dv := mem.read(addr)
          io.dataio.rdata := dv(io.dataio.addr(1,0))
          io.dataio.ready := true.B
        }        
    }
    
        
    io.blockio.ready := false.B
    io.blockio.rdata := 0.U
    //block IO port
    when(io.blockio.en & io.blockio.valid)
    {         
        val extraAddrWidth = log2Ceil(MemParams.BlockSize)
        val baseAddr = Cat(io.blockio.addr,0.U(extraAddrWidth.W))
        
        when(io.blockio.we === true.B)
        {
           val addr = io.blockio.addr
           val dv = Vec(io.blockio.wdata(32*1-1,32*0), 
                        io.blockio.wdata(32*2-1,32*1), 
                        io.blockio.wdata(32*3-1,32*2), 
                        io.blockio.wdata(32*4-1,32*3))
                         
           val dm = "b1111".U
           mem.write(addr, dv, dm.toBools)                                     
           //printf("Writting block dat =%x\n",io.blockio.wdata)
           io.blockio.ready := true.B
        }.otherwise{
           
          val addr = io.blockio.addr
          val dv = Wire(Vec(4, UInt(32.W)))
          dv := mem.read(addr)
          io.blockio.rdata := dv.asUInt
          io.blockio.ready := true.B
        }        
    }
    
//    when(reset.toBool())
//    {
//      val dv2 = Vec(0.U,0.U,0.U,0.U)
//      val dm2 = "b1111".U
//      for(i <- 0 to (MemParams.MemSizeInWord)-1)
//      {
//          
//           mem.write(i.U, dv2, dm2.toBools) 
//      }
//    }    
}

object elaboratememory2 extends App{
  chisel3.Driver.execute(args,()=> new Memory2)
}
