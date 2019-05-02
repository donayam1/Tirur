package intgCheck

import chisel3._
import chisel3.util._


class VfRegSize {
  val ivWithMacSize = 256
  val ivSize    = 128  
}

object VfRegSize extends VfRegSize

/**
 * 
 * 
 * cmd 000 do noting 
 * cmd 001 read vector 
 * cmd 010 write vector 
 * cmd 011 read mac 
 * cmd 100 make vector as free 
 * cmd 101 make all vectors free 
 * cmd 110 get free vector address 
 * 
 * 
 **/
class ivFileMac_io(regwidth:Int) extends Bundle
{
    val rdata   = Output(UInt((regwidth).W))
    val wdata   = Input(UInt(( (regwidth*2) + 1).W)) 
    val addr    = Input(UInt(5.W))       
    val cmd     = Input(UInt(3.W))  
    //val en = Input(Bool())
   override def cloneType = { new ivFileMac_io(regwidth).asInstanceOf[this.type] } 
}


class ivFileMac(regWidth:Int) extends Module {
  val io = IO(new ivFileMac_io(regWidth))
  //io <> DontCare
  //generate 16 registers which are reset to zero on reset 
  val regmap  = Reg(init = Vec(Seq.fill(16)(0.U((regWidth+1).W))))
  val macreg  = Reg(init = Vec(Seq.fill(16)(0.U((regWidth).W))))
  
  val lastOut = RegInit(0.U(regWidth.W))
  regmap(0) := "h180004000200080104008a004d002".U  //on for page swapper tester 3
  macreg(0) := "h3de39d24188a846a636f37b0f69124e6".U
//  regmap(1) := "h3".U
//  regmap(2) := "h4".U
//  regmap(3) := "h5".U
//  printf(p"cmd= 0x${Hexadecimal(io.cmd)}\n")
//  printf(p"addr= 0x${Hexadecimal(io.addr)}\n")
//  printf(p"data= 0x${Hexadecimal(io.wdata)}\n")
//  
//    val x= LFSR16(true.B)
//    printf(p"rnd= 0x${Hexadecimal(x)}\n")
  
  //Search For empty slot 
  val num = Mux((regmap(0)(regWidth))===0.U,0.U,
                       Mux((regmap(1)(regWidth))===0.U,1.U,
                       Mux((regmap(2)(regWidth))===0.U,2.U,
                       Mux((regmap(3)(regWidth))===0.U,3.U,
                       Mux((regmap(4)(regWidth))===0.U,4.U,
                       Mux((regmap(5)(regWidth))===0.U,5.U,
                       Mux((regmap(6)(regWidth))===0.U,6.U,
                       Mux((regmap(7)(regWidth))===0.U,7.U,
                       Mux((regmap(8)(regWidth))===0.U,8.U,    
                       Mux((regmap(9)(regWidth))===0.U,9.U,  
                       Mux((regmap(10)(regWidth))===0.U,10.U,
                       Mux((regmap(11)(regWidth))===0.U,11.U,
                       Mux((regmap(12)(regWidth))===0.U,12.U,
                       Mux((regmap(13)(regWidth))===0.U,13.U,  
                       Mux((regmap(14)(regWidth))===0.U,14.U, 
                       Mux((regmap(15)(regWidth))===0.U,15.U,16.U     
             ))))))))))))))))
  
                
   //io.rdata := lastOut
   val rout  = MuxLookup(
        io.cmd,
        lastOut,
        Array(
          1.U -> Mux(io.addr <=15.U, regmap(io.addr),macreg(io.addr)), //read IV 
          3.U -> macreg(io.addr), //read mac 
          6.U -> num          //look for empty slot 
        )
  )
  val cmdRead = io.cmd === 1.U || io.cmd === 3.U || io.cmd === 6.U 
  
  io.rdata := rout
  lastOut := Mux(cmdRead,rout,lastOut)
  
  //printf("read data=%x\n",rout)
  //printf("last out=%x\n",lastOut)    
  //printf("cmd =%x\n",io.cmd)
  //printf("addr =%x\n",io.addr)
  
//  printf("data =%x\n",io.rdata)
//  printf("^^^^^\ndata =%x\n",io.rdata)
    when(io.cmd === 2.U) //write 
    {
        val upperHalf = io.wdata((regWidth<<1),regWidth) 
        val lowerHalf =io.wdata((regWidth-1),0)
        //printf("address=%x,lowerHalf =%x,upperHalf=%x\n",io.addr,lowerHalf,upperHalf)
        when(io.addr <= 15.U)
        {
          regmap(io.addr) := upperHalf//io.wdata((regWidth<<1),regWidth)         
          macreg(io.addr) := lowerHalf//io.wdata((regWidth-1),0)
          //printf("writting mac = %x  and iv=%x\n",lowerHalf,upperHalf)
        }.otherwise{ //used during vf page in operation
          macreg(io.addr) := lowerHalf
          //printf("writting mac %x=",lowerHalf)
        }
        
    }.elsewhen(io.cmd === 5.U)
    {
      for(i <- 0 to 15) //reset all to zero
      {
           regmap(i) := 0.U
           macreg(i) := 0.U
      }
    }           
}
