package ram

import chisel3._
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.chip._
import testchipip._

class ExampleTop(implicit p: Parameters) extends BaseSystem
    with HasPeripheryMasterAXI4MemPort
    with HasPeripheryErrorSlave
    with HasPeripheryZeroSlave
    with HasPeripheryBootROM
    with HasPeripheryRTCCounter
    with HasRocketPlexMaster
    with HasNoDebug
    with HasPeripherySerial {
  override lazy val module = new ExampleTopModule(this)
}

class ExampleTopModule[+L <: ExampleTop](l: L) extends BaseSystemModule(l)
    with HasPeripheryMasterAXI4MemPortModuleImp
    with HasPeripheryBootROMModuleImp
    with HasPeripheryRTCCounterModuleImp
    with HasRocketPlexMasterModuleImp
    with HasNoDebugModuleImp
    with HasPeripherySerialModuleImp

/*class ExampleTopWithPWM(implicit p: Parameters) extends ExampleTop
    with HasPeripheryPWM {
  override lazy val module = new ExampleTopWithPWMModule(this)
}

class ExampleTopWithPWMModule(l: ExampleTopWithPWM)
  extends ExampleTopModule(l) with HasPeripheryPWMModuleImp


class ExampleTopWithSRAM(implicit p: Parameters) extends ExampleTop
    with HasPeripherySRAM {
  override lazy val module = new ExampleTopWithSRAMModule(this)
}

class ExampleTopWithSRAMModule(l: ExampleTopWithSRAM)
  extends ExampleTopModule(l) with HasPeripherySRAMModuleImp
  
*/

class ExampleTopWithSRAM(implicit p: Parameters) extends ExampleTop
    with HasPeripherySRAM {
  override lazy val module = new ExampleTopWithSRAMModule(this)
}

class ExampleTopWithSRAMModule(l: ExampleTopWithSRAM)
  extends ExampleTopModule(l) with HasPeripherySRAMModuleImp  
  
  
/*
class ExampleTopWithBlockDevice(implicit p: Parameters) extends ExampleTop
    with HasPeripheryBlockDevice {
  override lazy val module = new ExampleTopWithBlockDeviceModule(this)
}

class ExampleTopWithBlockDeviceModule(l: ExampleTopWithBlockDevice)
  extends ExampleTopModule(l)
  with HasPeripheryBlockDeviceModuleImp

class ExampleTopWithSimpleNIC(implicit p: Parameters) extends ExampleTop
    with HasPeripherySimpleNIC {
  override lazy val module = new ExampleTopWithSimpleNICModule(this)
}

class ExampleTopWithSimpleNICModule(outer: ExampleTopWithSimpleNIC)
  extends ExampleTopModule(outer)
  with HasPeripherySimpleNICModuleImp
*/
