/*
 * File:  PseudoColor.java
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
 * Revision 1.1  2004/06/18 19:19:26  dennis
 * Moved to Colors package
 *
 * Revision 1.2  2004/06/15 22:15:14  dennis
 * Removed unused import.
 *
 * Revision 1.1  2004/05/28 20:51:15  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Colors;
 
public class PseudoColor
{
 /**
   *  Set the color based on a color index for this object.
   *
   *  @param  index Specifies the index to be used to find the 
   *                color to use when drawing this object.
   */
  public void setColor( int index )
  {}


  /**
   *  Set the color for this object based on a Color Table.
   *  A color lookup table will be used, with an index into
   *  the color table computed based on specified value.
   *  The value will be clamped to the range 
   *  [min_color_value, max_color_value].
   *
   *  @param  value Used to find the index of the 
   *                color to use when drawing this object.
   */
  public void setColor( float value )
  {}


  /**
   *  Set color table range.
   */
  static public void setColorValueRange( float min, float max )
  {}


  /**
   *  Get the minimum for the color values.
   *
   */
  static public float getMinColorValue()
  { return -1; }


  /**
   *  Get the minimum for the color values.
   *
   */
  static public float getMaxColorValue()
  { return 1; }


  /**
   *  Set log color scale parameter
   */
  static public void setLogColorScaleParameter( float s )
  {}

  /**
   *  Get log color scale parameter
   */
  static public float getLogColorScaleParameter()
  {
    return 1;
  }

}
