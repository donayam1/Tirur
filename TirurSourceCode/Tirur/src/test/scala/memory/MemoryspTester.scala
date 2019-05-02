package memory

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

class MemoryspTestes(m:Memorysp) extends PeekPokeTester(m)
{ 
     
  
    
    poke(m.io.blockio.addr,0.U)
    poke(m.io.blockio.wdata,"h25".U)
    poke(m.io.blockio.we,true.B)
    poke(m.io.blockio.en,true.B)
    poke(m.io.blockio.valid,true.B)
    poke(m.io.wmask,"b0001".U)
    step(1)  
    
    poke(m.io.blockio.addr,0)
    poke(m.io.blockio.we,false.B)
    poke(m.io.blockio.en,true.B)
    poke(m.io.blockio.valid,true.B)    
    step(1) 
   
    expect(m.io.blockio.rdata,"h00000000000000000000000000000025".U)
 
    
    step(1)
    poke(m.io.blockio.addr,0.U)
    poke(m.io.blockio.wdata,"h0c0b0a09_08070605_04030201".U)
    poke(m.io.blockio.we,true.B)
    poke(m.io.blockio.en,true.B)
    poke(m.io.blockio.valid,true.B)
    poke(m.io.wmask,"b1101".U)
    step(1)  
    
    poke(m.io.blockio.addr,0.U)
    poke(m.io.blockio.we,false.B)
    poke(m.io.blockio.en,true.B)
    poke(m.io.blockio.valid,true.B)    
    step(1) 
   
    expect(m.io.blockio.rdata,"h00000000_0c0b0a09_00000000_04030201".U)
 
 
    
 
 
 
 
}

class MemoryspTester extends ChiselFlatSpec{
  behavior of "Memory"
  backends foreach{ backend =>
    it should s"read and write to some address $backend" in{
      Driver(() => new Memorysp,backend)(m => new MemoryspTestes(m))should be(true)
    }
    
  }
}