/*
 * File:  IMutableVirtualArray.java
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
 * $Log$
 * Revision 1.1  2006/03/30 23:54:35  dennis
 * New interfaces that define the methods for altering the values
 * in a VirtualArray.  These methods were previously part of the
 * IVirtualArray interfaces.  The mutator methods are now being
 * factored out of the IVirtualArray classes, so that the values
 * in an IVirtualArray class are not changeable.  This change has
 * a number of advantages.
 * 1. The basic IVirtualArray concept was intended for passing
 *    information to viewers.  The viewers should not alter the
 *    data, so we now remove methods that could be used to alter
 *    data.
 * 2. The intention was to implement the IVirtualArray concept with
 *    classes that "extract" a regular grid of values from an
 *    underlying more complicated data structure.  It makes no
 *    sense to set values in that context, and it was previously
 *    necessary to include "stub" methods for the mutator methods.
 * 3. Using an immutable virtual array saves memory in the following
 *    way.  A virtual array can be wrapped around a DataSet and the
 *    values used from the underlying DataSet tables of values.
 *    Since the interface doesn't provide mutator methods, there
 *    is no need to make copies of the data.
 *
 */


package gov.anl.ipns.ViewTools.Components;

/**
 * This interface adds a method to set all values of a virtual array to
 * one constant value.  It is a convenience method common to virtual arrays
 * whose values can be altered.
 */

public interface IMutableVirtualArray extends IVirtualArray
{

 /**
  * Set all values in the array to a value. This method will usually
  * serve to "initialize" or zero out the array. 
  *
  *  @param  value - single value used to set all other values in the array
  */
  public void setAllValues( float value );

}
