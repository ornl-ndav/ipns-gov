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
 *  Revision 1.1  2003/05/29 14:23:36  dennis
 *  Initial version, connects to SelectionJPanel and ImageViewComponent.
 *  (Mike Miller)
 *
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

public class SelectionOverlay extends OverlayJPanel 
{
   private SelectionJPanel sjp;           // panel overlaying the center jpanel
   private IAxisAddible2D component;      // component being passed
   private Vector regions;                // all selected regions
   private SelectionOverlay this_panel;   // used for repaint by SelectListener 
   private Color reg_color;
  
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
   }
   
  /**
   * This method sets all the colors for the selected regions.
   *
   *  @param  color
   */
   public void setRegionColor( Color c )
   {
      reg_color = c;
   }

  /**
   * Overrides paint method. This method will paint the selected regions.
   *
   *  @param  graphic
   */
   public void paint(Graphics g) 
   {  
      Graphics2D g2d = (Graphics2D)g; 
      sjp.setBounds( component.getRegionInfo() );
      // color of all of the selections.
      g2d.setColor(reg_color);
      
      Object region;
      for( int num_reg = 0; num_reg < regions.size(); num_reg++ )
      {
         region = regions.elementAt(num_reg);
         if( region instanceof Circle )
	 {
	    Point center = ((Circle)region).getCenter();
	    int radius = (int)(((Circle)region).getRadius());
	    g2d.drawOval( center.x - radius, center.y - radius, 
	                  2 * radius, 2 * radius ); 	    
	 }
         if( region instanceof Rectangle )
	 {
	    g2d.drawRect( (int)( ((Rectangle)region).getX() ), 
	                  (int)( ((Rectangle)region).getY() ),
	                  (int)( ((Rectangle)region).getWidth() ),
	                  (int)( ((Rectangle)region).getHeight() ) ); 	    
	 }
         if( region instanceof Point )
	 {
	    g2d.drawLine( ((Point)region).x - 5, 
	                  ((Point)region).y, 
			  ((Point)region).x + 5, 
			  ((Point)region).y ); 	    
	    g2d.drawLine( ((Point)region).x, 
	                  ((Point)region).y - 5, 
			  ((Point)region).x,
			  ((Point)region).y + 5 );
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
	    System.out.println("Clear all selected" ); 
	    if( regions.size() > 0 )
	       regions.clear(); 
	    else
	       System.out.println("No Regions selected");          
	 }
	 // remove the last selection from the vector
         else if( message.equals( SelectionJPanel.RESET_LAST_SELECTED ) )
         {
	    System.out.println("Clear last selected" ); 
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
	       regions.add( ((BoxCursor)
		       sjp.getCursor( SelectionJPanel.BOX )).region() );
	       System.out.println("Drawing box region" );
	    }
	    else if( message.indexOf( SelectionJPanel.CIRCLE ) > -1 )
	    {// test until keylisteners are fixed	    
	       regions.add( new Circle( 160, 160, 40.0f ) ); 
	     /*regions.add( ((CircleCursor)
	               sjp.getCursor( SelectionJPanel.CIRCLE )).region() );*/
	       System.out.println("Drawing circle region" );
	    }	    
	    else if( message.indexOf( SelectionJPanel.ELIPSE ) > -1 )
	    {
	       System.out.println("Elipse region not implemented" );
	    }	    
	    else if( message.indexOf( SelectionJPanel.POINT ) > -1 )
	    {
	       regions.add( ((PointCursor)
	               sjp.getCursor( SelectionJPanel.POINT )).region() );
	       System.out.println("Drawing point region" );
	    }
	 }
	 this_panel.repaint();  // Without this, the newly drawn regions would
	                        // not appear.
      }  // end actionPerformed()   
   } // end SelectListener  
   
}
