package undirectedGraphDisplay;

import java.util.Vector;


public class TypeObject {
	String name;
	Vector<ClusterTypeObject> usedInClusters;

    TypeObject()
    {
    	usedInClusters = new Vector<ClusterTypeObject>();
    	name = "";
    }
}


