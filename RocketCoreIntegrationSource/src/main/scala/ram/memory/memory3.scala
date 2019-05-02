package memory

import chisel3._
import chisel3.util._
import freechips.rocketchip.util._

class Memory3 extends Module{
  
    val io  = IO(new Bundle{
        val blockio = new memory_io(MemParams.blockPortWidth,MemParams.blockAddrPortWidth)
        val dataio = new memory_io(MemParams.dataPortWidth,MemParams.dataaddrPortWidth)
             
    })       
    val mem = SeqMem(MemParams.MemSizeInWord,Vec(4,UInt(MemParams.MemWordWidth.W)))//SyncReadMem
    
    val addr = Mux(io.dataio.en,io.dataio.addr>>2.U,
                   Mux(io.blockio.en,io.blockio.addr,0.U))
                   
    val wdatad = Vec.tabulate(4) { i => io.dataio.wdata(31, 0) } 
//     val wdatad = Vec(io.dataio.wdata, 
//                        io.dataio.wdata, 
//                        io.dataio.wdata, 
//                        io.dataio.wdata)
//    
    
    val wdatab = Vec.tabulate(4) { i => io.blockio.wdata(32*(i+1)-1, 32*i) }
    val vect0 = Vec.tabulate(4) { i => 0.U(32.W) } 
     
    val wdata =Mux(io.dataio.en,wdatad,
                   Mux(io.blockio.en,wdatab,vect0))
    
                   
    val wmaskd = 1.U<<io.dataio.addr(1,0)    
    val mb = "b1111".U
    val wmask = Mux(io.dataio.en,wmaskd,
                   Mux(io.blockio.en,mb,"b0".U))
    
    val wen = Mux(io.dataio.en,io.dataio.we,
                   Mux(io.blockio.en,io.blockio.en,false.B))  
                   
    val ren = Mux(io.dataio.en,!io.dataio.we,
                   Mux(io.blockio.en,!io.blockio.we,false.B))  
                   
                   

    printf("addr = %x,  wmask=%x\n",addr,wmask)
    printf("blockio.en = %x,  dataio.en=%x\n",io.blockio.en,io.dataio.en)
    printf("read en = %x\n",ren)
    when(wen)
    {                                                              
        mem.write(addr, wdata, wmask.toBools)                                            
    }
                      
    val dv = mem.readAndHold(addr,ren)
    
    printf("block data is %x\n",dv.asUInt)
    
    val bit = io.dataio.addr(1,0)
    io.dataio.rdata := Mux(io.dataio.en,dv(bit).asUInt,0.U)
    io.dataio.ready := io.dataio.en 
       
    
    io.blockio.rdata := Mux(io.blockio.en,dv.asUInt,0.U)
    io.blockio.ready := io.blockio.en && !io.dataio.en
    
                 
}

object elaboratememory3 extends App{
  chisel3.Driver.execute(args,()=> new Memory3)
}
