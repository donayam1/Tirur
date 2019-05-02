package aes

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester,ChiselFlatSpec}

class udecTestes(a:udec) extends PeekPokeTester(a)
{
    val pt ="h54776f204f6e65204e696e652054776f"    
    val ept= "h29c3505f571420f6402299b31a02d73a"
  
    
    poke(a.io.rk0,"h5468617473206d79204b756e67204675".U)
    poke(a.io.rk1,"hE232FCF191129188B159E4E6D679A293".U)
    poke(a.io.rk2,"h56082007C71AB18F76435569A03AF7FA".U)
    poke(a.io.rk3,"hD2600DE7157ABC686339E901C3031EFB".U)
    poke(a.io.rk4,"hA11202C9B468BEA1D75157A01452495B".U) 
    poke(a.io.rk5,"hB1293B3305418592D210D232C6429B69".U) 
    poke(a.io.rk6,"hBD3DC287B87C47156A6C9527AC2E0E4E".U) 
    poke(a.io.rk7,"hCC96ED1674EAAA031E863F24B2A8316A".U) 
    poke(a.io.rk8,"h8E51EF21FABB4522E43D7A0656954B6C".U)
    poke(a.io.rk9,"hBFE2BF904559FAB2A16480B4F7F1CBD8".U)
    poke(a.io.rk10,"h28FDDEF86DA4244ACCC0A4FE3B316F26".U) 
    poke(a.io.en,true.B)
    poke(a.io.data,ept.U)   
    step(1)   
    expect(a.io.out,"h54776f204f6e65204e696e652054776f".U)
    expect(a.io.done,true.B)
    
    
    
}


class aesudecTester extends ChiselFlatSpec{
  behavior of "Aes_Dec"
  backends foreach{ backend =>
    it should s"decrypt string " in{
      Driver(()=>new udec,backend)(a => new udecTestes(a)) should be (true)
    }   
  }
}