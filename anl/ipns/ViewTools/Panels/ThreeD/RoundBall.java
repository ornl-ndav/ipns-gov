/* 
 * File: RoundBall.java
 *
 * Copyright (C) 2009, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author$:
 *  $Date$:            
 *  $Rev$:
 */

package gov.anl.ipns.ViewTools.Panels.ThreeD;

import java.awt.Color;
import java.awt.Graphics;

import gov.anl.ipns.ViewTools.Panels.ThreeD.ThreeD_Object;
import gov.anl.ipns.MathTools.Geometry.Vector3D;


/**
 * This class represents a flat circle with one color and outer rings are more
 * white.
 */

public class RoundBall extends ThreeD_Object
{



   float radius;


   /**
    * Constructor
    * 
    * @param center
    *           center of the ball
    * @param radius
    *           radius(pixels) of the ball
    * @param color
    *           color of the ball
    */
   public RoundBall( Vector3D center, float radius, Color color )
   {

      super( new Vector3D[]
      {
         center
      } , color );
      
      this.radius = radius;
   }


   @Override
   public void Draw( Graphics g )
   {

      if( clipped )
         return;

      g.setColor( color );
      int red = color.getRed();
      int green = color.getGreen();
      int blue = color.getBlue();
      int alpha = color.getAlpha();
      
      Color CC = new Color( red , green , blue , alpha );
      
      int Wred = Color.white.getRed();
      int Wgreen = Color.white.getGreen();
      int Wblue = Color.white.getBlue();
      int Walpha = Color.white.getAlpha();
      
      int R = (int) ( radius + .5 );
      
      if( R <= 2 )
         R = 1;
      else
         R = R - 2;

      int xx = (int) x[ 0 ];
      int yy = (int) y[ 0 ];
      
      g.fillOval( xx , yy , R , R );
      g.drawOval( xx , yy , R , R );
      
      CC = new Color( (int) ( .7 * red + .3 * Wred ) ,
               (int) ( .7 * green + .3 * Wgreen ) ,
               (int) ( .7 * blue + .3 * Wblue ) , alpha );
      
      g.setColor( CC );
      
      if( R + 1 < radius )
         g.drawOval( xx , yy , R + 1 , R + 1 );

      CC = new Color( (int) ( .4 * red + .6 * Wred ) ,
               (int) ( .4 * green + .6 * Wgreen ) ,
               (int) ( .4 * blue + .6 * Wblue ) , alpha );
      
      g.setColor( CC );
      
      if( R + 2 < radius )
         g.drawOval( xx , yy , R + 2 , R + 2 );
   }


   
}
