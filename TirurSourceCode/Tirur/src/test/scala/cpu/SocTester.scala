package cpu

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester,ChiselFlatSpec}

class SocTestes(s:Soc) extends PeekPokeTester(s)
{
  
   //keep core at reset           
   poke(s.io.rst,true.B)
   step(1)
   
   //fill the memory with instractions 
   poke(s.io.mde.add,0)
   poke(s.io.mde.wdata,"h01400093".U(32.W))//01400093          	li	ra,20
   poke(s.io.mde.we,true.B)
   poke(s.io.mde.en,true.B)
   step(1)
   
   
   poke(s.io.mde.add,0)   
   poke(s.io.mde.we,false.B)
   poke(s.io.mde.en,true.B)
   step(1)
   expect(s.io.mde.rdata,"h01400093".U(32.W))
   
   
   poke(s.io.mde.add,1)
   poke(s.io.mde.wdata,"h02800113".U(32.W))//02800113          	li	sp,40
   poke(s.io.mde.we,true.B)
   poke(s.io.mde.en,true.B)
   step(1)
   
   poke(s.io.mde.add,1)   
   poke(s.io.mde.we,false.B)
   poke(s.io.mde.en,true.B)
   step(1)
   expect(s.io.mde.rdata,"h02800113".U(32.W))
   
   
   
   poke(s.io.mde.add,2)
   poke(s.io.mde.wdata,"h002081b3".U(32.W))//002081b3          	add	gp,ra,sp
   poke(s.io.mde.we,true.B)
   poke(s.io.mde.en,true.B)
   step(1)
   
   
   poke(s.io.mde.add,2)   
   poke(s.io.mde.we,false.B)
   poke(s.io.mde.en,true.B)
   step(1)
   expect(s.io.mde.rdata,"h002081b3".U(32.W))
   


   //execute the three instractions
   poke(s.io.rst,false.B)
   poke(s.io.rfd.add,"h3F".U(6.W))   
   poke(s.io.rfd.we,false.B)
   poke(s.io.rfd.en,true.B)   
   step(1)
   
   
//   poke(s.io.rfd.add,"h7FFF".U(15.W))   
//   poke(s.io.rfd.we,false.B)
//   poke(s.io.rfd.en,true.B)   
   //step(1)
   expect(s.io.rfd.rdata,1)
   
     
   poke(s.io.rfd.add,"h3F".U(6.W))   
   poke(s.io.rfd.we,false.B)
   poke(s.io.rfd.en,true.B)   
   step(1)
   expect(s.io.rfd.rdata,2)
   
   
   poke(s.io.rfd.add,"h3F".U(6.W))   
   poke(s.io.rfd.we,false.B)
   poke(s.io.rfd.en,true.B)   
   step(1)
   expect(s.io.rfd.rdata,3)
   
      
   //test regiter write instractions
   //keep core at reset           
   poke(s.io.rst,true.B)
   step(1)
   poke(s.io.rst,false.B)
   step(1) //release the core from reset 
   
   poke(s.io.rfd.add,1.U(15.W))
   poke(s.io.rfd.we,false.B)
   poke(s.io.rfd.en,true.B)
   step(1) //execute the first instraction 
   expect(s.io.rfd.rdata,20)
   
   poke(s.io.rfd.add,2.U(15.W))
   poke(s.io.rfd.we,false.B)
   poke(s.io.rfd.en,true.B)
   step(1) //execute the second instraction
   expect(s.io.rfd.rdata,40)
  
   poke(s.io.rfd.add,3.U(15.W))
   poke(s.io.rfd.we,false.B)
   poke(s.io.rfd.en,true.B)
   step(1)//execute the third instraction
   expect(s.io.rfd.rdata,60)
   
   
   
   
}

class SocTester extends ChiselFlatSpec
{
  behavior of "SOC"
  backends foreach {backend =>{
    it should s"execute instractions $backend" in{
       Driver(()=> new Soc,backend)(s=> new SocTestes(s)) should be (true)
     } 
    }
  }
}