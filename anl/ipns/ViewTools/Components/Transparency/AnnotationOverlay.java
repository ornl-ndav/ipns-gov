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
 *  Revision 1.2  2003/06/05 22:08:33  dennis
 *   (Mike Miller)
 *   - Corrected resize capability
 *   - Overlay now an AnnotationJPanel to allow arrow functionality
 *   - Added getFocus() to fix keylistener problems and addAnnotation() to allow
 *     internal classes (programmers) to add annotations.
 *   - Added private class Note and MiniViewer
 *
 *  Revision 1.1  2003/05/29 14:29:20  dennis
 *  Initial version, current functionality does not support
 *  annotation editing or annotation deletion. (Mike Miller)
 * 
 */

/* *************************************************************
 * *********Basic controls for the Annotation Overlay***********
 * *************************************************************
 * Keyboard Event    * Mouse Event       * Action              *
 ***************************************************************
 * press N (note)    * Press/Drag mouse  * add annotation      *
 * none              * Double click      * clear last note     *
 * press A (all)     * Double click      * clear all notes     *
 ***************************************************************
 * Important: 
 * All keyboard events must be done prior to mouse events.
 */ 

package DataSetTools.components.View.Transparency;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.util.*; 
import java.lang.Math;

import DataSetTools.components.View.TwoD.*;
import DataSetTools.components.View.Cursor.*;

/**
 * This class allows a user to write comments near a region on the 
 * IAxisAddible2D component. 
 */
public class AnnotationOverlay extends OverlayJPanel 
{
   private AnnotationJPanel overlay;      // panel overlaying the center jpanel
   private IAxisAddible2D component;      // component being passed
   private Vector notes;                  // all annotations
   private AnnotationOverlay this_panel;  // used for repaint by SelectListener 
   private Rectangle current_bounds;
   private Color reg_color;
   
  /**
   * Constructor creates OverlayJPanel with a transparent AnnotationJPanel that
   * shadows the region specified by the getRegionInfo() of the IAxisAddible2D
   * interface.
   *
   *  @param  component - must implement IAxisAddible2D interface
   */ 
   public AnnotationOverlay(IAxisAddible2D iaa)
   {
      super();
      this.setLayout( new GridLayout(1,1) );
      
      overlay = new AnnotationJPanel();
      component = iaa;
      notes = new Vector();      
      this_panel = this;
      reg_color = Color.black;
       
      this.add(overlay); 
      overlay.setOpaque(false); 
      overlay.addActionListener( new NoteListener() );  
      
      overlay.requestFocus();           
   }
   
  /**
   * This method sets the color of all annotations. Initially set to black.
   *
   *  @param  color
   */
   public void setTextColor( Color c )
   {
      reg_color = c;
      this_panel.repaint();
   }
   
  /**
   * This method requests focus from the parent. When called by the parent,
   * this gives keyboard focus to the AnnotationJPanel.
   */
   public void getFocus()
   {
      overlay.requestFocus();
   }      

  /**
   * Allows toplevel components to add annotations. To use this method, be sure
   * the path to the Line class is in your import statements.
   * Path: DataSetTools/components/View/Cursor/Line.java
   *
   *  @param  a_note
   *  @param  placement
   */
   public void addAnnotation( String a_note, Line placement )
   {
      notes.add( new Note( a_note, placement, current_bounds ) );
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

      // resize center "overlay" to size of center jpanel
      overlay.setBounds( current_bounds );
      // color of all of the annotations.
      g2d.setColor(reg_color);
      int ox = overlay.getLocation().x;
      int oy = overlay.getLocation().y;
     /* To "move" the annotations, an x & y scale had to be made. This
      * simply takes the width of the current rectangle/scale rectangle.
      */ 
      float xfactor = 0;
      float yfactor = 0;
      Note note;
      for( int comment = 0; comment < notes.size(); comment++ )
      {
         note = (Note)notes.elementAt(comment);
         xfactor = (float)current_bounds.getWidth()/
	           (float)note.getScale().getWidth();
	           
         yfactor = (float)current_bounds.getHeight()/
	           (float)note.getScale().getHeight();
                  
         Point at = note.getLocation();
	 String snote = note.getText();
	 
	 g2d.drawLine( (int)(note.getLine().getP1().x * xfactor) + ox, 
	               (int)(note.getLine().getP1().y * yfactor) + oy,
	               (int)(note.getLine().getP2().x * xfactor) + ox, 
		       (int)(note.getLine().getP2().y * yfactor) + oy );
	 
	 g2d.drawString( snote, (int)((at.x * xfactor) + ox), 
	                        (int)((at.y * yfactor) + oy));
	 /*
         System.out.println("X/Y factor " + xfactor + "/" + yfactor); 
	 System.out.println("PrePixel: (" + (at.x + ox) + "," + 
	                                    (at.y + oy) + ")" ); 
	 System.out.println("PostPixel: (" + (int)((at.x + ox) * xfactor) +
	                    "," + (int)((at.y + oy) * yfactor) + ")" );
	 */
      }     
   } // end of paint()

  /*
   * NoteListener listens to action events generated by the AnnotationJPanel.
   */
   private class NoteListener implements ActionListener
   {
      public void actionPerformed( ActionEvent ae )
      {	
         String message = ae.getActionCommand(); 
         // clear all notes from the vector
         if( message.equals( AnnotationJPanel.RESET_NOTE ) )
         {
	    //System.out.println("Clear all selected" ); 
	    if( notes.size() > 0 )
	       notes.clear(); 
	    else
	       System.out.println("No annotations created");          
	 }
	 // remove the last note from the vector
         else if( message.equals( AnnotationJPanel.RESET_LAST_NOTE ) )
         {
	    //System.out.println("Clear last selected" ); 
	    if( notes.size() > 0 )
	       notes.removeElementAt(notes.size() - 1); 
	    else
	       System.out.println("No annotations created");	      
	 }	 
	 else if( message.equals( AnnotationJPanel.NOTE_REQUESTED ) )
	 {	
	    //****************** this code here may change ********************
	    // this is here to allow the pop-up textfield to adjust if the
	    // viewer itself is moved.
	    Container viewer = this_panel.getParent().getParent().
			  getParent().getParent().getParent(); 
            //*****************************************************************
	    // all new stuff is created here otherwise the reference is
	    // maintained, which adjusts all notes to the same location...BAD
	    Line temp = ((LineCursor)
	                      overlay.getLineCursor()).region();
	    Point p1 = new Point( temp.getP1() );
	    Point p2 = new Point( temp.getP2() );
	    Line newline = new Line( p1, p2 );
	    new MiniViewer( newline, viewer );
	 }
	 this_panel.repaint();
      }
   }
  
  /*
   * MiniViewer is the textfield the pops up when a user adds a new annotation.
   * This class includes its own listener to the textfield.
   */ 
   private class MiniViewer
   {
      private JFrame frame;
      private JTextField text;
      private Line region;
      public MiniViewer( Line acline, Container viewer )
      {
         region = acline;
      
         frame = new JFrame();
         frame.setBounds(0,0,150,60);
         frame.getContentPane().setLayout( new GridLayout(1,1) );
         text = new JTextField(20);
         text.addKeyListener( new TextFieldListener() );
         frame.getContentPane().add(text); 
	 
	 Point place = new Point(region.getP2()); 
	 place.x = place.x + (int)viewer.getLocation().getX();
	 place.y = place.y + (int)viewer.getLocation().getY();
	 frame.setLocation(place);
	 frame.setVisible(true);
	 text.requestFocus();		   
      }
            
     /*
      * This internal class of the MiniViewer listens for the "enter" key to 
      * be pressed. Once pressed, information is then passed on to the Note
      * class and the MiniViewer is set to setVisible(false).
      */ 
      class TextFieldListener extends KeyAdapter
      {
         public void keyTyped( KeyEvent e )
         {
     	    if( e.getKeyChar() == KeyEvent.VK_ENTER )
     	    {
       	       //JTextField text = (JTextField)e.getComponent();
     	       this_panel.addAnnotation( text.getText(), region );
     	       frame.setVisible(false);
	       frame.dispose();  // since a new viewer is made each time,
	                         // dispose of the old one.
     	       this_panel.getParent().getParent().repaint();
     	       //System.out.println("KeyTyped " + e.getKeyChar() );	    
     	    }
         }
      }	      
   } // end of MiniViewer
     
  /*
   * This class creates an internal datastructure to easily group a string,
   * its location to be displayed, and the bounds it was created in.
   */
   private class Note
   {
      private JTextField textfield; // actual note being drawn
      private Line arrow;           // location to draw this note
      private Rectangle scale;      // the bounds of the overlay when this 
                                    // note was created
     /*
      * This constructor takes in three parameters, a string text which is 
      * the actual message, a line which has a beginning point at the region
      * of interest and an ending point where the annotation will be drawn, and
      * a third parameter rectangle, which represents the bounds in which this
      * annotation was created.
      */ 
      public Note(String t, Line l, Rectangle s)
      {
     	 textfield = new JTextField(t);
     	 arrow = new Line(l.getP1(), l.getP2());
	 scale = new Rectangle(s);
      }
     
     /*
      * @return the actual note itself
      */ 
      public String getText()
      {
     	 return textfield.getText();
      }
      
     /*
      * @return the location the annotation will be drawn at.
      */ 
      public Point getLocation()
      {
     	 return arrow.getP2();
      }
     
     /*
      * @return the rectangle bounds this annotation was created in.
      */ 
      public Rectangle getScale()
      {
         return scale;
      }
     
     /*
      * @return the textfield containing the string. A textfield was chosen
      *         instead of just a string because when editing is allowed,
      *         a textfield will be required anyways.
      */ 
      public JTextField getTextField()
      {
         return textfield;
      }
     
     /*
      * @return the line representing the arrow from p1 ( the point near
      *         the intended area ) to p2 ( the location of the annotation ).
      *         This also allows the paint to draw a line from p1 to p2. 
      */ 
      public Line getLine()
      {
         return arrow;
      }
   }
   
}
