/*
 * File: IOverlay.java
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
 *  Revision 1.3  2003/06/09 22:34:31  dennis
 *  - Added methods help() and getFocus()  (Mike Miller)
 *
 *  Revision 1.2  2003/05/16 14:57:09  dennis
 *  Added acknowledgement of NSF funding.
 *
 */

package DataSetTools.components.View.Transparency;

import java.awt.*;

/**
 * This interface lays out methods to be implemented by all view component
 * overlays.
 */
public interface IOverlay
{
  /**
   * This method repaints the overlay. In most instances, this method will be
   * implemented by the OverlayJPanel via the JPanel implementation.
   */
   public void repaint();
  
  /**
   * This method will give the focus of the overlay. In many cases the overlay
   * will need one of it's private data members to be granted window focus.
   * getFocus() will allow the overlay to specify focus by using requestFocus()
   * within this method.
   */ 
   public void getFocus();
}
