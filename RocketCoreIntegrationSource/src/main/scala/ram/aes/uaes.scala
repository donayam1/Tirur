package aes 

import chisel3._
import chisel3.util._


class ua_io2 extends Bundle{
  
  val result = Output(UInt(128.W))
  val done = Output(Bool())
  
  val ed = Input(Bool()) //true -> encr, fase -> decr
  val iData = Input(UInt(128.W))
  val key = Input(UInt(128.W))  
  val en = Input(Bool())
  
}

class uaes extends Module{
  val io = IO(new ua_io2)
  
  val encm =Module(new unrolledaesenc)
  val decm = Module(new udec)
    
  encm.io.en := io.en & io.ed 
  decm.io.en := (io.en & (~io.ed))
  
  //ke.io.start := io.en 
  io.done := Mux(io.en & io.ed,encm.io.done,
                Mux(io.en &(~io.ed),decm.io.done,false.B))
                     
  io.result := Mux(io.ed,encm.io.result,decm.io.out)
  encm.io.iData := io.iData
  encm.io.key := io.key
  //printf("data outer= %x\n",io.iData)
  
  decm.io.data := io.iData
  decm.io.rk0  := encm.io.rk0
  decm.io.rk1  := encm.io.rk1
  decm.io.rk2  := encm.io.rk2
  decm.io.rk3  := encm.io.rk3
  decm.io.rk4  := encm.io.rk4
  decm.io.rk5  := encm.io.rk5
  decm.io.rk6  := encm.io.rk6
  decm.io.rk7  := encm.io.rk7
  decm.io.rk8  := encm.io.rk8
  decm.io.rk9  := encm.io.rk9
  decm.io.rk10 := encm.io.rk10
  
}


object elaboratefulluaes1 extends App{
  chisel3.Driver.execute(args,()=> new uaes)
}

