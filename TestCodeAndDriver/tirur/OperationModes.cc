#include "OperationModes.h"
//#include <stdio.h>
#include "Registers.h"
#include "../mmio.h"



void setOperationMode(int mode)
{
   setRegisterValue(OPERATION_MODE_CONF_REGISTER,mode,OPERATION_MODE_START_BIT, OPERATION_MODE_BITS_COUNT);   
}

void setOperationModeWriteThrough()
{
    setOperationMode(OMODE_WRITE_THROUGH);
}
void setOperationModePoutVector()
{
    setOperationMode(OMODE_POUT_VECTOR);
}
void setOperationModePinVector()
{
    setOperationMode(OMODE_PIN_VECTOR);
}
void setOperationModePoutMemory()
{
    setOperationMode(OMODE_POUT_MEMORY);
}
void setOperationModePinMemory()
{
    setOperationMode(OMODE_PIN_MEMORY);
}
void setOperationModeIdeal()
{
    setOperationMode(OMODE_IDEAL);
}
