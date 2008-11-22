/**
 * File: TestDrawing.java
 *
 * Copyright (C) 2008, Dennis Mikkelson
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
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */

package gov.anl.ipns.ViewTools.Panels.TwoD;

import javax.swing.JFrame;
import javax.imageio.ImageIO;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * This class demonstrates the use of a TwoD_JPanel and some basic IDrawable
 * objects.
 * 
 * @author Dennis Mikkelson
 */
public class TestDrawing
{
  public static void main( String args[] )
  {                                               // make a frame
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(new Dimension(700,500));
                                                  // make a TwoD_JPanel to use
    TwoD_JPanel panel = new TwoD_JPanel();        // for displaying IDrawables

                                                  // make a couple of boxes
                                                  // of different colors, one
                                                  // of which we'll rotate
    Point rotate_point = new Point( 400, 200 );
    OvalDrawable oval1 = new OvalDrawable( 300, 10 );
    oval1.setPosition( rotate_point );
    oval1.setColor( Color.green );
    
    RectangleDrawable box2 = new RectangleDrawable ( 300, 10 );
    box2.setPosition( rotate_point );
    box2.setColor( Color.RED );
    box2.setRotationAngle( 30 );
                                                  // make a couple of default
                                                  // colored boxes at point
                                                  // (100,100) where we draw
                                                  // the text.
    Point text_point = new Point(100, 100);
    RectangleDrawable box3 = new RectangleDrawable( 50, 1 );
    RectangleDrawable box4 = new RectangleDrawable( 1, 50 );
    box3.setPosition( text_point );
    box4.setPosition( text_point );
                                                  // load an image and make
                                                  // a rotated image drawable
    String home = System.getProperty("user.home");
    String filename = home + "/north_american_nebula.jpg";
     BufferedImage image = null;
    try
    {
      image = ImageIO.read( new File(filename ) );
    }
    catch ( Exception ex )
    {
      System.out.println("Failed to load " + filename );
    }
    ImageDrawable image_drawable = new ImageDrawable( image, 200, 200 );
    Point image_point = new Point(200, 200);
    image_drawable.setPosition( image_point );
    image_drawable.setRotationAngle( 30 );
//  image_drawable.setScaleFactors( 1.5, 1.5 );

    LineDrawable line_drawable = new LineDrawable( 300, 300, 200, 100 );
    line_drawable.setColor( Color.GREEN );
    
    TextDrawable message1 = new TextDrawable("Hi Everyone");
    message1.setColor(Color.BLUE);
    message1.setPosition( text_point );
    message1.setAlignment( TextDrawable.Horizontal.CENTER, 
                           TextDrawable.Vertical.BOTTOM );
    
                                                 // Add the image, boxes and
                                                 // text to the TwoD_JPanel, so
                                                 // we see them
    panel.AddObject( image_drawable );

//    panel.AddObject( oval1 );
//    panelw.AddObject( box2 );
    IDrawable[] group = { oval1, box2, message1 };
    GroupDrawable box_group = new GroupDrawable( group );
    box_group.setScaleFactors( 1.5, 1.25 );
    box_group.setRotationAngle( 30 );

    panel.AddObject( box_group );

    panel.AddObject( box3 );
    panel.AddObject( box4 );
    panel.AddObject( line_drawable );
//  panel.AddObject( message1 );
                                                 // add the TwoD_JPanel to the 
    frame.add( panel );                          // frame and make it visible
    frame.setVisible(true);
                                                 // To do simple animation,
                                                 // repeatedly change some 
                                                 // angles, ask to draw the
    for ( int i = 0; i < 1000; i++ )             // objects, then "sleep"
    {                                            // 50 milliseconds.
      System.out.println("Animation step " + i );
      message1.setRotationAngle( 10*i );
      oval1.setRotationAngle(-i*10);
      panel.draw();
      try
      {
        Thread.sleep(50);
      }
      catch( Exception ex )
      {
        System.out.println("Error sleeping?");
      }
    }
    
    
//    message1.setRotationAngle(0);
    panel.draw();
  }

}
