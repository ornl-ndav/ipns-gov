/* 
 * File: TestParallelExecutor.java
 *
 * Copyright (C) 2008, Dennis Mikkelson
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
 *  $Rev$
 */

package gov.anl.ipns.Operator.Threads;

import gov.anl.ipns.Operator.IOperator;
import java.util.Vector;

/**
 *  This class does a simple test of the ParallelExecutor class.  jconsole
 *  can be used to monitor the number of threads.
 */
public class TestParallelExecutor
{

  public static void main( String[] args )
  { 
    System.out.println("Starting program...sleeping for 20 seconds");
    try
    {
      Thread.sleep( 20000 );   // wait for 20 seconds to allow time to 
    }                          // look at this with jconsole
    catch ( InterruptedException e )
    {
      System.out.println("sleep was interrupted"); 
    }
    
    Vector<IOperator> ops = new Vector<IOperator>();
    Vector results = new Vector();
     
    for ( int i = 0; i < 100; i++ )
    {
      IOperator op = new TestIOperator("IOperator " + i, 200000000 );
      ops.add( op );
    }

    int n_threads = 3;
    System.out.println("Starting " + n_threads + " threads");
    long start_time = System.nanoTime();
     
    try
    {
      ParallelExecutor exec = new ParallelExecutor( ops, n_threads, 20000 );
      results = exec.runOperators();
    }
    catch ( ExecFailException fail_exception )
    {
      results = fail_exception.getPartialResults();
      System.out.println("ExecFailException: " + 
                          fail_exception.getFailureStatus() );
    }
    long run_time = System.nanoTime() - start_time;
     
    System.out.println("RESULT OF PARALLEL EXECUTION:");
    for ( int i = 0; i < results.size(); i++ )
      System.out.println( results.elementAt(i) );
     
    System.out.println("Total run time(ms) = " + run_time/1000000 );
    System.out.println("Waiting 20 seconds for threads to die");
    try
    {
      Thread.sleep( 20000 );   // wait for 20 seconds to allow time to 
    }                          // check if threads are dying
    catch ( InterruptedException e )
    {
      System.out.println("sleep was interrupted"); 
    }
    System.out.println("Program complete");
  }

}
