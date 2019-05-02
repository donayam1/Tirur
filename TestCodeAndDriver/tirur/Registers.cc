#include "Registers.h"
#include "../mmio.h"

uint32_t Conf_reg_buffer[NUM_OF_CONFIG_REGS];

void initRegisters(){
#ifdef DEBUG
   printf("init regis\n");
#endif    
   //clean buffers	
   for(int i=0;i<NUM_OF_CONFIG_REGS;i++)
   {
   	Conf_reg_buffer[i] = 0;
   }
}

uint32_t CONFIG(int i)
{
	switch(i)
	{
	  case 0:
	     return CONFIG_0_ADDRESS;
	  case 1:
	     return CONFIG_1_ADDRESS;
	  case 2:
	     return CONFIG_2_ADDRESS;	   
	  case 3:
	     return CONFIG_3_ADDRESS;
	  case 4:
	     return CONFIG_4_ADDRESS;      
	  case 5:
	     return CONFIG_5_ADDRESS;   
	  case 6:
	     return CONFIG_6_ADDRESS;   
	  case 7:
	     return CONFIG_7_ADDRESS;   
	  case 8:
	     return CONFIG_8_ADDRESS;
	  case 9:
	     return CONFIG_9_ADDRESS;
	  default:
	     return CONFIG_9_ADDRESS;   
	}
}

void setRegisterValue(int registernumber,uint32_t value,int startbit, uint32_t numberOfBits)
{
#ifdef DEBUG
   printf("inside set r value\n");
#endif    
   uint32_t mask = 0;
   for(int i=0;i<numberOfBits;i++)
   {
   	mask += 1<<i;
   }   
   mask = mask<<startbit;
#ifdef DEBUG   
   printf("mask is %x\n",mask);
#endif    
   uint32_t newValue = (value<<startbit) & (mask);	    	
   uint32_t tmp = Conf_reg_buffer[registernumber] & ~mask;
   
   Conf_reg_buffer[registernumber] = tmp | newValue;
   uint32_t reg =CONFIG(registernumber); 
#ifdef DEBUG   
   printf("new value is  %x and reg is %x\n",Conf_reg_buffer[registernumber],reg);
#endif    
   reg_write32(reg, Conf_reg_buffer[registernumber]);  
}
