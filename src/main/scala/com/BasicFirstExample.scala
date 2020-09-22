//package com
//
//import java.util
//
//import org.cloudbus.cloudsim.brokers.DatacenterBroker
//import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
//import org.cloudbus.cloudsim.cloudlets.Cloudlet
//import org.cloudbus.cloudsim.cloudlets.CloudletSimple
//import org.cloudbus.cloudsim.core.CloudSim
//import org.cloudbus.cloudsim.datacenters.Datacenter
//import org.cloudbus.cloudsim.datacenters.DatacenterSimple
//import org.cloudbus.cloudsim.hosts.Host
//import org.cloudbus.cloudsim.hosts.HostSimple
//import org.cloudbus.cloudsim.resources.Pe
//import org.cloudbus.cloudsim.resources.PeSimple
//import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
//import org.cloudbus.cloudsim.vms.Vm
//import org.cloudbus.cloudsim.vms.VmSimple
//import org.cloudsimplus.builders.tables.CloudletsTableBuilder
//import java.util.ArrayList
//import java.util.List
//import com.CS441.cloudsim.utils.ConfigApplications
//import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyBestFit, VmAllocationPolicySimple}
//import org.cloudsimplus.util.Log
//
//
//object BasicFirstExample {
//  val configs = new ConfigApplications
//    val HOSTS = 1
//    val HOST_PES = 2 //CPU units
//    val VMS = 1
//    val VM_PES = 2 //VM CPU Units
//    val CLOUDLETS = 0 //Task or job submitted to cloud
//    val CLOUDLET_PES = 2 //Attributes of jobs
//    val CLOUDLET_LENGTH = 100
//
//
//  Log.setLevel(ch.qos.logback.classic.Level.INFO);
//
//  var simulation = new CloudSim
//  var datacenterList = new util.ArrayList[Datacenter]
//  for (i <- 0 until 0) {
//    datacenterList.add(createDatacenter)
//  }
// // var datacenter0 = createDatacenter()
//  //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
//  var broker0 = new DatacenterBrokerSimple(simulation)
//  var vmList = createVms()
//  var cloudletList = createCloudlets()
//  broker0 submitVmList vmList
//  broker0.submitCloudletList(cloudletList)
//  simulation.start
//  var finishedCloudlets = broker0.getCloudletFinishedList()
//  new CloudletsTableBuilder(finishedCloudlets).build()
//  /**
//   * Creates a Datacenter and its Hosts.
//   */
//    def createDatacenter(): Datacenter = {
//      var hostlist = new ArrayList[Host]()
//      def loop(n : Int):Datacenter={
//        if(n == HOSTS)
//          new DatacenterSimple (simulation, hostlist, new VmAllocationPolicyBestFit)
//        else{
//          hostlist.add(createHost)
//          loop(n+1)
//        }
//      }
//      loop(0)
//    //Uses a VmAllocationPolicySimple by default to allocate VMs
//    }
//
//  def createHost () : Host = {
//    var peList = new ArrayList[Pe]()
//    def loop(n : Int):Host={
//      if(n == HOST_PES)
//        {
//          val ram = 2048; //in Megabytes
//          val bw = 10000; //in Megabits/s
//          val storage = 1000000; //in Megabytes
//          new HostSimple(ram, bw, storage, peList)
//        }
//      else {
//        peList.add(new PeSimple(1000))
//        loop(n+1)
//      }
//    }
//    loop(0)
//  }
//
//  /**
//   * Creates a list of VMs.
//   */
//  def createVms(): ArrayList[Vm] ={
//    var list = new ArrayList[Vm]()
//    def loop(n : Int ):ArrayList[Vm]={
//      if( n == VMS){
//        list
//      }
//      else{
//        var vm = new VmSimple (1000, VM_PES);
//        vm.setRam (512).setBw (1000).setSize (10000);
//        list.add (vm);
//        loop(n+1)
//      }
//
//    }
//    loop(0)
//  }
//
//  /**
//   * Creates a list of Cloudlets.
//   */
//
//  def createCloudlets() :  ArrayList[Cloudlet]={
//    var list = new ArrayList[Cloudlet]()
//    var utilizationModel = new UtilizationModelDynamic(0.5)
//    def loop(n:Int) : ArrayList[Cloudlet]={
//      if(n == CLOUDLETS){
//        list
//      }
//      else{
//        var cloudlet = new CloudletSimple (CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
//        cloudlet.setSizes (1024);
//        list.add (cloudlet);
//        loop(n+1)
//      }
//    }
//    loop(0)
//  }
//
//  def main(args: Array[String]): Unit = {
//    BasicFirstExample
//  }
//}
