package org.jhydrocell.hydronetwork;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.DEdge;
import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.DPoint;
import org.jdelaunay.delaunay.DTriangle;
import org.jhydrocell.utilities.HydroLineUtil;
import org.jhydrocell.utilities.MathUtil;

import com.vividsolutions.jts.geom.Coordinate;
import org.jhydrocell.utilities.HydroTriangleUtil;

/**
 * The representation of a hydrologic network, based on a constrained triangulation.
 * @author alexis
 */
public final class HydroNetwork {

        private ConstrainedMesh theMesh;
        private List<DPoint> listEntry;
        private List<DPoint> listExit;
        private List<DPoint> listIntermediate;
        private List<DEdge> listEdges;
        private List<DPoint> listPrepare;
        private int listDefinition;
        private boolean connectToSurface;

        /**
         * Global initialization
         */
        private void init() {
                theMesh = null;

                listEntry = new LinkedList<DPoint>();
                listExit = new LinkedList<DPoint>();
                listIntermediate = new LinkedList<DPoint>();
                listEdges = new LinkedList<DEdge>();
                listPrepare = new LinkedList<DPoint>();
                listDefinition = 0;
                connectToSurface = true;
        }

        /**
         * Constructor
         */
        public HydroNetwork() {
                init();
        }

        /**
         * Constructor
         */
        public HydroNetwork(ConstrainedMesh aMesh) {
                init();
                theMesh = aMesh;
        }

        /**
         * Morphological qualification
         *
         * @throws DelaunayError
         */
        public void morphologicalQualification() throws DelaunayError {
                if (theMesh == null) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_NO_MESH);
                } else if (!theMesh.isMeshComputed()) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_NOT_GENERATED);
                } else {

                        // Edges : topographic qualifications
                        for (DEdge edge : theMesh.getEdges()) {
                                edge.forceTopographicOrientation();
                                HydroLineUtil hydroLineUtil = new HydroLineUtil(edge);
                                DTriangle aTriangleLeft = edge.getLeft();
                                DTriangle aTriangleRight = edge.getRight();

                                boolean rightTtoEdge = false;
                                boolean rightTColinear = false;
                                boolean righTFlat = false;
                                boolean leftTtoEdge = false;
                                boolean leftTColinear = false;
                                boolean leftTFlat = false;
                                boolean rightBorder = false;
                                boolean leftBorder = false;

                                // Qualification des triangles
                                if (aTriangleRight != null) {
                                        boolean pointeVersEdge = HydroTriangleUtil.getPenteVersEdge(edge, aTriangleRight);
                                        if (pointeVersEdge) {
                                                rightTtoEdge = true;
                                        } else if (HydroTriangleUtil.getSlope(aTriangleRight) > 0) {
                                                if (MathUtil.isColinear(hydroLineUtil.get3DVector(),
                                                        HydroTriangleUtil.get3DVector(aTriangleRight))) {
                                                        rightTColinear = true;
                                                }
                                        } else if (HydroTriangleUtil.getSlope(aTriangleRight) == 0) {
                                                righTFlat = true;
                                        }
                                } else {
                                        rightBorder = true;
                                }

                                if (aTriangleLeft != null) {

                                        boolean pointeVersEdge = HydroTriangleUtil.getPenteVersEdge(edge, aTriangleLeft);

                                        if (pointeVersEdge) {
                                                leftTtoEdge = true;
                                        } else if (HydroTriangleUtil.getSlope(aTriangleLeft) > 0) {
                                                if (MathUtil.isColinear(hydroLineUtil.get3DVector(),
                                                        HydroTriangleUtil.get3DVector(aTriangleLeft))) {
                                                        leftTColinear = true;
                                                }
                                        } else if (HydroTriangleUtil.getSlope(aTriangleLeft) == 0) {
                                                leftTFlat = true;
                                        }

                                } else {
                                        leftBorder = true;
                                }

                                // Recupération des noeuds associés à l'edge

                                // Qualification de la pente de l'edge parcouru
                                if (!leftBorder && !rightBorder) {
                                        // Traitement des ridges
                                        if ((!rightTtoEdge && !leftTtoEdge)
                                                && (!righTFlat && !leftTFlat)) {
                                                edge.addProperty(HydroProperties.RIDGE);
                                        } // Cas des talwegs
                                        else if (rightTtoEdge && leftTtoEdge) {
                                                edge.addProperty(HydroProperties.TALWEG);
                                                edge.getStart().addProperty(HydroProperties.TALWEG);
                                                edge.getEnd().addProperty(HydroProperties.TALWEG);

                                        } // Le triangle de gauche pointe sur l'edge mais pas le
                                        // triangle de droite
                                        else if ((leftTtoEdge && !rightTtoEdge) && !righTFlat) {
                                                edge.addProperty(HydroProperties.RIGHTSLOPE);
                                        } // Le triangle de droite pointe sur l'edge mais pas le
                                        // triangle de gauche
                                        else if ((rightTtoEdge && !leftTtoEdge) && (!leftTFlat)) {
                                                edge.addProperty(HydroProperties.LEFTTSLOPE);
                                        } // Traitement du rebord droit
                                        else if ((!rightTtoEdge && !leftTtoEdge)
                                                && (!leftTFlat && righTFlat)) {
                                                edge.addProperty(HydroProperties.LEFTSIDE);
                                        } // Traitement du rebord gauche
                                        else if ((!leftTtoEdge && !rightTtoEdge)
                                                && (!righTFlat && leftTFlat)) {
                                                edge.addProperty(HydroProperties.RIGHTSIDE);
                                        } // Traitement du fond gauche
                                        else if ((rightTtoEdge && !leftTtoEdge)
                                                && (leftTFlat && !righTFlat)) {
                                                edge.addProperty(HydroProperties.LEFTWELL);
                                        } // Traitement du fond droit
                                        else if ((!rightTtoEdge && leftTtoEdge)
                                                && (!leftTFlat && righTFlat)) {
                                                edge.addProperty(HydroProperties.RIGHTWELL);
                                        } // Cas particulier des talwegs colineaires
                                        // Talweg colineaire gauche
                                        else if ((!leftTtoEdge && rightTtoEdge) && leftTColinear) {
                                                edge.addProperty(HydroProperties.LEFTCOLINEAR);
                                                edge.getStart().addProperty(HydroProperties.TALWEG);
                                                edge.getEnd().addProperty(HydroProperties.TALWEG);

                                        } // Talweg colineaire droit
                                        else if ((leftTtoEdge && !rightTtoEdge) && rightTColinear) {
                                                edge.addProperty(HydroProperties.RIGHTCOLINEAR);
                                                edge.getStart().addProperty(HydroProperties.TALWEG);
                                                edge.getEnd().addProperty(HydroProperties.TALWEG);

                                        } // Les deux triangles sont colineaires
                                        else if ((!leftTtoEdge && !rightTtoEdge)
                                                && (rightTColinear && leftTColinear)) {
                                                edge.addProperty(HydroProperties.DOUBLECOLINEAR);

                                                edge.getStart().addProperty(HydroProperties.TALWEG);
                                                edge.getEnd().addProperty(HydroProperties.TALWEG);

                                        } // Le reste est plat
                                        else {
                                                edge.addProperty(HydroProperties.FLAT);
                                        }
                                } // Traitement des bords plats
                                else {
                                        edge.addProperty(HydroProperties.BORDER);
                                }
                        }
                }
        }

        /**
         * post process the edges according to their type
         */
        private void postProcessEdges() {
                List<DEdge> addedEdges = new LinkedList<DEdge>();
                List<DEdge> theEdges = theMesh.getEdges();
                for (DEdge anEdge : theEdges) {
                        if (anEdge.hasProperty(HydroProperties.getWallWeight())) {
                                // Process wall : duplicate edge and changes connections
                                if ((anEdge.getLeft() != null) && (anEdge.getRight() != null)) {
                                        // Something to do if and only if there are two triangles
                                        // connected
                                        DEdge newEdge = new DEdge(anEdge);

                                        // Changes left triangle connection
                                        DTriangle aTriangle = anEdge.getLeft();
                                        for (int i = 0; i < 3; i++) {
                                                if (aTriangle.getEdge(i) == anEdge) {
                                                        aTriangle.setEdge(i, newEdge);
                                                }
                                        }

                                        // Changes edges connections
                                        newEdge.setRight(null);
                                        anEdge.setLeft(null);

                                        // add the new edge
                                        addedEdges.add(newEdge);
                                }
                        }
                }

                // add edges to the structure
                for (DEdge anEdge : addedEdges) {
                        theEdges.add(anEdge);
                }
        }

        // ----------------------------------------------------------------
        /**
         * Defines a new branch type
         *
         * @param branchType
         * @param connectToSurface
         * @throws DelaunayError
         */
        private void branchStart(int branchType, boolean connectToSurface)
                throws DelaunayError {
                this.listEntry = new LinkedList<DPoint>();
                this.listExit = new LinkedList<DPoint>();
                this.listIntermediate = new LinkedList<DPoint>();
                this.listEdges = new LinkedList<DEdge>();
                this.listDefinition = branchType;
                this.connectToSurface = connectToSurface;
        }

        /**
         * Defines a new branch type on the surface
         *
         * @param branchType
         * @throws DelaunayError
         */
        private void branchStart(int branchType) throws DelaunayError {
                branchStart(branchType, true);
        }

        /**
         * defines a new branch
         *
         * @param theList
         * @throws DelaunayError
         */
        private void setNewBranch(List theList) throws DelaunayError {
                DPoint lastPoint = null;
                int count = theList.size();
                DPoint aPoint = null;
                Coordinate aCoordinate = null;
                ListIterator iterList = theList.listIterator();
                while (iterList.hasNext()) {
                        Object item = iterList.next();
                        if (item instanceof DPoint) {
                                aPoint = (DPoint) item;
                        } else if (item instanceof Coordinate) {
                                aCoordinate = (Coordinate) item;
                                aPoint = new DPoint(aCoordinate.x, aCoordinate.y,
                                        aCoordinate.z);
                        } else {
                                aPoint = null;
                        }

                        count--;
                        if (aPoint != null) {
                                if (lastPoint == null) {
                                        // First point of the list
                                        if (!listIntermediate.contains(aPoint)) {
                                                // Already an intermediate point => do nothing
                                                if (listExit.contains(aPoint)) {
                                                        // It is an exit
                                                        // It is also an entry
                                                        // => becomes an intermediate
                                                        listExit.remove(aPoint);
                                                        listIntermediate.add(aPoint);
                                                } else if (!listEntry.contains(aPoint)) {
                                                        // New entry
                                                        listEntry.add(aPoint);
                                                }
                                        }
                                        // else it is in Entry
                                } else {
                                        // Intermediate point
                                        if (!listIntermediate.contains(aPoint)) {
                                                // Already an intermediate point => do nothing
                                                if (listExit.contains(aPoint)) {
                                                        // It is an exit
                                                        if (count > 0) {
                                                                // and not the last point
                                                                // => becomes an intermediate
                                                                listExit.remove(aPoint);
                                                                listIntermediate.add(aPoint);
                                                        }
                                                } else if (listEntry.contains(aPoint)) {
                                                        // It is an entry
                                                        // => becomes an intermediate
                                                        listEntry.remove(aPoint);
                                                        listIntermediate.add(aPoint);
                                                } else if (count > 0) {
                                                        // new point => add it to Intermediate
                                                        listIntermediate.add(aPoint);
                                                } else {
                                                        // new point and Last point => Exit
                                                        listExit.add(aPoint);
                                                }
                                        }
                                        // Link lastPoint to new point
                                        DEdge anEdge = new DEdge(lastPoint, aPoint);
                                        anEdge.addProperty(listDefinition);
                                        listEdges.add(anEdge);
                                }
                                // other informations
                                aPoint.addProperty(listDefinition);

                                lastPoint = aPoint;
                        }
                }
        }

        /**
         * Validate branch and end that branch type
         *
         * @throws DelaunayError
         */
        private void branchValidate() throws DelaunayError {
//		DTriangle referenceTriangle = null;
//		List<DEdge> edges = theMesh.getEdges();
//		List<DPoint> points = theMesh.getPoints();
//
//		// add every entry point to the mesh
//		for (DPoint aPoint : listEntry) {
//			if (points.contains(aPoint)) {
//				// Already in the points list => do noting
//			} else {
//				aPoint.setMarked(0, true);//TODO check me
//				referenceTriangle = theMesh.getTriangle(aPoint);
//				if (referenceTriangle != null) {
//					// Connect it to the surface
//					double zValue = referenceTriangle.softInterpolateZ(aPoint);
//					aPoint.setZ(zValue);
//
//					theMesh.addPoint(referenceTriangle, aPoint);
//				} else {
//					theMesh.addPoint(aPoint);
//				}
//			}
//		}
//
//		// add every intermediate point to the point list
//		// do not include them in the mesh
//		for (DPoint aPoint : listIntermediate) {
//			if (points.contains(aPoint)) {
//				// Already in the points list => do noting
//			} else {
//				points.add(aPoint);
//				aPoint.setMarked(0, true);//TODO check me
//				referenceTriangle = theMesh.getTriangle(aPoint);
//				if (referenceTriangle != null) {
//					double zValue = referenceTriangle.softInterpolateZ(aPoint);
//					if (connectToSurface) {
//						// Connect it to the surface
//						aPoint.setZ(zValue);
//
//						theMesh.addPoint(referenceTriangle, aPoint);
//					} else {
//						if (aPoint.getZ() > zValue) {
//							aPoint.setZ(zValue - 1.0);
//						}
//					}
//				} else if (connectToSurface) {
//					theMesh.addPoint(aPoint);
//				}
//			}
//		}
//
//		// add every exit point to the mesh
//		for (DPoint aPoint : listExit) {
//			if (points.contains(aPoint)) {
//				// Already in the points list => do noting
//			} else {
//				aPoint.setMarked(0, true);//TODO check me
//				referenceTriangle = theMesh.getTriangle(aPoint);
//				if (referenceTriangle != null) {
//					// Connect it to the surface
//					double zValue = referenceTriangle.softInterpolateZ(aPoint);
//					aPoint.setZ(zValue);
//
//					theMesh.addPoint(referenceTriangle, aPoint);
//				} else {
//					theMesh.addPoint(aPoint);
//				}
//			}
//		}
//
//		// add edges
//		for (DEdge anEdge : listEdges) {
//			anEdge.setMarked(0,true); //FIXME check if it's good ( old version : anEdge.setMarked(true); )
//			if (connectToSurface) {
//				theMesh.addEdge(anEdge);
//			}
//			else {
//				anEdge.setOutsideMesh(true);
//				edges.add(anEdge);
//			}
//		}
//
//		// Reset informations
//		listEntry = new LinkedList<DPoint>();
//		listExit = new LinkedList<DPoint>();
//		listIntermediate = new LinkedList<DPoint>();
//		listEdges = new LinkedList<DEdge>();
//		listDefinition = 0;
//		connectToSurface = true;
        }

        // ----------------------------------------------------------------
        /**
         * add a sewer entry
         *
         * @param x
         * @param y
         * @param z
         * @throws DelaunayError
         */
        public void addSewerEntry(double x, double y, double z)
                throws DelaunayError {
                // Search for the point
                DPoint sewerPoint = theMesh.getPoint(x, y, z);
                addSewerEntry(sewerPoint);
        }

        /**
         * add a sewer entry
         *
         * @param sewerPoint
         * @throws DelaunayError
         */
        public void addSewerEntry(DPoint sewerPoint) throws DelaunayError {
                listPrepare = new LinkedList<DPoint>();
                listPrepare.add(sewerPoint);
        }

        /**
         * add a sewer exit
         *
         * @param x
         * @param y
         * @param z
         * @throws DelaunayError
         */
        public void addSewerExit(double x, double y, double z) throws DelaunayError {
                // Search for the point
                DPoint sewerPoint = theMesh.getPoint(x, y, z);
                addSewerExit(sewerPoint);
        }

        /**
         * add a sewer exit
         *
         * @param sewerPoint
         * @throws DelaunayError
         */
        public void addSewerExit(DPoint sewerPoint) throws DelaunayError {
                listPrepare.add(sewerPoint);
                sewerSet(listPrepare);
                listPrepare = new LinkedList<DPoint>();
        }

        /**
         * add a sewer point (neither start or exit
         *
         * @param x
         * @param y
         * @param z
         * @throws DelaunayError
         */
        public void addSewerPoint(double x, double y, double z)
                throws DelaunayError {
                // Search for the point
                DPoint aPoint = theMesh.getPoint(x, y, z);
                addSewerPoint(aPoint);
        }

        /**
         * add a sewer point (neither start or exit
         *
         * @param sewerPoint
         * @throws DelaunayError
         */
        public void addSewerPoint(DPoint sewerPoint) throws DelaunayError {
                listPrepare.add(sewerPoint);
        }

        /**
         * use a sewer point to start a new branch
         *
         * @param x
         * @param y
         * @param z
         * @throws DelaunayError
         */
        public void setSewerPoint(double x, double y, double z)
                throws DelaunayError {
                // Search for the point
                addSewerEntry(x, y, z);
        }

        /**
         * use a sewer point to start a new branch
         *
         * @param sewerPoint
         * @throws DelaunayError
         */
        public void setSewerPoint(DPoint sewerPoint) throws DelaunayError {
                addSewerEntry(sewerPoint);
        }

        // ----------------------------------------------------------------
        /**
         * Start sewers definition
         *
         * @throws DelaunayError
         */
        public void sewerStart() throws DelaunayError {
                branchStart(HydroProperties.getSewerWeight(), false);
        }

        /**
         * define a new sewer branch
         *
         * @param sewerPoint
         * @throws DelaunayError
         */
        public void sewerSet(List<DPoint> sewerList) throws DelaunayError {
                if (listDefinition == HydroProperties.NONE) {
                        branchStart(HydroProperties.getSewerWeight(), false);
                } else if (listDefinition != HydroProperties.getSewerWeight()) {
                        branchValidate();
                        branchStart(HydroProperties.getSewerWeight(), false);
                }
                setNewBranch(sewerList);
        }

        /**
         * Validate and end sewer definition
         *
         * @throws DelaunayError
         */
        public void sewerValidate() throws DelaunayError {
                if (listDefinition != HydroProperties.NONE) {
                        branchValidate();
                }
                listDefinition = HydroProperties.NONE;
        }

        // ----------------------------------------------------------------
        /**
         * Start ditches definition
         *
         * @throws DelaunayError
         */
        public void ditchStart() throws DelaunayError {
                branchStart(HydroProperties.getDitchWeight());
        }

        /**
         * define a new ditch branch
         *
         * @param ditchList
         * @throws DelaunayError
         */
        public void ditchSet(List<DPoint> ditchList) throws DelaunayError {
                if (listDefinition == HydroProperties.NONE) {
                        branchStart(HydroProperties.getDitchWeight());
                } else if (listDefinition != HydroProperties.getDitchWeight()) {
                        branchValidate();
                        branchStart(HydroProperties.getDitchWeight());
                }
                setNewBranch(ditchList);
        }

        /**
         * Validate and end ditches definition
         *
         * @throws DelaunayError
         */
        public void ditchValidate() throws DelaunayError {
                if (listDefinition != HydroProperties.NONE) {
                        branchValidate();
                }
                listDefinition = HydroProperties.NONE;
        }

        // ----------------------------------------------------------------
        /**
         * Start rivers definition
         *
         * @throws DelaunayError
         */
        public void riverStart() throws DelaunayError {
                branchStart(HydroProperties.getRiverWeight());
        }

        /**
         * define a new river branch
         *
         * @param riverList
         * @throws DelaunayError
         */
        public void riverSet(List<DPoint> riverList) throws DelaunayError {
                if (listDefinition == HydroProperties.NONE) {
                        branchStart(HydroProperties.getRiverWeight());
                } else if (listDefinition != HydroProperties.getRiverWeight()) {
                        branchValidate();
                        branchStart(HydroProperties.getRiverWeight());
                }
                setNewBranch(riverList);
        }

        /**
         * Validate and end rivers definition
         *
         * @throws DelaunayError
         */
        public void riverValidate() throws DelaunayError {
                if (listDefinition != HydroProperties.NONE) {
                        branchValidate();
                }
                listDefinition = HydroProperties.NONE;
        }

        // ----------------------------------------------------------------
        /**
         * Start walls definition
         *
         * @throws DelaunayError
         */
        public void wallStart() throws DelaunayError {
                branchStart(HydroProperties.getWallWeight());
        }

        /**
         * define a new wall branch
         *
         * @param wallList
         * @throws DelaunayError
         */
        public void wallSet(List<DPoint> wallList) throws DelaunayError {
                if (listDefinition == HydroProperties.NONE) {
                        branchStart(HydroProperties.getWallWeight());
                } else if (listDefinition != HydroProperties.getWallWeight()) {
                        branchValidate();
                        branchStart(HydroProperties.getWallWeight());
                }
                setNewBranch(wallList);
        }

        /**
         * Validate and end walls definition
         *
         * @throws DelaunayError
         */
        public void wallValidate() throws DelaunayError {
                if (listDefinition != HydroProperties.NONE) {
                        branchValidate();
                }
                listDefinition = HydroProperties.NONE;
        }
        // ----------------------------------------------------------------
}
