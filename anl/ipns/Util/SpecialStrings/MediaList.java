/*
 * File: MediaList.java
 *
 * Copyright (C) <year>, <original author>
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
 * Contact : <contact author> <email address>
 *           <street address>
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.2  2002/11/27 23:23:49  pfpeterson
 * standardized header
 *
 */
package DataSetTools.util;




public class MediaList extends SpecialString implements IStringList
  {
    String LL[] = { "Console", "File", "Table"};

    public MediaList()
     {
        super("");
     }

    public MediaList( String message )
      {super( message );
      }
    public String getString( int position )
      {if( position < 0)
         return null;
       if( position > 2)
         return null;
       return LL[ position ];
      }
    public int  num_strings()
     { return 3;
     }
      public static void main( String args[] )
      {System.out.println("MediaList");
      }
   }
