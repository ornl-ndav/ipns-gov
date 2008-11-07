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
import gov.anl.ipns.Operator.IDistributedOperator;

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
  private String[]          server_names= null; // list of available servers
  private int[]             max_process = null; // lists the maximum number of
                                                // processes that can be run
                                                // at once on the corresponding
                                                // server
  private int[]             num_process = null;

  /**
   *  Construct a new ParallelExecutor to execute the specified list of
   *  operators in separate Threads.
   *
   *  @param operators  The list of OperatorThreads that are to be run
   *                    concurrently.
   *
   *  @param max_threads The maximum total number of threads to use.
   *
   *  @param max_milliseconds The maximum total running time allow for 
   *                          executing all operators.  If this time is
   *                          execeeded, an exception will be thrown by
   *                          the runOperators() method.  The exception
   *                          contains the results from the operators that
   *                          did complete.
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
   *  Construct a new ParallelExecutor to execute the specified list of
   *  operators in separate Threads.
   *
   *  @param operators  The list of OperatorThreads that are to be run
   *                    concurrently.
   *
   *  @param max_milliseconds The maximum total running time allow for 
   *                          executing all operators.  If this time is
   *                          execeeded, an exception will be thrown by
   *                          the runOperators() method.  The exception
   *                          contains the results from the operators that
   *                          did complete.
   *
   *  @param  servers         Array of available server names.  If a String
   *                          is null, empty, or "localhost", the process
   *                          will be run locally.
   *
   *  @param  max_processes   Array of integers specifying the maximum
   *                          number of processes that should be run on
   *                          the corresponding server named in the array
   *                          servers.
   *
   *  @throws IllegalArgumentException if the Vector of operators is null
   *                                   or has a null IOperator, or if the
   *                                   max_threads or max_milliseconds is
   *                                   not positive.
   */
  public ParallelExecutor( Vector<IOperator> operators,
                           int               max_milliseconds,
                           String[]          servers,
                           int[]             max_processes )
                           throws IllegalArgumentException
  {
    this( operators, 1, max_milliseconds );
    
    if ( max_processes == null ) 
      throw new IllegalArgumentException("max_processes empty");

    if ( max_processes.length <= 0 ) 
      throw new IllegalArgumentException("max_processes has zero length");

    if ( servers == null ) 
      throw new IllegalArgumentException("array of server names is null");

    if ( servers.length <= 0 ) 
      throw new IllegalArgumentException("array of server names is empty");

    Vector other_servers = new Vector();
    Vector num_processes = new Vector();
    int     local_processes = 0;
    int     other_processes = 0;
    for ( int i = 0; i < servers.length; i++ )
    {
      if ( servers[i] == null              ||    // some form of localhost
           servers[i].trim().length() == 0 ||
           servers[i].equalsIgnoreCase( "localhost" ) )
      {
        if ( max_processes[i] >= 0 )
          local_processes = max_processes[i];
        else
          local_processes = 0;                   // use zero if negative
      }
      else
      {
        if ( max_processes[i] > 0 )              // ignore any remote machines
        {                                        // with zero processes
          other_servers.add( servers[i] );
          num_processes.add( max_processes[i] );
          other_processes += max_processes[i];
        }
      }
    }

    if ( other_processes <= 0 )                  // don't do any server
    {                                            // assignement, use local only
      if ( local_processes > 0 )
        this.max_threads = local_processes;
      else
        this.max_threads = 1;
      return;
    }
                                                // set up tables of server 
    int table_size = other_servers.size();      // names and process info
    if ( local_processes > 0 )
      table_size += 1;

    server_names = new String[ table_size ];
    max_process  = new int[ table_size ];
    num_process  = new int[ table_size ];
                                                // add info for other machines
    for ( int i = 0; i < other_servers.size(); i++ )
    {
      server_names[i] = (String)other_servers.elementAt(i);
      max_process[i]  = (Integer)num_processes.elementAt(i);
      num_process[i]  = 0;
    }
     
    if ( local_processes > 0 )                  // add info for local host
    {
      int last = server_names.length - 1;
      server_names[ last ] = null;             // null represents local host
      max_process [ last ] = local_processes;
      num_process [ last ] = 0;
    }
                                               // find the total number of
                                               // processes we can use.
    int total = 0;
    for ( int i = 0; i < max_process.length; i++ )
      total += max_process[i];

    this.max_threads = total;

    if ( total != (other_processes + local_processes) )
      System.out.println("ERROR: total = " + total + 
                         " Other Processes = " + other_processes +
                         " Local Processes = " + local_processes  );
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
       AllocateServer( op );
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
           {
              IOperator old_op = thread.getOperator();
              FreeServer( old_op );
                                                    // if thread is done
                                                    // and no more to run, just
             if ( next_op_index >= operator_list.size() )  
               running_threads.remove(i);           // remove it from list of
                                                    // running threads, else
             else                                   // start a new thread in
             {                                      // its place
               int index = next_op_index;
               next_op_index++;

               IOperator op = operator_list.elementAt( index );
               AllocateServer( op );
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
   *  Set the server for this operator if it is an IDistributedOperator,
   *  and do nothing otherwise.
   *
   *  @param  op  The operator that should be started.
   */
   private void AllocateServer( IOperator op )
   {
     if ( op instanceof IDistributedOperator )
     {
       if ( server_names == null )              // just use local machine
       {
         ((IDistributedOperator)op).setServerName( null );
         return;
       }
                                                // find the server with the
       int server_index = 0;                    // most free processes
       int max_free_proc = 0;
       for ( int i = 0; i < server_names.length; i++ )
       {
         int n_free = max_process[i] - num_process[i];
         if ( n_free > max_free_proc )
         {
           max_free_proc = n_free;
           server_index = i;
         }         
       }
                                                // set the op's server to the
                                                // server with most free procs  
       ((IDistributedOperator)op).setServerName( server_names[server_index] );
       num_process[server_index]++;
     }
                                               // if not a distributed op,
                                               // just return
   }
  

  /**
   *  Reduce the number of processes being handled by the server for this
   *  operator.
   *
   *  @param  op  The operator that has finished running.
   */
   private void FreeServer( IOperator op )
   {
     if ( server_names == null )                // no server lists
       return;

     if ( op instanceof IDistributedOperator )
     {
       String name = ((IDistributedOperator)op).getServerName();
       if ( name == null )                     // local host at end of the list
       {
          num_process[ num_process.length-1 ]--;
          return;
       }

       for ( int i = 0; i < server_names.length; i++ )
       {
         if ( name.equalsIgnoreCase( server_names[i] ) )
         {
           num_process[ i ]--;                 // decrement process count for
           return;                             // this server
         }
       }
     }
                                               // if not a distributed op,
                                               // just return
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
