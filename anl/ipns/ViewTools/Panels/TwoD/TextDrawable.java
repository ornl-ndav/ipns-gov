/*
 * File: TextDrawable.java 
 *
 * Copyright (C) 2008, Dennis Mikkelson
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
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */

package gov.anl.ipns.ViewTools.Panels.TwoD;


import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Font;

/**
 * Draw this TextDrawable using the specified graphics context, 
 * and any specified font, color, position alignment and rotation angle.
 * 
 * @param graphics  The graphics context for drawing this image.
 */

public class TextDrawable extends Drawable
{
  /**
   * Enumerated types to specify the vertical and horizontal alignment 
   * of the String that is drawn.  The alignment specifies the location
   * of the specified text position relative to characters that are drawn.
   */
  public static enum Vertical   { TOP, CENTER, BOTTOM };
  public static enum Horizontal { LEFT, CENTER, RIGHT }; 

  private String     text;
  private Font       font = new Font("Serif", Font.BOLD, 20 );
  private Vertical   v_align = Vertical.BOTTOM;
  private Horizontal h_align = Horizontal.LEFT;
  
  
  /**
   * Construct a TextDrawable for the specified String.  By default a
   * BOLD "Serif" 20 point font is used.
   *
   * @param text  The character string to be drawn.
   */
  public TextDrawable( String text )
  {
    this.text = text;
  }
  
  
  /**
   * Set a new font to be used to draw the string.  The font contains
   * information about the style of characters to use, as well as the
   * size of the characters.
   * 
   * @param font  The new Font to use for drawing the string.
   */
  public void setFont( Font font )
  {
    this.font = font;
  }
  
  
  /**
   * Get the font that is being used to draw this String.
   * 
   * @return A reference to the font used for this TextDrawable.
   */
  public Font getFont()
  {
    return this.font;
  }
  
  
  /**
   * Set the alignment of the string relative to the point specified
   * as it's location.
   * 
   * @param h_align  The alignment of the string in the horizontal direction,
   * @param v_align  The alignment of the string in the vertical direction.
   */
  public void setAlignment( Horizontal h_align, Vertical v_align )
  {
    this.v_align = v_align;
    this.h_align = h_align;
  }
  
  
  /**
   * Get the currently set horizontal alignement.
   * 
   * @return An enumerated value of type Horizontal, specifying the
   *         current horizontal alignment for this TextDrawable.
   */
  public Horizontal getHorizontalAlignment()
  {
    return this.h_align;
  }
  
  
  /**
   * Get the currently set vertical alignement.
   * 
   * @return An enumerated value of type Vertical, specifying the
   *         current vertical alignment for this TextDrawable.
   */
   public Vertical getVerticalAlignment()
  {
    return this.v_align;
  }
   
   
   /**
    * Draw this TextDrawable using the specified graphics context, 
    * and any specified font, color, position and rotation angle.
    * 
    * @param graphics  The graphics context for drawing the String.
    */
  public void draw(Graphics2D graphics)
  {
    graphics = setAttributes( graphics );  // Use super class method 
                                           // to set up the color, etc. and
                                           // get a new graphics context
                                           // with those attributes set.

    graphics.setFont( font );              // Set the specified font 

    FontMetrics metrics = graphics.getFontMetrics();  // Find the size of 
                                                      // the String in the
    int w = metrics.stringWidth( text );              // currently set Font
    if ( h_align == Horizontal.LEFT )                 // and calculate an
      w = 0;                                          // offset for the
    else if ( h_align == Horizontal.CENTER )          // String using the
      w = w/2;                                        // horizontal and
                                                      // vertical alignment
    int h = metrics.getHeight();                      // values.
    int v_offset = 1;
    if ( v_align == Vertical.BOTTOM )
      h = -v_offset;
    else if ( v_align == Vertical.CENTER )
      h = h/2 - v_offset;
    else if ( v_align == Vertical.TOP )
      h = h - v_offset;

    graphics.translate( -w, -h );    // Shift String to align it relative to 
                                     // the currently specified position.
    graphics.scale(1, -1);           // Reflect string to a right hand 
                                     // coordinate system, the draw it.

    graphics.drawString( text , 0, 0 );
   
    graphics.dispose();              // get rid of the new 
                                     // graphics context 
  }

}
