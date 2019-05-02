package intgCheck

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}


class counterTestes(a:counter) extends PeekPokeTester(a)
{
  
    val countInit = "h4e696e652054776f"
    poke(a.io.reset,true.B)
    poke(a.io.init,countInit.U)
    step(1)
    expect(a.io.out,countInit.U)
    
    val ex2 = "h4e696e6520547770".U
    poke(a.io.reset,false.B)
    poke(a.io.get,true.B)
    step(1)   
    expect(a.io.out,ex2)
    
    val ex3 = "h4e696e6520547771".U
    step(1)
    expect(a.io.out,ex3)
       
}

class counterTester extends ChiselFlatSpec
{
   behavior of "Coutner "
   backends foreach {backend =>
     it should s"perform counting operation  $backend" in {
        Driver(() => new counter, backend)(a => new counterTestes(a)) should be (true)
     }     
   }
}
