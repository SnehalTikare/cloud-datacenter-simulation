package com.CS441.cloudsim.Simulations

import com.CS441.cloudsim.Base.{CloudletBase, DataCenterBase}
import com.CS441.cloudsim.Simulations.Simulation_1.logger
import com.CS441.cloudsim.utils.ConfigApplications
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudsimplus.util.Log
import collection.JavaConverters._

class Simulation_2 extends LazyLogging{
  /**
   * This Simulations shows how Round Robin Allocation Policy and TimeShared Scheduling affects the assignment of VM and running of cloudlets
   * VM Allocation Policy - Round Robin
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
   */
  Log.setLevel(ch.qos.logback.classic.Level.INFO);

  logger.info("Running Simulation 2")
  logger.info("This Simulations shows how TimeShared Scheduling affects the assignment of VM and running of cloudlets")

  val config = new ConfigApplications
  val simulation_2 = new CloudSim
  val datacenterbase =  new DataCenterBase

  logger.info("Creating a Simple DataCenter with Round Robin VM Allocation Policy and Host Type 1..")
  val datacenter = datacenterbase.createDataCenter(config.DATACENTER_TYPE.getString("Simple"),config.ROUNDROBIN,simulation_2,config.HOST_TYPE.getString("host1"))
  datacenter.setSchedulingInterval(config.SCHEDULING_INTERVAL)
  logger.info("Creating a list of Simple VM")
  val vmList = datacenterbase.createVmList(config.VM_NO,config.VM_TYPE.getString("Simple"), config.CLOUDLETS_SCHEDULER.getString("TimeShared"))

  logger.info("Creating a list of Simple Cloudlets")
  val cloudlet = new CloudletBase
  //val cloudletList = cloudlet.createCloudlets(config.CLOUDLETS_CPU_UTIL.getDouble("full"))

  val cloudletList = cloudlet.createCloudletInVM(vmList,config.CLOUDLETS_CPU_UTIL.getDouble("full"))

  val broker = new DatacenterBrokerSimple(simulation_2)
  broker submitVmList(vmList.asJava)
  broker.submitCloudletList(cloudletList)

  simulation_2.start


  val finishedCloudlets1 = broker.getCloudletFinishedList()
  new CloudletsTableBuilder(finishedCloudlets1).build()



}
