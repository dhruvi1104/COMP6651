import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class data_generator {
	
	public static int random_node(Set<Integer> V) {
		int size = V.size();
		int item = new Random().nextInt(size);
		int j = 0;
		for(int obj : V){
		    if (j == item)
		        return obj;
		    j++;
		}
		return -1;
	}
	
	public static int random_int(int min, int max) {
	    Random r = new Random();
	    return r.nextInt((max - min) + 1) + min;
	}
	
	public static void data_generate(int n_bs, int Vx, int no_edges, String file_name1, int n_avail_bikes, String file_name2) throws FileNotFoundException, UnsupportedEncodingException {

		PrintWriter writer1 = new PrintWriter("D:\\Concordia\\COMP6651\\Generated_data\\"+file_name1, "UTF-8");
		PrintWriter writer2 = new PrintWriter("D:\\Concordia\\COMP6651\\Generated_data\\"+file_name2, "UTF-8");
		int total_nodes = n_bs + Vx + 1; // line 1 of file
		Set<Integer> V = new HashSet<Integer>();
		for(int i=0;i<total_nodes;i++) {
			V.add(i);
		}
		
		writer1.println(total_nodes+" "+no_edges);
		writer1.println(n_bs);
		
		Set<Integer> V_S = new HashSet<Integer>();
		Set<Integer> V_X = new HashSet<Integer>(V);
		for(int i=0;i<n_bs;i++) {
			int random_node = random_node(V_X);
			V_X.remove(random_node);
			V_S.add(random_node);
		}
		int depot = random_node(V_X);
		V_X.remove(depot);
		writer2.println(n_avail_bikes);
		int[][] bike_distr = new int[V_S.size()][2];
		int x=0;
		for(int n : V_S) {
			bike_distr[x][0] = n;
			if(n_avail_bikes>10) {
				bike_distr[x][1] = random_int(0,10);
			}else {
				bike_distr[x][1] = random_int(0,n_avail_bikes);
			}
			n_avail_bikes = n_avail_bikes-bike_distr[x][1];
			x++;
		}
		while(n_avail_bikes>0) {
			x=random_int(0,V_S.size()-1);
			int y=0;
			if(bike_distr[x][1]<10) {
				if(n_avail_bikes<10) {
					y = random_int(0,Math.min(10-bike_distr[x][1],n_avail_bikes));
				}else {
					y = random_int(0,10-bike_distr[x][1]);
				}
				bike_distr[x][1] = bike_distr[x][1] + y;
				n_avail_bikes = n_avail_bikes-y;
			}
		}
		
		for(int i=0;i<V_S.size();i++) {
			writer2.println(bike_distr[i][0]+" "+bike_distr[i][1]);
		}
		
		int flag=0;
		for(int n : V_S) {
			if(flag==0) {
				writer1.print(n);
				flag=1;
			}else {
				writer1.print(" "+n);
			}
		}
		writer1.println();
		
		Set<Integer> not_connected_V = new HashSet<Integer>(V);
		int[][] edges = new int[no_edges][3];
		for(int i=0;i<no_edges;i++) {
			Set<Integer> V_temp = new HashSet<Integer>(V);
			
			edges[i][0] = random_node(V);
			not_connected_V.remove(edges[i][0]);
			V_temp.remove(edges[i][0]);
			
			if(not_connected_V.size()!=0)
				edges[i][1] = random_node(not_connected_V);
			else
				edges[i][1] = random_node(V_temp);
			not_connected_V.remove(edges[i][1]);
			
			edges[i][2] = random_int(1,100);
		}
		
		Set<Integer> start_V = new HashSet<Integer>();
		for(int i=0;i<no_edges;i++) {
			if(!start_V.contains(edges[i][0])) {
				start_V.add(edges[i][0]);
			}
		}
		int no_lines = start_V.size();//line 4 of file
		
		writer1.println(no_lines);
		int j=0;
		for (int node : start_V) {
			writer1.print(node);
			for(int i=0;i<no_edges;i++) {
				if(node==edges[i][0]) {
					writer1.print(" ("+edges[i][1]+" "+edges[i][2]+")");
				}
			}
			j++;
			if(j!=start_V.size()) {
				writer1.println();
			}
		}
		writer1.close();
		writer2.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		int n_avail_bikes = 60;
		int n_bs = 10;//line 2 of file(10)
		int Vx = 20;//(20)
		int no_edges = 70; //line 1 of file(70)
		int nV = 100;
		int CAP = 10;
		String file_name1,file_name2 = null;
		
		for(int i=1;i<11;i++) {
			file_name1 = "topology_"+i+".txt";
			file_name2 = "bike_data_"+i+".txt";
			data_generate(n_bs, Vx, no_edges, file_name1, n_avail_bikes, file_name2);
		}
	}
}
