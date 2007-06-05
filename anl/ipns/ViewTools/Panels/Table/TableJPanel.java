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
 *  Revision 1.16  2007/06/05 20:12:54  rmikk
 *  Fixed it so the row labels and column labels sent in are seen
 *
 *  Revision 1.15  2007/03/16 18:34:02  dennis
 *  Adapted to use new Region methods.
 *
 *  Revision 1.14  2006/07/27 00:50:45  dennis
 *  Moved ExcelAdapter from package IsawGUI to ExtTools
 *
 *  Revision 1.13  2005/06/08 18:44:18  dennis
 *  Fixed javadoc error.
 *
 *  Revision 1.12  2005/05/25 20:28:48  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.11  2005/01/10 16:16:51  dennis
 *  Removed empty statement(s).
 *
 *  Revision 1.10  2004/12/05 06:03:44  millermi
 *  - Fixed Eclipse warnings.
 *
 *  Revision 1.9  2004/08/18 05:28:26  millermi
 *  - Removed private method setSelectedCells(Point,Point,boolean)
 *    and replaced all calls to this method with calls to
 *    addSelectedRegion(TableRegion).
 *  - Row Labels column now reduces size to largest label.
 *  - Spacer JPanel in upper-left corner now has line border to
 *    blend with the row_labels table.
 *
 *  Revision 1.8  2004/08/17 20:57:47  millermi
 *  - Added help() to provide help window for users.
 *  - Added functionality for selecting entire rows/columns using
 *    a single click.
 *  - Fixed bug that cleared selections when a table column was clicked
 *    on, but never moved.
 *
 *  Revision 1.7  2004/08/13 03:41:29  millermi
 *  - Added VIEWPORT_POSITION ObjectState key to save the visible
 *    area of the table for any Project saves.
 *
 *  Revision 1.6  2004/08/05 08:59:36  millermi
 *  - Added method getValueAt() to get value in original TableModel.
 *  - Number Formatter now formats Strings containing numeric values.
 *  - Added ObjectState keys ALIGNMENT and NUMBER_FORMAT to save
 *    the corresponding values.
 *
 *  Revision 1.5  2004/08/04 18:57:31  millermi
 *  - Restructured column labeling, now makes use of Java's column features.
 *  - Added ability for row_labels column to automatically adjust to the size
 *    of the largest label.
 *  - Now makes use of new class TableModelMaker to convert from an
 *    IVirtualArray2D to a TableModel.
 *  - Added getRowCount() and getColumnCount() for user convenience.
 *  - Renamed UnselectAll() to unselectAll().
 *  - Added getEnableMoveColumn() to verify if column movement was
 *    enabled or disabled.
 *  - Added getColumnLabels() and getRowLabels().
 *  - ObjectState now saves labels for Project saves.
 *  - Added setColumnAlignment() to allow data to be left, right or center
 *    justified.
 *  - Added capability to handle null data.
 *  - Corrected the code for calculating the number of visible rows in
 *    the ResizedListener.
 *
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
 
import ExtTools.ExcelAdapter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Point;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.JScrollBar;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.html.HTMLEditorKit;
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
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Components.Region.Region;
import gov.anl.ipns.ViewTools.Components.Region.PointRegion;
import gov.anl.ipns.ViewTools.Components.Region.TableRegion;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Numeric.Format;
import gov.anl.ipns.Util.Sys.WindowShower;

/*
 */

/**
 * This class has many of the capabilities of the JTable. The TableJPanel
 * allows for random selection, with selection methods parallelling that
 * of a typical spreadsheet program. This class does not extend JTable, thus
 * only a few of the JTable methods are found in this class.
 * <BR><BR><I>
 * Note: Java's JTable uses multiple column indices for each column. Each
 * column has a View index, where it visably "appears" in the table, and
 * a Model index, how it was initially entered into the TableModel. Reordering
 * of columns causes these two values to be different. This class expects
 * all column values to be in Model indices, since the JTable methods 
 * work on this system.
 * <BR><BR><B>
 * Example: Column 0 and Column 2 are interchanged. The index 0 should still
 * be used to reference Column 0, not Column 2.</B></I>
 */
public class TableJPanel extends ActiveJPanel implements IPreserveState
{
 /**
  * "Selected Changed" - This messaging String is sent out whenever the
  * mouse is released and a selection of cells has been completed. Calling
  * getSelectedRegions() will return a list of TableRegions with the last
  * region being the newly created region that generated this message.
  */
  public static final String SELECTED_CHANGED = "Selected Changed";
  
 /**
  * "Selection Changing" - This messaging String is sent out whenever the
  * mouse is pressed and dragged, causing the selection of cells to be changed
  * but not yet completed. Calling getSelectedRegions() will return a list
  * of TableRegions that will not include the region that generated this
  * message. Instead, call getActiveSelection() to get the changing selection.
  */
  public static final String SELECTION_CHANGING = "Selection Changing";
  
 /**
  * "Pointed At Changed" - This messaging String is sent out whenever the
  * pointed at cell is changed.
  */
  public static final String POINTED_AT_CHANGED = "Pointed At Changed";
  
 /**
  * "Viewport Changed" - This messaging String is sent out whenever the
  * horizontal or vertical scrollbars are adjusted.
  */
  public static final String VIEWPORT_CHANGED = "Viewport Changed";
 
 // The following variables are String keys used to preserve state information. 
 /**
  * "Label Background" - This String key is used to preserve state information
  * of the background color used for the label display. The value this key
  * references is of type Color.
  */
  public static final String LABEL_BACKGROUND = "Label Background";
 
 /**
  * "Row Labels" - This String key is used to preserve state information
  * which stores row labels that are displayed on the table.
  * The value this key references is an array of Objects.
  */
  public static final String ROW_LABELS = "Row Labels";
 
 /**
  * "Column Labels" - This String key is used to preserve state information
  * which stores column labels that are displayed on the
  * table. The value this key references is an array of Objects.
  */
  public static final String COLUMN_LABELS = "Column Labels";
 
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
  
 /**
  * "Viewport Position" - This String key is used to preserve state information
  * about what portion of the table was visible in the viewport at the
  * time of the ObjectState save. The value this key references is of type
  * Rectangle.
  */
  public static final String VIEWPORT_POSITION = "Viewport Position";
 
 /**
  * "Column Mobility" - This String key is used to preserve state information
  * which determines whether columns may be moved, either by calling
  * moveColumn() or by dragging column headers. The value this key references
  * is an of type Boolean.
  */
  public static final String COLUMN_MOBILITY = "Column Mobility";
  
 /**
  * "Alignment" - This String key is used to preserve state information
  * controlling the alignment of all table entries in the TableJPanel.
  * @see #setColumnAlignment(int alignment)
  */
  public static final String ALIGNMENT = "Alignment";
  
 /**
  * "Number Format" - This String key is used to preserve state information
  * controlling how numerical data is displayed in the table. The value this
  * key references is a Point with (x = predecimal digits, y = postdecimal
  * digits).
  * @see #setNumberFormat(int predecimal_digits,int postdecimal_digits)
  */
  public static final String NUMBER_FORMAT = "Number Format";
  
  private final float SHIFT_DIVISOR = 2f; // (SHIFT_DIVISOR-1)/SHIFT_DIVISOR
                                          // controls when the table is shifted
                                          // to the next row and/or column.
  private Point anchor = new Point(); // The beginning of the initial selection.
  private Point extend = null; // The variable end of the initial selection.
  private Point ctrl_anchor = null; // The beginning of an additional selection.
  private Point ctrl_extend = null; // The end of a changing selection.
  private Point focus; // The col/row pt. of the cell that has focus.
  private JTable table; // The table that will display the data.
  private JScrollPane scroll; // The scrollpane containing the table.
  private boolean[][] selected; // The grid used to determine selected cells.
  private CoordBounds active_selection; // The actively growing selection.
  private JTableHeader column_labels; // Save backup of header, so it can be
                                      // set to null, thus not appearing.
 
  private JTable row_labels; // The row labels.
  private JPanel row_label_container; // The row labels and spacers.
  private Vector row_label_list; // List of labels, one for each row.
  private Color label_color; // Background color of the labels.
  private Vector selections; // List of TableRegions
  private boolean ignore_notify; // This variable is used to limit the
                                     // number of selected_changed messages
				     // if setSelectedRegions() is called.
  private boolean do_clear;  // This will prevent a region from being added
                             // when the unselectAll() method is called.
  private String max_row_label; // Max width of any row label String.
  private boolean disable_auto_position; // disable setPointedAt() cell tracer.
  private boolean move_column_called; // was moveColumn() called to move column
  private int pre_digits; // Number of digits before decimal, used in formatting
  private int post_digits; // Number of digits after decimal, used in formatting
  private int col_alignment;
  
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
     // Prevent data from being null. If zero rows/columns then just create
     // a default JTable.
     if( tm == null || tm.getRowCount() <= 0 || tm.getColumnCount() <= 0 )
       table = new JTable();
     else
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
    if( num_rows <= 0 || num_columns <= 0 )
      table = new JTable();
    else
      table = new JTable( num_rows, num_columns );
    init();
  }
  
 /*
  * This method is used by the constructors to initialize the TableJPanel.
  */ 
  private void init()
  {
    setLayout( new BorderLayout() );
    // This will wrap the data in an additional TableModel to allow
    // formatting of cells within the JTable.
    table.setModel( new TableModelFormatter(table.getModel()) );
    table.getColumnModel().addColumnModelListener(
                                   new ColumnReorderedListener() );
    
    int rows = table.getModel().getRowCount();
    int columns = table.getModel().getColumnCount();
    selected = new boolean[rows][columns];
    focus = new Point();
    active_selection = null;
    label_color = table.getTableHeader().getBackground();
    column_labels = table.getTableHeader();
    // Add listener to make column selections when a double-click occurs on
    // the table header.
    if( column_labels != null )
      column_labels.addMouseListener(new ColumnRowSelectListener());
    selections = new Vector();
    ignore_notify = false;
    do_clear = false;
    max_row_label = "";
    disable_auto_position = false;
    move_column_called = false;
    setColumnAlignment(SwingConstants.RIGHT); // Initialize to right alignment.
    setNumberFormat(-1,-1); // Set the initial setting no formatting.
    // Add copy and paste ability.
    new ExcelAdapter( table );
    
    table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
    table.setCellSelectionEnabled( false );
    scroll = new JScrollPane(table);
    
    scroll.getHorizontalScrollBar().addAdjustmentListener(new ScrollAdjusted());
    scroll.getVerticalScrollBar().addAdjustmentListener( new ScrollAdjusted() );
    
    if( table.getRowCount() == 0 || table.getColumnCount() == 0 )
      add( new JLabel("Empty Table - No data to display.") );
    else
      add(scroll, BorderLayout.CENTER );
    
    // Make sure table isn't empty.
    if( table.getColumnCount() > 0 )
      table.setDefaultRenderer( table.getColumnClass(0), new CellRenderer() );
    table.addMouseMotionListener( new SelectionChanged() );
    table.addMouseListener( new SelectionStarted() );
    // put in dummy values
    row_labels = new JTable();
    row_labels.setCellSelectionEnabled( false );
    // Add listener to make row selections when a double-click occurs on
    // the row_labels table.
    row_labels.addMouseListener(new ColumnRowSelectListener());
    
    // Initialize the label lists to contain "Column #" or "Row #".
    Object[] column_label_array = new Object[columns];
    for( int col_num = 0; col_num < columns; col_num++ )
      column_label_array[col_num] = table.getColumnModel().getColumn(col_num).
                                                  getHeaderValue().toString();
                                 //new Integer(col_num);
    setColumnLabels(column_label_array);
    
    row_label_list = new Vector();
    Object[] row_label_array = new Object[rows];
    for( int row_num = 0; row_num < rows; row_num++ ){
      row_label_array[row_num] = new Integer(row_num);
      row_label_list.add( new Integer( row_num));
    }
    setRowLabels(row_label_array);
    row_label_container = new JPanel( new BorderLayout() );
    row_label_container.add( row_labels, BorderLayout.CENTER );
    add( row_label_container, BorderLayout.WEST );
    
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
      setPointedAtCell( (Point)temp );
      redraw = true;  
    }
    
    temp = new_state.get(LABEL_BACKGROUND);
    if( temp != null )
    {
      setLabelBackground( (Color)temp );
      redraw = true;  
    }
    
    temp = new_state.get(ROW_LABELS);
    if( temp != null )
    {
      setRowLabels( (Object[])temp );
      redraw = true;  
    }
    
    temp = new_state.get(COLUMN_LABELS);
    if( temp != null )
    {
      setColumnLabels( (Object[])temp );
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
    
    temp = new_state.get(VIEWPORT_POSITION);
    if( temp != null )
    {
      setVisibleLocation( ((Rectangle)temp).getLocation() );
      redraw = true;  
    }
    
    temp = new_state.get(COLUMN_MOBILITY);
    if( temp != null )
    {
      enableMoveColumn( ((Boolean)temp).booleanValue() );
      redraw = true;  
    }
    
    temp = new_state.get(ALIGNMENT);
    if( temp != null )
    {
      setColumnAlignment( ((Integer)temp).intValue() );
      redraw = true;  
    }
    
    temp = new_state.get(NUMBER_FORMAT);
    if( temp != null )
    {
      setNumberFormat( ((Point)temp).x, ((Point)temp).y );
      redraw = true;  
    }
    
    if( redraw )
    {
      validate();
      repaint();
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
                  new Boolean(table.getTableHeader() != null) );
    state.insert( LABEL_BACKGROUND, label_color );
    state.insert( COLUMN_MOBILITY,
                  new Boolean( getEnableMoveColumn() ) );
    state.insert( ALIGNMENT, new Integer(col_alignment) );
    state.insert( NUMBER_FORMAT, new Point(pre_digits,post_digits) );
    
    // Only do the following if this is a project save, not a setting save.
    if( !is_default )
    {
      state.insert( ROW_LABELS, getRowLabels() );
      state.insert( COLUMN_LABELS, getColumnLabels() );
      state.insert( POINTED_AT_CELL, getPointedAtCell() );
      state.insert( TABLE_SELECTIONS, getSelectedRegions() );
      state.insert( VIEWPORT_POSITION, getVisibleRectangle() );
    }
    
    return state;
  }
  
 /**
  * Contains control information about the TableJPanel. To view the information,
  * use the WindowShower to visualize the help frame.
  *
  *  @return JFrame containing help information for the table.
  */
  public static JFrame help()
  {
    JFrame helper = new JFrame("Help for Table");
    helper.setBounds(0,0,600,400);
    
    JEditorPane textpane = new JEditorPane();
    textpane.setEditable(false);
    textpane.setEditorKit( new HTMLEditorKit() );
    String text = "<H1>Description:</H1><P>" +
                  "The TableJPanel displays two-dimensional data in "+
		  "a table for detailed analysis of small portions of data. "+
                  "The functionality of the table mimics many of the "+
		  "features of a typical spreadsheet.</P>" +
                  "<H2>Commands for Table</H2>" +
		  "<I><B>Note:</B> The cut, copy, and paste key commands will "+
		  "only work for single selections. Complex selections are "+
		  "not supported by these commands.</I><BR><BR>"+
		  "<B>Ctrl+X</B> -> <I>Cut</I><BR>"+
		  "<B>Ctrl+C</B> -> <I>Copy</I><BR>"+
		  "<B>Ctrl+V</B> -> <I>Paste</I><BR><BR>"+
		  "<B>Mouse Click</B> -> <I>Clear Selections/Set New "+
		  "Pointed-at Cell</I><BR>"+
		  "<B>Mouse Click/Drag</B> -> <I>Begin New Selection</I><BR>"+
		  "<B>Ctrl+Mouse Click/Drag</B> -> <I>Add Selection to "+
		  "Existing Selections</I><BR>"+
		  "<B>Shift+Mouse Click</B> -> <I>Start/Stop of New Selection"+
		  "</I><BR>"+
		  "<B>Mouse Click on Column/Row Header</B> -> "+
		  "<I>Clear Selections and Select Entire Column/Row</I><BR>"+
		  "<B>Ctrl+Mouse Click on Column/Row Header</B> -> "+
		  "<I>Add Column/Row Selection to Existing Selections</I><BR>"+
		  "<B>Mouse Click/Drag on Column Header</B> -> "+
		  "<I>Move Column</I><BR>"+
		  "<B>Mouse Click/Drag on Column Header Boundary</B> -> "+
		  "<I>Resize Column</I><BR>";
		  
    textpane.setText(text);
    JScrollPane scroll = new JScrollPane(textpane);
    scroll.setVerticalScrollBarPolicy(
 				    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    return helper;
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
    if( active_selection != null )
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
  * Get the actively changing selection. This method will only return a value
  * if the selection is actively changing, otherwise null is returned. The
  * SELECTION_CHANGING messaging String will be sent out when valid values
  * exist.
  *
  *  @return Snapshot of the changing selection as a TableRegion.
  */
  public TableRegion getActiveSelection()
  {
    if( active_selection == null )
      return null;
    // Convert active_selection to a TableRegion.
    floatPoint2D[] def_pts = new floatPoint2D[2];
    def_pts[0] = new floatPoint2D( active_selection.getX1(),
                                   active_selection.getY1() );
    def_pts[1] = new floatPoint2D( active_selection.getX2(),
                                   active_selection.getY2() );
    return new TableRegion(def_pts,true);
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
    updateLabels();
    column_labels.setBackground(color);
    if( table.getTableHeader() != null )
      table.getTableHeader().setBackground(color);
  }
  
 /**
  * Set where the text is displayed in the table. Use Java's SwingContants
  * class to specify column alignment. Alignment is limited to horizontal
  * positioning, so valid alignments are LEFT, CENTER, RIGHT, EAST, and WEST.
  */
  public void setColumnAlignment( int alignment )
  {
    // If alignment is not valid, don't proceed.
    if( !( alignment == SwingConstants.LEFT ||
           alignment == SwingConstants.RIGHT ||
           alignment == SwingConstants.CENTER ||
           alignment == SwingConstants.EAST ||
           alignment == SwingConstants.WEST ) )
      return;
    
    DefaultTableCellRenderer renderer;
    for( int i = 0; i < table.getColumnCount(); i++ )
    {
      renderer = (DefaultTableCellRenderer)table.getDefaultRenderer(
                             table.getColumnClass(i) );
      renderer.setHorizontalAlignment( alignment );
    }
    col_alignment = alignment;
  }
  
 /**
  * Set the format of how numbers are displayed in the table. If either
  * predecimal_digits or postdecimal_digits is negative, formatting of that
  * portion of the number is left unchanged. Formatting does not alter data,
  * it only alters the appearance of the data.
  *
  *  @param  predecimal_digits The number of digits to appear "before" the
  *                            decimal. This is primarily used for nicely
  *                            displaying data in LEFT alignment. Numbers will
  *                            not be visually clipped if they exceed the
  *                            specified number of predecimal digits. However,
  *                            spaces will be prepended to the number if
  *                            the number of digits is below the specified
  *                            number of predecimal digits. Predecimal digits
  *                            include the ones, tens, hundreds, ... place.
  *  @param  postdecimal_digits The number of digits to appear "after" the
  *                            decimal. This includes the tenths,hundredths,
  *                            thousandths, ... place.
  */
  public void setNumberFormat( int predecimal_digits, int postdecimal_digits )
  {
    pre_digits = predecimal_digits;
    post_digits = postdecimal_digits;
    table.repaint();
  }
  
 /**
  * Choose whether or not to display the row labels found to the left of
  * the rows.
  *
  *  @param  display If true, display the row labels, else hide them.
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
  *  @param  display If true, display the column labels, else hide them.
  */
  public void displayColumnLabels( boolean display )
  {
    if( display )
    {
      if( column_labels != null )
        table.setTableHeader(column_labels);
    }
    else
    {
      // If there is currently a JTableHeader for this table, save it in
      // the column_labels variable so it may be restored if the labels
      // need to be displayed at a later date.
      if( table.getTableHeader() != null )
      {
        column_labels = table.getTableHeader();
        // Add listener to make column selections when a double-click
        // occurs on the table header.
        column_labels.addMouseListener(new ColumnRowSelectListener());
      }
      table.setTableHeader(null);
    }
  }
  
 /**
  * Use this method to get all of the labels used to identify rows.
  *
  *  @return  An Object array of labels.
  */
  public Object[] getRowLabels()
  {
    return row_label_list.toArray();
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
    if( labels != null && labels.length != table.getRowCount() )
      return;
    
    // Clear the old list of labels.
    row_label_list.clear();
    for( int i = 0; i < labels.length; i++ )
    {
      if( labels[i].toString().length() > max_row_label.length() )
        max_row_label = labels[i].toString();
      row_label_list.addElement(labels[i]);
    }
    updateLabels();
  }
  
 /**
  * Use this method to get all of the labels used to identify columns.
  *
  *  @return An Object array of labels.
  */
  public Object[] getColumnLabels()
  {
    Object[] values = new Object[table.getColumnCount()];
    TableColumnModel tcm;
    // If the column labels are visible, use the table's tableheader.
    if( table.getTableHeader() != null )
      tcm = table.getTableHeader().getColumnModel();
    // If column labels are not visible, use the stored copy.
    else
      tcm = column_labels.getColumnModel();
    for( int i = 0; i < values.length; i++ )
      values[i] = tcm.getColumn(i).getHeaderValue();
    return values;
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
    if( labels != null && labels.length != table.getColumnCount() )
      return;
    
    TableColumnModel tcm;
    // If the column labels are visible, use the table's tableheader.
    if( table.getTableHeader() != null )
      tcm = table.getTableHeader().getColumnModel();
    // If column labels are not visible, use the stored copy.
    else
      tcm = column_labels.getColumnModel();
    for( int i = 0; i < labels.length; i++ )
      tcm.getColumn(i).setHeaderValue(labels[i]);
  }
  
 /**
  * Get the (x=column, y=row) cell which has the PointedAt border around it.
  *
  *  @return A Point containing the column and row of the cell.
  */ 
  public Point getPointedAtCell()
  {
    return focus;
  }
  
 /**
  * Set the cell which has the pointed-at focus border around it.
  *
  *  @param  col_row_pt A Point with x = column, y = row.
  */ 
  public void setPointedAtCell( Point col_row_pt )
  {
    // If one of the two is not true, do nothing.
    if( !(isValidRow(col_row_pt.y) && isValidColumn(col_row_pt.x)) )
      return;
    
    focus = new Point(col_row_pt);
    
    // Auto-positioning done below disrupts some behaviors. This will allow
    // those things affected to temporarily disable the auto-positioning.
    // One such instance is if a user manually moves a column with the mouse,
    // causing the columnMoved() event to be fired. In this case, the table
    // scrolls through the columns too fast if auto-positioning is on.
    // If moveColumn() is called, reset the viewport, even though calling this
    // method will cause a columnMoved() event to be fired.
    if( disable_auto_position && !move_column_called )
      return;
    
    // Place the selected cell in the upper-lefthand corner of the Viewport,
    // when possible.
    setVisibleLocation(col_row_pt);
    send_message( POINTED_AT_CHANGED );
  }
  
 /**
  * Get the rectangle (x=column, y=row) consisting of the visible cells.
  *
  *  @return A Rectangle containing the visible cells.
  */ 
  public Rectangle getVisibleRectangle()
  {
    Rectangle pixel_rect = scroll.getViewport().getViewRect();
    Point top_left = pixel_rect.getLocation();
    Point bottom_right = new Point(top_left);
    bottom_right.x += pixel_rect.getWidth();
    bottom_right.y += pixel_rect.getHeight();
    // Convert the pixel Point to Column/Row coordinates.
    top_left = convertPixelToColumnRow(top_left);
    bottom_right = convertPixelToColumnRow(bottom_right);
    return new Rectangle( top_left,
                          new Dimension( bottom_right.x - top_left.x,
		                         bottom_right.y - top_left.y ) );
  }
  
 /**
  * Set the cell to be visible in the upper left-hand corner of the viewport.
  * This method is comparable to a setVisibleRectangle() method, only the size
  * of the viewport has already been determined.
  *
  *  @param  col_row_pt  The column/row point specifying which cell will appear 
  *                      in the upper left-hand corner of the viewport.
  */ 
  public void setVisibleLocation( Point col_row_pt )
  {
    // Place the selected cell in the upper-lefthand corner of the Viewport,
    // when possible.
    Rectangle cell = table.getCellRect(0,0,true);
    int x_pos = cell.width * col_row_pt.x;
    int y_pos = cell.height * col_row_pt.y;
    scroll.getViewport().setViewPosition( new Point(x_pos,y_pos) );
    send_message(VIEWPORT_CHANGED);
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
  * Get the value in the cell located at the row and column specified.
  *
  *  @param  row The row where the cell is located.
  *  @param  column The column where the cell is located.
  *  @return The value located in the specified cell.
  */
  public Object getValueAt( int row, int column )
  {
    // If one of the two is not true, return null.
    if( !(isValidRow(row) && isValidColumn(column)) )
      return null;
    
    // Since the TableModelFormatter adjusted the getValueAt() method,
    // use the getUnformattedValueAt() to get the values from the original
    // TableModel.
    return
      ((TableModelFormatter)table.getModel()).getUnformattedValueAt(row,column);
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
    floatPoint2D[] def_pts = new floatPoint2D[2];
    def_pts[0] = new floatPoint2D(0,0);
    def_pts[1] = new floatPoint2D( (float)(selected[0].length-1),
                                   (float)(selected.length-1) );
    TableRegion[] new_region = new TableRegion[1];
    new_region[0] = new TableRegion(def_pts, true);
    setSelectedRegions( new_region );
  }
  
 /**
  * This method will clear all selections, both in the vector and on the
  * grid. This will not clear the table entries.
  */
  public void unselectAll()
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
  *  @param  column_to_move
  *  @param  target_column_index
  */
  public void moveColumn( int column_to_move, int target_column_index )
  {
    // Note: The pointed-at cell is automatically kept with the column when
    // it moves. The JTable remembers moved columns, and uses the same column
    // index to reference a column, even after a move.
    
    // If table is set to not allow reordering, do nothing.
    if( !getEnableMoveColumn() )
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
    unselectAll();
    move_column_called = true;
    // Move the column label and the column of data.
    table.moveColumn( column_to_move, target_column_index );
    move_column_called = false;
  }
  
 /**
  * Enable (true) or disable (false) the ability to move columns. This
  * option is enabled by default.
  *
  *  @param  is_mobile If true, column mobility is enabled.
  */
  public void enableMoveColumn( boolean is_mobile )
  {
    if( table.getTableHeader() != null )
      table.getTableHeader().setReorderingAllowed(is_mobile);
    else
      column_labels.setReorderingAllowed(is_mobile);
  }
  
 /**
  * Returns whether column reordering via drag & drop is enabled (true)
  * or disabled (false). This option is enabled by default.
  *
  *  @return If true, column reordering is enabled.
  */
  public boolean getEnableMoveColumn()
  {
    if( table.getTableHeader() != null )
      return table.getTableHeader().getReorderingAllowed();
    else
      return column_labels.getReorderingAllowed();
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
  * clear all selections. Use convertToTableRegions() method to convert
  * any list of Regions to a list of TableRegions.
  *
  *  @param  regions The regions, each of which defines a rectangular region
  *                  of cells that are either all selected or all unselected.
  *                  Since regions may overlap, order is important.
  *  @see #convertToTableRegions( Region[] any_region )
  */
  public void setSelectedRegions( TableRegion[] regions )
  {
    // Set ignore_notify to true so unselectAll() doesn't send out a
    // SELECTED_CHANGED message.
    ignore_notify = true;
    // If this method is called, clear all previous selections.
    // Note that the (x,y) point is actually stored as (column,row).
    unselectAll();
    // If regions is null, clear all selections and that is it.
    if( regions == null )
      return;
    // Use this to have other methods not send out messages since this
    // method already does. Have to reset to true since unselectAll() sets it
    // to false.
    ignore_notify = true;
    
    Point p1;
    Point p2;
    floatPoint2D[] def_pts;
    for( int i = 0; i < regions.length; i++ )
    {
      def_pts = regions[i].getDefiningPoints();
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
    floatPoint2D[] def_pts = region.getDefiningPoints();
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
    CoordTransform identity_tran = new CoordTransform();
	while( i < any_region.length &&
	       !( any_region[i] instanceof TableRegion ) )
	{
          region_bounds = any_region[i].getRegionBounds( identity_tran );
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
     	  Point[] misc_point = Region.getRegionUnion(misc_region,
     			                                     identity_tran );
     	  
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
    // If unselectAll() method is called, do not add an unselected region
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
    
    // Mark the boolean table with new (un)selected values.
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
    
    //System.out.println("Variant vs location: " + row_variant+","+location);
    // Increment row if over half way scrolled down on next cell.
    if( !sameCell(row_variant,location) )
    {
      cell_row_col.y++;
    }
    
    // Set the labels for the columns and rows.
    Object label = new Object();
    for( int row = 0; row < row_labels.getRowCount(); row++ )
    {
      label = row_label_list.elementAt(row + cell_row_col.y);
      row_labels.setValueAt(label,row,0);
    }
    
    // This code will expand the row_labels table to the largest label if
    // the row_labels has a Graphics object and it has a column of data.
    Graphics graphics = row_labels.getGraphics();
    if( graphics != null && row_labels.getColumnCount() > 0 )
    {
      FontMetrics fontmet = graphics.getFontMetrics();
      int max_length = fontmet.stringWidth(max_row_label);
      TableColumn row_column = row_labels.getColumnModel().getColumn(0);
      if( row_column.getWidth() < max_length )
      {
    	row_column.setMinWidth(max_length + 10);
      }
      else if( row_column.getWidth() > max_length )
      {
    	row_column.setMinWidth(max_length + 10);
    	row_column.setPreferredWidth(max_length+10);
      }
    }
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
	// This code is here if user starts to drag before ctrl is pressed.
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
	  focus = convertPixelToColumnRow( new Point( ctrl_extend ) );
	  active_selection.setBounds( active_selection.getX1(),
	                              active_selection.getY1(),
	                              focus.x, focus.y );
	  // Since pointed_at_changed message sent on initial click, only send
          // message if on a different point.
	  if( !sameCell(ctrl_anchor,me.getPoint()) )
	  {
            send_message(POINTED_AT_CHANGED);
            send_message(SELECTION_CHANGING);
	  }
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
      focus = convertPixelToColumnRow( new Point( extend ) );
      active_selection.setBounds( active_selection.getX1(),
                                  active_selection.getY1(),
        		          focus.x, focus.y );
      // Since pointed_at_changed message sent on initial click, only send
      // message if on a different point.
      if( !sameCell(anchor,me.getPoint()) )
      {
        send_message(POINTED_AT_CHANGED);
        send_message(SELECTION_CHANGING);
      }
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
      // if the point is not on any cell, ignore the mouse event
      if( !isPointOnCell(me.getPoint()) )
        return;
      
      // A selection has been started
      active_selection = new CoordBounds();
      
      // If ctrl is down, we are adding/removing from initial selected cells.
      if( me.isControlDown() )
      {
        // Reset the starting point. Clear the ending point to null.
        ctrl_anchor = me.getPoint();
	ctrl_extend = null;
	// Set the cell that should have focus.
	focus = convertPixelToColumnRow(new Point(ctrl_anchor));
	active_selection.setBounds( focus.x, focus.y, focus.x, focus.y );
	send_message(POINTED_AT_CHANGED);
      }
      // If only shift is down, select everything between current point and
      // the anchor or first point.
      else if( me.isShiftDown() )
      {
        if( anchor == null )
	{
	  anchor = me.getPoint();
	  // Set the cell that should have focus.
	  focus = convertPixelToColumnRow( new Point( anchor ) );
	  active_selection.setBounds( focus.x, focus.y, focus.x, focus.y );
	  send_message(POINTED_AT_CHANGED);
	}
	else
	{
	  // If shift is misused, remove all selections and start new selection
	  // at the given point. Misuse occurs when a selection already exists
	  // and only the shift modifier is used to add a selection.
	  if( getSelectedCells().getDefiningPoints().length > 1 )
	  {
	    setSelectedRegions(null);
	    anchor = me.getPoint();
	    focus = convertPixelToColumnRow( new Point( anchor ) );
	    active_selection.setBounds( focus.x, focus.y, focus.x, focus.y );
	  }
	  // Get new ending value of rectangular region of selected cells.
	  extend = me.getPoint();
	  // Set the cell that should have focus.
	  focus = convertPixelToColumnRow( new Point(extend) );
	  active_selection.setBounds( active_selection.getX1(),
	                              active_selection.getY1(),
	                              focus.x, focus.y );
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
	  focus = convertPixelToColumnRow(new Point(anchor));
	  active_selection.setBounds( focus.x, focus.y, focus.x, focus.y );
	  send_message(POINTED_AT_CHANGED);
	}
	else
	{
	  // If clicked without any modifiers, clear all previous selections.
          unselectAll();
	  // Set new anchor point and select it.
	  anchor = me.getPoint();
	  // Set the cell that should have focus.
	  focus = convertPixelToColumnRow( new Point(anchor) );
	  active_selection.setBounds( focus.x, focus.y, focus.x, focus.y );
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
      active_selection = null;
      
      // If an additional selection was made, toggle the cells to the
      // opposite selected value of the ctrl_anchor.
      if( ctrl_anchor != null )
      {
	Point rc_ctrl_anchor = convertPixelToColumnRow(ctrl_anchor);
        // If more than once cell, toggle all cells in additional region.
	if( ctrl_extend != null )
	{
	  floatPoint2D[] def_pts = new floatPoint2D[2];
          def_pts[0] = new floatPoint2D(convertPixelToColumnRow(ctrl_anchor));
          def_pts[1] = new floatPoint2D(convertPixelToColumnRow(ctrl_extend));
          addSelectedRegion( new TableRegion( def_pts,
	                     !selected[rc_ctrl_anchor.y][rc_ctrl_anchor.x] ) );
	}
	// Else, selection is only one cell. Toggle that cell.
	else
	{
	  floatPoint2D[] def_pts = new floatPoint2D[2];
          def_pts[0] = new floatPoint2D(convertPixelToColumnRow(ctrl_anchor));
          def_pts[1] = new floatPoint2D(convertPixelToColumnRow(ctrl_anchor));
          addSelectedRegion( new TableRegion( def_pts,
	                     !selected[rc_ctrl_anchor.y][rc_ctrl_anchor.x] ) );
        }
      }
      // If an initial selection was made, select the cells in the new region.
      else if( anchor != null )
      {
        if( extend != null )
	{
	  floatPoint2D[] def_pts = new floatPoint2D[2];
          def_pts[0] = new floatPoint2D(convertPixelToColumnRow(anchor));
          def_pts[1] = new floatPoint2D(convertPixelToColumnRow(extend));
          addSelectedRegion( new TableRegion( def_pts, true ) );
	}
	else
	{
	  floatPoint2D[] def_pts = new floatPoint2D[2];
          def_pts[0] = new floatPoint2D(convertPixelToColumnRow(anchor));
          def_pts[1] = new floatPoint2D(convertPixelToColumnRow(anchor));
          addSelectedRegion( new TableRegion( def_pts, true ) );
	}
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
      Rectangle bounds = scroll.getViewport().getViewRect();
      // The size of a table cell.
      Rectangle cell = table.getCellRect(0,0,true);
      int num_rows = Math.round( (float)bounds.height/(float)cell.height );
      // If the number of rows/columns is larger than the size of the
      // selected array, use the dimensions of the selected array.
      if( num_rows > selected.length )
        num_rows = selected.length;
      
      // Make sure the number of rows/columns is not negative.
      if( num_rows < 0 )
        num_rows = 0;
	
      // Find the remainder of the height.
      int remainder_height = bounds.height - scroll.getBounds().height;
      // Set the scroll bar increments to the size of the table cells.
      scroll.getHorizontalScrollBar().setBlockIncrement(cell.width);
      scroll.getHorizontalScrollBar().setUnitIncrement(cell.width);
      scroll.getVerticalScrollBar().setBlockIncrement(cell.height);
      scroll.getVerticalScrollBar().setUnitIncrement(cell.height);
      
      // Remove the label panels so they can be rebuilt.
      remove( row_label_container );
      
      // ********* Build the left row label panel. *********
      
      // The actual labels containing the number of cells equal to the
      // number of entirely visible rows.
      row_labels = new JTable(num_rows,1);
      // This will prevent users from editing the row_labels table.
      row_labels.setModel( new RowLabelTableModel(num_rows,1) );
      // Add listener to make row selections when a double-click occurs on
      // the row_labels table.
      row_labels.addMouseListener(new ColumnRowSelectListener());
      // Make sure there are rows to render.
      if( num_rows > 0 )
        row_labels.setDefaultRenderer( row_labels.getColumnClass(0),
                                       new IgnoreFocusRenderer() );
      DefaultTableCellRenderer renderer =
       (DefaultTableCellRenderer)row_labels.getDefaultRenderer(
                             row_labels.getColumnClass(0) );
      renderer.setHorizontalAlignment( SwingConstants.CENTER );
      row_labels.setBackground(label_color);
      row_labels.setCellSelectionEnabled(false);
      
      // This spacer will account for the space taken up by the column headers,
      // enabling the correct rows to be aligned with the correct labels.
      JPanel up_row_spacer = new JPanel();
      up_row_spacer.setBackground(label_color);
      // This will blend the panel with the row_labels table.
      up_row_spacer.setBorder(
                  BorderFactory.createLineBorder( row_labels.getGridColor() ) );
      if( table.getTableHeader() != null )
        up_row_spacer.setPreferredSize( new Dimension( 0,
                  table.getTableHeader().getHeight() + cell_spacing.height ) );
      
      // The low_row_spacer accounts for a partial row not entirely
      // appearing in the JTable.
      JPanel low_row_spacer = new JPanel();
      low_row_spacer.setBackground(label_color);
      low_row_spacer.setPreferredSize( new Dimension( 0,
                                                      remainder_height ) );
      
      row_label_container.removeAll();
      row_label_container.add( up_row_spacer, BorderLayout.NORTH );
      row_label_container.add( row_labels, BorderLayout.CENTER );
      row_label_container.add( low_row_spacer, BorderLayout.SOUTH );
      
      // Add the two new panels to the container.
      add( row_label_container, BorderLayout.WEST );
      
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
      if( table.getRowCount() == 0 || table.getColumnCount() == 0 )
        return;
      // Ignore events until scroll bar has stopped moving.
      if( ((JScrollBar)ae.getSource()).getValueIsAdjusting() )
      {
        // Update the labels as the scrollbar moves.
        updateLabels();
        return;
      }
      
      // Removing this "if" will cause columns to be shifted so the top-left
      // corner of a cell is always in the top-left corner of the viewport.
      if( ae.getSource() == scroll.getHorizontalScrollBar() )
      {
        // Tell all listeners that the viewport has been changed.
        send_message(VIEWPORT_CHANGED);
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
      
      // Tell all listeners that the viewport has been changed.
      send_message(VIEWPORT_CHANGED);
    }
  }
  
 /*
  *
  */
  private class ColumnRowSelectListener extends MouseAdapter
  {
    public void mouseClicked( MouseEvent me )
    {
      if( me.getSource() == column_labels )
      {
        int col = table.columnAtPoint(me.getPoint());
     	floatPoint2D[] def_pts = new floatPoint2D[2];
     	def_pts[0] = new floatPoint2D(col,0);
     	def_pts[1] = new floatPoint2D(col,table.getRowCount()-1);
        if( me.isControlDown() )
	{
	  addSelectedRegion( new TableRegion(def_pts, true) );
	}
	else
	{
	  TableRegion[] new_region = new TableRegion[1];
	  new_region[0] = new TableRegion(def_pts, true);
	  setSelectedRegions(new_region);
        }
      }
      else if( me.getSource() == row_labels )
      {
        int row = row_labels.rowAtPoint(me.getPoint());
     	floatPoint2D[] def_pts = new floatPoint2D[2];
     	def_pts[0] = new floatPoint2D(0,row);
     	def_pts[1] = new floatPoint2D(table.getColumnCount()-1, row);
        if( me.isControlDown() )
	{
	  addSelectedRegion( new TableRegion(def_pts, true) );
	}
	else
	{
	  TableRegion[] new_region = new TableRegion[1];
	  new_region[0] = new TableRegion(def_pts, true);
	  setSelectedRegions(new_region);
        }
      }
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
      // If the current cell is in focus, pass in true for hasFocus.
      if( focus != null && focus.y == row && focus.x == column )
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
  * This renderer is for the row_labels table. Do not
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
  * This class listens for when columns are moved and reordered. If a column
  * is moved, this listener will clear all selections. If the column containing
  * the pointed-at cell is moved, either directly or indirectly, the pointed-at
  * cell location will be adjusted to the new column location.
  */
  private class ColumnReorderedListener implements TableColumnModelListener
  {
    public void columnAdded( TableColumnModelEvent tme )
    {
      // Stub
    }
    
    public void columnMarginChanged( ChangeEvent ce )
    {
      // Stub
    }
    
    public void columnMoved( TableColumnModelEvent tme )
    {
      // If the range is more than one cell, see if the to or from move affected
      // the column with the pointed-at cell.
      if( tme.getFromIndex() != tme.getToIndex() )
      {
        // Unselect all selections made since column order is disrupted.
        unselectAll();
        
	// Check to see if the pointed-at cell was the column moved.
        if( tme.getFromIndex() == getPointedAtCell().x )
	{
	  // Disable the auto-positioning done by setPointedAtCell() since it
	  // interfers with tracing the moved column.
          disable_auto_position = true;
	  setPointedAtCell(new Point(tme.getToIndex(),getPointedAtCell().y));
          disable_auto_position = false;
	}
        // Check to see if the pointed-at cell was replaced by the moved column.
        else if( tme.getToIndex() == getPointedAtCell().x )
	{
	  // If column is moved to the right to col. X, the column at X will
	  // shift to the left. If column is moved to left to col X, the 
	  // column at X will move to the right.
	  int shift = -1; // Assume moving column goes to the right.
	  // If actually to the left, adjust accordingly.
	  if( tme.getToIndex() < tme.getFromIndex() )
	    shift = 1;
	  shift += tme.getToIndex();
	  // Disable the auto-positioning done by setPointedAtCell() since it
	  // interfers with tracing the moved column.
          disable_auto_position = true;
	  setPointedAtCell(new Point(shift,getPointedAtCell().y));
          disable_auto_position = false;
	}
      }
    }
    
    public void columnRemoved( TableColumnModelEvent tme )
    {
      // Stub
    }
    
    public void columnSelectionChanged( ListSelectionEvent lse )
    {
      // Stub
    }
  }
  
 /*
  * The sole purpose of this class is to prevent users from editing the
  * row_labels table.
  */
  private class RowLabelTableModel extends DefaultTableModel
  {
    protected RowLabelTableModel(int row_count, int column_count)
    {
      super(row_count, column_count);
      
    }
    
    public boolean isCellEditable( int row, int column )
    {
      return false;
    }
  }
 
 /*
  * This class wraps around the TableModel in table and modifies the
  * getValueAt() method to account for formatting changes. This does not
  * modify the data, only modifies how the data is displayed.
  */ 
  private class TableModelFormatter implements TableModel
  {
    TableModel model;
    protected TableModelFormatter( TableModel modeled_data )
    {
      model = modeled_data;
    }
      
    public Object getValueAt( int row, int column )
    {
      Object value = model.getValueAt(row,column);
      // Make sure value is not null.
      if( value == null )
        return new String("");
	 
      if( value instanceof Number ||
          value instanceof String )
      {
        if( value instanceof String )
        {
	  // If a number, convert value to a Double.
          try
	  {
	    double temp = Double.parseDouble( (String)value );
	    value = new Double(temp);
	  }
	  // If not a number, return the original value.
	  catch( NumberFormatException nfe ) { return value; }
        }
      
        // If both values are negative, the format should be left unchanged.
        if( pre_digits < 0 && post_digits < 0 )
          return value;
        
	int field_width = 0;
	String s_num = value.toString();
	int decimal_index = s_num.indexOf(".");
	int negative_index = s_num.indexOf("-");
	int temp_post_digits = post_digits;
	// if true, leave whole number unformatted.
        if( pre_digits < 0 )
	{
	  // If no decimal, then whole s_num is before decimal.
	  if( decimal_index < 0 )
	  {
	    field_width = s_num.length();
	    // Add decimal and postdecimal digits.
	    field_width += 1 + post_digits;
          }
	  // Otherwise, give length of number including the decimal and
	  // any negative sign. Also add on postdecimal digits.
	  else
	    field_width = decimal_index + post_digits;
        }
	// If true, leave everything after decimal unformatted.
	else if( post_digits < 0 )
	{
	  if( decimal_index >= 0 )
	  {
	    // Get width of digits after the decimal.
	    field_width = (s_num.length()-1) - decimal_index;
	    // Remember the number of digits after the decimal for later
	    // formatting.
	    temp_post_digits = field_width;
	    // Add on width of pre-decimal portion, including the decimal point
	    // and space for any negative sign. 
	    field_width += pre_digits + 1; // +1 accounts for decimal point.
	    // If negative, increase field width by one to account for negative.
	    if( negative_index >= 0 )
	      field_width++;
	  }
	}
	else
	{
	  // Width made up of predecimal dgts, a decimal, and postdecimal dgts.
	  field_width = pre_digits + 1 + post_digits;
	  // If negative, increase field width by one to account for negative.
	  if( negative_index >= 0 )
	    field_width++;
	}
	
	return Format.real( ((Number)value).doubleValue(), field_width, 
	                    temp_post_digits );
      }
      // If not a number or a String representation of a number, return the
      // original value.
      return value;
    }
    
    public Object getUnformattedValueAt( int row, int column )
    {
      return model.getValueAt(row,column);
    }
    
    public Class getColumnClass(int i){ return new String().getClass(); }
    
    public void setValueAt( Object value, int row, int column )
    {
      model.setValueAt(value,row,column);
    }
    
    public int getRowCount(){ return model.getRowCount(); }
    
    public int getColumnCount(){ return model.getColumnCount(); }
    
    public String getColumnName(int i){ return model.getColumnName(i); }
    
    public boolean isCellEditable( int row, int column )
    {
      return model.isCellEditable(row,column);
    }
    
    public void addTableModelListener( TableModelListener tml )
    {
      model.addTableModelListener( tml );
    }
    
    public void removeTableModelListener( TableModelListener tml )
    {
      model.addTableModelListener( tml );
    }
  }
 
 /**
  * Test Program - use for testing only.
  *
  *  @param  args Command line arguments, these are ignored.
  */ 
  public static void main( String args[] )
  {
    JFrame frame = new JFrame("TableJPanel Test");
    frame.setBounds(0,0,600,300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    int row = 100;
    int col = 150;
    
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
      row_lab_list[i] = new String( "ROW: "+i );
    }
    
    VirtualArray2D iva = new VirtualArray2D(values);
    TableJPanel testable = new TableJPanel(TableModelMaker.getModel(iva));
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
    testable.setPointedAtCell(new Point(14,12));
    // Test the ObjectState by saving the initial settings, then changing
    // all of the defaults.
    ObjectState state = testable.getObjectState(IPreserveState.PROJECT);
    // Test to make sure these settings are not saved after setObjectState()
    // is called.
    testable.unselectAll();
    testable.setLabelBackground(Color.green);
    testable.setRowLabels(row_lab_list);
    testable.setColumnLabels(col_lab_list);
    testable.setPointedAtCell(new Point(0,0));
    testable.displayColumnLabels(true);
    testable.displayRowLabels(false);
    testable.enableMoveColumn(false);
    testable.setNumberFormat(2,2);
    testable.setColumnAlignment(SwingConstants.CENTER);
    testable.setVisibleLocation(new Point(18,20));
    testable.setObjectState(state);
    /*
    System.out.println("Table Size: [rows = "+testable.getRowCount()+", "+
                       "columns = "+testable.getColumnCount()+"]");
    
    testable.setPointedAtCell(new Point(2,0));
    testable.moveColumn(2,-4);  // Test error checking.
    testable.moveColumn(26,20); // Test error checking.
    */
    frame.getContentPane().add(testable);
    WindowShower.show(frame);
  }
}
