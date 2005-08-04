/*
 * File: IViewComponent3D.java
 *
 * Copyright (C) 2005, Chad Jones
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
 * Primary   Chad Jones <cjones@cs.utk.edu>
 * Contact:  Student Developer, University of Tennessee
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 * 
 * This work was supported by the University of Tennessee Knoxville and 
 * the Spallation Neutron Source at Oak Ridge National Laboratory under: 
 *   Support of HFIR/SNS Analysis Software Development 
 *   UT-Battelle contract #:   4000036212
 *   Date:   Oct. 1, 2004 - Sept. 30, 2006
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.3  2005/08/04 22:36:44  cjones
 *  Updated documentation and comment header.
 *
 *  Revision 1.2  2005/07/22 19:45:12  cjones
 *  Separated 3D components into one base object and two functional objects,
 *  one for data with frames and one for data without frames. Also, added features
 *  and tweaked functionality.
 *
 *  Revision 1.1  2005/07/19 15:56:37  cjones
 *  Added components for Display3D.
 * 
 */
 
package gov.anl.ipns.ViewTools.Components.ThreeD;

import gov.anl.ipns.ViewTools.Components.IViewComponent;
import gov.anl.ipns.ViewTools.Components.IPointList3D;

/**
 * Any class that implements this interface will interpret and display
 * data in a 3D scene form.
 */
public interface IViewComponent3D extends IViewComponent
{  
 /**
  * This method is invoked to notify the view component when the 
  * IPointList3D of data has changed. 
  *
  *  @param  v3D - virtual array of data
  */ 
  public void dataChanged(IPointList3D[] v3D);
}
