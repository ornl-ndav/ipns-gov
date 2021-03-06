/*
 * File:  StringListChoicePG.java
 *
 * Copyright (C) 2006, Dennis Mikkelson
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2006/07/10 16:25:06  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.1  2006/07/04 18:34:42  dennis
 *  Abstract base class for PGs that allow a choice from a list
 *  of Strings, such as ChoicePG and RadioButtonPG.  Basically,
 *  this takes care of the list of possible choices.
 */

package gov.anl.ipns.Parameters;

import java.util.*;
import java.lang.reflect.*;

/**
 * A StringListChoicePG is an abstract base class that implements common
 * methods for classes that allow the user to choose from a list of Strings.
 * Basically, this maintains the list of available choices.
 */
public abstract class StringListChoicePG extends StringPG_base
{
  protected Vector  choices  = null;


  /**
   *  Constructor, just passes the parameters to the 
   *  base class constructor.
   *
   * @param  name   The name (i.e. prompt string) for this PG.
   * @param  val    String or Vector of Strings giving the value 
   *                for this PG.  If one string is specified, it
   *                will be placed in the list of choices, and be
   *                the default choice.  If 'val' is a list of
   *                strings, and the last item in the list is a
   *                repeat of one of the earlier items, it will 
   *                become the default choice, but it will not
   *                appear twice in the list.
   */
  public StringListChoicePG( String name, Object val )
  {
    super( name, val );
    InitChoiceVector( val );
  }


  /**
   *  Set up the list of choices from the specified object.
   *
   *  @param  val  One String or a Vector of Strings.
   */
  protected void InitChoiceVector( Object val )
  {
    choices = new Vector();
    if ( val == null )
    {
       str_value = "";
       return;
    }
    if( val.getClass().isArray()){//Convert to Vector
       Vector V = new Vector();
       for( int i=0; i< Array.getLength(val); i++)
          V.addElement( Array.get( val , i ));
       val = V;
    }
    if ( val instanceof Vector )                // get an array of Strings from
    {                                           // the Vector
      String list[] = new String[ ((Vector)val).size() ];
      for ( int i = 0; i < list.length; i++ )
        list[i] = Conversions.get_String( ((Vector)val).elementAt(i) );

      boolean default_specified = false;
      String last = list[ list.length - 1 ];
      for ( int i = 0; i < list.length - 1; i++ )
        if ( list[i].equals( last ) )
          default_specified = true;

      if ( default_specified )
      {
        str_value = last;
        for ( int i = 0; i < list.length - 1; i++ )
          addItem( list[i] );
      }
      else
      {
        str_value = list[0];
        for ( int i = 0; i < list.length; i++ )
          addItem( list[i] );
      }
    }
    else                                        // set value from simple object
    {
      str_value = Conversions.get_String( val );
      addItem( val );                           // add initial value to list
    }
  }

  
  /**
   *  Add a new item to the list of choices to use, the next time that
   *  the widget is created.  NOTE: This does not have an effect on a 
   *  widget that is currently displayed.
   *
   *  @param item  A Object from which a String will be extracted
   *               and add to the list of items to choose from.
   */
  public void addItem( Object item )
  {
    String temp = Conversions.get_String( item );
    if ( choices == null )
      choices = new Vector();

    if ( choices.size() == 0 )
    {
      choices.add( temp );
      return;
    }
                                     // make sure this is not a duplicate
                                     // before adding it to the list
    boolean duplicate = false;
    for ( int i = 0; i < choices.size(); i++ )
      if ( temp.equals( choices.elementAt(i) ) )
        duplicate = true;

    if ( !duplicate )
      choices.add( temp ); 
  }


  /**
   *  Add new items to the list of choices to use, the next time that
   *  the widget is created.  NOTE: This does not have an effect on a 
   *  widget that is currently displayed.
   *
   *  @param vec  A Vector of Strings to add to the list of choices.
   */
  public void addItems( Vector vec )
  {
    if ( vec == null )
      return;

    for ( int i = 0; i < vec.size(); i++ )
      addItem( vec.elementAt(i) );
  }


  /**
   * Used to clear the list of available choices, by setting the Vector
   * of choices to a new empty Vector.
   */
  public void clear()
  {
    super.clear();
    choices = new Vector();
  }


  /**
   * Set the value of this PG, its GUI entry widget, and the valid flag,
   * if the specified object can be converted to a String value that is
   * already present in the list of possible choices.  Throw an
   * exception if conversion to a String is not possible, of if the
   * specified value is not in the list of choices.  NOTE:  Derived 
   * classes should only be responsible for handling the value displayed
   * in the GUI entry widget, via setWidgetValue(), so this method is
   * final.
   *
   * @param  obj  The new value.
   *
   * @throws IllegalArgumentException if the specific object cannot be 
   *         converted to a String value.
   */
  public final void setValue( Object obj ) throws IllegalArgumentException
  {
    String temp = Conversions.get_String( obj );    // this could throw an
                                                    // exception
    if ( choices == null || choices.size() == 0 )
      return;
                                                     
    boolean match_found = false;
    for ( int i = 0; i < choices.size(); i++ )
    {
      String choice_str = Conversions.get_String( choices.elementAt(i));
      if ( choice_str.equals( temp ) )
      {
        str_value = choice_str;
        match_found = true;
        setValidFlag( false );
      }
    }

    if ( !match_found )
      throw new IllegalArgumentException("Requested choice NOT in list");

    if ( hasGUI() )  
      setWidgetValue( str_value ); 
  }

}
