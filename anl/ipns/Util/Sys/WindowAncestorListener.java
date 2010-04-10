/* 
 * File: WindowAncestorListener.java
 *
 * Copyright (C) 2010, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author:$
 *  $Date:$            
 *  $Rev:$
 */
package gov.anl.ipns.Util.Sys;

import java.awt.Window;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * This Ancestor listener disposes a Window when the ancestor of a 
 * component is removed.  Components that spawn new windows should use
 * this to eliminate windows that they spawn after the window showing
 * a component is removed.
 * 
 * @author ruth
 *
 */
public class WindowAncestorListener implements AncestorListener
{

   Window window;
   
   public WindowAncestorListener( Window window)
   {
      this.window = window;
   }
   @Override
   public void ancestorAdded(AncestorEvent arg0)
   {

      
   }

   @Override
   public void ancestorMoved(AncestorEvent arg0)
   {

      
   }

   /**
    * If the ancestor of a given component( some window) is closed so
    * the component cannot be displayed,  this will dispose of the
    * window( possibly spawned by the component) passed in with the
    * constructor
    */
   @Override
   public void ancestorRemoved(AncestorEvent arg0)
   {
      
      if( window != null)
      {
         window.removeAll( );
         window.dispose( );
      }

   }

}
