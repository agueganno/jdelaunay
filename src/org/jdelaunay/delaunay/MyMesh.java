package org.jdelaunay.delaunay;
/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN
 * @date 2009-01-12
 * @version 1.0
 */


import java.awt.*;
import java.io.*;
import java.util.*;

public class MyMesh {
	// Vectors with points and edges
	protected ArrayList<MyPoint> points;
	protected LinkedList<MyEdge> edges;
	protected LinkedList<MyTriangle> triangles;
	protected LinkedList<MyEdge> compEdges;

	// bounding box
	protected int maxx, maxy;
	private double bminx, bmaxx, bminy, bmaxy;
	private MyPoint pminx, pmaxx, pminy, pmaxy;

	// Display elements
	private MyDrawing affiche;
	public long duration;
	private long startComputation;
	private boolean displayCircles;

	/**
	 * Create an empty Mesh. Allocate data structures
	 *
	 */
	public MyMesh() {
		// Generate vectors
		points = new ArrayList<MyPoint>();
		edges = new LinkedList<MyEdge>();
		triangles = new LinkedList<MyTriangle>();
		compEdges = new LinkedList<MyEdge>();

		maxx = 1200;
		maxy = 700;

		duration = 0;
		bminx = bmaxx = bminy = bmaxy = 0.0;
		pminx = pmaxx = pminy = pmaxy = null;

		affiche = null;
		displayCircles = false;
	}

	/**
	 * Set bounding box for the generation
	 *
	 * @param maxx
	 * @param maxy
	 */
	public void setMax(int maxx, int maxy) {
		this.maxx = maxx;
		this.maxy = maxy;
	}

	/**
	 * Generate random points
	 *
	 * @param _NbPoints
	 */
	public void setRandomPoints(int _NbPoints) {
		for (int i = 0; i < _NbPoints; i++) {
			// Generate random coordinates
			double x = Math.random() * maxx;
			double y = Math.random() * maxy;
			double z = Math.random() * 1000.0;

			points.add(new MyPoint(x, y, z));
		}
	}

	/**
	 * Generate random edges Can be applied only if points are created
	 *
	 * @param _NbEdges
	 */
	public void setRandomEdges(int _NbEdges) {
		int NbPoints = points.size() - 1;
		if (NbPoints > 1) {
			for (int i = 0; i < _NbEdges; i++) {
				int start = (int) Math.round(Math.random() * NbPoints);
				int end = (int) Math.round(Math.random() * NbPoints);
				while (end == start)
					end = (int) Math.round(Math.random() * NbPoints);
				MyEdge anEdge = new MyEdge(points.get(start), points.get(end));
				anEdge.marked = 1;
				compEdges.add(anEdge);
			}
		}
	}

	/**
	 * Get JPanel in which Mesh is displayed
	 *
	 * @return
	 */
	public MyDrawing getAffiche() {
		return affiche;
	}

	/**
	 * Set JPanel in which Mesh is displayed
	 *
	 * @param affiche
	 */
	public void setAffiche(MyDrawing affiche) {
		this.affiche = affiche;
	}

	/**
	 * Display circles around triangles when displayed in the JPanel
	 *
	 * @param displayCircles
	 */
	public void setDisplayCircles(boolean displayCircles) {
		this.displayCircles = displayCircles;
	}

	/**
	 * Compute Mesh bounding box
	 */
	private void getBoundingBox() {
		bminx = bmaxx = bminy = bmaxy = 0.0;
		pminx = pmaxx = pminy = pmaxy = null;

		for (MyPoint aPoint : points) {
			double x = aPoint.getX();
			double y = aPoint.getY();

			// update bounding box
			if (pminx == null) {
				bminx = bmaxx = x;
				bminy = bmaxy = y;
				pminx = pmaxx = pminy = pmaxy = aPoint;
			} else {
				if (bminx > x) {
					bminx = x;
					pminx = aPoint;
				} else if (bmaxx < x) {
					bmaxx = x;
					pmaxx = aPoint;
				}
				if (bminy > y) {
					bminy = y;
					pminy = aPoint;
				} else if (bmaxy < y) {
					bmaxy = y;
					pmaxy = aPoint;
				}
			}
		}
	}

	/**
	 * Add the bounding box to current data
	 */
	public void addBoundingBox() {
		getBoundingBox();

		// Add bounding Box
		MyPoint aPoint1 = new MyPoint(bminx, bminy);
		MyPoint aPoint2 = new MyPoint(bminx, bmaxy);
		MyPoint aPoint3 = new MyPoint(bmaxx, bmaxy);
		MyPoint aPoint4 = new MyPoint(bmaxx, bminy);

		points.add(aPoint1);
		points.add(aPoint2);
		points.add(aPoint3);
		points.add(aPoint4);

		compEdges.add(new MyEdge(aPoint1, pminx));
		compEdges.add(new MyEdge(pminx, aPoint2));

		compEdges.add(new MyEdge(aPoint2, pmaxy));
		compEdges.add(new MyEdge(pmaxy, aPoint3));

		compEdges.add(new MyEdge(aPoint3, pmaxx));
		compEdges.add(new MyEdge(pmaxx, aPoint4));

		compEdges.add(new MyEdge(aPoint4, pminy));
		compEdges.add(new MyEdge(pminy, aPoint1));
	}

	/**
	 * Get the current number of points in the Mesh
	 *
	 * @return NbPoints
	 */
	public int getNbPoints() {
		return points.size();
	}

	/**
	 * Get the current number of edges in the Mesh
	 *
	 * @return NbEdges
	 */
	public int getNbEdges() {
		return edges.size();
	}

	/**
	 * Add an edge to the mesh
	 */
	public void addEdge(MyPoint aPoint1, MyPoint aPoint2) {
		MyEdge anEdge = new MyEdge(aPoint1, aPoint2);
		compEdges.add(anEdge);
	}

	/**
	 * Add an edge to the mesh
	 */
	public void addEdge(MyEdge anEdge) {
		compEdges.add(new MyEdge(anEdge));
	}

	/**
	 * Get the current number of triangles in the Mesh
	 *
	 * @return NbTriangles
	 */
	public int getNbTriangles() {
		return triangles.size();
	}

	/**
	 * Get the points structure
	 *
	 * @return points
	 */
	public ArrayList<MyPoint> getPoints() {
		return points;
	}

	/**
	 * Get the edges structure
	 *
	 * @return edges
	 */
	public LinkedList<MyEdge> getEdges() {
		return edges;
	}

	/**
	 * Get the complementary edges structure
	 * This structure emorize the edges that have to be added to the triangularization
	 *
	 * @return edges
	 */
	public LinkedList<MyEdge> getCompEdges() {
		return compEdges;
	}

	/**
	 * Get the triangle structure
	 *
	 * @return triangle
	 */
	public LinkedList<MyTriangle> getTriangles() {
		return triangles;
	}

	/**
	 * Start timer
	 */
	public void setStart() {
		startComputation = Calendar.getInstance().getTime().getTime();
	}

	/**
	 * End of timer - generate duration
	 */
	public void setEnd() {
		duration = Calendar.getInstance().getTime().getTime()
				- startComputation;
	}

	/**
	 * Draw Mesh in the JPanel : triangles and edges. If duration is positive,
	 * also display it
	 * Must be used only when using package drawing
	 *
	 * @param g
	 */
	public void displayObject(Graphics g) {
		int decalageX = 10;
		int decalageY = maxy + 30;

		g.setColor(Color.white);
		g.fillRect(decalageX - 5, 30 - 5, maxx + 5, maxy + 5);

		// Draw triangles
		if (!triangles.isEmpty()) {
			for (MyTriangle aTriangle : triangles) {
				aTriangle.setColor(g);
				aTriangle.displayObject(g, decalageX, decalageY);
			}

			if (displayCircles)
			for (MyTriangle aTriangle : triangles) {
				aTriangle.setColor(g);
				aTriangle.displayObjectCircles(g, decalageX, decalageY);
			}
}
		// Draw lines
		if (false)
		if (!edges.isEmpty())
			for (MyEdge aVertex : edges) {
				aVertex.setColor(g);
				aVertex.displayObject(g, decalageX, decalageY);
			}

		if (duration > 0) {
			g.setColor(Color.black);
			g.drawString("Computation time : " + duration + " ms", decalageX,
					20 + decalageY);
		}
	}

	/**
	 * Refresh Mesh display in the JPanel
	 * Must be used only when using package drawing
	 */
	public void refresh() {
		if (affiche != null)
			affiche.refresh();
	}

	/**
	 * Save the Mesh elements in a XML file
	 */
	public void saveMeshXML() {
		Writer writer;
		try {
			writer = new FileWriter("Mesh.xml");
			writer.write("<Mesh>\n");
			writer.write("\t<Points>\n");
			for (MyPoint aPoint : points) {
				writer.write("\t\t<Point id=\"" + points.indexOf(aPoint)
						+ "\">\n");
				writer.write("\t\t\t<X>" + aPoint.xy[0] + "</X>\n");
				writer.write("\t\t\t<Y>" + aPoint.xy[1] + "</Y>\n");
				writer.write("\t\t\t<Z>" + aPoint.xy[2] + "</Z>\n");
				if (aPoint.type == null)
					writer.write("\t\t\t<Type />\n");
				else
					writer.write("\t\t\t<Type>" + aPoint.type + "</Type>\n");
				writer.write("\t\t</Point>\n");
			}
			writer.write("\t</Points>\n");

			writer.write("\t<Edges>\n");
			for (MyEdge anEdge : edges) {
				writer.write("\t\t<Segment id=\"" + edges.indexOf(anEdge)
						+ "\">\n");
				writer.write("\t\t\t<Start>" + points.indexOf(anEdge.point[0])
						+ "</Start>\n");
				writer.write("\t\t\t<End>" + points.indexOf(anEdge.point[1])
						+ "</End>\n");
				if (anEdge.type == null)
					writer.write("\t\t\t<Type />\n");
				else
					writer.write("\t\t\t<Type>" + anEdge.type + "</Type>\n");
				if (anEdge.left == null)
					writer.write("\t\t\t<Left>-1</Left>\n");
				else
					writer.write("\t\t\t<Left>" + triangles.indexOf(anEdge.left) + "</Left>\n");
				if (anEdge.right == null)
					writer.write("\t\t\t<Right>-1</Right>\n");
				else
					writer.write("\t\t\t<Right>" + triangles.indexOf(anEdge.right) + "</Right>\n");
				writer.write("\t\t</Segment>\n");
			}
			writer.write("\t</Edges>\n");

			writer.write("\t<Triangles>\n");
			for (MyTriangle aTriangle : triangles) {
				writer.write("\t\t<Triangle id=\""
						+ triangles.indexOf(aTriangle) + "\">\n");
				for (int i = 0; i < 3; i++)
					writer.write("\t\t\t<Edge>"
							+ edges.indexOf(aTriangle.edges[i]) + "</Edge>\n");
				writer.write("\t\t</Triangle>\n");
			}
			writer.write("\t</Triangles>\n");

			writer.write("</Mesh>\n");
			writer.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Save the Mesh points in a file
	 */
	public void saveMeshPoints() {
		Writer writer;
		try {
			writer = new FileWriter("Mesh.txt");
			for (MyPoint aPoint : points) {
				writer.write(aPoint.xy[0] + "\t" + aPoint.xy[1] + "\t"
						+ aPoint.xy[2] + "\t" + "\n");
			}
			writer.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Read Mesh points from the file
	 */
	public void readMeshPoints() {
		Reader reader;
		try {
			String delimiteurs = "\t";
			reader = new FileReader("Mesh.txt");

			BufferedReader in = new BufferedReader(reader);
			String ligne = in.readLine();
			while (ligne != null) {
				StringTokenizer st = new StringTokenizer(ligne, delimiteurs);
				MyPoint aPoint = new MyPoint();
				int i = 0;
				while (st.hasMoreTokens()) {
					String mot = st.nextToken();
					aPoint.xy[i] = Double.parseDouble(mot);
					i++;
				}
				if (i == 3)
					points.add(aPoint);
				ligne = in.readLine();
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Save Mesh
	 *
	 * @param path
	 */
	public void saveMesh(String path) {
		try {
			DataOutputStream output = new DataOutputStream(
					new FileOutputStream(path));
			int NbTriangles = getNbTriangles();

			output.writeUTF("Mesh triangles quantity:\n");
			output.writeUTF(NbTriangles + "\n");
			output.writeUTF("Triangle\tP1\tP2\tP3\n");
			int i = 0;
			for (MyTriangle aTriangle : triangles) {
				i++;
				output.writeUTF(i + "\t");
				for (int j = 0; j < 3; j++) {
					MyPoint aPoint = aTriangle.points[j];
					for (int k = 0; k < 3; k++) {
						output.writeUTF(aPoint.xy[k] + "");
						if ((k < 2) || (j < 2))
							output.writeUTF("\t");
						else
							output.writeUTF("\n");
					}
				}
			}
			output.writeUTF("\n");
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPoints(ArrayList<MyPoint> points) {
		this.points = points;
	}


	public void setEdges(LinkedList<MyEdge> breaklines) {
		this.compEdges = breaklines;


	}

}
