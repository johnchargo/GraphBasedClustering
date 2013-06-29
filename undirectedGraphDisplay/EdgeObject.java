package undirectedGraphDisplay;

import java.util.Vector;

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


