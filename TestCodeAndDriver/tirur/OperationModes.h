#ifndef _OPERATION_MODES_H_
#define _OPERATION_MODES_H_

#define OMODE_IDEAL 0
#define OMODE_WRITE_THROUGH 5
#define OMODE_POUT_VECTOR 3
#define OMODE_PIN_VECTOR 4
#define OMODE_POUT_MEMORY 1
#define OMODE_PIN_MEMORY 2

#define OPERATION_MODE_CONF_REGISTER 0
#define OPERATION_MODE_START_BIT 1
#define OPERATION_MODE_BITS_COUNT 3


void setOperationMode(int mode);

void setOperationModeIdeal();
void setOperationModeWriteThrough();
void setOperationModePoutVector();
void setOperationModePinVector();
void setOperationModePoutMemory();
void setOperationModePinMemory();


#endif 
