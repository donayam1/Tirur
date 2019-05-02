package intgCheck

import chisel3._
import chisel3.util._

import memory._

/*
 * 
 * TODO. Properly set the configuration masks for each registers   
 * 
 * */


//class mt_io extends Bundle{
//  val io = new memory_io(MemParams.dataPortWidth,MemParams.dataaddrPortWidth+1)
//  
//  val done = Output(Bool())
//  val err = Output(Bool())
//  val currOutReady = Output(Bool())  
//
//}

class ModuleTop2 extends Module{
   val io = IO(new mt_io)
  
   val ps = Module(new pageswappergcm2)
   
   ps.io.mainMemio <> io.io 
    
   io.done := ps.io.finished
   io.err := ps.io.err
   io.currOutReady := ps.io.currOutReady
}

object elaboratetop2 extends App{
  chisel3.Driver.execute(args,()=> new ModuleTop2)
}