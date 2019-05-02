package aes

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

class aesTestes(a:enc) extends PeekPokeTester(a)
{
  val pt ="h54776f204f6e65204e696e652054776f"
  val key= "h5468617473206d79204b756e67204675"
  val ept= "h29C3505F571420F6402299B31A02D73A"
  
  poke(a.io.en,true.B)
  poke(a.io.rkeyDone,true.B)
  poke(a.io.rkey,"h5468617473206d79204b756e67204675".U)
  poke(a.io.data,"h54776f204f6e65204e696e652054776f".U)
  step(1)
  expect(a.io.roundOut,0.U)
  expect(a.io.out,"h001f0e543c4e08596e221b0b4774311a".U)
  
  
  poke(a.io.rkey,"hE232FCF191129188B159E4E6D679A293".U)
  poke(a.io.sio.data2,"h63c0ab20eb2f30cb9f93af2ba092c7a2".U)
  step(1)
  expect(a.io.roundOut,1.U)
  expect(a.io.out,"h5847088b15b61cba59d4e2e8cd39dfce".U)
  
  
  poke(a.io.rkey,"h56082007c71ab18f76435569a03af7fa".U)
  poke(a.io.sio.data2,"h6aa0303d594e9cf4cb48989bbd129e8b".U)
  step(1)
  expect(a.io.roundOut,2.U)
  expect(a.io.out,"h43c6a9620e57c0c80908ebfe3df87f37".U)
  
  
  
  poke(a.io.rkey,"hd2600de7157abc686339e901c3031efb".U)
  poke(a.io.sio.data2,"h1ab4d3aaab5bbae80130e9bb2741d29a".U)
  step(1)
  expect(a.io.roundOut,3.U)
  expect(a.io.out,"h7876305470767d23993c375b4b3934f1".U)
  
  
  poke(a.io.rkey,"ha11202c9b468bea1d75157a01452495b".U)
  poke(a.io.sio.data2,"hbc3804205138ff26eeeb9a39b31218a1".U)
  step(1)
  expect(a.io.roundOut,4.U)
  expect(a.io.out,"hb1ca51ed08fc54e104b1c9d3e7b26c20".U)
  
  
  poke(a.io.rkey,"hb1293b3305418592d210d232c6429b69".U)
  poke(a.io.sio.data2,"hc874d15530b020f8f2c8dd66943750b7".U)
  step(1)
  expect(a.io.roundOut,5.U)
  expect(a.io.out,"h9b512068235f22f05d1cbd322f389156".U)
  
  
  
  poke(a.io.rkey,"hbd3dc287b87c47156a6c9527ac2e0e4e".U)
  poke(a.io.sio.data2,"h14d1b74526cf938c4c9c7a23150781b1".U)
  step(1)
  expect(a.io.roundOut,6.U)
  expect(a.io.out,"h149325778fa42be8c06024405e0f9275".U)
  
    
  poke(a.io.rkey,"hcc96ed1674eaaa031e863f24b2a8316a".U)
  poke(a.io.sio.data2,"hfadc3ff57349f19bbad0360958764f9d".U)
  step(1)
  expect(a.io.roundOut,7.U)
  expect(a.io.out,"h53398e5d430693f84f0a3b95855257bd".U)
  
  
  poke(a.io.rkey,"h8e51ef21fabb4522e43d7a0656954b6c".U)
  poke(a.io.sio.data2,"hed12194c1a6fdc418467e22a97005b7a".U)
  step(1)
  expect(a.io.roundOut,8.U)
  expect(a.io.out,"h66253c7470ce5aa8afd30f0aa3731354".U)
  
  
  poke(a.io.rkey,"hbfe2bf904559fab2a16480b4f7f1cbd8".U)
  poke(a.io.sio.data2,"h333feb92518bbec2796676670a8f7d20".U)
  step(1)
  expect(a.io.roundOut,9.U)
  expect(a.io.out,"h09668b78a2d19a65f0fce6c47b3b3089".U)
  
  
  poke(a.io.rkey,"h28fddef86da4244accc0a4fe3b316f26".U)
  poke(a.io.sio.data2,"h01333dbc3a3eb84d8cb08e1c21e204a7".U)
  step(1)
  expect(a.io.roundOut,10.U)
  expect(a.io.out,"h29c3505f571420f6402299b31a02d73a".U)
  expect(a.io.done,true.B)
//
  
//  
  //poke(a.io.sio.data2,"h".U)
  
  
}

class aesTester extends ChiselFlatSpec{
  behavior of "Aes"
  backends foreach{ backend =>
    it should s"encript the input data" in {
      Driver(()=>new enc,backend)(a=> new aesTestes(a)) should be (true)
    } 
    
  }
}