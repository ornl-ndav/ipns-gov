/*
 * File: TextFileReader.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2001/11/09 18:50:39  dennis
 *  Utility to simplify reading numerical and string values from an
 *  ordinary ASCII file.
 *
 *
 */

package DataSetTools.util;
import java.io.*;

/**
 *   This class supports reading of Strings, floats, ints etc. from an
 *   ordinary text file.  In addition to methods to read each of the 
 *   basic data types from the file, there is an "unread" method that 
 *   restores the last non-blank item that was read.  Error handling and
 *   end of file detection are done using exceptions.
 */
public class TextFileReader 
{
  public static final String EOF = "End of file";  // string used to construct 
                                                   // the eof exception 
  public static final int    BUFFER_SIZE = 200;    // Maximum number of chars 
                                                   // in a String or one line
  private BufferedReader in;


  /* -------------------------- Constructor -------------------------- */
  /**
   *  Construct a TextFileReader to read from the specified file.  The
   *  constructor will throw an exception if the file can't be opened.  The
   *  other methods of this class should not be used if the file can't be
   *  opened.
   *
   *  @param file_name  The fully qualified file name.
   */
  public TextFileReader( String file_name ) throws IOException
  {
    FileInputStream  file_stream;
    try
    {
      file_stream = new FileInputStream( file_name );
      in = new BufferedReader( new InputStreamReader( file_stream ) );
      in.mark(2);
    }
    catch ( IOException e )
    {
      file_stream = null;
      throw e;
    }
  }

  /* -------------------------- read_line ---------------------------- */
  /**
   *  Read one line from the file, starting at the current position in the
   *  file.  
   *
   *  @return The remaining characters on the current line of the file if
   *          there is one.
   *
   *  @throws IOException with the message TextFileReader.EOF, if the end
   *          of file has been reached.
   */
  public String read_line() throws IOException
  {
    try
    {
      in.mark(BUFFER_SIZE);
      String s = in.readLine();
      if ( s == null )
        throw new IOException( EOF );
      return s;
    }
    catch ( IOException e )
    {
      throw e;
    }
  }
 
  /* -------------------------- skip_blanks ---------------------------- */
  /**
   *  Skip whitespace characters in the file, starting at the current 
   *  position, stopping at the first non-blank character.  The first
   *  non-blank character encountered is placed back in the file and 
   *  will be the next character read. 
   *
   *  @throws IOException with the message TextFileReader.EOF, if the end
   *          of file has been reached.
   */
  public void skip_blanks() throws IOException
  {
    int  ch;

    try
    {
      in.mark(2);
      ch = in.read(); 
      while ( Character.isWhitespace( (char)ch ) )
      {
        in.mark(2);
        ch = in.read(); 
      }

      if ( ch < 0 )                                         // -1 is EOF
        throw new IOException( EOF );

      in.reset();
    }
    catch ( IOException e )
    {
      throw e;
    }
  }


  /* -------------------------- read_String ---------------------------- */
  /**
   *  Read a sequence of non-whitespace characters from the file, starting at 
   *  the current position in the file.  If the current position in the
   *  file is a whitespace character, whitespace characters will be skipped
   *  until the first non-whitespace character is encountered.  After reading
   *  the squences of non-whitespace characters, the following whitespace 
   *  character is read and NOT putback in the stream. 
   *
   *  @return The first non-blank sequence of characters encountered, 
   *          starting from the current position.
   *
   *  @throws IOException with the message TextFileReader.EOF, if the end
   *          of file has been reached.
   */
  public String read_String() throws IOException
  {
    byte buffer[] = new byte[BUFFER_SIZE];
    int  ch;
    int  n = 0;

    try
    {
      skip_blanks();

      n = 0;
      ch = in.read(); 
      while ( !Character.isWhitespace( (char)ch ) && 
               ch >= 0                            &&         // -1 is EOF
               n  < BUFFER_SIZE                       )
      {
        buffer[n] = (byte)ch;
        n++;
        ch = in.read();
      }
       
      if ( n > 0 )
        return new String( buffer, 0, n );
      else
        throw new IOException( EOF );
    }
    catch ( IOException e )
    {
      throw e;
    }
  }

  /* -------------------------- read_int ---------------------------- */
  /**
   *  Read a sequence of non-whitespace characters from the file, starting at
   *  the current position in the file and construct an int value from the
   *  the characters, if possible.
   *
   *  @return The int value represented by the next sequence of non-blank
   *          characters in the file.
   *
   *  @throws IOException with the message TextFileReader.EOF, if the end
   *          of file has been reached, or a NumberFormatException, if the
   *          characters don't represent an int.
   */
  public int read_int() throws Exception
  {
    try
    {
      String s = read_String();
      int val = (new Integer( s )).intValue();
      return val;
    }
    catch ( Exception e )
    {
      throw e;
    }
  }

  /* -------------------------- read_float ---------------------------- */
  /**
   *  Read a sequence of non-whitespace characters from the file, starting at
   *  the current position in the file and construct a float value from the
   *  the characters, if possible. 
   *
   *  @return The float value represented by the next sequence of non-blank
   *          characters in the file.
   *
   *  @throws IOException with the message TextFileReader.EOF, if the end
   *          of file has been reached, or a NumberFormatException, if the
   *          characters don't represent a float.
   */
  public float read_float() throws Exception
  {
    try 
    {
      String s = read_String();
      float val = (new Float( s )).floatValue();
      return val;
    }
    catch ( Exception e )
    {
      throw e;
    }
  }


  /* -------------------------- read_double ---------------------------- */
  /**
   *  Read a sequence of non-whitespace characters from the file, starting at
   *  the current position in the file and construct a double value from the
   *  the characters, if possible.
   *
   *  @return The double value represented by the next sequence of non-blank
   *          characters in the file.
   *
   *  @throws IOException with the message TextFileReader.EOF, if the end
   *          of file has been reached, or a NumberFormatException, if the
   *          characters don't represent a double.
   */
  public double read_double() throws Exception
  {
    try
    {
      String s = read_String();
      double val = (new Double( s )).doubleValue();
      return val;
    }
    catch ( Exception e )
    {
      throw e;
    }
  }


  /* -------------------------- read_boolean ---------------------------- */
  /**
   *  Read a sequence of non-whitespace characters from the file, starting at
   *  the current position in the file and construct a boolean value from the
   *  the characters.
   *
   *  @return The boolean value represented by the next sequence of non-blank
   *          characters in the file.  The value is "true" if the sequence of
   *          non-blank characters matches "true" ignoring case and is false
   *          otherwise.
   *
   *  @throws IOException with the message TextFileReader.EOF, if the end
   *          of file has been reached.
   */
  public boolean read_boolean() throws IOException
  {
    try
    {
      String s = read_String();
      boolean val = (new Boolean( s )).booleanValue();
      return val;
    }
    catch ( IOException e )
    {
      throw e;
    }
  }


  /* ---------------------------- read_char ------------------------------ */
  /**
   *  Read the next character from the file, including blanks.
   *
   *  @return The next character in the file.
   *
   *  @throws IOException with the message TextFileReader.EOF, if the end
   *          of file has been reached.
   */
  public char read_char() throws IOException
  {
    try
    {
      in.mark(2);
      return (char)(in.read());
    }
    catch ( IOException e )
    {
      throw e;
    }
  }

  /* ----------------------------- unread ------------------------------ */
  /**
   *  Put the last float, double, int, boolean, char or String read from 
   *  the file, back into the file so that it can be read again.  
   *
   *  @throws IOException if something goes wrong when trying to reset the
   *          BufferedReader.
   */
  public void unread() throws IOException
  {
    try
    {
      in.reset();
    }
    catch ( IOException e )
    {
      throw e;
    }
  }

 
  /* --------------------------  main  ---------------------------------- */
  /*
   *  Main program for testing purposes only.
   */
  public static void main( String args[] )
  {
    String         val   = "";
    String         line  = "";
    float          f_num = 0;
    int            i_num = 0;
    char           ch    = 0;
    boolean        b     = false;
    TextFileReader f     = null;
    try
    {
      f = new TextFileReader("my_file.dat");
      line = f.read_line();
      System.out.println("First line is " + line );
      f.unread();
      line = f.read_line();
      System.out.println("First line again is " + line );

      b = f.read_boolean();
      System.out.println("boolean val: " + b );
      b = f.read_boolean();
      System.out.println("boolean val: " + b );
    
      f_num = f.read_float();
      System.out.println("float value is " + f_num );
      f.unread();
      f_num = f.read_float();
      System.out.println("float value again is " + f_num );

      i_num = f.read_int();
      System.out.println("int value is " + i_num );
      f.unread();
      i_num = f.read_int();
      System.out.println("int value again is " + i_num );

      f.skip_blanks();
      ch = f.read_char();
      System.out.println("char value is " + ch );
      f.unread();
      ch = f.read_char();
      System.out.println("char value again is " + ch );
    }
    catch ( Exception e )
    {
      System.out.println("EXCEPTION: " + e );
    }
 

    try 
    {
      for ( int i = 0; i < 8; i++ )
      {
        val = f.read_String();
        System.out.println("Val: " + val );
        f.unread();
        val = f.read_String();
        System.out.println("Val again: " + val );

        line = f.read_line();
        System.out.println("Line |" + line + "| length = " + line.length());
      }
    }
    catch ( Exception e )
    {
      System.out.println("EXCEPTION: " + e );
    }
  }

} 
