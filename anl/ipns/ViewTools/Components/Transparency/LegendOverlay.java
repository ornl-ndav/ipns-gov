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
 *  Revision 1.1  2004/06/10 23:29:49  serumb
 *  Initial version of a class that displays a legend on a graph.
 *
 */

package gov.anl.ipns.ViewTools.Components.Transparency;

import javax.swing.*; 
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

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
  private Rectangle editor_bounds = new Rectangle(0,0,500,70);
  private String[] graphs;
  private int[] selectedGraphs;
  private GraphData[] lineInfo;

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

   //legend border width =120 hieght = 25 per line
    // left line
    g2d.drawLine((int)current_bounds.getX()+(int)current_bounds.getWidth() - 125,
                 (int)current_bounds.getY()+5,
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 125,
                 (int)current_bounds.getY()+10 + 20*(int)graphs.length );
    //top line
    g2d.drawLine((int)current_bounds.getX()+(int)current_bounds.getWidth() - 125,
                 (int)current_bounds.getY()+5,
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 120,
                 (int)current_bounds.getY()+5);
    g2d.drawString( "Legend", 
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 118,
                 (int)current_bounds.getY()+10);
    g2d.drawLine((int)current_bounds.getX()+(int)current_bounds.getWidth() - 74,
                 (int)current_bounds.getY()+5,
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 5,
                 (int)current_bounds.getY()+5);
    //right line
    g2d.drawLine((int)current_bounds.getX()+(int)current_bounds.getWidth() - 5,
                 (int)current_bounds.getY()+5,
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 5,
                 (int)current_bounds.getY()+10 + 20*(int)graphs.length );
    //Bottom line
    g2d.drawLine((int)current_bounds.getX()+(int)current_bounds.getWidth() - 125,
                 (int)current_bounds.getY()+10 + 20*(int)graphs.length,
                 (int)current_bounds.getX()+(int)current_bounds.getWidth() - 5,
                 (int)current_bounds.getY()+10 + 20*(int)graphs.length );
   
    //Draw text
    for (int index = 0; index < graphs.length; index++)
      {
        g2d.drawString( graphs[index], 
        (int)current_bounds.getX()+(int)current_bounds.getWidth() - 120,
        (int)current_bounds.getY()+ 5 + 20*(index+1) );
      }

    //Draw line
    for (int index = 0; index < graphs.length; index++)
      {  
         g2d.setStroke(lineInfo[index].Stroke);
         g2d.setColor(lineInfo[index].color);
         g2d.drawLine((int)current_bounds.getX()+(int)current_bounds
                                                    .getWidth() - 40,
                 (int)current_bounds.getY() + 20*(index+1),
                 (int)current_bounds.getX() +(int)current_bounds.getWidth() - 10,
                 (int)current_bounds.getY() + 20*(index+1) );
      }

    //Draw marks
      int size = 2;
      for (int index = 0; index < graphs.length; index++)
      {
         g2d.setColor(lineInfo[index].markcolor); 
         int x_int =(int)current_bounds.getX()+(int)current_bounds
                                                    .getWidth() - 30;
         int y_int =(int)current_bounds.getY() + 20*(index+1);         
         
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


  
   
 /*
  * This method will get the current bounds of the center and reset
  * the transform that converts pixel to world coords.
  */
  private void updateTransform()
  {
    current_bounds = component.getRegionInfo(); // current size of center 
   /* CoordBounds pixel_map =
            new CoordBounds( (float)current_bounds.getX(),
                             (float)current_bounds.getY(),
                             (float)(current_bounds.getX() +
                                     current_bounds.getWidth()),
                             (float)(current_bounds.getY() +
                                     current_bounds.getHeight() ) );
    pixel_local.setSource( pixel_map );
    pixel_local.setDestination( component.getLocalCoordBounds() );*/
  }

 /*
  * Converts from world coordinates to a pixel point
  */
 /* private Point convertToPixelPoint( floatPoint2D fp )
  {
    floatPoint2D fp2d = pixel_local.MapFrom( fp );
    return new Point( (int)fp2d.x, (int)fp2d.y );
  }*/

 /*
  * Converts from pixel coordinates to world coordinates.
  */
 /* private floatPoint2D convertToWorldPoint( Point p )
  {
    return pixel_local.MapTo( new floatPoint2D((float)p.x, (float)p.y) );
  }*/
  
  private class LegendEditor extends JFrame
  {
    private LegendEditor this_editor;
    private LabelCombobox selectedLineBox;
    private TextField labelField = new TextField((int)20);      
    private JButton changebutton = new JButton("Change");
    
    public LegendEditor(String[] graphs)
    {
      super("Legend Editor");
      this_editor = this;
      this.getContentPane().setLayout( new GridLayout() );
      this.setBounds(editor_bounds);
      this.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
      
      selectedLineBox = new LabelCombobox("Graph", graphs);
      selectedLineBox.addActionListener( new ControlListener() );
      

      changebutton.addActionListener( new ControlListener() );
      
      // this jpanel groups all other miscellaneous options into one row.
      JPanel labelPanel = new JPanel( new GridLayout() );
      labelPanel.add(selectedLineBox);
      labelPanel.add(labelField);
      labelPanel.add(changebutton);
      //miscoptions.add(
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
          if( message.equals("Change") )
          {
            graphs[selectedLineBox.cbox.getSelectedIndex()] = 
                                                labelField.getText();
            
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
