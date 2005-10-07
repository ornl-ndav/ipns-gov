/*
 * File: InformationHandler.java
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
 * Revision 1.2  2005/10/07 21:26:07  kramer
 * Added javadoc comments for everything in the class/interface.
 *
 * Revision 1.1  2005/07/25 20:34:28  kramer
 *
 * Initial checkin. The InformationHandler and InformationCenter interface
 * and class form the base of an information communication system.
 * For a particular piece of information, exactly one InformationHandler is
 * registered with an InformationCenter to be responsible for retrieving
 * the information at any time.  Next, the center can acquire the
 * information just based on its key.  Classes don't have to know where or
 * how the information is stored or who stores the information.
 *
 */
package gov.anl.ipns.Util.Messaging.Information;

/**
 * Classes that implement this interface have the ability to be registered 
 * with a {@link gov.anl.ipns.Util.Messaging.Information.InformationCenter 
 * InformationCenter} object as being able to store data that is aliased 
 * by string keys.
 */
public interface InformationHandler
{
   //public boolean supportsKey(String key);
   /**
    * Used to get the value aliased by the given key.  If this handler 
    * doesn't have the information requested, it should return 
    * <code>null</code>.
    * 
    * @return The data refered to by the string <code>key</code> or 
    *         <code>null</code> if <code>key</code> is unrecognized or 
    *         invalid.
    */
   public Object getValue(String key);
}
