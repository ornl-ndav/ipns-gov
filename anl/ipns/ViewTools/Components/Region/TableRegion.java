/*
 * File: TableRegion.java
 *
 * Copyright (C) 2004, Mike Miller
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
 *  Revision 1.5  2007/03/16 16:53:03  dennis
 *  Removed initializeSelectedPoints() method that was no longer needed.
 *  Adapted to newly revised Region class.
 *
 *  Revision 1.4  2004/05/20 20:48:27  millermi
 *  - Constructor now initializes world and image bounds to
 *    the bounds of the defining points.
 *
 *  Revision 1.3  2004/05/20 17:02:27  millermi
 *  - Made method getRegionBounds() public so it may be used by
 *    outside classes.
 *
 *  Revision 1.2  2004/05/11 01:25:31  millermi
 *  - Changed javadoc comments.
 *  - Removed unused variables.
 *
 *  Revision 1.1  2004/05/09 16:59:56  millermi
 *  - Initial Version - This region provides support for regions
 *    selected using a table.
 *
 */ 
package gov.anl.ipns.ViewTools.Components.Region;

import java.awt.Point;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;

/**
 * This class is a specific region designated by two points. A TableRegion is
 * used to pass a rectangular region of row/column cells from the view
 * component to the viewer. Given the defining points of a region,
 * this class can return all of the points (cells) inside the selected region. 
 */ 
public class TableRegion extends Region
{
  private boolean selected = false;

 /**
  * Constructor - provides basic initialization for all subclasses
  *
  *  @param  dp - defining points of the box
  */ 
  public TableRegion( floatPoint2D[] dp, boolean isSelected )
  {
    super(dp);
    selected = isSelected;
  }
  
  
 /**
  * Set whether the points in this region are selected or not selected.
  *
  *  @param  isSelected
  */
  public void setSelected( boolean isSelected )
  {
    selected = isSelected;
  }
  

 /**
  * Use this to determine whether this region is selected or deselected.
  *
  *  @return True if the region is selected, false otherwise.
  */
  public boolean isSelected()
  {
    return selected;
  }

  
 /**
  * Get the selected points for this table region using (col,row) coordinates.
  * The world_to_array transformation is taken to be the identity for 
  * TableRegions, so the parameter is ignored.
  *
  *  @param  world_to_array  Transform from world coordinates to array
  *                          coordinates must be the identity transform
  *                          for TableRegion.  This parameter is ignored.
  *
  *  @return array of points included within the region.
  */
  public Point[] getSelectedPoints( CoordTransform world_to_array )
  { 
    Point topleft = definingpoints[0].toPoint();
    Point bottomright = definingpoints[1].toPoint();
    int w = bottomright.x - topleft.x + 1;
    int h = bottomright.y - topleft.y + 1;
    Point[] selectedpoints = new Point[w*h];
    int index = 0;
    // Set through box rowwise, getting points that are on the image.
    for( int row = topleft.x; row <= bottomright.x; row++ )
    {
      for( int col = topleft.y; col <= bottomright.y; col++ )
      {
        selectedpoints[index] = new Point(row,col);
	index++;
      }
    }
    return selectedpoints;
  }

  
 /**
  * Display the region type with its defining points.
  *
  *  @return region type and defining points.
  */
  public String toString()
  {
    return ("Region: Table\n" +
            "Top-left Corner: " + definingpoints[0] + "\n" +
	    "Bottom-right Corner: " + definingpoints[1] + "\n" +
	    "Is Selected: " + selected + "\n");
  }


 /**
  *  Get a bounding box for the region, in World Coordinates.  The
  *  points of the region will lie in the X-interval [X1,X2] and
  *  in the Y-interval [Y1,Y2], where X1,X2,Y1 and Y2 are the values
  *  returned by the CoordBounds.getX1(), getX2(), getY1(), getY2()
  *  methods.
  * 
  *  @return a CoordBounds object containing the full extent of this
  *          region.
  */
 public CoordBounds getRegionBoundsWC()
 {
    return new CoordBounds( definingpoints[0].x,
                            definingpoints[0].y, 
                            definingpoints[1].x,
			    definingpoints[1].y );
 }

   
}
