# SUMO Experimentation

This repository provides the experimental setup to use the [SUMO traffic simulator](https://sumo.dlr.de/) to evaluate the expected number of queued vehicles at specific urban intersections with tram lines having right of way.

The results of the evaluation of the scenarios considered in this repository can be used to validate accuracy and complexity of the approach implemented by the [Omnibus Library](https://github.com/oris-tool/omnibus/) to derive optimal semaphore schedules for multimodal intersections. These experiments are reported in the paper titled "Efficient derivation of optimal signal schedules for multimodal intersections", authored by Nicola Bertocci, Laura Carnevali, Leonardo Scommegna, and Enrico Vicario, currently submitted to Simulation Modelling Practice and Theory.

## Experimental reproducibility

To support reproducibility of the experimental results reported in the paper and compare them with the ones obtained through the [Omnibus Library](https://github.com/oris-tool/omnibus/), this repository contains the code that builds and evaluates the considered traffic scenarios, and the steps reported below illustrate how to repeat the experiments.

Specifically, navigate to `test/java/org/oristool/sumo` and execute the main method of the Java classes listed below to reproduce the experiments (to execute the main method of a Java class in the Eclipse IDE, just open the class and click on the menu Run > Run as > Java Application):

- `Table2SuiteTestExperiment`:  for an intersection between a tram line made of two tram tracks and a vehicle flow (illustrated in Section 4.1 of the paper), this experiment computes the expected queue size over time for multiple scenarios with different values of street length, vehicle arrival rate, and maximum vehicle speed, writing results in the `/results` directory. This experiment should be launched before the `Table2ErrorComputing` experiment of the [Omnibus Library](https://github.com/oris-tool/omnibus/) where simulation results are used to compare the Omnibus analysis performance, producing the results reported in Table 2 of the paper.

- `Table3PatternComparator`: this experiment analyses the intersection among three vehicle flows and a tram line made of two tracks, shown in Figure 8 of the paper (and also reported below), under 390 different schedules for vehicle semaphores, generating a csv file where for each schedule, the maximum expected percentage of queue occupation of any flow is computed (the schedule yielding the minimum value is considered the optimal schedule). The results of this experiment are reported in the second column of Table 3 of the paper.

- `Table4PatternComparator`: this experiment is a variant of the one imlemented by the class `Table3PatternComparator.java` and produces the results reported in the second column of Table 4 of the paper.

- `Table5PatternComparator`: this experiment is a variant of the one imlemented by the class `Table3PatternComparator.java` and produces the results reported in the second column of Table 5 of the paper.

- `VaryingParamsSolutionPlotter`: this experiment simulates behavior of an intersection among three vehicle flows and a tram line made of two tracks, with time-varying vehicle arrival rates and tram departure period over 4~time intervals, considering within each time interval the optimal semaphore schedule at minimizing the expected percentage of queue occupation of each vehicle flow (computed trhough the [Omnibus Library](https://github.com/oris-tool/omnibus/).
  
<p align="center">
  <img src="imgs/threeFlows.png?raw=true" style="width:65%">
  <p align="center">
  <em>A graphical representation of an intersection among three vehicle flows and a bidirectional tram line. </em>
    </p>
</p>

## Installation and prerequisites

This repository provides a ready-to-use Maven project that you can easily import into an Eclipse workspace, to do so, just follow these steps:
1. **Install Java >= 11.** For Windows, you can download a [package from Oracle](https://www.oracle.com/java/technologies/downloads/#java11); for Linux, you can run `apt-get install openjdk-11-jdk`; for macOS, you can run `brew install --cask java`. 

2. **Download Eclipse.** The [Eclipse IDE for Java Developers](http://www.eclipse.org/downloads/eclipse-packages/) package is sufficient.

3. **Clone this project.** Inside Eclipse:
   - Select `File > Import > Maven > Check out Maven Projects from SCM` and click `Next`.
   - If the `SCM URL` dropbox is grayed out, click on `m2e Marketplace` and install `m2e-egit`. You will have to restart Eclipse.
   - As `SCM URL`, type: `git@github.com:oris-tool/sumo.git` and click `Next` and then `Finish`.

*Warning*: this repository assumes that you are launching the experiment from a Windows machine (>=Windows 10) and that you have already installed SUMO and set correctly the `SUMO_HOME` environment variable. For an in depth guide on how to install SUMO please refer to the guide at this [link](https://sumo.dlr.de/docs/Installing/index.html).

## License

This repository is released under the [Eclipse Public License v2.0](https://www.eclipse.org/legal/epl-2.0).


