/*
 * jDelaunay is a library dedicated to the processing of Delaunay and constrained 
 * Delaunay triangulations from PSLG inputs.
 * 
 * This library is developed at French IRSTV institute as part of the AvuPur and Eval-PDU project, 
 * funded by the French Agence Nationale de la Recherche (ANR) under contract 
 * ANR-07-VULN-01 and ANR-08-VILL-0005-01 .
 * 
 * jDelaunay is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Adelin PIAU, Jean-Yves MARTIN
 * Copyright (C) 2011 Alexis GUEGANNO, Jean-Yves MARTIN
 * 
 * jDelaunay is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * jDelaunay is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * jDelaunay. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://trac.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.jdelaunay.delaunay.geometries;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jdelaunay.delaunay.error.DelaunayError;

/**
 * A parallepiped defined by two points.
 * 
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU, Alexis Guéganno.
 */

public class BoundaryBox implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5019273961990097490L;
	private double minx, maxx;
	private double miny, maxy;
	private double minz, maxz;
	private Coordinate middle;
	private boolean empty;

	/**
	 *  initialization method
	 */
	private void init() {
		minx = 0.0;
                maxx = 0.0;
                miny = 0.0;
                maxy = 0.0;
                minz = 0.0;
                maxz = 0.0;
		empty = true;
		middle=new Coordinate(0, 0, 0);
	}

	/**
	 * generate an empty box centered on 0,0,0
	 */
	public BoundaryBox() {
		init();
	}

	/**
	 * set a box according to coordinates
	 * @param pminx
         *      The first x value
	 * @param pmaxx
         *      The second x value
	 * @param pminy
         *      The first y value
	 * @param pmaxy
         *      The second y value
	 * @param pminz
         *      The first z value
	 * @param pmaxz
         *      The second z value
	 */
	public BoundaryBox(double pminx, double pmaxx, double pminy, double pmaxy,
			double pminz, double pmaxz) {
		init();

		setBox(pminx, pmaxx, pminy, pmaxy, pminz, pmaxz);
	}

	/**
	 * set a box according to another existing box.
	 * @param aBox
	 */
	public BoundaryBox(BoundaryBox aBox) {
		init();
                empty = aBox.empty;
		setBox(aBox.minx,aBox.maxx, aBox.miny, aBox.maxy, aBox.minz, aBox.maxz);
	}

	/**
	 * Set the coordinates of the extremities of this box.
	 * 
	 * @param x1
         *      The first x value
	 * @param x2
         *      The second x value
	 * @param y1
         *      The first y value
	 * @param y2
         *      The second y value
	 * @param z1
         *      The first z value
	 * @param z2
         *      The second z value
	 */
	public final void setBox(double x1, double x2, double y1, double y2,
			double z1, double z2) {
		minx = x1 < x2 ? x1 : x2;
		maxx = x1 < x2 ? x2 : x1;
		miny = y1 < y2 ? y1 : y2;
		maxy = y1 < y2 ? y2 : y1;
		minz = z1 < z2 ? z1 : z2;
		maxz = z1 < z2 ? z2 : z1;
		empty = false;
		updateMiddle();
	}
	
	
	/**
	 * Update middle coordinate. 
	 */
	private void updateMiddle(){
		double mx = (maxx-minx)/2;
		double my = (maxy-miny)/2;
		double mz = (maxz-minz)/2;
		middle=new Coordinate(minx+mx, miny+my, minz+mz);
	}

	/**
	 * alter box coordinates according to the new point
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public final void alterBox(double x, double y, double z) {
		if (empty) {
			minx = x;
			maxx = x;
			miny = y;
			maxy = y;
			minz = z;
			maxz = z;
			empty = false;
		} else {
			if (minx > x) {
				minx = x;
			}
			else if (maxx < x) {
				maxx = x;
			}
			if (miny > y) {
				miny = y;
			}
			else if (maxy < y) {
				maxy = y;
			}
			if (minz > z) {
				minz = z;
			}
			else if (maxz < z) {
				maxz = z;
			}
		}
		updateMiddle();
	}

	/**
	 * alter box coordinates according to the new point
	 * 
	 * @param aPoint
	 */
	public final void alterBox(Coordinate aPoint) {
		alterBox(aPoint.x,aPoint.y,aPoint.z);
	}

	/**
	 * alter box coordinates according to the new point
	 * 
	 * @param aPoint
	 */
	public final void alterBox(DPoint point) {
                Coordinate aPoint = point.getCoordinate();
		alterBox(aPoint);
	}
	
	
	/**
	 * @return Coordinate of the middle of the box.
	 */
	public final Coordinate getMiddle() {
		return middle;
	}
	
        @Override
	public final String toString()
	{
		return "min x["+minx+"] y["+miny+"] z["+minz+"] | max x["+maxx+"] y["+maxy+"] z["+maxz+"]";
	}

	/**
	 * Get the list of points that define this boundary box. We just need two of them,
         * as we can build a parallelepiped just from two opposite points of it.
	 * @return
         *      The min and max apex of the box, in a List of DPoint instances, if it is not
         *      empty. The List contains two DPoint instances, consequently.<br/>
         *      An empty list if the box is empty.
	 * @throws DelaunayError
	 */
	public final List<DPoint> getPoints() throws DelaunayError {
                if(empty){
                        return new ArrayList<DPoint>();
                } else {
                        ArrayList<DPoint> points = new ArrayList<DPoint>();
                        points.add(new DPoint(minx, miny, minz));
                        points.add(new DPoint(maxx, maxy, maxz));
                        return points;
                }
	}
        
}
