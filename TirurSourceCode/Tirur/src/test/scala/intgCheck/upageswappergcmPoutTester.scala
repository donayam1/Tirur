package intgCheck


import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

/*
 * All testes with this 
 * session key=1000080004000201000080004000200
 * iv=18000400020004008a004d0026802
 * page out Memory 
 * */
class upageswappergcmTestes(a:upageswappergcm) extends PeekPokeTester(a)
{
   
   var clockCount=0;
   poke(a.io.mainMemio.en,false.B) //done configuration
   poke(a.io.mainMemio.valid,false.B) //config is no more valid
  
   step(22)
   clockCount+=22;
   
   poke(a.io.mainMemio.addr,8194.U) //Config2 number of blocks 
   poke(a.io.mainMemio.wdata,3.U)   //3blocks 
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
   
   val reslts = Array(
      "he1bf4b41987c229bc9c81b115ecfbb7".U,            
      "h4ace77bf1ebbf473f4c75c0081cd58cb".U,
      "he3f4d6aa0ff4cb05c3223bf0c38e492f".U
   )
   
   var i=0;
   step(2) //one encryption for IVTag generation
   clockCount+=1; 
   
   while(peek(a.io.finished) == BigInt(0))
   {
     
       //read the first data
       val pt = 0
       poke(a.io.memio.rdata,pt.U)
       poke(a.io.memio.ready, true.B)
       step(1)
       clockCount+=1; 
       poke(a.io.memio.ready, false.B)
   
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
       step(1)
                   

       if(i == 2) //this is for the last three clenaup clocks
         step(4)
       i+=1
   }
         
   println("clock count=%x".format(clockCount))
   
   
   
}

class upsgcmTester extends ChiselFlatSpec
{
   behavior of "Page Swapper"
   backends foreach {backend =>
     it should s"should page our memory areas  $backend" in {
        Driver(() => new upageswappergcm, backend)(a => new upageswappergcmTestes(a)) should be (true)
     }     
   }
}
