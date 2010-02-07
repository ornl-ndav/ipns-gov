/*
 * File:  PrinterNamePG.java
 *
 * Copyright (C) 2006, Dennis Mikkelson
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2006/06/30 17:00:14  dennis
 *  This extends ChoiceListPG and gets the list of available printers
 *  from the system.
 *
 *
 */

package gov.anl.ipns.Parameters;

import javax.print.*;
import javax.print.attribute.*;

/**
 * A PrinterNamePG extends ChoiceListPG to present the users with a list of
 * system printers to choose from.
 */
public class PrinterNamePG extends ChoiceListPG 
{

  /**
   * Creates a new PrinterNamePG with a list of the available printers.
   *
   * @param  name   The name (i.e. prompt string) for this PG.
   * @param  val    String or Vector of Strings giving the default printer
   *                name. 
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a String value.
   */
  public PrinterNamePG( String name, Object val ) 
                                        throws IllegalArgumentException
  {
    super( name, val );  
   
    PrintService[] services = null;
    try
    {
      DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

      HashPrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();

      services = PrintServiceLookup.lookupPrintServices(flavor, aset);

      for (int i = 0; i < services.length; i++)
        addItem(services[i].getName());
    }
    catch ( Throwable th )
    {
      System.out.println("PrintServiceLookup failed in PrinterNamePG");
    }

                                        // This happens if no printers present 
                                        // or there is a problem getting them 
    if ( services == null || services.length == 0 )     
    {
      addItem("NO Printer 1");
      addItem("NO Printer 2");
      addItem("NO Printer 3");
    }
  }


  /**
   *  This overides the super classes clear() method with a method that 
   *  DOES NOTHING.  This prevents clear() from destroying the list of
   *  available printers.
   */
  public void clear()
  {
    // overide method that clears the vector, since we would lose the list
    // of printers
  }

}
