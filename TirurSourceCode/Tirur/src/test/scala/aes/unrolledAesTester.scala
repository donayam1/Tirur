package aes

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

class unrolledAesTestes(a:unrolledaes) extends PeekPokeTester(a)
{
  val pt ="h54776f204f6e65204e696e652054776f"
  val key= "h5468617473206d79204b756e67204675"
  val ept= "h29C3505F571420F6402299B31A02D73A"
  
  //encryption test
  poke(a.io.key,key.U)
  poke(a.io.en,true.B)
  poke(a.io.iData,pt.U)
  poke(a.io.ed,true.B)
  step(1)
  expect(a.io.done,true.B)
  expect(a.io.result,ept.U)
  
      
}

class unrolledAesTester extends ChiselFlatSpec{
  behavior of "Aes"
  backends foreach{ backend =>
    it should s"encript the input data" in {
      Driver(()=>new unrolledaes,backend)(a=> new unrolledAesTestes(a)) should be (true)
    } 
    
  }
}