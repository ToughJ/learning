package vrp_GA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// 编码方式 为  全客户，判断分车是由计算超量来判断的
// 可以切换成 0 来隔开


public class Population {
    ArrayList<Node> arrNode = new ArrayList<>();
    ArrayList<Chromosom> arrChrom = new ArrayList<>();
    double probCross, probMutate;
    int totalPopulation, curGeneration;
    Chromosom bestChromosom;
    int iniNode = 0;
    int iniChrome = 50;
    
    //初始化种群
    public Population() {
    }

    public Population(double probCross, double probMutate, int totalPopulation) {       
        this.probCross = probCross;
        this.probMutate = probMutate;
        this.totalPopulation = totalPopulation;
        bestChromosom=new Chromosom();
    }

    public Population(double probCross, double probMutate, int totalPopulation, int generation) {
        this.probCross = probCross;
        this.probMutate = probMutate;
        this.totalPopulation = totalPopulation;
        this.curGeneration = generation;
        bestChromosom=new Chromosom();
    }
    
    public void setIniNode(int iniNode) {
    	this.iniNode = iniNode;
    }
    
    public ArrayList<Chromosom> getarrChrom() {
        return arrChrom;
    }

    public void setarrChrom(ArrayList<Chromosom> arrChrom) {
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

    
    public int getTotalPopulation() {
        return totalPopulation;
    }

    public void setTotalPopulation(int totalPopulation) {
        this.totalPopulation = totalPopulation;
    }
    
    public void setPopulation(ArrayList<Chromosom> arrChrom){
        this.arrChrom.addAll(arrChrom);
    }
    
    public void createPopulation(){
        for(int i=0; i<totalPopulation; i++){
            Chromosom chrom=new Chromosom();
            arrChrom.add(chrom);
        }
        for(int i=0; i<arrChrom.size(); i++){
            arrChrom.get(i).setArrNode(arrNode);
            arrChrom.get(i).setFirstNode();
            arrChrom.get(i).createGen();
            arrChrom.get(i).calTotalDistance();
            arrChrom.get(i).calFitness();
        }
    }
    
    //method crossover between Chromosome A and Chromosome B
    public Chromosom crossOverChromosom(Chromosom aChrom, Chromosom bChrom){
    	int numCar = aChrom.carCount+bChrom.carCount;
    	Chromosom tempChrom = new Chromosom(), crossedChrom = new Chromosom();
    	int i=1, j=1;
    	Random rand = new Random();
    	boolean[] flag = new boolean[iniNode+2];
    	
    	tempChrom.arrNode.add(aChrom.firstNode);
    	tempChrom.firstNode = aChrom.firstNode;
    	
    	while (i<aChrom.arrNode.size() || j<bChrom.arrNode.size()){
    		double randPro = rand.nextDouble();
    		boolean isCopy = true;
    		ArrayList<Node> tempSec= new ArrayList<Node>();
    		if (j>=bChrom.arrNode.size() || (randPro > 0.5*probCross && i<aChrom.arrNode.size())){ // 我们认为  排名靠前的a 更有资格贡献
    			while (i<aChrom.arrNode.size() && aChrom.arrNode.get(i) != tempChrom.firstNode  ){
    				int index = Integer.parseInt(aChrom.arrNode.get(i).index);
    				if (!flag[index]) {
    					tempSec.add(aChrom.arrNode.get(i));
    				} 
    				else {
    					isCopy = false;
    				}
    				i++;
    			}
    			i++;
    			if (isCopy) {
    				for (int k=0; k<tempSec.size();k++){
    					int index = Integer.parseInt(tempSec.get(k).index);
    					flag[index]=true;
    					tempChrom.arrNode.add(tempSec.get(k));
    				}
    			}
    		}    		
    		else {
    			while (j<bChrom.arrNode.size() && bChrom.arrNode.get(j) != tempChrom.firstNode){
    				int index = Integer.parseInt(bChrom.arrNode.get(j).index);
    				if (!flag[index]) {
    					tempSec.add(bChrom.arrNode.get(j));
    				} 
    				else {
    					isCopy = false;
    				}
    				j++;
    			}
    		    j++;
    		    if (isCopy) {
    				for (int k=0; k<tempSec.size();k++){
    					int index = Integer.parseInt(tempSec.get(k).index);
    					flag[index]=true;
    					tempChrom.arrNode.add(tempSec.get(k));
    				}
    			}
    		}
    	}
    	return completeChrom(flag, tempChrom, bChrom);
//    	tempChrom.calTotalDistance();	
//        tempChrom.calFitness();
//        crossedChrom = tempChrom;
//        // check if everyNode been put in
//        for (int k=1; k<iniNode;k++) {
//        	if (!flag[k]){
//        		System.out.println("点没有全部遍历哦");
//        	}
//        }
//    	return crossedChrom;  	
    	
//        Chromosom tempChrom=new Chromosom();
//        int lowerSection=aChrom.getArrNode().size()/2;
//        int upperSection=bChrom.getArrNode().size();
//        for(int i=0; i<lowerSection; i++){
//            tempChrom.arrNode.add(aChrom.getArrNode().get(i));
//            
//        }
//        for(int i=lowerSection; i<upperSection; i++){
//            tempChrom.arrNode.add(bChrom.getArrNode().get(i));
//        }
//        tempChrom.calTotalDistance();
//        tempChrom.calFitness();
//        return tempChrom;
    }
    
    private Chromosom completeChrom(boolean[] flag, Chromosom tempChrom, Chromosom bChrom) {
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

	//method  mutate  1 Chromosome
    public Chromosom mutateChromosom(Chromosom Chrom){
        Chromosom tempChrom = new Chromosom(), mutatedChrom = new Chromosom();
        tempChrom=Chrom;
        Node tempNodeA=null, tempNodeB=null;
        Random rand = new Random();
        int numNode = Chrom.arrNode.size() - 1;
        int randomNumA = rand.nextInt(((numNode) - 1) + 1) + 1;
        int randomNumB = rand.nextInt(((numNode) - 1) + 1) + 1;
        tempNodeA=Chrom.getArrNode().get(randomNumA);
        tempNodeB=Chrom.getArrNode().get(randomNumB);
        tempChrom.getArrNode().set(randomNumA, tempNodeB);
        tempChrom.getArrNode().set(randomNumB, tempNodeA);
        tempChrom.calTotalDistance();	
        tempChrom.calFitness();
        mutatedChrom=tempChrom;
        return mutatedChrom;
    }
    
    public void crossOverAll(){
        ArrayList<Chromosom> temparrChrom=new ArrayList<>();
        int currentMax=arrChrom.size();
        // add in the firstNode
        for(int i=0; i<currentMax || i < 11; i++){  // 前十一个发生交换(因为排过序了)
            Chromosom tempChrom=new Chromosom();
            Random rand = new Random();
            double randDouble=rand.nextDouble();
            for (int j=i+1; j<currentMax; j++){
            	if(randDouble<probCross){
            		int randomInt = rand.nextInt(((currentMax) - j) + 0) + j;
            		tempChrom=crossOverChromosom(arrChrom.get(i), arrChrom.get(randomInt));
            		if(tempChrom.getFitness()!=arrChrom.get(i).getFitness()||tempChrom.getFitness()!=arrChrom.get(randomInt).getFitness()){
            			temparrChrom.add(tempChrom);
            		}
            	}
            }
            
//            if(randDouble<probCross){s
//                int randomInt = rand.nextInt(((iniChrome) - 1) + 1) + 0;
////                System.out.println(arrChrom.get(i).getArrNode().get(0).getIndex()+" "+arrChrom.get(randomInt).getArrNode().get(0).getIndex());
//                tempChrom=crossOverChromosom((Chromosom)arrChrom.get(i), (Chromosom)arrChrom.get(randomInt));
//                if(tempChrom.getFitness()!=arrChrom.get(i).getFitness()||tempChrom.getFitness()!=arrChrom.get(randomInt).getFitness()){
//                    temparrChrom.add(tempChrom);
//                }
//                
//            }
            
//            for(int j=0; j<currentMax; j++){
//                Random rand = new Random();
//                double randDouble=rand.nextDouble();
//                if(randDouble<probCross){
//                    Chromosom tempChrom=new Chromosom(), tempChromA=new Chromosom(), tempChromB=new Chromosom();
//                    tempChromA=arrChrom.get(i);
//                    tempChromB=arrChrom.get(j);
//                    System.out.println(tempChromA.getFirstNode().getIndex()+" "+tempChromB.getFirstNode().getIndex());
//                    tempChrom=crossOverChromosom((Chromosom)tempChromA, (Chromosom)tempChromB);
//                    temparrChrom.add((Chromosom)tempChrom);
//                }
//            }
//            System.out.println(i);
            
            arrChrom.addAll(temparrChrom);
        }
    }
    
    public void mutateAll(){
        ArrayList<Chromosom> temparrChrom=new ArrayList<>();
        int currentMax=arrChrom.size();
        for(int i=0; i<currentMax; i++){
        	Chromosom toDoChrome = new Chromosom(arrChrom.get(i));
            Random rand = new Random();
            double randDouble=rand.nextDouble();
            if(randDouble<probMutate){
            	
                Chromosom tempChrom = mutateChromosom(toDoChrome);
                if(tempChrom.getFitness()!=arrChrom.get(i).getFitness()){
                    arrChrom.add(tempChrom);
                }
            }
        }
    }
    
    public void selectBestChroms(int startMinIndex){
        ArrayList<Double> arrFitness=new ArrayList<>();
        ArrayList<Chromosom> arrTempChrom=new ArrayList<>();
        boolean[] flag = new boolean[arrChrom.size()+1];
        for(int i=0; i<arrChrom.size(); i++){
            arrFitness.add(arrChrom.get(i).getFitness());
        }
//        Collections.sort(arrFitness, Collections.reverseOrder());   // 从 大到小
        Collections.sort(arrFitness);
        for (int i=0; i<startMinIndex; i++){
        	for(int j=0; j<arrChrom.size(); j++){
        		if(!flag[j] && arrChrom.get(j).getFitness()==arrFitness.get(i)){
        			flag[j] = true;
        			arrTempChrom.add(arrChrom.get(j));
        			if(i==0){
        				bestChromosom=arrChrom.get(j);
        			}
        			break;
        		}      
        	}
        }
       
        arrChrom.clear();
        arrChrom.addAll(arrTempChrom);              //重新排序过后的染色体数组
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
    
    public void testPrintWBestChrom(){
        for(int i=0; i<arrChrom.size(); i++){
            System.out.print(i+" ");
            for(int j=0; j<arrChrom.get(i).getArrNode().size(); j++){
                System.out.print(arrChrom.get(i).getArrNode().get(j).getIndex()+"-");
            }
            System.out.println("\tTotalDistance : "+arrChrom.get(i).getTotalDistance()+"\tFitness : "+arrChrom.get(i).getFitness());
        }
        System.out.println("Best Chromosom/Route : ");
        for(int i=0; i<bestChromosom.getArrNode().size(); i++){
            System.out.print(bestChromosom.getArrNode().get(i).getIndex()+"-");
        }
        System.out.println("\tTotalDistance : "+bestChromosom.getTotalDistance()+"\tFitness : "+bestChromosom.getFitness());
        System.out.println();
    }
}