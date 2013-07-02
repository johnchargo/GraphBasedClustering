package undirectedGraphDisplay;

import java.util.Vector;

/*
 * Class used to store an "edge" of a graph. An edge is made up of a source, destination, and weight.
 * For this implementation the edge also stores the names of types that make up the edge.
 */
public class EdgeObject {
	String source;
	String dest;
	Vector<String> types;
	int weight;

    EdgeObject()
    {
    	types = new Vector<String>();
    	source = "";
    	dest = "";
    	weight = 0;
    }
}


