package aes

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester,ChiselFlatSpec}

class expandertestes(e:expander) extends PeekPokeTester(e)
{
//    poke(e.io.start,false.B)
//    step(1)
    //poke(e.io.start,true.B)
    
    //round 0
    step(1)
    poke(e.io.key,"h0102030405060708090A0B0C0D0E0F".U)
    poke(e.io.round,0)
    poke(e.io.dir,true.B)
    
    expect(e.io.data,"h0102030405060708090A0B0C0D0E0F".U)//"h0F0E0D0C0B0A09080706050403020100".U
    
    //round 1
    step(1)
    poke(e.io.key,"h0102030405060708090A0B0C0D0E0F".U)
    poke(e.io.round,1)
    poke(e.io.sboxio.data,"hD7AB76FE".U)
    
    expect(e.io.data,"hd6aa74fdd2af72fadaa678f1d6ab76fe".U)
    
    
    //round 2
    step(1)
    poke(e.io.key,"h0102030405060708090A0B0C0D0E0F".U)
    poke(e.io.round,2)
    poke(e.io.sboxio.data,"h6238BBF6".U)    
    expect(e.io.data,"hB692CF0B643DBDF1BE9BC5006830B3FE".U)
    
    //round 3
    step(1)
    poke(e.io.key,"h0102030405060708090A0B0C0D0E0F".U)
    poke(e.io.round,3)
    poke(e.io.sboxio.data,"h046DBB45".U)
    expect(e.io.data,"hB6FF744ED2C2C9BF6C590CBF0469BF41".U)
    
    //round 4
    step(1)
    poke(e.io.key,"h0102030405060708090A0B0C0D0E0F".U)
    poke(e.io.round,4)
    poke(e.io.sboxio.data,"hF90883F2".U)
    expect(e.io.data,"h47F7F7BC95353E03F96C32BCFD058DFD".U)
    
    //round 5
    step(1)
    poke(e.io.key,"h0102030405060708090A0B0C0D0E0F".U)
    poke(e.io.round,5)
    poke(e.io.sboxio.data,"h6B5D5454".U)
    expect(e.io.data,"h3CAAA3E8A99F9DEB50F3AF57ADF622AA".U)
    
    //round 6
    step(1)
    poke(e.io.key,"h0102030405060708090A0B0C0D0E0F".U)
    poke(e.io.round,6)
    poke(e.io.sboxio.data,"h4293AC95".U)
    expect(e.io.data,"h5E390F7DF7A69296A7553DC10AA31F6B".U)
    
    //round 7
    step(1)
    poke(e.io.key,"h0102030405060708090A0B0C0D0E0F".U)
    poke(e.io.round,7)
    poke(e.io.sboxio.data,"h0AC07F67".U)
    expect(e.io.data,"h14F9701AE35FE28C440ADF4D4EA9C026".U)
    
    
    //round 8
    step(1)
    poke(e.io.key,"h0102030405060708090A0B0C0D0E0F".U)
    poke(e.io.round,8)
    poke(e.io.sboxio.data,"hD3BAF72F".U)
    expect(e.io.data,"h47438735A41C65B9E016BAF4AEBF7AD2".U)
    
    
    //round 9
    step(1)
    poke(e.io.key,"h0102030405060708090A0B0C0D0E0F".U)
    poke(e.io.round,9)
    poke(e.io.sboxio.data,"h08DAB5E4".U)
    expect(e.io.data,"h549932D1F08557681093ED9CBE2C974E".U)
    
    //round 10
    step(1)
    poke(e.io.key,"h0102030405060708090A0B0C0D0E0F".U)
    poke(e.io.round,10)
    poke(e.io.sboxio.data,"h71882FAE".U)   
    expect(e.io.data,"h13111D7FE3944A17F307A78B4D2B30C5".U)
    
    
    
//    for( i <- 0 to 3)
//    {
//      var out=((((i<<2)+3)<<24)|(((i<<2)+2)<<16)|(((i<<2)+1)<<8)|(i<<2))
//      println(" i="+i+ " ,i<<2="+(i<<2)+ " out="+out)
//      poke(e.io.keyio.rdata, ((((i<<2)+3)<<24)|(((i<<2)+2)<<16)|(((i<<2)+1)<<8)|(i<<2)))  
//      step(1)
//      expect(e.io.exKeyio.wdata,((((i<<2)+3)<<24)|(((i<<2)+2)<<16)|(((i<<2)+1)<<8)|(i<<2)))      
//      expect(e.io.exKeyio.addr,i)
//
//    }
    
    
    
    
}

class expanderTester extends ChiselFlatSpec{
  behavior of "expander"
  backends foreach {backend =>  
    it should s"expand the key $backend" in{
      Driver(()=> new expander,backend)(e => new expandertestes(e)) should be (true)
      
    }
  }
}