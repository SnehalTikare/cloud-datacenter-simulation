package com.CS441.cloudsim.Simulations

import java.util

import com.CS441.cloudsim.Base.{CloudletBase, DataCenterBase}
import com.CS441.cloudsim.utils.ConfigApplications
import com.CS441.tablebuilder.{CustomTableBuilder, DataCenterBrokerCustom}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter

import scala.collection.JavaConverters._
/**
This class provides the simulation for PaaS implementation
Based on the requirement of the user, the  cloudlets are assigned are to the Datacenter
Current implementation has 3 datacenters with different Hardware and Software Specification and different pricing
The cloudlet acts as the software application/web service that the user wants to run on the VM.
User chooses the service provider based on the requirements and pricing.
Service provider assigns the cloudlets in the chosen datacenter if it can accommodate the VM and Cloudlets.
**/

class PaasSimulation extends LazyLogging {
  //Log.setLevel(ch.qos.logback.classic.Level.ERROR);
  val simulation = new CloudSim()
  val base = new DataCenterBase()
  val config = new ConfigApplications
  logger.info("Running Platform as a Service Simulation ")
  Thread.sleep(1000)

  val dc1 = base.createDataCenter(config.HOSTS_NO,config.SERVICE_PROVIDER.getString("datacenter1"),config.DATACENTER_TYPE.getString("Simple"),config.FIRST_FIT,simulation,config.HOST_TYPE.getString("host1"))
  val dc2 = base.createDataCenter(config.HOSTS_NO,config.SERVICE_PROVIDER.getString("datacenter2"),config.DATACENTER_TYPE.getString("Simple"),config.BEST_FIT,simulation,config.HOST_TYPE.getString("host2"))
  val dc3 = base.createDataCenter(config.HOSTS_NO,config.SERVICE_PROVIDER.getString("datacenter3"),config.DATACENTER_TYPE.getString("Simple"),config.FIRST_FIT,simulation,config.HOST_TYPE.getString("host1"))
  val dclist = new util.ArrayList[Datacenter]
  dclist.add(dc1)
  dclist.add(dc2)
  dclist.add(dc3)
  logger.info("Dispalying the details of service provider ")
  Thread.sleep(1500)
  println(" ")
  println("======================================== Datacenter pricing and availibility ===========================================")
  println("-----------------------------------------------------------------------------------------------------------------------|")
  println("| Datacenter | VM Architecture | Cost Per Second($) | Cost Per Memory($) | Cost Per Storage($) | Cost Per Bandwidth($) |")
  println("|----------------------------------------------------------------------------------------------------------------------|")
  dclist.forEach(
    dc =>
      printf("| %-10s | %-15s | %-18s | %-18s | %-19s | %-19.5s   |%n",dc.getId(), dc.getCharacteristics.getArchitecture, dc.getCharacteristics.getCostPerSecond,  dc.getCharacteristics.getCostPerMem, dc.getCharacteristics.getCostPerStorage, dc.getCharacteristics.getCostPerBw)

  )

  println("|----------------------------------------------------------------------------------------------------------------------|")

  println(" ")
  println("Choose the Datacenter for allocating the task ")
  println("Choose from Options - 1, 2, 3")

  def findDataType(x: Any) = x match {
       case x : Int => println("Integer identified")
       case _ => println("Please enter a valid option")
  }
  val c = scala.io.StdIn.readInt()
  val broker =
    c match {
      case 1  => new DataCenterBrokerCustom (simulation, 0)
      case 2  => new DataCenterBrokerCustom (simulation, 1)
      case 3  => new DataCenterBrokerCustom (simulation, 2)
    }
  println("")
  printf("Assigning cloudlets to Datacenter - %d%n ", c)

  val vmList = base.createVmList(config.VM_NO,config.VM_TYPE.getString("Simple"), config.CLOUDLETS_SCHEDULER.getString("TimeShared"),config.VM_ARCH.getString("vm1"))
  val cloudlet = new CloudletBase()
  val cloudletList = cloudlet.createCloudlets(config.NO_OF_CLOUDLETS,config.CLOUDLET_TYPE.getString("cloudlet"),config.CLOUDLETS_CPU_UTIL.getDouble("full"))

  broker.submitVmList(vmList.asJava)
  broker.submitCloudletList(cloudletList.asJava)
  simulation.start

  val finishedCloudlets1 = broker.getCloudletFinishedList()
  val finishcloudletList = broker.getCloudletFinishedList.asInstanceOf[java.util.List[Cloudlet]].asScala
  new CustomTableBuilder(finishedCloudlets1).build()
}
