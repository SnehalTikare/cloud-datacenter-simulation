package com.CS441.cloudsim.Base

import java.util.ArrayList

import com.typesafe.scalalogging.{LazyLogging, Logger}
import com.CS441.cloudsim.utils.ConfigApplications
import com.typesafe.config.Config
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit, VmAllocationPolicyRoundRobin, VmAllocationPolicyWorstFit}
import org.cloudbus.cloudsim.core.{CloudSim, Simulation}
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.network.switches.{AggregateSwitch, EdgeSwitch, RootSwitch, Switch}
import org.cloudbus.cloudsim.vms.network.NetworkVm
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}

import scala.collection.JavaConverters._
import scala.reflect.ClassTag


class DataCenterBase extends LazyLogging{
  //Load Configurations
  val configs = new ConfigApplications

  /**
   * Creates a list of {@link Pe} with specified MIPS
   * @param PES_NO - No of Pe to be created
   * @param PES_CAPACITY - MIPS Capacity
   * @return
   */
  def createPesInstance(PES_NO: Int, PES_CAPACITY: Double): List[PeSimple] = {
    (1 to PES_NO)
      .map { pe =>
        new PeSimple(PES_CAPACITY)
      }
      .toList
  }

  def createPes(): List[Pe] = {
    createPesInstance(configs.HOSTS1.getInt("no_pes"), configs.PES_CAPACITY)
  }

  /**
   * Create a list of Hosts {@Link SimpleHost} and {@Link NetworkHost}
   * @param HOSTS_NO No of Hosts to be created
   * @param HostInstanceType Type of Host Network or Simple
   * @param HostType Different varieties of  Host with different parameters
   * @return
   */
  def createHosts(HOSTS_NO: Int,HostInstanceType:String, HostType:String ): List[Host] = {
    (1 to HOSTS_NO).map {
      host =>
        val pelist = createPes()
        HostType match {
          case "host1" => val host = createHostInstance(configs.HOSTS1.getLong("ram"), configs.HOSTS1.getLong("bandwidth"), configs.HOSTS1.getLong("storage"), pelist,HostInstanceType:String)
                           val scheduler = getVMScheduler(configs.HOSTS1.getString("vmscheduler"))
                            host.setVmScheduler(scheduler)
          case "host2" => val host = createHostInstance(configs.HOSTS2.getLong("ram"), configs.HOSTS2.getLong("bandwidth"), configs.HOSTS2.getLong("storage"), pelist,HostInstanceType:String)
                          val scheduler = getVMScheduler(configs.HOSTS2.getString("vmscheduler"))
                          host.setVmScheduler(scheduler)
        }
    }
      .toList
  }

  /**
   *  Create a Single of host with below parameters
   * @param RAM - RAM of each Host
   * @param BW - Bandwidth to be allocated to each Host
   * @param STORAGE - Size of the Disk
   * @param PES - Number of PE(Processing Element)
   * @param HostInstanceType - Network Host or Simple Host
   * @return the instance of created Host
   */
  def createHostInstance(RAM: Long, BW: Long, STORAGE: Long, PES: List[Pe],HostInstanceType:String): Host = {
    HostInstanceType match {
      case "NetworkHost" => new NetworkHost(RAM, BW, STORAGE, PES.asJava)
      case "SimpleHost" =>new HostSimple(RAM, BW, STORAGE, PES.asJava)
    }
  }

  /**
   * Creates a List of VM
   * @param VM_NO - Number of VM to be created
   * @param VM_TYPE - Type of VM - Simple or Network
   * * RAM - RAM of each VM(Virtual Machine)
   * * BW - Bandwidth to be allocated to each VM
   * * STORAGE - Size of the Disk of Virtual Machine
   * * PES - Number of PE(Processing Element)
   * @return List of VM
   */
  def createVmList(VM_NO:Int, VM_TYPE:String): List[Vm] ={
    (1 to VM_NO).map{
      vm =>
        VM_TYPE match{
          case "SimpleVm" => val vm = new VmSimple(configs.VMS.getInt("mips"),configs.VMS.getInt("vm_pes"))
                              vm.setBw(configs.VMS.getInt(" bandwidth")).setRam(configs.VMS.getInt(" ram")).setSize(configs.VMS.getInt(" size"))
          case "NetworkVm" => val vm = new NetworkVm(configs.VMS.getInt("mips"),configs.VMS.getInt("vm_pes"))
                              vm.setBw(configs.VMS.getInt(" bandwidth")).setRam(configs.VMS.getInt(" ram")).setSize(configs.VMS.getInt(" size"))
        }
    }.toList

  }

  /**
   * Creates the required type of Datacenter - Network or Simple
   * @param DataCenterType - Network or Simple
   * @param Vm_Allocation_Policy - Type of Policy used to allocate VM to hosts
   * @param simulation - instance of Simulation Object
   * @param HOSTTYPE - Type of Host to be created
   * @return Datacenter
   */

  def createDataCenter(DataCenterType:String,Vm_Allocation_Policy:String, simulation: CloudSim,HOSTTYPE:String) : Datacenter ={
   val DataCenter = DataCenterType match{
      case "SimpleDataCenter" => createSimpleDataCenter(Vm_Allocation_Policy:String, simulation: CloudSim,HOSTTYPE:String)
      case "NetworkDataCenter" => createNetworkDataCenter(Vm_Allocation_Policy:String, simulation: CloudSim,HOSTTYPE:String)
    }
    DataCenter.getCharacteristics().setCostPerBw(configs.DATACENTERS.getInt("costPerBandwidth"))
    DataCenter.getCharacteristics().setCostPerMem(configs.DATACENTERS.getInt("costPerMemory"))
    DataCenter.getCharacteristics().setCostPerSecond(configs.DATACENTERS.getInt("costPerSecond"))
    DataCenter.getCharacteristics().setCostPerStorage(configs.DATACENTERS.getInt("costPerStorage"))
    DataCenter

  }
  def createNetworkDataCenter(Vm_Allocation_Policy:String, simulation: CloudSim,HOSTTYPE:String) : Datacenter ={
    val hostlist = createHosts(configs.HOSTS_NO,configs.HOST_INSTANCE_TYPE.getString("Network"),HOSTTYPE:String).asJava
    val vmAllocationPolicy = getVMAllocatioPolicy(Vm_Allocation_Policy)
    val networkDataCenter = new NetworkDatacenter(simulation,hostlist,vmAllocationPolicy)
    networkDataCenter
  }

  def createSimpleDataCenter(Vm_Allocation_Policy:String, simulation: CloudSim,HOSTTYPE:String) : Datacenter ={
    val hostlist = createHosts(configs.HOSTS_NO,configs.HOST_INSTANCE_TYPE.getString("Simple"),HOSTTYPE:String).asJava
    val vmAllocationPolicy = getVMAllocatioPolicy(Vm_Allocation_Policy)
    val SimpleDataCenter = new DatacenterSimple(simulation,hostlist,vmAllocationPolicy)
    SimpleDataCenter
  }

  /**
   * Creates instances of different VM Allocation Policy
   * @param POLICY_NAME - Type of Policy to be created
   * @return Instance of VMAllocationPolicy
   */
  def getVMAllocatioPolicy(POLICY_NAME: String): VmAllocationPolicy ={
    POLICY_NAME match {
      case "FirstFit" => new VmAllocationPolicyFirstFit()
      case "BestFit" => new VmAllocationPolicyBestFit()
      case "RoundRobin" => new VmAllocationPolicyRoundRobin()
      case "WorstFit" => new VmAllocationPolicyWorstFit()
    }

  }

  /**
   * Creates a VM scheduler to provision PE for VM
   * @param SchedulerName - Type of VM Scheduler to be created - TimeShared or SpaceShared
   * @return Instance of VM Scheduler
   */
  def getVMScheduler(SchedulerName:String) : VmScheduler ={
    SchedulerName match{
      case "TimeShared" => new VmSchedulerTimeShared()
      case "SpaceShared" => new VmSchedulerSpaceShared()
    }
  }

  /**
   * Creates a Network within the Datacenter
   * @param simulation
   * @param datacenter
   */
  def createNetwork(simulation: CloudSim,datacenter: NetworkDatacenter):Unit = {
  val rootswitch = createSwitchInstance(configs.ROOT_SWITCH,
                                        simulation: CloudSim,
                                        datacenter: NetworkDatacenter,
                                        configs.DATACENTERS.getConfig(configs.ROOT_SWITCH).getDouble("bandwidth"),
                                        configs.DATACENTERS.getConfig(configs.ROOT_SWITCH).getInt("port"),
                                        configs.DATACENTERS.getConfig(configs.ROOT_SWITCH).getDouble("delay"))
  val aggregateswitches = createAggregateSwitches(simulation: CloudSim,datacenter: NetworkDatacenter,rootswitch,configs.DATACENTERS.getConfig(configs.AGGREGATE_SWITCH))
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
    (1 to config.getInt("num")).map{
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
   val tClass = implicitly[ClassTag[T]].runtimeClass
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

  def getSwitchIndex(host: NetworkHost, switchPorts: Int): Int = (host.getId % Integer.MAX_VALUE / switchPorts).toInt
}
