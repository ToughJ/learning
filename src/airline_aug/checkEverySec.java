package airline_aug;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;



public class checkEverySec {

	static int secNum = 1420;
	static String[] namelist = new String[400];
	static ArrayList<ArrayList<Integer>> rec = new ArrayList<>();
	static int ToD_num = 176;

	public checkEverySec() throws IOException {

		String nameFile = "./airline-new-data/namelist.txt";
		BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(nameFile), "UTF-8"));
		String l = b.readLine();
		int in = 0;
		while ((l = b.readLine()) != null) {
			String[] sl = l.split("\\s+");
			namelist[in++] = sl[1];
		}
		b.close();

		for (int i = 0; i < secNum; i++) {
			ArrayList<Integer> thisrec = new ArrayList<>();
			rec.add(thisrec);
		}

		for (int iD = 0; iD < ToD_num; iD++) {

			String File = "./airline-new-data/used_Schedule/No." + iD + ".txt";
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(File), "UTF-8"));
			String line = br.readLine();
			int fw = 0;
			int we = 0;
			int startTime = -1;
			int end = -1;
			while (line != null) {

				String[] splitted = line.split("\\s+");
				if (splitted[0].equals("tod_num")) { // heading

				} else if (splitted[0].equals("from")) {
					fw = -1;
				} else {
					if (splitted.length == 14) {
						we = Integer.valueOf(splitted[9]);
						int st = fw * 24 * 60 * 7 + (we - 1) * 24 * 60
								+ Integer.valueOf(splitted[11].split(":")[0]) * 60
								+ Integer.valueOf(splitted[11].split(":")[1]);
						int ed = fw * 24 * 60 * 7 + (we - 1) * 24 * 60
								+ Integer.valueOf(splitted[13].split(":")[0]) * 60
								+ Integer.valueOf(splitted[13].split(":")[1]);
						if (startTime == -1) {
							startTime = st;
						}
						if (end != -1 && st < end) {
							fw++;
							st += 24 * 60 * 7;
							ed += 24 * 60 * 7;
						}
						if (ed < st) {
							we++;
							ed += 24 * 60;
							if (we == 8 && fw == -1) {
								fw++;
								we = 1;
							}
						}
						end = ed;
						int sec = Integer.valueOf(splitted[1]);
						if (fw == 0) {
							rec.get(sec).add(iD);
						}
						if (we == 8) {
							fw++;
							we = 1;
						}

					} else if (splitted.length == 3) {

					}
				}
				line = br.readLine();
			}
			br.close();
		}

		File writename = new File("./airline-new-data/checkEverySec.txt");
		writename.createNewFile(); //
		BufferedWriter out = new BufferedWriter(new FileWriter(writename));
		for (int i = 0; i < secNum; i++) {
			out.write("Sec "+i+ ": ");
			for (int j:rec.get(i)){
				out.write(namelist[j] + "-"+j+" ");
			}
			out.write("\r\n");
		}
		out.flush(); //
		out.close(); //
	}
	
	public static void main(String[] args) throws IOException{
		System.out.println("start to do: ...");
		checkEverySec ces = new checkEverySec();
		System.out.print("done!");
	}

}
