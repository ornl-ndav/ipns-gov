/*
 * File: IObservable.java   
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.3  2001/04/25 22:24:25  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.2  2001/01/29 20:52:48  dennis
 *  Now uses CVS revision numbers.
 *
 *  Revision 1.1  2000/07/10 22:53:04  dennis
 *  Interfaces for observer/observable communications mechanism
 *
 *  Revision 1.3  2000/06/08 19:07:37  dennis
 *  Fixed DOS text problem
 *
 *  Revision 1.2  2000/05/11 16:18:22  dennis
 *  Added RCS logging
 *
 */

package DataSetTools.util;

import java.io.*;

/**
 * The IObservable interface provides an interface that "observable" objects
 * must implement.   An observable object maintains a list of observer objects 
 * to notify when the state of the observable object changes.  Notification 
 * is done by calling the observer's update method.  ( See Java Design, by
 * Peter Coad & Mark Mayfield, Prentice Hall, 1999, pp 233-249 )
 *
 * @see DataSetTools.util.IObserver
 * @see DataSetTools.util.IObserverList
 *
 */

public interface IObservable
{
  /**
   *  Add the specified object to the list of observers to notify when this 
   *  observable object changes.
   *  
   *  @param  iobs   The observer object that is to be notified.
   *
   */
   void addIObserver( IObserver iobs );

  /**
   *  Remove the specified object from the list of observers to notify when 
   *  this observable object changes.
   *  
   *  @param  iobs   The observer object that should no longer be notified.
   *
   */
   void deleteIObserver( IObserver iobs );

  /**
   *  Remove all objects from the list of observers to notify when this 
   *  observable object changes.
   */
   void deleteIObservers( );

}
