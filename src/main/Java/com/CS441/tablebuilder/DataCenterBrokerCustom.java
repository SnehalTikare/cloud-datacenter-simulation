package com.CS441.tablebuilder;

import com.CS441.cloudsim.Base.CloudletBase;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerAbstract;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

/*
Custom Broker class to assign VM to specific data center
 */
public class DataCenterBrokerCustom extends DatacenterBrokerAbstract {
    /**
     * Index of the last VM selected from the {@link #getVmExecList()}
     * to run some Cloudlet.
     */
    private int lastSelectedVmIndex;

    /**
     * Index of the Datacenter selected to place some VM.
     */
    private int SelectedDcIndex;

    public DataCenterBrokerCustom(final CloudSim simulation, int datacenter_no){
        this(simulation,datacenter_no, "");
    }
    public DataCenterBrokerCustom(final CloudSim simulation, int datacenter_no,final String name){
        super(simulation,name);
        this.lastSelectedVmIndex = -1;
        this.SelectedDcIndex = datacenter_no;
    }
    @Override
    protected Datacenter defaultDatacenterMapper(final Datacenter lastDatacenter, final Vm vm) {
        if(getDatacenterList().isEmpty()) {
            throw new IllegalStateException("You don't have any Datacenter created.");
        }
        if(SelectedDcIndex<0)
            return Datacenter.NULL;
        if (lastDatacenter != Datacenter.NULL) {
            return getDatacenterList().get(SelectedDcIndex);
        }

        /*If all Datacenter were tried already, return Datacenter.NULL to indicate
         * there isn't a suitable Datacenter to place waiting VMs.*/
        if(SelectedDcIndex == getDatacenterList().size()){
            return Datacenter.NULL;
        }

        return getDatacenterList().get(SelectedDcIndex);
    }
    @Override
    protected Vm defaultVmMapper(final Cloudlet cloudlet) {
        if (cloudlet.isBoundToVm()) {
            return cloudlet.getVm();
        }

        if (getVmExecList().isEmpty()) {
            return Vm.NULL;
        }

        /*If the cloudlet isn't bound to a specific VM or the bound VM was not created,
        cyclically selects the next VM on the list of created VMs.*/
        lastSelectedVmIndex = ++lastSelectedVmIndex % getVmExecList().size();
        return getVmFromCreatedList(lastSelectedVmIndex);
    }
    public List<Datacenter> getDataCenterList(){
        return getDatacenterList();
    }
}
