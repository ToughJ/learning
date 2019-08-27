package vrp_ec;

import ilog.concert.*;
import ilog.cplex.*;

//  第一版



/*
 * x 应该变一下    xijkl   k depot l vehicle  i   customer j order
 * 
 */

public class vrpCplexTest {
	public static double[][] w1,w2;
	static double[] d;
	static double[] t;
	static int numCus;
	static int numDep;
	static double M1=400;
	static double M2=2000;
	static double T1=480;
	static double T2=0;
	static double v = 0.66;          //km/min

	private static void randomlyGen(){
		numCus = 10;
		numDep = 1;
		w1 = new double[numCus+1][numCus+1];
		w2 = new double[numDep+1][numCus+1];
		t = new double[numCus+1];
		d = new double[numCus+1];
		for (int i = 1; i <= numCus; i++){
			t[i] = Math.random()*10;
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
	
	private static void creatCplexModel() throws IloException {
		IloCplex cplex = new IloCplex();
		int numCar = 5;
		int cons = 1;
		IloNumVar[][][][] x = new IloNumVar[numCus+2][numCus+1][numDep+1][numCar+1];
		IloNumVar[][] z = new IloNumVar[numDep+1][numCar+1];
		
		// variables
		for (int i = 0; i <= numCus; i++) {          //0代表depot  在起点和终点，形成一个封闭的环路。
	 		IloLinearNumExpr s3 = cplex.linearNumExpr();
	 		for (int j = 0; j <= numCus; j++)  {
	 			for (int k = 1; k <= numDep; k++) {
	 				      
	 				for (int l = 0; l <= numCar; l++) {
	 					x[i][j][k][l] = cplex.boolVar("x"+i+j+k+l);
	 					if(i!=j &&  i != 0) s3.addTerm(1, x[i][j][k][l]);
	 				//	if (i!=j && l!=0) cplex.addLe(x[i][j][k][l],x[i][j][k][l-1],cons++ + " l<=l-1");
	 				}
	 				
	 			}
	 		}
	 		if(i != 0) cplex.addEq(1, s3,cons++ + " =1");
		}
		
		//expressions
		IloLinearNumExpr objective = cplex.linearNumExpr();      
		for (int k = 1; k <= numDep; k++) {
			for (int l = 0; l <= numCar; l++) {
				z[k][l] = cplex.boolVar("z"+k+l);          //z var or exp?
				IloLinearNumExpr s7 = cplex.linearNumExpr();
				for (int i = 1; i<=numCus;i++){
					s7.addTerm(1, x[i][0][k][l]);
				}
				cplex.addEq(s7, z[k][l]);
			}
		}
		

		// define constraints
		for (int l = 0; l <= numCar; l++) {        
	 		for (int k = 1; k <= numDep; k++) {
	 			objective.addTerm(1,z[k][l]);
	 		}
		}
		// define objective
		cplex.addMinimize(objective);

		for (int k = 1; k <= numDep; k++) {
			IloLinearNumExpr s5 = cplex.linearNumExpr(); 
			for (int l = 0; l <= numCar; l++) {   
				IloLinearNumExpr s4 = cplex.linearNumExpr(); 
				IloLinearNumExpr s6 = cplex.linearNumExpr(); 
				for (int i = 0; i <= numCus; i++) {
					IloLinearNumExpr s1 = cplex.linearNumExpr();      
					IloLinearNumExpr s2 = cplex.linearNumExpr(); 
					for (int j = 0; j <= numCus; j++) if(i!=j){
						s1.addTerm(1, x[i][j][k][l]);
						s2.addTerm(1, x[j][i][k][l]);
						if (j!=0) {
							s4.addTerm(d[j], x[i][j][k][l]);
							if (i!=0) 
								s6.addTerm((w1[i][j]/v)+t[j], x[j][i][k][l]);
							else
								s6.addTerm((w2[k][j]/v)+t[j], x[j][i][k][l]);
						}
	 					cplex.addLe(x[i][j][k][l], z[k][l],cons++ +  " x<z");
	 				}
					cplex.addEq(s1, s2,cons++ +  " x=x");
	 			}
				cplex.addLe(s6, T1,cons++ +  " T1");
				cplex.addGe(s6, T2,cons++ +  " T2");
				s5.add(s4);
				cplex.addLe(s4, M1,cons++ + " M1");  
	 		}
			cplex.addLe(s5, M2,cons++ + " M2");
		}

		cplex.exportModel("sex.lp");
		if(cplex.solve()){
			System.out.println("obj = "+cplex.getObjValue());
			for (int i = 0; i <= numCus; i++) {         
		 		for (int j = 0; j <= numCus; j++) if(i!=j){
		 			for (int k = 1; k <= numDep; k++) {
		 				for (int l = 0; l <= numCar; l++) {
		 					if (cplex.getValue(x[i][j][k][l]) ==1)
		 						System.out.println(" x "+i+" "+j+" "+k+" "+l );	
		 				}
		 			}
		 		}
			}
			for (int l = 0; l <= numCar; l++) {        
		 		for (int k = 1; k <= numDep; k++) {
		 			System.out.println(" z "+k+" "+l + " "+cplex.getValue(z[k][l]));	
		 		}
			}
		}
		
		cplex.end();
	}

	//test 
	public static void main(String[] args) throws IloException{
		randomlyGen();
		creatCplexModel();
	}
}