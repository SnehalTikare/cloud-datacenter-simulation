package com

import com.CS441.cloudsim.Base.DataCenterBase
import com.CS441.cloudsim.utils.ConfigApplications
import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudsimplus.util.Log
import com.CS441.cloudsim.Base.CloudletBase
import scala.collection.JavaConverters._

object simulator1 extends LazyLogging{
  Log.setLevel(ch.qos.logback.classic.Level.INFO);

  logger.info("Initializing CloudSim...")

  val config = new ConfigApplications

  val simulation1 = new CloudSim
  val datacenterbase =  new DataCenterBase

  logger.info("Creating a Simple DataCenter with Best Fit VM Allocation Policy and Host Type 1..")
  val datacenter = datacenterbase.createDataCenter(config.DATACENTER_TYPE.getString("Simple"),config.BEST_FIT,simulation1,config.HOST_TYPE.getString("host1"))

  logger.info("Creating a list of Simple VM")
  val vmList = datacenterbase.createVmList(config.VM_NO,config.VM_TYPE.getString("Simple"))

  logger.info("Creating a list of Simple Cloudlets")
  val cloudlet = new CloudletBase
  val cloudletList = cloudlet.createCloudlets()

  val broker = new DatacenterBrokerSimple(simulation1)
  broker submitVmList(vmList.asJava)
  broker.submitCloudletList(cloudletList.asJava)

  simulation1.start

  val finishedCloudlets1 = broker.getCloudletFinishedList()
  new CloudletsTableBuilder(finishedCloudlets1).build()


  def main(args: Array[String]): Unit = {
   simulator1
  }
}
