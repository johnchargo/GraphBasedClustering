package undirectedGraphDisplay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.ExceptionListener;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.*;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.EdmondsKarpMaximumFlow;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.organic.*;


/*
 * The Undirected Graph Reader builds a graph from input files, clusters the results
 * based on the simple cluster algorithm, and visualizes it using the JGraph libraries.
 */

public class UndirectedGraphReader {

	private static JGraphModelAdapter<String, DefaultWeightedEdge> jgAdapter;
	private static SimpleWeightedGraph<String,DefaultWeightedEdge> g;
	
	public static void main(String[] args) throws IOException {
		System.out.println("Launching undirected graph analysis tool");
		
		GraphBuilder gb = new GraphBuilder();
		Vector<EdgeObject> edges = new Vector<EdgeObject>();
		g = gb.graphFromFile(edges);
		
		getNodeStatistics(g);
        SimpleClusterAlgorithm sca = new SimpleClusterAlgorithm();
        sca.clusterBasedOnConnectivity(g, edges);
		
        ///////////////////////////////////////////////////////////////////
        //
        // Now that clusters are created, visualize using JGraph libraries.
        //
    	///////////////////////////////////////////////////////////////////
        
		// create a visualization using JGraph, via the adapter
        jgAdapter = new JGraphModelAdapter<String, DefaultWeightedEdge>(g);
        
        JGraph jgraph = new JGraph(jgAdapter);
        
        formatEdges(g.edgeSet());
        
        JGraphFacade facade = new JGraphFacade(jgraph); // Pass the facade the JGraph instance
        
        JGraphFastOrganicLayout organicLayout = new JGraphFastOrganicLayout();
        organicLayout.setForceConstant(200);
        organicLayout.setInitialTemp(25);
        JGraphLayout layout = organicLayout; // Create an instance of the appropriate layout
        
        layout.run(facade); // Run the layout on the facade. Note that layouts do not implement the Runnable interface
        Map nested = facade.createNestedMap(true, true); // Obtain a map of the resulting attribute changes from the facade
        jgraph.getGraphLayoutCache().edit(nested); // Apply the results to the actual graph
        
        // output serialized objects to file so the graph can be viewed using the Graph Viewer without
        // recomputing clusters
        ObjectOutput out = new ObjectOutputStream(new FileOutputStream("ClusteredGraphOutput.ser"));
        
	    out.writeObject(g);
	    out.close();	        
        
        //Create a JPanel  
        JPanel panel=new JPanel();
        
        panel.add(jgraph);  // add graph to panel
        //Create a scrollbar using JScrollPane and add panel into it's viewport  
        //Set vertical and horizontal scrollbar always show  
        JScrollPane scrollBar=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  

        JFrame frame = new JFrame("JChargo Graph Analysis R1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().add(jgraph);
        
        //Add content to the window.
        //frame.add(new FileChooserDemo(), BorderLayout.PAGE_START);      
       
        FileIoClass fcd = new FileIoClass();
        JMenuBar menubar  = new JMenuBar();
        JMenu    fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem processItem = new JMenuItem("Process");
        JMenuItem saveItem = new JMenuItem("Save");
        openItem.addActionListener(fcd.new OpenAction());
        processItem.addActionListener(fcd.new ProcessAction());
        saveItem.addActionListener(fcd.new SaveAction());

        //... Assemble the menu
        menubar.add(fileMenu);
        fileMenu.add(openItem);
        fileMenu.add(processItem);
        fileMenu.add(saveItem);

        frame.setJMenuBar(menubar);
        frame.add(scrollBar);
        //frame.add(new FileChooserDemo());
        frame.pack();
        frame.setVisible(true);   
		
	}
	
	private static ObjectOutputStream ObjectOutputStream(
			FileOutputStream saveFile) {
		// TODO Auto-generated method stub
		return null;
	}


	// Format edges changes the edge color based on weight.
	public static void formatEdges(Set<DefaultWeightedEdge> edges)
	{
		ColorGradient cg = new ColorGradient();
		AttributeMap cellAttr = new AttributeMap();
		
		// figure out maximum size
		Iterator<DefaultWeightedEdge> it = edges.iterator();
		while (it.hasNext()) {
		    // Get element			
		    DefaultWeightedEdge edge = it.next();
		    
		    // TODO code here to be able to parameterize max and min of weights for coloring function 
		    double weight = g.getEdgeWeight(edge);
		}
		
		Iterator<DefaultWeightedEdge> it2 = edges.iterator();
		while (it2.hasNext()) {
		    // Get element			
		    DefaultWeightedEdge edge = it2.next();
		    
		    int weight = (int)g.getEdgeWeight(edge);
		    
		    DefaultEdge jgraphEdge= jgAdapter.getEdgeCell(edge);
		    
		    AttributeMap attr = jgraphEdge.getAttributes();
		    //System.out.println("weight read in is: " + weight);
		    GraphConstants.setLineColor(attr, cg.getColor(weight, 0, 10));
		    GraphConstants.setLabelEnabled(attr,false);
		    cellAttr.put(edge, attr);
		}
		
		
		jgAdapter.edit(cellAttr, null, null, null);
	}



static void getNodeStatistics(SimpleWeightedGraph<String,DefaultWeightedEdge> g) throws IOException
{
    FileWriter writer = new FileWriter("NodeStatsByFunction.csv");
    writer.append("Funciton Name");
    writer.append(',');
    writer.append("Number of functions sharing data type with (number of edges)");
    writer.append(',');
    writer.append("total number of common data types between functions (weight)");
    writer.append(',');
    writer.append("Average number of shared types per function");    
    writer.append(',');
    writer.append("Maximum edge weight");       
    writer.append('\n');
	
	Set<String> vertexSet = g.vertexSet();
	Iterator iter = vertexSet.iterator();
    while (iter.hasNext()) {
      String vertex = (String)iter.next();
      
      Set<DefaultWeightedEdge> edgeSet = g.edgesOf(vertex);
      Iterator edgeIter = edgeSet.iterator();
      int edgeWeightSum = 0;
      int maxEdgeWeight = 0;
      while(edgeIter.hasNext())
      {
    	  DefaultWeightedEdge edge = (DefaultWeightedEdge)edgeIter.next();  
    	  double individualEdgeWeight = g.getEdgeWeight(edge);
    	  edgeWeightSum += individualEdgeWeight;
    	  
    	  if(individualEdgeWeight > maxEdgeWeight)
    	  {
    		  maxEdgeWeight = (int)individualEdgeWeight;
    	  }
      }
      //System.out.println("Vertex is: " + vertex + " number of edges: " + edgeSet.size() );
      writer.append(vertex); //name
      writer.append(',');
      writer.append(Integer.toString(edgeSet.size())); // number of edges (number of functions sharing data types
      writer.append(',');
      writer.append(Integer.toString(edgeWeightSum));  // total edge weight
      writer.append(',');
      
      if(edgeSet.size() != 0)
      {
    	  writer.append(Double.toString((double)edgeWeightSum/(double)edgeSet.size())); // average
      }
      else
      {
    	  writer.append("0"); // average
      }
      writer.append(',');
      writer.append(Integer.toString(maxEdgeWeight));  // total edge weight
      
      writer.append('\n');
      

    }
    writer.flush();
	writer.close();
}

}
