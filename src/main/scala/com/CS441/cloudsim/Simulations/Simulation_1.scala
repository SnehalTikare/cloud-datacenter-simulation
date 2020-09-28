package com.CS441.cloudsim.Simulations


import com.CS441.cloudsim.Base.{CloudletBase, DataCenterBase}
import com.CS441.cloudsim.utils.ConfigApplications
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.vms.Vm
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudsimplus.listeners.EventInfo
import org.cloudsimplus.util.Log

import collection.JavaConverters._

object Simulation_1 extends LazyLogging{

  /**
   * This Simulations shows how First Fit Allocation Policy and TimeShared Scheduling
   * affects the assignment of VM and running of cloudlets
   * Host1 and Vm1 are used for this simulation
   * Change the parameter in these configs to see the results
   */
  Log.setLevel(ch.qos.logback.classic.Level.INFO);

  logger.info("Running Simulation 1")
  logger.info("This Simulations shows how First Fit Allocation Policy and TimeShared Scheduling affects the assignment of VM and running of cloudlets")

  val config = new ConfigApplications
  val simulation_1 = new CloudSim
  simulation_1.addOnClockTickListener(this.onClockTickListener)

  val datacenterbase =  new DataCenterBase

  logger.info("Creating a Simple DataCenter with First Fit VM Allocation Policy and Host Type 1..")
  val datacenter = datacenterbase.createDataCenter(config.HOSTS_NO,config.SERVICE_PROVIDER.getString("datacenter1"),config.DATACENTER_TYPE.getString("Simple"),config.FIRST_FIT,simulation_1,config.HOST_TYPE.getString("host1"))
  datacenter.setSchedulingInterval(config.SCHEDULING_INTERVAL)

  logger.info("Creating a list of Simple VM with Time Shared Scheduler for Cloudlets")
  val vmList = datacenterbase.createVmList(config.VM_NO,config.VM_TYPE.getString("Simple"), config.CLOUDLETS_SCHEDULER.getString("TimeShared"),config.VM_ARCH.getString("vm1"))

  logger.info("Creating a list of Simple Cloudlets")
  val cloudlet = new CloudletBase
 val cloudletList = cloudlet.createCloudlets(config.NO_OF_CLOUDLETS,config.CLOUDLET_TYPE.getString("cloudlet"),config.CLOUDLETS_CPU_UTIL.getDouble("full"))
  //val cloudletList = cloudlet.createCloudletInVM(vmList,config.CLOUDLETS_CPU_UTIL.getDouble("full"))

  logger.info("Creating a single broker to submit VM and cloudlets")
  val broker = new DatacenterBrokerSimple(simulation_1)
  broker submitVmList(vmList.asJava)
  broker.submitCloudletList(cloudletList.asJava)

  simulation_1.start

  val finishedCloudlets1 = broker.getCloudletFinishedList()


  new CloudletsTableBuilder(finishedCloudlets1).build()

  /**
   * Shows updates every time the simulation clock advances.
   *
   * @param evt information about the event happened (that for this Listener is just the simulation time)
   */
  private def onClockTickListener(evt: EventInfo): Unit = {
    vmList.foreach((vm: Vm) => System.out.printf("\t\tTime %6.1f: Vm %d CPU Usage: %6.2f%% (%2d vCPUs. Running Cloudlets: #%d). RAM usage: %.2f%% (%d MB)%n",
      evt.getTime, vm.getId, vm.getCpuPercentUtilization * 100.0, vm.getNumberOfPes, vm.getCloudletScheduler.getCloudletExecList.size,
      vm.getRam.getPercentUtilization * 100, vm.getRam.getAllocatedResource))
  }


}