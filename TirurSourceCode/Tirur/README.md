Trying to desing simple cpu using chisel

Steps

1. Download chisel_template from github 


To run testes
got to the root directroy of the project

e.g. To run AluTester class 
	sbt 'testOnly cpu.AluTester'
	
To generate verilog
       sbt 'run-main cpu.elaborator'	
       this will generate verilog file with same name as the main class name

