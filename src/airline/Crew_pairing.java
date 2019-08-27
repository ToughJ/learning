package airline;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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

public class Crew_pairing {
	
	public ArrayList<Air_section> arrSec=new ArrayList<>();
	public ArrayList<String> arrCity = new ArrayList<>();
	public ArrayList<ArrayList<Integer>> h0=new ArrayList<ArrayList<Integer>>(),h1=new ArrayList<ArrayList<Integer>>(),
			b0=new ArrayList<ArrayList<Integer>>(),b1=new ArrayList<ArrayList<Integer>>();
	public ArrayList<int[]> a=new ArrayList<int[]>();
	public ArrayList<Integer> c=new ArrayList<Integer>();
	private double[] pi;
	
	public MasterProblem masterproblem;
	public SubProblem subproblem;
	
	public int[][] BL;
	public int[] ft,w,r;
	public int secSize;
	public int[][][] XL,YL;
	
	public int maxh = 12*60;
	public int daySize = 7;
	
	public static double zero = -0.0001;
	public static double subproblemTiLim = 5;
	public static double subproblemObjVal = -1000;
	public static double setupCost = 100;
	
	public boolean fin = false;
	public double upper;
	
	public Crew_pairing(String url) throws Exception{
		ReadData(url);
		masterproblem = new MasterProblem();
		subproblem = new SubProblem();
	}
	
	public class MasterProblem {
		public IloCplex cplex;
		private IloObjective total_cost;
		public ArrayList<IloNumVar> x = new ArrayList<>();
		
		private List<IloConversion> mipConversion = new ArrayList<IloConversion>();
		public double lastObjValue;
		private ArrayList<IloRange> rng = new ArrayList<IloRange>();
		
		private boolean[] flag = new boolean[arrSec.size()];
		private int tmpCost;
		
		public MasterProblem() throws Exception {
			createModel();
			createDefaultPaths();
			pi = new double[arrSec.size()];
			
		}
		public void createDefaultPaths() throws Exception {
			flag = new boolean[arrSec.size()];
			int[] tmpA = new int[arrSec.size()];
			for (int i=0; i<arrSec.size(); i++) if(!flag[i]){
				int head,tail,period;
				tmpA = new int[arrSec.size()];
				tmpA[i] = 1;
				head = arrSec.get(i).getDeparture();
				tail = arrSec.get(i).getArrival();
				period = arrSec.get(i).getArrivalTime()-arrSec.get(i).getArrivalTime();
				int cost = 10;
				if (searchPath(i, period, head, tail, tmpA, cost)){
					a.add(tmpA);
					//tmpCost+=setupCost; // 100 setup cost
					c.add(tmpCost);
					for (int j=0; j<arrSec.size(); j++)  if(tmpA[j] == 1 && !flag[j]) flag[j] =true;
					this.addNewColumn();
				}
				else {
					throw new Exception("错误！ 编号为 " + i + "的航班段不能成环");
				}
			}
			
			int tmpCost = 0;
			
		}
		private boolean searchPath(int i, int period, int head, int tail, int[] tmpA, int cost){
			if (head == tail) {
				tmpCost = cost;
				return true;
			}
			int newHead, newTail, newPeriod, newCost;
			for (int j : h0.get(i)) if (tmpA[j] == 0){
				newHead = arrSec.get(j).getDeparture();
				tmpA[j] = 1;
				newPeriod = period + arrSec.get(j).getArrivalTime()-arrSec.get(j).getArrivalTime();
				newCost = cost + 10;
				if (newHead == tail) {
					tmpCost = newCost;
					return true; 
				}
				else {
					if (searchPath(j,newPeriod,newHead,tail,tmpA, newCost)) return true;
				}
				tmpA[j]=0;
			}
			for (int j : h1.get(i)) if (tmpA[j] == 0){
				newHead = arrSec.get(j).getDeparture();
				tmpA[j] = 1;
				newPeriod = arrSec.get(j).getArrivalTime()-arrSec.get(j).getArrivalTime();
				newCost = cost + 20;
				if (newHead == tail) {
					tmpCost = newCost;
					return true; 
				}
				else {
					if (searchPath(j,newPeriod,newHead,tail,tmpA, newCost)) return true;
				}
				tmpA[j]=0;
			}
			for (int j : b0.get(i)) if (tmpA[j] == 0){
				newTail = arrSec.get(j).getArrival();
				tmpA[j] = 1;
				newPeriod = period + arrSec.get(j).getArrivalTime()-arrSec.get(j).getArrivalTime();
				newCost = cost + 10;
				if (head == newTail) {
					tmpCost = newCost;
					return true; 
				}
				else {
					if (searchPath(j,newPeriod,head,newTail,tmpA, newCost)) return true;
				}
				tmpA[j]=0;
			}
			for (int j : b1.get(i)) if (tmpA[j] == 0){
				newTail = arrSec.get(j).getArrival();
				tmpA[j] = 1;
				newPeriod = arrSec.get(j).getArrivalTime()-arrSec.get(j).getArrivalTime();
				newCost = cost + 20;
				if (head == newTail) {
					tmpCost = newCost;
					return true; 
				}
				else {
					if (searchPath(j,newPeriod,head,newTail,tmpA, newCost)) return true;
				}
				tmpA[j]=0;
			}
			return false;
		}
		
		public void createModel() {
			try{
				cplex = new IloCplex();
				IloNumVar[] xx = new IloNumVar[a.size()]; 

				
				for (int i=0; i<a.size(); i++){
					x.add(xx[i]=cplex.numVar(0,1));
					x.get(i).setName("x" + i);
				}
				total_cost = cplex.addMinimize();
//				IloLinearNumExpr expr = cplex.linearNumExpr();
				for (int i=0; i<arrSec.size();i++){
//					rng[i] = cplex.addRange(1, cplex.scalProd(xx, a.get(i)), 1);
					rng.add(cplex.addRange(1, 100, "cust " + i));
				}
				
				//				x = new IloIntVar[a.size()];
//				for (int i=0; i<a.size(); i++){
//					x[i] = cplex.boolVar();
//					x[i].setName("x" + i);
//				}
//				total_cost = cplex.addMinimize();
//				IloLinearNumExpr expr = cplex.linearNumExpr();
//				for (int p=0; p<a.size();p++){ 
//					for (int i=0; i<arrSec.size();i++){
//						expr.addTerm(a.get(p)[i], x[p]);
//					}
//					cplex.addEq(expr, 1);	
			}
			catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}
		public void addNewColumn() {
			try {
				IloColumn new_column = cplex.column(total_cost, c.get(c.size()-1));
				for (int i=0; i<rng.size(); i++){
					new_column = new_column.and(cplex.column(rng.get(i),a.get(a.size()-1)[i]));
				}
				x.add(cplex.numVar(new_column,0,1, "x"+(a.size()-1)));
			}
			catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}
//		public void solveDerect() {
//			try {
//				if (cplex.solve()) {
//					saveDualValues();
//					lastObjValue = cplex.getObjValue();
//					System.out.println(lastObjValue + "   --- "+ a.size());
//				}
//			}
//			catch (IloException e) {
//				System.err.println("Concert exception caught: " + e);
//			}
//		}
		public void solveRelaxation() {
			try {
				if (cplex.solve()) {
					saveDualValues();
					lastObjValue = cplex.getObjValue();
					upper = lastObjValue;
				}
			}
			catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}
		public void saveDualValues() {
			try {
				for (int i=0; i<rng.size(); i++) 
					pi[i]=cplex.getDual(rng.get(i));
				check();
			}
			catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}
		public void check(){
			try {
				for (int i=0; i<x.size();i++) if (cplex.getValue(x.get(i)) == 1){
					System.out.print(i +" ");
				}
				System.out.println();
				System.out.println(" dual :");
				for (int i=0; i<rng.size(); i++) 
					System.out.print(i +" " + pi[i] + "  ");
				System.out.println();
			} catch (IloException e) {
				e.printStackTrace();
			}
		}
		public void solveMIP() {
			try {
				convertToMIP();
				if (cplex.solve()) {
					displaySolution();
				}
				else {
					System.out.println("Integer solution not found");
				}
			}
			catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}
		public void convertToMIP() {
			try {
				for (int i=0; i<x.size();i++){
					mipConversion.add(cplex.conversion(x.get(i), IloNumVarType.Bool)) ;
					cplex.add(mipConversion.get(mipConversion.size()-1));
				}
				
			}
			catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}
		public void displaySolution() {
			for (int i=0; i<x.size();i++){
				try {
					if (cplex.getValue(x.get(i))>0.99999) {
						System.out.print("path " + i +" ");
						for (int j=0; j<arrSec.size();j++) if (a.get(i)[j] == 1){
							System.out.print(j +"-- ");
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
		public IloIntVar[][][] x,y;
		public IloIntVar[][] e,s;
		private IloObjective reduced_cost;
		private List<IloConstraint> constraints;
		public double lastObjValue;
		public double lastObjValueRelaxed;
//		public MyMipCallBack mip_call_back;

//		private class MyMipCallBack extends IloCplex.MIPInfoCallback {
//			private boolean aborted;
//			private double time_start;
//			private double time_limit;
//			public void main() {
//				try {
//					if (!aborted && hasIncumbent()) {
//						double time_used = getCplexTime() - time_start;
//						if ((getIncumbentObjValue() < subproblemObjVal) 
//								|| (time_used > time_limit) 
//								|| (getBestObjValue() > zero) ) {
//							aborted = true;
//							abort();
//						}
//					}
//				} catch (IloException e) {
//					System.err.println("Concert exception caught: " + e);
//				}
//			}
//			public void reset() {
//				try {
//					aborted = false;
//					time_start = cplex.getCplexTime();
//					time_limit = subproblemTiLim;
//				} catch (IloException e) {
//					System.err.println("Concert exception caught: " + e);
//				}
//			}
//		}
		
		public SubProblem() {
			this.constraints = new ArrayList<IloConstraint>();
			createModel();
			System.out.println("这里跑完了");
//			this.mip_call_back = new MyMipCallBack();
//			try {
//				cplex.use(mip_call_back);
//			} catch (IloException e) {
//				System.err.println("Concert exception caught: " + e);
//			}
		}
		public void createModel() {
			try {
				// define model
				cplex = new IloCplex();
				// define variables
				x = new IloIntVar[arrSec.size()][arrSec.size()][daySize];
				y = new IloIntVar[arrSec.size()][arrSec.size()][daySize];
				e = new IloIntVar[arrSec.size()][daySize];
				s = new IloIntVar[arrSec.size()][daySize];
				for (int i=0; i<arrSec.size();i++){
					for (int j=0; j<arrSec.size();j++){
						for (int t=0; t<daySize;t++){
							x[i][j][t] = cplex.boolVar();
							x[i][j][t].setName("x"+i+","+j+","+t);
							y[i][j][t] = cplex.boolVar();
							y[i][j][t].setName("y"+i+","+j+","+t);
						}
					}
				}
				for (int i=0; i<arrSec.size();i++){
					for (int t=0; t<daySize;t++){
						e[i][t] = cplex.boolVar();
						e[i][t].setName("e"+i+","+t);
						s[i][t] = cplex.boolVar();
						s[i][t].setName("s"+i+","+t);
					}
				}
				// define objectives
				reduced_cost = cplex.addMinimize();
				// define constraints
				for (int i=0; i<arrSec.size();i++){
					for (int t=0; t<daySize;t++){
						IloLinearNumExpr expr1 = cplex.linearNumExpr(), expr2 = cplex.linearNumExpr();
						expr1.addTerm(1, s[i][t]);
						expr2.addTerm(1, e[i][t]);
						for (int j : h0.get(i)){
							expr1.addTerm(1, x[j][i][t]);
						}
						if (t>0)for (int j : h1.get(i)){
							expr1.addTerm(1, y[j][i][t-1]);
						}
						for (int j : b0.get(i)){
							expr2.addTerm(1, x[i][j][t]);
						}
						for (int j : b1.get(i)){
							expr2.addTerm(1, y[i][j][t]);
						}
						constraints.add(cplex.addEq(expr1, expr2, "c1"));
					}
				}
				IloLinearNumExpr expr3 = cplex.linearNumExpr(), expr4 = cplex.linearNumExpr();
				for (int i=0; i<arrSec.size();i++){
					for (int t=0; t<daySize;t++){
						expr3.addTerm(1, s[i][t]);
						expr4.addTerm(1, e[i][t]);
					}
				}
				constraints.add(cplex.addEq(expr3, 1, "c2"));
				constraints.add(cplex.addEq(expr4, 1, "c3"));
				for (int t=0; t<daySize;t++){
					IloLinearNumExpr expr5 = cplex.linearNumExpr();
					for (int i=0; i<arrSec.size();i++){
						for (int j=0; j<arrSec.size();j++){
							expr5.addTerm(ft[i],x[i][j][t]);
							expr5.addTerm(ft[i],y[i][j][t]);
						}
					}
					constraints.add(cplex.addLe(expr5, maxh, "c4"));
				}
				IloLinearNumExpr expr6 = cplex.linearNumExpr(), expr7 = cplex.linearNumExpr();
				for (int i=0; i<arrSec.size();i++){
					for (int t=0; t<daySize;t++){
						expr6.addTerm(arrSec.get(i).getArrival(), e[i][t]);
						expr7.addTerm(arrSec.get(i).getDeparture(), s[i][t]);
						constraints.add(cplex.addLe(s[i][t], BL[i][t], "c7"));
						constraints.add(cplex.addLe(e[i][t], BL[i][t], "c8"));
					}
				}
				constraints.add(cplex.addEq(expr6, expr7, "c9"));
				IloLinearNumExpr expr8 = cplex.linearNumExpr(); 
				for (int i=0; i<arrSec.size();i++){
					IloLinearNumExpr expr9 = cplex.linearNumExpr(); 
					for (int j=0; j<arrSec.size();j++){
						for (int t=0; t<daySize;t++){
							constraints.add(cplex.addLe(x[i][j][t], XL[i][j][t],"c10"));
							constraints.add(cplex.addLe(y[i][j][t], YL[i][j][t],"c11"));
							expr9.addTerm(1,x[i][j][t]);
							expr9.addTerm(1,y[i][j][t]);
						}
					}
//					constraints.add(cplex.addLe(expr9, 1,"c12"));
					expr8.add(expr9);
				}
				constraints.add(cplex.addGe(expr8, 3,"c13"));
			} catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}
		public void updateReducedCost() {
			try {
				IloLinearNumExpr num_expr = cplex.linearNumExpr();
				for (int i=0; i<arrSec.size();i++){
					for (int j=0; j<arrSec.size();j++){
						for (int t=0; t<daySize;t++){
							num_expr.addTerm(w[i] - pi[i], x[i][j][t]);
							num_expr.addTerm(r[i] - pi[i], y[i][j][t]);
						}
					}
				}
				reduced_cost.clearExpr();
				reduced_cost.setExpr(num_expr);
			}
			catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}
		public void solve() {
			try {
//				mip_call_back.reset();
//				cplex.exportModel("sbp.lp");
				if (cplex.solve()) {
					this.lastObjValue = cplex.getObjValue();
					this.lastObjValueRelaxed = cplex.getBestObjValue();
					int nPool = cplex.getSolnPoolNsolns();
					for (int k=0; k<nPool; k++) {
						if (cplex.getObjValue(k) /*+ setupCost*/ > zero) {
							fin = true;
						}
						savePath(k);	
						if (fin) return;
					}
				}
			}
			catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}
		public void savePath(int k) {
			try {
				//print
				
				//save
				int[] tmp= new int[arrSec.size()];
				int newCost = 0;
				for (int i=0; i<arrSec.size();i++){
					for (int j=0; j<arrSec.size();j++){
						for (int t=0; t<daySize;t++){
							if (cplex.getValue(x[i][j][t],k) > 0.99999) {
								tmp[i]=1;
								newCost += w[i];
							}
							if (cplex.getValue(y[i][j][t],k) > 0.99999) {
								tmp[i]=1;
								newCost += r[i];
							}
						}
					}
				}
				for (int i=0; i<arrSec.size();i++){
					for (int t=0; t<daySize;t++){
						if (cplex.getValue(e[i][t],k) > 0.99999) {
							tmp[i]=1;
							newCost += w[i];
						}
						if (cplex.getValue(s[i][t],k) > 0.99999) {
							tmp[i]=1;
						}
					}
				}
				//newCost+= setupCost; // 100 setup cost
				if (newCost - upper > zero) {  fin = true; return ;}
				a.add(tmp);
				c.add(newCost);
				Crew_pairing.this.masterproblem.addNewColumn();
			}
			catch (IloException e) {
				System.err.println("Concert exception caught: " + e);
			}
		}
		
	}
	
	public void test(){
		for (int j=0; j<arrSec.size();j++){
			System.out.println(j+"    :");
			System.out.println("---1---");
			for (int i : h0.get(j)){
				System.out.println(i + "  ");
			}
			System.out.println("---2---");
			for (int i : b0.get(j)){
				System.out.println(i + "  ");
			}
			System.out.println("---3---");
			for (int i : h1.get(j)){
				System.out.println(i + "  ");
			}
			System.out.println("---4---");
			for (int i : b1.get(j)){
				System.out.println(i + "  ");
			}
			System.out.println("-------");
		}
	}
	
	public void ReadData(String url) throws FileNotFoundException, IOException, ParseException {
		Data_loader dl = new Data_loader();
		dl.loadNodeInfo(url);
		arrSec.clear();      
		arrSec.addAll(dl.arrSec);
		arrCity.clear();     
		arrCity.addAll(dl.arrCity);
		CalParameter cp = new CalParameter(arrSec);
		cp.calculate();
		secSize = arrSec.size();
		BL = cp.BL;
		ft = cp.ft;
		w = cp.w;
		r = cp.r;
		XL=cp.XL;
		YL=cp.YL;
		h0.addAll(cp.h0);
		h1.addAll(cp.h1);
		b0.addAll(cp.b0);
		b1.addAll(cp.b1);
	}
	
	public void runCrew_pairing() {
		int iteration_counter = 0;
		do {
			iteration_counter++;
			masterproblem.solveRelaxation();
			subproblem.updateReducedCost();
			subproblem.solve();
			
//			displayIteration(iteration_counter);
		} while (fin);
		masterproblem.solveMIP();
	}
	
//	private void displayIteration(int iter) {
//		if ((iter)%20==0 || iter==1) {
//			System.out.println();
//			System.out.print("Iteration");
//			System.out.print("     Time");
//			System.out.print("   nPaths");
//			System.out.print("       MP lb");
//			System.out.print("       SB lb");
//			System.out.print("      SB int");
//			System.out.println();
//		}
//		System.out.format("%9.0f", (double)iter);
//		System.out.format("%9.0f", (double)paths.size());
//		System.out.format("%12.4f", masterproblem.lastObjValue);//master lower bound
//		System.out.format("%12.4f", subproblem.lastObjValueRelaxed);//sb lower bound
//		System.out.format("%12.4f", subproblem.lastObjValue);//sb lower bound
//		System.out.println();
//	}
}




