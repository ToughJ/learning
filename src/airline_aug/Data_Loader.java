package airline_aug;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Data_Loader {
	public ArrayList<Air_section> arrSec = new ArrayList<>();
	public ArrayList<String> arrCity = new ArrayList<>();

	public Data_Loader() {
	}

	@SuppressWarnings("deprecation")
	public void loadNodeInfo(String url) throws FileNotFoundException, IOException, ParseException {
		BufferedReader br = new BufferedReader(new java.io.FileReader(url));
		String line;
		String[] splitLine;
		int index = 0;
		ArrayList<Air_section> tempArrSec = new ArrayList<>();
		// Air_section lastSec = new Air_section();
		while ((line = br.readLine()) != null) {
			splitLine = line.split("\\s+");
			Air_section tempSec = new Air_section();

			tempSec.setPlaneIndex(Integer.valueOf(splitLine[0]));
			tempSec.setFlightName(splitLine[1]);
			tempSec.secNum = Integer.valueOf(splitLine[2]);

			String de = splitLine[3], ar = splitLine[5];
			tempSec.departureName = de;
			tempSec.arrivalName = ar;
			int deInt, arInt;
			if (!arrCity.contains(de))
				arrCity.add(de);
			deInt = arrCity.indexOf(de);
			if (!arrCity.contains(ar))
				arrCity.add(ar);
			arInt = arrCity.indexOf(ar);
			tempSec.setDeparture(deInt);
			tempSec.setArrival(arInt);

			Date date1, date2;
			int d1, d2;
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
			date1 = simpleDateFormat.parse(splitLine[4]);
			date2 = simpleDateFormat.parse(splitLine[6]);
			tempSec.dateArr = date2;
			tempSec.dateDep = date1;
			d1 = date1.getHours() * 60 + date1.getMinutes();
			d2 = date2.getHours() * 60 + date2.getMinutes();

			if (d2 < d1)
				tempSec.transDay = 1;

			String days = splitLine[7];
			for (int i = 0; i < days.length(); i++) {
				int day = Integer.valueOf(days.charAt(i));
				int tmpd1, tmpd2;
				Air_section temp = new Air_section(tempSec);
				temp.setIndex(index++);
				temp.setDay(day - 48);
				tmpd1 = d1 + (temp.day - 1) * 24 * 60;
				tmpd2 = d2 + (temp.day - 1) * 24 * 60;
				if (d2 < d1) {
					if (temp.day == 7)
						tmpd2 -= 6 * 24 * 60;
					else
						tmpd2 += 24 * 60;
				}
				temp.setDepartureTime(tmpd1);
				temp.setArrivalTime(tmpd2);
				printSec(temp);
				tempArrSec.add(temp);

			}

			/*
			 * splitLine=line.split("/s+"); Air_section tempSec = new
			 * Air_section(); tempSec.setIndex(index++);
			 * tempSec.setPlaneIndex(Integer.valueOf(splitLine[0]));
			 * tempSec.setPlaneName(splitLine[1]);
			 * tempSec.setFlightName(splitLine[2]); tempSec.secNum =
			 * Integer.valueOf(splitLine[3]);
			 * 
			 * String de = splitLine[4],ar = splitLine[5]; int deInt,arInt; if
			 * (!arrCity.contains(de)) arrCity.add(de); deInt =
			 * arrCity.indexOf(de); if (!arrCity.contains(ar)) arrCity.add(ar);
			 * arInt = arrCity.indexOf(ar); tempSec.setDeparture(deInt);
			 * tempSec.setArrival(arInt);
			 * 
			 * tempSec.setDay(Integer.valueOf(splitLine[6]));
			 * 
			 * 
			 * Date date1,date2; int d1,d2; SimpleDateFormat simpleDateFormat =
			 * new SimpleDateFormat("yyyy/mm/dd HH:mm"); date1 =
			 * simpleDateFormat.parse(splitLine[7]+" "+splitLine[8]); date2 =
			 * simpleDateFormat.parse(splitLine[9]+" "+splitLine[10]);
			 * tempSec.dateArr = date2; tempSec.dateDep = date1; d1 =
			 * date1.getHours()*60+date1.getMinutes(); d2 =
			 * date2.getHours()*60+date2.getMinutes();
			 * tempSec.setDepartureTime(d1); tempSec.setArrivalTime(d2); if(d2 <
			 * d1) tempSec.transDay = 1;
			 * 
			 * 
			 * tempArrSec.add(tempSec);
			 */
		}
		arrSec.clear();
		arrSec.addAll(tempArrSec);
		br.close();
		printCity();
	}

	private void printSec(Air_section temp) throws IOException {
		// print
		File writename = new File("./airline-new-data/SecLists/Sec" + temp.index + ".txt");
		writename.createNewFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(writename));
		DateFormat format1 = new SimpleDateFormat("HH:mm");
		// save
		out.write(temp.departureName + " --> " + temp.arrivalName + "\r\n");
		out.write("weekday " + temp.day + "    " + format1.format(temp.dateDep) + " -- " + format1.format(temp.dateArr)
				+ "\r\n");
		out.write("AirLine Index: " + temp.flightName + "   Plane Index: " + temp.planeIndex);

		out.flush();
		out.close();

	}
	
	private void printCity() throws IOException {
		// print
		for (int i=0; i<arrCity.size();i++){
			File writename = new File("./airline-new-data/CityLists/City" + i + ".txt");
			writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			out.write("City Index: " + i + "   " + arrCity.get(i));

			out.flush();
			out.close();
		}
	}
	
	public void testPrint() {
		System.out.println("check CitySet");
		for (String city : arrCity)
			System.out.println(arrCity.indexOf(city) + " : " + city);
		System.out.println("----" + "check arrSec ------");
		for (Air_section sec : arrSec) {
			System.out.println(sec.index + " : " + sec.day + "  " + arrCity.get(sec.departure) + " --> "
					+ arrCity.get(sec.arrival) + "  " + sec.tDep + "  " + sec.tArr);
			if (sec.index > 100)
				break;
		}

	}
}