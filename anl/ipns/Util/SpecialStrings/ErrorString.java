/*
 * File: ErrorString.java
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
 *  Revision 1.7  2003/06/02 20:59:07  pfpeterson
 *  Added a constructor which takes a Throwable.
 *
 *  Revision 1.6  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 */
package DataSetTools.util;

import java.io.Serializable;

/**
 * The ErrorString class is used to construct and return strings for error
 * messages in a form that allows them to be recognized as error messages.
 */
public class ErrorString  extends     SpecialString
                          implements  Serializable 
{
   public ErrorString( )
   {
     super("");
   }


   public ErrorString( String message )
   {
     super( message );
   }

  public ErrorString(Throwable exception){
    this();
    if(exception==null) return; // don't change anything

    // get the exception name
    String name=exception.getClass().getName();
    int index=name.lastIndexOf(".");
    if(index>=0)
      name=name.substring(index+1);

    // get the message from the exception and set the string
    String message=exception.getMessage();
    if(message==null && message.length()<=0)
      this.setString(name);
    else
      this.setString(name+": "+message);
  }
}
