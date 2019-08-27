package vrp_multiD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;




/*
 *  编码方式 为  全客户  (判断分车是由计算超量来判断的 可以切换成 0 来隔开)--划掉
 * 
 * 每一段路由，由一个depot开始，到下一个depot之前结束。或者到编码末尾自然结束。
 * 
 * e.g.  023-145-067    三段路由
 *    
*/

public class Population {
    ArrayList<Node> nodeList = new ArrayList<>();
    ArrayList<Node> arrNode = new ArrayList<>();
    ArrayList<Chromosome> arrChrom = new ArrayList<>();
    double probCross, probMutate, probSuvive;
    int totalPopulation, curGeneration;
    int numCus = 0, numDep = 0;
    double carDem = 0.0;
    int iniChrom = 50;
    Chromosome bestChromosome;
    
    //初始化种群
    public Population() {
    }

    public Population(double probCross, double probMutate, double probSuvive, int totalPopulation) {       
        this.probCross = probCross;
        this.probMutate = probMutate;
        this.probSuvive = probSuvive;
        this.totalPopulation = totalPopulation;
    }

    public Population(double probCross, double probMutate, double probSuvive, int totalPopulation, int generation) {
        this.probCross = probCross;
        this.probMutate = probMutate;
        this.probSuvive = probSuvive;
        this.totalPopulation = totalPopulation;
        this.curGeneration = generation;
    }
    
	public void setParameter(FileLoader fL) {
		this.carDem = fL.carDem;
		this.numCus = fL.numCus;
		this.numDep = fL.numDep;
	}
    
    public ArrayList<Chromosome> getarrChrom() {
        return arrChrom;
    }

    public void setarrChrom(ArrayList<Chromosome> arrChrom) {
        this.arrChrom.addAll(arrChrom);   
    }

    public ArrayList<Node> getArrNode() {
        return arrNode;
    }

    public void setArrNode(ArrayList<Node> arrNode) {
        this.arrNode.addAll(arrNode);
    }
    
    public double getProbCross() {
        return probCross;
    }

    public void setProbCross(double probCross) {
        this.probCross = probCross;
    }

    public double getProbMutate() {
        return probMutate;
    }

    public void setProbMutate(double probMutate) {
        this.probMutate = probMutate;
    }

    public int getGeneration() {
        return curGeneration;
    }

    public void setGeneration(int generation) {
        this.curGeneration = generation;
    }

    public void setNodeList(ArrayList<Node> arrNode) {
        this.nodeList.addAll(arrNode);
    }
    
    public int getTotalPopulation() {
        return totalPopulation;
    }

    public void setTotalPopulation(int totalPopulation) {
        this.totalPopulation = totalPopulation;
    }
    
    public void setPopulation(ArrayList<Chromosome> arrChrom){
        this.arrChrom.addAll(arrChrom);
    }
    
    public void createPopulation(){
        for(int i=0; i<totalPopulation; i++){
            Chromosome Chrom=new Chromosome(numCus, numDep, carDem, nodeList);
            arrChrom.add(Chrom);
        }
        for(int i=0; i<arrChrom.size(); i++){
            arrChrom.get(i).createGen();
            arrChrom.get(i).calTotalDistance();
            arrChrom.get(i).calFitness();
        }
    }

    public void crossOverAll(){
        ArrayList<Chromosome> temparrChrom=new ArrayList<>();
        int currentMax=arrChrom.size();
        for(int i=0; i<currentMax || i < 11; i++){  // 前十一个发生交换(因为排过序了)
            Chromosome tempChrom=new Chromosome(numCus, numDep, carDem, nodeList);
            Random rand = new Random();
            double randDouble=rand.nextDouble();
            for (int j=i+1; j<currentMax; j++){
            	if(randDouble<probCross){
            		tempChrom=crossOverChromosome(arrChrom.get(i), arrChrom.get(j));
            		if(tempChrom.getFitness()!=arrChrom.get(i).getFitness()&&tempChrom.getFitness()!=arrChrom.get(j).getFitness()){
            			temparrChrom.add(tempChrom);
            		}
            	}
            }
            arrChrom.addAll(temparrChrom);
        }
    }
    //method crossover between Chromosome A and Chromosome B
    public Chromosome crossOverChromosome(Chromosome aChrom, Chromosome bChrom){
    	int numCar = aChrom.carCount+bChrom.carCount;
    	Chromosome tempChrom = new Chromosome(numCus, numDep, carDem, nodeList), crossedChrom = new Chromosome(numCus, numDep, carDem, nodeList);
    	int i=0, j=0;
    	Random rand = new Random();
    	boolean[] flag = new boolean[numCus+numDep];
    	double randPro = rand.nextDouble();
    	
    	while (i<aChrom.arrNode.size() || j<bChrom.arrNode.size()){
    		boolean isCopy = true;
    		ArrayList<Node> tempSec= new ArrayList<Node>();
    		if (j>=bChrom.arrNode.size() || (randPro > 0.5*probCross && i<aChrom.arrNode.size())){ // 我们认为  排名靠前的a 更有资格贡献
    			tempSec.add(aChrom.arrNode.get(i));
    			i++;
    			while (i<aChrom.arrNode.size()){
    				int index = Integer.parseInt(aChrom.arrNode.get(i).index);
    				if (index < numDep) break;
    				if (!flag[index]) {
    					tempSec.add(aChrom.arrNode.get(i));
    				} 
    				else {
    					isCopy = false;
    				}
    				i++;
    			}
    			if (isCopy) {
    				for (int k=0; k<tempSec.size();k++){
    					int index = Integer.parseInt(tempSec.get(k).index);
    					flag[index]=true;
    					tempChrom.arrNode.add(tempSec.get(k));
    				}
    			}
    		}    		
    		else if (j<bChrom.arrNode.size()) {
    			tempSec.add(bChrom.arrNode.get(j));
    			j++;
    			while (j<bChrom.arrNode.size()){
    				int index = Integer.parseInt(bChrom.arrNode.get(j).index);
    				if (index < numDep) break;
    				if (!flag[index]) {
    					tempSec.add(bChrom.arrNode.get(j));
    				} 
    				else {
    					isCopy = false;
    				}
    				j++;
    			}
    		    if (isCopy) {
    				for (int k=0; k<tempSec.size();k++){
    					int index = Integer.parseInt(tempSec.get(k).index);
    					flag[index]=true;
    					tempChrom.arrNode.add(tempSec.get(k));
    				}
    			}
    		}
    	}
    	return completeChrome(flag, tempChrom, bChrom);
    }
    
    private Chromosome completeChrome(boolean[] flag, Chromosome tempChrom, Chromosome bChrom) {
		int i = tempChrom.arrNode.size();
		for (int j=1; j<bChrom.arrNode.size();j++) {
			int index = Integer.parseInt(bChrom.arrNode.get(j).index);
			if (!flag[index]){
				tempChrom.arrNode.add(bChrom.arrNode.get(j));
			}
		}
		Collections.shuffle(tempChrom.arrNode.subList(i, tempChrom.arrNode.size()));
    	tempChrom.calTotalDistance();	
    	tempChrom.calFitness();
		return tempChrom;
	}
    
    public void mutateAll(){
        ArrayList<Chromosome> temparrChrom=new ArrayList<>();
        int currentMax=arrChrom.size();
        for(int i=0; i<currentMax; i++){
        	Chromosome toDoChrom = new Chromosome(arrChrom.get(i));
            Random rand = new Random();
            double randDouble=rand.nextDouble();
            if(randDouble<probMutate){
                Chromosome tempChrom = mutateChromosome_change(toDoChrom);
                if(tempChrom.getFitness()!=arrChrom.get(i).getFitness()){
                    arrChrom.add(tempChrom);
                }
            }
        }
        currentMax=arrChrom.size();
        for(int i=0; i<currentMax; i++){
        	Chromosome toDoChrom = new Chromosome(arrChrom.get(i));
            Random rand = new Random();
            double randDouble=rand.nextDouble();
            if(randDouble<probMutate){
                Chromosome tempChrom = mutateChromosome_exchange(toDoChrom);
                if(tempChrom.getFitness()!=arrChrom.get(i).getFitness()){
                    arrChrom.add(tempChrom);
                }
            }
        }
    }
    
    public Chromosome mutateChromosome_change(Chromosome Chrom){
        Chromosome tempChrom = new Chromosome(Chrom);
        Node tempNodeA=null, tempNodeB=null;
        Random rand = new Random();
        double randDouble = rand.nextDouble();
        // 变异一： 变化或者删除 depot
        for (int i = 0; i < Chrom.arrNode.size(); i++) {
        	randDouble = rand.nextDouble();
        	if (randDouble > probMutate) {
        		tempChrom.arrNode.add(Chrom.arrNode.get(i));
        		continue;
        	}
        	int index = Integer.parseInt(Chrom.arrNode.get(i).getIndex());
        	if (index < numDep) {
        		randDouble = rand.nextDouble();
        		if (randDouble > 0.4) {
        			int newDep = rand.nextInt(numDep);
	        		tempChrom.arrNode.add(nodeList.get(newDep));	
        		}
        		else {
        			continue;
        		}
        	}
        	else {
        		tempChrom.arrNode.add(Chrom.arrNode.get(i));
        	}
        }
        tempChrom.calTotalDistance();	
        tempChrom.calFitness();
        return tempChrom;
    }
    
    public Chromosome mutateChromosome_exchange(Chromosome Chrom){
        Chromosome tempChrom = new Chromosome(numCus, numDep, carDem, nodeList);
        tempChrom = Chrom;
        int maxIndex = Chrom.arrNode.size();
        Random rand = new Random();
        double randDouble = rand.nextDouble();
        // 变异二 交换： 交换两个客户
        int exchangeTimes = rand.nextInt(numCus/2);
        for (int i =0; i < exchangeTimes; i++){
        	int posA = rand.nextInt(maxIndex), posB = rand.nextInt(maxIndex);
        	while (Integer.parseInt(Chrom.arrNode.get(posA).getIndex()) < numDep){
        		posA = rand.nextInt(maxIndex);
        	}
        	while (Integer.parseInt(Chrom.arrNode.get(posB).getIndex()) < numDep){
        		posB = rand.nextInt(maxIndex);
        	}
        	Node tempNodeA=Chrom.getArrNode().get(posA), tempNodeB=Chrom.getArrNode().get(posB);
	        tempChrom.getArrNode().set(posA, tempNodeB);
	        tempChrom.getArrNode().set(posB, tempNodeA);
        }
        tempChrom.calTotalDistance();	
        tempChrom.calFitness();
        return tempChrom;
    }
    
    public void selectBestChroms(int startMinIndex){
        bestChromosome=new Chromosome(numCus, numDep, carDem, nodeList);
        ArrayList<Double> arrFitness=new ArrayList<>();
        ArrayList<Chromosome> arrTempChrom=new ArrayList<>();
        boolean[] flag = new boolean[arrChrom.size()+1];
        for(int i=0; i<arrChrom.size(); i++){
            arrFitness.add(arrChrom.get(i).getFitness());
        }
//        Collections.sort(arrFitness, Collections.reverseOrder());   // 从 大到小
        Collections.sort(arrFitness);
        int count=0;
        for (int i=0; i<arrChrom.size(); i++){
        	for(int j=0; j<arrChrom.size(); j++){
        		if(!flag[j] && arrChrom.get(j).getFitness()==arrFitness.get(i)){
        			flag[j] = true;
        			if(i==0){
        				bestChromosome=arrChrom.get(j);
        				arrTempChrom.add(arrChrom.get(j));
        			}
        			else {
        				Random rand = new Random();
        				if (rand.nextDouble() < Math.max(0.5, Math.pow(0.9, count))) {
        					arrTempChrom.add(arrChrom.get(j));
        					count++;
        				}
        			}
        			break;
        		}      
        	}
        	if (count >= startMinIndex) break;
        }
       
        arrChrom.clear();
        arrChrom.addAll(arrTempChrom);              //重新排序过后的染色体数组
        bestChromosome.calTotalDistance();
    }

    public void testPrint(){
        for(int i=0; i<arrChrom.size(); i++){
            System.out.print(i+" ");
            for(int j=0; j<arrChrom.get(i).getArrNode().size(); j++){
                System.out.print(arrChrom.get(i).getArrNode().get(j).getIndex()+"-");
            }
            System.out.println("\tTotalDistance : "+arrChrom.get(i).getTotalDistance()+"\tFitness : "+arrChrom.get(i).getFitness());
        }
    }
    
    public void testPrintWBestChrome(){
        for(int i=0; i<arrChrom.size(); i++){
            System.out.print(i+" ");
            for(int j=0; j<arrChrom.get(i).getArrNode().size(); j++){
                System.out.print(arrChrom.get(i).getArrNode().get(j).getIndex()+"-");
            }
            System.out.println("\tTotalDistance : "+arrChrom.get(i).getTotalDistance()+"\tFitness : "+arrChrom.get(i).getFitness());
        }
        System.out.println("Best Chromosome/Route : ");
        for(int i=0; i<bestChromosome.getArrNode().size(); i++){
            System.out.print(bestChromosome.getArrNode().get(i).getIndex()+"-");
        }
        System.out.println("\tTotalDistance : "+bestChromosome.getTotalDistance()+"\tFitness : "+bestChromosome.getFitness());
        System.out.println();
    }
}