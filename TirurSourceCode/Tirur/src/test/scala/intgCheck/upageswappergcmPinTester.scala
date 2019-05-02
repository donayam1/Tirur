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
class upageswappergcmTestes3(a:upageswappergcm) extends PeekPokeTester(a)
{
  
   var clockCount=0;
   poke(a.io.mainMemio.en,false.B) //done configuration
   poke(a.io.mainMemio.valid,false.B) //config is no more valid
  
   step(22)
   clockCount+=22;
   
   poke(a.io.mainMemio.addr,8194.U) //Config2 
   poke(a.io.mainMemio.wdata,3.U)   //page in 3 blocks memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1;   
   
   poke(a.io.mainMemio.addr,8192.U) //Config0 
   poke(a.io.mainMemio.wdata,5.U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 

   poke(a.io.mainMemio.en,false.B) //done configuration
   poke(a.io.mainMemio.valid,false.B) //config is no more valid
   
   var data = Array(
     "h78734508ef757cea0ab93823b457a54a".U,
     "h1f8ba36210d8ff03abad258b162a2e00".U,
     "h5ffbadfebae01b72dce61cef3e737549".U
   )
   var i=0;
   while(peek(a.io.finished) == BigInt(0))
   {
   
       //78734508 ef757cea 0ab93823 b457a54a
       poke(a.io.mainMemio.addr,8197.U) //Config5
       poke(a.io.mainMemio.wdata,data(i)(31,0)) //"hb457a54a".U)   //page in memory
       poke(a.io.mainMemio.valid,true.B)
       poke(a.io.mainMemio.we,true.B)
       poke(a.io.mainMemio.en,true.B)
       step(1)
       clockCount+=1; 
       poke(a.io.mainMemio.addr,8198.U) //Config6 
       poke(a.io.mainMemio.wdata,data(i)(63,32))//"h0ab93823".U)   //page in memory
       poke(a.io.mainMemio.valid,true.B)
       poke(a.io.mainMemio.we,true.B)
       poke(a.io.mainMemio.en,true.B)
       step(1)
       clockCount+=1; 
       poke(a.io.mainMemio.addr,8199.U) //Config7
       poke(a.io.mainMemio.wdata,data(i)(95,64))//"hef757cea".U)   //page in memory
       poke(a.io.mainMemio.valid,true.B)
       poke(a.io.mainMemio.we,true.B)
       poke(a.io.mainMemio.en,true.B)
       step(1)
       clockCount+=1; 
       poke(a.io.mainMemio.addr,8200.U) //Config8
       poke(a.io.mainMemio.wdata,data(i)(127,96))// "h78734508".U)   //page in memory
       poke(a.io.mainMemio.valid,true.B)
       poke(a.io.mainMemio.we,true.B)
       poke(a.io.mainMemio.en,true.B)
       step(1)
       clockCount+=1;
       
       

       
       //generate the iv tag and
       //encrypt the first block
       while(peek(a.io.currOutReady) == BigInt(0))
       {
           step(1)
           clockCount +=1;
       }
       expect(a.io.currOutReady,true.B)     
       poke(a.io.memio.ready,true.B)
       step(1)
       clockCount+=1; 
       expect(a.io.currOutReady,false.B)  
       poke(a.io.memio.ready,false.B)
       i+=1;
       if(i==3)
         step(8)
   }
      
   println("clock count=%x".format(clockCount))
   //tag +iv = 180004000200080104008a004d002790fa47c190292ea522ca5d40a43d17b
}

class upsgcmTester3 extends ChiselFlatSpec
{
   behavior of "Page Swapper"
   backends foreach {backend =>
     it should s"should page our memory areas  $backend" in {
        Driver(() => new upageswappergcm, backend)(a => new upageswappergcmTestes3(a)) should be (true)
     }     
   }
}
