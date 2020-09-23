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
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletScheduler, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
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

  def createPes(NO_PES:Int,PES_CAPACITY:Int): List[Pe] = {
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


         HostType match {
          case "host1" => val pelist = createPes(configs.HOSTS1.getInt("no_pes"),configs.HOSTS1.getInt("mips"))
                          val host =createHostInstance(configs.HOSTS1.getLong("ram"), configs.HOSTS1.getLong("bandwidth"), configs.HOSTS1.getLong("storage"), pelist,HostInstanceType:String)
                          val scheduler =getVMScheduler(configs.HOSTS1.getString("vmscheduler"))
                          host.setVmScheduler(scheduler)
          case "host2" => val pelist = createPes(configs.HOSTS2.getInt("no_pes"),configs.HOSTS1.getInt("mips"))
                          val host =createHostInstance(configs.HOSTS2.getLong("ram"), configs.HOSTS2.getLong("bandwidth"), configs.HOSTS2.getLong("storage"), pelist,HostInstanceType:String)
                          val scheduler = getVMScheduler(configs.HOSTS2.getString("vmscheduler"))
                          host.setVmScheduler(scheduler)
        }
        //val scheduler = getVMScheduler(configs.HOSTS1.getString("vmscheduler"))

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
  def createVmList(VM_NO:Int, VM_TYPE:String,SchedulerType:String): List[Vm] ={
    (1 to VM_NO).map{
      vm =>
        val vm  =
        VM_TYPE match{
          case "SimpleVm" =>  new VmSimple(configs.VMS.getInt("mips"),configs.VMS.getInt("vm_pes"))
                              //vm.setBw(configs.VMS.getInt(" bandwidth")).setRam(configs.VMS.getInt(" ram")).setSize(configs.VMS.getInt(" size"))

          case "NetworkVm" =>  new NetworkVm(configs.VMS.getInt("mips"),configs.VMS.getInt("vm_pes"))
        }
          vm.setBw(configs.VMS.getInt(" bandwidth")).setRam(configs.VMS.getInt(" ram")).setSize(configs.VMS.getInt(" size"))
          val scheduler = getCloudletScheduler(SchedulerType)
          vm.setCloudletScheduler(scheduler)
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
  def getCloudletScheduler(SchedulerName:String) : CloudletScheduler ={
    SchedulerName match{
      case "TimeShared" => new CloudletSchedulerTimeShared()
      case "SpaceShared" => new CloudletSchedulerSpaceShared()
    }
  }


}
