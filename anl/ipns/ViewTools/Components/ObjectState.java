/* 
 * file: ObjectState.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
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
 *
 *  $Log$
 *  Revision 1.1  2003/09/23 23:11:14  millermi
 *  - Initial Version - allows preservation of settings after ISAW is exited.
 *  - Allows global variables for resetting at the top level.
 *
 */

 package DataSetTools.components.View;
 
 import java.util.Hashtable;
 import java.util.Enumeration;

/**
 * This class is used to preserve the state of on object. Similar to Java's
 * Hashtable, ObjectState uses a key to identify a field. However, since
 * ObjectStates can be nested, recursion is used to find which table the field
 * is contained in. Recursion is only available if the key is a string and
 * incorporates the following naming convention: 
 * 
 * ClassName1.ClassName2.ClassNameN.Key
 *
 * Each class that uses ObjectState should specify a public static final
 * key_name for each field whose state is being preserved. 
 * 
 */ 
public class ObjectState
{ 
 /**
  * GLOBAL string constant is a prefix used by any state variable that needs
  * to be reset at every level of recursion. Note that if this prefix is used,
  * any value with the same key as GLOBAL.key will be altered. The use of
  * GLOBAL is restricted to reset() only. insert() will do nothing but return
  * false if GLOBAL is used for inserting.
  */
  public static final String GLOBAL = "Global";
  private Hashtable table;

 /**
  * Constructor - Initializes the Hashtable
  */
  public ObjectState()
  {
    table = new Hashtable();
  }
 
 /**
  * This method adds a field and its key to the hashtable. If the key already
  * exists, the new field will not replace the old field. To alter a field
  * that has a key already in the hashtable, use the method reset(). This
  * method will return true if the put was successful. Since this method
  * takes in an Object for a field value, primative types must be stored
  * in their Object equivalent. Ex: int -> Integer, boolean -> Boolean, ect.
  *
  *  @param  key
  *  @param  field
  *  @return true if field and key were added, false if key already existed in
  *	     the hashtable.
  */ 
  public boolean insert( Object key, Object field )
  {
    return editTable( key, field, false );
  }
 
 /**
  * This method retrieves a field referenced by the key. If key does not
  * exist, null is returned. Since the put() method takes in an Object for
  * a field value, primative types must be stored in their Object equivalent.
  * Ex: int -> Integer, boolean -> Boolean, ect.
  * Because of this, the returned Object will never be a primative type.
  *
  *  @param  key
  *  @return field - null if key not found
  */ 
  public Object get( Object key )
  {
    if( key instanceof String )
    {
      String skey = (String)key;
      String nextkey = null;
      int period_index = skey.indexOf(".");
      if( period_index > 0 )
      {
	nextkey = skey.substring(period_index + 1);
	skey = skey.substring(0,period_index);
      }
      //System.out.println(skey + " " + temp_key);
      
      // if null, then no recursion needed because field is in this table
      if( nextkey == null )
	return table.get(key);
      else
      {
	Object nextstate = get( skey );
        if( nextstate instanceof ObjectState )
          return ((ObjectState)nextstate).get( nextkey );
        // if it gets to this point, the path was incorrect.
        System.out.println("Invalid Path in ObjectState.java"); 	
        return "Invalid Path in ObjectState.java";  
      }
    }
    // if the key is not a string, no parsing or recursion is required, the
    // key is assumed to be at this level in the ObjectState heirarchy.
    return table.get(key);
  }
 
 /**
  * This method is used to alter the value of a field that is already in
  * the ObjectState hashtable. If the field has not yet been added, use the
  * insert() method to add the field to the ObjectState hashtable. This method
  * will return true if the reset was successful.
  *
  *  @param  key
  *  @param  field
  *  @return true - if reset successful, false - if key not found
  */ 
  public boolean reset( Object key, Object field )
  {
    return editTable( key, field, true );
  } 
 
 /**
  * Get the number of keys stored in this ObjectState.
  *
  *  @return size - integer number of keys
  */ 
  public int size()
  {
    return table.size();
  }   
  
 /*
  * This method groups functionality for the insert() and reset() methods, with
  * the ability to replace/reject fields with redundant keys, basically splits
  * up the functionality of Java's Hashtable.put()
  */ 
  private boolean editTable( Object key, Object field, boolean allow_replace )
  {
    if( key instanceof String )
    {
      String skey = (String)key;
      String nextkey = null;
      int period_index = skey.indexOf(".");
      if( period_index > 0 )
      {
	nextkey = skey.substring(period_index + 1);
	skey = skey.substring(0,period_index);
      }
      //System.out.println(skey + " " + temp_key);
      // if null, then no recursion needed
      if( nextkey == null )
      {
	if( allow_replace )
        {
          if( get(skey) != null )
          {
            table.put(skey,field);
            return true;
          }
          return false;
        }
        // else insert() method called this
	Object temp = table.put(skey,field);
	// if temp != null, then the key already existed. Temp contains the old
	// field that key referenced.
	if( temp != null && !allow_replace)
	{
	  table.put(skey, temp); // reset the key to its original field.
      /*
      System.out.println("Unable to assign key the this field, key already " +
			 "in use."); */
	  return false;
	}
	return true;
      } // end if nextkey == null
      else if( skey.equals(GLOBAL) )
      {
        if( allow_replace )
	{
          if( get(nextkey) != null )
          {
            table.put(nextkey,field);
	    Enumeration e = table.elements();
	    Object temp_entry;
	    String jointkey = new String(skey.concat(".").concat(nextkey));
	    // go through this level and find all ObjectStates, then
	    // pass the global variable down to them.
	    while( e.hasMoreElements() )
	    {
	      temp_entry = e.nextElement();
	      if( temp_entry instanceof ObjectState )
	        ((ObjectState)temp_entry).reset( jointkey, field );
	    }
            return true;
          }
          return false;	  
	}
	// if !allow_replace, do nothing. Don't want to insert global variables
	return false;
      } // end if skey.equals(GLOBAL)
      else
      {
	Object nextstate = get( skey );
        if( nextstate instanceof ObjectState )
          ((ObjectState)nextstate).editTable( nextkey, field, allow_replace );
        else
          System.out.println("Invalid Path in ObjectState.java");
        // if it gets to here, the path was invalid
        return false;  
      }
    } // end if key instanceof String
    // the key is not a string, so insert it at the current level
    else
    {
      if( allow_replace )
      {
	if( get(key) != null )
	{
	  table.put(key,field);
	  return true;
	}
	return false;
      }
      Object temp = table.put(key,field);
      // if temp != null, then the key already existed. Temp contains the old
      // field that key referenced.
      if( temp != null && !allow_replace)
      {
	table.put(key, temp); // reset the key to its original field.
	return false;
      }
      return true;
    }
  }

 /*
  * MAIN - Basic main program to test the ObjectState class
  */
  public static void main( String args[] ) 
  {
    ObjectState ostest = new ObjectState();
    ObjectState state1 = new ObjectState();
    ObjectState state2 = new ObjectState();
    ObjectState state3 = new ObjectState();
    ObjectState state4 = new ObjectState();
    
    state4.insert("Test", "test4");
    state4.insert("Test2", "test4.2");
    state3.insert("Test", "test3");
    state2.insert("Three",state3);
    state2.insert("Test", "test2");
    state1.insert("Two",state2);
    state1.insert("Test", "test1");
    ostest.insert("One",state1);
    ostest.insert("Test", "ostest");
    ostest.insert("Four",state4);
    
    System.out.println( ostest.get("Test") );
    System.out.println( ostest.get("One.Test") );
    System.out.println( ostest.get("One.Two.Test") );
    System.out.println( ostest.get("One.Two.Three.Test") );
    System.out.println( ostest.get("Four.Test") );
    System.out.println( ostest.get("Four.Test2") );
    
    ostest.reset("One.Two.Three.Test", "test5");
    System.out.println( ostest.get("One.Two.Three.Test") );
    // resets any value for the variable "Test" in the heirarchy to 
    // "Global testing"
    ostest.reset("Global.Test", "Global testing");
    System.out.println( ostest.get("Test") );
    System.out.println( ostest.get("One.Test") );
    System.out.println( ostest.get("One.Two.Test") );
    System.out.println( ostest.get("One.Two.Three.Test") );
    System.out.println( ostest.get("Four.Test") );
    System.out.println( ostest.get("Four.Test2") );
  }
} 
