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
 *  Revision 1.31  2004/04/29 06:12:24  millermi
 *  - Revised the AnnotationEditor. New features include turning
 *    the anchor line on/off, a new color selection process,
 *    and a more flexible add/remove/edit format for manipulating
 *    annotations.
 *
 *  Revision 1.30  2004/03/15 23:53:52  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.29  2004/03/12 02:52:11  serumb
 *  Change package and imports.
 *
 *  Revision 1.28  2004/03/09 21:06:46  millermi
 *  - Added ToolTips to editors for user assistance.
 *
 *  Revision 1.27  2004/02/06 23:23:43  millermi
 *  - Changed how editor bounds were stored in the ObjectState,
 *    removed check if visible.
 *
 *  Revision 1.26  2004/01/29 23:46:17  millermi
 *  - Added clearAnnotations() to remove all annotations.
 *
 *  Revision 1.25  2004/01/29 08:16:27  millermi
 *  - Updated the getObjectState() to include parameter for specifying
 *    default state.
 *  - Added static variables DEFAULT and PROJECT to IPreserveState for
 *    use by getObjectState()
 *
 *  Revision 1.24  2004/01/03 04:36:12  millermi
 *  - help() now uses html tool kit to display text.
 *  - Replaced all setVisible(true) with WindowShower.
 *
 *  Revision 1.23  2003/12/23 02:00:31  millermi
 *  - Adjusted interface package locations since they
 *    were moved from the TwoD directory
 *
 *  Revision 1.22  2003/12/20 21:37:29  millermi
 *  - implemented kill() so editor and help windows are now
 *    disposed when the kill() is called.
 *
 *  Revision 1.21  2003/11/21 02:52:34  millermi
 *  - Improved the repainting of the overlay.
 *  - Editor bounds are now saved before dispose() is called.
 *
 *  Revision 1.20  2003/11/18 01:00:17  millermi
 *  - Made non-save dependent private variables transient.
 *
 *  Revision 1.19  2003/10/20 22:46:49  millermi
 *  - Added private class NotVisibleListener to listen
 *    when the overlay is no longer visible. When not
 *    visible, any editor that is visible will be made
 *    invisible too. This will not dispose the editor,
 *    just setVisible(false).
 *
 *  Revision 1.18  2003/10/17 23:01:47  millermi
 *  - Removed private class Note. Note is now a public
 *    class inside the package.
 *
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
package gov.anl.ipns.ViewTools.Components.Transparency;

import javax.swing.*; 
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.util.*; 
import java.lang.Integer;
import javax.swing.text.TextAction;
import javax.swing.text.Keymap;

import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.Cursor.AnnotationJPanel;
import gov.anl.ipns.ViewTools.Components.Cursor.LineCursor;
import gov.anl.ipns.ViewTools.Components.Cursor.Line;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;
import gov.anl.ipns.ViewTools.UI.ColorScaleImage;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.ColorSelector;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Panels.Transforms.*;

/**
 * This class allows a user to write comments near a region on the 
 * IZoomTextAddible component. Since this class extends an OverlayJPanel,
 * which extends a JPanel, this class is already serializable.
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
  
  private static JFrame helper = null;
  // panel overlaying the center jpanel
  private transient AnnotationJPanel overlay;
  private transient IZoomTextAddible component;	 // component being passed
  private Vector notes; 		 // all annotations
  // used for repaint by NoteListener  
  private transient AnnotationOverlay this_panel;
  private transient Rectangle current_bounds;
  private Color line_color;		 // annotation arrow color
  private Color text_color;		 // annotation text color
  private transient AnnotationEditor editor;
  private Rectangle editor_bounds = new Rectangle(0,0,430,265 );
  private Font font;
  private Font default_font;
  private transient CoordTransform pixel_local;
  private boolean show_anchor_line = true;
  
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
    addComponentListener( new NotVisibleListener() );
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
    pixel_local = new CoordTransform();
    updateTransform();
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
    helper = new JFrame("Help for Annotation Overlay");
    helper.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
    helper.setBounds(0,0,600,400);
    
    JEditorPane textpane = new JEditorPane();
    textpane.setEditable(false);
    textpane.setEditorKit( new HTMLEditorKit() );
    String text = "<H1>Description:</H1><P>" +
                  "The Annotation Overlay is used to add on-screen notes to " +
 		  "data. Below are some basic commands necessary for creating" +
 		  " annotations.</P>" +
                  "<H2>Commands for Annotation Overlay</H2>" +
                  "<P>Note:<BR>" +
 		  "- These commands will NOT work if the Annotation " +
 		  "Overlay checkbox IS NOT checked.<BR>" +
 		  "- Zooming on the image is only allowed if this overlay " +
 		  "is turned off.</P>" +
                  "<H2>Image Commands in conjunction with AnnotationEditor: " +
 		  "<BR>(Without clicking Edit button)</H2>" +
                  "<P>Click/Drag/Release Mouse w/Shift_Key pressed>" + 
 		  "CREATE ANNOTATION<BR>" +
                  "After annotation creation, Press Enter>ADD ANNOTATION<BR>" +
                  "Double Click Mouse>REMOVE LAST ANNOTATION<BR>" +
                  "Single Click Mouse w/A_Key>REMOVE ALL ANNOTATIONS</P>" +
                  "<H2>AnnotationEditor Commands <BR>" +
		  "(Edit Button under Annotation Overlay Control)</H2>" +
                  "<P>Commands below are listed in the following way:<BR>" +
                  "(Focus>Action>Result) Focus is where the cursor or " +
 		  "mouse must be. Focus is gained by clicking the mouse on " +
 		  "the desired area. Action is the action performed by you, " +
 		  "the user. Result is the consequence of your action.<BR>" +
                  "TextArea>Press Enter>REFRESH WINDOW<BR>" +
                  "TextArea>Hold Ctrl, Press Arrow Keys>MOVE LINE ANCHOR<BR>" +
                  "TextArea>Hold Shift, Press Arrow Keys>MOVE ANNOTATION<BR>" +
                  "Remove Button>Single Click>REMOVE ANNOTATION<BR>Change " +
                  "Button>Single Click>CHANGE CURRENTLY SELECTED ANNOTATION, " +
		  "IF NO ANNOTATIONS EXIST, TEXT IS ADDED TO CENTER OF IMAGE" +
		  "AS A NEW ANNOTATION.<BR>" +
                  "ColorSelector>Click Mouse>CHANGE TEXT AND LINE COLOR<BR>" +
                  "Checkbox>If Checked, line from anchor to text is drawn>" +
		  "ADD/REMOVE LINE<BR>" +
                  "Close Button>Single Mouse Click>CLOSE EDITOR</P>";
    textpane.setText(text);
    JScrollPane scroll = new JScrollPane(textpane);
    scroll.setVerticalScrollBarPolicy(
 				    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    WindowShower shower = new WindowShower(helper);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
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
       editor.setBounds( editor_bounds );  
     }
     
     if( redraw )
       this_panel.repaint(); 
   }
  
  /**
   * This method will get the current values of the state variables for this
   * object. These variables will be wrapped in an ObjectState.
   *
   *  @param  isDefault Should selective state be returned, that used to store
   *			user preferences common from project to project?
   *  @return if true, the default state containing user preferences,
   *	      if false, the entire state, suitable for project specific saves.
   */ 
   public ObjectState getObjectState( boolean isDefault )
   {
     ObjectState state = new ObjectState();
     state.insert( LINE_COLOR, line_color );
     state.insert( TEXT_COLOR, text_color );
     state.insert( FONT, font );
     state.insert( DEFAULT_FONT, default_font );
     state.insert( EDITOR_BOUNDS, editor_bounds );
    
     // load these for project specific instances.
     if( !isDefault )
     {
       state.insert( NOTES, notes );
     }
     
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
      editor_bounds = editor.getBounds();
      editor.dispose();
      editor = new AnnotationEditor();
      WindowShower shower = new WindowShower(editor);
      java.awt.EventQueue.invokeLater(shower);
      shower = null;
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
    floatPoint2D p12d = convertToWorldPoint( placement.getP1() );
    floatPoint2D p22d = convertToWorldPoint( placement.getP2() );
    notes.add( new Note( a_note, placement, p12d, p22d ) );
    // Update the combobox containing the list of notes.
    editor.updateNoteList();
    repaint();
  }
  
 /**
  * Remove the annotation at the specified index.
  *
  *  @param  index The index of the annotation.
  */ 
  public void removeAnnotation( int index )
  {
    if( !(index < 0 ) && index < notes.size() )
    {
      notes.remove(index);
      // Update the combobox containing the list of notes.
      editor.updateNoteList();
    }
    repaint();
  }

 /**
  * Allows toplevel components to remove all annotations.
  */
  public void clearAnnotations()
  {
    notes.clear();
    // Update the combobox containing the list of notes.
    editor.updateNoteList();
    repaint();
  }
     
 /**
  * This method is called by to inform the overlay that it is no
  * longer needed. In turn, the overlay closes all windows created
  * by it before closing.
  */ 
  public void kill()
  {
    editor.dispose();
    if( helper != null )
      helper.dispose();
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
    
    updateTransform();
    g2d.clipRect( (int)current_bounds.getX(),
 		  (int)current_bounds.getY(),
 		  (int)current_bounds.getWidth(),
 		  (int)current_bounds.getHeight() ); 
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
      snote = note.toString();
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
      
      // Only draw the line if it is specified, drawn by default.
      if( show_anchor_line )
      {
        // line color of all of the annotations.
        g2d.setColor(line_color);
        g2d.drawLine( p1.x, p1.y, p2.x, p2.y );
      }

      // text color of all of the annotations.
      g2d.setColor(text_color);
      g2d.drawString( snote, p2.x + autolocatex, p2.y + autolocatey );
    }	  
  } // end of paint()
  
 /*
  * This method will get the current bounds of the center and reset
  * the transform that converts pixel to world coords.
  */
  private void updateTransform()
  {
    current_bounds = component.getRegionInfo(); // current size of center 
    CoordBounds pixel_map = 
 	    new CoordBounds( (float)current_bounds.getX(), 
 			     (float)current_bounds.getY(),
 			     (float)(current_bounds.getX() + 
 				     current_bounds.getWidth()),
 			     (float)(current_bounds.getY() + 
 				     current_bounds.getHeight() ) );
    pixel_local.setSource( pixel_map );
    pixel_local.setDestination( component.getLocalCoordBounds() );
  }

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
 	  editor.updateNoteList();
 	}	  
      }
      // remove the last note from the vector
      else if( message.equals( AnnotationJPanel.RESET_LAST_NOTE ) )
      { 
 	if( notes.size() > 0 )
 	{
 	  notes.removeElementAt(notes.size() - 1);
 	  editor.updateNoteList();
 	}	  
      }       
      else if( message.equals( AnnotationJPanel.NOTE_REQUESTED ) )
      {      
 	 //****************** this code here may change ********************
 	 // this is here to allow the pop-up textfield to adjust if the
 	 // viewer itself is moved.
 	 Container viewer = this_panel;
         while( viewer.getParent() != null )
           viewer = viewer.getParent();
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
      WindowShower shower = new WindowShower(this);
      java.awt.EventQueue.invokeLater(shower);
      shower = null;
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
 	  this_panel.repaint();
 	  //System.out.println("KeyTyped " + e.getKeyChar() );         
 	}
      }
    }	    
  } // end of MiniViewer
     
 /*
  * This viewer contains everything for editing annotations. All of its
  * listeners are self-contained.
  */ 
  private class AnnotationEditor extends JFrame
  {
    private AnnotationEditor this_viewer;
    private JComboBox note_list;
    private JTextField text;
    private int current_fontsize;
    private Font[] fonts;
    private JPanel north_component;
   
   /*
    * constructs a new annotation editor.
    */ 
    public AnnotationEditor()
    {
      super("Annotation Editor");
      this_viewer = this;
      current_fontsize = font.getSize();
      
      this.setBounds( editor_bounds );
      this.addComponentListener( new EditorListener() );
      this.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
      this.getContentPane().setLayout( new BorderLayout() );
      buildFirstComponent();
      this.getContentPane().add(north_component, BorderLayout.NORTH );
      
      /* 
       * This is the old color selection method, it included
       * options to set line and annotation color independently.
       * This option is no longer available, unless a radio button
       * or multiple checkboxes were added to give users options.
      ColorScaleImage notecolor = 
            new ColorScaleImage(ColorScaleImage.HORIZONTAL_DUAL);
      notecolor.setNamedColorModel( IndexColorMaker.MULTI_SCALE,true,false );
      notecolor.setEventListening(false);
      notecolor.addMouseListener( new NoteColorListener() );
      notecolor.setToolTipText("Shift+Click=Text Color Only, " +
        		       "Ctrl+Click=Line Color Only");
      this.getContentPane().add( notecolor );
      */
      
      // Add a color selector to allow users to change the color of 
      // the annotation.
      ColorSelector colorchooser = new ColorSelector(ColorSelector.SWATCH);
      colorchooser.addActionListener( new ColorChangeListener() );
      this.getContentPane().add(colorchooser, BorderLayout.CENTER);
      
      JCheckBox draw_anchor = new JCheckBox("Draw Anchor Line");
      draw_anchor.setSelected(true);
      draw_anchor.addActionListener( new ButtonListener() );
      
      JButton help = new JButton("Help");
      help.addActionListener( new ButtonListener() );
      JPanel checkbox_and_help = new JPanel( new GridLayout(1,2) );
      checkbox_and_help.add(draw_anchor);
      checkbox_and_help.add(help);
      
      JButton closebutton = new JButton("Close");
      closebutton.addActionListener( new ButtonListener() );
      
      
      // To correct layout issues, put first two rows into one component.
      JPanel comp4 = new JPanel(new GridLayout(2,1));
      comp4.add(checkbox_and_help);
      comp4.add(closebutton);
      this.getContentPane().add(comp4, BorderLayout.SOUTH);
      
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

   /* *******************************updateNoteList****************************
    * This method will update the combo box containing the list of notes.
    */ 
    protected void updateNoteList()
    {
      int selected_index = 0;
      boolean constructor_call = true;
      // Store the selected index so that the new combo box can be set with
      // that index.
      if( note_list != null )
      {
        selected_index = note_list.getSelectedIndex();
        this.getContentPane().remove(north_component);
	// When first note is added, selected_index is kept at -1, need to
	// set it to 0.
	if( notes.size() > 0 && selected_index < 0 )
	  selected_index = 0;
      }
      buildFirstComponent();
      note_list.setSelectedIndex(selected_index);
      this.getContentPane().add( north_component, BorderLayout.NORTH );
      this_viewer.validate();
      this_viewer.repaint();
    }
    
   /*
    * This class builds the first three rows of buttons on the Annotation
    * Editor. This is done in a separate method so that the note_list
    * can be updated.
    */
    private void buildFirstComponent()
    {
      // Add a textfield which will allow the user to edit the annotations.
      // "text" must be initialized before updateNoteList() is called.
      text = new JTextField();
      text.addKeyListener( new TextFieldListener() );
      text.setToolTipText("Shift+Arrow Key=Move Text, " +
        		  "Ctrl+Arrow Key=Move Anchor");
      
      // construct the note_list
      note_list = new JComboBox(notes);
      note_list.addActionListener( new NoteListListener() );
      // restore the new combobox to the index of the old combo box.
      if( notes.size() > 0 )
      {
	text.setText( note_list.getSelectedItem().toString() );
      }
      else
        text.setText("");
      
      // This button will remove notes from the notes vector and consequently
      // from the JComboBox that displays the list of notes.
      JButton remove = new JButton("Remove");
      remove.addActionListener( new ButtonListener() );
      
      // A jpanel that contains both the JComboBox list of notes and the
      // remove button.
      JPanel list_and_remove = new JPanel( new BorderLayout() );
      list_and_remove.add( note_list, BorderLayout.CENTER );
      list_and_remove.add( remove, BorderLayout.EAST );
      
      
      // This button will remove notes from the notes vector and consequently
      // from the JComboBox that displays the list of notes.
      JButton change = new JButton("Change");
      change.addActionListener( new ButtonListener() );
      
      // A jpanel that contains both the textfield to edit notes and the
      // change button.
      JPanel text_and_change = new JPanel( new BorderLayout() );
      text_and_change.add( text, BorderLayout.CENTER );
      text_and_change.add( change, BorderLayout.EAST );
      
      GraphicsEnvironment ge =
        	   GraphicsEnvironment.getLocalGraphicsEnvironment();
      fonts = ge.getAllFonts();
      String[] fontnames = ge.getAvailableFontFamilyNames();
      JComboBox fontlist = new JComboBox( fontnames );
      fontlist.insertItemAt("Change Font",0);
      fontlist.setSelectedIndex(0);
      fontlist.addActionListener( new ComboBoxListener() );
      
      // Enable users to adjust size of the annotations.
      String[] sizes = {"Change Font Size","8","12","16","20","24","28"};
      JComboBox sizelist = new JComboBox( sizes );
      sizelist.addActionListener( new ComboBoxListener() );
      
      // To correct layout issues, put third row into one component.
      JPanel row3 = new JPanel(new GridLayout(1,2));
      row3.add(fontlist);
      row3.add(sizelist);
      
      // To correct layout issues, put first two rows into one component.
      north_component = new JPanel(new GridLayout(3,1));
      north_component.add(list_and_remove);
      north_component.add(text_and_change);
      north_component.add(row3);
    }
   
   /*
    * This method is called when either "Enter" is pressed while the textfield
    * has focus, or the "Change" button is pressed. This method will take the
    * text from the text area and change it in the note selected by the 
    * JComboBox. If no notes exist, the text will be added as a note to the
    * center of the image.
    */ 
    private void changeText()
    {
      int index = note_list.getSelectedIndex();
      // If there are no notes, add a note if text was entered.
      if( index < 0 )
      {
        // Make sure text has been entered into the TextField
        if( !text.getText().equals("") )
	{
	  // place new annotation at the center of the image.
	  CoordBounds pixel_coords = pixel_local.getSource();
	  Point anchor_pt = new Point();
	  Point text_pt = new Point();
	  anchor_pt.x = (int)((pixel_coords.getX1() + pixel_coords.getX2())/2f);
	  text_pt.x = anchor_pt.x;
	  anchor_pt.y = (int)((pixel_coords.getY1() + pixel_coords.getY2())/2f);
	  text_pt.y = anchor_pt.y/2;
	  addAnnotation( text.getText(), new Line(anchor_pt,text_pt) );
	}
      }
      // If notes exist, edit the selected note.
      else
      {
        ((Note)notes.elementAt(index)).setText( text.getText() );
        updateNoteList();
        this_panel.repaint();
      }
    }
    
   /*
    * Listener class for all of the buttons on the AnnotationEditor
    */ 
    class ButtonListener implements ActionListener
    {
      public void actionPerformed( ActionEvent e )
      {
        String message = e.getActionCommand();
        if( message.equals("Remove") )
	{
	  int index = note_list.getSelectedIndex();
	  this_panel.removeAnnotation( index );
	  // If index < 0, do nothing
	  if( index < 0 )
	    return;
	  // If index is 0, and there are no notes, make index invalid.
	  if( index == 0 )
	  {
	    if( notes.size() == 0 )
	    {
	      index = -1;
	    }
	  }
	  // Else index > 0, move selected to the previous item
	  else
	    note_list.setSelectedIndex(index - 1);
	  
	  // If index is invalid, clear the TextField.
	  if( index < 0 )
	  {
	    text.setText("");
	    return;
	  }	  
	  text.setText(note_list.getSelectedItem().toString());
	}
	else if( message.equals("Change") )
	{
	  changeText();
	}
        else if( message.equals("Draw Anchor Line") )
        {
	  JCheckBox anchor = (JCheckBox)e.getSource();
	  if( anchor.isSelected() )
	    show_anchor_line = true;
	  else
	    show_anchor_line = false;
          this_panel.repaint();
	}
	else if( message.equals("Help") )
	{
	  help();
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
	  changeText();
        }
      }
    }  // end TextFieldListener
   
   /*
    * This class defines the actions for the key strokes created above.
    * This class will cause notes to move using arrow keys and modifiers. 
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
        // find the selected note to move.
   	int note_index = note_list.getSelectedIndex();
   	// Make sure a note is selected.
	if( note_index < 0 )
	  return;
   	Note tempnote = (Note)notes.elementAt(note_index);
   	Point tempp1 = tempnote.getLine().getP1();
   	Point tempp2 = tempnote.getLocation();
   	
   	if( name.indexOf("Ctrl") > -1 )
   	{
   	  if( name.equals("Ctrl-UP") )
   	  {
   	     //if( tempp1.y > 0 )
   		tempp1.y = tempp1.y - 1;
   	  }	       
   	  else if( name.equals("Ctrl-DOWN") )
   	  {
   	     //if( tempp1.y < current_bounds.getHeight() )
   		tempp1.y = tempp1.y + 1;
          }	       
   	  else if( name.equals("Ctrl-LEFT") )
   	  {
   	     //if( tempp1.x > 0 )
   		tempp1.x = tempp1.x - 1;
   	  }
   	  else if( name.equals("Ctrl-RIGHT") )
   	  {
   	     //if( tempp1.x < current_bounds.getWidth() )
   		tempp1.x = tempp1.x + 1;
   	  }
   	  tempnote.setWCP1( pixel_local.MapTo( new floatPoint2D(
   						(float)tempp1.x, 
   						(float)tempp1.y) ) );
   	}
   	else if( name.indexOf("Shift") > -1 )
   	{
   	  if( name.equals("Shift-UP") )
   	  {
   	     //if( tempp2.y > 0 )
   		tempp2.y = tempp2.y - 1;
   	  }	       
   	  else if( name.equals("Shift-DOWN") )
   	  {
   	     //if( tempp2.y < current_bounds.getHeight() )
   		tempp2.y = tempp2.y + 1;
          }	       
   	  else if( name.equals("Shift-LEFT") )
   	  {
   	     //if( tempp2.x > 0 )
   		tempp2.x = tempp2.x - 1;
   	  }
   	  else if( name.equals("Shift-RIGHT") )
   	  {
   	     //if( tempp2.x < current_bounds.getWidth() )
   		tempp2.x = tempp2.x + 1;
   	  }
   	  tempnote.setWCP2( pixel_local.MapTo( new floatPoint2D(
   						(float)tempp2.x, 
   						(float)tempp2.y) ) );
   	}
   	
   	this_panel.repaint();		
      }     
    } // end KeyAction
   
   /*
    * This class listens to the ColorScaleImage on the AnnotationEditor.
    * This listener allows for annotation color change.
    * This listener corresponds to the old color chooser that used
    * the ColorScaleImage.
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
    }*/
    
   /*
    * This class changes the color of annotations.
    */
    class ColorChangeListener implements ActionListener
    {
      public void actionPerformed( ActionEvent ae )
      {
        String message = ae.getActionCommand();
        if( message.equals( ColorSelector.COLOR_CHANGED ) )
	{
	  ColorSelector cs = (ColorSelector)ae.getSource();
	  text_color = cs.getSelectedColor();
	  line_color = cs.getSelectedColor();
	  this_panel.repaint();
	}
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

   /*
    * This class handles the note list combobox.
    */
    class NoteListListener implements ActionListener
    {
      public void actionPerformed( ActionEvent e )
      {
 	JComboBox temp = ((JComboBox)e.getSource());
	// make sure selected index is not less than zero.
	if( !(temp.getSelectedIndex() < 0) )
	{
	  text.setText(temp.getSelectedItem().toString());
	}
	else
	  text.setText("");
      }
    }
    
    class EditorListener extends ComponentAdapter
    {
      public void componentResized( ComponentEvent we )
      {
    	editor_bounds = editor.getBounds();
      }
    }	     
  }
  
 /*
  * This class will hide the AnnotationEditor if the editor is visible but
  * the overlay is not.
  */
  private class NotVisibleListener extends ComponentAdapter
  {
    public void componentHidden( ComponentEvent ce )
    {
      editor.setVisible(false);
    }
  }
}
