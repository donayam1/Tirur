package aes 

import chisel3._
import chisel3.util._


class ua_io extends Bundle{
  val ed = Input(Bool()) //true -> encr, fase -> decr
  val result = Output(UInt(128.W))
  val iData = Input(UInt(128.W))
  val key = Input(UInt(128.W))
  val done = Output(Bool())
  val en = Input(Bool())
  
}

class unrolledaes extends Module{
  val io = IO(new ua_io)
  
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
  val rcon = Vec(Array(
      0x01.U,
	    0x02.U,
		  0x04.U,
		  0x08.U,
		  0x10.U,
		  0x20.U,
		  0x40.U,
		  0x80.U,
		  0x1B.U,
		  0x36.U,
		  0x6C.U,
		  0xD8.U,
		  0xAB.U,
		  0x4D.U,
		  0x9A.U
    ));
  
  //val res = RegInit(0.U(128.W))//output result register will introduce one clock delay due to register assignment
  val roundKey = Vec(Seq.fill(11)(0.U(128.W)))
  val out = Vec(Seq.fill(11)(0.U(128.W)))
      
  val prk =      Vec(Seq.fill(11)(0.U(128.W)))
  val prklast =  Vec(Seq.fill(11)(0.U(32.W)))
  val rtw =  Vec(Seq.fill(11)(0.U(32.W)))
  val swd =  Vec(Seq.fill(11)(0.U(32.W)))
  val rnd =  Vec(Seq.fill(11)(0.U(8.W)))
  val rconw =Vec(Seq.fill(11)(0.U(32.W)))
  val fk = Vec(Seq.fill(11)(0.U(32.W)))
  val k0 = Vec(Seq.fill(11)(0.U(32.W)))
  val k1 = Vec(Seq.fill(11)(0.U(32.W)))
  val k2 = Vec(Seq.fill(11)(0.U(32.W)))
  val k3 = Vec(Seq.fill(11)(0.U(32.W)))
  
        
  val temp1  = Vec(Seq.fill(11)(0.U(128.W)))
  val state1 = Vec(Seq.fill(11)(0.U(128.W)))
  val temp2  = Vec(Seq.fill(11)(0.U(128.W)))
  val state3 = Vec(Seq.fill(11)(0.U(128.W)))
  
  io.done := io.en 
  io.result := temp2(0)
  //res := Mux(io.en,temp2(0),res)
  when(io.en)
  {
      //first round
      
      roundKey(0) := io.key
      out(0) := addRoundKey(roundKey(0),io.iData)
//      printf("Key %x\n",roundKey(0))
//      printf("Data %x\n",io.iData)
//      printf("Out 0 = %x, Key = %x\n",out(0),roundKey(0))
//      
      for(i <- 1 to 9)
      {
         //Generate roundkey
         prk(i) := roundKey(i-1)
         prklast(i) := prk(i)(31,0)   
         rtw(i) := Cat(prklast(i)(23,0),prklast(i)(31,24)) // Rotate word
         swd(i) := byteSub32(rtw(i))
         
         rnd(i) := (i.U<<2.U) //1 to 4, 2 to 8, 3 to 12 ....                 
         rconw(i) := Cat(rcon((rnd(i)>>2)-1.U),0.U(24.W))    //RCON table look up   
              
         fk(i) := rconw(i) ^ swd(i)     //XOR RCON with sbox word                     
         k0(i) := fk(i) ^ prk(i)(127,96)         
         k1(i) := k0(i) ^ prk(i)(95,64)        
         k2(i) := k1(i) ^ prk(i)(63,32)        
         k3(i) := k2(i) ^ prk(i)(31,0)        
         roundKey(i) := Cat(k0(i),k1(i),k2(i),k3(i))
         
         //Encrypt 
         temp1(i) := byteSub128(out(i-1))
         state1(i) := shiftRow(temp1(i))
         temp2(i) := mixColums(state1(i))
         state3(i) := addRoundKey(roundKey(i),temp2(i))
         out(i) := state3(i)
         //printf("Out %x = %x, Key = %x\n",i.U,out(i),roundKey(i))
      }
      
       //final round
       //Generate roundkey
       prk(0) := roundKey(9)
       prklast(0) := prk(0)(31,0)   
       rtw(0) := Cat(prklast(0)(23,0),prklast(0)(31,24)) // Rotate word
       swd(0) := byteSub32(rtw(0))
       
       rnd(0) := (10.U<<2.U) //1 to 4, 2 to 8, 3 to 12 ....                 
       rconw(0) := Cat(rcon((rnd(0)>>2)-1.U),0.U(24.W))    //RCON table look up   
            
       fk(0) := rconw(0) ^ swd(0)     //XOR RCON with sbox word                     
       k0(0) := fk(0) ^ prk(0)(127,96)         
       k1(0) := k0(0) ^ prk(0)(95,64)        
       k2(0) := k1(0) ^ prk(0)(63,32)        
       k3(0) := k2(0) ^ prk(0)(31,0)        
      roundKey(10) := Cat(k0(0),k1(0),k2(0),k3(0))
      
      
      temp1(0)   := byteSub128(out(9))
      state1(0)  := shiftRow(temp1(0))
      temp2(0)   := addRoundKey(roundKey(10),state1(0))
      //printf("Out 10 = %x, Key = %x\n",temp2(0),roundKey(10))
      //res := temp2(0)
      //printf("result is=%x\n",temp2(0))
      //printf("result is regval=%x\n",res)
  }
  
  
  def byteSub32(addr:UInt):UInt={
    val sb0 = rom(addr(7,0))
    val sb1 = rom(addr(15,8))
    val sb2 = rom(addr(23,16))
    val sb3 = rom(addr(31,24))
    
    val ans = Cat(sb3,sb2,sb1,sb0) //rom(io.addr)
    return ans
  }
  def byteSub128(addr2:UInt):UInt={
    
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
    
    val ans= Cat(x4,x3,x2,x1) //rom(io.addr)
    return ans
  }
  
  def addRoundKey(roundkey:UInt,state:UInt):UInt={
      val addKey = state^roundkey
      return addKey
  }
  
  def shiftRow(state:UInt):UInt={
    
  	//val newa0= state(7,0);//nochange to state zero
    //val nstate = Reg(UInt(128.W))
  	//newa[1]= state[5];
   // val state1= state(15,8)

   val x1 = Cat(
        state(31,24),
        state(119,112),
        state(79,72),        
        state(39,32)
        )    
   val x2= 
     Cat(state(63,56),
         state(23,16),
        
        state(111,104),
        state(71,64)
        )
   val x3 = Cat(
         state(95,88),
         state(55,48),            
         state(15,8),
         state(103,96) 
   )
  var x4= Cat(
        state(127,120),        
        state(87,80),
        state(47,40),
        state(7,0)
        )
        
    return Cat(x4,x3,x2,x1)        
  }
  def galois_mul2(value:UInt):UInt={

       val anst=(( (value << 1.U) ^ "h1b".U))(7,0)
       val ansf=((value<<1.U))(7,0); 
       val cond=((value & "h80".U) === "h80".U)
       return Mux(cond,anst,ansf)
  
  }
  def mixColums(state:UInt):UInt={
    
//    val state1=state(7,0)
//    val state2=state(15,8)
//    val state3=state(23,16)
//    val state4=state(31,24)
    
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
    

//    val state11 = state(39,32)
//    val state21 = state(47,40)
//    val state31 = state(55,48)
//    val state41 = state(63,56)
    
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
    
    
    
    
//    val state12=state(71,64)
//    val state22=state(79,72)
//    val state32=state(87,80)
//    val state42=state(95,88)
      
    
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
    
    
    
    
//    val state13=state(103,96)
//    val state23=state(111,104)
//    val state33=state(119,112)
//    val state43=state(127,120)
    
    
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
  
}

object elaboratefulluaes extends App{
  chisel3.Driver.execute(args,()=> new unrolledaes)
}

