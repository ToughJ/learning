package vrp_twoEwithPnD;

import java.io.IOException;

public class vrp_main {

	public static void main(String[] args) throws IOException {
		
		
		FileLoader fL=new FileLoader();
		fL.loadNodeInfo("data-info- 4.txt"); 
//		fL.testPrint();
//	    System.out.println("--------");
        Population pop=new Population(0.8, 0.2, 50);
        pop.setNodeList(fL.getArrNode());
        pop.setParameter(fL);
        pop.createPopulation();
//        pop.testPrint();
        GA_driver gaDrive=new GA_driver();
        gaDrive.setPopulation(pop);
        gaDrive.setUrlSaveFile("Best Route - 4.txt"); 
        gaDrive.evolve(50); 
	}  



}
/* 
 * 
 * 		问题可描述成两级（三层）网络G(N,A)。
 * 		节点N由三层组成，
 * 			第一层是一个中央出发点D；
 * 			第二层是多个中间转运点S；
 * 			第三层是多个最终客户点C。
 * 		两级运输由两类车完成，
 * 			大货车V完成从中央点D到中间点S的巡游；
 * 			小型三轮车E完成从中间点S到客户点C的巡游。
 * 		允许从第一级D直接满足C的需求
 * 		两种车主要区别在于容量不同。
 * 		假设要我们所考虑优化的每个时间段内（每天）每个客户点C有确定的需求（包括取货和送货）。
 * 		我们的决策包括：
 * 			每天两类车的巡回路线是怎样的，从而使得总成本最小（可用距离、时间换算成的运输成本 + 车辆固定成本表示）
 */
