/*
 * File:  ProgressBarUpdater.java
 *
 * Copyright (C) 2004 Chris M. Bouzek
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
 * $Log$
 * Revision 1.1  2004/04/21 19:16:27  bouzekc
 * Added to CVS.
 *
 */
package gov.anl.ipns.Util.Sys;

import javax.swing.*;


/**
 * Runnable class to update a JProgressBar using the event thread, AFTER it has
 * been completely built.  This class was taken from the
 * gov.anl.ipns.Util.Sys.WindowShower  class.  This class was created because
 * of known conditions in the Swing toolset and the event queue causing
 * problems with thread deadlock and NullPointerException.  Here is how to use
 * this class:<br>
 * <br>
 * Call setVisible( false ) on the JProgressBar.<br>
 * <br>
 * Update your progress bar (setString, setIndeterminate, etc.<br>
 * <br>
 * Construct a new ProgressBarUpdater, passing in the JProgressBar to be
 * updated (e.g. pBarUpdater).<br>
 * <br>
 * Call EventQueue.invokeLater( pBarUpdater ).<br>
 * <br>
 * Set the pBarUpdater to null, so that it can be garbage collected  after the
 * event queue finishes with it.<br>
 * <br>
 * This class will call setVisible(true) in its run method, updating your
 * progress bar in the process.<br>
 * Note: you should follow these steps each time you want to update a progress
 * bar. See: Core Java Technologies Tech Tips, December 8, 2003
 */
public class ProgressBarUpdater implements Runnable {
  //~ Instance fields **********************************************************

  private JProgressBar pBar;

  //~ Constructors *************************************************************

  /**
   * Construct a ProgressBarUpdater runnable, for the specified JProgressBar so
   * that the updates can be called by the event thread.  The code that
   * creates this ProgressBarUpdater MUST also call  EventQueue.invokeLater(
   * pBarUpdater ).
   *
   * @param window The JProgressBar to be updated later.
   */
  public ProgressBarUpdater( JProgressBar progressBar ) {
    this(  );
    this.pBar = progressBar;
  }

  /**
   * Fail-safety to avoid creating one without a progress bar.
   */
  private ProgressBarUpdater(  ) {}

  //~ Methods ******************************************************************

  /**
   * The run method will be called later by the event thread to actually update
   * the JProgressBar.
   */
  public void run(  ) {
    pBar.setVisible( true );
  }
}
