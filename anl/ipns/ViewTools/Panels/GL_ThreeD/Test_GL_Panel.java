/*
 * File:  Test_GL_Panel.java
 *
 * Copyright (C) 2004, Dennis Mikkelson
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2004/05/28 20:51:16  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD;
import java.awt.event.*;
import javax.swing.*;
import gov.anl.ipns.MathTools.Geometry.*;
import net.java.games.jogl.*;


public class Test_GL_Panel
{
  public static void main( String args[] )
  {  
    int pick_id = 111000;

    JFrame frame = new JFrame("JOGL/ISAW Demo");
    frame.setSize(500, 500);
    
    ThreeD_GL_Panel panel = new ThreeD_GL_Panel();
    GL_Shape obj_0 = new Cube( 0, 0, 0, 0.1f );
    GL_Shape obj_1 = new Cube( 0, 0, 0, .8f );
    GL_Shape obj_2 = new Cube( 1, 0, 0, .8f );
    GL_Shape obj_3 = new Cube( 1, 1, 0, .8f );
    obj_0.setPickID( pick_id++ );
    obj_1.setPickID( pick_id++ );
    obj_2.setPickID( pick_id++ );
    obj_3.setPickID( pick_id++ );

    GL_Shape objects[] = { obj_0, obj_1, obj_2, obj_3 };
    panel.setObjects( "Ojects", objects );
    
    float red[]   = {0.9f, 0.4f, 0.4f};
//    float green[] = {0.4f, 0.9f, 0.4f};
    float blue[]  = {0.4f, 0.4f, 0.9f};
    float white[] = {1,1,1};
    float gray[]  = {0.4f, 0.4f, 0.4f};

    obj_0.setTransparency(1);
    for ( int i = 1; i < 4; i++ )
    {
      objects[i].setTransparency(0.5f);
      objects[i].setColor( red );
    }

    GL_Shape obj = null;
    IThreeD_GL_Object obj_list[]= new IThreeD_GL_Object[1];
/*    int size = 10;
   
    for ( int i = -size; i <= size; i++ )
      for ( int j = -size; j <= size; j++ )
      {
      	obj = new Cube( 0, j, i, 0.7f );
        obj.setTransparency( 0.2f );

        if ( i <= -size/2 )
          obj.setColor( green );

        else if ( i <= 0 )
          obj.setColor( white );

        else if ( i <= size/2 )
          obj.setColor( blue );

        else
           obj.setColor( red );

      	obj_list[0] = obj;
      	panel.setObjects( "Obj:" + i + ", " +j, obj_list );
      }
*/
    obj = new Cube( 0, 0, 0, 0.7f );
    obj.setPickID( pick_id++ );
    Tran3D tran = new Tran3D();
    tran.setOrientation( new Vector3D( 1, 0, 0 ), 
                         new Vector3D( 0, 0.5f, 1 ),  
                         new Vector3D( 3,4,5 ) );
    obj.setTransform( tran );
    obj.setColor( blue );
    obj_list[0] = obj;
    panel.setObjects("Moved Object", obj_list);

    byte image[] = { -1, -1,  0,  
                      0, -1, -1,
                     -1,  0, -1, 
                     -1, -1, -1 };
    int n_rows = 2;
    int n_cols = 2;

    Texture2D texture = new Texture2D( image, n_rows, n_cols );
    texture.setWrap_s( GL.GL_CLAMP );
    texture.setWrap_t( GL.GL_CLAMP );
    texture.setFilter( GL.GL_NEAREST );
    texture.setMode( GL.GL_MODULATE );
    obj = new Square( 2, 0, 0, 3 );
    obj.setPickID( pick_id++ );
    obj.setColor( gray );
    obj.setTexture( texture );
    obj_list[0] = obj;
    panel.setObjects("Textured Object", obj_list);

    byte image1[] = { -1,0,0, 0,-1,0, 0,0,-1, -1,-1,-1 };
    Texture1D texture1 = new Texture1D( image1, 4 );
    texture1.setFilter( GL.GL_NEAREST );
    texture1.setWrap_s( GL.GL_CLAMP );
    obj = new Square( 2, 3,4,5 );
    obj.setPickID( pick_id++ );
    obj.setTexture( texture1 );
    panel.setObject( "1D Textured Object", obj );

    byte image2[] = { -1, -1,  0,
                       0,  0, -1,
                      -1,  0,  0,
                       0, -1,  0 };
    Texture2D texture2 = new Texture2D( image2, n_rows, n_cols );
    texture2.setWrap_s( GL.GL_REPEAT );
    texture2.setWrap_t( GL.GL_REPEAT );
    texture2.setFilter( GL.GL_LINEAR );
    texture2.setMode( GL.GL_DECAL );
    obj = new Square( 2, 0, 0, 3 );
    obj.setPickID( pick_id++ );
    obj.setColor( white );
    obj.setTexture( texture2 );
    obj_list[0] = obj;
    tran = new Tran3D();
    tran.setTranslation( new Vector3D( 4,4,4 ) );
    obj.setTransform( tran );
    panel.setObjects("Textured Object 2", obj_list);

    GLU_QuadricObject glu_obj = new GLU_Sphere( 2, 10, 10 );
    glu_obj.setPickID( pick_id++ );
    glu_obj.setColor( white );
    glu_obj.setTexture( texture );
    glu_obj.setNormalType( GLU.GLU_SMOOTH );
    glu_obj.setDrawStyle( GLU.GLU_FILL );
    obj_list[0] = glu_obj;
    panel.setObjects( "Sphere", obj_list );

    glu_obj = new GLU_Cylinder( 2, 3, 4, 10, 10 );
    glu_obj.setPickID( pick_id++ );
    glu_obj.setColor( white );
    glu_obj.setTransparency( 0.4f );
    glu_obj.setTexture( texture2 );
    glu_obj.setNormalType( GLU.GLU_SMOOTH );
    glu_obj.setDrawStyle( GLU.GLU_FILL );
    obj_list[0] = glu_obj;
    panel.setObjects( "Cylinder", obj_list );
    
    float z[][] = new float[11][11];
    for ( int row = 0; row < 11; row++ )
      for ( int col = 0; col < 11; col++ )
        z[row][col] = -((row-5)*(row-5)+(col-5)*(col-5)) / 5.0f - 3;
        
    obj = new HeightField( z, 10, 10 );
    obj.setPickID( pick_id++ );
    obj.setTexture( texture );
    obj.setTransparency( 0.4f );
    obj.setColor( white );
    obj_list[0] = obj;
    panel.setObjects( "Surface", obj_list );

    frame.getContentPane().add( panel.getDisplayComponent());

    frame.addWindowListener(new WindowAdapter() {
       public void windowClosing(WindowEvent e) {
         System.exit(0);
       }
    });
    frame.show();

    JFrame f = new JFrame( "Controller for GL Window" );
    f.setBounds(0,0,200,200);
    AltAzController controller = new AltAzController();
    controller.setDistance(100);
    controller.setDistanceRange(1,500);
    ViewControlListener c_listener = new ViewControlListener(panel);
    controller.addActionListener( c_listener );
    Vector3D cop = controller.getCOP();
    panel.setCOP(cop);
    f.getContentPane().add( controller );
    f.setVisible( true );

   
    ThreeD_GL_Panel panel2 = new ThreeD_GL_Panel();
    obj = new HeightField( z, 10, 10 );
    obj.setPickID( pick_id++ );
    obj.setColor( white );
    obj.setTransparency( 0.4f );
    Texture2D texture3 = new Texture2D( image, n_rows, n_cols );
    obj.setTexture( texture3 );
    obj_list[0] = obj;
    panel2.setObjects("Surface_in_panel_2",obj_list );

    obj_3 = new Cube( 1, 1, 0, 1 );
    obj_3.setPickID( pick_id++ );
    obj_3.setColor( red );
    panel2.setObject("Cube_in_panel_2", obj_3 );

    c_listener = new ViewControlListener(panel2);
    panel2.setCOP(cop);
    controller.addActionListener( c_listener );
    frame = new JFrame("Window 2");
    frame.setSize(500, 500);
    frame.getContentPane().add( panel2.getDisplayComponent());
    frame.show();
    panel2.Draw();
  }
}
