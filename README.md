# Homework 1 - Cloud Simulation
## Description: 
Create simulations for evaluate of different characteristics and deployment models in a datacenter and studying their results

## Overview
This assignment simulates the working of datacenter and various service implementation in cloud computing.The assignment
uses Cloud Sim plus framework to simulate the model and is developed using Scala.It shows various implementation
of scheduling and allocation policies and their effects on execution time, provision of resources.It also shows
the result configuring parameters and their effect on the allocation.

## Instructions
### Prerequisites
- Sbt - This assignments need sbt to build the project;
- Clone the repository to your local system
using the command. Or download the zip;
```
git clone https://SnehalTikare@bitbucket.org/cs441-fall2020/snehal_tikare_hw1.git
```
- Navigate to the directory of the project;
- Use the below command to build and run the project;
```
sbt clean compile run
```
- To run the test 
```
sbt test
```
(The tests are written in Java, so if you don't see them then run the **TestClass.scala** in  src/test/scala)

###Program Execution:

-The below options are displayed once the program is run.
Choose from the options to run the desired simulations
```
|------------------------------|
| No | Simulation Name         |
|------------------------------|
| 1  | Simulation example 1    |
| 2  | Simulation example 2    |
| 3  | IaaS Simulation         |
| 4  | PaaS Simulation         |
| 5  | SaaS Simulation         |
|------------------------------|

```

Simulation example 1 and Simulation example 2 show results of various allocation policies and configuration parameters

#### IaaS Simulation
This class shows implementation of Infrastructure as a Service. Here, the user has control over the hardware and software specification of VM and Cloudlets. 
Configurations are specified in the config file under **“Infrastructure as a Service”**

User specifies the following things -
- Number of Virtual Machine and their specification;
- VM Allocation policy to be used by data center for allocation of VM;
- Number of cloudlets and their specification;
- Utilization Model to be used by Cloudlet;

