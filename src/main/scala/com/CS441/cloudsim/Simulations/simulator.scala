package com.CS441.cloudsim.Simulations

import com.typesafe.scalalogging.LazyLogging
import org.cloudsimplus.util.Log


object simulator extends LazyLogging{
  Log.setLevel(ch.qos.logback.classic.Level.INFO);

  def main(args: Array[String]): Unit = {
    println("Choose number from 1 to 4")
    val a = scala.io.StdIn.readInt()
    a match {
      case 1 => Simulation_1
      case 2 => new Simulation_2
      case 3 => new NetworkSimulation_1
    }

  }
}
