/* 
 * file: ViewerSim.java
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
 *  Revision 1.5  2003/10/16 05:00:03  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.4  2003/08/11 23:46:13  millermi
 *  - Adding test for selected regions.
 *
 *  Revision 1.3  2003/06/06 18:50:04  dennis
 *  (Mike Miller) Altered space allocated by the control viewer.
 *
 *  Revision 1.2  2003/05/29 14:26:54  dennis
 *  Two changes: (Mike Miller)
 *   -added exit on close feature
 *   -added coordination in displaying window and f2 JFrames, no longer initially
 *    display over each other.
 * 
 *  Revision 1.1  2003/05/22 13:06:55  dennis
 *  Basic test program for ViewComponent.
 *
 */

 package DataSetTools.components.View;
 
 import javax.swing.*;
 import java.awt.Point;
 import java.awt.event.*;
 import java.awt.Container;
 import java.awt.Rectangle;
 
 import DataSetTools.components.View.ViewControls.*;
 import DataSetTools.components.View.TwoD.*;
 import DataSetTools.components.View.Menu.*;
 import DataSetTools.components.View.Region.Region;
 import DataSetTools.components.View.Transparency.SelectionOverlay;

/**
 * This class is a mock viewer to test basic functionality of any 
 * IViewComponent2D components. One big difference between this tester
 * and the newer IVCTester is that the controls are in a separate panel.
 */ 
public class ViewerSim
{ 
   private IViewComponent2D ivc;
   private ViewMenuItem[] menus;
   private JComponent[] controls;

  /**
   * Constructor reads in an IViewComponent2D and gets the controls and menu
   * from that component.
   *
   *  @param  comp - IViewComponent2D component
   */
   public ViewerSim( IViewComponent2D comp )
   {
      ivc = comp;
      menus = ivc.getSharedMenuItems();
      controls = ivc.getSharedControls();
      ivc.addActionListener( new IVCListener() );
   }
  
  /**
   * Displays IViewComponent2D with its menus. The Controls are displayed
   * in a separate window.
   */ 
   public void show() 
   {
      JFrame window = new JFrame("Test Viewer");
      window.setBounds(0,0,500,500);
      window.getContentPane().add( ivc.getDisplayPanel() );
      
      if( menus.length > 0 )
      {
         JMenuBar menu_bar = new JMenuBar();
         window.setJMenuBar(menu_bar);       
 
         JMenu fileMenu    = new JMenu("File");      // Menus for the menu bar 
         JMenu editMenu    = new JMenu("Edit");
         JMenu viewMenu    = new JMenu("View");
         JMenu optionsMenu = new JMenu("Options");
   
         menu_bar.add(fileMenu); 
         menu_bar.add(editMenu); 
         menu_bar.add(viewMenu); 
         menu_bar.add(optionsMenu);
      
         for( int i = 0; i < menus.length; i++ )
         {
            if( ViewMenuItem.PUT_IN_FILE.toLowerCase().equals(
	             menus[i].getPath().toLowerCase()) )
               fileMenu.add( menus[i].getItem() ); 
            else if( ViewMenuItem.PUT_IN_EDIT.toLowerCase().equals(
	          menus[i].getPath().toLowerCase()) )
               editMenu.add( menus[i].getItem() );
            else if( ViewMenuItem.PUT_IN_VIEW.toLowerCase().equals(
	          menus[i].getPath().toLowerCase()) )
               viewMenu.add( menus[i].getItem() );
            else // put in options menu
               optionsMenu.add( menus[i].getItem() );           
         }      
      }
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.setVisible(true);
 
      if( controls.length > 0 )
      {
         JFrame f2 = new JFrame("ISAW ImageViewControls");
         Container cpain = f2.getContentPane();
         cpain.setLayout( new BoxLayout(cpain,BoxLayout.Y_AXIS));  

         for( int i = 0; i < controls.length; i++ )
            cpain.add(controls[i]);
	 Rectangle main = window.getBounds();
	 int x = (int)( main.getX() + main.getWidth() );
	 int y = (int)( main.getY() );
         f2.setBounds(x, y, 200, y + (60 * controls.length)); 
	 f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         f2.setVisible(true); //display the frame      
      }
   }
   
   private class IVCListener implements ActionListener
   {
     public void actionPerformed( ActionEvent ae )
     {
       String message = ae.getActionCommand();
       if( message.equals(SelectionOverlay.REGION_ADDED) )
       {
         Region[] selectedregions = ivc.getSelectedRegions();
	 for( int i = 0; i < selectedregions.length; i++ )
	 {
	   Point[] selectedpoints = selectedregions[i].getSelectedPoints();
	   System.out.println("NumSelectedPoints: " + selectedpoints.length);
	   for( int j = 0; j < selectedpoints.length; j++ )
	   {
	     System.out.println("(" + selectedpoints[j].x + "," + 
	                        selectedpoints[j].y + ")" );
	   }
	 }
       }
     }
   }

  /*
   * MAIN - Basic main program to test an ImageViewComponent object
   */
   public static void main( String args[] ) 
   {
      // *** test ImageViewComponent ***
      int col = 250;
      int row = 250;	
      //Make a sample 2D array
      VirtualArray2D va2D = new VirtualArray2D(row, col); 
      va2D.setAxisInfoVA( AxisInfo2D.XAXIS, .001f, .1f, 
                           "TestX","TestUnits", true );
      va2D.setAxisInfoVA( AxisInfo2D.YAXIS, 0f, -1f, 
                            "TestY","TestYUnits", true );
      va2D.setTitle("Main Test");
      //Fill the 2D array with the function x*y
      float ftemp;
      for(int i = 0; i < row; i++)
      {
         for(int j = 0; j < col; j++)
         {
            ftemp = i*j;
            if ( i % 25 == 0 )
	       va2D.setDataValue(i, j, i*col); //put float into va2D
            else if ( j % 25 == 0 )
	       va2D.setDataValue(i, j, j*row); //put float into va2D
            else
	       va2D.setDataValue(i, j, ftemp); //put float into va2D
	 }
      }
      ImageViewComponent livc = new ImageViewComponent(va2D);
      
      ViewerSim viewer = new ViewerSim(livc);
      viewer.show(); 
   }
} 
