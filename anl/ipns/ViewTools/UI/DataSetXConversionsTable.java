/*
 * File:  DataSetXConversionsTable
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.6  2002/03/13 16:21:43  dennis
 * Converted to new abstract Data class.
 *
 * Revision 1.5  2002/02/22 20:34:49  pfpeterson
 * Operator Reorganization.
 *
 * Revision 1.4  2001/04/23 21:50:00  dennis
 * Added copyright and GPL info at the start of the file.
 *
 * Revision 1.3  2001/01/29 21:43:19  dennis
 * Now uses CVS version numbers.
 *
 * Revision 1.2  2000/11/10 22:50:56  dennis
 * Changed to work with new Operator hierarchy.
 *
 * Revision 1.1  2000/07/10 22:18:32  dennis
 * user interface component, initial version
 *
 * Revision 1.3  2000/06/12 20:34:52  dennis
 * now implements Seriaizable
 *
 *  Revision 1.2  2000/05/11 16:54:45  dennis
 *  Added RCS logging
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

  private Vector     conv_ops    = null;     // List of X-Axis conversion ops
  private Vector     labels      = null;     // The labels for the table values

  private float      x           = 1000;     // The x, y and index value used
  private float      y           = 100;      // to calculate a number for the
  private int        index       = 0;        // table

  private boolean    x_specified = false;    // Show NaN until a value is given


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
    table.setFont( FontUtil.LABEL_FONT );

    if ( ds == null )
    {
      System.out.println("ERROR: DataSet null in DataSetXConversionsTable "+
                         "constructor" );
      return;
    }
                                           // fill out the list of operators 
    labels   = new Vector();               // and labels for the table
    labels.addElement( ds.getX_units() );
    labels.addElement( ds.getY_units() );
    conv_ops = new Vector();
    int n_ops         = ds.getNum_operators();
    DataSetOperator op; 
    for ( int i = 0; i < n_ops; i++ )
    {
      op = ds.getOperator(i);
      if ( op.getCategory() == DataSetOperator.X_AXIS_CONVERSION )
      {
        conv_ops.addElement( op );
        labels.addElement( ((XAxisConversionOp)op).new_X_label() );
      }
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
    if ( ds == null )
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
    if ( ds == null )
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
    if ( conv_ops != null )
      return 2+conv_ops.size();
    else
      return 2;
  }

  /* --------------------------- getValueAt --------------------------- */
  /*
   *  Returns the value to be displayed at a specified row and column 
   */
  public Object getValueAt(int row, int col)
  { 
    if ( ds == null || conv_ops == null )
      return "NaN";

    if ( row < 0 || row > conv_ops.size() + 1 )
      return "NaN"; 

    if ( col == 0 )
      return labels.elementAt(row);

    if ( !x_specified )
      return "NaN";

    float value = 0;
    if ( row == 0 )                                 // show current x
      value = x; 
    else if ( row == 1 )                            // show current y
      value = y;
    else                                            // do a conversion
    {
      XAxisConversionOp op = (XAxisConversionOp)conv_ops.elementAt(row-2);

      value = op.convert_X_Value( x, index );
    }

    NumberFormat f = NumberFormat.getInstance();
    return f.format( value );
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
