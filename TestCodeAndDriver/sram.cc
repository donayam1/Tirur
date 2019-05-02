
#include <stdio.h>
#include "mmio.h"
#include "tirur/System.h"
#include "tirur/OperationModes.h"
#include "tirur/Registers.h"

const int numRight=9;
const int blocks = numRight/4; 
int main(void) { 
	
	#ifdef DEBUG
		printf("hello tirur\n");
	#endif 
        initSystem();
        //setOperationModeWriteThrough();
        //setOperationModeIdeal();
         	
 	int init =0;
 	for(int i=init;i<init+numRight;i++)
 	{	 	
	 	int wval = i;
	 	#ifdef DEBUG
		printf("writting %x at =%x\n",wval,CONFIG(i)); 
		#endif 	
		setRegisterValue(i,wval,0, 32);	
		//setRegisterValue(i,wval,0, 32);		
 	}
 	
 	#ifdef DEBUG
 	printf("data write done\n");
 	#endif 
 	for(int j=init;j<init+numRight;j++)
	{
	    uint32_t addrs = CONFIG(j);
	    uint32_t temp = reg_read32(addrs);	
	    	     //temp = reg_read32(addrs);	
	    #ifdef DEBUG
	    printf("read at addr= %x and value =%x\n",addrs,temp);
	    #endif 
	
	}
	
	
	
	
 	 		 		 	 
	return 0; 
}
