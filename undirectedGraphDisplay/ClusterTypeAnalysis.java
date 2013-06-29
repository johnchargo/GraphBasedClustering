package undirectedGraphDisplay;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class ClusterTypeAnalysis {
	static void clusterBasedOnConnectivity(SimpleWeightedGraph<String,DefaultWeightedEdge> g, Vector<EdgeObject> edges) throws IOException
	{
	    FileWriter writer = new FileWriter("Clusters.csv");
	    writer.append("This file describes clusters of software");     
	    writer.append('\n');		
		
	    SimpleWeightedGraph<String,DefaultWeightedEdge> graphForStatistics = (SimpleWeightedGraph<String,DefaultWeightedEdge>)g.clone();
	    
	    
	    List<String> unconnectedNodes = new ArrayList<String>();
	    ConnectivityInspector<String,DefaultWeightedEdge> ci = new ConnectivityInspector<String,DefaultWeightedEdge>(g);
		List<Set<String>> connectedSets = ci.connectedSets();

		// before starting remove all unconnected components.
		Iterator<Set<String>> clusterListIter = connectedSets.iterator();
		while(clusterListIter.hasNext())
		{
			Set<String> setOfConnectedNodes = clusterListIter.next();
			
			//if the cluster only has one item it is un-connected, remove and report as unconnected component.
			if(setOfConnectedNodes.size() == 1)
			{
				String unconnectedNode = (String)setOfConnectedNodes.toArray()[0];
				unconnectedNodes.add(unconnectedNode);
				g.removeVertex(unconnectedNode);
				graphForStatistics.removeVertex(unconnectedNode);
			}
		}
        System.out.println("Number of unconnected nodes is: " + unconnectedNodes.size());
		
        ci = new ConnectivityInspector<String,DefaultWeightedEdge>(graphForStatistics);
        connectedSets = ci.connectedSets();
        
        System.out.println("There are now " + connectedSets.size() + " connected sets");
        int clusterCount = 1;
        
        while(connectedSets.size() > 0)
        //while(findMaxClusterSize(connectedSets) >30)	
        {
        	Set<String> smallestCluster = findSmallestCluster(connectedSets);
        	//Set<String> smallestCluster = findLargestCluster(connectedSets);
   
        	if(smallestCluster.size() < 30)
           {
  			 System.out.println("identified cluster small enough, writing to file"); 
        	 //Cluster is now small enough that we can remove it and document it
			 writer.append("Cluster " + Integer.toString(clusterCount) + ":,");
			 
			 Iterator<String> nodeIter = smallestCluster.iterator();
			 
			 Vector<String> nodesInCluster = new Vector<String>();
		     while(nodeIter.hasNext())
		     {
			    String nodeName = nodeIter.next();
		    	writer.append(nodeName);
			    writer.append(',');
			    graphForStatistics.removeVertex(nodeName);
			    nodesInCluster.add(nodeName);
		    	//g.removeVertex(nodeName);
		     }
		     writer.append('\n');            
		     writer.append("  Types in cluster " + Integer.toString(clusterCount) + ":,");
		     writer.append(findTypesInCluster(nodesInCluster,edges));
		     writer.append('\n'); 
		     
		     ++clusterCount;
           }
           else
           {
		      //start shrinking down cluster by removing smallest edge
      	      //System.out.println("Cluster is too big, finding smallest edge to remove");
      	      //DefaultWeightedEdge edgeToRemove = findSmallestEdge(smallestCluster, g);
      	      DefaultWeightedEdge edgeToRemove = findEdgeToRemove(smallestCluster, graphForStatistics);
      	      
      	      //String output = "Cluster has ";
      	      //output = output + smallestCluster.size();
      	      //output = output + " nodes, contains: ";
      	      
      		  // Iterator<String> nodeIterator = smallestCluster.iterator();
      		  // while(nodeIterator.hasNext())
      		  // {
      	       //   output = output + nodeIterator.next();
      	       //   output = output + " ";
      		   //}
      	      //System.out.println(output);
      	      
      	      String edgeSource = graphForStatistics.getEdgeSource(edgeToRemove);
      	      String edgeTarget = graphForStatistics.getEdgeTarget(edgeToRemove);
      	      double edgeWeight = graphForStatistics.getEdgeWeight(edgeToRemove);
      	      System.out.println("  removing edge between " + edgeSource + " and " + edgeTarget + " with weight " + edgeWeight + " to attempt to break cluster");
      	      g.removeEdge(edgeToRemove);   
      	      graphForStatistics.removeEdge(edgeToRemove);
           }
           ci = new ConnectivityInspector<String,DefaultWeightedEdge>(graphForStatistics);
           connectedSets = ci.connectedSets();
        }        
        
        
			
		writer.append("Unconnected Nodes(" + unconnectedNodes.size() + ") :,"); 
		Iterator<String> unconnectedNodeIterator = unconnectedNodes.iterator();
		while(unconnectedNodeIterator.hasNext())
		{
			writer.append(unconnectedNodeIterator.next());
			writer.append(',');
		}
	    writer.append('\n');	

	    writer.flush();
		writer.close();
	}	
	

	static DefaultWeightedEdge findSmallestEdge(Set<String> setOfNodes, SimpleWeightedGraph<String,DefaultWeightedEdge> g)
	{
	   DefaultWeightedEdge smallestEdge = null;
	   double smallestWeight = 1000000;
	   Iterator<String> nodeIterator = setOfNodes.iterator();
	   while(nodeIterator.hasNext())
	   {
		   String node = nodeIterator.next();
		   Set<DefaultWeightedEdge> edgesOfVertex = g.edgesOf(node);
		   Iterator<DefaultWeightedEdge> edges = edgesOfVertex.iterator();
		   while(edges.hasNext())
		   {
			   DefaultWeightedEdge edge = edges.next();
			   double edgeWeight = g.getEdgeWeight(edge);
			   if(edgeWeight < smallestWeight)
			   {
				   smallestWeight = edgeWeight;
				   smallestEdge = edge;
			   }
		   }
		   
		   
	   }
	   return smallestEdge;
	}
	
	static Set<String> findSmallestCluster(List<Set<String>> listOfClusters)
	{
		Set<String> smallestCluster = listOfClusters.get(0);;
		Iterator<Set<String>> clusterIter = listOfClusters.iterator();
		while(clusterIter.hasNext())
		{
			Set<String> nextCluster = clusterIter.next();
			if(nextCluster.size() < smallestCluster.size())
			{
				smallestCluster = nextCluster;
			}
		}
		return smallestCluster;
	}

	
	static Set<String> findLargestCluster(List<Set<String>> listOfClusters)
	{
		Set<String> largestCluster = listOfClusters.get(0);;
		Iterator<Set<String>> clusterIter = listOfClusters.iterator();
		while(clusterIter.hasNext())
		{
			Set<String> nextCluster = clusterIter.next();
			if(nextCluster.size() > largestCluster.size())
			{
				largestCluster = nextCluster;
			}
		}
		return largestCluster;
	}
	static DefaultWeightedEdge findEdgeToRemove(Set<String> setOfNodes, SimpleWeightedGraph<String,DefaultWeightedEdge> g)
	{
	   DefaultWeightedEdge smallestEdge = null;
	   int maxNumberOfEdges = 0;
	   double maxRatioEdgeToTotal = 0;
	   double smallestWeight = 1000000;
	   Iterator<String> nodeIterator = setOfNodes.iterator();
	   while(nodeIterator.hasNext())
	   {
		   String node = nodeIterator.next();
		   Set<DefaultWeightedEdge> edgesOfVertex = g.edgesOf(node);
		   Iterator<DefaultWeightedEdge> edges = edgesOfVertex.iterator();
		   while(edges.hasNext())
		   {
			   DefaultWeightedEdge edge = edges.next();
			   double edgeWeight = g.getEdgeWeight(edge);
			   
			   //check to make sure removing edge wouldn't create orphan.
			   String source = g.getEdgeSource(edge);
			   String target = g.getEdgeTarget(edge);
			   Set<DefaultWeightedEdge> edgesOfSource = g.edgesOf(source);
			   Set<DefaultWeightedEdge> edgesOfTarget = g.edgesOf(target);
			   if((edgesOfSource.size() > 1) &&  (edgesOfTarget.size() > 1))
			   {
				   //find total weight of nodes
				   double totalEdgeWeight = 0;
				   Iterator<DefaultWeightedEdge> sourceIterator = edgesOfSource.iterator();
				   while(sourceIterator.hasNext())
				   {
					  totalEdgeWeight = totalEdgeWeight + g.getEdgeWeight(sourceIterator.next());
				   }
				   Iterator<DefaultWeightedEdge> destIterator = edgesOfSource.iterator();
				   while(destIterator.hasNext())
				   {
					  totalEdgeWeight = totalEdgeWeight + g.getEdgeWeight(destIterator.next());
				   }				   
				   
				   
				   if(edgeWeight < smallestWeight)
				   {
					   smallestWeight = edgeWeight;
					   maxNumberOfEdges = edgesOfVertex.size();
					   maxRatioEdgeToTotal = edgeWeight/totalEdgeWeight;
					   smallestEdge = edge;
				   }
				   else if(edgeWeight == smallestWeight)
				   {
					   //if(edgesOfVertex.size() < maxNumberOfEdges)
					   if((edgeWeight/totalEdgeWeight) > maxRatioEdgeToTotal)
					   {
						   maxNumberOfEdges = edgesOfVertex.size();
						   maxRatioEdgeToTotal = edgeWeight/totalEdgeWeight;
						   smallestEdge = edge;
					   }
				   }
			   }
		   }
	   }
	   return smallestEdge;
	}
	
static int findMaxClusterSize(List<Set<String>> connectedSets)	
{
	int maxSize = 0;
	Iterator<Set<String>> clusterListIter = connectedSets.iterator();
	while(clusterListIter.hasNext())
	{
		Set<String> setOfConnectedNodes = clusterListIter.next();
		
		if(setOfConnectedNodes.size() > maxSize)
		{
			maxSize = setOfConnectedNodes.size();
		}
	}	
	return maxSize;
}

static String findTypesInCluster(Vector<String> nodes, Vector<EdgeObject> edges)
{
	Vector<String> typesUsedByCluster = new Vector<String>();
	
	Iterator<String> nodeIter = nodes.iterator();
	while(nodeIter.hasNext())
	{
		String node = nodeIter.next();
		
		//iterate through edges to find types
		Iterator<EdgeObject> edgeIterator = edges.iterator();
		while(edgeIterator.hasNext())
		{
			EdgeObject edge = edgeIterator.next();
			if(node.equals(edge.source) || node.equals(edge.dest))
			{
				Iterator<String> typesIter = edge.types.iterator();
				while(typesIter.hasNext())
				{
					String type = typesIter.next();
					
					if(!typesUsedByCluster.contains(type))
					{
						typesUsedByCluster.add(type);
					}
				}
			}
		}
	}
	String output = "";
	Iterator<String> outputIter = typesUsedByCluster.iterator();
	while(outputIter.hasNext())
	{
		output = output + outputIter.next() + " ";
	}
	return output;
}


}
