/*
 * File: ViewerEnum.java
 *
 * Copyright (C) 2005, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2005/03/28 05:54:07  millermi
 *  - Initial Version - This is a building block in the new viewer
 *    structure.
 *
 */
 package gov.anl.ipns.ViewTools.Layouts;

/**
 * This class provides a single location for the list of all ViewComponents,
 * ComponentSwappers, and LayoutManagers. This class will help fill the void
 * of enumerated types in Java 1.4 for the viewer structure. Any time a new
 * view component, swapper, or layout manager is created, it should be
 * added to this list.
 */
 public class ViewerEnum
 {
  /* -------------------------List of ViewComponents----------------------- */
  /**
   * "Table" - This variable specifies the TableViewComponent as an
   * IViewComponent display type for any given ComponentSwapper.
   */
   public static final String TABLE = "Table";
  /**
   * "Graph" - This variable specifies the FunctionViewComponent as an
   * IViewComponent display type for any given ComponentSwapper.
   */
   public static final String GRAPH = "Graph";
  /**
   * "Image" - This variable specifies the ImageViewComponent as an
   * IViewComponent display type for any given ComponentSwapper.
   */
   public static final String IMAGE = "Image";
  /**
   * List of all available ViewComponents for any ComponentSwapper. If a
   * new ViewComponent is created, it should be added to this list.
   */
   public static final String[] COMPONENT_LIST = 
                                        new String[]{TABLE,GRAPH,IMAGE};
  
  /* -----------------------List of ComponentSwappers---------------------- */
  /**
   * "Swapper2D" - This variable specifies the ComponentSwapper2D, which
   * can handle data in the form of an IVirtualArray2D.
   */
   public static final String SWAPPER2D = "Swapper2D";
  /**
   * List of all available swappers for any LayoutManager. If a swapper is
   * created, it should be added to this list.
   */
   public static final String[] SWAPPER_LIST = new String[]{SWAPPER2D};
   
  /* -----------------------List of LayoutManagers------------------------- */
  /**
   * "Single" - String for specifying the layout type being displayed in the
   * ComponentViewManager. This variable will specify a SingleLayoutManager.
   */
   public static final String SINGLE = "Single";
  /**
   * List of all available views for any ComponentSwapper. If a view is added,
   * it should be added to this list.
   */
   public static final String[] LAYOUT_LIST = new String[]{SINGLE};
   
  /**
   * Use this method to see if the String for the ViewComponent is a valid
   * String registered in ViewerEnum.
   *
   *  @param  view_component The String constant for the ViewComponent.
   *  @return True if found, false if not. If null parameter is passed in,
   *          false is returned.
   */
   public static boolean isValidViewType( String view_component )
   {
     return isValid(COMPONENT_LIST,view_component);
   }
   
  /**
   * Use this method to see if the String for the ComponentSwapper is a valid
   * String registered in ViewerEnum.
   *
   *  @param  swapper The String constant for the ComponentSwapper.
   *  @return True if found, false if not. If null parameter is passed in,
   *          false is returned.
   */
   public static boolean isValidSwapper( String swapper )
   {
     return isValid(SWAPPER_LIST,swapper);
   }
   
  /**
   * Use this method to see if the String for the ComponentSwapper is a valid
   * String registered in ViewerEnum.
   *
   *  @param  swapper The String constant for the ComponentSwapper.
   *  @return True if found, false if not. If null parameter is passed in,
   *          false is returned.
   */
   public static boolean isValidLayout( String layout )
   {
     return isValid(LAYOUT_LIST,layout);
   }
   
  /*
   * This generic method factors out the mechanism for determining if a
   * value is valid or not.
   */
   private static boolean isValid( String[] list, String variable )
   {
     // If String is null, return false.
     if( variable == null )
       return false;
     // Search through list trying to find "variable".
     for( int i = 0; i < list.length; i++ )
     {
       if( variable.equals(list[i]) )
         return true;
     }
     // If not found in list, return false.
     return false;
   }
 }
