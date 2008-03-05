/* 
 * File: TestIOperator.java
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
 *  This class is a simple IOperator used for testing the  ParallelExecutor
 *  class.  Its getResult method just calculates a sum of integers and
 *  returns an identifying message together with the elapsed time as 
 *  it's result.
 */
public class TestIOperator implements IOperator
{
  String id;
  int    max;

  /**
   *  Construct a TestOperator with a specified id and number of values
   *  to sum.
   *
   *  @param  id   String identifier for this instance of TestOperator
   *  @param  max  The number of numbers to sum
   */
  public TestIOperator( String id, int max )
  {
    this.id  = id;
    this.max = max;
  }

  /**
   *  Run this operator.
   *
   *  @return A String containing the operator id, result of summing max
   *          integers and the execution time in milliseconds.
   */
  public Object getResult()
  {
     long start_time = System.nanoTime();

     double sum = 0;
     for ( int i = 1; i <= max; i++ )
       sum += i;

     long run_time = System.nanoTime() - start_time;

     return id + " sum = " + sum +
                 " calculated in " + run_time/1000000 + " milliseconds";
  }

}
