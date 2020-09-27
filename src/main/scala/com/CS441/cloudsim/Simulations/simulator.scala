package com.CS441.cloudsim.Simulations

import com.typesafe.scalalogging.LazyLogging
import org.cloudsimplus.util.Log


object simulator extends LazyLogging{
  Log.setLevel(ch.qos.logback.classic.Level.INFO);

  def main(args: Array[String]): Unit = {
    println("")
    println("")
    println("|------------------------------|")
    println("| No | Simulation Name         |")
    println("|------------------------------|")
    printf("| 1  | Simulation example 1    |%n" +
           "| 2  | Simulation example 2    |%n" +
           "| 3  | IaaS Simulation         |%n" +
           "| 4  | PaaS Simulation         |%n" +
           "| 5  | SaaS Simulation         |%n")
    println("|------------------------------|")
    println("")
    println("Choose a simulation to run")
    val a = scala.io.StdIn.readInt()
    a match {
      case 1 => Simulation_1
      case 2 => new Simulation_2
      case 3 => new IaasSimulation
      case 4 => new PaasSimulation
      case 5 => new SaasSimulation
    }

  }
}
