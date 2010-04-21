/* 
 * File: CreateJMenuTree.java
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
package gov.anl.ipns.Util.Sys;

import java.awt.event.*;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.*;

/**
 * This class is used to create a tree under(or along side) a JMenu
 * 
 * @author ruth
 * 
 */
public class CreateJMenuTree
{

   String[]    PathNames;

   JMenuItem[] TailItems;

   String      JMenuTitle;

   Integer[]   SortPathNames;

   /**
    * Constructor
    * 
    * @param JMenuTitle
    *           The Title of the top JMenu if needed
    * 
    * @param PathNames
    *           The path after this JMenu where the following JMenuItems will be
    *           placed. The path/sub-paths are separated by commas
    * 
    * @param TailItems
    *           The JMenuItems to be placed at the tail. These should include
    *           their title and any necessary action listeners.
    */
   public CreateJMenuTree(String JMenuTitle, String[] PathNames,
         JMenuItem[] TailItems)
   {

      this.PathNames = PathNames;
      this.TailItems = TailItems;
      this.JMenuTitle = JMenuTitle;
      SortPathNames = new Integer[ 0 ];

      if ( PathNames == null || PathNames.length < 1 )
         return;

      SortPathNames = new Integer[ PathNames.length ];
      for( int i = 0 ; i < SortPathNames.length ; i++ )
         SortPathNames[i] = i;

      Arrays.sort( SortPathNames , new SComparator( this.PathNames ) );
   }

   /**
    * Creates a new JMenu filled with the Paths and the JMenuItems at the leaves
    * 
    * @return the new JMenu
    */
   public JMenu getJMenu()
   {

      JMenu menu = new JMenu( JMenuTitle );
      setUpJMenu( menu );
      
      return menu;
   }

   /**
    * Fills out an existing JMenu. Items will be added to the end.
    * 
    * @param TopMenu
    *           The parent of all the submenu's. NOTE: Its title will not be
    *           changed to the JMenutitle in the constructor.
    */
   public void setUpJMenu(JMenu TopMenu)
   {

      if ( TailItems == null || TailItems.length < 1 )
         return;

      JMenu[] prevJMenu = new JMenu[ 100 ];

      for( int i = 0 ; i < PathNames.length ; i++ )
      {
         int i1 = SortPathNames[i].intValue( );
         if ( i1 < TailItems.length )
         {
            String path = PathNames[i1];
            String[] subPaths = path.split( "," );
            int k = Match( prevJMenu , subPaths[0] );
            
            if ( k < 0 )
            {
               prevJMenu[0] = new JMenu( subPaths[0] );
               prevJMenu[1] = null;
               TopMenu.add( prevJMenu[0] );
               k = 0;
            }
            
            int si = 0;
            while( k < prevJMenu.length && prevJMenu[k] != null
                  && si < subPaths.length
                  && subPaths[si].equals( prevJMenu[k].getText( ) ) )
            {
               k++ ;
               si++ ;
            }
            // if( prevJMenu[k] == null)

            if ( si >= subPaths.length )
               
               prevJMenu[k - 1].add( TailItems[i1] );
            
            else
            {
               while( si < subPaths.length )
               {
                  prevJMenu[k] = new JMenu( subPaths[si] );
                  prevJMenu[k - 1].add( prevJMenu[k] );
                  k++ ;
                  si++ ;
               }
               
               prevJMenu[k - 1].add( TailItems[i1] );
            }

         }

      }

      // Now add the rest of the action listeners at level 1
      for( int i = PathNames.length ; i < TailItems.length ; i++ )
      {
         TopMenu.add( TailItems[i] );
      }
   }

   private int Match(JMenu[] prevJMenu, String match)
   {

      if ( prevJMenu == null )
         return -1;
      
      int i;
      for( i = 0 ; i < prevJMenu.length && prevJMenu[i] != null ; i++ )
         if ( !match.equals( prevJMenu[i].getText( ) ) )
            return i - 1;

      return i - 1;

   }

   public static JMenuItem getTestItem(int k)
   {

      JMenuItem res = new JMenuItem( "k=" + k );
      res.addActionListener( new XActionListener( k ) );
      return res;
   }

   /**
    * @param args
    */
   public static void main(String[] args)
   {

      String[] Menu =
      { "abc,ddd,fff" , "kkkl,abce" , "abc,ddd,www" , "cdef" , "kkkl,abce" };
      
      JMenuItem[] X = new JMenuItem[ 8 ];
      for( int i = 0 ; i < 8 ; i++ )
         X[i] = CreateJMenuTree.getTestItem( i + 2 );
      
      FinishJFrame jf = new FinishJFrame( "Test" );      
      jf.setSize( 300 , 400 );
      
      CreateJMenuTree CC = new CreateJMenuTree( "top" , Menu , X );
      
      JMenuBar menBar = new JMenuBar( );
      jf.setJMenuBar( menBar );
      menBar.add( CC.getJMenu( ) );
      
      WindowShower.show( jf );

   }

   class SComparator implements Comparator< Integer >
   {

      String[] strings;

      public SComparator(String[] strings)
      {

         this.strings = strings;
      }

      @Override
      public int compare(Integer arg0, Integer arg1)
      {

         if ( strings == null || strings.length < 1 )
            return 0;

         int i1 = arg0.intValue( );
         int i2 = arg1.intValue( );
         if ( i1 < 0 )
            if ( i2 < 0 )
               return 0;
            else
               return -1;
         else if ( i2 < 0 )
            return 1;
         if ( i1 > strings.length )
            if ( i2 > strings.length )
               return 0;
            else
               return 1;
         else if ( i2 > strings.length )
            return -1;

         String s1 = strings[i1];
         String s2 = strings[i2];
         if ( s1 == null )
            if ( s2 == null )
               return 0;
            else
               return -1;
         else if ( s2 == null )
            return 1;

         return s1.compareTo( s2 );

      }

   }

}

class XActionListener implements ActionListener
{

   int k;

   public XActionListener(int k)
   {

      this.k = k;
   }

   public void actionPerformed(ActionEvent evt)
   {

      JOptionPane.showMessageDialog( null , "K =" + k );
   }
}