/*
 * File: AnnotationOverlay.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2003/05/29 14:29:20  dennis
 *  Initial version, current functionality does not support
 *  annotation editing or annotation deletion. (Mike Miller)
 *
 */

package DataSetTools.components.View.Transparency;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.util.*; 
import java.lang.Math;

import DataSetTools.components.View.TwoD.*;

public class AnnotationOverlay extends OverlayJPanel 
{
   private JPanel overlay;                // panel overlaying the center jpanel
   private IAxisAddible2D component;      // component being passed
   private Vector notes;                  // all annotations
   private AnnotationOverlay this_panel;  // used for repaint by SelectListener 
   private Rectangle init_bounds;
   private Rectangle current_bounds;
   private int paint_num;
   private Color reg_color;
   
  /**
   * Constructor creates OverlayJPanel with a transparent JPanel that
   * shadows the region specified by the getRegionInfo() of the IAxisAddible2D
   * interface.
   *
   *  @param  component - must implement IAxisAddible2D interface
   */ 
   public AnnotationOverlay(IAxisAddible2D iaa)
   {
      super();
      this.setLayout( new GridLayout(1,1) );
      
      overlay = new JPanel();
      component = iaa;
      notes = new Vector();      
      this_panel = this;
      paint_num = 0;
      reg_color = Color.black;
       
      this.add(overlay); 
      overlay.setOpaque(false);     
      overlay.addMouseListener( new ClickListener() );              
   }
   
  /**
   * This method sets the color of all annotations.
   *
   *  @param  color
   */
   public void setTextColor( Color c )
   {
      reg_color = c;
   }   

  /**
   * Overrides paint method. This method will paint the annotations.
   *
   *  @param  graphic
   */
   public void paint(Graphics g) 
   {  
      Graphics2D g2d = (Graphics2D)g; 
      current_bounds = component.getRegionInfo();  // current size of center
      // get the initial size of center jpanel, only do this the first time.
      if( paint_num == 0 )
         init_bounds = current_bounds;
      // resize center "overlay" to size of center jpanel
      overlay.setBounds( current_bounds );
      // color of all of the annotations.
      g2d.setColor(reg_color);
      int ox = overlay.getLocation().x;
      int oy = overlay.getLocation().y;
     /* To "move" the annotations, an x & y scale had to be made. This
      * simply takes the width of the current rectangle/scale rectangle.
      * The factors are not quite accurate, so a power function is used
      * to make them more accurate.
      */ 
      float xfactor = 0;
      float yfactor = 0;
      for( int comment = 0; comment < notes.size(); comment++ )
      {
         xfactor = (float)current_bounds.getWidth()/
	       (float)((Note)notes.elementAt(comment)).getScale().getWidth();
	 xfactor = (float)Math.pow(xfactor,.82); // xfactor not quite accurate
	           
         yfactor = (float)current_bounds.getHeight()/
	       (float)((Note)notes.elementAt(comment)).getScale().getHeight();
	 yfactor = (float)Math.pow(yfactor,.89); // yfactor not quite accurate
               
	 //System.out.println("X/Y factor " + xfactor + "/" + yfactor);     
         Point at = ((Note)notes.elementAt(comment)).getLocation();
	 String note = ((Note)notes.elementAt(comment)).getText();
	 
	 g2d.drawString( note, (int)((at.x + ox) * xfactor), 
	                       (int)((at.y + oy) * yfactor) );
      }     
      paint_num++;  // only needed to get init_bounds
   } // end of paint()

  /*
   * ClickListener listens for mouse clicks on the overlay field
   * Currently the textfield that pops up can have a button that accompanies
   * it. However, this was removed, and the user can add by pressing "Return".
   */
   private class ClickListener extends MouseAdapter
   {
      private JFrame frame;
      private JTextField text;
      private Point current;
      //private JButton addbutton;
      
      public ClickListener()
      {
         frame = new JFrame();
	 frame.setBounds(0,0,150,60);
	 // if add button back in, replace these next two lines
	 //frame.getContentPane().setLayout( new GridLayout(1,2) );
	 frame.getContentPane().setLayout( new GridLayout(1,1) );
	 text = new JTextField( 20 );
	 text.addKeyListener( new TextListener() );
	 //addbutton = new JButton("Add");
	 //addbutton.setPreferredSize( new Dimension( 0, 40 ) );
	 //addbutton.addActionListener( new AddListener() );
	 frame.getContentPane().add(text);
	 //frame.getContentPane().add(addbutton);
      }  
      
      public void mouseClicked (MouseEvent e)
      {
         //System.out.println("Current = " + e.getPoint() );
	 
         if ( e.getClickCount() == 2 ) 
	 {	    
	    current = e.getPoint();
	    text.setText("");
	    Container viewer = this_panel.getParent().getParent().
			  getParent().getParent().getParent(); 
	    Point place = new Point(current); 
	    place.x = place.x + (int)viewer.getLocation().getX();
	    place.y = place.y + (int)viewer.getLocation().getY();
	    frame.setLocation(place);
	    frame.setVisible(true);
	    text.requestFocus();
	 }	        	    
      } 
/* here for the button 
      private class AddListener implements ActionListener
      {
         public void actionPerformed( ActionEvent e )
         {
            System.out.println("here in actionPerformed");
	    
            Note postit = new Note( text.getText(), current, current_bounds );
	    notes.add(postit);
	    frame.setVisible(false);
	    //this_panel.revalidate();
	    this_panel.getParent().getParent().repaint();
	 }	        	    
      } 
*/     
     /*
      * This class is used by the textfield to pass the "note" to paint()
      */ 
      private class TextListener extends KeyAdapter
      {
         public void keyTyped( KeyEvent e )
	 {
	    if( e.getKeyChar() == KeyEvent.VK_ENTER )
	    {
	       Note postit = new Note( text.getText(), current, current_bounds );
	       notes.add(postit);
	       frame.setVisible(false);
	       this_panel.getParent().getParent().repaint();
	       //System.out.println("KeyTyped " + e.getKeyChar() );
	    }
	 }
      }     
   
   } // end ClickListener  

  /*
   * This class creates an internal datastructure to easily group a string,
   * its location to be displayed, and the rectangle it was created in.
   */
   private class Note
   {
      private String text;         // actual note being drawn
      private Point location;      // location to draw this note
      private Rectangle scale;     // the bounds of the overlay when this 
                                   // note was created
      
      public Note(String t, Point l, Rectangle s)
      {
     	 text = t;
     	 location = l;
	 scale = s;
      }
      
      public String getText()
      {
     	 return text;
      }
      
      public Point getLocation()
      {
     	 return location;
      }
      
      public Rectangle getScale()
      {
         return scale;
      }
   }
   
}
