package undirectedGraphDisplay;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/* 
 * Tool used early in thesis research to analyze the connectivity of a software graph. Outputs
 * are saved to a file "ClustersByFilterSize.csv"
 */

public class ConnectivityAnalyzer {
	static void connectivityAnalysis(SimpleWeightedGraph<String,DefaultWeightedEdge> g) throws IOException
	{
	    FileWriter writer = new FileWriter("ClustersByFilterSize.csv");
	    writer.append("Filter Criteria (anything edges below this number are filtered)");
	    writer.append(',');
	    writer.append("Number of Clusters");
	    writer.append(',');
	    writer.append("Largest Cluster");
	    writer.append(',');
	    writer.append("Average Cluster Size");  
	    writer.append(',');
	    writer.append("Cluster listing by size");     
	    writer.append('\n');		
		
	    for(int i = 1; i <= 20; ++i)
	    {
			ConnectivityInspector<String,DefaultWeightedEdge> ci = new ConnectivityInspector<String,DefaultWeightedEdge>(g);
			List<Set<String>> connectedSets = ci.connectedSets();
			System.out.println("Number of connected sets is: " + connectedSets.size());
			Iterator<Set<String>> clusterListIter = connectedSets.iterator();
		
		    writer.append(Integer.toString(i));  // Filter Criteria (anything edges below this number are filtered)");
		    writer.append(',');
		    writer.append(Integer.toString(connectedSets.size()));  //("Number of Clusters");
		    writer.append(',');
			
			int clusterNumber = 0;
			int maxCluster = 0;
			int clusterTotal = 0;
			int clusterCounter = 0;
			while(clusterListIter.hasNext())
			{
				Set<String> setOfConnectedNodes = clusterListIter.next();
				clusterNumber++;
				//System.out.println("Cluster " + clusterNumber + " has " + setOfConnectedNodes.size() + " functions in it");
			
			    int numFuncsInCluster = setOfConnectedNodes.size();
			    clusterTotal += numFuncsInCluster;
			    clusterCounter ++;
			    
			    if(numFuncsInCluster > maxCluster) maxCluster = numFuncsInCluster;
			}
		    writer.append(Integer.toString(maxCluster)); //"Largest Cluster");
		    writer.append(',');
		    if(clusterCounter > 0)
		    {
		    	writer.append(Double.toString((double)clusterTotal/(double)clusterCounter)); //"Average Cluster Size");          
		    }
		    else 
		    {
		    	writer.append('0');
		    }
		    
		    Iterator<Set<String>> clusterListIter2 = connectedSets.iterator();
			while(clusterListIter2.hasNext())
			{
				Set<String> setOfConnectedNodes = clusterListIter2.next();
				writer.append(Integer.toString(setOfConnectedNodes.size()));
		        writer.append(',');
			}
		    writer.append('\n');				    
		    
		    //remove edges based on filter
		    Set<DefaultWeightedEdge> allEdges = g.edgeSet();
		    Iterator<DefaultWeightedEdge> edgeSetIter = allEdges.iterator();
		    int removedEdgeCounter = 0;
		    while(edgeSetIter.hasNext())
			{
				DefaultWeightedEdge edge = edgeSetIter.next();
				if(g.getEdgeWeight(edge) <= i)
				{
					removedEdgeCounter++;

					// if edge is less than i, remove edge
					g.removeEdge(edge);
					allEdges = g.edgeSet();
					edgeSetIter = allEdges.iterator();
				}
			}
		    System.out.println("Removed " + removedEdgeCounter + " edges of weight " + i );
		    
	    }
	    writer.flush();
		writer.close();
	}
}
