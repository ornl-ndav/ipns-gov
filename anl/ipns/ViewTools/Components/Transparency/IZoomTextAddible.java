/*
 * File: IZoomTextAddible.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2003/12/23 01:55:51  millermi
 *  - Moved from TwoD directory to a more logical directory. Now
 *    in the same directory as the class that it provides
 *    functionality for.
 *
 *  Revision 1.1  2003/07/25 14:33:57  dennis
 *  Initial Version - implements IZoomAddible and provides getFont()
 *  method for overlays that respond to zooming and display text
 *  (Annotation Overlay). (Mike Miller)
 *
 */
 
package DataSetTools.components.View.Transparency;

import java.awt.Font;

/**
 * This interface is implemented by view components that utilize overlays
 * which respond to zooming and display text. An example is the
 * ImageViewComponent which uses the Annotation Overlay.
 *
 *  @see DataSetTools.components.View.Transparency.AnnotationOverlay
 */
public interface IZoomTextAddible extends IZoomAddible
{    
  /**
   * This method will return the font used by the overlays. The overlay
   * will call this method to determine what font to use.
   */
   public Font getFont();
}

