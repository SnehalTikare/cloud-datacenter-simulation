package com.CS441.cloudsim.Simulations

import com.CS441.cloudsim.Base.{DataCenterBase, NetworkEntities}
import com.CS441.cloudsim.utils.ConfigApplications
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter

class NetworkSimulation_1 extends LazyLogging{
  val config = new ConfigApplications
  val nw_simulation_3 = new CloudSim
  val nw_datacenterbase =  new DataCenterBase
  val network_entity = new NetworkEntities
  val nw_datacenter = nw_datacenterbase.createDataCenter(config.DATACENTER_TYPE.getString("Network"),config.FIRST_FIT,nw_simulation_3,config.HOST_TYPE.getString("host1"))
  val nw_vmList = nw_datacenterbase.createVmList(config.VM_NO,config.VM_TYPE.getString("Simple"), config.CLOUDLETS_SCHEDULER.getString("TimeShared"))
  network_entity.createNetwork(nw_simulation_3,nw_datacenter.asInstanceOf[NetworkDatacenter])

  nw_simulation_3.start

}
