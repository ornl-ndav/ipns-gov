/*
 * File: ActionValueListener.java
 *
 * Copyright (C) 2005, Mike Miller
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
 *  Revision 1.1  2005/03/28 05:35:41  millermi
 *  - Initial Version - This new event, listener, and utility classes
 *    are used by ComponentSwappers, ComponentLayoutManagers, and
 *    ComponentViewManagers to pass messages and values associated
 *    with the messages.
 *
 */
 package gov.anl.ipns.ViewTools.UI;

/**
 * This interface allows listeners to be notified of an ActionEvent and
 * have access to the old and new values without calling a get/set method
 * on the source of the event.
 */
 public interface ActionValueListener extends java.util.EventListener
 {
  /**
   * This method is called to notify listeners when a value has changed.
   *
   *  @param  ave The event containing the action command and the old and new
   *              value associated with the action command.
   */
   public void valueChanged( ActionValueEvent ave );
 }
