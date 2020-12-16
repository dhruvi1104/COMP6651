import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Greedy_BRP2 {

	public static void main(String[] args) throws FileNotFoundException {
		
		Scanner scanner1 = new Scanner(new File("D:/Concordia/COMP6651/Project#2/Generated_data/bike_data_1.txt"));
		Scanner scanner2 = new Scanner(new File("D:/Concordia/COMP6651/Project#2/Generated_data/topology_1.txt"));
		
		int delta = 480;//min(8 hours)
		
		int no_nodes = scanner2.nextInt();
		int no_links = scanner2.nextInt();
		int n_BS = scanner2.nextInt();
		Set<Integer> V_S = new HashSet<Integer>();
		for(int i=0;i<n_BS;i++) {
			V_S.add(scanner2.nextInt());
		}
		int n = scanner2.nextInt();
		int depot=233;
		int graph[][] = new int[no_nodes][no_nodes];
		//int flag=0;
		for(int i=0;i<n;i++) {
			int start = scanner2.nextInt();
			/*if(!V_S.contains(start)&&flag==0) {
				depot=start;
				flag=1;
			}*/
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
		//System.out.println(bikes_per_station);
		
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
		/*for(int i=0;i<no_nodes;i++) {
			System.out.println(i+" "+d[i]);
		}*/
		int d1[] = new int[n_BS];
		int bs[] = new int[n_BS];
		int bike[] = new int[n_BS];
		int j = 0;
		for(int i=0;i<no_nodes;i++) {
			if(V_S.contains(i)) {
				d1[j] = d[i];
				bs[j] = i;
				bike[j] = bikes_per_station.get(i);
				j++;
			}
		}
		/*for(int i=0;i<n_BS;i++) {
			System.out.println(bs[i]+" "+d1[i]);
		}*/	
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
		/*for(int i=0;i<n_BS;i++) {
			System.out.println(bs[i]+" "+d1[i]);
		}*/
		
		//huristic_algo
		int v = 1;
		int cap_v = 0;
		int cap_depot = 0;
		int dis_v = 0;
		int depot_bike = 0;
		int flag=0;
		int r=0;
		Set<Integer> not_visited_bs = new HashSet<Integer>();
		not_visited_bs.addAll(V_S);
		for(int i=0;i<d1.length;i++) {
			if(not_visited_bs.contains(bs[i])) {
				if(bike[i]==5) {
				}else {
					int dis_l = d1[i];
					int l = i;
					while(dis_v+dis_l+d1[l]<=delta&&!not_visited_bs.isEmpty()) {
						int requirement = 5-bike[l];
						if(requirement>0&&cap_v==0) {
							//skip this station
							break;
						}else if(requirement<0&&cap_v==10) {
							//skip this station
							break;
						}else {
							//at bike station l
							flag++;
							dis_v = dis_v + dis_l;
							if(cap_v<=requirement) {
								bike[l] = bike[l] + cap_v;
								requirement = requirement - cap_v;
								r=r+cap_v;
								cap_v = 0;
							}else if(cap_v>requirement) {
								cap_v = cap_v - requirement;
								r=r-requirement;
								requirement = 0;
								not_visited_bs.remove(bs[l]);//only when requirement is 0 remove bs from to be visiting list
								bike[l] = bike[l] + requirement;
							}
							//find next nearest bike station with index l
							int[] bs_dis = new int[2];
							bs_dis = nearest_bs(bs[l], no_nodes, n_BS, graph, V_S);
							int bs_l = bs_dis[0];//nearest_bs from current bs
							for(int h=0;h<n_BS;h++) {
								if(bs_l==bs[h]) {
									l=h;//index of bs
								}
							}
							dis_l = bs_dis[1];//distance from current bs to l
						}
					}
					if(dis_v+dis_l+d1[l]>delta) {
						//return to depot from current_bs
						dis_v = 0;
						if(cap_v>0) {
							depot_bike = depot_bike + cap_v;
						}
						System.out.println("r : "+r);
						System.out.println("bs : "+flag);
						flag=0;
						r=0;
						v = v + 1;
						System.out.println("new vehical : "+v);
						if(depot_bike>5) {
							cap_v = depot_bike-5;
						}
					}
				}
			}
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime)/1000; 
		System.out.println("Number of vehical : "+v);
		System.out.println("Computational time : "+duration+" micro seconds");
		
	}
	
	public static int[] nearest_bs(int depot, int no_nodes, int n_BS, int[][] graph, Set<Integer> V_S) {
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
		
		int d1[] = new int[n_BS-1];
		int bs[] = new int[n_BS-1];
		int j = 0;
		V_S.remove(depot);
	//	System.out.println(V_S);
		for(int i=0;i<no_nodes-1;i++) {
			if(V_S.contains(i)) {
				d1[j] = d[i];
				bs[j] = i;
				j++;
			}
		}
		/*for(int i=0;i<n_BS-1;i++) {
			System.out.println(bs[i]+" "+d1[i]);
		}
		System.out.println(bs[88]);*/
		for(int i=0;i<n_BS-1&&d1[i]!=0;i++) {
			for(j=i;j<n_BS-1&&d1[j]!=0;j++) {
				if(d1[i]>d1[j]) {
					int x = d1[i];
					d1[i] = d1[j];
					d1[j] = x;
					
					int y = bs[i];
					bs[i] = bs[j];
					bs[j] = y;
				}
			}
		}
		/*System.out.println("-------------------------------------------");
		for(int i=0;i<n_BS-1&&d1[i]!=0;i++) {
			System.out.println(bs[i]+" "+d1[i]);
		}*/
		int[] reply = new int[2];
		reply[0] = bs[0];
		reply[1] = d1[0];
		return reply;
	}

}
