/*
 * File:  PixelPolymarker.java
 *
 * Copyright (C) 2005 Chad Jones
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
 * Modified:
 *
 *  $Log$
 *  Revision 1.3  2007/08/14 01:27:27  dennis
 *  Switched to JSR231 based jogl.
 *
 *  Revision 1.2  2005/08/04 22:36:46  cjones
 *  Updated documentation and comment header.
 *
 *  Revision 1.1  2005/07/27 20:36:41  cjones
 *  Added menu item that allows the user to choose between different shapes
 *  for the pixels. Also, in frames view, user can change the time between
 *  frame steps.
 *
 *
 */
package gov.anl.ipns.ViewTools.Components.ThreeD;

import java.awt.Color;
import gov.anl.ipns.MathTools.Geometry.Vector3D;
import SSG_Tools.SSG_Nodes.SimpleShapes.*;

/**
 *  This Node draws colored markers of a specified type and size at the 
 *  specified points. It holds an id and value for the pixel it represents.
 */
public class PixelPolymarker extends Polymarker
                             implements IPixelShape
{
  private int PixelID = -1;
  private float PixelVal = 0;
  
  /* --------------------------- Constructor --------------------------- */
  /**
   *  Construct a Polymarker with marks at the specified points.  The 
   *  default marker type is a dot.
   *
   *  @param  id           The pick id associate with this object
   *  @param  verts        Array of Vector3D objects specifying where the
   *                       markers should be drawn.
   *  @param  size         The size of the marker.  For all markers other 
   *                       than DOT, the size is specified in terms of a 
   *                       percentage of a unit length in world coordinates. 
   *                       For a DOT, the actual size in pixels is specified.
   *                       
   *  @param  type         Type code for the marker to use.  This should be 
   *                       one of the defined marker types such as DOT, PLUS, 
   *                       STAR, etc.
   *  @param  new_color    The color of the parallelogram.
   */
  public PixelPolymarker( int id,
                          Vector3D verts[], 
                          int      size,
                          int      type,
                          Color    new_color )
  {
    super( verts, size, type, new_color );
    PixelID = id;
  }
 
   /**
   * Return pixel's id.
   *
   *    @return Pixel id
   */
  public int getPixelID()
  {
  	return PixelID;
  }
  
  /**
   * Set the current value of the pixel.
   * 
   * @param value Value of pixel.
   */
  public void setValue(float value)
  {
  	PixelVal = value;
  }
  
  /**
   * Return pixel's value.
   *
   * @return value of pixel
   */
  public float getValue()
  {
  	return PixelVal;
  }
}