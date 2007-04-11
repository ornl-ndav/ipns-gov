/* 
 * File: RegionOp.java
 *  
 * Copyright (C) 2007  Johnathan Morck, Chad Diller
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797.
 *
 * Modified:
 * $Log$
 * Revision 1.1  2007/04/11 21:51:10  dennis
 * New class for dealing with unions, intersections and complements
 * of selected regions.
 *
 */
package gov.anl.ipns.ViewTools.Components.Region;


/**
 * Class RegionOp - Holds a Region and a corresponding operation for that 
 *                  region.
 */
public class RegionOp {
 
 /**
  * Enum holds values corresponding to Add, Intersect, Subtract, Reverse
  */
  public static enum Operation { UNION, 
                                 INTERSECT, 
                                 INTERSECT_COMPLEMENT, 
                                 COMPLEMENT  }
 
  private Operation op;
  private Region region;
 
 /**
  * Constructs RegionOp object that holds a region and an operation
  *
  * @param region Region of world coordinate points
  * @param op     Operation corresponding to region
  */
  public RegionOp(Region region, Operation op){
    this.op     = op;
    this.region = region;
  }
 

 /**
  * Get the Region object for this RegionOp.  If the operation is COMPLEMENT,
  * the Region object should normally be null.
  *
  * @return Region
  */
  public Region getRegion(){
    return region;
  }

 
 /**
  * Get the Operation for this RegionOp
  * 
  * @return Operation
  */
  public Operation getOp(){
    return op;
  }

 
 /**
  * Get a String representation for this object in the form 
  * "[operation] [region]"
  *
  * @return a String giving the Operation and region for this object.
  */
  public String toString(){
    return(op + " " + region);
  }
 
}
