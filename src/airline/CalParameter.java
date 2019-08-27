package airline;

import java.util.ArrayList;

public class CalParameter {
	public ArrayList<Air_section> arrSec=new ArrayList<>();
	public ArrayList<String> arrCity = new ArrayList<>();
	public ArrayList<ArrayList<Integer>> h0=new ArrayList<ArrayList<Integer>>(),h1=new ArrayList<ArrayList<Integer>>(),
										b0=new ArrayList<ArrayList<Integer>>(),b1=new ArrayList<ArrayList<Integer>>();
	public int[][] BL;
	public int[] ft,w,r;
	public int secSize;
	public int maxh = 8*60;
	public int daySize = 7;
	public int[][][] XL,YL;
	
	public CalParameter( ArrayList<Air_section> arrSec){
		this.arrSec.addAll(arrSec);
		secSize = arrSec.size();
		BL = new int[secSize][daySize];
		ft = new int[secSize];
		w = new int[secSize];
		r = new int[secSize];
		XL = new int[secSize][secSize][daySize];
		YL = new int[secSize][secSize][daySize];
	}
	
	public void calculate(){
		//  假设w 和r 为常量，不过这里还是用数组表示。
		for (int i=0; i<secSize; i++){
			w[i] = 10;
			r[i] = 15;
			ft[i] = arrSec.get(i).tArr-arrSec.get(i).tDep;
			if (ft[i] < 0) ft[i] += 60*24;
			int t = arrSec.get(i).day-1;
			BL[i][t] = 1;
		}
		for (int i=0; i<secSize; i++){
			int head = arrSec.get(i).getDeparture();
			int tail = arrSec.get(i).getArrival();
			int weekday = arrSec.get(i).getDay();
			int headTime = arrSec.get(i).getDepartureTime();
			int tailTime = arrSec.get(i).getArrivalTime();
			ArrayList<Integer> t1=new ArrayList<>(), t2=new ArrayList<>(), t3=new ArrayList<>(), t4=new ArrayList<>();
			for (int j=0; j<secSize; j++) if (i!=j){
				if (head == arrSec.get(j).getArrival()){
					if (arrSec.get(j).getDay() == weekday && arrSec.get(j).getArrivalTime() < headTime ) {
						t1.add(j);
						XL[j][i][weekday-1]=1;
					}
					else if (arrSec.get(j).getDay() == weekday-1) {
						t2.add(j);
						YL[j][i][weekday-2]=1;
					}
				}
				if (tail == arrSec.get(j).getDeparture()){
					if (arrSec.get(j).getDay() == weekday && arrSec.get(j).getDepartureTime() > tailTime ) {
						t3.add(j);
					}
					else if (arrSec.get(j).getDay() == weekday+1) {
						t4.add(j);
					}
				}
			}
			h0.add(t1);
			h1.add(t2);
			b0.add(t3);
			b1.add(t4);
			
		}
	}
}
