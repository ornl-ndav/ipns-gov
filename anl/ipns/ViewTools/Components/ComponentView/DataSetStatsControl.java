/*
 * File:  DataSetStatsControl.java
 *
 * Copyright (C) 2005, Dominic Kramer
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
 * Primary   Dominic Kramer <kramerd@uwstout.edu>
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
 * $ Log: DataSetStatsControl.java,v $
 */

package gov.anl.ipns.ViewTools.Components.ComponentView;

import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import DataSetTools.components.ui.DataSetXConversionsTable;
import DataSetTools.dataset.DataSet;

/**
 * This control is used to display the meta-data associated with a 
 * <code>DataSet</code>.
 */
public class DataSetStatsControl extends ViewControl
{
   /**
    * The key used to reference a <code>DataSet</code> in this class's 
    * <code>ObjectState</code>.
    */
   public static final String DATA_SET_KEY = "DataSet Key";
   
   /**
    * The <code>DataSet</code> stored in this class's default 
    * <code>ObjectState</code>.
    */
   public static final DataSet DEFAULT_DATA_SET = DataSet.EMPTY_DATA_SET;
   
   /** The table that displays the actual meta-data. */
   private DataSetXConversionsTable table;
   
   /** The <code>DataSet</code> whose meta-data is being displayed. */
   private DataSet dataSet;
   
   /**
    * Constructs a control with the given title and using the 
    * <code>DataSet</code>, <code>DEFAULT_DATA_SET</code>.
    * 
    * @param title This control's title.
    */
   public DataSetStatsControl(String title)
   {
      this(DEFAULT_DATA_SET, title);
   }
   
   /**
    * Constructs a control with the given <code>DataSet</code> and title.
    * 
    * @param dataSet The <code>DataSet</code> whose meta-data is being 
    *                displayed.
    * @param title   The control's title.
    */
   public DataSetStatsControl(DataSet dataSet, String title)
   {
      super(title);
      setControlValue(dataSet);
   }
   
   /**
    * Encapsulates this control's state in an <code>ObjectState</code>.
    * 
    * @param isDefault <code>true</code> if this control's default state 
    *                  should be encapsulated and <code>false</code> if 
    *                  the current state should be.
    */
   @Override
   public ObjectState getObjectState(boolean isDefault)
   {
      ObjectState state = super.getObjectState(isDefault);
      if (isDefault)
         state.insert(DATA_SET_KEY, DEFAULT_DATA_SET);
      else
         state.insert(DATA_SET_KEY, this.dataSet);
      return state;
   }
   
   /**
    * Used to set this control's state.
    * 
    * @param new_state This control's new state encapsulated in an 
    *                  <code>ObjectState</code>.
    */
   @Override
   public void setObjectState(ObjectState new_state)
   {
      if (new_state == null)
         return;
      
      super.setObjectState(new_state);
      
      Object val = new_state.get(DATA_SET_KEY);
      if ( (val != null) && (val instanceof DataSet) )
         this.dataSet = (DataSet)val;
   }

   /**
    * Sets this control's value.  For this control, this value is 
    * a <code>DataSet</code>.
    * 
    * @param The new <code>DataSet</code> whose meta-data is being 
    *        viewed.  This object must be of type <code>DataSet</code>.
    */
   @Override
   public void setControlValue(Object value)
   {
      if ( (value == null) || !(value instanceof DataSet))
         return;
      
      this.dataSet = (DataSet)value;
      if (this.dataSet == null)
         this.dataSet = DEFAULT_DATA_SET;
      
      removeAll();
      this.table = new DataSetXConversionsTable(this.dataSet);
      add(this.table.getTable());
      validate();
   }

   /**
    * Used to get this class's value.  For this control this value is 
    * the <code>DataSet</code> whose meta-data is being viewed.
    * 
    * @return The <code>DataSet</code> whose meta-data is being viewed.
    */
   @Override
   public Object getControlValue()
   {
      return this.dataSet;
   }

   /**
    * Constructs a deep copy of this control.
    * 
    * @return A deep copy of this control.
    */
   @Override
   public ViewControl copy()
   {
      DataSet newDs = new DataSet();
      newDs.copy(this.dataSet);
      
      return new DataSetStatsControl(newDs, getTitle());
   }
   
   /**
    * Updates the control to view the meta-data for the 
    * <code>DataSet</code> at the given location.
    * 
    * @param x The x value at which the data is viewed.
    * @param y The y value to display.
    * @param index The index of the <code>Data</code> block in the 
    *              <code>DataSet</code> to view.
    */
   public void displayDataAt(float x, float y, int index)
   {
      this.table.showConversions(x, y, index);
   }
   
   /**
    * Updates the control to view the meta-data for the 
    * <code>DataSet</code> at the given location.
    * 
    * @param x The x value at which the data is viewed.
    * @param index The index of the <code>Data</code> block in the 
    *              <code>DataSet</code> to view.
    */
   public void displayDataAt(float x, int index)
   {
      this.table.showConversions(x, index);
   }
}
