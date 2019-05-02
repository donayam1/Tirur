package intgCheck

import chisel3._
import chisel3.util._

import memory._

/*
 * 
 * TODO. Properly set the configuration masks for each registers   
 * 
 * */


class mt_io extends Bundle{
  val io = new memory_io(MemParams.dataPortWidth,MemParams.dataaddrPortWidth+1)
  
  val done = Output(Bool())
  val err = Output(Bool())
  val currOutReady = Output(Bool())  

}

class ModuleTop extends Module{
   val io = IO(new mt_io)
  
   val mem= Module(new Memory4)
   val ps = Module(new pageswappergcm)
   
   ps.io.mainMemio <> io.io 
   ps.io.socMemio <> mem.io.dataio
   ps.io.memio <> mem.io.blockio
    
   io.done := ps.io.finished
   io.err := ps.io.err
   io.currOutReady := ps.io.currOutReady
}

object elaboratetop extends App{
  chisel3.Driver.execute(args,()=> new ModuleTop)
}