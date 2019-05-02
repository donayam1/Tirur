
package cpu

import chisel3._
import common._

class RfileInput extends Bundle
{
    val rs1        = Input(UInt(RvSpec.RALEN.W))
    val rs2        = Input(UInt(RvSpec.RALEN.W))
    val rData1     = Output(UInt(RvSpec.XLEN.W))
    val rData2     = Output(UInt(RvSpec.XLEN.W))
    
    val wData  = Input(UInt(RvSpec.XLEN.W))
    val rd     = Input(UInt(RvSpec.RALEN.W))
    val we     = Input(Bool())  
}


class Rfile extends Module {
  val io = IO(new RfileInput)
   
  //generate 32 registers which are reset to zero on reset 
  val regmap = Reg(init = Vec(Seq.fill(RvSpec.XSIZE)(0.U(RvSpec.XLEN.W))))
  
  regmap(0) := 0.U(32.W)//X0 hardwired to 0
  
  when(io.we){
      regmap(io.rd) := io.wData 
  }
  
  io.rData1 := regmap(io.rs1)
  io.rData2 := regmap(io.rs2)
   
}