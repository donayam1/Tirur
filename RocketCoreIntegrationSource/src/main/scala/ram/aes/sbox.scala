package aes

import chisel3._
import chisel3.util._

class sbox_io2 extends Bundle{
  val addr2 = Input(UInt(128.W))
  val data2 = Output(UInt(128.W))  
}

class sbox_io extends Bundle{
  val addr = Input(UInt(32.W))
  val data = Output(UInt(32.W))  
   
  val addr2 = Input(UInt(128.W))
  val data2 = Output(UInt(128.W))
}

class sbox_io3 extends Bundle{
  val addr = Input(UInt(32.W))
  val data = Output(UInt(32.W))     
}



class sbox extends Module{
  val io = IO(new sbox_io)
  val rom = Vec(Array(
    0x63.U, 0x7C.U, 0x77.U, 0x7B.U, 0xF2.U, 0x6B.U, 0x6F.U, 0xC5.U, 0x30.U, 0x01.U, 0x67.U, 0x2B.U, 0xFE.U, 0xD7.U, 0xAB.U, 0x76.U,
		0xCA.U, 0x82.U, 0xC9.U, 0x7D.U, 0xFA.U, 0x59.U, 0x47.U, 0xF0.U, 0xAD.U, 0xD4.U, 0xA2.U, 0xAF.U, 0x9C.U, 0xA4.U, 0x72.U, 0xC0.U,
		0xB7.U, 0xFD.U, 0x93.U, 0x26.U, 0x36.U, 0x3F.U, 0xF7.U, 0xCC.U, 0x34.U, 0xA5.U, 0xE5.U, 0xF1.U, 0x71.U, 0xD8.U, 0x31.U, 0x15.U,
		0x04.U, 0xC7.U, 0x23.U, 0xC3.U, 0x18.U, 0x96.U, 0x05.U, 0x9A.U, 0x07.U, 0x12.U, 0x80.U, 0xE2.U, 0xEB.U, 0x27.U, 0xB2.U, 0x75.U,
		0x09.U, 0x83.U, 0x2C.U, 0x1A.U, 0x1B.U, 0x6E.U, 0x5A.U, 0xA0.U, 0x52.U, 0x3B.U, 0xD6.U, 0xB3.U, 0x29.U, 0xE3.U, 0x2F.U, 0x84.U,
		0x53.U, 0xD1.U, 0x00.U, 0xED.U, 0x20.U, 0xFC.U, 0xB1.U, 0x5B.U, 0x6A.U, 0xCB.U, 0xBE.U, 0x39.U, 0x4A.U, 0x4C.U, 0x58.U, 0xCF.U,
		0xD0.U, 0xEF.U, 0xAA.U, 0xFB.U, 0x43.U, 0x4D.U, 0x33.U, 0x85.U, 0x45.U, 0xF9.U, 0x02.U, 0x7F.U, 0x50.U, 0x3C.U, 0x9F.U, 0xA8.U,
		0x51.U, 0xA3.U, 0x40.U, 0x8F.U, 0x92.U, 0x9D.U, 0x38.U, 0xF5.U, 0xBC.U, 0xB6.U, 0xDA.U, 0x21.U, 0x10.U, 0xFF.U, 0xF3.U, 0xD2.U,
		0xCD.U, 0x0C.U, 0x13.U, 0xEC.U, 0x5F.U, 0x97.U, 0x44.U, 0x17.U, 0xC4.U, 0xA7.U, 0x7E.U, 0x3D.U, 0x64.U, 0x5D.U, 0x19.U, 0x73.U,
		0x60.U, 0x81.U, 0x4F.U, 0xDC.U, 0x22.U, 0x2A.U, 0x90.U, 0x88.U, 0x46.U, 0xEE.U, 0xB8.U, 0x14.U, 0xDE.U, 0x5E.U, 0x0B.U, 0xDB.U,
		0xE0.U, 0x32.U, 0x3A.U, 0x0A.U, 0x49.U, 0x06.U, 0x24.U, 0x5C.U, 0xC2.U, 0xD3.U, 0xAC.U, 0x62.U, 0x91.U, 0x95.U, 0xE4.U, 0x79.U,
		0xE7.U, 0xC8.U, 0x37.U, 0x6D.U, 0x8D.U, 0xD5.U, 0x4E.U, 0xA9.U, 0x6C.U, 0x56.U, 0xF4.U, 0xEA.U, 0x65.U, 0x7A.U, 0xAE.U, 0x08.U,
		0xBA.U, 0x78.U, 0x25.U, 0x2E.U, 0x1C.U, 0xA6.U, 0xB4.U, 0xC6.U, 0xE8.U, 0xDD.U, 0x74.U, 0x1F.U, 0x4B.U, 0xBD.U, 0x8B.U, 0x8A.U,
		0x70.U, 0x3E.U, 0xB5.U, 0x66.U, 0x48.U, 0x03.U, 0xF6.U, 0x0E.U, 0x61.U, 0x35.U, 0x57.U, 0xB9.U, 0x86.U, 0xC1.U, 0x1D.U, 0x9E.U,
		0xE1.U, 0xF8.U, 0x98.U, 0x11.U, 0x69.U, 0xD9.U, 0x8E.U, 0x94.U, 0x9B.U, 0x1E.U, 0x87.U, 0xE9.U, 0xCE.U, 0x55.U, 0x28.U, 0xDF.U,
		0x8C.U, 0xA1.U, 0x89.U, 0x0D.U, 0xBF.U, 0xE6.U, 0x42.U, 0x68.U, 0x41.U, 0x99.U, 0x2D.U, 0x0F.U, 0xB0.U, 0x54.U, 0xBB.U, 0x16.U    
  ));
  val sb0 = rom(io.addr(7,0))
  val sb1 = rom(io.addr(15,8))
  val sb2 = rom(io.addr(23,16))
  val sb3 = rom(io.addr(31,24))
  
  io.data := Cat(sb3,sb2,sb1,sb0) //rom(io.addr)
  
  
  
  val sb4 = rom(io.addr2(7,0))
  val sb5 = rom(io.addr2(15,8))
  val sb6 = rom(io.addr2(23,16))
  val sb7 = rom(io.addr2(31,24))
  
  val x1= Cat(sb7,sb6,sb5,sb4)
  
  val sb8 = rom(io.addr2(39,32))
  val sb9 = rom(io.addr2(47,40))
  val sb10 = rom(io.addr2(55,48))
  val sb11 = rom(io.addr2(63,56))
  
  val x2= Cat(sb11,sb10,sb9,sb8)
  
  val sb12 = rom(io.addr2(71,64))
  val sb13 = rom(io.addr2(79,72))
  val sb14 = rom(io.addr2(87,80))
  val sb15 = rom(io.addr2(95,88))
  
  val x3= Cat(sb15,sb14,sb13,sb12)
  
  val sb16 = rom(io.addr2(103,96))
  val sb17 = rom(io.addr2(111,104))
  val sb18 = rom(io.addr2(119,112))
  val sb19 = rom(io.addr2(127,120))
  
  val x4= Cat(sb19,sb18,sb17,sb16)
  
  io.data2 := Cat(x4,x3,x2,x1) //rom(io.addr)
  
  
}
class rsbox extends Module{
  val io = IO(new sbox_io2)
  val rom = Vec(Array(
      0x52.U, 0x09.U, 0x6a.U, 0xd5.U, 0x30.U, 0x36.U, 0xa5.U, 0x38.U, 0xbf.U, 0x40.U, 0xa3.U, 0x9e.U, 0x81.U, 0xf3.U, 0xd7.U, 0xfb.U
		, 0x7c.U, 0xe3.U, 0x39.U, 0x82.U, 0x9b.U, 0x2f.U, 0xff.U, 0x87.U, 0x34.U, 0x8e.U, 0x43.U, 0x44.U, 0xc4.U, 0xde.U, 0xe9.U, 0xcb.U
		, 0x54.U, 0x7b.U, 0x94.U, 0x32.U, 0xa6.U, 0xc2.U, 0x23.U, 0x3d.U, 0xee.U, 0x4c.U, 0x95.U, 0x0b.U, 0x42.U, 0xfa.U, 0xc3.U, 0x4e.U
		, 0x08.U, 0x2e.U, 0xa1.U, 0x66.U, 0x28.U, 0xd9.U, 0x24.U, 0xb2.U, 0x76.U, 0x5b.U, 0xa2.U, 0x49.U, 0x6d.U, 0x8b.U, 0xd1.U, 0x25.U
		, 0x72.U, 0xf8.U, 0xf6.U, 0x64.U, 0x86.U, 0x68.U, 0x98.U, 0x16.U, 0xd4.U, 0xa4.U, 0x5c.U, 0xcc.U, 0x5d.U, 0x65.U, 0xb6.U, 0x92.U
		, 0x6c.U, 0x70.U, 0x48.U, 0x50.U, 0xfd.U, 0xed.U, 0xb9.U, 0xda.U, 0x5e.U, 0x15.U, 0x46.U, 0x57.U, 0xa7.U, 0x8d.U, 0x9d.U, 0x84.U
		, 0x90.U, 0xd8.U, 0xab.U, 0x00.U, 0x8c.U, 0xbc.U, 0xd3.U, 0x0a.U, 0xf7.U, 0xe4.U, 0x58.U, 0x05.U, 0xb8.U, 0xb3.U, 0x45.U, 0x06.U
		, 0xd0.U, 0x2c.U, 0x1e.U, 0x8f.U, 0xca.U, 0x3f.U, 0x0f.U, 0x02.U, 0xc1.U, 0xaf.U, 0xbd.U, 0x03.U, 0x01.U, 0x13.U, 0x8a.U, 0x6b.U
		, 0x3a.U, 0x91.U, 0x11.U, 0x41.U, 0x4f.U, 0x67.U, 0xdc.U, 0xea.U, 0x97.U, 0xf2.U, 0xcf.U, 0xce.U, 0xf0.U, 0xb4.U, 0xe6.U, 0x73.U
		, 0x96.U, 0xac.U, 0x74.U, 0x22.U, 0xe7.U, 0xad.U, 0x35.U, 0x85.U, 0xe2.U, 0xf9.U, 0x37.U, 0xe8.U, 0x1c.U, 0x75.U, 0xdf.U, 0x6e.U
		, 0x47.U, 0xf1.U, 0x1a.U, 0x71.U, 0x1d.U, 0x29.U, 0xc5.U, 0x89.U, 0x6f.U, 0xb7.U, 0x62.U, 0x0e.U, 0xaa.U, 0x18.U, 0xbe.U, 0x1b.U
		, 0xfc.U, 0x56.U, 0x3e.U, 0x4b.U, 0xc6.U, 0xd2.U, 0x79.U, 0x20.U, 0x9a.U, 0xdb.U, 0xc0.U, 0xfe.U, 0x78.U, 0xcd.U, 0x5a.U, 0xf4.U
		, 0x1f.U, 0xdd.U, 0xa8.U, 0x33.U, 0x88.U, 0x07.U, 0xc7.U, 0x31.U, 0xb1.U, 0x12.U, 0x10.U, 0x59.U, 0x27.U, 0x80.U, 0xec.U, 0x5f.U
		, 0x60.U, 0x51.U, 0x7f.U, 0xa9.U, 0x19.U, 0xb5.U, 0x4a.U, 0x0d.U, 0x2d.U, 0xe5.U, 0x7a.U, 0x9f.U, 0x93.U, 0xc9.U, 0x9c.U, 0xef.U
		, 0xa0.U, 0xe0.U, 0x3b.U, 0x4d.U, 0xae.U, 0x2a.U, 0xf5.U, 0xb0.U, 0xc8.U, 0xeb.U, 0xbb.U, 0x3c.U, 0x83.U, 0x53.U, 0x99.U, 0x61.U
		, 0x17.U, 0x2b.U, 0x04.U, 0x7e.U, 0xba.U, 0x77.U, 0xd6.U, 0x26.U, 0xe1.U, 0x69.U, 0x14.U, 0x63.U, 0x55.U, 0x21.U, 0x0c.U, 0x7d.U    
  ));
  
  val sb4 = rom(io.addr2(7,0))
  val sb5 = rom(io.addr2(15,8))
  val sb6 = rom(io.addr2(23,16))
  val sb7 = rom(io.addr2(31,24))
  
  val x1= Cat(sb7,sb6,sb5,sb4)
  
  val sb8 = rom(io.addr2(39,32))
  val sb9 = rom(io.addr2(47,40))
  val sb10 = rom(io.addr2(55,48))
  val sb11 = rom(io.addr2(63,56))
  
  val x2= Cat(sb11,sb10,sb9,sb8)
  
  val sb12 = rom(io.addr2(71,64))
  val sb13 = rom(io.addr2(79,72))
  val sb14 = rom(io.addr2(87,80))
  val sb15 = rom(io.addr2(95,88))
  
  val x3= Cat(sb15,sb14,sb13,sb12)
  
  val sb16 = rom(io.addr2(103,96))
  val sb17 = rom(io.addr2(111,104))
  val sb18 = rom(io.addr2(119,112))
  val sb19 = rom(io.addr2(127,120))
  
  val x4= Cat(sb19,sb18,sb17,sb16)
  
  io.data2 := Cat(x4,x3,x2,x1) //rom(io.addr)
 
}

object elaboratesbox extends App {
  chisel3.Driver.execute(args, () => new sbox)
}


