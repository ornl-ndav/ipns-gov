/*
 * File:  ViewControlsPanel.java
 *
 * Copyright (C) 2003 Chris Bouzek
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
 *           Chris Bouzek <coldfusion78@yahoo.com>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA and by
 * the National Science Foundation under grant number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.2  2004/03/12 03:05:53  dennis
 * Moved to package gov.anl.ipns.ViewTools.Components.ViewControls
 *
 * Revision 1.1  2003/07/08 16:31:34  serumb
 * Added to CVS.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;

/**
 * This class is to create a JPanel of ViewControls that has the capability to
 * add ActionListeners (adding an ActionListener to this class adds it to all
 * of the ViewControls in the JPanel of controls), and to allow for outside
 * setting of the layout.
 */
  public class ViewControlsPanel {
  private JPanel controlsPanel;
  private Vector controlsArray;

  /**
   *  Default constructor.  makeGUI() is NOT called.
   */
  public ViewControlsPanel(  ) {
    controlsPanel = new JPanel(  );
    controlsArray = new Vector(  );
  }

  /**
   *  Takes an array of JComponents that should,
   *  incidentally, be view controls.  makeGUI() is called.
   *
   *  @param           comps           Array of JComponents to add.
   */
  public ViewControlsPanel( JComponent[] comps ) {
    controlsArray = new Vector(  );
    controlsPanel = new JPanel(  ); 
  
    for( int i = 0; i < comps.length; i++ ) {
      controlsArray.add( comps[i] );
    }

    makeGUI(  );
  }

  /**
   *  Method to add ActionListeners to this ViewControlsPanel's ViewControls.
   *  Only components which extend AbstractButton will have listeners added.
   *
   *  @param   al               ActionListener to add.
   */
  public void addActionListener( ActionListener al ) {
    for( int i = 0; i < controlsArray.size(  ); i++ ) {
      if( controlsArray.elementAt( i ) instanceof AbstractButton ) {
        ( ( AbstractButton )( controlsArray.elementAt( i ) ) ).addActionListener( 
          al );
      }
    }
  }

  /**
   *  Accessor method to return the JPanel associated with this
   *  ViewControlsPanel.
   *
   *  @return   The panel containing all of the ViewControls.
   */
  public JPanel getPanel(  ) {
    return controlsPanel;
  }

  /**
   *  Method to add a ViewControl to the JPanel holding all of the
   *  ViewControls.
   *
   *  @param  viewCtrl   The ViewControl to add.
   */
  public void addViewControl( JComponent viewCtrl ) {
    controlsPanel.add( viewCtrl );
  }

  /**
   *  Method to add a ViewControl to the JPanel holding all of the
   *  ViewControls.  Does nothing if the ViewControl is not in there.
   *
   *  @param  viewCtrl   The ViewControl to remove.
   */
  public void removeViewControl( JComponent viewCtrl ) {
    controlsPanel.remove( viewCtrl );
  }

  /**
   *  Makes the JPanel holding all the ViewControls.
   */
  public void makeGUI(  ) {
    controlsPanel = new JPanel(  );

    for( int i = 0; i < controlsArray.size(  ); i++ ) {
      controlsPanel.add( ( JComponent )( controlsArray.elementAt( i ) ) );
    }
  }

  /**
   *  Sets the layout for this ViewControlsPanel's JPanel.
   */
  public void setLayout( LayoutManager layout ) {
    if( controlsPanel == null ) {
      makeGUI(  );
    }

    controlsPanel.setLayout( layout );
  }

}
