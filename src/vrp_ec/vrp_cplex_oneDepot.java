package vrp_ec;

import java.io.FileNotFoundException;
import java.io.IOException;

import ilog.concert.*;
import ilog.cplex.*;
import vrp_GA.FileLoader;

//  这是基础版  one-depot CVRP 问题

public class vrp_cplex_oneDepot {

	static double[] d;
	static double[][] c;

	static int numCus;
	static double Q1=85;
	static int M=99999;
	static int numCar = 4;
	
	private static void randomlyGen(){
		numCus = 30;
		c = new double[numCus+1][numCus+1];
		d = new double[numCus+1];
		for (int i = 1; i <= numCus; i++){
        	d[i] = Math.random()*100;
        	for(int j = 0; j <= numCus; j++){
        		if (j==i)
        			c[i][j] =0;
        		else{
        			c[i][j] = Math.random()*100;
        			c[j][i] = c[i][j];
        		}
        	}
		}
	}
	
	private static void creatCplexModel() throws IloException {
		IloCplex cplex = new IloCplex();
		
		int cons = 1;
		IloNumVar[][] x = new IloNumVar[numCus+1][numCus+1];
		IloIntVar[] yd = new IloIntVar[numCus+1];
		
		for (int i = 0; i <= numCus; i++) { 
	 		yd[i] =cplex.intVar(0, M);
	 		cplex.addGe(yd[i], 0,cons++ + " =11");
		}
		// variables
		
		for (int i = 0; i <= numCus; i++) {          //0代表depot  在起点和终点，形成一个封闭的环路。
	 		for (int j = 0; j <= numCus; j++)  {
	 			x[i][j] = cplex.boolVar("x"+i+j);
			}
		}
	 		
		IloLinearNumExpr objective = cplex.linearNumExpr();
		for (int i = 0; i <= numCus; i++) {          //0代表depot  在起点和终点，形成一个封闭的环路。
	 		IloLinearNumExpr s3 = cplex.linearNumExpr();
	 		IloLinearNumExpr s4 = cplex.linearNumExpr();
	 		for (int j = 0; j <= numCus; j++)  {
	 			if(i!=j &&  i != 0) {
	 					s3.addTerm(1, x[i][j]);	
	 					s4.addTerm(1, x[j][i]);	
	 			}

	 			if(j!=0){
	 				cplex.addGe(cplex.diff(yd[i], yd[j]), 
 							cplex.diff(d[j],cplex.prod(M, cplex.diff(1, x[i][j]))),cons++ + " =8");
	 			}
	 			
	 			objective.addTerm(c[i][j], x[i][j]);
	 		}
	 		if(i != 0) {
	 			cplex.addEq(1, s3,cons++ + " =3");
	 			cplex.addEq(1, s4,cons++ + " =4");
	 		}
		}
		cplex.addEq(Q1, yd[0]);
		
		//expressions
		
		IloLinearNumExpr s2 = cplex.linearNumExpr();
		IloLinearNumExpr s1 = cplex.linearNumExpr();
		for (int i = 1; i<=numCus;i++){
			s2.addTerm(1, x[i][0]);
			s1.addTerm(1, x[0][i]);
		}
		cplex.addEq(s1, s2);
		cplex.addLe(s1, numCar,cons++ + " =14");
		

		cplex.addMinimize(objective);


		cplex.exportModel("two.lp");
		if(cplex.solve()){
			System.out.println("obj = "+cplex.getObjValue());
			for (int i = 0; i <= numCus; i++) {         
		 		for (int j = 0; j <= numCus; j++) if(i!=j){
		 			if (cplex.getValue(x[i][j]) ==1)
		 				System.out.println(" x "+i+" "+j);	
		 			
		 		}
			}     
		}
		
		cplex.end();
	}

	//test 
	public static void main(String[] args) throws IloException, FileNotFoundException, IOException{
//		randomlyGen();
		fileGen();
		creatCplexModel();
	}
	
	public static void fileGen() throws FileNotFoundException, IOException{
		FileLoader fL=new FileLoader();
        fL.loadNodeInfo("Node-info - 5.txt"); 
		int size = fL.getArrNode().size();
		numCus = size-1;
		c = new double[numCus+1][numCus+1];
		d = new double[numCus+1];
		
		for (int i = 0; i < size; i++){
			c[i][i] = 0;
			d[i] = fL.getArrNode().get(i).getDemand();
			for (int j = i+1; j < size; j++){
				c[i][j] = Math.sqrt(Math.pow(fL.getArrNode().get(i).getX()-fL.getArrNode().get(j).getX(), 2)
						            +Math.pow(fL.getArrNode().get(i).getY()-fL.getArrNode().get(j).getY(), 2));
				c[j][i] = c[i][j];
			}
		}
	}
}