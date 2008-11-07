/*
 * File:  IDistributedOperator.java 
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

package gov.anl.ipns.Operator;

/**
 * This interface should be implemented by operators that run separate
 * in a separate process that may be assigned to a different node in 
 * a cluster.  It adds methods to get and set the name of the server
 * where the operator should be run.
 */

public interface IDistributedOperator extends IOperator
{

  /**
   *  Set the name of the server where this operator should be run.
   *
   *  @param server_name  The name of the server where this operator should
   *                      be executed.
   */ 
  void setServerName( String server_name );


  /**
   *  Get the name of the server where this operator should be run.
   *
   *  @return the name of the server where this operator should be run.
   */
  String getServerName();

}

