/*
 * File:  ISlicePlaneSelector.java
 *
 * Copyright (C) 2004, Dennis Mikkelson
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
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.4  2004/03/12 00:20:10  serumb
 * Changed package and imports.
 *
 * Revision 1.3  2004/03/04 20:48:21  dennis
 * Added constants for HKL and QXYZ mode, for use by related classes.
 *
 * Revision 1.2  2004/03/03 23:19:08  dennis
 * Added message string for slice mode changed.
 *
 * Revision 1.1  2004/01/24 23:28:58  dennis
 * Interface for objects that specify a plane as a
 * SlicePlane3D object.
 *
 */

package gov.anl.ipns.ViewTools.UI;

import gov.anl.ipns.MathTools.Geometry.*;

/**
 *  This interface defines the methods required of a component that will
 *  specify a plane in 3D. 
 */

public interface ISlicePlaneSelector
{
  /**
   *  Integer mode ID for selections interms of HKL
   */
  public static final int HKL_MODE  = 0;

  /**
   *  Integer mode ID for selections interms of Qxyz 
   */
  public static final int QXYZ_MODE = 1;

  /**
   *  Message String sent when the user selects a different slice plane
   */
  public static final String PLANE_CHANGED = "Plane Changed";  


  /**
   *  Message String sent when the user selects a different slice mode
   */
  public static final String SLICE_MODE_CHANGED = "Slice Mode Changed";

                                                                
  /**
   *  Set the plane whose parameters are to be displayed by this plane selector.
   *
   *  @param new_plane  The plane to display in this selector.
   */
  public void setPlane( SlicePlane3D new_plane );


  /**
   *  Get the plane currently specified by this plane selector.
   */
  public SlicePlane3D getPlane();

}

