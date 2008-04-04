/*
 * File:ICoordInfoSource.java 
 *  
 * Copyright (C) 2007 Andrew Moe
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
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * Modified:
 *
 * $Log$            
 */
package gov.anl.ipns.ViewTools.Panels.Transforms;

/**
 * This interface is used to specify how to retrieve an information string 
 * from implementing classes.
 */
public interface ICoordInfoSource 
{  
  public String getInfoString(int pix_x,int pix_y);
}


