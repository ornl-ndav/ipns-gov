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
 *  Revision 1.11  2006/06/28 15:37:05  rmikk
 *  Added the ArrayPG test case
 *
 *  Revision 1.10  2006/06/27 21:55:23  rmikk
 *  Added a test program for the StringArrayPG
 *
 *  Revision 1.9  2006/06/27 21:39:32  rmikk
 *  Moved the BooleanEnable start to a point after the GUI was made.
 *
 *  Added a FloatArrayPG test case
 *
 *  Revision 1.8  2006/06/27 20:33:33  dennis
 *  Minor reformat
 *
 *  Revision 1.7  2006/06/27 19:50:23  rmikk
 *  Added the IntegerArrayPG test case
 *
 *  Revision 1.6  2006/06/27 16:35:48  taoj
 *  Modified main() to test the float PG.
 *
 *  Revision 1.5  2006/06/26 22:15:20  rmikk
 *  Incorporated a test case for the BooleanEnablePG
 *
 *  Revision 1.4  2006/06/23 14:15:20  dennis
 *  Added test of StringPG.
 *
 *  Revision 1.3  2006/06/15 23:37:14  dennis
 *  Added test of IntegerPG.
 *  Added basic exception handling when calling getValue().
 *
 *  Revision 1.2  2006/06/13 19:51:12  dennis
 *  Modified Test GUI that includes controls for testing the functionality
 *  of the NewParameterGUI objects.  The NewParameterGUI objects
 *  are enclosed in a JFrame, with several control buttons to allow testing
 *  the Enable/Disable, get/setValue and get/setValidFlag methods.
 *  As new PGs are created, the main method of this class should be
 *  modified to test the new PGs.
 *
 *  Revision 1.1  2006/06/12 21:52:30  dennis
 *  Initial version of new code for parameter GUIs.  While this is
 *  loosely based on the parameter GUIs developed several years ago
 *  by Peter Peterson and Chris Bouzek, the current system is much
 *  simplified, and should be easier to maintain and extend.
 *
 */

package gov.anl.ipns.Parameters;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

/**
 *  This class tests the NewParameterGUIs.  The NewParameterGUI objects 
 *  are enclosed in a JFrame, with several control buttons to allow testing
 *  the Enable/Disable, get/setValue and get/setValidFlag methods.
 *  As new PGs are created, the main method of this class should be 
 *  modified to test the new PGs.
 */
public class TestPGs
{
  public static final String SET_VALID   = " Set Valid ";
  public static final String SET_INVALID = "Set InValid";

  public static final String ENABLE      = "Enable ";
  public static final String DISABLE     = "Disable";

  public static final String SET_VAL_1   = "Set Value 1";
  public static final String SET_VAL_2   = "Set Value 2";

  Vector pg_list    = new Vector();     // list of PG's being tested
  Vector val_1_list = new Vector();     // list of default values for the PG's
  Vector val_2_list = new Vector();     // list of second values for the PG's
  

  /**
   *  Add the specified pg and pair of values to the list of pgs being
   *  tested.
   *
   *  @param  pg     The PG being tested.
   *  @param  val_1  The value to set into the pg when the "Set Value 1" 
   *                 button is pushed.
   *  @param  val_2  The value to set into the pg when the "Set Value 2" 
   *                 button is pushed.
   */
  private void AddToTestList( INewParameterGUI pg, 
                              Object        val_1, 
                              Object        val_2 )
  {
    pg_list.add( pg );
    val_1_list.add( val_1 );    
    val_2_list.add( val_2 );    

    pg.setValue( val_1 );
  }


  /**
   *  Make the GUI for this test.
   *
   *  @param  show_valid_box  Flag that indicates whether or not the valid
   *                          check box should be drawn.
   */
  private void MakeGUI( boolean show_valid_box )
  {
    
    int num_guis = pg_list.size();

    JFrame f = new JFrame("Test for ParameterGUIs");
    f.setBounds( 0, 0, 500, 25 * (num_guis + 1) + 25 );

    f.getContentPane().setLayout( new GridLayout(num_guis + 1, 1) );
    for ( int i = 0; i < num_guis; i++ )
    {
      INewParameterGUI pg = (INewParameterGUI)pg_list.elementAt(i);
      f.getContentPane().add( pg.getGUIPanel(show_valid_box) );
    }

    JPanel  button_panel  = new JPanel();
    JButton enable_button = new JButton( DISABLE );
    JButton value_button  = new JButton( SET_VAL_2 );
    JButton show_button   = new JButton( "Show Values" );
    JButton valid_button  = new JButton( SET_VALID );

    enable_button.addActionListener( new EnableListener() );
    value_button.addActionListener( new ValueListener() );
    show_button.addActionListener( new ShowListener() );
    valid_button.addActionListener( new ValidListener() );

    button_panel.setLayout( new GridLayout( 1, 4 ) );
    button_panel.add( enable_button ); 
    button_panel.add( value_button ); 
    button_panel.add( show_button ); 
    button_panel.add( valid_button ); 

    f.getContentPane().add( button_panel );
    f.setVisible( true );
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    for(int i =0; i< pg_list.size(); i++)
        if( pg_list.elementAt(i) instanceof BooleanEnablePG)
          ((BooleanEnablePG)pg_list.elementAt(i)).addPropertyChangeListener( 
  					new EnableParamListener(pg_list,i));

  }


  /**
   *  This class handles the Enable/Disable button events,
   *  and goes through the list of PGs, enabling or disabling them.
   */
  class EnableListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      String  command = e.getActionCommand();
      JButton button  = (JButton)(e.getSource());
      boolean enable; 
      if ( command.equals( ENABLE ) )
      {
        enable = true;
        button.setText( DISABLE ); 
      }
      else
      {
        enable = false;
        button.setText( ENABLE );
      }

      for ( int i = 0; i < pg_list.size(); i++ )
      {
        INewParameterGUI pg = (INewParameterGUI)pg_list.elementAt(i);
        pg.setEnabled( enable );
      }
    }
  }


  /**
   *  This class handles the SetValue1/SetValue2 button events,
   *  and goes through the list of PGs, setting the first or second value
   *  into the PGs, as specified. 
   */
  class ValueListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      String  command = e.getActionCommand();
      JButton button  = (JButton)(e.getSource());
      Vector  val_list;
      if ( command.equals( SET_VAL_1 ) )
      {
        val_list = val_1_list;
        button.setText( SET_VAL_2 );
      }
      else
      {
        val_list = val_2_list;
        button.setText( SET_VAL_1 );
      }

      for ( int i = 0; i < pg_list.size(); i++ )
      {
        INewParameterGUI pg = (INewParameterGUI)pg_list.elementAt(i);
        pg.setValue( val_list.elementAt(i) );
      }
    }
  }


  /**
   *  This class handles the Show Values button events, and goes through 
   *  the list of PGs, printing the values of all of the PGs to the 
   *  console. 
   */
  class ShowListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      System.out.println("\n\n=================== PG_VALUES ================");
      for ( int i = 0; i < pg_list.size(); i++ )
      {
        INewParameterGUI pg = (INewParameterGUI)pg_list.elementAt(i);
        System.out.println("------------- " + pg.getName() + " -------------");
        System.out.println( pg.toString() );
        try
        {
          System.out.println( "      Value = " + pg.getValue() );
          System.out.println( "StringValue = " + pg.getStringValue() );
        }
        catch ( IllegalArgumentException exception )
        {
          System.out.println( "NO VALID VALUE AVAILABLE IN GUI" );
        }
      }
    }
  }


  /**
   *  This class handles the Valid/Invalid button events,
   *  and goes through the list of PGs, setting the valid flag to the 
   *  requested state.
   */
  class ValidListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      String  command = e.getActionCommand();
      JButton button  = (JButton)(e.getSource());
      boolean valid;
      if ( command.equals( SET_VALID ) )
      {
        valid = true;
        button.setText( SET_INVALID );
      }
      else
      {
        valid = false;
        button.setText( SET_VALID );
      }

      for ( int i = 0; i < pg_list.size(); i++ )
      {
        INewParameterGUI pg = (INewParameterGUI)pg_list.elementAt(i);
        pg.setValidFlag( valid );
      }
    }
  }


  public static void main( String args[] )
  {
    TestPGs tester = new TestPGs();

    BooleanPG checkbox1 = new BooleanPG( "Boolean PG 1", false );
    BooleanPG checkbox2 = new BooleanPG( "Boolean PG 2", false );

    Vector V = new Vector();
      V.addElement( new Boolean( true));
      V.addElement( new Integer( 1));
      V.addElement( new Integer(1));

    BooleanEnablePG Check3 = new BooleanEnablePG("do.no do",V);

    IntegerPG int_pg    = new IntegerPG( "Integer PG Test", 2 );

    FloatPG   float_pg  = new FloatPG( "Float PG Test", 3.1416f );

    StringPG  str_pg    = new StringPG( "String PG Test", "Some String" );

    tester.AddToTestList( checkbox1, true, false );
    tester.AddToTestList( checkbox2, false, true );
    tester.AddToTestList( Check3 , true, false);
    tester.AddToTestList( int_pg, 1, 2 );
    tester.AddToTestList( float_pg, 3.1416f, 2.7183f);
    tester.AddToTestList( str_pg, "First String", "Second String" );

    Vector VV= new Vector(),
           VV1 = new Vector();
    VV.addElement( new Integer(1) );
    VV.addElement( new Integer(2) );
    VV.addElement( new Integer(3) );
    VV1.addElement( new Integer(21) );
    VV1.addElement( new Integer(32) );
    VV1.addElement( new Integer(43) );
      
    tester.AddToTestList( new IntegerArrayPG("big int array",null),VV,VV1);
   
    VV=new Vector(); 
    VV.addElement( 1.1f);
    VV.addElement( 3.2f); 
    VV.addElement( -5.4f);
    VV1=new Vector(); 
    VV1.addElement( 11.1f);
    VV1.addElement( 13.2f); 
    VV1.addElement( -15.4f);
 
    
    tester.AddToTestList( new FloatArrayPG("big float array",null),VV,VV1);
    
    
    VV=new Vector();
    VV.addElement("jjj");
    VV.addElement("kkk");
    VV.addElement("lll");
    VV1=new Vector();
    VV1.addElement("abc");
    VV1.addElement("def");
    VV1.addElement("ghi");
    
    
    tester.AddToTestList( new StringArrayPG("big String array",null),VV,VV1);
    
    tester.AddToTestList( new ArrayPG("small arrays",null),"[1,2,[3,4]]","[abc,def,ghi]")
    ;
    tester.MakeGUI( true );      // show the valid check box
//  tester.MakeGUI( false );     // don't show the valid check box
  }


}
