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
 *  Revision 1.5  2003/06/09 22:32:44  dennis
 *  - Added setEventListening(false) method call for ColorScaleImage to
 *    ignore keyboard/mouse events on the AnnotationEditor.
 *  - Fixed ArrayOutOfBounds exception when the ColorScaleImage
 *    extreme values are passed.
 *  - Added space between line and text in paint method.
 *    (Mike Miller)
 *
 *  Revision 1.4  2003/06/09 14:45:29  dennis
 *  - Added a ColorScaleImage to the AnnotationEditor to allow the
 *    user to change text and line colors.
 *  - Replaced private data member reg_color which controlled color
 *    for both the text and line of the annotation with two variables,
 *    text_color and line_color, to support functionality introduced
 *    by the ColorScaleImage.
 *  - Added Shift-UP/DOWN/LEFT/RIGHT keymaps to allow for movement
 *    of the anchor of the annotation.
 *  - Added static method help() to display commands via the HelpMenu.
 *    (Mike Miller)
 *
 *  Revision 1.3  2003/06/06 18:47:48  dennis
 *  (Mike Miller)
 *  - Added private class AnnotationEditor to display all annotations.
 *  - Added method editAnnotation() to call creation of AnnotationEditor
 *  - Added autopositioning of the arrow from the annotation.
 *  - Tied together annotation removal via double click with new annotation
 *    removal featured in AnnotationEditor.
 *
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

/* *****************************************************************************
 * ******************Basic controls for the Annotation Overlay******************
 * *****************************************************************************
 * Keyboard Event    * Mouse Event       * Action                              *
 *******************************************************************************
 * press N (note)    * Press/Drag mouse  * add annotation                      *
 * none              * Double click      * clear last note                     *
 * press A (all)     * Double click      * clear all notes                     *
 * PRESS RETURN      * NONE              * UPDATE NOTES, CLEAR IF EMPTY STRING *
 * NONE              * CLICK ON REFRESH  * UPDATE NOTES, CLEAR IF EMPTY STRING *
 * NONE              * CLICK ON CLOSE    * CLOSES WINDOW                       *
 *******************************************************************************
 * ALL EVENTS IN UPPERCASE ARE DONE TO THE AnnotationEditor AFTER IT POPS UP.
 * Important: 
 * All keyboard events must be done prior to mouse events.
 */ 

package DataSetTools.components.View.Transparency;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.util.*; 
import java.lang.Math;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.image.IndexColorModel;

import DataSetTools.components.View.TwoD.*;
import DataSetTools.components.View.Cursor.*;
import DataSetTools.components.image.IndexColorMaker;
import DataSetTools.components.ui.ColorScaleImage;

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
   private Color line_color;              // annotation arrow color
   private Color text_color;              // annotation text color
   private boolean editorOpen;
   private AnnotationEditor editor;
   
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
      line_color = Color.black;
      text_color = Color.black;
      editorOpen = false; 
      this.add(overlay); 
      overlay.setOpaque(false); 
      overlay.addActionListener( new NoteListener() );  
      
      overlay.requestFocus();           
   }

  /**
   * Contains/Displays control information about this overlay.
   */
   public static void help()
   {
      JFrame helper = new JFrame("Help for Annotation Overlay");
      helper.setBounds(0,0,600,400);
      JTextArea text = new JTextArea("Commands for Annotation Overlay\n\n");
      helper.getContentPane().add(text);
      text.setEditable(false);
      text.setLineWrap(true);
      text.append("Note: These commands will NOT work if the Annotation " +
                  "Overlay checkbox IS NOT checked.\n\n");
      text.append("Image Commands in conjunction with AnnotationEditor:\n");
      text.append("Click/Drag/Release Mouse w/N_Key pressed>" + 
                  "CREATE ANNOTATION\n");
      text.append("After annotation creation, Press Enter>ADD ANNOTATION\n");
      text.append("Double Click Mouse>REMOVE LAST ANNOTATION\n");
      text.append("Double Click Mouse w/A_Key>REMOVE ALL ANNOTATIONS\n\n");
      text.append("AnnotationEditor Commands (Focus>Action>Result)\n");
      text.append("TextArea>Press Enter>REFRESH WINDOW\n");
      text.append("TextArea>Hold Ctrl, Press Arrow Keys>MOVE ANNOTATION\n");
      text.append("TextArea>Hold Shift, Press Arrow Keys>MOVE LINE ANCHOR\n");
      text.append("TextArea>Remove All Text, Press Enter/Refresh>REMOVE "+
                  "ANNOTATION\n");		 
      text.append("ColorScale>Click Mouse>CHANGE TEXT AND LINE COLOR\n");
      text.append("ColorScale>Hold Ctrl, Click Mouse>CHANGE LINE COLOR\n");
      text.append("ColorScale>Hold Shift, Click Mouse>CHANGE TEXT COLOR\n");
      
      helper.setVisible(true);
   }
   
  /**
   * This method sets the text color of all annotations. Initially set to black.
   *
   *  @param  color
   */
   public void setTextColor( Color c )
   {
      text_color = c;
      this_panel.repaint();
   }

  /**
   * This method sets the line color of all annotations. Initially set to black.
   *
   *  @param  color
   */
   public void setLineColor( Color c )
   {
      line_color = c;
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
   * This method creates a new AnnotationEditor ( display is done internally
   * in the AnnotationEditor.
   */
   public void editAnnotation()
   {
      if( !editorOpen )
         editor = new AnnotationEditor( notes );
      else
      {
         editor.refresh();
	 //editor = new AnnotationEditor( notes );
      }
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
      FontMetrics fontinfo = g2d.getFontMetrics();
      // resize center "overlay" to size of center jpanel
      overlay.setBounds( current_bounds );

      int ox = overlay.getLocation().x;
      int oy = overlay.getLocation().y;
     /* To "move" the annotations, an x & y scale had to be made. This
      * simply takes the width of the current rectangle/scale rectangle.
      */ 
      float xfactor = 0;
      float yfactor = 0;
      Note note;
      Point at;
      String snote;
      // these variables are used to auto position the text to the arrow
      Point p1;
      Point p2;
      float slope = 0;
      int autolocatex = 0;
      int autolocatey = 0;
      int fontheight = fontinfo.getAscent();
      int textwidth = 0;
      for( int comment = 0; comment < notes.size(); comment++ )
      {
         note = (Note)notes.elementAt(comment);
         xfactor = (float)current_bounds.getWidth()/
	           (float)note.getScale().getWidth();
	           
         yfactor = (float)current_bounds.getHeight()/
	           (float)note.getScale().getHeight();

         at = note.getLocation();
	 snote = note.getText();
         textwidth = fontinfo.stringWidth(snote);
         p1 = note.getLine().getP1();
	 p2 = note.getLine().getP2();
	 // negate the slope since the x scale is top-down instead of bottom-up
	 slope = -(float)(p2.y - p1.y)/(float)(p2.x - p1.x);

	 // Any 2s or 3s except for x/2 are to provide spacing between line/text
	 // Octdrent I or V
	 if( (slope < .5) && (slope >= (-.5)) )
	 {  // Octdrent I, between -22.5 and 22.5 degrees
	    if( p1.x < p2.x )
	    {
	       //System.out.println("Octdrent I");
	       autolocatex = 3;
	       autolocatey = fontheight/2;
	    }
	    else // Octdrent V, between 157.5 and 202.5 degrees
	    {
	       //System.out.println("Octdrent V");
	       autolocatex = -textwidth - 2;
	       autolocatey = fontheight/2;	       
	    }
	 }
	 // Octdrent II or VI
	 else if( (slope >= .5) && (slope <= 2) ) 
	 {  // Octdrent II, between 22.5 and 67.5 degrees
	    if( p1.x < p2.x )
	    {
	       //System.out.println("Octdrent II");
	       autolocatex = 3;
	       autolocatey = -2;
	    }
	    else // Octdrent VI, between 202.5 and 247.5 degrees
	    {
	       //System.out.println("Octdrent VI");
	       autolocatex = -textwidth;
	       autolocatey = fontheight;
	    }
	 }
	 // Octdrent III or VII
	 else if( (slope < -2) || (slope > 2) )
	 {  // Octdrent III, between 67.5 and 112.5 degrees
	    if( p1.y > p2.y )
	    {
	       //System.out.println("Octdrent III");
	       autolocatex = -textwidth/2;
	       autolocatey = -2;
	    }
	    else // Octdrent VII, between 247.5 and 292.5 degrees
	    {
	       //System.out.println("Octdrent VII");
	       autolocatex = -textwidth/2;
	       autolocatey = fontheight;	       
	    }
	 }
	 // Octdrent IV or VIII
	 else
	 {  // Octdrent IV, between 112.5 and 157.5 degrees
	    if( p1.y > p2.y )
	    {
	       //System.out.println("Octdrent IV");
	       autolocatex = -textwidth;
	       autolocatey = -2;
	    }
	    else // Octdrent VIII, between 292.5 and 337.5 degrees
	    {
	       //System.out.println("Octdrent VIII");
	       autolocatex = 3;
	       autolocatey = fontheight;	       
	    }	 
	 }
	 	 
         // line color of all of the annotations.
         g2d.setColor(line_color);	
                	 
	 g2d.drawLine( (int)(note.getLine().getP1().x * xfactor) + ox, 
	               (int)(note.getLine().getP1().y * yfactor) + oy,
	               (int)(note.getLine().getP2().x * xfactor) + ox, 
		       (int)(note.getLine().getP2().y * yfactor) + oy );
	 
         // text color of all of the annotations.
         g2d.setColor(text_color);

	 g2d.drawString( snote, 
	                 (int)(((at.x + autolocatex) * xfactor) + ox), 
	                 (int)(((at.y + autolocatey) * yfactor) + oy));
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
	    {
	       notes.clear(); 
	       // if an editAnnotaton window is open, update it to the changes
	       if( editorOpen )
	          editor.refresh();
	    }
	    else
	       System.out.println("No annotations created");          
	 }
	 // remove the last note from the vector
         else if( message.equals( AnnotationJPanel.RESET_LAST_NOTE ) )
         {
	    //System.out.println("Clear last selected" ); 
	    if( notes.size() > 0 )
	    {
	       notes.removeElementAt(notes.size() - 1);
	       // if an editAnnotaton window is open, update it to the changes 
	       if( editorOpen )
	          editor.refresh();
	    }	       
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
     	       this_panel.addAnnotation( text.getText(), region );
	       frame.dispose();  // since a new viewer is made each time,
	                         // dispose of the old one.
     	       this_panel.getParent().getParent().repaint();
	       // if an editAnnotaton window is open, update it to the changes
	       if( editorOpen )
	          editor.refresh();	       
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
   } // end of Note
  
  /*
   * This viewer contains the meat and bones in editing annotations.
   */ 
   private class AnnotationEditor
   {
      private JFrame viewer;
      private Vector textfields;
      private AnnotationEditor this_viewer;
      // adjust this if more than the text color editor, refresh button,
      // and close button are added. Anything that is not a JTextField from a
      // note should increment this number.
      private final int ADD_COMPONENTS = 3; 
      
      public AnnotationEditor( Vector textvect )
      {
         textfields = textvect;
         buildViewer();
	 this_viewer = this;

      } // end of constructor
      
      public void refresh()
      {
         viewer.dispose();
	 buildViewer();
      }
      
      private void buildViewer()
      {
         viewer = new JFrame("Editor");
	 viewer.addWindowListener( new FrameListener() );
         viewer.setBounds(0,0,200,(35 * (textfields.size() + ADD_COMPONENTS)) );
         viewer.getContentPane().setLayout( 
	                new GridLayout(textfields.size() + ADD_COMPONENTS, 1) );
	 
	 JTextField text = new JTextField();
	 for( int i = 0; i < textfields.size(); i++ )
	 { 
	    text = ((Note)textfields.elementAt(i)).getTextField();
	    
	    text.addKeyListener( new TextFieldListener() );
	    viewer.getContentPane().add(text);
	 }
	 ColorScaleImage notecolor = new ColorScaleImage();
	 notecolor.setNamedColorModel( IndexColorMaker.MULTI_SCALE, false );
	 notecolor.setEventListening(false);
	 notecolor.addMouseListener( new NoteColorListener() );
	 viewer.getContentPane().add( notecolor );
	 
	 JButton refreshbutton = new JButton("Refresh");
	 refreshbutton.addActionListener( new ButtonListener() );
	 viewer.getContentPane().add( refreshbutton );
	 
	 JButton closebutton = new JButton("Close");
	 closebutton.addActionListener( new ButtonListener() );
	 viewer.getContentPane().add( closebutton );

	 // following was created with aid from code examples provided by 
	 // Java's Tech Tips
	 //*********************************************************************
	 Keymap km = text.getKeymap();
	 // these move p2 of the line (the actual note)
	 KeyStroke up = KeyStroke.getKeyStroke( KeyEvent.VK_UP,
	                                        Event.CTRL_MASK );
	 KeyStroke down = KeyStroke.getKeyStroke( KeyEvent.VK_DOWN,
	                                          Event.CTRL_MASK );
	 KeyStroke left = KeyStroke.getKeyStroke( KeyEvent.VK_LEFT,
	                                          Event.CTRL_MASK );
	 KeyStroke right = KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT,
	                                           Event.CTRL_MASK );
						   
         Action actup = new KeyAction("Ctrl-UP"); 
	 Action actdown = new KeyAction("Ctrl-DOWN"); 
	 Action actleft = new KeyAction("Ctrl-LEFT");
	 Action actright = new KeyAction("Ctrl-RIGHT");
	 
	 km.addActionForKeyStroke( up, actup );
	 km.addActionForKeyStroke( down, actdown );
	 km.addActionForKeyStroke( left, actleft );
	 km.addActionForKeyStroke( right, actright );	 
	 // these move p1 of the line (the starting point of the line)
	 KeyStroke sup = KeyStroke.getKeyStroke( KeyEvent.VK_UP,
	                                        Event.SHIFT_MASK );
	 KeyStroke sdown = KeyStroke.getKeyStroke( KeyEvent.VK_DOWN,
	                                          Event.SHIFT_MASK );
	 KeyStroke sleft = KeyStroke.getKeyStroke( KeyEvent.VK_LEFT,
	                                          Event.SHIFT_MASK );
	 KeyStroke sright = KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT,
	                                           Event.SHIFT_MASK );	 
	 
	 Action actsup = new KeyAction("Shift-UP"); 
	 Action actsdown = new KeyAction("Shift-DOWN"); 
	 Action actsleft = new KeyAction("Shift-LEFT");
	 Action actsright = new KeyAction("Shift-RIGHT");
	 
	 km.addActionForKeyStroke( sup, actsup );
	 km.addActionForKeyStroke( sdown, actsdown );
	 km.addActionForKeyStroke( sleft, actsleft );
	 km.addActionForKeyStroke( sright, actsright );
         //*********************************************************************
	 	 
	 viewer.setVisible(true);
	 //viewer.getContentPane().getComponent(0).requestFocus();	      
      } // end of buildViewer()
      
      class ButtonListener implements ActionListener
      {
         public void actionPerformed( ActionEvent e )
	 {
	    String message = e.getActionCommand();
	    
	    int viewersize = viewer.getContentPane().getComponentCount();
	    // ADD_COMPONENTS accountS for components added to viewer that 
	    // are not notes.
	    for( int compid = 0; compid < 
	                         (viewersize - ADD_COMPONENTS); compid++ )
	    { 
	       if( ((JTextField)viewer.getContentPane().getComponent(compid)).
	                                            getText().equals("") )
	       {
	          viewer.dispose();
	          notes.removeElementAt(compid);
		  this_viewer.buildViewer();
		  viewersize = viewer.getContentPane().getComponentCount();
	       }
	    }
	    
	    if( message.equals("Refresh") )
	    {
	       this_panel.repaint();
	    }
	    else if( message.equals("Close") )
	    {	
	       viewer.dispose();
	       this_panel.repaint();
	    }
	 }
      }       
      
      class TextFieldListener extends KeyAdapter
      {
         public void keyTyped( KeyEvent e )
         {
	    // if enter is pressed, update the image
     	    if( e.getKeyChar() == KeyEvent.VK_ENTER )
     	    {
	       int viewersize = viewer.getContentPane().getComponentCount();
	       // -2 in for is to account for two buttons added to viewer
	       for( int compid = 0; compid < 
	                            (viewersize - ADD_COMPONENTS); compid++ )
	       { 
	          if( ((JTextField)viewer.getContentPane().
		          getComponent(compid)).getText().equals("") )
	          {
	             viewer.dispose();
	             notes.removeElementAt(compid);
		     this_viewer.buildViewer();
		     viewersize = viewer.getContentPane().getComponentCount();
	          }
	       }
       	       this_panel.repaint();	       	    
     	    }
         }
      }  // end TextFieldListener

      class FrameListener extends WindowAdapter
      {
         public void windowOpened( WindowEvent we )
	 {
	    //System.out.println("windowOpened");
	    editorOpen = true;
	 }
	 
	 public void windowClosed( WindowEvent we )
	 {
	    //System.out.println("windowClosed");
	    editorOpen = false;
	 }

	 public void windowClosing( WindowEvent we )
	 {
	    //System.out.println("windowClosing");
	    editorOpen = false;
	 }	 
      }// end FrameListener
      
      class KeyAction extends TextAction
      {
         private String name;
         public KeyAction( String tname )
	 {
	    super(tname);
	    name = tname;
	 }
	 
         public void actionPerformed( ActionEvent e )
	 {
	    int viewersize = viewer.getContentPane().getComponentCount();
	    // -2 in for is to account for two buttons added to viewer
	    
	    int compid = 0;
	    while( viewer.getContentPane().getComponent(compid) 
	 	   != e.getSource() && compid < (viewersize - ADD_COMPONENTS))
	    {
	       compid++; 
	    }
	    
	    Note tempnote = (Note)textfields.elementAt(compid);
	    Point tempp1 = tempnote.getLine().getP1();
	    Point tempp2 = tempnote.getLocation();
	    if( name.indexOf("Ctrl") > -1 )
	    {
	       if( name.equals("Ctrl-UP") )
	       {
	          if( tempp2.y > 0 )
	             tempp2.y = tempp2.y - 1;
	       }	    
	       else if( name.equals("Ctrl-DOWN") )
	       {
	          if( tempp2.y < current_bounds.getHeight() )
	             tempp2.y = tempp2.y + 1;
               }	    
	       else if( name.equals("Ctrl-LEFT") )
	       {
	          if( tempp2.x > 0 )
	             tempp2.x = tempp2.x - 1;
	       }
	       else if( name.equals("Ctrl-RIGHT") )
	       {
	          if( tempp2.x < current_bounds.getWidth() )
	             tempp2.x = tempp2.x + 1;
	       }
	    }
	    else if( name.indexOf("Shift") > -1 )
	    {
	       if( name.equals("Shift-UP") )
	       {
	          if( tempp1.y > 0 )
	             tempp1.y = tempp1.y - 1;
	       }	    
	       else if( name.equals("Shift-DOWN") )
	       {
	          if( tempp1.y < current_bounds.getHeight() )
	             tempp1.y = tempp1.y + 1;
               }	    
	       else if( name.equals("Shift-LEFT") )
	       {
	          if( tempp1.x > 0 )
	             tempp1.x = tempp1.x - 1;
	       }
	       else if( name.equals("Shift-RIGHT") )
	       {
	          if( tempp1.x < current_bounds.getWidth() )
	             tempp1.x = tempp1.x + 1;
	       }
	    }	    
	    
	    this_panel.repaint();	    
	 }     
      } // end KeyAction
     
     /*
      * This class listens to the ColorScaleImage on the AnnotationEditor.
      * This listener allows for annotation color change.
      */ 
      class NoteColorListener extends MouseAdapter
      {
         public void mousePressed( MouseEvent e )
	 {
	    ColorScaleImage coloreditor = (ColorScaleImage)e.getSource();
	    Color colorarray[] = IndexColorMaker.getColorTable(
	                           IndexColorMaker.MULTI_SCALE, 127 );
	    Color grayarray[] = IndexColorMaker.getColorTable(
	                           IndexColorMaker.GRAY_SCALE, 127 );
            int colorindex = (int)coloreditor.ImageValue_at_Cursor();
	    System.out.println("ColorIndex: " + colorindex );
	    System.out.println("ColorArray Size: " + colorarray.length );
	    System.out.println("GrayArray Size: " + grayarray.length ); 
	    if( colorindex > 0 )
	    {
	       colorindex = colorindex - 1;
	       if( !e.isControlDown() )		   
	          text_color = colorarray[colorindex];
	       if( !e.isShiftDown() )
	          line_color = colorarray[colorindex];
	    }
	    else
	    {
	       colorindex = colorindex + 1;
	       colorindex = -colorindex;
	       if( !e.isControlDown() )
	          text_color = grayarray[colorindex];
	       if( !e.isShiftDown() )
	          line_color = grayarray[colorindex];	       
	    }
	    this_panel.repaint();	 
	 }
      }
   }
   
}
