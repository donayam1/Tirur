package intgCheck


import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

/*
 * All testes with this 
 * session key=1000080004000201000080004000200
 * iv=180004000200080104008a004d002
 * */
class UModuleTopPoVectorTestes(a:UModuleTop) extends PeekPokeTester(a)
{
  
   var clockCount=0;
   poke(a.io.io.en,false.B) //done configuration
   poke(a.io.io.valid,false.B) //config is no more valid
  
   step(22)
   clockCount+=22;
   
   poke(a.io.io.addr,8192.U) //Config0 number of blocks 
   poke(a.io.io.wdata,7.U)   //page out vectors
   poke(a.io.io.valid,true.B)
   poke(a.io.io.we,true.B)
   poke(a.io.io.en,true.B)
   step(1) //wait for the output register update
   clockCount+=1;
   
   poke(a.io.io.en,false.B) //done configuration
   poke(a.io.io.valid,false.B) //config is no more valid
   
   
   
   val reslts = Array(
      "h7872c508af755cea8aa9782b14537548".U,
      "h1f8ba36210d8ff03abad258b162a2e00".U,
      "h5ffbadfebae01b72dce61cef3e737549".U,
      "ha92f2439b0d58b557f0d824a9c861fdf".U,
      "h3421dbe72438bb80c2f8827b60fdfee0".U,
      "h51b27f7ea5f3ce449d80ba347605f982".U,
      "h7ee576b578117e2857803921138142e2".U,
      "h263e7acb82d1721e26fd02de075a22f5".U,
      "h50a046fd05882137fde692179229fb26".U,
      "h7f81c0dd4fdb31e7c9eaa3aa2d246914".U,
      "h50aee2734dc1f72dbcfde55736bc256a".U,
      "hd3053f39f80e0bc09119b631b973ff44".U,
      "h9401f2ef722f8a8aed895ee716dcd453".U,
      "h2a58941c7a042efbb2cf01184eea7952".U,
      "h79738f927340cec45524a0657496ab7f".U,
      "had194290f46c6d513b297b7f2cd68584".U,
      "h98661e7e3abeadd4a9697c38d7d9533c".U,
      "hba020fd1b96e0f05e586c2cc3075751b".U,
      "h2561726b920c2c93b4f84e2b3a80d845".U,
      "h7a16019e4693f159ad0c6b5538d91cd7".U,
      "h425979608f62cc0583a36f6c81e0ab4f".U,
      "hbd0b80f859842b7476aaa072ae3ec20d".U,
      "h68d0513c5a12abcfe642866862a2d55a".U,
      "h50b5b8c199c7810e44697f088846ab64".U,
      "hfe0066a4f8877af5ef31f59d14f07fd6".U,
      "h36eaf5e04da79ccc14d211691dd11f79".U,
      "ha165cbd460576bc6e12bb70eae57faac".U,
      "hc9f93f3cd92446c3f22d3a312848bb26".U,
      "h76ccd92263e291b7b6481dc7e4fde337".U,
      "hccb5973a521e156d6af74afad60fd38c".U,
      "hf0328d19cea41d83c23710495c2a4e35".U,
      "h9d4e1b8f14d0b0a51749f0942d43e6a5".U
   )
   
   var i=0;
   
   while(peek(a.io.done) == BigInt(0))
   {
     while(peek(a.io.currOutReady) == BigInt(0))
     {
       step(1)
       clockCount +=1;      
     }
     //step(1)
   
     //check for the result is available correctly
     //78734508 ef757cea 0ab93823 b457a54a
     poke(a.io.io.addr,8197.U) //Config0 
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,false.B)
     poke(a.io.io.en,true.B)
     step(1)
     expect(a.io.io.rdata,reslts(i)(31,0))
     expect(a.io.io.ready,true.B)
     
     poke(a.io.io.addr,8198.U) //Config0 
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,false.B)
     poke(a.io.io.en,true.B)
     step(1)
     expect(a.io.io.rdata,reslts(i)(63,32))
     expect(a.io.io.ready,true.B)
  
     poke(a.io.io.addr,8199.U) //Config0 
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,false.B)
     poke(a.io.io.en,true.B)
     step(1)
     expect(a.io.io.rdata,reslts(i)(95,64))
     expect(a.io.io.ready,true.B)
     
     poke(a.io.io.addr,8200.U) //Config0 
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,false.B)
     poke(a.io.io.en,true.B)
     step(1)
     expect(a.io.io.rdata,reslts(i)(127,96))
     expect(a.io.io.ready,true.B)
     step(1)
       
     i+=1;       
   }
   
   expect(a.io.done,true.B)
   expect(a.io.err,false.B)
   expect(a.io.currOutReady,false.B)
     
}

class UModuleTopPoVectorTester extends ChiselFlatSpec
{
   behavior of "Page Swapper"
   backends foreach {backend =>
     it should s"should page our memory areas  $backend" in {
        Driver(() => new UModuleTop, backend)(a => new UModuleTopPoVectorTestes(a)) should be (true)
     }     
   }
}
