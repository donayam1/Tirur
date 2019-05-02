package intgCheck

import chisel3._
import chisel3.util._

class counter_io extends Bundle{
  val get = Input(Bool())
  val out = Output(UInt(64.W))
  val reset = Input(Bool())
  val init = Input(UInt(64.W))
}

class counter extends Module{
  val io = IO(new counter_io)
  val cReg = Reg(UInt(64.W))
 // val out = Wire(UInt(64.W))
  
  
  
  val crp = cReg + 1.U
  var crp2 =  Mux(io.reset,io.init,Mux(io.get,crp,cReg))
  //cReg := out 
  //printf("counter get is =%x \n crp=%x\n",io.get,crp)
  //printf("cReg is =%x \n",cReg)
  
  io.out := crp2
  
  when(io.get || io.reset)
  {
    //printf("counter cReg Incremented \n")
    cReg := crp2
  }
  
//  when(io.reset)
//  {
//     cReg := crp2   
//  }      
}
