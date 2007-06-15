package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.*;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;

/**
 * This class serves as a base class for all RegionOpEditFrames
 * @author Josh Oakgrve
 *
 */
public interface RegionOpEditFrame
{
  public static String DRAW_CURSOR = "Draw Cursor";
  public static String CANCEL = "Cancel";
  public static String DONE = "Done";
  public static String DRAW_REGION = "Draw Region";
  public floatPoint2D[] getDefiningPoints();
  public int getRegionIndex();
  public CursorTag getTypeCursor();
  public RegionOp.Operation getOp();
  public void dispose();
}