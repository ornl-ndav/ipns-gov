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
 *  Revision 1.12  2003/08/14 21:48:11  millermi
 *  - Added toFront() to SelectionEditor to display it over the viewer.
 *
 *  Revision 1.11  2003/08/14 17:11:57  millermi
 *  - Added SelectionEditor class
 *  - Now capable of changing the selection color and also the opacity
 *    of the selections.
 *  - Edited help() to provide more description.
 *
 *  Revision 1.10  2003/08/11 23:45:23  millermi
 *  - grouped multiple sendMessage() statements into one statement
 *    after all if statements.
 *  - Added static variable ALL_REGIONS_REMOVED for messaging.
 *
 *  Revision 1.9  2003/08/08 15:54:24  millermi
 *  - Edited Revision 1.8 so it did not exceed 80 characters per line.
 *  - Now uses method getWorldCoordPoints() to access WCRegion class.
 *    This method is more efficient because it returns the whole
 *    array of points, as opposed to getWorldCoordPointAt() which
 *    requires multiple method calls.
 *
 *  Revision 1.8  2003/08/07 22:47:55  millermi
 *  - Added line selection capabilities
 *  - Changed Help menu for REMOVE ALL SELECTIONS from "Double" to "Single" 
 *    click
 *  - Usage of Region class changed to WCRegion class and adapted for generic
 *    number of points
 *
 *  Revision 1.7  2003/08/07 17:57:41  millermi
 *  - Added line selection capabilities
 *  - Changed Help menu for REMOVE ALL SELECTIONS from "Double" to "Single"
 *    click
 *
 *  Revision 1.6  2003/08/06 13:56:45  dennis
 *  - Added sjp.setOpaque(false) to constructor. Fixes bug when
 *    Axis Overlay is turned off and Selection Overlay is on.
 *
 *  Revision 1.5  2003/07/25 14:39:34  dennis
 *  - Constructor now takes component of type IZoomAddible instead of
 *    IAxisAddible2D
 *  - Private class Region now moved to an independent file to allow for use by
 *    components using the selection overlay.
 *  - Added public methods addActionListener(), removeActionListener(),
 *    removeAllActionListeners(), and private method sendMessage() to allow
 *    listeners for when a selection occurs.
 *  - Added getSelectedRegion()
 *    (Mike Miller)
 *
 *  Revision 1.4  2003/06/17 13:21:37  dennis
 *  (Mike Miller)
 *  - Made selections zoomable. clipRect() method was added to paint
 *    to restrict the painted area to only that directly above
 *    the center panel.
 *
 *  Revision 1.3  2003/06/09 14:47:19  dennis
 *  Added static method help() to display commands via the HelpMenu.
 *  (Mike Miller)
 *
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
 * press L           * Press/Drag mouse  * line selection      *
 * press P	     * Press/Drag mouse  * point selection     * 
 * none  	     * Double click      * clear last selected *
 * press A (all)     * Single click      * clear all selected  *
 ***************************************************************
 * Important: 
 * All keyboard events must be done prior to mouse events.
 */ 
 
package DataSetTools.components.View.Transparency;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector; 
import java.lang.Math;

import DataSetTools.components.image.*;
import DataSetTools.components.View.Region.WCRegion;
import DataSetTools.components.View.TwoD.IZoomAddible;
import DataSetTools.components.View.Cursor.*; 
import DataSetTools.components.View.ViewControls.ControlSlider;
import DataSetTools.components.View.ViewControls.IViewControl;
import DataSetTools.util.floatPoint2D;

/**
 * This class allows users to select a region for calculation purposes.
 * Three types of regions may currently be selected: point, box, and circle.
 * The selected region will initially show up in white.
 */
public class SelectionOverlay extends OverlayJPanel 
{
   public static final String REGION_ADDED   = "REGION_ADDED";
   public static final String REGION_REMOVED = "REGION_REMOVED";
   public static final String ALL_REGIONS_REMOVED = "ALL_REGIONS_REMOVED";
   
   private SelectionJPanel sjp;           // panel overlaying the center jpanel
   private IZoomAddible component;        // component being passed
   private Vector regions;                // all selected regions
   private SelectionOverlay this_panel;   // used for repaint by SelectListener 
   private Color reg_color;
   private Rectangle current_bounds;
   private CoordTransform pixel_local;  
   private Vector Listeners = null;  
   private float opacity = 1.0f;          // value [0,1] where 0 is clear, 
                                          // and 1 is solid.
   private SelectionEditor editor;
  
  /**
   * Constructor creates an OverlayJPanel with a SeletionJPanel that shadows the
   * center panel of the IZoomAddible component.
   *
   *  @param  component
   */ 
   public SelectionOverlay(IZoomAddible iza)
   {
      super();
      this.setLayout( new GridLayout(1,1) );
      editor = new SelectionEditor();
      //editor.setVisible(false);
      sjp = new SelectionJPanel();
      component = iza;
      regions = new Vector();       
      this_panel = this;
      reg_color = Color.white;
       
      this.add(sjp);
      sjp.addActionListener( new SelectListener() ); 
      current_bounds = component.getRegionInfo();
      //this_panel.setBounds( current_bounds );
      CoordBounds pixel_map = 
                   new CoordBounds( (float)current_bounds.getX(), 
                                    (float)current_bounds.getY(),
                                    (float)(current_bounds.getX() + 
				            current_bounds.getWidth()),
			            (float)(current_bounds.getY() + 
				            current_bounds.getHeight() ) );
      pixel_local = new CoordTransform( pixel_map, 
                                        component.getLocalCoordBounds() );
      
      Listeners = new Vector();
      sjp.requestFocus();               
   }

  /**
   * Contains/Displays control information about this overlay.
   */
   public static void help()
   {
      JFrame helper = new JFrame("Help for Selection Overlay");
      helper.setBounds(0,0,600,400);
      JTextArea text = new JTextArea("Discription:\n\n");
      text.setEditable(false);
      text.setLineWrap(true);
      text.append("The Selection Overlay is used to selection regions of " +
                  "data for analysis. The selected region will initially be " +
                  "outlined in white, unless otherwise specified.\n\n");
      text.append("Commands for Selection Overlay\n\n");
      text.append("Note:\n" +
                  "- These commands will NOT work if the Annotation " +
                  "Overlay checkbox IS checked or if the Selection " + 
		  "Overlay IS NOT checked.\n" +
		  "- Zooming on the image is only allowed if this overlay " +
		  "is turned off.\n\n" );
      text.append("*******************************************\n");
      text.append("Image Commands:\n");
      text.append("*******************************************\n");
      text.append("Click/Drag/Release Mouse w/B_Key pressed>" + 
                  "ADD BOX SELECTION\n");
      text.append("Click/Drag/Release Mouse w/C_Key pressed>" + 
                  "ADD CIRCLE SELECTION\n");
      text.append("Click/Drag/Release Mouse w/P_Key pressed>" + 
                  "ADD POINT SELECTION\n");
      text.append("Double Click Mouse>REMOVE LAST SELECTION\n");
      text.append("Single Click Mouse w/A_Key>REMOVE ALL SELECTIONS\n\n");
      text.append("*******************************************\n");
      text.append("Selection Editor Commands (Edit button under Selection " +
                  "Overlay Control)\n");
      text.append("*******************************************\n");
      text.append("Click on \"Change Color\" to CHANGE COLOR OF SELECTION.\n");
      text.append("Move slider to CHANGE OPACITY OF SELECTION. If highly " +
                  "opaque, lines show bright. Low opacity makes selections " +
		  "clear or transparent.\n\n");
      
      JScrollPane scroll = new JScrollPane(text);
      scroll.setVerticalScrollBarPolicy(
                                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      helper.getContentPane().add(scroll);
      helper.setVisible(true);
   }
   
   public void setOpacity( float value )
   {
     if( value > 1 )
       opacity = 1.0f;
     else if( value < 0 )
       opacity = 0;
     else
       opacity = value;
   }
   
   public void editSelection()
   {
     if( editor.isVisible() )
     {
       editor.toFront();
       editor.requestFocus();
     }
     else
     {
       editor.dispose();
       editor = new SelectionEditor();
       editor.setVisible(true);
       editor.toFront();
     }
   }
   
  /**
   * Method to add a listener to this overlay.
   *
   *  @param act_listener
   */
   public void addActionListener( ActionListener act_listener )
   {          
      for ( int i = 0; i < Listeners.size(); i++ )    // don't add it if it's
        if ( Listeners.elementAt(i).equals( act_listener ) ) // already there
          return;

      Listeners.add( act_listener ); //Otherwise add act_listener
   }
  
  /**
   * Method to remove a listener from this component.
   *
   *  @param act_listener
   */ 
   public void removeActionListener( ActionListener act_listener )
   {
      Listeners.remove( act_listener );
   }
  
  /**
   * Method to remove all listeners from this component.
   */ 
   public void removeAllActionListeners()
   {
      Listeners.removeAllElements();
   }
   
   public Vector getSelectedRegions()
   {
      return regions;
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
   * center of the IZoomAddible component.
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
      // testing...
      AlphaComposite ac = AlphaComposite.getInstance( AlphaComposite.SRC_OVER,
                                                      opacity );
      g2d.setComposite(ac);
      // end of testing...
      current_bounds = component.getRegionInfo();  // current size of center
      sjp.setBounds( current_bounds );
      // this limits the paint window to the size of the background image.
      g2d.clipRect( (int)current_bounds.getX(),
                    (int)current_bounds.getY(),
		    (int)current_bounds.getWidth(),
		    (int)current_bounds.getHeight() );
      //this_panel.setBounds( current_bounds ); 
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
      // color of all of the selections.
      g2d.setColor(reg_color);

      WCRegion regionclass;      
      String region;
      floatPoint2D[] fp;
      Point[] p;
      for( int num_reg = 0; num_reg < regions.size(); num_reg++ )
      {
         regionclass = (WCRegion)regions.elementAt(num_reg);
         p = new Point[regionclass.getNumWCP()];
	 fp = regionclass.getWorldCoordPoints();
	 for( int i = 0; i < regionclass.getNumWCP(); i++ )
	   p[i] = convertToPixelPoint( fp[i] );
	 	    	   
	 region = ((WCRegion)regions.elementAt(num_reg)).getRegionType(); 
         //System.out.println("Point: " + p[0].x + "," + p[0].y );   
         if( region.equals( SelectionJPanel.CIRCLE ) )
	 {
	    g2d.drawOval( p[0].x, p[0].y, p[1].x - p[0].x, p[1].y - p[0].y );
	 }
         else if( region.equals( SelectionJPanel.BOX ) )
	 {
	    g2d.drawRect( p[0].x, p[0].y, p[1].x - p[0].x, p[1].y - p[0].y );
	 }
         else if( region.equals( SelectionJPanel.LINE ) )
	 {
	    g2d.drawLine( p[0].x, p[0].y, p[1].x, p[1].y ); 	    
	 }
         else if( region.equals( SelectionJPanel.POINT ) )
	 {
	    //System.out.println("Drawing instance of point at " + 
	    //                 ((Point)region).x + "/" + ((Point)region).y );
	    g2d.drawLine( p[0].x - 5, p[0].y, p[0].x + 5, p[0].y ); 	    
	    g2d.drawLine( p[0].x, p[0].y - 5, p[0].x, p[0].y + 5 );
	 }	 	 
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
   * Tells all listeners about a new action.
   *
   *  @param  message
   */  
   private void sendMessage( String message )
   {
     for ( int i = 0; i < Listeners.size(); i++ )
     {
       ActionListener listener = (ActionListener)Listeners.elementAt(i);
       listener.actionPerformed( new ActionEvent( this, 0, message ) );
     }
   }

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
	    if( regions.size() > 0 )
	    {
	       regions.clear(); 
	       sendMessage(ALL_REGIONS_REMOVED);
	    }         
	 }
	 // remove the last selection from the vector
         else if( message.equals( SelectionJPanel.RESET_LAST_SELECTED ) )
         {
	    if( regions.size() > 0 )
	    {
	       regions.removeElementAt(regions.size() - 1);  
	       sendMessage(REGION_REMOVED);
	    }      	      
	 }
	 // region is specified by REGION_SELECTED>BOX >CIRCLE >POINT	
	 // if REGION_SELECTED is in the string, find which region 
	 else if( message.indexOf( SelectionJPanel.REGION_SELECTED ) > -1 )
         {
	    if( message.indexOf( SelectionJPanel.BOX ) > -1 )
	    {
	       Rectangle box = ((BoxCursor)sjp.getCursor( 
	                              SelectionJPanel.BOX )).region();
	       Point p1 = new Point( box.getLocation() );
	       p1.x += (int)current_bounds.getX();
	       p1.y += (int)current_bounds.getY();
	       Point p2 = new Point( p1 );
	       p2.x += (int)box.getWidth();
	       p2.y += (int)box.getHeight();
	       floatPoint2D[] tempwcp = new floatPoint2D[2];
	       tempwcp[0] = convertToWorldPoint( p1 );
	       tempwcp[1] = convertToWorldPoint( p2 );
	                                            
	       WCRegion boxregion = new WCRegion(SelectionJPanel.BOX, tempwcp);
	                                      
	       regions.add( boxregion );
	       //System.out.println("Drawing box region" );
	    }
	    else if( message.indexOf( SelectionJPanel.CIRCLE ) > -1 )
	    {
	       Circle circle = ((CircleCursor)sjp.getCursor( 
	                              SelectionJPanel.CIRCLE )).region();
	       Point p1 = new Point( circle.getDrawPoint() );
	       p1.x += (int)current_bounds.getX();
	       p1.y += (int)current_bounds.getY();
	       Point p2 = new Point( circle.getCenter() );
	       p2.x += circle.getRadius() + (int)current_bounds.getX();
	       p2.y += circle.getRadius() + (int)current_bounds.getY();
	       floatPoint2D[] tempwcp = new floatPoint2D[2];
	       tempwcp[0] = convertToWorldPoint( p1 );
	       tempwcp[1] = convertToWorldPoint( p2 );
	                                            
	       WCRegion circleregion = new WCRegion( SelectionJPanel.CIRCLE, 
	                                             tempwcp );
	       regions.add( circleregion );
	    }	    
	    else if( message.indexOf( SelectionJPanel.LINE ) > -1 )
	    {
	      Line line = ((LineCursor)sjp.getCursor( 
	                              SelectionJPanel.LINE )).region();
	      Point p1 = new Point( line.getP1() );
	      p1.x += (int)current_bounds.getX();
	      p1.y += (int)current_bounds.getY();
	      Point p2 = new Point( line.getP2() );
	      p2.x += (int)current_bounds.getX();
	      p2.y += (int)current_bounds.getY();
	      floatPoint2D[] tempwcp = new floatPoint2D[2];
	      tempwcp[0] = convertToWorldPoint( p1 );
	      tempwcp[1] = convertToWorldPoint( p2 );
	                                           
	      WCRegion lineregion = new WCRegion( SelectionJPanel.LINE, 
	                                          tempwcp );
	      regions.add( lineregion );
	    }	    
	    else if( message.indexOf( SelectionJPanel.POINT ) > -1 )
	    { 
	       //System.out.println("Drawing point region" );
	       // create new point, otherwise regions would be shared.
	       Point np = new Point( ((PointCursor)
	               sjp.getCursor( SelectionJPanel.POINT )).region() );
	       np.x += (int)current_bounds.getX();
	       np.y += (int)current_bounds.getY();
	       floatPoint2D[] tempwcp = new floatPoint2D[1];
	       tempwcp[0] = convertToWorldPoint( np );
               //System.out.println("tempwcp[0]: "+tempwcp[0].x+tempwcp[0].y );
	       regions.add( new WCRegion(SelectionJPanel.POINT, tempwcp) );
	    }
	    sendMessage(REGION_ADDED);
	 }
	 this_panel.repaint();  // Without this, the newly drawn regions would
	                        // not appear.
      }  // end actionPerformed()   
   } // end SelectListener
   
   private class SelectionEditor extends JFrame
   {
     private Box pane;
     private SelectionEditor this_editor;
     
     public SelectionEditor()
     {
       super("SelectionEditor");
       this.setBounds(0,0,200,125);
       this_editor = this;
       pane = new Box( BoxLayout.Y_AXIS	);
       JButton colorbutton = new JButton("Change Color");
       colorbutton.setAlignmentX( Component.CENTER_ALIGNMENT );
       colorbutton.addActionListener( new ControlListener() );
       pane.add(colorbutton);
       
       ControlSlider opacityscale = new ControlSlider();
       opacityscale.setValue(opacity*100f);
       opacityscale.setTitle("Selection Opacity Scale");
       opacityscale.addActionListener( new ControlListener() );
       pane.add(opacityscale);
	 
       JButton closebutton = new JButton("Close");
       closebutton.setAlignmentX( Component.CENTER_ALIGNMENT );
       closebutton.addActionListener( new ControlListener() );
       pane.add( closebutton );
       
       this.getContentPane().add(pane);
     }
     
     class ControlListener implements ActionListener
     {
       public void actionPerformed( ActionEvent ae )
       {
	 String message = ae.getActionCommand();
         if( message.equals("Change Color") )
	 {
           Color temp =
	          JColorChooser.showDialog(pane, "Selection Color", reg_color);
	   if( temp != null )
	   {
	     reg_color = temp;
	     this_panel.repaint();
	   }
         }
	 else if( message.equals( IViewControl.SLIDER_CHANGED ) )
	 {
	   opacity = ((ControlSlider)ae.getSource()).getValue() / 100f;
	   this_panel.repaint();
	 }
	 else if( message.equals("Close") )
	 {   
	   this_editor.dispose();
	   this_panel.repaint();
	 }
       }
     }
   }
}
