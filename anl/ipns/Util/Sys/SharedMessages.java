/*
 * File: SharedMessages.java
 *
 * Copyright (C) 2004, Mike Miller
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
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.2  2004/03/11 23:53:56  millermi
 *  - Removed Command prefix from StatusPane
 *
 *  Revision 1.1  2004/03/11 22:13:14  millermi
 *  - Changed package names and replaced SharedData with
 *    SharedMessages class.
 *
 *
 ********************Last Log message from ISAW's SharedData*******************
 **************See ISAW's SharedData for further revision details**************
 *  Revision 1.18  2004/01/24 20:57:51  bouzekc
 *  Removed unused imports.
 ******************************************************************************
 */

package gov.anl.ipns.Util.Sys;
/**
 *  Objects of this class have one instance of objects that are to be shared
 *  by several packages.  The shared objects are instantiated one time 
 *  as static members of the class.
 */

public class SharedMessages implements java.io.Serializable 
{
  /** The Global StatusPane.  Everyone can "add"(Display) values on this pane
   * if Displayable or the Values will be displayed on System.out
   */
  private static StatusPane status_pane=  null;

  /**
   * Returns a pointer to this classes (static) StatusPane for use in
   * GUIs. This will create the StatusPane if it does not already
   * exist.
   */
  public static StatusPane getStatusPane(){
    if(status_pane==null)
      status_pane=new StatusPane( 30,70);
    return status_pane;
  }
 
  /**
   * Convenience method to ease adding to the status pane.
   */
  public static void addmsg(Object value){
    if( status_pane==null || ! status_pane.isDisplayable())
      System.out.println( StringUtil.toString( value) );  
    else
      status_pane.add(value);
  }
}
