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
 *  Revision 1.7  2004/03/17 20:27:33  dennis
 *  Fixed @see tag that was broken when view components, math and
 *  util were moved to gov package.
 *
 *  Revision 1.6  2004/03/11 22:53:03  rmikk
 *  Changed the package name
 *
 *  Revision 1.5  2004/01/24 20:54:24  bouzekc
 *  Removed unused imports.
 *
 *  Revision 1.4  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 */

package gov.anl.ipns.Util.Messaging;

/**
 * The IObservable interface provides an interface that "observable" objects
 * must implement.   An observable object maintains a list of observer objects 
 * to notify when the state of the observable object changes.  Notification 
 * is done by calling the observer's update method.  ( See Java Design, by
 * Peter Coad & Mark Mayfield, Prentice Hall, 1999, pp 233-249 )
 *
 * @see gov.anl.ipns.Util.Messaging.IObserver
 * @see gov.anl.ipns.Util.Messaging.IObserverList
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
