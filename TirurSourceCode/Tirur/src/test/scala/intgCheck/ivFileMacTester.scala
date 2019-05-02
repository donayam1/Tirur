package intgCheck

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}


class ivFileMacTestes(a:ivFileMac) extends PeekPokeTester(a)
{
            
     for( i <- 0 to 15)
     {
      poke(a.io.cmd,2.U) // write 
      poke(a.io.addr,i.U)
      poke(a.io.wdata,i.U) // 
      //poke(a.io.en,true.B)
      step(1)  
     } 
      
     for( i <- 0 to 15)
     {
      
      poke(a.io.cmd,1.U) // read v
      poke(a.io.addr,i.U)         
      step(1)
      expect(a.io.rdata,0.U) // 
     }
     
     for( i <- 0 to 15)
     {
      
      poke(a.io.cmd,3.U) // read mac
      poke(a.io.addr,i.U)         
      step(1)
      expect(a.io.rdata,i.U) // 
     } 
//     
//     
//     
      poke(a.io.cmd,6.U) // read         
      step(1)
      expect(a.io.rdata,0.U) //  
     
      
//
//      
      poke(a.io.cmd,5.U) // make all unused
      step(1)
      
      
      poke(a.io.cmd,6.U) // no empty address   
      step(1)
      expect(a.io.rdata,0.U) // 
      
    
}

class ivFileMacTester extends ChiselFlatSpec
{
   behavior of "ivFile"
   backends foreach {backend =>
     it should s"store values  $backend" in{
        Driver(() => new ivFileMac(128), backend)(a => new ivFileMacTestes(a)) should be (true)
     }     
   }
}