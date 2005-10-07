/*
 * File: PropertyHandler.java
 *
 * Copyright (C) 2005, Dominic Kramer
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
 * Primary   Dominic Kramer <kramerd@uwstout.edu>
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
 * $Log$
 * Revision 1.2  2005/10/07 21:28:28  kramer
 * Added javadoc comments for everything in the class/interface.
 *
 * Revision 1.1  2005/07/25 20:27:44  kramer
 *
 * Initial checkin.  Classes that implement this interface can be added to
 * a PropertyChangeConnector.  Thus, they can be placed in a web such that
 * every PropertyChangeHandler can communicate with every other
 * PropertyChangeHandler.
 *
 */
package gov.anl.ipns.Util.Messaging.Property;

/**
 * Classes that implement this interface have the ability to 
 * be notified when a specific named property has changed.
 */
public interface PropertyChangeHandler
{
   /**
    * Invoked when a particular named property has changed.
    * 
    * @param property The name of the property that has changed.
    * @param value    The property's new value.
    */
   public void propertyChanged(String property, Object value);
}
