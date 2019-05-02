package intgCheck

import chisel3._
import chisel3.util._

import memory._

class umt_io extends Bundle{
  val io = new memory_io(MemParams.dataPortWidth,MemParams.dataaddrPortWidth+1)
  
  val done = Output(Bool())
  val err = Output(Bool())
  val currOutReady = Output(Bool())  

}

class UModuleTop extends Module{
   val io = IO(new umt_io)
  
   val mem= Module(new Memory2)
   val ps = Module(new upageswappergcm)
   
   ps.io.mainMemio <> io.io 
   ps.io.socMemio <> mem.io.dataio
   ps.io.memio <> mem.io.blockio
    
   io.done := ps.io.finished
   io.err := ps.io.err
   io.currOutReady := ps.io.currOutReady
}

object elaborateutop extends App{
  chisel3.Driver.execute(args,()=> new UModuleTop)
}