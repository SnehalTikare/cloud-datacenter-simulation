package com.CS441.cloudsim.Simulations

import com.CS441.cloudsim.Base.{CloudletBase, PaaS}
import com.CS441.cloudsim.utils.ConfigApplications
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import collection.JavaConverters._

class PaaS_Simulation {
  val simulation = new CloudSim()
  val paas = new PaaS()
  val config = new ConfigApplications
  val datacenter = paas.createDataCenterListWithVm(config.NUM_DATACENTERS,config.FIRST_FIT,simulation,config.VM_ARCH.getString("vm1"))
  //logger.info("Creating a list of Simple Cloudlets")
  val cloudlet = new CloudletBase
  val cloudletList = cloudlet.createCloudlets(config.CLOUDLETS_CPU_UTIL.getDouble("half"))
  val broker = new DatacenterBrokerSimple(simulation)
  val vms = broker.getVmCreatedList
  print(vms)
  broker.submitCloudletList(cloudletList.asJava)
  simulation.start
  val finishedCloudlets1 = broker.getCloudletFinishedList()
  new CloudletsTableBuilder(finishedCloudlets1).build()

}
