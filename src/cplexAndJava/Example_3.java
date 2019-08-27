package cplexAndJava;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;


public class Example_3 {

    public static void main(String[] args) {
        try {

            IloCplex cplex = new IloCplex();

            IloNumVar x = cplex.numVar(0, Double.MAX_VALUE, "x");
            IloNumVar y = cplex.numVar(0, Double.MAX_VALUE, "y");

            cplex.addMinimize(cplex.sum(cplex.prod(0.12, x), cplex.prod(0.15, y)));

            IloRange c1 = cplex.addGe(cplex.sum(cplex.prod(60, x), cplex.prod(60, y)), 300, "c1");
            cplex.addGe(cplex.sum(cplex.prod(12, x), cplex.prod(6, y)), 36, "c2");
            cplex.addGe(cplex.sum(cplex.prod(10, x), cplex.prod(30, y)), 90, "c3");

            if (cplex.solve()) {
                System.out.println("Objective = " + cplex.getObjValue());
                System.out.println("x = " + cplex.getValue(x));
                System.out.println("y = " + cplex.getValue(y));
                System.out.println("dual of c1 = " + cplex.getDual(c1));
                System.out.println(cplex.getModel());
            }

        } catch (IloException e) {
            e.printStackTrace();
        }
    }

}