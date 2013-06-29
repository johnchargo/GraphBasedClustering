package GraphDisplay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

public class GraphBuilder {
	
	DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graphFromFile() throws IOException
{
	// Graph<V, E> where V is the type of the vertices and E is the type of the edges
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class); 

	File file = new File("OutputRelationships.csv");
	BufferedReader bufRdr  = new BufferedReader(new FileReader(file));
	String line = null;
	Vector<String> nodes = new Vector<String>();
	 
	//read header of file
	//ignore the first line
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
			if((!intersection.equals("")) && (!intersection.equals("0")))
			{
				DefaultWeightedEdge e = g.addEdge(nodeName, nodes.get(i));
				g.setEdgeWeight(e, Integer.parseInt(intersection));	
			}
			i++;
		}
	}
	 
	//close the file
	bufRdr.close();
	
	return g;
}

}
