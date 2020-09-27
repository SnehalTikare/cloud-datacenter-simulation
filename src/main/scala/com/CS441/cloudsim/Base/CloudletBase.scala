package com.CS441.cloudsim.Base

import java.util

import com.CS441.cloudsim.utils.ConfigApplications
import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, CloudletReceiveTask, CloudletSendTask, CloudletTask, NetworkCloudlet}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModelDynamic, UtilizationModelFull}
import org.cloudbus.cloudsim.vms.Vm
import org.cloudbus.cloudsim.vms.network.NetworkVm

class CloudletBase {

  val config = new ConfigApplications

  /**
   * Creates a list of Cloudlets
   * @return list of cloudlets
   */
  def createCloudlets(PERCENT:Double) : List[Cloudlet] ={
    (1 to config.NO_OF_CLOUDLETS).map{
      cd =>
        new CloudletSimple(config.LENGTH_OF_CLOUDLET,config.NO_OF_PES_PER_CLOUDLET)
          .setUtilizationModelCpu(CPUUtilization(PERCENT))
          .setFileSize(config.FILE_SIZE)
          .setOutputSize(config.OUTPUT_SIZE)
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

   def createListNetworkCloudlets(VM_LIST:List[Vm]):List[NetworkCloudlet] = {
    val networkCloudletList = (1 to config.NO_OF_CLOUDLETS).map{
      x =>
        createNetworkCloudlet(VM_LIST(x).asInstanceOf[NetworkVm])
    }.toList
    //NetworkCloudlet 0 Tasks
    addExecutionTask(networkCloudletList(0))
    addSendTask(networkCloudletList(0), networkCloudletList(1))
    //NetworkCloudlet 1 Tasks
    addReceiveTask(networkCloudletList(1), networkCloudletList(0))
    addExecutionTask(networkCloudletList(1))
    networkCloudletList
  }

  /**
   * Creates a {@link NetworkCloudlet}.
   *
   * @param vm the VM that will run the created { @link  NetworkCloudlet)
   * @return
   */
   def createNetworkCloudlet(vm: NetworkVm):NetworkCloudlet = {
    val cloudlet = new NetworkCloudlet(config.LENGTH_OF_CLOUDLET,config.NO_OF_PES_PER_CLOUDLET)
    cloudlet.setMemory(config.TASK_RAM)
      .setFileSize(config.FILE_SIZE)
      .setOutputSize(config.OUTPUT_SIZE)
      .setUtilizationModel(new UtilizationModelFull).setVm(vm)
      .setBroker(vm.getBroker)
    cloudlet
  }

  /**
   * Adds an execution task to the list of tasks of the given
   * {@link NetworkCloudlet}.
   *
   * @param cloudlet the { @link NetworkCloudlet} the task will belong to
   */
   def addExecutionTask(cloudlet: NetworkCloudlet): Unit = {
    val task = new CloudletExecutionTask(cloudlet.getTasks.size,config.LENGTH_OF_CLOUDLET)
    task.setMemory(config.TASK_RAM)
    cloudlet.addTask(task)
  }

  /**
   * Adds a send task to the list of tasks of the given {@link NetworkCloudlet}.
   *
   * @param sourceCloudlet the { @link NetworkCloudlet} from which packets will be sent
   * @param destinationCloudlet the destination { @link NetworkCloudlet} to send packets to
   */
   def addSendTask(sourceCloudlet: NetworkCloudlet, destinationCloudlet: NetworkCloudlet): Unit = {
    val task = new CloudletSendTask(sourceCloudlet.getTasks.size)
    task.setMemory(config.TASK_RAM)
    sourceCloudlet.addTask(task)
    (0 until config.NUMBER_OF_PACKETS_TO_SEND).foreach(
      _ => task.addPacket(destinationCloudlet, config.PACKET_DATA_LENGTH_IN_BYTES)
    )
  }

  /**
   * Adds a receive task to the list of tasks of the given
   * {@link NetworkCloudlet}.
   *
   * @param cloudlet the { @link NetworkCloudlet} the task will belong to
   * @param sourceCloudlet the { @link NetworkCloudlet} expected to receive packets from
   */
  def addReceiveTask(cloudlet: NetworkCloudlet, sourceCloudlet: NetworkCloudlet): Unit = {
    val task = new CloudletReceiveTask(cloudlet.getTasks.size, sourceCloudlet.getVm)
    task.setMemory(config.TASK_RAM)
    task.setExpectedPacketsToReceive(config.NUMBER_OF_PACKETS_TO_SEND)
    cloudlet.addTask(task)
  }
}
