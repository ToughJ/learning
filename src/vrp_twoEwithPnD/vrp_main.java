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
 * 		��������������������㣩����G(N,A)��
 * 		�ڵ�N��������ɣ�
 * 			��һ����һ�����������D��
 * 			�ڶ����Ƕ���м�ת�˵�S��
 * 			�������Ƕ�����տͻ���C��
 * 		�������������೵��ɣ�
 * 			�����V��ɴ������D���м��S��Ѳ�Σ�
 * 			С�����ֳ�E��ɴ��м��S���ͻ���C��Ѳ�Ρ�
 * 		����ӵ�һ��Dֱ������C������
 * 		���ֳ���Ҫ��������������ͬ��
 * 		����Ҫ�����������Ż���ÿ��ʱ����ڣ�ÿ�죩ÿ���ͻ���C��ȷ�������󣨰���ȡ�����ͻ�����
 * 		���ǵľ��߰�����
 * 			ÿ�����೵��Ѳ��·���������ģ��Ӷ�ʹ���ܳɱ���С�����þ��롢ʱ�任��ɵ�����ɱ� + �����̶��ɱ���ʾ��
 */
