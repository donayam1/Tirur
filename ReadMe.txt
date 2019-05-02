How to build and run


1. Download the rocket core project template repository form GitHub
	https://github.com/ucb-bar/project-template.git
	
2. Open source/main/scala directory
3. From the provide source code on the CD copy the "Rocket Core Integration source" folder and pest it to the folder in 2
4. Open teriminal and navigate to the rocket core prject template's "verisim" folder 
5. Paste the following command to compile the project 
	make PROJECT=ram CONFIG=SRAMConfig
	
	This should build a binary in the verisim folder named similar to "simulator-ram-SRAMConfig"
	
Note that:-during the time of my work rocket core was still under continious development and things might change like how to integrate with rocket core. I have included folder named "Tirur source code" which can be used on its own to do testes and learn tirurs implementation.

How to run the simulator and driver testes 

1. Copy the contents of the test code and driver folder to rocket core's prject template tests folder 
2. Open terminal and run make 
3. Go to the verisim folder and run the following command 
	 ./simulator-ram-SRAMConfig  ../tests/sram.idebug 
	 

How to run tests without rocket core integration 
1. Install scala build tool 
2. Open terminal and navigate to Tirur source code  folder
3. Paste the follwing command to run e.g. ModuleTopTester  	 	 
	sbt 'testOnly intgCheck.ModuleTopTester' 
	If the scala build tool complains about stack overflow or similar error execute  the following line in the terminal and try again
	 export SBT_OPTS="-Xmx4000M -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled  -Xss1000M -Duser.timeZone=GMT"
