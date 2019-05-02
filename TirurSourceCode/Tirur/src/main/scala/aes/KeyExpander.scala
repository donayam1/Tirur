package aes

import chisel3._
import chisel3.util._


class expanderio extends Bundle{
  
  val sboxio = Flipped(new sbox_io3)    
  val round = Input(UInt(4.W))
  val data = Output(UInt(128.W)) 
  
  val key = Input(UInt(128.W))

  val done = Output(Bool())
  val dir = Input(Bool()) // ture -> enc, false decription  
}

class expander extends Module{
  val io = IO(new expanderio)
  //io := DontCare
  
  val pKey = RegInit(0.U(128.W))  
  val gDone = RegInit(false.B)//check 
  val tround = Wire(UInt(4.W))
  tround := 0.U
  
  val roundkeys = Reg(init = Vec(Seq.fill(11)(0.U(128.W)))) // init= Vec(Seq.fill(10)(0.U(128.W)))) //the ten round keys
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
  
  
  
  
  val prk = roundkeys(tround - 1.U)
  val prklast = prk(31,0)             
  
  val rtw = Cat(prklast(23,0),prklast(31,24)) // Rotate word
  
  
  io.sboxio.addr := rtw                 
  val swd = io.sboxio.data  // Sbox word
 
  
  val rnd = (tround<<2.U) //1 to 4, 2 to 8, 3 to 12 ....                 
  val rconw = Cat(rcon((rnd>>2)-1.U),0.U(24.W))    //RCON table look up   
  
  val fk = rconw ^ swd     //XOR RCON with sbox word           
  //val ek2 = 
  
  val k0 = fk ^ prk(127,96)         
  val k1 = k0 ^ prk(95,64)        
  val k2 = k1 ^ prk(63,32)        
  val k3 = k2 ^ prk(31,0)   
  
  val out = Cat(k0,k1,k2,k3)
  
  
  io.data := Mux(gDone,
      Mux(io.key === pKey,roundkeys(io.round),0.U),
      Mux(tround === 0.U,io.key,out))
  
  
  
  
  
  
  
  
  //val cKey = Reg(next = io.key) 
  //Handle KeyChange event 
  
    val round = io.round
    io.done := Mux(io.dir,true.B,gDone) // for ecnription it is generated per clock
                                        // for decription we need to check if keys have already been 
                                        // generated first  
    when(gDone === true.B)
    {
       //io.done := true.B
       val x = io.key === pKey         
       switch(x) // key changed 
        {
             is(false.B) // key has changed 
             {
               //io.done := false.B
               gDone := false.B
             }
             is(true.B) //key has not changed 
             {
               //printf(p"round after generation=0x${Hexadecimal(io.round)} \n" )
//               printf(p"round key is\n" )
//               
               //val key = roundkeys(io.round)//
               //io.data := key
               
               //printState(key)
               //io.done := true.B
               //gDone := true.B
             }
         }
       
    }.elsewhen(gDone === false.B)
    {          
      switch(io.dir)
      {
         is(true.B) // enc
         {
           tround := round
         }
         is(false.B) //dec 
         {
           tround := 10.U - round 
         }
      }
    //printf(p"tround=0x${Hexadecimal(tround)} and round = 0x${Hexadecimal(round)}\n" )
      when(tround === 0.U)
      {                          
          roundkeys(tround) := io.key 
          //io.data := io.key
          pKey := io.key 
      }.elsewhen(tround > 0.U){      
          
          /*val prk = roundkeys(tround - 1.U)
          val prklast = prk(31,0)             
          
          val rtw = Cat(prklast(23,0),prklast(31,24)) // Rotate word
          
          
          io.sboxio.addr := rtw                 
          val swd = io.sboxio.data  // Sbox word
         
          
          val rnd = (tround<<2.U) //1 to 4, 2 to 8, 3 to 12 ....                 
          val rconw = Cat(rcon((rnd>>2)-1.U),0.U(24.W))    //RCON table look up   
          
          val fk = rconw ^ swd     //XOR RCON with sbox word           
          //val ek2 = 
          
          val k0 = fk ^ prk(127,96)         
          val k1 = k0 ^ prk(95,64)        
          val k2 = k1 ^ prk(63,32)        
          val k3 = k2 ^ prk(31,0)   
          
          val out = Cat(k0,k1,k2,k3)
          io.data := out     */
          roundkeys(tround) := out
            
          
          //printf(p"rkey  =0x${Hexadecimal(tround)} \n" )
//          printState(out)
               
          
          gDone := Mux(tround === 10.U,true.B,false.B)
      }
  } 

        
}

object elaborateexp extends App {
  chisel3.Driver.execute(args, () => new expander)
}