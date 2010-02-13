/* 
 * File: StringListChoiceViewer.java
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
 *  $Author$:
 *  $Date$:            
 *  $Rev$:
 */
package gov.anl.ipns.ViewTools.Panels;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author ruth
 * 
 */
public class StringListChoiceViewer extends JPanel 
                                    implements ChangeListener,
                                               ActionListener
{

   String[]    Choices;

   boolean     showSelectButton;

   JTextArea   text;

   JSpinner    spinner;
   
   JLabel      Nitems;

   int         selected;

   JScrollPane scr;

   /**
    * Constructor
    * @param Choices  The array of Strings to present
    * 
    * @param nrows    Number of rows in display to show the Strings
    *                 It will be scrolled if necessary
    *                 
    * @param ncols    Number of columns in display to show the Strings
    *                 It will be scrolled if necessary
    *                 
    * @param showSelectButton  If true the Select button will be shown
    */
   public StringListChoiceViewer(String[] Choices, 
                                int       nrows, 
                                int       ncols,
                                boolean   showSelectButton)
   {
      super( );
      
      setLayout( new BorderLayout( ) );
      
      this.Choices = Choices;
      this.showSelectButton = showSelectButton;
      text = new JTextArea( nrows , ncols );
      
      spinner = new JSpinner( 
             new SpinnerNumberModel( 1 , 1 , Choices.length , 1 ) );
      spinner.addChangeListener( this );
      
      text.setText( Choices[0] );
      text.setFont( gov.anl.ipns.ViewTools.UI.FontUtil.MONO_FONT );
      
      selected = -1;
      
      JButton select = null;
      if ( showSelectButton )
      {
         select = new JButton( "Select" );
         select.addActionListener( this );
      }
      
      Nitems = new JLabel("List Size="+ Choices.length);
      // -------------------------------------
      JPanel panel = new JPanel( );
      BoxLayout bl = new BoxLayout( panel , BoxLayout.X_AXIS );
      panel.setLayout( bl );
      
      panel.add( Nitems);
      panel.add( Box.createHorizontalGlue( ) );
      if ( select != null )
         panel.add( select );
      
      panel.add( new JLabel( "Choice" , SwingConstants.RIGHT ) );
      
      panel.add( spinner );
      
      add( panel , BorderLayout.NORTH );
      
      // -------------------------------
      
      scr = new JScrollPane( text );
      add( scr , BorderLayout.CENTER );
   }

   
   /**
    * Sets a new List of Strings to cycle through
    * 
    * @param Choices  The list of Strings
    */
   public void setNewStringList(String[] Choices)
   {

      this.Choices = Choices;
      spinner.setModel( new SpinnerNumberModel( 1 , 1 , Choices.length , 1 ) );
      text.setText( Choices[0] );
      selected = -1;
      Nitems.setText( "List Size="+Choices.length );
   }

   /**
    * The last choice from the Select button. It Starts at 0 event
    * though the spinner starts at 1.
    * 
    * @return The last choice from the Select button. Starting at 0.
    */
   public int getSelectedChoice()
   {

      return selected;
   }

   /**
    * The index-1 of the viewed String. It Starts at 0 event 
    * though the spinner starts at 1.
    * 
    * @return The index of the viewed String. Starting at 0.
    */
   public int getLastViewedChoice()
   {

      return ( ( Number ) spinner.getValue( ) ).intValue( ) - 1;
   }

   @Override
   public void actionPerformed(ActionEvent arg0)
   {

      selected = ( ( Integer ) spinner.getValue( ) ).intValue( ) - 1;

   }

   @Override
   public void stateChanged(ChangeEvent arg0)
   {

      int show = ( ( Integer ) spinner.getValue( ) ).intValue( ) - 1;
      text.setText( Choices[show] );
      scr.getViewport( ).setViewPosition( new Point( 0 , 0 ) );
   }

   /**
    * @param args
    */
   public static void main(String[] args)
   {

      String[] choices =
      { "abc" , "def" , "ghi" , "jkl" };
      StringListChoiceViewer L = new StringListChoiceViewer( choices , 5 , 12 ,
            false );

      // JOptionPane.showMessageDialog( null , L );
      // System.out.println( L.getSelectedChoice()+","+L.getLastViewedChoice());
      JFrame jf = new JFrame( );
      jf.getContentPane( ).setLayout( new GridLayout( 1 , 1 ) );
      jf.getContentPane( ).add( L );
      jf.setSize( 300 , 500 );
      jf.setVisible( true );

   }

}
