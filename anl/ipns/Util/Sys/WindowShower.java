/*
 * File:  WindowShower.java
 *
 * Copyright (C) 2003, Dennis Mikkelson
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
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.2  2004/01/24 20:57:51  bouzekc
 * Removed unused imports.
 *
 * Revision 1.1  2003/12/11 18:18:37  dennis
 * Initial version of "helper" class to make it easier to use a separate
 * thread to actually display a JFrame or window.  This is needed to avoid
 * deadlock or race conditions when using frame.show() from a thread other
 * than the Swing event handling thread.  See: Core Java Technologies Tech
 * Tips, December 8, 2003, (Multithreading in Swing, ThreadLocal Variables).
 *
 */

package DataSetTools.util;

import java.awt.*;

  /**
   *  Runnable class to show a window using the event thread, AFTER it has
   *  been completely built.  This is the recommended way to show a window
   *  or JFrame in Swing.  Specifically, to actually display a window or
   *  JFrame, the following sequence of steps should be used:
   *  
   *   First, construct a new WindowShower, passing in the component to be 
   *          shown.
   *
   *   Second, call EventQueue.invokeLater( window_shower ).
   *
   *   Third, set the window_shower to null, so that it can be garbage 
   *          after the event queue finishes with it.
   *
   *  See: Core Java Technologies Tech Tips, December 8, 2003
   */
   public class WindowShower implements Runnable 
   {
     private Window window;


     private WindowShower()
     {
     }
 
     /**
      *  Construct a WindowShower runnable, for the specified Window (or
      *  JFrame) so that the window can be shown by the event thread.  The
      *  code that creates this WindowShower MUST also call 
      *  EventQueue.invokeLater( window_shower ).
      *
      *  @param  window  The Window or JFrame to be shown later.
      */
     public WindowShower( Window window ) 
     {
       this();
       this.window = window;
     }

     /**
      *  The run method will be called later by the event thread to actually
      *  show the window (or JFrame).
      */
     public void run() 
     {
       window.show();
     }
   }
