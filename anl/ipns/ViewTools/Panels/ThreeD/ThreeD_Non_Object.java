/*
 * File:  ThreeD_Non_Object.java
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.4  2002/11/27 23:12:53  pfpeterson
 * standardized header
 *
 * Revision 1.3  2002/10/29 23:46:02  dennis
 * Added method position().
 *
 */

package DataSetTools.components.ThreeD;

import java.awt.*;
import DataSetTools.components.image.*;
import DataSetTools.math.*;

/** 
 *  An object of this class can serve as a "place holder" in a list of 
 *  IThreeD_Objects.  It does nothing and is invisible. 
 */

public class ThreeD_Non_Object implements IThreeD_Object
{
  /**
   *  This has no effect. 
   */
  public void setColor( Color color )
  {
  }


  /**
   *  This returns null. 
   */
  public Vector3D position()
  {
    return null;
  }


  /**
   *  This has no effect. 
   */
  public void Project( Tran3D         projection,   
                       CoordTransform window_tran,
                       float          front_clip  )
  {
  }


  /**
   *  This has no effect. 
   */
  public void Transform( Tran3D transform )
  {
  }


  /**
   *  This has no effect. 
   */
  public void Draw( Graphics g )
  {
  }


  /**
   *  This has no effect. 
   *
   *  @return  Float.MAX_VALUE; 
   */
  public float depth( )
  {
    return Float.MAX_VALUE;
  }


  /**
   *  This has no effect. 
   *
   *  @return  Returns Float.MAX_VALUE 
   */
  public float distance_to( float pix_x, float pix_y )
  {
    return Float.MAX_VALUE;
  }


  /**
   *  This has no effect. 
   */
  public void setPickID( int pick_id )
  {
  }


  /**
   *  This has no effect. 
   *
   *  @return  Returns INVALID_PICK_ID.
   */
  public int getPickID( )
  {
    return IThreeD_Object.INVALID_PICK_ID;
  }

}
