/*
 * File: TranslationOverlay.java
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
 *  Revision 1.5  2003/12/20 21:37:29  millermi
 *  - implemented kill() so editor and help windows are now
 *    disposed when the kill() is called.
 *
 *  Revision 1.4  2003/11/25 00:59:51  millermi
 *  - Removed private data member global_bounds, now uses call to
 *    TranslationJPanel's global bounds.
 *
 *  Revision 1.3  2003/11/18 01:01:28  millermi
 *  - Minor changes to help()
 *
 *  Revision 1.2  2003/10/29 20:33:52  millermi
 *  -Fixed java docs.
 *  -Added ObjectState info.
 *
 *  Revision 1.1  2003/10/27 08:47:49  millermi
 *  - Initial Version - This class was created to enable users
 *    panning options for images too large to view in the
 *    viewport.
 *
 */

package DataSetTools.components.View.Transparency;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.util.Vector; 

import DataSetTools.components.View.ObjectState;
import DataSetTools.components.View.Cursor.BoxPanCursor;
import DataSetTools.components.View.Cursor.TranslationJPanel;
import DataSetTools.components.image.CoordJPanel;
import DataSetTools.components.image.CoordBounds;
import DataSetTools.components.image.CoordTransform;
import DataSetTools.util.floatPoint2D;

/**
 * This overlay is not like the Axis, Annotation, or Selection Overlays. 
 * Although it extends an OverlayJPanel, it is not used by view components.
 * Instead it's primary purpose is for panning an image. This overlay
 * will outline the "local bounds" of the image.
 *
 *  @see DataSetTools.components.View.Cursor.TranslationJPanel
 *  @see DataSetTools.components.View.ViewControls.PanViewControl
 *  @see DataSetTools.components.View.Cursor.XOR_PanCursor
 */
public class TranslationOverlay extends OverlayJPanel
{
 /**
  * "TranslationJPanel" - This constant String is a key for referencing the
  * state information about the TranslationJPanel. Since all of the state info
  * needed by this overlay is contained in the TranslationJPanel ObjectState, 
  * this value is of type ObjectState, and contains the state of the overlay. 
  */
  public static final String TRANSLATION_JPANEL  = "TranslationJPanel";
    
  private static JFrame helper = null;
  
  private TranslationJPanel tjp;
  private CoordJPanel main_image;
  private CoordBounds viewport;       // local_bounds in pixel coords
  private Vector Listeners = null;  

 /**
  * Constructor - this will only initialize the local and global bounds.
  * To actually set these values, use the setLocalBounds() and setGlobalBounds()
  * methods.
  */
  public TranslationOverlay()
  {
    super();
    this.setLayout( new GridLayout(1,1) );
    tjp = new TranslationJPanel();
    setLocalBounds( new CoordBounds(0,0,1,1) );
    setGlobalBounds( new CoordBounds(0,0,1,1) );
    tjp.addActionListener( new TranslateListener() );
    Listeners = new Vector();
    add(tjp);
    tjp.requestFocus();
    addComponentListener( new ResizedListener() );
  }
  
 /**
  * Constructor - this will call the default constructor before setting state
  * information.
  */
  public TranslationOverlay(ObjectState state)
  {
    super();
    setObjectState(state);
  }
  
 /**
  * This method will be used to display help information about this overlay.
  * It should open it's own JFrame with information about the overlay.
  */ 
  public static void help()
  {
    helper = new JFrame("Help for Translation Overlay");
    helper.setBounds(0,0,600,400);
    JTextArea text = new JTextArea("Description:\n\n");
    text.setEditable(false);
    text.setLineWrap(true);
    text.append("The Translation Overlay is used to pan over an image " +
                "to view parts not directly visible on the image.\n\n");
    text.append("Commands for Translation Overlay\n\n");
    text.append("Use the mouse to click and drag to a " +
                "new selected region on the image.\n\n" );
    text.append("********************************************************\n");
    
    JScrollPane scroll = new JScrollPane(text);
    scroll.setVerticalScrollBarPolicy(
 				    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    helper.setVisible(true);
  }
 
 /**
  * This paint will draw a rectangle over the region selected by the cursor.
  *
  *  @param  g - graphics object required by the paint method.
  */ 
  public void paint(Graphics g)
  {
    g.drawRect( (int)viewport.getX1(), (int)viewport.getY1(),
                (int)viewport.getX2() - (int)viewport.getX1(),
		(int)viewport.getY2() - (int)viewport.getY1() );
  }
  
 /**
  * Get the viewport or local bounds viewable to the user.
  *
  *  @return viewport - viewable area
  */ 
  public CoordBounds getLocalBounds()
  {
    return tjp.getLocalWorldCoords().MakeCopy();
  }
  
 /**
  * Set the size of the viewport. This method must be called whenever the
  * local bounds of the CoordJPanel are changed. Also call this method to
  * initialize a viewport size.
  *
  *  @param  vp CoordBounds representing the viewable area.
  */ 
  public void setLocalBounds( CoordBounds vp )
  {
    tjp.setViewPort( vp.MakeCopy() );
  }
  
 /**
  * Set the size of the whole image. This size is usually going to be larger
  * than the viewport.
  *
  *  @return CoordBounds representing the total possible viewable area.
  */ 
  public CoordBounds getGlobalBounds()
  {
    return tjp.getGlobalWorldCoords();
  }
  
 /**
  * Set the size of the whole image. This size is usually going to be larger
  * than the viewport.
  *
  *  @param  global CoordBounds representing the total possible viewable area.
  */ 
  public void setGlobalBounds( CoordBounds global )
  {
    tjp.setGlobalPanelBounds( global.MakeCopy() );
    repaint();
  }
 
 /**
  * This method requests window focus for the overlay. If focus is wanted
  * by a private data member of an overlay, this method should be overloaded
  * to have the data member itself call requestFocus(). 
  */ 
  public void getFocus()
  {
    tjp.requestFocus();
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
    Object temp = new_state.get(TRANSLATION_JPANEL);
    if( temp != null )
    {
      tjp.setObjectState( (ObjectState)temp );
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
  *  @return state of this overlay.
  */ 
  public ObjectState getObjectState()
  {
    ObjectState state = new ObjectState();
    state.insert( TRANSLATION_JPANEL, tjp.getObjectState() );
    
    return state;
  }
  
 /**
  * Method to add a listener to this overlay.
  *
  *  @param act_listener
  */
  public void addActionListener( ActionListener act_listener )
  {	     
    for ( int i = 0; i < Listeners.size(); i++ )    // don't add it if it's
      if ( Listeners.elementAt(i).equals( act_listener ) ) // already there
        return;

    Listeners.add( act_listener ); //Otherwise add act_listener
  }
 
 /**
  * Method to remove a listener from this component.
  *
  *  @param act_listener
  */ 
  public void removeActionListener( ActionListener act_listener )
  {
    Listeners.remove( act_listener );
  }
 
 /**
  * Method to remove all listeners from this component.
  */ 
  public void removeAllActionListeners()
  {
    Listeners.removeAllElements();
  }
     
 /**
  * This method is called by to inform the overlay that it is no
  * longer needed. In turn, the overlay closes all windows created
  * by it before closing.
  */ 
  public void kill()
  {
    if( helper != null )
      helper.dispose();
  }
  
 /*
  * Tells all listeners about a new action.
  *
  *  @param  message
  */  
  private void sendMessage( String message )
  {
    for ( int i = 0; i < Listeners.size(); i++ )
    {
      ActionListener listener = (ActionListener)Listeners.elementAt(i);
      listener.actionPerformed( new ActionEvent( this, 0, message ) );
    }
  }

 /*
  * Converts from world coordinates to a pixel point.
  * ***Note*** This transform is in opposite order of the CoordJPanel.
  */
  private Point convertToPixelPoint( floatPoint2D fp )
  {
    CoordTransform pixel_global = tjp.getGlobal_transform();
    floatPoint2D fp2d = pixel_global.MapTo( fp );
    return new Point( (int)fp2d.x, (int)fp2d.y );
  }
 
 /*
  * Converts from pixel coordinates to world coordinates.
  * ***Note*** This transform is in opposite order of the CoordJPanel.
  */
  private floatPoint2D convertToWorldPoint( Point p )
  {
    CoordTransform pixel_global = tjp.getGlobal_transform();
    return pixel_global.MapFrom( new floatPoint2D((float)p.x, (float)p.y) );
  }

 /*
  * TranslateListener listens for messages being passed from the
  * TranslationJPanel.
  */
  private class TranslateListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand(); 
      // clear all selections from the vector
      if( message.equals( TranslationJPanel.BOUNDS_CHANGED ) )
      {
	Rectangle box = ((BoxPanCursor)tjp.getBoxCursor()).region();
        Point p1 = new Point( box.getLocation() );
        Point p2 = new Point( p1 );
        p2.x += (int)box.getWidth();
        p2.y += (int)box.getHeight();
	
	viewport = new CoordBounds( p1.x, p1.y,
	                            p2.x, p2.y );
	
	repaint();           // Without this, the newly drawn regions would
			     // not appear.
        sendMessage(message);
      }
    }  // end actionPerformed()   
  } // end TranslateListener 
 
 /*
  * If the component is resized, the bounds of the cursor need to be adjusted.
  * This listener will move and resize the cursor after the component is
  * resized. 
  */ 
  private class ResizedListener extends ComponentAdapter
  {
    public void componentResized( ComponentEvent ce )
    {
      Rectangle box = ((BoxPanCursor)tjp.getBoxCursor()).region();
      Point p1 = new Point( box.getLocation() );
      Point p2 = new Point( p1 );
      p2.x += (int)box.getWidth();
      p2.y += (int)box.getHeight();
	
      viewport = new CoordBounds( p1.x, p1.y,
        			  p2.x, p2.y );
    }
  }

}
