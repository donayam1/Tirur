package intgCheck


import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

/*
 * All testes with this 
 * session key=feffe9928665731c 6d6a8f9467308308
 * iv=         cafebabefacedbad decaf88800000001
 * Note : before running this test set the counter init value to 1
 * 			  and random output to zero in each respective module 
 * */
class pageswappergcmTestesss2(a:pageswappergcm) extends PeekPokeTester(a)
{
//   //poke(a.io.gnv, true.B)
//   step(16) //module is ready after 16 clocks 
//      
//   step(2)//Init, generate IV, 
//   
//   //step(2)
//   val pt = "h54776f204f6e65204e696e652054776f"
//   poke(a.io.memio.rdata,pt.U)
//   poke(a.io.memio.ready, true.B)
//   step(2)//read memory bock
//      
//   step(80)//encrypt
//   
//   
   var clockCount=0;
   poke(a.io.mainMemio.en,false.B) //done configuration
   poke(a.io.mainMemio.valid,false.B) //config is no more valid
  
   step(22)
   clockCount+=22;
   
   poke(a.io.mainMemio.addr,8194.U) //Config2 number of blocks 
   poke(a.io.mainMemio.wdata,4.U)   //3blocks 
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1) //wait for the output register update
   clockCount+=1;  

   
   poke(a.io.mainMemio.addr,8192.U) //Config0 
   poke(a.io.mainMemio.wdata,3.U)   //3 operation mode page out and start
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
   
   while(peek(a.io.currOutReady) == BigInt(0))
   {
     step(1)
     clockCount +=1;
   }
   

   expect(a.io.currOutReady,true.B)
   step(1)
   clockCount+=1; 
   expect(a.io.currOutReady,false.B)
   
   
   step(1)
   //val pt = "h54776f204f6e65204e696e652054776f"
   val pt = "hd9313225f88406e5a55909c5aff5269a"
   poke(a.io.memio.rdata,pt.U)
   poke(a.io.memio.ready, true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.memio.ready, false.B)
   step(1)
   clockCount+=1; 
   
   
   while(peek(a.io.currOutReady) == BigInt(0))
   {
     step(1)
     clockCount +=1;
   }
   expect(a.io.currOutReady,true.B)   

   
   step(1)
   val pt2 = "h86a7a9531534f7da2e4c303d8a318a72"
   poke(a.io.memio.rdata,pt2.U)
   poke(a.io.memio.ready, true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.memio.ready, false.B)
   step(1)
   clockCount+=1; 
   
   
   while(peek(a.io.currOutReady) == BigInt(0))
   {
     step(1)
     clockCount +=1;
   }
   expect(a.io.currOutReady,true.B)   
   
   step(1)
   val pt3 = "h1c3c0c95956809532fcf0e2449a6b525"
   poke(a.io.memio.rdata,pt3.U)
   poke(a.io.memio.ready, true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.memio.ready, false.B)
   step(1)
   clockCount+=1; 
   
   
   while(peek(a.io.currOutReady) == BigInt(0))
   {
     step(1)
     clockCount +=1;
   }
   expect(a.io.currOutReady,true.B)  
   
   
   step(1)
   val pt4 = "hb16aedf5aa0de657ba637b391aafd255"
   poke(a.io.memio.rdata,pt4.U)
   poke(a.io.memio.ready, true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.memio.ready, false.B)
   step(1)
   clockCount+=1; 
   
   
   while(peek(a.io.finished) == BigInt(0))
   {
     step(1)
     clockCount +=1;
   }
   expect(a.io.currOutReady,true.B)  
   
     
   
   println("clock count=%x".format(clockCount))
   

   
   
   
}

class psgcmss2Tester extends ChiselFlatSpec
{
   behavior of "Page Swapper"
   backends foreach {backend =>
     it should s"should page our memory areas  $backend" in {
        Driver(() => new pageswappergcm, backend)(a => new pageswappergcmTestesss2(a)) should be (true)
     }     
   }
}
