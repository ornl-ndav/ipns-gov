/* 
 * file: IPreserveState.java
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
 *  Revision 1.2  2004/01/29 08:16:25  millermi
 *  - Updated the getObjectState() to include parameter for specifying
 *    default state.
 *  - Added static variables DEFAULT and PROJECT to IPreserveState for
 *    use by getObjectState()
 *
 *  Revision 1.1  2003/09/23 23:13:20  millermi
 *  - Initial Version - this interface is required by classes using an
 *    ObjectState to preserve state information.
 *
 */

 package DataSetTools.components.View;

/**
 * This interface is required by any class that uses the ObjectState class
 * to preserve state information. State information allows a user to preserve
 * object settings after closing the program.
 */ 
public interface IPreserveState
{ 
 /**
  * "true" - This variable is used as a parameter for the getObjectState()
  * method. Use this variable if the state returned is to store user
  * preferences, those things common from project to project.
  */
  public static final boolean DEFAULT = true;
  
 /**
  * "false" - This variable is used as a parameter for the getObjectState()
  * method. Use this variable if the state returned is to project specific
  * preferences, things such as image selections.
  */
  public static final boolean PROJECT = false;

 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param new_state
  */
  public void setObjectState( ObjectState new_state );
  
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  is_default True if default state, use static variable.
  *  @return if true, the selective default state, else the state for with
  *          all possible saved values.
  */ 
  public ObjectState getObjectState( boolean is_default );
}
