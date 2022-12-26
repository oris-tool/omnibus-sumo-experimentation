# SUMO Experimentation

This repository provides the experimental setup to evaluate with [SUMO traffic simulator](https://sumo.dlr.de/), the expected number of queued vehicles in specific urban intersection scenarios with tram lines having right of way.

Analysis results of the scenarios present in this repository can be used to validate the performances of the [Omnibus Library](https://github.com/oris-tool/omnibus/) (find the repository at this [link](https://github.com/oris-tool/omnibus/) ) and they are selected coherently to the paper titled "An efficient compositional approach to the derivation of optimal semaphore schedules for signalized urban intersections" 
authored by Nicola Bertocci, Laura Carnevali, Leonardo Scommegna and Enrico Vicario, currently submitted to the journal "Transportation Research Part C: Emerging Technologies".

## Experimental reproducibility

To support reproducibility of the experimental results reported in the paper and compare them with the one obtained with the [Omnibus Library](https://github.com/oris-tool/omnibus/), this repository contains the code that builds and evaluates the considered traffic scenarios.

Steps reported below illustrate how to repeat the experiments, specifically, it is sufficient to navigate to `test/java/org/oristool/omnibus` and execute the main method of the Java classes to reproduce the experiments.

To execute the main method of a Java class in Eclipse IDE, open the class and click on the menu Run > Run as > Java Application.

For each class, items below report a brief description of the related experiment:
- `Table1SuiteTestExperiment`: it analyses scenarios with different values of street length, queue capacity, vehicle arrival rate and maximum vehicle speed, finally writes results in the `/results` directory. This experiment should be launched before the `Table1ErrorComputing` experiment of the [Omnibus Library](https://github.com/oris-tool/omnibus/) where simulation results are used to compare the Omnibus analysis performance reproducing results reported in Table 1 of the paper.

- `Table2PatternComparator`: it analyses the intersection of Fig. 1 with arrival rates $&lambda;_1=0.05 s^{−1}$, $&lambda;_2=0.1 s^{−1}$ and $&lambda;_3=0.15 s^{−1}$ for each vehicle flow and maximum speed $V = 50 km h^{-1}$. Specifically the experiment will generate a csv file where for each schedule, the maximum expected percentage of queue occupation of any flow computed by the SUMO simulation. It reproduces results presented in the paper in Table 2.
- `Table3PatternComparator`: it is a variant of the `Table2PatternComparator` experiment with arrival rates $&lambda;_1=0.1 s^{−1}$, $&lambda;_2=0.2 s^{−1}$ and $&lambda;_3=0.3 s^{−1}$. It reproduces SUMO results presented in the paper in Table 3.
- `Table4PatternComparator`: it is a variant of the `Table2PatternComparator` experiment with maximum vehicle speed $V = 30 km h^{-1}$. It reproduces SUMO results presented in the paper in Table 4.
- `VaryingParamsSolutionPlotter`: still referring to the intersection of Fig. 1, it simulates a scenario where the tram arrival period $T$ and the arrival rates $\lambda_1$, $\lambda_2$, and $\lambda_3$ of vehicle flows vary within time intervals 1, 2, 3, and 4 reproducing the experiment represented in Figure 10 of the paper:
    - within time interval 1, $T=440 s$, $\lambda_1=0.025 s$, $\lambda_2= 0.025 s$, and $\lambda_3= 0.025 s$;
    - within time interval 2, $T=220 s$, $\lambda_1=0.1 s$, $\lambda_2=0.2 s$, and $\lambda_3=0.3 s$;
    - within time interval 3, $T=330 s$, $\lambda_1=0.05 s$, $\lambda_2=0.1 s$, and $\lambda_3=0.15 s$;
    - within time interval 4, $T=220 s$, $\lambda_1=0.075 s$, $\lambda_2=0.15 s$, and $\lambda_3=0.2 s$.
  
<p align="center">
  <img src="imgs/threeFlows.png?raw=true" style="width:65%">
  <p align="center">
  <em>Fig. 1 A graphical representation of an intersection among three vehicle flows and a bidirectional tram line. </em>
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

This repository is released under the ... License TBD


