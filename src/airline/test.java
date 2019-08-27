package airline;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

public class test {
	 public static void main(String[] args) throws Exception {
//		 Data_loader dl = new Data_loader();
//		 dl.loadNodeInfo("data-info-2.txt");
//		 dl.testPrint();
		 Crew_pairing cp = new Crew_pairing("data-info-.txt");
//		 cp.test();
		 cp.runCrew_pairing();
	 }
}
