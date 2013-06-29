package GraphDisplay;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.organic.*;

public class GraphReader {

	private static JGraphModelAdapter<String, DefaultWeightedEdge> jgAdapter;
	private static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g;
	
	public static void main(String[] args) throws IOException {
		System.out.println("Hello, launching John's graph analysis tool");
		
		GraphBuilder gb = new GraphBuilder();
		g = gb.graphFromFile();
		
		// create a visualization using JGraph, via the adapter
        jgAdapter = new JGraphModelAdapter<String, DefaultWeightedEdge>(g);
        
        JGraph jgraph = new JGraph(jgAdapter);
        
        formatEdges(g.edgeSet());
        
        JGraphFacade facade = new JGraphFacade(jgraph); // Pass the facade the JGraph instance
        
        JGraphFastOrganicLayout organicLayout = new JGraphFastOrganicLayout();
        organicLayout.setForceConstant(200);
        JGraphLayout layout = organicLayout; // Create an instance of the appropriate layout
        
        layout.run(facade); // Run the layout on the facade. Note that layouts do not implement the Runnable interface, to avoid confusion
        Map nested = facade.createNestedMap(true, true); // Obtain a map of the resulting attribute changes from the facade
        jgraph.getGraphLayoutCache().edit(nested); // Apply the results to the actual graph
        
      
        //Create a JPanel  
        JPanel panel=new JPanel();
        panel.add(jgraph);  // add graph to panel
        
        //Create a scrollbar using JScrollPane and add panel into it's viewport  
        //Set vertical and horizontal scrollbar always show  
        JScrollPane scrollBar=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  

        JFrame frame = new JFrame("JChargo Graph Analysis R1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().add(jgraph);
       
        frame.add(scrollBar);   
        frame.pack();
        frame.setVisible(true);   
		
	}

	
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
}
