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

public class SimpleClusterAlgorithm {
	
	Vector<TypeObject> clusterTypeUsage;
	
	SimpleClusterAlgorithm()
	{
		clusterTypeUsage = new Vector<TypeObject>();
	}
	
	void clusterBasedOnConnectivity(SimpleWeightedGraph<String,DefaultWeightedEdge> g, Vector<EdgeObject> edges) throws IOException
	{
	    FileWriter writer = new FileWriter("Clusters.csv");	
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
        {
        	Set<String> smallestCluster = findSmallestCluster(connectedSets);
   
        	if(smallestCluster.size() <= 2)
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
		     }
		     writer.append('\n');            
		     
		     ++clusterCount;
           }
           else
           {
		      //start shrinking down cluster by removing smallest edge
        	   System.out.println("Smallest cluster size is: " + smallestCluster.size());
      	      DefaultWeightedEdge edgeToRemove = findEdgeToRemove(smallestCluster, graphForStatistics);
      	      
      	      System.out.println("removing edge");
      	      g.removeEdge(edgeToRemove);   
      	      graphForStatistics.removeEdge(edgeToRemove);
           }
           ci = new ConnectivityInspector<String,DefaultWeightedEdge>(graphForStatistics);
           connectedSets = ci.connectedSets();
        }        

	    writer.flush();
		writer.close();
		
		printTypeUsage();
		printClusterScore();
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
	   DefaultWeightedEdge edge = null;
	   double smallestWeight = 1000000;
	   Iterator<String> nodeIterator = setOfNodes.iterator();
	   while(nodeIterator.hasNext())
	   {
		   String node = nodeIterator.next();
		   Set<DefaultWeightedEdge> edgesOfVertex = g.edgesOf(node);
		   Iterator<DefaultWeightedEdge> edges = edgesOfVertex.iterator();
		   while(edges.hasNext())
		   {
			   edge = edges.next();
			   double edgeWeight = g.getEdgeWeight(edge);
			   
			   //check to make sure removing edge wouldn't create orphan.
			   String source = g.getEdgeSource(edge);
			   String target = g.getEdgeTarget(edge);
			   Set<DefaultWeightedEdge> edgesOfSource = g.edgesOf(source);
			   Set<DefaultWeightedEdge> edgesOfTarget = g.edgesOf(target);
			   if((edgesOfSource.size() > 1) &&  (edgesOfTarget.size() > 1))
			   {
				   if(edgeWeight < smallestWeight)
				   {
					   smallestWeight = edgeWeight;
					   smallestEdge = edge;
					   
				   }
			   }
		   }
	   }
	   
	   if(smallestEdge == null)
	   {
		   smallestEdge = edge;
		   System.out.println("null edge! broke");
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

String findTypesInCluster(SimpleWeightedGraph<String,DefaultWeightedEdge> g, Vector<String> nodes, Vector<EdgeObject> edges, int clusterNum)
{
	Vector<String> typesUsedByCluster = new Vector<String>();
	
	Iterator<String> nodeIter = nodes.iterator();
	while(nodeIter.hasNext())
	{
		String node = nodeIter.next();
			
		//iterate through edges to find types
		Set<DefaultWeightedEdge> edgesOfNode = g.edgesOf(node);
		Iterator<DefaultWeightedEdge> allEdgeIter = edgesOfNode.iterator();
		while(allEdgeIter.hasNext())
		{
			DefaultWeightedEdge weightedEdge= allEdgeIter.next();
			Iterator<EdgeObject> allEdgeIterator = edges.iterator();
			while(allEdgeIterator.hasNext())
			{
				
				EdgeObject edge = allEdgeIterator.next();
				if((edge.source.equals(g.getEdgeSource(weightedEdge))) && (edge.dest.equals(g.getEdgeTarget(weightedEdge))))
				{
					Iterator<String> typesIter = edge.types.iterator();
					while(typesIter.hasNext())
					{
						String type = typesIter.next();
						if(!typesUsedByCluster.contains(type))
						{
							typesUsedByCluster.add(type);
						}
						
						//add to list of used types for later analysis.
						addTypeInfo(type, clusterNum);
					}
					break;
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

void addTypeInfo(String type, int clusterNum)
{
	//see if type already exists
	Iterator<TypeObject> clusterTypeUseIter = clusterTypeUsage.iterator();
	boolean foundType = false;
	while(clusterTypeUseIter.hasNext())
	{
		TypeObject to = clusterTypeUseIter.next();
		if(to.name.equals(type))
		{
			foundType = true;
			boolean clusterFound = false;
			//type is found, now see if this cluster already exists...
			Iterator<ClusterTypeObject> clusterIter = to.usedInClusters.iterator();
			while(clusterIter.hasNext())
			{
			   ClusterTypeObject cto = clusterIter.next();
			   if(cto.clusterNumber == clusterNum)
			   {
				   // type exists and this cluster has been identified. Increment number of edges this contributes to.
				   cto.numEdgesTypeContributesToCluster++;
				   clusterFound = true;
				   break;
			   }
			}
			if(!clusterFound)
			{
				//this type hasn't been attributed to this cluster yet, add it.
				ClusterTypeObject cto = new ClusterTypeObject();
				cto.clusterNumber = clusterNum;
				cto.numEdgesTypeContributesToCluster = 1;
				to.usedInClusters.add(cto);
			}
			break;
		}
	}
	if(!foundType)
	{
		// type isn't in list already, add it.
		TypeObject newTO = new TypeObject();
		newTO.name = type;
		
		ClusterTypeObject cto = new ClusterTypeObject();
		cto.clusterNumber = clusterNum;
		cto.numEdgesTypeContributesToCluster = 1;
		newTO.usedInClusters.add(cto);
		clusterTypeUsage.add(newTO);
	}
	
}

void printTypeUsage() throws IOException
{
    FileWriter writer = new FileWriter("ClusterTypeUsage.csv");
    writer.append("This file lists the types used in clusters, which clusters they are used in, and how many edges they contribute to in the cluster");     
    writer.append('\n');	
    writer.append("Format is:   Type: (type name)       (Cluster Number)< Number of edges this type contributes to in the cluster>");
    writer.append('\n');	
    
    Iterator<TypeObject> clusterTypeUseIter = clusterTypeUsage.iterator();
	while(clusterTypeUseIter.hasNext())
	{
		TypeObject to = clusterTypeUseIter.next();
		writer.append("Type: ");
		writer.append(to.name);
		writer.append(',');
		
		Iterator<ClusterTypeObject> ctoIter = to.usedInClusters.iterator();
		while(ctoIter.hasNext())
		{
			ClusterTypeObject cto = ctoIter.next();
			writer.append(Integer.toString(cto.clusterNumber));
			writer.append('<');
			writer.append(Integer.toString(cto.numEdgesTypeContributesToCluster/2));
			writer.append('>');
			writer.append(',');
			
		}
		writer.append('\n');
	}
    writer.flush();
	writer.close();
    
}

void printClusterScore() throws IOException
{
    FileWriter writer = new FileWriter("ClusterScores.csv");
    writer.append("This file lists all the clusters and then a 'score' for the cluster. \n");
    writer.append("The score of cluster x the sum of all the clusters the types of x is in \n");     
    writer.append("Cluster Number");
    writer.append(',');
    writer.append("Score");
    writer.append('\n');	
    
    Vector<ClusterTypeObject> clusterList = new Vector<ClusterTypeObject>();
    
    Iterator<TypeObject> clusterTypeUseIter = clusterTypeUsage.iterator();
	while(clusterTypeUseIter.hasNext())
	{
		TypeObject to = clusterTypeUseIter.next();
		
		Iterator<ClusterTypeObject> ctoIter = to.usedInClusters.iterator();
		while(ctoIter.hasNext())
		{
			ClusterTypeObject cto = ctoIter.next();
			
			Iterator<ClusterTypeObject> clusterListIter = clusterList.iterator();
			boolean foundCluster = false;
			while(clusterListIter.hasNext())
			{
				ClusterTypeObject outputCto = clusterListIter.next();
				if(outputCto.clusterNumber == cto.clusterNumber)
				{
					foundCluster = true;
					outputCto.totalWeightOfTypes++;
					break;
				}
			}
			if(!foundCluster)
			{
				ClusterTypeObject newCto = new ClusterTypeObject();
				newCto.clusterNumber = cto.clusterNumber;
				newCto.totalWeightOfTypes = 1;
				clusterList.add(newCto);
			}
		}
	}
	
	Iterator<ClusterTypeObject> clusterListPrintIter = clusterList.iterator();
	while(clusterListPrintIter.hasNext())
	{
		ClusterTypeObject cto = clusterListPrintIter.next();
		writer.append(Integer.toString(cto.clusterNumber));
		writer.append(',');
		writer.append(Integer.toString(cto.totalWeightOfTypes));
		writer.append('\n');
	}
	
    writer.flush();
	writer.close();
}

}
