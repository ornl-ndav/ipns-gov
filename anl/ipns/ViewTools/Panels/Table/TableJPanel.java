/*
 * File: TableJPanel.java
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
 *  Revision 1.4  2004/07/23 07:38:53  millermi
 *  - Updated how row and column labels were stored and displayed.
 *  - Added setRowLabels() and setColumnLabels() methods to modify
 *    labels.
 *  - Added getRowCount() and getColumnCount() so programmers can get the
 *    dimensions of the data.
 *  - Added drag & drop ability for columns, now can be reordered using
 *    mouse drags.
 *  - Added methods moveColumn() and enableMoveColumn().
 *
 *  Revision 1.3  2004/07/06 07:17:02  millermi
 *  - Improved convertToTableRegions() so that selection order is now
 *    maintained.
 *  - Now implements IPreserveState, including static variables to save
 *    various state information.
 *  - Improved main() test program.
 *  - setPointedAtCell() now moves the Viewport to the selected cell.
 *  - All Points now use convention (x=column,y=row) to maintain
 *    consistency with the TableRegion, which was maintaining consistency
 *    with all other Regions.
 *  - Added method getSelectedCells() that is an alternative to
 *    getSelectedRegions(), only the "shape" of selections is not
 *    maintained.
 *
 *  Revision 1.2  2004/05/22 20:29:52  millermi
 *  - Added ExcelAdapter to allow copy and paste capabilities.
 *  - Added method convertToTableRegion() to convert any Region
 *    to a TableRegion.
 *
 *  Revision 1.1  2004/05/12 21:38:30  millermi
 *  - New Directory added to Panels directory.
 *  - Initial Version of TableJPanel - This class parallels the
 *    ImageJPanel and the CoordJPanel.
 *
 */
package gov.anl.ipns.ViewTools.Panels.Table;
 
import IsawGUI.ExcelAdapter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Point;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import gov.anl.ipns.ViewTools.UI.ActiveJPanel;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.IPreserveState;
import gov.anl.ipns.ViewTools.Components.IVirtualArray;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Components.IVirtualArrayList1D;
import gov.anl.ipns.ViewTools.Components.Region.Region;
import gov.anl.ipns.ViewTools.Components.Region.PointRegion;
import gov.anl.ipns.ViewTools.Components.Region.TableRegion;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.WindowShower;

/**
 * This class has many of the capabilities of the JTable. The TableJPanel
 * allows for random selection, with selection methods parallelling that
 * of a typical spreadsheet program. This class does not extend JTable, thus
 * none of the JTable methods apply.
 */
public class TableJPanel extends ActiveJPanel implements IPreserveState
{
 /**
  * "Selected Changed" - This messaging String is sent out whenever the
  * selection of cells is changed.
  */
  public static final String SELECTED_CHANGED = "Selected Changed";
  
 /**
  * "Pointed At Changed" - This messaging String is sent out whenever the
  * pointed at cell is changed.
  */
  public static final String POINTED_AT_CHANGED = "Pointed At Changed";
 
 // The following variables are String keys used to preserve state information. 
 /**
  * "Label Background" - This String key is used to preserve state information
  * of the background color used for the label display. The value this key
  * references is of type Color.
  */
  public static final String LABEL_BACKGROUND = "Label Background";
 
 /**
  * "Show Row Labels" - This String key is used to preserve state information
  * which determines whether or not the row labels are displayed on the table.
  * The value this key references is of type Boolean.
  */
  public static final String SHOW_ROW_LABELS = "Show Row Labels";
 
 /**
  * "Show Column Labels" - This String key is used to preserve state information
  * which determines whether or not the column labels are displayed on the
  * table. The value this key references is of type Boolean.
  */
  public static final String SHOW_COLUMN_LABELS = "Show Column Labels";
   
 /**
  * "Pointed At Cell" - This String key is used to preserve state information
  * identifying the last pointed-at cell. The value this key references is
  * a Point with (x=column,y=row).
  */ 
  public static final String POINTED_AT_CELL = "Pointed At Cell";
   
 /**
  * "Table Selections" - This String key is used to preserve state information
  * identifying selections made on this table. The value this key references is
  * an array of TableRegions.
  */ 
  public static final String TABLE_SELECTIONS = "Table Selections";
  
  private final float SHIFT_DIVISOR = 4f; // 1/SHIFT_DIVISOR controls when
                                          // the table is shifted to the next
					  // row and/or column.
  private Point anchor = new Point(); // The beginning of the initial selection.
  private Point extend = null; // The variable end of the initial selection.
  private Point ctrl_anchor = null; // The beginning of an additional selection.
  private Point ctrl_extend = null; // The end of a changing selection.
  private Point focus = null; // The pix. pt. of the cell that has focus.
  private JTable table; // The table that will display the data.
  private JScrollPane scroll; // The scrollpane containing the table.
  private boolean[][] selected; // The grid used to determine selected cells.
  private boolean active_selection; // true if mouse pressed, dragged, but
                                    // not released.
  private JTable column_labels; // The column labels.
  private JTable row_labels; // The row labels.
  private JPanel column_label_container; // The column labels and spacers.
  private JPanel row_label_container; // The row labels and spacers.
  private Vector column_label_list; // List of labels, one for each column.
  private Vector row_label_list; // List of labels, one for each row.
  private Color label_color; // Background color of the labels.
  private Vector selections; // List of TableRegions
  private boolean ignore_notify; // This variable is used to limit the
                                     // number of selected_changed messages
				     // if setSelectedRegions() is called.
  private boolean do_clear;  // This will prevent a region from being added
                             // when the UnselectAll() method is called.
  private Point initial_column; // Initial column selected for drag & drop
  private Point current_column; // Current column selected for drag & drop
  private int column_label_offset = 0; // Offset for labels to match viewable
                                       // columns.
  private boolean isMobile;
 /**
  * Default Constructor
  */ 
  public TableJPanel()
  {
    super();
    table = new JTable();
    init();    
  }
  
 /**
  * Constructor with setable TableModel. Use this constructor to pass in an
  * array of data to be displayed by this table.
  *
  *  @param  tm The TableModel containing displayable data.
  */ 
  public TableJPanel( TableModel tm )
  {
    super();
    table = new JTable(tm);
    init();
  }
  
 /**
  * Constructor that takes in the dimensions of the table. This will
  * create an empty table with a specified number of rows and columns.
  *
  *  @param  num_rows The number of rows for this table.
  *  @param  num_columns The number of columns for this table.
  */ 
  public TableJPanel( int num_rows, int num_columns )
  {
    super();
    table = new JTable( num_rows, num_columns );
    init();
  }
  
 /*
  * This method is used by the constructors to initialize the TableJPanel.
  */ 
  private void init()
  {
    setLayout( new BorderLayout() );
    int rows = table.getModel().getRowCount();
    int columns = table.getModel().getColumnCount();
    selected = new boolean[rows][columns];
    active_selection = false;
    label_color = Color.lightGray;
    selections = new Vector();
    ignore_notify = false;
    do_clear = false;
    isMobile = true;
    // Add copy and paste ability.
    new ExcelAdapter( table );
    
    table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
    table.setCellSelectionEnabled( false );
    scroll = new JScrollPane(table);
    scroll.getHorizontalScrollBar().addAdjustmentListener(new ScrollAdjusted());
    scroll.getVerticalScrollBar().addAdjustmentListener( new ScrollAdjusted() );
    add(scroll, BorderLayout.CENTER );
    
    // Make sure table isn't empty.
    if( table.getColumnCount() > 0 )
      table.setDefaultRenderer( table.getColumnClass(0), new CellRenderer() );
    table.addMouseMotionListener( new SelectionChanged() );
    table.addMouseListener( new SelectionStarted() );
    // put in dummy values
    row_labels = new JTable();
    column_labels = new JTable();
    // Add these two listeners to control column drag & drop.
    column_labels.addMouseListener( new ColumnSelectListener() );
    column_labels.addMouseMotionListener( new DragListener() );
    // Initialize the label lists to contain "Column #" or "Row #".
    row_label_list = new Vector();
    column_label_list = new Vector();
    for( int row_num = 0; row_num < rows; row_num++ )
      row_label_list.add("Row "+row_num);
    for( int col_num = 0; col_num < columns; col_num++ )
      column_label_list.add("Column "+col_num);
    column_label_container = new JPanel( new BorderLayout() );
    column_label_container.add( column_labels, BorderLayout.CENTER );
    row_label_container = new JPanel( new BorderLayout() );
    row_label_container.add( row_labels, BorderLayout.CENTER );
    add( row_label_container, BorderLayout.WEST );
    add( column_label_container, BorderLayout.NORTH );
    
    addComponentListener( new ResizedListener() );
  }

 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    boolean redraw = false;  // if any values are changed, repaint table.
    Object temp = new_state.get(POINTED_AT_CELL);
    if( temp != null )
    {
      setPointedAtCell( ((Point)temp).y, ((Point)temp).x );
      redraw = true;  
    }
    
    temp = new_state.get(LABEL_BACKGROUND);
    if( temp != null )
    {
      setLabelBackground( (Color)temp );
      redraw = true;  
    }
    
    temp = new_state.get(SHOW_ROW_LABELS);
    if( temp != null )
    {
      displayRowLabels( ((Boolean)temp).booleanValue() );
      redraw = true;  
    }
    
    temp = new_state.get(SHOW_COLUMN_LABELS);
    if( temp != null )
    {
      displayColumnLabels( ((Boolean)temp).booleanValue() );
      redraw = true;  
    }
    
    temp = new_state.get(TABLE_SELECTIONS);
    if( temp != null )
    {
      setSelectedRegions((TableRegion[])temp);
      redraw = true;  
    }
  }
  
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  is_default True if default state, use static variable.
  *  @return if true, the selective default state, else the state for with
  *          all possible saved values.
  */ 
  public ObjectState getObjectState( boolean is_default )
  {
    ObjectState state = new ObjectState();
    state.insert( SHOW_ROW_LABELS,
                  new Boolean(row_label_container.isVisible()) );
    state.insert( SHOW_COLUMN_LABELS,
                  new Boolean(column_label_container.isVisible()) );
    state.insert( LABEL_BACKGROUND, label_color );
    
    // Only do the following if this is a project save, not a setting save.
    if( !is_default )
    {
      state.insert( POINTED_AT_CELL, getPointedAtCell() );
      state.insert( TABLE_SELECTIONS, getSelectedRegions() );
    }
    
    return state;
  }
  
 /**
  * This method will return whether or not a cell is selected. If a selection
  * of cells is actively occurring while this method is called, the cells within
  * the active region will be judged as they appear, even though changing the
  * active region will affect whether or not these cells are selected. It is
  * safest to call this method after a selection is complete.
  *
  *  @param  row The cell row.
  *  @param  column The cell column.
  *  @return True if selected or appear as selected if within an active
  *          selection.
  */ 
  public boolean isSelected( int row, int column )
  {
    // If a selection is in progress, treat the selection of cells in their
    // current state.
    if( active_selection )
    {
      // Find the row/column bounds of the active selection.
      int act_row_min = -1;
      int act_row_max = -1;
      int act_col_min = -1;
      int act_col_max = -1;
      // The selection value of the anchor cell, which determines the value
      // for all other cells during that selection.
      boolean is_selected = false;
      // If CTRL was used to add/remove a region of cells
      if( ctrl_anchor != null )
      {
        Point row_col_ctrl_anchor = convertPixelToColumnRow(ctrl_anchor);
        act_col_min = row_col_ctrl_anchor.x;
	act_row_min = row_col_ctrl_anchor.y;
	// If active selection encompasses more than one cell.
        if( ctrl_extend != null )
	{
	  Point row_col_ctrl_extend = convertPixelToColumnRow(ctrl_extend);
	  act_col_max = row_col_ctrl_extend.x;
	  act_row_max = row_col_ctrl_extend.y;
	}
	// If active selection is only one cell.
	else
	{
          act_col_max = row_col_ctrl_anchor.x;
	  act_row_max = row_col_ctrl_anchor.y;
	}
	is_selected = selected[row_col_ctrl_anchor.y][row_col_ctrl_anchor.x];
      }
      // Region is being drawn using the mouse to drag and select a single
      // region of cells.
      else
      {
        Point row_col_anchor = convertPixelToColumnRow(anchor);
        act_col_min = row_col_anchor.x;
	act_row_min = row_col_anchor.y;
	// If active selection encompasses more than one cell.
        if( extend != null )
	{
	  Point row_col_extend = convertPixelToColumnRow(extend);
	  act_col_max = row_col_extend.x;
	  act_row_max = row_col_extend.y;
	}
	// If active selection is only one cell.
	else
	{
          act_col_max = row_col_anchor.x;
	  act_row_max = row_col_anchor.y;
	}
	is_selected = selected[row_col_anchor.y][row_col_anchor.x];
      }
      // Make sure row and column values are ordered correctly.
      if( act_row_min > act_row_max )
      {
        int temp = act_row_min;
	act_row_min = act_row_max;
	act_row_max = temp;
      }
      
      if( act_col_min > act_col_max )
      {
        int temp = act_col_min;
	act_col_min = act_col_max;
	act_col_max = temp;
      }
      // If row and column fall within the active selection, return the
      // inverse selection value of the anchor.
      if( (row >= act_row_min && row <= act_row_max) &&
          (column >= act_col_min && column <= act_col_max) )
      {
        return !is_selected;
      }
    }
    // If no selection is in progress, or the point is not within the active
    // selection just look at the boolean grid.
    return selected[row][column];
  }

 /**
  * Set the background color for the cells where the row and column labels
  * appear. The default is light gray.
  *
  *  @param  color The background color of the cells containing labels.
  */  
  public void setLabelBackground( Color color )
  {
    label_color = color;
  }
  
 /**
  * Choose whether or not to display the row labels found to the left of
  * the rows.
  *
  *  @param  display True to display the row labels, false to hide them.
  */
  public void displayRowLabels( boolean display )
  {
    if( display )
      row_label_container.setVisible(true);
    else
      row_label_container.setVisible(false);
  }
  
 /**
  * Choose whether or not to display the column labels found above the columns.
  *
  *  @param  display True to display the column labels, false to hide them.
  */
  public void displayColumnLabels( boolean display )
  {
    if( display )
      column_label_container.setVisible(true);
    else
      column_label_container.setVisible(false);
  }
  
 /**
  * Use this method to change the all of the labels used to identify rows.
  * The new list of labels must exactly match the number of rows existing
  * in the table.
  *
  *  @param  labels An array of labels, with size exactly matching the number
  *                 of rows in the table.
  */
  public void setRowLabels( Object[] labels )
  {
    // If not enough/too many labels, ignore the request.
    if( labels.length != row_label_list.size() )
      return;
    
    // Clear the old list of labels.
    row_label_list.clear();
    for( int i = 0; i < labels.length; i++ )
      row_label_list.addElement(labels[i]);
  }
  
 /**
  * Use this method to change the all of the labels used to identify columns.
  * The new list of labels must exactly match the number of columns existing
  * in the table.
  *
  *  @param  labels An array of labels, with size exactly matching the number
  *                 of columns in the table.
  */
  public void setColumnLabels( Object[] labels )
  {
    // If not enough/too many labels, ignore the request.
    if( labels.length != column_label_list.size() )
      return;
    
    // Clear the old list of labels.
    column_label_list.clear();
    for( int i = 0; i < labels.length; i++ )
      column_label_list.addElement(labels[i]);
  }
  
 /**
  * Set the cell which has the pointed-at focus border around it.
  *
  *  @param  row The cell row.
  *  @param  column The cell column.
  */ 
  public void setPointedAtCell( int row, int column )
  {
    // If one of the two is not true, do nothing.
    if( !(isValidRow(row) && isValidColumn(column)) )
      return;
    
    // Find pixel coordinates for the center of the cell.
    Rectangle cell = table.getCellRect(0,0,true);
    int pixel_x = cell.width/2 + cell.width*column;
    int pixel_y = cell.height/2 + cell.height*row;
    focus = new Point(pixel_x,pixel_y);
    
    // Place the selected cell in the upper-lefthand corner of the Viewport,
    // when possible.
    Rectangle viewport = scroll.getViewport().getViewRect();
    int x_pos = cell.width * column;
    int y_pos = cell.height * row;
    scroll.getViewport().setViewPosition( new Point(x_pos,y_pos) );
    send_message( POINTED_AT_CHANGED );
  }
  
 /**
  * Get the (x=column, y=row) cell which has the PointedAt border around it.
  *
  *  @return A Point containing the column and row of the cell.
  */ 
  public Point getPointedAtCell()
  {
    return convertPixelToColumnRow(focus);
  }
  
 /**
  * Get the number of rows in this table.
  *
  *  @return The number of rows in this table.
  */
  public int getRowCount()
  {
    return table.getRowCount();
  }
  
 /**
  * Get the number of columns in this table.
  *
  *  @return The number of columns in this table.
  */
  public int getColumnCount()
  {
    return table.getColumnCount();
  }
  
 /**
  * Set the value in the cell located at the row and column specified.
  *
  *  @param  value The value to be set into the cell.
  *  @param  row The row where the cell is located.
  *  @param  column The column where the cell is located.
  */
  public void setValueAt( Object value, int row, int column )
  {
    // If one of the two is not true, do nothing.
    if( !(isValidRow(row) && isValidColumn(column)) )
      return;
    table.setValueAt( value, row, column );
    table.repaint();
  }
 
 /**
  * Convert a pixel point to a corresponding cell. The cell is specified by
  * a Point pt, with pt.x = row # and pt.y = column # of the cell.
  *
  *  @param  pixel_pt The pixel point to be converted.
  *  @return A Point with cell row stored in x and cell column stored in y.
  */ 
  public Point convertPixelToColumnRow( Point pixel_pt )
  {
    if( pixel_pt == null )
      return null;
    return new Point( table.columnAtPoint(pixel_pt),
		      table.rowAtPoint(pixel_pt) );
  }
  
 /**
  * Set all of the cells in this table to selected.
  */
  public void selectAll()
  {
    // Remove all previous selections.
    UnselectAll();
    
    // Mark all of the grid entries to selected.
    for( int row = 0; row < selected.length; row++ )
      for( int col = 0; col < selected[0].length; col++ )
        selected[row][col] = true;
    
    // Add a new TableRegion to the selections list that covers the whole table.
    // Note that the (x,y) point is actually (column,row).
    floatPoint2D[] def_pts = new floatPoint2D[2];
    def_pts[0] = new floatPoint2D(0,0);
    def_pts[1] = new floatPoint2D( (float)(selected[0].length-1),
                                   (float)(selected.length-1) );
    selections.add( new TableRegion(def_pts, true) );
    // Update the table.
    table.repaint();
    send_message( SELECTED_CHANGED );
  }
  
 /**
  * This method will clear all selections, both in the vector and on the
  * grid. This will not clear the table entries.
  */
  public void UnselectAll()
  {
    // If there are no selections, do not send out the selected_changed message.
    if( selections.size() == 0 )
      ignore_notify = true;
    selections.clear();
    do_clear = true;
    setSelectedCells( 0, table.getModel().getRowCount()-1,
	              0, table.getModel().getColumnCount()-1, false);
    do_clear = false;
    ignore_notify = false;
  }
  
 /**
  * Move a column from its current index to a new index. Column mobility
  * can also be achieved interactively by doing drag & drop on the column
  * labels. Any column moves will void all existing selections, so column
  * moves should be made before any selections occur. 
  *
  *  @param 
  */
  public void moveColumn( int column_to_move, int target_column_index )
  {
    // If moveColumn() has been disabled, do nothing.
    if( !isMobile )
      return;
    // If they are equal, do nothing.
    if( column_to_move == target_column_index )
      return;
    // Make sure column_to_move is valid. If not, do nothing.
    if( !isValidColumn( column_to_move ) )
      return;
    // Make sure target_column_index is a valid option.
    if( target_column_index < 0 )
      target_column_index = 0;
    else if( target_column_index >= selected[0].length )
      target_column_index = selected[0].length - 1;
    // Since column order is changing, selections are no longer stable.
    UnselectAll();
    // Move the column label and the column of data.
    table.moveColumn( column_to_move, target_column_index );
    // Reorder the saved list of column labels.
    column_label_list.insertElementAt( 
    	 column_label_list.remove(column_to_move), target_column_index );
    // Visually update the labels.
    updateLabels();
  }
  
 /**
  * Enable (true) or disable (false) the ability to move columns. This
  * option is enabled by default.
  *
  *  @param  is_mobile If true, column mobility is enabled.
  */
  public void enableMoveColumn( boolean is_mobile )
  {
    isMobile = is_mobile;
  }
  
 /**
  * Use this method to get all the selected cells without preserving the
  * "shape" of selections. The list of (column,row) points is wrapped in
  * a single PointRegion instance.
  *
  *  @return A single PointRegion containing a list of selected cells.
  */
  public PointRegion getSelectedCells()
  {
    // Make a dynamic list of all the cells.
    Vector selected_cells = new Vector();
    for( int row = 0; row < selected.length; row++ )
    {
      for( int col = 0; col < selected[0].length; col++ )
      {
        // If selected, add it to the list of selected cells.
        if( selected[row][col] )
	{
	  selected_cells.add( new floatPoint2D(col,row) );
	}
      }
    }
    
    // Convert the dynamic array of cells to a static array of floatPoint2D
    // to be passed into a single PointRegion.
    floatPoint2D[] pts = new floatPoint2D[selected_cells.size()];
    for( int cell = 0; cell < pts.length; cell++ )
      pts[cell] = new floatPoint2D( 
                          ((floatPoint2D)selected_cells.elementAt(cell)) );
    return new PointRegion( pts );
  }
  
 /**
  * Get the selected cells in the form of a set of TableRegions. Use this
  * method to preserve the "shape" of how cells were selected.
  *
  *  @return The set of TableRegions that defines the selected cells.
  */
  public TableRegion[] getSelectedRegions()
  {
    TableRegion[] tr = new TableRegion[selections.size()];
    for( int i = 0; i < tr.length; i++ )
    {
      tr[i] = (TableRegion)selections.elementAt(i);
    }
    return tr;
  }
  
 /**
  * Set the selected cells in the form of a set of TableRegions. This call
  * can be used to set a complicated pattern of selected cells. Calling
  * this method will clear all previously selected cells. Passing in null will
  * clear all selections.
  *
  *  @param  regions The regions, each of which defines a rectangular region
  *                  of cells that are either all selected or all unselected.
  *                  Since regions may overlap, order is important.
  */
  public void setSelectedRegions( TableRegion[] regions )
  {
    // Set ignore_notify to true so UnselectAll() doesn't send out a
    // SELECTED_CHANGED message.
    ignore_notify = true;
    // If this method is called, clear all previous selections.
    // Note that the (x,y) point is actually stored as (column,row).
    UnselectAll();
    // If regions is null, clear all selections and that is it.
    if( regions == null )
      return;
    // Use this to have other methods not send out messages since this
    // method already does. Have to reset to true since UnselectAll() sets it
    // to false.
    ignore_notify = true;
    
    Point p1;
    Point p2;
    floatPoint2D[] def_pts;
    for( int i = 0; i < regions.length; i++ )
    {
      def_pts = regions[i].getDefiningPoints(Region.WORLD);
      p1 = def_pts[0].toPoint();
      p2 = def_pts[1].toPoint();
      setSelectedCells( p1.y, p2.y, p1.x, p2.x, regions[i].isSelected() );
    }
    ignore_notify = false;
    send_message( SELECTED_CHANGED );
    table.repaint();
  }
  
 /**
  * This method will add a selected or unselected rectangular region to the
  * table. Since regions may overlap, the order in which regions are added
  * is important. Previous selections will be retained using this method.
  *
  *  @param  region A rectangular region of cells that are
  *                 either all selected or all unselected.
  */
  public void addSelectedRegion( TableRegion region )
  {
    floatPoint2D[] def_pts = region.getDefiningPoints(Region.WORLD);
    Point p1 = def_pts[0].toPoint();
    Point p2 = def_pts[1].toPoint();
    // Note that the (x,y) point is actually stored as (column,row).
    setSelectedCells( p1.y, p2.y, p1.x, p2.x, region.isSelected() );
    table.repaint();
  }
  
 /**
  * This method will take a list of any type of region and convert it
  * to a list of TableRegions. Order is preserved, so selected and deselected
  * TableRegions can be included in the list of Regions.
  *
  *  @param  any_region the list of Regions to be converted to TableRegions.
  *  @return A list of TableRegions that make up the same region as the
  *          list of regions passed in.
  */
  public static TableRegion[] convertToTableRegions( Region[] any_region )
  {
    Vector table_regions = new Vector();
    Vector other_regions = new Vector();
    int row_min = Integer.MAX_VALUE;
    int row_max = 0;
    int col_min = Integer.MAX_VALUE;
    int col_max = 0;
    CoordBounds region_bounds;
    
    // Go through the list of regions and separate the TableRegions from the
    // rest of the regions.
    for( int i = 0; i < any_region.length; i++ )
    {
      // If already a TableRegion, put it directly into the TableRegion list.
      if( any_region[i] instanceof TableRegion )
        table_regions.add(any_region[i]);
      // If not a TableRegion, group all non-TableRegions together until
      // you run into another TableRegion. Form that group into TableRegions
      // and and those regions to the table_regions vector.
      else
      {
        // Start a new group of non-TableRegions to be converted.
	other_regions.clear();
	// Group non-TableRegions together until the next TableRegion.
	while( i < any_region.length &&
	       !( any_region[i] instanceof TableRegion ) )
	{
          region_bounds = any_region[i].getRegionBounds();
	  // Find absolute min and max row and column of all the regions to
	  // reduce the size of the table rows and columns that are looked at.
	  if( row_min > region_bounds.getY1() )
	    row_min = (int)region_bounds.getY1();
	  if( row_max < region_bounds.getY2() )
	    row_max = (int)region_bounds.getY2();
	  if( col_min > region_bounds.getX1() )
	    col_min = (int)region_bounds.getX1();
	  if( col_max < region_bounds.getX2() )
	    col_max = (int)region_bounds.getX2();
	  // Add the region to the miscellaneous region list for further
	  // analysis.
	  other_regions.add(any_region[i]);
	  i++;
	}
	// Must reset i back one since the "while" loop incremented i one
	// past the last added region.
        i--;
	
     	// Only do this stuff if regions have been added.
     	if( other_regions.size() > 0 )
     	{
     	  // Convert the vector of misc. regions to an array of regions so the
     	  // union of the regions can be calculated.
     	  Region[] misc_region = new Region[other_regions.size()];
     	  for( int reg_num = 0; reg_num < other_regions.size(); reg_num++ )
     	  {
     	    misc_region[reg_num] = (Region)other_regions.elementAt(reg_num);
     	  }
     	  
     	  // Get all of the unique points selected by these regions.
     	  Point[] misc_point = Region.getRegionUnion(misc_region);
     	  
     	  // Make grid and mark all of the selected points. Add an extra row
     	  // that should be all false, this will cause any remaining bounds in
     	  // the bounds_list to be converted to TableRegions.
     	  boolean[][] points_grid = 
	                  new boolean[row_max-row_min+2][col_max-col_min+1];
     	  for(int point_index = 0; point_index<misc_point.length; point_index++)
     	    points_grid[misc_point[point_index].y-row_min]
     		       [misc_point[point_index].x-col_min] = true;
     	  
     	  // Keep a list of CoordBounds that are actively growing. If a bound
     	  // is found to have stopped, remove it and convert it to a\
	  // TableRegion.
     	  Vector bounds_list = new Vector();
     	  // Analyze the grid row by row.
     	  for( int row = 0; row < points_grid.length; row++ )
     	  {
     	    for( int col = 0; col < points_grid[0].length; col++ )
     	    {
     	      // First row is special case.
     	      if( row == 0 )
     	      {
     		// Only do something is a cell is selected in the first row.
     		if( points_grid[row][col] )
     		{
     		  CoordBounds temp_bound = new CoordBounds( (float)col,
		                                            (float)row,
     						            (float)col+.1f,
							    (float)row+.1f );
     		  col++;
     		  // While col is valid and the next cell is selected,
		  // increment col.
     		  while( col < points_grid[0].length && points_grid[row][col] )
     		  {
     		    col++;
     		  }
     		  // col is now one past the last true value, decrement col
     		  // back to the last true value.
     		  col--;
     		  temp_bound.setBounds( temp_bound.getX1(), temp_bound.getY1(),
     					(float)col+.1f, temp_bound.getY2() );
     		  bounds_list.add(temp_bound);
     		}
     	      }
     	      // If not the first row, now check for differences between the
     	      // current row and the previous row.
     	      else if( points_grid[row-1][col] != points_grid[row][col] )
     	      {
     		// Find the index of the bound that has col in its interval.
     		int bound_index = getBoundIndex( col, bounds_list );
     		// If bound_index = -1 and the cell at row,col is selected,
     		// a new bound must be added.
     		if( bound_index == -1 && points_grid[row][col] )
     		{
     		  CoordBounds temp_bound = new CoordBounds( (float)col,
		                                            (float)row,
     						            (float)col+.1f,
							    (float)row+.1f );
     		  col++;
     		  // While col is valid, the next cell is selected, and the next
     		  // cell is not part of an existing bound, increment col.
     		  // This will find the extent of the new bound.
     		  while( col < points_grid[0].length && points_grid[row][col] &&
     			 (getBoundIndex(col,bounds_list) == -1) )
     		  {
     		    col++;
     		  }
     		  // col is now one past the last true value, decrement col back
     		  // to the last true value.
     		  col--;
     		  // Set the extent of the column values for this bound.
     		  temp_bound.setBounds( temp_bound.getX1(), temp_bound.getY1(),
     					(float)col+.1f, temp_bound.getY2() );
     		  // Add the bound to the actively changing bound.
     		  bounds_list.add(temp_bound);
     		}
     		// If valid index, one of the existing bounds has ended.
     		else if( bound_index != -1 )
     		{
     		  // End the bound at bound_index
     		  CoordBounds ending =
     			(CoordBounds)bounds_list.elementAt(bound_index);
     		  // The initial row/col and ending column have been set,
     		  // now set the ending row to the current row.
     		  ending.setBounds( ending.getX1(), ending.getY1(),
     				    ending.getX2(), (float)(row-1)+.1f );
     		  // Remove the ending bound since it is no longer actively
		  // growing.
     		  bounds_list.removeElementAt(bound_index);
     		  // Convert the ending bound into a TableRegion and add it
     		  // to the table_regions list.
     		  floatPoint2D[] def_pts = new floatPoint2D[2];
     		  def_pts[0] = new floatPoint2D( ending.getX1()+(float)col_min,
     						 ending.getY1()+(float)row_min);
     		  def_pts[1] = new floatPoint2D( ending.getX2()+(float)col_min,
     						 ending.getY2()+(float)row_min);
     		  table_regions.add( new TableRegion( def_pts, true ) );
     		  
     		  // If the difference did not occur at the beginning of the
     		  // bound, col should be the ending of a new bound that started
     		  // at the same column as "ending".
     		  if( Math.round( ending.getX1()) != col )
     		  {
     		    bounds_list.add( new CoordBounds( ending.getX1(),(float)row,
     				         (float)(col-1)+.1f, (float)row+.1f ) );
     		  }
     		  // If difference at beginning column, make sure no other
     		  // bounds were created that make up only part of the
		  // ending bounds.
     		  else
     		  {
     		    CoordBounds new_bound = new CoordBounds();
     		    boolean making_bound = false;
     		    boolean col_incremented = false;
     		    while( col < points_grid[0].length && 
     			   getBoundIndex(col,bounds_list) == -1 )
     		    {
     		      // If the points_grid is selected, start or grow the
		      // bound.
     		      if( points_grid[row][col] )
     		      {
     			// Set the beginning row and column and mark
     			// making_bound flag to true.
     			if( !making_bound )
     			{
     			  new_bound.setBounds( (float)col, (float)row,
     					       (float)col+.1f, (float)row+.1f );
     			  making_bound = true;
     			}
     			// If started, grow the bound's columns.
     			else
     			{
     			  new_bound.setBounds( new_bound.getX1(),
			                       new_bound.getY1(),
     					       (float)col+.1f,
					       new_bound.getY2() );
     			}
     		      }
     		      else
     		      {
     			// If bound started, add it to the bounds_list.
     			if( making_bound )
     			{
     			  bounds_list.add( new_bound );
     			  making_bound = false;
     			}
     		      }
     		      col++;
     		      col_incremented = true;
     		    }
     		    
     		    // If col was incremented at all, move it back one so the
		    // last column value is not skipped.
     		    if( col_incremented )
     		    {
     		      col--;
     		      col_incremented = false;
     		    }
     		    // If new_bound was never ended, end now and add to
		    // bounds_list.
     		    if( making_bound )
     		    {
     		      bounds_list.add( new_bound );
     		      making_bound = false;
     		    }
     		  }
     		} // end else if()
     	      } // end else if()
     	    } // end for( col )
     	  } // end for( row )
     	}
      } // end else (not a TableRegion)
    }
    
    // Convert table_regions vector into a list of TableRegions.
    TableRegion[] table_region_array = new TableRegion[table_regions.size()];
    for( int i = 0; i < table_region_array.length; i++ )
      table_region_array[i] = (TableRegion)table_regions.elementAt(i);
    
    return table_region_array;
  }
  
 /*
  * Given a column number and a Vector list of bounds, find the index of the
  * bound that has the given column number in its interval.
  */   
  private static int getBoundIndex( int col_num, Vector elements )
  {
    // make sure there are existing bounds.
    if( elements.size() <= 0 )
      return -1;
    int index = 0;
    CoordBounds bound = (CoordBounds)elements.elementAt(index);
    // While the col_num is not in the column bounds and the index is still
    // valid, increment the index.
    while( index < elements.size() &&
        !((CoordBounds)elements.elementAt(index)).onXInterval((float)col_num) )
      index++;
    
    // If while loop terminated because region was not found, return -1.
    if( index == elements.size() )
      return -1;
    else
      return index;  
  }
 
 /*
  * Specify a rectangular region to either be selected or deselected.
  * The points are specified in table row/column coordinates.
  *
  *  @param  row_min
  *  @param  row_max
  *  @param  col_min
  *  @param  col_max
  *  @param  isSelected
  */ 
  private void setSelectedCells( int row_min, int row_max,
                                 int col_min, int col_max,
				 boolean isSelected )
  {
    // Make sure data exists.
    if( table.getColumnCount() == 0 )
      return;
    
    // If one of the four is not true, do nothing.
    if( !(isValidRow(row_min) && isValidRow(row_max)) ||
        !(isValidColumn(col_min) && isValidColumn(col_max)) )
      return;
    
    // Make sure row_min < row_max and col_min < col_max.
    if( row_min > row_max)
    {
      int temp = row_min;
      row_min = row_max;
      row_max = temp;
    }
    if( col_min > col_max)
    {
      int temp = col_min;
      col_min = col_max;
      col_max = temp;
    }
    // If UnselectAll() method is called, do not add an unselected region
    // spanning the entire table.
    if( !do_clear )
    {
      // Create a table region and add it to the list of selected rectangles.
      // Note that the (x,y) point is actually stored as (column,row).
      floatPoint2D[] def_pts = new floatPoint2D[2];
      def_pts[0] = new floatPoint2D((float)col_min, (float)row_min);
      def_pts[1] = new floatPoint2D((float)col_max, (float)row_max);
      TableRegion tr = new TableRegion(def_pts,isSelected);
      selections.add( tr );
    }
    
    //System.out.println("Row Min/Max: "+row_min+"/"+row_max);
    //System.out.println("Column Min/Max: "+col_min+"/"+col_max);
    for( int row = row_min; row < row_max + 1; row++ )
    {
      for( int col = col_min; col < col_max + 1; col++ )
      {
        selected[row][col] = isSelected;
      }
    }
    
    // Do this to prevent multiple selected_changed messages from being sent
    // when the setSelectedRegions() method is called.
    if( !ignore_notify )
      send_message( SELECTED_CHANGED );
  }
  
 /*
  * Specify a rectangular region using two pixel point values. Convenience
  * method for converting pixel to row/column and then selecting cells.
  */ 
  private void setSelectedCells( Point pt1, Point pt2, boolean isSelected )
  {
    Point temp_pt1 = convertPixelToColumnRow(pt1);
    Point temp_pt2 = convertPixelToColumnRow(pt2);
    
    setSelectedCells( temp_pt1.y, temp_pt2.y, 
                      temp_pt1.x, temp_pt2.x, isSelected );
  }
 
 /*
  * Method to check of row is valid.
  */ 
  private boolean isValidRow(int row)
  {
    // If row is negative or equal or larger than the largest row,
    // then it is invalid.
    if( row < 0 || row >= selected.length )
      return false;
    // otherwise it is valid.
    return true;
  }
 
 /*
  * Method to check of column is valid.
  */ 
  private boolean isValidColumn(int column)
  {
    // If column is negative or equal or larger than the largest column,
    // then it is invalid.
    if( column < 0 || column >= selected[0].length )
      return false;
    // otherwise it is valid.
    return true;
  }
  
 /*
  * Check to see if two points are on the same cell. The points passed in
  * are in pixel values.
  */ 
  private boolean sameCell( Point pt1, Point pt2 )
  {
    // Make sure points are not null.
    if( pt1 == null || pt2 == null )
      return false;
    
    // Get the row/column values for the two points and see if they are equal.
    int current_row = table.rowAtPoint(pt1);
    int current_col = table.columnAtPoint(pt1);
    int anchor_row = table.rowAtPoint(pt2);
    int anchor_col = table.columnAtPoint(pt2);
    if( anchor_row == current_row && anchor_col == current_col )
      return true;
    // If it gets to here, they are not equal.
    return false;
  }
 
 /*
  * Make sure the point passed in is on a cell. This will be false if the
  * window is stretched past the extent the rows/columns and the user clicks
  * on the filler space.
  */ 
  private boolean isPointOnCell( Point pt )
  {
    int row = table.rowAtPoint(pt);
    int col = table.columnAtPoint(pt);
    if( !isValidRow(row) || !isValidColumn(col) )
      return false;
    return true;
  }
 
 /*
  * This method is used to update the labels for the rows and columns
  */ 
  private void updateLabels()
  {
    Point location = scroll.getViewport().getViewRect().getLocation();
    // Convert from pixel to row/column.
    Point cell_row_col = convertPixelToColumnRow(location);
    
    // If less than half of the cell is showing, use labels for next cell.
    Rectangle cell_size = table.getCellRect(cell_row_col.y,cell_row_col.x,true);
    // Look half a cell down, if its a different cell, increment the row labels.
    Point row_variant = new Point(location);
    row_variant.y += (SHIFT_DIVISOR-1)*cell_size.height/SHIFT_DIVISOR;
    // Look half a cell to the right, if its a different cell, increment the
    // column labels.
    Point col_variant = new Point(location);
    col_variant.x += (SHIFT_DIVISOR-1)*cell_size.width/SHIFT_DIVISOR;
    // Increment row if over half way scrolled down on next cell.
    if( !sameCell(row_variant,location) )
    {
      cell_row_col.y++;
    }
    // Increment column if over half way scrolled to the right on next cell.
    if( !sameCell(col_variant,location) )
    {
      cell_row_col.x++;
    }
    
    // Set the labels for the columns and rows.
    Object label = new Object();
    for( int row = 0; row < row_labels.getRowCount(); row++ )
    {
      label = row_label_list.elementAt(row + cell_row_col.y);
      row_labels.setValueAt(label,row,0);
    }
    
    for( int col = 0; col < column_labels.getColumnCount(); col++ )
    {
      label = column_label_list.elementAt(col + cell_row_col.x);
      column_labels.setValueAt(label,0,col);
    }
    // Store the offset used to make the labels match the viewable columns.
    column_label_offset = cell_row_col.x;
  }
 
 /*
  * This listener selects/deselects according to a dragged mouse.
  */ 
  private class SelectionChanged extends MouseMotionAdapter
  {
    public void mouseDragged( MouseEvent me )
    {
      // if the point is not on any cell, ignore the mouse event
      if( !isPointOnCell(me.getPoint()) )
        return;
      
      // If control is down, we are adding or removing a region of cells
      // to the initial cell region.
      if( me.isControlDown() )
      {
        // If this is the first mouse click/drag, set the ctrl_anchor point.
        if( ctrl_anchor == null )
	{
	  ctrl_anchor = me.getPoint();
	}
	else
	{
	  // Ignore if this is the same point as the previous point.
          if( sameCell(ctrl_extend,me.getPoint()) )
	  {
	    return;
	  }
	  // Set the new extend point
	  ctrl_extend = me.getPoint();
	  // Set the cell that should have focus.
	  focus = new Point(ctrl_extend);
	  // Since pointed_at_changed message sent on initial click, only send
          // message if on a different point.
	  if( !sameCell(ctrl_anchor,me.getPoint()) )
            send_message(POINTED_AT_CHANGED);
	}
	table.repaint();
	// Do not do anything after this point.
	return;
      }
      
      // Send pointed_at message only if changed to a new cell.
      if( sameCell(extend,me.getPoint()) )
        return;
      // get new extent
      extend = me.getPoint();
      // Set the cell that should have focus.
      focus = new Point(extend);
      // Since pointed_at_changed message sent on initial click, only send
      // message if on a different point.
      if( !sameCell(anchor,me.getPoint()) )
        send_message(POINTED_AT_CHANGED);
      
      table.repaint();
    }
  }
 
 /*
  * This class is responsible for starting either the initial selection of
  * cells or the addition/removal of cells from the initial selection (ctrl).
  */ 
  private class SelectionStarted extends MouseAdapter
  {
    public void mousePressed( MouseEvent me )
    {
      // A selection has been started
      active_selection = true;
      // if the point is not on any cell, ignore the mouse event
      if( !isPointOnCell(me.getPoint()) )
        return;
      
      // If ctrl is down, we are adding/removing from initial selected cells.
      if( me.isControlDown() )
      {
        // Reset the starting point. Clear the ending point to null.
        ctrl_anchor = me.getPoint();
	ctrl_extend = null;
	// Set the cell that should have focus.
	focus = new Point(ctrl_anchor);
	send_message(POINTED_AT_CHANGED);
      }
      // If shift is down, select everything between current point and
      // the anchor or first point.
      else if( me.isShiftDown() )
      {
        if( anchor == null )
	{
	  anchor = me.getPoint();
	  // Set the cell that should have focus.
	  focus = new Point(anchor);
	  send_message(POINTED_AT_CHANGED);
	}
	else
	{
	  // Get new ending value of rectangular region of selected cells.
	  extend = me.getPoint();
	  // Set the cell that should have focus.
	  focus = new Point(extend);
	  send_message(POINTED_AT_CHANGED);
	}
      }
      // Select cells between first and end point by dragging.
      else
      {
        if( anchor == null )
	{
	  anchor = me.getPoint();
	  // Set the cell that should have focus.
	  focus = new Point(anchor);
	  send_message(POINTED_AT_CHANGED);
	}
	else
	{
	  // If clicked without any modifiers, clear all previous selections.
          UnselectAll();
	  // Set new anchor point and select it.
	  anchor = me.getPoint();
	  // Set the cell that should have focus.
	  focus = new Point(anchor);
	  send_message(POINTED_AT_CHANGED);
	  // New initial selection started, reset other values to null.
	  extend = null;
	  ctrl_anchor = null;
	  ctrl_extend = null;
	}
      }
      table.repaint();
    }
   
   /*
    * This method is deactivates the selection. No cell is actually selected
    * until the mouse is released. 
    */ 
    public void mouseReleased( MouseEvent me )
    {
      active_selection = false;
      
      // If an additional selection was made, toggle the cells to the
      // opposite selected value of the ctrl_anchor.
      if( ctrl_anchor != null )
      {
	Point rc_ctrl_anchor = convertPixelToColumnRow(ctrl_anchor);
        // If more than once cell, toggle all cells in additional region.
	if( ctrl_extend != null )
	{
	  setSelectedCells( ctrl_anchor, ctrl_extend,
	                    !selected[rc_ctrl_anchor.y][rc_ctrl_anchor.x] );
	}
	// Else, selection is only one cell. Toggle that cell.
	else
	  setSelectedCells( ctrl_anchor, ctrl_anchor,
	                    !selected[rc_ctrl_anchor.y][rc_ctrl_anchor.x] );
      }
      // If an initial selection was made, select the cells in the new region.
      else if( anchor != null )
      {
        if( extend != null )
	{
	  setSelectedCells(anchor,extend,true);
	}
	else
	  setSelectedCells(anchor,anchor,true);
      }
      table.repaint();
    }
  }
  
 /*
  * This listener adjusts the row and column labels when the component is
  * resized.
  */
  private class ResizedListener extends ComponentAdapter
  {
    public void componentResized( ComponentEvent ce )
    {
      // Get the spacing between the cells.
      Dimension cell_spacing = table.getIntercellSpacing();
      // Bounds of the scroll pane, ignoring the presence of scroll bars.
      Rectangle bounds = scroll.getBounds();
      int bound_width_adjuster = 0;
      int bound_height_adjuster = 0;
     /* ***** Uncommenting this will prevent the column labels from   *****
      * ***** appearing until the entire cell is in the viewport. The *****
      * ***** current functionality allows for the label to appear    *****
      * ***** above the vertical scrollpane.			      *****
      // If the vertical scrollbar is visible, reduce width by the width of
      // the scrollbar.
      if( scroll.getVerticalScrollBar().isVisible() )
        bound_width_adjuster = scroll.getVerticalScrollBar().getSize().width;
      * ***************************************************************** */
      // If the horizontal scrollbar is visible, remove one row from the table.
      if( scroll.getHorizontalScrollBar().isVisible() )
        bound_height_adjuster =scroll.getHorizontalScrollBar().getSize().height;
      bounds.width -= bound_width_adjuster;
      bounds.height -= bound_height_adjuster;
      // The size of a cell with it's spacing.
      Rectangle cell = table.getCellRect(0,0,true);
      // Find the number of complete rows and columns currently viewable.
      int num_rows = (int)Math.floor( (double)(bounds.height - cell.height) /
                                 (double)cell.height) + 1;
      int num_cols = (int)Math.floor( (double)(bounds.width - cell.width) /
                                 (double)cell.width ) + 1;
      
      // If the number of rows/columns is larger than the size of the
      // selected array, use the dimensions of the selected array.
      if( num_rows > selected.length )
        num_rows = selected.length;
      if( num_cols > selected[0].length )
        num_cols = selected[0].length;
      
      // Make sure the number of rows/columns is not negative.
      if( num_rows < 0 )
        num_rows = 0;
      if( num_cols < 0 )
        num_cols = 0;
      
      // Find the remainder of the width.
      int remainder_width = bounds.width + bound_width_adjuster -
                            num_cols * cell.width - cell_spacing.width;
      
      // Find the remainder of the height.
      int remainder_height = bounds.height + bound_height_adjuster -
                             num_rows * cell.height - cell_spacing.height;
      
      // Set the scroll bar increments to the size of the table cells.
      scroll.getHorizontalScrollBar().setBlockIncrement(cell.width);
      scroll.getHorizontalScrollBar().setUnitIncrement(cell.width);
      scroll.getVerticalScrollBar().setBlockIncrement(cell.height);
      scroll.getVerticalScrollBar().setUnitIncrement(cell.height);
      
      // Remove the label panels so they can be rebuilt.
      remove( row_label_container );
      remove( column_label_container );
      
      // ********* Build the upper column label panel. **********
      // The left_col_spacer accounts for the space created by the row_labels
      // column.
      JPanel left_col_spacer = new JPanel();
      left_col_spacer.setBackground(label_color);
      // If the row labels are not visible, the spacer does not have to
      // account for the extra column.
      if( !row_label_container.isVisible() )
      {
        left_col_spacer.setPreferredSize( 
                          new Dimension( cell_spacing.width, cell.height) );
      }
      else
      {
        left_col_spacer.setPreferredSize( 
               new Dimension( cell.width + cell_spacing.width, cell.height) );
      }
      
      // The right_col_spacer accounts for a partial column not entirely
      // appearing in the JTable.
      JPanel right_col_spacer = new JPanel();
      right_col_spacer.setBackground(label_color);
      right_col_spacer.setPreferredSize( new Dimension( remainder_width,
                                                        cell.height ) );
      // The actual labels for the columns, contains the number of cells
      // equal to the number of entirely visible columns.
      column_labels = new JTable(1,num_cols);
      // Add these two listeners to control column drag & drop.
      column_labels.addMouseListener( new ColumnSelectListener() );
      column_labels.addMouseMotionListener( new DragListener() );
      
      // Make sure there are columns to render.
      if( num_cols > 0 )
        column_labels.setDefaultRenderer( column_labels.getColumnClass(0),
                                          new IgnoreFocusRenderer() );
      column_labels.setBackground(label_color);
      column_labels.setCellSelectionEnabled(false);
      column_label_container.removeAll();
      column_label_container.add( left_col_spacer, BorderLayout.WEST );
      column_label_container.add( column_labels, BorderLayout.CENTER );
      column_label_container.add( right_col_spacer, BorderLayout.EAST );
      
      // ********* Build the left row label panel. *********
      // This spacer will account for the row spacing, enabling the rows
      // to be aligned with the labels.
      JPanel up_row_spacer = new JPanel();
      up_row_spacer.setBackground(label_color);
      up_row_spacer.setPreferredSize( new Dimension( cell.width,
                                                     cell_spacing.height ) );
      // The low_row_spacer accounts for a partial row not entirely
      // appearing in the JTable.
      JPanel low_row_spacer = new JPanel();
      low_row_spacer.setBackground(label_color);
      low_row_spacer.setPreferredSize( new Dimension( cell.width,
                                                      remainder_height ) );
      // The actual labels containing the number of cells equal to the
      // number of entirely visible rows.
      row_labels = new JTable(num_rows,1);
      // Make sure there are rows to render.
      if( num_rows > 0 )
        row_labels.setDefaultRenderer( row_labels.getColumnClass(0),
                                       new IgnoreFocusRenderer() );
      row_labels.setBackground(label_color);
      row_labels.setCellSelectionEnabled(false);
      row_label_container.removeAll();
      row_label_container.add( up_row_spacer, BorderLayout.NORTH );
      row_label_container.add( row_labels, BorderLayout.CENTER );
      row_label_container.add( low_row_spacer, BorderLayout.SOUTH );
      
      // Add the two new panels to the container.
      add( row_label_container, BorderLayout.WEST );
      add( column_label_container, BorderLayout.NORTH );
      
      // Update the labels when the component gets resized.
      updateLabels();
      
      // Repack and repaint the entire TableJPanel.
      validate();
      repaint();
    }
  }
 
 /*
  * This listener will ensure that the upper left-hand corner of the table
  * is always at the corner of a cell.
  */ 
  private class ScrollAdjusted implements AdjustmentListener
  {
    public void adjustmentValueChanged( AdjustmentEvent ae )
    {
      // Ignore events until scroll bar has stopped moving.
      if( ((JScrollBar)ae.getSource()).getValueIsAdjusting() )
      {
        // Update the labels as the scrollbar moves.
        updateLabels();
        return;
      }
      
      Rectangle viewport = scroll.getViewport().getViewRect();
      Rectangle cell = table.getCellRect(0,0,true);
      int partial_width = viewport.x % cell.width;
      // If there is a portion of a row on the left edge of the
      // viewport, shift the view.
      if( partial_width != 0 )
      {
        // If less than half a cell width, shift it to the left.
        if( partial_width < ((float)cell.width)/SHIFT_DIVISOR )
	{
	  viewport.x -= partial_width;
	}
	// If more than half a cell width, shift it to the right.
	else
	{
	  viewport.x += (cell.width - partial_width);
	}
	scroll.getViewport().setViewPosition(viewport.getLocation());
	scroll.repaint();
      }
      // If there is an extra portion of a column on the top edge of the 
      // viewport, shift the last row.
      int partial_height = viewport.y % cell.height;
      if( partial_height != 0 )
      {
        // If less than half a cell height, shift it up. 
        if( partial_height < ((float)cell.height)/SHIFT_DIVISOR )
	{
	  viewport.y -= partial_height;
	}
	// If more than half a cell height, shift it down.
	else
	{
	  viewport.y += (cell.height - partial_height);
	}
	scroll.getViewport().setViewPosition(viewport.getLocation());
	scroll.repaint();
      }
      
      // Set the labels for the columns and rows.
      updateLabels();
    }
  }
 
 /*
  * This class will make the cells appear to be selected by modifying their
  * color and using the selected[][] array to determine if the cell is
  * selected or not.
  */ 
  private class CellRenderer extends DefaultTableCellRenderer
  {
    public CellRenderer()
    {
      super();
    }
    
   /*
    * This is the method that tells the table how to display a cell
    * at row/column. This method uses the default rendering tools, but
    * modifies the arguments passed in to get the desired cell model.
    */ 
    public Component getTableCellRendererComponent( JTable tab, Object action,
                                   boolean isSelected, boolean hasFocus,
				   int row, int column )
    {
      // Get the cell in focus and convert to row/column coordinates.
      Point row_col_focus = convertPixelToColumnRow(focus);
      // If the current cell is in focus, pass in true for hasFocus.
      if( row_col_focus != null && row_col_focus.y == row &&
          row_col_focus.x == column )
      {
        // If selected, return the selected model with focus.
        if( isSelected(row,column) )
	{
          return super.getTableCellRendererComponent( tab, action,
                                   true, true, row, column );
        }
	// Else, return the unselected model with focus.
        else
	{
          return super.getTableCellRendererComponent( tab, action,
                                   false, true, row, column );
        }
      }
      // If cell not in focus, pass in false for hasFocus.
      else
      {
        // If selected, return the selected model without focus.
        if( isSelected(row,column) )
	{
          return super.getTableCellRendererComponent( tab, action,
                                   true, false, row, column );
        }
	// Else, return the unselected model without focus.
        else
	{
          return super.getTableCellRendererComponent( tab, action,
                                   false, false, row, column );
        }
      }
    }
  }
 
 /*
  * This renderer is for the row_labels and column_labels tables. Do not
  * show any focus or selections, only the text.
  */ 
  private class IgnoreFocusRenderer extends DefaultTableCellRenderer
  {
    public IgnoreFocusRenderer()
    {
      super();
    }
    
   /*
    * This method will use the default renderer, but ignore all events since
    * this class is used for an uneditable, unselectable, and unfocusable
    * JTable. 
    */ 
    public Component getTableCellRendererComponent( JTable tab, Object action,
                                   boolean isSelected, boolean hasFocus,
				   int row, int column )
    {
      return super.getTableCellRendererComponent( tab, action,
                                   false, false, row, column );
    }
  }
  
 /* 
  * This listener is used by the column_labels to control column drag & drop.
  */ 
  private class DragListener extends MouseMotionAdapter
  {
    public void mouseDragged( MouseEvent me )
    {
      JTable temp = (JTable)me.getSource();
      if( isValidColumn( temp.columnAtPoint(me.getPoint()) ) )
      {
        current_column = me.getPoint();
        int move_from_column = temp.columnAtPoint(initial_column);
        int move_to_column = temp.columnAtPoint(current_column);
	moveColumn( move_from_column + column_label_offset,
	            move_to_column + column_label_offset );
	// Reset the initial column to the current column.
        initial_column = new Point(current_column);
      }
    }
  }
  
 /* 
  * This listener is used by the column_labels to initiate column drag & drop.
  */  
  private class ColumnSelectListener extends MouseAdapter
  {
    public void mousePressed( MouseEvent me )
    {
      JTable temp = (JTable)me.getSource();
      // If the initial mouse click is on a valid column value return the value.
      if( isValidColumn( temp.columnAtPoint(me.getPoint()) ) )
      {
        initial_column = me.getPoint();
      }
      // If invalid column, initialize point to (0,0).
      else
      {
        initial_column = new Point();
      }
    }
  }
 
 /*
  * This class is just here to allow easy use of the testing program.
  */ 
  private static class VirtualTableModel implements TableModel
  {
    private IVirtualArray iva;
    
    protected VirtualTableModel( IVirtualArray array )
    {
      iva = array;
    }
    
    public void addTableModelListener( TableModelListener tml )
    {
      ; // stub
    }
    
    public Class getColumnClass( int column_index )
    {
      return iva.getClass();
    }
    
    public int getColumnCount()
    {
      if( iva instanceof IVirtualArray2D )
        return ((IVirtualArray2D)iva).getNumColumns();
      return 0;
    }
    
    public String getColumnName(int index)
    {
      return "";
    }
    
    public int getRowCount()
    {
      if( iva instanceof IVirtualArray2D )
        return ((IVirtualArray2D)iva).getNumRows();
      return 0;
    }
    
    public Object getValueAt( int row, int column )
    {
      if( iva instanceof IVirtualArray2D )
        return new Float( ((IVirtualArray2D)iva).getDataValue(row,column) );
      return null;
    }
    
    public boolean isCellEditable( int row, int column )
    {
      return false;
    }
    
    public void removeTableModelListener( TableModelListener tml )
    {
      ; // stub
    }
    
    public void setValueAt( Object float_value, int row, int column )
    {
      float value = ((Float)float_value).floatValue();
      if( iva instanceof IVirtualArray2D )
        ((IVirtualArray2D)iva).setDataValue(row,column,value);
    }
  }
 
 /**
  * Test Program - use for testing only.
  *
  *  @param  args Command line arguments, these are ignored.
  */ 
  public static void main( String args[] )
  {
    JFrame frame = new JFrame();
    frame.setBounds(0,0,600,300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    int row = 30;
    int col = 40;
    float[][] values = new float[row][col];
    Object[] row_lab_list = new Object[row];
    Object[] col_lab_list = new Object[col];
    for( int i = 0; i < row; i++ )
    {
      for( int j = 0; j < col; j++ )
      {
        values[i][j] = (float)(i + j);
	// Create list of column labels, do only once.
	if( i == 0 )
	  col_lab_list[j] = new String( "COL: "+j );
      }
      row_lab_list[i] = new String( "ROW: "+(100+i) );
    }
    
    VirtualArray2D iva = new VirtualArray2D(values);
    TableJPanel testable = new TableJPanel(new VirtualTableModel(iva));
    // Add a listener to send out pointed-at and selected changed messages.
    testable.addActionListener( new ActionListener()
      {
        public void actionPerformed( ActionEvent ae )
	{ /*
	  String message = ae.getActionCommand();
	  System.out.println( message );
	  // After selection is changed, print to console all of the regions.
	  if( message.equals( SELECTED_CHANGED ) )
	  {
	    TableRegion[] regions = 
	            ((TableJPanel)ae.getSource()).getSelectedRegions();
	    
	    System.out.println("*****Regions*****");
	    for( int i = 0; i < regions.length; i++ )
	      System.out.println((i+1)+". "+regions[i]);
	    System.out.println("*******END*******");
	  }
	  // Test the isSelected() method.
	  else if( message.equals( POINTED_AT_CHANGED ) )
	  {
            System.out.println("Is cell (column=1,row=3) selected? " +
                       ((TableJPanel)ae.getSource()).isSelected(3,1) );
            System.out.println("Is cell (column=10,row=5) selected? " +
                       ((TableJPanel)ae.getSource()).isSelected(5,10) );
	  }*/
	}
      });
    // Make TableRegions to select/deselect cells.
    TableRegion[] reg = new TableRegion[3];
    floatPoint2D[] def_pts1 = new floatPoint2D[2];
    def_pts1[0] = new floatPoint2D(1f,4f);
    def_pts1[1] = new floatPoint2D(10f,13f);
    reg[0] = new TableRegion( def_pts1, true );
    
    floatPoint2D[] def_pts2 = new floatPoint2D[2];
    def_pts2[0] = new floatPoint2D(1f,2f);
    def_pts2[1] = new floatPoint2D(5f,6f);
    reg[1] = new TableRegion( def_pts2, false );
    
    floatPoint2D[] def_pts3 = new floatPoint2D[2];
    def_pts3[0] = new floatPoint2D(0,0);
    def_pts3[1] = new floatPoint2D(3f,3f);
    reg[2] = new TableRegion( def_pts3, true );
    
    /* testing addSelectedRegion() method 
    testable.addSelectedRegion(reg[0]);
    testable.addSelectedRegion(reg[1]);
    testable.addSelectedRegion(reg[2]);*/
    
    testable.setSelectedRegions(reg);
    // Check to make sure that the regions from the previous method were saved.
    testable.setSelectedRegions( testable.getSelectedRegions() );
    // Set the pointed at cell
    testable.setPointedAtCell(14,12);
    //testable.displayColumnLabels(false);
    //testable.displayRowLabels(false);
    
    // Test the ObjectState by saving the initial settings, then changing
    // all of the defaults.
    ObjectState state = testable.getObjectState(IPreserveState.PROJECT);
    testable.UnselectAll();
    testable.setLabelBackground(Color.green);
    testable.setRowLabels(row_lab_list);
    testable.setColumnLabels(col_lab_list);
    testable.setPointedAtCell(0,0);
    testable.setObjectState(state);
    /*
    System.out.println("Table Size: [rows = "+testable.getRowCount()+", "+
                       "columns = "+testable.getColumnCount()+"]");
    
    testable.moveColumn(2,-4);  // Test error checking.
    testable.moveColumn(26,99); // Test error checking.
    */
    frame.getContentPane().add(testable);
    WindowShower shower = new WindowShower(frame);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }
}
