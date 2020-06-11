package vrp_withJ;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import ilog.concert.*;
import ilog.cplex.*;

/**
 * 2019-11-19 the problem is developed by the problem proposed with TJ.
 * 
 */

public class vrp_cplex {
	private double[][] disCusCus,disCusSat,disSatSat,disSatDep;
	private double[] demand;
	private int numCus, numSat, numDep;
	private double smallCap, largeCap;
	private double p1, p2, p3 ,p4;
	ArrayList<Node> nodeList;

	final int M = 99999;

	public vrp_cplex() {
		nodeList = new ArrayList<>();
	}

	public void setParameter(FileLoader fL) {
		this.smallCap = fL.carDem;
		this.largeCap = fL.truckDem;
		this.numCus = fL.numCus;
		this.numSat = fL.numSat;
	}

//	public void setNodeList(ArrayList<Node> arrNode) {
//		this.nodeList.addAll(arrNode);
//		// for (Node a : nodeList) {
//		// dem += a.demand;
//		// }
//		distanceMatrix = new double[nodeList.size()][nodeList.size()];
//		for (int i = 0; i < nodeList.size(); i++) {
//			Node from = nodeList.get(i);
//			for (int j = i; j < nodeList.size(); j++) {
//				Node to = nodeList.get(j);
//				double Delta_x = (from.x - to.x);
//				double Delta_y = (from.y - to.y);
//				double distance = Math.sqrt((Delta_x * Delta_x) + (Delta_y * Delta_y));
//
//				distance = Math.round(distance);
//				distanceMatrix[i][j] = distance;
//				distanceMatrix[j][i] = distance;
//			}
//		}
//	}

	private void creatCplexModel(String url) throws IloException, FileNotFoundException {
		IloCplex cplex = new IloCplex();
		IloLinearNumExpr objective = cplex.linearNumExpr();

		// variables
		IloNumVar[][][] x = new IloNumVar[numCus + 1][numCus + 1][numSat + 1];
		IloNumVar[][][] y = new IloNumVar[numSat + 1][numSat + 1][numDep + 1];
		for (int i = 0; i <= numCus; i++) {
			for (int j = 0; j <= numCus; j++) {
				for (int k = 1; k <= numSat; k++) {
					x[i][j][k] = cplex.boolVar("x" + i + j + k);
				}
			}
		}
		for (int i = 0; i <= numSat; i++) {
			for (int j = 0; j <= numSat; j++) {
				for (int k = 1; k <= numDep; k++) {
					y[i][j][k] = cplex.boolVar("y" + i + j + k);
				}
			}
		}
		// expressions
		IloLinearNumExpr[] smallVelNum = new IloLinearNumExpr[numSat + 1];
		IloLinearNumExpr[] largeVelNum = new IloLinearNumExpr[numDep + 1];

		for (int k = 1; k <= numSat; k++) {
			smallVelNum[k] = cplex.linearNumExpr();
			for (int j = 0; j <= numCus; j++) {
				smallVelNum[k].addTerm(p1, x[0][j][k]);
			}
			objective.add(smallVelNum[k]);
		}
		for (int k = 1; k <= numDep; k++) {
			largeVelNum[k] = cplex.linearNumExpr();
			for (int j = 0; j <= numSat; j++) {
				largeVelNum[k].addTerm(p2, y[0][j][k]);
			}
			objective.add(largeVelNum[k]);
		}
		

		for (int i = 1; i <= numCus; i++) {
			IloLinearNumExpr inDeg = cplex.linearNumExpr();
			IloLinearNumExpr outDeg = cplex.linearNumExpr();
			for (int j = 0; j <= numCus; j++)
				if (j != i) {
					for (int k = 1; k <= numSat; k++) {
						inDeg.addTerm(1, x[j][i][k]);
						outDeg.addTerm(1, x[i][j][k]);
					}
				}
			cplex.addEq(inDeg, 1, "S:inDeg=1 _ " + i);
			cplex.addEq(inDeg, outDeg, "S:inDeg=outDeg _ " + i);
		}
		for (int i = 1; i <= numSat; i++) {
			IloLinearNumExpr inDeg = cplex.linearNumExpr();
			IloLinearNumExpr outDeg = cplex.linearNumExpr();
			for (int j = 0; j <= numSat; j++)
				if (j != i) {
					for (int k = 1; k <= numDep; k++) {
						inDeg.addTerm(1, y[j][i][k]);
						outDeg.addTerm(1, y[i][j][k]);
					}
				}
			cplex.addEq(inDeg, 1, "L:inDeg=1 _ " + i);
			cplex.addEq(inDeg, outDeg, "L:inDeg=outDeg _ " + i);
		}

		IloLinearNumExpr[] smallDemand = new IloLinearNumExpr[numCus + 1];
		IloLinearNumExpr[] largeDemand = new IloLinearNumExpr[numSat + 1];
		IloLinearNumExpr[] grossDemand = new IloLinearNumExpr[numSat + 1];
		for (int k = 1; k <= numSat; k++) {
			for (int i = 1; i <= numCus; i++) {
				for (int j = 0; j <= numCus; j++)
					if (j != i) {
						grossDemand[k].addTerm(demand[i], x[j][i][k]);
					}
			}
		}
		for (int i = 0; i <= numCus; i++) {
			smallDemand[i] = cplex.linearNumExpr();
		}
		for (int i = 0; i <= numSat; i++) {
			largeDemand[i] = cplex.linearNumExpr();
		}
		cplex.addEq(smallDemand[0], 0, "S:demand_0");
		cplex.addEq(largeDemand[0], 0, "L:demand_0");
		for (int i = 1; i <= numCus; i++) {
			for (int j = 0; j <= numCus; j++)
				if (i != j) {
					IloLinearNumExpr isIncr = cplex.linearNumExpr();
					for (int k = 1; k <= numSat; k++) {
						isIncr.addTerm(1, x[j][i][k]);
					}
					cplex.addGe(cplex.diff(smallDemand[i], smallDemand[j]),
							cplex.sum(demand[i], cplex.prod(M, cplex.diff(1, isIncr))), "S:demand_" + i + "_" + j);
				}
			cplex.addLe(smallDemand[i], smallCap, "S:capacity_" + i);
		}
		for (int i = 1; i <= numSat; i++) {
			for (int j = 0; j <= numSat; j++)
				if (i != j) {
					IloLinearNumExpr isIncr = cplex.linearNumExpr();
					for (int k = 1; k <= numDep; k++) {
						isIncr.addTerm(1, y[j][i][k]);
					}
					cplex.addGe(cplex.diff(largeDemand[i], largeDemand[j]),
							cplex.sum(grossDemand[i], cplex.prod(M, cplex.diff(1, isIncr))),"L:demand_" + i + "_" + j);
				}
			cplex.addLe(largeDemand[i], largeCap, "S:capacity_" + i);
		}

		// define objective
		
		for (int i = 1;i<=numCus;i++){
			for (int j =0;j<=numCus;j++) if(i!=j){
				for (int k=1;k<=numSat;k++){
					if (j == 0) {
						objective.addTerm(p3*disCusSat[i][k], x[i][j][k]);
					}
					else {
						objective.addTerm(p3*disCusCus[i][j], x[i][j][k]);
					}
				}
			}
		}
		for (int i = 1;i<=numSat;i++){
			for (int j =0;j<=numSat;j++) if(i!=j){
				for (int k=1;k<=numDep;k++){
					if (j == 0) {
						objective.addTerm(p4*disSatDep[i][k], y[i][j][k]);
					}
					else {
						objective.addTerm(p4*disSatSat[i][j], y[i][j][k]);
					}
				}
			}
		}
		cplex.addMinimize(objective);

		cplex.exportModel("typeOne.lp");
		if (cplex.solve()) {
			url = "./vrp-data/delta/" + url;
			PrintWriter printer = new PrintWriter(url);
			printer.println("obj = " + cplex.getObjValue());
//			for (int i = 0; i <= numCus; i++) {
//				for (int j = 0; j <= numCus; j++)
//					if (i != j) {
//						for (int k = 1; k <= numDep; k++) {
//							if (cplex.getValue(x[i][j][k]) == 1)
//								printer.println(" x " + i + " " + j + " " + k);
//						}
//					}
//			}
//			for (int k = 1; k <= numDep; k++) {
//				printer.println(" z " + k + " " + String.format("%.2f", cplex.getValue(z[k])));
//			}
//			for (int i = 1; i <= numCus; i++) {
//				printer.println(" d " + i + " " + String.format("%.2f", cplex.getValue(yd[i])));
//			}
			printer.close();
		}

		cplex.end();
	}

}
