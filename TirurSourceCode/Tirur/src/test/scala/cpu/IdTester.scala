
package cpu


import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec,Driver,PeekPokeTester}

import common._

class IdTestes(id:Id) extends PeekPokeTester(id)
{
  
  poke(id.io.inst,"b11111111111111111000111110010011".U(32.W))
  step(1)
  expect(id.io.func,alu_func.ADDI)
  expect(id.io.op1_sel,alu_op1.reg)
  expect(id.io.op2_sel,alu_op2.Imm)
  
      
  
}


class IdTester extends ChiselFlatSpec{
  behavior of "Id"
  backends foreach {backend =>
    it should s"perform instration decoding $backend" in{
      Driver(() => new Id,backend)(b => new IdTestes(b)) should be (true)
    }
  }
}