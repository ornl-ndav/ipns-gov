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
 *  Revision 1.26  2006/07/10 16:25:06  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.27  2006/07/04 20:10:49  dennis
 *  Added test of passing in null to setValue().
 *
 *  Revision 1.26  2006/07/04 17:57:18  dennis
 *  Added test of RadioButtonPG.
 *  Replaced GridLayout with Box, so that different height PGs
 *  get laid out ok.
 *
 *  Revision 1.25  2006/07/03 20:50:51  dennis
 *  Now explicitly constructs some new objects to be passed into the
 *  tester, rather than relying on autoboxing.
 *
 *  Revision 1.24  2006/06/30 17:00:52  dennis
 *  Added test of PrinterNamePG.
 *  Minor code reformat.
 *
 *  Revision 1.23  2006/06/30 16:14:22  dennis
 *  Added test for ChoiceListPG.
 *  Added try...catch around setValue listener to indicate which
 *  PGs throw exceptions.
 *
 *  Revision 1.22  2006/06/30 14:59:43  rmikk
 *  Added a test for the loadFileArrayPg
 *
 *  Revision 1.21  2006/06/30 14:20:45  rmikk
 *  Added a test for the FloatArrayArrayPG
 *  Fixed an illegalArgumentException that occurs in setValue2. This causes the
 *     rest of the tests to fail to update
 *
 *  Revision 1.20  2006/06/29 23:08:22  dennis
 *  Added tests for LoadFilePG, SaveFilePG, DataDirPG, MaterialPG and
 *  InstNamePG
 *
 *  Revision 1.19  2006/06/29 22:18:48  rmikk
 *  Added test program for SampleDataSetPG's
 *
 *  Revision 1.18  2006/06/29 22:02:09  rmikk
 *  Added test cases for DataSetPg
 *
 *  Revision 1.17  2006/06/29 21:09:34  dennis
 *  Replaced log message that was removed in last checkin.
 *  Fixed format.
 *
 *  Revision 1.16  2006/06/29 20:59:16  rmikk
 *  Added tests for RealArrayPG and PlaceHolder PG's
 *
 *  Revision 1.15  2006/06/29 20:34:28  dennis
 *  Added tests for IntArrayPG and FunctStringPG
 *  
 *  Revision 1.14  2006/06/28 21:12:43  rmikk
 *  Added a test for the PlaceHolderPG
 *
 *  Revision 1.13  2006/06/28 20:22:01  rmikk
 *  Added a test case for the QbinsPG
 *
 *  Revision 1.12  2006/06/28 20:02:31  rmikk
 *  Added a test case for Qbins1PG
 *
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
 *  of the ParameterGUI objects.  The ParameterGUI objects
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
import DataSetTools.dataset.*;
import DataSetTools.parameter.DataSetPG;
import DataSetTools.parameter.MonitorDataSetPG;
import DataSetTools.parameter.PulseHeightDataSetPG;
import DataSetTools.parameter.SampleDataSetPG;
/**
 *  This class tests the NewParameterGUIs.  The ParameterGUI objects 
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
  public static final String SET_NULL    = "Set Value null";

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
  private void AddToTestList( IParameterGUI pg, 
                              Object        val_1, 
                              Object        val_2 )
  {
    pg_list.add( pg );
    val_1_list.add( val_1 );    
    val_2_list.add( val_2 );    
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
    f.setBounds( 0, 0, 500, 25 * (num_guis + 1) + 100 );

    Box box = new Box( BoxLayout.Y_AXIS );

    f.getContentPane().setLayout( new GridLayout(1,1) );
    f.getContentPane().add( box );
    for ( int i = 0; i < num_guis; i++ )
    {
      IParameterGUI pg = (IParameterGUI)pg_list.elementAt(i);
      box.add( pg.getGUIPanel(show_valid_box) );
    }

    JPanel spacer = new JPanel();
    spacer.setPreferredSize( new Dimension( 0, 500 ) );
    box.add( spacer );

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

    box.add( button_panel );
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
        IParameterGUI pg = (IParameterGUI)pg_list.elementAt(i);
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
      else if ( command.equals( SET_VAL_2 ) )
      {
        val_list = val_2_list;
        button.setText( SET_NULL );
      }
      else // command.equals( SET_NULL )
      {
        val_list = null;
        button.setText( SET_VAL_1 );
      }
      

      for ( int i = 0; i < pg_list.size(); i++ )
      {
        IParameterGUI pg = (IParameterGUI)pg_list.elementAt(i);
        try
        {
          if ( val_list != null )
            pg.setValue( val_list.elementAt(i) );
          else
            pg.setValue( null );
        }
        catch ( IllegalArgumentException exception )
        {
          System.out.print( "IllegalArgumentException in setValue() when " );
          System.out.println( "setting value for " + pg.getClass() );
        }
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
        IParameterGUI pg = (IParameterGUI)pg_list.elementAt(i);
        System.out.println("------------- " + pg.getName() + " -------------");
        System.out.println( pg.toString() );
        try
        {
          System.out.println( "      Value = " + pg.getValue() );
          System.out.println( "StringValue = " + pg.getStringValue() );
        }
        catch ( IllegalArgumentException exception )
        {
          System.out.println( "NO VALID VALUE AVAILABLE IN getValue() " +
                              "or in getStringValue()" );
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
        IParameterGUI pg = (IParameterGUI)pg_list.elementAt(i);
        pg.setValidFlag( valid );
      }
    }
  }


  public static void main( String args[] )
  {
    new DataSetTools.util.SharedData();
    TestPGs tester = new TestPGs();

    Boolean   TRUE  = new Boolean( true  );
    Boolean   FALSE = new Boolean( false );
    BooleanPG checkbox1 = new BooleanPG( "Boolean PG 1", FALSE );
    BooleanPG checkbox2 = new BooleanPG( "Boolean PG 2", FALSE );

    Vector V = new Vector();
      V.addElement( new Boolean( true));
      V.addElement( new Integer( 1));
      V.addElement( new Integer(1));

    BooleanEnablePG Check3 = new BooleanEnablePG("do.no do",V);

    IntegerPG int_pg    = new IntegerPG( "Integer PG Test", new Integer(2) );

    FloatPG   float_pg  = new FloatPG( "Float PG Test", new Float(3.1416f) );

    StringPG      str_pg     = new StringPG( "String PG Test", "Some String" );
    IntArrayPG    int_arr_pg = new IntArrayPG("Int List String", "-10:-5,0:1");
    FunctStringPG f_str_pg   = new FunctStringPG( "Function", "3.2*sin(x)" );
    MaterialPG    mat_pg     = new MaterialPG( "Material", "C,O_2" );
    InstNamePG    inst_pg    = new InstNamePG( "Instrument", null );
    ChoiceListPG  choice_pg  = new ChoiceListPG( "ChoiceList", "Third Choice");
    choice_pg.addItem( "First Choice" );
    choice_pg.addItem( "Second Choice" );
    choice_pg.addItem( "Third Choice" );
    choice_pg.addItem( "Fourth Choice" );
    RadioButtonPG radio_pg   = new RadioButtonPG("RadioButton","Third Choice");
    radio_pg.addItem( "First Choice" );
    radio_pg.addItem( "Second Choice" );
    radio_pg.addItem( "Third Choice" );
    radio_pg.addItem( "Fourth Choice" );

    PrinterNamePG print_pg = new PrinterNamePG( "Choose Printer", 
                                                "NO Printer 2" );

    DataDirPG dat_pg    = new DataDirPG( "DataDir PG Test", "Directory String");
    LoadFilePG ldf_pg   = new LoadFilePG( "LoadFile PG Test", "Load String");
    SaveFilePG sav_pg   = new SaveFilePG( "SaveFile PG Test", "Save String");   

    tester.AddToTestList( checkbox1, TRUE, FALSE );
    tester.AddToTestList( checkbox2, FALSE, TRUE );
    tester.AddToTestList( Check3 , TRUE, FALSE );
    tester.AddToTestList( int_pg, new Integer(1), new Integer(2) );
    tester.AddToTestList( float_pg, new Float(3.1416f), new Float(2.7183f));
    tester.AddToTestList( str_pg, "First String", "Second String" );
    tester.AddToTestList( int_arr_pg, "-10:-5,0:1","-10:-5,1:2");
    tester.AddToTestList( f_str_pg, "3.2*sin(x)", "3.2*cos(x)"); 
    tester.AddToTestList( mat_pg, "C,O_2", "H_2,O"); 
    tester.AddToTestList( inst_pg, "SCD0", "GPPD" );
    tester.AddToTestList( choice_pg, "First Choice", "Second Choice" );
    tester.AddToTestList( radio_pg, "First Choice", "Second Choice" );
    tester.AddToTestList( print_pg, "NO Printer 1", "NO Printer 3" );


    tester.AddToTestList( dat_pg, "/First_Dir", "/Second_Dir");
    tester.AddToTestList( ldf_pg, "/First_File.run", "/Second_File.dat");
    tester.AddToTestList( sav_pg, "/First_Save.dat", "/Second_Save.sav");

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
    VV.addElement( new Float(1.1f) );
    VV.addElement( new Float(3.2f) ); 
    VV.addElement( new Float(-5.4f) );
    VV1=new Vector(); 
    VV1.addElement( new Float(11.1f) );
    VV1.addElement( new Float(13.2f) ); 
    VV1.addElement( new Float(-15.4f) );
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
    
    tester.AddToTestList( new ArrayPG("small arrays",null),"[1,2,[3,4]]",
    		                                             "[abc,def,ghi]");
    
    tester.AddToTestList( new Qbins1PG("Qbins","[1,5,3,true]"),"[2,8,3,true]",
    		                                          "[2,16,4,FALSE]");
    
    tester.AddToTestList( new QbinsPG("Qbins","[1,3,5,7,9]"),
    		                          "[2,4,6,8,10]","[2,2,2,2,2,2]");
    
    tester.AddToTestList( new PlaceHolderPG( "PlaceHolder", new Integer(35)),
                         "String", 
                          new Float(15.2f));
    
    tester.AddToTestList( new RealArrayPG("Real Array", new int[1]),
    		          Conversions.StringToVec("[1,3,5,7]"), 
                          Conversions.StringToVec("[2.1,3.2,5.5]"));
    
    tester.AddToTestList( new RealArrayPG("Real Array", new int[0][0]),
    		Conversions.StringToVec("[[1,3,5,7],[2,4,6,8]]"), 
                Conversions.StringToVec("[[2.1,3.2,5.5],[3.1,4.1,5.1,6.1]]"));
    
    float[][]F1 ={{1.1f,2.3f,7.2f},{3.2f,8.3f,7.1f}};
    float[][]F2 ={{2.1f,3.3f,8.2f},{4.2f,9.3f,8.1f}};
    tester.AddToTestList( new RealArrayPG("Real Array", new float[0][0]),F1,F2);
    
    String path= System.getProperty( "ISAW_HOME")+"/SampleRuns/";
    try {
         DataSet[] DS1 = Command.ScriptUtil.load( path + "GPPD12358.RUN" );
         DataSet[] DS2 = Command.ScriptUtil.load( path + "hrcs2955.run" );

         DataSetPG DSpg = new DataSetPG( "DataSetPG" , null );
         SampleDataSetPG SDSpg = new SampleDataSetPG( "SampleDataSetPG " , null );
         MonitorDataSetPG MDSpg = new MonitorDataSetPG( "MonitorDataSetPG", null );
         PulseHeightDataSetPG PHDSpg = 
                          new PulseHeightDataSetPG( "PulseHeightDataSetPG", null );

         for ( int i = 0; i < DS1.length; i++ )  // add some DataSets to the PGs
         {
           DSpg.addItem  ( DS1[ i ] );
           SDSpg.addItem ( DS1[ i ] );
           MDSpg.addItem ( DS1[ i ] );
           PHDSpg.addItem( DS1[ i ] );
         }

         for ( int i = 0; i < DS2.length; i++ )
         {
           DSpg.addItem  ( DS2[ i ] );
           SDSpg.addItem ( DS2[ i ] );
           MDSpg.addItem ( DS2[ i ] );
           PHDSpg.addItem( DS2[ i ] );
         }

         tester.AddToTestList( DSpg , DS1[ DS1.length - 1 ], 
                                      DS2[ DS2.length - 1 ] );

         tester.AddToTestList( SDSpg , DS1[ DS1.length - 1 ] ,
                                       DS1[ DS1.length - 1 ] );

         tester.AddToTestList( MDSpg, DS1[ 0 ], DS2[ 0 ] );

         tester.AddToTestList( PHDSpg, DS1[ 1 ], DS2[ 1 ] );

      } catch( Exception s ) {
         System.out.println( "Could not find files " + s.toString() );
      }

    float[][] F ={{3.1f,3.1f,3.1f},{2.2f,2.2f,2.2f}};
    tester.AddToTestList( new FloatArrayArrayPG("FloatArrayArray", null), 
                          "[[1,2,3,4],[5,6,7,8]]", 
                           F);
    
    Vector V1 = Conversions.StringToVec( 
                          "["+path+"GPPD12358.RUN,"+path+"hrcs2955.run]");
    tester.AddToTestList( new LoadFileArrayPG("LoadFileArray", null),
                          V1, 
                         "["+path+"SCD06496.RUN]");

    tester.MakeGUI( true );      // show the valid check box
//  tester.MakeGUI( false );     // don't show the valid check box
  }


}
