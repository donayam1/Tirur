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
class pageswappergcmTestes4(a:pageswappergcm) extends PeekPokeTester(a)
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
   
   //78734508 ef757cea 0ab93823 b457a54a
   poke(a.io.mainMemio.addr,8197.U) //Config5
   poke(a.io.mainMemio.wdata,"hb457a54a".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.mainMemio.addr,8198.U) //Config6 
   poke(a.io.mainMemio.wdata,"h0ab93823".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.mainMemio.addr,8199.U) //Config7
   poke(a.io.mainMemio.wdata,"hef757cea".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.mainMemio.addr,8200.U) //Config8
   poke(a.io.mainMemio.wdata,"h78734508".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1;
   
   
   poke(a.io.mainMemio.addr,8192.U) //Config0 
   poke(a.io.mainMemio.wdata,9.U)   //page in ivs
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 

   poke(a.io.mainMemio.en,false.B) //done configuration
   poke(a.io.mainMemio.valid,false.B) //config is no more valid
   
   //generate the iv tag and
   //encrypt the first block
   while(peek(a.io.currOutReady) == BigInt(0))
   {
       step(1)
       clockCount +=1;
   }
   expect(a.io.currOutReady,true.B)     
   step(1)
   clockCount+=1; 
   expect(a.io.currOutReady,false.B)  

   
   
   //1f8ba362 10d8ff03 abad258b 162a2e00
   poke(a.io.mainMemio.addr,8197.U) //Config5
   poke(a.io.mainMemio.wdata,"h162a2e00".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.mainMemio.addr,8198.U) //Config6 
   poke(a.io.mainMemio.wdata,"habad258b".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.mainMemio.addr,8199.U) //Config7
   poke(a.io.mainMemio.wdata,"h10d8ff03".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.mainMemio.addr,8200.U) //Config8
   poke(a.io.mainMemio.wdata,"h1f8ba362".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1;
   
   poke(a.io.mainMemio.valid,false.B)
   poke(a.io.mainMemio.en,false.B)
   
   //encrypt the second block
   while(peek(a.io.currOutReady) == BigInt(0))
   {
       step(1)
       clockCount +=1;
   }
   expect(a.io.currOutReady,true.B)     
   step(1)
   clockCount+=1; 
   expect(a.io.currOutReady,false.B)  
   
   
   //5ffbadfe bae01b72 dce61cef 3e737549
   poke(a.io.mainMemio.addr,8197.U) //Config5
   poke(a.io.mainMemio.wdata,"h3e737549".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.mainMemio.addr,8198.U) //Config6 
   poke(a.io.mainMemio.wdata,"hdce61cef".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.mainMemio.addr,8199.U) //Config7
   poke(a.io.mainMemio.wdata,"hbae01b72".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1; 
   poke(a.io.mainMemio.addr,8200.U) //Config8
   poke(a.io.mainMemio.wdata,"h5ffbadfe".U)   //page in memory
   poke(a.io.mainMemio.valid,true.B)
   poke(a.io.mainMemio.we,true.B)
   poke(a.io.mainMemio.en,true.B)
   step(1)
   clockCount+=1;
   
   poke(a.io.mainMemio.valid,false.B)
   poke(a.io.mainMemio.en,false.B)
   
   //encrypt the second block
   while(peek(a.io.currOutReady) == BigInt(0))
   {
       step(1)
       clockCount +=1;
   }
   expect(a.io.currOutReady,true.B)     
   

  
   while(peek(a.io.finished) == BigInt(0))
   {
     
       step(1)
       clockCount+=1; 
       expect(a.io.currOutReady,false.B)
       
       poke(a.io.mainMemio.addr,8200.U) //Config8
       poke(a.io.mainMemio.wdata,"h5ffbadfe".U)   //page in memory
       poke(a.io.mainMemio.valid,true.B)
       poke(a.io.mainMemio.we,true.B)
       poke(a.io.mainMemio.en,true.B)
       step(1)
       clockCount+=1;
       
       poke(a.io.mainMemio.valid,false.B)
       poke(a.io.mainMemio.en,false.B) 
     
       while((peek(a.io.currOutReady) == BigInt(0))&(peek(a.io.finished) == BigInt(0)))
       {
           step(1)
           clockCount +=1;
       }
       if((peek(a.io.finished) == BigInt(0)))
         expect(a.io.currOutReady,true.B)     
       //println("curr out ready "+peek(a.io.currOutReady))  
            
   }
   
   
   println("clock count=%x".format(clockCount))
   //tag +iv = 180004000200080104008a004d002790fa47c190292ea522ca5d40a43d17b
}

class psgcmTester4 extends ChiselFlatSpec
{
   behavior of "Page Swapper"
   backends foreach {backend =>
     it should s"should page our memory areas  $backend" in {
        Driver(() => new pageswappergcm, backend)(a => new pageswappergcmTestes4(a)) should be (true)
     }     
   }
}
