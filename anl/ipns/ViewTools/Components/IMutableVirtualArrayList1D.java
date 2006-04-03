/*
 * File: IMutableVirtualArrayList1D.java
 *
 * Copyright (C) 2006, Dennis Mikkelson
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2006/04/03 00:18:54  dennis
 *  Initial version with Mutator methods moved to IMutableVirtualArrayList1D.
 *  (More work needs to be done to clean up this implementation of the concept.)
 *
 */
 
package gov.anl.ipns.ViewTools.Components;
 
/**
 * This interface defines methods to set values and error values for elements
 * of an IVirtualArrayList1D.
 */
public interface IMutableVirtualArrayList1D extends IVirtualArrayList1D
{
  
 /**
  * Set values for one tabulated function together with it's error
  * estimates.
  *
  *  @param  x_values
  *  @param  y_values
  *  @param  errors
  *  @param  graph_title
  *  @param  graph_num
  */
  public void setXYValues( float[] x_values, 
                           float[] y_values, 
                           float[] errors,
                           String graph_title,
                           int graph_num);
}
