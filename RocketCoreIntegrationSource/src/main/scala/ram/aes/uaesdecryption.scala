
package aes

import chisel3._
import chisel3.util._

class uaes_dec_io extends Bundle{
 

  val out = Output(UInt(128.W))
  val done = Output(Bool())
  
  val data = Input(UInt(128.W))
  val en = Input(Bool())
  
  
  val rk0 = Input(UInt(128.W))
  val rk1 = Input(UInt(128.W))
  val rk2 = Input(UInt(128.W))
  val rk3 = Input(UInt(128.W))
  val rk4 = Input(UInt(128.W))
  val rk5 = Input(UInt(128.W))
  val rk6 = Input(UInt(128.W))
  val rk7 = Input(UInt(128.W))
  val rk8 = Input(UInt(128.W))
  val rk9 = Input(UInt(128.W))
  val rk10 = Input(UInt(128.W))
  
}

class udec extends Module{
  val io =IO(new uaes_dec_io)
  

  val res = RegInit(0.U(128.W))
  io.done := io.en
  io.out := res 
  
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
  
  val roundKey = Vec(Seq.fill(11)(0.U(128.W)))
  
   roundKey(0) := io.rk0 
   roundKey(1) := io.rk1
   roundKey(2) := io.rk2
   roundKey(3) := io.rk3
   roundKey(4) := io.rk4
   roundKey(5) := io.rk5
   roundKey(6) := io.rk6
   roundKey(7) := io.rk7
   roundKey(8) := io.rk8
   roundKey(9) := io.rk9
   roundKey(10) := io.rk10 
  
  
  val temp1  = Vec(Seq.fill(11)(0.U(128.W)))
  val state1 = Vec(Seq.fill(11)(0.U(128.W)))
  val temp2  = Vec(Seq.fill(11)(0.U(128.W)))
  val state3 = Vec(Seq.fill(11)(0.U(128.W)))
  
 when(io.en)
 {
  //first round
    temp1(0) := addRoundKey(roundKey(10),io.data)
    state1(0) := shiftRow(temp1(0))
    temp2(0) := byteSubs(state1(0))
            
   
    for(i <- 1 to 9)
    {
      
      temp1(i) := addRoundKey(roundKey(10-i),temp2(i-1))
      state1(i) := mixColums(temp1(i))
      state3(i) := shiftRow(state1(i))
      temp2(i) := byteSubs(state3(i))
        
    }
    
   state3(0) := addRoundKey(roundKey(0),temp2(9))
       
   res := state3(0)

 }
  
  
  
  
  
  
def byteSubs(addr2:UInt):UInt = {
  val sb4 = rom(addr2(7,0))
  val sb5 = rom(addr2(15,8))
  val sb6 = rom(addr2(23,16))
  val sb7 = rom(addr2(31,24))
  
  val x1= Cat(sb7,sb6,sb5,sb4)
  
  val sb8 = rom(addr2(39,32))
  val sb9 = rom(addr2(47,40))
  val sb10 = rom(addr2(55,48))
  val sb11 = rom(addr2(63,56))
  
  val x2= Cat(sb11,sb10,sb9,sb8)
  
  val sb12 = rom(addr2(71,64))
  val sb13 = rom(addr2(79,72))
  val sb14 = rom(addr2(87,80))
  val sb15 = rom(addr2(95,88))
  
  val x3= Cat(sb15,sb14,sb13,sb12)
  
  val sb16 = rom(addr2(103,96))
  val sb17 = rom(addr2(111,104))
  val sb18 = rom(addr2(119,112))
  val sb19 = rom(addr2(127,120))
  
  val x4= Cat(sb19,sb18,sb17,sb16)
  
  return Cat(x4,x3,x2,x1);
}  
  
def galois_mul2(value:UInt):UInt={
       //printf(p"value=0x${Hexadecimal(value)} \n" )
       //val regs= Reg(UInt(8.W))
       val anst=(( (value << 1.U) ^ "h1b".U))(7,0)
       val ansf=((value<<1.U))(7,0); 
       val cond=((value & "h80".U) === "h80".U)
       return Mux(cond,anst,ansf)
  } 
 def addRoundKey(roundkey:UInt,state:UInt):UInt={
      val addKey = state^roundkey
      return addKey
  }
 def mixColums(stateor:UInt):UInt={
   
	    //col1
   
    val st0 = stateor(127,120)
    val st1 = stateor(119,112)
    val st2 = stateor(111,104)
    val st3 = stateor(103,96)
   
    val st7=stateor(71,64)
    val st6=stateor(79,72)
    val st5=stateor(87,80)
    val st4=stateor(95,88)
   
    val st11=stateor(39,32)
    val st10=stateor(47,40)
    val st9=stateor(55,48)
    val st8=stateor(63,56)
    
    val st15=stateor(7,0)
    val st14=stateor(15,8)
    val st13=stateor(23,16)
    val st12=stateor(31,24)
    
   
  val tb1in = st0 ^ st2
  val g1= galois_mul2(tb1in)
  val sbuf1 = galois_mul2(g1)
  val tb2in = st1^st3
  val g2 = galois_mul2(tb2in)
  val sbuf2 = galois_mul2(g2)
  
	val nst0 = st0 ^ sbuf1     
	val nst1 = st1 ^ sbuf2    
	val nst2 = st2 ^ sbuf1    
	val nst3 = st3 ^ sbuf2
	//col2
	val sbuf3 = galois_mul2(galois_mul2(st4^st6));
	val sbuf4 = galois_mul2(galois_mul2(st5^st7));
	val nst4 = st4 ^ sbuf3;    
	val nst5 = st5 ^ sbuf4;    
	val nst6 = st6 ^ sbuf3;    
	val nst7 = st7 ^ sbuf4;
	//col3
	val sbuf5 = galois_mul2(galois_mul2(st8^st10));
	val sbuf6 = galois_mul2(galois_mul2(st9^st11));
	val nst8  = st8  ^ sbuf5;    
	val nst9  = st9  ^ sbuf6;   
	val nst10 = st10 ^ sbuf5;   
	val nst11 = st11 ^ sbuf6;
	//col4
	val sbuf7 = galois_mul2(galois_mul2(st12^st14));
	val sbuf8 = galois_mul2(galois_mul2(st13^st15));
	val nst12 = st12 ^ sbuf7;    
	val nst13 = st13 ^ sbuf8;   
	val nst14 = st14 ^ sbuf7;    
	val nst15 = st15 ^ sbuf8;

	
	val hb1 = Cat(nst0,nst1,nst2,nst3)
	val hb2 = Cat(nst4,nst5,nst6,nst7)
	val hb3 = Cat(nst8,nst9,nst10,nst11)
	val hb4 = Cat(nst12,nst13,nst14,nst15)
	
  val state = Cat(hb1,hb2,hb3,hb4)
   
  
	
	
	
	  val state4=state(103,96)
    val state3=state(111,104)
    val state2=state(119,112)
    val state1=state(127,120)  
    
    
//    printf(p"state1=0x${Hexadecimal(state1)} \n" )
//    printf(p"state2=0x${Hexadecimal(state2)} \n" )
//    printf(p"state3=0x${Hexadecimal(state3)} \n" )
//    printf(p"state4=0x${Hexadecimal(state4)} \n" )
//    
    val buf1 = state1 ^ state2 ^ state3 ^ state4
    //printf(p"buf1=0x${Hexadecimal(buf1)} \n" )
    val buf2 = state1
      val buf3 = state1^state2
       //printf(p"gin=0x${Hexadecimal(buf3)} \n" )
      val buf4=galois_mul2(buf3)
       //printf(p"galois=0x${Hexadecimal(buf4)} \n" )
      val res0 = state1 ^ buf4 ^ buf1
       //printf(p"res0=0x${Hexadecimal(res0)} \n" )
    
        val buf7 = state2^state3
         //printf(p"buf7=0x${Hexadecimal(buf7)} \n" )
        val buf5 = galois_mul2(buf7) 
         //printf(p"galoisb7=0x${Hexadecimal(buf5)} \n" )
        val res1 = state2 ^ buf5 ^ buf1
        //printf(p"res1=0x${Hexadecimal(res1)} \n" )
    
    
    val buf8 = state3^state4
    val buf6=galois_mul2(buf8)
    val res2 = state3 ^ buf6 ^ buf1
    //printf(p"res2=0x${Hexadecimal(res2)} \n" )
    
      val buf10 = state4^buf2     
      val buf9=galois_mul2(buf10) 
      val res3 = state4 ^ buf9 ^ buf1
    //printf(p"res3=0x${Hexadecimal(res3)} \n" )
    
    val S0= Cat(res0,res1,res2,res3)
    
    //printf(p"resS0=0x${Hexadecimal(S0)} \n" )
    

    val state41=state(71,64)
    val state31=state(79,72)
    val state21=state(87,80)
    val state11=state(95,88)
    
      
    val buf11 = state11 ^ state21 ^ state31 ^ state41
    val buf21 = state11
      val buf31 = state11^state21
      val buf41=galois_mul2(buf31)
      val res01 = state11 ^ buf41 ^ buf11
        val buf71 = state21^state31
        val buf51 = galois_mul2(buf71) 
        val res11 = state21 ^ buf51 ^ buf11
    val buf81 = state31^state41
    val buf61=galois_mul2(buf81)
    val res21 = state31 ^ buf61 ^ buf11
      val buf101 = state41^buf21     
      val buf91=galois_mul2(buf101) 
      val res31 = state41 ^ buf91 ^ buf11
    
    val S1= Cat(res01,res11,res21,res31)
    
    
    
      val state42 = state(39,32)
      val state32 = state(47,40)
      val state22 = state(55,48)
      val state12 = state(63,56)
    
    
    val buf12 = state12 ^ state22 ^ state32 ^ state42
    val buf22 = state12
      val buf32 = state12^state22
      val buf42=galois_mul2(buf32)
      val res02 = state12 ^ buf42 ^ buf12
        val buf72 = state22^state32
        val buf52 = galois_mul2(buf72) 
        val res12 = state22 ^ buf52 ^ buf12
    val buf82 = state32^state42
    val buf62=galois_mul2(buf82)
    val res22 = state32 ^ buf62 ^ buf12
      val buf102 = state42^buf22     
      val buf92=galois_mul2(buf102) 
      val res32 = state42 ^ buf92 ^ buf12
    
    val S2= Cat(res02,res12,res22,res32)
    
    
    
    val state43=state(7,0)
    val state33=state(15,8)
    val state23=state(23,16)
    val state13=state(31,24)
    
    
    
    val buf13 = state13 ^ state23 ^ state33 ^ state43
    val buf23 = state13
      val buf33 = state13^state23
      val buf43=galois_mul2(buf33)
      val res03 = state13 ^ buf43 ^ buf13
        val buf73 = state23^state33
        val buf53 = galois_mul2(buf73) 
        val res13 = state23 ^ buf53 ^ buf13
    val buf83 = state33^state43
    val buf63=galois_mul2(buf83)
    val res23 = state33 ^ buf63 ^ buf13
      val buf103 = state43^buf23     
      val buf93=galois_mul2(buf103) 
      val res33 = state43 ^ buf93 ^ buf13
    
    val S3= Cat(res03,res13,res23,res33)
    
    
    
    
    
    val ans= Cat(S0,S1,S2,S3)
    
    
    
    
    return ans	
 }
 
 def shiftRow(state:UInt):UInt={
   
     val x1 = Cat(
          state(31,24),
          state(55,48),
          state(79,72),        
          state(103,96)
          )    
     val x2= 
       Cat(
           state(63,56),
           state(87,80),
           state(111,104),
           state(7,0)
          )
     val x3 = Cat(
           state(95,88),
           state(119,112),            
           state(15,8),
           state(39,32) 
     )
    var x4= Cat(
          state(127,120), 
          state(23,16),
          state(47,40),                 
          state(71,64)
          )
        
    return Cat(x4,x3,x2,x1)
 }
 
// def printState(state:UInt){
//  for(i <- Array(3,2,1,0) )//addRoundKey(io.rkey,io.data)
//  {
//    for( j <- Array(3,2,1,0) )
//    {
//      val x=state(i*32 + j*8 + 7,i*32 + j*8);
//      printf(p"0x${Hexadecimal(x)} ")
//      //printf(p"%x ", x)
//    }
//     printf("\n") 
//  }
//} 
   
}







object elaborateaesudec extends App{
  chisel3.Driver.execute(args,()=> new udec)
}


