GCC=riscv64-unknown-elf-gcc
OBJDUMP=riscv64-unknown-elf-objdump
CFLAGS=-mcmodel=medany -std=gnu99 -O2 -fno-common -fno-builtin-printf -Wall
LDFLAGS=-static -nostdlib -nostartfiles -lgcc
DBFLAGS=-g -Og 

PROGRAMS = pwm blkdev accum charcount nic-loopback pingd sram
PROGRAMS2 = sram

default: $(addsuffix .riscv,$(PROGRAMS))

dumps: $(addsuffix .dump,$(PROGRAMS))

debug: $(addsuffix .idebug,$(PROGRAMS2))


%.idebug:	%.o crt.o syscalls.o link.ld
	$(GCC) $(CFLAGS) -c ./tirur/Registers.cc 
	$(GCC) $(CFLAGS) -c ./tirur/OperationModes.cc 
	$(GCC) $(CFLAGS) -c ./tirur/System.cc 
	$(GCC) $(CFLAGS) -c ./sram.cc 
	$(GCC) -T link.ld $(DBFLAGS) $(LDFLAGS) System.o Registers.o OperationModes.o $< crt.o syscalls.o -o $@
	 
%.o: %.S
	$(GCC) $(CFLAGS) -D__ASSEMBLY__=1 -c $< -o $@

%.o: %.c mmio.h
	$(GCC) $(CFLAGS) -c $< -o $@

%.riscv: %.o crt.o syscalls.o link.ld
	$(GCC) -T link.ld $(LDFLAGS) $< crt.o syscalls.o -o $@

%.dump: %.riscv
	$(OBJDUMP) -D $< > $@

clean:
	rm -f *.riscv *.o *.dump *.idebug *.idebugdef
	
