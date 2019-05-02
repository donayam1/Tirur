package aes

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}


class aesctrTestes(a:aesctr) extends PeekPokeTester(a)
{
  

    val pt = "h54776f204f6e65204e696e652054776f"
    val iv = "h00000000_00000000_00000000_00000001"
    val key= "h00000000_00000000_00000000_00000000"

    val out1 = "hc9593eeb51055417816733284b33235"
    //val out2 = "h3D4383B7601A64CB4A785B1D998ACABA"
    
   
    poke(a.io.intVect,iv.U)
    poke(a.io.data,pt.U)
    poke(a.io.key,key.U)
    //poke(a.io.reset,true.B)
    poke(a.io.dir,true.B)
    poke(a.io.en,true.B)    
    step(11)
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
    step(11)
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
    step(11)
    expect(a.io.out, out3.U)
    expect(a.io.newR,true.B)
  
    
    
}

class aesctrTester extends ChiselFlatSpec
{
   behavior of "aescbc"
   backends foreach {backend =>
     it should s"perform Cipher Block Chaining  $backend" in {
        Driver(() => new aesctr, backend)(a => new aesctrTestes(a)) should be (true)
     }     
   }
}