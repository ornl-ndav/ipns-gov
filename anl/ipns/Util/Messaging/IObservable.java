/*
 * @(#)IObservable.java   
 *
 *  Programmer:  Dennis Mikkelson
 *
 *  $Log$
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
