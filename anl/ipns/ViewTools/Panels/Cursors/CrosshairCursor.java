package DataSetTools.components.image;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Rectangle;

/** 
 * A Rubberband that draws a crosshair cursor.
 *
 * @version 1.00, 7/29/98
 * @author  Dennis Mikkelson
 * @see     Rubberband
 */
public class CrosshairCursor extends Rubberband {

    public CrosshairCursor(JPanel component) {
        super(component);
    }

    public void drawLast(Graphics graphics) {
        Rectangle rect = component.getVisibleRect();
        int min_x = rect.x;
        int max_x = rect.x + rect.width;
        int min_y = rect.y;
        int max_y = rect.y + rect.height;
                                                    // only draw in region 
        if ( last.x >= min_x && last.x <= max_x )
          graphics.drawLine(last.x, min_y, last.x, max_y);

        if ( last.y >= min_y && last.y <= max_y )
          graphics.drawLine( min_x, last.y, max_x, last.y);
    }

    public void drawNext(Graphics graphics) {
        Rectangle rect = component.getVisibleRect();
        int min_x = rect.x;
        int max_x = rect.x + rect.width;
        int min_y = rect.y;
        int max_y = rect.y + rect.height;
                                                    // only draw in region
        if ( stretched.x >= min_x && stretched.x <= max_x )
          graphics.drawLine( stretched.x, min_y, stretched.x, max_y );

        if ( stretched.y >= min_y && stretched.y <= max_y )
          graphics.drawLine( min_x, stretched.y, max_x, stretched.y );
    }

}
