/*
 * @(#)TestMenuHandler.java   1.0  2000/05/10  Dennis Mikkelson
 *
 *  Trivial menu handler class only for testing a menu.
 *
 *  $Log$
 *  Revision 1.1  2000/11/10 22:55:46  dennis
 *  Trivial ActionListener to test menus.
 *
 *
 */
package DataSetTools.components.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


/* ------------------------- TestMenuHandler ------------------------------ */
/**
 *  Trivial menu handler class only for testing a menu.
 */
  public class TestMenuHandler implements ActionListener,
                                           Serializable
  {
    public void actionPerformed( ActionEvent e )
    {
      System.out.println( e.getActionCommand() );
    }
  }

