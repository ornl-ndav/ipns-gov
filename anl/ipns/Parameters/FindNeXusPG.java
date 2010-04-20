package gov.anl.ipns.Parameters;

import gov.anl.ipns.Util.File.FileIO;
import gov.anl.ipns.Util.SpecialStrings.ErrorString;
import gov.anl.ipns.Util.Sys.*;
import gov.anl.ipns.ViewTools.Panels.GraphTagFrame;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class FindNeXusPG extends ButtonPG
{

   ButtonPressedListener ButtonListener;
   public FindNeXusPG()
   {

      super( "Find SNS NeXus File");
      ButtonListener = new ButtonPressedListener( this);
      super.addActionListener( ButtonListener);
      ButtonListener.setRecipient( this );
     
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
   
   class ButtonPressedListener implements java.awt.event.ActionListener,
                                          IhasWindowClosed, IReturnValue
   {

      FinishJFrame jf = null;
      ButtonPG  button_pg;
      FindNexJPanel    FindNexParams;
      IParameter ValueRecipient;
      
   
      public ButtonPressedListener( ButtonPG  button_pg)
      {
         this.button_pg = button_pg;
         FindNexParams = new FindNexJPanel();
      }
      
      public void actionPerformed(ActionEvent arg0)
      {
         if( jf == null )
         {
            jf = new FinishJFrame();
            jf.getContentPane( ).setLayout(  new GridLayout(1,1) );
          
           
            jf.getContentPane().add( FindNexParams);
            JPanel butPan = button_pg.getGUIPanel( false );
            Point P = GraphTagFrame.getPositionAbs( butPan );
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

         jf = null;
         
      }

      @Override
      public void setRecipient(IParameter ValueRecipient)
      {
        this.ValueRecipient = ValueRecipient;
        ValueRecipient.setValue("");// To get Data Type to be a String for this Parameter

        FindNexParams.setRecipient( ValueRecipient );
         
      }
      
      
      
      
   }
   
   class FindNexJPanel  extends JPanel implements IReturnValue, ActionListener
   {

      IParameter ValueRecipient;
      JTextField Run, Inst,Prop,Coll,Recurs;
      JLabel Result;
      FileChooserPanel filePick;
      JButton OK;
      public FindNexJPanel( )
      {
         super();
         ValueRecipient = null;
         BoxLayout bl = new BoxLayout( this,BoxLayout.Y_AXIS);
         setLayout( bl);
         JPanel TopPanel = new JPanel( new GridLayout( 3,4));
         Run = new JTextField(5);
         Inst = new JTextField(5);
         Prop = new JTextField(5);
         Coll = new JTextField(5);
         Recurs = new JTextField(5);
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
         Result = new JLabel();
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
    
        Object Res = FileIO.findNexusAtSNS(run, instrument, proposal,collect, 
             StartDir,"", recurs);
        if( !(Res instanceof String))
           JOptionPane.showMessageDialog( null , "Could Not find a File" );
        else
        {
           Result.setText( (String)Res);
           ValueRecipient.setValue( (String )Res); 
           
        }
        
      }
      @Override
      public void setRecipient(IParameter param)
      {

         
         
      }
      
   }

}
