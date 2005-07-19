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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *  Modified:
 *
 *  $Log$
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

import SSG_Tools.Appearance.*;
import SSG_Tools.SSG_Nodes.*;
import SSG_Tools.SSG_Nodes.Shapes.*;
import SSG_Tools.SSG_Nodes.Shapes.Shape;
import SSG_Tools.SSG_Nodes.Groups.*;
import SSG_Tools.SSG_Nodes.Groups.Transforms.*;

import gov.anl.ipns.ViewTools.Components.IPhysicalArray3D;
import gov.anl.ipns.ViewTools.Components.PhysicalArray3D;
import gov.anl.ipns.ViewTools.Components.LogScaleColorModel;

/**
 * This class is used to draw a 3D scene consisting of detector groups.  Each
 * IPhysicalArray3D that is fed into the constructor represents a collection of
 * detector pixels, which will be drawn as solid boxes using the physical 
 * information stored in the array.  The detectors will be given the ID of 
 * the array, and each pixel within an array will be given its index as its
 * ID.
 */
public class DetectorScene extends Group
{
  private IPhysicalArray3D[] points;
  
  private float[] center = new float[3];
  
  private float diameter = 0;
  
  private boolean compileDisplayList = true;     
                         

  /* --------------------------- Constructor --------------------------- */
  /**
   *  Construct the scene objects with using the given physical arrays.
   *  Each 3d point will be given a box.  The orientation and volume of box
   *  is also contained within the physical arrays.
   *
   *    @param pa3D Arrays containing position, extent, and orientation data.
   */
  public DetectorScene(IPhysicalArray3D[] pa3D)
  {
    setPickID( Node.INVALID_PICK_ID );       // set a PickID for the root, for testing
    
    points = pa3D;
    
    float value;
    float[] extents, base, up, position;
    float[] max_point = new float[3];
    float[] min_point= new float[3];
    float[] bbox_low = new float[3];
    float[] bbox_high = new float[3];
    
    // Init min's
    bbox_low[0] = bbox_low[1] = bbox_low[2] = 
    min_point[0] = min_point[1] = min_point[2] = Float.POSITIVE_INFINITY;
    
    // Init max's
    bbox_high[0] = bbox_high[1] = bbox_high[2] = 
    max_point[0] = max_point[1] = max_point[2] = Float.NEGATIVE_INFINITY;
    
    // construct the boxes and add them to the scene graph
    for ( int det = 0; det < points.length; det++)
    { 
      Group detector;      
            
      if(points[det] != null)
      {
      	detector = new DetectorGroup(points[det].getArrayID());
      	
        for ( int i = 0; i < points[det].getNumPoints(); i++ )
        {            
          // Use extents to give size of box         
          extents = points[det].getExtents(i).get();
          Shape box = new PixelBox( i,
          							extents[0], extents[1], extents[2] );
        
          // THIS NEEDS TO BE CHANGED TO UNIQUE ID
          box.setPickID( det*100+i );
      
          // Use position and orientation to place the box in space.
          base = points[det].getOrientation(i)[0].get();
          up   = points[det].getOrientation(i)[1].get();
          position = points[det].getPoint(i).get();
          OrientationTransform trans = 
              new OrientationTransform(new Vector3D( base ), new Vector3D( up ),
                                       new Vector3D( position ) );
          
          // Use max point to find center
          for(int j = 0; j < 3; j++) 
          {
            if(position[j] > max_point[j]) max_point[j] = position[j];
            if(position[j] < min_point[j]) min_point[j] = position[j];
          }
                                                   
          trans.setPickID( Node.INVALID_PICK_ID );
          trans.addChild( box );
          detector.addChild( trans );
        }
      
        // Use edges to set bounding box information
        float[] minedges = points[det].getMinEdges().get();
        float[] maxedges = points[det].getMaxEdges().get();
                
        for(int i = 0; i < 3; i++)
        {
          if(minedges[i] < bbox_low[i]) bbox_low[i] = minedges[i];
          if(maxedges[i] > bbox_high[i]) bbox_high[i] = maxedges[i];
        } 
      }
      else detector = new DetectorGroup(-1);

      //THIS NEEDS TO BE CHANGED TO A UNIQUE ID
      detector.setPickID(10000+det+1);
      addChild(detector);
    }
    
    center[0] = (min_point[0] + max_point[0])/2;
    center[1] = (min_point[1] + max_point[1])/2;
    center[2] = (min_point[2] + max_point[2])/2;
    
    if((bbox_high[0]-bbox_low[0]) > diameter) 
      diameter = bbox_high[0]-bbox_low[0];
    if((bbox_high[1]-bbox_low[1]) > diameter) 
      diameter = bbox_high[1]-bbox_low[1];
    if((bbox_high[2]-bbox_low[2]) > diameter) 
      diameter = bbox_high[2]-bbox_low[2];
  }

 /**
  * This method updates the colors of each pixel to reflect changes
  * in the color table.
  * 
  *  @param model This object is used to map the data value to a 
  *               color within the table.
  */
  public void applyColor(LogScaleColorModel model)
  {
    for ( int det = 0; det < points.length; det++ )
    {
      Group detector = (Group)getChild(det);
    	
      for( int i = 0; i < detector.numChildren(); i++)
      {
      	// From DetectorGroup, get Transform. From Transform, get Shape
        Shape box = (Shape)((Group)detector.getChild(i)).getChild(0);
        float value = points[det].getValue(i);
        box.setAppearance( new Appearance(
        		            new Material( model.getColor(value) ) ) );
      }
    }
    compileDisplayList = true;  //Recompile display list.
  }
  
  /**
   * This creates a camera based on the bounding box surrounding the scene.
   * It will center the view of the camera to the middle of the scene, place
   * the up position along the y axis and move the camera back along the z axis
   *
   *   @return Camera to be put into JoglPanel
   */
  public Camera makeCamera()
  {
    float[] eye = new float[3];
    float[] up = new float[3];
    
    eye[0] = center[0];
    eye[1] = center[1];
    eye[2] = center[2];
    eye[2] += 1.5f*diameter;

    
    up[0] = 0;
    up[1] = 1;
    up[2] = 0;
    
    Camera view = new PerspectiveCamera();
    
    /*
    System.out.println("Eye: <" + eye[0] + ", " + eye[1] + ", " + eye[2] + ">");
    System.out.println("Center: <" + center[0] + ", " + center[1] + ", " + 
                        center[2] + ">");
    System.out.println("Up: <" + up[0] + ", " + up[1] + ", " + up[2] + ">");
    */
    
    view.SetView(new Vector3D(eye), new Vector3D(center), new Vector3D(up));
    view.SetViewVolume(1, diameter*10, 60.f);
    
    return view;
  }
  
  /**
   * This returns the diameter of volume containing the entire scene
   *
   *  @return The diameter of volume containing entire scene.
   */
  public float getDiameter()
  {
    return diameter;
  }

   
  /**
   * This method renders the scene by first collecting the GL calls into
   * a display list and making a call to that display list once it is compiled.
   *
   *   @param  drawable  The drawable on which the object is to be rendered.
   */
  public void Render( GLDrawable drawable )
  { 
    GL gl = drawable.getGL();

    if(compileDisplayList) {
      gl.glNewList( 1, GL.GL_COMPILE_AND_EXECUTE ); 
      super.Render( drawable );
      gl.glEndList();
      
      compileDisplayList = false;
    }
    else 
    {
      gl.glCallList(1);
    }
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
    DetectorScene scene = new DetectorScene(data);

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