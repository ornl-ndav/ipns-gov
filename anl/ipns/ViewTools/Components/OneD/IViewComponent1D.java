/*
 * File IViewComponent1D.java
 *
 * Copyright (C) 2003 Brent Serum
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
 * 
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USAB
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
 *  Revision 1.5  2004/01/23 19:35:30  serumb
 *  Added the GPL.
 *
 */
 
package DataSetTools.components.View.OneD;

import javax.swing.*;
import java.awt.*;
import java.lang.*;
import java.awt.event.*;
import DataSetTools.components.View.IViewComponent;
/**
 * Any class that implements this interface will interpret and display
 * data in a usable form. Examples include images, tables, and graphs.
 */
public interface IViewComponent1D extends IViewComponent
{
  /**
   * This method is a notification to the view component that the selected
   * point has changed.
   */ 
   public void setPointedAt( Point pt );
  
  /**
   *  Returns the point in World coords that is being pointed at.
   */ 
   public Point getPointedAt( );
  
}

