/*
 * File:  IObserverList.java
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
 *  Revision 1.6  2004/03/17 20:27:33  dennis
 *  Fixed @see tag that was broken when view components, math and
 *  util were moved to gov package.
 *
 *  Revision 1.5  2004/03/11 22:53:03  rmikk
 *  Changed the package name
 *
 *  Revision 1.4  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 */

package gov.anl.ipns.Util.Messaging;

import java.io.*;
import java.util.*;

/**
 * An IObserverList object maintains a list of observer objects to notify when 
 * the state of an observable object changes.  Notification is done by calling 
 * the observer's update method for each observer in the list.  ( See Java 
 * Design, by Peter Coad & Mark Mayfield, Prentice Hall, 1999, pp 233-249 )
 *
 * @see gov.anl.ipns.Util.Messaging.IObserver
 * @see gov.anl.ipns.Util.Messaging.IObservable
 *
 */

public class IObserverList implements Serializable
{
  private Vector observers = new Vector();

  /**
   *  Add the specified object to the list of observers to notify when an 
   *  observable object changes, provided the object is NOT already in the
   *  list of observers.
   *  
   *  @param  iobs   The observer object that is to be notified.
   *
   */
   public void addIObserver( IObserver iobs )
   {
     if ( observers.indexOf( iobs ) < 0 )    // only add the observer if it's 
       observers.addElement( iobs );         // NOT already there.
   }

  /**
   *  Remove the specified object from the list of observers to notify when 
   *  an observable object changes.
   *  
   *  @param  iobs   The observer object that should no longer be notified.
   *
   */
   public void deleteIObserver( IObserver iobs )
   {
     observers.removeElement( iobs );
   }

  /**
   *  Remove all objects from the list of observers to notify when an 
   *  observable object changes.
   */
   public void deleteIObservers( )
   {
     observers.removeAllElements();
   }

  /**
   *  Notify all observers in the list ( by calling their update(,) method )
   *  that the obsevered object has changed.
   *
   * @param  observed_obj  The observable object that has changed and will be
   *                       passed as the first parameter to the update(,) 
   *                       method of the observers. 
   *
   * @param  reason        Object indicating the nature of the change in the
   *                       observable object, or specifying the action that the
   *                       observer should take.  This is passed as the second
   *                       parameter to the update(,) method of the observers
   *                       and will typically be a String.
   */
   public void notifyIObservers( Object observed_obj, Object reason )
   {
     // Note: The notification may cause changes to the list of observers.
     //       In particular, if the notification is that the observed object
     //       is being destroyed, so the observers should destroy themselves,
     //       then the call to iobs.update() will probably remove iobs from
     //       the the list of observers.  In order to update all of the 
     //       observers in this case, it is necessary to step backwards through
     //       the list of observers.
     for ( int i = observers.size()-1; i >= 0; i-- ) 
     {
       IObserver iobs = (IObserver) observers.elementAt(i);
       iobs.update( observed_obj, reason );
     }
   }

  
  /**
   * Get a deep copy of the current list of observers 
   *
   *  @return  A new IObserverList object with the same entries in the
   *           list.
   */
   public Object clone()
   {
     IObserverList list = new IObserverList();
     
     for ( int i = 0; i < observers.size(); i++ )
       list.observers.addElement( observers.elementAt(i) );

     return list; 
   }
  
}
