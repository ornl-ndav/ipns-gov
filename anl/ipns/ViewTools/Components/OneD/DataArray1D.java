/*
 * File: DataArray1D.java
 *
 * Copyright (C) 2004, Brent Serum, Mike Miller
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
 *  Revision 1.1  2004/04/16 20:21:27  millermi
 *  - Initial Version - Object taken in by the VirtualArrayList1D
 *    class.
 *
 */
 package gov.anl.ipns.ViewTools.Components.OneD;

/**
 * This class is a datastructure for containing information needed to display
 * a graph in the VirtualArrayList1D class.
 */
public class DataArray1D
{
  private boolean isSelected;
  private boolean isPointedAt;
  private String data_title;
  private float[] x_vals;
  private float[] y_vals;
  private float[] err_vals;
  
 /**
  * Constructor for initializing the x and y values.
  *
  *  @param  x_values
  *  @param  y_values
  */ 
  public DataArray1D( float[] x_values, float[] y_values )
  {
    this( x_values, y_values, null );
  }
  
 /**
  * Constructor for initializing the x, y, and error values.
  *
  *  @param  x_values
  *  @param  y_values
  *  @param  error_values
  */ 
  public DataArray1D( float[] x_values, float[] y_values, float[] error_values )
  {
    this( x_values, y_values, error_values, "", true, false );
  }
  
 /**
  * Constructor for initializing the x, y, and error values.
  *
  *  @param  x_values
  *  @param  y_values
  *  @param  error_values
  *  @param  title Title of the data.
  *  @param  selected True if graph is selected, false if unselected.
  *  @param  pointedAt True if graph is pointed-at, false if not.
  */ 
  public DataArray1D( float[] x_values, float[] y_values, float[] error_values,
                      String title, boolean selected, boolean pointedAt  )
  {
    x_vals = x_values;
    y_vals = y_values;
    err_vals = error_values;
    setTitle(title);
    setSelected(selected);
    setPointedAt(pointedAt);
  }
  
 /**
  * Get the x values.
  *
  *  @return The array of x values.
  */
  public float[] getXArray()
  {
    return x_vals;
  }
  
 /**
  * Get the y values.
  *
  *  @return The array of y values.
  */
  public float[] getYArray()
  {
    return y_vals;
  }
  
 /**
  * Get the error values.
  *
  *  @return The array of error values.
  */
  public float[] getErrorArray()
  {
    return err_vals;
  }
 
 /**
  * Set whether or not the data will be displayed.
  *
  *  @param  selected True if graph is selected, false if unselected.
  */ 
  public void setSelected( boolean selected )
  {
    isSelected = selected;
  }
  
 /**
  * Set whether or not the data will be the pointed-at graph.
  *
  *  @param  selected True if graph is pointed-at, false if not.
  */ 
  public void setPointedAt( boolean pointedAt )
  {
    isPointedAt = pointedAt;
  }
 
 /**
  * Set a title to associate with the data.
  *
  *  @param  title Title given to the data.
  */ 
  public void setTitle( String title )
  {
    data_title = title;
  }
 
 /**
  * Set a title to associate with the data.
  *
  *  @return Title given to the data.
  */ 
  public String getTitle()
  {
    return data_title;
  }
 
 /**
  * Returns whether the graph is selected or not.
  *
  *  @return True if selected, false if not.
  */ 
  public boolean isSelected()
  {
    return isSelected;
  }
 
 /**
  * Returns whether the graph is pointed-at or not.
  *
  *  @return True if pointed-at, false if not.
  */ 
  public boolean isPointedAt()
  {
    return isPointedAt;
  }
}
