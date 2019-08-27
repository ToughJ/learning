package airline;

import java.util.ArrayList;

public class Air_section {
	int index;
	int departure, arrival;
	int tDep, tArr;
	int day;
	ArrayList<Air_section> h0,h1,b0,b1;
	
	public Air_section(){
		h0 = new ArrayList<>();
		h1 = new ArrayList<>();
		b0 = new ArrayList<>();
		b1 = new ArrayList<>();
	}
	
	public Air_section(int index, int departure, int arrival, int tDep, int tArr){
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
	
	public void addB0(Air_section sec){
		b0.add(sec);
	}
	
	public void addB1(Air_section sec){
		b1.add(sec);
	}
	
	public void addH0(Air_section sec){
		h0.add(sec);
	}
	
	public void addH1(Air_section sec){
		h1.add(sec);
	}
	
	public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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
    
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

}
