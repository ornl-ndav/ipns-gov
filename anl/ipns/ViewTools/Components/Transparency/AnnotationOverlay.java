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
 *  Revision 1.17  2003/10/16 05:00:05  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.16  2003/10/02 04:39:21  millermi
 *  - Added java docs to public static variables.
 *  - Added constructor for setting ObjectState.
 *
 *  Revision 1.15  2003/09/24 01:33:39  millermi
 *  - Added static variables to be used as keys by ObjectState
 *  - Added methods setObjectState() and getObjectState() to adjust to
 *    changes made in OverlayJPanel.
 *  - Added componentResized() listener to set editor bounds
 *    when the editor is resized.
 *
 *  Revision 1.14  2003/08/14 21:47:30  millermi
 *  - AnnotationEditor now extends JFrame, makes use of repaint instead of
 *    always rebuilding the editor.
 *  - MiniViewer now extends JFrame for consistency.
 *  - TextArea of MiniViewer now calls grabFocus() instead of requestFocus()
 *
 *  Revision 1.13  2003/08/14 17:11:01  millermi
 *  - Edited help() for more description and for new annotation commands.
 *
 *  Revision 1.12  2003/08/11 23:49:17  millermi
 *  - Changed help docs from "Double Click" for REMOVE ALL ANNOTATIONS to
 *    "Single Click"
 *
 *  Revision 1.11  2003/08/10 19:29:26  millermi
 *  - Fixed minor bug, when default font was selected, fontsize changed to 12 pt
 *
 *  Revision 1.10  2003/08/07 16:00:05  dennis
 *  - Made "Default" font selection for allowing user to easily
 *    return to the original font.
 *    (Mike Miller)
 *
 *  Revision 1.9  2003/07/25 14:42:04  dennis
 *  - Constructor now takes component of type IZoomTestAddible instead
 *    of IAxisAddible2D. (Mike Miller)
 *
 *  Revision 1.8  2003/07/05 19:43:17  dennis
 *  - Updated to match the changes made to the ImageJPanel
 *    and the ColorScaleImage.  (Mike Miller)
 *
 *  Revision 1.7  2003/06/17 13:20:39  dennis
 *  (Mike Miller)
 *  - Corrected the "jump" when a note is zoomed in.
 *  - Annotations no longer changed in the viewer, now displayed as is
 *    when zoomed.  This was possible after clipRect() was added to
 *    paint method, restricting the painted region.
 *
 *  Revision 1.6  2003/06/13 14:40:23  dennis
 *  (Mike Miller)
 *  - Removed debug statements.
 *  - Added setFont() method and functionality.
 *  - Changed mapping, now used world coordinates with ratios to
 *    account for image zoom consistency.
 *  - Added JComboBox for font and font size to AnnotationEditor.
 *
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
package DataSetTools.components.View.Transparency;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.util.*; 
import java.lang.Math;
import java.lang.Integer;
import javax.swing.text.TextAction;
import javax.swing.text.Keymap;
import javax.swing.event.*;
import java.awt.image.IndexColorModel;

import DataSetTools.components.View.ObjectState;
import DataSetTools.components.View.TwoD.IZoomTextAddible;
import DataSetTools.components.View.Cursor.AnnotationJPanel;
import DataSetTools.components.View.Cursor.LineCursor;
import DataSetTools.components.View.Cursor.Line;
import DataSetTools.components.image.IndexColorMaker;
import DataSetTools.components.ui.ColorScaleImage;
import DataSetTools.components.image.*;
import DataSetTools.util.floatPoint2D;

/**
 * This class allows a user to write comments near a region on the 
 * IZoomTextAddible component. 
 */
public class AnnotationOverlay extends OverlayJPanel
{
  // these public variables are used to preserve the annotation state
 /**
  * "Notes" - This constant String is a key for referencing the state
  * information about the annotations currently stored by this overlay. The
  * value that this key references is a Vector of Note instances. Note is
  * a private internal class within the AnnotationOverlay. 
  */
  public static final String NOTES        = "Notes";
 
 /**
  * "Line Color" - This constant String is a key for referencing the state
  * information about the color of the line connecting the annotation to the
  * region of interest. The value that this key references is a Color.
  */
  public static final String LINE_COLOR   = "Line Color";
 
 /**
  * "Text Color" - This constant String is a key for referencing the state
  * information about the color of the annotation text. The value that
  * this key references is a Color.
  */
  public static final String TEXT_COLOR   = "Text Color";
 
 /**
  * "Font" - This constant String is a key for referencing the state
  * information about the font of the annotation text. The value that
  * this key references is of type Font.
  */
  public static final String FONT         = "Font";
 
 /**
  * "Default Font" - This constant String is a key for referencing the state
  * information about the initial font of the annotation text. The value that
  * this key references is of type Font.
  */
  public static final String DEFAULT_FONT = "Default Font";
    
 /**
  * "Editor Bounds" - This constant String is a key for referencing the state
  * information about the size and bounds of the Annotation Editor window. 
  * The value that this key references is a Rectangle. The Rectangle contains
  * the dimensions for the editor.
  */
  public static final String EDITOR_BOUNDS  = "Editor Bounds";
  
  private AnnotationJPanel overlay;	 // panel overlaying the center jpanel
  private IZoomTextAddible component;	 // component being passed
  private Vector notes; 		 // all annotations
  private AnnotationOverlay this_panel;  // used for repaint by SelectListener 
  private Rectangle current_bounds;
  private Color line_color;		 // annotation arrow color
  private Color text_color;		 // annotation text color
  private AnnotationEditor editor;
  // if ADD_COMPONENTS changes in the editor, the 5 should be incremented in
  // the statement below.
  private Rectangle editor_bounds = new Rectangle(0,0,200,(35 * 5) );
  private Font font;
  private Font default_font;
  private CoordTransform pixel_local;
  
 /**
  * Constructor creates OverlayJPanel with a transparent AnnotationJPanel that
  * shadows the region specified by the getRegionInfo() of the 
  * IZoomTextAddible interface.
  *
  *  @param  izta - component must implement IZoomTextAddible interface
  */ 
  public AnnotationOverlay(IZoomTextAddible izta)
  {
    super();
    this.setLayout( new GridLayout(1,1) );
    
    overlay = new AnnotationJPanel();
    component = izta;
    notes = new Vector();      
    this_panel = this;
    line_color = Color.black;
    text_color = Color.black;
    editor = new AnnotationEditor();
    font = izta.getFont();
    default_font = izta.getFont();
    this.add(overlay); 
    overlay.setOpaque(false); 
    overlay.addActionListener( new NoteListener() );  
    current_bounds =  component.getRegionInfo();
    Rectangle temp = component.getRegionInfo();
    CoordBounds pixel_map = 
 		 new CoordBounds( (float)temp.getX(), 
 				  (float)temp.getY(),
 				  (float)(temp.getX() + temp.getWidth()),
 				  (float)(temp.getY() + temp.getHeight() ) );
    pixel_local = new CoordTransform( pixel_map, 
 				      component.getLocalCoordBounds() );
    overlay.requestFocus();	      
  }
  
 /**
  * Constructor creates OverlayJPanel with a transparent AnnotationJPanel that
  * shadows the region specified by the getRegionInfo() of the 
  * IZoomTextAddible interface.
  *
  *  @param  izta - component must implement IZoomTextAddible interface
  *  @param  state - ObjectState this overlay is being set to.
  */ 
  public AnnotationOverlay(IZoomTextAddible izta, ObjectState state)
  {
    this(izta);
    setObjectState(state);
  }

 /**
  * Contains/Displays control information about this overlay.
  */
  public static void help()
  {
    JFrame helper = new JFrame("Help for Annotation Overlay");
    helper.setBounds(0,0,600,400);
    JTextArea text = new JTextArea("Discription:\n\n");
    text.setEditable(false);
    text.setLineWrap(true);
    text.append("The Annotation Overlay is used to add on-screen notes to " +
 		"data. Below are some basic commands necessary for creating" +
 		" annotations.\n\n");
    text.append("Commands for Annotation Overlay\n\n");
    text.append("Note:\n" +
 		"- These commands will NOT work if the Annotation " +
 		"Overlay checkbox IS NOT checked.\n" +
 		"- Zooming on the image is only allowed if this overlay " +
 		"is turned off.\n\n" );
    text.append("********************************************************\n");
    text.append("Image Commands in conjunction with AnnotationEditor: " +
 		"(Without clicking Edit button)\n");
    text.append("********************************************************\n");
    text.append("Click/Drag/Release Mouse w/Shift_Key pressed>" + 
 		"CREATE ANNOTATION\n");
    text.append("After annotation creation, Press Enter>ADD ANNOTATION\n");
    text.append("Double Click Mouse>REMOVE LAST ANNOTATION\n");
    text.append("Single Click Mouse w/A_Key>REMOVE ALL ANNOTATIONS\n\n");
    text.append("********************************************************\n");
    text.append("AnnotationEditor Commands (Edit Button under Annotation " +
 		"Overlay Control)\n");
    text.append("********************************************************\n");
    text.append("Commands below are listed in the following way:\n");
    text.append("(Focus>Action>Result) where Focus is where the cursor or " +
 		"mouse must be. Focus is gained by clicking the mouse on " +
 		"the desired area. Action is the action performed by you, " +
 		"the user. Result is the consequence of your action.\n");
    text.append("TextArea>Press Enter>REFRESH WINDOW\n");
    text.append("TextArea>Hold Ctrl, Press Arrow Keys>MOVE ANNOTATION\n");
    text.append("TextArea>Hold Shift, Press Arrow Keys>MOVE LINE ANCHOR\n");
    text.append("TextArea>Remove All Text, Press Enter/Refresh>REMOVE "+
 		"ANNOTATION\n");	       
    text.append("ColorScale>Click Mouse>CHANGE TEXT AND LINE COLOR\n");
    text.append("ColorScale>Hold Ctrl, Click Mouse>CHANGE LINE COLOR\n");
    text.append("ColorScale>Hold Shift, Click Mouse>CHANGE TEXT COLOR\n");
    
    JScrollPane scroll = new JScrollPane(text);
    scroll.setVerticalScrollBarPolicy(
 				    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    helper.setVisible(true);
  }
   
  /**
   * This method will set the current state variables of the object to state
   * variables wrapped in the ObjectState passed in.
   *
   *  @param new_state
   */
   public void setObjectState( ObjectState new_state )
   { 
     boolean redraw = false;  // if any values are changed, repaint overlay.
     Object temp = new_state.get(NOTES);
     if( temp != null )
     {
       notes = ((Vector)temp);
       redraw = true;  
     }
     
     temp = new_state.get(LINE_COLOR);
     if( temp != null )
     {
       line_color = (Color)temp;
       redraw = true;  
     } 
     
     temp = new_state.get(TEXT_COLOR);
     if( temp != null )
     {
       text_color = (Color)temp;
       redraw = true;  
     }   
     
     temp = new_state.get(FONT);
     if( temp != null )
     {
       font = ((Font)temp); 
       redraw = true;  
     }  
     
     temp = new_state.get(DEFAULT_FONT);
     if( temp != null )
     {
       default_font = ((Font)temp); 
       redraw = true;  
     }  
     
     temp = new_state.get(EDITOR_BOUNDS);
     if( temp != null )
     {
       editor_bounds = (Rectangle)temp;
       if( editor.isVisible() )
         editor.setBounds( editor_bounds );  
     }
     
     if( redraw )
       this_panel.repaint(); 
   }
  
  /**
   * This method will get the current values of the state variables for this
   * object. These variables will be wrapped in an ObjectState. Keys will be
   * put in alphabetic order.
   */ 
   public ObjectState getObjectState()
   {
     ObjectState state = new ObjectState();
     state.insert( NOTES, notes );
     state.insert( LINE_COLOR, line_color );
     state.insert( TEXT_COLOR, text_color );
     state.insert( FONT, font );
     state.insert( DEFAULT_FONT, default_font );
     state.insert( EDITOR_BOUNDS, editor_bounds );
     
     return state;
   }
  
 /**
  * This method sets the text color of all annotations. Initially set to black.
  *
  *  @param  color
  */
  public void setTextColor( Color color )
  {
    text_color = color;
    this_panel.repaint();
  }

 /**
  * This method sets the line color of all annotations. Initially set to black.
  *
  *  @param  color
  */
  public void setLineColor( Color color )
  {
    line_color = color;
    this_panel.repaint();
  }

 /**
  * This method sets the font for all annotations.
  *
  *  @param newfont
  */
  public void setFont( Font newfont )
  {
    font = newfont;
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
    if( editor.isVisible() )
    {
      editor.toFront();
      editor.requestFocus();
    }
    else
    {
      editor.dispose();
      editor = new AnnotationEditor();
      editor.setVisible(true);
      editor.toFront();
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
    CoordBounds now = new CoordBounds( pixel_local.getDestination().getX1(),
 				       pixel_local.getDestination().getY1(),
 				       pixel_local.getDestination().getX2(),
 				       pixel_local.getDestination().getY2() );
    floatPoint2D p12d = convertToWorldPoint( placement.getP1() );
    floatPoint2D p22d = convertToWorldPoint( placement.getP2() );
    notes.add( new Note( a_note, placement, now, p12d, p22d ) );
  }

 /**
  * Overrides paint method. This method will paint the annotations.
  *
  *  @param  g - graphics object
  */
  public void paint(Graphics g) 
  {  
    Graphics2D g2d = (Graphics2D)g;
    g2d.setFont( font );
    
    current_bounds = component.getRegionInfo(); // current size of center 
    g2d.clipRect( (int)current_bounds.getX(),
 		  (int)current_bounds.getY(),
 		  (int)current_bounds.getWidth(),
 		  (int)current_bounds.getHeight() ); 
    // the current pixel coordinates
    CoordBounds pixel_map = 
 	    new CoordBounds( (float)current_bounds.getX(), 
 			     (float)current_bounds.getY(),
 			     (float)(current_bounds.getX() + 
 				     current_bounds.getWidth()),
 			     (float)(current_bounds.getY() + 
 				     current_bounds.getHeight() ) );
    pixel_local.setSource( pixel_map );
    pixel_local.setDestination( component.getLocalCoordBounds() );
    FontMetrics fontinfo = g2d.getFontMetrics();
    // resize center "overlay" to size of center jpanel
    overlay.setBounds( current_bounds );

    Note note;
    String snote;
    // these variables are used to auto position the text to the arrow
    Point p1;
    Point p2;
    float slope = 0;
    int autolocatex = 0;
    int autolocatey = 0;
    int fontheight = fontinfo.getAscent();
    int textwidth = 0;
    // draw each note
    for( int comment = 0; comment < notes.size(); comment++ )
    {
      note = (Note)notes.elementAt(comment);
      snote = note.getText();
      textwidth = fontinfo.stringWidth(snote);
      p1 = new Point( note.getLine().getP1() );
      p2 = new Point( note.getLine().getP2() );
      // negate the slope since the x scale is top-down instead of bottom-up
      slope = -(float)(p2.y - p1.y)/(float)(p2.x - p1.x);

      // This section of code will adjust the text portion of the annotation
      // according to the slope of the line. 
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
      
      p1 = convertToPixelPoint( note.getWCP1() );
      p2 = convertToPixelPoint( note.getWCP2() );
      //System.out.println("WCP1 = " + note.getWCP1() );
      //System.out.println("P1 = " + p1 );

      // line color of all of the annotations.
      g2d.setColor(line_color);
      g2d.drawLine( p1.x, p1.y, p2.x, p2.y );	      

      // text color of all of the annotations.
      g2d.setColor(text_color);
      g2d.drawString( snote, p2.x + autolocatex, p2.y + autolocatey );
    }	  
  } // end of paint()

 /*
  * Converts from world coordinates to a pixel point
  */
  private Point convertToPixelPoint( floatPoint2D fp )
  {
    floatPoint2D fp2d = pixel_local.MapFrom( fp );
    return new Point( (int)fp2d.x, (int)fp2d.y );
  }
 
 /*
  * Converts from pixel coordinates to world coordinates.
  */
  private floatPoint2D convertToWorldPoint( Point p )
  {
    return pixel_local.MapTo( new floatPoint2D((float)p.x, (float)p.y) );
  }
  
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
 	if( notes.size() > 0 )
 	{
 	  notes.clear(); 
 	  // if an editAnnotaton window is open, update it to the changes
 	  if( editor.isVisible() )
 	  {
 	    editor.dispose();
 	    editor = new AnnotationEditor();
	    editor.setVisible(true);
 	  }
 	}	  
      }
      // remove the last note from the vector
      else if( message.equals( AnnotationJPanel.RESET_LAST_NOTE ) )
      { 
 	if( notes.size() > 0 )
 	{
 	  notes.removeElementAt(notes.size() - 1);
 	  // if an editAnnotaton window is open, update it to the changes 
 	  if( editor.isVisible() )
 	  {
 	    editor.dispose();
 	    editor = new AnnotationEditor();
	    editor.setVisible(true);
 	  }
 	}	  
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
 	 // since the mapping is from the current_bounds to world coordinates
 	 // the local pixel coords are translated to global pixel coords,
 	 // which is how they are displayed.
 	 Point p1 = new Point( temp.getP1() );
 	 p1.x += current_bounds.getX();
 	 p1.y += current_bounds.getY();
 	 Point p2 = new Point( temp.getP2() );
 	 p2.x += current_bounds.getX();
 	 p2.y += current_bounds.getY();
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
  private class MiniViewer extends JFrame
  {
    private MiniViewer this_mini;
    private JTextField text;
    private Line region;
    public MiniViewer( Line acline, Container viewer )
    {
      super("Annotation");
      region = acline;
      this_mini = this;
      this.setBounds(0,0,150,60);
      this.getContentPane().setLayout( new GridLayout(1,1) );
      text = new JTextField(20);
      text.addKeyListener( new TextFieldListener() );
      this.getContentPane().add(text); 

      Point place = new Point(region.getP2()); 
      place.x = place.x + (int)viewer.getLocation().getX();
      place.y = place.y + (int)viewer.getLocation().getY();
      this.setLocation(place);
      this.setVisible(true);
      text.grabFocus();		
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
 	  this_mini.dispose();  // since a new viewer is made each time,
 			        // dispose of the old one.
 	  this_panel.getParent().getParent().repaint();
 	  // if an editAnnotaton window is open, update it to the changes
 	  if( editor.isVisible() )
 	  {
 	    editor.dispose();
 	    editor = new AnnotationEditor();
	    editor.setVisible(true);
 	  }
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
     private Line arrow;	   // location to draw this note (p1, p2)
     private CoordBounds scale;    // the bounds of the overlay when this 
 				   // note was created
     private floatPoint2D wcp1;    // the world coordinate associated with p1
     private floatPoint2D wcp2;    // the world coordinate associated with p2
    /*
     * This constructor takes in five parameters, a string text which is 
     * the actual message, a line which has a beginning point at the region
     * of interest and an ending point where the annotation will be drawn, a
     * rectangle which represents the bounds in which this annotation was 
     * created, and two world points, corresponding to the world values
     * referred to by p1 and p2 of the line.
     */ 
     public Note(String t, Line l, CoordBounds s, floatPoint2D wc_p1,
 						floatPoint2D wc_p2 )
     {
 	textfield = new JTextField(t);
 	arrow = new Line(l.getP1(), l.getP2());
        scale = s;
        wcp1 = new floatPoint2D( wc_p1 );
        wcp2 = new floatPoint2D( wc_p2 );
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
     public CoordBounds getScale()
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
    
    /*
     * @return the world coordinate point p1 refers to.
     */ 
     public floatPoint2D getWCP1()
     {
 	return wcp1;
     } 
     
     public void setWCP1( floatPoint2D p1 )
     {
 	wcp1 = p1;
     }
 	 
    /*
     * @return the world coordinate point p2 refers to.
     */ 
     public floatPoint2D getWCP2()
     {
 	return wcp2;
     }
     
     public void setWCP2( floatPoint2D p2 )
     {
 	wcp2 = p2;
     }
  } // end of Note
 
 /*
  * This viewer contains everything for editing annotations. All of its
  * listeners are self-contained.
  */ 
  private class AnnotationEditor extends JFrame
  {
    private AnnotationEditor this_viewer;
    // adjust this if more than the font, fontsize, text color editor, 
    // refresh button, and close button are added. Anything that is not a 
    // JTextField from a note should increment this number.
    private final int ADD_COMPONENTS = 5; 
    private int current_fontsize;
    private Font[] fonts;
   
   /*
    * constructs a new annotation editor.
    */ 
    public AnnotationEditor()
    {
      super("Annotation Editor");
      this_viewer = this;
      current_fontsize = font.getSize();
      int height = 35 * (ADD_COMPONENTS + notes.size() );
      if( editor_bounds.getHeight() > height )
        height = (int)editor_bounds.getHeight();
      this.setBounds( (int)editor_bounds.getX(), (int)editor_bounds.getY(), 
                      (int)editor_bounds.getWidth(), height );
      this.addComponentListener( new EditorListener() );
      this.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
      this.getContentPane().setLayout( 
        	     new GridLayout(notes.size() + ADD_COMPONENTS, 1) );
      
      JTextField text = new JTextField();
      for( int i = 0; i < notes.size(); i++ )
      { 
         text = ((Note)notes.elementAt(i)).getTextField();
         
         text.addKeyListener( new TextFieldListener() );
         this.getContentPane().add(text);
      }
      GraphicsEnvironment ge =
        	   GraphicsEnvironment.getLocalGraphicsEnvironment();
      fonts = ge.getAllFonts();
      String[] fontnames = ge.getAvailableFontFamilyNames();
      JComboBox fontlist = new JComboBox( fontnames );
      fontlist.insertItemAt("Change Font",0);
      fontlist.setSelectedIndex(0);
      fontlist.addActionListener( new ComboBoxListener() );
      this.getContentPane().add( fontlist );
      
      String[] sizes = {"Change Font Size","8","12","16","20","24","28"};
      JComboBox sizelist = new JComboBox( sizes );
      sizelist.addActionListener( new ComboBoxListener() );
      this.getContentPane().add( sizelist );
      
      ColorScaleImage notecolor = 
            new ColorScaleImage(ColorScaleImage.HORIZONTAL_DUAL);
      notecolor.setNamedColorModel( IndexColorMaker.MULTI_SCALE,true,false );
      notecolor.setEventListening(false);
      notecolor.addMouseListener( new NoteColorListener() );
      this.getContentPane().add( notecolor );
      
      JButton refreshbutton = new JButton("Refresh");
      refreshbutton.addActionListener( new ButtonListener() );
      this.getContentPane().add( refreshbutton );
      
      JButton closebutton = new JButton("Close");
      closebutton.addActionListener( new ButtonListener() );
      this.getContentPane().add( closebutton );

      // These commands will create key events for moving the annotation
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
    } // end of constructor

    
    class ButtonListener implements ActionListener
    {
      public void actionPerformed( ActionEvent e )
      {
        String message = e.getActionCommand();
        
        int viewersize = this_viewer.getContentPane().getComponentCount();
        // ADD_COMPONENTS accountS for components added to viewer that 
        // are not notes.
	boolean noteRemoved = false;
        for( int compid = 0; compid < 
                	     (viewersize - ADD_COMPONENTS); compid++ )
        { 
          if( ((JTextField)this_viewer.getContentPane().
          		     getComponent(compid)).getText().equals("") )
          {
            notes.removeElementAt(compid);
            this_viewer.repaint();
            viewersize = this_viewer.getContentPane().getComponentCount();
            noteRemoved = true;
	  }
        }
        
        if( message.equals("Refresh") )
        {
	  if( noteRemoved )
	  {
	    editor.dispose();
	    editor = new AnnotationEditor();
	    editor.setVisible(true);
          }
	  this_panel.repaint();
        }
        else if( message.equals("Close") )
        {  
          this_viewer.setVisible(false);
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
   	  int viewersize = this_viewer.getContentPane().getComponentCount();
   	  // -2 in for is to account for two buttons added to viewer
   	  for( int compid = 0; compid < 
   			       (viewersize - ADD_COMPONENTS); compid++ )
   	  { 
   	    if( ((JTextField)this_viewer.getContentPane().
   		    getComponent(compid)).getText().equals("") )
   	    {
   	      notes.removeElementAt(compid);
   	      this_viewer.repaint();
   	      viewersize = this_viewer.getContentPane().getComponentCount();
   	    }
   	  }
          this_panel.repaint(); 	       
        }
      }
    }  // end TextFieldListener
   
   /*
    * This class defines the actions for the key strokes created above. 
    */ 
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
   	int viewersize = this_viewer.getContentPane().getComponentCount();
   	// -2 in for is to account for two buttons added to viewer
   	
   	int compid = 0;
   	while( this_viewer.getContentPane().getComponent(compid) 
   	       != e.getSource() && compid < (viewersize - ADD_COMPONENTS))
   	{
   	   compid++; 
   	}
   	
   	Note tempnote = (Note)notes.elementAt(compid);
   	Point tempp1 = tempnote.getLine().getP1();
   	Point tempp2 = tempnote.getLocation();
   	CoordTransform pix_to_world = new 
   		    CoordTransform( pixel_local.getSource(),
   				    tempnote.getScale() );
   	
   	if( name.indexOf("Ctrl") > -1 )
   	{
   	  if( name.equals("Ctrl-UP") )
   	  {
   	     //if( tempp2.y > 0 )
   		tempp2.y = tempp2.y - 1;
   	  }	       
   	  else if( name.equals("Ctrl-DOWN") )
   	  {
   	     //if( tempp2.y < current_bounds.getHeight() )
   		tempp2.y = tempp2.y + 1;
          }	       
   	  else if( name.equals("Ctrl-LEFT") )
   	  {
   	     //if( tempp2.x > 0 )
   		tempp2.x = tempp2.x - 1;
   	  }
   	  else if( name.equals("Ctrl-RIGHT") )
   	  {
   	     //if( tempp2.x < current_bounds.getWidth() )
   		tempp2.x = tempp2.x + 1;
   	  }
   	  tempnote.setWCP2( pix_to_world.MapTo( new floatPoint2D(
   						(float)tempp2.x, 
   						(float)tempp2.y) ) );
   	}
   	else if( name.indexOf("Shift") > -1 )
   	{
   	  if( name.equals("Shift-UP") )
   	  {
   	     //if( tempp1.y > 0 )
   		tempp1.y = tempp1.y - 1;
   	  }	       
   	  else if( name.equals("Shift-DOWN") )
   	  {
   	     //if( tempp1.y < current_bounds.getHeight() )
   		tempp1.y = tempp1.y + 1;
          }	       
   	  else if( name.equals("Shift-LEFT") )
   	  {
   	     //if( tempp1.x > 0 )
   		tempp1.x = tempp1.x - 1;
   	  }
   	  else if( name.equals("Shift-RIGHT") )
   	  {
   	     //if( tempp1.x < current_bounds.getWidth() )
   		tempp1.x = tempp1.x + 1;
   	  }
   	  tempnote.setWCP1( pix_to_world.MapTo( new floatPoint2D(
   						(float)tempp1.x, 
   						(float)tempp1.y) ) );
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

   /*
    * This class handles the font and font size.
    */
    class ComboBoxListener implements ActionListener
    {
      public void actionPerformed( ActionEvent e )
      {
 	JComboBox temp = ((JComboBox)e.getSource());
 	String message = (String)temp.getSelectedItem();
 	boolean isNumeric = false;
 	try
 	{
 	  current_fontsize = Integer.parseInt(message);
 	  isNumeric = true;
 	}
 	catch( NumberFormatException nfe )
 	{
 	  isNumeric = false;
 	}
 	if( !isNumeric )
 	{
 	  int fontindex = 0;
 	  while( message.indexOf("Change") < 0 &&
 		 !(fonts[fontindex].getFamily().equals(message)) &&
 		 (fontindex < fonts.length - 1) )
 	  {
 	    fontindex++;
 	  }
 	  if( message.equals("Default") )
 	  {
 	    font = default_font;
 	    font = font.deriveFont( Font.PLAIN );
 	    font = font.deriveFont( (float)current_fontsize );
 	  }
 	  else
 	  {
 	    font = fonts[fontindex];
 	    font = font.deriveFont( Font.PLAIN );
 	    font = font.deriveFont( (float)current_fontsize );
 	  }
 	  this_panel.repaint();
 	}
 	else
 	{
 	  font = font.deriveFont( (float)current_fontsize );
 	  this_panel.repaint();
 	}
      }
    } // end ComboBoxListener
     
    class EditorListener extends ComponentAdapter
    {
      public void componentResized( ComponentEvent we )
      {
    	editor_bounds = editor.getBounds();
      }
    }	     
  }
}
