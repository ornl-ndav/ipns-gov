/*
 * File: TableModelMaker.java
 *
 * Copyright (C) 2004, Mike Miller
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
 *  Revision 1.2  2004/08/17 20:55:28  millermi
 *  - setValueAt() no longer edits the IVirtualArray2D. Code must be
 *    uncommented to restore this functionality.
 *
 *  Revision 1.1  2004/08/04 18:49:27  millermi
 *  - Initial Version - This class is used to convert IVirtualArrays
 *    into TableModels.
 *
 */
package gov.anl.ipns.ViewTools.Panels.Table;

import javax.swing.JFrame;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelListener;

import gov.anl.ipns.ViewTools.Components.IVirtualArray;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.Util.Sys.WindowShower;


/**
 * This class converts various types of data into a TableModel so it may
 * be viewed in a table. Each type of data has its own getModel() method
 * to define how the table will lay out the data. This TableModel assumes
 * that the cells should NOT be edited, and thus isCellEditable() will always
 * return false.
 */ 
public class TableModelMaker
{
 /*
  * No constructor needed, do not allow an instance to be created.
  */
  private TableModelMaker() {}
 
 /* ---------------------- Two Dimensional Conversion -----------------------*/
 /**
  * Convert a IVirtualArray2D object into a TableModel so the array can be
  * viewed in a JTable.
  *
  *  @param  varray The IVirtualArray2D object to be converted.
  *  @return The TableModel of how the IVirtualArray2D object will appear in
  *          a JTable.
  */ 
  public static TableModel getModel( IVirtualArray2D varray )
  {
    return getModel( varray, null );
  } // End of getTableModel(IVirtualArray2D)
 
 /**
  * Convert a IVirtualArray2D object into a TableModel so the array can be
  * viewed in a JTable.
  *
  *  @param  varray The IVirtualArray2D object to be converted.
  *  @param  column_labels A list of column labels for the table. The length
  *                        of this list should be equal to the number of
  *                        columns. 
  *  @return The TableModel of how the IVirtualArray2D object will appear in
  *          a JTable.
  */ 
  public static TableModel getModel( IVirtualArray2D varray,
                                     String[] column_labels )
  {
    return new TableModel2D( varray, column_labels );
  } // End of getTableModel(IVirtualArray2D,String[])
  
 /*
  * This TableModel is used by the getModel(IVirtualArray2D) methods.
  */
  private static class TableModel2D extends DefaultTableModel
  {
    private IVirtualArray2D iva;
    private String[] column_labels;
    protected TableModel2D( IVirtualArray2D array, String[] labels )
    {
      super(0,0);
      iva = array;
      column_labels = labels;
      if( iva == null )
      {
	return;
      }
      setRowCount( iva.getNumRows() );
      setColumnCount( iva.getNumColumns() );
    }
    
   /*
    * Get the column name, by default specify the column number.
    */ 
    public String getColumnName(int index)
    {
      // If index is invalid, return an empty String.
      if( index < 0 || index >= getColumnCount() )
  	return "";

      // If valid index, and the index is in the column_labels array,
      // return the value stored in the column_labels array.
      if( column_labels != null && index < column_labels.length )
  	return column_labels[index];
      
      // If index is valid, but the column_labels array is smaller than
      // the number of columns, then return the label "Column #". 
      return new String("Column " + index);
    }
      
    public Object getValueAt( int row, int column )
    {
      // Make sure iva is not null.
      if( iva == null )
        return null;
      return new Float( iva.getDataValue(row,column) );
    }
    
    public void setValueAt( Object float_value, int row, int column )
    {
      return;
     /*
      * The code below will change values in the VirtualArray when this
      * method is called. However, since this class assumes the data is NOT
      * editable, this method currently does not edit the VirtualArray.
      * To have this method edit the VirtualArray, uncomment this code and
      * remove the return statement above.
      *
      // Make sure iva is not null.
      if( iva == null )
        return;
      float value; 
      if( float_value instanceof Number )
      {
        value = ((Number)float_value).floatValue();
        iva.setDataValue(row,column,value);
      }
      else if( float_value instanceof String )
      {
        try
	{
	  value = Float.parseFloat( (String)float_value );
          iva.setDataValue(row,column,value);
	}
	catch( NumberFormatException nfe ) { return; }
      }
      // Otherwise do nothing.
      *
      */
    }
    
   /*
    * Always prevent the data from being editable, unless otherwise specified.
    */ 
    public boolean isCellEditable( int row, int column ){ return false; }
  }
 /* ------------------ End Of Two Dimensional Conversion --------------------*/
 
 /**
  * For testing purposes only.
  *
  *  @param  args Parameter not used in testing.
  */ 
  public static void main( String args[] )
  {
    int row = 30;
    int col = 40;
    float[][] values = new float[row][col];
    String[] labels = new String[col+2];
    
    for( int i = 0; i < row; i++ )
    {
      for( int j = 0; j < col; j++ )
      {
        values[i][j] = (float)(i*2 + j);
	if( i == 0 && j < labels.length )
	  labels[j] = new String("Test "+j);
      }
    }
    // Check for arrays that are too large.
    labels[col] = new String("Error 1");
    labels[col+1] = new String("Error 2");
    
    VirtualArray2D va = new VirtualArray2D(values);
    JFrame frame = new JFrame("TableModelMaker Test");
    frame.setBounds(0,0,600,300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add( 
             new TableJPanel(TableModelMaker.getModel(va,labels)) );
    WindowShower shower = new WindowShower(frame);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }
}
