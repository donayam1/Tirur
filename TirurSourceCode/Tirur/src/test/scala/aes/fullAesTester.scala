package aes

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

class fullAesTestes(a:aes) extends PeekPokeTester(a)
{
  val pt ="h54776f204f6e65204e696e652054776f"
  val key= "h5468617473206d79204b756e67204675"
  val ept= "h29C3505F571420F6402299B31A02D73A"
  
  //encryption test
  poke(a.io.key,key.U)
  poke(a.io.en,true.B)
  poke(a.io.iData,pt.U)
  poke(a.io.ed,true.B)
  step(11)
  expect(a.io.done,true.B)
  expect(a.io.result,ept.U)
  
  
  poke(a.io.key,key.U)
  poke(a.io.en,true.B)
  poke(a.io.iData,pt.U)
  poke(a.io.ed,true.B)
  step(11)
  expect(a.io.done,true.B)
  expect(a.io.result,ept.U)
  
  
  //decryption test, round keys already generated by the encription process
  poke(a.io.key,key.U)
  poke(a.io.en,true.B)
  poke(a.io.iData,ept.U)
  poke(a.io.ed,false.B)
  step(11)
  expect(a.io.done,true.B)
  expect(a.io.result,pt.U)
  
  
  //key change logic test
  val nres = "h2C936BA3DDFFDDF452D4E407570A5388"
  val nk= "h5468617470206d79204b756e67204675"
  
  poke(a.io.key,nk.U)
  poke(a.io.en,true.B)
  poke(a.io.iData,ept.U)
  poke(a.io.ed,false.B)
  step(24)
  expect(a.io.done,true.B)
  expect(a.io.result,nres.U)
  
  
  
}

class fullAesTester extends ChiselFlatSpec{
  behavior of "Aes"
  backends foreach{ backend =>
    it should s"encript the input data" in {
      Driver(()=>new aes,backend)(a=> new fullAesTestes(a)) should be (true)
    } 
    
  }
}