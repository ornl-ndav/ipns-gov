/*
 * File:  StrokeFont.java
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
 * Revision 1.3  2004/07/28 19:59:38  dennis
 * Fixed javadoc error.
 *
 * Revision 1.2  2004/06/18 19:44:44  dennis
 * Moved to Fonts package.
 *
 *
 * Revision 1.1  2004/06/01 03:43:30  dennis
 * Initial version of classes for drawing strings as sequences of
 * line segments, using the "Hershey" fonts.
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Fonts;

import net.java.games.jogl.*;

/**
 *  A StrokeFont object encapsulates the low-level data describing how to draw
 *  individual characters using a sequence of line segments.  Normally 
 *  applications will not use this directly, but will use the StrokeText
 *  class (which in turn uses this class).
 */

abstract public class StrokeFont 
{
  protected short num_chars;
  protected short first_char_code;
  protected short left_edge;
  protected short top;
  protected short cap;
  protected short half;
  protected short base;
  protected short bottom;
  
  protected short char_start[];
  protected short char_width[];
  protected short font_x[];
  protected short font_y[];
  

  /* ---------------------------- CharWidth --------------------------- */
  /**
   *  Get the width of a character in font coordinates.
   *
   *  @param ch  The character whose width is to be calcuated.
   */
  public float CharWidth( char ch )
  {
    int index = (int)ch - first_char_code;
    if ( index < 0 || index >= num_chars ) 
      return 0;

    return char_width[ index ];
  }


  /* ---------------------------- CharHeight --------------------------- */
  /**
   *  Get the nominal height of characters in this font, in font coordinates.
   *
   *  @return The height of an upper case character in this font, from the
   *          base to the top of the characters, in font coordinates.
   */
  public float CharHeight()
  {
    return cap - base;
  }

  
  /* ---------------------------- LeftEdge --------------------------- */
  /**
   *  Get the nominal offset of the LeftEdge of the characters in this
   *  font, in font coordinates.
   *
   *  @return The offset of the left edge of characters in this font, 
   *          in font coordinates.
   */
  public float LeftEdge()
  {
    return left_edge;
  }


  /* ---------------------------- Top --------------------------- */
  /**
   *  Get the nominal top of a box that is high enough to contain the
   *  the characters in this font, in font coordinates.
   *
   *  @return The font coordinates of the top of a box containing the
   *          characters of this font.
   */
  public float Top()
  {
    return top;
  }


  /* ---------------------------- Cap --------------------------- */
  /**
   *  Get the level for the top of the capital letters in this font, 
   *  in font coordinates.
   *
   *  @return the level of the top of capital letters in font coordinates.
   */
  public float Cap()
  {
    return cap;
  }


  /* ---------------------------- Half --------------------------- */
  /**
   *  Get the level for the middle of the capital letters in this font, 
   *  in font coordinates.
   *
   *  @return the level of the middle of capital letters in font coordinates.
   */
  public float Half()
  {
    return half;
  }


  /* ---------------------------- Base --------------------------- */
  /**
   *  Get the level for the Base of the capital letters in this font, 
   *  in font coordinates.
   *
   *  @return the level of the base of capital letters in font coordinates.
   */
  public float Base()
  {
    return base;
  }


  /* ---------------------------- Bottom --------------------------- */
  /**
   *  Get the level for the bottom of the decenders for letters in this font, 
   *  in font coordinates.
   *
   *  @return the level of the bottom of decenders for letters in font
   *          coordinates.
   */
  public float Bottom()
  {
    return bottom;
  }


  /* ----------------------------- DrawCharacter ------------------------- */
  /**
   *  Draw the specified character.
   *
   *  @param  drawable  The OpenGL drawable to which the character is drawn.
   *  @param  ch        The character to draw.
   */
  public void DrawCharacter( GLDrawable drawable, char ch )
  {
    int char_num = (int)ch - first_char_code;
    if ( char_num < 0 || char_num >= num_chars )     // invalid char so just
      return;                                        // ignore it

    GL gl = drawable.getGL();
    int    cur_pt;
    float  x, y;
 
    cur_pt = char_start[char_num]; 
    do
    {
       gl.glBegin( GL.GL_LINE_STRIP );
       x = font_x[cur_pt];
       y = font_y[cur_pt];
       gl.glVertex2f( x, y );
       cur_pt = cur_pt + 1;
       while ( x != 0 )
       {
         x = font_x[cur_pt];
         y = font_y[cur_pt];
         cur_pt = cur_pt + 1;
         if ( x != 0 )
           gl.glVertex2f( x, y );
      };
      gl.glEnd();
  } while ( !( (x==0) && (y==2) ) );
    
  }

}
