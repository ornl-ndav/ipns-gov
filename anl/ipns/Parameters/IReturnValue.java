/* 
 * File: IReturnValue.java
 *
 * Copyright (C) 2010, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author$:
 *  $Date$:            
 *  $Rev$:
 */

package gov.anl.ipns.Parameters;

/**
 *  Implementers of this class can return values to an IParameter by invoking its
 *  setValue method.
 *  
 * @author ruth
 *
 */

public interface IReturnValue
{
   /**
    * Sets the parameter that is to receive a value.
    *  
    * @param param   the parameter that could receive a value
    * 
    * NOTE: If the IParameter is an ParameterGUI it should immediately set the
    *  IParameter's value to some default value with the proper data type to work
    *  correctly with the ISAW's scripting system.
    */
    public void setRecipient( IParameter param);
}
