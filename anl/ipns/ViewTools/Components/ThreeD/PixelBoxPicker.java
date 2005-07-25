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
 * Modified:
 *
 *  $Log$
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
import SSG_Tools.SSG_Nodes.Shapes.PixelBox;
import SSG_Tools.SSG_Nodes.Groups.DetectorGroup;

/* ------------------------ PixelBoxPicker --------------------------- */
/**
 *  This class listens for mouse clicks and then uses the JoglPanel's 
 *  pickHitList() method to get and print the closest PixelBox 
 *  that is "hit" by a ray through the current x,y pixel locations.
 *
 *  One a hit, the handler will store the detector id and pixel id of any
 *  click on a detector pixel.
 */
public class PixelBoxPicker extends MouseAdapter
{
   private JoglPanel my_panel;
   private HitRecord closestHit;
   private Vector3D point;
   private int detectorid, pixelid;
   private float pixelval;

   /**
    * Constructor. Makes PixelBoxPicker
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
    * coordinates for the click.
    *
    *   @param e Mouse Event gives (x,y) screen coordinates.
    */
   public void mouseClicked (MouseEvent e)
   {
     if ( e.getClickCount() == 1 )
     {
        int x = e.getX();
        int y = e.getY();

        // Test object selection by printing the "hit list", giving the IDs
        // for the object selected and it's named parents. 

        HitRecord hitlist[] = my_panel.pickHitList( x, y );
        if ( hitlist.length > 0 )
          Toolkit.getDefaultToolkit().beep();

        System.out.println( "MouseClickHandler: num_hits = "+hitlist.length);
        System.out.println( "x,y = " + x + ", " + y );
        
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
        if(closestHit != null)
        {
          System.out.println("Closest hit = " + closestHit );
           
          int name = closestHit.lastName();
          Node node = Node.getNodeWithID( name );
          detectorid = pixelid = -1;
          pixelval = 0;
          while( node != null)
          {
            if(node instanceof PixelBox && pixelid == -1) 
            {
              pixelid = ((PixelBox)node).getPixelID();
              pixelval = ((PixelBox)node).getValue();
              System.out.println( "Pixel = " + pixelid );
            }
                        
            if(node instanceof DetectorGroup && detectorid == -1)
            {
              detectorid = ((DetectorGroup)node).getDetectorID();
              System.out.println( "Detector = " + detectorid );
            }
            
            node = node.getParent();
          }
        }
        
        // Test Picked Point by printing the 3D coordinates of the point
        // that was clicked on.
        point = my_panel.pickedPoint( x, y );
        System.out.println("3D point = " + point );      
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
    * Returns 3d coordinates of click
    *
    *   @return 3d coordinates of click
    */
    public float[] get3DPoint()
    {
      return point.get();
    }
}

