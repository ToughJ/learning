package vrp_consistency;

import ilog.concert.*;
import ilog.cplex.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class inventory_age1 {// where k represent the vehicle number
	static double RC_EPS = 1.0e-6;

	public static void main(String[] args) throws IloException, IOException {

		// ---read data ---//
		String filePathFac = "./vrp-con-data/auto-gen/at_int_02.txt";
		Scanner in1 = new Scanner(Paths.get(filePathFac));

		String[] temp = in1.nextLine().split(" ");
		int Nd = Integer.parseInt(temp[0]);
		int Ns = Integer.parseInt(temp[1]);
		int Nc = Integer.parseInt(temp[2]);
		int small_cap = Integer.parseInt(temp[3]);
		int big_cap = Integer.parseInt(temp[4]);
		int N = Nd + Ns + Nc;
		System.out.println("Nd " + Nd + " Ns " + Ns + " Nc " + Nc + " Small_cap " + small_cap + " Big_cap " + big_cap);

		String[] temp1 = in1.nextLine().split(" ");
		int Nv_small = Integer.parseInt(temp1[0]);
		int Nv_large = Integer.parseInt(temp1[1]);
		int Nt = Integer.parseInt(temp1[2]);
		int Ng = Integer.parseInt(temp1[3]);
		System.out.println("Nv_small " + Nv_small + "Nv_large " + Nv_large + " Nt " + Nt + " Ng " + Ng);

		String[] temp2 = in1.nextLine().split(" ");
		int up_s = Integer.parseInt(temp2[0]);
		int up_l = Integer.parseInt(temp2[1]);
		int Tmax = Integer.parseInt(temp2[2]);
		int L = Integer.parseInt(temp2[3]);

		System.out.println("Tmax " + Tmax + "L " + L + " up_l " + up_l + " up_s " + up_s);
		// read i x y h d_i_0 d_i_1...I_i_0_0,I_i_1_0,...
		int velocity = 5;
		int[][] distance = new int[N][N];
		int[][] minute = new int[N][N];
		double[] coorX = new double[N];
		double[] coorY = new double[N];
		int[] H = new int[N];// max inventory level
		int[][] Demand = new int[N][Nt + 1];
		int[][] initial_inventory = new int[N][Ng];
		for (int i = 0; i < N; i++) {
			String[] temp3 = in1.nextLine().split(" ");
			int index = Integer.parseInt(temp3[0]);
			coorX[i] = Integer.parseInt(temp3[1]);
			coorY[i] = Integer.parseInt(temp3[2]);
			H[i] = Integer.parseInt(temp3[3]);
			for (int j = 1; j <= Nt; j++) {
				Demand[index][j] = Integer.parseInt(temp3[3 + j]);
			}
			for (int k = 0; k < Ng; k++) {
				initial_inventory[index][k] = Integer.parseInt(temp3[4 + Nt + k]);
			}
		}
		in1.close();
		// --- finish read ---//

		// ---Define some parameters--- //
		int G = Ng; // g from 0.. Ng-1
		int T = Nt + 1; // t from 1...T
						// departures
		int C_large = Nv_large;// each depot have c_large cars
		int C = Nv_small;// each satellite have c cars
		int M = 1000;
		int[] Q_large = new int[Nd * up_l * C_large];
		for (int i = 0; i < Nd * C_large * up_l; i++) {
			Q_large[i] = big_cap;
		}
		int[] Q_small = new int[Ns * up_s * C];
		for (int i = 0; i < Ns * C * up_s; i++) {
			Q_small[i] = small_cap;
		}
		int[][][] hold_cost = new int[N][G][T];
		for (int i = 0; i < N; i++) {
			for (int g = 0; g < G; g++) {
				for (int t = 1; t < T; t++) { // 0->1
					hold_cost[i][g][t] = 10;
				}
			}
		}
		int p1 = 1;
		int p2 = 1;
		int p3 = 1;
		int p4 = 1;
		int p5 = 1;

		// ---Define SET--- //
		ArrayList<Integer> depot = new ArrayList<Integer>();
		ArrayList<Integer> satellite = new ArrayList<Integer>();
		ArrayList<Integer> customer = new ArrayList<Integer>();
		ArrayList<Integer> Cvehicle = new ArrayList<Integer>();

		for (int i = 0; i < Nd; i++) {
			depot.add(i);
		}
		for (int i = Nd; i < Nd + Ns; i++) {
			satellite.add(i);
		}
		for (int i = Nd + Ns; i < Nd + Ns + Nc; i++) {
			customer.add(i);
		}
		for (int i = 0; i < C; i++) { //TODO
			Cvehicle.add(i);
		}

		ArrayList<Integer> d_s = new ArrayList<Integer>();
		for (int i = 0; i < Nd + Ns; i++) {
			d_s.add(i);
		}
		ArrayList<Integer> s_c = new ArrayList<Integer>();
		for (int i = Nd; i < Nd + Ns + Nc; i++) {
			s_c.add(i);
		}

		ArrayList<Integer> LV = new ArrayList<Integer>();
		for (int i = 0; i < Nd * up_l * C_large; i++) {
			LV.add(i);
		}
		ArrayList<Integer> SV = new ArrayList<Integer>();
		for (int i = 0; i < Ns * up_s * C; i++) {
			SV.add(i);
		}

		ArrayList<Integer> Gset_0 = new ArrayList<Integer>();
		ArrayList<Integer> Gset = new ArrayList<Integer>();
		ArrayList<Integer> Tset = new ArrayList<Integer>();
		for (int i = 1; i < G; i++) {
			Gset_0.add(i);
		}
		for (int i = 0; i < G; i++) {
			Gset.add(i);
		}
		for (int i = 1; i < T; i++) {
			Tset.add(i);
		}
		// calculate distance cost Nd/Ns Nd/Nc Ns/Nc
		for (Integer i : depot) {
			for (Integer j : satellite) {
				distance[i][j] = (int)Math.hypot(coorX[i] - coorX[j], coorY[i] - coorY[j]);
				distance[j][i] = distance[i][j];
				minute[i][j] = distance[i][j] / velocity;
				minute[j][i] = minute[i][j];
			}
		}
		for (Integer i : depot) {
			for (Integer j : customer) {
				distance[i][j] = 0;
				distance[j][i] = 0;
			}
		}
		for (Integer i : satellite) {
			for (Integer j : customer) {
				distance[i][j] = (int)Math.hypot(coorX[i] - coorX[j], coorY[i] - coorY[j]);
				distance[j][i] = distance[i][j];
				minute[i][j] = distance[i][j] / velocity;
				minute[j][i] = minute[i][j];
			}
		}
		for (Integer i : customer) {
			for (Integer j : customer) {
				distance[i][j] = (int)Math.hypot(coorX[i] - coorX[j], coorY[i] - coorY[j]);
				distance[j][i] = distance[i][j];
				minute[i][j] = distance[i][j] / velocity;
				minute[j][i] = minute[i][j];
			}
		}

		// ---Define cplex variables--- //
		IloCplex cplex = new IloCplex();
		int variable_count = 0;
		int st_count = 0;
		IloIntVar[][][][] x = new IloIntVar[N][N][SV.size()][T];// i,j,k,t
		IloIntVar[][][] y = new IloIntVar[N][SV.size()][T];// vehicle capacity
		IloIntVar[][][] r = new IloIntVar[N][C][T];//
		IloIntVar[][][] w = new IloIntVar[N][C][T];
		IloIntVar[][][] sl = new IloIntVar[N][T][T];
		IloIntVar[][][] inventory = new IloIntVar[N][G][T];
		IloIntVar[][][][] quantity = new IloIntVar[N][G][SV.size()][T];// j g k
																		// t
		IloIntVar[][][] p = new IloIntVar[N][LV.size()][T];// j k t
		IloIntVar[][][] u = new IloIntVar[N][LV.size()][T];// j k t
		IloIntVar[][][] v = new IloIntVar[N][SV.size()][T];// j k t
		IloIntVar[][][] demand = new IloIntVar[N][G][T];// j g t

		// ---build objective ---//
		IloLinearNumExpr exprObj1 = cplex.linearNumExpr();
		for (int i = 1; i < Nd + Ns + Nc; i++) {
			for (int j = 1; j < Nd + Ns + Nc; j++) {
				for (int k : SV) {
					for (int t = 1; t < T; t++) {
						x[i][j][k][t] = cplex.intVar(0, 1, "x" + i + "," + j + "," + k + "," + t);
						variable_count += 1;
						exprObj1.addTerm(p1 * distance[i][j], x[i][j][k][t]);
					}
				}
			}
		}

		IloLinearNumExpr exprObj2 = cplex.linearNumExpr();
		for (Integer i : s_c) {
			for (int g = 0; g < G; g++) {
				for (int t = 0; t < T; t++) { // 0->1
					inventory[i][g][t] = cplex.intVar(0, Integer.MAX_VALUE, "ven" + i + "," + g + "," + t);
					variable_count += 1;
					exprObj2.addTerm(p2 * hold_cost[i][g][t], inventory[i][g][t]);
				}
			}
		}

		IloLinearNumExpr exprObj3 = cplex.linearNumExpr();
		for (Integer i : s_c) {
			for (Integer k : SV) {
				for (int t = 1; t < T; t++) { // 0->1
					v[i][k][t] = cplex.intVar(0, 1, "v" + i + "," + k + "," + t);
					variable_count += 1;
					if (k % up_s == 0) {
						exprObj3.addTerm(p3, v[i][k][t]);
					}
				}
			}
		}

		IloLinearNumExpr exprObj4 = cplex.linearNumExpr();
		for (Integer i : satellite) {
			for (Integer k : LV) {
				for (int t = 1; t < T; t++) { // 0->1
					u[i][k][t] = cplex.intVar(0, 1, "u" + i + "," + k + "," + t);
					variable_count += 1;
					if (k % up_l == 0) {
						exprObj4.addTerm(p4, u[i][k][t]);
					}
				}
			}
		}

		for (int i = 0; i < N; i++) {
			for (Integer g : Gset) {
				for (int t = 1; t < T; t++) { // 0->1
					demand[i][g][t] = cplex.intVar(0, Integer.MAX_VALUE, "demand" + i + "," + g + "," + t);
					variable_count += 1;
				}
			}
		}
		for (int i = 0; i < N; i++) {
			for (Integer g : Gset) {
				for (Integer k : SV) {
					for (int t = 1; t < T; t++) { // 0->1
						quantity[i][g][k][t] = cplex.intVar(0, Integer.MAX_VALUE,
								"q" + i + "," + g + "," + k + "," + t);
						variable_count += 1;
					}
				}
			}
		}
		for (int i = 0; i < N; i++) {
			for (Integer k : LV) {
				for (int t = 1; t < T; t++) { // 0->1
					p[i][k][t] = cplex.intVar(0, Integer.MAX_VALUE, "p" + i + "," + k + "," + t);
					variable_count += 1;
				}
			}
		}
		for (int i = 0; i < N; i++) {
			for (Integer c : Cvehicle) {
				for (int t = 1; t < T; t++) { // 0->1
					r[i][c][t] = cplex.intVar(0, Integer.MAX_VALUE, "r" + i + "," + c + "," + t);
					variable_count += 1;
				}
			}
		}
		for (int i = 0; i < N; i++) {
			for (Integer c : Cvehicle) {
				for (int t = 1; t < T; t++) { // 0->1
					w[i][c][t] = cplex.intVar(0, 1, "w" + i + "," + c + "," + t);
					variable_count += 1;
				}
			}
		}
		IloLinearNumExpr exprObj5 = cplex.linearNumExpr();
		for (int i = 0; i < N; i++) {
			for (int t1 = 1; t1 < T; t1++) {
				for (int t2 = 1; t2 < T; t2++) { // 0->1
					if (t1 != t2) {
						sl[i][t1][t2] = cplex.intVar(0, 1, "sl" + i + "," + t1 + "," + t2);
						variable_count += 1;
						exprObj5.addTerm(p5, sl[i][t1][t2]);
					}
				}
			}
		}
		for (int i = 0; i < N; i++) {
			for (int k : SV) {
				for (int t = 1; t < T; t++) { // 0->1
					y[i][k][t] = cplex.intVar(0, Integer.MAX_VALUE, "y" + i + "," + k + "," + t);
					variable_count += 1;
				}
			}
		}

		cplex.addMinimize(cplex.sum(exprObj1, exprObj2, exprObj3, exprObj4,exprObj5));

		// System.out.println("Start constraint");
		for (Integer i : s_c) {
			for (Integer g : Gset) {
				cplex.addEq(inventory[i][g][0], initial_inventory[i][g]);
			}
		}
		// --- constraint1 --- //
		for (Integer i : satellite) {
			for (Integer g : Gset_0) {
				for (Integer t : Tset) {
					IloLinearNumExpr constraint1 = cplex.linearNumExpr();
					constraint1.addTerm(1, inventory[i][g][t]);
					constraint1.addTerm(-1, inventory[i][g - 1][t - 1]);
					for (int k = (i - Nd) * up_s * C; k < (i - Nd + 1) * up_s * C; k++) {// sv_i
						for (Integer j : customer) {
							constraint1.addTerm(-1, quantity[j][g][k][t]);
						}
					}
					cplex.addEq(constraint1, 0);
					st_count += 1;
				}
			}
		}
		// --- constraint2 --- //
		for (Integer i : satellite) {
			for (Integer t : Tset) {
				IloLinearNumExpr constraint2 = cplex.linearNumExpr();
				constraint2.addTerm(1, inventory[i][0][t]);
				for (int k = (i - Nd) * up_s * C; k < (i - Nd + 1) * up_s * C; k++) {// sv_i
					for (Integer j : customer) {
						constraint2.addTerm(1, quantity[j][0][k][t]);
					}
				}
				for (int k : LV) {// LV
					constraint2.addTerm(-1, p[i][k][t]);
				}
				cplex.addEq(constraint2, 0);
				st_count += 1;
			}
		}
		// --- constraint2-1 --- //
		for (Integer i : satellite) {
			for (int k : LV) {// LV
				for (Integer t : Tset) {
					IloLinearNumExpr constraint2_1_a = cplex.linearNumExpr();
					IloLinearNumExpr constraint2_1_b = cplex.linearNumExpr();
					IloLinearNumExpr constraint2_1_c = cplex.linearNumExpr();
					constraint2_1_a.addTerm(1, p[i][k][t]);
					constraint2_1_b.addTerm(Q_large[k], u[i][k][t]);
					constraint2_1_c.addTerm(0.5 * Q_large[k], u[i][k][t]);
					cplex.addLe(constraint2_1_a, constraint2_1_b);
					cplex.addGe(constraint2_1_a, constraint2_1_c);
					st_count += 2;
				}
			}
		}
		// --- constraint2-2 --- //
		for (int k : LV) {// LV
			for (Integer t : Tset) {
				IloLinearNumExpr constraint2_2 = cplex.linearNumExpr();
				for (Integer i : satellite) {
					constraint2_2.addTerm(1, u[i][k][t]);
				}
				cplex.addLe(constraint2_2, 1);
				st_count += 1;
			}
		}
		// --- constraint3 --- //
		for (Integer i : customer) {
			for (Integer g : Gset_0) {
				for (Integer t : Tset) {
					IloLinearNumExpr constraint3 = cplex.linearNumExpr();
					constraint3.addTerm(1, inventory[i][g][t]);
					constraint3.addTerm(-1, inventory[i][g - 1][t - 1]);
					for (int k : SV) { // sv_i -> SV
						constraint3.addTerm(-1, quantity[i][g][k][t]);
					}
					constraint3.addTerm(1, demand[i][g][t]);
					cplex.addEq(constraint3, 0);
					st_count += 1;
				}
			}
		}
		// --- constraint4 --- //
		for (Integer i : customer) {
			for (Integer t : Tset) {
				IloLinearNumExpr constraint4 = cplex.linearNumExpr();
				constraint4.addTerm(1, inventory[i][0][t]);
				constraint4.addTerm(1, demand[i][0][t]);
				for (Integer k : SV) {// sv
					constraint4.addTerm(-1, quantity[i][0][k][t]);
				}
				cplex.addEq(constraint4, 0);
				st_count += 1;
			}
		}
		// --- constraint5 --- inventory capacity at Node//
		for (Integer i : s_c) {
			for (Integer t : Tset) {
				IloLinearNumExpr constraint5 = cplex.linearNumExpr();
				for (Integer g : Gset) {
					constraint5.addTerm(1, inventory[i][g][t]);
				}
				cplex.addLe(constraint5, H[i]);
				st_count += 1;
			}
		}
		// --- constraint6 --- Demand shall be split as ages vary//
		for (Integer i : customer) {
			for (Integer t : Tset) {
				IloLinearNumExpr constraint6 = cplex.linearNumExpr();
				for (Integer g : Gset) {
					constraint6.addTerm(1, demand[i][g][t]);
				}
				cplex.addEq(constraint6, Demand[i][t]);
				st_count += 1;
			}
		}
		// --- constraint7 satellite delivery quantity --- //
		for (Integer i : satellite) {
			for (Integer t : Tset) {
				IloLinearNumExpr constraint7 = cplex.linearNumExpr();
				for (Integer k : LV) {
					constraint7.addTerm(1, p[i][k][t]);
				}
				for (Integer g : Gset) {
					constraint7.addTerm(1, inventory[i][g][t - 1]);
				}
				cplex.addLe(constraint7, H[i]);
				st_count += 1;
			}
		}
		// --- constraint8 customer delivery quantity --- //
		for (Integer i : customer) {
			for (Integer t : Tset) {
				IloLinearNumExpr constraint8 = cplex.linearNumExpr();
				for (Integer k : SV) {
					for (Integer g : Gset) {
						constraint8.addTerm(1, quantity[i][g][k][t]);
					}
				}
				for (Integer g : Gset) {
					constraint8.addTerm(1, inventory[i][g][t - 1]);
				}
				cplex.addLe(constraint8, H[i]);
				st_count += 1;
			}
		}
		// --- constraint9 customer delivery quantity --- //
		for (Integer i : customer) {
			for (Integer t : Tset) {
				for (Integer k : SV) {
					IloLinearNumExpr constraint9 = cplex.linearNumExpr();
					constraint9.addTerm(-H[i], v[i][k][t]);
					for (Integer g : Gset) {
						constraint9.addTerm(1, quantity[i][g][k][t]);
					}
					cplex.addLe(constraint9, 0);
					st_count += 1;
				}
			}
		}

		// --- constraint12-2 customer delivery quantity --- //
		for (Integer t : Tset) {
			for (Integer k : SV) {
				IloLinearNumExpr constraint12_2 = cplex.linearNumExpr();
				for (Integer i : customer) {
					for (Integer g : Gset) {
						constraint12_2.addTerm(1, quantity[i][g][k][t]);
					}
				}
				cplex.addLe(constraint12_2, Q_small[k]);
				st_count += 1;
			}
		}

		// --- constraint13 --- visited//
		for (Integer t : Tset) {
			for (Integer i : s_c) {
				IloLinearNumExpr constraint13 = cplex.linearNumExpr();
				for (Integer k : SV) {// vehicle start from satellite
					for (Integer j : s_c) {
						if (i != j) {// System.err.println("i "+i+" j "+j+" k
										// "+k);
							constraint13.addTerm(1, x[i][j][k][t]);
						}
					}
					constraint13.addTerm(-1, v[i][k][t]);
					cplex.addEq(constraint13, 0);
					st_count += 1;
				}
			}
		}

		// --- constraint14 --- In-degree and out-degree Small vehicle
		for (Integer i : customer) {
			for (Integer t : Tset) {
				IloLinearNumExpr constraint14 = cplex.linearNumExpr();
				IloLinearNumExpr constraint15 = cplex.linearNumExpr();
				for (Integer k : SV) {// vehicle start from satellite
					for (Integer j : s_c) {
						if (i != j) {// System.err.println("i "+i+" j "+j+" k
										// "+k);
							constraint14.addTerm(1, x[i][j][k][t]);
							constraint14.addTerm(-1, x[j][i][k][t]);
							constraint15.addTerm(1, x[j][i][k][t]);
						}
					}
					cplex.addEq(constraint14, 0);
				} // System.err.println(constraint1);
				cplex.addEq(constraint15, 1);
				st_count += 2;
			}
		}
		// --- constraint16 --- In-degree and out-degree Small vehicle
		for (Integer i : satellite) {
			for (Integer t : Tset) {
				for (int k = (i - Nd) * up_s * C; k < (i - Nd + 1) * up_s * C; k++) {  // sv_i
					IloLinearNumExpr constraint16 = cplex.linearNumExpr();
					for (Integer j : customer) {
						if (i != j) {// System.err.println("i "+i+" j "+j+" k
										// "+k);
							constraint16.addTerm(1, x[i][j][k][t]);
							constraint16.addTerm(-1, x[j][i][k][t]);
						}
					}
					cplex.addEq(constraint16, 0);
					st_count += 1;
				}
			}
		}
		// --- constraint23 --- 第k+1车次出车必须在第k车次出车之后
		for (Integer i : satellite) {
			for (Integer t : Tset) {
				for (int k = (i - Nd) * up_s * C; k < (i - Nd + 1) * up_s * C; k++) { // sv_i
					if (k % up_s >= 0 && k % up_s <= up_s - 2) {
						IloLinearNumExpr constraint23 = cplex.linearNumExpr();
						for (Integer j : customer) {
							if (i != j) {// System.err.println("i "+i+" j "+j+"
											// k "+k);
								constraint23.addTerm(1, x[i][j][k][t]);
								constraint23.addTerm(-1, x[i][j][k + 1][t]);
							}
						}
						cplex.addGe(constraint23, 0);
						st_count += 1;
					}
				}
			}
		}
		// --- constraint24 --- 每个站点每辆车的出车车次之间的差值小于等于1
		for (Integer t : Tset) {
			for (Integer i : satellite) {
				for (Integer c : Cvehicle) {
					IloLinearNumExpr constraint24 = cplex.linearNumExpr();
					constraint24.addTerm(1, r[i][c][t]);
					for (int k = (i-Nd) * up_s*C + c*up_s; k < (i-Nd) * up_s*C + (c+1)*up_s; k++) { // sv_c
						constraint24.addTerm(-1, v[i][k][t]);
					}
					cplex.addEq(constraint24, 0);
					st_count += 1;
				}
			}
		}
		// --- constraint25 --- 每个站点每辆车的出车车次之间的差值小于等于1
		for (Integer t : Tset) {
			for (Integer i : satellite) {
				for (Integer c1 : Cvehicle) {
					for (Integer c2 : Cvehicle)
						if (c1 != c2) {
							IloLinearNumExpr constraint25 = cplex.linearNumExpr();
							constraint25.addTerm(1, r[i][c1][t]);
							constraint25.addTerm(-1, r[i][c2][t]);
							cplex.addGe(constraint25, -1);
							cplex.addLe(constraint25, 1);
							st_count += 2;
						}
				}
			}
		}
		// define w  26-a
		for (Integer i : customer)
			for (Integer t : Tset) {
				for (Integer c : Cvehicle) {
					IloLinearNumExpr constraint26_a = cplex.linearNumExpr();
					for (int k = c*up_s; k < (c+1)*up_s;k++){
						constraint26_a.addTerm(1, v[i][k][t]);
					}
					cplex.addEq(constraint26_a, w[i][c][t]);
				}
			}
		
		// --- constraint26&27 --- consistency
		for (Integer i : customer) {
			for (Integer c : Cvehicle) {
				for (Integer t1 : Tset) {
					for (Integer t2 : Tset) {
						IloLinearNumExpr constraint26 = cplex.linearNumExpr();
						IloLinearNumExpr constraint27 = cplex.linearNumExpr();
						if (t1 != t2) {
							for (Integer k : SV) {
								constraint26.addTerm(1, v[i][k][t1]);
								constraint26.addTerm(1, v[i][k][t2]);
								constraint27.addTerm(-1, v[i][k][t1]);
								constraint27.addTerm(-1, v[i][k][t2]);
							}
							constraint26.addTerm(1, w[i][c][t1]);
							constraint26.addTerm(-1, w[i][c][t2]);
							constraint26.addTerm(-1, sl[i][t1][t2]);
							constraint27.addTerm(1, w[i][c][t1]);
							constraint27.addTerm(-1, w[i][c][t2]);
							constraint27.addTerm(1, sl[i][t1][t2]);
							cplex.addLe(constraint26, 2);
							cplex.addGe(constraint27, -2);
							st_count += 2;
						}
					}
				}
			}
		}

		// --- constraint28 --- consistency
		for (Integer i : satellite) {
			for (Integer t : Tset) {
				for (Integer k : SV) {
					if (k % up_s == 0) {
						IloLinearNumExpr constraint28 = cplex.linearNumExpr();
						constraint28.addTerm(1, y[i][k][t]);
						cplex.addEq(constraint28, 0);
						st_count += 1;
					}
				}
			}
		}
		// --- constraint29 --- consistency
		for (Integer i : satellite) {
			for (Integer j : customer) {
				for (Integer t : Tset) {
					for (Integer k : SV) {
						if (k % up_s != 0) {
							IloLinearNumExpr constraint29_1 = cplex.linearNumExpr();
							IloLinearNumExpr constraint29_2 = cplex.linearNumExpr();
							constraint29_1.addTerm(1, y[i][k][t]);
							constraint29_1.addTerm(-1, y[j][k - 1][t]);
							constraint29_1.addTerm(M, x[j][i][k - 1][t]);
							constraint29_2.addTerm(1, y[i][k][t]);
							constraint29_2.addTerm(-1, y[j][k - 1][t]);
							constraint29_2.addTerm(-M, x[j][i][k - 1][t]);
							cplex.addLe(constraint29_1, minute[j][i] + M);
							cplex.addGe(constraint29_2, minute[j][i] - M);
							st_count += 2;
						}
					}
				}
			}
		}
		// --- constraint30 --- consistency
		for (Integer i : customer) {
			for (Integer j : s_c) {
				for (Integer t : Tset) {
					for (Integer k : SV) {
						IloLinearNumExpr constraint30_1 = cplex.linearNumExpr();
						IloLinearNumExpr constraint30_2 = cplex.linearNumExpr();
						constraint30_1.addTerm(1, y[i][k][t]);
						constraint30_1.addTerm(-1, y[j][k][t]);
						constraint30_1.addTerm(M, x[j][i][k][t]);
						constraint30_2.addTerm(1, y[i][k][t]);
						constraint30_2.addTerm(-1, y[j][k][t]);
						constraint30_2.addTerm(-M, x[j][i][k][t]);
						cplex.addLe(constraint30_1, minute[j][i] + M);
						cplex.addGe(constraint30_2, minute[j][i] - M);
						st_count += 2;
					}
				}
			}
		}
		// --- constraint31 --- consistency
		for (Integer i : customer) {
			for (Integer t1 : Tset) {
				for (Integer t2 : Tset) {
					IloLinearNumExpr constraint31_1 = cplex.linearNumExpr();
					IloLinearNumExpr constraint31_2 = cplex.linearNumExpr();
					if (t1 != t2) {
						for (Integer k : SV) {
							constraint31_1.addTerm(1, y[i][k][t1]);
							constraint31_1.addTerm(-1, y[i][k][t2]);
							constraint31_1.addTerm(Tmax, v[i][k][t1]);
							constraint31_1.addTerm(Tmax, v[i][k][t2]);

							constraint31_2.addTerm(1, y[i][k][t1]);
							constraint31_2.addTerm(-1, y[i][k][t2]);
							constraint31_2.addTerm(-Tmax, v[i][k][t1]);
							constraint31_2.addTerm(-Tmax, v[i][k][t2]);
						}
						cplex.addLe(constraint31_1, L + 2 * Tmax);
						cplex.addGe(constraint31_2, -L - 2 * Tmax);
						st_count += 2;
					}
				}
			}
		}
		cplex.exportModel("./vrp-con-data/VRP_model/tvrp.lp");

		// --- constraint --- variable equals zero ???

		// --- constraint Finish ---//
		System.out.println("variable_count: " + variable_count);
		System.out.println("st_count: " + st_count);
		System.out.println("Finish build model");
		// if(sout){cplex.setOut(null);
		double robust_obj = 0;
		cplex.solve();
		/*
		 * try { cplex.solve(); obj=cplex.getObjValue();
		 * //System.out.println("The optimal objValue is "+String.valueOf(obj));
		 * boolean feasible=cplex.solve(); if(feasible){
		 * //System.err.println(obj); } } catch (IloException ex) {
		 * System.err.println("\n!!!Unable to solve the model:\n" +
		 * ex.getMessage() + "\n!!!"); System.exit(2); }
		 */

	}
}
