/*
 * File: ViewMenu.java
 *
 * Copyright (C) 2001 Kevin Neff
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
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>

 *
 * $Log$
 * Revision 1.4  2004/01/22 02:05:38  bouzekc
 * Removed unused variables.
 *
 * Revision 1.3  2002/11/27 23:13:34  pfpeterson
 * standardized header
 *
 */
 
package DataSetTools.components.ui;

import DataSetTools.dataset.DataSet;
import DataSetTools.viewer.ViewManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


/**
 * builds a menu of different options for viewing data.  by default,
 * a simple listener is added to the menu.  to override this behavior,
 * call the constructor that takes (in addition to other things) an
 * ActionListener.
 */
public class ViewMenu
{
  private static final String IMAGE_VIEW_MI      = "Image View";
  private static final String SCROLL_VIEW_MI     = "Scrolled Graph View";
  private static final String SELECTED_VIEW_MI   = "Selected Graph View";
  private static final String THREED_VIEW_MI     = "3D View";

  private DataSet[]  dss = null;


  /**
   * builds a menu of each view option.  this constructor uses the default
   * ActionListener.
   */ 
  public void build( JMenu main_menu, 
                     DataSet[] dss )
  {
    build( main_menu, dss, new DefaultViewMenuListener() );
  }


  public void build( JMenu main_menu,
                     DataSet[] dss,
                     ActionListener listener )
  {
    main_menu.addActionListener( listener );

    this.dss = dss;

    JMenuItem image_mi  = new JMenuItem( IMAGE_VIEW_MI );
              image_mi.addActionListener( listener ); 
    JMenuItem scroll_mi = new JMenuItem( SCROLL_VIEW_MI );
              scroll_mi.addActionListener( listener );
    JMenuItem select_mi = new JMenuItem( SELECTED_VIEW_MI );  
              select_mi.addActionListener( listener );
    JMenuItem threed_mi = new JMenuItem( THREED_VIEW_MI );
              threed_mi.addActionListener( listener );

    main_menu.add( image_mi );
    main_menu.add( scroll_mi );
    main_menu.add( select_mi );
    main_menu.add( threed_mi );
  }


  /* 
   * listens to this menu and provides default actions for each
   * menu item.  note that this class is NOT a container.  all
   * containing is done by ViewMenu, which is responsible for
   * the details of knowing which DataSet objects to show.
   */
  class DefaultViewMenuListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      System.out.println( "ViewMenu option selected" ); 

      if(  e.getActionCommand().equals( ViewManager.IMAGE )  )
        System.out.println( "IMAGE viewer invoked" );

      if(  e.getActionCommand().equals( ViewManager.SCROLLED_GRAPHS )  )
        System.out.println( "SCROLLED_GRAPHS viewer invoked" );

      if(  e.getActionCommand().equals( ViewManager.SELECTED_GRAPHS )   )
        System.out.println( "SELECTED_GRAPHS viewer invoked" );

      if(  e.getActionCommand().equals( ViewManager.THREE_D )  )
        System.out.println( "THREE_D invoked" );
    }
  }

}
