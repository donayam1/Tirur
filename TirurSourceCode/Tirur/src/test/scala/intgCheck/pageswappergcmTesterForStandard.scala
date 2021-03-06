package intgCheck


import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

/*
 * All testes with this 
 * session key=0
 * iv=0
 * Note : before running this test set the counter init value to 1
 * 			  and random output to zero in each respective module 
 * */
class pageswappergcmTestesss(a:pageswappergcm) extends PeekPokeTester(a)
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
   poke(a.io.mainMemio.wdata,1.U)   //3blocks 
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
   
   
   //val pt = "h54776f204f6e65204e696e652054776f"
   val pt = 0
   poke(a.io.memio.rdata,pt.U)
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

class psgcmssTester extends ChiselFlatSpec
{
   behavior of "Page Swapper"
   backends foreach {backend =>
     it should s"should page our memory areas  $backend" in {
        Driver(() => new pageswappergcm, backend)(a => new pageswappergcmTestesss(a)) should be (true)
     }     
   }
}
