package com.CS441.cloudsim.utils

import com.typesafe.config.{Config, ConfigFactory}

class ConfigApplications {

  val config: Config = ConfigFactory.load()
  val cloud_service_provider = config.getConfigList("CloudCompanies")

  val NUM_DATACENTERS = cloud_service_provider.get(0).getInt("no_of_datacenter")
  val DATACENTER_TYPE = cloud_service_provider.get(0).getConfig("DataCenterType")

  val SERVICE_PROVIDER = cloud_service_provider.get(0).getConfig("ServiceProvider")

  val DATACENTERS1 = cloud_service_provider.get(0).getConfig("datacenters1")
  val DATACENTERS2 = cloud_service_provider.get(0).getConfig("datacenters2")
  val DATACENTERS3 = cloud_service_provider.get(0).getConfig("datacenters3")

  val HOSTS_NO = cloud_service_provider.get(0).getInt("no_of_hosts")
  val HOST_INSTANCE_TYPE = cloud_service_provider.get(0).getConfig("HostInstanceType")
  val HOST_TYPE = cloud_service_provider.get(0).getConfig("HostType")
  val HOSTS1 = cloud_service_provider.get(0).getConfig("hosts1")
  val HOSTS2 = cloud_service_provider.get(0).getConfig("hosts2")
  val HOSTS3 = cloud_service_provider.get(0).getConfig("hosts3")

  val VM_NO = cloud_service_provider.get(0).getInt("no_of_vms")
  val VM_TYPE = cloud_service_provider.get(0).getConfig("Vm_Type")
  val VM_ARCH = cloud_service_provider.get(0).getConfig("vm_arch")
  val VMS1 = cloud_service_provider.get(0).getConfig("vm1")
  val VMS2 = cloud_service_provider.get(0).getConfig("vm2")
  val VMS3 = cloud_service_provider.get(0).getConfig("vm3")

  val PES = cloud_service_provider.get(0).getConfig("pes")
  val PES_CAPACITY = PES.getDouble("mips_capacity")

  val VMAllocationPolicy = cloud_service_provider.get(0).getConfig("vm_allocation_policy")
  val BEST_FIT = VMAllocationPolicy.getString("Best_fit")
  val FIRST_FIT = VMAllocationPolicy.getString("First_fit")
  val WORST_FIT = VMAllocationPolicy.getString("Worst_fit")
  val ROUNDROBIN = VMAllocationPolicy.getString("RoundRobin")

  val SWITCH_TYPE = cloud_service_provider.get(0).getConfig("SwitchType")
  val ROOT_SWITCH = SWITCH_TYPE.getString("RootSwitch")
  val AGGREGATE_SWITCH = SWITCH_TYPE.getString("AggregateSwitch")
  val EDGE_SWITCH = SWITCH_TYPE.getString("EdgeSwitch")

  val CLOUDLETS = cloud_service_provider.get(0).getConfig("Cloudlets")
  val NO_OF_CLOUDLETS = CLOUDLETS.getInt("cloudlets_num")
  val NO_OF_PES_PER_CLOUDLET = CLOUDLETS.getInt("cloudlet_pes")
  val LENGTH_OF_CLOUDLET = CLOUDLETS.getInt("cloudlet_length")
  val FILE_SIZE = CLOUDLETS.getInt("fileSize")
  val OUTPUT_SIZE = CLOUDLETS.getInt("outputSize")
  val PACKET_DATA_LENGTH_IN_BYTES = CLOUDLETS.getInt("packet_data_length_in_bytes")
  val NUMBER_OF_PACKETS_TO_SEND = CLOUDLETS.getInt("number_of_packets_to_send")
  val TASK_RAM = CLOUDLETS.getInt("task_ram")

  val CLOUDLETS_SCHEDULER = cloud_service_provider.get(0).getConfig("Cloudlet_Scheduler")
  val CLOUDLETS_CPU_UTIL = cloud_service_provider.get(0).getConfig("UtilizationModel")

  val SCHEDULING_INTERVAL = cloud_service_provider.get(0).getInt("scheduling_interval")

  val NETWORK_TOPOLOGY_FILE = cloud_service_provider.get(0).getString("network_topology_file")


}
