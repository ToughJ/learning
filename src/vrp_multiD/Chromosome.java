package vrp_multiD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

/*      编码方式 为 
 * 		0...numDep-1 为 仓库的点，  
 * 		numSat...numSat+numCus-1  为客户的点。    Demand 为客户需求。
 * 
 */

// 做出调整   保存一个原始顺序的NodeList  生成的是一组新的ArrNode 方便后续使用。

public class Chromosome {
	ArrayList<Node> nodeList;
    ArrayList<Node> arrNode;
    double totalDistance, fitness;
    int carCount;
    double carDemand = 0.0;
    int numCus = 0, numDep = 0;
    
//    public Chromosome() {
//    }
    
//    public Chromosome(int numCus, int numDep, double carDem) {
//        arrNode=new ArrayList<>();
//        nodeList=new ArrayList<>();
//        this.carDemand = carDem;
//        this.numCus = numCus;
//        this.numDep = numDep;
//        carCount=0;
//        totalDistance=0;
//        fitness=0;
//    }
    
    public Chromosome(int numCus, int numDep, double carDem, ArrayList<Node> nodeList) {
        arrNode=new ArrayList<>();
        this.nodeList = nodeList;
        this.carDemand = carDem;
        this.numCus = numCus;
        this.numDep = numDep;
        carCount=0;
        totalDistance=0;
        fitness=0;
    }
    
    public Chromosome(Chromosome chrom) {
    	this.carDemand = chrom.carDemand;
        this.numCus = chrom.numCus;
        this.numDep = chrom.numDep;
        this.carCount=chrom.carCount; 
        this.totalDistance=chrom.totalDistance;
        this.fitness=chrom.fitness;
        this.nodeList = chrom.nodeList;
        arrNode=new ArrayList<>();
        for (int i=0; i<chrom.arrNode.size();i++){
        	this.arrNode.add(chrom.arrNode.get(i));
        }
    }
    
    public ArrayList<Node> getArrNode() {
        return arrNode;
    }
    
    public void setNodeList(ArrayList<Node> arrNode) {
        this.nodeList.clear();
        this.nodeList.addAll(arrNode);
    }
    
    public void setArrNode(ArrayList<Node> arrNode) {
        this.arrNode.clear();
        this.arrNode.addAll(arrNode);
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    public void calFitness(){
//        this.fitness=1/(totalDistance+0.00001);  
    	this.fitness = totalDistance + carCount*100;
    }
    
    public double calDistNode(Node aNode, Node bNode){
        double tempDistance=0;
        tempDistance=Math.sqrt(Math.pow(aNode.getX()-bNode.getX(), 2)+Math.pow(aNode.getY()-bNode.getY(), 2));
        return tempDistance;
    }
    
    public void carCountInc(){
    	carCount++;
    }
    
    public void carCountRes(){
    	carCount=0;
    }
    
    public int getCarCountInc(){
    	return carCount;
    }
    
    public void calTotalDistance() {
    	Random rand = new Random();
        double tempDist=0, tempTotalDist=0;
        double[] capDem = new double[numDep+2];   // capDem   应该记录的是 同一个卫星的所有 路径中出现的最大正值的和  需要修改
        carCountRes();
		double currentDemand = 0;
		int start; 
		int index; 
        ArrayList<Node> tempArrNode = new ArrayList<Node>();
        
        int from = 0, to;
        if (from<arrNode.size()-1){
        	start = Integer.parseInt(arrNode.get(from).index);
        	tempArrNode.add(nodeList.get(start));
        	while (from<arrNode.size()){
	        	tempDist=0;
	        	to = from+1;
	        	if (to>=arrNode.size()) {
	        		tempTotalDist=tempTotalDist+calDistNode(arrNode.get(from), nodeList.get(start));
	        		break;
	        	}
	        	// 如果两个Depot连着了，就删去第二个。
	        	while (to<arrNode.size() && arrNode.get(from).demand==0 && arrNode.get(to).demand==0) {
	        		to++;
	        	}
	        	if (to>=arrNode.size()) break;
	        	if (arrNode.get(from).demand==0) carCountInc();
	        	
	        	index = Integer.parseInt(arrNode.get(to).index);
	        	if (index < numDep){  // 如果是depot 
	    			tempDist += calDistNode(arrNode.get(from), nodeList.get(start));  
	    			start = index;
	    			from = to;
	    			if (to==arrNode.size()-1) break;
	    			currentDemand = 0;
	        	}
	        	else {   // 那么就是客户
	        		if (currentDemand + arrNode.get(to).demand > this.carDemand){
	        			int newStart = rand.nextInt(numDep);
	        			tempArrNode.add(nodeList.get(newStart));
	        			currentDemand=0;
	        			tempDist += calDistNode(arrNode.get(from), nodeList.get(start))
	        					    + calDistNode(arrNode.get(to), nodeList.get(newStart));
	        			start = newStart;
	        			carCountInc();
	        		}
	        		else {
	        			tempDist += calDistNode(arrNode.get(from), arrNode.get(to));
	        		}
	        		currentDemand += arrNode.get(to).demand;
	        	}
	        	tempTotalDist=tempTotalDist+tempDist;
	        	tempArrNode.add(arrNode.get(to));
	        	from=to;
	        }
	        tempTotalDist=tempTotalDist+tempDist;
	        totalDistance=tempTotalDist;
        }
        totalDistance=tempTotalDist;
        arrNode.clear();
        arrNode.addAll(tempArrNode);
    }
    

    
    // 生成一条随机染色体
    public void createGen(){
        Random rand = new Random();
        ArrayList<Integer> randCusIndex = new ArrayList<>();
        for (int i=0; i<numCus; i++) randCusIndex.add(numDep+i);
        Collections.shuffle(randCusIndex, rand);
        
        int start = rand.nextInt(numDep);
        this.arrNode.add(nodeList.get(start));
        for (int i = 0; i<numCus; i++){
        	this.arrNode.add(nodeList.get(randCusIndex.get(i)));
        }
    }
    
    
    public void testPrint(){
        for(int i=0; i<arrNode.size(); i++){
             System.out.println(arrNode.get(i).getIndex()+" "+arrNode.get(i).getX()+" "+arrNode.get(i).getY()+" "+arrNode.get(i).getDemand());
        }
    }
}