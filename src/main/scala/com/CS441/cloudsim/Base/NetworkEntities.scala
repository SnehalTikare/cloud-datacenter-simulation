package com.CS441.cloudsim.Base

import java.util

import com.typesafe.config.Config
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
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
  def createNetwork(simulation: CloudSim,datacenter: NetworkDatacenter,ServiceProvider:String):Unit = {
    val configDATACENTERS =
      ServiceProvider match {
        case "datacenter1" => configs.DATACENTERS1
        case "datacenter2" => configs.DATACENTERS2
        case "datacenter3" => configs.DATACENTERS3

      }
    logger.info("Creating Root Switches" )
    val rootswitch = createSwitchInstance[RootSwitch](configs.ROOT_SWITCH,
      simulation: CloudSim,
      datacenter: NetworkDatacenter,
      configDATACENTERS.getConfig(configs.ROOT_SWITCH).getDouble("bandwidth"),
      configDATACENTERS.getConfig(configs.ROOT_SWITCH).getInt("port"),
      configDATACENTERS.getConfig(configs.ROOT_SWITCH).getDouble("delay"))
    logger.info("Creating Aggregates Switches" )
    val aggregateswitches = createAggregateSwitches(simulation: CloudSim,datacenter: NetworkDatacenter,rootswitch,configDATACENTERS.getConfig(configs.AGGREGATE_SWITCH),configDATACENTERS)
    logger.info("Creating Edge Switches" )
    val edgeswitches = createEdgeSwitches(simulation: CloudSim,datacenter: NetworkDatacenter,aggregateswitches,configDATACENTERS.getConfig(configs.EDGE_SWITCH),configDATACENTERS)
    val networkHostList = datacenter.getHostList[NetworkHost]
    networkHostList.forEach { host =>
      val switch_num = getSwitchIndex(host, configDATACENTERS.getConfig(configs.EDGE_SWITCH).getInt("port"))
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
  def createEdgeSwitches(sim: CloudSim, datacenter: NetworkDatacenter, aggregate: List[Switch], config: Config,configDATACENTERS:Config): List[Switch] ={
    (1 until config.getInt("num")).map{
      ed =>
        val edSwitch = createSwitchInstance[EdgeSwitch](configs.EDGE_SWITCH,   sim: CloudSim,
          datacenter: NetworkDatacenter,
          configDATACENTERS.getConfig(configs.EDGE_SWITCH).getDouble("bandwidth"),
          configDATACENTERS.getConfig(configs.EDGE_SWITCH).getInt("port"),
          configDATACENTERS.getConfig(configs.EDGE_SWITCH).getDouble("delay"))
        datacenter.addSwitch(edSwitch)
        val agswitchnum = ed / configDATACENTERS.getConfig(configs.AGGREGATE_SWITCH).getInt("port")
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
  def createAggregateSwitches(sim: CloudSim, datacenter: NetworkDatacenter, rootswitch: Switch, config:Config,configDATACENTERS:Config): List[Switch] ={
    (1 to config.getInt("num")).map{
      ag =>
        val agSwitch = createSwitchInstance[AggregateSwitch](configs.AGGREGATE_SWITCH,
          sim: CloudSim,
          datacenter: NetworkDatacenter,
          configDATACENTERS.getConfig(configs.AGGREGATE_SWITCH).getDouble("bandwidth"),
          configDATACENTERS.getConfig(configs.AGGREGATE_SWITCH).getInt("port"),
          configDATACENTERS.getConfig(configs.AGGREGATE_SWITCH).getDouble("delay"))
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

  private def getSwitchIndex(host: NetworkHost, switchPorts: Int): Int = (host.getId % Integer.MAX_VALUE).toInt / switchPorts


  /**
   * Creates the network topology from a brite file.
   */
  def configureNetworkTopology(simulation:CloudSim,datacenterList:List[Datacenter], brokerList:List[DatacenterBroker]): Unit = { //load the network topology file
    val networkTopology = BriteNetworkTopology.getInstance("topology.brite")
    var count = 0
    simulation.setNetworkTopology(networkTopology)
    //Maps CloudSim entities to BRITE entities
    //Datacenter0 will correspond to BRITE node 0
    datacenterList.zipWithIndex.foreach{ case(x,i) =>
      networkTopology.mapNode(datacenterList(i).getId, i)
      count = i
    }
    brokerList.zipWithIndex.foreach { case (broker, j) =>
      networkTopology.mapNode(brokerList(j).getId,count)
      count+=1
    }
  }


}
