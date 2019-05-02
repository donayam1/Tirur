package aes

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

class unrolledAesencTestes(a:unrolledaesenc) extends PeekPokeTester(a)
{
  val pt ="h54776f204f6e65204e696e652054776f"
  val key= "h5468617473206d79204b756e67204675"
  val ept= "h29C3505F571420F6402299B31A02D73A"
  
  //encryption test
  poke(a.io.key,key.U)
  poke(a.io.en,true.B)
  poke(a.io.iData,pt.U)
  step(1)
  expect(a.io.done,true.B)
  expect(a.io.result,ept.U)
  expect(a.io.rk0,"h5468617473206d79204b756e67204675".U)
  expect(a.io.rk1,"hE232FCF191129188B159E4E6D679A293".U)
  expect(a.io.rk2,"h56082007C71AB18F76435569A03AF7FA".U)
  expect(a.io.rk3,"hD2600DE7157ABC686339E901C3031EFB".U)
  expect(a.io.rk4,"hA11202C9B468BEA1D75157A01452495B".U) 
  expect(a.io.rk5,"hB1293B3305418592D210D232C6429B69".U) 
  expect(a.io.rk6,"hBD3DC287B87C47156A6C9527AC2E0E4E".U) 
  expect(a.io.rk7,"hCC96ED1674EAAA031E863F24B2A8316A".U) 
  expect(a.io.rk8,"h8E51EF21FABB4522E43D7A0656954B6C".U)
  expect(a.io.rk9,"hBFE2BF904559FAB2A16480B4F7F1CBD8".U)
  expect(a.io.rk10,"h28FDDEF86DA4244ACCC0A4FE3B316F26".U)
  
}

class unrolledAesencTester extends ChiselFlatSpec{
  behavior of "Aes"
  backends foreach{ backend =>
    it should s"encript the input data" in {
      Driver(()=>new unrolledaesenc,backend)(a=> new unrolledAesencTestes(a)) should be (true)
    } 
    
  }
}