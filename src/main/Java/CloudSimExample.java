import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

public class CloudSimExample {
    private CloudSimExample(){
        //Enables just some level of logging.
//Make sure to import org.cloudsimplus.util.Log;
//Log.setLevel(ch.qos.logback.classic.Level.WARN);

//Creates a CloudSim object to initialize the simulation.
        CloudSim cloudsim = new CloudSim();

        /*Creates a Broker that will act on behalf of a cloud user (customer).*/
        DatacenterBroker broker0 = new DatacenterBrokerSimple(cloudsim);

//Creates a list of Hosts, each host with a specific list of CPU cores (PEs).
        List<Host> hostList = new ArrayList<>(1);
        List<Pe> hostPes = new ArrayList<>(1);
//Uses a PeProvisionerSimple by default to provision PEs for VMs
        hostPes.add(new PeSimple(20000));
        long ram = 100000; //in Megabytes
        long storage = 1000; //in Megabytes
        long bw = 1000; //in Megabits/s

//Uses ResourceProvisionerSimple by default for RAM and BW provisioning
//Uses VmSchedulerSpaceShared by default for VM scheduling
        Host host0 = new HostSimple(ram, bw, storage, hostPes);
        hostList.add(host0);

//Creates a Datacenter with a list of Hosts.
//Uses a VmAllocationPolicySimple by default to allocate VMs
        Datacenter dc0 = new DatacenterSimple(cloudsim, hostList);

//Creates VMs to run applications.
        List<Vm> vmList = new ArrayList<>(1);
//Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
        Vm vm0 = new VmSimple(1000, 1);
        vm0.setRam(1000).setBw(1000).setSize(1000);
        vmList.add(vm0);

//Creates Cloudlets that represent applications to be run inside a VM.
        List<Cloudlet> cloudletList = new ArrayList<>(1);
//UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        Cloudlet cloudlet0 = new CloudletSimple(10000, 1, utilizationModel);
        Cloudlet cloudlet1 = new CloudletSimple(10000, 1, utilizationModel);
        cloudletList.add(cloudlet0);
        cloudletList.add(cloudlet1);

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

/*Starts the simulation and waits all cloudlets to be executed, automatically
stopping when there is no more events to process.*/
        cloudsim.start();

/*Prints results when the simulation is over
(you can use your own code here to print what you want from this cloudlet list).*/
        new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();

    }
    public static void  main(String[] args){new CloudSimExample();}
}
