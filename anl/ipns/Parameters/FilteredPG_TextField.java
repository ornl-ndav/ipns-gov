/*
 * File:  FilteredPG_TextField.java
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
 *  Revision 1.3  2006/07/10 16:25:04  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.2  2006/06/30 14:24:41  dennis
 *  Removed unused imports.
 *
 *  Revision 1.1  2006/06/29 15:45:09  dennis
 *  This class extends JTextField and accepts a filter that is applied
 *  to make certain that the new String should be accepted BEFORE the
 *  String even appears in the JTextField.  This class can also accept
 *  a ParameterGUI object to notify of the change.  If the ParameterGUI
 *  object is passed in as null, no notification will be attempted.
 *  That means that this class can be easily used as a drop-in
 *  replacement for a JTextField, when filtering is needed.
 *
 *
 */

package gov.anl.ipns.Parameters;

import javax.swing.*;
import javax.swing.text.*;

/**
 *  A FilteredTextField object extends JTextField and uses an IStringFilter
 *  object to limit what can be typed into the text field.  In addition,
 *  when changes are accepted, a specified PG is notified of the change.
 */
public class FilteredPG_TextField extends JTextField 
{

  /**
   *  Construct a JTextField object that applies the specified filter to
   *  characters, as they are being typed, to prevent clearly invalid input.
   *  The simple IStringFilter is used to create a Java DocumentFilter which
   *  is used with the document model for this JTextField object.  The 
   *  document model is set to a PlainDocument object to guarantee that the
   *  document supports filtering.
   *
   *  @param  pg      The ParameterGUI to be notified when the value in
   *                  this text field is changed.  If this is null, no
   *                  notification will be attempted.
   *                   
   *  @param  filter  The IStringFilter that is used to check the validity
   *                  of a resulting String, BEFORE it a change to the
   *                  text field's document is accepted.  If null is passed
   *                  in for the filter, a default filter that accepts any
   *                  String is used.
   */
  public FilteredPG_TextField( ParameterGUI pg, IStringFilter filter )
  {
    if ( filter == null )                      // make sure we have a valid 
      filter = new AllPassFilter();            // filter

    PlainDocument document = new PlainDocument();
    setDocument( document );

    DocumentFilter doc_filter = new PG_DocumentFilter( pg, filter );
    document.setDocumentFilter( doc_filter );
  }


  /**
   *  Construct a JTextField object that applies the specified filter to
   *  characters, as they are being typed.  This just passes in null for
   *  the ParameterGUI to the other constructor.
   *
   *  @param  filter  The IStringFilter that is used to check the validity
   *                  of a resulting String, BEFORE it a change to the
   *                  text field's document is accepted. 
   */
  public FilteredPG_TextField( IStringFilter filter )
  {
    this( null, filter );
  }


}
