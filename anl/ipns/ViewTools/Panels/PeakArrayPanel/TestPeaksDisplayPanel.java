/* 
 * File: TestPeaksDisplayPanel.java
 *
 * Copyright (C) 2008, Dennis Mikkelson
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

package gov.anl.ipns.ViewTools.Panels.PeakArrayPanel;

import javax.swing.*;

/**
 *  This class provides a basic test of the PeaksDisplayPanel class.
 */
public class TestPeaksDisplayPanel 
{
  public static void main( String args[] )
  {
    int NUM_INFOS = 7;
    PeakDisplayInfo[] infos = new PeakDisplayInfo[NUM_INFOS];

    float[][] test_data_0 = { { 1, 0, 1, 0, 0 },
                              { 0, 2, 3, 0, 1 },
                              { 0, 1, 5, 3, 1 },
                              { 1, 2, 2, 0, 1 },
                              { 0, 1, 1, 2, 1 } };

    float[][] test_data_1 = { { 1, 0, 1, 0, 0 },
                              { 0, 2, 3, 0, 1 },
                              { 0, 1, 7, 3, 1 },
                              { 1, 2, 2, 0, 1 },
                              { 0, 1, 1, 2, 1 } };

    float[][] test_data_2 = { { 1, 0, 1, 0, 0 },
                              { 0, 2, 3, 0, 1 },
                              { 0, 1, 9, 3, 1 },
                              { 1, 2, 2, 0, 1 },
                              { 0, 1, 1, 2, 1 } };

    float[][][] counts = { test_data_0, test_data_1, test_data_2 };

    for ( int i = 0; i < NUM_INFOS; i++ )
    {
      int first_row  = 10 + i;
      int first_col  = 20 + i;
      int first_chan = 30 + i;

      int seq_num  = i;
      int mid_row  = first_row  + 2;
      int mid_col  = first_col  + 2;
      int mid_chan = first_chan + 2;

      String name = String.format("%1d:%1d,%1d,%1d",
                                   seq_num,
                                   mid_col,
                                   mid_row,
                                   mid_chan );
      boolean valid;
      if ( i%3 == 0 )
        valid = false;
      else
        valid = true;

      infos[i] = new PeakDisplayInfo( name,
                                      counts, 
                                      10+i,
                                      20+i,
                                      30+i,
                                      valid );
    }

    PeaksDisplayPanel main_panel = new PeaksDisplayPanel( infos );

    int frame_width  = 100 * main_panel.numPanelCols();
    int frame_height = 100 * main_panel.numPanelRows() + 30;

    JFrame frame = new JFrame( "Peaks Display Test" );
    frame.setSize( frame_width, frame_height );

    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.getContentPane().add( main_panel );
    frame.setVisible( true );
  }

}
