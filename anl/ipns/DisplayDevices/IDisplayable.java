/* 
 * File: IDisplayable.java 
 *  
 * Copyright (C) 2007     Dennis Mikkelson
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.3  2007/07/17 16:16:04  oakgrovej
 * Added Throws Exception where needed
 * setLineAttribute takes one int and two Strings instead of one int, one String and an Object
 *
 * Revision 1.2  2007/07/12 22:03:13  amoe
 * Made this interface public.
 *
 * Revision 1.1  2007/07/12 16:37:50  dennis
 * Initial version of interface for objects that bundle data with
 * a view so that they can be displayed on a graphics device.
 *
 */

package gov.anl.ipns.DisplayDevices;

import javax.swing.*;


/**
 *  This interface is used to configure a view of data, and produce
 *  a JComponent that can be printed, saved to a file or displayed on
 *  the screen by a specific GraphicsDevice.
 */
public interface IDisplayable
{

 /**
  *  This method returns a JComponent that can be displayed in a Frame,
  *  printed, or saved to a file.
  *
  *  @param  with_controls   If this is false, any interactive controls
  *                          associated with the view of the data will
  *                          NOT be visible on the JComponent
  *
  *  @return A reference to a JComponent containing the configured 
  *          display.
  */
  public JComponent getJComponent( boolean with_controls );


 /**
  *  This method sets an attribute of the displayable that pertains
  *  to the overall display, such as a background color.
  *
  *  @param  name     The name of the attribute being set.
  *  @param  value    The value to use for the attribute.
  */
  public void setViewAttribute( String name, Object value ) throws Exception;


 /**
  *  This method sets an attribute of the displayable that pertains
  *  to a particular portion of the display, such as one particular
  *  line. 
  *
  *  @param  index    An index identifying the part of the display
  *                   that the attribute applies to, such as a 
  *                   specific line number.
  *  @param  name     The name of the attribute being set.
  *  @param  value    The value to use for the attribute.
  */
  public void setLineAttribute( int index, String name, String value )throws Exception;

}
