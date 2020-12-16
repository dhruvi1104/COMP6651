import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Greedy_BRP3 implements Cloneable{

	static HashMap<Integer, Integer> bikes_per_station = new HashMap<Integer, Integer>();
	static Set<Integer> excess = new HashSet<Integer>();
	static Set<Integer> defict = new HashSet<Integer>();
	static Set<Integer> satisfied = new HashSet<Integer>();
	static int depot=233;
	static int no_nodes;
	static int delta;
	static int no_links;
	static int n_BS;
	static int total_veh;
	static int index;
	static Set<Integer> V_S = new HashSet<Integer>();
	static HashMap<Integer,Set<Integer>> result = new HashMap<Integer,Set<Integer>>();
	static int graph[][];
	
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		
		Scanner scanner1 = new Scanner(new File("D:/Concordia/COMP6651/Project#2/Generated_data/bike_data_1.txt"));
		Scanner scanner2 = new Scanner(new File("D:/Concordia/COMP6651/Project#2/Generated_data/topology_1.txt"));
		
		delta = 480;//min(8 hours)
		
		no_nodes = scanner2.nextInt();
		no_links = scanner2.nextInt();
		n_BS = scanner2.nextInt();
		for(int i=0;i<n_BS;i++) {
			V_S.add(scanner2.nextInt());
		}
		int n = scanner2.nextInt();
		
		graph = new int[no_nodes][no_nodes];
		for(int i=0;i<n;i++) {
			int start = scanner2.nextInt();
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
		
		scanner1.nextInt();
		for(int i=0;i<n_BS;i++) {
			int x = scanner1.nextInt();
			int y = scanner1.nextInt();
			bikes_per_station.put(x,y);
		}
		
		for(int x : V_S) {
			if(bikes_per_station.get(x)>7.5) {//10*(3/4)->7.5
				excess.add(x);
			}else if(bikes_per_station.get(x)<2.5) {//10*(1/4)->2.5
				defict.add(x);
			}else {
				satisfied.add(x);
			}
		}
		
		long startTime = System.nanoTime();
		int est_veh;
		est_veh = estimate_vehicle(graph,V_S);
		System.out.println(est_veh);
		
		//clusters
		Set<Integer> new_V_S = new HashSet<Integer>();
		new_V_S.addAll(defict);
		new_V_S.addAll(excess);
		
		result = kmeans(graph, new_V_S, est_veh);
		System.out.println(result);
		
		//greedy algo
		ExecutorService es = Executors.newCachedThreadPool();
		for(int x : result.keySet()) {
		    es.execute(new Runnable() {
				@Override
				public void run() {
					index=tour(result.get(x));
				} });
		}
		es.shutdown();
		boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime)/1000; 
		System.out.println("Number of vehical : "+total_veh);
		System.out.println("Computational time : "+duration+" micro seconds");
	}
	
	public static int tour(Set<Integer> bs_centroid) {
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
		int j = 0;
		for(int i=0;i<no_nodes;i++) {
			if(bs_centroid.contains(i)) {
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
		int v = 1;
		int cap_v = 0;
		int dis_v = 0;
		int depot_bike = 0;
		int flag=0;
		int r=0;
		Set<Integer> visited_bs = new HashSet<Integer>();
		for(int i=0;i<d1.length;i++) {
			if(!visited_bs.contains(bs[i])) {
				if(bike[i]==5) {
				}else {
					int dis_l = d1[i];
					int l = i;
					while(dis_v+dis_l+d1[l]<=delta&&visited_bs.size()!=bs_centroid.size()) {
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
								visited_bs.add(bs[l]);//only when requirement is 0 remove bs from to be visiting list
								bike[l] = bike[l] + requirement;
							}
							//find next nearest bike station with index l
							int[] bs_dis = new int[2];
							bs_dis = nearest_bs(bs[l], no_nodes, n_BS, graph, bs_centroid, visited_bs);
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
						flag=0;
						r=0;
						v = v + 1;
						if(depot_bike>5) {
							cap_v = depot_bike-5;
						}
					}
				}
			}
		}
		total_veh = total_veh+v;
		return v;
	}
	
	public static HashMap<Integer,Set<Integer>> kmeans(int[][] graph, Set<Integer> V_S, int est_veh) {
		
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
		int d1[] = new int[V_S.size()];
		int bs[] = new int[V_S.size()];
		int j = 0;
		for(int i=0;i<no_nodes;i++) {
			if(V_S.contains(i)) {
				d1[j] = d[i];
				bs[j] = i;
				j++;
			}
		}
		
		HashMap<Integer,Integer> cluster_data = new HashMap<Integer,Integer>();//data of all bike station with distance to calculate clusters
		int[] centroid = new int[est_veh];//bike station and distance to represent the centroid
		int[] new_centroid = new int[est_veh];//new cluster after calculation to compare with previous one
		Set<Integer> clusters_temp = new HashSet<Integer>();//index of cluster - temporary
		HashMap<Integer,HashMap<Integer,Integer>> data = new HashMap<Integer,HashMap<Integer,Integer>>();//to check the cluster of each bike station
		HashMap<Integer,Set<Integer>> result = new HashMap<Integer,Set<Integer>>();//to store final cluster
		
		for(int i=0;i<V_S.size();i++) {
			cluster_data.put(bs[i], d1[i]);
		}	
		int f=0;
		while(f!=est_veh) {
			int x = new Random().nextInt(d1.length);
			while(clusters_temp.contains(x)) {
				x = new Random().nextInt(d1.length);
			}
			clusters_temp.add(x);
			centroid[f]=d1[x];
			f++;
		}
		
		while(true) {
			result.clear();
			for(int x : cluster_data.keySet()) {
				int val = cluster_data.get(x);
				HashMap<Integer,Integer> temp = new HashMap<Integer,Integer>();
				for(int y : centroid) {
					if(val>y) {
						temp.put(y, val-y);
					}else {
						temp.put(y, y-val);
					}
				}
				data.put(x, temp);
			}
			for(int x : centroid) {
				Set<Integer> t = new HashSet<Integer>();
				result.put(x, t);
			}
			//143={208=14, 166=33, 169=20, 219=40}
			for(int x : data.keySet()) {//x->143
				HashMap<Integer,Integer> temp = data.get(x);
				int val=Integer.MAX_VALUE;
				int clust=0;
				for(int y : temp.keySet()) {//y->208
					if(temp.get(y)<val) {
						val=temp.get(y);
						clust=y;
					}
				}
				result.get(clust).add(x);
			}	
			//finding mean as new centroid
			int z=0;
			for(int x : result.keySet()) {
				int sum=0;
				Set<Integer> t = result.get(x);
				for(int y : t) {
					sum=sum+cluster_data.get(y);
				}
				int new_x = sum/t.size();
				new_centroid[z]=new_x;
				z++;
			}
			if(!Arrays.equals(centroid, new_centroid)) {
				centroid=Arrays.copyOf(new_centroid, new_centroid.length);
			}else {
				break;
			}
		}
		
		return result;
	}
	
	public static int estimate_vehicle(int[][] graph, Set<Integer> V_S){
		
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
		int j = 0;
		for(int i=0;i<no_nodes;i++) {
			if(V_S.contains(i)) {
				d1[j] = d[i];
				bs[j] = i;
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
				}
			}
		}
		
		int v = 1;
		int dis_v = 0;
		int flag=0;
		int r=0;
		Set<Integer> visited_bs = new HashSet<Integer>();
		
		for(int i=0;i<d1.length;i++) {
			if(!visited_bs.contains(bs[i])) {
				int dis_l = d1[i];
				int l = i;
				while(dis_v+dis_l+d1[l]<=delta&&visited_bs.size()!=V_S.size()) {
					//at bike station l
					flag++;
					dis_v = dis_v + dis_l;
					visited_bs.add(bs[l]);
					
					//find next nearest bike station with index l
					int[] bs_dis = new int[2];
					bs_dis = nearest_bs(bs[l], no_nodes, n_BS, graph, V_S, visited_bs);
					int bs_l = bs_dis[0];//nearest_bs from current bs
					for(int h=0;h<n_BS;h++) {
						if(bs_l==bs[h]) {
							l=h;//index of bs
						}
					}
					dis_l = bs_dis[1];//distance from current bs to l
				}
				if(dis_v+dis_l+d1[l]>delta) {
					//return to depot from current_bs
					dis_v = 0;
					flag=0;
					r=0;
					v = v + 1;
				}
			}
		}
		return v;
	}

	public static int[] nearest_bs(int depot, int no_nodes, int n_BS, int[][] graph, Set<Integer> V_S, Set<Integer> visited_bs) {
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
		for(int i=0;i<no_nodes-1;i++) {
			if(V_S.contains(i)&&!visited_bs.contains(i)) {
				d1[j] = d[i];
				bs[j] = i;
				j++;
			}
		}
		V_S.add(depot);
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
		int[] reply = new int[2];
		reply[0] = bs[0];
		reply[1] = d1[0];
		return reply;
	}
}

