/*
 * File:  FileStrokeFont.java
 *
 * Copyright (C) 2004, Dennis Mikkelson
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2004/06/01 03:43:29  dennis
 * Initial version of classes for drawing strings as sequences of
 * line segments, using the "Hershey" fonts.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD;
import  gov.anl.ipns.Util.File.*;

/**
 *  A FileStrokeFont object loads the low-level font data from a file 
 *  containing the "Hershey Fonts".  This class reads the font data from
 *  from one of the text files: cyrilc.txf, gothitt.txf, italicc.txf,
 *  romand.txf, scriptc.txf, gothgbt.txf, greekc.txf, italict.txf,
 *  romans.txf, scripts.txf, gothgrt.txf, greeks.txf, romanc.txf or romant.txf
 *  that hold a simple textual form of the Hershey font data.
 */

public class FileStrokeFont extends StrokeFont
{

  /**
   *  Construct a StrokeFont object by reading the data from the specified
   *  file.
   *
   *  @param file_name  The fully qualified file name for the font file.
   */ 
  public FileStrokeFont( String file_name )
  {
     try 
     {
       TextFileReader tfr = new TextFileReader( file_name );
    
       for ( int i = 0; i < 39; i++ )      // skip file header info
         tfr.read_line();  

       num_chars       = (short)tfr.read_int(); 
       first_char_code = (short)tfr.read_int(); 
       left_edge       = (short)tfr.read_int(); 
       top             = (short)tfr.read_int(); 
       cap             = (short)tfr.read_int(); 
       half            = (short)tfr.read_int(); 
       base            = (short)tfr.read_int(); 
       bottom          = (short)tfr.read_int(); 
       int point_list_size = tfr.read_int(); 

       char_start = new short[ num_chars ];
       char_width = new short[ num_chars ];

       for ( int i = 0; i < num_chars; i++ )
         char_start[i] = (short) tfr.read_int();

       for ( int i = 0; i < num_chars; i++ )
         char_width[i] = (short) tfr.read_int();

       font_x = new short[ point_list_size ]; 
       font_y = new short[ point_list_size ]; 
       for ( int i = 0; i < point_list_size; i++ )
       {
         font_x[i] = (short) tfr.read_int();
         font_y[i] = (short) tfr.read_int();
       }
     }
     catch ( Exception ioe )
     {
       System.out.println("IOException in FileStrokeFont constructor " );
       System.out.println("" + ioe );
       ioe.printStackTrace();
     }
  }

}
