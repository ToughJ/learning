package vrp_GA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

//  编码方式 为  全客户，判断分车是由计算超量来判断的
//  可以切换成 0 来隔开   这种切换方式不太好。  考虑不做切换。
//  该版本不做切换了，一直保持由0分割的状态。  原始版本在旧代码文件夹中

public class Chromosom {
    ArrayList<Node> arrNode;
    double totalDistance, fitness;
    private double currentDemand;
    int rechargeCount, carCount;
    Node firstNode;
    int IniDemand = 50;
    
    public Chromosom() {
        arrNode=new ArrayList<>();
        currentDemand=IniDemand;
        carCount=1;
        rechargeCount=0;
        totalDistance=0;
        fitness=0;
        firstNode=new Node();
    }
    
    public Chromosom(Chromosom chrome) {
        arrNode = chrome.arrNode;
        currentDemand=chrome.currentDemand;
        carCount=chrome.carCount;
        rechargeCount=chrome.rechargeCount;
        totalDistance=chrome.totalDistance;
        fitness=chrome.fitness;
        firstNode=chrome.firstNode;
    }

//    public Chromosom(Node firstNode) {
//        this.firstNode = firstNode;
//        carCount=1;
//        currentDemand=IniDemand;
//        rechargeCount=0;
//        totalDistance=0;
//    }

    
    public ArrayList<Node> getArrNode() {
        return arrNode;
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
    
    //main methods
    
    public void setFirstNode(){
        this.firstNode=this.arrNode.get(0);
//        this.arrNode.remove(0);    //这里remove 了0
    }

    public Node getFirstNode() {
        return firstNode;
    }
    
    public void calFitness(){
//        this.fitness=1/(totalDistance+0.00001);  
    	this.fitness = totalDistance;
    }
    
    public double calDistNode(Node aNode, Node bNode){
        double tempDistance=0;
        tempDistance=Math.sqrt(Math.pow(aNode.getX()-bNode.getX(), 2)+Math.pow(aNode.getY()-bNode.getY(), 2));
        return tempDistance;
    }
    
    public boolean deductDemand(double demand){
//        boolean recharge=false;
//        if(currentDemand-demand>0){
//            currentDemand=currentDemand-demand;
//            recharge=false;
//        }
//        else if(currentDemand-demand<=0){                                           
//            double tempDemand=Math.sqrt(Math.pow(currentDemand-demand, 2));
//            currentDemand=0;
//            rechargeDemand();
//            currentDemand=currentDemand-tempDemand;
//            recharge=true;
//        }
//        return recharge;
    	
    	boolean reCar = false;
    	if (currentDemand-demand>=0){ 
    		currentDemand=currentDemand-demand;
    		reCar = false;
    	}
    	else if (currentDemand-demand<0) {
    		rechargeDemand();   //新车新容量
    		currentDemand=currentDemand-demand;
    		reCar = true;
    	}
    	return reCar;
    }
    
    // 不采用Re-charge 的方法
    
    public void rechargeDemand(){
        currentDemand=IniDemand;
        carCountInc();
    }
    
    public void carCountInc(){
    	carCount++;
    }
    
    public int getCarCountInc(){
    	return carCount;
    }
//    
//    public void rechargeCountInc(){
//        rechargeCount++;
//    }
    
    public void calTotalDistance(){
        double tempDist=0, tempTotalDist=0;
        carCount = 1;
        currentDemand = IniDemand;

//        if (deductDemand(arrNode.get(0).getDemand())) {
//        	System.out.println("有顾客点的需求超过车辆容量限制");
//        }
        ArrayList<Node> tempArrNode = new ArrayList<Node>();
        for(int i=0; i<arrNode.size()-1; i++){
        	while (i > 0 && arrNode.get(i) == firstNode) {
        		if (arrNode.get(i-1) == firstNode) {
        			arrNode.remove(i);
        		}
        		else {
        			currentDemand = IniDemand;
        		}
        		if (i+1>=arrNode.size()-1 || arrNode.get(i+1)!=firstNode){
        			break;
        		}
        		i++;
        	}
        	tempArrNode.add(arrNode.get(i));
            boolean tempBool=false;
            tempDist=calDistNode(arrNode.get(i), arrNode.get(i+1));
            tempBool=deductDemand(arrNode.get(i+1).getDemand());
            if(tempBool==true){
                tempTotalDist += calDistNode(firstNode, arrNode.get(i)) + calDistNode(firstNode, arrNode.get(i+1));
                tempArrNode.add(firstNode);
            }
            else 
            tempTotalDist += tempDist;
        }
        tempTotalDist=tempTotalDist+calDistNode(firstNode, arrNode.get(arrNode.size()-1));
        totalDistance=tempTotalDist;
    }
    

    //    不在需要这个功能anymore   
//    // 去除所有0点  变异的时候 以及最后结果输出的时候 不需要0点

//    public void clearFirstNodeIn() {
//		for (int i=1;i<arrNode.size();i++){
//			if(arrNode.get(i) == firstNode){
//				arrNode.remove(i);
//			}
//		}
//	}
    
 // 相对的，我们需要一个加上所有零点的功能。
//  public void addFirstNodeIn() {
//  	double tempDemand = IniDemand;
//  	ArrayList<Node> tempArrNode = new ArrayList<Node>();
//  	tempArrNode.add(arrNode.get(0));
//		for (int i=1; i<arrNode.size(); i++){
//			tempDemand -= arrNode.get(i).getDemand();
//			if (tempDemand < 0){
//				tempDemand = IniDemand - arrNode.get(i).getDemand();
//				tempArrNode.add(firstNode);
//			}
//			tempArrNode.add(arrNode.get(i));
//		}
//		arrNode = tempArrNode;
//	}
    
    public void createGen(){
        Collections.shuffle(arrNode.subList(1, arrNode.size()));
    }
    
    public void testPrint(){
        for(int i=0; i<arrNode.size(); i++){
             System.out.println(arrNode.get(i).getIndex()+" "+arrNode.get(i).getX()+" "+arrNode.get(i).getY()+" "+arrNode.get(i).getDemand());
        }
    }
    
}