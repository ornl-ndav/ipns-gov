/*
 * File: IObserver.java
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
 *  Revision 1.9  2004/03/17 20:27:33  dennis
 *  Fixed @see tag that was broken when view components, math and
 *  util were moved to gov package.
 *
 *  Revision 1.8  2004/03/11 22:53:03  rmikk
 *  Changed the package name
 *
 *  Revision 1.7  2004/01/24 21:00:01  bouzekc
 *  Removed unused imports.
 *
 *  Revision 1.6  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 *  Revision 1.5  2002/11/07 16:31:40  pfpeterson
 *  Added new constant for telling viewers to close.
 *
 */

package gov.anl.ipns.Util.Messaging;

/**
 * The IObserver interface provides an interface that "observer" objects
 * must implement.  An observable object maintains a list of observer objects 
 * to notify when the state of the observable object changes.  Notification
 * is done by calling the observer's update method.  ( See Java Design, by 
 * Peter Coad & Mark Mayfield, Prentice Hall, 1999, pp 233-249 )  
 *
 * @see gov.anl.ipns.Util.Messaging.IObservable
 * @see gov.anl.ipns.Util.Messaging.IObserverList
 *
 */

public interface IObserver
{
  public static final String CLOSE_VIEWERS      = "CLOSE VIEWERS";
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
