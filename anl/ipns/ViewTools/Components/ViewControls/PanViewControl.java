/*
 * File: PanViewControl.java
 *
 * Copyright (C) 2003, Mike Miller
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
 
 package DataSetTools.components.View.ViewControls;

 import java.awt.event.ActionListener;
 import java.awt.event.ActionEvent;
 import java.awt.event.MouseAdapter;
 import java.awt.event.MouseEvent;
 import java.awt.Dimension;
 import java.awt.Point;
 import java.awt.Graphics;
 import javax.swing.JFrame;
 import javax.swing.OverlayLayout;
 
 import DataSetTools.components.image.CoordJPanel;
 import DataSetTools.components.image.BoxCursor;
 import DataSetTools.components.image.CoordBounds;
 import DataSetTools.components.image.CoordTransform;
 import DataSetTools.components.image.ImageJPanel;
 import DataSetTools.components.View.IPreserveState;
 import DataSetTools.components.View.ObjectState;
 import DataSetTools.components.View.Cursor.TranslationJPanel;
 import DataSetTools.components.View.Transparency.TranslationOverlay;
 import DataSetTools.util.floatPoint2D;
 
/**
 * This view control is used to "pan" an image. Adding a PanViewControl
 * will cause a thumbnail of the image to appear in the control panel.
 */
public class PanViewControl extends ViewControl implements IPreserveState
{
 /**
  * "Translation Overlay" - This constant String is a key for referencing the
  * state information about the TranslationOverlay. Because the overlay has 
  * its own state info, this value is of type ObjectState,
  * and contains the state of the overlay. 
  */
  public static final String TRANSLATION_OVERLAY  = "Translation Overlay";
  
 /**
  * "Thumbnail" - This constant String is a key for referencing the
  * state information about the CoordJPanel "shadowing" the true image.
  * Because the CoordJPanel has  its own state info, this value is of type
  * ObjectState, and contains the state info about the bounds of the control. 
  */
  public static final String THUMBNAIL = "Thumbnail";
  
  private CoordJPanel panel;           // replica of actual image
  private TranslationOverlay overlay;  // where the region outline is drawn.
  // local bounds of the panel must be the same as global bounds, thus
  // the local bounds must be saved separately.
  private CoordBounds local_bounds = new CoordBounds();
  
 /**
  * Constructor for creating a thumbnail of the CoordJPanel passed in.
  *
  *  @param  cjp The CoordJPanel that will be thumbnailed.
  */
  public PanViewControl(CoordJPanel cjp)
  {
    super("");
    panel = cjp;
    overlay = new TranslationOverlay();
    overlay.addActionListener( new OverlayListener() );
    setGlobalBounds( cjp.getGlobalWorldCoords() );
    setLocalBounds( cjp.getLocalWorldCoords() );
    panel.setEventListening(false);
    
    OverlayLayout layout = new OverlayLayout(this);
    setLayout(layout);
    setPreferredSize( new Dimension(0,150) );
    add(overlay);
    add(panel);
    addMouseListener( new PanMouseAdapter() );
  } 
    
 /**
  * Constructor for creating a thumbnail of the CoordJPanel using state info.
  *
  *  @param  cjp The CoordJPanel that will be thumbnailed.
  *  @param  state The objectstate of this control.
  */
  public PanViewControl(CoordJPanel cjp, ObjectState state)
  {
    this(cjp);
    setObjectState(state);
  }
  
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    boolean redraw = false;  // if any values are changed, repaint overlay.
    Object temp = new_state.get(TRANSLATION_OVERLAY);
    if( temp != null )
    {
      overlay.setObjectState( (ObjectState)temp );
      panel.setGlobalWorldCoords( overlay.getGlobalBounds() );
      local_bounds = overlay.getLocalBounds();
      redraw = true;  
    }
    
    temp = new_state.get(THUMBNAIL);
    if( temp != null )
    {
      panel.setObjectState( (ObjectState)temp );
      overlay.setGlobalBounds( panel.getGlobalWorldCoords() );
      overlay.setLocalBounds( local_bounds );
      redraw = true;  
    } 
    
    if( redraw )
      repaint();
  }
 
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState. Keys will be
  * put in alphabetic order.
  *
  *  @return state of this control.
  */ 
  public ObjectState getObjectState()
  {
    ObjectState state = new ObjectState();
    state.insert( TRANSLATION_OVERLAY, overlay.getObjectState() );
    state.insert( THUMBNAIL, panel.getObjectState() );
    
    return state;
  }
  
 /**
  * Get the local bounds of the thumbnail. These bounds represent the area
  * viewable by the user and are shown graphically by the cursor. 
  *
  *  @return Bounds of the viewable region.
  */ 
  public CoordBounds getLocalBounds()
  {
    return local_bounds;
  }  
  
 /**
  * Set the local bounds of the thumbnail. These bounds represent the area
  * viewable by the user and are shown graphically by the cursor. 
  *
  *  @param  lb Bounds of the viewable region.
  */ 
  public void setLocalBounds( CoordBounds lb )
  {
    local_bounds = lb.MakeCopy();
    overlay.setLocalBounds( local_bounds );
    repaint();
  }  
  
 /**
  * Get the global bounds of the thumbnail. These bounds represent the entire 
  * possible area viewable by the user. 
  *
  *  @return Bounds of the viewable region.
  */ 
  public CoordBounds getGlobalBounds()
  {
    return panel.getGlobalWorldCoords();
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
    panel.initializeWorldCoords( gb.MakeCopy() );
    overlay.setGlobalBounds( gb.MakeCopy() );
  }
 
 /**
  * Since the classes that extend the CoordJPanel are affected by a logscale
  * factor, this method is used to inform those classes when the logscale has
  * changed.
  *
  *  @param  s The logscale factor.
  */ 
  public void setLogScale( double s )
  {
    if( panel instanceof ImageJPanel )
      ((ImageJPanel)panel).changeLogScale( s, true );
  }
 
 /**
  * Since some of the classes that extend the CoordJPanel are affected by a
  * a colorscale, this method is used to inform those classes when the
  * colorscale has changed.
  *
  *  @param  colorscale The colorscale of the image.
  *  @param  isTwoSided Is the colorscale two-sided? Yes = true.
  */ 
  public void setImageColorScale( String colorscale, boolean isTwoSided )
  {
    if( panel instanceof ImageJPanel )
      ((ImageJPanel)panel).setNamedColorModel(colorscale, isTwoSided, true);
  }

 /**
  * Test program...
  */
  public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for PanViewControl");
    f.setBounds(0,0,200,200);
    f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    CoordJPanel test = new CoordJPanel();
    CoordTransform globaltf = new CoordTransform( new CoordBounds(0,0,200,200),
                                                  new CoordBounds(0,0,1,1) );
    CoordTransform localtf = new CoordTransform( new CoordBounds(10,50,100,100),
                                                 new CoordBounds(0,0,1,1) );
    //test.setGlobalWorldCoords( new CoordBounds(0,0,200,200) );
    //test.setLocalWorldCoords( new CoordBounds(50,50,100,100) );
    ObjectState teststate = new ObjectState();
    ObjectState superteststate = new ObjectState();
    teststate.insert( CoordJPanel.GLOBAL_BOUNDS,
                      new CoordTransform( globaltf ) );
    teststate.insert( CoordJPanel.LOCAL_BOUNDS,
                      new CoordTransform( localtf ) );
    //test.setObjectState( teststate );
    superteststate.insert( PanViewControl.THUMBNAIL, teststate );
    PanViewControl pvc = new PanViewControl( test );
    pvc.setObjectState(superteststate);
    f.getContentPane().add(pvc);
    f.setVisible(true);
    //pvc.setLocalBounds( new CoordBounds(0,0,50,50) );
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
      if( message.equals( TranslationJPanel.BOUNDS_CHANGED ) )
      {
        local_bounds = overlay.getLocalBounds();
      }
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
}
