/*
 * File: Region.java
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
 *  Revision 1.1  2003/07/25 14:41:03  dennis
 *  - Initial version - Separated this private class from Selection Overlay,
 *    now an independent public class to be used by the Selection Overlay
 *    and any components that use the Selection Overlay. (Mike Miller)
 *
 */ 
package DataSetTools.components.View.Transparency;

import DataSetTools.util.floatPoint2D; 

/**
 * This class groups together the selected region and the bounding world 
 * coordinate points of this region. If the region is a point, wcp2 = null.
 */ 
public class Region
{
   private Object region;
   private floatPoint2D wcp1;	   
   private floatPoint2D wcp2;
   
   public Region( Object o, floatPoint2D p1, floatPoint2D p2 )
   {
      region = o;
      wcp1 = p1;
      wcp2 = p2;
   }
   
   public Object getRegion()
   {
      return region;
   }
   
   public floatPoint2D getWCP1()
   {
      return wcp1;
   }  
       
   public floatPoint2D getWCP2()
   {
      return wcp2;
   }
}
