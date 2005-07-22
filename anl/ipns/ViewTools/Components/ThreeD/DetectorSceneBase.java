/*
 * File:  DetectorSceneBase.java
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
 *  Revision 1.2  2005/07/22 21:39:48  cjones
 *  Adjusted the colored lines for the axes.
 *
 *  Revision 1.1  2005/07/22 19:45:11  cjones
 *  Separated 3D components into one base object and two functional objects,
 *  one for data with frames and one for data without frames. Also, added features
 *  and tweaked functionality.
 *
 * 
 */
 
package gov.anl.ipns.ViewTools.Components.ThreeD;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.java.games.jogl.*;

import gov.anl.ipns.MathTools.Geometry.Vector3D;
import SSG_Tools.Viewers.*;
import SSG_Tools.Appearance.Appearance;
import SSG_Tools.Appearance.Material;
import SSG_Tools.Cameras.*;

import SSG_Tools.SSG_Nodes.*;
import SSG_Tools.SSG_Nodes.Shapes.*;
import SSG_Tools.SSG_Nodes.Shapes.Shape;
import SSG_Tools.SSG_Nodes.Groups.*;
import SSG_Tools.SSG_Nodes.Groups.Transforms.*;

import gov.anl.ipns.ViewTools.Components.IPhysicalArray3D;
import gov.anl.ipns.ViewTools.Components.IPointList3D;
import gov.anl.ipns.ViewTools.Components.IBoundsList3D;
import gov.anl.ipns.ViewTools.Components.PhysicalArray3D;

/**
 * This base class is used to draw a 3D scene consisting of detector groups.  Each
 * Detector is represented by two arrays: an array of points and an array of bounds 
 * information for each point. Every detector point will be drawn as solid boxes 
 * using the physical information stored in the bounds array.  The detectors are
 * assigned the given ids and each pixel within the a detector will assume its
 * index within the points array as its id.
 * 
 * The volume bounding box and center point are calculated as each detector is added.
 * Once all detectors are added, makeCamera can be called to create a camera that will
 * view the volume from an adjusted distance along away from the scene.  In addition,
 * addSceneCircle will encompose the volume with a 2d circle, and addLineAxes will 
 * draw coordinate axes at the center of the scene.
 */
public class DetectorSceneBase extends Group
{ 
  private float diameter = 0;
  
  protected boolean compileDisplayList = true;   
  
  protected boolean changeBackground = false; 
  private Color backgroundColor;
  
  private float[] max_point = new float[3], min_point = new float[3];
  private float[] center = new float[3];
  private float[] bbox_low = new float[3], bbox_high = new float[3];
                         

  /* --------------------------- Constructor --------------------------- */
  /**
   *  Constructor. Initalizes the base scene.  To add objects, use
   *  addDetector()
   */
  public DetectorSceneBase()
  {
    setPickID( Node.INVALID_PICK_ID );       // set a PickID for the root, for testing
           
    // Init min's
    bbox_low[0] = bbox_low[1] = bbox_low[2] = 
    min_point[0] = min_point[1] = min_point[2] = Float.POSITIVE_INFINITY;
    
    // Init max's
    bbox_high[0] = bbox_high[1] = bbox_high[2] = 
    max_point[0] = max_point[1] = max_point[2] = Float.NEGATIVE_INFINITY;
  }
  
  /**
   * This method adds a detector to the scene.  A detector is constructed by given
   * a list of points to represent the pixels and a list of bound information for
   * each point to define the shape and orientation of the pixel.  Entries in
   * point_list and bounds_list should coorrespond, e.g. a point at index 1 in point_list
   * will have its bounds information at index 1 in bounds_list.  The bounds list should
   * have equal or more entries than point_list, but excess indices in bounds_list will
   * be ignored.  The volume bounding box and center point are recalculated for each
   * detector added.
   * 
   * @param id			Detector id.
   * @param point_list  An array of 3D points. Each point represents a pixel of the
   *                    Detector.
   * @param bounds_list An array of bounds information for each point in point_list.
   *                    This defines the shape of each pixel.
   */
  public void addDetector(int id, IPointList3D point_list, IBoundsList3D bounds_list)
  {
    Group detector;    
    float[] extents, base, up, position;
            
    if(point_list != null && bounds_list != null)
    {
      detector = new DetectorGroup(id);
      	
      for ( int i = 0; i < point_list.getNumPoints(); i++ )
      {            
        // Use extents to give size of box         
        extents = bounds_list.getExtents(i).get();
        Shape box = new PixelBox( i,
        							extents[0], extents[1], extents[2] );
      
        // THIS NEEDS TO BE CHANGED TO UNIQUE ID
        box.setPickID( 2+id*100+i );
        
        // Use position and orientation to place the box in space.
        base = bounds_list.getOrientation(i)[0].get();
        up   = bounds_list.getOrientation(i)[1].get();
        position = point_list.getPoint(i).get();
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
      
      // Use critial extents to set bounding box information
      float[] minexts = bounds_list.getMinExtents().get();
      float[] maxexts = bounds_list.getMaxExtents().get();
              
      // Update bounding box
      for(int i = 0; i < 3; i++)
      {
        if(min_point[i]-minexts[i] < bbox_low[i]) bbox_low[i] = min_point[i] - minexts[i];
        if(max_point[i]+maxexts[i] > bbox_high[i]) bbox_high[i] = max_point[i] + maxexts[i];
      }
      
      // Update center
      center[0] = (min_point[0] + max_point[0])/2;
      center[1] = (min_point[1] + max_point[1])/2;
      center[2] = (min_point[2] + max_point[2])/2;
      
      // Update scene's diameter
      if(Math.abs(bbox_high[0]-bbox_low[0]) > diameter) 
          diameter = Math.abs(bbox_high[0]-bbox_low[0]);
      if(Math.abs(bbox_high[1]-bbox_low[1]) > diameter) 
          diameter = Math.abs(bbox_high[1]-bbox_low[1]);
      if(Math.abs(bbox_high[2]-bbox_low[2]) > diameter) 
          diameter = Math.abs(bbox_high[2]-bbox_low[2]);
      
    }
    else detector = new DetectorGroup(-1);

     //THIS NEEDS TO BE CHANGED TO A UNIQUE ID
    detector.setPickID(10000*id+1);
    addChild(detector);
  }
  
  /**
   * This method draws a 2d circle on the horizontal plane
   * running through the volume.  The circle should expand
   * past the diameter of the volume to encompose all of
   * the objects.
   */
  public void addSceneCircle()
  {
  	Circle circle = new Circle(diameter/1.7f, 55);
  
  	circle.setAppearance( new Appearance( new Material(Color.WHITE)) );
  	
    OrientationTransform trans = 
        new OrientationTransform(new Vector3D( 1,0,0 ), new Vector3D( 0,1,0 ),
                                 new Vector3D( center ) );
    
    trans.addChild(circle);
    
    addChild(trans);
  	
  }
  
  /**
   * This method draws lines to represent the axes of
   * the scene.  Red for x-axis, green for y-axis, and
   * blue for z-axis.
   */
  public void addLineAxes()
  {
  	OrientationTransform trans = new OrientationTransform(new Vector3D( 1,0,0 ), 
                                                          new Vector3D( 0,1,0 ),
                                                          new Vector3D( center ) );
  	
    Line xaxis = new Line( new Vector3D(0,0,0), new Vector3D(diameter/4.f, 0, 0));
  	xaxis.setAppearance( new Appearance( new Material(Color.RED)) );
    trans.addChild(xaxis);

  
  	Line zaxis = new Line( new Vector3D(0,0,0), new Vector3D(0, 0, diameter/4.f));
  	zaxis.setAppearance( new Appearance( new Material(Color.BLUE)) );
    trans.addChild(zaxis);

    
  	Line yaxis = new Line( new Vector3D(0,0,0), new Vector3D(0, diameter/4.f, 0));
  	yaxis.setAppearance( new Appearance( new Material(Color.GREEN)) );
    trans.addChild(yaxis);
    
    addChild(trans);  	
  }
  
  /**
   * This creates a camera based on the bounding box surrounding the scene.
   * It will center the view of the camera to the middle of the scene, place
   * the up position along the z axis and move the camera back along the x axis
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
    eye[0] += 1.5f*diameter;

    
    up[0] = 0;
    up[1] = 0;
    up[2] = 1;
    
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
   * This changes the background (glClearColor) to the specified
   * Color object.
   *
   *  @param background The color to change the background to.
   */
  public void setBackgroundColor(Color background)
  {
    backgroundColor = background;
    changeBackground = true;
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
    
    if(changeBackground)
    {
      gl.glClearColor(backgroundColor.getRed()/255.f, 
      				  backgroundColor.getGreen()/255.f,
					  backgroundColor.getBlue()/255.f,
					  backgroundColor.getAlpha()/255.f);
      gl.glClear(GL.GL_COLOR_BUFFER_BIT);
      changeBackground = false;
    }
    
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
    DetectorSceneBase scene = new DetectorSceneBase();
    
    for(int i = 0; i < data.length; i++)
    {
      scene.addDetector(data[i].getArrayID(), data[i], data[i]);
    }

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