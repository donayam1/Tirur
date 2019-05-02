package intgCheck

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}


class randomTestes(a:randomgen) extends PeekPokeTester(a)
{
    for(i <- 0 to 20)
    {
      poke(a.io.en,true.B)
      step(4)
      expect(a.io.done,true.B)
      //poke(a.io.en,false.B)
      
    }
}

class randomTester extends ChiselFlatSpec
{
   behavior of "Random"
   backends foreach {backend =>
     it should s"generate random numbers  $backend" in {
        Driver(() => new randomgen, backend)(a => new randomTestes(a)) should be (true)
     }     
   }
}
