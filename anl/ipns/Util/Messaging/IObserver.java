/*
 * @(#)IObserver.java     1.0  2000/01/31  Dennis Mikkelson
 *
 * ---------------------------------------------------------------------------
 *  $Log$
 *  Revision 1.2  2000/11/07 15:31:27  dennis
 *  Added ATTRIBUTE_CHANGED and FIELD_CHANGED messages.
 *
 *  Revision 1.1  2000/07/10 22:53:05  dennis
 *  Interfaces for observer/observable communications mechanism
 *
 *  Revision 1.5  2000/06/12 19:49:08  dennis
 *  Added DATA_CHANGED message
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

/**
 * The IObserver interface provides an interface that "observer" objects
 * must implement.  An observable object maintains a list of observer objects 
 * to notify when the state of the observable object changes.  Notification
 * is done by calling the observer's update method.  ( See Java Design, by 
 * Peter Coad & Mark Mayfield, Prentice Hall, 1999, pp 233-249 )  
 *
 * @see DataSetTools.util.IObservable
 * @see DataSetTools.util.IObserverList
 *
 */

public interface IObserver
{
  public static final String DESTROY            = "DESTROY";
  public static final String DATA_REORDERED     = "DATA_REORDERED";
  public static final String DATA_DELETED       = "DATA_DELETED";
  public static final String DATA_CHANGED       = "DATA_CHANGED";
  public static final String SELECTION_CHANGED  = "SELECTION CHANGED";
  public static final String POINTED_AT_CHANGED = "POINTED AT CHANGED";
  public static final String HIDDEN_CHANGED     = "HIDDEN CHANGED";
  public static final String GROUPS_CHANGED     = "GROUPS CHANGED";
  public static final String ATTRIBUTE_CHANGED  = "ATTRIBUTE CHANGED";
  public static final String FIELD_CHANGED      = "FIELD CHANGED";
 
  /**
   *  The update() method must update the current observer object, based on
   *  the new state of the observed object.  This method will be called when
   *  when the observable object changes state.  
   *  
   *  @param  observed_obj   The observable object whose state has changed.
   *  @param  reason         Object containing information about the change
   *                         in the observable object.  This object will 
   *                         usually be a String.
   *
   */

  void update( Object observed_obj, Object reason );

}
