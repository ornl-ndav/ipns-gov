/* 
 * File: ProcessMethod.java
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

import java.util.Vector;

import Wizard.TOF_SCD.Util;

import gov.anl.ipns.Operator.Threads.*;
import gov.anl.ipns.Util.Sys.*;

/**
 *  This class has a method to run a list of operators in parallel either 
 *  using slurm, or local processes, if the slum queue name is null or
 *  of zero length.
 */
public class ProcessMethod 
{

  /**
   * Execute the specified list of operators on the specified SLURM queue
   * or using local processes, using at most the specified maximum number 
   * of cores.  The operators to execute must only take "simple" parameters 
   * such as integer, float, boolean and String(NO DataSets!).  If SLURM
   * is used, any output of the individual operators and 
   * scripts will be written to a temporary file in 
   * <user_home>/ISAW/tmp/*_returned.txt.  These temporary files are 
   * intended only for debugging purposes and will be erased each time 
   * the srunOps() operator is.
   * All of the parameters of the operators must be specified 
   * in the correct order and be of the correct data types.
   * The individual operators must not require an X-Window
   * display, but must communicate results via files.  The
   * file names are controlled by the user and must be 
   * coordinated between the low-level operators & scripts
   * and the script using this srunOps( ) operator. 
   *
   * @param queue_name    The name of the SLURM queue to use.  If this
   *                      is null or a zero length string, local processes
   *                      will be used instead of SLURM.  The number of
   *                      local processes is limited to the number of cores.
   * @param max_processes The maximum number of processes to launch
   *                      simultaneously.  This should be less than or
   *                      equal to the number of cores available in the
   *                      SLURM queue.
   * @param max_time      The maximum allowed total run time as an
   *                      integer number of seconds.
   * @param mem_size      The amount of memory to allocate for each process
   *                      specified by an integer, giving the number of 
   *                      megabytes.
   * @param op_commands   Vector of Strings, each of which specifies the
   *                      name of the operator to run, followed by the list
   *                      of parameters to use for that operator.
   *
   * @return true if the processes seemed to complete normally.
   */
  public static boolean srunOps( String         queue_name, 
                                 int            max_processes,
                                 int            max_time,
                                 int            mem_size,
                                 Vector<String> op_commands )
  {
                                        // Make sure ISAW/tmp directory exists
                                        // and clear out any old return files 
    Util.CheckTmpDirectory();
    Util.ClearFiles( "", Util.SLURM_RETURN_SUFFIX );

    if ( op_commands.size() == 0 )             // Nothing to do
      throw new IllegalArgumentException("NO Operators to run in srunOps");

    if ( max_processes > op_commands.size() )  // Don't ask for more processes 
      max_processes = op_commands.size();      // than the number of Ops to run

                                               // if not using slurm, don't
                                               // take too many processes 
    if ( queue_name == null || queue_name.trim().length() == 0 )
    {
      int n_cores = Runtime.getRuntime().availableProcessors();
      if ( max_processes > n_cores )
        max_processes = n_cores;
    }
 
    if ( max_processes <= 0 )
      max_processes = 1;

    int processes_per_core  = op_commands.size() / max_processes;
    int seconds_per_process = max_time / processes_per_core;

    Vector ops = new Vector();
    for ( int i = 0; i < op_commands.size(); i++ )
    {
      String op_command = op_commands.elementAt(i);
 
      RunOperatorCaller caller =
               new RunOperatorCaller( queue_name, 
                                      mem_size, 
                                      seconds_per_process, 
                                      op_command );
      ops.add( caller );
    }

    max_time = max_time * 1000;   // convert from seconds to milli-seconds

    System.out.println("Made ParallelExecutor with " + max_processes );
    ParallelExecutor executor =
                          new ParallelExecutor( ops, max_processes, max_time );
    
    Vector results = null;
    try                                     // try to do everything during the
    {                                       // alloted time and return partial
      results = executor.runOperators();    // results if something fails
    }                                       // or we run out of time.
    catch ( ExecFailException fail_ex )
    {
      FailState state = fail_ex.getFailureStatus();

      String reason;
      if ( state == FailState.NOT_DONE )
        reason = "maximum time, " + max_time/1000 + " seconds, elapsed ";
      else
        reason = "a process was interrupted.";

      SharedMessages.addmsg("WARNING: Operators did not finish: " + reason);
      SharedMessages.addmsg("The result returned is incomplete.");
      results = fail_ex.getPartialResults();

      if ( results != null )
      for ( int i = 0; i < results.size(); i++ )
      {
         System.out.println( "RESULT: " + results.elementAt(i) + 
                             " For OP: " + op_commands.elementAt(i) );
      }

      return new Boolean( false );
    }

    return new Boolean( true );
  }


  /**
   *  Basic test program for method srunOps.
   */
  public static void main( String args[] )
  {
     String queue_name = "mikkcomp";
     int    max_processes = 20;     // spawn at most 10 processes at once
     int    max_time = 600;         // allow at most 10 minutes
     int    mem_size = 3800;        // 3.8GB max per core (4 cores/16 GB)

     Vector<String> commands = new Vector<String>();

     for (int i = 0; i < 80; i += 4 )
       commands.add( "SEQUOIA-223_test2 92 " + i + " " + (i+3) );

     boolean result = srunOps( queue_name,  
                               max_processes, 
                               max_time, 
                               mem_size, 
                               commands );

     System.out.println("Result = " + result );
  }

}
