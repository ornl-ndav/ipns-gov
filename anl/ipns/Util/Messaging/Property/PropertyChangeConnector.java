/*
 * File: PropertyChangeConnector.java
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
 * Revision 1.1  2005/07/25 20:29:15  kramer
 *
 * Initial checkin.  This class acts like the center of a web of
 * communication between various PropertyChangeHandlers in the web.  Its
 * job is to relay messages exactly once to every item in the web.
 *
 */
package gov.anl.ipns.Util.Messaging.Property;

import java.util.Vector;

/**
 * This class is used to connect several <code>PropertyChangeHandlers</code> 
 * together.  That is, the <code>PropertyChangeHandlers</code> can be 
 * thought of as nodes that are positioned on the outer edge of a circle.  
 * Then, a <code>PropertyChangeConnector</code> can be thought of as 
 * a node at the center of the circle.  When a property has changed, one 
 * of the <code>PropertyChangeHandlers</code> on the edge of the circle 
 * would inform the <code>PropertyChangeConnector</code> at the circle's 
 * center via the 
 * {@link #propertyChanged(String, Object, PropertyChangeHandler) 
 * propertyChanged()} method.  The <code>PropertyChangeConnector</code> 
 * would in turn redirect the message to all of the <b>other</b> 
 * <code>PropertyChangeHandlers</code> <b>exactly once</b>.  Thus, in 
 * summary, this class is used to redirect messages across several 
 * <code>PropertyChangeHandlers</code> and is responsible for making sure 
 * that each <code>PropertyChangeHandler</code> is informed exactly once.  
 */
public class PropertyChangeConnector
{
   /**
    * When the {@link #propertyChanged(String, Object, PropertyChangeHandler) 
    * propertyChanged} method is invoked, it sets this field to be the 
    * current property that has changed.  Then, if the method is invoked 
    * again with the same property (without being able to first terminate) 
    * it ignores the property which stops infinite loops.
    */
   private String currentProperty;
   /**
    * The Vector of <code>PropertyChangeHandlers</code> that this 
    * connector knows about.
    */
   private Vector/*<PropertyChangeHandler>*/ handlerVec;

   /**
    * Constructs a <code>PropertyChangeConnector</code> without any 
    * initial known <code>PropertyChangeHandlers</code>.
    */
   public PropertyChangeConnector()
   {
      this(null);
   }
   
   /**
    * Constructs a <code>PropertyChangeConnector</code> with the 
    * initial known <code>PropertyChangeHandlers</code> as specified.
    * 
    * @param handlerVec The Vector of <code>PropertyChangeHandlers</code> 
    *                   that this connector originally knows about.  If 
    *                   this parameter is <code>null</code>, the connector 
    *                   is constructed without any known 
    *                   <code>PropertyChangeHandlers</code>.
    */
   public PropertyChangeConnector(Vector handlerVec)
   {
      this.currentProperty = null;
      
      if (handlerVec != null)
         this.handlerVec = handlerVec;
      else
         this.handlerVec = new Vector();
   }
   
   /**
    * Adds the specified <code>PropertyChangeHandler</code> to the 
    * list of handlers that are informed when a property has changed.
    * 
    * @param handler The <code>PropertyChangeHandler</code> to add to 
    *                this class's record of handlers.  When a property 
    *                changes, this handler will be informed.
    */
   public void addHandler(PropertyChangeHandler handler)
   {
      if (handler==null || handlerVec.contains(handler))
         return;
      
      handlerVec.add(handler);
   }
   
   /**
    * Removes the specified <code>PropertyChangeHandler</code> from the 
    * list of handlers that are informed when a property has changed.
    * 
    * @param handler The <code>PropertyChangeHandler</code> to remove from 
    *                this class's record of handlers.  When a property 
    *                changes, this handler will be no longer be informed.
    */
   public void removeHandler(PropertyChangeHandler handler)
   {
      if (handler==null)
         return;
      
      handlerVec.remove(handler);
   }
   
   /**
    * Invoke this method to inform this connector that the given 
    * property has changed.  This connector will in turn inform 
    * all of the other <code>PropertyChangeHandlers</code> that 
    * the given property has changed.  That is, all 
    * <code>PropertyChangeHandlers</code> except the 
    * <code>PropertyChangeHandler</code> specified by the 
    * parameter <code>source</code> will be informed.
    * 
    * @param property A string alias for the property that has 
    *                 changed.
    * @param newValue The property's new value.
    * @param source   The <code>PropertyChangeHandler</code> that 
    *                 is informing this connector that the property 
    *                 has changed.
    */
   public void propertyChanged(String property, 
                               Object newValue, 
                               PropertyChangeHandler source)
   {
      //if propertyChanged() is currently working on notifying all of the 
      //PropertyChangeHandlers for the given property, just ignore this 
      //request 
      if ( (currentProperty!=null) && (currentProperty.equals(property)) )
         return;
         
      //record the property being analyzed
      currentProperty = property;
      
      //tell all of the PropertyChangeHandlers other than 'source' about 
      //the change
      Object current = null;
      for (int i=0; i<handlerVec.size(); i++)
         if (  ( (current=handlerVec.elementAt(i)) != null) && 
                 (current != source) )
            ((PropertyChangeHandler)current).propertyChanged(property, 
                                                             newValue);
                                                             
      //record that propertyChanged is done working with the 
      //property specified
      currentProperty = null;
   }
   
   /**
    * Testbed.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      class TestChangeHandler implements PropertyChangeHandler
      {
         private PropertyChangeConnector connector;
         private int index;
         
         public TestChangeHandler(PropertyChangeConnector connector, int index)
         {
            this.connector = connector;
            this.index = index;
         }
         
         public void propertyChanged(String property, Object value)
         {
            System.out.println("handler"+index+" doing some work....");
            System.out.println("  ....property = "+property);
            System.out.println("  ....value = "+value);
            System.out.println("  ....notifying everyone else");
            System.out.println();
         
            connector.propertyChanged(property, 
                                   value, 
                                   this);
         }
         
         public Object getPropertyValue(String property)
         {
            if (property==null)
               return null;
            
            if (property.equals("handler"+index))
               return new Integer(index);
            
            return null;
         }
      }
      
      final PropertyChangeConnector connector = new PropertyChangeConnector();
      
      int numHandlers = 10;
      Vector handlerVec = new Vector(numHandlers);
      for (int i=0; i<numHandlers; i++)
      {
         TestChangeHandler handler = new TestChangeHandler(connector, i);
         handlerVec.add(handler);
         connector.addHandler(handler);
      }
      
      //suppose that handler1 one changed some property such as the colorscale 
      String property = "COLORSCALE";
      Object value = "HEAT_2";
      
      //it changed the property and now wants to notify everyone else
      connector.propertyChanged(property, 
                               value, 
                               (PropertyChangeHandler)handlerVec.elementAt(5));
   }
}
