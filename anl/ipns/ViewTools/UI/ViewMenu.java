
/*
 * $Id$
 *
 * $Log$
 * Revision 1.2  2001/07/31 16:07:56  neffk
 * added some System.out.println( ... ) statements to the default listener.
 *
 * Revision 1.1  2001/07/25 18:20:57  neffk
 * encapsulates a menu that displays and listens to various view
 * options.
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

  private static final String VIEW_M             = "View";
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
