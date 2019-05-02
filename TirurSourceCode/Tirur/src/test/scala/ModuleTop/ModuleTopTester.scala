package intgCheck


import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

/*
 * All testes with this 
 * session key=1000080004000201000080004000200
 * iv=180004000200080104008a004d002
 * */
class ModuleTopTestes(a:ModuleTop) extends PeekPokeTester(a)
{ 
   var clockCount=0;
   poke(a.io.io.en,false.B) //done configuration
   poke(a.io.io.valid,false.B) //config is no more valid
  
   step(22)
   clockCount+=22;
   
   poke(a.io.io.addr,8192.U) //Config2 
   poke(a.io.io.wdata,10.U)   //Configure Right Through 
   poke(a.io.io.valid,true.B)
   poke(a.io.io.we,true.B)
   poke(a.io.io.en,true.B)
   step(1) //wait for the output register update
   clockCount+=1;
   
   //write the initial data
   for(i <- 0 to 12)
   {
     poke(a.io.io.addr,i.U) //Write zero to each block
     poke(a.io.io.wdata,0.U)   
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,true.B)
     poke(a.io.io.en,true.B)
     step(1) //wait for the output register update
     clockCount+=1;      
   }
   
   //set the operation mode to stop
   poke(a.io.io.addr,8192.U)  
   poke(a.io.io.wdata,0.U)    
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
      "he1bf4b41987c229bc9c81b115ecfbb7".U,
//      "he1bf4b41987c229bc9c81b115ecfbb7".U,
      "h4ace77bf1ebbf473f4c75c0081cd58cb".U,
      "he3f4d6aa0ff4cb05c3223bf0c38e492f".U
   )
   
   
   
   var i=0;

   while(peek(a.io.done) == BigInt(0))
   {
     //step(100)
     while(peek(a.io.currOutReady) == BigInt(0))
     {
       step(1)
       clockCount +=1;      
     }
     var tmp=0;
     step(20)
     //check for the result is available correctly
     //78734508 ef757cea 0ab93823 b457a54a
     poke(a.io.io.addr,8197.U) //Config5 
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,false.B)
     poke(a.io.io.en,true.B)
     step(5)
     var tmp1=peek(a.io.io.rdata);
    
     expect(a.io.io.rdata,reslts(i)(31,0))
     expect(a.io.io.ready,true.B)
     
     poke(a.io.io.addr,8198.U) //Config6 
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,false.B)
     poke(a.io.io.en,true.B)
     step(3)
     expect(a.io.io.rdata,reslts(i)(63,32))
     expect(a.io.io.ready,true.B)
  
     var tmp2=  peek(a.io.io.rdata);    
     poke(a.io.io.addr,8199.U) //Config7 
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,false.B)
     poke(a.io.io.en,true.B)
     step(4)
     expect(a.io.io.rdata,reslts(i)(95,64))
     expect(a.io.io.ready,true.B)
     
     var tmp3=  peek(a.io.io.rdata);
     poke(a.io.io.addr,8200.U) //Config8 
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,false.B)
     poke(a.io.io.en,true.B)
     step(1)
     expect(a.io.io.rdata,reslts(i)(127,96))
     expect(a.io.io.ready,true.B)
     
     var tmp4=  peek(a.io.io.rdata);
     
     step(1)
     
     poke(a.io.io.addr,8201.U) //Config9 
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,true.B)
     poke(a.io.io.en,true.B)
     poke(a.io.io.wdata,"hFFFFFFFF".U)
     step(1)
     poke(a.io.io.en,false.B)
     step(1)
     
     
     
     
     println("res = %x %x %x %x\n".format(tmp4,tmp3,tmp2,tmp1))
     
     i+=1;       
     
     
     if(i==3)
       step(20)
   }
   
   expect(a.io.done,true.B)
   expect(a.io.err,false.B)
   expect(a.io.currOutReady,false.B)     
}

class ModuleTopTester extends ChiselFlatSpec
{
   behavior of "Page Swapper"
   backends foreach {backend =>
     it should s"should page our memory areas  $backend" in {
        Driver(() => new ModuleTop, backend)(a => new ModuleTopTestes(a)) should be (true)
     }     
   }
}
