package memory

import chisel3._
import chisel3.util._


class MemParams{
  val MemWordWidth = 32
  val MemSize = 32*1024*8  //In bits //8Kx32bit 
  val MemSizeInWord  = MemSize/MemWordWidth
  val SOCAddressSize = 0xffff  
  
  val dataPortWidth = MemWordWidth
  val dataaddrPortWidth = log2Ceil(MemSizeInWord)
  
  val BlockSize = 4
  
  val MemSizeInBlock = MemSize/(BlockSize*MemWordWidth)
	val blockPortWidth = BlockSize*MemWordWidth;
	val blockAddrPortWidth = log2Ceil(MemSizeInBlock);     
		
}

object MemParams extends MemParams 

class memory_io(data_width:Int,addr_width:Int) extends Bundle{
  
  val addr  = Input(UInt(addr_width.W)) // 32K memory 2^5 * 2^10 
  val valid = Input(Bool())
  
  val wdata = Input(UInt(data_width.W))
  val we = Input(Bool())
  val en = Input(Bool())
    
  val rdata = Output(UInt(data_width.W))
  val ready = Output(Bool())
  
  override def cloneType = { new memory_io(data_width,addr_width).asInstanceOf[this.type] }

}



class Memory extends Module{
    val io = IO(
        new Bundle{
            val dataio = new memory_io(MemParams.dataPortWidth,MemParams.dataaddrPortWidth)
            val blockio = new memory_io(MemParams.blockPortWidth,MemParams.blockAddrPortWidth)            
          }
        )
    //io <> DontCare
    
    io.dataio.rdata :="hdd".U
    io.blockio.rdata :="hdd".U
    
    val mem = Mem(MemParams.MemSizeInWord,UInt(MemParams.MemWordWidth.W))//SyncReadMem
    val out = Vec.fill(MemParams.BlockSize) {0.U(MemParams.MemWordWidth.W)}
    
    io.dataio.ready := false.B
    //memory data read and write
    when(io.dataio.en === true.B)
    {      
      when(io.dataio.valid === true.B)
      {        
        when(io.dataio.we === true.B &  ~(io.blockio.en & io.blockio.we))
        {
          printf("Writting data=%x to address=%x\n",io.dataio.wdata,io.dataio.addr)
           mem.write(io.dataio.addr,io.dataio.wdata)  
           io.dataio.ready := true.B
        }.elsewhen(io.dataio.we === false.B){
           io.dataio.rdata := mem.read(io.dataio.addr)
           io.dataio.ready := true.B
        }        
      }
    }
    
    
    io.blockio.ready := false.B
    //block IO port
    when(io.blockio.en === true.B)
    {      
      when(io.blockio.valid === true.B)
      {    
        val extraAddrWidth = log2Ceil(MemParams.BlockSize)
        val baseAddr = Cat(io.blockio.addr,0.U(extraAddrWidth.W))
        
        when(io.blockio.we === true.B)
        {
           for( i <- 0 to (MemParams.BlockSize - 1))
           {
             val na = baseAddr + i.U 
             val data = io.blockio.wdata(i*MemParams.MemWordWidth + (MemParams.MemWordWidth-1), i*MemParams.MemWordWidth)
             mem.write(na,data)  
           }                  
           printf("Writting block dat\n")
           io.blockio.ready := true.B
        }.otherwise{
           
           
           for( j <- 0 to (MemParams.BlockSize - 1))
           {
                 //val na2 = baseAddr + j.U 
               out(j) :=  mem.read((baseAddr + j.U ))
                 //out := out|(tmp2<< (j*MemParams.MemWordWidth))
           }
           
           
           //Todo:- Modify this
           //assuming 4 block size memory
           val res = Cat(out(3), out(2), out(1),out(0))
           printf("memory read data is = %x, from base address=%x \n",res,baseAddr)           
           io.blockio.rdata := res
           io.blockio.ready := true.B
        }        
      }
    }
    
    when(reset.toBool())
    {
//      for(i <- 0 to 20)
//          mem.write(i.U,0.U)  
    }   
}

object elaboratememory extends App{
  chisel3.Driver.execute(args,()=> new Memory)
}
