package com.CS441.cloudsim.Base

import com.typesafe.config.Config
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.switches.{AggregateSwitch, EdgeSwitch, RootSwitch, Switch}
import org.cloudbus.cloudsim.network.topologies.{BriteNetworkTopology, NetworkTopology}

import scala.reflect.ClassTag

class NetworkEntities extends DataCenterBase {
  /**
   * Creates a Network within the Datacenter
   * @param simulation
   * @param datacenter
   */
  def createNetwork(simulation: CloudSim,datacenter: NetworkDatacenter):Unit = {
    logger.info("Creating Root Switches" )
    val rootswitch = createSwitchInstance[RootSwitch](configs.ROOT_SWITCH,
      simulation: CloudSim,
      datacenter: NetworkDatacenter,
      configs.DATACENTERS.getConfig(configs.ROOT_SWITCH).getDouble("bandwidth"),
      configs.DATACENTERS.getConfig(configs.ROOT_SWITCH).getInt("port"),
      configs.DATACENTERS.getConfig(configs.ROOT_SWITCH).getDouble("delay"))
    logger.info("Creating Aggregates Switches" )
    val aggregateswitches = createAggregateSwitches(simulation: CloudSim,datacenter: NetworkDatacenter,rootswitch,configs.DATACENTERS.getConfig(configs.AGGREGATE_SWITCH))
    logger.info("Creating Edge Switches" )
    val edgeswitches = createEdgeSwitches(simulation: CloudSim,datacenter: NetworkDatacenter,aggregateswitches,configs.DATACENTERS.getConfig(configs.EDGE_SWITCH))
    val networkHostList = datacenter.getHostList[NetworkHost]
    networkHostList.forEach { host =>
      val switch_num = getSwitchIndex(host, configs.DATACENTERS.getConfig(configs.EDGE_SWITCH).getInt("port"))
      host.setEdgeSwitch(edgeswitches(switch_num).asInstanceOf[EdgeSwitch])
      //edgeswitches(switch_num).connectHo st(host)
    }

  }

  /**
   * Creates an Edge Switch to which Hosts are connected
   * @param sim - Simulation Object
   * @param datacenter - Datacenter within which switches are created
   * @param aggregate - Aggregate Switch to which Edge Switches connect
   * @param config - Config Object
   * @return List of Edge Switches
   */
  def createEdgeSwitches(sim: CloudSim, datacenter: NetworkDatacenter, aggregate: List[Switch], config: Config): List[Switch] ={
    (1 until config.getInt("num")).map{
      ed =>
        val edSwitch = createSwitchInstance[EdgeSwitch](configs.EDGE_SWITCH,   sim: CloudSim,
          datacenter: NetworkDatacenter,
          configs.DATACENTERS.getConfig(configs.EDGE_SWITCH).getDouble("bandwidth"),
          configs.DATACENTERS.getConfig(configs.EDGE_SWITCH).getInt("port"),
          configs.DATACENTERS.getConfig(configs.EDGE_SWITCH).getDouble("delay"))
        datacenter.addSwitch(edSwitch)
        val agswitchnum = ed / configs.DATACENTERS.getConfig(configs.AGGREGATE_SWITCH).getInt("port")
        edSwitch.getUplinkSwitches.add(aggregate(agswitchnum))
        aggregate(agswitchnum).getDownlinkSwitches.add(edSwitch)
        edSwitch
    }.toList

  }

  /**
   * Creates an Aggregate Switch which connects to the Root Switch
   * @param sim - Simulation Object
   * @param datacenter - Network Datacenter
   * @param rootswitch - Root Switch
   * @param config
   * @return List of Aggregate Switches
   */
  def createAggregateSwitches(sim: CloudSim, datacenter: NetworkDatacenter, rootswitch: Switch, config:Config): List[Switch] ={
    (1 to config.getInt("num")).map{
      ag =>
        val agSwitch = createSwitchInstance[AggregateSwitch](configs.AGGREGATE_SWITCH,
          sim: CloudSim,
          datacenter: NetworkDatacenter,
          configs.DATACENTERS.getConfig(configs.AGGREGATE_SWITCH).getDouble("bandwidth"),
          configs.DATACENTERS.getConfig(configs.AGGREGATE_SWITCH).getInt("port"),
          configs.DATACENTERS.getConfig(configs.AGGREGATE_SWITCH).getDouble("delay"))
        datacenter.addSwitch(agSwitch)
        rootswitch.getDownlinkSwitches.add(agSwitch)
        agSwitch.getUplinkSwitches.add(rootswitch)
        agSwitch
    }.toList

  }

  /**
   * Create a Switch based on the given Type - Root Switch, Aggregate Switch or Edge Switch
   * @param SwitchType Root Switch, Aggregate Switch or Edge Switch
   * @param bw - the bandwidth with which the Switch has to communicate with Switches in the lower layer or Upper layer.
   * @param port -the number of ports the switch has.
   * @param delay the latency time the switch spends to process a received packet.
   * @return returns the created Switch
   */
  def createSwitchInstance[T:ClassTag](SwitchType:String,simulation: CloudSim,datacenter: NetworkDatacenter,bw:Double, port:Int, delay:Double): T ={
    //val tClass = implicitly[ClassTag[T]].runtimeClass
    val switch = SwitchType match{
      case "RootSwitch" => new RootSwitch(simulation,datacenter)
      case "AggregateSwitch" => new AggregateSwitch(simulation,datacenter)
      case "EdgeSwitch" => new EdgeSwitch(simulation,datacenter)
    }
    switch.setPorts(port)
    switch.setUplinkBandwidth(bw)
    switch.setDownlinkBandwidth(bw)
    switch.setSwitchingDelay(delay)
    switch.asInstanceOf[T]
  }

  def getSwitchIndex(host: NetworkHost, switchPorts: Int): Int = (host.getId % Integer.MAX_VALUE).toInt / switchPorts

   def configureNetwork(sim: CloudSim,datacenter: NetworkDatacenter, broker: DatacenterBroker): Unit = { //load the network topology file
    val networkTopology = BriteNetworkTopology.getInstance(configs.NETWORK_TOPOLOGY_FILE)
    sim.setNetworkTopology(networkTopology)
    var briteNode = 0
    networkTopology.mapNode(datacenter.getId, briteNode)
    briteNode = 3
    networkTopology.mapNode(broker.getId, briteNode)
  }
}
