package airline_aug;

import java.util.ArrayList;

public class Cal_Parameter {
	public ArrayList<Air_section> arrSec = new ArrayList<>();
	public ArrayList<String> arrCity = new ArrayList<>();
	public ArrayList<ArrayList<Integer>> h0 = new ArrayList<ArrayList<Integer>>(),
			h1 = new ArrayList<ArrayList<Integer>>(), b0 = new ArrayList<ArrayList<Integer>>(),
			b1 = new ArrayList<ArrayList<Integer>>();
	public int[] ft;
	public int[][] xlimit;
	public int[][] ylimit;
	public int secSize;
	public int daySize = 7;

	private int transInterval = 15 * 6; // changing plane 1.5
	private int dayMin = 60 * 24;
	private int weekMin = 7 * dayMin;
	private int restInterval = 10 * 60 + transInterval; // rest time 10+1.5
	private int dutyMin = 14 * 60 - transInterval;

	public Cal_Parameter(ArrayList<Air_section> arrSec) {
		this.arrSec.addAll(arrSec);
		secSize = arrSec.size();
		xlimit = new int[secSize][secSize];
		ylimit = new int[secSize][secSize];
		ft = new int[secSize];
	}

	public void calculate() {
		for (int i = 0; i < secSize; i++) {
			ft[i] = arrSec.get(i).tArr - arrSec.get(i).tDep;
			if (ft[i] < 0)
				ft[i] += weekMin;
		}
		for (int i = 0; i < secSize; i++) {
			int head = arrSec.get(i).getDeparture();
			int tail = arrSec.get(i).getArrival();
			int headTime = arrSec.get(i).getDepartureTime();
			int tailTime = arrSec.get(i).getArrivalTime();
			int planeIndex = arrSec.get(i).planeIndex;
			// calculate h and b \ XL and YL
			ArrayList<Integer> t1 = new ArrayList<>(), t2 = new ArrayList<>(), t3 = new ArrayList<>(),
					t4 = new ArrayList<>();
			for (int j = 0; j < secSize; j++)
				if (i != j) {
					if (head == arrSec.get(j).getArrival()) {
						if ((arrSec.get(j).planeIndex == planeIndex) && ((headTime - arrSec.get(j).getArrivalTime() >= 0
								&& headTime - arrSec.get(j).getArrivalTime() <= dutyMin)
								|| (headTime + weekMin - arrSec.get(j).getArrivalTime() >= 0
										&& headTime + weekMin - arrSec.get(j).getArrivalTime() <= dutyMin))) {
							t1.add(j);
							xlimit[j][i] = 1;
						}
						if (arrSec.get(j).planeIndex != planeIndex
								&& ((headTime - arrSec.get(j).getArrivalTime() >= transInterval
										&& headTime - arrSec.get(j).getArrivalTime() <= dutyMin)
										|| (headTime + weekMin - arrSec.get(j).getArrivalTime() >= transInterval
												&& headTime + weekMin - arrSec.get(j).getArrivalTime() <= dutyMin))) {
							t1.add(j);
							xlimit[j][i] = 1;
						}
						if (((headTime - arrSec.get(j).getArrivalTime() >= restInterval)
								&& (headTime - arrSec.get(j).getArrivalTime() <= 3.5 * dayMin))
								|| ((headTime + weekMin - arrSec.get(j).getArrivalTime() <= 3.5 * dayMin)
										&& (headTime + weekMin - arrSec.get(j).getArrivalTime() >= restInterval))) {
							t2.add(j);
							xlimit[j][i] = 1;
							if ((headTime - arrSec.get(j).getArrivalTime() <= 1.5 * dayMin)
								|| (headTime + weekMin - arrSec.get(j).getArrivalTime() <= 1.5 * dayMin)){
								ylimit[j][i] = 1;
							}
						}
					}
					if (tail == arrSec.get(j).getDeparture()) {
						if (arrSec.get(j).planeIndex != planeIndex
								&& ((arrSec.get(j).getDepartureTime() - tailTime >= transInterval
										&& arrSec.get(j).getDepartureTime() - tailTime <= dutyMin)
										|| (arrSec.get(j).getDepartureTime() + weekMin - tailTime >= transInterval
												&& arrSec.get(j).getDepartureTime() + weekMin - tailTime <= dutyMin))) {
							t3.add(j);
							xlimit[i][j] = 1;
						}
						if (arrSec.get(j).planeIndex == planeIndex && ((arrSec.get(j).getDepartureTime() - tailTime >= 0
								&& arrSec.get(j).getDepartureTime() - tailTime <= dutyMin)
								|| (arrSec.get(j).getDepartureTime() + weekMin - tailTime >= 0
										&& arrSec.get(j).getDepartureTime() + weekMin - tailTime <= dutyMin))) {
							t3.add(j);
							xlimit[i][j] = 1;
						}
						if (((arrSec.get(j).getDepartureTime() - tailTime >= restInterval)
								&& (arrSec.get(j).getDepartureTime() - tailTime <= 3.5 * dayMin))
								|| ((arrSec.get(j).getDepartureTime() + weekMin - tailTime >= restInterval)
										&& (arrSec.get(j).getDepartureTime() + weekMin - tailTime <= 3.5 * dayMin))) {
							t4.add(j);
							if ((arrSec.get(j).getDepartureTime() - tailTime <= 1.5 * dayMin)
								|| (arrSec.get(j).getDepartureTime() + weekMin - tailTime <= 1.5 * dayMin)){
								ylimit[i][j] = 1;
							}
						}
					}
				}
			h0.add(t1);
			h1.add(t2);
			b0.add(t3);
			b1.add(t4);

		}
	}

	public void test_cal() throws Exception {
		for (int i = 0; i < arrSec.size(); i++) {
			for (int j : h0.get(i)) {
				if (!b0.get(j).contains(i)) {
					throw new Exception("error!  preproccess -- not symmetrical   :  " + i + "--" + j);
				}
			}
			for (int j : h1.get(i)) {
				if (!b1.get(j).contains(i)) {
					throw new Exception("error!  preproccess -- not symmetrical  :  " + i + "--" + j);
				}
			}
		}
		for (int i = 0; i < arrSec.size(); i++) {
			if (ft[i] <= 0) {
				throw new Exception("error!  some plane  not fly at all :   " + i);
			}
		}
	}
}
