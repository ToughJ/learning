package airline_aug;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ss_Parameter {

	public  ArrayList<Air_section> arrSec = new ArrayList<>();
	public  ArrayList<String> arrCity = new ArrayList<>();
	public  ArrayList<Tour_ofDuty> arrToD = new ArrayList<>();

	public  int toDNum = 250; // the number of ToD we used

	public ss_Parameter() throws FileNotFoundException, IOException, ParseException {
		Data_Loader dl = new Data_Loader();
	    dl.loadNodeInfo("./airline-new-data/data-info-0.txt");
		arrSec = dl.arrSec;
		arrCity = dl.arrCity;
	}

	public void produce_schedule(){
		
	}
	
	public  void loadToDs() throws FileNotFoundException, IOException, ParseException {
		int index = 0;
		for (; index < toDNum; index++) {
			String url = "./airline-new-data/usedToD/ToD" + index + ".txt";
			loadToDInfo(url);
		}
	}

	static int index = 0;

	public  void loadToDInfo(String url) throws FileNotFoundException, IOException, ParseException {
		BufferedReader br = new BufferedReader(new java.io.FileReader(url));
		String line;
		String[] splitLine;

		Tour_ofDuty tempToD = new Tour_ofDuty();

		tempToD.ToDindex = index++;
		line = br.readLine();
		int tempCity = 0, tempTE = -1, tempTS = -1, day = 0;
		String cityName = null;
		ArrayList<Air_section> tempDay = new ArrayList<>();
		while ((line = br.readLine()) != null) {
			splitLine = line.split("\\s+");
			if (splitLine[0].compareTo("ToD") == 0) {
				tempToD.arrDay.add(tempDay);
				tempDay = new ArrayList<>();
				day++;
				continue;
			}
			// STILL don't care if the order is wrong
			tempDay.add(arrSec.get(Integer.valueOf(splitLine[1])));

			Date date1, date2;
			int d1, d2;
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
			date1 = simpleDateFormat.parse(splitLine[14]);
			date2 = simpleDateFormat.parse(splitLine[16]);
			tempToD.dateDep=date1;
			tempToD.dateArr=date2;
			d1 = date1.getHours() * 60 + date1.getMinutes();
			d2 = date2.getHours() * 60 + date2.getMinutes();
			
			if (day == 0) {
				int tmp1 = d1 + Integer.valueOf(splitLine[11]) * 24 * 60;
				if (tempTS == -1 || tempTS > tmp1) {
					cityName = splitLine[6];
					tempCity = arrCity.indexOf(cityName);
					tempTS = tmp1;
				}
			}
			int tmp2 = d2 + Integer.valueOf(splitLine[11]) * 24 * 60;
			if (tmp2 < tempTS)
				tmp2 += 7 * 24 * 60;
			if (tempTE == -1 || tempTE < tmp2) {
				tempTE = tmp2;
			}
		}
		tempToD.tSta = tempTS;
		tempToD.tEnd = tempTE;
		tempToD.settle = tempCity;
		tempToD.settleName = cityName;
		arrToD.add(tempToD);
		br.close();
	}

	public  void testLoad() throws FileNotFoundException, IOException, ParseException{
		// to test loading 
		toDNum = 100;
		loadToDs();
		SimpleDateFormat Format = new SimpleDateFormat("HH:mm");
		for (int i = 0; i < toDNum; i++) {
			System.out.println("City name: " + arrCity.get(arrToD.get(i).settle) + " date: "+
					Format.format(arrToD.get(i).dateDep) + " --> " + Format.format(arrToD.get(i).dateArr)); 
		}
	}
	
}
