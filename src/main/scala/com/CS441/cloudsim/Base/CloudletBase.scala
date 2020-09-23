package com.CS441.cloudsim.Base

import java.util

import com.CS441.cloudsim.utils.ConfigApplications
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.Vm

class CloudletBase {

  val config = new ConfigApplications

  /**
   * Creates a list of Cloudlets
   * @return list of cloudlets
   */
  def createCloudlets(PERCENT:Double) : List[Cloudlet] ={
    (1 to config.NO_OF_CLOUDLETS).map{
      cd =>
        new CloudletSimple(config.LENGTH_OF_CLOUDLET,config.NO_OF_PES_PER_CLOUDLET).setUtilizationModelCpu(CPUUtilization(PERCENT))
    }.toList
  }

  def createCloudletInVM(VM_LIST:List[Vm], PERCENT:Double):util.ArrayList[Cloudlet] ={
    val CLOUDLET_LIST = new util.ArrayList[Cloudlet]
    VM_LIST.map{
      vm =>
       val cloudlet =  new CloudletSimple(config.LENGTH_OF_CLOUDLET,config.NO_OF_PES_PER_CLOUDLET).setUtilizationModelCpu(CPUUtilization(PERCENT))
        cloudlet.setVm(vm)
        CLOUDLET_LIST.add(cloudlet)
    }
    CLOUDLET_LIST

  }
  def CPUUtilization(PERCENT:Double): UtilizationModelDynamic ={
    val capacity = new UtilizationModelDynamic(PERCENT/100)
    capacity
  }

}
