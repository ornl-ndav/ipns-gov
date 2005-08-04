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
 * This work was supported by the University of Tennessee Knoxville and 
 * the Spallation Neutron Source at Oak Ridge National Laboratory under: 
 *   Support of HFIR/SNS Analysis Software Development 
 *   UT-Battelle contract #:   4000036212
 *   Date:   Oct. 1, 2004 - Sept. 30, 2006
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.6  2005/08/04 22:34:57  cjones
 *  Now contains methods for add, removing, and returning selected pixels using
 *  the SelectedListItem object. Also, fixed some documentation.
 *
 *  Revision 1.5  2005/07/29 20:48:21  cjones
 *  The base DetectorScene now contains methods for selecting and unselecting
 *  pixels. Selected pixels can then be set to a given color.
 *
 *  Revision 1.4  2005/07/27 20:36:33  cjones
 *  Added menu item that allows the user to choose between different shapes
 *  for the pixels. Also, in frames view, user can change the time between
 *  frame steps.
 *
 *  Revision 1.3  2005/07/25 21:27:54  cjones
 *  Added support for MouseArcBall and a control checkbox to toggle it. Also,
 *  the value of the selected pixel is now displayed with the Pixel Info, and
 *  updates on frame changes.
 *
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
import java.util.Vector;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Iterator;

import javax.swing.*;

import net.java.games.jogl.*;

import gov.anl.ipns.MathTools.Geometry.Vector3D;
import SSG_Tools.Viewers.*;
import SSG_Tools.Appearance.Appearance;
import SSG_Tools.Appearance.Material;
import SSG_Tools.Cameras.*;

import SSG_Tools.SSG_Nodes.*;
import SSG_Tools.SSG_Nodes.Shapes.*;
import SSG_Tools.SSG_Nodes.SimpleShapes.*;
import SSG_Tools.SSG_Nodes.Groups.*;
import SSG_Tools.SSG_Nodes.Groups.Transforms.*;

import gov.anl.ipns.Util.Numeric.UniqueIntGenerator;
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
 * The volume bounding box is calculated as each detector is added, and the center
 * is assumed to be at the origin, <0,0,0>.
 * Once all detectors are added, makeCamera can be called to create a camera that will
 * view the volume from an adjusted distance along away from the scene.  In addition,
 * addSceneCircle will encompose the volume with a 2d circle, and addLineAxes will 
 * draw coordinate axes at the center of the scene.
 * 
 * The object also stores a collection of currently selected pixels.
 * The user may add/remove pixels from the set to specify whether they have been
 * selected or not.  The selected pixels can then be colored with a given color.
 */
public class DetectorSceneBase extends Group
{ 
  /**
   * This represents the 3D box pixel shape.
   */
  public static int BOX = 1;
  
  /**
   * This represents the 2D rectangle pixel shape.
   */
  public static int RECTANGLE = 2;
  
  /**
   * This represents the 1D dot pixel shape.
   */
  public static int DOT = 3;
  
  protected boolean sceneCircleOn = false;
  protected boolean axisLinesOn = false;
	
  private float diameter = 0;
  private float circle_radius = 0;
  
  protected boolean compileDisplayList = true;   
  
  private boolean changeBackground = false; 
  private Color backgroundColor;
  private Group scene_circle;
  private Group scene_axes;
  
  private TreeMap detectorid_map;
  private TreeSet selected_pixels; 
  
  private float[] max_point = new float[3], min_point = new float[3];
  private float[] center = {0.f, 0.f, 0.f};
  private float[] bbox_low = new float[3], bbox_high = new float[3];
                         

  /* --------------------------- Constructor --------------------------- */
  /**
   *  Constructor. Initalizes the base scene.  To add objects, use
   *  addDetector(). The center is assumed to be at the origin.
   */
  public DetectorSceneBase()
  {
    setPickID( Node.INVALID_PICK_ID );       // set a PickID for the root, for testing
    
    detectorid_map = new TreeMap();
    selected_pixels = new TreeSet(); 
           
    // Init min's
    bbox_low[0] = bbox_low[1] = bbox_low[2] = 
    min_point[0] = min_point[1] = min_point[2] = Float.POSITIVE_INFINITY;
    
    // Init max's
    bbox_high[0] = bbox_high[1] = bbox_high[2] = 
    max_point[0] = max_point[1] = max_point[2] = Float.NEGATIVE_INFINITY;
  }
  
  /**
   * This method adds a detector to the scene.  A detector is constructed by giving
   * a list of points to represent the pixels and a list of bound information for
   * each point to define the shape and orientation of the pixel.  Entries in
   * point_list and bounds_list should coorrespond, e.g. a point at index 1 in point_list
   * will have its bounds information at index 1 in bounds_list.  The bounds list should
   * have equal the number of entries as point_list, and excess indices in bounds_list will
   * be ignored.  The volume bounding box is recalculated for each
   * detector added.  The center is assumed to be at the origin.
   * 
   * @param id			Detector id.
   * @param shapeType  The type of shape that each pixel will be drawn as. 
   *                    Default is BOX.
   * @param point_list  An array of 3D points. Each point represents a pixel of the
   *                    Detector.
   * @param bounds_list An array of bounds information for each point in point_list.
   *                    This defines the shape of each pixel.
   */
  public void addDetector(int id, int shapeType,
  		                  IPointList3D point_list, IBoundsList3D bounds_list)
  {
    Group detector;    
    float[] extents, base, up, position;
    SimpleShape shape;
            
    if(point_list != null && bounds_list != null)
    {
      detector = new DetectorGroup(id);
      	
      // Got through each 
      for ( int i = 0; i < point_list.getNumPoints(); i++ )
      {                                      
        if(shapeType == DOT)
        {
          // The DOT will take the size of the X dimension extent.
          extents = bounds_list.getExtents(i).get();
          Vector3D[] pts = new Vector3D[1];
          pts[0] = point_list.getPoint(i);
          
          shape = new PixelPolymarker(i, pts, Color.WHITE);
          ((PixelPolymarker)shape).setSize((int)extents[0]);
          ((PixelPolymarker)shape).setType(PixelPolymarker.DOT);
        }
        
        else if(shapeType == RECTANGLE)
        {
          // The rectangle will take the width of the x dimension
          // extent and the height of the y dimension extent.
          extents = bounds_list.getExtents(i).get();
          
          // Width - X Vector
          Vector3D width = bounds_list.getOrientation(i)[0];
          width.normalize();           
          width.multiply(extents[0]);
          
          // Height - Y Vector
          Vector3D height = bounds_list.getOrientation(i)[1];
          height.normalize();
          height.multiply(extents[1]);
           
          shape = new PixelParallelogram(i, point_list.getPoint(i),
                    width, height,
    				Color.WHITE);
        }
        
        else
        {
          // Default Shape: Box
          // All extents, orientations use.
          shape = new PixelPositionedBox(i, point_list.getPoint(i),
        		                                bounds_list.getOrientation(i)[0],
        		                                bounds_list.getOrientation(i)[1],
                                                bounds_list.getExtents(i),
												Color.WHITE);
        }

        // Use max point to find center and bounding box
        position = point_list.getPoint(i).get();
        for(int j = 0; j < 3; j++) 
        {
          if(position[j] > max_point[j]) max_point[j] = position[j];
          if(position[j] < min_point[j]) min_point[j] = position[j];
        }
                  
        // Generate pick id that is unique
        shape.setPickID( UniqueIntGenerator.getNextInt() );
        
        detector.addChild( shape );
        
        // Map user detector id to the detector object
        detectorid_map.put(new Integer(id), detector);
        
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
      /*
      center[0] = (min_point[0] + max_point[0])/2;
      center[1] = (min_point[1] + max_point[1])/2;
      center[2] = (min_point[2] + max_point[2])/2;
      */
      
      /// Update the radius of the scene circle
      Vector3D plane_high = new Vector3D(bbox_high[0], bbox_high[1], 0);
      circle_radius = plane_high.distance(new Vector3D(bbox_low[0], bbox_low[1], 0))/2.0f;
      
      // Update scene's diameter
      Vector3D high_point = new Vector3D(bbox_high);
      diameter = high_point.distance(new Vector3D(bbox_low));
      
    }
    else detector = new DetectorGroup(-1);

     //THIS NEEDS TO BE CHANGED TO A UNIQUE ID
    detector.setPickID(  UniqueIntGenerator.getNextInt() );
    addChild(detector);
    
    compileDisplayList = true;
  }
  
  /**
   * This method draws a 2d circle on the horizontal plane
   * running through the volume.  The circle should expand
   * past the diameter of the volume to encompose all of
   * the objects.
   */
  public void addSceneCircle()
  {
  	scene_circle = new Group();
  	
  	Circle circle = new Circle(circle_radius, 55);
  
  	circle.setAppearance( new Appearance( new Material(Color.WHITE)) );
  	
    OrientationTransform trans = 
        new OrientationTransform(new Vector3D( 1,0,0 ), new Vector3D( 0,1,0 ),
                                 new Vector3D( center ) );
    
    trans.addChild(circle);
    
    scene_circle.addChild(trans);
    
    addChild(scene_circle);
    
    compileDisplayList = true;
    sceneCircleOn = true;
  }
  
  /**
   * Removes the circle guide from the scene.
   */
  public void removeSceneCircle()
  {
    removeChild(scene_circle); 
    
    compileDisplayList = true;
    sceneCircleOn = false;
  }
  
  /**
   * This method draws lines to represent the axes of
   * the scene.  Red for x-axis, green for y-axis, and
   * blue for z-axis.
   */
  public void addLineAxes()
  {
  	scene_axes = new Group();
  	
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
    
    scene_axes.addChild(trans);  	
    
    addChild(scene_axes);
    
    compileDisplayList = true;
    axisLinesOn = true;
  }
  
  /**
   * Removes the line axis guide from the scene.
   */
  public void removeLineAxes()
  {
    removeChild(scene_axes); 
    
    compileDisplayList = true;
    axisLinesOn = false;
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
  
  /*-------------- SELECTION METHODS ------------------------*/
  
  /**
   * Return all of the selected pixels in an array of
   * strings.  Each string is formatted as follows: 
   *   "Det <detector_user_id> Pix <pixel_user_id>"
   * where detector_user_id is an integer representing
   * the id given to the detector when it was added and
   * the pixel_user_id is the index to the pixel within
   * the detector. 
   * 
   * Each selected pixel has an individual string.
   *
   * @return An array of formatted strings, one
   *         for each selected pixel.
   */
  public String[] getFormattedSelectedIDs()
  {
  	if(selected_pixels.size() <= 0) return null;
  	
  	// One string for each pixel
  	String[] output = new String[selected_pixels.size()];
  	
  	int i =0;
    for(Iterator it = selected_pixels.iterator(); it.hasNext(); i++)
    {
      // Use pickid to find PixelID and DetectorID
      int pickid = ((Integer)it.next()).intValue();
      SimpleShape pixel = (SimpleShape)Node.getNodeWithID(pickid);
      DetectorGroup det = (DetectorGroup)pixel.getParent();
      int pixelid = ((IPixelShape)pixel).getPixelID();
      int detid = det.getDetectorID();
     
      // Construct its String
      output[i] = new String("Det " + detid + " Pix " + pixelid);
    }
    
    return output;
  }
  
  /**
   * Return all of the selected pixels as a Vector of
   * SelectListItem objects.  Each SelectedListItem
   * is grouped by a single detector and all selected
   * pixels for that detector.
   *
   * @return A Vector SelectedListItem objects
   *         representing all selected pixels.
   */
  public Vector getSelectedItems()
  {  	
  	Vector output = new Vector();
  	
  	// Since the PickIDs are in increasing order, all of the pixels
  	// for any given detector should be grouped together.  Worst case
  	// one detector is split if the UniqueIDs wrap around to the 
  	// beginning
  	
  	int i = 0;
  	int cur_det = -1;  // Current detector id
  	TreeSet pixelids = new TreeSet(); // To hold and sort Pixel IDs
    for(Iterator it = selected_pixels.iterator(); it.hasNext(); i++)
    {
      // Find PixelID and DetectorID for selected pickid
      int pickid = ((Integer)it.next()).intValue();
      SimpleShape pixel = (SimpleShape)Node.getNodeWithID(pickid);
      DetectorGroup det = (DetectorGroup)pixel.getParent();
      int pixelid = ((IPixelShape)pixel).getPixelID();
      int detid = det.getDetectorID();
      
      // If first iteration, set current detector id and
      // add the pixel id to its collection.
      if(i == 0)
      {
      	pixelids.add(new Integer(pixelid));
      	cur_det = detid;
      }
      // If this detector is same as last detector, then add pixel id
      else if(detid == cur_det)
      {
        //Add pixel id to list of selected pixels for this detector.
      	pixelids.add(new Integer(pixelid));
      }
      // If detector is new, store last detector into SelectedListItem
      else
      {
        //Add last detector with selected pixels and go to next.
      	// Get all pixel ids in increasing order
      	Object[] tempids = pixelids.toArray();
      	int[] pix_array = new int[tempids.length];
      	for(int j = 0; j < tempids.length; j++)
      	  pix_array[j] = ((Integer)tempids[j]).intValue();
      	
      	int[] det_array  = { cur_det };
      	// Generate SelectedListItem for that detector
      	output.add( new SelectedListItem(det_array, pix_array));
      	
      	// Start the new detector
      	pixelids.clear();
      	pixelids.add(new Integer(pixelid));
      	cur_det = detid;
      }
    }
    
    // If iterations stop and there is a detector to be transformed
    // into a SelectedListItem
    if(i != 0)
    {
      // Get all pixel ids in increasing order
      Object[] tempids = pixelids.toArray();
      int[] pix_array = new int[tempids.length];
      for(int j = 0; j < tempids.length; j++)
        pix_array[j] = ((Integer)tempids[j]).intValue();
    	
      int[] det_array  = { cur_det };
      // Generate SelectedListItem for that detector.
      output.add( new SelectedListItem(det_array, pix_array));
    }
    
    return output;
  }
  
  
 /**
  * This will intersect the set of given selected pixels with the
  * current selected pixels.  The lists of ids must match in 
  * length, where every index represents corresponding ids within
  * the two arrays. Thus, for index i, retain pixel at i that is
  * in detector at i.
  * 
  * @param det_ids    The detector ids as given when the detector
  *                        was added.
  * @param pixel_ids  The pixel ids within the detector id.
  */
  public void retainSelectedPixels(int[] det_ids, 
  		                           int[] pixel_ids)
  {
  	if(det_ids.length != pixel_ids.length)
      return;
  	
  	TreeSet tmpset = new TreeSet();
  	
  	for(int i = 0; i < det_ids.length; i++)
  	{
  	  // Map detector id to detector scene node
  	  DetectorGroup det = (DetectorGroup)
	        detectorid_map.get(new Integer(det_ids[i]));
  	  
  	  // If node exists, get child at pixel id 
  	  if(det != null && det.getDetectorID() == det_ids[i] && 
  	  	 pixel_ids[i] < det.numChildren())
  	    tmpset.add(new Integer(det.getChild(pixel_ids[i]).getPickID()));
  	}
  	
  	selected_pixels.retainAll(tmpset);
  }
  
  /**
   * This will intersect the set of given selected pixels with the
   * current selected pixels.  
   * 
   * @param items An array of SelectedListItem that contains 
   *              detector and pixels ids that should be retained.
   */
   public void retainSelectedPixels(SelectedListItem[] items)
   {   	
   	TreeSet tmpset = new TreeSet();
   	
   	for(int i = 0; i < items.length; i++)
   	{
   	  // The detectors for which all of pixels are
   	  // selected
   	  int[] dets = items[i].getDetectorArray();
   	  int[] pixs = items[i].getPixelArray();
   	  
      if(dets != null && pixs != null)
   	    for(int d_i = 0; d_i < dets.length; d_i++)
   	    {
   	      // Get detector scene node
   	      DetectorGroup det = (DetectorGroup)
 	            detectorid_map.get(new Integer(dets[d_i]));
   	  
   	      // If it's not there, move on to next detector
   	      if(det == null || det.getDetectorID() != dets[d_i]) break;
   	    
   	      // If it is there, add all the pixel ids to retainable set
   	      for(int p_i = 0; p_i < pixs.length; p_i++)
   	        if(pixs[p_i] < det.numChildren())
   	          tmpset.add(new Integer(det.getChild(pixs[p_i]).getPickID()));
   	    }
   	}
   	
   	selected_pixels.retainAll(tmpset);
   }
  
 /**
  * This method adds the pixels contained within the given item.
  * For each detector id, all of the pixel ids will be added 
  * to the set of selected pixels.
  * 
  * @param item The item containing detectors with pixel ids
  *             that will be added to selection.
  */
  public void addSelectedPixel(SelectedListItem item)
  {
  	int[] det_array = item.getDetectorArray();
  	int[] pix_array = item.getPixelArray();
  	
  	// This makes sure the SelectedListItems were set
  	if(det_array == null || pix_array == null)
  	  return;
  	
  	for(int i = 0; i < det_array.length; i++)
  	{
  	  // get detector scene object
   	  DetectorGroup det = 
   		  (DetectorGroup)detectorid_map.get(new Integer(det_array[i]));
    	
   	  // Select all the specified pixels
  	  if(det != null && det.getDetectorID() == det_array[i])
  	    for(int j = 0; j < pix_array.length; j++)
  	      if(pix_array[j] < det.numChildren())
  	        addSelectedPixel(det.getChild(pix_array[j]).getPickID());
  	}
  }
   
 /**
  * This method adds the pixel found within the specified ids
  * to the set of selected pixels.  The first id represents
  * the id number given to the detector when it was added (ie,
  * the user id) and the second id represents the pixels index
  * within that detector.
  * 
  * @param detid   The detector id number.
  * @param pixelid The pixel id number within the detector.
  */
  public void addSelectedPixel(int detid, int pixelid)
  {
  	DetectorGroup det = 
  		(DetectorGroup)detectorid_map.get(new Integer(detid));
  	
  	if(det != null && det.getDetectorID() == detid && 
  	   pixelid < det.numChildren())
  	  addSelectedPixel(det.getChild(pixelid).getPickID());
  }
  
 /**
  * This method removes the pixels contained within the given item.
  * For each detector id, all of the pixel ids will be removed 
  * from the set of selected pixels.
  * 
  * @param item The item containing detectors with pixel ids
  *             that will be removed from selection.
  */
  public void removeSelectedPixel(SelectedListItem item)
  {
  	int[] det_array = item.getDetectorArray();
  	int[] pix_array = item.getPixelArray();
  	
  	if(det_array == null || pix_array == null)
      return;
  	
  	for(int i = 0; i < det_array.length; i++)
  	{
   	  DetectorGroup det = 
   		  (DetectorGroup)detectorid_map.get(new Integer(det_array[i]));
    	
  	  if(det != null && det.getDetectorID() == det_array[i])
  	    for(int j = 0; j < pix_array.length; j++)
  	      if(pix_array[j] < det.numChildren())
  	        removeSelectedPixel(det.getChild(pix_array[j]).getPickID());
  	}
  }
  
 /**
  * This method removes the pixel found within the specified ids
  * from the set of selected pixels.  The first id represents
  * the id number given to the detector when it was added (ie,
  * the user id) and the second id represents the pixels index
  * within that detector.
  * 
  * @param detid   The detector id number.
  * @param pixelid The pixel id number within the detector.
  */
  public void removeSelectedPixel(int detid, int pixelid)
  {
  	DetectorGroup det = 
  		(DetectorGroup)detectorid_map.get(new Integer(detid));
  	
  	if(det != null && det.getDetectorID() == detid && 
  	   pixelid < det.numChildren())
  	  removeSelectedPixel(det.getChild(pixelid).getPickID());
  }
 
 /**
  * This method adds the pixel node with the given
  * PickID to the set of selected pixels.  The PickID
  * is the unique identification number within the scene
  * graph.
  * 
  * @param pickid Unique identification number of the
  *               Pixel shape.
  */
  public void addSelectedPixel(int pickid)
  {
  	if(pickid == -1)
  		return;
  	
    selected_pixels.add(new Integer(pickid));
  }
  
 /**
  * This method removes the pixel node with the given
  * PickID from the set of selected pixels.  The PickID
  * is the unique identification number within the scene
  * graph.
  * 
  * @param pickid Unique identification number of the
  *               Pixel shape.
  */
  public void removeSelectedPixel(int pickid)
  {
  	if(pickid == -1)
  		return;
  	
    selected_pixels.remove(new Integer(pickid));
  }
  
 /**
  * This method adds the all pixel nodes contained in the
  * detector group with the given PickID to the set of selected 
  * pixels.  The PickID is the unique identification number within 
  * the scene graph.
  * 
  * @param pickid Unique identification number of the
  *               detector group.
  */
  public void addSelectedDetector(int pickid)
  { 	
  	DetectorGroup det = (DetectorGroup)Node.getNodeWithID(pickid);
  	
  	if(det == null)
  		return;
  	
  	for(int i = 0; i < det.numChildren(); i ++)
      selected_pixels.add(new Integer(det.getChild(i).getPickID()));
  }
  
 /**
  * This method removes the all pixel nodes contained in the
  * detector group with the given PickID from the set of selected 
  * pixels.  The PickID is the unique identification number within 
  * the scene graph.
  * 
  * @param pickid Unique identification number of the
  *               detector group.
  */
  public void removeSelectedDetector(int pickid)
  { 	
  	DetectorGroup det = (DetectorGroup)Node.getNodeWithID(pickid);
  	
  	if(det == null)
  		return;
  	
  	for(int i = 0; i < det.numChildren(); i ++)
      selected_pixels.remove(new Integer(det.getChild(i).getPickID()));
  }
  
  /**
   * This method unselects all pixels.
   */
  public void clearSelected() { selected_pixels.clear(); }
  
 /**
  * This method colors all the selected pixels with the given 
  * color.  The change will not be seen until the next call
  * to the Draw method for the jogl panel.
  * 
  * @param color The color to apply to selected pixels.
  */
  public void colorSelected(Color color)
  {
  	SimpleShape pixel;
  	int pickid = -1;
  	
    for(Iterator it = selected_pixels.iterator(); it.hasNext(); )
    {
      pickid = ((Integer)it.next()).intValue();
      pixel = (SimpleShape)Node.getNodeWithID(pickid);
      if(pixel != null) pixel.setColor(color);
    }
    
    compileDisplayList = true;
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
      scene.addDetector(BOX, data[i].getArrayID(), data[i], data[i]);
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