/*
 * Created on 9/06/2004
 * 
 * @author Ferdi Franceschini The Bragg Institute Australian Nuclear Science and
 * Technology Organisation ffr@ansto.gov.au
 */
package gov.anl.ipns.ViewTools.Displays;

import java.awt.Color;
import java.awt.Rectangle;

public interface IOneDPlot {

    /**
     * Add a plot to an existing graph
     * 
     * @param xVals
     *            array of x data
     * @param yVals
     *            array of y data
     * @param yErrs
     *            array of y errors, use null for no errors
     */
    public void addPlot(float[] x, float[] y, float[] yerr);

    /**
     * Remove plot with index equal to gnum
     * 
     * @param gnum
     */
    public void clear(int gnum);

    /**
     * Remove all plots
     */
    public void clearAll();

    /**
     * @return Next available plot index
     */
    public int nextPlotIndex();

    /**
     * Set colour of plot gnum
     * 
     * @param gnum:
     *            plot index
     * @param col :
     *            colour for plot at gnum
     */
    public void setColor(int gnum, Color col);

    /**
     * Set log scale on Y axis and redraw plot.
     * 
     * @param scale
     *            true=log, false = linear
     */
    public void setLogScaleY(boolean scale);

    /**
     * Set log scale on X axis and redraw plot.
     * 
     * @param scale
     *            true=log, false = linear
     */
    public void setLogScaleX(boolean scale);

    /**
     * Get pixel coordinates of top left corner of plot region
     * with width and height.
     * @return Rectangle
     */
    public Rectangle getPlotBounds();

    /**
     * @return minimum X value from all plotted data sets
     * or the minimum set by setXDataRange
     */
    public float getXPlotMin();

    /**
     * @return maximum X value from all plotted data sets
     * or the maximum set by setXDataRange
     */
    public float getXPlotMax();

    /**
     * @return minimum Y value from all plotted data sets
     * or the minimum set by setYDataRange
     */
    public float getYPlotMin();

    /**
     * @return maximum Y value from all plotted data sets
     * or the maximum set by setYDataRange
     */
    public float getYPlotMax();

    /**
     * Plot a point without error bars
     * 
     * If gnum == the index number of an existing plot then append the point.
     * Otherwise start a new plot.
     * 
     * @param x
     * @param y
     * @param gnum :
     *            index of plot
     * @return Current number of points for plot at gnum
     */
    public int setPoint(float x, float y, int gnum);

    /**
     * Plot a point with error bars If gnum == the index number of an existing
     * plot then append the point. Otherwise start a new plot.
     * 
     * @param x
     * @param y
     * @param yerr
     * @param gnum :
     *            index of plot
     * @return Current number of points for plot at gnum
     */
    public int setPoint(float x, float y, float yerr, int gnum);

    /**
     * Use this to set the number of points to show in a live data plot
     * @param np
     */
    public void setNumPoints(int np);

    public void setGraphTitle(String t);

    public void setYLabel(String t);

    public void setXLabel(String t);

    public void setXUnits(String u);

    public void setYUnits(String u);

    /** Set Y data range for plot
     * Note: Implementors are free to set the Y axis scale
     * to leave space around the plotted data. 
     * @param min
     * @param max
     */
    public void setYDataRange(float min, float max);

    /** Set X data range for plot
     * Note: Implementors are free to set the X axis scale
     * to leave space around the plotted data. 
     * @param min
     * @param max
     */
    public void setXDataRange(float min, float max);

    /**
     * Transform x screen coordinate to data coordinate
     * 
     * @param pixel count from x bound of the axes container
     * @return
     *            screen coordinate
     */
    public float xScreen2Data(float x);

    /**
     * Transform y screen coordinate to data coordinate
     * 
     * @param pixel count from y bound of the axes container
     * @return
     *            screen coordinate
     */
    public float yScreen2Data(float y);
    
    /**
     * Autoscale Y axis
     */
    public void autoYScale();

    /**
     * Autoscale X axis
     */
    public void autoXScale();

    /**
     * @return true if X axis is auto-scaling
     */
    public boolean is_autoXScale();

    /**
     * @return true if Y axis is auto-scaling
     */
    public boolean is_autoYScale();
}