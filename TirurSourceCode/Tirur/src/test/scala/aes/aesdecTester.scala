package aes

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester,ChiselFlatSpec}

class fdecTestes(a:dec) extends PeekPokeTester(a)
{
    val pt ="h54776f204f6e65204e696e652054776f"    
    val ept= "h29c3505f571420f6402299b31a02d73a"
  
 
    poke(a.io.en,true.B)
    poke(a.io.rkeyDone,true.B)
    poke(a.io.rkey,"h28fddef86da4244accc0a4fe3b316f26".U)
    poke(a.io.data,ept.U)   
    step(1)
    expect(a.io.roundOut,10.U)
    expect(a.io.out,"h09668b78a2d19a65f0fce6c47b3b3089".U)
    
    
    step(1)
    poke(a.io.rkey,"hbfe2bf904559fab2a16480b4f7f1cbd8".U)
    expect(a.io.roundOut,9.U)
    expect(a.io.out,"h66253c7470ce5aa8afd30f0aa3731354".U)
    
    
    step(1)
    poke(a.io.rkey,"h8e51ef21fabb4522e43d7a0656954b6c".U)
    expect(a.io.roundOut,8.U)
    expect(a.io.out,"h53398e5d430693f84f0a3b95855257bd".U)
    
    
    
    step(1)
    poke(a.io.rkey,"hcc96ed1674eaaa031e863f24b2a8316a".U)
    expect(a.io.roundOut,7.U)
    expect(a.io.out,"h149325778fa42be8c06024405e0f9275".U)    
    
    
    
    step(1)
    poke(a.io.rkey,"hbd3dc287b87c47156a6c9527ac2e0e4e".U)
    expect(a.io.roundOut,6.U)
    expect(a.io.out,"h9b512068235f22f05d1cbd322f389156".U)    
    
    
    step(1)
    poke(a.io.rkey,"hb1293b3305418592d210d232c6429b69".U)
    expect(a.io.roundOut,5.U)
    expect(a.io.out,"hb1ca51ed08fc54e104b1c9d3e7b26c20".U)    
    
    
    step(1)
    poke(a.io.rkey,"ha11202c9b468bea1d75157a01452495b".U)
    expect(a.io.roundOut,4.U)
    expect(a.io.out,"h7876305470767d23993c375b4b3934f1".U)    
    

    step(1)
    poke(a.io.rkey,"hd2600de7157abc686339e901c3031efb".U)
    expect(a.io.roundOut,3.U)
    expect(a.io.out,"h43c6a9620e57c0c80908ebfe3df87f37".U)    
    
    
    
    step(1)
    poke(a.io.rkey,"h56082007c71ab18f76435569a03af7fa".U)
    expect(a.io.roundOut,2.U)
    expect(a.io.out,"h5847088b15b61cba59d4e2e8cd39dfce".U)    
    
    
    
    step(1)
    poke(a.io.rkey,"hE232FCF191129188B159E4E6D679A293".U)
    expect(a.io.roundOut,1.U)
    expect(a.io.out,"h001f0e543c4e08596e221b0b4774311a".U)    
    
    
      
    step(1)
    poke(a.io.rkey,"h5468617473206d79204b756e67204675".U)  
    expect(a.io.roundOut,0.U)
    expect(a.io.out,"h54776f204f6e65204e696e652054776f".U)
    expect(a.io.done,true.B)
    
    
    
}


class aesdecTester extends ChiselFlatSpec{
  behavior of "Aes_Dec"
  backends foreach{ backend =>
    it should s"decrypt string " in{
      Driver(()=>new dec,backend)(a => new fdecTestes(a)) should be (true)
    }   
  }
}