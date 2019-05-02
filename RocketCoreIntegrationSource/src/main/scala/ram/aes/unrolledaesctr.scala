package aes

import chisel3._
import chisel3.util._

import aes._


class uaesctr_io extends Bundle{
  val intVect = Input(UInt(128.W))
  val newR = Output(Bool())
  val out = Output(UInt(128.W))
  val data = Input(UInt(128.W))
  val reset = Input(Bool())
  val dir = Input(Bool()) /*true -> enc , false -> dec*/
  val en = Input(Bool())
  val key=Input(UInt(128.W))
}

class unrolledaesctr extends Module{
  
  val io = IO(new aesctr_io)  
  
  val aes = Module(new unrolledaes)
  when(io.en)
  {
    printf("**New\ninit vector = %x\n",io.intVect)
    printf("data = %x\n",io.data)
    printf("key = %x\n",io.key)
    printf("result = %x \n end new**\n",io.out)
  }
  aes.io.ed := io.dir //Input(Bool()) //true -> encr, fase -> decr
  aes.io.key := io.key //Input(UInt(128.W)) 
  aes.io.iData := io.intVect
  //printf("XordText=%x\n",xord)
  //printf(p"xord= 0x${Hexadecimal(xord)}\n")
  aes.io.en := io.en 
  io.newR := aes.io.done     
  val out = aes.io.result ^ io.data
  io.out := Mux(aes.io.done, out,0.U)
//  when(aes.io.done)
//  {
//     //printf("result = %x\n",aes.io.result)
//     val res = aes.io.result ^ io.data 
//     io.out := res          
//  }.otherwise{
//    io.out := 0.U
//  }
     
}