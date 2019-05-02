RunFile = "intgCheck.elaborateutop"
tc = "intgCheck.UModuleTopTester"
clean:
	rm -f *.fir *.tmp *.v *.anno *.xml
	rm -rf ./test_run_dir
test:
	sbt "testOnly intgCheck.ModuleTopTester"
	sbt "testOnly intgCheck.ModuleTopPinTester"
	sbt "testOnly intgCheck.ModuleTopPinVectorTester"
	sbt "testOnly intgCheck.ModuleTopPoVectorTester"
utest:
	sbt "testOnly intgCheck.UModuleTopTester"
	sbt "testOnly intgCheck.UModuleTopPinTester"
	sbt "testOnly intgCheck.UModuleTopPinVectorTester"
	sbt "testOnly intgCheck.UModuleTopPoVectorTester"	
run:
	sbt "run-main $(RunFile)"
tc:
	sbt "testOnly $(tc)"			
