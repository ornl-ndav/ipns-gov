/* 
 * File: OperatorThread.java
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

/**
 * An OperatorThread is responsible for running a specified operator in a
 * separate thread.  This is intended to support running an existing operator
 * in a separate thread.  Some care may be necessary if operators need to 
 * access shared resources, such as a file.  The join() method is used 
 * by the ParallelExecutor class to manage the execution of multiple 
 * OperatorTthreads and return when all have completed.
 */
public class OperatorThread extends Thread
{
  private IOperator my_operator;
  private Object    result = null;

  /**
   *  Construct a new thread to execute the specified operator.
   *
   *  @param operator  The operator that will be run in this thread.
   */  
  public OperatorThread( IOperator operator )
  {
    if ( operator == null )
      throw new IllegalArgumentException( "operator null" );
    this.my_operator = operator;
  }
  

  /**
   *  Get the result of running the operator.  This method should only be
   *  called after the thread has completed.  If it is called before the 
   *  thread is started, or after it is started, but before it is completed,
   *  an exception will be thrown.
   */
  public Object getResult()
  {
    if ( isAlive() )
      throw new IllegalArgumentException( "Thread is still running " + this );
    else if ( getState() == Thread.State.NEW )
      throw new IllegalArgumentException( "Thread NOT Started " + this );

    return result;
  }
  
  
  /**
   *  Execute the specified operator by calling its getResult() method
   *  and save the result when the operator's getResult() method completes.
   *  This method should not be called directly, rather the start() method 
   *  should be called to start this Thread.  
   */
  public void run()
  {
     result = null;
     result = my_operator.getResult();
  }
  
}
