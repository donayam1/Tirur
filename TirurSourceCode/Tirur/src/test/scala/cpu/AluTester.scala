package cpu

import chisel3._

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

import common._

class AluTestes(a:Alu) extends PeekPokeTester(a){
  
  //ADDI tests
  poke(a.io.alu_op1,20)
  poke(a.io.alu_op2,2)
  poke(a.io.alu_func,alu_func.ADDI)
  
  val expected = 22
  step(1)
  expect(a.io.alu_out,expected)
  
  
  //2049 is 12bit signed equivalent in two's complement of -2047
  poke(a.io.alu_op1,20)
  poke(a.io.alu_op2,2049) //-2047
  poke(a.io.alu_func,alu_func.ADDI)
  
  val expected2 = "hFFFFF815".U(32.W)// 4294965269.asUInt(32.W)//
  step(1)

  expect(a.io.alu_out,expected2)

  
  //SLTI tests
  poke(a.io.alu_op1,20)
  poke(a.io.alu_op2,2)
  poke(a.io.alu_func,alu_func.SLTI)
  
  val expected3 = 0
  step(1)
  expect(a.io.alu_out,expected3)

  //-2<-1
  val in1 = "hFFFFFFFE".U(32.W)
  val in2 = "hFFFFFFFF".U(32.W)
  
  poke(a.io.alu_op1,in1)
  poke(a.io.alu_op2,in2)
  poke(a.io.alu_func,alu_func.SLTI)
  
  val expected4 = 1
  step(1)
  expect(a.io.alu_out,expected4)
  
  
  
  //SLTIU tests
  //-2<-1
  val in3 = "hFFFFFFFE".U(32.W)
  val in4 = "hFFFFFFFF".U(32.W)
  
  poke(a.io.alu_op1,in3)
  poke(a.io.alu_op2,in4)
  poke(a.io.alu_func,alu_func.SLTIU)
  
  val expected5 = 1
  step(1)
  expect(a.io.alu_out,expected5)
  
  val in5 = "hFFFFFFFE".U(32.W)
  poke(a.io.alu_op1,in5)
  poke(a.io.alu_op2,8)
  poke(a.io.alu_func,alu_func.SLTIU)
  
  val expected6 = 0
  step(1)
  expect(a.io.alu_out,expected6)
  
  

  poke(a.io.alu_op1,9)
  poke(a.io.alu_op2,8)
  poke(a.io.alu_func,alu_func.SLTIU)
  
  val expected7 = 0
  step(1)
  expect(a.io.alu_out,expected7)
  
  
  poke(a.io.alu_op1,10)
  poke(a.io.alu_op2,20)
  poke(a.io.alu_func,alu_func.SLTIU)
  
  val expected8 = 1
  step(1)
  expect(a.io.alu_out,expected8)
  
  
  //ANDI tests
  
  poke(a.io.alu_op1,1)
  poke(a.io.alu_op2,2)
  poke(a.io.alu_func,alu_func.ANDI)
  
  val expected9 = 0
  step(1)
  expect(a.io.alu_out,expected9)
  
  //ORI tests
  poke(a.io.alu_op1,1)
  poke(a.io.alu_op2,2)
  poke(a.io.alu_func,alu_func.ORI)
  
  val expected10 = 3
  step(1)
  expect(a.io.alu_out,expected10)
  
  //XORI tests
  poke(a.io.alu_op1,6)
  poke(a.io.alu_op2,5)
  poke(a.io.alu_func,alu_func.XORI)
  
  val expected11 = 3
  step(1)
  expect(a.io.alu_out,expected11)
  
  
  //SLLI tests
  poke(a.io.alu_op1,1)
  poke(a.io.alu_op2,5)
  poke(a.io.alu_func,alu_func.SLLI)
  
  val expected12 = 32
  step(1)
  expect(a.io.alu_out,expected12)
  
  //SRLI tests
  
  poke(a.io.alu_op1,32)
  poke(a.io.alu_op2,5)
  poke(a.io.alu_func,alu_func.SRLI)
  
  val expected13 = 1
  step(1)
  expect(a.io.alu_out,expected13)
  
  //SRAI tests
  val in10="hFFFE_FFFF".U(32.W)
  poke(a.io.alu_op1,in10)
  poke(a.io.alu_op2,4)
  poke(a.io.alu_func,alu_func.SRAI)
  
  val expected14 = "hFFFF_EFFF".U(32.W)
  step(1)
  expect(a.io.alu_out,expected14)
  
  
  //LUI tests
  val in11="h1FFEF0FF".U(32.W)
  poke(a.io.alu_op1,0)
  poke(a.io.alu_op2,in11)
  poke(a.io.alu_func,alu_func.LUI)
  
  val expected15 = "hEF0FF000".U(32.W)
  step(1)
  expect(a.io.alu_out,expected15)
 
  //AUIPC tests
  val in12="h1FFEF0FF".U(32.W)
  poke(a.io.alu_op1,1)
  poke(a.io.alu_op2,in12)
  poke(a.io.alu_func,alu_func.AUIPC)
  
  val expected16 = "hEF0FF001".U(32.W)
  step(1)
  expect(a.io.alu_out,expected16)
  
  //ADD tests
  val in13="hFFFF_FFFF".U(32.W)
  //println(" in13 is=" + in13)
  poke(a.io.alu_op1,in13)
  poke(a.io.alu_op2,3)
  poke(a.io.alu_func,alu_func.ADD)
  
  val expected17 = 2//"hFFFF_FFFF".U(32.W)
  step(1)
  expect(a.io.alu_out,expected17)
  
  val in14="hFFFF_FFFF".U(32.W)
  //println(" in13 is=" + in13)
  poke(a.io.alu_op1,in14)
  poke(a.io.alu_op2,in14)
  poke(a.io.alu_func,alu_func.ADD)
  
  val expected18 = "hFFFF_FFFE".U(32.W)
  step(1)
  expect(a.io.alu_out,expected18)
  
  
  //SLT test 
  val in15="hFFFF_FFFF".U(32.W)
  poke(a.io.alu_op1,in15)
  poke(a.io.alu_op2,2)
  poke(a.io.alu_func,alu_func.SLT)
  
  val expected19 = 1
  step(1)
  expect(a.io.alu_out,expected19)
  
  
  //SLTU test 
  val in16="hFFFF_FFFF".U(32.W)
  poke(a.io.alu_op1,in16)
  poke(a.io.alu_op2,2)
  poke(a.io.alu_func,alu_func.SLTU)
  
  val expected20 = 0
  step(1)
  expect(a.io.alu_out,expected20)
  
  
  //AND tests same as ANDI
  //OR tests same as ORI
  //XOR tests same as XORI
  
  //SLL tests same as SLLI
  //SRL tests same as SRLI
  
  //SUB tests  same as ADD??
  //SRA tests same as SRAI
  
  
} 

class AluTester extends ChiselFlatSpec {
  behavior of "Alu"
  backends foreach {backend =>
    it should s"perform artimatic and logic operation  $backend" in {
      Driver(() => new Alu, backend)(a => new AluTestes(a)) should be (true)
    }
  }
}