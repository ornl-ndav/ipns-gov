/*
 * File: SelectionOverlay.java
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
 *  Revision 1.2  2003/06/05 22:07:21  dennis
 *     (Mike Miller)
 *   - Added resize capability
 *   - Corrected duplication error
 *   - Added private class Region which includes the current_bounds for each
 *     region in the regions vector.
 *   - Added getFocus() method to fix keylistener problems
 *
 *  Revision 1.1  2003/05/29 14:29:20  dennis
 *  Initial version, current functionality does not support
 *  annotation editing or annotation deletion. (Mike Miller)
 * 
 */
 
/* *************************************************************
 * *********Basic controls for the Selection Overlay************
 * *************************************************************
 * Keyboard Event    * Mouse Event       * Action	       *
 ***************************************************************
 * press B	     * Press/Drag mouse  * box selection       *
 * press C	     * Press/Drag mouse  * circle selection    *
 * press P	     * Press/Drag mouse  * point selection     * 
 * none  	     * Double click      * clear last selected *
 * press A (all)     * Double click      * clear all selected  *
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

import DataSetTools.components.image.*; //ImageJPanel & CoordJPanel
import DataSetTools.components.View.TwoD.*;
import DataSetTools.components.View.Cursor.*; 

/**
 * This class allows users to select a region for calculation purposes.
 * Three types of regions may currently be selected: point, box, and circle.
 * The selected region will initially show up in white.
 */
public class SelectionOverlay extends OverlayJPanel 
{
   private SelectionJPanel sjp;           // panel overlaying the center jpanel
   private IAxisAddible2D component;      // component being passed
   private Vector regions;                // all selected regions
   private SelectionOverlay this_panel;   // used for repaint by SelectListener 
   private Color reg_color;
   private Rectangle current_bounds;
  
  /**
   * Constructor creates an OverlayJPanel with a SeletionJPanel that shadows the
   * center panel of the IAxisAddible2D component.
   *
   *  @param  component
   */ 
   public SelectionOverlay(IAxisAddible2D iaa)
   {
      super();
      this.setLayout( new GridLayout(1,1) );
      
      sjp = new SelectionJPanel();
      component = iaa;
      regions = new Vector();       
      this_panel = this;
      reg_color = Color.white;
       
      this.add(sjp);
      sjp.addActionListener( new SelectListener() ); 
      
      sjp.requestFocus();               
   }
   
  /**
   * This method sets all the colors for the selected regions. Initially set
   * to white.
   *
   *  @param  color
   */
   public void setRegionColor( Color c )
   {
      reg_color = c;
      this_panel.repaint();
   }
   
  /**
   * This method gives focus to the SelectionJPanel, which is overlayed on the
   * center of the IAxisAddible2D component.
   */
   public void getFocus()
   {
      sjp.requestFocus();
   }

  /**
   * Overrides paint method. This method will paint the selected regions.
   *
   *  @param  graphic
   */
   public void paint(Graphics g) 
   {  
      Graphics2D g2d = (Graphics2D)g; 
      current_bounds = component.getRegionInfo();  // current size of center
      sjp.setBounds( current_bounds );
      // color of all of the selections.
      g2d.setColor(reg_color);
      // top left corner of sjp
      Point tlc = new Point( sjp.getLocation() );
     /* To "move" the annotations, an x & y scale had to be made. This
      * simply takes the width of the current rectangle/scale rectangle.
      * This is calculated in the for loop.
      */ 
      float xfactor = 0;
      float yfactor = 0;
      Region regionclass;      
      Object region;
      for( int num_reg = 0; num_reg < regions.size(); num_reg++ )
      {
         regionclass = (Region)regions.elementAt(num_reg);
	 
         xfactor = (float)current_bounds.getWidth()/
	           (float)regionclass.getScale().getWidth();
	           
         yfactor = (float)current_bounds.getHeight()/
	           (float)regionclass.getScale().getHeight();	
		   
	 region = ((Region)regions.elementAt(num_reg)).getRegion();    
         if( region instanceof Circle )
	 {
	    Point center = ((Circle)region).getCenter();
	    int radius = (int)(((Circle)region).getRadius());
	    g2d.drawOval( (int)((center.x - radius) * xfactor ) + tlc.x, 
	                  (int)((center.y - radius) * yfactor ) + tlc.y, 
	                  (int)(2 * radius * xfactor), 
			  (int)(2 * radius * yfactor) ); 	    
	 }
         if( region instanceof Rectangle )
	 {
	    g2d.drawRect( (int)( ((Rectangle)region).getX() * xfactor) + tlc.x, 
	                  (int)( ((Rectangle)region).getY() * yfactor) + tlc.y,
	                  (int)( ((Rectangle)region).getWidth() * xfactor),
	                  (int)( ((Rectangle)region).getHeight() * yfactor) ); 	    
	 }
         if( region instanceof Point )
	 {
	    //System.out.println("Drawing instance of point at " + 
	    //                 ((Point)region).x + "/" + ((Point)region).y );
	    int x = ((Point)region).x;
	    int y = ((Point)region).y;
	    g2d.drawLine( (int)((x - 5 ) * xfactor ) + tlc.x, 
	                  (int)(y * yfactor) + tlc.y, 
			  (int)((x + 5 ) * xfactor ) + tlc.x, 
			  (int)(y * yfactor) + tlc.y ); 	    
	    g2d.drawLine( (int)(x * xfactor) + tlc.x, 
	                  (int)((y - 5 ) * yfactor ) + tlc.y, 
			  (int)(x * xfactor) + tlc.x,
			  (int)((y + 5 ) * yfactor ) + tlc.y );
	 }	 	 
      }
   } // end of paint()

  /*
   * SelectListener listens for messages being passed from the SelectionJPanel.
   */
   private class SelectListener implements ActionListener
   {
      public void actionPerformed( ActionEvent ae )
      {
         String message = ae.getActionCommand(); 
         // clear all selections from the vector
         if( message.equals( SelectionJPanel.RESET_SELECTED ) )
         {
	    //System.out.println("Clear all selected" ); 
	    if( regions.size() > 0 )
	       regions.clear(); 
	    else
	       System.out.println("No Regions selected");          
	 }
	 // remove the last selection from the vector
         else if( message.equals( SelectionJPanel.RESET_LAST_SELECTED ) )
         {
	    //System.out.println("Clear last selected" ); 
	    if( regions.size() > 0 )
	       regions.removeElementAt(regions.size() - 1); 
	    else
	       System.out.println("No Regions selected");          	      
	 }
	 // region is specified by REGION_SELECTED>BOX >CIRCLE >POINT	
	 // if REGION_SELECTED is in the string, find which region 
	 else if( message.indexOf( SelectionJPanel.REGION_SELECTED ) > -1 )
         {
	    if( message.indexOf( SelectionJPanel.BOX ) > -1 )
	    {
	       Region boxregion = new Region( 
	            ((BoxCursor)sjp.getCursor( SelectionJPanel.BOX )).region(),
		    current_bounds );
	       regions.add( boxregion );
	       //System.out.println("Drawing box region" );
	    }
	    else if( message.indexOf( SelectionJPanel.CIRCLE ) > -1 )
	    {
	       Region circleregion = new Region( ((CircleCursor)
	            sjp.getCursor( SelectionJPanel.CIRCLE )).region(),
		    current_bounds );
	       regions.add( circleregion );
	       //System.out.println("Drawing circle region" );
	    }	    
	    else if( message.indexOf( SelectionJPanel.ELIPSE ) > -1 )
	    {
	       //System.out.println("Elipse region not implemented" );
	    }	    
	    else if( message.indexOf( SelectionJPanel.POINT ) > -1 )
	    { 
	       //System.out.println("Drawing point region" );
	       // create new point, otherwise regions would be shared.
	       Point np = new Point( ((PointCursor)
	               sjp.getCursor( SelectionJPanel.POINT )).region() );
	       
	       regions.add( new Region(np, current_bounds) );
	    }
	 }
	 this_panel.repaint();  // Without this, the newly drawn regions would
	                        // not appear.
      }  // end actionPerformed()   
   } // end SelectListener  
  
  /*
   * This class groups together the selected region and the context in which
   * it was drawn. Without this class, objects drawn on a resized component
   * would assume they were drawn at the component's original state.
   */ 
   private class Region
   {
      private Object region;
      private Rectangle scale;      // bounds when this region was created.
      
      public Region( Object o, Rectangle r )
      {
         region = o;
	 scale = r;
      }
      
      public Object getRegion()
      {
         return region;
      }
      
      public Rectangle getScale()
      {
         return scale;
      }
   }
}
