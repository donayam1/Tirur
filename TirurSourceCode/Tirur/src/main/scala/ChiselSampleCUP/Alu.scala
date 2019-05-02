package cpu

import chisel3._
import common._
import chisel3.util._

class AluInput extends Bundle{
  val alu_op1 = Input(UInt(RvSpec.XLEN.W))
  val alu_op2 = Input(UInt(RvSpec.XLEN.W))
  val alu_out = Output(UInt(RvSpec.XLEN.W))
  val alu_func= Input(UInt(alu_spec.funcw.W))
}

//sign extending for the immidiates is done outside this module 
//Cat(Fill(20,io.alu_op2(11)),io.alu_op2(11,0))
class Alu extends Module{
  val io = IO(new AluInput)
  
  val signExtendedIm =Cat(Fill(20,io.alu_op2(11)),io.alu_op2(11,0))
  val shiftValsI = io.alu_op2(4,0)
  
  val out = MuxLookup(io.alu_func,
      1.U(32.W),
     Array(               //             Cat(Fill(24,io.dmem.resp.data(7)), io.dmem.resp.data(7,0)),
          alu_func.ADDI -> ( io.alu_op1 + signExtendedIm ),// &  .asSInt
          alu_func.SLTI -> ( io.alu_op1.asSInt < signExtendedIm.asSInt ).asUInt,
          alu_func.SLTIU -> ( io.alu_op1 < signExtendedIm ).asUInt,
          
          alu_func.ANDI -> ( io.alu_op1 & signExtendedIm ).asUInt,
          alu_func.ORI -> ( io.alu_op1 | signExtendedIm ).asUInt,
          alu_func.XORI -> ( io.alu_op1 ^ signExtendedIm ).asUInt,
          
          alu_func.SLLI -> ( io.alu_op1 << shiftValsI ).asUInt,
          alu_func.SRLI -> ( io.alu_op1 >> shiftValsI ).asUInt,
          alu_func.SRAI -> ( io.alu_op1.asSInt >> shiftValsI ).asUInt,
          
          alu_func.LUI -> ( Cat(io.alu_op2(20,0),Fill(12,0.U)) ),
          alu_func.AUIPC -> ( io.alu_op1 + Cat(io.alu_op2(20,0),Fill(12,0.U)) ),
          
          
          alu_func.ADD -> ( io.alu_op1.asSInt + io.alu_op2.asSInt ).asUInt,
          alu_func.SLT -> ( io.alu_op1.asSInt < io.alu_op2.asSInt ).asUInt,
          alu_func.SLTU -> ( io.alu_op1 < io.alu_op2 ),
          
          alu_func.AND -> ( io.alu_op1 & io.alu_op2 ),
          alu_func.OR -> ( io.alu_op1 | io.alu_op2 ),
          alu_func.XOR -> ( io.alu_op1 ^ io.alu_op2 ),
          
          
          alu_func.SLL -> ( io.alu_op1 << shiftValsI ).asUInt,
          alu_func.SRL -> ( io.alu_op1 >> shiftValsI ).asUInt,
          
          
          alu_func.SUB -> ( io.alu_op1.asSInt - io.alu_op2.asSInt ).asUInt,
          alu_func.SRA -> ( io.alu_op1.asSInt >> shiftValsI ).asUInt
           
      )
      )(RvSpec.XLEN-1,0)
  

  io.alu_out := out 
  
}
