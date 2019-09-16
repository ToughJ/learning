package airline_aug;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ss_Parameter {

	public ArrayList<Air_section> arrSec = new ArrayList<>();
	public ArrayList<String> arrCity = new ArrayList<>();
	public ArrayList<Tour_ofDuty> arrToD = new ArrayList<>();
	public ArrayList<Schedule> arrSch = new ArrayList<>();
	public ArrayList<Integer> ali = new ArrayList<>(), bli = new ArrayList<>(), cli = new ArrayList<>();
	public int[][] cl;

	public int toDNum = 1663; // the number of ToD we used

	public ss_Parameter() throws FileNotFoundException, IOException, ParseException {
		Data_Loader dl = new Data_Loader();
		dl.loadNodeInfo("./airline-new-data/data-info-0.txt");
		arrSec = dl.arrSec;
		arrCity = dl.arrCity;
		loadRes();
		produce_schedule();
		initial_limit();
		initial_crewliving();
	}

	public void initial_limit() {
		// ali should be input by file
		for (int i = 0; i < arrSec.size(); i++) {
			bli.add(i);
			if (arrSec.get(i).tDep < 5 * 24 * 60) {
				cli.add(i);
			}
		}
	}

	public void initial_crewliving() {
		// imagine that people in each city is enough.
		cl = new int[10][arrCity.size()];
		for (int i = 0; i < 8; i++) { // -3 -2 -1 0 1 2 3 4 
			for (int j = 0; j < arrCity.size(); j++) {
				cl[i][j] = 100;
			}
		}
	}

	public void loadRes() throws FileNotFoundException, IOException, ParseException {
		int i = 0;
		for (; i < toDNum; i++) {
			String url = "./airline-new-data/res/res_" + i + ".txt";
			loadResInfo(url);
		}
	}

	int index = 0;

	// ending time includes the rest two days
	public void loadResInfo(String url) throws FileNotFoundException, IOException, ParseException {
		BufferedReader br = new BufferedReader(new java.io.FileReader(url));
		String line;
		String[] splitLine;
		Tour_ofDuty tempToD = new Tour_ofDuty();
		tempToD.ToDindex = index++;
		line = br.readLine();
		splitLine = line.split(",|\\s+");
		int fr, ed;
		int sp = 0, length = splitLine.length;
		fr = Integer.valueOf(splitLine[sp]);
		while (fr >= 1420) {
			sp++;
			fr = Integer.valueOf(splitLine[sp]);
		}
		ed = Integer.valueOf(splitLine[length - 1]);
		while (ed >= 1420) {
			length--;
			ed = Integer.valueOf(splitLine[length - 1]);
		}
		tempToD.settle = arrSec.get(fr).getDeparture();
		tempToD.settleName = arrCity.get(tempToD.settle);
		tempToD.day_ofWeek = arrSec.get(fr).day - 1;
		tempToD.dateDep = arrSec.get(fr).dateDep;
		tempToD.tSta = arrSec.get(fr).tDep;
		tempToD.dateArr = arrSec.get(ed).dateArr;
		tempToD.tEnd = arrSec.get(ed).tArr + 2 * 24 * 60;
		if (tempToD.tEnd > 7 * 24 * 60)
			tempToD.tEnd -= 7 * 24 * 60;
		if (tempToD.settle != arrSec.get(ed).getArrival()) {
			System.err.println("city not the same!!");
		}
		for (int i = 0; i <= length - 1; i++) {
			int tp = Integer.valueOf(splitLine[i]);
			if (tp >= 1420)
				continue;
			tempToD.secs.add(arrSec.get(tp));
		}
		arrToD.add(tempToD);
		br.close();

	}

	public void testLoad() throws FileNotFoundException, IOException, ParseException {
		// to test loading
		toDNum = 100;
		loadRes();
		// loadToDs();
		SimpleDateFormat Format = new SimpleDateFormat("HH:mm");
		for (int i = 0; i < toDNum; i++) {
			System.out.println("City name: " + arrCity.get(arrToD.get(i).settle) + " date: "
					+ Format.format(arrToD.get(i).dateDep) + " --> " + Format.format(arrToD.get(i).dateArr));
		}
	}

	public void produce_schedule() {
		int city, startday, endTime;
		for (Tour_ofDuty tod : arrToD) {
			Schedule sche = new Schedule();
			city = tod.settle;
			startday = tod.day_ofWeek;
			sche.city = city;
			sche.arrToD.add(tod);
			sche.tSta = tod.tSta;
			sche.tEnd = tod.tEnd;
			if (sche.tEnd < sche.tSta) {
				sche.tEnd += 7 * 60 * 24;
			}
			if (startday < 4) {
				sche.startDay = startday;
				for (Air_section ase : tod.secs) {
					if (ase.tDep < 7 * 24 * 60 && ase.tDep>=sche.tSta)
						sche.bcv.add(ase.index);
					else 
						sche.ccv.add(ase.index);
				}
				arrSch.add(sche);

				for (Tour_ofDuty newtod : arrToD) {
					if (newtod.settle == sche.city && newtod.tSta > sche.tEnd && sche.tEnd > sche.tSta) {
						Schedule newSche = new Schedule(sche);
						newSche.arrToD.add(newtod);
						newSche.tEnd = newtod.tEnd;
						if (newSche.tEnd < newtod.tSta)
							newSche.tEnd += 7 * 24 * 60;
						for (Air_section ase : newtod.secs) {
							if (ase.tDep >= newtod.tSta)
								newSche.bcv.add(ase.index);
							else {
								newSche.ccv.add(ase.index);
							}
						}
						arrSch.add(newSche);
					}
				}
			} else {
				sche.startDay = startday;
				Schedule sche1 = new Schedule(sche);
				for (Air_section ase : tod.secs) {
					if (ase.tDep < 7 * 24 * 60 && ase.tDep>4*24*60) {
						sche.bcv.add(ase.index);
					} else {
						sche.ccv.add(ase.index);
					}
				}
				arrSch.add(sche);

				sche1.startDay = startday - 7;
				sche1.tSta -= 7 * 60 * 24;
				sche1.tEnd -= 7 * 60 * 24;
				for (Air_section ase : tod.secs) {
					if (ase.tDep < 7 * 24 * 60 && ase.tDep>4*24*60) {
						sche1.acv.add(ase.index);
					} else {
						sche1.bcv.add(ase.index);
					}
				}
				arrSch.add(sche1);

				for (Tour_ofDuty newtod : arrToD) {
					if (newtod.settle == sche1.city && newtod.tSta > sche1.tEnd &&newtod.tSta -sche1.tEnd > 2*24*60 ) {
						Schedule newSche = new Schedule(sche1);
						newSche.arrToD.add(newtod);
						newSche.tEnd = newtod.tEnd;
						if (newSche.tEnd < newtod.tSta)
							newSche.tEnd += 7 * 24 * 60;
						for (Air_section ase : newtod.secs) {
							if (ase.tDep > newtod.tSta)
								newSche.bcv.add(ase.index);
							else {
								newSche.ccv.add(ase.index);
							}
						}
						arrSch.add(newSche);
					}
				}
			}
		}
	}

	public void testSchedule() throws FileNotFoundException, IOException, ParseException {
		// to test producing schedule
		loadRes();
		produce_schedule();
		int scheNum = 1000;
		for (int i = 0; i < scheNum; i += 10) {
			System.out.print(i + "  : ");
			for (Tour_ofDuty tod : arrSch.get(i).arrToD) {
				System.out.print(tod.ToDindex + " ");
			}
			System.out.println("  ");
		}
	}

	// public void loadToDs() throws FileNotFoundException, IOException,
	// ParseException {
	// int index = 0;
	// for (; index < toDNum; index++) {
	// String url = "./airline-new-data/usedToD/ToD" + index + ".txt";
	// loadToDInfo(url);
	// }
	// }
	//
	// static int index = 0;
	//
	// public void loadToDInfo(String url) throws FileNotFoundException,
	// IOException, ParseException {
	// BufferedReader br = new BufferedReader(new java.io.FileReader(url));
	// String line;
	// String[] splitLine;
	//
	// Tour_ofDuty tempToD = new Tour_ofDuty();
	//
	// tempToD.ToDindex = index++;
	// line = br.readLine();
	// int tempCity = 0, tempTE = -1, tempTS = -1, day = 0;
	// String cityName = null;
	// ArrayList<Air_section> tempDay = new ArrayList<>();
	// while ((line = br.readLine()) != null) {
	// splitLine = line.split("\\s+");
	// if (splitLine[0].compareTo("ToD") == 0) {
	// tempToD.days.add(tempDay);
	// tempDay = new ArrayList<>();
	// day++;
	// continue;
	// }
	// // STILL don't care if the order is wrong
	// tempDay.add(arrSec.get(Integer.valueOf(splitLine[1])));
	//
	// Date date1, date2;
	// int d1, d2;
	// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
	// date1 = simpleDateFormat.parse(splitLine[14]);
	// date2 = simpleDateFormat.parse(splitLine[16]);
	// tempToD.dateDep = date1;
	// tempToD.dateArr = date2;
	// d1 = date1.getHours() * 60 + date1.getMinutes();
	// d2 = date2.getHours() * 60 + date2.getMinutes();
	//
	// tempToD.day_ofWeek = Integer.valueOf(splitLine[11]) - 1;
	// if (day == 0) {
	// int tmp1 = d1 + tempToD.day_ofWeek * 24 * 60;
	// if (tempTS == -1 || tempTS > tmp1) {
	// cityName = splitLine[6];
	// tempCity = arrCity.indexOf(cityName);
	// tempTS = tmp1;
	// }
	// }
	// int tmp2 = d2 + tempToD.day_ofWeek * 24 * 60;
	// if (tmp2 < tempTS)
	// tmp2 += 7 * 24 * 60;
	// if (tempTE == -1 || tempTE < tmp2) {
	// tempTE = tmp2;
	// }
	// }
	// tempToD.tSta = tempTS;
	// tempToD.tEnd = tempTE;
	// tempToD.settle = tempCity;
	// tempToD.settleName = cityName;
	// arrToD.add(tempToD);
	// br.close();
	// }
	//

}