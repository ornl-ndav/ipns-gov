/*
 * File:  PG_DocumentFilter.java
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
 *  Revision 1.5  2006/07/10 16:25:05  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.4  2006/06/30 14:24:39  dennis
 *  Removed unused imports.
 *
 *  Revision 1.3  2006/06/29 15:46:58  dennis
 *  This no longer requires a ParameterGUI object to notify when
 *  changes to the text are accepted.  No notification will be attempted,
 *  if the ParameterGUI to notify is null.
 *
 *  Revision 1.2  2006/06/28 22:46:25  dennis
 *  Added some explanatory javadocs.
 *
 *  Revision 1.1  2006/06/28 21:35:42  dennis
 *  This class extends Java's DocumentFilter class to provide
 *  character by character filtering of the characters typed into
 *  a TextField or TextArea.  The constructor takes an IStringFilter
 *  object, so that different filter behaviors can be plugged in.
 *
 */

package gov.anl.ipns.Parameters;

import java.awt.*;
import javax.swing.text.*;

/**
 *  This class is a DocumentFilter that allows plugging in different 
 *  filter functions to restrict the contents of a JTextComponent to 
 *  a particular type of String.  It will notify a specific PG when the
 *  Text is being changed. 
 */

public class PG_DocumentFilter extends DocumentFilter 
{
  private IStringFilter my_filter = null;  // If present, only Strings that
                                           // satisfy the isOkay() method of
                                           // the filter will be accepted.

  private ParameterGUI my_pg  = null;   // This is the PG whose 
                                           // notifyChanging()
                                           // method should be called.

  /**
   *  Construct a PG_DocumentFilter to notify the specified ParameterGUI 
   *  that the widget's value is being changed, AND to check that the newly 
   *  entered String is acceptable, based on the specified filter.
   *
   *  @param  pg     The ParameterGUI object to be notified when changes
   *                 are made.  This is null, no notification will be done.

   *  @param  filter The IStringFilter that will be used to check wheter 
   *                 or not the current String is acceptable.  If this is
   *                 null, a default filter that accepts all changes will
   *                 be used.
   */
  public PG_DocumentFilter( ParameterGUI pg, IStringFilter filter )
  {
    my_pg     = pg;
    if ( filter == null )
      filter = new AllPassFilter();
    my_filter = filter;
  }


  /**
   *  This method just calls super.insertString() after printing a message.
   *  It is not actually called when this DocumentFilter is associated with
   *  a TextField, so the message is just printed in case a later version
   *  of Java starts to use this method.  At that time the method should
   *  be implemented to check the validity of the String.
   */
  public void insertString( DocumentFilter.FilterBypass fb, 
                            int          offset, 
                            String       string, 
                            AttributeSet attr )  throws BadLocationException
  {
    System.out.println("***insertString called " + offset + ", " + string );
    super.insertString( fb, offset, string, attr );
  }


  /**
   *  Remove a specified number of characters from the current String, 
   *  provided the resulting String is still a valid String as defined
   *  by the IStringFilter.isOkay() method.  This method is called when
   *  the user presses the <delete> or <backspace> key.
   */
  public void remove( DocumentFilter.FilterBypass fb, 
                      int offset, 
                      int length )  throws BadLocationException
  {
                                                        // get copy of String
    Document my_doc = fb.getDocument();
    String cur_text = my_doc.getText( 0, my_doc.getLength() ); 

                                                        // and try the change
    String new_string = cur_text.substring( 0, offset ) +  
                        cur_text.substring( offset + length );

    if ( my_filter.isOkay( new_string ) )               // if ok make the change
    {                                                   // by calling super.()
      super.remove( fb, offset, length );
      if ( my_pg != null )
        my_pg.notifyChanging();
    }
    else                                                // else sound bell to
      Toolkit.getDefaultToolkit().beep();               // warn the user
  }


  /**
   *  Replace a specified number of characters in the current String, 
   *  provided the resulting String is still a valid String as defined
   *  by the IStringFilter.isOkay() method.  This method is called when
   *  the user presses the <delete> or <backspace> key.
   */
  public void replace( DocumentFilter.FilterBypass fb, 
                       int          offset, 
                       int          length, 
                       String       text, 
                       AttributeSet attrs )  throws BadLocationException
  {
                                                        // get copy of String
    Document my_doc = fb.getDocument();
    String cur_text = my_doc.getText( 0, my_doc.getLength() ); 

                                                        // and try the change
    String new_string = "";
    new_string = cur_text.substring( 0, offset ) + 
                 text + 
                 cur_text.substring( offset + length );

    if ( my_filter.isOkay( new_string ) )               // if ok make the change
    {                                                   // by calling super.()
      super.replace( fb, offset, length, text, attrs );
      if ( my_pg != null )
        my_pg.notifyChanging();
    }
    else                                                // else sound bell to
      Toolkit.getDefaultToolkit().beep();               // warn the user
  }
 
}
