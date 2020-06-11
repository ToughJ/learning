package DO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;



/**
 * The class <code>Coloring</code> is to solve the Graph Coloring Problem 
 *
 */

public class ColoringSlove {
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
    static int E,V;
	static Bag<Integer>[] bag;
    static int[] count,color, colorOpt;
    static int obj,obj2;
    static int[][] f;
  
    @SuppressWarnings("unchecked")
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
        V = Integer.parseInt(firstLine[0]);
        E = Integer.parseInt(firstLine[1]);

        bag = new Bag[V];
        color = new int[V];
        colorOpt = new int[V];
        count = new int[V];
        
        for (int i = 0; i < V;  i++) bag[i] = new Bag<Integer>();

        for (int i = 0; i < E; i++){
          String line = lines.get(i+1);
          String[] parts = line.split("\\s+");
          
          int p,q;
          p = Integer.parseInt(parts[0]);
          q = Integer.parseInt(parts[1]);
          count[p]++;
          count[q]++;
          bag[p].add(q);
          bag[q].add(p);
        }
        
        // choose the vertex with the most edges
     //   int t0 = -1;
        int countT0 = 0;
        for (int i = 0; i < V; i++) 
        	if (countT0 < count[i]) {
        		countT0 = count[i];
    //    		t0 = i;
        	}
        obj = countT0;
        obj2 = obj;
        
        f = new int[V][obj2+1];
        search(0,0);
        System.out.println(obj +" 0");
        for (int i = 0; i < V-1; i++) {
        	System.out.print(colorOpt[i]-1 + " ");
        }
        System.out.println(colorOpt[V-1]-1);
    }
    
    static void search (int t, int k) {
    	if (t == V) {
    		if (k < obj) {
    			obj = k;
    			for (int i = 0; i < V; i++) {colorOpt[i] = color[i];}
    		}
    		return ;
    	}
    	if (k >= obj) return ;
    	for (int i = 1; i <= k+1; i++) if(f[t][i] == 0){
    		color[t] = i;
    		for (int tot : bag[t]) 
        		f[tot][i]++;
    		search(t+1, Math.max(k, i));
    		for (int tot : bag[t]) 
        		f[tot][i]--;
    	}
    	return ;
    }
    
    static class Bag<Item> implements Iterable<Item> {
        private int N;
        private Node<Item> first;
        private static class Node<Item> {
            private Item item;
            private Node<Item> next;
        }
        public Bag() {
            first = null;
            N = 0;
        }
        public boolean isEmpty() {
            return first == null;
        }
        public int size() {
            return N;
        }
        public void add(Item item) {
            Node<Item> oldfirst = first;
            first = new Node<Item>();
            first.item = item;
            first.next = oldfirst;
            N ++;
        }
        public Iterator<Item> iterator() {
            return new ListIterator<Item>(first);
        }
        @SuppressWarnings("hiding")
    	private class ListIterator<Item> implements Iterator<Item> {
            private Node<Item> current;
            
            public ListIterator(Node<Item> first) {
                current = first;
            }
            public boolean hasNext() { return current != null; }
            public void remove() { throw new UnsupportedOperationException(); }
            
            public Item next() {
                if(!hasNext()) throw new NoSuchElementException();
                Item item = current.item;
                current = current.next;
                return item;
            }
        }
    }
}

