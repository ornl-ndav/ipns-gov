/**
 * @author Ferdi Franceschini The Bragg Institute Australian Nuclear Science and
 *         Technology Organisation ffr@ansto.gov.au Created on 22/08/2004
 */
package gov.anl.ipns.ViewTools.Displays;

import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.ViewTools.Components.Transparency.AxisOverlay2D;
import gov.anl.ipns.ViewTools.Components.Transparency.IZoomTextAddible;
import gov.anl.ipns.ViewTools.Components.ViewControls.IColorScaleAddible;
import gov.anl.ipns.ViewTools.Panels.Image.ImageJPanel;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordJPanel;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * DisplayPanel2D currently allows a 2D array to be plotted.
 * It consists of an ImageJPanel contained within an AxisOverlay2D object and is
 * suitable for embedding within SWT containers.
 */
public class DisplayPanel2D extends ImageJPanel implements IColorScaleAddible,
        IZoomTextAddible, ActionListener, ITwoDPlot {
    private String colorscale;

    private double logscale;

    private String xLabel = "xlabel", xUnits = "xunits", yLabel = "ylabel",
            yUnits = "yunits";

    private String zLabel = "zlabel", zUnits = "zunits";

    private String title = "Title";

    private int dP2DPrecision;

    private Rectangle regionInfo;

    private AxisOverlay2D axisPane;

    private JPanel plotPanel;

    private Container _container = null;

    private int xOffset = 60, yOffset = 50;

    /**
     * Create a 2D plot in the given container
     * @param container : generic container object
     * 
     */
    public DisplayPanel2D(Object container) {
        super();
        _container = (Container) container;
        int w = _container.getWidth() / 2, h = _container.getHeight() / 2;
        setBounds(xOffset, yOffset, w, h);
        regionInfo = new Rectangle(getBounds());
        dP2DPrecision = 4;

        axisPane = new AxisOverlay2D(this);
//        axisPane.setOpaque(true);
        axisPane.setLayout(null);
        axisPane.add(this);
        _container.add(axisPane);
        addActionListener(this);
    }

    public JPanel getPlot() {
        return axisPane;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.ViewControls.IColorScaleAddible#getColorScale()
     */
    public String getColorScale() {
        return colorscale;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.ViewControls.IColorScaleAddible#getValueAxisInfo()
     */
    public AxisInfo getValueAxisInfo() {
        return getAxisInformation(AxisInfo.Z_AXIS);
    }

    /*
     * (non-Javadoc) Required by IColorScaleAddible
     * 
     * @see gov.anl.ipns.ViewTools.Components.Transparency.ILogAxisAddible#getLogScale()
     */
    public double getLogScale() {
        return logscale;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.Transparency.IAxisAddible#getAxisInformation(int)
     */
    public AxisInfo getAxisInformation(int axiscode) {
        if (axiscode == AxisInfo.X_AXIS) {
            // return x info
            float x1 = getLocalWorldCoords().getX1();
            float x2 = getLocalWorldCoords().getX2();
            return new AxisInfo(x1, x2, xLabel, xUnits, AxisInfo.LINEAR);
        } else if (axiscode == AxisInfo.Y_AXIS) {
            // return y info
            float y1 = getLocalWorldCoords().getY1();
            float y2 = getLocalWorldCoords().getY2();
            return new AxisInfo(y1, y2, yLabel, yUnits, AxisInfo.LINEAR);
        } else {
            // return z info
            return new AxisInfo(getDataMin(), getDataMax(), zLabel, zUnits,
                    AxisInfo.LINEAR);
        }
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
        return dP2DPrecision;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Components.Transparency.IOverlayAddible#getRegionInfo()
     */
    public Rectangle getRegionInfo() {
        // Calculate new bounds from _container container's current bounds.
        int w = 2 * _container.getWidth() / 3, h = 2 * _container.getHeight() / 3;
        setBounds(xOffset, yOffset, w, h);
        regionInfo = new Rectangle(xOffset, yOffset, w, h);
        return regionInfo;
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
     * @see gov.anl.ipns.ViewTools.Displays.ITwoDPlot#clear()
     */
    public void clear() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.ITwoDPlot#setPoint(int, int, float)
     */
    public void setPoint(int row, int col, float z) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.ITwoDPlot#setPlotTitle(java.lang.String)
     */
    public void setPlotTitle(String t) {
        title = t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.ITwoDPlot#setYLabel(java.lang.String)
     */
    public void setYLabel(String t) {
        yLabel = t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.ITwoDPlot#setXLabel(java.lang.String)
     */
    public void setXLabel(String t) {
        xLabel = t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.ITwoDPlot#setScanNum(int)
     */
    public void setScanNum(int sn) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.ITwoDPlot#addRow(float[], int)
     */
    public void addRow(float[] row, int rows) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.ITwoDPlot#newImage(float[][])
     */
    public void newImage(float[][] image) {
        setData(image, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.anl.ipns.ViewTools.Displays.ITwoDPlot#setColors(java.lang.String,
     *      boolean, boolean)
     */
    public void setColors(String scale, boolean sides, boolean redraw) {

        /*
         * XXX ISAW scale is compared against colorscale by ColorChangedListener
         * which is called by sendMessage(scale)
         */
        colorscale = scale;
        setNamedColorModel(scale, sides, true);

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

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        //		TranslationJPanel tjp = (TranslationJPanel) arg0.getSource();
        // TODO Auto-generated method stub
        String message = arg0.getActionCommand();
        System.out.println("DisplayPanel2D.actionPerformed: " + message);

        if (message == CoordJPanel.ZOOM_IN || message == CoordJPanel.RESET_ZOOM) {
            axisPane.repaint();
        }

        //		if( message.equals(TranslationJPanel.BOUNDS_CHANGED) ||
        //		          message.equals(TranslationJPanel.BOUNDS_MOVED) ||
        //				  message.equals(TranslationJPanel.BOUNDS_RESIZED) )
        //			      {
        //			System.out.println("myPanView.actionPerformed: if
        // BOUNDS_CHANGED||_MOVED||RESIZED ");
        //				Rectangle box = ((BoxPanCursor)tjp.getBoxCursor()).region();
        //			        Point p1 = new Point( box.getLocation() );
        //			        Point p2 = new Point( p1 );
        //			        p2.x += (int)box.getWidth();
        //			        p2.y += (int)box.getHeight();

        //				setGlobalWorldCoords(new CoordBounds( p1.x, p1.y,p2.x, p2.y ));
        //				repaint(); // Without this, the newly drawn regions would
        // not appear.
        //			        sendMessage(message);
        //			      }
    }

    public static void main(String[] args) {
        // build my 2-D data
        int row = 200;
        int col = 180;

        JFrame f = new JFrame("Test for DisplayPanel2D");
        f.setBounds(0, 0, 500, 500);
        Container cp = f.getContentPane();
        cp.setLayout(new GridLayout(1, 2));

        DisplayPanel2D twoDPlot = new DisplayPanel2D(cp);
        float test_array[][] = new float[row][col];
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++)
                test_array[i][j] = i - j;

        twoDPlot.newImage(test_array);
        twoDPlot
                .initializeWorldCoords(new CoordBounds(-1.5f, 3.1f, 1.5f, -3.1f));
        //        cp.add(twoDPlot.getPlot());
        f.setVisible(true);
        f.repaint();
        //	    // Put 2-D data into a VirtualArray2D wrapper
        //	    IVirtualArray2D va2D = new VirtualArray2D( test_array );
        //	    // Give meaningful range, labels, units, and linear or log display
        // method.
        //	    va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, 10000f,
        //	    		        "TestX","TestUnits", true );
        //	    va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, 1500f,
        //	    			"TestY","TestYUnits", true );
        //	    va2D.setAxisInfo( AxisInfo.Z_AXIS, 0f, 1f, "Z", "Units", false );
        //	    va2D.setTitle("Display2D Test");
        //	    // Make instance of a Display2D frame, giving the array, the initial
        //	    // view type, and whether or not to add controls.
        //	    Display2D display = new
        // Display2D(va2D,Display2D.TABLE,Display2D.CTRL_ALL);
        //	    
        //	    // Class that "correctly" draws the display.
        //	    WindowShower shower = new WindowShower(f);
        //	    java.awt.EventQueue.invokeLater(shower);
        //	    shower = null;

    }

}
//class PanControl extends ViewControl implements ActionListener {
//    private Image image;
//
//    private DisplayPanel2D ijp;
//
//    private TranslationJPanel tjp;
//
//    private transient CoordBounds local_bounds;
//
//    private transient CoordBounds global_bounds;
//
//    private CoordBounds viewport;
//
//    // public myPanView() {
//    // super("");
//    // DisplayPanel2D dummy = new DisplayPanel2D();
//    // dummy.setData(new float[1][1], true);
//    // init(dummy);
//    // }
//    /**
//     * @param con_title
//     */
//    public PanControl(DisplayPanel2D cjp) {
//        super("");
//        // setBounds(0,0,10,10);
//        init(cjp);
//    }
//
//    public void init(DisplayPanel2D cjp) {
//        ijp = cjp;
//        ijp.addActionListener(this);
//
//        image = ijp.getThumbnail(100, 100);
//        tjp = new TranslationJPanel();
//        tjp.setViewPort((new CoordBounds(0, 0, 1, 1)).MakeCopy());
//        tjp.setGlobalPanelBounds((new CoordBounds(0, 0, 1, 1)).MakeCopy());
//        tjp.enableStretch(true);
//        tjp.addActionListener(this);
//        add(tjp);
//    }
//
//    public void setImage(DisplayPanel2D ijp) {
//        image = ijp.getThumbnail(100, 100);
//    }
//
//    public void paint(Graphics g) {
//        super.paint(g);
//        g.drawImage(image, 10, 10, this);
//    }
//
//    /**
//     * Set the local bounds of the thumbnail. These bounds represent the
//     * area viewable by the user and are shown graphically by the cursor.
//     *
//     * @param lb
//     * Bounds of the viewable region.
//     */
//    public void setLocalBounds(CoordBounds lb) {
//        tjp.setViewPort(lb.MakeCopy());
//        repaint();
//    }
//
//    /**
//     * Get the viewport or local bounds viewable to the user.
//     *
//     * @return viewport - viewable area
//     */
//    public CoordBounds getLocalBounds() {
//        return tjp.getLocalWorldCoords().MakeCopy();
//    }
//
//    /**
//     * Set the global bounds of the thumbnail. These bounds represent the
//     * entire possible area viewable by the user. If the global bounds are
//     * changed, the local bounds are reinitialized to the global bounds.
//     *
//     * @param gb
//     * Bounds of the viewable region.
//     */
//    public void setGlobalBounds(CoordBounds gb) {
//        tjp.setGlobalPanelBounds(gb.MakeCopy());
//        repaint();
//    }
//
//    /**
//     * Get the global bounds of the thumbnail. These bounds represent the
//     * entire possible area viewable by the user.
//     *
//     * @return Bounds of the viewable region.
//     */
//    public CoordBounds getGlobalBounds() {
//        return tjp.getGlobalWorldCoords();
//    }
//
//    /**
//     * This method requests window focus for the overlay. If focus is wanted
//     * by a private data member of an overlay, this method should be
//     * overloaded to have the data member itself call requestFocus().
//     */
//    public void getFocus() {
//        tjp.requestFocus();
//    }
//
//    /** Catch events from 2D plot */
//    public void actionPerformed(ActionEvent ae) {
//        // If the original data passed in was null, do nothing.
//        // if( null_data )
//        // return;
//        String message = ae.getActionCommand();
//        System.out.println("myPanView.actionPerformed " + message);
//
//        if (message == CoordJPanel.ZOOM_IN) {
//            // For plot image panel but sets bounds on pan view
//            System.out.println("myPanView.actionPerformed: if ZOOM_IN ");
//            ImageJPanel center = (ImageJPanel) ae.getSource();
//            setGlobalBounds(ijp.getGlobalWorldCoords().MakeCopy());
//            setLocalBounds(ijp.getLocalWorldCoords().MakeCopy());
//            // buildAspectImage();
//        } else if (message == CoordJPanel.RESET_ZOOM) {
//            ImageJPanel center = (ImageJPanel) ae.getSource();
//            local_bounds = center.getLocalWorldCoords().MakeCopy();
//            global_bounds = center.getGlobalWorldCoords().MakeCopy();
//            setGlobalBounds(global_bounds);
//            setLocalBounds(local_bounds);
//            // buildAspectImage();
//            // paintComponents( big_picture.getGraphics() );
//        } else if (message.equals(PanViewControl.BOUNDS_MOVED)
//                || message.equals(PanViewControl.BOUNDS_RESIZED)) {
//            // since the pan view control has a CoordJPanel in it with the
//            // same bounds, set its local bounds to the image local bounds.
//            ijp.setLocalWorldCoords(getLocalBounds());
//            ijp.getPlot().repaint();
//            ijp.changeLogScale(0.0, true);
//            // buildAspectImage();
//        }
//    }
//}

