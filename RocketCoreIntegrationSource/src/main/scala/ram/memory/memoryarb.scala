package memory

import chisel3._
import chisel3.util._

class marb_io extends Bundle{
  val dataio = new memory_io(MemParams.dataPortWidth,MemParams.dataaddrPortWidth)
  val blockio = new memory_io(MemParams.blockPortWidth,MemParams.blockAddrPortWidth)
}

class memoryarb extends Module{
  val io = IO(new marb_io)
  
  val mem = Module(new Memorysp)
  
  val dataSelected = (io.dataio.valid & io.dataio.en)
  val blockSelected = (~dataSelected & io.blockio.valid & io.blockio.en) 
  printf("Block Selected = %x\n",blockSelected)
  printf("Data Selected = %x\n",dataSelected)
  mem.io.blockio.addr := Mux(dataSelected,(io.dataio.addr>>2.U),
                              Mux(blockSelected,io.blockio.addr,0.U))  
  
  mem.io.blockio.valid := Mux(dataSelected,io.dataio.valid,
                              Mux(blockSelected,io.blockio.valid,0.U))
                              
  mem.io.blockio.wdata := Mux(dataSelected,io.dataio.wdata,
                              Mux(blockSelected,io.blockio.wdata,0.U))                            
  mem.io.wmask := Mux(dataSelected,"b0001".U,
                              Mux(blockSelected,"b1111".U,0.U))
  
  mem.io.blockio.we := Mux(dataSelected,io.dataio.we,
                       Mux(blockSelected,io.blockio.we,false.B))

  mem.io.blockio.we := Mux(dataSelected,io.dataio.en,
                       Mux(blockSelected,io.blockio.en,false.B))                      
                                
  io.dataio.rdata := Mux(dataSelected,mem.io.blockio.rdata(31,0),0.U)                     
  io.dataio.ready := Mux(dataSelected,mem.io.blockio.ready,false.B) 
  
  io.blockio.rdata := Mux(blockSelected,mem.io.blockio.rdata,0.U)                     
  io.blockio.ready := Mux(blockSelected,mem.io.blockio.ready,false.B)
  
  
}