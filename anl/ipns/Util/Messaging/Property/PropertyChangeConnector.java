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
 * Revision 1.1  2005/07/25 20:29:15  kramer
 * Initial checkin.  This class acts like the center of a web of
 * communication between various PropertyChangeHandlers in the web.  Its
 * job is to relay messages exactly once to every item in the web.
 *
 */
package gov.anl.ipns.Util.Messaging.Property;


import java.util.Vector;

public class PropertyChangeConnector
{
   private String currentProperty;
   private Vector/*<PropertyChangeHandler>*/ handlerVec;

   public PropertyChangeConnector()
   {
      this(new Vector());
   }
   
   public PropertyChangeConnector(Vector handlerVec)
   {
      this.currentProperty = null;
      this.handlerVec = handlerVec;
   }
   
   public void addHandler(PropertyChangeHandler handler)
   {
      if (handler==null || handlerVec.contains(handler))
         return;
      
      handlerVec.add(handler);
   }
   
   public void removeHandler(PropertyChangeHandler handler)
   {
      if (handler==null)
         return;
      
      handlerVec.remove(handler);
   }
   
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
