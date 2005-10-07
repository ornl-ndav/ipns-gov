/*
 * File: InformationCenter.java
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


import java.util.Hashtable;

/**
 * This class acts as a central warehouse of data that is shared by 
 * several different <code>InformationHandler</code> objects.  It allows 
 * the objects to access the shared data without having to know where or 
 * how the data is stored.
 * <p>
 * Here is an example of how this class is used:
 * <ul>
 *   Suppose that there are four variables x, y, z, and w such that each 
 *   Object X, Y, Z, and W uses all some or none of the variables in its 
 *   operation.  Moreover, suppose that Object X will actually be storing 
 *   the data for variable x, Object Y for variable y, and so forth.  Then, 
 *   this class can be used in the following way.  Each object will have a 
 *   reference to an <code>InformationCenter</code> object.  Object X will 
 *   register itself with the <code>InformationCenter</code> using one of 
 *   the <code>registerHandler()</code> methods and will specify some string 
 *   key (for example "varX") that will be used to refer to the data for 
 *   variable x.  The other objects will do the same. Then, suppose that one 
 *   of the Objects, say Z, wants to get the data for variable x.  All it 
 *   would have to do is invoke the <code>obtainValue("varX")</code> method 
 *   on the <code>InformationCenter</code> to obtain the data stored for the 
 *   variable x.  The <code>InformationCenter</code> is responsible for 
 *   determining where to acquire the data and to get the correct data 
 *   back.  It is also designed such that given some data that this class 
 *   maintains, there is exactly one <code>InformationHandler</code> that is 
 *   responsible for storing that data.  This means that for any data, it 
 *   will be consistently be accessed through the same 
 *   <code>InformationHandler</code> object.
 * </ul>
 * <p>
 * The design of this class, as described above, has several benefits.  
 * First, it allows for abstraction.  That is, if several Objects need to 
 * refer to the data encapsulated by a specific variable, by using an instance 
 * of this class, the Objects do not need to know where or how the data is 
 * stored to acquire the data.  They only need to know the key that is used 
 * to reference the data.  Next, the way this class is designed, it makes 
 * some <code>InformationHandler</code> store some particular data.  The 
 * <code>InformationHandler</code> in turn just registers itself with an 
 * instance of this class to allow the class to know which data the 
 * <code>InformationHandler</code> is storing.  This allows for 
 * efficiency optimizations.  Here is an example how:  
 * <ul>
 *   Suppose Object X uses variable x 90% of the time, Object Y using it 
 *   5% of the time, and Object Z using it 5% of the time.  Then, Object X 
 *   could be responsible for storing the data for variable x and will 
 *   register itself with an instance of this class.  Then, when Object X 
 *   wants to use variable x it can reference it directly as a field (without  
 *   having to ask the instance of this class for the data).  However, Objects 
 *   Y and Z have to use the instance of this class to access the data for 
 *   variable x.  Asking an instance of this class for the data for a  
 *   particular variable takes a little more stack space than accessing a  
 *   class's fields directly.  This is because when asking an instance of 
 *   this class for some particular data, it has to in turn find the correct 
 *   <code>InformationHandler</code> and ask it for the data.  When accessing 
 *   a field to acquire some data, fewer and even no method calls need to be 
 *   made to access the data.  Thus, this class lets the Objects that use the 
 *   data most often store the data.  To improve efficiency they can in 
 *   turn access the data directly as a field.  However, to allow for 
 *   abstraction any othe Object that uses the data can easily access the data 
 *   through an instance of this class.
 * </ul>
 */
public class InformationCenter
{
   /**
    * This is the hashtable of <code>InformationHandlers</code> that this 
    * class knows about.  The keys in the hashtable are <code>Strings</code> 
    * and are the strings that are used to refer to some data.  The 
    * values in the hashtable are <code>InformationHandler</code> objects.  
    * Thus, given a string that refers to some data, this hashtable stores 
    * the <code>InformationHandler</code> that is responsible for that data.
    */
   private Hashtable handlerTable;
   
   /**
    * Creates a new <code>InformationCenter</code> with no registered 
    * <code>InformationHandler</code> objects.
    */
   public InformationCenter()
   {
      this.handlerTable = new Hashtable();
   }
   
   /**
    * Used to determine if this class has a <code>InformationHandler</code> 
    * that is responsible for storing the data that is referenced by the 
    * string <code>key</code>.
    * 
    * @param key A string that refers to some data.
    * @return <code>True</code> if this class has a 
    *         <code>InformationHandler</code> that recognizes the string 
    *         <code>key</code> and <code>false</code> if it doesn't.
    */
   public boolean hasRegisteredHandler(String key)
   {
      return this.handlerTable.containsKey(key);
   }
   
   /**
    * Used to get the <code>InformationHandler</code> that is registered as 
    * being able to acquire the data refered to by the string 
    * <code>key</code>.
    * 
    * @param key A string that refers to some data.
    * @return The <code>InformationHandler</code>, registered with this 
    *         class, that recognizes the string <code>key</code> or 
    *         <code>null</code> if no such <code>InformationHandler</code> 
    *         exists.
    */
   public InformationHandler getHandler(String key)
   {
      if (!hasRegisteredHandler(key))
         return null;
         
      return (InformationHandler)this.handlerTable.get(key);
   }
   
   /**
    * Informs this class that the given <code>InformationHandler</code> knows 
    * how to work with the data referenced by the string <code>key</code>.  
    * If a <code>InformationHandler</code> already exists that is 
    * registered to work with the data referenced by the string 
    * <code>key</code>, this method does nothing.  Note:  For a given string 
    * key, there can be one and only one registered 
    * <code>InformationHandler</code> that is able to work with the data 
    * referenced by that string.
    * 
    * @param handler The object that is able to work with the data 
    *                referenced by the string <code>key</code>.
    * @param key     The string that is used to refer to some data.
    * @return        <code>True</code> if the <code>InformationHandler</code> 
    *                was successfully registered and <code>false</code> if 
    *                it wasn't.
    */
   public boolean registerHandler(InformationHandler handler, String key)
   {
      return registerHandler(handler, key, false);
   }
   
   /**
    * Informs this class that the given <code>InformationHandler</code> knows 
    * how to work with the data referenced by the string <code>key</code>.  
    * Through the <code>override</code> parameter, this method allows any 
    * previous <code>InformationHandler</code> to be removed if it was 
    * registered to work with the data referenced by the string 
    * <code>key</code>.  Note:  For a given string key, there can be one and 
    * only one registered <code>InformationHandler</code> that is able to 
    * work with the data referenced by that string.
    * 
    * @param handler  The object that is able to work with the data 
    *                 referenced by the string <code>key</code>.
    * @param key      The string that is used to refer to some data.
    * @param override If <code>true</code>, any previous 
    *                 <code>InformationHandler</code> that is registered to 
    *                 work with the string <code>key</code> will be 
    *                 overwritten.  If <code>false</code>, it will not be 
    *                 overwritten.
    * @return         <code>True</code> if the <code>InformationHandler</code> 
    *                 was successfully registered and <code>false</code> if 
    *                 it wasn't.
    */
   public boolean registerHandler(InformationHandler handler, String key, 
                                  boolean override)
   {
      if (hasRegisteredHandler(key))
      {
         if (override)
            deregisterHandler(key);
         else
            return false;
      }
      
      this.handlerTable.put(key, handler);
      return true;
   }
   
   /**
    * Used to de-register the <code>InformationHandler</code>, registered 
    * with this class, that was responsible for acquiring the data refered to 
    * by the string <code>key</code>.  If no such 
    * <code>InformationHandler</code> exists, nothing is done.  In other 
    * words, this method informs this class not to maintain a way to acquire 
    * the data referenced by the string <code>key</code>.
    * 
    * @param key A string that refers to some data.
    */
   public void deregisterHandler(String key)
   {
      this.handlerTable.remove(key);
   }
   
   /**
    * Used to obtain the data referenced by the given string.
    * 
    * @param key A string that refers to the data to acquire.
    * @return The value of the data referenced by the string <code>key</code> 
    *         or <code>null</code> if this class does not have a 
    *         <code>InformationHandler</code> that is registered to work 
    *         with the data referenced by the string <code>key</code>.
    */
   public Object obtainValue(String key)
   {
      InformationHandler handler = getHandler(key);
      
      if (handler == null)
         return null;
      
      return handler.getValue(key);
   }
}
