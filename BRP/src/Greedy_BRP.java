import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Greedy_BRP {

	public static void main(String[] args) throws IOException {

		Scanner scanner1 = new Scanner(new File("D:/Concordia/COMP6651/Generated_data/bike_data_1.txt"));
		Scanner scanner2 = new Scanner(new File("D:/Concordia/COMP6651/Generated_data/topology_1.txt"));
		
		int delta = 480;//min(8 hours)
		
		int no_nodes = scanner2.nextInt();
		int no_links = scanner2.nextInt();
		int n_BS = scanner2.nextInt();
		Set<Integer> V_S = new HashSet<Integer>();
		for(int i=0;i<n_BS;i++) {
			V_S.add(scanner2.nextInt());
		}
		int n = scanner2.nextInt();
		int depot=0;
		int graph[][] = new int[no_nodes][no_nodes];
		int flag=0;
		for(int i=0;i<n;i++) {
			int start = scanner2.nextInt();
			if(!V_S.contains(start)&&flag==0) {
				depot=start;
				flag=1;
			}
			String x = scanner2.nextLine();
			x=x.trim().replace(") (", ",").replace(")", "").replace("(", "").replace(" ", ",");
			char[] chars = x.toCharArray();
			for(int j=0;j<chars.length;j++) {
				String r = "";
				while(chars[j]!=',') {
					String ss = Character.toString(chars[j]);
					r = r.concat(ss);
					j++;
				}
				j++;
				int end = Integer.parseInt(r);
				r="";
				while(chars[j]!=',') {
					String ss = Character.toString(chars[j]);
					r = r.concat(ss);
					if(j==chars.length-1) {
						break;
					}
					j++;
				}
				int dis = Integer.parseInt(r);
				graph[start][end] = dis;
				graph[end][start] = dis;
			}			
		}
		HashMap<Integer, Integer> bikes_per_station = new HashMap<Integer, Integer>();
		scanner1.nextInt();
		for(int i=0;i<n_BS;i++) {
			int x = scanner1.nextInt();
			int y = scanner1.nextInt();
			bikes_per_station.put(x,y);
		}
		
		//*****Dijkstra Algorithm************
		long startTime = System.nanoTime();
		int d[] = new int[no_nodes];
		Boolean included_node[] = new Boolean[no_nodes];
		
		for(int i=0;i<no_nodes;i++) {
			d[i] = Integer.MAX_VALUE;
			included_node[i] = false;
		}
		d[depot] = 0;
		
		for(int i=0;i<no_nodes-1;i++) {
			
			int x = Integer.MAX_VALUE;
			int y = -1;
			for(int j=0;j<no_nodes;j++) {
				if(included_node[j]==false&&d[j]<=x) {
					x=d[j];
					y=j;
				}
			}
			included_node[y] = true;
			for(int j=0;j<no_nodes;j++) {
				if(!included_node[j] && graph[y][j]!=0 && d[y]!=Integer.MAX_VALUE && d[y]+graph[y][j]<d[j]) {
					d[j] = d[y] + graph[y][j];
				}
			}
		}
		
		int d1[] = new int[n_BS];
		int bs[] = new int[n_BS];
		int bike[] = new int[n_BS];
		int com = -1;
		int j = 0;
		for(int i=0;i<no_nodes;i++) {
			if(V_S.contains(i)) {
				d1[j] = d[i];
				bs[j] = i;
				bike[j] = bikes_per_station.get(i);
				j++;
			}
		}
		for(int i=0;i<n_BS;i++) {
			for(j=i;j<n_BS;j++) {
				if(d1[i]>d1[j]) {
					int x = d1[i];
					d1[i] = d1[j];
					d1[j] = x;
					
					int y = bs[i];
					bs[i] = bs[j];
					bs[j] = y;
					
					int z = bike[i];
					bike[i] = bike[j];
					bike[j] = z;
				}
			}
		}	
		//huristic_algo
		int v = 1;
		int cap_v = 10;
		int dis_v = 0;
		for(int i=0;i<d1.length;i++) {
			if(bike[i]==5) {
			}else {
				if(dis_v+(2*d1[i])<=delta && cap_v != 0) {
					dis_v = dis_v + (2*d1[i]);
					int required_bike = 5-bike[i];
					if(cap_v<=required_bike) {
						bike[i] = bike[i] + cap_v;
						required_bike = required_bike - cap_v;
						cap_v = 0;
					}else if(cap_v>required_bike) {
						cap_v = cap_v - required_bike;
						required_bike = 0;
						bike[i] = bike[i] + required_bike;
					}
				}else if(2*d1[i]>delta){
				}else {
					v = v + 1;
					dis_v = 0;
					cap_v=10;
					i--;
				}
			}
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime)/1000; 
		System.out.println("Number of vehical : "+v);
		System.out.println("Computational time : "+duration+" micro seconds");
	}
}
