/*
 * Created on 9/06/2004
 * 
 * @author Ferdi Franceschini
 * The Bragg Institute
 * Australian Nuclear Science and Technology Organisation
 * ffr@ansto.gov.au
 */
package gov.anl.ipns.ViewTools.Displays;

import java.awt.Color;

public interface IOneDPlot {
    /**
     * Add a plot to an existing graph and set the title
     * 
     * @param x
     *            array of x data
     * @param y
     *            array of y data
     * @param yerr
     *            array of y errors, use null for no errors
     * @param t
     *            plot title
     */
    public void addPlot(float[] x, float[] y, float[] yerr, String title,
            int gnum);

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
     * @param scale true=log, false = linear
     */
    public void setLogScaleY(boolean scale);
    
    /**
     * Set log scale on X axis and redraw plot.
     * 
     * @param scale true=log, false = linear
     */
    public void setLogScaleX(boolean scale);

    /**
     * Allows user to set the minimum with a single click on the plot.
     * The maximum is set to the data maximum.
     * TODO Maximum should auto-scale 
     * @param x minimum
     */
    public void setXScaleMin(float x);

    public float getXScaleMin();

    /**
     * Allows user to set the maximum with a single click on the plot.
     * The maximum is set to the data minimum.
     * TODO Minimum should auto-scale 
     * @param x maximum
     */
    public void setXScaleMax(float x);

    public float getXScaleMax();

    /**
     * Allows user to set the minimum with a single click on the plot.
     * The maximum is set to the data maximum.
     * TODO Maximum should auto-scale 
     * @param y minimum
     */
    public void setYScaleMin(float y);

    public float getYScaleMin();

    /**
     * Allows user to set the maximum with a single click on the plot.
     * The maximum is set to the data minimum.
     * TODO Minimum should auto-scale 
     * @param y maximum
     */
    public void setYScaleMax(float y);

    public float getYScaleMax();

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

    public void setNumPoints(int np);

    public void setPlotTitle(String t);

    public void setYLabel(String t);

    public void setXLabel(String t);

    public void setXUnits(String u);

    public void setYUnits(String u);

    public void setYScale(float min, float max);

    public void setXScale(float min, float max);

    public void autoYScale();

    public void autoXScale();
}