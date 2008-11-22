/* 
 * File: PeakArrayPanels.java
 *
 * Copyright (C) 2008, Ruth Mikkelson
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
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 */

package gov.anl.ipns.ViewTools.Panels.PeakArrayPanel;

import gov.anl.ipns.Util.Sys.WindowShower;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import java.awt.*;
import java.io.*;

/**
 * This class creates a JFrame to cycle through a series of PeakDisplayPanels
 * 
 * @author Ruth
 * @see PeakDisplayPanels
 */
public class PeakArrayPanels extends JFrame {



   private Vector< PeaksDisplayPanel > PeakDisplays;

   private Vector< String >            IDs;

   private Vector< String >            Titles;

   private Integer[]                   sortInfo;

   String[]                            SpinnerValues;

   JPanel                              Comp;


   /**
    * Constructor
    * 
    * @param Title
    *           The Title for the JFrame
    */
   public PeakArrayPanels( String Title ) {

      super( Title );
      PeakDisplays = new Vector< PeaksDisplayPanel >();
      IDs = new Vector< String >();
      Titles = new Vector< String >();
      sortInfo = null;

      Dimension D = getToolkit().getScreenSize();
      D.width /= 2;
      D.height /= 2;
      setSize( D );
      setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
      Comp = null;

   }


   /**
    * Adds one new PeaksDisplayPanel that will be cycle.
    * 
    * @param panel
    *           The PeaksDisplayPanel
    *           
    * @param Title
    *           The Title for this panel
    *           
    * @param ID
    *           The ID for this panel
    */
   public void addPanel( PeaksDisplayPanel panel , String Title , String ID ) {

      PeakDisplays.add( panel );
      IDs.add( ID );
      Titles.add( Title );
   }


   /**
    * Displays the JFrame that will cycle through the PeaksDisplayPanels
    * 
    * @param TitlePrompt
    *           The prompt describing what the title of the Panel means
    *           
    * @param IDPrompt
    *           The prompt describing what the ID of the Panel means.
    */
   public void display( String TitlePrompt , String IDPrompt ) {

      sortInfo = new Integer[ IDs.size() ];
      for( int i = 0 ; i < sortInfo.length ; i++ )
         sortInfo[ i ] = i;

      SpinnerValues = new String[ IDs.size() ];
      for( int i = 0 ; i < SpinnerValues.length ; i++ ) {

         String Val = TitlePrompt + ":" + Titles.elementAt( i ) + "--"
                  + IDPrompt + ":" + IDs.elementAt( i )+" ";
         SpinnerValues[ i ] = Val;
      }

      java.util.Arrays.sort( sortInfo , 0 , sortInfo.length ,
               new ThisComparator( SpinnerValues ) );
      
      java.util.Arrays.sort( SpinnerValues , 0 , SpinnerValues.length );
     
      this.setLayout( new BorderLayout() );
      
      JPanel base = new JPanel();
      base.setLayout( new BoxLayout( base, BoxLayout.X_AXIS) );
      JSpinner spinner = new JSpinner( new SpinnerListModel( SpinnerValues ) );
      spinner.addChangeListener( new SpinnerChangeListener( this ) );
      base.add( Box.createHorizontalGlue());
      base.add( spinner );
      base.add( Box.createHorizontalGlue());
      

      getContentPane().add( base , BorderLayout.SOUTH );

      spinner.setValue( SpinnerValues[ 0 ] );
      Comp = new JPanel( new GridLayout( 1 , 1 ) );
      PeaksDisplayPanel panel = PeakDisplays.elementAt( 0 );
      Comp.add( new PeaksDisplayPanel( panel.getPeakInfos() ) );

      getContentPane().add( Comp , BorderLayout.CENTER );


      invalidate();

      WindowShower.show( this );


   }

  
   class SpinnerChangeListener implements ChangeListener , Serializable {



      JFrame jf;


      /*
       * (non-Javadoc)
       * 
       * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
       */

      public void stateChanged( ChangeEvent e ) {

         if( Comp == null )
            return;
         
         if( ! ( e.getSource() instanceof JSpinner ) )
            return;
         
         String Value = ( (JSpinner) e.getSource() ).getValue().toString();
         int i = java.util.Arrays.binarySearch( SpinnerValues , Value );
         
         if( i < 0 || i >= sortInfo.length )
            return;

         int k = sortInfo[ i ];


         Comp.removeAll();
         Comp.setLayout( new GridLayout( 1 , 1 ) );
         PeaksDisplayPanel panel = PeakDisplays.elementAt( k );
         panel = new PeaksDisplayPanel( panel.getPeakInfos() );
         Comp.add( panel );

         jf.setVisible( true );

      }

      

      public SpinnerChangeListener( JFrame jf ) {

         this.jf = jf;
      }

   }

   /**
    * Implements rank sort on an array of Strings
    * 
    * @author Ruth
    * 
    */
   class ThisComparator implements Comparator {



      String[] ID;


      public ThisComparator( String[] IDs ) {

         this.ID = IDs;
      }


      /*
       * (non-Javadoc) The two objects to compare represent indices in a second
       * array. The values in the second array are compared.
       * 
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */

      public int compare( Object o1 , Object o2 ) {

         if( o1 == null )
            return 1;
         
         if( o2 == null )
            return - 1;
         
         if( ! ( o1 instanceof Integer ) )
            return 1;
         
         if( ! ( o2 instanceof Integer ) )
            return - 1;
         

         int n1 = ( (Integer) o1 ).intValue();
         int n2 = ( (Integer) o2 ).intValue();
         if( n1 < 0 )
            return 1;
         
         if( n2 < 0 )
            return - 1;
         
         if( n1 >= IDs.size() )
            return 1;
         
         if( n2 >= IDs.size() )
            return - 1;
         
         return ID[ n1 ].compareTo( ID[ n2 ] );
      }

   }


   /**
    * Utility to create this JFrame a Display the Peaks. The PeaksDisplayPanels
    * are serialized and stored in files in the specified Directory. The names
    * of the files start with the specified prefix and have the specified
    * extension. Only files created after currentTime will be used. Also, part
    * of the filename after the last "_" will represent an ID and the part of
    * the name of the file before the last "_" will represent a title associated
    * with the PeaksDisplay panel in that file.
    * 
    * @param Title
    *           Title to appear on the JFrame that will pop up
    *           
    * @param Directory
    *           The Directory where the serialized PeaksDisplayPanels are
    *           stored. If null it will be in {user.hom}/ISAW/tmp/
    *           
    * @param prefix
    *           The prefix these filenames will have. If null this will be
    *           "PeakV"
    *           
    * @param extension
    *           The extension with the "." that these file will have. If null it
    *           will ".pvw"
    *           
    * @param currentTime
    *           The time in milliseconds since January 1, 1970 UTC. Only files
    *           created after this time will be considered.
    *           
    */
   public static void DisplayPeaks( String Title , String Directory ,
            String prefix , String extension , long currentTime ) {
      
      boolean defaultDirectory = false;
      if( Directory == null ){
         Directory = System.getProperty( "user.home" );
         defaultDirectory = true;
      }
      
      Directory = Directory.replace( '\\' , '/' );
      if( ! Directory.endsWith( "/ " ) )
         Directory += "/";
      
      if( defaultDirectory)
         Directory += "ISAW/tmp/";
      
      if( prefix == null )
         prefix = "PeakV";
      
      if( extension == null )
         extension = ".pvw";

      File[] Files = ( new File( Directory ) ).listFiles();
      if( Files == null )
         return;
      
      PeakArrayPanels panels = new PeakArrayPanels( Title );
      
      for( int i = 0 ; i < Files.length ; i++ )
         if( ! Files[ i ].isDirectory() ) {
            
            String filename = Files[ i ].getName();
            
            if( filename != null && filename.startsWith( prefix )
                     && filename.endsWith( extension )
                     && currentTime < Files[ i ].lastModified() )
               try {
                  
                  ObjectInputStream inp = new ObjectInputStream(
                           new FileInputStream( Files[ i ] ) );
                  
                  PeaksDisplayPanel disp = (PeaksDisplayPanel) inp.readObject();
                  filename = filename.substring( 0 , filename.length()
                           - extension.length() );
                  
                  int k1 = filename.lastIndexOf( "_" );
                  String ID = null;
                  
                  if( k1 >= 0 )
                     ID = filename.substring( k1 + 1 );
                  
                  else {
                     ID = null;
                     k1 = filename.length() - 1;
                  }
                  
                  int k2 = filename.lastIndexOf( "/" , k1 );
                  
                  String title = null;
                  if( k2 < 0 )
                     k2 = - 1;
                  
                  title = filename.substring( k2 + 1 , k1 );
                  panels.addPanel( disp , title , ID );
                  
               }
               catch( Exception ss ) {
                
                  JOptionPane.showMessageDialog( null ,
                           "Cannot read Peak images for "
                                    + Files[ i ].toString() );

               }
         }
      
      panels.display( "File" , "Detector" );


   }


   /**
    *  Will Display Peaks whose files are in default directory with default prefix 
    *  and extension
    * @param args   Not used
    */
   public static void main( String[] args ) {

      PeakArrayPanels.DisplayPeaks( "Test" , null , null , null , - 1 );

   }

}
