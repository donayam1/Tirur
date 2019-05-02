#ifndef _CONFIG_H
#define _CONFIG_H

#define DEBUG

#define TIRUR_BASE 0x52000000 //base address inside ROC
#define TIRUR_SIZE 0x10000 //the maximum size of tirur 
#define TIRUR_SOC_MEMORY_SIZE 0x2000*4 //the onchip memory has 8kx32bit size, *4 bc the address increments in 4 
#define TIRUR_ADDRESS_END TIRUR_BASE + TIRUR_SIZE - 4 
#define TIRUR_CONFIG_BASE TIRUR_BASE + TIRUR_SOC_MEMORY_SIZE //the config registers follow the memory immidiatly,   


#endif 
