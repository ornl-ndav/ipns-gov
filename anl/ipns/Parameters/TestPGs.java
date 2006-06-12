/*
 * File:  TestPGs.java
 *
 * Copyright (C) 2006, Dennis Mikkelson
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
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2006/06/12 21:52:30  dennis
 *  Initial version of new code for parameter GUIs.  While this is
 *  loosely based on the parameter GUIs developed several years ago
 *  by Peter Peterson and Chris Bouzek, the current system is much
 *  simplified, and should be easier to maintain and extend.
 *
 */

package gov.anl.ipns.Parameters;

import javax.swing.*;
import java.awt.*;
import java.util.*;


public class TestPGs
{
  
  private static void Exercise( INewParameterGUI pg, 
                                Object        val_1, 
                                Object        val_2,
                                boolean       test_valid_flag )
  {
    System.out.println("\nStart testing " + pg.getName() + " .............. " );
    System.out.println("TYPE IDENTIFIED AS " + pg.getType() );
    Scanner sc = new Scanner( System.in );
    
    System.out.println("Test set value ......");
    pg.setValue( val_1 );
    System.out.println("Set Value to " + val_1 );
    System.out.println("Press return to continue");
    sc.nextLine();

    pg.setValue( val_2 );
    System.out.println("Set Value to " + val_2 );
    System.out.println("Press return to continue");
    sc.nextLine();
     
    if ( test_valid_flag )
    {
      System.out.println("Test valid flag ......");
      pg.setValidFlag( true );
      System.out.println("VALID FLAG set TRUE");
      System.out.println("VALID FLAG is " + pg.getValidFlag() );
      System.out.println("Press return to continue");
      sc.nextLine();
      pg.setValidFlag( false );
      System.out.println("VALID FLAG set FALSE");
      System.out.println("VALID FLAG is " + pg.getValidFlag() );
      System.out.println("Press return to continue");
      sc.nextLine();
    }

    System.out.println("Test get value ......");
    String answer = "Y";
    while ( answer.toUpperCase().startsWith( "Y" ) )
    {
      System.out.println("Value currently is " + pg.getValue() );
      System.out.println("If you want to change it, change GUI and press 'y'");
      answer = sc.next();
    }

    pg.setEnabled( false );

    System.out.println("Done testing " + pg.getName() + " .............. \n" );
    System.out.println();
  }


  public static void main( String args[] )
  {
     JFrame f = new JFrame("Test for ParameterGUIs");
     f.setBounds(0,0,400,400);

     f.getContentPane().setLayout( new GridLayout(4,1) );

     BooleanPG checkbox1 = new BooleanPG( "Bool Test 1", false );
     f.getContentPane().add( checkbox1.getGUIPanel( false ) );

     BooleanPG checkbox2 = new BooleanPG( "Bool Test 2 (X)", false );
     f.getContentPane().add( checkbox2.getGUIPanel( true ) );

     f.setVisible( true );

     Exercise( checkbox1, true, false, true );
     Exercise( checkbox2, true, false, true );

     System.out.println("Press return to exit");
     Scanner sc = new Scanner( System.in );
     sc.nextLine();

     checkbox1.destroyGUIPanel();
     checkbox2.destroyGUIPanel();
     System.exit(0);
  }


}
