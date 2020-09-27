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

### Program Execution:

- The below options are displayed once the program is run.
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

User specifies the following things.

-  Number of Virtual Machine and their specification;
-  VM Allocation policy to be used by data center for allocation of VM;
-  Number of cloudlets and their specification;
-  Utilization Model to be used by Cloudlet;

#### PaaS Simulation
This class provides the simulation for Platform as a Service implementation. Based on the requirement of the user, the cloudlets are assigned are to the Datacenter. Current implementation has 3 datacenters with different Hardware and software specification and different pricing. The cloudlet acts as the software application/web service that the user wants to run on the VM. User chooses the service provider based on the requirements and pricing. Service provider assigns the cloudlets in the chosen datacenter if it can accommodate the VM and Cloudlets.
Configuration for PaaS Cloudlet can be specified in the config file. Change the parameters for “Cloudlets”
```
 
======================================== Datacenter pricing and availibility ===========================================
-----------------------------------------------------------------------------------------------------------------------|
| Datacenter | VM Architecture | Cost Per Second($) | Cost Per Memory($) | Cost Per Storage($) | Cost Per Bandwidth($) |
|----------------------------------------------------------------------------------------------------------------------|
| 1          | Linux x86       | 0.05               | 2.35               | 1.4                 | 0.5                   |
| 2          | MacOS           | 1.0                | 0.04               | 0.86                | 0.45                  |
| 3          | Windows 10      | 0.15               | 1.25               | 0.08                | 0.56                  |
|----------------------------------------------------------------------------------------------------------------------|
 
Choose the Datacenter for allocating the task 
Choose from Options - 1, 2, 3
```
```
Choose from Options - 1, 2, 3
2
```
```

                                                                SIMULATION RESULTS

Cloudlet|Status |DC|Host|Host PEs |VM|VM PEs   |CloudletLen|CloudletPEs|StartTime|FinishTime|ExecTime|Cost Per Second|Cost Per Bandwidth|Total Cost
      ID|       |ID|  ID|CPU cores|ID|CPU cores|         MI|  CPU cores|  Seconds|   Seconds| Seconds|              $|                 $|         $
---------------------------------------------------------------------------------------------------------------------------------------------------
       0|SUCCESS| 2|   0|        6| 0|        2|      10000|          1|        0|        10|      10|         1.0000|            0.4500|  280.1100
       8|SUCCESS| 2|   0|        6| 0|        2|      10000|          1|        0|        10|      10|         1.0000|            0.4500|  280.1100
       1|SUCCESS| 2|   1|        6| 1|        2|      10000|          1|        0|        10|      10|         1.0000|            0.4500|  280.1100
       9|SUCCESS| 2|   1|        6| 1|        2|      10000|          1|        0|        10|      10|         1.0000|            0.4500|  280.1100
       2|SUCCESS| 2|   2|        6| 2|        2|      10000|          1|        0|        10|      10|         1.0000|            0.4500|  280.1100
       3|SUCCESS| 2|   3|        6| 3|        2|      10000|          1|        0|        10|      10|         1.0000|            0.4500|  280.1100
       4|SUCCESS| 2|   4|        6| 4|        2|      10000|          1|        0|        10|      10|         1.0000|            0.4500|  280.1100
       5|SUCCESS| 2|   5|        6| 5|        2|      10000|          1|        0|        10|      10|         1.0000|            0.4500|  280.1100
       6|SUCCESS| 2|   6|        6| 6|        2|      10000|          1|        0|        10|      10|         1.0000|            0.4500|  280.1100
       7|SUCCESS| 2|   7|        6| 7|        2|      10000|          1|        0|        10|      10|         1.0000|            0.4500|  280.1100
---------------------------------------------------------------------------------------------------------------------------------------------------
```

#### SaaS Simulation
This class shows implementation of Software as a Service. The simulation shows an application to transfer data (Like a file transfer service). Cloudlets use the service to transfer the packets between two VM hosted on two different hosts. The user only invokes this application and has no control over the internal hardware and software specification. Specification file size can be specified in SaaS_Cloudlets in the config file.

```
                                         SIMULATION RESULTS

Cloudlet|Status |DC|Host|Host PEs |VM|VM PEs   |CloudletLen|CloudletPEs|StartTime|FinishTime|ExecTime
      ID|       |ID|  ID|CPU cores|ID|CPU cores|         MI|  CPU cores|  Seconds|   Seconds| Seconds
-----------------------------------------------------------------------------------------------------
       0|SUCCESS| 1|   1|        6| 1|        2|      10000|          1|        0|        10|      10
       1|SUCCESS| 1|   2|        6| 2|        2|      10000|          1|        0|        20|      20
-----------------------------------------------------------------------------------------------------

Host 1 data transferred: 3000 bytes 
```

More detailed explanation and discussion of the results is provided in **"Report.docx"**
