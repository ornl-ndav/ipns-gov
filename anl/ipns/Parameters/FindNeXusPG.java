/* 
 * File: FindNeXusPG.java
 *
 * Copyright (C) 2010, Ruth Mikkelson
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
 *  $Author:$
 *  $Date:$            
 *  $Rev:$
 */

package gov.anl.ipns.Parameters;

import gov.anl.ipns.Util.File.FileIO;
//import gov.anl.ipns.Util.SpecialStrings.ErrorString;
import gov.anl.ipns.Util.Sys.*;
import gov.anl.ipns.ViewTools.Panels.GraphTagFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.*;

/**
 * The class represents a PG that works only at SNS. It calls the findnexus command
 * @author ruth
 *
 */
public class FindNeXusPG extends ButtonPG
{
   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   Vector InitialValue = null;
   ButtonPressedListener ButtonListener;
   
   /**
    * Constructor
    * @param Prompt  Prompt(Not used)
    * @param Value   The initial input values followed by the initial value.
    *               The order is runNum,Instrument,proposal num,collection num, 
    *               recursive level, and starting directory. 
    */
   public FindNeXusPG(String Prompt, Object Value)
   {

      super( "Find SNS NeXus File");;
      try
      {   
        InitialValue =Conversions.ToVec( Value );
      }catch( Exception s)
      {
         InitialValue = null;
      }
      if( InitialValue != null && InitialValue.size()>=7)
            
         super.setValue( Conversions.get_String( InitialValue.elementAt(6) ));

      ButtonListener = new ButtonPressedListener( this,InitialValue);
      super.addActionListener( ButtonListener);
      ButtonListener.setRecipient( this );
   }
   public FindNeXusPG()
   {
      this( null, null);
     
   }
   
   public Object clone()
   {
      FindNeXusPG fpg = new FindNeXusPG("Find SNS NeXus File", InitialValue);
      
      for( int i=0; i< listeners.size(); i++)
         if( listeners.elementAt(i).get() != null &&
                !(listeners.elementAt(i).get() instanceof ButtonPressedListener))
         fpg.addActionListener(  listeners.elementAt(i).get() );
      
      if( panel != null || button != null)
         fpg.getWidget();
     
      return fpg;
   }

   /**
    * @param args
    */
   public static void main(String[] args)
   {

       FindNeXusPG fpg = new FindNeXusPG();
       FinishJFrame jf = new FinishJFrame("Test");
       jf.getContentPane( ).setLayout(new GridLayout(1,1));
       jf.getContentPane( ).add( fpg.getGUIPanel( false) );
       jf.setSize( 100,100 );
       jf.setVisible( true );

   }
   
   /**
    * Pops up and handles a JFrame containing the input textfields.
    * @author ruth
    *
    */
   class ButtonPressedListener implements java.awt.event.ActionListener,
                                          IhasWindowClosed, IReturnValue
   {

      FinishJFrame jf = null;
      ButtonPG  button_pg;
      FindNexJPanel    FindNexParams = null;
      IParameter ValueRecipient;
      Vector InitialValue;
      
   
      public ButtonPressedListener( ButtonPG  button_pg, Vector InitialValue)
      {
         this.button_pg = button_pg;
         this.InitialValue = InitialValue;
      }
 
      public void actionPerformed(ActionEvent arg0)
      {
         if( FindNexParams == null)
         {
            FindNexParams = new FindNexJPanel(InitialValue);
            FindNexParams.setRecipient( ValueRecipient );
         }
         
         if( jf == null )
         {
            jf = new FinishJFrame("Find NeXus Parameters");
            jf.getContentPane( ).setLayout(  new GridLayout(1,1) );
          
           
            jf.getContentPane().add( FindNexParams);
            JPanel butPan = button_pg.getWidget( );
            
             
            Point P = GraphTagFrame.getPositionAbs( butPan );
            if( P== null)
               P= new Point(0,0);
            Dimension D= butPan.getToolkit( ).getScreenSize( );
            butPan.addAncestorListener(  new WindowAncestorListener( jf) );
            jf.addWindowListener(  new IndirectWindowCloseListener( this,"Frame" ) );
            jf.setBounds( P.x+100 , P.y+100 , Math.min( D.width/4 , D.width-P.x ) ,
                            Math.min( D.height/3 , D.height-P.y ));
            WindowShower.show( jf );
           
            
         }
         
         
      }

      @Override
      public void WindowClose(String ID)
      {

         if( ID.equals( "Frame" ))
               jf = null;
         
      }

      @Override
      public void setRecipient(IParameter ValueRecipient)
      {
        this.ValueRecipient = ValueRecipient;
        ValueRecipient.setValue("");// To get Data Type to be a String for this Parameter

        if( FindNexParams != null)
           FindNexParams.setRecipient( ValueRecipient );
         
      }
      
      
      
      
   }
   
   private String GetFieldValue( Vector values, int i)
   {
      if(values == null || i <0 || i >= values.size())
         return "-1";
      return new Integer(Conversions.get_int( values.elementAt(i) )).toString();
   }
   
   

   private String GetStringFieldValue( Vector values, int i)
   {
      if(values == null || i <0 || i >= values.size())
         return "";
      return Conversions.get_String(  values.elementAt(i) );
   }
   
   
   /**
    * This panel contains the text fields that have to be filled out
    * to specify the file desired.
    * @author ruth
    *
    */
   class FindNexJPanel  extends JPanel implements IReturnValue, ActionListener
   {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;
      IParameter ValueRecipient;
      JTextField Run, Inst,Prop,Coll,Recurs;
      JLabel Result;
      FileChooserPanel filePick;
      JButton OK;
      
      
      public FindNexJPanel(Vector InitialValue )
      {
         super();
         ValueRecipient = null;
         BoxLayout bl = new BoxLayout( this,BoxLayout.Y_AXIS);
         setLayout( bl);
         JPanel TopPanel = new JPanel( new GridLayout( 3,4));
         Run = new JTextField(GetFieldValue(InitialValue,0));
         Inst = new JTextField(GetStringFieldValue(InitialValue,1));
         Prop = new JTextField(GetFieldValue(InitialValue,2));
         Coll = new JTextField(GetFieldValue(InitialValue,3));
         Recurs = new JTextField(GetFieldValue(InitialValue,4));
         TopPanel.add(  new JLabel("Run Number", SwingConstants.RIGHT) );
         TopPanel.add( Run);
         TopPanel.add(  new JLabel("Collection Number", SwingConstants.RIGHT) );
         TopPanel.add( Coll);
         TopPanel.add(  new JLabel("Instrument Name", SwingConstants.RIGHT) );
         TopPanel.add( Inst);
         TopPanel.add(  new JLabel("Level of Recursion", SwingConstants.RIGHT) );
         TopPanel.add( Recurs);
         TopPanel.add(  new JLabel("Proposal Number", SwingConstants.RIGHT) );
         TopPanel.add( Prop);
         TopPanel.add(  new JLabel() );
         TopPanel.add( new JLabel());
         add(TopPanel);
         add( Box.createVerticalGlue( ));
         
         filePick = new FileChooserPanel( FileChooserPanel.SET_DIRECTORY,"Start Directory");
         add( filePick);
         Result = new JLabel(GetStringFieldValue(InitialValue,6));
         OK = new JButton("OK");
         JPanel bottom = new JPanel( new GridLayout( 1,2));
         bottom.add( Result );
         bottom.add( OK);
         OK.addActionListener(  this );
         add(bottom); 
      }
      
      public void actionPerformed( ActionEvent evt)
      {
         int run = -1,
             proposal =-1,
             collect = -1,
             recurs  = -1;
         String StartDir ="",
                instrument ="";
        try
        {
          run = Integer.parseInt( Run.getText( ).trim());
        }catch(Exception s_run)
        {
           JOptionPane.showMessageDialog(  null , "Must have a legitimate run number" );
           return;
        }
        String ErrMessages ="";
        try
        {
          proposal = Integer.parseInt( Prop.getText( ).trim());
          
        }catch(Exception s_run)
        {
           if( Prop.getText( ).trim( ).length() >0)
              ErrMessages +="Proposal,";
          proposal = -1;
        }
       
        instrument =  Inst.getText( ).trim();
       
        try
        {
          collect = Integer.parseInt( Coll.getText( ).trim());
        }catch(Exception s_run)
        {

           if( Coll.getText( ).trim( ).length() >0)
              ErrMessages +="  Collect,";
           collect = -1;
        }
        try
        {
          recurs = Integer.parseInt( Recurs.getText( ).trim());
        }catch(Exception s_run)
        {

           if( Recurs.getText( ).trim( ).length() >0)
              ErrMessages +="  Recursive level,";
           recurs = -1;
          
        }
        if( ErrMessages.length() > 0)
        {
           ErrMessages = ErrMessages.substring( 0,ErrMessages.length()-1 )+" were improper";
           JOptionPane.showMessageDialog(  null , ErrMessages );
        }
    
        StartDir = filePick.getTextField( ).getText( ).trim( );
        Object Res = FileIO.findNexusAtSNS(run, instrument, proposal,collect, 
             StartDir,"", recurs);
        if( !(Res instanceof String))
        { 
           JOptionPane.showMessageDialog( null , "Could Not find a File" );
          
        
        }
        else
        {
           Result.setText( (String)Res);
           if(ValueRecipient != null)
              ValueRecipient.setValue( (String )Res); 
           
        }
        
      }
      @Override
      public void setRecipient(IParameter param)
      {

         ValueRecipient = param;
         
      }
      
   }

}
