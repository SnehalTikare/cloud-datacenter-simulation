package com.CS441.cloudsim.Base

import com.typesafe.scalalogging.{LazyLogging, Logger}
import com.CS441.cloudsim.utils.ConfigApplications
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit, VmAllocationPolicyRoundRobin, VmAllocationPolicyWorstFit}
import org.cloudbus.cloudsim.core.{CloudSim, Simulation}
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletScheduler, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.vms.network.NetworkVm
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}

import scala.collection.JavaConverters._


class DataCenterBase extends LazyLogging{
  //Load Configurations
  val configs = new ConfigApplications

  /**
   * Creates a list of {@link Pe} with specified MIPS
   * @param PES_NO - No of Pe to be created
   * @param PES_CAPACITY - MIPS Capacity
   * @return
   */
  private def createPesInstance(PES_NO: Int, PES_CAPACITY: Double): List[PeSimple] = {
    (1 to PES_NO)
      .map { pe =>
        new PeSimple(PES_CAPACITY)
      }
      .toList
  }

   def createPes(NO_PES:Int,PES_CAPACITY:Double): List[Pe] = {
    createPesInstance(NO_PES, PES_CAPACITY)
  }


  /**
   * Create a list of Hosts {@Link SimpleHost} and {@Link NetworkHost}
   * @param HOSTS_NO No of Hosts to be created
   * @param HostInstanceType Type of Host Network or Simple
   * @param HostType Different varieties of  Host with different parameters
   * @return
   */

  def createHosts(HOSTS_NO: Int,HostInstanceType:String, HostType:String ): List[Host] = {
    logger.info("Number of Hosts created - {}",HOSTS_NO )
    (1 to HOSTS_NO).map {
      host =>
        val configHost = HostType match {
          case "host1" =>  configs.HOSTS1
          case "host2" =>  configs.HOSTS2
          case "host3" => configs.HOSTS3
        }
        val pelist = createPes(configHost.getInt("no_pes"),configHost.getDouble("mips"))
        val host =createHostInstance(configHost.getLong("ram"), configHost.getLong("bandwidth"), configHost.getLong("storage"), pelist,HostInstanceType:String)
        val scheduler =getVMScheduler(configHost.getString("vmscheduler"))
        host.enableStateHistory()
        host.setVmScheduler(scheduler)
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
  def createVmList(VM_NO:Int, VM_TYPE:String,SchedulerType:String,VM_ARCH:String): List[Vm] ={
    logger.info("Creating {} VM of type {} which uses {} for scheduling cloudlets",VM_NO,VM_TYPE,SchedulerType)
    val configVMS = VM_ARCH match {
      case "vm1" => configs.VMS1
      case "vm2" => configs.VMS2
      case "vm3" => configs.VMS3
    }
    (1 to VM_NO).map{
      vm =>
        val vm  =
        VM_TYPE match{
          case "SimpleVm" =>  new VmSimple(configVMS.getInt("mips"),configVMS.getInt("vm_pes"))
          case "NetworkVm" =>  new NetworkVm(configVMS.getInt("mips"),configVMS.getInt("vm_pes"))
        }
          vm.setBw(configVMS.getInt(" bandwidth")).setRam(configVMS.getInt(" ram")).setSize(configVMS.getInt(" size"))
          val scheduler = getCloudletScheduler(SchedulerType)
          vm.setCloudletScheduler(scheduler)
    }.toList

  }

  def createDataCenterList(ServiceProvider:String,DataCenterType:String,NO_OF_DATACENTER:Int,Vm_Allocation_Policy:String, simulation: CloudSim,HOSTTYPE:String):List[Datacenter] = {
    (1 to NO_OF_DATACENTER).map{
      dc => createDataCenter(ServiceProvider:String,DataCenterType:String,Vm_Allocation_Policy:String, simulation: CloudSim,HOSTTYPE:String)
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

  def createDataCenter(ServiceProvider:String,DataCenterType:String,Vm_Allocation_Policy:String, simulation: CloudSim,HOSTTYPE:String) : Datacenter ={
   val DataCenter = DataCenterType match{
      case "SimpleDataCenter" => createSimpleDataCenter(Vm_Allocation_Policy:String, simulation: CloudSim,HOSTTYPE:String)
      case "NetworkDataCenter" => createNetworkDataCenter(Vm_Allocation_Policy:String, simulation: CloudSim,HOSTTYPE:String)
    }
    val configDATACENTERS =
      ServiceProvider match {
        case "datacenter1" => configs.DATACENTERS1
        case "datacenter2" => configs.DATACENTERS2
        case "datacenter3" => configs.DATACENTERS3
      }
    DataCenter.getCharacteristics.setArchitecture(configDATACENTERS.getString("os"))
    DataCenter.getCharacteristics.setCostPerBw(configDATACENTERS.getDouble("costPerBandwidth"))
    DataCenter.getCharacteristics.setCostPerMem(configDATACENTERS.getDouble("costPerMemory"))
    DataCenter.getCharacteristics.setCostPerSecond(configDATACENTERS.getDouble("costPerSecond"))
    DataCenter.getCharacteristics.setCostPerStorage(configDATACENTERS.getDouble("costPerStorage"))
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
  def getCloudletScheduler(SchedulerName:String) : CloudletScheduler ={
    SchedulerName match{
      case "TimeShared" => new CloudletSchedulerTimeShared()
      case "SpaceShared" => new CloudletSchedulerSpaceShared()
    }
  }



}
