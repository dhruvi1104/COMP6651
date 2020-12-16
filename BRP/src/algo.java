
public class algo {

	public static void main(String[] args) {

		int d[] = {1,3,4,4,5};
		int bs[] = {7,1,2,3,5};
		int bike[] = {0,3,4,6,1};
		int delta = 8;
		
		int v = 1;
		int cap_v = 10;
		int dis_v = 0;
		
		for(int i=0;i<d.length;i++) {
			System.out.println("d[i] : "+d[i]);
			System.out.println("bike[i] : "+bike[i]);
			System.out.println("bs[i] : "+bs[i]);
			if(bike[i]==5) {
				System.out.println("----In first if");
				//break;
			}else {
				System.out.println("----In first else");
				System.out.println("dis_v:"+dis_v);
				System.out.println("cap_v:"+cap_v);
				if(dis_v+(2*d[i])<=delta && cap_v != 0) {
					System.out.println("----In second if");
					dis_v = dis_v + (2*d[i]);
					System.out.println("Total distance for vehical : "+dis_v);
					int required_bike = 5-bike[i];
					System.out.println("Bikes required : "+required_bike);
					if(cap_v<=required_bike) {
						System.out.println("----in first case");
						bike[i] = bike[i] + cap_v;
						required_bike = required_bike - cap_v;
						cap_v = 0;
					}else if(cap_v>required_bike) {
						System.out.println("----in second case");
						cap_v = cap_v - required_bike;
						required_bike = 0;
						bike[i] = bike[i] + required_bike;
					}
					System.out.println("bikes in vehical : "+cap_v);
					System.out.println("required bikde in station : "+required_bike);
				}else if(2*d[i]>delta){
					//skip
				}else {
					System.out.println("----In second else");
					v = v + 1;
					dis_v = 0;
					cap_v=10;
					i--;
				}
				System.out.println("number of vehical : "+v);
				System.out.println("distance of last vehical : "+dis_v);
			}
		}
		System.out.println("Number of vehical : "+v);
	}

}
