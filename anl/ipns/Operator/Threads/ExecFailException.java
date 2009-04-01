/* 
 * File: ExecFailException.java
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

import java.util.Vector;

/**
 *  An ExecFailException is thrown when the execution of a list of 
 *  operators in separate Threads, did not complete normally.
 *  This could occur if one of the threads is interrupted, or if a 
 *  maximum alloted time is exceeded.  This class has methods to get a
 *  Vector containing the results from operators that did finish, and
 *  one of the ennumerated values INTERRUPTED or NOT_DONE for those 
 *  operators that did not finish. 
 */
public class ExecFailException extends RuntimeException
{
  private FailState status; 
  private Vector    results;

  /**
   *  Construct an ExecFaileException for the specified reason and
   *  list of partial results.
   *
   *  @param  status  FailState indicating whether the execution was
   *                  interrupted or the work was not done due to exceeding
   *                  the maximum time limit. 
   *  @param  results Ordered Vector containing results from all Operators.
   *                  If an Operator finished normally, the Object returned
   *                  by getResult() MUST be stored in the corresponding
   *                  position of this Vector.  If an Operator did NOT
   *                  finish correctly, FailState.NOT_DONE will be
   *                  stored in the corresponding position.
   */
  public ExecFailException( FailState status, Vector results )
  {
    this.status   = status;
    this.results = results;
  }


  /**
   *  Get a vector containing results from Operators that completed and
   *  FaileState.NOT_DONE or FailState.INTERRUPED for any Operators that 
   *  did not finish. 
   *
   *  @return A Vector containing results of any operators that finished
   *          completely before the execution halted and flags indicating
   *          the state of the other operators.
   */
  public Vector getPartialResults()
  {
     return results;
  }


  /**
   *  Get the reason a parallel execution of operators failed.
   *
   *  @return A FailState Object with the value INTERRUPTED if one of
   *          the Operators was interrupted, and NOT_DONE if the the
   *          maximum run time was exceeded.
   */
  public FailState getFailureStatus()
  {
     return status;
  }


}
