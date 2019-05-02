package aes

import chisel3._
import chisel3.util._


class aes_enc_io extends Bundle{
   val rkey = Input(UInt(128.W))
   val done = Output(Bool())
   val sio = Flipped(new sbox_io2)
   val out = Output(UInt(128.W))
   val roundOut = Output(UInt(4.W))
   val data = Input(UInt(128.W))
   val rkeyDone = Input(Bool())
   val en = Input(Bool())
} 

class enc extends Module{
  val io= IO(new aes_enc_io)
  //io <> DontCare
  
  val roundCounter = RegInit(15.U(4.W))
  val state = RegInit(0.U(128.W))
  io.done := Mux(io.en,roundCounter === 10.U,false.B)
  
  //when(true.B)
  //{
    
      //printf(p"round= 0x${Hexadecimal(roundCounter)}\n")
//      printf("data\n")
//      printState(io.data)
//      printf("roundkey\n")
//      printState(io.rkey)
//     
      
//  when(io.en === false.B)
//  {
//    roundCounter := 0.U
//  }

//  val rg = RegInit(false.B)
//  rg := io.en
  roundCounter := Mux(io.en,roundCounter + 1.U,0.U)  
  io.roundOut := Mux(io.en,roundCounter,0.U) 
  
  val t= addRoundKey(io.rkey,io.data)//io.rkey ^ io.data
  
  
           
        io.sio.addr2 := state
        val state0 = io.sio.data2 
        val state1 = shiftRow(state0)
        val state2 = mixColums(state1)
        val state3 = addRoundKey(io.rkey,state2)

  
          io.sio.addr2 := state          
          val state01 = io.sio.data2 
          val state11 = shiftRow(state01)
          val state31 = addRoundKey(io.rkey,state11)
          state := state31
          //io.out := state31 
        
  
  
  val output = MuxLookup(roundCounter,
      state3
      ,
      Array(
        0.U -> t,
        10.U -> state31
      ))
  
  io.out := Mux(io.en && io.rkeyDone,output,0.U)    
      
  
  when(io.en)
  {
    //printf("aes - enable = %x\n",io.en)
    //printf(p"round = 0x${Hexadecimal(roundCounter)}\n")
    when(io.rkeyDone)
    {
        
        when(roundCounter === 0.U)
        {
          //printf(p"0 round counter= 0x${Hexadecimal(roundCounter)}\n")
            //val t= addRoundKey(io.rkey,io.data)//io.rkey ^ io.data
            state := t
            
            
  //          printf("state\n")
  //          printState(t)
            //io.out := t
//            printf("enc-data=%x\n",io.data)
//            printf("0 round counter= %x out=%x\n",roundCounter,t)
                      
        }.elsewhen((roundCounter>0.U) & (roundCounter <= 9.U)){
         
          //byte sub
//          io.sio.addr2 := state
//          val state0 = io.sio.data2 
  //        printf("byte sub\n")
  //        printState(state0)
          //shift rows
          //val state1 = shiftRow(state0)
  //        printf("shift rows\n")
  //        printState(state1)
          
          //mix columns 
          //val state2 = mixColums(state1)
  //        printf("mix columns\n")
  //        printState(state2)
  //        
          //add round key
          //val state3 = addRoundKey(io.rkey,state2)
  //        printf("add round key\n")
  //        printState(state3)
  //        
          //printf("other round counter= %x out=%x\n",roundCounter,state3)
          state := state3
          //io.out := state3
               
        }.elsewhen(roundCounter === 10.U)
        {
          //io.done := true.B       
          //io.sio.addr2 := state
          roundCounter := 0.U
          //val state01 = io.sio.data2 
  //        printf("byte sub\n")
  //        printState(state01)
  //        //shift rows
          //val state11 = shiftRow(state01)
  //        printf("shift rows\n")
  //        printState(state11)
  //                
          //add round key
         // val state31 = addRoundKey(io.rkey,state11)
  //        printf("add round key\n")
  //        printState(state31)
  //                
          state := state31
          //io.out := state31  
          //printf("done enc^^^^^^^^^^^^^^%x\n",state31)
        }
      
    }  
  }
//  .otherwise{
//    roundCounter := 15.U
//  }
            
  //}
  
  
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
       //printf(p"value=0x${Hexadecimal(value)} \n" )
       //val regs= Reg(UInt(8.W))
       val anst=(( (value << 1.U) ^ "h1b".U))(7,0)
       val ansf=((value<<1.U))(7,0); 
       val cond=((value & "h80".U) === "h80".U)
       return Mux(cond,anst,ansf)
//       if ((value & "h80".U) == "h80".U)
//        {
//         val ans=(( (value << 1.U) ^ "h1b".U)&"hff".U)
//         printf(p"valueif=0x${Hexadecimal(ans)} \n" )
//      		return ans
//      		//return (value^0x1b);
//      	}
//       else
//    		  return ((value<<1.U)&"hff".U); 
       
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
//  def printState(state:UInt){
//    for(i <- Array(3,2,1,0) )//addRoundKey(io.rkey,io.data)
//    {
//      for( j <- Array(3,2,1,0) )
//      {
//        val x=state(i*32 + j*8 + 7,i*32 + j*8);
//        printf(p"0x${Hexadecimal(x)} ")
//        //printf(p"%x ", x)
//      }
//       printf("\n") 
//    }
//  }  
}

object elaborateaes extends App{
  chisel3.Driver.execute(args,()=> new enc)
}
