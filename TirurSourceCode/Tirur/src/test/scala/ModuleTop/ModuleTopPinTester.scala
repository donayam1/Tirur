package intgCheck


import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

/*
 * All testes with this 
 * session key=1000080004000201000080004000200
 * iv=180004000200080104008a004d002
 * */
class ModuleTopPinTestes(a:ModuleTop) extends PeekPokeTester(a)
{
  
   var clockCount=0;
   poke(a.io.io.en,false.B) //done configuration
   poke(a.io.io.valid,false.B) //config is no more valid
  
   step(22)
   clockCount+=22;
   
   poke(a.io.io.addr,8192.U) //Config2 number of blocks 
   poke(a.io.io.wdata,10.U)   //write through
   poke(a.io.io.valid,true.B)
   poke(a.io.io.we,true.B)
   poke(a.io.io.en,true.B)
   step(1) //wait for the output register update
   clockCount+=1;
   
   //write the initial data
   for(i <- 0 to 12)
   {
     poke(a.io.io.addr,i.U) //Config2 number of blocks 
     poke(a.io.io.wdata,i.U)   //3blocks
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,true.B)
     poke(a.io.io.en,true.B)
     step(1) //wait for the output register update
     clockCount+=1;      
   }
   
   var i=0;
   
   val reslts = Array(
      "h78734508ef757cea0ab93823b457a54a".U,
      "h1f8ba36210d8ff03abad258b162a2e00".U,
      "h5ffbadfebae01b72dce61cef3e737549".U
   )
   
   
     //3 blocks 
     poke(a.io.io.addr,8194.U) //Config2 
     poke(a.io.io.wdata,3.U)   //page in 3 blocks memory
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,true.B)
     poke(a.io.io.en,true.B)
     step(1)
     clockCount+=1; 
   
   while(peek(a.io.done) == BigInt(0))
   {
     

     
     //78734508 ef757cea 0ab93823 b457a54a
     poke(a.io.io.addr,8197.U) //Config5
     poke(a.io.io.wdata,reslts(i)(31,0))   //page in memory
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,true.B)
     poke(a.io.io.en,true.B)
     step(1)
     clockCount+=1; 
     poke(a.io.io.addr,8198.U) //Config6 
     poke(a.io.io.wdata,reslts(i)(63,32))   //page in memory
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,true.B)
     poke(a.io.io.en,true.B)
     step(1)
     clockCount+=1; 
     poke(a.io.io.addr,8199.U) //Config7
     poke(a.io.io.wdata,reslts(i)(95,64))   //page in memory
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,true.B)
     poke(a.io.io.en,true.B)
     step(1)
     clockCount+=1; 
     poke(a.io.io.addr,8200.U) //Config8
     poke(a.io.io.wdata,reslts(i)(127,96))   //page in memory
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,true.B)
     poke(a.io.io.en,true.B)
     step(1)
     clockCount+=1;
     
     if(i==0)
     {
       poke(a.io.io.addr,8192.U) //Config0 
       poke(a.io.io.wdata,5.U)   //page in memory
       poke(a.io.io.valid,true.B)
       poke(a.io.io.we,true.B)
       poke(a.io.io.en,true.B)
       step(1)
       clockCount+=1; 
     }
    
     poke(a.io.io.en,false.B) //done configuration
     poke(a.io.io.valid,false.B) //config is no more valid
       
     
     while(peek(a.io.currOutReady) == BigInt(0))
     {
         step(1)
         clockCount +=1;
     }
     if(i==2) //this is for the last three clenaup clocks
       step(4)
     i+=1
   }
   
   expect(a.io.done,true.B)
   expect(a.io.err,false.B)
   expect(a.io.currOutReady,false.B)
        
   
   
   poke(a.io.io.addr,8192.U) //Config2 number of blocks 
   poke(a.io.io.wdata,10.U)   //write through
   poke(a.io.io.valid,true.B)
   poke(a.io.io.we,true.B)
   poke(a.io.io.en,true.B)
   step(1) //wait for the output register update
   clockCount+=1;
   
   //write the initial data
   for(i <- 0 to 11)
   {
     poke(a.io.io.addr,i.U) //Config2 number of blocks 
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,false.B)
     poke(a.io.io.en,true.B)
     step(1) //wait for the output register update
     clockCount+=1;
     expect(a.io.io.rdata,0.U)
     expect(a.io.io.ready,true.B)
   } 
}

class ModuleTopPinTester extends ChiselFlatSpec
{
   behavior of "Page Swapper"
   backends foreach {backend =>
     it should s"should page our memory areas  $backend" in {
        Driver(() => new ModuleTop, backend)(a => new ModuleTopPinTestes(a)) should be (true)
     }     
   }
}
