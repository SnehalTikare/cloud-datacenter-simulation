package com.CS441.cloudsim.Simulations

import com.CS441.cloudsim.Base.{CloudletBase, DataCenterBase}
import com.CS441.cloudsim.utils.ConfigApplications
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudsimplus.util.Log

import collection.JavaConverters._

class IaasSimulation extends LazyLogging{
  /**
  This class shows implementation of Infrastructure as a Service
  Here, the user has control over the hardware and software specification of VM and Cloudlets
  Configurations are specified in the config file under Infrastructure as a Service

  User specifies the following things -
  1)Number of Virtual Machine and their specification
  2)VM Allocation policy to be used by data center for allocation of VM
  3)Number of cloudlets and their specification
  4)Utilization Model to be used by Cloudlet
  **/

  logger.info("Running Infrastructure as a Service Simulation ")
  Thread.sleep(1500)
  val config = new ConfigApplications
  val sim = new CloudSim()
  val db = new DataCenterBase
  logger.info("Creating a Simple DataCenter with First Fit VM Allocation Policy and Host Type 1..")
  val datacenter = db.createDataCenter(config.HOSTS_NO,config.SERVICE_PROVIDER.getString("infra_datacenter"),config.DATACENTER_TYPE.getString("Simple"),config.infra_vm_policy,sim,config.HOST_TYPE.getString("infra_host"))
  logger.info("Creating a list of Simple VM with Time Shared Scheduler for Cloudlets")
  val vmList = db.createVmList(config.infra_vm_no,config.VM_TYPE.getString("Simple"), config.infra_scheduler,config.VM_ARCH.getString("infra_vm"))
  logger.info("Creating a list of Simple Cloudlets")
  val cloudlet = new CloudletBase
  val cloudletList = cloudlet.createCloudlets(config.infra_cloudlets.getInt("cloudlets_num"),config.CLOUDLET_TYPE.getString("infra_cloudlet"),config.infra_util)
  logger.info("Creating a single broker to submit VM and cloudlets")
  val broker = new DatacenterBrokerSimple(sim)
  broker submitVmList(vmList.asJava)
  broker.submitCloudletList(cloudletList.asJava)
  logger.info("Starting the simulation ")
  Thread.sleep(1500)
  sim.start
  val finishedCloudlets = broker.getCloudletFinishedList()
  new CloudletsTableBuilder(finishedCloudlets).build()

}
