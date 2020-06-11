package DO;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * The class <code>Solver</code> is an implementation of a greedy algorithm to solve the knapsack problem.
 *
 */
public class KnapsackSolve {
    
    /**
     * The main class
     */
    public static void main(String[] args) {
        try {
            solve(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Read the instance, solve it, and print the solution in the standard output
     */
    
    public static void solve(String[] args) throws IOException {
        String fileName = null;
        
        // get the temp file name
        for(String arg : args){
            if(arg.startsWith("-file=")){
                fileName = arg.substring(6);
            } 
        }
        if(fileName == null)
            return;
        
        // read the lines out of the file
        List<String> lines = new ArrayList<String>();

        BufferedReader input =  new BufferedReader(new FileReader(fileName));
        try {
            String line = null;
            while (( line = input.readLine()) != null){
                lines.add(line);
            }
        }
        finally {
            input.close();
        }
        
        
        // parse the data in the file
        String[] firstLine = lines.get(0).split("\\s+");
        int items = Integer.parseInt(firstLine[0]);
        int capacity = Integer.parseInt(firstLine[1]);

        int[] values = new int[items];
        int[] weights = new int[items];

        for(int i=1; i < items+1; i++){
          String line = lines.get(i);
          String[] parts = line.split("\\s+");

          values[i-1] = Integer.parseInt(parts[0]);
          weights[i-1] = Integer.parseInt(parts[1]);
        }

        // a trivial greedy algorithm for filling the knapsack
        // it takes items in-order until the knapsack is full
//        int value = 0;
//        int weight = 0;
//        int[] taken = new int[items];
//
//        for(int i=0; i < items; i++){
//            if(weight + weights[i] <= capacity){
//                taken[i] = 1;
//                value += values[i];
//                weight += weights[i];
//            } else {
//                taken[i] = 0;
//            }
//        }
        
        int[] taken = new int[items];
        int[] f = new int[capacity+1];   ///flag
        int[] F = new int[capacity+1];
        for (int j = 0; j <= capacity; j++) F[j] = 0;
        for (int i = 1; i <= items; i++) {
            for (int j = capacity; j >= 0; j--) {
                if (weights[i-1] <= j) {
                    if (F[j] < F[j-weights[i-1]] + values[i-1]) {
                        F[j] = F[j-weights[i-1]] + values[i-1];
                        f[j] = i;
                    }
                }
            }
        }
        int value = 0;
        int maxj = 0;
        for (int j = 0; j <= capacity; j++) {
            if (F[j] > value) {
                value = F[j];
                maxj = j;
            }
        }
        int mj = maxj,mi = f[mj];
        mj -= weights[mi-1];
        taken[mi-1] = 1;
        taken = trace(weights,values,mi-1,mj,taken);
        
        // prepare the solution in the specified output format
        System.out.println(value+" 0");
        for(int i=0; i < items; i++){
            System.out.print(taken[i]+" ");
        }
        System.out.println("");        
    }

    private static int[] trace(int[] weights, int[] values, int mi, int mj, int[] taken) {
        if(mi==0 || mj == 0) return taken;
        
        int[] f = new int[mj+1];   ///flag
        int[] F = new int[mj+1];
        for (int j = 0; j <= mj; j++) F[j] = 0;
        for (int i = 1; i <= mi; i++) {
            for (int j = mj; j >= 0; j--) {
                if (weights[i-1] <= j) {
                    if (F[j] < F[j-weights[i-1]] + values[i-1]) {
                        F[j] = F[j-weights[i-1]] + values[i-1];
                        f[j] = i;
                    }
                }
            }
        }
        mi = f[mj];
        mj -= weights[mi-1];
        taken[mi-1] = 1;
        taken = trace(weights,values,mi-1,mj,taken);
        return taken;
    }
}