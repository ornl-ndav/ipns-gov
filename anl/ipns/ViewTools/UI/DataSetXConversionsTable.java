/*
 * File:  DataSetXConversionsTable
 *
 * Copyright (C) 2000-2003, Dennis Mikkelson
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.12  2003/07/09 14:36:23  dennis
 * Added another form of the method showConversions(), that takes
 * a new XScale and calculates the y value based on Data rebinned
 * to match the new XScale.  This will make it possible to display
 * proper values in the ImageView, ThreeDView and ContourView, even
 * when the Data has been rebinned.
 *
 * Revision 1.11  2003/07/08 15:20:07  dennis
 * Added some debug prints and boolean debug flag.
 *
 * Revision 1.10  2002/11/27 23:13:34  pfpeterson
 * standardized header
 *
 * Revision 1.9  2002/07/31 16:01:48  dennis
 * Now displays info from operators that implement one of the
 * interfaces: IDataBlockInfo or IDataPointInfo
 *
 * Revision 1.8  2002/07/12 18:33:38  dennis
 * Now sets TableHeader to null so that A,B doesn't appear on
 * ContourView.
 *
 * Revision 1.7  2002/05/29 22:49:39  dennis
 * Now includes XAxisInformationOperators when generating the table and
 * gets the column labels at the time the table is regenerated, rather
 * than once when the viewer is constructed.  This allows changing the
 * labels dynamically.
 *
 * Revision 1.6  2002/03/13 16:21:43  dennis
 * Converted to new abstract Data class.
 *
 * Revision 1.5  2002/02/22 20:34:49  pfpeterson
 * Operator Reorganization.
 *
 */


package DataSetTools.components.ui;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;
import DataSetTools.dataset.*;
import DataSetTools.operator.*;
import DataSetTools.operator.DataSet.*;
import DataSetTools.operator.DataSet.Conversion.XAxis.*;
import DataSetTools.operator.DataSet.Information.XAxis.*;
import DataSetTools.retriever.*;
import DataSetTools.util.*;

/**
 * This class provides a table listing quantitative information about data in 
 * a DataSet at a specified x-value ( and possibly specified y-value ).  The
 * information includes all equivalent x-axis values for a specified x-value
 * and a specified Data block in the DataSet.
 *
 * @see DataSetTools.dataset.DataSet
 *
 * @version 1.0
 */


public class DataSetXConversionsTable  implements Serializable
{
  private DataSet    ds          = null;    // The DataSet for this table  

  private JTable     table       = null; 
  private TableModel dataModel   = null;

  private Vector     data_info_ops = null;   // List of Data information ops
  private Vector     x_info_ops    = null;   // List of X-Axis information ops

  private float      x           = 1000;     // The x, y and index value used
  private float      y           = 100;      // to calculate a number for the
  private int        index       = 0;        // table

  private boolean    x_specified = false;    // Show NaN until a value is given

  private transient boolean debug_flag = false;  // set true to include some
                                                 // debug prints 

  /* --------------------------- CONSTRUCTOR ------------------------------ */
  /**
   *  Constructs DataSetXConversionsTable for the specified DataSet.
   *
   *  @param ds  The DataSet to be used as a source of information for this
   *             table.
   */
  public DataSetXConversionsTable( DataSet ds )
  {
    this.ds = ds;
    dataModel = new DataSetXConversionTableModel();
    table = new JTable(dataModel);
    table.setTableHeader(null);                // disable table header
    table.setFont( FontUtil.LABEL_FONT );

    if ( ds == null )
    {
      System.out.println("ERROR: DataSet null in DataSetXConversionsTable "+
                         "constructor" );
      return;
    }
                                           // fill out the list of operators 
    data_info_ops = new Vector();
    x_info_ops = new Vector();
    int n_ops         = ds.getNum_operators();
    DataSetOperator op; 
    for ( int i = 0; i < n_ops; i++ )
    {
      op = ds.getOperator(i);
      if ( op instanceof IDataBlockInfo )
        data_info_ops.addElement( op );
      else if ( op instanceof IDataPointInfo )
        x_info_ops.addElement( op );
    }
  }
 
  /* -------------------------- showConversions ---------------------------- */
  /**
   *  Change the x and y value used to generate the table and repaint the 
   *  table.  If a valid table can't be shown, the table will be filled with
   *  NaN.       
   *
   *  @param  x      The x value at which the x-axis conversions should be 
   *                 calculated.
   *  @param  y      The y value to be displayed.
   *  @param  index  The index of the Data block in the DataSet that is to
   *                 be used for the x-axis conversions.
   */
 
  public void showConversions( float x, float y, int index )
  {
    if ( ds == null || x == Float.NaN )
      x_specified = false;

    else if ( index < 0 || index >= ds.getNum_entries() )
      x_specified = false; 

    else
    {
      x_specified = true;
      this.x = x;
      this.y = y;
      this.index = index;
    }

    table.repaint();     
  }

  /* -------------------------- showConversions ---------------------------- */
  /**
   *  Change the x and y value used to generate the table and repaint the
   *  table.  Since a y value is not specified in this form of the
   *  showConversions() method, a y value will be calculated.  This form of
   *  showConversions() will find the value of the data by first resampling
   *  the data, with respect to the specified x-scale.  This works well for
   *  histogram data.  It may NOT work well for function data, since more
   *  information is needed about how the function values should be resampled.
   *  This method calls the simpler showConversions(x,index) if problems
   *  are encounterd.
   *
   *  @param  x            The x value at which the x-axis conversions 
   *                       should be calculated.
   *  @param  index        The index of the Data block in the DataSet that
   *                       is to be used for the x-axis conversions.
   *  @param  new_x_scale  The XScale to be used when resampling the
   *                       Data block. 
   */
  public void showConversions( float x, int index, XScale new_x_scale )
  {
    boolean success = false;

    if ( ds   != null             &&
          x   != Float.NaN        && 
         index >= 0               && 
         index < ds.getNum_entries() )
    {                                       // try to calculate a rebinned y
      Data d = ds.getData_entry( index );
      int glb_i = new_x_scale.getI_GLB( x );
      int lub_i = new_x_scale.getI( x );
      if (glb_i == lub_i)                    // by chance, frame value
        lub_i++;                             // was on a grid point

      float glb = new_x_scale.getX( glb_i );
      float lub = new_x_scale.getX( lub_i );

      if ( glb != Float.NaN && lub != Float.NaN && glb < lub )      
      {
        new_x_scale = new UniformXScale( glb, lub, 2 );
        float y[];
        y = d.getY_values(new_x_scale, IData.SMOOTH_NONE);
        if ( y.length > 0 )
        {
          showConversions( x, y[0], index );
          success = true;
        }
      }
    }

    if ( !success )
      showConversions( x, index );                   // just use the default 
  }

  /* -------------------------- showConversions ---------------------------- */
  /**
   *  Change the x and y value used to generate the table and repaint the 
   *  table.  Since a y value is not specified in this form of the
   *  showConversions() method, a y value that corresponds to the specified
   *  x-value will be interpolated in the specified Data block and used for the
   *  table. If a valid table can't be shown, the table will be filled with
   *  NaN.  
   *
   *  @param  x      The x value at which the x-axis conversions should be
   *                 calculated.
   *  @param  index  The index of the Data block in the DataSet that is to
   *                 be used for the x-axis conversions.
   */
  public void showConversions( float x, int index )
  {
    if ( ds == null || x == Float.NaN )
      x_specified = false;

    else if ( index < 0 || index >= ds.getNum_entries() )
      x_specified = false;

    else
    {
      x_specified = true;
      this.x = x;
      if ( index < 0 || index >= ds.getNum_entries() )   // Try to calculate
        y = Float.NaN;                                   // the corresponding
      else                                               // y value from the
      {                                                  // given index and 
        Data d = ds.getData_entry( index );              // x value.  
        y = d.getY_value( x, IData.SMOOTH_LINEAR );
        if ( debug_flag )
        {
          XScale scale = d.getX_scale();
          System.out.println("N Ys = " + d.getY_values().length );
          System.out.println("x = " + x );
          System.out.println("y = " + y );
          System.out.println("XScale = " + scale );
          int i = scale.getI(x);
          System.out.println("i = " + i );
          if ( i < d.getY_values().length )
            System.out.println("y[i] = " + d.getY_values()[i]);
          else
            System.out.println("i past end of list");
        }
      }
      this.index = index;
    }

    table.repaint();
  }

  /* ----------------------------- getTable ------------------------------- */
  /**
   *  Get a reference to the actual JTable containing the display of the 
   *  values.
   * 
   *  @return  reference to the JTable with the values 
   */
  public JTable getTable()
  {
    return table;  
  }


/* -------------------------------------------------------------------------
 *
 *  INTERNAL CLASSES
 *
 */

/*
 *  The class DataSetXConversionTableModel handles the calculation of the
 *  values that are displayed in the table.  The methods in this class
 *  are called by JTable when the table needs to be generated.
 */

public class DataSetXConversionTableModel extends    AbstractTableModel
                                          implements Serializable
{

  /* --------------------------- getColumnCount --------------------------- */
  /*
   *  Returns the number of columns in the table
   */
  public int getColumnCount() 
  { 
    return 2; 
  }

  /* --------------------------- getRowCount --------------------------- */
  /*
   *  Returns the number of rows in the table
   */
  public int getRowCount() 
  { 
    int size = 2;
    if ( data_info_ops != null )
      size += data_info_ops.size();
    if ( x_info_ops != null )
      size += x_info_ops.size();
      
    return size;
  }

  /* --------------------------- getValueAt --------------------------- */
  /*
   *  Returns the value to be displayed at a specified row and column 
   */
  public Object getValueAt(int row, int col)
  { 
    if ( ds == null )
      return "NaN";

    if ( row < 0 || row > data_info_ops.size() + x_info_ops.size() + 1 )
      return "NaN"; 

    if ( col >= 1 && !x_specified )
      return "NaN";

    NumberFormat f = NumberFormat.getInstance();
    int offset = 2 + data_info_ops.size();

    if ( row == 0 )                                   // show x info
    {
      if ( col == 0 )
        return ds.getX_units();
      else
        return f.format( x );
    }
    else if ( row == 1 )                              // show y info 
    {
      if ( col == 0 )
        return ds.getY_units();
      else
        return f.format( y );
    }
    else if ( row < offset )                          // get String values from 
    {                                                 // IDataBlockInfo op 
      IDataBlockInfo op = (IDataBlockInfo)data_info_ops.elementAt(row-2);
      if ( col == 0 )
        return op.DataInfoLabel( index );
      else
        return op.DataInfo( index );
    }
    else                                              // get String values from
    {                                                 // IDataPointInfo op 
      IDataPointInfo op =(IDataPointInfo)x_info_ops.elementAt(row-offset);
      if ( col == 0 )
        return op.PointInfoLabel( x, index );
      else
        return op.PointInfo( x, index );
    }

  }
}

/* ----------------------------- main ------------------------------- */
/*
 *  Main program for testing purposes only.
 */

public static void main(String[] args)
{
  // Get a DataSet from a runfile and populate the table.
  DataSet      A_histogram_ds;
  String       run_A = "../../../SampleRuns/gppd9902.run";

  RunfileRetriever rr;
  rr = new RunfileRetriever( run_A );
  A_histogram_ds = rr.getDataSet( 1 );

  DataSetXConversionsTable DS_conversions =
                          new DataSetXConversionsTable(A_histogram_ds);

  JFrame f = new JFrame("Test DataSetXConversionsTable class");
  f.setBounds(0,0,200,200);

  JComponent table = DS_conversions.getTable();
  f.getContentPane().add(table);
  f.setVisible(true);
}

}
