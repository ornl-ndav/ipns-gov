/*
 * File: ThreeDAttributeJMenu.java
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
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author:  $
 */

package gov.anl.ipns.ViewTools.UI;

import gov.anl.ipns.ViewTools.Panels.Graph.GraphJPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;
import java.util.Vector;

/**
 * Creates a JMenu  that allows a user to specify color, size, and fill 
 * attributes of objects in a graphical system which has the method
 * drawObjects
 * 
 * @author Ruth
 *
 */
public class ThreeDAttributeJMenu extends JMenu implements ActionListener {

   private String[]          FillMenuLabels;

   //Default Values
   private static String[]   FillMenLabels     = {
                          "No Fill" , 
                          "Hollow Fill" , 
                          "Filled" , 
                          "Star" , 
                          "Cross" , 
                          "Plus" ,
                          "Box" , 
                          "Dot" , 
                          "Bar"
                                               };

   //Default values
   private int[]             FillMenuConstants = {
            IThreeD_drawObject.NO_FILL , IThreeD_drawObject.NO_FILL + 1 ,
            IThreeD_drawObject.NO_FILL + 2 ,
            IThreeD_drawObject.FILLED_FILL + GraphJPanel.STAR ,
            IThreeD_drawObject.FILLED_FILL + GraphJPanel.CROSS ,
            IThreeD_drawObject.FILLED_FILL + GraphJPanel.PLUS ,
            IThreeD_drawObject.FILLED_FILL + GraphJPanel.BOX ,
            IThreeD_drawObject.FILLED_FILL + GraphJPanel.DOT ,
            IThreeD_drawObject.FILLED_FILL + GraphJPanel.BAR

                                               };

   Float[]                   SizeList;                      //List of sizes to choose from

   float                     stepSize;

   float                     minSize;

   float                     size;                         //Last chosen size

   private JMenuItem         ColorMenuItem;

   private Color             color             = Color.red;

   Vector< Vector< Object >> pickIDs;

   Vector< String >          PickIDLabels;

   JMenu                     Fill , 
                             Objects;

   JMenuItem                 OK , 
                             Size;

   IThreeD_drawObject        DrawObjectImplementor;


   /**
    * Constructor for the JMenu  and its action listener.  An OK JMenuItem
    *     invokes the action Listener.
    * @param DrawObjectImplementor  The implementor of the DrawObject method
    * @param JMenuLabel   The label for this JMenu
    * @param FillLabel    The label for the fill type JMenu selection
    * @param SizeLabel    The label for the JMenu size selections
    * @param ColorLabel   The label for the Color JMenu. If null, no Color 
    *                     choice will appear.
    * @param pickIDs      The user defined ID's  for each of the sets of 
    *                      objects in this system. If null, the pickIDLabels
    *                      will be considered the pickIDs. Should be a Vector
    *                      of Vectors
    * @param pickIDLabels The labels corresponding to each of the pickIDs above.
    *                     These appear in the Objects JMenu list 
    */
   public ThreeDAttributeJMenu( IThreeD_drawObject        DrawObjectImplementor,
                                String                    JMenuLabel, 
                                String                    FillLabel, 
                                String                    SizeLabel,
                                String                    ColorLabel, 
                                Vector< Vector< Object >> pickIDs,
                                Vector< String >          pickIDLabels ) {

      this( DrawObjectImplementor , JMenuLabel , FillLabel , FillMenLabels ,
               SizeLabel , 1f , 6f , 1f , ColorLabel , pickIDs , pickIDLabels );
   }


   /**
    * Constructor for the JMenu  and its action listener.  An OK JMenuItem
    *     invokes the action Listener.
    * @param DrawObjectImplementor  The implementor of the DrawObject method
    * @param JMenuLabel   The label for this JMenu
    * @param FillLabel    The label for the fill type JMenu selection
    * @param FillTypes    the user defined names for Fills. If null, there is
    *                     no fillable types.
    * @param SizeLabel    The label for the JMenu size selections
    * @param minSize      minimum size. If NaN or > maxSize no size selections are allowed
    * @param maxSize      maximum size. If NaN or < minSize no size selections are allowed
    * @param stepSize     stepSize. If <=0 no size selections are allowed
    * @param ColorLabel   The label for the Color JMenu
    * @param pickIDs      The user defined ID's  for each of the sets of 
    *                      objects in this system. If null, the pickIDLabels
    *                      will be considered the pickIDs. Should be a Vector
    *                      of Vectors
    * @param pickIDLabels The labels corresponding to each of the pickIDs above.
    *                     These appear in the Objects JMenu list 
    */
   public ThreeDAttributeJMenu( IThreeD_drawObject        DrawObjectImplementor,
                                String                    JMenuLabel, 
                                String                    FillLabel, 
                                String[]                  FillTypes,
                                String                    SizeLabel, 
                                float                     minSize, 
                                float                     maxSize, 
                                float                     stepSize,
                                String                    ColorLabel, 
                                Vector< Vector< Object >> pickIDs,
                                Vector< String >          pickIDLabels ) {


      super( JMenuLabel );
      this.DrawObjectImplementor = DrawObjectImplementor;

      // ------ Fill--------
      if( FillTypes != null ) {
         Fill = new JMenu( ChangeNullValue( FillLabel , "Fill Type" ) );
         if( FillTypes != null && FillTypes.length > 0 ) {
            FillMenuLabels = FillTypes;
            if( FillTypes != FillMenLabels ) {
               FillMenuConstants = new int[ FillTypes.length ];
               for( int i = 0 ; i < FillTypes.length ; i++ )
                  FillMenuConstants[ i ] = i;
            }
         }
         ButtonGroup FillBG = new ButtonGroup();
         for( int i = 0 ; i < FillMenuLabels.length ; i++ ) {
            JCheckBoxMenuItem jmu = new JCheckBoxMenuItem( FillMenuLabels[ i ] );
            FillBG.add( jmu );
            Fill.add( jmu );
            if( i == 0 )
               jmu.setSelected( true );
         }

         add( Fill );
      }
      else
         Fill = null;


      //------- Size -------

      if( Float.isNaN( minSize ) || Float.isNaN( maxSize )
               || Float.isNaN( stepSize ) || minSize >= maxSize
               || stepSize <= 0 ) {
         SizeList = null;
         this.minSize = Float.NaN;
         this.stepSize = Float.NaN;
         this.size = 1;
         Size = null;
      }
      else {
         Size = new JMenuItem( ChangeNullValue( SizeLabel , "Size" ) );
         SizeList = new Float[ (int) ( ( maxSize - minSize ) / stepSize ) + 1 ];
         SizeList[ 0 ] = minSize;
         for( int i = 1 ; i < SizeList.length ; i++ )
            SizeList[ i ] = SizeList[ i - 1 ] + stepSize;
         this.size = minSize;
         this.minSize = minSize;
         Size.addActionListener( this );
         add( Size );
      }


      //--------Color --------
      if( ColorLabel != null ) {
         ColorMenuItem = new JMenuItem( ChangeNullValue( ColorLabel , "Color" ) );
         ColorMenuItem.addActionListener( this );
         add( ColorMenuItem );
      }
      else {
         ColorMenuItem = null;
         color = Color.red;
      }

      //------------- Objects -------------
      this.pickIDs = pickIDs;
      this.PickIDLabels = pickIDLabels;
      if( this.pickIDs == null && this.PickIDLabels != null ) {
         this.pickIDs = new Vector( this.PickIDLabels.size() );
         for( int i = 0 ; i < this.PickIDLabels.size() ; i++ ) {
            Vector V = new Vector();
            V.addElement( this.PickIDLabels.elementAt( i ) );
            this.pickIDs.addElement( V );
         }
      }
      if( this.PickIDLabels == null && this.pickIDs != null ) {
         this.PickIDLabels = new Vector< String >( this.pickIDs.size() );
         for( int i = 0 ; i < this.pickIDs.size() ; i++ )
            this.PickIDLabels.addElement( "" + ( i + 1 ) );
      }

      Objects = null;
      if( this.PickIDLabels != null ) {
         Objects = new JMenu( "Objects" );
         for( int i = 0 ; i < this.PickIDLabels.size() ; i++ ) {
            JCheckBoxMenuItem jmc = new JCheckBoxMenuItem( this.PickIDLabels
                     .elementAt( i ).toString() );
            Objects.add( jmc );

         }


         add( Objects );


      }

      add( new JSeparator() );

      OK = new JMenuItem( "OK" );
      add( OK );
      OK.addActionListener( this );
   }


   private String ChangeNullValue( String newVal , String valIfNull ) {

      if( newVal != null && newVal.length() > 1 )
         return newVal;
      return valIfNull;
   }


   private String FindSubMenuSelected( JMenu jm ) {

      for( int i = 0 ; i < jm.getItemCount() ; i++ )
         if( jm.getItem( i ).isSelected() )
            return jm.getItem( i ).getActionCommand();

      return null;
   }


   private int Search( String[] list , String item ) {

      for( int i = 0 ; i < list.length ; i++ )
         if( list[ i ].equals( item ) )
            return i;
      return - 1;
   }


   /**
    *  Handles ColorMenu selection, Size Selection and OK selection
    */
   public void actionPerformed( ActionEvent e ) {

      if( e.getSource().equals( ColorMenuItem ) ) {
         Color CC = JColorChooser
                  .showDialog( null , "Select Color" , Color.red );
         if( CC != null )
            color = CC;
         return;
      }
      if( Size != null && e.getSource().equals( Size ) ) {
         Float F = (Float) JOptionPane.showInputDialog( null , "Enter size" ,
                  "Size" , JOptionPane.QUESTION_MESSAGE , null , SizeList ,
                  size );
         if( F == null )
            return;
         size = F.floatValue();
         return;

      }
      if( e.getActionCommand().equals( "OK" ) ) {
         int fillType = 0;
         if( Fill != null ) {
            String S = FindSubMenuSelected( Fill );
            if( S == null )
               S = FillMenuLabels[ 0 ];
            int i = Search( FillMenuLabels , S );
            if( i < 0 || i >= FillMenuConstants.length )
               i = 0;
            fillType = FillMenuConstants[ i ];
         }


         Vector pick_ids = null;
         if( Objects != null ) {
            pick_ids = new Vector();
            for( int i = 0 ; i < Objects.getItemCount() ; i++ ) {
               if( Objects.getItem( i ).isSelected() )
                  pick_ids.addAll( pickIDs.elementAt( i ) );
            }

         }

         String Res = DrawObjectImplementor.drawObjects( fillType , size ,
                  color , pick_ids );
         if( Res != null && Res.length() < 1 )
            javax.swing.JOptionPane.showMessageDialog( null , "Error : " + Res );
      }

   }


   /**
    * Test program for this class
    * @param args
    */
   public static void main( String[] args ) {

      String[] ObjectPrompts = {
               "abc" , "def" , "ghi" , "jkl"
      };
      ThreeD_drawObjectImplementor threeD = new ThreeD_drawObjectImplementor();
      Vector< String > ObjPrompts = new Vector< String >();
      for( int i = 0 ; i < 4 ; i++ )
         ObjPrompts.add( ObjectPrompts[ i ] );
      ThreeDAttributeJMenu Jmenu3D = new ThreeDAttributeJMenu( threeD ,
               "3D attr" , "Fill" , ObjectPrompts , "Size" , 1f , 7.2f , 2f ,
               "Color1" , null , ObjPrompts );

      JFrame jf = new JFrame( "Test" );
      JMenuBar jmb = new JMenuBar();
      jmb.add( Jmenu3D );
      jf.setJMenuBar( jmb );
      jf.setSize( 300 , 300 );
      jf.setVisible( true );
   }


}


/**
 * For Testing purposes only
 * @author Ruth
 *
 */
class ThreeD_drawObjectImplementor implements IThreeD_drawObject {



   public String drawObjects( int fillType , float size , java.awt.Color color ,
            Vector pick_ids ) {

      System.out.println( "in drawObjects fillType, size, color =" + fillType
               + "," + size + "," + color );
      System.out.println( "pickIDs are " );
      if( pick_ids != null )
         for( int i = 0 ; i < pick_ids.size() ; i++ )
            System.out.println( pick_ids.elementAt( i ) );
      System.out.println( "-------------------------" );
      return null;


   }
}
