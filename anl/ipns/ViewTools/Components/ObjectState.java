/* 
 * file: ObjectState.java
 *
 * Copyright (C) 2003-2005, Mike Miller
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
 *  Revision 1.17  2007/07/16 15:46:48  rmikk
 *  Added a public static String that is a returned value of the get method when
 *    the path was absent( this was not as advertised it should return null)
 *
 *  Revision 1.16  2007/07/12 19:35:05  rmikk
 *  returned the result of a recursive call instead of false in reset
 *
 *  Revision 1.15  2005/08/17 21:25:28  kramer
 *
 *  Added the containsKey() method.  This method was added so that it could
 *  be used by the Operators.Special.ObjectState.ObjectStateUtilities class.
 *
 *  Revision 1.14  2005/08/05 21:53:04  kramer
 *
 *  Added the methods 'getValues()' and 'getKeys()' which are used to get an
 *  Enumeration of the values stored in and keys used by this ObjectState.
 *
 *  Revision 1.13  2005/06/02 21:19:41  kramer
 *
 *  Modified the editTable(....) method so that if it is called to insert a
 *  null field, it will immediately return false.
 *
 *  Corrected the indentation of the editTable(...) method (tab spaces were
 *  used instead of spaces).
 *
 *  Modified the javadocs for the reset(....) method to reflect the changes
 *  made to the editTable(....) method.
 *
 *  Revision 1.12  2005/03/14 20:50:56  millermi
 *  - Added private method getGlobal() which will recursively find
 *    all instances of a key if GLOBAL option is used.
 *  - Added functionality to get() to get all values referenced by
 *    a key if GLOBAL is used.
 *  - Revised documentation and javadocs for get() to reflect new
 *    functionality.
 *
 *  Revision 1.11  2005/03/13 23:21:59  millermi
 *  - Improved comments and javadocs.
 *
 *  Revision 1.10  2005/01/26 22:27:00  millermi
 *  - Removed public messaging string STATE_CHANGED since it
 *    was unused.
 *
 *  Revision 1.9  2004/03/15 23:53:50  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.8  2004/03/12 00:02:31  rmikk
 *  Fixed Package Names
 *
 *  Revision 1.7  2004/01/29 08:08:21  millermi
 *  - Added method silentFileChooser() to allow programmer to
 *    set read/write file without JFileChooser popping up.
 *
 *  Revision 1.6  2003/12/19 01:37:14  millermi
 *  - Overloaded toString(), now uses the Hashtable.toString()
 *
 *  Revision 1.5  2003/12/17 06:37:10  millermi
 *  - openFileChooser() now returns a boolean, true if successful,
 *    false if not. Also added javadocs to this method.
 *
 *  Revision 1.4  2003/11/18 17:29:47  millermi
 *  - Fixed bug found by Chris Bouzek that prevented ObjectState
 *    from saving to the current directory set by the user.
 *
 *  Revision 1.3  2003/11/18 01:19:29  millermi
 *  - changed System.out.println() to SharedMessages.addmsg() for
 *    output pertaining to the user.
 *
 *  Revision 1.2  2003/11/18 00:58:07  millermi
 *  - Now implements Serializable
 *  - Added capabilities for saving the state.
 *
 *  Revision 1.1  2003/09/23 23:11:14  millermi
 *  - Initial Version - allows preservation of settings after ISAW is exited.
 *  - Allows global variables for resetting at the top level.
 *
 */

 package gov.anl.ipns.ViewTools.Components;
 
 import java.util.Vector; 
 import java.util.Hashtable;
 import java.util.Enumeration;
 import javax.swing.JFrame;
 import javax.swing.JFileChooser;
 
 import gov.anl.ipns.Util.File.SerializeUtil;
 import gov.anl.ipns.Util.Sys.*;

/**
 * This class is used to preserve the state of on object. Similar to Java's
 * Hashtable, ObjectState uses a key to identify a field. However, since
 * ObjectStates can be nested, recursion is used to find which table the field
 * is contained in. Recursion is only available if the key is a string and
 * incorporates the following naming convention: <BR><BR>
 * <B>
 * ClassName1.ClassName2.ClassNameN.Key
 * </B><BR><BR>
 * Each class that uses ObjectState should specify a public static final
 * key_name for each field whose state is being preserved. 
 * 
 */ 
public class ObjectState implements java.io.Serializable
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
  private transient ObjectState this_state;
  private transient JFrame f;
  private transient String projectsDirectory;
  private transient JFileChooser fc;
  private transient Vector listeners;
  
  public static String INVALID_PATH ="Invalid Path in ObjectState.java";

 /**
  * Constructor - Initializes the Hashtable
  */
  public ObjectState()
  {
    table = new Hashtable();
    this_state = this;
    f = new JFrame();
    projectsDirectory = System.getProperty("user.home");
    listeners = new Vector();
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
  * If GLOBAL.key is used, all instances of the key will be returned in the
  * following form: If key not found, null is returned. If any instance of the
  * key is found, a Vector containing all of the values is returned. To
  * decifer values, the entries in the Vector will be a two-element Object[]
  * consisting of the full path key and the value.<BR><BR>
  * <I>Example use of <B>GLOBAL</B></I><BR>
  * ObjectState1 contains key1(Float), key2(Float), key3(ObjectState2)<BR>
  * ObjectState2 contains key1(String), key4(Integer), key5(ObjectState3)<BR>
  * ObjectState3 contains key6(Float), key1(Float)<BR><BR>
  * Calling get(GLOBAL.key1) would return:<BR>
  * Vector containing three Object[2] pairs.<BR>
  * Element 1: Object[0] = String key3.key5.key1, Object[1] = Float value<BR>
  * Element 2: Object[0] = String key3.key1, Object[1] = String value<BR>
  * Element 3: Object[0] = String key1, Object[1] = Float value<BR>
  *
  *  @param  key Key referencing the saved value. Key does not have to be
  *              a String unless the value is in an imbedded ObjectState,
  *              but use of Strings is recommended.
  *  @return The value referenced by the key. If GLOBAL.key is used,
  *          the returned value will be a Vector of two-element Object arrays.
  *          See method description for more detail. If key is not found,
  *          a null is returned or a String INVALID_PATH
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
      // Special Case: if key is global (GLOBAL.key), get all instances of
      // that key at every level of the ObjectState.
      if( skey.equals(GLOBAL) )
      {
        if( nextkey == null )
	  return null;
        // Store values at every level in a vector.
	Vector key_list = new Vector();
	// Get list of keys that match "nextkey" at every level.
        getGlobal(nextkey,null,key_list);
	// If any instance of key was found, return list.
	if( key_list.size() > 0 )
	  return key_list;
        // If key was not found, return null.
	return null;
      }
      
      // if null, then no recursion needed because field is in this table
      if( nextkey == null )
	return table.get(key);
      else
      {
        // Get skey value.
	Object nextstate = get( skey );
	// nextstate must be ObjectState since this is a compound key,
	// os1.os2.os3...osN.key. Repeat process until only the key is left.
        if( nextstate instanceof ObjectState )
          return ((ObjectState)nextstate).get( nextkey );
	// if it gets to this point, the path was incorrect.
        SharedMessages.addmsg("Invalid Path in ObjectState.java"); 	
        return INVALID_PATH;  
      }
    }
    // if the key is not a string, no parsing or recursion is required, the
    // key is assumed to be at this level in the ObjectState heirarchy.
    return table.get(key);
  }
  
  /**
   * Used to get an enumeration of all of the values contained in this 
   * ObjectState.
   * 
   * @return A list of all of the values stored in this ObjectState.
   */
  public Enumeration getValues()
  {
     return table.elements();
  }
  
  /**
   * Used to get an enumeration of the keys used to access the values in 
   * this ObjectState.
   * 
   * @return A list of the keys used to access the data in this ObjectState.
   */
  public Enumeration getKeys()
  {
     return table.keys();
  }
 
 /**
  * This method is used to alter the value of a field that is already in
  * the ObjectState hashtable. If the field has not yet been added, use the
  * insert() method to add the field to the ObjectState hashtable. This method
  * will return true if the reset was successful.
  *
  *  @param  key
  *  @param  field
  *  @return <ul>
  *            <li>
  *              true - if reset successful
  *            </li>
  *            <li>
  *              false - if <code>key</code> was not found or 
  *                      if <code>field</code> was <code>null</code>
  *            </li>
  *          </ul>
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
  
  /**
   * Used to determine if the given key is used in this ObjectState.
   * 
   * @param key The key to analyze.
   * @return True if the given key is used used in this ObjectState and 
   *         false if it isn't.
   */
  public boolean containsKey(String key)
  {
     return table.containsKey(key);
  }
  
 /**
  * This method is an alternative way to read/write files, without the GUI.
  * Using this method allows the programmer to read/write a file without
  * the user knowing. This is handy when loading default state information.
  *
  *  @param  filename The filename where to read/write file, Includes full path.
  *  @param  isSave true if saving state, false if loading state
  *  @return true if save/load was successful,
  *          false if unsuccessful
  */
  public boolean silentFileChooser( String filename, boolean isSave )
  {
    // Add .isv extension if not already present.
    filename = new StateFileFilter().appendExtension(filename);
    if( isSave )
    {
      return SerializeUtil.writeObjectToFile( this_state, filename );   
    }
    else
    {
      Object temp = SerializeUtil.readObjectFromFile( filename );
      if( temp == null || !(temp instanceof ObjectState) )
      {
        return false;
      }
      else
      { 	
        this_state.table = ((ObjectState)temp).table;
        return true;
      } 
    }
  }
  
 /**
  * This method will pop-up a JFileChooser to allow ObjectState to be loaded
  * or saved. 
  *
  *  @param  isSave true if saving state, false if loading state
  *  @return true if save/load was successful,
  *          false if unsuccessful or operation was cancelled.
  */ 
  public boolean openFileChooser( boolean isSave )
  {
    String title = "";
    // if projectsDirectory is null, java handles it.
    fc = new JFileChooser(projectsDirectory);
    StateFileFilter sff = new StateFileFilter();
    fc.setFileFilter( sff );
    //System.out.println("Current: " + fc.getCurrentDirectory().getPath() );
    // Set title of JDialog window.
    if( isSave )
    {
      title = "Save State";   
    }
    else
    {
      title = "Load State";
    }
    
    int result = fc.showDialog(f,title);
    // If affirmative button pressed, attempt to save/load file.
    if( result == JFileChooser.APPROVE_OPTION )
    {
      String filename = fc.getSelectedFile().toString();
      filename = sff.appendExtension(filename);
      //File file = new File(filename);
      if( title.equals("Save State") )
      {
        if( !SerializeUtil.writeObjectToFile( this_state, filename ) )
        {
          SharedMessages.addmsg("Error saving state information in " +
        		     "ObjectState.java. State was not saved!!!" );
        }
	else
	  return true;
      }
      else if( fc.getApproveButtonText().equals("Load State") )
      {
        Object temp = SerializeUtil.readObjectFromFile( filename );
        if( temp == null || !(temp instanceof ObjectState) )
        {
          SharedMessages.addmsg("Error loading state information in " +
        		     "ObjectState.java. State was not loaded!!!" );
        }
        else
        {	  
          this_state.table = ((ObjectState)temp).table;
	  return true;
        }
      }
    }
    return false;
  }
  
  /* 
   * setProjectsDirectory() and getProjectsDirectory() are taken from 
   * Chris Bouzek's Wizard.java.
   */
 /**
  * Sets the directory for the state save files to go into. By default,
  * files are saved to the home directory.
  *
  *  @param  dir Directory projects are to be saved to.
  */
  public void setProjectsDirectory( String dir )
  {
    projectsDirectory = dir;
  }

 /**
  * Gets the directory for the state save files. By default, projects are
  * saved to the home directory.
  *
  *  @return Directory where files will be saved to.
  */
  public String getProjectsDirectory()
  {
    return projectsDirectory;
  }
 
 /**
  * Override Object.toString(). Uses Hashtable.toString() for better println
  * readout.
  *
  *  @return String representation of this ObjectState.
  */ 
  public String toString()
  {
    return table.toString();
  }
 
 /*
  * This method uses recursion to retrieve a value at any level referenced by
  * the specified key. If key does not exist, nothing is added to the Vector.
  *
  *  @param  key The key being searched for.
  *  @param  level_up The key(s) from the ObjectStates above this one.
  *  @param  value_list The list of values where all the values of a global
  *                     key are stored.
  */ 
  private void getGlobal( String key, String level_up, Vector value_list )
  {
    // Get list of keys at this level.
    Enumeration keys = table.keys();
    String tempkey;
    Object value;
    // Go through each key, find all ObjectState values. If value is an
    // ObjectState, search it for further instances of nextkey.
    while( keys.hasMoreElements() )
    {
      tempkey = (String)keys.nextElement();
      value = table.get(tempkey);
      // Use recursion to go down each level of the ObjectState.
      if( value instanceof ObjectState )
      {
        // If level_up = null, do not prepend it to the key.
        if( level_up == null )
    	  ((ObjectState)value).getGlobal( key, tempkey, value_list );
    	else
	  ((ObjectState)value).getGlobal( key,
	                                  new String(level_up+"."+tempkey),
					  value_list );
      }
    }
    // If key exists at this level, make an entry consisting of the
    // full path and the value.
    String compound_key;
    // if no level above this, do not prepend anything to key.
    if( level_up == null )
      compound_key = key;
    // Prepend the path to get to this key.
    else
      compound_key = new String(level_up+"."+key);
    Object tempvalue = get(key);
    // If tempvalue exists...
    if( tempvalue != null )
    {
      // Create entry consisting of full path key and value.
      Object[] key_value_entry;
      key_value_entry = new Object[2];
      key_value_entry[0] = new String(compound_key);
      key_value_entry[1] = tempvalue;
      // Add entry to list.
      value_list.add(key_value_entry);
    }
  }
    
 /*
  * This method groups functionality for the insert() and reset() methods, with
  * the ability to replace/reject fields with redundant keys, basically splits
  * up the functionality of Java's Hashtable.put()
  */ 
  private boolean editTable( Object key, Object field, boolean allow_replace )
  {
    //first ensure that the field is not null
    if (field == null)
       return false;
     
    // Ensure key is a String.
    if( key instanceof String )
    {
      String skey = (String)key;
      String nextkey = null;
      int period_index = skey.indexOf(".");
      // If period exists, let skey = preperiod, nextkey = postperiod string.
      if( period_index > 0 )
      {
         nextkey = skey.substring(period_index + 1);
         skey = skey.substring(0,period_index);
      }
      //System.out.println(skey + " " + temp_key);
      // if null, no period found, skey is the key registered in table.
      // No more recursion needed.
      if( nextkey == null )
      {
        // reset() called, replace value.
        if( allow_replace )
        {
           // If the key is found in the hashtable, replace the old value.
          if( get(skey) != null )
          {
            table.put(skey,field);
            return true;
          }
          return false;
        }
        // else insert() method called.
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
        // If reset() called...
        if( allow_replace )
        {
          // If nextkey is a key in the hashtable...
          if( get(nextkey) != null )
          {
            // replace the existing value.
            table.put(nextkey,field);
            Enumeration e = table.elements();
            Object temp_entry;
            // Create new key "GLOBAL.nextkey"
            String jointkey = new String(skey.concat(".").concat(nextkey));
            // go through this level and find all ObjectStates, then
            // pass (reset) the global variable down to all lower levels.
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
      // Else, have case: Level1.Level2.Level3...LevelN.key, need to get
      // through levels to find key.
      else
      {
         Object nextstate = get( skey );
         // Get next level, Must be ObjectState, if not, something is wrong.
        if( nextstate instanceof ObjectState )
          return  ((ObjectState)nextstate).editTable( nextkey, field, allow_replace );
        else
          SharedMessages.addmsg("Invalid Path in ObjectState.java");
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
    System.out.println("Testing global get...");
    Vector list = (Vector)ostest.get(new String(GLOBAL+".Test"));
    Object[] temp;
    if( list != null )
    {
      for( int i = 0; i < list.size(); i++ )
      {
        temp = (Object[])list.elementAt(i);
        System.out.println("Key: "+temp[0]+", Value: "+temp[1]);
      }
    }
    else
      System.out.println(GLOBAL+".Test not found.");
    // resets any value for the variable "Test" in the heirarchy to 
    // "Global testing"
    ostest.reset("Global.Test", "Global testing");
    System.out.println( ostest.get("Test") );
    System.out.println( ostest.get("One.Test") );
    System.out.println( ostest.get("One.Two.Test") );
    System.out.println( ostest.get("One.Two.Three.Test") );
    System.out.println( ostest.get("Four.Test") );
    System.out.println( ostest.get("Four.Test2") );
    System.out.println("Before write...");
    ostest.openFileChooser(true);
    
    System.out.println("Changing Values...");    
    state4.reset("Test", "test4reset");
    state4.reset("Test2", "test4.2reset");
    state3.reset("Test", "test3reset");
    state2.reset("Test", "test2reset");
    state1.reset("Test", "test1reset");
    ostest.reset("Test", "ostestreset");
    System.out.println( ostest.get("Test") );
    System.out.println( ostest.get("One.Test") );
    System.out.println( ostest.get("One.Two.Test") );
    System.out.println( ostest.get("One.Two.Three.Test") );
    System.out.println( ostest.get("Four.Test") );
    System.out.println( ostest.get("Four.Test2") );
    
    System.out.println("Before read...");
    ostest.openFileChooser(false);
    System.out.println("After read...Restoring values");
    
    System.out.println( ostest.get("Test") );
    System.out.println( ostest.get("One.Test") );
    System.out.println( ostest.get("One.Two.Test") );
    System.out.println( ostest.get("One.Two.Three.Test") );
    System.out.println( ostest.get("Four.Test") );
    System.out.println( ostest.get("Four.Test2") );
    
    System.out.println("Directory: " + ostest.getProjectsDirectory() );
    
    System.out.println("toString(): " + ostest.toString() );
    
    System.exit(1);
  }
} 
