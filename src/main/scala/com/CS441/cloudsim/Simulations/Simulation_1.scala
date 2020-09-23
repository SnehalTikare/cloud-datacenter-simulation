package com.CS441.cloudsim.Simulations


import com.CS441.cloudsim.Base.{CloudletBase, DataCenterBase}
import com.CS441.cloudsim.utils.ConfigApplications
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudsimplus.util.Log

import collection.JavaConverters._

object Simulation_1 extends LazyLogging{

  /**
   * This Simulations shows how First Fit Allocation Policy and TimeShared Scheduling affects the assignment of VM and running of cloudlets
   * VM Allocation Policy - First Fit
   * Type of host - Host1
   * HOSTS - 4
   * HOSTS_PE - 4
   * TOTAL_PE - 16
   * HOSTS_MIPS - 2000
   * TOTAL_CAPACITY - 32000 MIPS
   * VM - 32
   * VM_PE - 2
   * TOTAL_VM_PE - 64
   * VM_MIPS- 500
   * TOTAL_VM_CAPACITY - 32000 MIPS
   * VM Scheduler - TimeShared
   * Cloudlet Scheduler - TimeShared
   * Utilization Model - 100%
   * Each Cloudlet is assigned to one VM
   * }
   */
  Log.setLevel(ch.qos.logback.classic.Level.INFO);

  logger.info("Running Simulation 1")
  logger.info("This Simulations shows how First Fit Allocation Policy and TimeShared Scheduling affects the assignment of VM and running of cloudlets")

  val config = new ConfigApplications
  val simulation_1 = new CloudSim
  val datacenterbase =  new DataCenterBase

  logger.info("Creating a Simple DataCenter with First Fit VM Allocation Policy and Host Type 1..")
  val datacenter = datacenterbase.createDataCenter(config.DATACENTER_TYPE.getString("Simple"),config.FIRST_FIT,simulation_1,config.HOST_TYPE.getString("host1"))
  datacenter.setSchedulingInterval(config.SCHEDULING_INTERVAL)
  logger.info("Creating a list of Simple VM with Time Shared Scheduler for Cloudlets")
  val vmList = datacenterbase.createVmList(config.VM_NO,config.VM_TYPE.getString("Simple"), config.CLOUDLETS_SCHEDULER.getString("TimeShared"))

  logger.info("Creating a list of Simple Cloudlets")
  val cloudlet = new CloudletBase
  val cloudletList = cloudlet.createCloudlets(config.CLOUDLETS_CPU_UTIL.getDouble("half"))

 // val cloudletList = cloudlet.createCloudletInVM(vmList,config.CLOUDLETS_CPU_UTIL.getDouble("full"))

  val broker = new DatacenterBrokerSimple(simulation_1)
  broker submitVmList(vmList.asJava)
  broker.submitCloudletList(cloudletList.asJava)

  simulation_1.start

  val finishedCloudlets1 = broker.getCloudletFinishedList()
  new CloudletsTableBuilder(finishedCloudlets1).build()

}