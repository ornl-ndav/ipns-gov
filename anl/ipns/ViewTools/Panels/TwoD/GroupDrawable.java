/*
 * File: GroupDrawable.java
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
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */

package gov.anl.ipns.ViewTools.Panels.TwoD;


import java.awt.Graphics2D;

/**
 * This class is a Drawable that draws a list of other
 * drawables. 
 * 
 * @author Dennis Mikkelson
 *
 */
public class GroupDrawable extends Drawable
{
  private IDrawable[] list;
  
  /**
   * Construct a GroupDrawable consisting of the
   * specified array of IDrawable objects.
   * 
   * @param drawables  The list of IDrawable objects 
   */
  public GroupDrawable( IDrawable[] drawables )
  {
    list = new IDrawable[ drawables.length ];
    for ( int i = 0; i < drawables.length; i++ )
      list[i] = drawables[i];
  }
  
  
  /**
   * Draw this group using the specified graphics context, 
   * and any specified color, position and rotation angle.
   * 
   * @param graphics  The graphics context for drawing this group.
   */
  public void draw(Graphics2D graphics)
  {
    graphics = setAttributes( graphics );   // Use super class method 
                                            // to set up the color, etc. and
                                            // get a new graphics context
                                            // with those attributes set.

    for ( int i = 0; i < list.length; i++ ) // Draw the drawables in the list 
      list[i].draw( graphics );

    graphics.dispose();                     // get rid of the new 
                                            // graphics context 
  }

}
