package cpu

import chisel3._
import chisel3.util._

//register debug interface 
class mem_debug(data_width:Int,addr_width:Int) extends memory_io(data_width,addr_width)


class rfdebug(data_width:Int,addr_width:Int) extends memory_io (data_width,addr_width)


class Soc extends Module{
  
  val io=IO(new Bundle{
    val rst = Input(Bool())
    val rfd = new rfdebug(32,6)
    val mde = new mem_debug(32,15)
    //val mem = new  memory_io
  })
  io <> DontCare 
  
  val core    =  Module(new CpuCore)
  val mem     =  Module(new Memory(32,15))
  
  core.io.mem <> mem.io.inst 
  
  core.io.rfdeb <> io.rfd
  
  mem.io.debug <> io.mde 
  
  core.reset := io.rst
      
}

object elaborate extends App {
  chisel3.Driver.execute(args, () => new Soc)
}