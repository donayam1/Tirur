package aes

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}


class uaesctrTestes(a:unrolledaesctr) extends PeekPokeTester(a)
{
  

    val pt = "h54776f204f6e65204e696e652054776f"
    val iv = "h00000000_00000000_00000000_00000001"
    val key= "h00000000_00000000_00000000_00000000"

    val out1 = "hc9593eeb51055417816733284b33235"
    
   
    poke(a.io.intVect,iv.U)
    poke(a.io.data,pt.U)
    poke(a.io.key,key.U)
    poke(a.io.dir,true.B)
    poke(a.io.en,true.B)    
    step(1)
    expect(a.io.out, out1.U)
    expect(a.io.newR,true.B)
    
    val iv1 = "h00000000_00000000_00000000_00000002"
    val out2 = "h57ffb5ee2fd8c6b2bd41acdc51e68917"
    
    
    //poke(a.io.reset,false.B)
    poke(a.io.intVect,iv1.U)
    poke(a.io.data,pt.U)
    poke(a.io.en,true.B)
    poke(a.io.dir,true.B)
    poke(a.io.key,key.U)
    step(1)
    expect(a.io.out, out2.U)
    expect(a.io.newR,true.B)
    

    
    val iv3 = "h180004000200080104008a004d002"
    val out3 = "h4ee723e0aba50c0ec4d881850823dc95"
    val key3 = "h1000080004000201000080004000200"
    
    val pt3 = 0
    //poke(a.io.reset,true.B)
    poke(a.io.intVect,iv3.U)
    poke(a.io.data,pt3.U)
    poke(a.io.en,true.B)
    poke(a.io.dir,true.B)
    poke(a.io.key,key3.U)
    step(1)
    expect(a.io.out, out3.U)
    expect(a.io.newR,true.B)
    
    
    val iv4 = "h0"
    val out4 = "h9b24e499d4621d9180dbe3492cbbe2fa"
    val key4 = "h1000080004000201000080004000200"
    
    val pt4 = 0
    //poke(a.io.reset,true.B)
    poke(a.io.intVect,iv4.U)
    poke(a.io.data,pt4.U)
    poke(a.io.en,true.B)
    poke(a.io.dir,true.B)
    poke(a.io.key,key4.U)
    step(1)
    expect(a.io.out, out4.U)
    expect(a.io.newR,true.B)
    
    
    
    val iv5 = "h180004000200080104008a004d003"
    val out5 = "h78734508ef757cea0ab93823b457a54a"
    val key5 = "h1000080004000201000080004000200"
    
    val pt5 = 0
    //poke(a.io.reset,true.B)
    poke(a.io.intVect,iv5.U)
    poke(a.io.data,pt5.U)
    poke(a.io.en,true.B)
    poke(a.io.dir,true.B)
    poke(a.io.key,key5.U)
    step(1)
    expect(a.io.out, out5.U)
    expect(a.io.newR,true.B)
    
    
    
    val iv6 = "h18000400020004008a004d0026802"
    val out6 = "hdec7fb9a8b6bce2f45ad0d8981e03821"
    val key6 = "h1000080004000201000080004000200"
    
    val pt6 = 0
    //poke(a.io.reset,true.B)
    poke(a.io.intVect,iv6.U)
    poke(a.io.data,pt6.U)
    poke(a.io.en,true.B)
    poke(a.io.dir,true.B)
    poke(a.io.key,key6.U)
    step(1)
    expect(a.io.out, out6.U)
    expect(a.io.newR,true.B)
    

    val iv7 = "h18000400020004008a004d0026803"
    val out7 = "he1a74b45987e2293c8cc1b9b5e82bb5"
    val key7 = "h1000080004000201000080004000200"
    
    val pt7 = "h180004000200080104008a004d002"
    //poke(a.io.reset,true.B)
    poke(a.io.intVect,iv7.U)
    poke(a.io.data,pt7.U)
    poke(a.io.en,true.B)
    poke(a.io.dir,true.B)
    poke(a.io.key,key7.U)
    step(1)
    expect(a.io.out, out7.U)
    expect(a.io.newR,true.B)    
    
    
    
    
}

class uaesctrTester extends ChiselFlatSpec
{
   behavior of "aescbc"
   backends foreach {backend =>
     it should s"perform Cipher Block Chaining  $backend" in {
        Driver(() => new unrolledaesctr, backend)(a => new uaesctrTestes(a)) should be (true)
     }     
   }
}