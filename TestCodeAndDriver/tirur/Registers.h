#ifndef _REGISTERS_H
#define _REGISTERS_H
#include <stdio.h>
#include "Config.h"

#define NUM_OF_CONFIG_REGS 9
#define CONFIG_0_ADDRESS TIRUR_CONFIG_BASE
#define CONFIG_1_ADDRESS CONFIG_0_ADDRESS + 4
#define CONFIG_2_ADDRESS CONFIG_1_ADDRESS + 4
#define CONFIG_3_ADDRESS CONFIG_2_ADDRESS + 4
#define CONFIG_4_ADDRESS CONFIG_3_ADDRESS + 4
#define CONFIG_5_ADDRESS CONFIG_4_ADDRESS + 4
#define CONFIG_6_ADDRESS CONFIG_5_ADDRESS + 4
#define CONFIG_7_ADDRESS CONFIG_6_ADDRESS + 4
#define CONFIG_8_ADDRESS CONFIG_7_ADDRESS + 4
#define CONFIG_9_ADDRESS CONFIG_8_ADDRESS + 4

//#define CONFIG(i) ( CONFIG_## 4 ##_ADDRESS  )//CONFIG_##

#define OPERATION_MODE_MASK 0xE
#define START_BIT_MASK 0x01


uint32_t CONFIG(int i);

void initRegisters();

void setRegisterValue(int registernumber,uint32_t value,int startbit, uint32_t numberOfBits);


#endif 
