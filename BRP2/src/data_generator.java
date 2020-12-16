import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class data_generator {

	public static int random_int(int min, int max) {
	    Random r = new Random();
	    return r.nextInt((max - min) + 1) + min;
	}
	
	public static int random_v(int[][] intersection, int random_u) {
		int random_v = -1;
		for(int j=0;j<143;j++) {
			if(intersection[random_u][j] != -1) {
				random_v = j;
				break;
			}
		}
		return random_v;
	}
	
	public static void data_generate(int n_bs, int Vx, int no_arcs, String file_name1, int n_avail_bikes, String file_name2) throws FileNotFoundException, UnsupportedEncodingException {
		
		PrintWriter writer1 = new PrintWriter("D:\\Concordia\\COMP6651\\Project#2\\Generated_data\\"+file_name1, "UTF-8");
		PrintWriter writer2 = new PrintWriter("D:\\Concordia\\COMP6651\\Project#2\\Generated_data\\"+file_name2, "UTF-8");
		
		int total_nodes = n_bs + Vx + 1;//143(street intersection)+90(bike station)+1(depot)
		
		writer1.println(total_nodes+" "+no_arcs);
		writer1.println(n_bs);
		
		writer2.println(n_avail_bikes);
		
		int[][] intersection = new int[total_nodes][total_nodes];
		for(int i=0;i<total_nodes;i++) {
			for(int j=0;j<total_nodes;j++) {
				intersection[i][j] = -1;
			}
		}
		//131=143-last line of street intersection
		int count = 0;
		for(int i=0;i<131;i++) {
			if(count==10) {
				count=0;
				i++;
			}
			intersection[i][i+1] = random_int(2,25);
			intersection[i+1][i] = intersection[i][i+1];
			intersection[i][i+11] = random_int(2,25);
			intersection[i+11][i] = intersection[i][i+11];
			count++;
			
		}
		/*
		int f1=0;
		for(int i=0;i<143;i++) {
			for(int j=0;j<143;j++) {
				if(intersection[i][j] != -1) {
					System.out.println(i+" to "+j);
					f1++;
				}
			}
		}
		System.out.println(f1);
		System.out.println("==========================================================================");
		*/
		
		int total_arcs = 524;
		//System.out.println(no_arcs);
		while(total_arcs != no_arcs) {
			int random_u = random_int(0,Vx-1);
			int random_v = random_v(intersection,random_u);
			if(random_v == -1) {
				continue;
			}
			intersection[random_u][random_v] = -1;
			List<Integer> open_list = new ArrayList<Integer>();
			if(findPath(intersection, random_u, random_v, open_list)) {
				if(findPath(intersection, random_v, random_u, open_list)) {
					total_arcs--;
				}else {
					intersection[random_u][random_v] = random_int(2,25);
				}
			}else {
				intersection[random_u][random_v] = random_int(2,25);
			}
		}
		/*
		int f2=0;
		for(int i=0;i<143;i++) {
			for(int j=0;j<143;j++) {
				if(intersection[i][j] != -1) {
					System.out.println(i+" to "+j);
					f2++;
				}
			}
		}
		System.out.println(f2);
		*/
		
		//For bike station between street intersection
		Set<Integer> V_S = new HashSet<Integer>();
		for(int i=Vx;i<Vx+n_bs;i++) {
			int random_u = random_int(0,Vx-1);
			int random_v = random_v(intersection,random_u);
			while(random_v == -1) {
				random_u = random_int(0,Vx-1);
				random_v = random_v(intersection,random_u);
			}
			intersection[random_u][i]=random_int(1,intersection[random_u][random_v]-1);
			intersection[i][random_v]=intersection[random_u][random_v]-intersection[random_u][i];
			if(intersection[random_v][random_u] != -1) {
				intersection[random_v][i] = intersection[i][random_v];
				intersection[i][random_u] = intersection[random_u][i];
			}
			intersection[random_u][random_v] = -1;
			intersection[random_v][random_u] = -1;
			V_S.add(i);
			//System.out.println(V_S);
		}
		//bike station finish
		//for depot(last index of array(233rd) will be depot)
		int random_u = random_int(0,Vx-1);
		int random_v = random_v(intersection,random_u);
		while(random_v == -1) {
			random_u = random_int(0,Vx-1);
			random_v = random_v(intersection,random_u);
		}
		intersection[random_u][total_nodes-1]=random_int(1,intersection[random_u][random_v]-1);
		intersection[total_nodes-1][random_v]=intersection[random_u][random_v]-intersection[random_u][total_nodes-1];
		if(intersection[random_v][random_u] != -1) {
			intersection[random_v][total_nodes-1] = intersection[total_nodes-1][random_v];
			intersection[total_nodes-1][random_u] = intersection[random_u][total_nodes-1];
		}
		intersection[random_u][random_v] = -1;
		intersection[random_v][random_u] = -1;
		//depot finish
		
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
		
		//for all starting nodes
		Set<Integer> start_V = new HashSet<Integer>();
		for(int i=0;i<total_nodes;i++) {
			for(int j=0;j<total_nodes;j++) {
				if(intersection[i][j] != -1) {
					if(!start_V.contains(i)) {
						start_V.add(i);
					}
				}
			}
		}
		//System.out.println(start_V);
		
		int no_lines = start_V.size();//number of lines to read
		writer1.println(no_lines);
		
		int k=0;
		for (int i : start_V) {
			writer1.print(i);
			for(int j=0;j<total_nodes;j++) {
				if(intersection[i][j] != -1) {
					writer1.print(" ("+j+" "+intersection[i][j]+")");
				}
			}
			k++;
			if(k!=start_V.size()) {
				writer1.println();
			}
		}
		
		//bike distribution
		int[][] bike_distr = new int[V_S.size()][2];
		int x=0;
		for(int n : V_S) {
			bike_distr[x][0] = n;
			if(n_avail_bikes>8) {
				bike_distr[x][1] = random_int(0,8);
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
		
		writer1.close();
		writer2.close();
	}
	
	public static boolean findPath(int[][] intersection, int start, int goal, List open_list) {
		open_list.add(start);
		if(intersection[start][goal]!=-1) {
			return true;
		}else {
			for(int j=0;j<143;j++) {
				if(!open_list.contains(j)) {
					if(intersection[start][j]!=-1) {
						if(findPath(intersection,j,goal,open_list)) {
							return true;
						}else {
							return false;
						}
					}
				}
			}
		}
		
		return false;
		
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		int n_avail_bikes = 380;//total bikes
		int n_bs = 90;//number of bike stations
		int Vx = 143;//street intersection(11*13)
		int no_arcs = 460;//(int) (524*0.6); //number of total arcs before adding bike stations
		int nV = 100;
		int CAP = 10;
		String file_name1,file_name2 = null;
		
		for(int i=1;i<11;i++) {
			file_name1 = "topology_"+i+".txt";
			file_name2 = "bike_data_"+i+".txt";
			data_generate(n_bs, Vx, no_arcs, file_name1, n_avail_bikes, file_name2);
		}

	}

}
