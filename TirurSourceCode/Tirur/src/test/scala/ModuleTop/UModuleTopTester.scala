package intgCheck


import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

/*
 * All testes with this 
 * session key=1000080004000201000080004000200
 * iv=180004000200080104008a004d002
 * */
class UModuleTopTestes(a:UModuleTop) extends PeekPokeTester(a)
{
  
   var clockCount=0;
   poke(a.io.io.en,false.B) //done configuration
   poke(a.io.io.valid,false.B) //config is no more valid
  
   step(13)
   clockCount+=13;
   
   poke(a.io.io.addr,8192.U) //Config2 number of blocks 
   poke(a.io.io.wdata,10.U)   //3blocks 
   poke(a.io.io.valid,true.B)
   poke(a.io.io.we,true.B)
   poke(a.io.io.en,true.B)
   step(1) //wait for the output register update
   clockCount+=1;
   
   //write the initial data
   for(i <- 0 to 12)
   {
     poke(a.io.io.addr,i.U) //Config2 number of blocks 
     poke(a.io.io.wdata,0.U)   //3blocks
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,true.B)
     poke(a.io.io.en,true.B)
     step(1) //wait for the output register update
     clockCount+=1;      
   }
   
   //set the operation mode to stop
   poke(a.io.io.addr,8192.U) //Config2 number of blocks 
   poke(a.io.io.wdata,0.U)   //3blocks 
   poke(a.io.io.valid,true.B)
   poke(a.io.io.we,true.B)
   poke(a.io.io.en,true.B)
   step(1) //wait for the output register update
   clockCount+=1;
   
   
   
   poke(a.io.io.addr,8194.U) //Config2 number of blocks 
   poke(a.io.io.wdata,3.U)   //3blocks 
   poke(a.io.io.valid,true.B)
   poke(a.io.io.we,true.B)
   poke(a.io.io.en,true.B)
   step(1) //wait for the output register update
   clockCount+=1;  

   
   poke(a.io.io.addr,8192.U) //Config0 
   poke(a.io.io.wdata,3.U)   //3 operation mode page out and start
   poke(a.io.io.valid,true.B)
   poke(a.io.io.we,true.B)
   poke(a.io.io.en,true.B)
   step(1)
   clockCount+=1; 

   poke(a.io.io.en,false.B) //done configuration
   poke(a.io.io.valid,false.B) //config is no more valid
   
   
   
   val reslts = Array(
      "h78734508ef757cea0ab93823b457a54a".U,
      "h1f8ba36210d8ff03abad258b162a2e00".U,
      "h5ffbadfebae01b72dce61cef3e737549".U
   )
   
   var i=0;
   
   while(peek(a.io.done) == BigInt(0))
   {
     while(peek(a.io.currOutReady) == BigInt(0))
     {
       step(1)
       clockCount +=1;      
     }
     
   
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

class UModuleTopTester extends ChiselFlatSpec
{
   behavior of "Page Swapper"
   backends foreach {backend =>
     it should s"should page our memory areas  $backend" in {
        Driver(() => new UModuleTop, backend)(a => new UModuleTopTestes(a)) should be (true)
     }     
   }
}
