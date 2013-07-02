package undirectedGraphDisplay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/*
 * Basis of graph builder. This takes a .csv file that contains software type-usage relationships and builds them into a
 * jGraph SimpleWeightedGraph that can later be manipulated for clustering.
 */

public class GraphBuilder {
	
	SimpleWeightedGraph<String, DefaultWeightedEdge> graphFromFile(Vector<EdgeObject> edges) throws IOException
{
	// Graph<V, E> where V is the type of the vertices and E is the type of the edges
	SimpleWeightedGraph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class); 

	File file = new File("OutputRelationshipsBetweenFunctions.csv");
	BufferedReader bufRdr  = new BufferedReader(new FileReader(file));
	String line = null;
	Vector<String> nodes = new Vector<String>();
	
	//read header of file
	//Ignore the first line
	line = bufRdr.readLine();
	line = bufRdr.readLine();
	StringTokenizer st = new StringTokenizer(line,",");
	while (st.hasMoreTokens())
	{
		String nodeName = st.nextToken();
		g.addVertex(nodeName);
		nodes.add(nodeName);
	}
	
	//read each line of text file
	while((line = bufRdr.readLine()) != null)
	{
		st = new StringTokenizer(line,",");
		int i=0;
		// get name of node
		String nodeName = st.nextToken();
		while (st.hasMoreTokens())
		{
			//get next token and store it in the array
			String intersection = st.nextToken();
			//System.out.println("String is : " + intersection);
			
			//break intersection weight and items in list
			if(!intersection.equals(""))
			{
				StringTokenizer edgeTokenizer = new StringTokenizer(intersection," ");
				EdgeObject eo = new EdgeObject();
				eo.weight = Integer.parseInt(edgeTokenizer.nextToken());
				eo.source = nodeName;
				eo.dest = nodes.get(i);
				if(eo.weight != 0)
				{
					while(edgeTokenizer.hasMoreTokens())
					{
						String type = edgeTokenizer.nextToken();
						System.out.println("Adding type to list: " + type);
						eo.types.add(type);
					}
					System.out.println("Adding edge between: " + nodeName + "  and " + nodes.get(i));
					DefaultWeightedEdge e = g.addEdge(nodeName, nodes.get(i));
					if(e != null)
					{
						g.setEdgeWeight(e, eo.weight);	
					}
					else
					{
						System.out.println("null edge");
					}
				}
				edges.add(eo);
			}
			i++;
		}
	}
	 
	//close the file
	bufRdr.close();
	
	return g;
}

}
