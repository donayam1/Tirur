package aes

import chisel3._
import chisel3.util._

import aes._


class aesctr_io extends Bundle{
  val intVect = Input(UInt(128.W))
  val newR = Output(Bool())
  val out = Output(UInt(128.W))
  val data = Input(UInt(128.W))
  val reset = Input(Bool())
  val dir = Input(Bool()) /*true -> enc , false -> dec*/
  val en = Input(Bool())
  val key=Input(UInt(128.W))
}

class aesctr extends Module{
  
  val io = IO(new aesctr_io)  
  
  val aes = Module(new aes)
  
  
  aes.io.ed := io.dir //Input(Bool()) //true -> encr, fase -> decr
  aes.io.key := io.key //Input(UInt(128.W)) 
  aes.io.iData := io.intVect
  //printf("XordText=%x\n",xord)
  //printf(p"xord= 0x${Hexadecimal(xord)}\n")
  aes.io.en := io.en 
  io.newR := aes.io.done     
   
  io.out := Mux(aes.io.done, aes.io.result ^ io.data,0.U)
  
//  when(aes.io.done)
//  {
//     
//     val res = aes.io.result ^ io.data 
//     io.out := res
////     printf("result = %x\n",res)
//  }.otherwise{
//    io.out := 0.U
//  }
     
}