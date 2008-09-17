/*
 * File:  ProgressBarUpdater.java
 *
 * Copyright (C) 2008 Dennis Mikkelson
 *
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
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */

package gov.anl.ipns.Util.Sys;

import javax.swing.*;
import java.awt.*;


/**
 * This class provides a mechanism to record requested updates to a 
 * JProgressBar object, and subsequently apply those updates from the 
 * event handling thread.  In particular after constructing a
 * a ProgressBarUpdater for a specific JProgressBar, calls to setValue(),
 * setString() and setIndeterminate() should be directed to the 
 * ProgressBarUpdater, NOT to the JProgressBar directly.  The 
 * ProgressBarUpdater will use EventQueue.invokeLater() to call the
 * corresponding methods on the actual JProgressBar.
 *
 * The ideas behind this class were adapted from:
 * http://forums.sun.com/thread.jspa?threadID=5316900&messageID=10352456
 *
 */
public class ProgressBarUpdater
{

  private JProgressBar pBar;
  private boolean      queued;
  private int          value;
  private boolean      indeterminate;
  private String       string_value;

  private final Runnable updater = new Runnable() {
                                                    public void run()
                                                    {
                                                      update(); 
                                                    }
                                                  };

  /**
   * Construct a ProgressBarUpdater runnable, for the specified JProgressBar so
   * that changes to the progress bar can be made from the the event thread. 
   *
   * @param  progressBar  The JProgressBar to be updated later.
   */
  public ProgressBarUpdater( JProgressBar progressBar ) 
  {
    if ( progressBar == null )
      throw new IllegalArgumentException( "ProgressBar is NULL " + 
                                          "in ProgressBarUpdater constructor" );
    this.pBar     = progressBar;
    queued        = false;
    value         = progressBar.getValue();
    indeterminate = pBar.isIndeterminate();
    string_value  = pBar.getString();
  }


  /**
   * Set the value of the progress bar using EventQueue.invokeLater().
   *
   * @param  newValue  The new value for the progress bar
   */
  public void setValue(int newValue) 
  {
    boolean wasQueued;

    synchronized(updater) 
    {
      value = newValue;
      wasQueued = queued;
      queued = true;
    }

    if (!wasQueued) 
      EventQueue.invokeLater(updater);
  }


  /**
   * Set the String the progress bar using EventQueue.invokeLater().
   *
   * @param  newString  The new String to display in the progress bar
   */
  public void setString(String newString) 
  {
    boolean wasQueued;
    
    synchronized(updater)
    {
      string_value = newString;
      wasQueued = queued;
      queued = true;
    }

    if (!wasQueued) 
      EventQueue.invokeLater(updater);
  }


  /**
   * Set the indeterminate property of the progress bar using 
   * EventQueue.invokeLater().
   *
   * @param  flag  The new indeterminate state to set for the progress bar
   */
  public void setIndeterminate( boolean flag )
  {
    boolean wasQueued;
   
    synchronized(updater)
    {
      indeterminate = flag;
      wasQueued = queued;
      queued = true;
    }
    
    if (!wasQueued)
      EventQueue.invokeLater(updater);
  }


 /**
  * This method is called by the updater Runnable, from the 
  * EventQueue.
  */
  private void update() 
  {
    int     valueCopy;
    String  string_valueCopy;
    boolean indeterminateCopy;

    synchronized(updater) 
    {
      valueCopy         = value;
      string_valueCopy  = string_value;
      indeterminateCopy = indeterminate; 

      queued = false;
    }

    pBar.setIndeterminate( indeterminateCopy );
    pBar.setValue(valueCopy);
    pBar.setString(string_valueCopy);
  }


}
