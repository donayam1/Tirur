package aes

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester,ChiselFlatSpec}

//class rcontestes(rc:rcon) extends PeekPokeTester(rc)
//{
//    poke(rc.io.addr,0.U)
//    expect(rc.io.data,0x1.U)
//  
//    
//    poke(rc.io.addr,14.U)
//    expect(rc.io.data,0x9A.U)
//  
//}
class sboxtestes(sb:sbox) extends PeekPokeTester(sb)
{
    poke(sb.io.addr,0.U)
    expect(sb.io.data,"h63636363".U)
  
    
    poke(sb.io.addr,0x19.U)
    expect(sb.io.data,"h636363D4".U)
  
    poke(sb.io.addr,  "h03020100".U)
    expect(sb.io.data,"h7b777c63".U)
}
class rsboxtestes(rsb:rsbox) extends PeekPokeTester(rsb)
{
    poke(rsb.io.addr2,0.U)
    expect(rsb.io.data2,"h52525252_52525252_52525252_52525252".U)
  
    
    poke(rsb.io.addr2,0xD4.U)
    expect(rsb.io.data2,"h52525252_52525252_52525252_52525219".U)
  
}
class sboxTester extends ChiselFlatSpec{
  behavior of "rcon"
  backends foreach {backend =>  
    it should s"return stored data $backend" in{
      //Driver(()=> new rcon,backend)(a => new rcontestes(a)) should be (true)
      Driver(()=> new sbox,backend)(a => new sboxtestes(a)) should be (true)
      Driver(()=> new rsbox,backend)(a => new rsboxtestes(a)) should be (true)
    }
  }
}