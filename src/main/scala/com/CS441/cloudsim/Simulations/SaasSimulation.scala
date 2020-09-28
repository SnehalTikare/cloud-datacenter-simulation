package com.CS441.cloudsim.Simulations

import com.CS441.cloudsim.Base.{CloudletBase, DataCenterBase, NetworkEntities}
import com.CS441.cloudsim.utils.ConfigApplications
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudsimplus.util.Log

import scala.collection.JavaConverters._

class SaasSimulation extends LazyLogging{
  /**
  This class shows implementation of Software as a Service
  The simulation show an application to transfer data(Like a file transfer service)
  Cloudlets use the service to transfer the packets between two VM hosted on two different hosts
  The user only invokes this application and has no control over the internal hardware and software specification
   **/

  //Log.setLevel(ch.qos.logback.classic.Level.ERROR);

  logger.info("Running Software as a Service Simulation ")
  logger.info("Starting the application to transfer file")
  Thread.sleep(1000)

  val config = new ConfigApplications
  val nw_simulation_3 = new CloudSim
  val nw_datacenterbase =  new DataCenterBase
  val network_entity = new NetworkEntities
  //Creating network datacenter
  val nw_datacenter = nw_datacenterbase.createDataCenter(config.HOSTS_NO,config.SERVICE_PROVIDER.getString("datacenter1"),config.DATACENTER_TYPE.getString("Network"),config.FIRST_FIT,nw_simulation_3,config.HOST_TYPE.getString("host1"))
  //List of Network VM with Time Shared Policy
  val nw_vmList = nw_datacenterbase.createVmList(config.HOSTS_NO,config.VM_TYPE.getString("Network"), config.CLOUDLETS_SCHEDULER.getString("TimeShared"),config.VM_ARCH.getString("vm1"))
  network_entity.createNetwork(nw_simulation_3,nw_datacenter.asInstanceOf[NetworkDatacenter],config.SERVICE_PROVIDER.getString("datacenter1"))
  val broker = new DatacenterBrokerSimple(nw_simulation_3)
  val cloudletList = new CloudletBase().createListNetworkCloudlets(nw_vmList)
  broker.submitVmList(nw_vmList.asJava)
  broker.submitCloudletList(cloudletList.asJava)

  nw_simulation_3.start
  showSimulationResults()

  private def showSimulationResults(): Unit = {
    val newList = broker.getCloudletFinishedList
    new CloudletsTableBuilder(newList).build()
    val hostslists = nw_datacenter.getHostList[NetworkHost].asScala.filter(_.getTotalDataTransferBytes!=0)
    hostslists.foreach(
      host =>
        printf("%nHost %d data transferred: %d bytes", host.getId, host.getTotalDataTransferBytes)
    )
    println(" ")
    println(getClass.getSimpleName + " finished!")
  }

}
