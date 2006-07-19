/*
 * File:  DetectorScene.java
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
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.6  2006/07/19 18:07:43  dennis
 *  Removed unused imports.
 *
 *  Revision 1.5  2005/08/04 22:36:43  cjones
 *  Updated documentation and comment header.
 *
 *  Revision 1.4  2005/07/27 20:36:31  cjones
 *  Added menu item that allows the user to choose between different shapes
 *  for the pixels. Also, in frames view, user can change the time between
 *  frame steps.
 *
 *  Revision 1.3  2005/07/25 21:27:53  cjones
 *  Added support for MouseArcBall and a control checkbox to toggle it. Also,
 *  the value of the selected pixel is now displayed with the Pixel Info, and
 *  updates on frame changes.
 *
 *  Revision 1.2  2005/07/22 19:45:09  cjones
 *  Separated 3D components into one base object and two functional objects,
 *  one for data with frames and one for data without frames. Also, added features
 *  and tweaked functionality.
 *
 *  Revision 1.1  2005/07/19 15:56:34  cjones
 *  Added components for Display3D.
 * 
 */
 
package gov.anl.ipns.ViewTools.Components.ThreeD;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.java.games.jogl.*;

import gov.anl.ipns.MathTools.Geometry.Vector3D;
import SSG_Tools.Viewers.*;
import SSG_Tools.Cameras.*;

import SSG_Tools.SSG_Nodes.SimpleShapes.SimpleShape;
import SSG_Tools.SSG_Nodes.Groups.*;

import gov.anl.ipns.ViewTools.Components.IPhysicalArray3D;
import gov.anl.ipns.ViewTools.Components.PhysicalArray3D;
import gov.anl.ipns.ViewTools.Components.LogScaleColorModel;

/**
 * This class is used to draw a 3D scene consisting of detector groups.  Each
 * IPhysicalArray3D that is fed into the constructor represents a collection of
 * detector pixels, which will be drawn as solid boxes using the physical 
 * information stored in the array.  The detectors will be given the ID of 
 * the array, and each pixel within an array will be given its index as its
 * ID.  A circle will be created to surround the detectors on the horizontal
 * axis and three lines will be created to represent the axes.
 * 
 * Each pixel has a single value that is used to color the pixel
 * when a color model is given.  The shape of the pixels can be 
 * changed by providing on of the statically defined ints describing
 * shapes.  
 */
public class DetectorScene extends DetectorSceneBase
{
  private IPhysicalArray3D[] points;
                          

  /* --------------------------- Constructor --------------------------- */
  /**
   *  Construct the scene objects using the given physical arrays.
   *  Each 3d point will be given the specified shape.  The orientation
   *  and dimensions of the shape are also contained within the physical 
   *  arrays.
   *
   *   @param pa3D       Arrays containing position, extent, and 
   *                     orientation data.
   *   @param shapeType  The type of shape to draw for all the given
   *                     detectors' pixels.
   */
  public DetectorScene(IPhysicalArray3D[] pa3D, int shapeType)
  {
    super();
    
    points = pa3D;
    
    for(int det = 0, id = 0; det < points.length; det++)
    {
      // This is the detector id that the user set in the array.
      if(points[det] != null) id = points[det].getArrayID();
      else id = -1;
      
      // Add detector to the scene.
      addDetector(id, shapeType, points[det], points[det]);
    }
    
    addSceneCircle();
    addLineAxes();
  }
  
  
  /**
   * This method changes all of the detectors' pixels
   * to be of the given shape type.  The scene is cleared
   * and reconstructed.
   * 
   * @param shapeType The type of shape to use as defined
   *                  by DetectorSceneBase.
   */
  public void changeShape(int shapeType)
  {
    Clear();
    
    for(int det = 0, id = 0; det < points.length; det++)
    {
      if(points[det] != null) id = points[det].getArrayID();
      else id = -1;
      
      addDetector(id, shapeType, points[det], points[det]);
    }
    
    if(sceneCircleOn) addSceneCircle();
    if(axisLinesOn) addLineAxes();
  }

 /**
  * This method updates the colors of each pixel to reflect changes
  * in the color table.  It assumes the detectors are the first
  * items added to the group and that there is a detector group
  * for each element of the data array.
  * 
  *  @param model This object is used to map the data value to a 
  *               color within the table.
  */
  public void applyColor(LogScaleColorModel model)
  {
    for ( int det = 0; det < points.length; det++ )
    {
      if(points[det] != null)
      {
        DetectorGroup detector = (DetectorGroup)getChild(det);
    	
        for( int i = 0; i < detector.numChildren(); i++)
        {
          float value = points[det].getValue(i);
          
          // Each child of the detector is a shape
          SimpleShape shape = (SimpleShape)detector.getChild(i);
          // Update pixel's color and value
          shape.setColor( model.getColor(value) );
          ((IPixelShape)shape).setValue(value);
        }
      }
    }
    compileDisplayList = true;  //Recompile display list.
  }
  

  /* --------------------------- main ----------------------------------- */
  /**
   *  Main program that constructs an instance of the scene and displays 
   *  it in 3D for testing purposes.  
   */
  public static void main( String args[] )
  {  
  	int detectorsize = 100;
  	int numdetectors = 2;
  	
  	IPhysicalArray3D[] data = new IPhysicalArray3D[numdetectors];

    // Create 2 square panels
    // PANEL 1
    data[0] = new PhysicalArray3D(detectorsize);
    for(int i = 0; i < 10; i++)
      for(int j = 0; j < 10; j++)
      {
      	data[0].set(i*10+j, // Index
      			  new Vector3D(i*50, j*50, 700), // 3D Coordinates
                  new Vector3D(11, 13, 12),  // Box volume
                  new Vector3D(1, 0, 0), // Orientation (X-Axis)
				  new Vector3D(0, 1, 0), // Orientation (Y-Axis)
				  j*30+i*10-150); // Value
      }
  	data[0].setArrayID(0);  // Panel ID
    
    // PANEL 2
    data[1] = new PhysicalArray3D(detectorsize);
    for(int i = 0; i < 10; i++)
      for(int j = 0; j < 10; j++)
      {
        data[1].set(i*10+j, // Index
			      new Vector3D(i*50, j*50, -200), // 3D Coordinates
                  new Vector3D(9, 10, 9), // Box Volume
                  new Vector3D(4, 0, -2), // Orientation (X-Axis)
				  new Vector3D(1, 1, 2),  // Orientation (Y-Axis)
				  j*30+i*10+750); // Value
      }
    data[1].setArrayID(1); // Panel ID
  
    // Make Scene
    DetectorScene scene = new DetectorScene(data, BOX);

    // Make JoglPanel to render scene
    final JoglPanel demo = new JoglPanel( scene );
    
    // Make camera that is accurately positioned to view volume
    demo.setCamera( scene.makeCamera() );
    
    demo.getDisplayComponent().addMouseListener( new PixelBoxPicker( demo ));
    demo.enableLighting( true );
    demo.enableHeadlight( true );
  
    // Make Controller to move scene
    final gov.anl.ipns.ViewTools.Components.ViewControls.AltAzController
    controller = 
    new gov.anl.ipns.ViewTools.Components.ViewControls.AltAzController
                                        (90, 0, 1, 
                                        4*scene.getDiameter(), 
                                        1.5f*scene.getDiameter());
    controller.setMaximumSize(new Dimension(100,300));
    
    controller.setCOP(new Vector3D(demo.getCamera().getCOP().get()));
    controller.setVRP(new Vector3D(demo.getCamera().getVRP().get()));
    controller.setVUV(new Vector3D(demo.getCamera().getVUV().get()));
    
    controller.addActionListener( 
       new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           Camera view = demo.getCamera();
           
           view.setCOP(new Vector3D(controller.getCOP().get()));
           
           ((GLCanvas)demo.getDisplayComponent()).display();
         }
       });
   
    JPanel test = new JPanel();
    test.setSize(500, 500);
    test.setLayout( new GridLayout() );
    test.setMinimumSize(new java.awt.Dimension(10,10));
    test.add(demo.getDisplayComponent());
    test.setVisible(true);   
    
    JFrame frame = new JFrame( "Box Scene" );
    frame.setSize(800, 600);
    frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
    frame.getContentPane().setLayout( new BoxLayout(frame.getContentPane(),
                                      BoxLayout.X_AXIS) );
    frame.getContentPane().add( test );
    frame.getContentPane().add( controller );
    frame.setVisible(true);
    
    frame.show();
  }

}