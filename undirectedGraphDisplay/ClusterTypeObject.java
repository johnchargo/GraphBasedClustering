package undirectedGraphDisplay;

/*
 * Class used for calculating statistics about the clusters in the system.
 */

public class ClusterTypeObject
{
	int clusterNumber;  // number is unique per cluster
	int numEdgesTypeContributesToCluster;
	int totalWeightOfTypes;
	
	
	ClusterTypeObject()
    {
		clusterNumber = 0;
		numEdgesTypeContributesToCluster = 0;
		totalWeightOfTypes = 0;
    }
	
}

