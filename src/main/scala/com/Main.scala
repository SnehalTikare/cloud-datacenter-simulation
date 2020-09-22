package com

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import com.CS441.cloudsim.utils.ConfigApplications
import com.typesafe.config.{Config, ConfigFactory}

object Main extends App {
  val configs = new ConfigApplications
  //println(configs.num_datacenters)
  println(configs.PES.getString("mips_capacity"))

  /*val logger = Logger(LoggerFactory.getLogger("name"))
  logger.info("Hello there from info!")
  logger.debug("Hello there from debug!")
  logger.error("Hello there from debug!")*/

}