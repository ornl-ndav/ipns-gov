/* 
 * File: EnableDisableActionListener.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.awt.*;
import javax.swing.*;

/**
 * A action listener to a toggle button that disables some components and
 * disables others depending on the state of this toggle button
 * 
 * @author ruth
 * 
 */
public class EnableDisableActionListener implements ActionListener
{

   Component[] TrueEnabledList;

   Component[] FalseEnabledList;

   Vector[]    DisabledSubComponents;

   /**
    * Constructor
    * 
    * @param TrueEnabledList
    * @param FalseEnabledList
    */
   public EnableDisableActionListener(Vector< Component > TrueEnabledList,
         Vector< Component > FalseEnabledList)
   {

      this.TrueEnabledList = this.FalseEnabledList = null;
      
      if ( TrueEnabledList != null && TrueEnabledList.size( ) > 0 )
         this.TrueEnabledList = TrueEnabledList.toArray( new Component[ 0 ] );
      
      if ( FalseEnabledList != null && FalseEnabledList.size( ) > 0 )
         this.FalseEnabledList = FalseEnabledList.toArray( new Component[ 0 ] );

      DisabledSubComponents = null;
   }

   public EnableDisableActionListener(Component[] TrueEnabledList,
         Component[] FalseEnabledList)
   {

      this.TrueEnabledList = TrueEnabledList;
      this.FalseEnabledList = FalseEnabledList;

      DisabledSubComponents = null;

   }

   @Override
   public void actionPerformed(ActionEvent arg0)
   {

      if ( !( arg0.getSource( ) instanceof JToggleButton ) )
         return;

      JToggleButton src = ( JToggleButton ) arg0.getSource( );
      
      if ( src.isSelected( ) )
      {

         Enable( TrueEnabledList , DisabledSubComponents );
         DisabledSubComponents = Disable( FalseEnabledList );

      } else
      {
         Enable( FalseEnabledList , DisabledSubComponents );
         DisabledSubComponents = Disable( TrueEnabledList );
      }

   }

   /**
    * Appends new object to the given vector( or newly created one if there is
    * no original). The original vector is changed and is also returned so the
    * Add's can be chained.
    * 
    * @param orig
    *           The original Vector. If null a new Vector will be created
    * 
    * @param newElement
    *           The new element that will be added to the original vector
    * 
    * @return The original vector with the new element added. NOTE the original
    *         vector is CHANGED.
    */
   public static Vector Add(Vector orig, Object newElement)
   {

      if ( orig == null )
         orig = new Vector( );

      orig.add( newElement );

      return orig;
   }

   private void Enable(Component[] list, Vector[] origDisabledList)
   {

      if ( list == null || list.length < 1 )
         return;

      if ( origDisabledList == null )
      {
         origDisabledList = new Vector[ list.length ];
         
         for( int i = 0 ; i < origDisabledList.length ; i++ )
            
            origDisabledList[i] = new Vector( );
      }

      for( int i = 0 ; i < list.length ; i++ )
      {
         if ( list[i] == null )
         {

         } else if ( list[i] instanceof Container )
         {
            Component[] kids = new Component[ ( ( Container ) list[i] )
                                                 .getComponentCount( ) ];

            for( int k = 0 ; k < kids.length ; k++ )
               
               kids[k] = ( ( Container ) list[i] ).getComponent( k );
            

            if ( kids != null )
               
               Enable( kids , origDisabledList );
            
            list[i].setEnabled( true );

         } else if ( origDisabledList[i].contains( list[i] ) )
         {
         } else
            list[i].setEnabled( true );
      }

   }

   
   private Vector[] Disable(Component[] list)
   {

      if ( list == null || list.length < 1 )
      {
         return new Vector[ 0 ];
      }
      
      Vector[] Res = new Vector[ list.length ];
      
      for( int i = 0 ; i < list.length ; i++ )
      {
         Res[i] = new Vector( );
         
         if ( list[i] == null )
         {

         } else if ( list[i] instanceof Container )
         {
            Component[] kids = new Component[ ( ( Container ) list[i] )
                                                    .getComponentCount( ) ];

            for( int k = 0 ; k < kids.length ; k++ )
               kids[k] = ( ( Container ) list[i] ).getComponent( k );

            Vector[] V;
            
            if ( kids != null )
            {
               V = Disable( kids );

               for( int k = 0 ; k < kids.length ; k++ )
                  
                  Res[i].addAll( V[k] );
            }

            list[i].setEnabled( false );

         } else if ( !list[i].isEnabled( ) )
         {
            
            Res[i].add( list[i] );
            
         } else
            
            list[i].setEnabled( false );

      }
      return Res;

   }
}
