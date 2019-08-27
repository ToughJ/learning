package vrp_ec;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;



public class input_data {
	public int numCus = 0, numDep = 0;
	public double carDem =0.0;
	ArrayList<Node> Depot = new ArrayList<>();
	ArrayList<Node> Customer = new ArrayList<>();

    public input_data() {
    }
    
    public void loadNodeInfo(String url) throws FileNotFoundException, IOException{
    	url = "C:/learning part//operationResearchh//code/learning/vrp-data/multi_depot/" + url;
        BufferedReader br = new BufferedReader(new java.io.FileReader(url));
        String line;
        String[] splitLine;
        line = br.readLine();
        splitLine=line.split("\\s+");
        numDep = Integer.parseInt(splitLine[0]);
        numCus = Integer.parseInt(splitLine[1]);
        carDem = Double.valueOf(splitLine[2]);
        for (int i=0; i<numDep; i++) {
        	line = br.readLine();
        	splitLine=line.split("\\s+");
        	Node tempNode = new Node();
            tempNode.setIndex(splitLine[0]);
            tempNode.setX(Double.valueOf(splitLine[1]));
            tempNode.setY(Double.valueOf(splitLine[2]));
            Depot.add(tempNode);
        }
        for (int i=0; i<numCus; i++) {
        	line = br.readLine();
        	splitLine=line.split("\\s+");
        	Node tempNode = new Node();
            tempNode.setIndex(splitLine[0]);
            tempNode.setX(Double.valueOf(splitLine[1]));
            tempNode.setY(Double.valueOf(splitLine[2]));
            tempNode.setDemand(Double.valueOf(splitLine[3]));
            Customer.add(tempNode);
        }
    }

    public double[] getDemand(){
    	double[] demand = new double[Customer.size()+1];
    	for (int i=0; i<Customer.size(); i++){
    		demand[i+1] = Customer.get(i).demand;
    	}
    	return demand;
    }
    
    public double[][] getw1(){
    	double[][] w1 = new double[Customer.size()+1][Customer.size()+1];
    	for (int i=0; i<Customer.size(); i++){
    		for (int j=0; j<Customer.size(); j++){
    			w1[i+1][j+1] = calDistNode(Customer.get(i),Customer.get(j));
    			w1[j+1][i+1] = w1[i+1][j+1];
    		}
    	}
    	return w1;
    }
    
    public double[][] getw2(){
    	double[][] w2 = new double[Customer.size()+1][Depot.size()+1];
    	for (int i=0; i<Customer.size(); i++){
    		for (int j=0; j<Depot.size(); j++){
    			w2[i+1][j+1] = calDistNode(Customer.get(i),Depot.get(j));
    		}
    	}
    	return w2;
    }

    public ArrayList<Node> getDepot() {
        return Depot;
    }
    
    public void setDepot(ArrayList<Node> Depot) {
        this.Depot = Depot;
    }

    public void setCustomer(ArrayList<Node> Customer) {
        this.Customer = Customer;
    }
    
    public ArrayList<Node> getCustomer() {
        return Customer;
    }
    
    public double calDistNode(Node aNode, Node bNode){
        double tempDistance=0;
        tempDistance=Math.sqrt(Math.pow(aNode.getX()-bNode.getX(), 2)+Math.pow(aNode.getY()-bNode.getY(), 2));
        return tempDistance;
    }

    public void testPrint(){
        for(int i=0; i<Depot.size(); i++){
            System.out.println("Depot :" + Depot.get(i).getIndex()+" "+Depot.get(i).getX()+" "+Depot.get(i).getY()+" ");
        }
        for(int i=0; i<Customer.size(); i++){
            System.out.println("Customer :" + Depot.get(i).getIndex()+" "+Customer.get(i).getX()+" "+Customer.get(i).getY()+" "+Customer.get(i).demand);
        }
    }
}