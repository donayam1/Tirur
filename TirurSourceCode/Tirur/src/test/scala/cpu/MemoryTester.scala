package cpu

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

class MemoryTestes(m:Memory) extends PeekPokeTester(m)
{
  poke(m.io.inst.add,10)
  poke(m.io.inst.wdata,200)
  poke(m.io.inst.we,true.B)
  poke(m.io.inst.en,true.B)
  step(1)
  expect(m.io.inst.ready,true.B)
  
  
  poke(m.io.inst.add,10)
  poke(m.io.inst.we,false.B)
  poke(m.io.inst.en,true.B)
  step(1)

  expect(m.io.inst.rdata,200)
  
  
}

class MemoryTester extends ChiselFlatSpec{
  behavior of "Memory"
  backends foreach{ backend =>
    it should s"read and write to some address $backend" in{
      Driver(() => new Memory(32,15),backend)(m => new MemoryTestes(m))should be(true)
    }
    
  }
}