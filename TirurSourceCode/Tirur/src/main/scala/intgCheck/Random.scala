package intgCheck

import chisel3._
import chisel3.util._

class rnd_io extends Bundle{
   val out = Output(UInt(64.W))
   val en = Input(Bool())
   val done = Output(Bool())   
}

class randomgen extends Module{
  val io = IO(new rnd_io)
  val curReg = RegInit(0.U(64.W))
  val cnt = RegInit(0.U(2.W))
  val rst = RegInit(0.U(1.W))
  val don = RegInit(0.U(1.W))
  
  io.done := Mux(io.en,cnt === 0.U ,false.B)
  io.out := curReg
  
  cnt := Mux(io.en,cnt+1.U,0.U)
  
  
  when(io.en)
  {    
    val x = LFSR16(true.B)
    val rnc = (curReg<<16.U) | x 
    curReg := rnc
  }
 
}