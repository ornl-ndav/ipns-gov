/*
 * File: WRegion.java
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
 *  Revision 1.3  2003/11/18 01:03:29  millermi
 *  - Now implement serializable to allow saving of state.
 *
 *  Revision 1.2  2003/08/08 15:56:55  millermi
 *  - Changed filename at top from Region.java to WCRegion.java
 *  - Added method getWorldCoordPoints() for quicker access of data
 *  - Changed name of getWCP() to getWorldCoordPointAt()
 *
 *  Revision 1.1  2003/08/07 22:44:57  millermi
 *  - Initial Version - Renamed /View/Transpareny/Region.java to
 *  /View/Region/WCRegion.java
 *  - Now takes a generic number of points for specifying the region.
 *
 ***************************Region.java Log Messages*************************
 *  Revision 1.1  2003/07/25 14:41:03  dennis
 *  - Initial version - Separated this private class from Selection Overlay,
 *    now an independent public class to be used by the Selection Overlay
 *    and any components that use the Selection Overlay. (Mike Miller)
 *
 */ 
package DataSetTools.components.View.Region;

import DataSetTools.util.floatPoint2D; 

/**
 * This class groups together the selected region and the bounding world 
 * coordinate points of this region. The selected region type is a string that
 * is specified in the SelectionJPanel.java. Use those static variables to
 * specify this type. This class is also used to pass region information from
 * the Selection Overlay to the view component. The Region class in the Region
 * directory is used to pass region info from the view component to the viewer. 
 */ 
public class WCRegion implements java.io.Serializable
{
   private String region;
   private floatPoint2D[] wcp;
   
  /**
   * Constructor
   *
   *  @param  regiontype
   *  @param  points
   */ 
   public WCRegion( String regiontype, floatPoint2D[] points )
   {
     region = regiontype;
     wcp = points;
   }
   
  /**
   * This will return the region type as listed in the SelectionJPanel.java.
   *
   *  @return region - String describing the region
   */ 
   public String getRegionType()
   {
     return region;
   }
   
  /**
   * Get the world coordinate point at the index specified. The index of the 
   * first element is 0. If the index exceeds the size of the array, the last
   * element will be returned.
   *
   *  @param  index
   *  @return wcp - floatPoint2D point at index
   */
   public floatPoint2D getWorldCoordPointAt(int index)
   {
     // if index out of bounds (negative)
     if( index < 0 )
       index = 0;
     // if index out of bounds (too big)
     else if( index >= getNumWCP() )
       index = getNumWCP() - 1;
     return wcp[index];
   }  
   
  /**
   * Get the entire array of world coordinate points.
   *
   *  @return wcp - floatPoint2D array
   */
   public floatPoint2D[] getWorldCoordPoints()
   {
     return wcp;
   }
   
  /**
   * Get the number of world coordinate points used to specify the region.
   *
   *  @return number of world coordinate points
   */ 
   public int getNumWCP()
   {
     return wcp.length;
   }
}
