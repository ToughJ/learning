package vrp_ec;

import ilog.concert.*;
import ilog.cplex.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import cplexAndJava.Example_2;


public class vrpCplex {
	public static Map<Integer,Depot> all_depots = new HashMap<Integer,Depot>();
	public static Map<Integer,Customer> all_customers = new HashMap<Integer,Customer>();
	public static Map<Integer,Node> all_nodes = new HashMap<Integer,Node>();
	public static double[][] w;
	static int numCus;
	static int numDep;
	static double M1=500000;
	static double M2=200000;
	static double T1=4800;
	static double T2=0;
	static double v = 2400;          //km/min

	private String instance;
	private static class Node {    
		//  Depot, Customer
		public int id;
		public int id_external;
		private double xcoord;
		private double ycoord;
		private double t_at_node;
		public Node(int external_id, double x, double y, double t) {
			this.id = all_nodes.size();
			this.id_external = external_id;
			this.xcoord = x;
			this.ycoord = y;
			this.t_at_node = t;
			all_nodes.put(this.id, this);
		}
		public double time_to_node(Node node_to) {   // node to node
			return Math.sqrt(Math.pow(this.xcoord-node_to.xcoord, 2)+Math.pow(this.ycoord-node_to.ycoord, 2));
		}
		public double time_at_node() {    //SERVICE TIME
			return t_at_node;
		}
	}
	private static class Depot extends Node {
		public Depot(int external_id, double x, double y) {
			super(external_id,x,y,0);
			all_depots.put(this.id_external, this);
		}
	}
	private static class Customer extends Node{
		private double demand;
		public Customer(int external_id, double x, double y, double demand, double service_time) {
			super(external_id,x,y,service_time);
			this.demand = demand;
			all_customers.put(this.id_external, this);
		}
		public double d() {
			return demand;
		}
	}

	private static void randomlyGen(){
		numCus = 20;
		numDep = 3;
		w = new double[numCus+numDep+5][numCus+numDep+5];
		for (int i = 1; i <= numDep; i++){
			int idEx = i;
        	double xCoord = Math.random()*20;
        	double yCoord = Math.random()*20;
        	new Depot(idEx,xCoord,yCoord); 
		}
		
		for (int i = 1; i <= numCus; i++){
			int idEx = i;
        	double xCoord = Math.random()*20;
        	double yCoord = Math.random()*20;
        	double demand = Math.random()*100;
        	double serviceTime = Math.random()*10;
        	new Customer(idEx,xCoord,yCoord,demand,serviceTime); 
		}
		
		for (int i : all_nodes.keySet()) {
			for (int j : all_nodes.keySet()) {
				double w_ij = all_nodes.get(i).time_at_node()+all_nodes.get(i).time_to_node(all_nodes.get(j));
				w[i][j] = w_ij;
				w[j][i] = w_ij;
			}
		}
	}
	
	private static void creatCplexModel() throws IloException {
		IloCplex cplex = new IloCplex();
		int numCar = 5;
		IloNumVar[][][][] x = new IloNumVar[numCus+2][numCus+1][numDep+1][numCar+1];
		IloLinearNumExpr[][] z = new IloLinearNumExpr[numDep+1][numCar+1];
		
		// variables
		for (int i = 0; i <= numCus; i++) {          //0代表depot  在起点和终点，形成一个封闭的环路。
	 		for (int j = 0; j <= numCus; j++) {
	 			for (int k = 1; k <= numDep; k++) {
	 				IloLinearNumExpr s3 = cplex.linearNumExpr();      
	 				for (int l = 0; l <= numCar; l++) {
	 					x[i][j][k][l] = cplex.boolVar("x"+i+j+k+l);
	 					if(i != 0) s3.addTerm(1, x[i][j][k][l]);
	 					if (l!=0) cplex.addLe(x[i][j][k][l],x[i][j][k][l-1],"l<=l-1");
	 				}
	 				if(i != 0) cplex.addEq(1, s3,"=1");
	 			}
	 		}
		}
		
		//expressions
		IloLinearNumExpr objective = cplex.linearNumExpr();      
		for (int k = 1; k <= numDep; k++) {
			for (int l = 0; l <= numCar; l++) {
				z[k][l] = cplex.linearNumExpr();            //z var or exp?
				
			}
		}
		
		
		

		// define constraints
		for (int l = 0; l <= numCar; l++) {        
	 		for (int k = 1; k <= numDep; k++) {
	 			for (int i = 0; i <= numCus; i++) {  
	 				z[k][l].addTerm(1, x[i][0][k][l]);
	 				objective.add(z[k][l]);
	 			}
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
					for (int j = 0; j <= numCus; j++) {
						if (j!=0) {
							s1.addTerm(1, x[i][j][k][l]);
							s2.addTerm(1, x[j][i][k][l]);
							s4.addTerm(all_customers.get(j).d(), x[i][j][k][l]);
							if (i!=0) 
								s6.addTerm((w[i+numDep][j+numDep]/v)+all_customers.get(j).time_at_node(), x[j][i][k][l]);
							else
								s6.addTerm((w[k][j+numDep]/v)+all_customers.get(j).time_at_node(), x[j][i][k][l]);
						}
	 					cplex.addLe(x[i][j][k][l], z[k][l],"x<z");
	 				}
					cplex.addEq(s1, s2,"x=x");
	 			}
				cplex.addLe(s6, T1,"T1");
				cplex.addGe(s6, T2,"T2");
				s5.add(s4);
				cplex.addLe(s4, M1,"M1");  
	 		}
			cplex.addLe(s5, M2,"M2");
		}

		if(cplex.solve()){
			System.out.println("obj = "+cplex.getObjValue());
			for (int i = 0; i <= numCus; i++) {         
		 		for (int j = 0; j <= numCus; j++) {
		 			for (int k = 1; k <= numDep; k++) {
		 				for (int l = 0; l <= numCar; l++) {
		 					if (cplex.getValue(x[i][j][k][l]) ==1)
		 						System.out.println(" x "+i+" "+j+" "+k+" "+l);	
		 				}
		 			}
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