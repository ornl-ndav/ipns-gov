/*
 * File: IViewComponent2D.java
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 */
 
package DataSetTools.components.View.TwoD;

import javax.swing.*;
import java.awt.*;
import java.lang.*;
import java.awt.event.*;

/**
 * Any class that implements this interface will interpret and display
 * data in a usable form. Examples include images, tables, and graphs.
 */
public interface IViewComponent2D
{
  /*
   * These variables are messaging strings for use by action listeners.
   */
   public static final String POINTED_AT_CHANGED = "POINTED_AT_CHANGED";
   public static final String SELECTED_CHANGED = "SELECTED_CHANGED";
   
  /**
   * This method is a notification to the view component that the selected
   * point has changed.
   */ 
   public void setPointedAt( Point pt );
  
  /**
   * Given an array of points, a selection overlay can be created.
   */ 
   public void setSelectedSet( Point[] pts );
  
  /**
   * Retrieve array of points for selection overlay
   */
   public Point[] getSelectedSet();
   
  /**
   * To be continued...
   */ 
   public void dataChanged();
   
  /**
   * Add a listener to this view component. A listener will be notified
   * when a selected point or region changes on the view component.
   */
   public void addActionListener( ActionListener act_listener );
   
  /**
   * Remove a specified listener from this view component.
   */ 
   public void removeActionListener( ActionListener act_listener );
  
  /**
   * Remove all listeners from this view component.
   */ 
   public void removeAllActionListeners();
  
  /**
   * To be continued...
   */ 
   public JComponent[] getSharedControls();

  /**
   * To be continued...
   */   
   public JComponent[] getPrivateControls();

  /**
   * To be continued...
   */   
   public JMenuItem[] getSharedMenuItems();
   
  /**
   * To be continued...
   */
   public JMenuItem[] getPrivateMenuItems();
   
}
