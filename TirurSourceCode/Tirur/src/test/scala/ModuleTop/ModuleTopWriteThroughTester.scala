package intgCheck


import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

/*
 * All testes with this 
 * session key=1000080004000201000080004000200
 * iv=180004000200080104008a004d002
 * */
class ModuleTopWrightThroughTestes(a:ModuleTop) extends PeekPokeTester(a)
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
     poke(a.io.io.wdata,(i*2).U)   //3blocks
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,true.B)
     poke(a.io.io.en,true.B)
     step(1) //wait for the output register update
     clockCount+=1;      
   }
   
   //read the data initial data
   for(i <- 0 to 12)
   {
     poke(a.io.io.addr,i.U) //Config2 number of blocks 
    // poke(a.io.io.wdata,i.U)   //3blocks
     poke(a.io.io.valid,true.B)
     poke(a.io.io.we,false.B)
     poke(a.io.io.en,true.B)
     step(1) //wait for the output register update
     clockCount+=1;    
     expect(a.io.io.rdata,(i*2).U)
     step(1)
   }
   
   
   
   
}

class ModuleTopWTHTester extends ChiselFlatSpec
{
   behavior of "Page Swapper"
   backends foreach {backend =>
     it should s"should page our memory areas  $backend" in {
        Driver(() => new ModuleTop, backend)(a => new ModuleTopWrightThroughTestes(a)) should be (true)
     }     
   }
}