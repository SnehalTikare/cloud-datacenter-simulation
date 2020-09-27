package com.CS441.cloudsim.Base

import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.vms.VmSimple

import scala.collection.JavaConverters._

class PaaS extends  DataCenterBase {
  def createVminHosts(HOST_NO: Int, HostInstanceType: String,VM_ARCH:String): List[Host] = {
    val configVMS = VM_ARCH match {
      case "vm1" => configs.VMS1
      case "vm2" => configs.VMS2
      case "vm3" => configs.VMS3
    }
    (1 to HOST_NO).map {
      host =>
        val vm = new VmSimple(configVMS.getInt("mips"), configVMS.getInt("vm_pes"))
        val pelist = createPes(configs.HOSTS1.getInt("no_pes"), configs.HOSTS1.getDouble("mips"))
        val host = createHostInstance(configs.HOSTS1.getLong("ram"), configs.HOSTS1.getLong("bandwidth"), configs.HOSTS1.getLong("storage"), pelist, HostInstanceType: String)
        val scheduler = getVMScheduler(configs.HOSTS1.getString("vmscheduler"))
        host.createVm(vm)
        host.setVmScheduler(scheduler)
    }.toList
  }

  def createDataCenterWithVM(simulation: CloudSim, Vm_Allocation_Policy: String,VM_ARCH:String): Datacenter = {
    val hostlist = createVminHosts(configs.HOSTS_NO, configs.HOST_INSTANCE_TYPE.getString("Simple"),VM_ARCH:String).asJava
    val vmAllocationPolicy = getVMAllocatioPolicy(Vm_Allocation_Policy)
    val SimpleDataCenter = new DatacenterSimple(simulation, hostlist, vmAllocationPolicy)
    SimpleDataCenter
  }
  def createDataCenterListWithVm(NO_OF_DATACENTER:Int,Vm_Allocation_Policy:String, simulation: CloudSim,VM_ARCH:String):List[Datacenter] = {
    (1 to NO_OF_DATACENTER).map{
      dc => createDataCenterWithVM(simulation: CloudSim,Vm_Allocation_Policy:String,VM_ARCH:String)
    }.toList
  }
}