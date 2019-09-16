package airline_aug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ilog.concert.IloColumn;
import ilog.concert.IloConstraint;
import ilog.concert.IloConversion;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;

public class Crew_Pairing {

	public ArrayList<Air_section> arrSec = new ArrayList<>();
	public ArrayList<String> arrCity = new ArrayList<>();
	public ArrayList<ArrayList<Integer>> h0 = new ArrayList<ArrayList<Integer>>(),
			h1 = new ArrayList<ArrayList<Integer>>(), b0 = new ArrayList<ArrayList<Integer>>(),
			b1 = new ArrayList<ArrayList<Integer>>();
	public ArrayList<int[]> a = new ArrayList<int[]>();
	public ArrayList<Integer> c = new ArrayList<Integer>();
	private double[] pi;

	public MasterProblem masterproblem;
	public SubProblem subproblem;

	public int[][] xlimit, ylimit;
	public int[] ft;
	public int secSize;

	public int maxdays = 3;
	public int maxhours = 125 * 6; // duty Period 12.5*60 14-1.5
	public int maxh = 90 * 6; // flight Period 9 hour
	public int dutySize = 4;
	public int weekMin = 7 * 24 * 60; //
	public int allDutyMin = 4 * 24 * 60;

	public static double zero = -0.0001;
	public static double one = -zero + 1;
	public static double subproblemTiLim = 5;
	public static double subproblemObjVal = -1000;
	public static int M = 1000;

	public boolean fin = false;
	public double upper;

	public int toDNum = 0;

	public Crew_Pairing(String url) throws Exception {
		ChangeStdToFile();
		ReadData(url);
		masterproblem = new MasterProblem();
		subproblem = new SubProblem();
	}

	private void ChangeStdToFile() throws IOException {
		DateFormat format0 = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
		Date now = new Date();

		String logFileName = "./airline-new-data/logFile/crew_pairing_log_" + format0.format(now.getTime()) + ".log";
		File file = new File(logFileName);
		if (!file.exists())
			file.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(logFileName);
		PrintStream printStream = new PrintStream(fileOutputStream);
		System.setOut(printStream);
		System.out.println("logging:");
	}

	public class MasterProblem {
		public IloCplex cplex;
		private IloObjective total_cost;
		public ArrayList<IloNumVar> x = new ArrayList<>();

		private List<IloConversion> mipConversion = new ArrayList<IloConversion>();
		public double ObjValue;
		public double lastObjValue;
		private ArrayList<IloRange> rng = new ArrayList<IloRange>();

		private boolean[] flag = new boolean[arrSec.size()];
		private int[] tmpA = new int[arrSec.size()];

		private LinkedList<Integer> record;

		public MasterProblem() throws Exception {
			createModel();
			createDefaultPaths();
			cplex.setOut(null);
			// cplex.exportModel("./airline-new-data/masterproblemModel/mp.lp");
			pi = new double[arrSec.size()];

		}

		public void createDefaultPaths() throws Exception {
			flag = new boolean[arrSec.size()];
			for (int i = 0; i < arrSec.size(); i++) {
				record = new LinkedList<>();
				if (!flag[i]) {
					int period, tDep, tArr;
					tmpA = new int[arrSec.size()];
					tmpA[i] = 1;
					period = arrSec.get(i).getPeriod();
					tDep = arrSec.get(i).getDepartureTime();
					tArr = arrSec.get(i).getArrivalTime();
					record.add(i);
					if (searchPath(i, i, tDep, tArr, tDep, tArr, period, period, period, 0)) {
						a.add(tmpA);
						c.add(1);
						for (int j = 0; j < arrSec.size(); j++)
							if (tmpA[j] == 1 && !flag[j])
								flag[j] = true;
						this.addNewColumn();
					} else {
						throw new Exception("error! Num " + i + " secition can't be involved in any ToD");
					}
				}
			}
			System.out.println("initial master model!!");
		}

		private boolean searchPath(int head, int tail, int tDep1, int tArr1, int tDep2, int tArr2, int totPeriod,
				int period1, int period2, int overNight) throws IOException {
			if (arrSec.get(head).getDeparture() == arrSec.get(tail).getArrival()) {
				return true;
			}
			if (totPeriod > 4 * 24 * 60 || overNight > 3)
				return false;
			int newPeriod1, newPeriod2, newTDep1, newTArr1, newTDep2, newTArr2, newTotPeriod;
			for (int j : h0.get(head))
				if (tmpA[j] == 0) {
					newPeriod1 = period1 + arrSec.get(j).getPeriod(); // new
																		// flying
																		// time
					newTDep1 = arrSec.get(j).getDepartureTime(); // new
																	// Departure
																	// Time 1
					newTDep2 = tDep2; // new DewParture Time 2
					newPeriod2 = period2;
					int tmp = tDep1 - newTDep1;
					if (tmp < 0)
						tmp += weekMin;
					newTotPeriod = totPeriod + tmp; // all ToD time
					if (tDep2 == tDep1) {
						newTDep2 = newTDep1;
						newPeriod2 = newPeriod1;
					}
					if (newPeriod1 > maxh)
						continue;
					int dutyPeriod = tArr1 - newTDep1;
					if (newTotPeriod > 4 * 24 * 60)
						continue;
					if (dutyPeriod < 0)
						dutyPeriod += weekMin;
					if (dutyPeriod > maxhours)
						continue;
					tmpA[j] = 1;
					record.addFirst(j);
					if (arrSec.get(j).getDeparture() == arrSec.get(tail).getArrival()) {
						printTods(record);
						return true;
					} else if (searchPath(j, tail, newTDep1, tArr1, newTDep2, tArr2, newTotPeriod, newPeriod1,
							newPeriod2, overNight)) {
						return true;
					}
					tmpA[j] = 0;
					record.removeFirst();
				}
			for (int j : h1.get(head))
				if (overNight < 3 && tmpA[j] == 0) {
					newPeriod1 = arrSec.get(j).getPeriod();
					newTDep1 = arrSec.get(j).getDepartureTime();
					newTArr1 = arrSec.get(j).getArrivalTime();
					int tmp = tDep1 - newTDep1;
					if (tmp < 0)
						tmp += weekMin;
					newTotPeriod = totPeriod + tmp; // all ToD time
					if (newPeriod1 > maxh)
						continue;
					if (newTotPeriod > 4 * 24 * 60)
						continue;
					int dutyPeriod = newTArr1 - newTDep1;
					if (dutyPeriod < 0)
						dutyPeriod += 7 * 24 * 60;
					if (dutyPeriod > maxhours)
						continue;
					tmpA[j] = 1;
					record.addFirst(-1);
					record.addFirst(j);
					if (arrSec.get(j).getDeparture() == arrSec.get(tail).getArrival()) {
						printTods(record);
						return true;
					} else if (searchPath(j, tail, newTDep1, newTArr1, tDep2, tArr2, newTotPeriod, newPeriod1, period2,
							overNight + 1)) {
						return true;
					}
					tmpA[j] = 0;
					record.removeFirst();
					record.removeFirst();
				}
			for (int j : b0.get(tail))
				if (tmpA[j] == 0) {
					newPeriod2 = period2 + arrSec.get(j).getPeriod();
					newTArr2 = arrSec.get(j).getArrivalTime();
					newTArr1 = tArr1;
					newPeriod1 = period1;
					int tmp = newTArr2 - tArr2;
					if (tmp < 0)
						tmp += weekMin;
					newTotPeriod = totPeriod + tmp; // all ToD time
					if (tArr2 == tArr1) {
						newTArr1 = newTArr2;
						newPeriod1 = newPeriod2;
					}
					if (newPeriod2 > maxh)
						continue;
					if (newTotPeriod > 4 * 24 * 60)
						continue;
					int dutyPeriod = newTArr2 - tDep2;
					if (dutyPeriod < 0)
						dutyPeriod += 7 * 24 * 60;
					if (dutyPeriod > maxhours)
						continue;
					tmpA[j] = 1;
					record.addLast(j);
					if (arrSec.get(head).getDeparture() == arrSec.get(j).getArrival()) {
						printTods(record);
						return true;
					} else if (searchPath(head, j, tDep1, newTArr1, tDep2, newTArr2, newTotPeriod, newPeriod1,
							newPeriod2, overNight)) {
						return true;
					}
					tmpA[j] = 0;
					record.removeLast();
				}
			for (int j : b1.get(tail))
				if (overNight < 3 && tmpA[j] == 0) {
					newPeriod2 = arrSec.get(j).getPeriod();
					newTDep2 = arrSec.get(j).getDepartureTime();
					newTArr2 = arrSec.get(j).getArrivalTime();
					int tmp = newTArr2 - tArr2;
					if (tmp < 0)
						tmp += weekMin;
					newTotPeriod = totPeriod + tmp; // all ToD time
					if (newPeriod2 > maxh)
						continue;
					if (newTotPeriod > 4 * 24 * 60)
						continue;
					int dutyPeriod = newTArr2 - newTDep2;
					if (dutyPeriod < 0)
						dutyPeriod += 7 * 24 * 60;
					if (dutyPeriod > maxhours)
						continue;
					tmpA[j] = 1;
					record.addLast(-1);
					record.addLast(j);
					if (arrSec.get(head).getDeparture() == arrSec.get(j).getArrival()) {
						printTods(record);
						return true;
					} else if (searchPath(head, j, tDep1, tArr1, newTDep2, newTArr2, newTotPeriod, period1, newPeriod2,
							overNight + 1)) {
						return true;
					}
					tmpA[j] = 0;
					record.removeLast();
					record.removeLast();
				}

			return false;

		}

		public void createModel() {
			try {
				cplex = new IloCplex();
				IloNumVar[] xx = new IloNumVar[a.size()];

				for (int i = 0; i < a.size(); i++) {
					x.add(xx[i] = cplex.numVar(0, 1));
					x.get(i).setName("x" + i);
				}
				total_cost = cplex.addMinimize();
				for (int i = 0; i < arrSec.size(); i++) {
					rng.add(cplex.addRange(1, 100, "cust" + i));
				}

			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}

		public void addNewColumn() {
			try {
				IloColumn new_column = cplex.column(total_cost, c.get(c.size() - 1));
				for (int i = 0; i < rng.size(); i++) {
					new_column = new_column.and(cplex.column(rng.get(i), a.get(a.size() - 1)[i]));
				}
				x.add(cplex.numVar(new_column, 0, 1, "x" + (a.size() - 1)));
			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}

		public void solveRelaxation() {
			try {
				if (cplex.solve()) {
					saveDualValues();
					ObjValue = cplex.getObjValue();
					// if (ObjValue == lastObjValue) {
					// fin = true;
					// System.out.println("no better solution");
					// }
					lastObjValue = ObjValue;

					System.out.println("the ToD used now : " + lastObjValue);

				}
			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}

		void printTods(LinkedList<Integer> rec) throws IOException {
			// print
			File writename = new File("./airline-new-data/ToDLists/ToD" + toDNum + ".txt");
			toDNum++;
			writename.createNewFile(); //
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			DateFormat format1 = new SimpleDateFormat("HH:mm");
			int tot = 0;
			out.write("ToD " + tot + " : \r\n");
			tot++;
			for (int i = 0; i < record.size(); i++) {
				if (record.get(i) == -1) {
					out.write("ToD " + tot + " : \r\n");
					tot++;
					continue;
				}
				out.write("Sec: " + arrSec.get(i).getIndex() + "  Plane index : " + arrSec.get(i).getPlaneIndex()+"  "
						+ arrCity.get(arrSec.get(record.get(i)).getDeparture()) + " --> "
						+ arrCity.get(arrSec.get(record.get(i)).getArrival()) + "  weekday : "
						+ arrSec.get(record.get(i)).getDay() + " time : "
						+ format1.format(arrSec.get(record.get(i)).dateDep) + " -- "
						+ format1.format(arrSec.get(record.get(i)).dateArr) + "\r\n");

			}
			out.flush(); //
			out.close(); //
		}

		public void saveDualValues() {
			try {
				for (int i = 0; i < rng.size(); i++)
					pi[i] = cplex.getDual(rng.get(i));
				// check();
			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}

		public void check() {
			try {
				for (int i = 0; i < x.size(); i++)
					if (cplex.getValue(x.get(i)) == 1) {
						System.out.print(i + " ");
					}
				System.out.println();
				System.out.println(" dual :");
				for (int i = 0; i < rng.size(); i++)
					System.out.print(i + " " + pi[i] + "  ");
				System.out.println();
			} catch (IloException e) {
				e.printStackTrace();
			}
		}

		public void solveMIP() {
			try {
				convertToMIP();
				if (cplex.solve()) {
					// displaySolution();
				} else {
					System.out.println("Integer solution not found");
				}
			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}

		public void convertToMIP() {
			try {
				for (int i = 0; i < x.size(); i++) {
					mipConversion.add(cplex.conversion(x.get(i), IloNumVarType.Bool));
					cplex.add(mipConversion.get(mipConversion.size() - 1));
				}

			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}

		public void displaySolution() {
			for (int i = 0; i < x.size(); i++) {
				try {
					if (cplex.getValue(x.get(i)) > 0.99999) {
						System.out.print("path " + i + " ");
						for (int j = 0; j < arrSec.size(); j++)
							if (a.get(i)[j] == 1) {
								System.out.print(j + "-- ");
							}
						System.out.println();
					}
				} catch (IloException e) {
					e.printStackTrace();
				}
			}
			System.out.println();
		}
	}

	public class SubProblem {
		public IloCplex cplex;
		public IloIntVar[][][] x, y;
		public IloIntVar[][] f;
		public IloIntVar[] s;
		private IloObjective reduced_cost;
		private List<IloConstraint> constraints;
		public double lastScore;

		public SubProblem() {
			this.constraints = new ArrayList<IloConstraint>();
			createModel();
			System.out.println("initial sub model!");
		}

		public void createModel() {
			try {
				// define model
				cplex = new IloCplex();
				// define variables
				x = new IloIntVar[arrSec.size()][arrSec.size()][dutySize];
				y = new IloIntVar[arrSec.size()][arrSec.size()][dutySize];
				f = new IloIntVar[arrSec.size()][dutySize];
				s = new IloIntVar[arrSec.size()];
				for (int i = 0; i < arrSec.size(); i++) {
					for (int j = 0; j < arrSec.size(); j++) {
						if (xlimit[i][j] > 0) {
							for (int t = 0; t < dutySize; t++) {
								x[i][j][t] = cplex.boolVar();
								x[i][j][t].setName("x" + i + "," + j + "," + t);

							}
						}
						if (ylimit[i][j] > 0) {
							for (int t = 0; t < dutySize - 1; t++) {
								y[i][j][t] = cplex.boolVar();
								y[i][j][t].setName("y" + i + "," + j + "," + t);
							}
						}
					}
				}
				for (int i = 0; i < arrSec.size(); i++) {
					for (int t = 0; t < dutySize; t++) {
						f[i][t] = cplex.boolVar();
						f[i][t].setName("f" + i + "," + t);
					}
					s[i] = cplex.boolVar();
					s[i].setName("s" + i);
				}
				// define objectives
				reduced_cost = cplex.addMaximize();
				// define constraints

				for (int i = 0; i < arrSec.size(); i++) {
					for (int j = 0; j < arrSec.size(); j++) {
						if (xlimit[i][j] > 0) {
							if (!b0.get(i).contains(j))
								for (int t = 0; t < dutySize; t++)
									constraints.add(cplex.addEq(x[i][j][t], 0, "c100_" + i + j + t));
						}
						if (ylimit[i][j] > 0) {
							if (!b1.get(i).contains(j))
								for (int t = 0; t < dutySize - 1; t++)
									constraints.add(cplex.addEq(y[i][j][t], 0, "c101_" + i + j + t));
						}
					}
				}
				for (int t = 0; t < dutySize; t++) {
					for (int i = 0; i < arrSec.size(); i++) {
						IloLinearNumExpr expr1 = cplex.linearNumExpr(), expr2 = cplex.linearNumExpr();
						if (t == 0)
							expr1.addTerm(1, s[i]);
						expr2.addTerm(1, f[i][t]);
						for (int j : h0.get(i))
							if (xlimit[j][i] > 0) {
								expr1.addTerm(1, x[j][i][t]);
							}
						if (t > 0)
							for (int j : h1.get(i))
								if (ylimit[j][i] > 0) {
									expr1.addTerm(1, y[j][i][t - 1]);
								}
						for (int j : b0.get(i))
							if (xlimit[i][j] > 0) {
								expr2.addTerm(1, x[i][j][t]);
							}
						if (t < dutySize - 1)
							for (int j : b1.get(i))
								if (ylimit[i][j] > 0) {
									expr2.addTerm(1, y[i][j][t]);
								}
						constraints.add(cplex.addEq(expr1, expr2, "c1_" + i + t));
						constraints.add(cplex.addLe(expr1, 1, "c15_" + i + t));
					}
				}
				IloLinearNumExpr expr3 = cplex.linearNumExpr(), expr4 = cplex.linearNumExpr();
				for (int i = 0; i < arrSec.size(); i++) {
					expr3.addTerm(1, s[i]);
					for (int t = 0; t < dutySize; t++) {
						expr4.addTerm(1, f[i][t]);
					}
				}
				constraints.add(cplex.addEq(expr3, 1, "c2"));
				constraints.add(cplex.addEq(expr4, 1, "c3"));
				for (int t = 0; t < dutySize; t++) {
					IloLinearNumExpr expr5 = cplex.linearNumExpr();
					for (int i = 0; i < arrSec.size(); i++) {
						for (int j = 0; j < arrSec.size(); j++) {
							if (xlimit[i][j] > 0) {
								expr5.addTerm(ft[i], x[i][j][t]);
							}

							if (ylimit[i][j] > 0 && t != dutySize - 1) {
								expr5.addTerm(ft[i], y[i][j][t]);
							}
						}
						expr5.addTerm(ft[i], f[i][t]);
					}
					constraints.add(cplex.addLe(expr5, maxh, "c4_" + t));
				}
				IloLinearNumExpr yijt = cplex.linearNumExpr();
				IloLinearNumExpr yAt = cplex.linearNumExpr(), yDt = cplex.linearNumExpr(), et = cplex.linearNumExpr(),
						st = cplex.linearNumExpr();

				for (int i = 0; i < arrSec.size(); i++) {
					st.addTerm(arrSec.get(i).getDepartureTime(), s[i]);
					et.addTerm(arrSec.get(i).getArrivalTime(), f[i][0]);
					for (int j = 0; j < arrSec.size(); j++)
						if (ylimit[i][j] > 0) {
							yAt.addTerm(arrSec.get(i).getArrivalTime(), y[i][j][0]);
						}
				}
				IloIntVar[] p1 = new IloIntVar[dutySize];
				IloIntVar[] p2 = new IloIntVar[dutySize - 1];
				p1[0] = cplex.boolVar();
				constraints.add(cplex.addLe(cplex.diff(cplex.sum(et, yAt), st),
						cplex.sum(cplex.prod(weekMin, cplex.diff(p1[0], 1)), maxhours), "c5_1"));
				constraints.add(cplex.addGe(cplex.diff(cplex.sum(et, yAt), st),
						cplex.prod(weekMin, cplex.diff(p1[0], 1)), "c5_2"));
				for (int t = 1; t < dutySize; t++) {
					yAt = cplex.linearNumExpr();
					yDt = cplex.linearNumExpr();
					et = cplex.linearNumExpr();
					for (int i = 0; i < arrSec.size(); i++) {
						et.addTerm(arrSec.get(i).getArrivalTime(), f[i][t]);
						for (int j = 0; j < arrSec.size(); j++)
							if (ylimit[i][j] > 0) {
								if (t < dutySize - 1) {
									yAt.addTerm(arrSec.get(i).getArrivalTime(), y[i][j][t]);
								}
								yDt.addTerm(arrSec.get(j).getDepartureTime(), y[i][j][t - 1]);
								yijt.addTerm(1, y[i][j][t - 1]);
							}
					}
					p1[t] = cplex.boolVar();
					p2[t - 1] = cplex.boolVar();
					constraints.add(cplex.addLe(cplex.diff(cplex.sum(et, yAt), yDt),
							cplex.sum(cplex.prod(weekMin, cplex.diff(p1[t], 1)), maxhours), "c6_1" + t));
					constraints.add(cplex.addGe(cplex.diff(cplex.sum(et, yAt), yDt),
							cplex.prod(weekMin, cplex.diff(p1[t], 1)), "c6_2" + t));
					constraints.add(cplex.addLe(cplex.diff(cplex.sum(et, yAt), st),
							cplex.sum(cplex.prod(weekMin, cplex.diff(p2[t - 1], 1)), allDutyMin), "c7_1" + t));
					constraints.add(cplex.addGe(cplex.diff(cplex.sum(et, yAt), st),
							cplex.prod(weekMin, cplex.diff(p2[t - 1], 1)), "c7_2" + t));
				}

				IloLinearNumExpr ee = cplex.linearNumExpr();
				for (int t = dutySize - 2; t >= 0; t--) {
					IloLinearNumExpr yt1 = cplex.linearNumExpr(), yt2 = cplex.linearNumExpr();
					for (int i = 0; i < arrSec.size(); i++) {
						ee.addTerm(1, f[i][t + 1]);
						for (int j = 0; j < arrSec.size(); j++)
							if (ylimit[i][j] > 0) {
								yt1.addTerm(1, y[i][j][t]);
								if (t > 0)
									yt2.addTerm(1, y[i][j][t - 1]);
							}
					}
					if (t > 0) {
						constraints.add(cplex.addEq(yt1, ee, "c9_" + t));
						constraints.add(cplex.addLe(yt1, yt2, "c10_" + t));
					} else {
						constraints.add(cplex.addEq(yt1, ee, "c19_" + t));
					}
				}
				// constraints.add(cplex.addGe(yijt, maxdays - 1, "c11"));
				IloLinearNumExpr expr6 = cplex.linearNumExpr(), expr7 = cplex.linearNumExpr();
				for (int i = 0; i < arrSec.size(); i++) {
					expr7.addTerm(arrSec.get(i).getDeparture(), s[i]);
					for (int t = 0; t < dutySize; t++) {
						expr6.addTerm(arrSec.get(i).getArrival(), f[i][t]);
					}
				}
				constraints.add(cplex.addEq(expr6, expr7, "c12"));
				for (int i = 0; i < arrSec.size(); i++) {
					IloLinearNumExpr expr9 = cplex.linearNumExpr();
					IloLinearNumExpr expr10 = cplex.linearNumExpr();
					for (int j = 0; j < arrSec.size(); j++) {

						for (int t = 0; t < dutySize; t++) {
							if (xlimit[i][j] > 0) {
								expr9.addTerm(1, x[i][j][t]);
							}
							if (ylimit[i][j] > 0 && t != dutySize - 1) {
								expr9.addTerm(1, y[i][j][t]);
							}
						}

						for (int t = 0; t < dutySize; t++) {
							if (xlimit[j][i] > 0) {
								expr10.addTerm(1, x[j][i][t]);
							}
							if (ylimit[j][i] > 0 && t != dutySize - 1) {
								expr10.addTerm(1, y[j][i][t]);
							}
						}
					}
					constraints.add(cplex.addLe(expr9, 1, "c13_" + i));
					constraints.add(cplex.addLe(expr10, 1, "c14_" + i));
				}
			} catch (

			IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}

		public void updateReducedCost() {
			try {
				IloLinearNumExpr num_expr = cplex.linearNumExpr();
				double bigPi = 0;
				for (int i = 0; i < arrSec.size(); i++) {
					bigPi += pi[i];
					for (int t = 0; t < dutySize; t++) {
						for (int j = 0; j < arrSec.size(); j++) {
							if (xlimit[i][j] > 0) {
								num_expr.addTerm(pi[i]+0.1, x[i][j][t]);
							}
							if (ylimit[i][j] > 0 && t != dutySize - 1) {
								num_expr.addTerm(pi[i]+0.1, y[i][j][t]);
							}
						}
						num_expr.addTerm(pi[i]+0.1, f[i][t]);
					}
				}
				reduced_cost.clearExpr();
				reduced_cost.setExpr(num_expr);
				System.out.println("big Pi is " + bigPi);
			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}

		public void testSubModel() {
			// initial objective
			IloLinearNumExpr num_expr;
			try {
				num_expr = cplex.linearNumExpr();
				for (int i = 0; i < arrSec.size(); i++) {
					for (int t = 0; t < dutySize; t++) {
						for (int j = 0; j < arrSec.size(); j++) {
							if (xlimit[i][j] > 0) {
								num_expr.addTerm(1, x[i][j][t]);
							}
							if (ylimit[i][j] > 0 && t != dutySize - 1) {
								num_expr.addTerm(1, y[i][j][t]);
							}
						}
						num_expr.addTerm(1, f[i][t]);
					}
				}
				reduced_cost.clearExpr();
				reduced_cost.setExpr(num_expr);
				solve();
			} catch (IloException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public boolean solve() throws IOException {
			try {
				// cplex.exportModel("./airline-new-data/subproblemModel/sbp_" +
				// toDNum + ".lp");
				// cplex.setOut(null);
				cplex.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 0.81);
				cplex.setParam(IloCplex.Param.TimeLimit, 300);
				if (cplex.solve()) {
					this.lastScore = cplex.getObjValue();
					System.out.println("lastScore is " + lastScore);
					int nPool = cplex.getSolnPoolNsolns();
					if (cplex.getObjValue(0) <= one) { // 1-pi>=0
						fin = true;
					}
					for (int k = 0; k < nPool; k++) {
						savePath(k);
						if (fin)
							return true;
					}
				} else {
					return false;
				}
			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
			return true;
		}

		public void savePath(int k) throws IOException, UnknownObjectException, IloException {
			// print
			File writename = new File("./airline-new-data/ToDLists/NewToD" + toDNum + ".txt");
			toDNum++;
			writename.createNewFile(); //
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			DateFormat format1 = new SimpleDateFormat("HH:mm");

			// save
			int[] tmp = new int[arrSec.size()];
			for (int t = 0; t < dutySize; t++) {
				out.write("ToD " + t + " : \r\n");
				for (int i = 0; i < arrSec.size(); i++) {
					if (cplex.getValue(f[i][t], k) > 0.99999) {
						System.out.println("f " + i + "," + t + "  =  1");
						tmp[i] = 1;
						out.write("Sec: " + arrSec.get(i).getIndex() + "  Plane index : " + arrSec.get(i).getPlaneIndex()+"  "
								+ arrCity.get(arrSec.get(i).getDeparture()) + " --> "
								+ arrCity.get(arrSec.get(i).getArrival()) + "  weekday : " + arrSec.get(i).getDay()
								+ " time : " + format1.format(arrSec.get(i).dateDep) + " -- "
								+ format1.format(arrSec.get(i).dateArr) + "\r\n");
					}
					for (int j = 0; j < arrSec.size(); j++)
						if ((xlimit[i][j] > 0 && cplex.getValue(x[i][j][t], k) > 0.99999) || (ylimit[i][j] > 0
								&& (t != dutySize - 1 && cplex.getValue(y[i][j][t], k) > 0.99999))) {
							if (cplex.getValue(x[i][j][t], k) > 0.99999)
								System.out.println("x " + i + "," + j + "," + t + "  =  1");
							else
								System.out.println("y " + i + "," + j + "," + t + "  =  1");
							tmp[i] = 1;
							out.write("Sec: " + arrSec.get(i).getIndex() + "  Plane index : " + arrSec.get(i).getPlaneIndex()+"  "
									+ arrSec.get(i).getPlaneIndex() + arrCity.get(arrSec.get(i).getDeparture())
									+ " --> " + arrCity.get(arrSec.get(i).getArrival()) + "  weekday : "
									+ arrSec.get(i).getDay() + " time : " + format1.format(arrSec.get(i).dateDep)
									+ " -- " + format1.format(arrSec.get(i).dateArr) + "\r\n");
						}
				}
			}
			out.flush(); //
			out.close(); //
			a.add(tmp);
			c.add(1);
			Crew_Pairing.this.masterproblem.addNewColumn();
//			Crew_Pairing.this.masterproblem.cplex
//					.exportModel("./airline-new-data/masterproblemModel/mp_" + toDNum + ".lp");
		}

	}

	public void test() {
		for (int j = 0; j < arrSec.size(); j++) {
			System.out.println(j + "    :");
			System.out.println("---1---");
			for (int i : h0.get(j)) {
				System.out.println(i + "  ");
			}
			System.out.println("---2---");
			for (int i : b0.get(j)) {
				System.out.println(i + "  ");
			}
			System.out.println("---3---");
			for (int i : h1.get(j)) {
				System.out.println(i + "  ");
			}
			System.out.println("---4---");
			for (int i : b1.get(j)) {
				System.out.println(i + "  ");
			}
			System.out.println("-------");
		}
	}

	public void ReadData(String url) throws Exception {
		Data_Loader dl = new Data_Loader();
		dl.loadNodeInfo(url);
		arrSec.clear();
		arrSec.addAll(dl.arrSec);
		arrCity.clear();
		arrCity.addAll(dl.arrCity);
		Cal_Parameter cp = new Cal_Parameter(arrSec);
		cp.calculate();
		cp.test_cal();
		secSize = arrSec.size();
		ft = cp.ft;
		xlimit = cp.xlimit;
		ylimit = cp.ylimit;
		h0.addAll(cp.h0);
		h1.addAll(cp.h1);
		b0.addAll(cp.b0);
		b1.addAll(cp.b1);
		System.out.println("Cal Parameter: OK");
	}

	public void runCrew_pairing() throws Exception {
		do {
			int iteration_counter = 0;
			iteration_counter++;
			masterproblem.solveRelaxation();
			// if (fin == true)
			// break;
			subproblem.updateReducedCost();
			if (subproblem.solve() == false) {
				throw new Exception("error! " + iteration_counter + " sub no feasible solution!");
			}
		} while (!fin);
		masterproblem.solveMIP();
	}

}
