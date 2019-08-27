package vrp_ec;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import ilog.concert.*;
import ilog.cplex.*;

//  第三版是错误的 这是第四版
//  目前可用，但是太慢了。

public class vrpCplex4 {
	public static double[][] w1,w2;
	static double[] d;
//	static double[] t;
	static int numCus;
	static int numDep;
	static double Q1=400;
//	static double Q2=2000;
//	static double T1=480;
//	static double T2=0;
	static int M=99999;
	static double v = 0.66;          //km/min

	private static void randomlyGen(){
		numCus = 10;
		numDep = 4;
		w1 = new double[numCus+1][numCus+1];
		w2 = new double[numCus+1][numDep+1];
//		t = new double[numCus+1];
		d = new double[numCus+1];
		for (int i = 1; i <= numCus; i++){
//			t[i] = Math.random()*10;
        	d[i] = Math.random()*100;
		}
		for (int i = 1; i <= numCus; i++) {
			for (int j = i+1; j <= numCus; j++)  if(i!=j){
				w1[i][j] = Math.random()*30;
				w1[j][i]=w1[i][j];
			}
			for (int j = 1; j <= numDep; j++)  {
				w2[i][j] = Math.random()*30;
			}
		}
	}
	
	private static void creatCplexModel(String url) throws IloException, FileNotFoundException {
		IloCplex cplex = new IloCplex(); 
		int numCar = 7;
		int cons = 1;
		IloNumVar[][][] x = new IloNumVar[numCus+2][numCus+2][numDep+1];
//		IloNumVar[] yt = new IloNumVar[numCus+2];
		IloNumVar[] yd = new IloNumVar[numCus+2];
		
		for (int i = 0; i <= numCus; i++) { 
//			yt[i] =cplex.numVar(0, M);
	 		yd[i] =cplex.numVar(0, Q1);
//	 		cplex.le(yd[i], Q1,cons++ + " =11");
		}
		
		for (int i = 0; i <= numCus; i++) {          //0代表depot  在起点和终点，形成一个封闭的环路。
	 		for (int j = 0; j <= numCus; j++)  {
	 			for (int k = 1; k <= numDep; k++) {
	 				x[i][j][k] = cplex.boolVar("x"+i+j+k);
	 			}
			}
		}
	 		
		// variables
		for (int i = 0; i <= numCus; i++) {          //0代表depot  在起点和终点，形成一个封闭的环路。
	 		IloLinearNumExpr s3 = cplex.linearNumExpr();
	 		IloLinearNumExpr s4 = cplex.linearNumExpr();
	 		for (int j = 0; j <= numCus; j++)  {
	 			IloLinearNumExpr s5 = cplex.linearNumExpr();
	 			for (int k = 1; k <= numDep; k++) {
	 				if(i!=j &&  i != 0) {
	 					s3.addTerm(1, x[i][j][k]);	
	 					s4.addTerm(1, x[j][i][k]);	
	 					s5.addTerm(1, x[i][j][k]);
	 				}
//	 				cplex.addGe(yt[i], 
// 							cplex.diff(w2[i][k]/v+t[i],cplex.prod(M, cplex.diff(1, x[0][i][k]))),cons++ + " =5");

	 				cplex.addGe(yd[i], 
 							cplex.diff(d[i],cplex.prod(M, cplex.diff(1, x[i][0][k]))),cons++ + " =8");

	 			}
	 			if(i!=0&&j!=0){

//	 				cplex.addGe(cplex.diff(yt[i], yt[j]), 
//	 						cplex.diff(w1[i][j]/v+t[i],cplex.prod(M, cplex.diff(1, s5))),cons++ + " =5");

	 				cplex.addGe(cplex.diff(yd[i], yd[j]), 
 							cplex.diff(d[i],cplex.prod(M, cplex.diff(1, s5))),cons++ + " =8");

	 			}
	 		}
	 		if(i != 0) {
	 			cplex.addEq(1, s3,cons++ + " =3");
	 			cplex.addEq(1, s4,cons++ + " =4");
	 		}
		}
		
		//expressions
		IloLinearNumExpr objective = cplex.linearNumExpr();
		IloLinearNumExpr[] z = new IloLinearNumExpr[numDep+1];
		for (int k = 1; k <= numDep; k++) { 
			IloLinearNumExpr s2 = cplex.linearNumExpr();
			z[k] = cplex.linearNumExpr();
			for (int i = 1; i<=numCus;i++){
				s2.addTerm(100, x[i][0][k]);
				z[k].addTerm(1, x[i][0][k]);
			}
			objective.add(s2);
			cplex.addLe(z[k], numCar,cons++ + " =14");
		}
		
		for (int k = 1; k <= numDep; k++) {
			for (int j = 1; j <= numCus; j++) {
				IloLinearNumExpr s1l = cplex.linearNumExpr();
				IloLinearNumExpr s1r = cplex.linearNumExpr();
				for (int i = 0; i <= numCus; i++) if(i!=j){
					s1l.addTerm(1, x[i][j][k]);
					s1r.addTerm(1, x[j][i][k]);
				}
				cplex.addEq(s1l, s1r,cons++ + " =1");
			}
		}
		
		for (int k = 1; k <= numDep; k++) {
			for (int j = 0; j <= numCus; j++) {
				for (int i = 0; i <= numCus; i++) if(i!=j){
					if (i == 0)
						objective.addTerm(w2[j][k], x[i][j][k]);
					else if (j == 0)
						objective.addTerm(w2[i][k], x[i][j][k]);
					else
						objective.addTerm(w1[i][j], x[i][j][k]);
				}
			}
		}
		// define objective
		cplex.addMinimize(objective);
		
//		for (int j = 1; j <= numCus; j++){
//
//			for (int k = 1; k<= numDep; k++){
//				cplex.addLe(yt[j],  T1-w2[j][k]/v,cons++ + " =12");
//				cplex.addGe(yt[j], 
//						cplex.diff((T1-w2[j][k]/v),cplex.prod(M, cplex.diff(1, x[j][0][k]))),cons++ + " =13");
//			}
//		}

		cplex.exportModel("two.lp");
		if(cplex.solve()){
		 	url = "C:/learning part//operationResearchh//code/learning/vrp-data/multi_depot/" + url;
		 	PrintWriter printer = new PrintWriter(url);
		 	printer.println("obj = "+cplex.getObjValue());
			for (int i = 0; i <= numCus; i++) {         
		 		for (int j = 0; j <= numCus; j++) if(i!=j){
		 			for (int k = 1; k <= numDep; k++) {
		 				if (cplex.getValue(x[i][j][k]) ==1)
		 					printer.println(" x "+i+" "+j+" "+k );	
		 			}
		 		}
			}     
		 	for (int k = 1; k <= numDep; k++) {
		 		printer.println(" z "+k+" " +String.format("%.2f", cplex.getValue(z[k])));	
		 	}
		 	for (int i = 1; i <= numCus; i++){
		 		printer.println(" d "+i+" " + String.format("%.2f", cplex.getValue(yd[i])) );
		 	}
		 	printer.close();
		}
		
		cplex.end();
	}


	//test 
	public static void main(String[] args) throws IloException, FileNotFoundException, IOException{
//		randomlyGen();
		input_data in = new input_data();
		in.loadNodeInfo("Node-info-MD  - 4.txt");
		setParameter(in);
		
		creatCplexModel("cplex-output-MD  - 4.txt");
		System.out.println("done");
	}

	private static void setParameter(input_data in) {
		w1 = in.getw1();
		w2 = in.getw2();
		d = in.getDemand();
		numCus = in.numCus;
		numDep = in.numDep;
		Q1 = in.carDem;
	}
}