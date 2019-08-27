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
 * 		�����������һ������G(N,A)��
 * 		�ڵ�N ��Ϊ����
 * 			��һ����Depot
 * 			�ڶ�����Customer��
 * 			
 * 		
 * 		����Ҫ�����������Ż���ÿ��ʱ����ڣ�ÿ�죩ÿ���ͻ���C��ȷ��������d>0����ȻҲ����С���� ��ʾͬʱȡ�ͻ�������������Ȳ����ǣ���
 * 		���ǵľ��߰�����
 * 			ÿ�쳵��Ѳ��·���������ģ��Ӷ�ʹ���ܳɱ���С�����þ��롢ʱ�任��ɵ�����ɱ� + �����̶��ɱ���ʾ��
 */
