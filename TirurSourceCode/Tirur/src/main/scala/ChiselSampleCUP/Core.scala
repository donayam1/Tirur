package cpu

import chisel3._
import chisel3.util._

import common._

class cpucore_io extends Bundle
{
  val mem = Flipped(new memory_io(32,15))
  //val dmem = new mem_debug
  val rfdeb = new rfdebug(32,6)
}

class CpuCore extends Module{
  
  val io=IO(new cpucore_io)
  
  io <> DontCare
  
  val pc = Reg(UInt(RvSpec.XLEN.W))
  //var regs = Module(new Rfile) 
  val alu = Module(new Alu)
  val id = Module(new Id)
  val regmap = Reg(init = Vec(Seq.fill(RvSpec.XSIZE)(0.U(RvSpec.XLEN.W))))  
  regmap(0) := 0.U(RvSpec.XLEN.W)//X0 hardwired to 0
  
  
  //instraction feach
  val pc_next = Wire(UInt(RvSpec.XLEN.W))
  pc_next := pc + 1.U(RvSpec.XLEN.W)
  
  io.mem.add := pc(14,0)
  io.mem.we := false.B
  io.mem.en := true.B
  
  //update pc to the next instraction
  when(io.mem.ready)
  {
    pc := pc_next 
  }
  val inst = Mux(io.mem.ready, io.mem.rdata,0.U(32.W))
  
  //instraction decore  
  id.io.inst := inst
  
  val rs1_addr = inst(19,15)
  val rs2_addr = inst(24,20)
  val rd_addr = inst(11,7)
  val imd = inst(31,20)
      
  //operand featch
  val op1 =MuxLookup(
        id.io.op1_sel,
        0.U(RvSpec.XLEN.W),
        Array(
          alu_op1.reg ->  regmap(rs1_addr)  
        )
      )
          
  val op2 =MuxLookup(
        id.io.op2_sel,
        0.U(RvSpec.XLEN.W),
        Array(
          alu_op2.reg ->  regmap(rs2_addr), 
          alu_op2.Imm ->  Cat(Fill(20,0.U(1.W)),imd)
        )
      )
  
      
  //execute  
  alu.io.alu_op1 := op1
  alu.io.alu_op2 := op2
  alu.io.alu_func := id.io.func

  val alu_out = alu.io.alu_out 

  //write back 
  switch(id.io.wb_sel)
  {
    is(alu_wb.rd)
    {
    
      regmap(rd_addr) := alu_out
    }
    is(alu_wb.pc)
    {
      pc := alu_out
    }
//    is(alu_wb.m)
//    {
//      
//    }
  }
  
  
  when(io.rfdeb.en && io.rfdeb.we)
  {       
     regmap(io.rfdeb.add) := io.rfdeb.wdata
     io.rfdeb.ready := true.B
     
  }.elsewhen(io.rfdeb.en && ~io.rfdeb.we)
  {
    
     io.rfdeb.rdata := Mux(
         io.rfdeb.add === "h3F".U(6.W),
         pc,
         regmap(io.rfdeb.add(4,0)))
     io.rfdeb.ready := true.B
  }
  
  
  
  when(reset.toBool())
  {
    pc := 0.U(RvSpec.XLEN.W)
  }
  
  
}


object elaboratecore extends App {
  chisel3.Driver.execute(args, () => new CpuCore)
}