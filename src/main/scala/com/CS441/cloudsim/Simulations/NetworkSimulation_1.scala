package com.CS441.cloudsim.Simulations

import java.util
import java.util.List

import com.CS441.cloudsim.Base.{CloudletBase, DataCenterBase, NetworkEntities}
import com.CS441.cloudsim.utils.ConfigApplications
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.vms.network.NetworkVm
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import scala.collection.JavaConverters._

class NetworkSimulation_1 extends LazyLogging{
  val config = new ConfigApplications
  val nw_simulation_3 = new CloudSim
  val nw_datacenterbase =  new DataCenterBase
  val network_entity = new NetworkEntities
  val nw_datacenter = nw_datacenterbase.createDataCenter(config.SERVICE_PROVIDER.getString("datacenter1"),config.DATACENTER_TYPE.getString("Network"),config.FIRST_FIT,nw_simulation_3,config.HOST_TYPE.getString("host1"))
  val nw_vmList = nw_datacenterbase.createVmList(config.VM_NO,config.VM_TYPE.getString("Network"), config.CLOUDLETS_SCHEDULER.getString("TimeShared"),config.VM_ARCH.getString("vm1"))
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
    print(newList.get(0).asInstanceOf[Cloudlet].getTotalCost)
    nw_datacenter.getHostList[NetworkHost].forEach(
      host =>
      printf("%nHost %d data transferred: %d bytes", host.getId, host.getTotalDataTransferBytes)
    )
    println(" ")
    println(getClass.getSimpleName + " finished!")
  }

}
