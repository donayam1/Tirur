package cpu

import chisel3._
import chisel3.util._

import common._

class Id_io extends Bundle{
  val inst    = Input(UInt(RvSpec.XLEN.W))
  val func    = Output(UInt(alu_spec.funcw.W))
  val op1_sel = Output(UInt(alu_spec.op1_selw.W))  //reg
  val op2_sel = Output(UInt(alu_spec.op2_selw.W))  //Imm,reg
  val wb_sel  = Output(UInt(alu_spec.wb.W))     
}

//class is_inst{
//  val yes= UInt(1,1)
//  val no = UInt(0,1)
//}
//object is_inst extends is_inst 


class Id extends Module{
  var io = IO(new Id_io)
  //io <> DontCare 
  
  var sigs = ListLookup(
             io.inst,
             List(alu_func.ADDI, alu_op1.reg, alu_op2.Imm, alu_wb.m),
             Array(
                RvInst.ADDI  -> List(alu_func.ADDI,  alu_op1.reg,  alu_op2.Imm, alu_wb.rd),
                RvInst.SLTI  -> List(alu_func.SLTI,  alu_op1.reg,  alu_op2.Imm, alu_wb.rd),
                RvInst.ADD   -> List(alu_func.ADD,   alu_op1.reg,  alu_op2.reg, alu_wb.rd)
             )
             )
  
  val func :: op1_sel :: op2_sel :: wb_sel :: Nil = sigs 
  
  io.func := func
  io.op1_sel := op1_sel
  io.op2_sel := op2_sel
  io.wb_sel  := wb_sel
  
}

