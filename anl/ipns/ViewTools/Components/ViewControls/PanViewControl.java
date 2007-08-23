/*
 * File: PanViewControl.java
 *
 * Copyright (C) 2003-2005, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.30  2007/08/23 21:06:32  dennis
 *  Removed unused imports.
 *
 *  Revision 1.29  2007/07/20 02:51:57  dennis
 *  No longer sends two message when the region is changed, but
 *  for now just forwards the message from the translation overlay.
 *  This should eventually be changed so it just sends a
 *  VALUE_CHANGED message.
 *  Removed ignore_change flag that is no longer used.
 *  Removed some redundant calls to setting global or local bounds.
 *  The setLocalBounds() method is now private, since users of this
 *  control should use the generic setControlValue() method.
 *
 *  Revision 1.28  2007/07/11 19:49:35  dennis
 *  Removed some extra calls to repaint when the bounds were
 *  adjusted from outside, since these caused extra repaints and
 *  would redraw the thumbnail image over the pan cursor, when
 *  the user zoomed in using the main JPanel.
 *
 *  Revision 1.27  2007/06/15 18:18:52  rmikk
 *  Checked for a null thumbnail image
 *
 *  Revision 1.26  2007/06/15 16:55:18  dennis
 *  Replaced paint() with paintComponent() and removed call to
 *  super.paint();
 *  When data is refreshed this no longer finds the uppermost parent
 *  panel and repaints that panel.  This caused components that used
 *  this to be redrawn too many times, and caused flickering as the
 *  components were laid out.
 *
 *  Revision 1.25  2007/06/13 15:28:22  rmikk
 *  Added a variable,  makeNewPanImage, that is used to get a new image
 *  whenever the current image in the Pan View is out of date.
 *
 *  Revision 1.24  2007/02/12 04:35:00  dennis
 *  The refreshData() method is no longer executed completely, if
 *  the PanViewControl is not currently shown.  This breaks a cycle
 *  of method calls that occured when the refreshData() method
 *  proceeded to get the thumbnail image and attempt to draw it
 *  when the control was not used.  This caused an infinite loop in
 *  the SANDWedgeViewer and HKLSliceViewer, since those viewers did
 *  not show the PanViewControl.
 *
 *  Revision 1.23  2005/07/28 15:50:20  kramer
 *
 *  Modified to support ContourJPanels.  The refreshData() and
 *  setImageDimension() methods were modified to identify a ContourJPanel and
 *  invoke the correct methods to get a thumbnail and the image's dimensions.
 *
 *  Revision 1.22  2005/06/17 20:15:28  kramer
 *
 *  Modified so that when ImageJPanel2.getThumbnail() method is called the
 *  thumbnail is forced to be redrawn (by giving the method
 *  'forceRedraw=true').  This fixes the problem that if the color scale is
 *  changed on the image it doesn't change on the thumbnail.  However, a
 *  better solution should be found.
 *
 *  Revision 1.21  2005/05/25 20:28:42  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.20  2005/03/28 05:57:31  millermi
 *  - Added copy() which will make an exact copy of the ViewControl.
 *
 *  Revision 1.19  2005/03/20 05:37:00  millermi
 *  - Modified main() to reflect parameter changes to
 *    ControlManager.makeManagerTestWindow().
 *
 *  Revision 1.18  2005/03/09 22:37:55  millermi
 *  - Added methods get/setControlValue() and messaging of VALUE_CHANGED
 *    to enable controls to be linked.
 *  - Added "cm" as parameter to main() to test control with the
 *    ControlManager.
 *  - Now uses ImageJPanel2 instead of ImageJPanel for displaying image.
 *
 *  Revision 1.17  2005/01/20 23:37:16  millermi
 *  - Added super.paint() to ThumbnailJPanel.paint().
 *
 *  Revision 1.16  2004/08/04 18:54:23  millermi
 *  - Added code so selection would not disappear.
 *  - Added messaging Strings from TranslationJPanel.
 *  - Added enableStretch() and isStretchEnabled() to turn resizing
 *    of the viewport on/off.
 *
 *  Revision 1.15  2004/04/05 02:36:43  millermi
 *  - Fixed bug that scaled the image in the refreshData() when
 *    the actual panel_size should have been used.
 *
 *  Revision 1.14  2004/03/15 23:53:54  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.13  2004/03/12 03:05:12  millermi
 *  - Changed package, fixed imports.
 *
 *  Revision 1.12  2004/03/02 22:12:52  dennis
 *  Now only calls update if the PanViewControl "isShowing()".  This
 *  fixes a problem where the PanViewControl would "flash" on the
 *  screen when it state was changed, even though it was on a tabbed
 *  pane that was currently hidden behind another tabbed pane.
 *
 *  Revision 1.11  2004/02/06 20:26:09  millermi
 *  - Added ObjectState key and methods.
 *
 *  Revision 1.10  2004/01/05 18:14:06  millermi
 *  - Replaced show()/setVisible(true) with WindowShower.
 *  - Removed excess imports.
 *
 *  Revision 1.9  2003/12/30 23:17:47  millermi
 *  - Added setMaximumSize(200,200) to limit the size of the pan
 *    control to 200 x 200.
 *
 *  Revision 1.8  2003/12/20 04:42:50  millermi
 *  - changed super.repaint() in refreshData() to update(). This
 *    was needed to consistently paint the overlay over the image.
 *
 *  Revision 1.7  2003/12/17 20:32:26  millermi
 *  - made refreshData() private, now is called by the repaint()
 *    method which is overloaded.
 *  - Fixed bug which did not allow the image to refresh properly.
 *
 *  Revision 1.6  2003/11/25 23:33:18  millermi
 *  - Bug Fix - panel_size in refreshData is set to the
 *    size of the containing panel and not the size of
 *    the whole PanViewControl.
 *
 *  Revision 1.5  2003/11/21 01:27:29  millermi
 *  - Removed println() statements.
 *  - Fixed bug that caused image to be repainted over the cursor.
 *
 *  Revision 1.4  2003/11/21 00:43:03  millermi
 *  - replica of image is now an Image instead of a CoordJPanel.
 *  - Replaced methods that updated the logscale and colorscale
 *    with refreshData() since the Image doesn't know about
 *    these values.
 *
 *  Revision 1.3  2003/11/18 01:00:54  millermi
 *  - Changed CoordJPanel ObjectState key strings to keep consistent
 *    with changes made to the CoordJPanel.
 *
 *  Revision 1.2  2003/10/29 20:34:55  millermi
 *  -Added colorscale and logscale capabilities for the instances
 *   of ImageJPanels
 *  -Added ObjectState info
 *
 *  Revision 1.1  2003/10/27 08:47:48  millermi
 *  - Initial Version - This class was created to enable users
 *    panning options for images too large to view in the
 *    viewport.
 *
 */
 
 package gov.anl.ipns.ViewTools.Components.ViewControls;

 import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.Cursor.TranslationJPanel;
import gov.anl.ipns.ViewTools.Components.Transparency.TranslationOverlay;
import gov.anl.ipns.ViewTools.Components.TwoD.ArrayGenerator;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;
import gov.anl.ipns.ViewTools.Panels.Image.ImageJPanel2;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordJPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
 
/**
 * This view control is used to "pan" an image. Adding a PanViewControl
 * will cause a thumbnail of the image to appear in the control panel.
 */
public class PanViewControl extends ViewControl
{
 /**
  * This messaging String is sent out when either to local or global bounds
  * change by way of setLocalBounds() or ObjectState restoration.
  */
  public static final String BOUNDS_CHANGED = TranslationJPanel.BOUNDS_CHANGED;
 
 /**
  * "Bounds Moved" - This message String is used to tell listeners that
  * the "viewport" or local bounds have been translated.
  */ 
  public static final String BOUNDS_MOVED = TranslationJPanel.BOUNDS_MOVED;
  
 /**
  * "Bounds Resized" - This message String is used to tell listeners that
  * the "viewport" or local bounds have been resized.
  */ 
  public static final String BOUNDS_RESIZED = TranslationJPanel.BOUNDS_RESIZED;
  
 /**
  * "Cursor Changed" - This message String is used to tell listeners that
  * the mouse cursor has been changed due to boundry dragging.
  */
  public static final String CURSOR_CHANGED = TranslationJPanel.CURSOR_CHANGED;
  
 // ---------------------ObjectState Keys---------------------------------
 /**
  * "Overlay" - This constant String is a key for referencing the state
  * information about the width allocated for the label.
  * The value that this key references is of type String.
  */
  public static final String OVERLAY = "Overlay";
  
  private ThumbnailJPanel panel;       // thumbnail of actual CoordJPanel
  private Image panel_image;
  private CoordJPanel actual_cjp;      // reference to actual CoordJPanel
  private TranslationOverlay overlay;  // where the region outline is drawn.
  private double data_width = 0;
  private double data_height = 0;

  /**
   * Set to True if the data represented in the image has changed.
   */
  public boolean makeNewPanImage = false;
 /**
  * Constructor for creating a thumbnail of the CoordJPanel passed in.
  *
  *  @param  cjp The CoordJPanel that will be thumbnailed.
  */
  public PanViewControl(CoordJPanel cjp)
  {
    super("");
    panel = new ThumbnailJPanel();
    overlay = new TranslationOverlay();
    actual_cjp = cjp;
    overlay.addActionListener( new OverlayListener() );
    setLayout( new OverlayLayout(this) );
    setPreferredSize( new Dimension(0,150) );
    setMaximumSize( new Dimension(200,200) );
    add(overlay);
    add(panel);
    addMouseListener( new PanMouseAdapter() );
    // this listener will preserve the aspect ratio of the control
    addComponentListener( new MaintainAspectRatio() );
    setGlobalBounds( cjp.getGlobalWorldCoords() );
    refreshData();
  }
 
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */ 
  public ObjectState getObjectState( boolean isDefault )
  {
    ObjectState state = super.getObjectState(isDefault);
    state.insert( OVERLAY, overlay.getObjectState(isDefault) );
    return state;
  }
     
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    // Do nothing if state is null.
    if( new_state == null )
      return;
    // call setObjectState of ViewControl, sets title if one exists.
    super.setObjectState( new_state );
    Object temp = new_state.get(OVERLAY);
    if( temp != null )
    {
      overlay.setObjectState((ObjectState)temp);
    }
  }
  
 /**
  * This method will set the local bounds for the PanViewControl. This is
  * the same as calling setLocalBounds() only this method is used for generic
  * getting and setting of values.
  *
  *  @param  value Local bounds (zoomed area) of the panel.
  */
  public void setControlValue(Object value)
  {
    if( value == null || !(value instanceof CoordBounds) )
      return;
    setLocalBounds( (CoordBounds)value );
  }
  
 /**
  * Get local bounds of the panel.
  *
  *  @return Local bounds of the panel.
  */
  public Object getControlValue()
  {
    return getLocalBounds();
  }
  
 /**
  * This method will make an exact copy of the PanViewControl.
  *
  *  @return A new, identical instance of the control.
  */
  public ViewControl copy()
  {
    PanViewControl clone = new PanViewControl(actual_cjp);
    /*
    clone.setGlobalBounds(getGlobalBounds());
    clone.setLocalBounds(getLocalBounds());
    clone.enableStretch(isStretchEnabled());
    */
    clone.setObjectState( getObjectState(PROJECT) );
    return clone;
  }
  
 /**
  * Use this method to enable/disable the stretching ability of the viewport.
  *
  *  @param  can_stretch True to enable stretching, false to disable.
  */ 
  public void enableStretch( boolean can_stretch )
  {
    overlay.enableStretch(can_stretch);
  }
  
 /**
  * Use this method to find out if stretching is enabled.
  *
  *  @return True is enabled, false if disabled.
  */
  public boolean isStretchEnabled()
  {
    return overlay.isStretchEnabled();
  }
  
 /**
  * Get the local bounds of the thumbnail. These bounds represent the area
  * viewable by the user and are shown graphically by the cursor. 
  *
  *  @return Bounds of the viewable region.
  */ 
  public CoordBounds getLocalBounds()
  {
    return overlay.getLocalBounds();
  }  
  
 /**
  * Set the local bounds of the thumbnail. These bounds represent the area
  * viewable by the user and are shown graphically by the cursor. 
  *
  *  @param  lb Bounds of the viewable region.
  */ 
  private void setLocalBounds( CoordBounds lb )
  {
    CoordBounds global = getGlobalBounds();
    float x1 = lb.getX1();
    float x2 = lb.getX2();
    float y1 = lb.getY1();
    float y2 = lb.getY2();
    // Make sure local x1 and x2 are between global x1 and x2.
    if( global.getX1() < global.getX2() )
    {
      if( x1 < global.getX1() )
        x1 = global.getX1();
      if( x2 > global.getX2() )
        x2 = global.getX2();
    }
    else // x2 < x1, so opposite of above.
    {
      if( x1 > global.getX1() )
        x1 = global.getX1();
      if( x2 < global.getX2() )
        x2 = global.getX2();
    }
    // Make sure local y1 and y2 are between global y1 and y2.
    if( global.getY1() < global.getY2() )
    {
      if( y1 < global.getY1() )
        y1 = global.getY1();
      if( y2 > global.getY2() )
        y2 = global.getY2();
    }
    else // y2 < y1, so opposite of above.
    {
      if( y1 > global.getY1() )
        y1 = global.getY1();
      if( y2 < global.getY2() )
        y2 = global.getY2();
    }
    // Set clamped local bounds, which are stored in overlay.
    overlay.setLocalBounds( new CoordBounds(x1,y1,x2,y2) );

//  repaint();  DON'T CALL REPAINT HERE, SINCE setLocalBounds() will cause
//              a repaint!!!!
  }  
  
 /**
  * Get the global bounds of the thumbnail. These bounds represent the entire 
  * possible area viewable by the user. 
  *
  *  @return Bounds of the viewable region.
  */ 
  public CoordBounds getGlobalBounds()
  {
    return overlay.getGlobalBounds();
  } 
  
 /**
  * Set the global bounds of the thumbnail. These bounds represent the entire 
  * possible area viewable by the user. If the global bounds are changed, the
  * local bounds are reinitialized to the global bounds.
  *
  *  @param  gb Bounds of the viewable region.
  */ 
  public void setGlobalBounds( CoordBounds gb )
  {
    overlay.setGlobalBounds( gb.MakeCopy() );

//  repaint();  DON'T CALL REPAINT HERE, SINCE setGlobalBounds() will cause
//              a repaint!!!!
  }
  

 /**
  * This will overload the repaint method. This method calls refreshData()
  */
  public void repaint()
  {
    refreshData();
  }


 /*
  * Call this method to repaint the thumbnail whenever the actual image changes.
  * This method calls the repaint method, so calling refreshData() will 
  * correctly update the thumbnail.
  */ 
  private void refreshData()
  {
    if ( !isShowing() )
      return; 
    // System.out.println("refreshData.....");

    setImageDimension();
    setAspectRatio();

    boolean validPanel = false;
    if( actual_cjp instanceof ImageJPanel2 )
    { 
      validPanel = true;
      Dimension panel_size = panel.getSize();
      // Since panel is in the PanViewControl, the aspect ratio of the
      // control affects the aspect ratio of the image. Don't need to
      // alter the image size according to the aspect ratio.
      panel_image = ((ImageJPanel2)actual_cjp).getThumbnail( panel_size.width, 
                            panel_size.height, makeNewPanImage );
      if( panel_image != null) 
        makeNewPanImage = false;
    }
    else if ( actual_cjp instanceof ContourJPanel )
    {
       validPanel = true;
       Dimension panel_size = panel.getSize();
       panel_image = 
          ((ContourJPanel)actual_cjp).getThumbnail(panel_size.width,
                                                   panel_size.height);
    }
    
    if( validPanel && (panel_image != null) )
    {
      panel.setImage(panel_image);
      panel.repaint();
    }
  }
  
 /*
  * This method will find the dimension of the data we are working with.
  */ 
  private void setImageDimension()
  {
    if( actual_cjp instanceof ImageJPanel2 )
    {
      data_width = (double)((ImageJPanel2)actual_cjp).getNumDataColumns();
      data_height = (double)((ImageJPanel2)actual_cjp).getNumDataRows();
    }
    else if ( actual_cjp instanceof ContourJPanel )
    {
       data_width = ((ContourJPanel)actual_cjp).getData().getNumColumns();
       data_height = ((ContourJPanel)actual_cjp).getData().getNumRows();
    }
  }
 
 /*
  * This method uses aspect ratio of height/width to keep the ratio of the
  * panviewcontrol similar to that of the data.
  */ 
  private void setAspectRatio()
  { 
    double aspect_ratio = data_height / data_width;
    
    // limit aspect ratio between 2/3 and 3/2
    if( aspect_ratio < (2f/3f) )
      aspect_ratio = 2.0/3.0;
    else if( aspect_ratio > (3f/2f) )
      aspect_ratio = 3.0/2.0;
     
    double pan_width = getWidth();
    double pan_height = pan_width * aspect_ratio;
    //System.out.println("Aspect Ratio: " + actual_cjp.getHeight() + "/" +
    //                   actual_cjp.getWidth() );
    //System.out.println("Pan Width: " + pan_width );
    //System.out.println("Pan Height: " + pan_height );
    setPreferredSize( new Dimension( (int)pan_width, (int)pan_height ) );
    setSize( new Dimension( (int)pan_width, (int)pan_height ) );
  }

 /*
  * Listens for messages being passed from the TranslationOverlay and passes
  * them onto its listeners.
  */
  private class OverlayListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand();
                                            // NOTE: Eventually this should
                                            // just send VALUE_CHANGED and 
                                            // the receiver should get the
                                            // new value 
      if( message.equals(BOUNDS_CHANGED) ||
          message.equals(BOUNDS_MOVED)   || 
          message.equals(BOUNDS_RESIZED)  )
        send_message(message);
    }
  }
 
 /*
  * If the mouse passes over the region, request focus.
  */ 
  private class PanMouseAdapter extends MouseAdapter
  {  
    public void mouseEntered (MouseEvent e)
    {
      requestFocus();
    }
  }
  
 /*
  * The primary purpose of this listener is to keep the aspect ratio of this
  * control close to that of the actual image.
  */ 
  private class MaintainAspectRatio extends ComponentAdapter
  {
    public void componentResized( ComponentEvent e )
    {
      refreshData();
    }
  }

  
 /*
  * This private JPanel is used to hold the image of the thumbnail. A
  * separate class was required to repaint the image without interferring
  * with the rest of the PanViewControl.
  */ 
  private class ThumbnailJPanel extends JPanel
  {
    private Image image = null;
    public ThumbnailJPanel()
    {
      super();
    }
    
    public ThumbnailJPanel(Image i)
    {
      super();
      image = i;
    }
    
    public void paintComponent( Graphics g )
    {
      // System.out.println("PanViewControl paintComponent");
      if( image != null )
        g.drawImage( image, 0, 0, this );
    }
    
    public void setImage( Image i )
    {
      image = i;
    }
  }

 /**
  *  Test program...If "cm" is passed as an argument, the
  *  ControlManager will link controls.
  */
  public static void main(String[] args)
  {
     // If cm is passed in, test with control manager.
     if( args.length > 0 && args[0].equalsIgnoreCase("cm") )
     {
       ImageJPanel2 test = new ImageJPanel2();
       ArrayGenerator test_array = new ArrayGenerator(1000,1000);
       test.setData(test_array, true);
       ViewControl[] controls = new ViewControl[3];
       controls[0] = new PanViewControl( test );
       controls[0].setTitle("Pan1");
       controls[0].setSharedKey("Pan");
       controls[1] = new PanViewControl( test );
       controls[1].setTitle("Pan2");
       controls[1].setSharedKey("Pan");
       controls[2] = new PanViewControl( test );
       controls[2].setTitle("Pan3");
       controls[2].setSharedKey("Pan");

       JFrame frame = ControlManager.makeManagerTestWindow( controls );
       frame.setBounds(0,0,450,500);

       WindowShower.show(frame);
       return;
     }

     JFrame f = new JFrame("Test for PanViewControl");
     f.getContentPane().setLayout( new java.awt.GridLayout(2,1) );
     f.setBounds(0,0,200,400);
     f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
     ImageJPanel2 test = new ImageJPanel2();
     ArrayGenerator test_array = new ArrayGenerator(1000,1000);

     test.setData(test_array, true);

     PanViewControl pvc = new PanViewControl( test );
     CoordBounds local = pvc.getGlobalBounds();
     local.scaleBounds(.5f,.5f);
     pvc.setLocalBounds(local);
     pvc.enableStretch(false);
     PanViewControl pvc2 = (PanViewControl)pvc.copy();
     pvc2.setTitle("Clone");
     f.getContentPane().add(pvc);
     f.getContentPane().add(pvc2);
     WindowShower.show(f);
  }
}
