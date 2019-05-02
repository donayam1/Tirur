package intgCheck

import chisel3._
import chisel3.util._
import freechips.rocketchip.util._

import memory._
import aes._
class psgcm2_io extends Bundle{
   //ps interface to the system bus  
   //the data memory + 1 extra bit for configuration ports
   val mainMemio = new memory_io(MemParams.dataPortWidth,(MemParams.dataaddrPortWidth+1))  
  

   val err = Output(Bool())
   val currOutReady = Output(Bool())  
   val finished = Output(Bool())
}
class pageswappergcm2 extends Module{
  val io = IO(new psgcm2_io)  

   val mem = SeqMem(MemParams.MemSizeInWord,Vec(4,UInt(8.W)))//SyncReadMem
  
  val ConfReg = Reg(init = Vec(Seq.fill(14)(0.U(32.W))))   

  val currOperation = ConfReg(0)(3,1)
  
  io.currOutReady := true.B
    
                                 
  io.err := false.B
  io.finished := true.B 


  val socmselect = io.mainMemio.addr < MemParams.MemSizeInWord.U//((io.mainMemio.addr & (1.U<<(MemParams.dataaddrPortWidth.U))) === 0.U)
  
  
  
  val addr    = io.mainMemio.addr
  val wdata   = Vec.tabulate(4) { i => io.mainMemio.wdata(8*(i+1)-1, 8*i) }  
  val valid   = Mux((currOperation === 5.U) && socmselect, io.mainMemio.valid,false.B)
  val we      = Mux((currOperation === 5.U) && socmselect, io.mainMemio.we,false.B)
  val en      = Mux((currOperation === 5.U) && socmselect, io.mainMemio.en,false.B)
  val wmaskd  = "b1111".U 
  
  when(en && we)
  {
    mem.write(addr,wdata,wmaskd.toBools)
  }
  val ren = en && !we
  val dv = mem.readAndHold(addr,ren)
    
  
  io.mainMemio.rdata  := Mux((currOperation === 5.U) && socmselect,dv.asUInt,ConfReg(io.mainMemio.addr(3,0))) 
  io.mainMemio.ready  := en
    

 
  val confMask = MuxLookup(io.mainMemio.addr(3,0),
           0.U(32.W),
           Array(
           9.U -> "b011111".U(32.W)    
           )                
  )
  
  when(io.mainMemio.en)
  {   
    when(io.mainMemio.valid)
    {
        //printf("main memory enabled and valid.\n")
        when(~socmselect)
        {
           when(io.mainMemio.we)
           {
             val data = (io.mainMemio.wdata & (~confMask))
             //printf("mask = %x\n",(~confMask))
             //printf("input data = %x\n",io.mainMemio.wdata)
             //printf("Writting to config addr=%x , value= %x\n",io.mainMemio.addr(3,0),data)
             val cond0 = confMask === 0.U
             when(~cond0)
             {             
               ConfReg(io.mainMemio.addr(3,0)) := (ConfReg(io.mainMemio.addr(3,0))|data)
               
             }.otherwise{
               ConfReg(io.mainMemio.addr(3,0)) := data
             }
             
             val cond = ((currOperation === 2.U) || (currOperation === 4.U) || (currOperation === 0.U)) && (io.mainMemio.addr(3,0) === 8.U)
             when(cond)
             {               
               ConfReg(9) := ConfReg(9) | (1.U<<5.U)  
             }
             
           }.otherwise{
             val cond2 = ((currOperation === 1.U) || (currOperation === 3.U) || (currOperation === 0.U)) && (io.mainMemio.addr(3,0) === 8.U)
             when(cond2)
             {          
               printf("Config 9 read ")
               ConfReg(9) := ConfReg(9) | (1.U<<6.U)  
             }
             
             //io.mainMemio.rdata := ConfReg(io.mainMemio.addr(3,0))
             //printf("reading data from config addr=%x , value= %x\n",io.mainMemio.addr(3,0),ConfReg(io.mainMemio.addr(3,0)))
           }
        }       
    }
  }
  
      
         
}


object elaboratepsgcm2 extends App{
  chisel3.Driver.execute(args,()=> new pageswappergcm2)
}