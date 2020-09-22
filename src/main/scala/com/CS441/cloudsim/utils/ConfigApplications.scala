package com.CS441.cloudsim.utils

import com.typesafe.config.{Config, ConfigFactory}

class ConfigApplications {

  val config: Config = ConfigFactory.load()
  val cloud_service_provider = config.getConfigList("CloudCompanies")

  val NUM_DATACENTERS = cloud_service_provider.get(0).getInt("no_of_datacenter")
  val DATACENTER_TYPE = cloud_service_provider.get(0).getConfig("DataCenterType")
  val DATACENTERS = cloud_service_provider.get(0).getConfig("datacenters")

  val HOSTS_NO = cloud_service_provider.get(0).getInt("no_of_hosts")
  val HOST_INSTANCE_TYPE = cloud_service_provider.get(0).getConfig("HostInstanceType")
  val HOST_TYPE = cloud_service_provider.get(0).getConfig("HostType")
  val HOSTS1 = cloud_service_provider.get(0).getConfig("hosts1")
  val HOSTS2 = cloud_service_provider.get(0).getConfig("hosts2")

  val VM_NO = cloud_service_provider.get(0).getInt("no_of_vms")
  val VMS = cloud_service_provider.get(0).getConfig("Vms")
  val VM_TYPE = cloud_service_provider.get(0).getConfig("Vm_Type")

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
  val NO_OF_CLOUDLETS = CLOUDLETS.getInt("CLOUDLETS_NUM")
  val NO_OF_PES_PER_CLOUDLET = CLOUDLETS.getInt("CLOUDLET_PES")
  val LENGTH_OF_CLOUDLET = CLOUDLETS.getInt("CLOUDLET_LENGTH")



}
