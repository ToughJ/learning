package vrp_multiD;

import java.io.IOException;

public class vrp_main {

	public static void main(String[] args) throws IOException {
		
		
		FileLoader fL=new FileLoader();
		fL.loadNodeInfo("Node-info-MD  - 4.txt"); 
//		fL.testPrint();
//	    System.out.println("--------");
        Population pop=new Population(0.8, 0.3, 0.9, 50);
        pop.setNodeList(fL.getArrNode());
        pop.setParameter(fL);
        pop.createPopulation();
//        pop.testPrint();
        GA_driver gaDrive=new GA_driver();
        gaDrive.setPopulation(pop);
        gaDrive.setUrlSaveFile("Best Route for MD - 4.txt"); 
        gaDrive.evolve(50); 
	}  

}
/* 
 * 
 * 		问题可描述成一个网络G(N,A)。
 * 		节点N 分为两类
 * 			第一类是Depot
 * 			第二类是Customer；
 * 			
 * 		
 * 		假设要我们所考虑优化的每个时间段内（每天）每个客户点C有确定的需求（d>0，当然也可以小于零 表示同时取送货的情况，这里先不考虑）。
 * 		我们的决策包括：
 * 			每天车的巡回路线是怎样的，从而使得总成本最小（可用距离、时间换算成的运输成本 + 车辆固定成本表示）
 */
