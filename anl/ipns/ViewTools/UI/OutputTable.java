/*
 * @(#)OutputTable.java  0.1 2000/08/17   Dongfeng Chen
 *                                        Alok Chatterjee
 *
 * $Log$
 * Revision 1.1  2000/08/17 19:04:50  dennis
 * Class to display a table of numbers with column headings and a title.
 *
 *
 */
package DataSetTools.components.ui;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;

/**
 * This class produces a table displaying a two dimensional array of values
 * in a JTable
 */

public class OutputTable extends JFrame {

    private boolean DEBUG = true;

    /**
     *  Construct the table for the given array of values, list of labels
     *  and title.
     *
     *  @param  a          Two dimensional array of floats.  NOTE: This must 
     *                     be a rectangular array.  Each row must have the 
     *                     same number of elements.
     *
     *  @param  headings   List of column headings to be placed on each 
     *                     column of the table.  
     *
     *  @param  title      Title to be placed on the JFrame 
     */

    public OutputTable(float [][] a, String [] headings, String title ) {

        super(title);

        int nrow=a.length;
        int ncol=a[0].length;
        
        Object[][] data =new Object[nrow][ncol];
        
        for(int i = 0; i<nrow; i++)
            for(int j = 0; j <ncol; j++)
            data[i][j]=new Float(a[i][j]);
            
	//	DefaultTableModel dtm = new DefaultTableModel(data, headings );
	//	table.setModel(dtm);
	//	table.setSize( 200, 200 );
        
        
        //*
        
        final JTable table = new JTable(data, headings);
        table.setPreferredScrollableViewportSize(new Dimension(500, 650));
        IsawGUI.ExcelAdapter el = new IsawGUI.ExcelAdapter(table);
        
        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    printDebugData(table);
                }
            });
        }

        //Create the scroll pane and add the table to it. 
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this window.
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //*/
    }

    private void printDebugData(JTable table) {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        javax.swing.table.TableModel model = table.getModel();

        System.out.println("Value of data: ");
        for (int i=0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j=0; j < numCols; j++) {
                System.out.print("  " + model.getValueAt(i, j));
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }
// for debug 
    public static void main(String[] args) {
        
        float [][] a = new float [2000][4];
        for(int i = 0; i<a.length; i++)
            for(int j = 0; j <a[i].length; j++)
             a[i][j] = (float)(i+j);
        
        String [] b =           {"Index", 
                                "x",
                                "y",
                                "Err"
                                          };
        
        OutputTable frame = new OutputTable(a,b,"text");
        frame.pack();
        frame.setVisible(true);
    }
}

