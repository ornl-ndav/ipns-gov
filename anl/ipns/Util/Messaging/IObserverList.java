/*
 * @(#)IObserverList.java     1.0  2000/01/31  Dennis Mikkelson
 *
 * ---------------------------------------------------------------------------
 *  $Log$
 *  Revision 1.1  2000/07/10 22:53:06  dennis
 *  Interfaces for observer/observable communications mechanism
 *
 *  Revision 1.6  2000/06/13 14:40:11  dennis
 *  Added method to get a clone of the list of observers.
 *
 *  Revision 1.5  2000/06/12 19:48:42  dennis
 *  now implements Serializable
 *
 *  Revision 1.4  2000/06/08 19:07:37  dennis
 *  Fixed DOS text problem
 *
 *  Revision 1.3  2000/05/11 16:18:22  dennis
 *  Added RCS logging
 *
 *
 */

package DataSetTools.util;

import java.io.*;
import java.util.*;

/**
 * An IObserverList object maintains a list of observer objects to notify when 
 * the state of an observable object changes.  Notification is done by calling 
 * the observer's update method for each observer in the list.  ( See Java 
 * Design, by Peter Coad & Mark Mayfield, Prentice Hall, 1999, pp 233-249 )
 *
 * @see DataSetTools.util.IObserver
 * @see DataSetTools.util.IObservable
 *
 */

public class IObserverList implements Serializable
{
  private Vector observers = new Vector();

  /**
   *  Add the specified object to the list of observers to notify when an 
   *  observable object changes.
   *  
   *  @param  iobs   The observer object that is to be notified.
   *
   */
   public void addIObserver( IObserver iobs )
   {
     observers.addElement( iobs );
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
