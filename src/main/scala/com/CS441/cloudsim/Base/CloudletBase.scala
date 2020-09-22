package com.CS441.cloudsim.Base

import com.CS441.cloudsim.utils.ConfigApplications
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic

class CloudletBase {

  val config = new ConfigApplications

  /**
   * Creates a list of Cloudlets
   * @return list of cloudlets
   */
  def createCloudlets() : List[Cloudlet] ={
    (1 to config.NO_OF_CLOUDLETS).map{
      cd =>
        new CloudletSimple(config.LENGTH_OF_CLOUDLET,config.NO_OF_PES_PER_CLOUDLET)
    }.toList
  }

}
