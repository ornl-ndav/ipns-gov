/*
 * File:  FinishWindowListener.java 
 *             
 * Copyright (C) 2004, Ruth Mikkelson
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 * This work was supported by the National Science Foundation under
 * grant number DMR-0218882
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.2  2004/07/28 16:07:32  dennis
 * Added java docs.
 *
 * Revision 1.1  2004/03/23 14:46:24  rmikk
 * Initial Checkin.  This window listener calls the finish method of an
 * IFinish Window.  This method should call the protected finalize method
 *
 */
package gov.anl.ipns.Util.Sys;
import java.awt.*;
import java.awt.event.*;

/**
 *  This class calls the finalize on Window to dispose of input methods and
 *  context and removes the weak reference which formally pointed to this
 *  window from the parents owned window list.  This will actually dispose
 *  of a window.
 */

public class FinishWindowListener extends WindowAdapter{

   public void windowClosed(WindowEvent e){
      Object src = e.getSource();
      if( src instanceof Window)
        if( src instanceof IFinish)
           ((IFinish)src).finish();
   }

}
