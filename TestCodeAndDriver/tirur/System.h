#ifndef _SYSTEM_H_
#define _SYSTEM_H_
#include <stdio.h>
#include <stdint.h>

#define START_CONF_REGISTER 0
#define START_START_BIT 0
#define START_BITS_COUNT 1

#define NUMBER_OF_BLOCKS_REGISTER 2
#define RESULTS_REGISTER_BASE 4

void initSystem();
void startOperation();
void stopOperation();
void wrightMemory(uint32_t relativeAddress,uint32_t value);
uint32_t readMemory(uint32_t relativeAddress);
void setContinueBit();
void setNumberOfBlocksToPage(uint32_t blocks);
bool isCurrOperationDone();
bool isFinished();

/*
  register number 1,2,3,4
*/
uint32_t readResultRegister(int registerNumber);

/*
  relative address 1,2,3,4
  value the value to write
*/
void wrightMemory(uint32_t relativeAddress,uint32_t value);

#endif 
