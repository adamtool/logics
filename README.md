A Framework for Temporal Logics
===============================
A framework for working with temporal logics.
This module is used for model checking distributed system against LTL, Flow-LTL, CTL, and Flow-CTL in AdamMC.

Contains:
---------
- data structures and parsers for
  * LTL,
  * Flow-LTL (cf. [ATVA'19](http://arxiv.org/abs/1907.11061)),
  * CTL,
  * Flow-CTL (cf. [ATVA'20](https://arxiv.org/abs/2007.07235)),
- transformer for CTL to alternating Büchi tree automaton,
- tools for creating formulas regarding Petri nets.

Integration:
------------
This module can be used as separate library and
- is integrated in: [adam](https://github.com/adamtool/adam), [adammc](https://github.com/adamtool/adammc), [webinterface](https://github.com/adamtool/webinterface),
- contains the packages: logics,
- depends on the repos: [libs](https://github.com/adamtool/libs), [framework](https://github.com/adamtool/framework).

Related Publications:
---------------------
For Flow-LTL
- _Bernd Finkbeiner, Manuel Gieseking, Jesko Hecking-Harbusch, Ernst-Rüdiger Olderog:_
  [Model Checking Data Flows in Concurrent Network Updates](https://doi.org/10.1007/978-3-030-31784-3_30). ATVA 2019: 515-533 [(Full Version)](http://arxiv.org/abs/1907.11061).

For Flow-CTL
- _Bernd Finkbeiner, Manuel Gieseking, Jesko Hecking-Harbusch, Ernst-Rüdiger Olderog:_
  [Model Checking Branching Properties on Petri Nets with Transits](
https://doi.org/10.1007/978-3-030-59152-6_22). ATVA 2020: 394-410 [(Full Version)](https://arxiv.org/abs/2007.07235).

A tool using the logics (AdamMC):
- _Bernd Finkbeiner, Manuel Gieseking, Jesko Hecking-Harbusch, Ernst-Rüdiger Olderog:_
  [AdamMC: A Model Checker for Petri Nets with Transits against Flow-LTL](https://doi.org/10.1007/978-3-030-53291-8_5). CAV (2) 2020: 64-76 [(Full Version)](https://arxiv.org/abs/2005.07130).

------------------------------------

How To Build
------------
A __Makefile__ is located in the main folder.
First, pull a local copy of the dependencies with
```
make pull_dependencies
```
then build the whole framework with all the dependencies with
```
make
```
To build a single dependencies separately, use, e.g,
```
make tools
```
To delete the build files and clean-up
```
make clean
```
To also delete the files generated by the test and all temporary files use
```
make clean-all
```
Some of the algorithms depend on external libraries or tools. To locate them properly create a file in the main folder
```
touch ADAM.properties
```
and add the absolute paths of the necessary libraries or tools:
```
libraryFolder=<path2repo>/dependencies/libs
aigertools=
dot=dot
time=/usr/bin/time
```
You may leave some of the properties open if you don't use the corresponding libraries/tools.

Tests
-----
You can run all tests for the module by just typing
```
ant test
```
For testing a specific class use for example
```
ant test-class -Dclass.name=uniolunisaar.adam.tests.logics.flowltl.TestFlowLTL
```
and for testing a specific method use for example
```
ant test-method -Dclass.name=uniolunisaar.adam.tests.transformers.TestCTL2ABTA -Dmethod.name=firstTest
```
