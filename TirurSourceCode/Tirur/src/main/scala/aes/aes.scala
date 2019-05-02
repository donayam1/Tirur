package aes 

import chisel3._
import chisel3.util._


class a_io extends Bundle{
  val ed = Input(Bool()) //true -> encr, fase -> decr
  val result = Output(UInt(128.W))
  val iData = Input(UInt(128.W))
  val key = Input(UInt(128.W))
  val done = Output(Bool())
  val en = Input(Bool())
  
}

class aes extends Module{
  val io = IO(new a_io)
  
  val encm =Module(new enc)
  val decm = Module(new dec)
  val ke = Module(new expander)
  val sb = Module(new sbox)
  
  /*io <> DontCare
  encm.io <> DontCare
  decm.io <> DontCare 
  ke.io <> DontCare 
  sb.io <> DontCare 
  */
  ke.io.key := io.key 
  ke.io.dir := io.ed 
  
  ke.io.sboxio.data := sb.io.data
  sb.io.addr := ke.io.sboxio.addr 
  
  sb.io.addr2 := encm.io.sio.addr2
  encm.io.sio.data2 := sb.io.data2
  
  encm.io.en := io.en & io.ed 
  decm.io.en := (io.en & (~io.ed)) | (~ke.io.done)
  
  //ke.io.start := io.en 
  io.done := Mux(io.en & io.ed,encm.io.done,
                Mux(io.en &(~io.ed),decm.io.done,false.B))
  
                
//  when( io.en )
//  {
    
  io.result := Mux(io.ed,encm.io.out,decm.io.out)
  encm.io.data := io.iData
  encm.io.rkey := ke.io.data
  encm.io.rkeyDone := ke.io.done
  ke.io.round :=Mux(io.ed,encm.io.roundOut,decm.io.roundOut)
  
  decm.io.data := io.iData
  decm.io.rkey := ke.io.data 
  decm.io.rkeyDone := ke.io.done
    
//    switch(io.ed)
//    {
//      is(true.B)
//      {        
//        io.result := encm.io.out  
//        encm.io.data := io.iData
//        encm.io.rkey := ke.io.data 
//        //io.done := encm.io.done   
//        encm.io.rkeyDone := ke.io.done 
//        ke.io.round := encm.io.roundOut
//        
//                       
//      }
//      is(false.B)
//      {
//        io.result := decm.io.out  
//        decm.io.data := io.iData
//        decm.io.rkey := ke.io.data 
//        decm.io.rkeyDone := ke.io.done 
//        //io.done := decm.io.done  
//        ke.io.round := decm.io.roundOut
//         
//                 
//      }
//    }
    
  //}
//  .otherwise{
//    io.done := false.B
//    io.result := 0.U
//  }  
}

object elaboratefullaes extends App{
  chisel3.Driver.execute(args,()=> new aes)
}

