#include "System.h"
#include "Registers.h"
#include "../mmio.h"
#include "Errors.h"

extern void exit(int code);

void initSystem()
{
#ifdef DEBUG
 printf("System init\n");
#endif  
 initRegisters();
}

void startOperation()
{
   setRegisterValue(START_CONF_REGISTER,1,START_START_BIT, START_BITS_COUNT);    	  
}

void stopOperation()
{
   setRegisterValue(START_CONF_REGISTER,0,START_START_BIT, START_BITS_COUNT); 
}

void setNumberOfBlocksToPage(uint32_t blocks)
{
   setRegisterValue(NUMBER_OF_BLOCKS_REGISTER,blocks,0, 32); 	
}

void setContinueBit()
{
	setRegisterValue(9,1,6, 1); 
}

uint32_t readFlagsRegister()
{
	
	uint32_t absAddress =CONFIG(9);// CONFIG_9_ADDRESS;// TIRUR_CONFIG_BASE + (9<<2);

#ifdef DEBUG	
	printf("Address =%x\n",absAddress);
#endif 	

	uint32_t doneRegister = reg_read32(absAddress);	
	return doneRegister;
}

bool isCurrOperationDone(){
#ifdef DEBUG
	printf("Reading flags register\n");
#endif  

   	uint32_t value = readFlagsRegister();
   	uint32_t mask =0x4;
   	
#ifdef DEBUG
	printf("value= %x\n",value);
#endif   	
   	
   	return (value & mask) != 0;	
}

bool isFinished(){

#ifdef DEBUG
	printf("Reading flags register\n");
#endif  

   	uint32_t value = readFlagsRegister();
   	uint32_t mask =0x10;
   	
#ifdef DEBUG
	printf("value= %x\n",value);
#endif   	
   	
   	return (value & mask) != 0;	

}

uint32_t readResultRegister(int registerNumber){
    uint32_t absAddress =CONFIG(RESULTS_REGISTER_BASE+registerNumber);// TIRUR_CONFIG_BASE + ((RESULTS_REGISTER_BASE+registerNumber)<<2);
    uint32_t temp = reg_read32(absAddress);
    
#ifdef DEBUG
    printf("reading at address %x value %x\n",absAddress,temp);
#endif     
    
    return temp;
}

void writeInputRegiser(uint32_t regiserNumber,uint32_t value){
    
    setRegisterValue(RESULTS_REGISTER_BASE+regiserNumber,value,0, 32); 
    //uint32_t absAddress = TIRUR_CONFIG_BASE + ((RESULTS_REGISTER_BASE+regiserNumber)<<2);
    //reg_write32(absAddress, value); 
    
/*#ifdef DEBUG
    printf("writting at address %x value %x\n",absAddress,value);
#endif*/     
    
}


void wrightMemory(uint32_t relativeAddress,uint32_t value)
{
   uint32_t absAddress = TIRUR_BASE + (relativeAddress<<2);
#ifdef DEBUG   
   printf("writting at %x value =%x\n",absAddress,value); 	
#endif    
    if(absAddress >= (TIRUR_BASE + TIRUR_SOC_MEMORY_SIZE))
    {
    	//exit(ERROR_SOC_ADDRESS_OUT_OF_RANGE);
    }	

   reg_write32(absAddress, value);
}

uint32_t readMemory(uint32_t relativeAddress)
{
	uint32_t absAddress = TIRUR_BASE + (relativeAddress<<2);
#ifdef DEBUG	
	printf("Calculated Address is %x\n",absAddress);
#endif 	
	uint32_t temp = reg_read32(absAddress);
#ifdef DEBUG	
	printf("reading at address %x value = %x\n",absAddress,temp);
#endif 	
        return temp;
}
