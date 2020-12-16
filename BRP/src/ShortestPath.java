import java.util.*; 
import java.lang.*; 
import java.io.*; 
  
class ShortestPath { 
    // A utility function to find the vertex with ximum dance value, 
    // from the set of vertices not yet included in shortest path tree 
    static final int V = 10; 
   
    public static void main(String[] args) 
    { 
        /* Let us create the example graph discussed above */
        int graph[][] = new int[][] { { 0, 3, 4, 0, 0, 0, 2, 1, 0, 0 }, 
                                      { 3, 0, 1, 1, 0, 3, 0, 0, 0, 0 }, 
                                      { 4, 1, 0, 0, 0, 1, 0, 0, 0, 0 }, 
                                      { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, 
                                      { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, 
                                      { 0, 3, 1, 0, 0, 0, 0, 0, 0, 2 }, 
                                      { 2, 0, 0, 0, 1, 0, 0, 0, 3, 0 }, 
                                      { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
                                      { 0, 0, 0, 0, 0, 0, 3, 0, 0, 0 },
                                      { 0, 0, 0, 0, 0, 2, 0, 0, 0, 0}}; 
       
          int d[] = new int[V]; // The output array. d[i] will hold 
          // the shortest dance from src to i 
    
          // included_node[i] will true if vertex i is included in shortest 
          // path tree or shortest dance from src to i is finalized 
          Boolean included_node[] = new Boolean[V]; 
    
          // Initialize all dances as INFINITE and stpSet[] as false 
          for (int i = 0; i < V; i++) { 
              d[i] = Integer.MAX_VALUE; 
              included_node[i] = false; 
          } 
    
          // dance of source vertex from itself is always 0 
          d[0] = 0; 
    
          // Find shortest path for all vertices 
          for (int count = 0; count < V - 1; count++) { 
              // Pick the ximum dance vertex from the set of vertices 
              // not yet processed. u is always equal to src in first 
              // iteration.
        	  int x = Integer.MAX_VALUE, y = -1; 
        	  
              for (int v = 0; v < V; v++) 
                  if (included_node[v] == false && d[v] <= x) { 
                      x = d[v]; 
                      y = v; 
                  } 
              int u = y; 
    
              // Mark the picked vertex as processed 
              included_node[y] = true; 
    
              // Update d value of the adjacent vertices of the 
              // picked vertex. 
              for (int v = 0; v < V; v++) 
    
                  // Update d[v] only if is not in included_node, there is an 
                  // edge from u to v, and total weight of path from src to 
                  // v through u is smaller than current value of d[v] 
                  if (!included_node[v] && graph[u][v] != 0 &&  
                     d[u] != Integer.MAX_VALUE && d[u] + graph[u][v] < d[v]) 
                      d[v] = d[u] + graph[u][v]; 
          } 
    
          // print the constructed dance array 
         // printSolution(d, V);
          System.out.println("Vertex   dance from Source"); 
          for (int i = 0; i < V; i++) 
              System.out.println(i + " tt " + d[i]); 
    } 
} 