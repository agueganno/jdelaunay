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
package org.jdelaunay.delaunay;

import java.util.ArrayList;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DPoint;

public class Test {

	public static ArrayList<DPoint> getPoints() throws DelaunayError {
		ArrayList<DPoint> points = new ArrayList<DPoint>();
		points.add(new DPoint(12, 10, 2));
		points.add(new DPoint(120, 10, 20));
		points.add(new DPoint(12, 100, 12));
		points.add(new DPoint(102, 100, 1));
		points.add(new DPoint(52, 100, 1));
		points.add(new DPoint(10, 50, 5));
		points.add(new DPoint(50, 50, 1));
		points.add(new DPoint(150, 50, 11));
		points.add(new DPoint(50, 150, 2));
		points.add(new DPoint(5, 50, 3));
		points.add(new DPoint(5, 5, 10));

		return points;
	}

}
