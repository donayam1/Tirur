
/*
 * Performs Galios (2^128) multiplication in hardware 
 * */
package aes

import chisel3._
import chisel3.util._

class mul_io extends Bundle{
  val x = Input(UInt(128.W))
  val y = Input(UInt(128.W))
  val out = Output(UInt(128.W))
  //val en =Input(Bool())
}

class g128Multiplay extends Module{
  val io = IO(new mul_io)
  
  //val X=Wire(UInt(128.W))
 
  //Reg(init = Vec(Seq.fill(14)(0.U(32.W)))) 
  val Z = Vec(Seq.fill(129)(0.U(128.W)))
  val V = Vec(Seq.fill(129)(0.U(128.W)))
  V(0) := io.x
  val t=Seq.fill(1)(0.U(120.W))
  val R= Cat("he1".U,t(0))//"h10000111".U(128.W)//
//  printf("R=%b\n , z=%b\n, v=%b\n",R,Z(0),V(0))
//  printf("x=%b\n",io.x)
//  printf("y=%b\n",io.y)
//  
//  when(io.en)
//  {
    for( i <- 1 to 128)
    {    
  
      when(io.y(128-i) === 1.U)
      {      
        //printf("z xored\n")
        Z(i) := Z(i-1) ^ V(i-1)
      }.otherwise{
        //printf("z copied\n")
        Z(i) := Z(i-1)
      }
      when(V(i-1)(0) === 0.U)
      {
        //printf("Is Zero\n")
        V(i) := (V(i-1)>>1.U)(127,0)
      }.otherwise{
        //printf("Is not Zero\n")
        V(i) := ((V(i-1)>>1.U)^R)(127,0)
      }
      //printf("i=%x\n  , z=%b\n , v=%b\n",i.U,Z(i),V(i-1))
    }  
    //printf("z=%b\n",Z(128))    
  //}
  io.out := Z(128)  
}

object elaborategmult extends App{
  chisel3.Driver.execute(args,()=> new g128Multiplay)
}