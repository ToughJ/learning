package airline_aug;

import java.util.ArrayList;

public class Schedule {
	ArrayList<Integer> acv = new ArrayList<>(), bcv = new ArrayList<>(), ccv = new ArrayList<>();
	int city = 0;
	int startDay = 0;
	int tSta,tEnd;
	ArrayList<Tour_ofDuty> arrToD = new ArrayList<>();

	public Schedule() {

	}

	public Schedule(Schedule one) {
		this.city = one.city;
		this.startDay = one.startDay;
		this.tSta = one.tSta;
		this.tEnd = one.tEnd;
		for (Tour_ofDuty tod : one.arrToD) {
			this.arrToD.add(tod);
		}
		for (int a : one.acv) {
			this.acv.add(a);
		}
		for (int b : one.bcv) {
			this.bcv.add(b);
		}
		for (int c : one.ccv) {
			this.ccv.add(c);
		}
	}
}