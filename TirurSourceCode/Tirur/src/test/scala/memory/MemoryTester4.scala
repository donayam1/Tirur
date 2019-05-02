package memory

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

class MemoryTestes4(m:Memory4) extends PeekPokeTester(m)
{ 
  for( i <- 0 to 40)
  {
    poke(m.io.dataio.addr,i)
    poke(m.io.dataio.wdata,i)
    poke(m.io.dataio.valid,true.B)
    poke(m.io.dataio.we,true.B)
    poke(m.io.dataio.en,true.B)
    step(1)
//    expect(m.io.dataio.ready,true.B)
//    step(1)
//    poke(m.io.dataio.en,false.B)
//    poke(m.io.dataio.valid,false.B)   
//    step(1)
//    expect(m.io.dataio.ready,false.B)
  }
  
   for( i <- 0 to 40)   {
      poke(m.io.dataio.addr,i)
      poke(m.io.dataio.we,false.B)
      poke(m.io.dataio.en,true.B)
      poke(m.io.dataio.valid,true.B)
      step(1)  
      expect(m.io.dataio.rdata,i)
   }
  
    poke(m.io.dataio.en,false.B)
    poke(m.io.blockio.addr,0)
    poke(m.io.blockio.we,false.B)
    poke(m.io.blockio.en,true.B)
    poke(m.io.blockio.valid,true.B)
    step(1)  
    expect(m.io.blockio.rdata,"h00000003000000020000000100000000".U)
 
 
 
 
 
}

class MemoryTester4 extends ChiselFlatSpec{
  behavior of "Memory"
  backends foreach{ backend =>
    it should s"read and write to some address $backend" in{
      Driver(() => new Memory4,backend)(m => new MemoryTestes4(m))should be(true)
    }
    
  }
}