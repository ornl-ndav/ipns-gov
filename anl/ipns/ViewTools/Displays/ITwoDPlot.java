/*
 * Created on 9/06/2004
 * 
 * @author Ferdi Franceschini
 * The Bragg Institute
 * Australian Nuclear Science and Technology Organisation
 * ffr@ansto.gov.au
 */
package gov.anl.ipns.ViewTools.Displays;

public interface ITwoDPlot {
	/**
	 * Clear plot data
	 */
	public void clear();
	
	public void setPoint(int row, int col, float z);
	
	public void setPlotTitle(String t);
	
	public void setYLabel(String t);
	
	public void setXLabel(String t);
	
	public void setScanNum(int sn);
	
	public void addRow(float row[], int rows);
	
	public void newImage(float image[][]);
	
	public void setColors(String CS, boolean sides, boolean redraw);
}
