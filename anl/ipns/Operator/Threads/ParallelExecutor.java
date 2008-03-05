/* 
 * File: ParallelExecutor.java
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

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import gov.anl.ipns.Operator.IOperator;

/**
 * A ParallelExecutor takes a list of IOperator objects that can be run 
 * concurrently, and runs them in separate threads until they have all 
 * completed.  The maximum number of threads that should be started at 
 * any one time is specified, as is the maximum amount of time that can 
 * be used, before stopping and destroying all threads and returning.
 */
public class ParallelExecutor 
{
  public  static final int  WAIT_MILLIS = 20;   // time to wait for join 
  private Vector<IOperator> operator_list;
  private int               max_threads;
  private int               max_milliseconds;

  /**
   *  Construct a new ParallelExecutor to execute the specified list of
   *  operators in separate Threads.
   *
   *  @param operators  The list of OperatorThreads that are to be run
   *                    concurrently.
   *
   *  @throws IllegalArgumentException if the Vector of operators is null
   *                                   or has a null IOperator, or if the
   *                                   max_threads or max_milliseconds is
   *                                   not positive.
   */  
  public ParallelExecutor( Vector<IOperator> operators, 
                           int               max_threads,
                           int               max_milliseconds )
                           throws IllegalArgumentException
  {
    if ( operators == null || operators.size() <= 0 )
      throw new IllegalArgumentException("operators list null or zero length");

    operator_list = new Stack<IOperator>();
    for ( int i = 0; i < operators.size(); i++ )
      if ( operators.elementAt(i) == null )
        throw new IllegalArgumentException("operator " + i + " null");
      else
        operator_list.add( operators.elementAt(i) );

    if ( max_threads <= 0 )
      throw new IllegalArgumentException("max_threads <= 0");
    this.max_threads = max_threads;

    if ( max_milliseconds <= 0 )
      throw new IllegalArgumentException("max_milliseconds <= 0");
    this.max_milliseconds = max_milliseconds;
  }
  

  /**
   *  Get the result of running all of operator threads.  This method will
   *  only return after all operator threads have been completed, or the
   *  maximum time has elapsed.
   *
   *  @return a Vector containing the result of running OperatorThread k,
   *          stored as the kth entry in the Vector.
   */
  public synchronized Vector runOperators()
  {
    int                    next_op_index   = 0;
    Hashtable              index_table     = new Hashtable();
    Vector<OperatorThread> running_threads = new Vector<OperatorThread>();

    Object[]               results      = new Object[ operator_list.size() ];
    long                   start_time   = System.currentTimeMillis();
    long                   elapsed_time = 0;

                                                    // Mark all as incomplete
                                                    // initially.
    for ( int i = 0; i < results.length; i++ )
      results[i] = FailState.NOT_DONE;
    
                                                     // Start as many threads
                                                     // as we can to run them
    while ( running_threads.size() < max_threads && 
            next_op_index          < operator_list.size() ) 
    {
       int index = next_op_index;
       next_op_index++;

       IOperator op = operator_list.elementAt( index );
       OperatorThread thread = new OperatorThread( op );
       running_threads.add( thread );
       index_table.put( thread, new Integer( index ) );
       thread.start();
    }
                                               // As long as some are running
    try                                        // the total time is not too big
    {
      while ( running_threads.size() > 0 && elapsed_time < max_milliseconds )
      {
                                                    // check each thread in
                                                    // turn to see if its done 
        for ( int i = running_threads.size()-1; i >= 0; i-- )
        {
           OperatorThread thread = running_threads.elementAt(i);
           thread.join( WAIT_MILLIS ); 
           if ( thread.getState() == Thread.State.TERMINATED )
           {                                        // if thread is done
                                                    // and no more to run, just
             if ( next_op_index >= operator_list.size() )  
               running_threads.remove(i);           // remove it from list of
                                                    // running threads, else
             else                                   // start a new thread in
             {                                      // its place
               int index = next_op_index;
               next_op_index++;

               IOperator op = operator_list.elementAt( index );
               OperatorThread new_thread = new OperatorThread( op );
               running_threads.set( i, new_thread );
               index_table.put( new_thread, new Integer( index ) );
               new_thread.start(); 
             }
                                                    // record the result for  
                                                    // operator that finshed
             Integer index = (Integer)(index_table.get( thread ));
             results[index.intValue()] = thread.getResult();
           } 
        }
        elapsed_time = System.currentTimeMillis() - start_time;
      }
    }
    catch ( InterruptedException e )
    {
      stop_threads( running_threads );
      Vector partial = pack_results( results );       
      throw new ExecFailException(FailState.INTERRUPTED, partial); 
    }

    if ( elapsed_time >= max_milliseconds )
    {
      stop_threads( running_threads );
      Vector partial = pack_results( results );
      throw new ExecFailException(FailState.NOT_DONE, partial); 
    }
    
    return pack_results( results );
  }  
  
  
  /**
   * 
   */
   private void stop_threads( Vector<OperatorThread> running_threads )
   {
     for ( int i = running_threads.size()-1; i >= 0; i-- )
     {
        OperatorThread thread = running_threads.elementAt(i);
        thread.interrupt();
     }
   }
  
  
  /**
   *  Pack the specified array of Objects into a Vector in the 
   *  order that they appear in the array.
   *  
   *  @param results  Vector of Objects to put into a Vector.
   *  
   *  @return a Vector containing the Objects from the specified array.
   */
  private Vector pack_results( Object[] results )
  {
    Vector result = new Vector( results.length );
    for ( int i = 0; i < results.length; i++ )
      result.add( results[i] );

    return result;  
  }
  
}
