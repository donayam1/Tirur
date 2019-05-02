// See LICENSE.SiFive for license details.

package freechips.rocketchip

import Chisel._


package object util {

  implicit class DataToAugmentedData[T <: Data](val x: T) extends AnyVal {
    def holdUnless(enable: Bool): T = Mux(enable, x, RegEnable(x, enable))
  }

  implicit class SeqMemToAugmentedSeqMem[T <: Data](val x: SeqMem[T]) extends AnyVal {
    def readAndHold(addr: UInt, enable: Bool): T = x.read(addr, enable) holdUnless RegNext(enable)
  }

  
}
