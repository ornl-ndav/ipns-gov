/*
 * File: IDrawable.java
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


import java.awt.*;
/**
 * This interface describes the one method that an object must implement
 * in order to be drawn in a TwoD_JPanel.  A TwoD_JPanel maintains a list
 * of IDrawable objects and is responsible for calling the draw() method 
 * on each of the objects being viewed.
 * 
 * @author Dennis Mikkelson
 * 
 */
public interface IDrawable
{
  /**
   * Draw this object using the specified graphics context.
   * 
   * @param graphics The graphics context in which the object will be 
   *                 drawn.
   */
  public void draw( Graphics2D graphics );
}
