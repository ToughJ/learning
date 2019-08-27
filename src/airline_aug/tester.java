package airline_aug;

public class tester {
	public static void main(String[] args) throws Exception {
		// Data_Loader dl = new Data_Loader();
		// dl.loadNodeInfo("./airline-new-data/masterproblemModel/data-info-0.txt");
		// dl.testPrint();
		Crew_Pairing cp = new Crew_Pairing("./airline-new-data/data-info-0.txt");
		// cp.subproblem.testSubModel();
		// cp.test();
		cp.runCrew_pairing();
	}
}
