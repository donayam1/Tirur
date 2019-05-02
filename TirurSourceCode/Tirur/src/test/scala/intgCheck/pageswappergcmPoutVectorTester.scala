package intgCheck


import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

/*
 * All testes with this 
 * session key=1000080004000201000080004000200
 * iv=180004000200080104008a004d002
 * page out vector files test
 * */
class pageswappergcmTestes2(a:pageswappergcm) extends PeekPokeTester(a)
{
  
   var clockCount=0;
   poke(a.io.mainMemio.en,false.B) //done configuration
   poke(a.io.mainMemio.valid,false.B) //config is no more valid
  
   step(22)
   clockCount+=22;
   
   poke(a.io.mainMemio.addr,8194.U) //Config2 number of blocks 
   poke(a.io.mainMemio.wdata,32.U)   //32blocks 
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1) //wait for the output register update
   clockCount+=1;  

   
   poke(a.io.mainMemio.addr,8192.U) //Config0 
   poke(a.io.mainMemio.wdata,7.U)   //page out vector file and start 
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 

   poke(a.io.mainMemio.en,false.B) //done configuration
   poke(a.io.mainMemio.valid,false.B) //config is no more valid
   
   
   step(1)
   clockCount+=1; 
   
   poke(a.io.memio.ready, false.B)
   step(1)
   clockCount+=1; 
   
 val reslts = Array(
      "he1a74b45987e2293c8cc1b9b5e82bb5".U,            
      "h4ace77bf1ebbf473f4c75c0081cd58cb".U,
      "he3f4d6aa0ff4cb05c3223bf0c38e492f".U,
      "h2437c2aecb28d55813295815658e5c5a".U,
      "ha6540fb73b64f69a1d39ba223b289cf9".U,
      "hb20d141078cb162080915071f784b700".U,      
      "h915df187a8ac1a6b66e3e0bbbe38a547".U,
      "had021c4b356bd6b23326f19f98c3a30a".U,
      "h6e13f8b5aaad2ac397ebc52635040c15".U,
      "h9197a6ab8f0d82a47f90deb53f257efa".U,
      "h1f041324b2f41d6d323d887d1c6037ac".U,
      "hf421f86758199d9e7b39348aa7ae51a5".U,
      "h249587817d103db22ec32ba66fbbc7ce".U,
      "hc38ea467439416e12a4ec6c66c4ad797".U,
      "h5f9606affb0a40b698522252cb361cae".U,
      "hc06bf371afb73b9799597b6d6d00679b".U,
      "hf25c12221d9560fef70fb015484e3f66".U,
      "hcdc5475df5ce1b94da4c26e88c1f9263".U,
      "h4e568a1ab357f046e78a1660e25981e7".U,
      "h23d8ff3be3cacd57a62223f749b207cd".U,
      "h8346768c16e460ccdacb6ac93b2cc39a".U,
      "hfd3f44d560f911ea9a9097e61fcb78aa".U,
      "h1c68364b0a848e91517cecff0e5c3851".U,
      "h6d1d37eba7f6ae335b7f28480a8b1cc9".U,
      "ha1311a1a352f5cba61cb2abc1e45a790".U,
      "h59dd1a8654a130ec669cd537c5ec714c".U,
      "hd8b8852b1cdcc5d691f6e8b1a30c1ea4".U,
      "h3981bed281819ebf796e92ab984fc629".U,
      "h47ca3805284afdb181a1ef377c24bff0".U,
      "he357b03d6868c4039a9505eb45ded69a".U,
      "h9bd146a8e75f322e0b2e073596a5f0fd".U,
      "hcdee1d0def559abc39f869c46c8393b7".U
     )   
   var i=0;
   step(11) //one encryption for IVTag generation
   clockCount+=11; 
   
   while(peek(a.io.finished) == BigInt(0))
   {
        
       while(peek(a.io.currOutReady) == BigInt(0))
       {
         step(1)
         clockCount +=1;
       }
       expect(a.io.currOutReady,true.B)
       
          
       //check for the result is available correctly
       //4ee723e0 aba50c0e c4d88185 0823dc95
       println("read at config 5")
       poke(a.io.mainMemio.addr,8197.U) //Config5 
       //poke(a.io.mainMemio.wdata,3.U)   //3 operation mode page out and start
       poke(a.io.mainMemio.valid,true.B)
       poke(a.io.mainMemio.we,false.B)
       poke(a.io.mainMemio.en,true.B)
       step(1)
       expect(a.io.mainMemio.rdata,reslts(i)(31,0))
       
       println("read at config 6")
       poke(a.io.mainMemio.addr,8198.U) //Config6 
       //poke(a.io.mainMemio.wdata,3.U)   //3 operation mode page out and start
       poke(a.io.mainMemio.valid,true.B)
       poke(a.io.mainMemio.we,false.B)
       poke(a.io.mainMemio.en,true.B)
       step(1)
       expect(a.io.mainMemio.rdata,reslts(i)(63,32))
    
       println("read at config 7")
       poke(a.io.mainMemio.addr,8199.U) //Config7 
       //poke(a.io.mainMemio.wdata,3.U)   //3 operation mode page out and start
       poke(a.io.mainMemio.valid,true.B)
       poke(a.io.mainMemio.we,false.B)
       poke(a.io.mainMemio.en,true.B)
       step(1)
       expect(a.io.mainMemio.rdata,reslts(i)(95,64))
       
       println("read at config 8")
       poke(a.io.mainMemio.addr,8200.U) //Config8 
       //poke(a.io.mainMemio.wdata,3.U)   //3 operation mode page out and start
       poke(a.io.mainMemio.valid,true.B)
       poke(a.io.mainMemio.we,false.B)
       poke(a.io.mainMemio.en,true.B)
       step(1)
       expect(a.io.mainMemio.rdata,reslts(i)(127,96))
         
       
       poke(a.io.mainMemio.en,false.B) //done configuration
       poke(a.io.mainMemio.valid,false.B) //config is no more valid   
       step(2)
                   

       if(i == 31) //this is for the last three clenaup clocks
         step(4)
       i+=1
   }
         
   println("clock count=%x".format(clockCount))
   

}

class psgcmTester2 extends ChiselFlatSpec
{
   behavior of "Page Swapper"
   backends foreach {backend =>
     it should s"should page our memory areas  $backend" in {
        Driver(() => new pageswappergcm, backend)(a => new pageswappergcmTestes2(a)) should be (true)
     }     
   }
}
