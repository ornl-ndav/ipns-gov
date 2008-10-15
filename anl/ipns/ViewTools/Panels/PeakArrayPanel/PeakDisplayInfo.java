/* 
 * File: PeakDisplayInfo.java
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

/**
 *  This class holds the basic data needed to display one peak in a
 *  PeaksDisplayPanel object showing a whole array of peaks.
 */
public class PeakDisplayInfo 
{
  private String       name;             // name for the peak on titled border
  private float[][][]  counts;           // array of counts centered on peak

  private int          min_row;          // range of row numbers from the
  private int          max_row;          // overall volume of data that 
                                         // contains this peak.
  private int          min_col;          // range of column numbers
  private int          max_col;

  private int          min_chan;          // range of channel numbers
  private int          max_chan;

  private int          relative_channel;  // We number the center slice
                                          // as zero, and step forward
                                          // or backward using +- ints
  private int          mid_channel;       // actual channel displayed is
                                          // mid_channel + relative channel;
  private boolean      is_valid;

  /**
   *  Construct a PeakDisplayInfo object using the specified "small" array of
   *  counts, presumably extracted from some larger 3D array of data.
   *  NOTE: The array of counts must be indexed as counts[page][row][col].
   *        That is, it is a list of two dimensional arrays of the same size
   *        and each two dimensional array is stored in row major order.
   *  NOTE: The array of counts must have an odd number of rows, columns and
   *        pages, and must be centered on the center of the peak.
   *  NOTE: Although this array has row, col, channel indexes starting at 
   *        (0, 0, 0) the data is assumed to be extracted from a larger array.
   *        Consequently the min_row, min_col and min_channel where this 
   *        data was taken from also is needed.
   *  NOTE: Row 0 will be displayed at the BOTTOM of the image.
   *
   *  @param name      Name that will be displayed on a titled border around
   *                   the image of one slice from the counts array.  This
   *                   should include the sequence number, and col, row,
   *                   channel of the peak center.
   *  @param counts    A 3D array of floats, containing the portion of a 
   *                   larger array, that is centered on a peak.  This class
   *                   keeps a REFERENCE to the counts[][][] array that is 
   *                   passed in.
   *  @param min_row   Minimum row number in the array where counts[][][]
   *                   was extracted.
   *  @param min_col   Minimum column number in the array where counts[][][]
   *                   was extracted.
   *  @param min_chan  Minimum channel number in the array where counts[][][]
   *                   was extracted.
   *  @param is_valid  Flag indicating whether or not this peak is considered
   *                   valid.  If false, the border text will be drawn in
   *                   RED   
   */
  public PeakDisplayInfo( String      name, 
                          float[][][] counts,
                          int         min_row,
                          int         min_col,
                          int         min_chan,
                          boolean     is_valid  )
  {
    if ( counts == null || counts.length <= 0 )
      throw new IllegalArgumentException(
                                    "counts array is NULL or ZERO length" );

    for ( int i = 0; i < counts.length; i++ ) 
    {
      if ( counts[i] == null || counts[i].length <= 0 )
        throw new IllegalArgumentException( 
                               "counts array["+i+"] is NULL or ZERO length" );
      if ( counts[i].length != counts[0].length )
        throw new IllegalArgumentException( 
                         "counts array["+i+"] has improper length " + 
                          counts[i].length );
     }
     // we could check further....

     this.name     = name;
     this.counts   = counts;
     this.min_row  = min_row;
     this.max_row  = min_row  + counts[0].length - 1;
     this.min_col  = min_col;
     this.max_col  = min_col  + counts[0][0].length - 1;
     this.min_chan = min_chan;
     this.max_chan = min_chan + counts.length - 1;
     this.is_valid = is_valid;

     mid_channel = counts.length / 2;
     relative_channel = 0;
  } 

  /**
   * Set offset, -2, -1, 0, 1, 2 etc. from the mid channel, that will
   * determine which slice is returned by getImageData().  The value is
   * clamped, so that the specified slice exists.
   *
   * @param rel_channel  The offset from the middle page of the counts
   *                     array.
   */
  public boolean setRelativeChannel( int rel_chan )
  {
    int temp = mid_channel + rel_chan;

    if ( temp < 0 )
    {
      relative_channel = 0 - mid_channel;
      return false;
    }

    if ( temp > counts.length - 1 )
    {
      relative_channel = counts.length - 1 - mid_channel;
      return false;
    } 

    relative_channel = rel_chan;
    return true;
  }

  /**
   *  Get the effective absolute channel number based on the relative
   *  channel number that was last specified by a call to setRelativeChannel.
   *  Specifically, this returns (min_chan + max_chan) / 2 + relative_channel;
   *
   *  @return the channel number (in the original data) of the page that 
   *          will be returned by getSlice().  
   */
  public int absoluteDisplayedChannel()
  {
    return ( min_chan + max_chan ) / 2 + relative_channel;
  }

  /**
   *  Get the "clamped" relative channel number based on the relative channel
   *  number that was last specified by a call to setRelativeChannel.
   *
   *  @return the page number (in counts[page][row][col]) of the page that 
   *          will be returned by getSlice().  
   */
  public int relativeDisplayedChannel()
  {
    return relative_channel;
  }

  /**
   *  Get a reference to the slice of data that was specified by the 
   *  last call to setRelativeChannel();
   */
  public float[][] getSlice()
  {
    return counts[ relative_channel + counts.length/2 ];
  } 
  
  /**
   *  Get the number of the first row that was extracted from the data
   *  for this peak.
   *
   *  @return the smallest column number.
   */
  public int minRow()
  {
    return min_row;
  }

  /**
   *  Get the number of the last row that was extracted from the data
   *  for this peak.
   *
   *  @return the largest row number.
   */
  public int maxRow()
  {
    return max_row;
  }

  /**
   *  Get the number of the first column that was extracted from the data
   *  for this peak.
   *
   *  @return the smallest column number.
   */
  public int minCol()
  {
    return min_col;
  }

  /**
   *  Get the number of the last column that was extracted from the data
   *  for this peak.
   *
   *  @return the largest column number.
   */
  public int maxCol()
  {
    return max_col;
  }

  /**
   *  Get the number of the first channel that was extracted from the data
   *  for this peak.
   *
   *  @return the smallest channel number.
   */
  public int minChan()
  {
    return min_chan;
  }

  /**
   *  Get the number of the last channel that was extracted from the data
   *  for this peak.
   *
   *  @return the largest channel number.
   */
  public int maxChan()
  {
    return max_chan;
  }
  
  /**
   *  Get the name of the peak to use on a titled border
   *
   *  @return the name of the peak.
   */
  public String getName()
  {
    return name;
  }

  /**
   *  Get the flag that indicates whether or not this peak is considered
   *  to be valid.
   *
   *  @return the valid flag.
   */
  public boolean isValid()
  {
    return is_valid;
  }
 
}
