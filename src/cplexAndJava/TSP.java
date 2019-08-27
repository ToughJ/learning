package cplexAndJava;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.Random;


public class TSP {

    public static void main(String[] args) {

        int n = 5;

        double[] coorX = new double[n + 1];
        double[] coorY = new double[n + 1];

        Random rnd = new Random();

        for (int i = 1; i <= n; i++) {
            coorX[i] = 100 * rnd.nextDouble();
            coorY[i] = 100 * rnd.nextDouble();
        }

        double[][] c = new double[n + 1][n + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                c[i][j] = Math.hypot(coorX[i] - coorX[j], coorY[i] - coorY[j]);
            }
        }

        try {

            IloCplex cplex = new IloCplex();

            IloNumVar[][] x = new IloNumVar[n + 1][n + 1];

            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    if (i != j) {
                        x[i][j] = cplex.boolVar("x." + i + "." + j);
                    }
                }
            }

            IloNumVar[] u = new IloNumVar[n + 1];
            for (int i = 2; i <= n; i++) {
                u[i] = cplex.numVar(0, Double.MAX_VALUE, "u." + i);
            }

            IloLinearNumExpr obj = cplex.linearNumExpr();
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    if (i != j) {
                        obj.addTerm(c[i][j], x[i][j]);
                    }
                }
            }
            cplex.addMinimize(obj);

            for (int j = 1; j <= n; j++) {
                IloLinearNumExpr expr = cplex.linearNumExpr();
                for (int i = 1; i <= n; i++) {
                    if (i != j) {
                        expr.addTerm(1, x[i][j]);
                    }
                }
                cplex.addEq(expr, 1);
            }

            for (int i = 1; i <= n; i++) {
                IloLinearNumExpr expr = cplex.linearNumExpr();
                for (int j = 1; j <= n; j++) {
                    if (j != i) {
                        expr.addTerm(1, x[i][j]);
                    }
                }
                cplex.addEq(expr, 1);
            }

            for (int i = 2; i <= n; i++) {
                for (int j = 2; j <= n; j++) {
                     if (i!=j){
                        IloLinearNumExpr expr = cplex.linearNumExpr();
                        expr.addTerm(1, u[i]);
                        expr.addTerm(-1, u[j]);
                        expr.addTerm(n - 1, x[i][j]);
                        cplex.addLe(expr, n - 2);
                    }
                }
            }
            System.out.println(cplex.getModel());
            cplex.solve();

        } catch (IloException e) {
            e.printStackTrace();
        }
    }

}