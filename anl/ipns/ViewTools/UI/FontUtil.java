/**
 *  File: FontUtil.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.8  2004/03/11 23:01:00  serumb
 *  Changed package.
 *
 *  Revision 1.7  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 *  Revision 1.6  2002/10/31 23:12:35  dennis
 *  Added definition of smaller mono-spaced font, MONO_FONT0
 *
 *  Revision 1.5  2002/07/09 16:14:36  dennis
 *  Added all Greek letters.
 *
 *  Revision 1.4  2002/07/08 20:08:34  dennis
 *  Added string constants for some commonly used special symbols, Angstrom,
 *  inverse Angstrom, delta, theta, phi, lamda and pi.
 *
 */ 

package gov.anl.ipns.ViewTools.UI;

import java.awt.*;
import javax.swing.*;

/**
 *  Provide common set of fonts to use for borders, labels, special symbols,
 *  etc. to keep the fonts uniform across different parts of an application
 */

public class FontUtil
{
  // Special symbols:
  public static final String ANGSTROM     = "\u00c5";
  public static final String INV_ANGSTROM = "Inv("+ANGSTROM+")";

//  five digit unicode not working, 7/9/2002, D.M.
//  public static final String SCRIPT_L     = "\u02113"; 
                                                  
  // lower case Greek
  public static final String ALPHA        = "\u03b1"; 
  public static final String BETA         = "\u03b2";
  public static final String GAMMA        = "\u03b3";
  public static final String DELTA        = "\u03b4";
  public static final String EPSILON      = "\u03b5";
  public static final String EPSILON2     = "\u2208";
  public static final String ZETA         = "\u03b6";
  public static final String ETA          = "\u03b7";
  public static final String THETA        = "\u03b8";
  public static final String IOTA         = "\u03b9";
  public static final String KAPPA        = "\u03ba";
  public static final String LAMBDA       = "\u03bb";
  public static final String MU           = "\u03bc";
  public static final String NU           = "\u03bd";
  public static final String XI           = "\u03be";
  public static final String OMICRON      = "\u03bf";
  public static final String PI           = "\u03c0";
  public static final String RHO          = "\u03c1";
  public static final String SIGMA2       = "\u03c2";
  public static final String SIGMA        = "\u03c3";
  public static final String TAU          = "\u03c4";
  public static final String UPSILON      = "\u03c5";
  public static final String PHI          = "\u03c6";
  public static final String PHI2         = "\u03d5";
  public static final String CHI          = "\u03c7";
  public static final String PSI          = "\u03c8";
  public static final String OMEGA        = "\u03c9";
  public static final String OMEGA2       = "\u03d6";

  // upper case Greek
  public static final String ALPHA_UC     = "\u0391"; 
  public static final String BETA_UC      = "\u0392";
  public static final String GAMMA_UC     = "\u0393";
  public static final String DELTA_UC     = "\u0394";
  public static final String EPSILON_UC   = "\u0395";
  public static final String ZETA_UC      = "\u0396";
  public static final String ETA_UC       = "\u0397";
  public static final String THETA_UC     = "\u0398";
  public static final String IOTA_UC      = "\u0399";
  public static final String KAPPA_UC     = "\u039a";
  public static final String LAMBDA_UC    = "\u039b";
  public static final String MU_UC        = "\u039c";
  public static final String NU_UC        = "\u039d";
  public static final String XI_UC        = "\u039e";
  public static final String OMICRON_UC   = "\u039f";
  public static final String PI_UC        = "\u03a0";
  public static final String RHO_UC       = "\u03a1";
  public static final String SIGMA_UC     = "\u03a3";
  public static final String TAU_UC       = "\u03a4";
  public static final String UPSILON_UC   = "\u03a5";
  public static final String PHI_UC       = "\u03a6";
  public static final String CHI_UC       = "\u03a7";
  public static final String PSI_UC       = "\u03a8";
  public static final String OMEGA_UC     = "\u03a9";
    

  // default fonts for borders, labels, etc.:

  static public final Font LABEL_FONT   = new Font("SansSerif", Font.PLAIN, 10);
  static public final Font LABEL_FONT2  = new Font("SansSerif", Font.PLAIN, 12);
  static public final Font BORDER_FONT  = new Font("SansSerif", Font.BOLD,   9);
  static public final Font BORDER_FONT2 = new Font("SansSerif", Font.BOLD,  11);
  static public final Font MONO_FONT0   = new Font("Monospaced", Font.PLAIN,10);
  static public final Font MONO_FONT    = new Font("Monospaced", Font.PLAIN,12);
  static public final Font MONO_FONT2   = new Font("Monospaced", Font.PLAIN,14);

  /**
   * Don't instantiate this class, just use the Fonts provided.
   */
  private FontUtil() {}

  public static void main( String args[] )
  {
    JFrame   f       = new JFrame( "Font Test" );
    JTextArea display = new JTextArea();
    display.setFont( LABEL_FONT2 );
    
    display.append ("Special Characters"+"\n");

    display.append( " " + ANGSTROM + " " + INV_ANGSTROM + "\n");
//  display.append( " " + SCRIPT_L                      + "\n");

    display.append( " " + ALPHA   + " " + ALPHA_UC   + "\n");
    display.append( " " + BETA    + " " + BETA_UC    + "\n");
    display.append( " " + GAMMA   + " " + GAMMA_UC   + "\n");
    display.append( " " + DELTA   + " " + DELTA_UC   + "\n");
    display.append( " " + EPSILON + " " + EPSILON_UC + "\n");
    display.append( " " + EPSILON2                   + "\n");
    display.append( " " + ZETA    + " " + ZETA_UC    + "\n");
    display.append( " " + ETA     + " " + ETA_UC     + "\n");
    display.append( " " + THETA   + " " + THETA_UC   + "\n");
    display.append( " " + IOTA    + " " + IOTA_UC    + "\n");
    display.append( " " + KAPPA   + " " + KAPPA_UC   + "\n");
    display.append( " " + LAMBDA  + " " + LAMBDA_UC  + "\n");
    display.append( " " + MU      + " " + MU_UC      + "\n");
    display.append( " " + NU      + " " + NU_UC      + "\n");
    display.append( " " + XI      + " " + XI_UC      + "\n");
    display.append( " " + OMICRON + " " + OMICRON_UC + "\n");
    display.append( " " + PI      + " " + PI_UC      + "\n");
    display.append( " " + RHO     + " " + RHO_UC     + "\n");
    display.append( " " + SIGMA   + " " + SIGMA_UC   + "\n");
    display.append( " " + SIGMA2                     + "\n");
    display.append( " " + TAU     + " " + TAU_UC     + "\n");
    display.append( " " + UPSILON + " " + UPSILON_UC + "\n");
    display.append( " " + PHI     + " " + PHI_UC     + "\n");
    display.append( " " + PHI2                       + "\n");
    display.append( " " + CHI     + " " + CHI_UC     + "\n");
    display.append( " " + PSI     + " " + PSI_UC     + "\n");
    display.append( " " + OMEGA   + " " + OMEGA_UC   + "\n");
    display.append( " " + OMEGA2                     + "\n");

    f.getContentPane().add( display );
    f.getContentPane().setLayout( new GridLayout(1,1) );
    f.setSize( 200, 500 );
    f.show();
  }

}
