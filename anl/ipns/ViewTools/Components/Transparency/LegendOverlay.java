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
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.TextAction;
import javax.swing.text.Keymap;
import java.awt.event.*;

import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.Util.Numeric.Format;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.ViewControls.*;
import gov.anl.ipns.ViewTools.UI.*;
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
  * "Axes Displayed" - This constant String is a key for referencing the state
  * information about which axes are to be displayed by the axis overlay.
  * The value that this key references is a primative integer. The integer
  * values are specified by public variables NO_AXES, X_AXIS, Y_AXIS, and
  * DUAL_AXES.
  */
  public static final String LEGEND_DISPLAYED = "Legend Displayed";

 /**
  * "Editor Bounds" - This constant String is a key for referencing the state
  * information about the size and bounds of the Axis Editor window. 
  * The value that this key references is a Rectangle. The Rectangle contains
  * the dimensions for the editor.
  */
  public static final String EDITOR_BOUNDS  = "Editor Bounds";    

  private static JFrame helper = null;
  

  private transient ILegendAddible component;
  private Font f;
  private boolean legend_drawn;
  private transient LegendOverlay this_panel;
  private transient LegendEditor editor;
  private transient Rectangle current_bounds;
  private Rectangle editor_bounds = new Rectangle(0,0,500,140);
  private String[] graphs;
  private int[] selectedGraphs;
  private GraphData[] lineInfo;
  private int x_offset = 0;
  private int y_offset = 0;
  private boolean draw_border = true;
  private String legend_label = "";

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
         graphs[i] = (String)("Group ID: " +  ila.getText(selectedGraphs[i]));
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
    
    temp = new_state.get(LEGEND_DISPLAYED);
    if( temp != null )
    {
      legend_drawn = ((Boolean)temp).booleanValue(); 
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
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */ 
  public ObjectState getObjectState( boolean isDefault )
  {
    ObjectState state = new ObjectState();

    state.insert( EDITOR_BOUNDS, editor_bounds );
    state.insert( FONT, f );
    state.insert( LEGEND_DISPLAYED, new Boolean(legend_drawn) );
    if( !isDefault )
     {
      
     }    
    return state;
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
      WindowShower shower = new WindowShower(editor);
      java.awt.EventQueue.invokeLater(shower);
      shower = null;
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

 
 /**
  * This method will paint the legend.
  * These graphics will overlay onto a jpanel.
  *
  *  @param  g - graphics object
  */
  public void paint(Graphics g)
  {
    Graphics2D g2d = (Graphics2D)g;
    f.deriveFont(0.1f);
    g2d.setFont(f);
    FontMetrics fontdata = g2d.getFontMetrics();
    // System.out.println("Precision = " + precision);
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
    //legend border width =120 hieght = 25 per line
    // left line
   if(draw_border)
   {
    g2d.drawLine((int)current_bounds.getX()+(int)current_bounds.getWidth() - 125
                 + x_offset,
                 (int)current_bounds.getY()+5 + y_offset,
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 125
                 + x_offset,
                 (int)current_bounds.getY()+10 + 20*(int)graphs.length
                 + y_offset );
    //top line
    g2d.drawLine((int)current_bounds.getX()+(int)current_bounds.getWidth() - 125
                 + x_offset,
                 (int)current_bounds.getY()+5 + y_offset,
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 120
                 + x_offset,
                 (int)current_bounds.getY()+5 + y_offset);
    g2d.drawString(  legend_label,
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 118
                 + x_offset,
                 (int)current_bounds.getY()+10 + y_offset);
    if((120 - 7.6f * legend_label.length()) > 0)
      g2d.drawLine((int)current_bounds.getX()+(int)current_bounds.getWidth() -
                 (int)(120 - 7.6f * legend_label.length())
                 + x_offset,
                 (int)current_bounds.getY()+5 + y_offset,
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 5
                 + x_offset,
                 (int)current_bounds.getY()+5 + y_offset);
   
    //right line
    g2d.drawLine((int)current_bounds.getX()+(int)current_bounds.getWidth() - 5
                 + x_offset,
                 (int)current_bounds.getY()+5 + y_offset,
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 5
                 + x_offset,
                 (int)current_bounds.getY()+10 + 20*(int)graphs.length
                 + y_offset );
    //Bottom line
    g2d.drawLine((int)current_bounds.getX()+(int)current_bounds.getWidth() - 125
                 + x_offset,
                 (int)current_bounds.getY()+10 + 20*(int)graphs.length 
                 + y_offset,
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 5
                 + x_offset,
                 (int)current_bounds.getY()+10 + 20*(int)graphs.length
                 + y_offset );
   }
    //Draw text
    for (int index = 0; index < graphs.length; index++)
      {
        g2d.drawString( graphs[index],
        (int)current_bounds.getX()+(int)current_bounds.getWidth() - 120
        + x_offset,
        (int)current_bounds.getY()+ 5 + 20*(index+1)+ y_offset );
      }

    //Draw line
    for (int index = 0; index < graphs.length; index++)
      {
         g2d.setStroke(lineInfo[index].Stroke);
         g2d.setColor(lineInfo[index].color);
         g2d.drawLine((int)current_bounds.getX()+(int)current_bounds
                              .getWidth() - 40 + x_offset,
                 (int)current_bounds.getY() + 20*(index+1) + y_offset,
                 (int)current_bounds.getX() +(int)current_bounds.getWidth() - 10
                 + x_offset,
                 (int)current_bounds.getY() + 20*(index+1) + y_offset );
      }
    //Draw marks
      int size = 2;
      for (int index = 0; index < graphs.length; index++)
      {
         g2d.setColor(lineInfo[index].markcolor);
         int x_int =(int)current_bounds.getX()+(int)current_bounds
                            .getWidth() - 30 + x_offset;
         int y_int =(int)current_bounds.getY() + 20*(index+1)
                            + y_offset;

         if( lineInfo[index].marktype == 0){}
         else if( lineInfo[index].marktype == 1)
         {
            g2d.drawLine( x_int, y_int,
                              x_int, y_int );
         }
         else if ( lineInfo[index].marktype == 2 )
         {
            g2d.drawLine( x_int-size, y_int,
                            x_int+size, y_int      );
            g2d.drawLine( x_int,     y_int-size,
                            x_int,      y_int+size );
         }
         else if ( lineInfo[index].marktype == 3 )
         {
           g2d.drawLine( x_int-size, y_int,
                             x_int+size, y_int      );
           g2d.drawLine( x_int,      y_int-size,
                             x_int,      y_int+size );
           g2d.drawLine( x_int-size, y_int-size,
                             x_int+size, y_int+size );
           g2d.drawLine( x_int-size, y_int+size,
                             x_int+size, y_int-size );
         }
         else if ( lineInfo[index].marktype == 4 )
         {
           g2d.drawLine( x_int-size, (y_int-size),
                    x_int-size, (y_int+size) );
           g2d.drawLine( x_int-size, y_int+size,
                     x_int+size, y_int+size );
           g2d.drawLine( x_int+size, y_int+size,
                     x_int+size, y_int-size );
           g2d.drawLine( x_int+size, y_int-size,
                     x_int-size, y_int-size );
         }
         else   // type = CROSS
         {
           g2d.drawLine( x_int-size, y_int-size,
                     x_int+size, y_int+size );
           g2d.drawLine( x_int-size, y_int+size,
                     x_int+size, y_int-size );
         }
      }
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
    private ControlCheckbox border_check = 
                                  new ControlCheckbox(true);
    
    public LegendEditor(String[] graphs)
    {
      super("Legend Editor");
      this_editor = this;
      this.getContentPane().setLayout( new GridLayout(1,3) );
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
            graphs[selectedLineBox.cbox.getSelectedIndex()] = 
                                                labelField.getText();
            this_panel.repaint();
	    
          }
          else if( message.equals("Change Legend Label"))
          {
            legend_label = legend_field.getText();
            this_panel.repaint();
          } 
          else if( message.equals("Close") )
          { 
	    editor_bounds = this_editor.getBounds(); 
            this_editor.dispose();
            this_panel.repaint();
          }
        }
        else if( e.getSource() instanceof LabelCombobox )
        {
          String message = e.getActionCommand();
          if( message.equals("COMBOBOX_CHANGED") )
          {
             labelField.setText(graphs[selectedLineBox.cbox.getSelectedIndex()]);        
          }  
        }
        else if( e.getSource() instanceof ControlCheckbox )
        {
          String message = e.getActionCommand();
          if (message.equals("Checkbox Changed") )
          draw_border = border_check.isSelected();
          this_panel.repaint();
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
