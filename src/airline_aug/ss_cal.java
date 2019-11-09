package airline_aug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

public class ss_cal {

	public ArrayList<Air_section> arrSec = new ArrayList<>();
	public ArrayList<String> arrCity = new ArrayList<>();
	public ArrayList<Tour_ofDuty> arrToD = new ArrayList<>();
	public ArrayList<Schedule> arrSch = new ArrayList<>();
	public ArrayList<Integer> ali = new ArrayList<>(), bli = new ArrayList<>(), cli = new ArrayList<>();
	public int[][] cl;
	public double coef = 0.0001;
	
	public ArrayList<ArrayList<Integer>> rec = new ArrayList<>();

	private int used = 0;

	public ss_cal() throws FileNotFoundException, IOException, ParseException {
		ss_Parameter sp = new ss_Parameter();
		arrSec = sp.arrSec;
		arrCity = sp.arrCity;
		arrToD = sp.arrToD;
		arrSch = sp.arrSch;
		ali = sp.ali;
		bli = sp.bli;
		cli = sp.cli;
		cl = new int[8][arrCity.size()];
		cl = sp.cl.clone();
		solve();
	}

	public void solve() throws IOException {
		try {
			IloCplex cplex;
			ArrayList<IloNumVar> x = new ArrayList<>();
			cplex = new IloCplex();
			cplex.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 0.01); //0.025
			IloLinearNumExpr obj = cplex.linearNumExpr();
			IloNumVar[] s = new IloNumVar[arrSec.size()];
			// define variables and objectives part one
			for (int i = 0; i < arrSec.size(); i++) {
				s[i] = cplex.boolVar();
				s[i].setName("s" + i);
				obj.addTerm(coef, s[i]);
			}
			for (int i = 0; i < arrSch.size(); i++) {
				IloNumVar xx = cplex.boolVar();
				x.add(xx);
				x.get(i).setName("x" + i);
				obj.addTerm(1, x.get(i));
			}
			// define constraints
			IloLinearNumExpr[][] crew_living = new IloLinearNumExpr[10][arrCity.size()];
			IloLinearNumExpr[][] cover = new IloLinearNumExpr[3][arrSec.size()];
			
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < arrCity.size(); j++) {
					crew_living[i][j] = cplex.linearNumExpr();
				}
			}
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < arrSec.size(); j++) {
					cover[i][j] = cplex.linearNumExpr();
				}
			}
			for (int i = 0; i < arrSch.size(); i++) {
				int city = arrSch.get(i).city;
				int day = arrSch.get(i).startDay + 3;
				for (int j = 0; j < 8; j++) {
					if (j <= day) {
						crew_living[j][city].addTerm(1, x.get(i));
					}
				}
				for (int a : arrSch.get(i).acv) {
					cover[0][a].addTerm(1, x.get(i));
				}
				for (int b : arrSch.get(i).bcv) {
					cover[1][b].addTerm(1, x.get(i));
				}
				for (int c : arrSch.get(i).ccv) {
					cover[2][c].addTerm(1, x.get(i));
				}
			}

			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < arrCity.size(); j++) {
					cplex.addLe(crew_living[i][j], cl[i][j], "cl" + i + "," + j);
				}
			}
			for (int j : ali) {
				cplex.addGe(cover[0][j], 1, "cover0," + j);
			}
			for (int j : bli) {
				cplex.addGe(cover[1][j], 1, "cover1," + j);
				cplex.addLe(cover[1][j], 3, "cover1," + j);
			}
			for (int j : cli) {
				cplex.addGe(cplex.sum(cover[2][j], s[j]), 1, "cover2," + j);
			}
			cplex.addMinimize(obj);
			cplex.exportModel("./airline-new-data/ss.lp");
			if (cplex.solve()) {
				for (int i = 0; i < arrSec.size(); i++) {
					ArrayList<Integer> thisrec = new ArrayList<>();
					rec.add(thisrec);
				}
				
				System.out.println("obj is " + cplex.getObjValue());
				used = 0;
				for (int i = 0; i < x.size(); i++)
					if (cplex.getValue(x.get(i)) >= 0.99) {
						printit(i);
					}
				
				
				File writename = new File("./airline-new-data/checkingSec.txt");
				writename.createNewFile(); //
				BufferedWriter out = new BufferedWriter(new FileWriter(writename));
				
				
				for (int i = 0; i < arrSec.size(); i++) {
					out.write("Sec "+i+ ": ");
					for (int j:rec.get(i)){
						out.write(j + " ");
					}
					out.write("\r\n");
				}
				out.flush(); //
				out.close(); //
			}

		} catch (IloException e) {
			e.printStackTrace();
		}

	}

	private void printit(int i) throws IOException {
		for (int b : arrSch.get(i).bcv){
			rec.get(b).add(used);
		}
		
		File writename = new File("./airline-new-data/used_Schedule/NO." + used + ".txt");
		used++;
		writename.createNewFile(); //
		BufferedWriter out = new BufferedWriter(new FileWriter(writename));
		DateFormat format = new SimpleDateFormat("HH:mm");
		Schedule schedule = arrSch.get(i);
		if (schedule.startDay<0) {
			out.write("from last week\r\n");
		}
		Tour_ofDuty tod=schedule.arrToD.get(0);
		int fd=tod.day_ofWeek;
		out.write("tod_num : " + tod.ToDindex + "\r\n");
		for (Air_section sec : tod.secs){
			out.write("Sec: " + sec.getIndex() + "  Plane index: " + sec.getPlaneIndex()+"  "
					+ arrCity.get(sec.getDeparture()) + " --> "
					+ arrCity.get(sec.getArrival()));
			int weekday=sec.getDay();
			if (schedule.startDay<0 && weekday > fd) 
				weekday -= 7;
			out.write("  weekday: " + sec.getDay() + " time: " + format.format(sec.dateDep) + " -- "
					+ format.format(sec.dateArr) + "\r\n");
		}
		out.write("two days off\r\n\r\n");
		if (schedule.arrToD.size()>1){
			tod =schedule.arrToD.get(1);
			out.write("tod_num : " + tod.ToDindex + "\r\n");
			fd=tod.day_ofWeek;
			for (Air_section sec : tod.secs){
				out.write("Sec: " + sec.getIndex() + "  Plane index: " + sec.getPlaneIndex()+"  "
						+ arrCity.get(sec.getDeparture()) + " --> "
						+ arrCity.get(sec.getArrival()));
				int weekday=sec.getDay();
				if (weekday < fd+1) 
					weekday += 7;
				out.write("  weekday: " + sec.getDay() + " time: " + format.format(sec.dateDep) + " -- "
						+ format.format(sec.dateArr) + "\r\n");
			}
			out.write("two days off\r\n");
		}
		
		out.flush(); //
		out.close(); //

	}

}
