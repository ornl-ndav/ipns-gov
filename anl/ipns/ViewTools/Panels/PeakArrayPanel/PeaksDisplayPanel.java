/* 
 * File: PeaksDisplayPanel.java
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import gov.anl.ipns.ViewTools.Panels.Image.*;
import gov.anl.ipns.ViewTools.Panels.Transforms.*;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.UI.FontUtil;
import gov.anl.ipns.Util.Numeric.*;

/**
 *  This class is a JPanel that will display an array of volumes of data
 *  representing regions of interest (i.e. peaks) extracted from some
 *  larger 3D array.  Each volume of interest will be displayed as an
 *  image in a titled border.  The individual peak displays will be arranged
 *  in rows and columns, and the number of rows and columns used can be
 *  obtained from this object.  This information should be used to determine
 *  the size of the Frame that displays this panel.
 */
public class PeaksDisplayPanel extends JPanel
{
  /**
   *  Limit on the maximum number of images that can be displayed.
   */
  public static final int MAX_DISPLAYABLE = 1200;
  private ImageJPanel2[]     ijp_array;

  private PeakDisplayInfo[]  peak_infos;

  private int N_PANEL_ROWS;
  private int N_PANEL_COLS;

  static final float[][] DEFAULT_ARRAY   = { {0} }; 
  static final VirtualArray2D ZERO_ARRAY = new VirtualArray2D( DEFAULT_ARRAY ); 

  private JLabel   coord_label;

  private JSpinner slice_spinner;
  private int      max_slice_delta;  

  private JSlider  intensity_slider;
  private int      INITIAL_LOG_SCALE_VALUE = 30;


  /**
   *  Construct a PeaksDisplayPanel to display a list of peaks represented 
   *  by PeakDisplayInfo objects.
   *
   *  NOTE: The PeaksDisplayPanel saves a REFERENCE to the array of peak_info
   *        objects.
   *
   *  NOTE: The size of the 3D array of values MUST be the same for all of
   *        the PeakDisplayInfo objects in the list.
   *
   *  @param peak_infos  The array of peaks to be displayed
   */
  public PeaksDisplayPanel( PeakDisplayInfo[] peak_infos )
  {
    if ( peak_infos == null || peak_infos.length < 1 )
      throw new IllegalArgumentException(
                           "null or empty array of PeakDisplayInfo objects ");
    init( peak_infos );
  }


  /**
   *  Initialize the panel for this array of peak objects.
   */
  private void init( PeakDisplayInfo[] peak_infos )
  {
    removeAll();                       // if this was previously initialized
                                       // get rid of any old components
    this.peak_infos = peak_infos;

    int num_displayed = Math.min( peak_infos.length, MAX_DISPLAYABLE );
    if ( num_displayed != peak_infos.length )
      System.out.println("Warning: number of peaks displayed limited to" +
                          MAX_DISPLAYABLE );
                                               // figure out how to split
    if ( num_displayed <= 5 )                  // these into rows and columns
    {
      N_PANEL_COLS = num_displayed;
      N_PANEL_ROWS = 1;
    }
    else if ( num_displayed <= 25 )
    {
      N_PANEL_COLS = 5;
      N_PANEL_ROWS = (num_displayed - 1) / 5 + 1;
    }
    else if ( num_displayed <= 100 )
    {
      N_PANEL_COLS = 10;
      N_PANEL_ROWS = (num_displayed-1) / 10 + 1;
    }
    else if  ( num_displayed <= 225 )
    {
      N_PANEL_COLS = 15;
      N_PANEL_ROWS = (num_displayed-1) / 15 + 1;
    }
    else if ( num_displayed <= 400 )
    {
      N_PANEL_COLS = 20;
      N_PANEL_ROWS = (num_displayed-1) / 20 + 1;
    }
    else
    {
      N_PANEL_COLS = 25;
      N_PANEL_ROWS = (num_displayed-1) / 25 + 1;
    }

                                           // Make the main panel
    setLayout( new BorderLayout() );
    JPanel center_panel = new JPanel();
    add( center_panel, BorderLayout.CENTER );
    center_panel.setLayout( new GridLayout(N_PANEL_ROWS,N_PANEL_COLS) ); 

                                           // Make the control panel across
                                           // the bottom of the display
    Box control_panel = Box.createHorizontalBox();
    add( control_panel, BorderLayout.SOUTH );

    coord_label = new JLabel();
    control_panel.add( coord_label );

    max_slice_delta = (peak_infos[0].maxChan() - peak_infos[0].minChan()) / 2;
    JLabel spinner_label = new JLabel("  Slice Offset ");
    control_panel.add( spinner_label );

    slice_spinner = new JSpinner();
    slice_spinner.addChangeListener( new SliceSpinnerListener() );
    JPanel spinner_container = new JPanel();
    spinner_container.setLayout( new GridLayout(1,1) );
    spinner_container.setMinimumSize( new Dimension( 40, 20) );
    spinner_container.setMaximumSize( new Dimension( 40, 20) );
    spinner_container.setPreferredSize( new Dimension( 40, 20) );
    spinner_container.add( slice_spinner );
    control_panel.add( spinner_container );

    intensity_slider = new JSlider( 0, 100, INITIAL_LOG_SCALE_VALUE );
    intensity_slider.addChangeListener( new SliderListener() );
    intensity_slider.setMinimumSize( new Dimension( 150, 20) );
    intensity_slider.setMaximumSize( new Dimension( 150, 20) );
    intensity_slider.setPreferredSize( new Dimension( 150, 20) );
    control_panel.add( intensity_slider );

    JPanel filler = new JPanel();
    filler.setPreferredSize( new Dimension( 2000, 20) );
    control_panel.add( filler );

                                            // Now build all the images and 
                                            // add them to the central display
    ijp_array = new ImageJPanel2[ num_displayed ];    
    VirtualArray2D va2D;
    TitledBorder   border;
    JPanel         container;
    String         name;
    ActionListener location_listener = new LocationListener();

    for ( int index = 0; index < num_displayed; index ++ )
    {
      ijp_array[index] = new ImageJPanel2();
      ijp_array[index].setDataRange(0,2000);
      ijp_array[index].changeLogScale( INITIAL_LOG_SCALE_VALUE, false );
      ijp_array[index].addActionListener( location_listener );

      float[][] slice = peak_infos[index].getSlice();
      va2D = new VirtualArray2D( slice ); 
      ijp_array[index].setData( va2D, true );

      int min_col = peak_infos[index].minCol();
      int max_col = peak_infos[index].maxCol() + 1;
      int min_row = peak_infos[index].minRow();
      int max_row = peak_infos[index].maxRow() + 1;
      CoordBounds bound = new CoordBounds(min_col, max_row,  max_col, min_row);
      ijp_array[index].initializeWorldCoords( bound );

      container = new JPanel();
      container.setLayout( new GridLayout(1,1) );
      name   = peak_infos[index].getName();
      border = new TitledBorder( LineBorder.createBlackLineBorder(), name );
      border.setTitleFont( FontUtil.BORDER_FONT );
      if ( !peak_infos[index].isValid() ) 
        border.setTitleColor( Color.RED );
      container.setBorder( border );
      container.add( ijp_array[index] );
      center_panel.add(container);
    }
                                          // If we don't have a full rectangle
                                          // of panels, the grid layout doesn't 
                                          // work, so fill out any remaining
                                          // positions with empty panels.
    int num_positions = N_PANEL_ROWS * N_PANEL_COLS;
    for ( int i = num_displayed; i < num_positions; i++ )
      center_panel.add( new JPanel() );
  }


  /**
   *  Get the number of rows used for the display of the peaks.  
   *  Approximately 100 pixels in height should be used for each row,
   *  plus an additional 30 pixels for the header and footer.
   *
   *  @return the number of rows of images in the panel
   */
  public int numPanelRows()
  {
    return N_PANEL_ROWS;
  }


  /**
   *  Get the number of columns used for the display of the peaks.  
   *  Approximately 100 pixels in width should be used for each column. 
   *
   *  @return the number of rows of images in the panel
   */
  public int numPanelCols()
  {
    return N_PANEL_COLS;
  }


  /**
   *  Listener class for the intensity slider
   */
  public class SliderListener implements ChangeListener
  {
     public void stateChanged( ChangeEvent e )
     {
        int value = intensity_slider.getValue();
        if ( !intensity_slider.getValueIsAdjusting() )
          for ( int i = 0; i < ijp_array.length; i++ )
            ijp_array[i].changeLogScale(value,true);
     }
  }


  /**
   *  Listener class for the slice offset selector
   */
  public class SliceSpinnerListener implements ChangeListener
  {
     public void stateChanged( ChangeEvent e )
     {
        Integer value = (Integer)slice_spinner.getValue();
        if ( value > max_slice_delta )
        {
          slice_spinner.setValue( max_slice_delta );
          return;
        }
        else if ( value < -max_slice_delta )
        {
          slice_spinner.setValue( -max_slice_delta );
          return;
        }
        
        float[][] slice;
        VirtualArray2D va2D;
        for ( int i = 0; i < peak_infos.length; i++ )
        {
          peak_infos[i].setRelativeChannel( value );
          slice = peak_infos[i].getSlice();
          va2D = new VirtualArray2D( slice );
          ijp_array[i].setData( va2D, true );
        }
     }
  }


  /**
   *  Listener class for the cross hair pixel selector.
   */
  public class LocationListener implements ActionListener
  {
     public LocationListener()
     {
       setLabel( 0, 0, 0 );
     }

     public void actionPerformed( ActionEvent event )
     {
       ImageJPanel2 ijp = (ImageJPanel2)event.getSource();        
       floatPoint2D wc_point = ijp.getCurrent_WC_point();
       float        value    = ijp.ImageValue_at_Cursor();
       setLabel( wc_point.x, wc_point.y, value );
     }

     private void setLabel( float x, float y, float value )
     {
       String format = " Col: %5.1f, Row: %5.1f, Counts: %4.0f ";
       String coord_str = String.format(format, x, y, value);
       coord_label.setText( coord_str );
     }
  }

}
