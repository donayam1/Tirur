package cpu

import chisel3._

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

//sbt 'testOnly cpu.RegFileTester'
class RegFileTestes(rf:Rfile) extends PeekPokeTester(rf){
  
    for(i <- 0 to 31)
    {
      poke(rf.io.we, 1)
      poke(rf.io.wData, i)
      poke(rf.io.rd, i)
      step(1)
    }
    
    for( j <- 0 to 31){
      poke(rf.io.we, 0)
      poke(rf.io.rs1, j)
      poke(rf.io.rs2, j)
      step(1)
      
      val expected1 = j 
      expect(rf.io.rData1, expected1)
      expect(rf.io.rData2, expected1)
    }
    
    
    //Test X0 hardwired to zero
    for(i <- 0 to 31)
    {
      poke(rf.io.we, 1)
      poke(rf.io.wData, i)
      poke(rf.io.rd, 0)
      step(1)
      
      
      poke(rf.io.we, 0)
      poke(rf.io.rs1, 0)
      poke(rf.io.rs2, 0)
      step(1)
      
      val expected1 = 0 
      expect(rf.io.rData1, expected1)
      expect(rf.io.rData2, expected1)
      
    }
        
      //expect(gcd.io.outputValid, 1)  
}

class RegFileTester extends ChiselFlatSpec {
  behavior of "RegFile"
  backends foreach {backend =>
    it should s"correctly store numbers numbers $backend" in {
      Driver(() => new Rfile, backend)(c => new RegFileTestes(c)) should be (true)
    }
  }
}

//object RegFileMain extends App {
//  iotesters.Driver.execute(args, () => new Rfile) {
//    rf => new RegFileTestes(rf)
//  }
//}