/*
 * File:  ArrayEntryJFrame.java
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
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
 * Revision 1.2  2006/06/30 14:57:23  rmikk
 * Added a JOptionPane dialog if new value is not Okay.
 *
 * Revision 1.1  2006/06/27 19:47:17  rmikk
 * Initial Checkin.  This is part of the parameterGUI that allows for editing
 * medium sized lists
 *
 * Revision 1.6  2005/06/10 15:53:56  rmikk
 * Fixed titles to be more descriptive of the data that is to be entered
 *
 * Revision 1.5  2005/05/25 18:01:13  dennis
 * Replaced direct call to .show() method for window,
 * since .show() is deprecated in java 1.5.
 * Now calls WindowShower.show() to create a runnable
 * that is run from the Swing thread and sets the
 * visibility of the window true.
 *
 * Revision 1.4  2004/03/15 03:27:20  dennis
 * Moved view components, math and utils to new source tree
 * gov.anl.ipns.*
 *
 * Revision 1.3  2003/09/03 14:53:05  bouzekc
 * Modified to listen to the Enter key on each Component of a ParameterGUI.
 * This is to facilitate easy entry with things like FloatArrayArrayPG.
 *
 * Revision 1.2  2003/09/02 18:21:04  bouzekc
 * Added method to get at the inner panel with the data entry GUI components
 * on it.
 *
 * Revision 1.1  2003/08/30 19:46:38  bouzekc
 * Derived from ArrayEntryJPanel and committed to CVS.  Builds upon
 * ArrayEntryJPanel's functionality and is more self-contained.
 *
 */
package gov.anl.ipns.Parameters;




import gov.anl.ipns.Util.Messaging.PropertyChanger;
import gov.anl.ipns.Util.Sys.WindowShower;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.util.Vector;

import javax.swing.*;
import javax.swing.text.JTextComponent;


/**
 * This is a class to allow entry of values into one or two dimensional arrays.
 * It uses String entry methods to enter floats, ints, Strings, etc.  This
 * class was extracted from VectorPG and redesigned.
 */
public class ArrayEntryJFrame extends JFrame implements ActionListener,
  PropertyChanger, KeyListener {
  //~ Static fields/initializers ***********************************************

  private static final String UP_LABEL     = "Move Item Up";
  private static final String DOWN_LABEL   = "Move Item Down";
  private static final String DELETE_LABEL = "Delete Item";
  private static final String ADD_LABEL    = "Add Item";
  private static final String CHANGE_LABEL = "Change Value";
  private static final String DONE_LABEL   = "Done";
  private static final String SHOW_LABEL   = "Show Item";
  private static final String CLEAR_LABEL  = "Clear";
  private static final String HELP_LABEL   = "Help";

  //~ Instance fields **********************************************************

  private DefaultListModel jlistModel;
  private JList jlist;
  private Vector buttons            = new Vector( 9, 2 );
  private PropertyChangeSupport pcs;
  private Vector oldVector;
  private INewParameterGUI param;
  private int position              = -1;
  private JDialog entryDialog;
  private JPanel innerPanel;
  private String helpMessage        = "No help message available";

  //~ Constructors *************************************************************

  /**
   * ArrayEntryJFrame constructor.
   *
   * @param param ParameterGUI that determines the resultant type of the
   *        elements stored in the ArrayEntryPanel.
   */
  public ArrayEntryJFrame( INewParameterGUI param ) {
    super( param.getName(  ) );
    innerPanel = new JPanel( new BorderLayout(  ) );
    this.setSize( 500, 300 );

    //leave this commented code in here.  There is a strange flaw elsewhere
    //that requires a JDialog, but at some point I would like to remove the
    //modal/modeless operation choice. -7/1/2003 CMB
    entryDialog = new JDialog( this, param.getName(  ), true );
    entryDialog.setSize( 500, 300 );

    //entryFrame.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
    entryDialog.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );

    this.addWindowListener( new ArrayEntryWindowListener(  ) );
    entryDialog.addWindowListener( new ArrayEntryWindowListener(  ) );

    //entryDialog.getContentPane(  ).setLayout( new GridLayout( 1, 1 ) );
    //entryDialog.getContentPane(  ).add( GUI );
    this.getContentPane(  )
        .add( innerPanel );
    oldVector    = getValues(  );
    jlistModel   = new DefaultListModel(  );
    jlist        = new JList( jlistModel );
    this.param   = param;

    if( oldVector != null ) {
      for( int i = 0; i < oldVector.size(  ); i++ ) {
        jlistModel.addElement( oldVector.elementAt( i ) );
      }
    }

    innerPanel.add( new JScrollPane( jlist ), BorderLayout.CENTER );

    JPanel jp = new JPanel( new GridLayout( 0, 1 ) );

    buttons.add( new JButton( UP_LABEL ) );
    buttons.add( new JButton( DOWN_LABEL ) );
    buttons.add( new JButton( SHOW_LABEL ) );
    buttons.add( new JButton( ADD_LABEL ) );
    buttons.add( new JButton( DELETE_LABEL ) );
    buttons.add( new JButton( CHANGE_LABEL ) );
    buttons.add( new JButton( CLEAR_LABEL ) );
    buttons.add( new JButton( HELP_LABEL ) );
    buttons.add( new JButton( DONE_LABEL ) );

    JButton tempButton;

    for( int i = 0; i < buttons.size(  ); i++ ) {
      tempButton = ( JButton )buttons.get( i );
      jp.add( tempButton );
      tempButton.addActionListener( this );
    }

    innerPanel.add( jp, BorderLayout.EAST );

    JPanel dataPanel = new JPanel( new BorderLayout(  ) );

    //to avoid ambiguity when the parameter's initGUI( Vector ) method is
    //overloaded, we'll just send a new Vector.
   

    //use the inner parameter's entrywidget for entering values
    dataPanel.add( param.getGUIPanel( false ), BorderLayout.CENTER );

    //add a key listener to the parameter's TextField components
    Component[] temp = param.getGUIPanel( false )
                            .getComponents(  );

    for( int i = 0; i < temp.length; i++ ) {
      temp[i].addKeyListener( this );
    }

    innerPanel.add( dataPanel, BorderLayout.NORTH );

    //just changed the value, so invalidate the parameter.
    innerPanel.invalidate(  );

    pcs = new PropertyChangeSupport( this );
  }

  //~ Methods ******************************************************************

  /**
   * Mutator method for the help message.
   *
   * @param msg String consisting of the help message.
   */
  public void setHelpMessage( String msg ) {
    this.helpMessage = msg;
  }

  /**
   * Accessor method for the help message.
   *
   * @return String consisting of the help message.
   */
  public String getHelpMessage(  ) {
    return helpMessage;
  }

  /**
   * Accessor method for external classes to get the JPanel holding the buttons
   * and input fields.
   *
   * @return The inner JPanel, if it exists, null otherwise.
   */
  public JPanel getInnerPanel(  ) {
    if( innerPanel == null ) {
      return null;
    }

    return innerPanel;
  }
 
  /**
   * Sets the value of the GUI elements.
   *
   * @param newVal The new value to set the GUI elements to.
   */
  public void setValue( Object newVal ) {
    if( jlistModel != null ) {
      jlistModel.clear(  );

      if( ( newVal != null ) && newVal instanceof Vector ) {
        for( int i = 0; i < ( ( Vector )newVal ).size(  ); i++ ) {
          jlistModel.addElement( ( ( Vector )newVal ).elementAt( i ) );
        }
      }
    }

    position = -1;
  }

  /**
   * Accessor method to get the values in the GUI.
   *
   * @return A Vector of String representations of the GUI elements.
   */
  public Vector getValues(  ) {
    if( jlist == null ) {
      return new Vector(  );
    }

    ListModel lmodel = jlist.getModel(  );
    Vector V         = new Vector( lmodel.getSize(  ) );

    for( int i = 0; i < lmodel.getSize(  ); i++ ) {
      V.addElement( lmodel.getElementAt( i ) );
    }

    return V;
  }

  // ActionListener requirements.
  public void actionPerformed( ActionEvent evt ) {
    String command = evt.getActionCommand(  );

    if( command == UP_LABEL ) {
      move( -1 );
    } else if( command == DOWN_LABEL ) {
      move( +1 );
    } else if( command == ADD_LABEL ) {
      updateData(  );
    } else if( command == CLEAR_LABEL ) {
      jlistModel.clear(  );
    } else if( command == HELP_LABEL ) {
      showHelpWindow(  );
    } else if( command == CHANGE_LABEL ) {
      int pos = jlist.getSelectedIndex(  );

      if( ( pos >= 0 ) && ( pos < jlistModel.getSize(  ) ) ) {
        jlistModel.setElementAt( param.getValue(  ), pos );
      }
    } else if( command == DELETE_LABEL ) {
      int j = jlist.getSelectedIndex(  );

      position = -1;

      if( j < 0 ) {
        //this should throw an exception at some point
        return;
      }

      //found an element, so delete it
      jlistModel.removeElementAt( j );

      if( j < jlistModel.getSize(  ) ) {
        jlist.setSelectedIndex( j );
      }
    } else if( command == SHOW_LABEL ) {
      int index = jlist.getSelectedIndex(  );

      if( index < 0 ) {
        return;
      }

      this.setInnerParameterValue( index );

      //display the parameter
      //new ParameterViewer( param ).showParameterViewer(  );
    } else if( command == DONE_LABEL ) {
      Vector newVector = getValues(  );

      //let any property listeners know that the values have changed
      pcs.firePropertyChange( VectorPG.DATA_CHANGED, oldVector, newVector );
      oldVector = newVector;
      this.setVisible( false );
    } else if( command.equals( param.getName(  )+" Array" ) ) {
      this.setVisible( true );
    }
  }

  /**
   * Adds a PropertyChangeListener
   *
   * @param listener The PropertyChangeListener to add.
   */
  public void addPropertyChangeListener( PropertyChangeListener listener ) {
    pcs.addPropertyChangeListener( listener );
  }

  /**
   * Adds a PropertyChangeListener
   *
   * @param property The name of the property to listen for.
   * @param listener The PropertyChangeListener to add.
   */
  public void addPropertyChangeListener( 
    String property, PropertyChangeListener listener ) {
    pcs.addPropertyChangeListener( property, listener );
  }

  /**
   * We are interested in listening for the Enter key here.
   */
  public void keyPressed( KeyEvent evt ) {
    if( evt.getKeyCode(  ) == KeyEvent.VK_ENTER ) {
      updateData(  );
    }
  }

  /**
   * Needed for implementation of KeyListener, but unnecessary here.
   */
  public void keyReleased( KeyEvent evt ) {}

  /**
   * Needed for implementation of KeyListener, but unnecessary here.
   */
  public void keyTyped( KeyEvent evt ) {}

  /**
   * Overridden to set the focus to the entrywidget when this JPanel is shown.
   *
   * @param g Graphics object that is drawn on.
   */
  public void paint( Graphics g ) {
    Component[] temp = param.getGUIPanel( false )
                            .getComponents(  );

    //give the focus to the first component
    if( ( temp.length > 0 ) && ( temp[0] != null ) ) {
      temp[0].requestFocus(  );
    }

    //select all text in all JTextComponents in the EntryWidget
    for( int i = 0; i < temp.length; i++ ) {
      if( temp[i] instanceof JTextComponent ) {
        ( ( JTextComponent )temp[i] ).selectAll(  );
      }
    }

    super.paint( g );
  }

  /**
   * Removes a PropertyChangeListener
   *
   * @param listener The PropertyChangeListener to remove.
   */
  public void removePropertyChangeListener( PropertyChangeListener listener ) {
    pcs.removePropertyChangeListener( listener );
  }

  /**
   * Sets the value of the parameter to the value at the position given in the
   * list.
   *
   * @param pos The index of the position where the new value is at.
   */
  private void setInnerParameterValue( int pos ) {
    position = pos;

    if( !( param instanceof VectorPG ) ) {
      if( ( pos >= 0 ) && ( pos < jlistModel.getSize(  ) ) ) {
        param.setValue( jlistModel.elementAt( pos ) );
      }

      //( ( VectorPG )param ).actionPerformed( 
      //new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "NEW" ) );
    }
  }

  /**
   * Method to add elements to this ArrayEntryJFrame based on whether they are
   * horizontal Vectors (as is the case for FloatArrayArrayPG) or vertical
   * Vectors which should have their elements concatenated (such as for
   * QbinsPG).
   *
   * @param param The ParameterGUI whose value we should add.  If  gives us a
   *        Vector and param IS A Concatenator, we will add in a vertical
   *        fashion.
   */
  private void addVectorToList( INewParameterGUI param ) {
    Object val;
    try{
       val= param.getValue(  );
     }catch( IllegalArgumentException s){
        JOptionPane.showMessageDialog( this , s.getMessage() );
        return;
     }

    if( val == null ) {
      return;
    }

    if( val instanceof Vector && param instanceof Concatenator ) {
      Vector list = ( Vector )val;

      for( int i = 0; i < list.size(  ); i++ ) {
        if( list.get( i ) != null ) {
          jlistModel.addElement( list.get( i ) );
        }
      }
    } else {
      jlistModel.addElement( val );
    }
  }

  /**
   * Utility method to navigate through the GUI display.
   *
   * @param i The direction and magnitude to move.
   */
  private void move( int i ) {
    int j = jlist.getSelectedIndex(  );

    if( j <= 0 ) {
      if( i == -1 ) {
        return;
      }
    }

    if( j < 0 ) {
      return;
    }

    if( j >= jlist.getModel(  )
                    .getSize(  ) ) {
      return;
    }

    if( i > 0 ) {
      if( j == ( jlist.getModel(  )
                        .getSize(  ) - 1 ) ) {
        return;
      }
    }

    Object V = jlistModel.elementAt( j );

    jlistModel.removeElementAt( j );
    jlistModel.insertElementAt( V, j + i );
    jlist.setSelectedIndex( j + i );
  }

  /**
   * Shows a Help frame for this ArrayEntryJFrame.
   */
  private void showHelpWindow(  ) {
    JFrame jjf = null;

    if( jjf != null ) {
      WindowShower.show( jjf );
    }

    jjf = new JFrame( "Help" );

    JEditorPane jep = new JEditorPane( "text/plain", getHelpMessage(  ) );

    jjf.getContentPane(  )
       .add( jep );
    jjf.setSize( 400, 300 );
    jjf.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
    WindowShower.show( jjf );
  }

  /**
   * Attempts to make entering data as easy as possible for the user by working
   * with the entry widget to re-highlight text, etc. as well as actually
   * entering data into the ParameterGUI.
   */
  private void updateData(  ) {
    Component[] temp = param.getGUIPanel( false  )
                            .getComponents(  );

    //get the value from the data entry panel and add it
    //jlistModel.addElement( param.getValue(  ) );
    addVectorToList( param );

    for( int i = 0; i < temp.length; i++ ) {
      if( temp[i] instanceof JTextComponent ) {
        //re-highlight the text
        ( ( JTextComponent )temp[i] ).selectAll(  );
      }
    }
  }

  //~ Inner Classes ************************************************************

  /**
   * Triggers a property change event when the window is closed, and gives us
   * the values from the GUI.
   */
  private class ArrayEntryWindowListener extends WindowAdapter {
    //~ Methods ****************************************************************

    /**
     * Executes when a window is closed.
     *
     * @param e The window close event.
     */
    public void windowClosing( WindowEvent e ) {
      pcs.firePropertyChange( 
        new PropertyChangeEvent( 
          this, VectorPG.DATA_CHANGED, oldVector, getValues(  ) ) );
    }
  }
}
