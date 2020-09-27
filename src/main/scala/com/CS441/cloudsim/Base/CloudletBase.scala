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
/*
 val NO_OF_CLOUDLETS = CLOUDLETS.getInt("cloudlets_num")
  val NO_OF_PES_PER_CLOUDLET = CLOUDLETS.getInt("cloudlet_pes")
  val LENGTH_OF_CLOUDLET = CLOUDLETS.getInt("cloudlet_length")
  val FILE_SIZE = CLOUDLETS.getInt("fileSize")
  val OUTPUT_SIZE = CLOUDLETS.getInt("outputSize")
  val PACKET_DATA_LENGTH_IN_BYTES = CLOUDLETS.getInt("packet_data_length_in_bytes")
  val NUMBER_OF_PACKETS_TO_SEND = CLOUDLETS.getInt("number_of_packets_to_send")
  val TASK_RAM = CLOUDLETS.getInt("task_ram")

*/

  /**
   * Creates a list of Cloudlets
   * @return list of cloudlets
   */
  def createCloudlets(Cloudlet_No:Int,Cloudlet_type:String,PERCENT:Double) : List[Cloudlet] ={
    val configs =
      Cloudlet_type match {
        case "cloudlet" => config.CLOUDLETS
        case "infra_cloudlet" => config.infra_cloudlets
      }
    (1 to Cloudlet_No).map{
      cd =>
        new CloudletSimple(configs.getInt("cloudlet_length"),configs.getInt("cloudlet_pes"))
          .setUtilizationModelCpu(CPUUtilization(PERCENT))
          .setFileSize(configs.getInt("fileSize"))
          .setOutputSize(configs.getInt("outputSize"))
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
    val networkCloudletList = (1 to config.saas_cloudlet.getInt("cloudlets_num")).map{
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
    val cloudlet = new NetworkCloudlet(config.saas_cloudlet.getInt("cloudlet_length"),config.saas_cloudlet.getInt("cloudlet_pes"))
    cloudlet.setMemory(config.saas_cloudlet.getInt("task_ram"))
      .setFileSize(config.saas_cloudlet.getInt("fileSize"))
      .setOutputSize(config.saas_cloudlet.getInt("outputSize"))
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
    val task = new CloudletExecutionTask(cloudlet.getTasks.size,config.saas_cloudlet.getInt("cloudlet_length"))
    task.setMemory(config.saas_cloudlet.getInt("task_ram"))
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
    task.setMemory(config.saas_cloudlet.getInt("task_ram"))
    sourceCloudlet.addTask(task)
    (0 until config.saas_cloudlet.getInt("number_of_packets_to_send")).foreach(
      _ => task.addPacket(destinationCloudlet, config.saas_cloudlet.getInt("packet_data_length_in_bytes"))
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
    task.setMemory(config.saas_cloudlet.getInt("task_ram"))
    task.setExpectedPacketsToReceive(config.saas_cloudlet.getInt("number_of_packets_to_send"))
    cloudlet.addTask(task)
  }
}
