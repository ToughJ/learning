package airline_aug;

import java.util.ArrayList;
import java.util.Date;

public class Air_section {
	int index;
	int planeIndex;
	int departure, arrival;
	String departureName, arrivalName;
	int tDep, tArr;
	Date dateDep, dateArr;
	int day;
	int transDay = 0;
	String planeName;
	String flightName;
	int secNum;
	ArrayList<Air_section> h0, h1, b0, b1;

	public Air_section() {
		h0 = new ArrayList<>();
		h1 = new ArrayList<>();
		b0 = new ArrayList<>();
		b1 = new ArrayList<>();
	}

	public Air_section(Air_section tmp) {
		h0 = new ArrayList<>();
		h1 = new ArrayList<>();
		b0 = new ArrayList<>();
		b1 = new ArrayList<>();
		this.index = tmp.index;
		this.planeIndex = tmp.planeIndex;
		this.planeName = tmp.planeName;
		this.departure = tmp.departure;
		this.arrival = tmp.arrival;
		this.departureName = tmp.departureName;
		this.arrivalName = tmp.arrivalName;
		this.secNum = tmp.secNum;
		this.flightName = tmp.flightName;
		this.transDay = tmp.transDay;
		this.dateDep = tmp.dateDep;
		this.day = tmp.day;
		this.dateArr = tmp.dateArr;
		this.tArr = tmp.tArr;
		this.tDep = tmp.tDep;
	}

	public Air_section(int index, int departure, int arrival, int tDep, int tArr) {
		this.index = index;
		this.departure = departure;
		this.arrival = arrival;
		this.tDep = tDep;
		this.tArr = tArr;
		h0 = new ArrayList<>();
		h1 = new ArrayList<>();
		b0 = new ArrayList<>();
		b1 = new ArrayList<>();
	}

	public void addB0(Air_section sec) {
		b0.add(sec);
	}

	public void addB1(Air_section sec) {
		b1.add(sec);
	}

	public void addH0(Air_section sec) {
		h0.add(sec);
	}

	public void addH1(Air_section sec) {
		h1.add(sec);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getPlaneIndex() {
		return planeIndex;
	}

	public void setPlaneIndex(int planeIndex) {
		this.planeIndex = planeIndex;
	}

	public String getPlaneName() {
		return planeName;
	}

	public void setPlaneName(String planeName) {
		this.planeName = planeName;
	}

	public String getFlightName() {
		return flightName;
	}

	public void setFlightName(String flightName) {
		this.flightName = flightName;
	}

	public int getDeparture() {
		return departure;
	}

	public void setDeparture(int departure) {
		this.departure = departure;
	}

	public int getArrival() {
		return arrival;
	}

	public void setArrival(int arrival) {
		this.arrival = arrival;
	}

	public int getDepartureTime() {
		return tDep;
	}

	public void setDepartureTime(int tDep) {
		this.tDep = tDep;
	}

	public int getArrivalTime() {
		return tArr;
	}

	public void setArrivalTime(int tArr) {
		this.tArr = tArr;
	}

	public int getPeriod() {
		int period = tArr - tDep;
		if (period < 0)
			period += 7 * 60 * 24;
		return period;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

}
