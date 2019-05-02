package aes

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

class g128Testes(a:g128Multiplay) extends PeekPokeTester(a)
{
//  val pt = "h54776f204f6e65204e696e652054776f"
//  val key= "h5468617473206d79204b756e67204675"
//  val ept= "h29C3505F571420F6402299B31A02D73A"
//  
//  //val p1 = "b1".U(128.W)
//  val p2 = key// "h8000_0000_0000_0000_0000_0990_0000_0000"
//  poke(a.io.x,p2.U)
//  poke(a.io.y,pt.U)
//  step(1)
//  println("%032x x %032x = %032x".format(peek(pt.U),peek(p2.U),peek(a.io.out)))
//  
  
//  val h="h66e94bd4ef8a2c3b884cfa59ca342b2e"
//  val c="h0388dace60b6a392f328c2b971b2fef8"
  //val c="h58e2fccefa7e3061367f1d57a4e7455a"
  val c="hb83b533708bf535d0aa6e52980d53b78"
  val h="hbc5db047d525480b9ac87d7731994b82"
  val r="he2f2ec6e0e873e10d6b96006d4eea1e7"
  //poke(a.io.en,true.B)
  poke(a.io.x,h.U)
  poke(a.io.y,c.U)
  step(1)
  expect(a.io.out,r.U)
  println("%032x x %032x = %032x".format(peek(h.U),peek(c.U),peek(a.io.out)))
  
  
  val h2="hff000000000000000000000000000000"
  val r2="hbc5db047d525480b9ac87d7731994b82"

  poke(a.io.x,h2.U)
  poke(a.io.y,c.U)
  step(1)
  expect(a.io.out,r2.U)
  println("%032x x %032x = %032x\n".format(peek(h2.U),peek(c.U),peek(a.io.out)))
  
  val h3="h66e94bd4ef8a2c3b884cfa59ca342b2e"
  val c3="h0388dace60b6a392f328c2b971b2fe78"
  val r3="h5e2ec746917062882c85b0685353deb7"
    
  poke(a.io.x,h3.U)
  poke(a.io.y,c3.U)
  step(1)
  expect(a.io.out,r3.U)
  println("%032x x %032x = %032x\n".format(peek(h3.U),peek(c3.U),peek(a.io.out)))
  
  val h4="h66e94bd4ef8a2c3b884cfa59ca342b2e"
  val c4="h5e2ec746917062882c85b0685353de37"
  val r4="hf38cbb1ad69223dcc3457ae5b6b0f885"
  
  poke(a.io.x,h4.U)
  poke(a.io.y,c4.U)
  step(1)
  expect(a.io.out,r4.U)
  println("%032x x %032x = %032x\n".format(peek(h4.U),peek(c4.U),peek(a.io.out)))
  
  
  val h5="h9b24e499d4621d9180dbe3492cbbe2fa"
  val c5="h2c042a28a01b19ca44d056469403d225"
  val r5="h1eb2df666e8df81ea5e8678914827d18"
  
  poke(a.io.x,h5.U)
  poke(a.io.y,c5.U)
  step(1)
  expect(a.io.out,r5.U)
  println("%032x x %032x = %032x\n".format(peek(h5.U),peek(c5.U),peek(a.io.out)))
  
  val h6="h9b24e499d4621d9180dbe3492cbbe2fa"
       //  1eb2df666e8df81ea5e8678914827d18
       //"h4bfccc425fb69a23e5c44bee367e596f"
  val c6="h554e1324313b623d402c2c6722fc2477"
  val r6="hb236bf8959b2f170a1c417c98153c535"
  
  poke(a.io.x,h6.U)
  poke(a.io.y,c6.U)
  step(1)
  expect(a.io.out,r6.U)
  println("%032x x %032x = %032x\n".format(peek(h6.U),peek(c6.U),peek(a.io.out)))
  
  
  
  val h7="h9b24e499d4621d9180dbe3492cbbe2fa"
       //  0b8cc2def58e7e52928f728a1e270226
       //"hb236bf8959b2f170a1c417c98153c535"
  val c7="hb9ba7d57ac3c8f22334b65439f74c713"
  val r7="he5a930d73d18a78753b0ac151eda18af"
  
  poke(a.io.x,h7.U)
  poke(a.io.y,c7.U)
  step(1)
  expect(a.io.out,r7.U)
  println("%032x x %032x = %032x\n".format(peek(h7.U),peek(c7.U),peek(a.io.out)))
  
  val h8="h9b24e499d4621d9180dbe3492cbbe2fa"
       //  0b8cc2def58e7e52928f728a1e270226
       //"hb236bf8959b2f170a1c417c98153c535"
  val c8="he5a930d73d18a78753b0ac151eda192f"
  val r8="he3d5ddec6ebdb0353fcb68d524b1742d"
  
  poke(a.io.x,h8.U)
  poke(a.io.y,c8.U)
  step(1)
  expect(a.io.out,r8.U)
  println("%032x x %032x = %032x\n".format(peek(h8.U),peek(c8.U),peek(a.io.out)))
  
  

  
}

class g128Tester extends ChiselFlatSpec{
  behavior of "Aes"
  backends foreach{ backend =>
    it should s"encript the input data" in {
      Driver(()=>new g128Multiplay,backend)(a=> new g128Testes(a)) should be (true)
    } 
    
  }
}