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
    
    // Give the image and world bounds meaningful values.
    setWorldBounds( new CoordBounds( definingpoints[0].x,
                                     definingpoints[0].y, 
                                     definingpoints[1].x,
			             definingpoints[1].y ) );
    setImageBounds( new CoordBounds( definingpoints[0].x,
                                     definingpoints[0].y, 
                                     definingpoints[1].x,
			             definingpoints[1].y ) );
  }
  
 /**
  * Get all of the points inside the region. This method assumes
  * that the input points are in (x,y) where (x = col, y = row ) form.
  * The points are entered into the array row by row.
  *
  *  @return array of points included within the region.
  */
  public Point[] getSelectedPoints()
  {
    return initializeSelectedPoints();
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
  * This method is here to factor out the setting of the selected points.
  * By doing this, regions can make use of the getRegionUnion() method.
  *
  *  @return array of points included within the region.
  */
  protected Point[] initializeSelectedPoints()
  { 
    Point topleft = definingpoints[0].toPoint();
    Point bottomright = definingpoints[1].toPoint();
    int w = bottomright.x - topleft.x + 1;
    int h = bottomright.y - topleft.y + 1;
    selectedpoints = new Point[w*h];
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
  * This method returns the rectangle of table cells.
  *
  *  @return The bounds of the TableRegion.
  */
  public CoordBounds getRegionBounds()
  {
    return new CoordBounds( definingpoints[0].x,
                            definingpoints[0].y, 
                            definingpoints[1].x,
			    definingpoints[1].y );
  }
}
