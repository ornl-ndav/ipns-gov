/*
 * File: LegendOverlay.java
 *
 * Copyright (C) 2003, Brent Serum
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
 * Primary   Brent Serum <serumb@uwstout.edu>
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
 *  Revision 1.20  2007/06/06 17:21:59  amoe
 *  -After every action from the LegendEditor's components, focus will be
 *  requested back to the titleField.  This is so the input keys for moving
 *  the legend will be heard.
 *  -Edited the help pane so it would be a bit more easier to read.
 *
 *  Revision 1.19  2007/06/01 19:33:08  amoe
 *  -Added note comment to strokeType(..), regarding transparent lines.
 *  -Added if-statement to drawLegend(..), so the sample lines would not
 *  be drawn if the graph line was transparent.
 *  -Changed position of sample point markers, since the sample lines
 *  were moved in previous CVS change.
 *
 *  Revision 1.18  2007/05/31 19:24:10  amoe
 *  - Changed the legend so the sample line would be displayed left of
 *  the graph label.
 *  - Changed the legend box so it now auto-sizes around the length of
 *  the graph labels and the legend title.
 *
 *  Revision 1.17  2007/04/13 19:59:24  amoe
 *  - Added Bar mark type for Legend.
 *  - Changed hardcoded constants to public GraphJPanel constants,
 *   in drawLegend(..) for the marks.
 *
 *  Revision 1.16  2006/07/25 16:06:31  amoe
 *  Made the sample line in the legend box slightly smaller in order to make 
 *  room for the corresponding text.
 *
 *  Revision 1.15  2005/11/11 05:35:56  serumb
 *  Added object state variables for the legend.
 *
 *  Revision 1.14  2005/10/07 18:37:21  serumb
 *  Removed extra label in front of graph title.
 *
 *  Revision 1.13  2005/05/25 20:28:33  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.12  2005/03/28 06:00:40  serumb
 *  Now uses new methods for instances of labelComboBox.
 *
 *  Revision 1.11  2005/03/14 19:25:20  serumb
 *  Added call to get the combobox instead of using the public variable.
 *
 *  Revision 1.10  2005/03/14 18:20:57  serumb
 *  Fixed eclipse warning.
 *
 *  Revision 1.9  2005/03/11 19:54:19  serumb
 *  Added method to get the stroke using an integer key that is passed in as
 *  a parameter.
 *
 *  Revision 1.8  2005/01/20 23:05:52  millermi
 *  - Added super.paint(g) to paint method.
 *
 *  Revision 1.7  2005/01/10 16:14:45  dennis
 *  Removed unused imports.
 *
 *  Revision 1.6  2004/07/02 19:15:49  serumb
 *  Added controls for help and close to the editor.
 *
 *  Revision 1.5  2004/06/23 19:04:23  serumb
 *  Initialized the legend label to an empty string.
 *
 *  Revision 1.4  2004/06/23 18:58:44  serumb
 *  Added controls to change the Legend label and to hide the border.
 *
 *  Revision 1.2  2004/06/17 22:36:27  serumb
 *  Added controls to move the legend by holding the control key
 *  and pressing a directional key while the legend editor is open.
 *
 *  Revision 1.1  2004/06/10 23:29:49  serumb
 *  Initial version of a class that displays a legend on a graph.
 *
 */

package gov.anl.ipns.ViewTools.Components.Transparency;

import javax.swing.*; 
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import javax.swing.text.TextAction;
import javax.swing.text.Keymap;
import java.awt.event.*;

import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.ViewControls.*;
import gov.anl.ipns.ViewTools.Panels.Graph.*;

/**
 * This class is used by view components to add a legend 
 * to a graph.
 */


public class LegendOverlay extends OverlayJPanel
{

  // these public variables are for preserving axis state information  
  
 /**
  * "Font" - This constant String is a key for referencing the state
  * information about the font of the calibrations for this axis overlay.
  * The value that this key references is of type Font.
  */
  public static final String FONT	    = "Font";
    
 /**
  * "Legend Label" - This constant String is a key for referencing the state
  * information about the legend label.
  */
  public static final String LEGEND_LABEL = "Legend Label";

 /**
  * "Legend Border" - This constant String is a key for referencing the state
  * information about the legend border.
  */
  public static final String LEGEND_BORDER = "Legend Border";

 /**
  * "Editor Bounds" - This constant String is a key for referencing the state
  * information about the size and bounds of the Axis Editor window. 
  * The value that this key references is a Rectangle. The Rectangle contains
  * the dimensions for the editor.
  */
  public static final String EDITOR_BOUNDS  = "Editor Bounds";    

  public static final int DOTTED   = 6;
  public static final int DASHED   = 7;
  public static final int LINE     = 8;
  public static final int DASHDOT  = 9;
  public static final int TRANSPARENT  = 10;
	    
  private static JFrame helper = null;
  private transient ILegendAddible component;
  private Font f;
  private transient LegendOverlay this_panel;
  private transient LegendEditor editor;
  private transient Rectangle current_bounds;
  private Rectangle editor_bounds = new Rectangle(0,0,500,180);
  private transient String[] graphs;
  private transient int[] selectedGraphs;
  private transient GraphData[] lineInfo;
  private transient int x_offset = 0;
  private transient int y_offset = 0;
  private boolean draw_border = true;
  private transient String legend_label = "";

 /**
  * Constructor for initializing a new LegendOverlay
  *
  *  @param  ila - ILegendAddible object
  */ 
  public LegendOverlay(ILegendAddible ila)
  {
    super();
    addComponentListener( new NotVisibleListener() );
    component = ila;
    f = ila.getFont();
    selectedGraphs = ila.getSelectedGraphs();
    graphs = new String[selectedGraphs.length];
    lineInfo = new GraphData[selectedGraphs.length];
    if( selectedGraphs.length > 0)
    {
      for (int i=0; i < selectedGraphs.length; i++)
      {
        graphs[i] =   ila.getText(selectedGraphs[i]);
        lineInfo[i] = ila.getGraphData(i+1);
      }
    }

    editor = new LegendEditor(graphs);
    this_panel = this;
    this_panel.setVisible(false);
  }
  
 /**
  * Constructor for creating a new LegendOverlay with previous state settings.
  *
  *  @param  ila - ILegendAddible object
  *  @param  state - previously saved state
  */ 
  public LegendOverlay(ILegendAddible ila, ObjectState state)
  {
    this(ila);
    setObjectState(state);
  }
 
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    boolean redraw = false;  // if any values are changed, repaint overlay.
    Object temp = new_state.get(FONT);
    if( temp != null )
    {
      f = (Font)temp;
      redraw = true;  
    }  
    
    temp = new_state.get(EDITOR_BOUNDS);
    if( temp != null )
    {
      editor_bounds = (Rectangle)temp;
      editor.setBounds( editor_bounds );
      redraw = true;  
    }
    
    temp = new_state.get(LEGEND_LABEL);    
    if( temp != null )
    {
      legend_label = (String)temp;
      redraw = true;
    }

    temp = new_state.get(LEGEND_BORDER);
    if( temp != null )
    {
      draw_border = ((Boolean)temp).booleanValue();
      redraw = true;
    }

    if( redraw )
      this_panel.repaint();
  }
 
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */ 
  public ObjectState getObjectState( boolean isDefault )
  {
    ObjectState state = new ObjectState();

    state.insert( EDITOR_BOUNDS, editor_bounds );
    state.insert( FONT, f );
    state.insert( LEGEND_BORDER, new Boolean(draw_border) );
    state.insert( LEGEND_LABEL, legend_label );
    if( !isDefault )
    {
      //
    }    
    return state;
  }

 /**
  * Contains/Displays control information about this overlay.
  */
  public static void help()
  {
    helper = new JFrame("Help for Legend Overlay");
    helper.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
    helper.setBounds(0,0,575,500);

    JEditorPane textpane = new JEditorPane();
    textpane.setEditable(false);
    textpane.setEditorKit( new HTMLEditorKit() );
    String text = "<H1>Description:</H1><P>" +
                  "The Legend Overlay is used to add an on-screen legend to " +
                  "a selected set of graphs. Below are some basic commands " +
                  " necessary for creating the legend.</P>" +
                  "<P><I>Note:</I><BR>" +
                  "- These commands will NOT work if the Legend " +
                  "Overlay checkbox IS NOT checked.<BR><BR>" +
                  "<H2>LegendEditor Commands <BR>" +
                  "(Edit Button under Annotation Overlay Control)</H2>" +    
                  "<P>To move LEGEND: Click on the Legend Editor window's gray space and hold <Ctrl> and press any arrow key.</P>"+
                  "<P>To toggle LEGEND BORDER: Click on \"Display Border\" checkbox.</P>"+
                  "<P>To SELECT GRAPH: Click on \"Graph\" combo-box and click on a graph title.</P>"+
                  "<P>To change GRAPH LABEL: Click on the textfield, type in a new label, and click on \"Change Label\" button.</P>"+
                  "<P>To change LEGEND LABEL: Click on the textfield, type in a new label, and click on \"Change Legend Label\" button.</P>";
    textpane.setText(text);
    JScrollPane scroll = new JScrollPane(textpane);
    scroll.setVerticalScrollBarPolicy(
                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    WindowShower.show(helper);
  }


 /**
  * This method displays the LegendEditor.
  */ 
  public void editLegend()
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
      editor = new LegendEditor(graphs);
      WindowShower.show(editor);

      editor.toFront();
    }
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

 /*------------------------------ StrokeType --------------------------------*/
 /**
  *  Makes the different stroke types to be returned
  *
  *  @param  key         the integer constant for the stroke types.
  *
  *  @param  graph_num   the index of the graph.
  *                      The index must be at least zero and less than the
  *                      number of graphs currently held in this GraphJPanel.
  *                      If the graph_num is not valid, this method has no
  *                      effect and returns false.
  *
  *  @return             the stroke type for the particular key.
  */    
  public BasicStroke strokeType(int key, int graph_num)
  {
    if (graph_num < 0 || graph_num >= graphs.length )    // no such graph
      return new BasicStroke();
                                                                                                     
    if (key == DASHED)
    {
      float dash1[] = {10.0f};
      BasicStroke dashed = new BasicStroke(lineInfo[graph_num].linewidth,
      BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, dash1, 0.0f);
      return dashed;
    }
    else if (key == DOTTED)
    {
      float dots1[] = {0,6,0,6};
      BasicStroke dotted = new BasicStroke(lineInfo[graph_num].linewidth,
      BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, dots1, 0);
      return dotted;
    }
    else if (key == LINE)
    {
      BasicStroke stroke = new BasicStroke(lineInfo[graph_num].linewidth);
      return stroke;
    }
    else if (key ==DASHDOT)
    {
      float[] dash2 = {6.0f, 4.0f, 2.0f, 4.0f, 2.0f, 4.0f};
      BasicStroke dashdot = new BasicStroke(lineInfo[graph_num].linewidth, 
      BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, dash2, 0.0f);
      return dashdot;
    }
    else if (key == TRANSPARENT)
    {
      //NOTE:Transparency is not set as a stroke, the sample line is simply not
      // drawn when transparent.  This is in drawLegend(..) .
      
      //float clear[] = {0.00f, 100.00f};
      BasicStroke transparent = new BasicStroke(0.0f);
      return transparent;
    }
    else
    {
      System.out.println("ERROR: no Stroke of this type, default is returned");
      return new BasicStroke();
    }
  }
                                            
 
 /**
  * This method will paint the legend.
  * These graphics will overlay onto a jpanel.
  *
  *  @param  g - graphics object
  */
  public void paint(Graphics g)
  {
    super.paint(g);
    Graphics2D g2d = (Graphics2D)g;
    f.deriveFont(0.1f);
    g2d.setFont(f);
    //FontMetrics fontdata = g2d.getFontMetrics();
    //System.out.println("Precision = " + precision);
    updateTransform();
    
    drawLegend(g2d);
   }  
   
 /*
  * This method will get the current bounds of the center and reset
  * the transform that converts pixel to world coords.
  */
  private void updateTransform()
  {
    current_bounds = component.getRegionInfo(); // current size of center 
  }


 /*
  * This method draws the legend in the top left corner of the graph.
  */
  private void drawLegend(Graphics2D g2d)
  {	  
	  FontMetrics f_metric = g2d.getFontMetrics();
    
	  //The maximum width of labels, sample lines, and the legend label
	  int maxLablWidth = getMaxStringWidth(graphs,f_metric);
	  int maxSampLineWidth = 20;
	  int maxLegLablWidth = f_metric.stringWidth(legend_label);
	  
    //The maximum total width will be (maxLablWidth + maxSampLineWidth) or 
    //maxLegLablWidth, whichever one is bigger.
	  int maxTotalWidth = ( (maxLablWidth+maxSampLineWidth) > maxLegLablWidth ? 
        (maxLablWidth+maxSampLineWidth) : maxLegLablWidth);
	  
    
	  if(draw_border)
	  {            
	    //Left line
	    g2d.drawLine( (int)current_bounds.getX() + (int)current_bounds.getWidth()
	        - (maxTotalWidth+17) + x_offset,
                    (int)current_bounds.getY() + 5 + y_offset,
                    (int)current_bounds.getX() + (int)current_bounds.getWidth()
                    - (maxTotalWidth+17) + x_offset,
                    (int)current_bounds.getY() + 10 + (20*(int)graphs.length) +
                    y_offset );
    
	    //Top line (pre legend_label - left)
	    g2d.drawLine( (int)current_bounds.getX() + (int)current_bounds.getWidth()
          - (maxTotalWidth+17) + x_offset,
                    (int)current_bounds.getY() + 5 + y_offset,
                    (int)current_bounds.getX() + (int)current_bounds.getWidth()
                    - (maxTotalWidth+13) + x_offset,
                    (int)current_bounds.getY() + 5 + y_offset);
    
	    //Legend label
	    g2d.drawString( legend_label,
                      (int)current_bounds.getX() + (int)current_bounds.getWidth()
                      - (maxTotalWidth+11) + x_offset,
                      (int)current_bounds.getY() + 10 + y_offset);

	    //Top line (post legend_label - right)
	    g2d.drawLine( (int)current_bounds.getX() + (int)current_bounds.getWidth()
          - (maxTotalWidth+(13-maxLegLablWidth)) + x_offset,
                    (int)current_bounds.getY() + 5 + y_offset,
                    (int)current_bounds.getX() + (int)current_bounds.getWidth()
                    - 5 + x_offset,
                      (int)current_bounds.getY() + 5 + y_offset);
        
	    //Right line
	    g2d.drawLine( (int)current_bounds.getX() + (int)current_bounds.getWidth()
          - 5 + x_offset,
                  (int)current_bounds.getY() + 5 + y_offset,
                  (int)current_bounds.getX() + (int)current_bounds.getWidth()
                  - 5 + x_offset,
                  (int)current_bounds.getY() + 10 + (20*(int)graphs.length)
                  + y_offset );

	    //Bottom line
	    g2d.drawLine( (int)current_bounds.getX() + (int)current_bounds.getWidth()
          - (maxTotalWidth+17) + x_offset,
                  (int)current_bounds.getY() + 10 + (20*(int)graphs.length) + 
                  y_offset,
                  (int)current_bounds.getX() + (int)current_bounds.getWidth() -
                  5 + x_offset,
                  (int)current_bounds.getY() + 10 + (20*(int)graphs.length) + 
                  y_offset );
	  }
    
    //Graph labels
    for (int index = 0; index < graphs.length; index++)
    {  
      g2d.drawString( graphs[index],
                (int)current_bounds.getX() + (int)current_bounds.getWidth() - 
                ((maxTotalWidth)-12) + x_offset,
                (int)current_bounds.getY() + 5 + (20*(index+1)) + y_offset );        
    }
    
    //Graph sample line
    for (int index = 0; index < graphs.length; index++)
    {
      g2d.setStroke(strokeType(lineInfo[index].linetype, index) );      
      g2d.setColor(lineInfo[index].color);        
      
      //if the line is transparent, don't draw it
      if(!lineInfo[index].transparent)
        g2d.drawLine( (int)current_bounds.getX() + (int)current_bounds.getWidth()
            - (maxTotalWidth + 13) + x_offset,
                   (int)current_bounds.getY() + (20*(index+1)) + y_offset,
                   (int)current_bounds.getX() + (int)current_bounds.getWidth() - 
                   ((maxTotalWidth)-7) + x_offset,
                   (int)current_bounds.getY() + (20*(index+1)) + y_offset );
    }
    
    //Graph sample line marks
    int size = 2;
    for (int index = 0; index < graphs.length; index++)
    {
      g2d.setStroke(new BasicStroke(1));
      g2d.setColor(lineInfo[index].markcolor);
         
      int x_int = (int)current_bounds.getX() + (int)current_bounds.getWidth() -
        (maxTotalWidth + 3) + x_offset;
      int y_int = (int)current_bounds.getY() + (20*(index+1)) + y_offset;
         
      if( lineInfo[index].marktype == GraphJPanel.DOT)
      {
        g2d.drawLine( x_int, y_int, x_int, y_int );
      }
      else if ( lineInfo[index].marktype == GraphJPanel.PLUS )
      {
        g2d.drawLine( x_int - size, y_int, x_int + size, y_int );            
        g2d.drawLine( x_int, y_int - size, x_int, y_int + size );
      }
      else if ( lineInfo[index].marktype == GraphJPanel.STAR )
      {
        g2d.drawLine( x_int - size, y_int, x_int + size, y_int );           
        g2d.drawLine( x_int, y_int - size, x_int, y_int + size );           
        g2d.drawLine( x_int - size, y_int - size, x_int + size, y_int + size );           
        g2d.drawLine( x_int - size, y_int + size, x_int + size, y_int - size );
      }
      else if ( lineInfo[index].marktype == GraphJPanel.BOX )
      {
        g2d.drawLine( x_int - size, y_int - size, x_int - size, y_int + size );           
        g2d.drawLine( x_int - size, y_int + size, x_int + size, y_int + size );           
        g2d.drawLine( x_int + size, y_int + size, x_int + size, y_int - size );           
        g2d.drawLine( x_int + size, y_int - size, x_int - size, y_int - size );
      }
      else if( lineInfo[index].marktype == GraphJPanel.CROSS )
      {
        g2d.drawLine( x_int - size, y_int - size, x_int + size, y_int + size );           
        g2d.drawLine( x_int - size, y_int + size, x_int + size, y_int - size );
      }
      else if( lineInfo[index].marktype == GraphJPanel.BAR )
      {
        g2d.drawLine( x_int, y_int - size, x_int, y_int + size );
      }
      else
      {
        //System.out.println(":No Point Marks");
      }         
    }
  }

 /*
  * This method takes in an array of strings and a font metric, and finds
  * the widest string in the specified font.
  */
  private int getMaxStringWidth(String[] s_arr,FontMetrics fm)
  {
    int longestWidth = 0;
    int currentWidth;
    for(int i=0;i<s_arr.length;i++)
    {
      currentWidth = fm.stringWidth(s_arr[i]);
      if( currentWidth > longestWidth)
		  {
			  longestWidth = currentWidth;
		  }
	  }
    
	  return(longestWidth);		  
  }
 
  private class LegendEditor extends JFrame
  {
    private LegendEditor this_editor;
    private LabelCombobox selectedLineBox;
    private JTextField labelField = new JTextField((int)20);      
    private JTextField titleField = new JTextField(null);
    private JLabel the_label = new JLabel("   Legend Label   "); 
    private JTextField legend_field = new JTextField(legend_label);      
    private JButton changebutton = new JButton("Change Label");
    private JButton changebutton2 = new JButton("Change Legend Label");
    private Box vertical = new Box(1);
    private Box topBox = new Box(0);
    private Box middle = new Box(0);
    private Box bottomBox = new Box(0);
    private ControlCheckbox border_check = new ControlCheckbox(draw_border);
    
    public LegendEditor(String[] graphs)
    {
      super("Legend Editor");
      this_editor = this;
      this.getContentPane().setLayout( new GridLayout(1,4) );
      this.setBounds(editor_bounds);
      this.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
      
      if(graphs.length > 0) 
        labelField.setText(graphs[0]);

      selectedLineBox = new LabelCombobox("Graph", graphs);
      selectedLineBox.addActionListener( new ControlListener() );
 
      titleField.setEditable(false);

      changebutton.addActionListener( new ControlListener() );
      changebutton2.addActionListener( new ControlListener() );

      border_check.setText("Display Border");
      border_check.addActionListener( new ControlListener() );
     
      JButton help = new JButton("Help");
      help.addActionListener( new ControlListener() );
      JButton closebutton = new JButton("Close");
      closebutton.addActionListener( new ControlListener() );
      // For layout reasons, put both of them into this JPanel.
      JPanel close_and_help = new JPanel( new GridLayout(1,2) );
      close_and_help.add(closebutton);
      close_and_help.add(help);
 
      // this jpanel groups all other miscellaneous options into one row.
      JPanel labelPanel = new JPanel( new GridLayout() );
      vertical.add(titleField);
      topBox.add(border_check);
      middle.add(selectedLineBox);
      middle.add(labelField);
      middle.add(changebutton);
      bottomBox.add(the_label);
      bottomBox.add(legend_field);
      bottomBox.add(changebutton2);
      vertical.add(topBox);
      vertical.add(middle);
      vertical.add(bottomBox);
      vertical.add(close_and_help);
      labelPanel.add(vertical);

      // These commands will create key events for moving the Legend
      //*********************************************************************
      Keymap km = titleField.getKeymap();
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
    
      labelPanel.remove(titleField);
      this.getContentPane().add( labelPanel );
      this_editor.addComponentListener( new EditorListener() );
    }
    
    class ControlListener implements ActionListener
    {
      public void actionPerformed( ActionEvent e )
      {
        if( e.getSource() instanceof JButton )
        {
          String message = e.getActionCommand();
          if( message.equals("Change Label") )
          {
            graphs[selectedLineBox.getSelectedIndex()] = labelField.getText();
            this_panel.repaint();
            titleField.requestFocus();	    
          }
          else if( message.equals("Change Legend Label"))
          {
            legend_label = legend_field.getText();
            this_panel.repaint();
            titleField.requestFocus();
          } 
          else if( message.equals("Close") )
          { 
            editor_bounds = this_editor.getBounds(); 
            this_editor.dispose();
            this_panel.repaint();
          }
          else if( message.equals("Help") )
          {
            help();
          }
        }
        else if( e.getSource() instanceof LabelCombobox )
        {
          String message = e.getActionCommand();
          if( message.equals("COMBOBOX_CHANGED") )
          {
             labelField.setText(graphs[selectedLineBox.getSelectedIndex()]);        
          }
          titleField.requestFocus();
        }
        else if( e.getSource() instanceof ControlCheckbox )
        {
          String message = e.getActionCommand();
          if (message.equals("Checkbox Changed") )
          draw_border = border_check.isSelected();
          this_panel.repaint();
          titleField.requestFocus();
        }    
      }
    }
    
    class EditorListener extends ComponentAdapter
    {
      public void componentResized( ComponentEvent we )
      {
        editor_bounds = editor.getBounds();
      }
    }
    
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
        if( name.indexOf("Ctrl") > -1 )
        {
          if( name.equals("Ctrl-UP") )
          {
            y_offset--;
          }
          else if( name.equals("Ctrl-DOWN") )
          {
            y_offset++;
          }
          else if( name.equals("Ctrl-LEFT") )
          {
            x_offset--;
          }
          else if( name.equals("Ctrl-RIGHT") )
          {
            x_offset++;
          }

          this_panel.repaint();
        }
      } 
    }	    
  }
  
 /*
  * This class will hide the LegendEditor if the editor is visible but
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
