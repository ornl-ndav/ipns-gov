/**
 * @author Ferdi Franceschini
 * The Bragg Institute, ANSTO
 * ffr@ansto.gov.au
 * 
 * Created on 24/07/2004
 *
 */
package gov.anl.ipns.ViewTools.Displays;

import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.ViewTools.Components.Transparency.AxisOverlay2D;
import gov.anl.ipns.ViewTools.Components.Transparency.ITruLogAxisAddible;
import gov.anl.ipns.ViewTools.Panels.Graph.GraphJPanel;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordJPanel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

/**
 * DisplayPanel1D allows data arrays or single points to be plotted. It consists
 * of a GraphJPanel contained within an AxisOverlay2D object and is suitable for
 * embedding within SWT containers.
 */
public class DisplayPanel1D extends GraphJPanel implements IOneDPlot,
        ITruLogAxisAddible, ActionListener {

    public Rectangle regionInfo;

    public int dP1DPrecision, numPlots = 0, numPoints = 0;

    public AxisOverlay2D axisPane;

    private String title = "", xLabel = "", xUnits = "", yLabel = "",
            yUnits = "";

    protected Float minX=null, maxX=null, minY=null, maxY=null;

    private float xValues[] = new float[1];

    private float yValues[] = new float[1];

    private float yErrs[] = new float[1];

    // Container for plot
    private Container _container;

    // Make space for axis labels
    int xOffset = 60, yOffset = 50;

    /**
     * Create an empty graph in the given container, this is useful for live
     * data acquisition
     * 
     * @param container
     *            generic container object
     */
    public DisplayPanel1D(Object container) {
        super();
        _container = (Container) container;

        float xmin, xmax, ymin, ymax;
        dP1DPrecision = 4;
        axisPane = new AxisOverlay2D(this);
        axisPane.setLayout(null);
//        axisPane.setOpaque(true);
        axisPane.add(this);
        _container.add(axisPane);
        addActionListener(this);
    }

    /**
     * Create a graph from the given x and y data arrays with optional error
     * bars.
     * 
     * @param container :
     *            generic container object
     * @param xVals
     *            array of x data
     * @param yVals
     *            array of y data
     * @param yErrs
     *            array of y errors, use null for no errors
     */
    public DisplayPanel1D(Object container, float[] xVals, float[] yVals,
            float yErrs[]) {
        this(container);
        addPlot(xVals, yVals, yErrs);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#addPlot(float[], float[],
     *      float[])
     */
    public void addPlot(float[] xVals, float[] yVals, float[] yErrs) {
        setData(xVals, yVals, numPlots, false);
        if (yErrs != null) {
            setErrors(yErrs, 11, numPlots, false);
        }
        numPlots++;
        axisPane.repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#clear(int)
     */
    public void clear(int gnum) {
        numPoints = 0;
        xValues = new float[1];
        yValues = new float[1];
        yErrs = new float[1];
        setData(xValues, yValues, gnum, false);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#clearAll()
     */
    public void clearAll() {
        numPoints = 0;
        numPlots = 0;
        clearData();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#setColor(int,
     *      java.awt.Color)
     */
    public void setColor(int gnum, Color col) {
        setColor(col, gnum, true); //might need gnum+1
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#nextPlotIndex()
     */
    public int nextPlotIndex() {
        return getNum_graphs();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#setPoint(float, float,
     *      int)
     */
    public int setPoint(float x, float y, int gnum) {
        //TODO assert(gnum == numPlots || gnum - numPlots == 1);
        numPoints++; //TODO get current number of points for plot gnum

        if (numPoints > 1) {
            float tempyValues[] = new float[numPoints];
            float tempxValues[] = new float[numPoints];
            for (int i = 0; i < numPoints - 1; i++) {
                tempyValues[i] = yValues[i];
                tempxValues[i] = xValues[i];
            }
            yValues = tempyValues;
            xValues = tempxValues;
        }
        xValues[numPoints - 1] = x;
        yValues[numPoints - 1] = y;
        if (numPoints > 1) {
            setData(xValues, yValues, gnum, false);
        }
        axisPane.repaint();
        if (gnum > numPlots || numPlots == 0)
            numPlots++;
        return numPoints;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#setPoint(float, float,
     *      float, int)
     */
    public int setPoint(float x, float y, float yerr, int gnum) {
        if (numPoints > 1) {
            float tempyErrs[] = new float[numPoints];
            for (int i = 0; i < numPoints - 1; i++) {
                tempyErrs[i] = yErrs[i];
            }
            yErrs = tempyErrs;
        }
        yErrs[numPoints - 1] = yerr;
        if (numPoints > 1) {
            setErrors(yErrs, 11, gnum, true);
        }
        numPoints = setPoint(x, y, gnum);
        return numPoints;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#setNumPoints(int)
     */
    public void setNumPoints(int np) {
        numPoints = np;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#setPlotTitle(java.lang.String)
     */
    public void setGraphTitle(String t) {
        title = t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#setYLabel(java.lang.String)
     */
    public void setYLabel(String t) {
        yLabel = t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#setXLabel(java.lang.String)
     */
    public void setXLabel(String t) {
        xLabel = t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#setXUnits(java.lang.String)
     */
    public void setXUnits(String u) {
        xUnits = u;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#setYUnits(java.lang.String)
     */
    public void setYUnits(String u) {
        yUnits = u;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#setYScale(float, float)
     */
    public void setYDataRange(float min, float max) {
        minY = new Float(min);
        maxY = new Float(max);
        setY_bounds(minY.floatValue(), maxY.floatValue());
        axisPane.repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#setXScale(float, float)
     */
    public void setXDataRange(float min, float max) {
        minX = new Float(min);
        maxX = new Float(max);
        setX_bounds(min, max);
        axisPane.repaint();
    }

    /* (non-Javadoc)
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#yScreen2Data(int)
     */
    public float yScreen2Data(float y) {
        return local_transform.MapYFrom(y
                + local_transform.getDestination().getY1());
    }

    /* (non-Javadoc)
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#xScreen2Data(int)
     */
    public float xScreen2Data(float x) {
        return local_transform.MapXFrom(x
                + local_transform.getDestination().getX1());
    }

    /* (non-Javadoc)
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#getPlotBounds()
     */
    public Rectangle getPlotBounds() {
        return getBounds();
    }

    public float getXPoint() {
        return getCurrent_WC_point().x;
    }

    public float getYPoint() {
        return getCurrent_WC_point().y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.Transparency.IAxisAddible#getAxisInformation(int)
     */
    public AxisInfo getAxisInformation(int axiscode) {
        CoordBounds logBounds = null;
        AxisInfo ai = null;
        boolean logX = false, logY = false;
        float x1, y1, x2, y2;

        if (axiscode == AxisInfo.X_AXIS) {
            x1 = getPositiveXmin();
            x2 = getXmax();
            ai = new AxisInfo(x1, x2, xLabel, xUnits, AxisInfo.LINEAR);
        } else {
            y1 = getPositiveYmin();
            y2 = getYmax();
            ai = new AxisInfo(y1, y2, yLabel, yUnits, AxisInfo.LINEAR);
        }
        return ai;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.Transparency.IAxisAddible#getTitle()
     */
    public String getTitle() {
        return title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.Transparency.IAxisAddible#getPrecision()
     */
    public int getPrecision() {
        return dP1DPrecision;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.Transparency.IOverlayAddible#getRegionInfo()
     */
    public Rectangle getRegionInfo() {
        int w = 2 * _container.getWidth() / 3, h = 2 * _container.getHeight() / 3;
        setBounds(xOffset, yOffset, w, h);
        regionInfo = new Rectangle(xOffset, yOffset, w, h);
        return regionInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#getXAxisMin()
     */
    public float getXPlotMin() {
        if (minX == null) {
            minX = new Float(getXmin());
        }
        return minX.floatValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#getXAxisMax()
     */
    public float getXPlotMax() {
        if (maxX == null) {
            maxX = new Float(getXmax());
        }
        return maxX.floatValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#getYAxisMin()
     */
    public float getYPlotMin() {
        if (minY == null) {
            minY = new Float(getYmin());
        }
        return minY.floatValue();
    }



    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#getYAxisMax()
     */
    public float getYPlotMax() {
        if (maxY == null) {
            maxY = new Float(getYmax());
        }
        return maxY.floatValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Panels.Graph.GraphJPanel#setLogScaleY(boolean)
     */
    public void setLogScaleY(boolean scale) {
        if (scale == true) {
            axisPane.setYScale(AxisInfo.TRU_LOG);
        } else {
            axisPane.setYScale(AxisInfo.LINEAR);
        }
        axisPane.setTwoSided(false);
        super.setLogScaleY(scale);
        axisPane.repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Panels.Graph.GraphJPanel#setLogScaleX(boolean)
     */
    public void setLogScaleX(boolean scale) {
        if (scale == true) {
            axisPane.setXScale(AxisInfo.TRU_LOG);
        } else {
            axisPane.setXScale(AxisInfo.LINEAR);
        }
        axisPane.setTwoSided(false);
        super.setLogScaleX(scale);
        axisPane.repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        String message = arg0.getActionCommand();
        //        System.out.println("DisplayPanel2D.actionPerformed: " + message);
        if (message == CoordJPanel.ZOOM_IN || message == CoordJPanel.RESET_ZOOM) {
            axisPane.repaint();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.Transparency.IZoomAddible#getLocalCoordBounds()
     */
    public CoordBounds getLocalCoordBounds() {
        return getLocalWorldCoords().MakeCopy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.Transparency.IZoomAddible#getGlobalCoordBounds()
     */
    public CoordBounds getGlobalCoordBounds() {
        return getGlobalWorldCoords().MakeCopy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.Transparency.ITruLogAxisAddible#getPositiveMin(int)
     */
    public float getPositiveMin(int axis) {
        int i;
        float min;

        if (axis == ITruLogAxisAddible.X_AXIS) {
            for (i = 0; i < xValues.length && xValues[i] <= 0; i++)
                ;
            min = xValues[i];
        } else {
            for (i = 0; i < yValues.length && yValues[i] <= 0; i++)
                ;
            min = yValues[i];
        }
        if (min <= 0)
            min = 1.0f;
        return min;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.Transparency.ITruLogAxisAddible#getBoundScaleFactor(int)
     */
    public float getBoundScaleFactor(int axis) {
        float sf;
        if (axis == ITruLogAxisAddible.X_AXIS)
            sf = 1.0f;
        else
            sf = getScaleFactor();
        return sf;
    }

    public static void main(String[] args) {
        int DOT = 1;
        int PLUS = 2;
        int STAR = 3;
        int BOX = 4;
        int CROSS = 5;
        int DOTTED = 6;
        int DASHED = 7;
        int LINE = 8;
        int DASHDOT = 9;
        int TRANSPARENT = 10;
        int ERROR_AT_POINT = 11;
        int ERROR_AT_TOP = 12;

        JFrame f = new JFrame("Test for ImageJPanel");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(0, 0, 500, 500);

        Container cp = f.getContentPane();

        float g1_x_vals[] = { 1f, (float) 2, (float) 4, 10 };
        float g1_y_vals[] = { 30f, (float) 40, (float) 60, 70f };
        float g1_e_vals[] = { 0.1f, 0.2f, 0.3f, 0.4f };

        float g2_x_vals[] = { 1, 2 };
        float g2_y_vals[] = { 2, 1 };

        float g3_x_vals[] = { 0, (float) .5, (float) .6, 1 };
        float g3_y_vals[] = { (float) .1, (float) .2, (float) .7, (float) .6 };

        float g4_x_vals[] = { 0, (float) .4, (float) .6, 1 };
        float g4_y_vals[] = { (float) .3, (float) .1, (float) .4, (float) .2 };

        DisplayPanel1D graph = new DisplayPanel1D(cp);
        graph.setGraphTitle("Test DsiplayPanel1D");
        graph.setXLabel("x label");
        graph.setXUnits("x units");
        graph.setYLabel("y labels");
        graph.setYUnits("y units");
        graph.setBackground(Color.white);
        graph.setColor(Color.black, 0, false);
        graph.setStroke(graph.TRANSPARENT, 0, false);
        graph.setLineWidth(1, 0, false);
        graph.setMarkColor(Color.green, 0, false);
        graph.addPlot(g1_x_vals, g1_y_vals, null);
        graph.setMarkType(BOX, 0, false);

        f.setVisible(true);
        graph.setLogScaleY(true);
        graph.setLogScaleX(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#autoYScale()
     */
    public void autoYScale() {
        autoY_bounds();
        minY = new Float(getYmin());
        maxY = new Float(getYmax());
        axisPane.repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#autoXScale()
     */
    public void autoXScale() {
        autoX_bounds();
        minX = new Float(getXmin());
        maxX = new Float(getXmax());
        axisPane.repaint();
    }

    /* (non-Javadoc)
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#is_autoXScale()
     */
    public boolean is_autoXScale() {
        return is_autoX_bounds();
    }

    /* (non-Javadoc)
     * @see gov.anl.ipns.ViewTools.Displays.IOneDPlot#is_autoYScale()
     */
    public boolean is_autoYScale() {
        return is_autoY_bounds();
    }
}
