/*
 * File: AxisOverlay2D.java
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
 *  Revision 1.14  2003/09/24 01:32:40  millermi
 *  - Added static variables to be used as keys by ObjectState
 *  - Added methods setObjectState() and getObjectState() to adjust to
 *    changes made in OverlayJPanel.
 *  - Added componentResized() listener to set editor bounds
 *    when the editor is resized.
 *
 *  Revision 1.13  2003/08/14 21:46:32  millermi
 *  - Added toFront() to AxisEditor to display it over the viewer.
 *  - Added grid color changing abilities to AxisEditor
 *  - Added information pertaining to grid color into help()
 *  - Fixed bug that displayed grid line for special case minor tick when
 *    only drawing for major tick marks was selected.
 *
 *  Revision 1.12  2003/08/14 17:12:46  millermi
 *  - Edited help() to provide more description.
 *
 *  Revision 1.11  2003/08/06 13:55:57  dennis
 *  - Added Axis Editor to allow for the addition of grid lines.
 *  - Log calibration display changed. Previously all calibrations
 *    were shown, now a tick is shown only if it is at least 5 pixels
 *    from the last calibration drawn.
 *    (Mike Miller)
 *
 *  Revision 1.10  2003/07/25 14:42:57  dennis
 *  - Added if statement to ensure component is of type LogAxisAddible2D
 *    in paintLogX() and paintLogY(). (Mike Miller)
 *
 *  Revision 1.9  2003/07/05 19:44:21  dennis
 *  - Implemented private methods paintLogX() and paintLogY()
 *    (Mike Miller)
 *
 *  Revision 1.8  2003/06/18 22:14:28  dennis
 *  (Mike Miller)
 *  - Restructured the paint method to allow linear or log axis display.
 *    Paint method now calls private methods to paint the axis.
 *
 *  Revision 1.7  2003/06/18 13:35:25  dennis
 *  (Mike Miller)
 *  - Added method setDisplayAxes() to turn on/off x and/or y axes.
 *
 *  Revision 1.6  2003/06/17 13:22:14  dennis
 *  - Updated help menu. No functional changes made. (Mike Miller)
 *
 *  Revision 1.5  2003/06/09 22:33:33  dennis
 *  - Added static method help() to display commands via the HelpMenu.
 *    (Mike Miller)
 *
 *  Revision 1.4  2003/05/22 17:51:05  dennis
 *  Corrected problem of missing calibratons at beginning and end
 *  of y-axis and beginning of x-axis. (Mike Miller)
 *
 *  Revision 1.3  2003/05/16 14:56:12  dennis
 *  Added calibration intervals on the X-Axis when resizing. (Mike Miller)
 *
 */ 
// X axis has a constant position 50 for it's label

package DataSetTools.components.View.Transparency;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import DataSetTools.components.image.*; //ImageJPanel & CoordJPanel
import DataSetTools.components.View.*;
import DataSetTools.components.View.TwoD.IAxisAddible2D;
import DataSetTools.components.View.TwoD.ILogAxisAddible2D;
import DataSetTools.components.View.TwoD.ImageViewComponent;
import DataSetTools.util.Format; 
import java.lang.Math;

/**
 * This class is used by view components to calibrate a JPanel. Besides
 * calibration, axis labels and graph title are set here. To add grid lines,
 * make a call to editGridLines() to view the AxisEditor.
 */
public class AxisOverlay2D extends OverlayJPanel
{
   public static final int NO_AXES = 0;
   public static final int X_AXIS = 1;
   public static final int Y_AXIS = 2;
   public static final int DUAL_AXES = 3;
   public static final boolean LINEAR = true;
   public static final boolean LOG    = false;
   // these public variables are for preserving axis state information
   public static final String PRECISION      = "Precision";
   public static final String FONT           = "Font";
   public static final String AXES_DISPLAYED = "Axes Displayed";
   public static final String DRAW_LINEAR_X  = "Draw Linear X";
   public static final String DRAW_LINEAR_Y  = "Draw Linear Y";
   public static final String TWO_SIDED      = "Two Sided";
   public static final String GRID_DISPLAY_X = "Grid Display X";
   public static final String GRID_DISPLAY_Y = "Grid Display Y";
   public static final String GRID_COLOR     = "Grid Color";
   public static final String EDITOR_BOUNDS  = "Editor Bounds";
   
   // these variables simulate the interval of values of the data
   private float xmin;
   private float xmax;
   private float ymin;
   private float ymax;
   private int xaxis = 0;
   private int yaxis = 0;
   private int xstart = 0;
   private int ystart = 0;
   //private ILogAxisAddible2D component;
   private IAxisAddible2D component;
   private int precision;
   private Font f;
   private int axesdrawn;
   private boolean drawXLinear;
   private boolean drawYLinear;
   private boolean isTwoSided = true;
   private int gridxdisplay = 0;  // 0 = none, 1 = major, 2 = major/minor
   private int gridydisplay = 0;
   private AxisOverlay2D this_panel;
   private AxisEditor editor;
   private Color gridcolor = Color.black;
   private Rectangle editor_bounds = new Rectangle(0,0,400,110);
   
  /**
   * Default constructor
   */ 
   public AxisOverlay2D()
   {
      super();
      f = new Font("Default", Font.PLAIN, 12);
      xmin = 0;
      xmax = 1;
      ymin = 0;
      ymax = 1;
      VirtualArray2D va = new VirtualArray2D( 1, 1 );
      va.setDataValue(0,0,0);
      component = new ImageViewComponent( va );
      precision = 4;
      axesdrawn = DUAL_AXES;
      drawXLinear = true;
      drawYLinear = true;
      editor = new AxisEditor();
      this_panel = this;
   }
   
  /**
   * Constructor
   */ 
   public AxisOverlay2D(IAxisAddible2D iaa)
   {
      super();
      component = iaa;
      f = iaa.getFont();
      xmin = component.getAxisInfo(true).getMin();
      xmax = component.getAxisInfo(true).getMax();
      ymax = component.getAxisInfo(false).getMin();
      ymin = component.getAxisInfo(false).getMax(); 
      setPrecision( iaa.getPrecision() );
      setDisplayAxes( DUAL_AXES );
      setXAxisLinearOrLog( component.getAxisInfo(true).getIsLinear() );
      setYAxisLinearOrLog( component.getAxisInfo(false).getIsLinear() );
      editor = new AxisEditor();
      this_panel = this;
   }

  /**
   * Contains/Displays control information about this overlay.
   */
   public static void help()
   {
      JFrame helper = new JFrame("Help for Axes Overlay");
      helper.setBounds(0,0,600,400);
      JTextArea text = new JTextArea("Description:\n\n");
      text.setEditable(false);
      text.setLineWrap(true);

      text.append("The Axis Overlay provides calibration for data. Most " +
                  "viewers initially have this overlay on, however, turning " +
		  "it off provides more room for displaying data. This " +
		  "overlay may also be used for providing grid lines.\n\n");
      text.append("Commands for Axes Overlay\n\n");
      text.append("Note:\n" +
                  "- The Axes Overlay has no commands associated with it. " +
                  "Instead, it allows the commands of the underlying image.\n" +
                  "- These commands will NOT work if any other overlay " +
		  "is checked.\n\n");
      text.append("********************************************************\n");
      text.append("Commands for Underlying image (Without Edit button)\n");
      text.append("********************************************************\n");
      text.append("Click/Drag/Release MouseButton2>ZOOM IN\n");
      text.append("Click/Drag/Release Mouse w/Shift_Key>ZOOM IN ALTERNATE\n");
      text.append("Double Click Mouse>RESET ZOOM\n");
      text.append("Single Click Mouse>SELECT CURRENT POINT\n\n");
      text.append("********************************************************\n");
      text.append("Commands for AxisEditor (Edit button under Axis Overlay)\n");
      text.append("********************************************************\n");
      text.append("Use drop-down box to choose GRID OPTIONS for X and/or Y " +
                  "axis.\n");
      text.append("Click on \"Change Grid Color\" button to CHANGE GRID COLOR" +
                  " for both X and Y axis.\n\n");
      
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
     Object temp = new_state.get(PRECISION);
     if( temp != null )
     {
       precision = ((Integer)temp).intValue();
       redraw = true;  
     }
     
     temp = new_state.get(FONT);
     if( temp != null )
     {
       f = (Font)temp;
       redraw = true;  
     }  
     
     temp = new_state.get(AXES_DISPLAYED);
     if( temp != null )
     {
       axesdrawn = ((Integer)temp).intValue(); 
       redraw = true;  
     }  
     
     temp = new_state.get(DRAW_LINEAR_X);
     if( temp != null )
     {
       drawXLinear = ((Boolean)temp).booleanValue(); 
       redraw = true;  
     } 
     
     temp = new_state.get(DRAW_LINEAR_Y); 
     if( temp != null )
     {
       drawYLinear = ((Boolean)temp).booleanValue();
       redraw = true;  
     }  
     
     temp = new_state.get(TWO_SIDED);
     if( temp != null )
     {
       isTwoSided = ((Boolean)temp).booleanValue();
       redraw = true;  
     }  
     
     temp = new_state.get(GRID_DISPLAY_X);
     if( temp != null )
     {
       gridxdisplay = ((Integer)temp).intValue(); 
       redraw = true;  
     } 
     
     temp = new_state.get(GRID_DISPLAY_Y);
     if( temp != null )
     {
       gridydisplay = ((Integer)temp).intValue();
       redraw = true;  
     }  
     
     temp = new_state.get(GRID_COLOR);
     if( temp != null )
     {
       gridcolor = (Color)temp;
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
     state.insert( AXES_DISPLAYED, new Integer(axesdrawn) );
     state.insert( DRAW_LINEAR_X, new Boolean(drawXLinear) );
     state.insert( DRAW_LINEAR_Y, new Boolean(drawYLinear) );
     state.insert( FONT, f );
     state.insert( GRID_COLOR, gridcolor );
     state.insert( GRID_DISPLAY_X, new Integer(gridxdisplay) );
     state.insert( GRID_DISPLAY_Y, new Integer(gridydisplay) );
     state.insert( PRECISION, new Integer(precision) );
     state.insert( TWO_SIDED, new Boolean(isTwoSided) );
     state.insert( EDITOR_BOUNDS, editor_bounds );
     
     return state;
   }
  
  /**
   * Sets the significant digits to be displayed.
   *
   *  @param digits
   */ 
   public void setPrecision( int digits )
   {
      precision = digits;
   } 
   
  /**
   * Specify which axes should be drawn. Options are NO_AXES, X_AXIS, Y_AXIS,
   * or DUAL_AXES.
   *
   *  @param  display_scheme
   */ 
   public void setDisplayAxes( int display_scheme )
   {
      axesdrawn = display_scheme;
   }
   
   public void setTwoSided( boolean doublesided )
   {
      isTwoSided = doublesided;
   }
   
  /**
   * Specify x axis as linear or logarithmic. Options are LINEAR or LOG.
   *
   *  @param  display_scheme
   */ 
   public void setXAxisLinearOrLog( boolean isXLinear )
   {
     drawXLinear = isXLinear;
   }   
   
  /**
   * Specify y axis as linear or logarithmic. Options are LINEAR or LOG.
   *
   *  @param  display_scheme
   */ 
   public void setYAxisLinearOrLog( boolean isYLinear )
   {
     drawYLinear = isYLinear;
   }   
  
  /**
   * This method displays the AxisEditor.
   */ 
   public void editGridLines()
   {
     if( editor.isVisible() )
     {
       editor.toFront();
       editor.requestFocus();
     }
     else
     {
       editor.dispose();
       editor = new AxisEditor();
       editor.setVisible(true);
       editor.toFront();
     }
   }
   
  /**
   * This method sets the color for the grid lines. Initially set to black.
   *
   *  @param  color
   */
   public void setGridColor( Color c )
   {
      gridcolor = c;
      this_panel.repaint();
   }
  
  /**
   * This method creates tick marks and numbers for this transparency.
   * These graphics will overlay onto a jpanel.
   *
   *  @param  graphic
   */  
   public void paint(Graphics g) 
   {  
      Graphics2D g2d = (Graphics2D)g;
      g2d.setFont(f);
      FontMetrics fontdata = g2d.getFontMetrics();
      // System.out.println("Precision = " + precision);
      
      xmin = component.getAxisInfo(true).getMin();
      xmax = component.getAxisInfo(true).getMax();
      
      // ymin & ymax swapped to adjust for axis standard
      ymax = component.getAxisInfo(false).getMin();
      ymin = component.getAxisInfo(false).getMax();
        
      // get the dimension of the center panel (imagejpanel)
      xaxis = (int)( component.getRegionInfo().getWidth() );
      yaxis = (int)( component.getRegionInfo().getHeight() );
      // x and y coordinate for upper left hand corner of component
      xstart = (int)( component.getRegionInfo().getLocation().getX() );
      ystart = (int)( component.getRegionInfo().getLocation().getY() );
           
      // System.out.println("X,Y axis = " + xaxis + ", " + yaxis );
      // System.out.println("X,Y start = " +  xstart + ", " + ystart );
      
      // draw title on the overlay if one exists
      if( component.getTitle() != IVirtualArray2D.NO_TITLE )
         g2d.drawString( component.getTitle(), xstart + xaxis/2 -
                      fontdata.stringWidth(component.getTitle())/2, 
     	              ystart/2 + (fontdata.getHeight())/2 );

      if( axesdrawn == X_AXIS || axesdrawn == DUAL_AXES )
      {
         if( drawXLinear )
            paintLinearX( g2d );
	 else
	    paintLogX( g2d );
      }
      if( axesdrawn == Y_AXIS || axesdrawn == DUAL_AXES )
      {
         if( drawYLinear )
            paintLinearY( g2d );
	 else
	    paintLogY( g2d );
      }
   } // end of paint()
   
  /* ***********************Private Methods************************** */
   
  /*
   * Draw the x axis with horizontal numbers and ticks spaced linearly.
   */	
   private void paintLinearX( Graphics2D g2d )
   {
      FontMetrics fontdata = g2d.getFontMetrics();
      
      // info for putting tick marks and numbers on transparency   
      String num = "";
      int xtick_length = 5;

      CalibrationUtil util = new CalibrationUtil( xmin, xmax, precision, 
        					  Format.ENGINEER );
      float[] values = util.subDivide();
      float step = values[0];
      float start = values[1];    // the power of the step
      int numxsteps = (int)values[2];	      
   //   System.out.println("X ticks = " + numxsteps );        
      int pixel = 0;
      int subpixel = 0;
      /* xaxis represents Pmax - Pmin
      float Pmin = start;
      float Pmax = start + xaxis;
      float Amin = xmin;
      */
      float A = 0;   
      int exp_index = 0;
      //boolean drawn = false;
      int prepix = (int)( (float)xaxis*(start - xmin)/
        		  (xmax-xmin) + xstart); 
      int skip = 0;
      
      for( int steps = 0; steps < numxsteps; steps++ )
      {  
         A = (float)steps*step + start; 	      
         pixel = (int)( 
     	     	 (float)xaxis*(A - xmin)/
     	     	 (xmax-xmin) + xstart);       
         //System.out.println("Pixel " + pixel );
       //System.out.println("Xmin/Xmax " + xstart + "/" + (xstart + xaxis) );
         subpixel = (int)( 
     	     	 ( (float)xaxis*(A - xmin - step/2 ) )/
     	     	 (xmax-xmin) + xstart);      
 
         num = util.standardize( (step * (float)steps + start) );
         exp_index = num.lastIndexOf('E');        


         if( (prepix + 2 + 
              fontdata.stringWidth(num.substring(0,exp_index))/2) >
             (pixel - fontdata.stringWidth(num.substring(0,exp_index))/2) )
         {
            skip++;
         }

         if( steps%skip == 0 )
         {
            g2d.drawString( num.substring(0,exp_index), 
        	 pixel - fontdata.stringWidth(num.substring(0,exp_index))/2, 
        	 yaxis + ystart + xtick_length + fontdata.getHeight() );
         }	   

         //System.out.println("Subpixel/XStart " + subpixel + "/" + xstart );
         if( subpixel > xstart && subpixel < (xstart + xaxis) )
         {
	   if( gridxdisplay == 2 )
	   {
	     // first draw gridlines in their color
	     g2d.setColor( gridcolor );
             g2d.drawLine( subpixel, ystart, subpixel, yaxis + ystart );
             
	     // then draw tick mark in black
	     g2d.setColor( Color.black );
	   }
	   //always draw tick marks
           g2d.drawLine( subpixel, yaxis + ystart, 
     	     		 subpixel, yaxis + ystart + xtick_length-2 );
         }
	 
	 // to draw grid line for major ticks
         if( gridxdisplay == 1 || gridxdisplay == 2 )
	 {
	   // first draw gridlines in their color
	   g2d.setColor( gridcolor );
           g2d.drawLine( pixel, ystart, pixel, yaxis + ystart );
           
	   // then draw tick mark in black
	   g2d.setColor( Color.black );
	 }
	 g2d.drawLine( pixel, yaxis + ystart, 
     	     	       pixel, yaxis + ystart + xtick_length );  
         //System.out.println("Y Position: " + (yaxis + ystart + 
         //					xtick_length) );
         if( steps == (numxsteps - 1) && 
             ( xaxis + xstart - pixel) > xaxis/(2*numxsteps) )
         { 
	   // draw gridlines for subtick after last major tick
	   if( gridxdisplay == 2 )
	   {
	     // first draw gridlines in their color
	     g2d.setColor( gridcolor );
	     g2d.drawLine( pixel + (pixel - subpixel), ystart, 
     	     		   pixel + (pixel - subpixel), 
     	     		   yaxis + ystart );
             
	     // then draw tick mark in black
	     g2d.setColor( Color.black );
	   }
	   g2d.drawLine( pixel + (pixel - subpixel), yaxis + ystart, 
     	     		 pixel + (pixel - subpixel), 
     	     		 yaxis + ystart + xtick_length-2 );
           steps++;
           A = (float)steps*step + start;	     
           pixel = (int)( (float)xaxis*(A - xmin)/(xmax-xmin) + xstart); 
           if( steps%skip == 0 && pixel <= (xstart + xaxis) )
           {
              num = util.standardize( (step * (float)steps + start) );
              exp_index = num.lastIndexOf('E');
           
              g2d.drawString( num.substring(0,exp_index), pixel - 
	   	   fontdata.stringWidth(num.substring(0,exp_index))/2, 
                   yaxis + ystart + xtick_length + fontdata.getHeight() );
	      // to draw grid line for major ticks
              if( gridxdisplay == 1 || gridxdisplay == 2 )
	      {
	        // first draw gridlines in their color
	        g2d.setColor( gridcolor );
	   	g2d.drawLine( pixel, ystart, pixel, yaxis + ystart );
             
	        // then draw tick mark in black
	        g2d.setColor( Color.black );
              }
	      g2d.drawLine( pixel, yaxis + ystart, 
     	   		    pixel, yaxis + ystart + xtick_length );
           }	
         }   
      } // end of for
    
   // This will display the x label, x units, and common exponent (if not 0).
      
      String xlabel = "";
      if( !component.getAxisInfo(true).getLabel().equals( 
          IVirtualArray2D.NO_XLABEL) )
         xlabel = xlabel + component.getAxisInfo(true).getLabel();
      if( !component.getAxisInfo(true).getUnits().equals( 
          IVirtualArray2D.NO_XUNITS) )
         xlabel = xlabel + "  " + component.getAxisInfo(true).getUnits();
      if( Integer.parseInt( num.substring( exp_index + 1) ) != 0 )
         xlabel = xlabel + "  " + num.substring( exp_index );
      if( xlabel != "" )
         g2d.drawString( xlabel, xstart + xaxis/2 -
        	   fontdata.stringWidth(xlabel)/2, 
        	   yaxis + ystart + fontdata.getHeight() * 2 + 6 );
   } // end of paintLinearX()
   
  /*
   * Draw the y axis with horizontal numbers and ticks spaced linearly.
   */
   private void paintLinearY( Graphics2D g2d )
   {
      FontMetrics fontdata = g2d.getFontMetrics();   
      String num = "";
      int xtick_length = 5;
      
      CalibrationUtil yutil = new CalibrationUtil( ymin, ymax, precision, 
        					   Format.ENGINEER );
      float[] values = yutil.subDivide();
      float ystep = values[0];
      float starty = values[1];
      int numysteps = (int)values[2];
      
      //   System.out.println("Y Start/Step = " + starty + "/" + ystep);
      int ytick_length = 5;	// the length of the tickmark is 5 pixels
      int ypixel = 0;		// where to place major ticks
      int ysubpixel = 0;	// where to place minor ticks
        
      int exp_index = 0;
      	     	 
      float pmin = ystart + yaxis;
      float pmax = ystart;
      float a = 0;
      float amin = ymin - starty;
      
      // yskip is the space between calibrations: 1 = every #, 2 = every other
      
      int yskip = 1;
      while( (yaxis*yskip/numysteps) < 
             fontdata.getHeight() && yskip < numysteps)
         yskip++;
      int mult = (int)(numysteps/yskip);
      int rem = numysteps%yskip;
   //   System.out.println("numysteps/yskip: (" + numysteps + "/" + yskip + 
   //                      ") = " + mult + "R" + rem);
 
      for( int ysteps = numysteps - 1; ysteps >= 0; ysteps-- )
      {   
         a = ysteps * ystep;
     
         ypixel = (int)( (pmax - pmin) * ( a - amin) /
     	     		 (ymax - ymin) + pmin);
   //         System.out.println("YPixel " + ypixel ); 

         //System.out.println("Ymin/Ymax " + ymin + "/" + ymax );
     
         ysubpixel = (int)( (pmax - pmin) * ( a - amin  + ystep/2 ) /
     	     		 (ymax - ymin) + pmin); 
     
         num = yutil.standardize(ystep * (float)ysteps + starty);
         exp_index = num.lastIndexOf('E');

         /*
         System.out.println("Ypixel/Pmin = " + ypixel + "/" + pmin );
         System.out.println("Ypixel/Pmax = " + ypixel + "/" + pmax );
         System.out.println("Num = " + num );
         */
         // if pixel is between top and bottom of imagejpanel, draw it  
         if( ypixel <= pmin && ypixel >= pmax )
         {
            if( ((float)(ysteps-rem)/(float)yskip) == ((ysteps-rem)/yskip) )
            {
               g2d.drawString( num.substring(0,exp_index), 
        		 xstart - ytick_length - 
	     		 fontdata.stringWidth(num.substring(0,exp_index)),
        		 ypixel + fontdata.getHeight()/4 );
            }		   
            
	    // paint gridlines for major ticks
            if( gridydisplay == 1 || gridydisplay == 2 )
	    {
	      // change color for grid painting
	      g2d.setColor(gridcolor);
	      g2d.drawLine( xstart - 1, ypixel - 1, 
        		    xstart + xaxis - 1, ypixel - 1 );   
	      // change color for tick marks
	      g2d.setColor(Color.black);
	    }
	    g2d.drawLine( xstart - ytick_length, ypixel - 1, 
        		  xstart - 1, ypixel - 1 );   
         }
         // if subpixel is between top and bottom of imagejpanel, draw it
         if( ysubpixel <= pmin && ysubpixel >= pmax )
         {
	    // paint gridlines for minor ticks
            if( gridydisplay == 2 )
	    {
	      // change color for grid painting
	      g2d.setColor(gridcolor);
              g2d.drawLine( xstart - 1, ysubpixel - 1, 
     	     		    xstart + xaxis - 1, ysubpixel - 1 );   
	      // change color for tick marks
	      g2d.setColor(Color.black);
	    }
	    g2d.drawLine( xstart - (ytick_length - 2), ysubpixel - 1, 
     	     		  xstart - 1, ysubpixel - 1 );
         }
         // if a tick mark should be drawn at the end, draw it
         // since the above "if" takes care of all subtick marks before the
         // actual numbered ticks, there may be a tick mark needed after the 
         // last tick. ( end refers to smallest y value )
         if( ysteps == 0 && 
             (pmin - ypixel) > yaxis/(2*numysteps) ) 
         {
	    // paint gridlines for major ticks
            if( gridydisplay == 2 )
	    {
	      // change color for grid painting
	      g2d.setColor(gridcolor);
              g2d.drawLine( xstart - 1, (int)(ysubpixel + 
	       ( (pmin - pmax) * ystep / (ymax - ymin) ) ), xstart + xaxis - 1, 
     	       (int)( ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin))));  
	      // change color for tick marks
	      g2d.setColor(Color.black);
	    }
            g2d.drawLine( xstart - (ytick_length - 2), (int)(ysubpixel + 
	       ( (pmin - pmax) * ystep / (ymax - ymin) ) ), xstart - 1, 
     	       (int)( ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin))));
         }
      }
     
   // This will display the y label, y units, and common exponent (if not 0).
      
      String ylabel = "";
      if( !component.getAxisInfo(false).getLabel().equals( 
          IVirtualArray2D.NO_YLABEL) )
         ylabel = ylabel + component.getAxisInfo(false).getLabel();
      if( !component.getAxisInfo(false).getUnits().equals(
          IVirtualArray2D.NO_YUNITS) )
         ylabel = ylabel + "  " + component.getAxisInfo(false).getUnits();
      if( Integer.parseInt( num.substring( exp_index + 1) ) != 0 )
         ylabel = ylabel + "  " + num.substring( exp_index );
      if( ylabel != "" )
      {
         g2d.rotate( -Math.PI/2, xstart, ystart + yaxis );    
         g2d.drawString( ylabel, xstart + yaxis/2 -
        		 fontdata.stringWidth(ylabel)/2, 
        		 yaxis + ystart - xstart + fontdata.getHeight() );
         g2d.rotate( Math.PI/2, xstart, ystart + yaxis );
      }
   } // end of paintLinearY()
   
  /*
   * Draw the x axis with horizontal numbers and ticks spaced logarithmically.
   */   
   private void paintLogX( Graphics2D g2d )
   {
      if( component instanceof ILogAxisAddible2D )
      {
        ILogAxisAddible2D logcomponent = (ILogAxisAddible2D)component;
        FontMetrics fontdata = g2d.getFontMetrics(); 
        String num = "";
        int TICK_LENGTH = 5;
        int xtick_length = 0;
        int negtick_length = 0;
        //isTwoSided = false;
        CalibrationUtil util = new CalibrationUtil( xmin, xmax, precision, 
          					    Format.ENGINEER );
        util.setTwoSided(isTwoSided);
        float[] values = util.subDivideLog();
        int numxsteps = values.length;	      
   //   System.out.println("X ticks = " + numxsteps );        
        int pixel = 0;
        /* xaxis represents Pmax - Pmin
        float Pmin = start;
        float Pmax = start + xaxis;
        float Amin = xmin;
        */
        float A = 0; 
        int skip = 1;
        int counter = 0;
        int maxcounter = 0;
        int tempprec = 3;
        if( xmax/xmin < 10 )
          tempprec = precision;
        // Draw tick marks for a one-sided color model
        if( !isTwoSided )
        {
          A = values[0];
          LogScaleUtil logger = new LogScaleUtil( 0, xaxis,xmin, xmax + 1);
          double logscale = logcomponent.getLogScale();

	  int division = 0;    // 0-5, divisions in the xaxis.
	  // rightmost pixel coord of last label drawn
	  int last_drawn = -xstart;
	  int last_tick = 0;    // position of last tick mark drawn  
	 
	  // find division where the first label is to be drawn
	  while( (int)logger.toSource(A, logscale) >= 
	         (int)(xaxis/5 * (division + 1) ) )
	    division++;
	 
	  for( int steps = 0; steps < numxsteps; steps++ )
          {  
            A = values[steps];
	    
	    pixel = xstart + (int)logger.toSource(A, logscale);

            num = Format.choiceFormat( A, Format.SCIENTIFIC, tempprec );
 
	    //g2d.setColor(Color.black);
            xtick_length = TICK_LENGTH;
	    // divide axis into 5, if next tick is in the next fifth, show
	    // the number
            if( (pixel - xstart) >= (int)(xaxis/5 * division) )
	    {
	       // if this label does not interfer with the label before it
               if( last_drawn < (pixel - fontdata.stringWidth(num)) )
	       {
	          g2d.drawString( num, pixel - fontdata.stringWidth(num)/2, 
        	    yaxis + ystart + TICK_LENGTH + fontdata.getHeight() );
	          last_drawn = pixel + fontdata.stringWidth(num)/2;
		  if( A != 0 )
                     division++;
		  xtick_length += 3;
               }
	    }
	    if( last_tick + 5 < pixel )
	    {
	      // paint gridlines for major ticks
              if( xtick_length == TICK_LENGTH + 3 &&
	          (gridxdisplay == 1 || gridxdisplay == 2) )
	      {
	        // change color for grid painting
	        g2d.setColor(gridcolor);
	        g2d.drawLine( pixel, ystart, 
     	     	              pixel, yaxis + ystart );
	        // change back to black for tick marks
		g2d.setColor(Color.black);
	      }
	      // paint gridlines for minor ticks
              if( xtick_length == TICK_LENGTH && gridxdisplay == 2 )
	      {
	        // change color for grid painting
	        g2d.setColor(gridcolor);
	        g2d.drawLine( pixel, ystart, 
     	     	              pixel, yaxis + ystart );
	        // change back to black for tick marks
		g2d.setColor(Color.black);
	      }
	      // only paint tick marks
	      g2d.drawLine( pixel, yaxis + ystart, 
     	     	            pixel, yaxis + ystart + xtick_length );   
            }
	    last_tick = pixel;
	    // draw xmax if no numbers are near the end of the calibration
	    if( steps == numxsteps - 1 )
	    {
               A = xmax;
	       pixel = xstart + (int)logger.toSource(A, logscale);
               num = Format.choiceFormat( A, Format.SCIENTIFIC, tempprec );
               if( last_drawn < 
	    	   pixel - fontdata.stringWidth(num)/2 )
	       {
	          g2d.drawString( num, pixel - fontdata.stringWidth(num)/2, 
        	    yaxis + ystart + TICK_LENGTH + fontdata.getHeight() );
	       
	          // paint gridlines for major ticks
                  if( gridxdisplay == 1 || gridxdisplay == 2 )
		  {
		    // change to grid color
		    g2d.setColor(gridcolor);
	            g2d.drawLine( pixel, ystart, 
     	     	                  pixel, yaxis + ystart );
	            // change back to black for tick marks
		    g2d.setColor(Color.black);
	          }
		  g2d.drawLine( pixel, yaxis + ystart, 
     	     	                pixel, yaxis + ystart + TICK_LENGTH + 3 );
	       }
	    }
	    // debug axis divider
	    /*g2d.setColor(Color.red);
	    for( int i = 0; i <= 5; i++ )
	       g2d.drawLine( xstart + xaxis*i/5, yaxis + ystart, xstart + 
			     xaxis*i/5, yaxis + ystart + xtick_length );*/
	  } // end of for
        } // end of if( !isTwoSided )
        // draw tickmarks for a two-sided color model
        else
        {
          A = values[0];
          LogScaleUtil logger = new LogScaleUtil( 0,(int)(xaxis/2),
	                                         xmin, xmax + 1);
          double logscale = logcomponent.getLogScale();

          int neg_pixel = 0;
	  String neg_num = "";
	  int division = 5;     // 5-10, division # in the xaxis.
	  int neg_division = 5; // 5-0 , division # in the xaxis
	  int last_drawn = 0;   // rightmost pixel coord of last label drawn
	  int last_neg_drawn = xaxis;// leftmost pixel coords of last neg. label
	  int first_drawn = 0;  // the leftmost pixel coords of first pos. label
	  int last_tick = 0;    // last tick mark drawn
	  int last_neg_tick = (int)xmax;// last negative tick mark drawn
	  // find division where the first label is to be drawn
	  while( (int)logger.toSource(A, logscale) >= 
	         (int)(xaxis/10 * (division + 1) ) )
	    division++;
	  // find division where the first negative label is to be drawn
	  while( (int)logger.toSource(A, logscale) >= 
	         (int)(xaxis/10 * (10 - (neg_division-1) ) ) )
	    neg_division--;
	 
	  for( int steps = 0; steps < numxsteps; steps++ )
          { 
            A = values[steps];
             
       //System.out.println("here" + xmin + "/" + xmax + "/" + steps + "/" + A);
	    pixel = (int)(xstart + xaxis/2 + (int)logger.toSource(A,logscale) );
            neg_pixel = (int)(xstart + xaxis/2 - 
	                (int)logger.toSource(A, logscale) );
	    num = Format.choiceFormat( A, Format.SCIENTIFIC, tempprec );
	    neg_num = Format.choiceFormat( -A, Format.SCIENTIFIC, tempprec );
	      
	    if( A == 0 )
	       first_drawn = pixel - fontdata.stringWidth(num)/2;    

	    //g2d.setColor(Color.black);
	    xtick_length = TICK_LENGTH;
	    negtick_length = TICK_LENGTH;
            // this will handle all positive labels
	    if( (pixel - xstart) >= (int)(xaxis/10 * division) )
	    {
	       // if this label does not interfer with the label before it
               if( last_drawn < 
	           pixel - fontdata.stringWidth(num)/2 ) 
	       {
	          g2d.drawString( num, pixel - fontdata.stringWidth(num)/2, 
        	    yaxis + ystart + xtick_length + fontdata.getHeight() );
               
	          last_drawn = pixel + fontdata.stringWidth(num)/2;
	          division++;
		  xtick_length += 3;
	       }
	    }
	    // this will handle all negative labels, if a negative label
	    // interfers with a positive label, don't display the negative label
	    if( (neg_pixel - xstart) <= (int)(xaxis/10 * neg_division) )
	    {
	       if( first_drawn > ( neg_pixel + fontdata.stringWidth(neg_num)/2 )
		   && last_neg_drawn > (neg_pixel +
        	   fontdata.stringWidth(neg_num)/2) )
	       {
	          g2d.drawString( neg_num, neg_pixel -
        	    fontdata.stringWidth(neg_num)/2, 
        	    yaxis + ystart + negtick_length + fontdata.getHeight() ); 
	       
	          last_neg_drawn = neg_pixel - 
	            fontdata.stringWidth(neg_num)/2;
	          neg_division--;
		  negtick_length += 3;
               }
	    }
	    // this if will "weed out" tick marks close to each other
	    if( last_tick + 5 < pixel )
	    {
	      // paint gridlines for major ticks
              if( xtick_length == TICK_LENGTH + 3 &&
	          (gridxdisplay == 1 || gridxdisplay == 2) )
	      {
	        // change color for grid painting
		g2d.setColor(gridcolor);
	        g2d.drawLine( pixel, ystart, pixel, yaxis + ystart );
	        // change color to black for tick marks
		g2d.setColor(Color.black);
	      }
	      // paint gridlines for minor ticks
              if( xtick_length == TICK_LENGTH && gridxdisplay == 2 )
	      {
	        // change color for grid painting
		g2d.setColor(gridcolor);
	        g2d.drawLine( pixel, ystart, pixel, yaxis + ystart );
	        // change color to black for tick marks
		g2d.setColor(Color.black);
	      }
	      // only paint tick marks
              g2d.drawLine( pixel, yaxis + ystart, 
     	     	            pixel, yaxis + ystart + xtick_length ); 
            }
	    // this if will "weed out" neg tick marks close to each other
	    if( last_neg_tick - 5 > neg_pixel )
	    {
	      // paint gridlines for major negative ticks
              if( negtick_length == TICK_LENGTH + 3 &&
	          (gridxdisplay == 1 || gridxdisplay == 2) )
	      {
	        // change color for grid painting
		g2d.setColor(gridcolor);
	        g2d.drawLine( neg_pixel, ystart, neg_pixel, yaxis + ystart );
	        // change color to black for tick marks
		g2d.setColor(Color.black);
	      }
	      // paint gridlines for minor ticks
              if( negtick_length == TICK_LENGTH && gridxdisplay == 2 )
	      {
	        // change color for grid painting
		g2d.setColor(gridcolor);
	        g2d.drawLine( neg_pixel, ystart, neg_pixel, yaxis + ystart );
	        // change color to black for tick marks
		g2d.setColor(Color.black);
	      }
	      // only paint tick marks
	      g2d.drawLine( neg_pixel, yaxis + ystart, 
     	     	            neg_pixel, yaxis + ystart + negtick_length );
            }
	    last_tick = pixel;
	    last_neg_tick = neg_pixel;
	    // draw xmax if no numbers are near the end of the calibration
	    if( steps == numxsteps - 1 )
	    {
	       xtick_length = TICK_LENGTH;
	       negtick_length = TICK_LENGTH;
               A = xmax;
	       pixel = (int)(xstart + xaxis/2 + 
	               (int)logger.toSource(A,logscale) );
               neg_pixel = (int)(xstart + xaxis/2 - 
	                   (int)logger.toSource(A, logscale) );
	       num = Format.choiceFormat( A, Format.SCIENTIFIC, tempprec );
	       neg_num = Format.choiceFormat( -A, Format.SCIENTIFIC, tempprec );
               if( last_drawn < 
	    	   pixel - fontdata.stringWidth(num)/2 )
	       {
	          g2d.drawString( num, pixel - fontdata.stringWidth(num)/2, 
        	    yaxis + ystart + TICK_LENGTH + fontdata.getHeight() );
	          xtick_length += 3;
	       }
	       if( last_neg_drawn > (neg_pixel +
        	   fontdata.stringWidth(neg_num)/2) )
	       {
	          g2d.drawString( neg_num, 
		    neg_pixel -
        	    fontdata.stringWidth(neg_num)/2, 
        	    yaxis + ystart + negtick_length + fontdata.getHeight() ); 
                  negtick_length += 3;
	       }
	       g2d.drawLine( pixel, yaxis + ystart, 
     	     	             pixel, yaxis + ystart + xtick_length );  
	       g2d.drawLine( neg_pixel, yaxis + ystart, 
     	     	             neg_pixel, yaxis + ystart + negtick_length );
	    }
            // debug axis divider
	    /*g2d.setColor(Color.red);
	    for( int i = 0; i <= 10; i++ )
	       g2d.drawLine( xstart + xaxis*i/10, yaxis + ystart, xstart + 
			     xaxis*i/10, yaxis + ystart + TICK_LENGTH );*/
	  } // end of for
        } // end of else (isTwoSided)
      
   // This will display the x label, x units, and common exponent (if not 0).
      
        String xlabel = "";
        if( !logcomponent.getAxisInfo(true).getLabel().equals( 
            IVirtualArray2D.NO_XLABEL) )
          xlabel = xlabel + logcomponent.getAxisInfo(true).getLabel();
        if( !logcomponent.getAxisInfo(true).getUnits().equals( 
            IVirtualArray2D.NO_XUNITS) )
          xlabel = xlabel + "  " + logcomponent.getAxisInfo(true).getUnits();
        if( xlabel != "" )
          g2d.drawString( xlabel, xstart + xaxis/2 -
        	   fontdata.stringWidth(xlabel)/2, 
        	   yaxis + ystart + fontdata.getHeight() * 2 + 6 );   
      } // end if( instanceof)
      else
        System.out.println("Instance of ILogAxisAddible2D needed " +
	                   "in AxisOverlay2D.java");
   }
   
  /*
   * Draw the y axis with horizontal numbers and ticks spaced logarithmically.
   */    
   private void paintLogY( Graphics2D g2d )
   {
     if( component instanceof ILogAxisAddible2D )
     {
       ILogAxisAddible2D logcomponent = (ILogAxisAddible2D)component;
       FontMetrics fontdata = g2d.getFontMetrics();   
       String num = "";
       int TICK_LENGTH = 5;
       //isTwoSided = false;
       CalibrationUtil yutil = new CalibrationUtil( ymin, ymax, precision, 
         					    Format.ENGINEER );
       yutil.setTwoSided(isTwoSided);
       float[] values = yutil.subDivideLog();
       int numysteps = values.length;
      
       //   System.out.println("Y Start/Step = " + starty + "/" + ystep);
       int ytick_length = 5;	// the length of the tickmark is 5 pixels
       int ypixel = 0;		// where to place major ticks
       int tempprec = 3;
       if( xmax/xmin < 10 )
         tempprec = precision;
      	     	 
       float a = 0;
       // Draw tick marks for a one-sided color model
       if( !isTwoSided )
       {
         a = values[0];
         LogScaleUtil logger = new LogScaleUtil( 0, yaxis, ymax, ymin + 1);
         double logscale = logcomponent.getLogScale();
	 int division = 0;    // 0-5, divisions in the yaxis.
	 // top pixel coord of last label drawn
	 int last_drawn = yaxis + ystart + fontdata.getHeight(); 
	 int last_tick = last_drawn + 6;
	 // find division where the first label is to be drawn
	 while( (int)logger.toSource(a, logscale) >= 
	        (int)(yaxis/5 * (division + 1) ) )
	    division++;
   //   System.out.println("numysteps/yskip: (" + numysteps + "/" + yskip + 
   //                      ") = " + mult + "R" + rem);
	 for( int ysteps = 0; ysteps < numysteps; ysteps++ )
         { 
            a = values[ysteps];  
            //System.out.println("Logger: " + logger.toSource(a, logscale) );
            ypixel = ystart + yaxis - (int)logger.toSource(a, logscale);
            num = Format.choiceFormat( a, Format.SCIENTIFIC, tempprec );
  
            ytick_length = TICK_LENGTH;
	    //g2d.setColor(Color.black);
            if( (int)logger.toSource(a, logscale) >= (int)(yaxis/5 * division) )
	    {  
	       // if this label does not interfer with the label before it
               if( last_drawn > (ypixel + fontdata.getHeight()/2) ) 
	       {
                  ytick_length += 3;
                  g2d.drawString( num, xstart - ytick_length - 
	     	      fontdata.stringWidth(num),
        	      ypixel + fontdata.getHeight()/4 );
	          last_drawn = ypixel - fontdata.getHeight()/2;
		  if( a != 0 )
                     division++;
	       }	   
	    }
	    // this if is to "weed out" the nearby tick marks
	    if( last_tick - 5 > ypixel )
	    {
	      // paint gridlines for major ticks
              if( ytick_length == TICK_LENGTH + 3 &&
	    	  (gridydisplay == 1 || gridydisplay == 2) )
              {
	        // change color for grid painting
		g2d.setColor(gridcolor);
	        g2d.drawLine( xstart, ypixel - 1, 
            	              xstart + xaxis - 1, ypixel - 1 ); 
	        // change color for tick marks
		g2d.setColor(Color.black);
	      }
	      // paint gridlines for minor ticks
              if( ytick_length == TICK_LENGTH && gridydisplay == 2 )
              {
	        // change color for grid painting
		g2d.setColor(gridcolor);
                g2d.drawLine( xstart, ypixel - 1, 
            		      xstart + xaxis - 1, ypixel - 1 ); 
	        // change color for tick marks
		g2d.setColor(Color.black);
	      }
	      // only paint tick marks
              g2d.drawLine( xstart - ytick_length, ypixel - 1, 
                            xstart - 1, ypixel - 1 );   
              ytick_length = TICK_LENGTH;
	    }
	    last_tick = ypixel;
	    // draw end marker if nothing no values are near the end.
	    if( ysteps == (numysteps - 1) )
	    {
	       a = ymin;
               ypixel = ystart + yaxis - (int)logger.toSource(a,logscale);
	       	    
               num = Format.choiceFormat( a, Format.SCIENTIFIC, tempprec );
	       if( last_drawn > (ypixel - fontdata.getHeight()/2) ) 
	       {	  
	    	  ytick_length += 3;  	
                  g2d.drawString( num, xstart - ytick_length - 
	     	      fontdata.stringWidth(num),
        	      ypixel + fontdata.getHeight()/4 );
	       }
	       
	       // paint gridlines for major ticks
               if( ytick_length == TICK_LENGTH + 3 &&
	           (gridydisplay == 1 || gridydisplay == 2) )
               {
	         // change color for grid painting
		 g2d.setColor(gridcolor);
	         g2d.drawLine( xstart, ypixel - 1, 
                               xstart + xaxis - 1, ypixel - 1 ); 
	         // change color for tick marks
		 g2d.setColor(Color.black);
	       }
	       // paint gridlines for minor ticks
               if( ytick_length == TICK_LENGTH && gridydisplay == 2 )
               {
	         // change color for grid painting
		 g2d.setColor(gridcolor);
                 g2d.drawLine( xstart, ypixel - 1, 
                               xstart + xaxis - 1, ypixel - 1 ); 
	         // change color for tick marks
		 g2d.setColor(Color.black);
	       } 
	       // only paint tick marks
	       g2d.drawLine( xstart - ytick_length, ypixel - 1, 
                             xstart - 1, ypixel - 1 ); 
	    }
	     
            // debug axis divider
	    /*g2d.setColor(Color.red);
	    for( int i = 0; i <= 5; i++ )
	       g2d.drawLine( xstart - ytick_length, ystart + yaxis*i/5, 
                             xstart - 1, ystart + yaxis*i/5 );*/
         }
       }
       // if two-sided color model
       else
       {
         a = values[0];
         LogScaleUtil logger = new LogScaleUtil( 0,(int)(yaxis/2),
	                                         ymax,ymin + 1);
         double logscale = logcomponent.getLogScale();
         int negtick_length = 0;
         int neg_ypixel = 0;
	 String neg_num = "";
	 int division = 0;     // 0-5,  division # in the xaxis.
	 int neg_division = 0; // 0-5, division # in the xaxis
	 // top pixel coord of last label drawn
	 int last_drawn = yaxis/2 + ystart + fontdata.getHeight();
	 //bottommost pixel coords of last neg label
	 int last_neg_drawn = yaxis/2 + ystart - fontdata.getHeight();
	 int first_drawn = 0;  //the bottommost pixel coords of first pos label
	 int last_tick = last_drawn + 6;
	 int last_neg_tick = last_neg_drawn - 6;
	 // find division where the first label is to be drawn
	 while( (int)logger.toSource(a, logscale) >= 
	        (int)(yaxis/10 * (division + 1) ) )
	    division++;
	 // find division where the first negative label is to be drawn
	 while( (int)logger.toSource(a, logscale) >= 
	        (int)(yaxis/10 * (neg_division+1) ) )
	    neg_division++;

   //   System.out.println("numysteps/yskip: (" + numysteps + "/" + yskip + 
   //                      ") = " + mult + "R" + rem);
	 for( int ysteps = 0; ysteps < numysteps; ysteps++ )
         {  
            a = values[ysteps];
            ypixel = ystart + (int)(yaxis/2) - (int)logger.toSource(a,logscale);
            neg_ypixel = ystart + (int)(yaxis/2) + 
	                          (int)logger.toSource(a,logscale);
	    	      
	    if( ysteps == 0 )
	       first_drawn = ypixel + (int)(fontdata.getHeight()/2);
	    
            num = Format.choiceFormat( a, Format.SCIENTIFIC, tempprec );
	    neg_num = Format.choiceFormat( -a, Format.SCIENTIFIC, tempprec );
  
            ytick_length = TICK_LENGTH;
	    negtick_length = TICK_LENGTH;
	    //g2d.setColor(Color.black);
            if( (int)logger.toSource(a,logscale) >= (int)(yaxis/10 * division) )
	    {  
	       // positive number labels
               if( last_drawn > (ypixel + fontdata.getHeight()/2) ) 
	       {
                  ytick_length += 3;
                  g2d.drawString( num, 
        	      xstart - ytick_length - 
	     	      fontdata.stringWidth(num),
        	      ypixel + fontdata.getHeight()/4 );
	          last_drawn = ypixel - fontdata.getHeight()/2;
		  if( ysteps != 0 )
                     division++;
	       }
            }
	    if( (int)logger.toSource(a,logscale) >= 
	        (int)(yaxis/10 * neg_division) &&
	        first_drawn < (neg_ypixel - fontdata.getHeight()/2) )
	    {  
	       // negative number labels
               if( last_neg_drawn < (neg_ypixel - fontdata.getHeight()/2) )
	       {
		  negtick_length += 3;
                  g2d.drawString( neg_num, xstart - ytick_length - 
	     	      fontdata.stringWidth(neg_num),
        	      neg_ypixel + fontdata.getHeight()/4 + 2);
	          last_neg_drawn = neg_ypixel + fontdata.getHeight()/2;
                  neg_division++;
               }	   
	    }
	    
	    // since only draw middle once, a = 0 is a special case
	    if( a == 0 )
	    {	
	      // paint gridlines for major ticks
              if( ytick_length == TICK_LENGTH + 3 &&
	          (gridydisplay == 1 || gridydisplay == 2) ) 
	      {
	        // change color for grid painting
		g2d.setColor(gridcolor);   	    
                g2d.drawLine( xstart, ypixel, 
                              xstart + xaxis - 1, ypixel ); 
	        // change color for tick marks
		g2d.setColor(Color.black);  
	      }	   	    
              g2d.drawLine( xstart - ytick_length, ypixel, 
                            xstart - 1, ypixel );
              last_tick = ypixel;
	      last_neg_tick = ypixel;
	    }
	    else // two sided, draw both negative and positive
	    {
	      if( last_tick - 5 > ypixel )
	      {
	        // paint gridlines for major ticks
                if( ytick_length == TICK_LENGTH + 3 &&
	            (gridydisplay == 1 || gridydisplay == 2) )
	        {
	          // change color for grid painting
		  g2d.setColor(gridcolor);   	    
	          g2d.drawLine( xstart, ypixel - 1, 
                                xstart + xaxis - 1, ypixel - 1 ); 
	          // change color for tick marks
		  g2d.setColor(Color.black);  
	        }	   	    
	        // paint gridlines for minor ticks
                if( ytick_length == TICK_LENGTH && gridydisplay == 2 )
	        {
	          // change color for grid painting
		  g2d.setColor(gridcolor);   	    
	          g2d.drawLine( xstart, ypixel - 1, 
                                xstart + xaxis - 1, ypixel - 1 );
	          // change color for tick marks
		  g2d.setColor(Color.black);  
	        }	   	     
	        // only paint tick marks
	        g2d.drawLine( xstart - ytick_length, ypixel - 1, 
                              xstart - 1, ypixel - 1 ); 
              }
	      last_tick = ypixel;
	      
	      if( last_neg_tick + 5 < neg_ypixel )
	      {
	        // paint gridlines for major negative ticks
                if( negtick_length == TICK_LENGTH + 3 &&
	            (gridydisplay == 1 || gridydisplay == 2) )
	        {
	          // change color for grid painting
		  g2d.setColor(gridcolor);   	    
	          g2d.drawLine( xstart, neg_ypixel + 1, 
                                xstart + xaxis - 1, neg_ypixel + 1 );
	          // change color for tick marks
		  g2d.setColor(Color.black);
		}
		// paint gridlines for minor ticks
                if( negtick_length == TICK_LENGTH && gridydisplay == 2 )
	        {
	          // change color for grid painting
		  g2d.setColor(gridcolor);   	    
	          g2d.drawLine( xstart, neg_ypixel + 1, 
                                xstart + xaxis - 1, neg_ypixel + 1 );
	          // change color for tick marks
		  g2d.setColor(Color.black);
		}
	        // only paint tick marks
	        g2d.drawLine( xstart - negtick_length, neg_ypixel + 1, 
                              xstart - 1, neg_ypixel + 1 );
              }
	      last_neg_tick = neg_ypixel;
	    }
	   
	    // draw end marker if no values are near the end.
	    if( ysteps == (numysteps - 1) )
	    {
	       a = ymax;
               ypixel = ystart + (int)(yaxis/2) - 
	        	(int)logger.toSource(a,logscale);
               neg_ypixel = ystart + (int)(yaxis/2) + 
	        	       (int)logger.toSource(a,logscale);
	       	    
               num = Format.choiceFormat( a, Format.SCIENTIFIC, tempprec );
	       neg_num = Format.choiceFormat( -a, Format.SCIENTIFIC, tempprec );
               ytick_length = TICK_LENGTH;
	       negtick_length = TICK_LENGTH;
	       if( last_drawn > (ypixel + fontdata.getHeight()/2) ) 
	       {
	          ytick_length += 3;
		  negtick_length += 3;
                  g2d.drawString( num, xstart - ytick_length - 
	     	      fontdata.stringWidth(num),
        	      ypixel + fontdata.getHeight()/4 );
		      
                  g2d.drawString( neg_num, xstart - negtick_length - 
	     	      fontdata.stringWidth(neg_num),
        	      neg_ypixel + fontdata.getHeight()/4 + 2);	
	       }    
               g2d.drawLine( xstart - ytick_length, ypixel, 
                	     xstart - 1, ypixel );  	 
               g2d.drawLine( xstart - negtick_length, neg_ypixel, 
                             xstart - 1, neg_ypixel );  
	    }
	    
	    // debug axis divider 
	    /*g2d.setColor(Color.red);
	    for( int i = 0; i <= 10; i++ )
	       g2d.drawLine( xstart - ytick_length, ystart + yaxis*i/10, 
                             xstart - 1, ystart + yaxis*i/10 ); */
         }
      
       }
   // This will display the y label, y units, and common exponent (if not 0).
      
       String ylabel = "";
       if( !logcomponent.getAxisInfo(false).getLabel().equals( 
           IVirtualArray2D.NO_YLABEL) )
         ylabel = ylabel + logcomponent.getAxisInfo(false).getLabel();
       if( !logcomponent.getAxisInfo(false).getUnits().equals(
           IVirtualArray2D.NO_YUNITS) )
         ylabel = ylabel + "  " + logcomponent.getAxisInfo(false).getUnits();
       if( ylabel != "" )
       {
         g2d.rotate( -Math.PI/2, xstart, ystart + yaxis );    
         g2d.drawString( ylabel, xstart + yaxis/2 -
        		 fontdata.stringWidth(ylabel)/2, 
        		 yaxis + ystart - xstart + fontdata.getHeight() );
         g2d.rotate( Math.PI/2, xstart, ystart + yaxis );
       }
     } // end if( instanceof)
     else
        System.out.println("Instance of ILogAxisAddible2D needed " +
	                   "in AxisOverlay2D.java");
   }
   
   private class AxisEditor extends JFrame
   {
     private AxisEditor this_editor;
     private JComboBox xbox;
     private JComboBox ybox;
     
     public AxisEditor()
     {
       super("Axis Editor");
       this_editor = this;
       this.getContentPane().setLayout( new GridLayout(2,0) );
       this.setBounds(editor_bounds);
       this.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
       String[] xlist = {"X Axis (none)","Major Grid","Major/Minor Grid"};
       String[] ylist = {"Y Axis (none)","Major Grid","Major/Minor Grid"};
       xbox = new JComboBox(xlist);
       xbox.setName("XBOX");
       xbox.setSelectedIndex(gridxdisplay);
       xbox.addActionListener( new ControlListener() );
       ybox = new JComboBox(ylist);
       ybox.setName("YBOX");
       ybox.setSelectedIndex(gridydisplay);
       ybox.addActionListener( new ControlListener() );
       
       JButton gridcolor = new JButton("Change Grid Color");
       gridcolor.addActionListener( new ControlListener() );
       JButton closebutton = new JButton("Close");
       closebutton.addActionListener( new ControlListener() );
       
       // this jpanel groups all grid options into one row.
       JPanel gridoptions = new JPanel( new GridLayout() );
       gridoptions.add( new JLabel("Grid Options") );
       gridoptions.add(xbox);
       gridoptions.add(ybox);
       
       // this jpanel groups all other miscellaneous options into one row.
       JPanel miscoptions = new JPanel( new GridLayout() );
       miscoptions.add(gridcolor);
       miscoptions.add(closebutton);
       //miscoptions.add(
       this.getContentPane().add( gridoptions );
       this.getContentPane().add( miscoptions );
       this_editor.addComponentListener( new EditorListener() );
     }
     
     class ControlListener implements ActionListener
     {
       public void actionPerformed( ActionEvent e )
       {
         if( e.getSource() instanceof JComboBox )
	 {
           JComboBox temp = ((JComboBox)e.getSource());
	   if( temp.getName().equals("XBOX") )
	     gridxdisplay = temp.getSelectedIndex();
	   else
	     gridydisplay = temp.getSelectedIndex();
	   this_panel.repaint();
         }
	 else if( e.getSource() instanceof JButton )
	 {
	   String message = e.getActionCommand();
	   if( message.equals("Change Grid Color") )
	   {
             Color temp =
	         JColorChooser.showDialog(this_editor, "Grid Color", gridcolor);
	     if( temp != null )
	     {
	       gridcolor = temp;
	       this_panel.repaint();
	     }
	   }
	   else if( message.equals("Close") )
	   {
	     this_editor.dispose();
	     this_panel.repaint();
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
}
