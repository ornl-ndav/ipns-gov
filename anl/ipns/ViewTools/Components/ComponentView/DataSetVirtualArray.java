/*
 * File:  DataSetVirtualArray.java
 *
 * Copyright (C) 2005, Dominic Kramer
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
 * Primary   Dominic Kramer <kramerd@uwstout.edu>
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
 * $ Log: DataSetVirtualArray.java,v $
 */

package gov.anl.ipns.ViewTools.Components.ComponentView;

import gov.anl.ipns.Util.Numeric.ClosedInterval;
import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.IVirtualArrayList1D;
import DataSetTools.dataset.Attribute;
import DataSetTools.dataset.Data;
import DataSetTools.dataset.DataSet;
import DataSetTools.dataset.IData;
import DataSetTools.dataset.StringAttribute;
import DataSetTools.dataset.UniformXScale;
import DataSetTools.dataset.XScale;

//*** Note:  DataSets are immutable.  Thus all setter methods in this     ****//
//***        class do nothing.                                            ****//

/**
 * This class serves as a bridge between a <code>DataSet</code> and 
 * a VirtualArray.  That is, this class contains a <code>DataSet</code> and 
 * makes it act like an <code>IVirtualArray2D</code> and 
 * <code>IVirtualArrayList1D</code>.
 */
public class DataSetVirtualArray implements IVirtualArray2D, 
                                            IVirtualArrayList1D
{
   /**
    * The name of the <code>StringAttribute</code> for a given 
    * <code>Data</code> object (corresponding to a graph in the 
    * virtual array), which holds the graph's title.
    */
   private static final String TITLE_ATTR_NAME = "Title";
   
   /** The <code>DataSet</code> that this class encapsulates. */
   private DataSet dataSet;
   
   /**
    * The minimum x value in the <code>DataSet</code>.  For example 
    * suppose this value is 'xmin' and the minimum x value for some 
    * row in the arr is 'x'.  Then, the data enteries in the array 
    * from 'xmin' to 'x' are empty.
    */
   private float minXScaleValue;
   
   /**
    * The maximum x value in the <code>DataSet</code>.  For example 
    * suppose this value is 'xmax' and the maximum x value for some 
    * row in the arr is 'x'.  Then, the data enteries in the array 
    * from 'x' to 'xmax' are empty.
    */
   private float maxXScaleValue;
   
   /** The number of columns in this array. */
   private int numCols;
   
   /**
    * Constructs a virtual array with the given <code>DataSet</code> and 
    * number of columns.
    * 
    * @param dataSet The <code>DataSet</code> that this class encapsulates.
    * @param numCols The number of columns that this virtual array should 
    *                have.
    */
   public DataSetVirtualArray(DataSet dataSet, int numCols)
   {
      setDataSet(dataSet);
      setNumColumns(numCols);
   }
   
   /**
    * Used to get the <code>DataSet</code> that this virtual array 
    * encapsulates.
    * 
    * @return The <code>DataSet</code> that this virtual array 
    *         encapsulates.
    */
   public DataSet getDataSet()
   {
      return this.dataSet;
   }
   
   /**
    * Used to set the <code>DataSet</code> that this virtual array 
    * encapsulates.
    * 
    * @param dataSet The new <code>DataSet</code> that this virtual 
    *                array encapsulates.
    */
   private void setDataSet(DataSet dataSet)
   {
      if (dataSet == null)
         dataSet = DataSet.EMPTY_DATA_SET;
      
      this.dataSet = dataSet;
      this.minXScaleValue = Float.MAX_VALUE;
      this.maxXScaleValue = Float.MIN_VALUE;
      
      Data tmpData;
      XScale tmpXScale;
      float tmpFloat;
      for (int i=0; i<this.dataSet.getNum_entries(); i++)
      {
         tmpData = this.dataSet.getData_entry(i);
         if (tmpData == null)
            continue;
         
         tmpXScale = tmpData.getX_scale();
         if (tmpXScale == null)
            continue;
         
         tmpFloat = tmpXScale.getStart_x();
         if (tmpFloat < this.minXScaleValue)
            this.minXScaleValue = tmpFloat;
         
         tmpFloat = tmpXScale.getEnd_x();
         if (tmpFloat > this.maxXScaleValue)
            this.maxXScaleValue = tmpFloat;
      }
   }
   
   /**
    * Used to set the number of columns that this virtual array should 
    * have.  This method is designed to work with graphical displays of 
    * this virtual array.  That is, the number of columns is equal to the 
    * width of the display in pixels.  If the number of columns is too 
    * small, not all of the data may be displayed.  If the number of 
    * columns is too large, the data may appear pixelated.
    * 
    * @param numCols  The new number of columns that this virtual array 
    *                 should have.
    */
   public void setNumColumns(int numCols)
   {
      if (numCols < 0)
         numCols = 0;
      
      this.numCols = numCols;
   }
   
//-------------=[ Implemented for the IVirtualArray2D interface ]=------------//
   /**
    * Used to get the number of rows in this virtual array.  The number of 
    * rows corresponds to the number of <code>Data</code> enteries in this 
    * virtual array's <code>DataSet</code>.
    * 
    * @return The number of rows in this virtual array.
    */
   public int getNumRows()
   {
      return this.dataSet.getNum_entries();
   }

   /**
    * Used to get the number of columns in this virtual array.
    * 
    * @return The number of columns in this virtual array.
    */
   public int getNumColumns()
   {
      return this.numCols;
   }
   
   /**
    * Used to get the data value at the given row and column in the data.
    * 
    * @param row    The row in the virtual array.
    * @param column The column in the virtual array.
    * 
    * @return The data value at the specified row and column.
    */
   public float getDataValue(int row, int column)
   {
      final float ERROR_VAL = Float.NaN;
      
      Data block = this.dataSet.getData_entry(row);
      if (block == null)
         return ERROR_VAL;
      
      XScale scale = block.getX_scale();
      if (scale == null)
         return ERROR_VAL;
      
      float approxXScaleVal = 
       (this.maxXScaleValue-this.minXScaleValue)/(getNumColumns()-1)*(column)+
         this.minXScaleValue;
      
      return block.getY_value(approxXScaleVal, IData.SMOOTH_NONE);
   }
   
   /**
    * This method does nothing because objects of this class's 
    * type are immutable.
    */
   public void setAxisInfo(int axis, float min, float max, String label,
         String units, int scale)
   { /* do nothing because DataSet's are immutable. */ }

   /**
    * Used to determine if the given range of numbers is valid.  That is, 
    * this is used to determine if the interval <code>[from, to]</code> 
    * lies within <code>[0, max]</code>.
    * 
    * @param from The left bound of the interval.
    * @param to   The right bound of the interval.
    * @param max  The maximum possible value in the interval.
    * 
    * @return <code>True</code> if the interval <code>[from, to]</code> 
    *        lies within <code>[0, max]</code> and <code>false</code> 
    *        if it isn't.
    */
   private boolean isFromToValid(int from, int to, int max)
   {
      if (from > to)
         return false;
      
      if (from < 0 || to < 0)
         return false;
      
      if (from >= max || to >= max)
         return false;
      
      return true;
   }
   
   /**
    * Get the values in the virtual array at the given row in the 
    * interval <code>[from, to]</code>.
    * 
    * @param row  The row at which the data should be retrieved.
    * @param from The minimum index of the interval in which the 
    *             data should be retrieved.
    * @param to   The maximum index of the interval in which the 
    *             data should be retrieved.
    * 
    * @return The interval of retrieved data.
    */
   public float[] getRowValues(int row, int from, int to)
   {
      final float[] ERROR_VAL = new float[]{};
      
      if (!isFromToValid(from, to, getNumColumns()))
         return ERROR_VAL;
      
      float[] dataArr = new float[(to-from)+1];
      for (int i=from; i<=to; i++)
         dataArr[i] = getDataValue(row, i);
      
      return dataArr;
   }

   /**
    * This method does nothing because objects of this class's 
    * type are immutable.
    */
   public void setRowValues(float[] values, int row, int start)
   { /* do nothing because DataSet's are immutable. */ }

   /**
    * Get the values in the virtual array at the given column in the 
    * interval <code>[from, to]</code>.
    * 
    * @param column  The column at which the data should be retrieved.
    * @param from    The minimum index of the interval in which the 
    *                data should be retrieved.
    * @param to      The maximum index of the interval in which the 
    *                data should be retrieved.
    * 
    * @return The interval of retrieved data.
    */
   public float[] getColumnValues(int column, int from, int to)
   {
      final float[] ERROR_VAL = new float[]{};
      
      if (!isFromToValid(from, to, getNumRows()))
         return ERROR_VAL;
      
      float[] dataArr = new float[(to-from)+1];
      for (int i=from; i<=to; i++)
         dataArr[i] = getDataValue(i, column);
      
      return dataArr;
   }
   
   /**
    * This method does nothing because objects of this class's 
    * type are immutable.
    */
   public void setColumnValues(float[] values, int column, int start)
   { /* do nothing because DataSet's are immutable. */ }
   
   /**
    * This method does nothing because objects of this class's 
    * type are immutable.
    */
   public void setDataValue(int row, int column, float value)
   { /* do nothing because DataSet's are immutable. */ }

   public float[][] getRegionValues(int row_start, int row_stop, int col_start,
         int col_stop)
   {
      final float[][] ERROR_VAL = new float[][]{{}};
      
      if ( !isFromToValid(row_start, row_stop, getNumRows()-1) || 
           !isFromToValid(col_start, col_stop, getNumColumns()-1) )
         return ERROR_VAL;
      
      float[][] valArr = new float[row_stop-row_start][col_stop-col_start];
      for (int i=row_start; i<=row_stop; i++)
         for (int j=col_start; j<=col_stop; j++)
            valArr[i][j] = getDataValue(i, j);
      
      return valArr;
   }

   /**
    * This method does nothing because objects of this class's 
    * type are immutable.
    */
   public void setRegionValues(float[][] values, int row_start, int col_start)
   { /* do nothing because DataSet's are immutable. */ }
   
   /**
    * This method does nothing because objects of this class's 
    * type are immutable.
    * 
    * @return <code>true</code>.
    */
   public boolean setErrors(float[][] error_values)
   {
      /* do nothing because DataSet's are immutable. */
      // return true because no errors have occured
      return true;
   }

   /**
    * Used to get the array of error values corresponding to the entire 
    * virtual array.
    * 
    * @return The error values for the entire virtual array.
    */
   //TODO:  Should this return a ragged array or not
   //       Currently its returnng a ragged array
   public float[][] getErrors()
   {
      int numRows = getNumRows();
      int numCols = getNumColumns();
      float[][] valArr = new float[numRows][numCols];
      for (int i=0; i<=numRows; i++)
         for (int j=0; j<=numCols; j++)
            valArr[i][j] = getErrorValue(i, j);
      
      return valArr;
   }

   /**
    * This method does nothing because objects of this class's 
    * type are immutable.
    */
   public void setSquareRootErrors(boolean use_sqrt)
   { /* do nothing because DataSet's are immutable. */ }

   /**
    * Used to get the error value at the given row and column in the 
    * virtual array.
    * 
    * @param row    The row in the virtual array in which the error 
    *               value should be retrieved.
    * @param column The column in the virtual array in which the error 
    *               value should be retrieved.
    * 
    * @return The error value at the given row and column.
    */
   public float getErrorValue(int row, int column)
   {
      final float ERROR_VAL = Float.NaN;
      
      Data block = this.dataSet.getData_entry(row);
      if (block == null)
         return ERROR_VAL;
      
      XScale scale = block.getX_scale();
      if (scale == null)
         return ERROR_VAL;
      
      float approxXScaleVal = 
       (this.maxXScaleValue-this.minXScaleValue)/(getNumColumns()-1)*(column)+
         this.minXScaleValue;
      
      int glbIndex = block.getX_scale().getI_GLB(approxXScaleVal);
      float[] errArr = block.getErrors();
      if ( (glbIndex < 0) || (glbIndex >= errArr.length) )
         return ERROR_VAL;
      
      return errArr[glbIndex];
   }

   /**
    * Used to get this virtual array's title.
    * 
    * @return The virtual array's title which is really the 
    *         encapsulated <code>DataSet's</code> title.
    */
   public String getTitle()
   {
      return this.dataSet.getTitle();
   }

   /**
    * Used to set this virtual array's title.
    * 
    * @param title This virtual array's new title.
    */
   public void setTitle(String title)
   {
      this.dataSet.setTitle(title);
   }

   /**
    * This method does nothing because objects of this class's 
    * type are immutable.
    */
   public void setAllValues(float value)
   { /* do nothing because DataSet's are immutable. */ }

   /**
    * Used to get this virtual arrays dimension.
    * 
    * @return <code>2</code>.
    */
   public int getDimension()
   {
      return 2;
   }

   /**
    * Get an encapsulation of the information for a given axis.
    * 
    * @param axis One of: <br>
    *             <ul>
    *               <li><code>AxisInfo.X_AXIS</code></li>
    *               <li><code>AxisInfo.Y_AXIS</code></li>
    *               <li><code>AxisInfo.Z_AXIS</code></li>
    *             </ul>
    * 
    * @return The axis information for the given axis.
    */
   public AxisInfo getAxisInfo(int axis)
   {
      final AxisInfo ERROR_VAL = new AxisInfo();
      
      if (axis == AxisInfo.X_AXIS)
      {
         String units = this.dataSet.getX_units();
         String label = this.dataSet.getX_label();
         UniformXScale range = this.dataSet.getXRange();
         
         if (range == null)
            return new AxisInfo(0, 
                                0, 
                                label, 
                                units, 
                                AxisInfo.LINEAR);
         
         return new AxisInfo(range.getStart_x(), 
                             range.getEnd_x(), 
                             label, 
                             units, 
                             AxisInfo.LINEAR);
      }
      else if (axis == AxisInfo.Y_AXIS)
      {
         String units = AxisInfo.NO_UNITS;
         String label = "Group ID";
         Data block = this.dataSet.getData_entry(0);
         
         if (block == null)
            return new AxisInfo(this.dataSet.getMaxGroupID(), 
                                -1, 
                                label, 
                                units, 
                                AxisInfo.LINEAR);
         
         return new AxisInfo(this.dataSet.getMaxGroupID(), 
                             block.getGroup_ID(), 
                             label, 
                             units, 
                             AxisInfo.LINEAR);
      }
      else if (axis == AxisInfo.Z_AXIS)
      {
         //the Z axis of the view is the Y axis of the DataSet
         
         String units = this.dataSet.getY_units();
         String label = this.dataSet.getY_label();
         ClosedInterval range = this.dataSet.getYRange();
         
         if (range == null)
            return new AxisInfo(0, 
                                0, 
                                label, 
                                units, 
                                AxisInfo.LINEAR);
         
         return new AxisInfo(range.getStart_x(), 
                             range.getEnd_x(), 
                             label, 
                             units, 
                             AxisInfo.LINEAR);
      }
      
      return ERROR_VAL;
   }

   /**
    * This method does nothing because objects of this class's 
    * type are immutable.
    */
   public void setAxisInfo(int axis, AxisInfo info)
   { /* do nothing because DataSet's are immutable. */ }
//-----------=[ End implemented for the IVirtualArray2D interface ]=----------//


//-----------=[ Implemented for the IVirtualArrayList1D interface ]=----------//
   /**
    * This method is used to get the array of x values that correspond to the 
    * given row in the data.
    * 
    * @param graph_number Specifies the graph to view.  This number is the 
    *                     index of a row in the data.
    * 
    * @return The x values for the specified graph.
    */
   public float[] getXValues(int graph_number)
   {
      final float[] ERROR_VAL = new float[]{};
      
      Data graph = this.dataSet.getData_entry(graph_number);
      if (graph == null)
         return ERROR_VAL;
      
      return graph.getX_values();
   }

   /**
    * This method is used to get the array of y values that correspond to the 
    * given row in the data.
    * 
    * @param graph_number Specifies the graph to view.  This number is the 
    *                     index of a row in the data.
    * 
    * @return The y values for the specified graph.
    */
   public float[] getYValues(int graph_number)
   {
      final float[] ERROR_VAL = new float[]{};
      
      Data graph = this.dataSet.getData_entry(graph_number);
      if (graph == null)
         return ERROR_VAL;
      
      return graph.getCopyOfY_values();
   }

   /**
    * This method does nothing because objects of this class's 
    * type are immutable.
    */
   public void setXYValues(float[] x_values, float[] y_values, 
                           float[] errors, String graph_title, int graph_num)
   { /* do nothing because DataSet's are immutable. */ }

   /**
    * This method is used to get the array of error values that correspond 
    * to the given row in the data.
    * 
    * @param graph_number Specifies the graph to view.  This number is the 
    *                     index of a row in the data.
    * 
    * @return The error values for the specified graph.
    */
   public float[] getErrorValues(int graph_number)
   {
      final float[] ERROR_VAL = new float[]{};
      
      Data graph = this.dataSet.getData_entry(graph_number);
      if (graph == null)
         return ERROR_VAL;
      
      return graph.getCopyOfErrors();
   }

   /**
    * Used to set the title of the graph at index <code>graph_num</code>.
    * 
    * @param title     The graph's new title.
    * @param graph_num The index of the graph whose title should be 
    *                  changed.  This number should be the index of some 
    *                  row in the data.
    * 
    * @return An empty string if an error occured or the title given 
    *         if the title was successfully set.
    */
   public String setGraphTitle(String title, int graph_num)
   {
      final String ERROR_VAL = "";
      Data graph = this.dataSet.getData_entry(graph_num);
      if (graph == null)
         return ERROR_VAL;
      
      graph.setAttribute(new StringAttribute(TITLE_ATTR_NAME, title));
      return getGraphTitle(graph_num);
   }

   /**
    * Used to get the given graph's title.  If the 
    * <code>setGraphTitle(String, int)</code> method was not used to 
    * specify a custom title, a default value of 
    * "Group:  <GROUP ID>" is used.  Here the group ID corresponds the 
    * the ID of the graph in the <code>DataSet</code> that this class 
    * encapsulates.
    * 
    * @param graph_num The index of the graph whose title should be accessed.
    * 
    * @return The corresponding graph's title.
    */
   //QUESTION:  Should the graph's title be its group ID
   public String getGraphTitle(int graph_num)
   {
      final String ERROR_VAL = "";
      Data graph = this.dataSet.getData_entry(graph_num);
      if (graph == null)
         return ERROR_VAL;
      
      Attribute attr = graph.getAttribute(TITLE_ATTR_NAME);
      if ( (attr == null) || !(attr instanceof StringAttribute) )
         return "Group:  "+graph.getGroup_ID();
      
      return attr.getStringValue();
   }

   /**
    * Used to get the number of graphs selected by the user.
    * 
    * @return The number of selected graphs.
    */
   public int getNumSelectedGraphs()
   {
      return this.dataSet.getNumSelected();
   }

   /**
    * Used to get the indices of the graphs selected by the user.
    * 
    * @return The indices of the selected graphs.
    */
   public int[] getSelectedIndexes()
   {
      return this.dataSet.getSelectedIndices();
   }

   /**
    * Used to get the index of the graph that is currently being pointed 
    * at by the user.
    * 
    * @return The index of the graph being pointed at by the user.
    */
   public int getPointedAtGraph()
   {
      return this.dataSet.getPointedAtIndex();
   }

   /**
    * Used to determine if the graph at the given index is currently 
    * selected by the user.
    * 
    * @param index The index of the graph in question.
    * 
    * @return <code>True</code> if the graph is selected and false 
    *         otherwise.
    */
   public boolean isSelected(int index)
   {
      return this.dataSet.isSelected(index);
   }

   /**
    * Used to get the number of graphs.
    * 
    * @return The number of graphs.
    */
   public int getNumGraphs()
   {
      return this.dataSet.getNum_entries();
   }
//---------=[ End implemented for the IVirtualArrayList1D interface ]=--------//
}
