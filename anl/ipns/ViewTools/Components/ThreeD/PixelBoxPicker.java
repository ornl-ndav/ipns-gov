/*
 * File:  PixelBoxPicker.java
 *
 * Copyright (C) 2005, Chad Jones
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
 * Primary   Chad Jones <cjones@cs.utk.edu>
 * Contact:  Student Developer, University of Tennessee
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 * 
 * This work was supported by the University of Tennessee Knoxville and 
 * the Spallation Neutron Source at Oak Ridge National Laboratory under: 
 *   Support of HFIR/SNS Analysis Software Development 
 *   UT-Battelle contract #:   4000036212
 *   Date:   Oct. 1, 2004 - Sept. 30, 2006
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.5  2005/08/04 22:36:45  cjones
 *  Updated documentation and comment header.
 *
 *  Revision 1.4  2005/07/29 20:42:46  cjones
 *  The picker will now store the Pick IDs for the pixel shape and detector
 *  group on mouse hit.  It will also update the detector pick id
 *  on double clicks.
 *
 *  Revision 1.3  2005/07/27 20:36:37  cjones
 *  Added menu item that allows the user to choose between different shapes
 *  for the pixels. Also, in frames view, user can change the time between
 *  frame steps.
 *
 *  Revision 1.2  2005/07/25 21:27:54  cjones
 *  Added support for MouseArcBall and a control checkbox to toggle it. Also,
 *  the value of the selected pixel is now displayed with the Pixel Info, and
 *  updates on frame changes.
 *
 *  Revision 1.1  2005/07/19 15:56:38  cjones
 *  Added components for Display3D.
 *
 */

package gov.anl.ipns.ViewTools.Components.ThreeD;

import java.awt.*;
import java.awt.event.*;

import  gov.anl.ipns.MathTools.Geometry.Vector3D;
import SSG_Tools.Viewers.*;
import SSG_Tools.SSG_Nodes.*;
import SSG_Tools.SSG_Nodes.Util.*;
import SSG_Tools.SSG_Nodes.Groups.DetectorGroup;

/* ------------------------ PixelBoxPicker --------------------------- */
/**
 *  This class listens for mouse clicks and then uses the JoglPanel's 
 *  pickHitList() method to get and print the closest PixelBox 
 *  that is "hit" by a ray through the current x,y pixel locations.
 *
 *  One a hit, the handler will store the detector id and pixel id of any
 *  click on a detector pixel along with the unique pick ids for both.
 *  The pixel value will also be stored.
 */
public class PixelBoxPicker extends MouseAdapter
{
   private JoglPanel my_panel;  // To get hitlist
   private HitRecord closestHit;
   private Vector3D point;
   private int detectorid, detector_pickid;
   private int pixelid, pixel_pickid;
   private float pixelval;

   /**
    * Constructor. Makes PixelBoxPicker.
    *
    *   @param panel The JoglPanel mouse will be used in.
    */
   public PixelBoxPicker( JoglPanel panel )
   {
     my_panel = panel;
   }

   /**
    * Mouse Click Handler.  Whenever there is a single click, this method
    * will find the HitRecord closest to the user and, if availiable, store
    * the pixel id and detector id on object hit.  It will also store the 3d
    * coordinates and pixel values for the click.  A double click will update
    * the detector id.
    *
    *   @param e Mouse Event gives (x,y) screen coordinates.
    */
   public void mouseClicked (MouseEvent e)
   {
   	 // Update selected pixel.
     if ( e.getClickCount() == 1 )
     {
        int x = e.getX();
        int y = e.getY();

        // Test object selection by printing the "hit list", giving the IDs
        // for the object selected and it's named parents. 

        HitRecord hitlist[] = my_panel.pickHitList( x, y );
        if ( hitlist.length > 0 )
          Toolkit.getDefaultToolkit().beep();
        
        // Find closest hit
        closestHit = null;
        for ( int i = 0; i < hitlist.length; i++ ) {
          //System.out.println("hit = " + hitlist[i] );
          if(i == 0) 
            closestHit = hitlist[i];
          else if(hitlist[i].getMin() < closestHit.getMin()) 
            closestHit = hitlist[i];     
        }
        
        // Traverse hit to find pixel id and detector id.
        pixel_pickid = detector_pickid = -1;
        if(closestHit != null)
        {
           
          int name = closestHit.lastName();
          Node node = Node.getNodeWithID( name );
          
          detectorid = pixelid = -1;
          pixelval = 0;
          while( node != null)
          { // Go through node that was hit to find pixel info
            if(node instanceof IPixelShape && pixelid == -1) 
            {
              pixelid = ((IPixelShape)node).getPixelID();
              pixelval = ((IPixelShape)node).getValue();
              pixel_pickid = node.getPickID();
            }
            // Find detector info
            if(node instanceof DetectorGroup && detectorid == -1)
            {
              detectorid = ((DetectorGroup)node).getDetectorID();
              detector_pickid = node.getPickID();
            }
            
            node = node.getParent();
          }
        }
        
        // Test Picked Point by printing the 3D coordinates of the point
        // that was clicked on.
        point = my_panel.pickedPoint( x, y );      
     }
     
     // Detector ID update.
     else if ( e.getClickCount() == 2 )
     {
        int x = e.getX();
        int y = e.getY();

        // Test object selection by printing the "hit list", giving the IDs
        // for the object selected and it's named parents. 

        HitRecord hitlist[] = my_panel.pickHitList( x, y );

        // Find closest hit
        closestHit = null;
        for ( int i = 0; i < hitlist.length; i++ ) {
          //System.out.println("hit = " + hitlist[i] );
          if(i == 0) 
            closestHit = hitlist[i];
          else if(hitlist[i].getMin() < closestHit.getMin()) 
            closestHit = hitlist[i];     
        }
        
        // Traverse hit to find pixel id and detector id.
        detector_pickid = -1;
        if(closestHit != null)
        {          
          int name = closestHit.lastName();
          Node node = Node.getNodeWithID( name );

          while( node != null)
          { // Just find detector info
            if(node instanceof DetectorGroup && detector_pickid == -1)
            {
              detector_pickid = node.getPickID();
              break;
            }
            
            node = node.getParent();
          }
        }
     }
   }
   
   /**
    * Returns Pixel ID.
    *
    *   @return Pixel ID or -1 for no hit.
    */
    public int getPixelID()
    {
      return pixelid;
    }
    
    /**
     * Returns Pixel Pick ID that identifies the shape
     * within the scene graph
     *
     *   @return Pixel Pick ID or -1 for no hit.
     */
     public int getPixelPickID()
     {
       return pixel_pickid;
     }
    
   /**
    * Returns Pixel Value.
    *
    *   @return Pixel Value if hit
    */
    public float getPixelValue()
    {
      return pixelval;
    }
    
   /**
    * Returns Detector ID.
    *
    *   @return Detector ID or -1 for no hit.
    */
    public int getDetectorID()
    {
      return detectorid;
    }
    
    /**
     * Returns Detector Pick ID that identifies the detector
     * group within the scene graph
     *
     *   @return Detector Pick ID or -1 for no hit.
     */
     public int getDetectorPickID()
     {
       return detector_pickid;
     }
    
   /**
    * Returns 3d coordinates of click
    *
    *   @return 3d coordinates of click
    */
    public float[] get3DPoint()
    {
      return point.get();
    }
}

