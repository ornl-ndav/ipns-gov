/*
 * File:  ClearDocListener.java 
 *             
 * Copyright (C) 2001, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.4  2004/03/11 22:46:18  millermi
 * - Changed package.
 *
 * Revision 1.3  2002/11/27 23:12:10  pfpeterson
 * standardized header
 *
 * Revision 1.2  2002/08/19 17:06:56  pfpeterson
 * Reformated file to make it easier to read.
 *
 * Revision 1.1  2001/12/21 17:48:00  dennis
 * An ActionListener that Clears the contents of the
 * associated document
 *
*/
package gov.anl.ipns.Util.Sys;
import java.awt.event.*;
import javax.swing.text.*;


/** Clears the contents of a document when triggered
*/
public class ClearDocListener implements ActionListener{
    Document Doc;
    /**
     * Sets the Document that will be cleared when triggered by and
     * ActionEvent
     *
     * @param  Doc  sets up the Document that is affected by the clear
     */
    public ClearDocListener( Document Doc){
        this.Doc = Doc;
    }
    
    /**
     * The event that triggers a Clear operation invokes this method
     * which erases any text from the document
     */
    public void actionPerformed( ActionEvent evt){
        try{
            Doc.remove( Doc.getStartPosition().getOffset(), Doc.getLength());
        }catch( Exception s){
            // let it drop on the floor
        }
    }
}
