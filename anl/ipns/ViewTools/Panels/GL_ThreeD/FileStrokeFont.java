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
 * Revision 1.2  2004/06/01 22:51:25  dennis
 * Now includes main program that writes the font data to a java
 * source file, so that fonts can be used without needing the
 * original text file containing the font data.
 *
 * Revision 1.1  2004/06/01 03:43:29  dennis
 * Initial version of classes for drawing strings as sequences of
 * line segments, using the "Hershey" fonts.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD;
import  gov.anl.ipns.Util.File.*;
import  java.io.*;

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


  public static void main( String args[] )
  {
    if ( args.length < 3 )
      System.out.println("ERROR: need input file, output path and output " +
                         "font name as parameters" );

    FileStrokeFont font = new FileStrokeFont( args[0] );

    PrintStream ps = null;
    try
    {
      File out_file      = new File( args[1]+args[2]+".java" );
      OutputStream os = new FileOutputStream( out_file );
      ps = new PrintStream( os );
    }
    catch ( Exception e )
    {
      System.out.println("WARNING: Couldn't open output file: " + args[1] );
    }

    ps.println("package gov.anl.ipns.ViewTools.Panels.GL_ThreeD;");
    ps.println();
    ps.println("public class " + args[2] + " extends StrokeFont");
    ps.println("{");
                                            // dump out the char_start array
    ps.println(" private static short my_char_start[] = {");
    for ( int i = 0; i < font.num_chars; i++ )
    {
      ps.print(" " + font.char_start[i] );
      if ( i < font.num_chars - 1 )
        ps.print( "," );
      if ( i > 0 && i % 13 == 0 )
        ps.println();
    }
    ps.println(" };");
    ps.println();

                                             // dump out the char_width array
    ps.println(" private static short my_char_width[] = {");
    for ( int i = 0; i < font.num_chars; i++ )
    {
      ps.print(" " + font.char_width[i] );
      if ( i < font.num_chars - 1 )
        ps.print( "," );
      if ( i > 0 && i % 18 == 0 )
        ps.println();
    }
    ps.println(" };");
    ps.println();

                                             // dump out the font_x array
    int n_points = font.font_x.length;
    ps.println(" private static short my_font_x[] = {");
    for ( int i = 0; i < n_points; i++ )
    {
      ps.print(" " + font.font_x[i] );
      if ( i < n_points - 1 )
        ps.print( "," );
      if ( i > 0 && i % 18 == 0 )
        ps.println();
    }
    ps.println(" };");
    ps.println();

                                             // dump out the font_x array
    ps.println(" private static short my_font_y[] = {");
    for ( int i = 0; i < n_points; i++ )
    {
      ps.print(" " + font.font_y[i] );
      if ( i < n_points - 1 )
        ps.print( "," );
      if ( i > 0 && i % 18 == 0 )
        ps.println();
    }
    ps.println(" };");
    ps.println();
                                            // now make the constructor
    ps.println("  public " + args[2] + "()");
    ps.println("  {");
    ps.println("    num_chars       = " + font.num_chars +";" );
    ps.println("    first_char_code = " + font.first_char_code +";" );
    ps.println("    left_edge       = " + font.left_edge + ";" );
    ps.println("    top    = " + font.top + ";" );
    ps.println("    cap    = " + font.cap + ";" );
    ps.println("    half   = " + font.half + ";" );
    ps.println("    base   = " + font.base + ";" );
    ps.println("    bottom = " + font.bottom + ";" );
    ps.println("    char_start = my_char_start;" );
    ps.println("    char_width = my_char_width;" );
    ps.println("    font_x     = my_font_x;" );
    ps.println("    font_y     = my_font_y;" );
    ps.println("  }");

    ps.println("}");
    ps.println();
 
    ps.close();
  }
}
