/*
 * File:  ServerTypeString.java
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 *  Revision 1.3  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 */
package DataSetTools.util;

import java.io.*;

/**
 * The ServerTypeString class is used to pass parameters between the
 * LoadRemoteData operators and the GUI so that appropriate GUI components 
 * can be created to get the input values from the user. 
 */
public class ServerTypeString  extends     SpecialString
                               implements  IStringList,
                                           Serializable 
{
   static public final String  LIVE_DATA_SERVER  = "Live Data";
   static public final String  ISAW_FILE_SERVER  = "ISAW File Server";
   static public final String  NDS_FILE_SERVER   = "NDS File Server";

   static private final String strings[] = { LIVE_DATA_SERVER,
                                             ISAW_FILE_SERVER,
                                             NDS_FILE_SERVER   };
   public ServerTypeString( )
   {
     this( "" );
   }

   public ServerTypeString( String message )
   {
     super( message );

     boolean found = false;
     int     i     = 0;
     while ( (i < strings.length) && !found )
       if ( strings[i].equals( message ) )
         found = true;
       else
         i++;
        

     if ( !found ) 
       setString( strings[0] );
   }

  /* --------------------------- num_strings ------------------------------ */
  /**
   *  Get the number of Strings contained in this list of Strings.
   *
   *  @return  the number of Strings in the list of Strings.
   */

  public int num_strings()
  {
     return strings.length; 
  }


  /* ----------------------------- getString ----------------------------- */
  /**

sition is not valid.
   */

  public String getString( int position )
  {
     if ( position < 0 || position >= strings.length )
       return null;
     else
       return strings[ position ];
  }
    public static void main( String args[] )
    {  ServerTypeString STS = new ServerTypeString( 
                  ServerTypeString.ISAW_FILE_SERVER);
      System.out.println("OK");
    }
}
