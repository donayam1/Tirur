package common

import chisel3._
import chisel3.util._

class RvSpec{
  val XLEN = 32
  val RALEN = 5
  val XSIZE = 32
}

object RvSpec extends RvSpec

class alu_spec{
  val funcw = 5
  val op2_selw = 2
  val op1_selw = 1
  val wb = 2
}

object alu_spec extends alu_spec

class alu_op1{
    val reg = 0.U(alu_spec.op1_selw.W)
}

object alu_op1 extends alu_op1

class alu_op2{
    val Imm = 0.U(alu_spec.op2_selw.W)
    val reg = 1.U(alu_spec.op2_selw.W)
    val pc  = 2.U(alu_spec.op2_selw.W)
}
object alu_op2 extends alu_op2 


class alu_wb{
  val rd = 0.U(alu_spec.wb.W)
  val pc = 1.U(alu_spec.wb.W)
  val m  = 2.U(alu_spec.wb.W)
}
object alu_wb extends alu_wb


class alu_func{
  
  //Integer Register-Immediate Instructions
  val ADDI               = 0.U(alu_spec.funcw.W) //BitPat("b?????????????????000?????0010011")
  val SLTI               = 1.U(alu_spec.funcw.W) //BitPat("b?????????????????010?????0010011")
  val SLTIU              = 2.U(alu_spec.funcw.W) //BitPat("b?????????????????011?????0010011")
  
  val ANDI               = 3.U(alu_spec.funcw.W) //BitPat("b?????????????????111?????0010011")
  val ORI                = 4.U(alu_spec.funcw.W) //BitPat("b?????????????????110?????0010011")
  val XORI               = 5.U(alu_spec.funcw.W) //BitPat("b?????????????????100?????0010011")
    
  val SLLI               = 6.U(alu_spec.funcw.W) //BitPat("b000000???????????001?????0010011")   
  val SRLI               = 7.U(alu_spec.funcw.W) //BitPat("b000000???????????101?????0010011")
  val SRAI               = 8.U(alu_spec.funcw.W) //BitPat("b010000???????????101?????0010011")  
  
  val LUI                = 10.U(alu_spec.funcw.W) //BitPat("b?????????????????????????0110111")
  val AUIPC              = 11.U(alu_spec.funcw.W) //BitPat("b?????????????????????????0010111")
  
  
  //Integer Register-Register Operations
  val ADD                = 12.U(alu_spec.funcw.W) //BitPat("b0000000??????????000?????0110011")
  val SLT                = 13.U(alu_spec.funcw.W) //BitPat("b0000000??????????010?????0110011")
  val SLTU               = 14.U(alu_spec.funcw.W) //BitPat("b0000000??????????011?????0110011")
      
  val AND                = 15.U(alu_spec.funcw.W) //BitPat("b0000000??????????111?????0110011")
  val OR                 = 16.U(alu_spec.funcw.W) //BitPat("b0000000??????????110?????0110011")
  val XOR                = 17.U(alu_spec.funcw.W) //BitPat("b0000000??????????100?????0110011")
  
  val SLL                = 18.U(alu_spec.funcw.W) //BitPat("b0000000??????????001?????0110011")
  val SRL                = 19.U(alu_spec.funcw.W) //BitPat("b0000000??????????101?????0110011")
  
  val SUB                = 20.U(alu_spec.funcw.W) //BitPat("b0100000??????????000?????0110011")
  val SRA                = 21.U(alu_spec.funcw.W) //BitPat("b0100000??????????101?????0110011")
  

}

object alu_func extends alu_func

//Generated from riscv-tools/riscv-opcods directory by running make 
class RvInst{
  
  val ADDI               = BitPat("b?????????????????000?????0010011")
  val SLTI               = BitPat("b?????????????????010?????0010011")
  val SLTIU              = BitPat("b?????????????????011?????0010011")
  
  val ANDI               = BitPat("b?????????????????111?????0010011")
  val ORI                = BitPat("b?????????????????110?????0010011")
  val XORI               = BitPat("b?????????????????100?????0010011")
    
  val SLLI               = BitPat("b000000???????????001?????0010011")   
  val SRLI               = BitPat("b000000???????????101?????0010011")
  val SRAI               = BitPat("b010000???????????101?????0010011")  
  
  val LUI                = BitPat("b?????????????????????????0110111")
  val AUIPC              = BitPat("b?????????????????????????0010111")
  
  //Integer Register-Register Operations

  val ADD                = BitPat("b0000000??????????000?????0110011")
  val SLT                = BitPat("b0000000??????????010?????0110011")
  val SLTU               = BitPat("b0000000??????????011?????0110011")
      
  val AND                = BitPat("b0000000??????????111?????0110011")
  val OR                 = BitPat("b0000000??????????110?????0110011")
  val XOR                = BitPat("b0000000??????????100?????0110011")
  
  val SLL                = BitPat("b0000000??????????001?????0110011")
  val SRL                = BitPat("b0000000??????????101?????0110011")
  
  val SUB                = BitPat("b0100000??????????000?????0110011")
  val SRA                = BitPat("b0100000??????????101?????0110011")
    

}

object RvInst extends RvInst
