package intgCheck

import chisel3._
import chisel3.util._

import memory._
import aes._
class upsgcm_io extends Bundle{
   //ps interface to the system bus  
   //the data memory + 1 extra bit for configuration ports
   val mainMemio = new memory_io(MemParams.dataPortWidth,(MemParams.dataaddrPortWidth+1))  
  
   //write through port to OSC memory 
   val socMemio = Flipped(new memory_io(MemParams.dataPortWidth,(MemParams.dataaddrPortWidth+1)))
   
   //interface to SOC memory,block io  
   val memio = Flipped(new memory_io(MemParams.blockPortWidth,MemParams.blockAddrPortWidth))   
      
   val err = Output(Bool())
   val currOutReady = Output(Bool())  
   val finished = Output(Bool())
}

class upageswappergcm extends Module{
  val io = IO(new upsgcm_io)  
  printf("*********************************************\n")
  
  val rndgenm = Module(new randomgen)  
  val cntrm   = Module(new counter)
  val vfm     = Module(new ivFileMac(VfRegSize.ivSize))
  val aesm    = Module(new unrolledaesctr) 
  
  val gmultm = Module(new g128Multiplay)
  val h = RegInit(0.U(128.W))
  val tag = RegInit(0.U(128.W))
  val IVTag = RegInit(0.U(128.W))
  
  //io <> DontCare
  
  val nonce = RegInit(0.U(64.W))
  val curriv = RegInit(0.U(128.W))
  val sessionIv = RegInit(0.U(128.W))
  //val delay = RegInit(0.U(2.W))
    
  val sessionKey = RegInit(0.U(128.W))
  val currentAddrBase= RegInit(0.U(MemParams.blockAddrPortWidth.W))
  val blockCounter = RegInit(0.U(32.W)) // properly calculate this width
  val currBlockBuff = RegInit(0.U(128.W))
  val currBlock = Wire(UInt(128.W))
  
  /*
   * 0 ->  different configurations mask= 0000  
   *       bit 
   *       0 -> start operation
   *       1 + 2 + 3 -> operation select 
   *                    000 no op
   *                    001 page out memory 
   *                    010 page in memory 
   *                    011 page out IVs
   *                    100 page in IVs 
   *                    101 write and read through 
   *                    110
   *                    111

   * 1 -> page start address 
   * 2 -> mumber of blocks 

   * 3 -> Last used vector Number or vector number to use next during pagging in memory    
   * 4 -> erros register  //readonly 
   *       0 -> vector file full
   *       1 -> tag verificaiton error
   * 5 -> Last Output Result 
   * 6 -> Last Output Result
   * 7 -> Last Output Result
   * 8 -> Last Output Result  
   * 9 -> flags register  readonly mask 10110
   *      
   *      0 -> //tag verificaiton error   
   *      1 -> module Init  //ReadOnly
   *      2 -> encriptd block output ready //ReadOnly 
   *      3 ->  
   *      4 -> done bit  
   *      5 -> data to be paged in ready 
   *      6 -> data to be paged out has been read
   * 10 -> decreption result 0  mask ffffffff
   * 11 -> decreption result 1  mask ffffffff
   * 12 -> decreption result 2  mask ffffffff
   * 13 -> decreption result 3  mask ffffffff
   * 14 ->
   * 15 ->
   * 16 ->
   * */  
  
  
  
  val ConfReg = Reg(init = Vec(Seq.fill(14)(0.U(32.W))))   
  //    0     1       2           3          4       5       6     7
  val init::gNonce::gSessKey1::gSessKey2::iCounter::waith::isDone::Nil = Enum(7)
  val initState = RegInit(init)
  val initStateNext = RegInit(init)
  initState := initStateNext
  //   0       1        2        3            4           5       6     7
  val gVect::rdBlock::encryp::rightBack::outblockwait::cleanup::ideal::Nil = Enum(7)//init2::
  val poState = RegInit(ideal) 
  val poStateNext = Wire(UInt(3.W))
  poState := poStateNext
  val cleanupState = RegInit(0.U(2.W)) 
  val cleanupStateNext = RegInit(0.U(2.W)) 
  cleanupState := cleanupStateNext
  
  val poutState = RegInit(0.U(3.W))  

  val start = ConfReg(0)(0)
  val currOperation = ConfReg(0)(3,1)
  ////printf("Current Operation is = %x\n",currOperation)
  val aesDone = ConfReg(9)(2)
  val aesdecInput = Cat(ConfReg(8),ConfReg(7),ConfReg(6),ConfReg(5))
  val aesInOut = Mux((currOperation === 1.U) | (currOperation === 3.U), 
                      aesdecInput,
                      Cat(ConfReg(13),ConfReg(12),ConfReg(11),ConfReg(10)))
    
  val moduleReady = ConfReg(9)(1) //ConfReg(0)(6)  //only written by the system  
  
  val zero::one::two::three::Nil = Enum(4)  
  val tagState = RegInit(one)
  val tagUp = RegInit(true.B)
  gmultm.io.x := h
  gmultm.io.y := MuxLookup(tagState,
      0.U,
      Array(
       one -> 0.U,
       two   -> Mux(aesDone ===1.U ,
                    Mux((currOperation===1.U) | (currOperation ===3.U),(aesInOut ^ tag),(aesdecInput ^ tag)),
                    tag), //wait for the encryption done bit to calculae tag
       three  -> ((ConfReg(2) << 7) ^ tag)
      )
      )
  
  IVTag := Mux(moduleReady === 0.U,0.U,Mux(tagState === one,Mux(aesDone,aesInOut,0.U),IVTag))   
  tag := MuxLookup(tagState,
      tag,
      Array(
      two ->  Mux(aesDone && (tagUp === true.B),gmultm.io.out,tag),
      three -> Mux(cleanupState === 0.U, gmultm.io.out, (gmultm.io.out^IVTag))
      ))
      //=== two)) & aesDone,gmultm.io.out,tag) 
  
  rndgenm.io.en := (initState != isDone)
  
//  printf("******************\n")
  printf("h = %x\n",h)
  printf("ivtage = %x\n",IVTag)
  printf("h = %x\n",h)
  printf("tag state = %x\n",tagState)
  printf("tag = %x\n",tag)
  printf("y input=%x\n",gmultm.io.y)
  printf("gmult out=%x\n",gmultm.io.out)
//  printf("******************\n")
  
  val newC = Wire(Bool())
  cntrm.io.reset := (moduleReady === 0.U) & (initState === iCounter)
  cntrm.io.get :=  newC //((poState === gVect) & (start === 1.U)) | newC
  cntrm.io.init := Mux((initState === iCounter)&(moduleReady === 0.U),rndgenm.io.out,0.U)//"hdecaf88800000001".U//
  
  val cond1 = (poState === gVect)
  val cond2 = blockCounter < ConfReg(2)
  val cond3 = (start === 1.U)
  val cond4 = (cond1 || ((poState === rdBlock) && cond2) ) && cond3
  val cond5 = (cond1 || ((poState === rdBlock) && io.memio.ready && cond2) ) && cond3
  
  val newc24 = ((poState === ideal) | (aesm.io.newR && cond2)) && cond3  
  val cond = MuxLookup(currOperation, 
      0.U,
      Array(
        1.U -> cond5,
        3.U -> cond4,
        2.U -> newc24,
        4.U -> newc24
      ))
  newC := Mux(moduleReady===0.U,
      0.U,
      cond)//& (delay===3.U)
  printf("poState == gVect = %x\n",cond1)
  printf("no max block = %x\n",cond2)
  printf("start is one = %x\n",cond3)
  printf("aes new result = %x\n",aesm.io.newR)
  printf("io.memio.ready = %x\n",io.memio.ready)
  printf("newIV = %x\n",newC)
  //val cmd = MuxLookup() 
  
  val x1 = MuxLookup(poState,  //pagging out Memory
           0.U,
           Array(
               cleanup -> Mux(cleanupState === 2.U,2.U,0.U),
               gVect -> 6.U
           ))
  val x3 = MuxLookup(poState,    //paging out IV
           0.U,
           Array(
               cleanup -> MuxLookup(cleanupState,
                          0.U,
                          Array(
                              0.U -> 5.U,
                              2.U -> 2.U 
                              )),//===0.U,5.U,2.U),  //clean up then write back
               rdBlock -> 1.U   //read block
           ))
  val x2 = MuxLookup(poState,    //paging in memory
           0.U,
           Array(
               ideal -> Mux(start===1.U,1.U,0.U),  //read IV 
               cleanup -> 3.U
           ))    
  val x4 = MuxLookup(poState,    //paging in memory
           0.U,
           Array(
               ideal -> Mux(start===1.U,1.U,0.U),  //read IV 
               //gVect -> 3.U,
               rightBack -> Mux((blockCounter > 1.U),2.U,0.U),//
               encryp -> Mux(blockCounter === 0.U,3.U,0.U)
           ))              
           
  vfm.io.cmd := MuxLookup(currOperation,
      0.U,
      Array(
          1.U -> x1,
          2.U -> x2,
          3.U -> x3,
          4.U -> x4
          )      
  ) 
    
                      
  vfm.io.addr := MuxLookup(currOperation,
      ConfReg(3),
      Array(
       //1.U   -> ConfReg(3) // last used vector number
       3.U -> Mux(poState === cleanup,0.U,ConfReg(3)), 
       4.U -> MuxLookup(poState,
              0.U,
              Array(
                  rightBack -> Mux((blockCounter > 1.U),(blockCounter-2.U),(blockCounter))
                  //cleanup -> 0.U
              ))
//       Mux(poState === ideal,0.U,
//                  Mux((blockCounter > 1.U)&(blockCounter < 15.U),(blockCounter-1.U),(blockCounter)))
      ))
      
  vfm.io.wdata := MuxLookup(currOperation,
        0.U,
        Array(
          1.U -> Cat("b1".U,((sessionIv<<128.U) | tag)),  //mem paged out, write  the current iv to the required slot  
          2.U -> Cat("b0".U,((sessionIv<<128.U) | tag)),  //mem paged in,mark the current iv as not used 
          3.U -> Cat("b1".U,((sessionIv<<128.U) | tag)),  //Iv paged in
          4.U -> Cat("b1".U,((aesInOut<<128.U) | aesInOut))
        )
      )

  
  //current inital vector is either read from vmf file when 
  //switching data in to the soc or 
  //new vector nonce^counter when pagging data out of the soc
  //TODO. add check for the current operation and state 
      
  val cv13 = Mux(newC,((nonce<<64.U) | cntrm.io.out),curriv)//(poState === gVect)|newC  
  val cv2  = Mux((poState === gVect)&(blockCounter === 0.U),vfm.io.rdata,
    Mux((poState === rightBack),curriv + 1.U,curriv)     
  ) 
  //val cv4  = Mux((poState === gVect)|(blockCounter === 0.U),vfm.io.rdata,curriv) 
  
  
  val xcv = MuxLookup(currOperation,
          curriv,
          Array(
            1.U -> cv13,
            3.U -> cv13,
            2.U -> cv2,
            4.U -> cv2
          ))
      
  curriv := xcv
  //sesssion inital vector or final tag 
  sessionIv := MuxLookup(currOperation,
      sessionIv,
      Array(
         1.U -> Mux(poState === gVect,xcv,sessionIv),
         3.U -> Mux(poState === gVect,xcv,sessionIv),
         2.U -> Mux(poState === cleanup,vfm.io.rdata,sessionIv),
         4.U -> Mux((poState === encryp)&((blockCounter === 0.U)),vfm.io.rdata,sessionIv)
      ))
  
  
  printf("poState = %x,blockCounter=%x, vfdata=%x,curriv=%x\n",poState,blockCounter,vfm.io.rdata,curriv)
  //printf("Session Iv = %x\n",sessionIv)
  
  //printf("vfm cmd=%x\n",vfm.io.cmd)
  //printf("vfm addr=%x\n",vfm.io.addr)
  //printf("vfm read data=%x\n",vfm.io.rdata)
  //printf("vfm write data=%x\n",vfm.io.wdata)
  
  val bcg1 = Mux(blockCounter < 1.U,false.B,ConfReg(9)(2))
  val bcg2 = Mux(blockCounter <=1.U,false.B,ConfReg(9)(2))
  
  io.currOutReady := MuxLookup(currOperation,
                                false.B,
                                Array(
                                  1.U -> bcg1,
                                  2.U -> bcg2,
                                  3.U -> bcg1,
                                  4.U -> bcg2
                                ))
    
      
//      Mux((currOperation === 2.U) | (currOperation===4.U),
//                          Mux(blockCounter <=1.U,false.B,ConfReg(9)(2)),
//                          ConfReg(9)(2))
  //printf("current output ready=%x\n",io.currOutReady)
  
//    MuxLookup(currOperation,
//      0.U,
//      Array(
//      1.U -> ConfReg(9)(2),
//      2.U -> ConfReg(9)(3),
//      3.U -> ConfReg(9)(2),
//      4.U -> ConfReg(9)(3)
//      ))
  io.err := ConfReg(4).orR
  io.finished := ConfReg(9)(4) //(poState === cleanup)&(cleanupState===2.U)
  //printf("finished %x\n",ConfReg(9)(4))
  val cb = MuxLookup(currOperation,
      currBlockBuff,
      Array(
       1.U -> Mux(tagState === one,0.U,Mux(poState===rdBlock,io.memio.rdata,currBlockBuff)),//currBlock//io.memio.ready
       3.U -> Mux(tagState === one,0.U,vfm.io.rdata),
       2.U -> aesdecInput,//Mux(poState === rdBlock,aesdecInput,currBlock)
       4.U -> aesdecInput//Mux(poState === rdBlock,aesdecInput,currBlock)
      ))
  //printf("cb = %x\n",cb)    
  currBlockBuff:=cb
  currBlock := cb 
  printf("poSate in en =%x",poState)
  val encry=RegInit(false.B)
  encry :=Mux(moduleReady === 0.U,(initState === waith),(poState === encryp))
  aesm.io.en := encry 
  aesm.io.intVect := Mux(moduleReady===0.U,0.U,curriv)
  aesm.io.data := MuxLookup(currOperation,
                  currBlock,
                  Array(
                  1.U -> Mux(moduleReady===0.U,0.U,currBlock),
                  3.U -> Mux(moduleReady===0.U,0.U,currBlock),
                  2.U -> Mux(blockCounter===0.U,0.U,currBlock),
                  4.U -> Mux(blockCounter===0.U,0.U,currBlock)
                  ))
                  
  aesm.io.key := sessionKey 
  aesm.io.dir := true.B //Mux(moduleReady===0.U,true.B,(currOperation === 1.U) | (currOperation === 3.U))  //true.B
  
  val var24 = Mux((poState === encryp)&(blockCounter === 0.U),true.B,false.B) // (poState === gVect)&
  
  aesm.io.reset := MuxLookup(currOperation,
      0.U,
      Array(
       1.U ->      (blockCounter === 0.U),
       2.U ->      var24,
       3.U ->      (blockCounter === 0.U),
       4.U ->      var24
      ))
  
  //printf("aes dir = %x\n",aesm.io.dir)
  //check if the last bit is not set 
  //soc is located in the front of the memory 
  //followed by the ConfReg memory 
  val socmselect = io.mainMemio.addr < MemParams.MemSizeInWord.U  //((io.mainMemio.addr & (1.U<<(MemParams.dataaddrPortWidth.U))) === 0.U)
  val validConfAddress = io.mainMemio.addr >= MemParams.MemSizeInWord.U && io.mainMemio.addr <= MemParams.MemSizeInWord.U + 9.U
  
  
  io.socMemio.addr    := io.mainMemio.addr 
  io.socMemio.wdata   := io.mainMemio.wdata 
  io.socMemio.valid   := Mux(currOperation === 5.U & socmselect, io.mainMemio.valid,false.B)
  io.socMemio.we      := Mux((currOperation === 5.U) & socmselect, io.mainMemio.we,false.B)
  io.socMemio.en      := Mux(currOperation === 5.U & socmselect, io.mainMemio.en,false.B)
  
  
  io.mainMemio.rdata  := Mux(currOperation === 5.U & socmselect,io.socMemio.rdata,ConfReg(io.mainMemio.addr(3,0))) 
  io.mainMemio.ready  := MuxLookup(socmselect,
                        false.B,
                        Array(
                          false.B -> (io.mainMemio.en &  io.mainMemio.valid),
                          true.B  -> Mux(currOperation===5.U,io.socMemio.ready,false.B)
                        ))
    
//           MuxLookup(
//            currOperation,
//            0.U,
//            Array(
//             1.U -> ConfReg(9)(2),                         //soc mem page out
//             3.U -> ConfReg(9)(2),                         //vector file page out
//             5.U -> Mux(socmselect,io.socMemio.ready,0.U)  //write to sos memory     
//            )
//  ) 
 
  val confMask = MuxLookup(io.mainMemio.addr(3,0),
           0.U(32.W),
           Array(
           9.U -> "b011111".U(32.W)    
           )                
  )
  val pindataReady = Mux((currOperation === 2.U | currOperation === 4.U), ConfReg(9)(5),false.B)
  val poutdataReady = Mux((currOperation === 1.U | currOperation === 3.U), ConfReg(9)(6),false.B)
  //printf(" page in ready = %x\n",pindataReady)
  //read and write configruation registers 
  when(io.mainMemio.en && io.mainMemio.valid)
  {   
//    when(io.mainMemio.valid)
//    {
        //printf("main memory enabled and valid.\n")
        when(~socmselect && validConfAddress)
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
             
             val cond = (currOperation === 2.U | currOperation === 4.U | currOperation === 0.U) & (io.mainMemio.addr(3,0) === 8.U)
             when(cond)
             {               
               ConfReg(9) := ConfReg(9) | (1.U<<5.U)  
             }
             
           }.otherwise{
             val cond2 = (currOperation === 1.U | currOperation === 3.U | currOperation === 0.U) & (io.mainMemio.addr(3,0) === 8.U)
             when(cond2)
             {          
               printf("Config 9 read ")
               ConfReg(9) := ConfReg(9) | (1.U<<6.U)  
             }
             
             io.mainMemio.rdata := ConfReg(io.mainMemio.addr(3,0))
             //printf("reading data from config addr=%x , value= %x\n",io.mainMemio.addr(3,0),ConfReg(io.mainMemio.addr(3,0)))
           }
        }       
//    }
  }
  

  
  val vfNotFull = vfm.io.rdata <=15.U
  
  val conf3= MuxLookup(currOperation,
      0.U,
      Array(
        1.U ->  Mux((poState === gVect)& (vfNotFull),vfm.io.rdata,0.U),
        3.U -> blockCounter 
      ))
   
  ConfReg(3) := Mux(start.toBool,conf3,ConfReg(3))  
      
  val conf4 = MuxLookup(currOperation,
      0.U,
      Array(
       1.U -> Mux((poState === gVect) & (~vfNotFull),(ConfReg(4) | 1.U),ConfReg(4))
      ))
  ConfReg(4) := Mux(start.toBool,conf4,ConfReg(4))      
      
   val mmenvalid = MuxLookup(currOperation,
    false.B,
    Array(
        1.U -> Mux(poState === rdBlock,true.B,false.B),
        2.U -> Mux(poState === rightBack,true.B,false.B)
    ))
    
   io.memio.en    := mmenvalid
   io.memio.valid := mmenvalid
   io.memio.we := MuxLookup(currOperation,
       false.B,
       Array(
         2.U -> true.B      
       ))

   val addr2 = (ConfReg(1)(MemParams.blockAddrPortWidth-1,0)) + (blockCounter-2.U)
                  //io.memio.addr  := addr    
       
   val addr1 = (ConfReg(1)(MemParams.blockAddrPortWidth-1,0)) + blockCounter
              //io.memio.addr  := addr    
   val addr =MuxLookup(currOperation,
       0.U,
       Array(
        1.U -> Mux(poState === rdBlock ,addr1,0.U),
        2.U -> Mux(poState === rightBack,addr2,0.U)
       ))
       
   io.memio.addr := addr    
   io.memio.wdata := Mux(poState === rightBack && currOperation===2.U,
       Cat(ConfReg(13),ConfReg(12),ConfReg(11),ConfReg(10)),
       0.U)       
       
       
       
  val notFinalBlock = blockCounter < ConfReg(2)     
  val nPoState = Mux(tagState === one,rdBlock, outblockwait)
  
   val pos13 = MuxLookup(poState,
      6.U,
      Array(
         ideal -> gVect,
         gVect -> encryp,
         encryp -> Mux(aesm.io.newR,Mux(notFinalBlock,nPoState,cleanup),encryp),
         outblockwait -> Mux(poutdataReady,rdBlock,outblockwait),
         rdBlock -> Mux(currOperation === 1.U,Mux(io.memio.ready,encryp,rdBlock),encryp),
         cleanup -> Mux(cleanupState === 2.U,ideal,cleanup)
         
      ))
      
  poStateNext := Mux(start && moduleReady,  pos13,ideal)
  
  printf("poState = %x\n",poState)
  printf("poStateNext=%x\n",poStateNext)
  printf("calcPoStateNext=%x\n",pos13)
  printf("start is = %x\n",start)
  printf("init state is=%x\n",initState)
  when(moduleReady.toBool)
  {
    //printf("Module is Ready with start = %x, po state = %x\n",start,poState)
    //printf("Done encryption bit=%x\n",ConfReg(9)(2))
    when(start.toBool)
    {
      
      when((currOperation === 1.U) | (currOperation === 3.U)) // page our mem, or IV 
      {
        //printf("Curr operation %x\n",currOperation)
        when(poState === ideal)
        {
          //poStateNext := gVect
          printf("state is ideal\n")       
        }.elsewhen(poState === gVect)
        {
             printf("state is gVect\n")
 
            when(currOperation === 3.U)
            {
              ConfReg(2) := 32.U //page out the entire ivs table 
              cleanupStateNext := 0.U
            }
            //poStateNext := encryp
        }.elsewhen(poState === rdBlock)
        {
            printf("state is rBlock\n")
            ConfReg(9) := ConfReg(9) & (~(1.U<<2.U)) // clean currout ready bit
            when(currOperation === 1.U)
            {
//              val addr = (ConfReg(1)(MemParams.blockAddrPortWidth-1,0)) + blockCounter
//              io.memio.addr  := addr
//              io.memio.we    := false.B
//              io.memio.en    := true.B
//              io.memio.valid := true.B
              
              when(io.memio.ready)
              {                
                blockCounter := blockCounter + 1.U
                //poStateNext := encryp
              }
            }.elsewhen(currOperation === 3.U)
            {               
              blockCounter := blockCounter + 1.U
              //poStateNext := encryp
            }
           tagState := two                          
        }
        .elsewhen(poState === encryp)
        {
          printf("state is encrypt\n")
//          delay := delay+1.U                           
          when(aesm.io.newR)//&(delay===3.U)
          {
            printf("encrypting memory block bc=%x,max block=%x\n",blockCounter,ConfReg(2))
            printf("session key=%x\n",sessionKey)
            printf("iv=%x\n",curriv)
            printf("data=%x\n",currBlock)
            printf("Done encryption res=%x\n",aesm.io.out)

            ConfReg(5) := aesm.io.out(31,0)
            ConfReg(6) := aesm.io.out(63,32)
            ConfReg(7) := aesm.io.out(95,64)
            ConfReg(8) := aesm.io.out(127,96)
            tagUp := true.B
            ConfReg(9) := ConfReg(9) | (1.U<<2.U) //set current round output ready bit
//            when(blockCounter < ConfReg(2))
//            {
//              poStateNext := Mux(tagState === one,rdBlock, outblockwait)
//              //poState := outblockwait
//            }.otherwise{
//              
//              poStateNext := cleanup
//            }            
          }
        }
        .elsewhen(poState === outblockwait)
        {
              printf("outblock wait state\n")
              tagUp := false.B
              when(poutdataReady)
              {
                printf("final block read \n")
                ConfReg(9) := ConfReg(9) & ~(1.U(32.W)<<6.U) // the data is going to be used, wait for new data on the next time
                                                             //wait for continue flag, or do this when the last address is written back
                //poStateNext := rdBlock
              }
        }
        .elsewhen(poState === cleanup)
        {
           
           printf("clean up\n")
           tagState := three
           
          //done paging out memory area  
           when(cleanupState === 0.U)
           {
               printf("Clean up first\n")
               
               cleanupStateNext := 1.U
           }.elsewhen((cleanupState === 1.U))//(currOperation === 1.U)|
           {
              cleanupStateNext := 2.U
              tagState := one
           }.elsewhen(cleanupState === 2.U)//((currOperation === 3.U) | (currOperation === 1.U))&
           {
              printf("Clean up second\n")
              ConfReg(0) := (ConfReg(0) & "hfffffffe".U) // clean bit zero(start)
              ConfReg(9) := (ConfReg(9) | (1.U(32.W)<<4.U)) & (~(1.U(32.W)<<2.U))   // set done bit to one
              //ConfReg(9) := ConfReg(9) & (~(1.U(32.W)<<2.U))   // ???????,clean the current output bit ready bit
              //poStateNext := ideal //init2
              tagState := one
              //delay :=0.U
              blockCounter := 0.U 
              cleanupStateNext := 0.U
           }.elsewhen(cleanupState === 3.U)
           {
              
           }
        }
      }.elsewhen((currOperation === 2.U)|(currOperation === 4.U))
      {
          when(poState === ideal)
          {
            poStateNext := gVect
            //tagState := one
            when(currOperation === 4.U)
            {
              ConfReg(2) := 32.U //page in the entire ivs table 32 vectors except the first one
                                 //33 b/c bc 0 and 1 are skipped 
              cleanupStateNext := 0.U
            }
            
          }.elsewhen(poState === gVect)
          {
            poStateNext := rdBlock//encryp
          }.elsewhen(poState === rdBlock)
          {            
            when(pindataReady)
            {
              ConfReg(9) := ConfReg(9) & ~(1.U(32.W)<<5.U) // the data is going to be used, wait for new data on the next time
              poStateNext := encryp //wait for continue flag, or do this when the last address is written back
            }
          }.elsewhen(poState === encryp)
          {
            //delay :=delay+1.U
            when(aesm.io.newR)//&(delay===3.U)
            {
              //printf("decrypting memory block bc=%x,max block=%x\n",blockCounter,ConfReg(2))
              //printf("session key=%x\n",sessionKey)
              //printf("iv=%x\n",curriv)
              //printf("data=%x\n",currBlock)
              //printf("Done decryption res=%x\n",aesm.io.out)

              ConfReg(10) := aesm.io.out(31,0)
              ConfReg(11) := aesm.io.out(63,32)
              ConfReg(12) := aesm.io.out(95,64)
              ConfReg(13) := aesm.io.out(127,96)
              
              poStateNext := rightBack
              ConfReg(9) := ConfReg(9) | (1.U<<2.U)
              
              blockCounter := blockCounter + 1.U
              
            }            
          }.elsewhen(poState === rightBack)
          {
              ConfReg(9) := ConfReg(9) & ~(1.U(32.W)<<2.U)
              when((blockCounter-1.U) === 0.U)
              {               
                poStateNext := encryp
                
              }.otherwise{
                when(currOperation === 2.U)
                {
//                  val addr = (ConfReg(1)(MemParams.blockAddrPortWidth-1,0)) + (blockCounter-2.U)
//                  io.memio.addr  := addr
//                  io.memio.we    := true.B
//                  io.memio.en    := true.B
//                  io.memio.valid := true.B
//                  io.memio.wdata := Cat(ConfReg(13),ConfReg(12),ConfReg(11),ConfReg(10))
                  when(io.memio.ready)
                  {
                    
                    when((blockCounter-1.U) < ConfReg(2))
                    {
                      poStateNext := rdBlock
                    }.otherwise{
                      poStateNext := cleanup
                    }                 
                  }
                }.otherwise{
                   when((blockCounter-1.U) < ConfReg(2))
                    {
                      poStateNext := rdBlock
                    }.otherwise{
                      poStateNext := cleanup
                    } 
                }
              }
              tagState := two
          }.elsewhen(poState === cleanup)
          {
              
           //printf("clean up\n")
           tagState := three
           
          //done paging out memory area  
           when(cleanupState === 0.U)
           {
               //printf("Clean up first\n")
               
               cleanupStateNext := 1.U
           }.elsewhen((cleanupState === 1.U))//(currOperation === 1.U)|
           {
              cleanupStateNext := 2.U
              tagState := one
           }.elsewhen(cleanupState === 2.U)//((currOperation === 3.U) | (currOperation === 1.U))&
           {
              //printf("Clean up second\n")
              ConfReg(0) := (ConfReg(0) & "hfffffffe".U) // clean bit zero(start)
              val cgval = (ConfReg(9) | (1.U(32.W)<<4.U)) & (~(1.U(32.W)<<5.U)) & (~(1.U(32.W)<<2.U))  // set done bit to one
              ConfReg(9) := cgval //(ConfReg(9) | (1.U<<4.U)) & (~(1.U<<5.U)) & (~(1.U<<2.U))  // set done bit to one

              //printf("Configuration 9 is set to = %x\n",cgval)
              //              ConfReg(9) := ConfReg(9) & (~(1.U<<5.U))   // ???????,clean the current output bit ready bit
//              ConfReg(9) := ConfReg(9) & (~(1.U<<2.U))   // ???????,clean the current output bit ready bit
//            
              poStateNext := ideal //init2
              tagState := one
              //delay :=0.U
              
              blockCounter := 0.U 
              cleanupStateNext := 0.U
              
              when(sessionIv === tag)
              {                 
                ConfReg(4) := ConfReg(4) & ~(2.U) 
                //printf("Tag generated sussesfully\n")
              }.otherwise{
                ConfReg(4) := ConfReg(4) | 2.U
              }
              
           }.elsewhen(cleanupState === 3.U)
           {
              
           }
          }
      }
    }         
  }.otherwise{
    printf("Module is not Ready\n")
    printf("initState = %x\n",initState)
    when(initState === init)
    {
      initStateNext := gNonce
    }.elsewhen(initState === gNonce)
    {           
          when(rndgenm.io.done)
          {           
            nonce := rndgenm.io.out//"hcafebabefacedbad".U //rndgenm.io.out       
            initStateNext := gSessKey1
          } 
    }.elsewhen(initState === gSessKey1)//generate session key lower half        
    {         
          when(rndgenm.io.done)
          {
            sessionKey := rndgenm.io.out   
            initStateNext := gSessKey2
          } 
    }.elsewhen(initState === gSessKey2)//generate session key upper half
    {          
          when(rndgenm.io.done)
          {
             val sk = sessionKey | (rndgenm.io.out<<64.U) 
             sessionKey := sk //"hfeffe9928665731c6d6a8f9467308308".U//sk               
             initStateNext := iCounter
          }                  
     }.elsewhen(initState === iCounter)
     {
        when(rndgenm.io.done)
        {            
             initStateNext := waith
        } 
     }.elsewhen(initState === waith) //wait for the generation of hash key  
     {
          when(aesm.io.newR)
          {
              printf("encrypting memory block\n")
              printf("session key=%x\n",sessionKey)
              printf("iv=%x\n",curriv)
              printf("data=%x\n",currBlock)
              printf("Done encryption res=%x\n",aesm.io.out)
              h := aesm.io.out 
              ConfReg(9) := ConfReg(9) | (1.U<<1.U) // now the module is ready set module ready bit
              initStateNext := isDone
          }
     }
  }           
}


object elaborateupsgcm extends App{
  chisel3.Driver.execute(args,()=> new upageswappergcm)
}