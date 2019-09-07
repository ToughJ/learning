package airline_aug;

import java.util.ArrayList;

public class Schedule {
	ArrayList<Integer> acv = new ArrayList<>(), bcv = new ArrayList<>(), ccv = new ArrayList<>();
	int city = 0;
	int startDay = 0;
	ArrayList<Tour_ofDuty> arrToD = new ArrayList<>();

	public Schedule() {

	}

	public Schedule(Schedule one) {
		this.city = one.city;
		this.startDay = one.startDay;
		for (Tour_ofDuty tod : one.arrToD) {
			this.arrToD.add(tod);
		}
		for (int a : one.acv) {
			this.acv.add(a);
		}
		for (int b : one.bcv) {
			this.acv.add(b);
		}
		for (int c : one.ccv) {
			this.acv.add(c);
		}
	}
}