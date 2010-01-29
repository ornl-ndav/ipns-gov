/* 
 * File: RunOperatorCaller.java
 *
 * Copyright (C) 2009, Dennis Mikkelson
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */

package gov.anl.ipns.Operator.Processes;

import gov.anl.ipns.Operator.IOperator;

import java.io.*;
import java.util.*;

import gov.anl.ipns.Util.Sys.*;
import Wizard.TOF_SCD.Util;
import IsawGUI.Isaw;

/**
 * This class is an Operator that will call RunOperator to run an
 * Operator as a separate process.
 */
public class RunOperatorCaller implements IOperator 
{
  private static String RETURN_NAME = "RunOp_returned.txt";
  private static int    instance_count = 0;

  private String queue_name;
  private int    mem_size;
  private int    seconds;
  private String op_command;

  /**
   * Construct a RunOperatorCaller to execute the specified operator on
   * the specified SLURM queue using the specified maximum memory size.
   * The operator to execute must only take "simple" parameters such as 
   * integer, float, boolean and String.
   *
   * @param queue_name    The name of the SLURM queue to use.
   * @param mem_size      The amount of memory to allocate for each process
   *                      specified by an integer, giving the number of 
   *                      megabytes.
   * @param seconds       The maximum amount of time each process 
   *                      is allowed to run. CAUTION: This must NOT
   *                      exceed the SLURM TIME LIMIT set in the 
   *                      slurm configuration, or the process will
   *                      be left in a PENDING state!  The number of
   *                      seconds allowed will be forced to be at least 10.
   * @param op_command    String specifying the name of the operator to run, 
   *                      followed by the list of parameters to use for that 
   *                      operator.
   */
  public RunOperatorCaller( String  queue_name, 
                            int     mem_size,
                            int     seconds,
                            String  op_command )
  {
    this.queue_name = queue_name;
    this.mem_size   = mem_size;
    this.seconds    = seconds;
    this.op_command = op_command;
  }


  /**
   *  Execute the operator on the SLURM queue as specified in the 
   *  constructor. 
   *
   *  @return true if the process completed.
   */
  public Object getResult()
  {
    if ( seconds < 10 )                        // allow at least 10 seconds
      seconds = 10;                            // per process.

    String result = Util.ISAW_SCRATCH_DIRECTORY + 
                    instance_count++  +
                    RETURN_NAME;

    boolean use_slurm = true;
    if ( queue_name == null || queue_name.trim().length() == 0 )
      use_slurm = false;
   
    String command = "";
    if ( use_slurm )
    { 
      command = "srun -p " + queue_name +
//              " --exclusive " +
//              " --ntasks=1 " +
                " --time=" + seconds +
                " --mem-per-cpu=" + mem_size +
                " -J ISAW_RunOperatorCaller -o " + result;
    }

    String cp = System.getProperty( "java.class.path" );
    if ( cp == null )
      cp = " ";
    else
      cp = " -cp " + cp + " ";

    String cmd  = " java -mx" + mem_size + "M "+
                  " -XX:+AggressiveHeap "      +
                  " -XX:+DisableExplicitGC "   +
                  " -XX:ParallelGCThreads=4 "  + 
                  cp +
                  " gov.anl.ipns.Operator.Processes.RunOperator " + op_command;

    command = command + cmd;
    
    System.out.println("=====================================================");
    System.out.println("COMMAND = " + command );

    SimpleExec.Exec( command );

    if ( use_slurm &&
         Util.ISAW_SCRATCH_DIRECTORY.startsWith("/SNS/") )// do SNS Logging if
                                                          // using slurm at SNS
    {
      cmd = "/usr/bin/logger -p local5.notice ISAW ISAW_" +
             Isaw.getVersion(false) +
            " " + System.getProperty("user.name");

      Calendar calendar = Calendar.getInstance();
      int      year     = calendar.get( Calendar.YEAR );

      cmd = cmd + " " + year;

      SimpleExec.Exec( cmd );
    }

    return true;
  }


  /**
   *  Simple test for RunOperatorCaller
   */
  public static void main( String args[] )
  {
    RunOperatorCaller caller = new RunOperatorCaller(
                          "mikkcomp", 3000, 300, "SEQUOIA-223_test2 92 3 10"); 

    System.out.println( caller.getResult() );
  }
}
