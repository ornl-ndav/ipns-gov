/*
 * File: TwoD_JPanel.java
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


import java.util.*;
import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;

/**
 * This class maintains and draws a collection of IDrawable objects.
 * It extends a JPanel, and must be added to a JFrame or other 
 * container that is on the display.  IDrawable objects may be 
 * added to or removed from the panel using the AddObject() and
 * RemoveObject() methods.  The draw() method is used to request that
 * the objects be redrawn.
 *  
 * @author Dennis Mikkelson
 */
public class TwoD_JPanel extends JPanel
{
  static final long serialVersionUID = 1;  // Keep the compiler happy

  private Vector<IDrawable> object_list = new Vector<IDrawable>();
  private Color background = Color.LIGHT_GRAY;
  private Color foreground = Color.BLACK;
  
  /**
   * Add the specified object to the list of objects that are drawn
   * in this panel.  The object will not actually appear in the 
   * panel until the draw() method is called.
   * 
   * @param drawable The new object to be drawn.
   */
  public void AddObject( IDrawable drawable )
  {
    object_list.add( drawable );
  }
  
  /**
   * Remove the specified object from the list of objects that are
   * drawn in this panel.  The object will not actually disappear from
   * the panel until the draw() method is called.
   * 
   * @param drawable The object to be removed from the display.
   */
  public void RemoveObject( IDrawable drawable )
  {
    object_list.remove(drawable);
  }
  
  /**
   * Remove ALL objects from the list of objects that are drawn in
   * this panel.
   */
  public void Clear()
  {
    object_list.clear();
  }
  
  /**
   * Request that this TwoD_JPanel be redrawn.
   */
  public void draw()
  {
    repaint();
  }
  
  /**
   * Set the background color in this TwoD_JPanel panel.
   * 
   * @param background The new color to use for the background.
   */
  public void setBackgroud( Color background )
  {
    this.background = background;
  }
  
  /**
   * Set the default color that will be used for drawing objects.
   * 
   * @param foreground The new default color for objects.
   */
  public void setForegroud( Color foreground )
  {
    this.foreground = foreground;
  }
  
  /**
   * This method should NOT be called by application code directly;
   * it is called automatically by the system when the panel is first
   * displayed, is resized or uncovered.  A call to this method is
   * also scheduled to made at some future time when the draw() method
   * calls repaint().
   * 
   * @param graphics  The graphics context that is used for drawing.
   */
  public void paintComponent( Graphics graphics )
  {
                              // copy the graphics context so that changes
                              // that might be made by IDrawable objects 
                              // don't have side effects
    Graphics2D g2d = (Graphics2D)graphics.create(); 
    
                              // clear the panel to the background color
                              // before drawing the IDrawable objects
    int width  = getWidth();
    int height = getHeight();                                      
    g2d.setBackground( background );
    g2d.clearRect( 0, 0, width, height );
    
    g2d.setColor( foreground );   // Set the default foreground color.
    
    g2d.translate(0, height);     // Apply transformations to place (0,0)
    g2d.scale(1,-1);              // in the lower left corner instead of
                                  // the upper left corner.  NOTE: This
                                  // switches to a right hand coordinate
                                  // system.
    
                                  // Step through the list of IDrawable
                                  // objects and call each one's draw()
                                  // method to actually draw it.
    for (int i = 0; i < object_list.size(); i++ )
      object_list.elementAt(i).draw(g2d);
    
    g2d.dispose();                // Dispose of the new graphics context
                                  // now that we are done with it.
  }
   
}
