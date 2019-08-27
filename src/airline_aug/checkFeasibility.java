package airline_aug;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class checkFeasibility {

	private static int transInterval = 15 * 6; // changing plane 1.5
	private static int dayMin = 60 * 24;
	private static int weekMin = 7 * dayMin;
	private static int flyMin = 9 * 60;
	private static int restInterval = 10 * 60 + transInterval; // rest time
																// 10+1.5
	private static int dutyMin = 14 * 60 - transInterval;

	public static void main(String[] args) throws IOException, ParseException {
		int startIndex = 0, endIndex = 789, newStartIndex = 790, newEndIndex = 792;
		for (int i = startIndex; i <= endIndex; i++) {
			if (checkToD(false, i) && i % 10 == 0) {
				System.out.println(i + " is done");
			}
		}
		for (int i = newStartIndex; i <= newEndIndex; i++) {
			if (checkToD(true, i) && i % 10 == 0) {
				System.out.println(i + " is done");
			}
		}
	}

	@SuppressWarnings("resource")
	public static boolean checkToD(boolean isNew, int num) throws IOException, ParseException {
		String url;
		if (!isNew) {
			url = "./airline-new-data/ToDLists/ToD" + num + ".txt";
		} else {
			url = "./airline-new-data/ToDLists/NewToD" + num + ".txt";
		}
		BufferedReader br = new BufferedReader(new java.io.FileReader(url));
		String line = br.readLine(); // first line is ToD0. neglect it
		int nowDutyTime = 0, nowAllDutyTime = 0, nowFlyTime = 0, nowArriveTime = 0;
		int newTod = 0;
		while ((line = br.readLine()) != null) {
			String[] splitLine = line.split("\\s+");
			if (splitLine[0].compareTo("ToD") == 0) {
				newTod = 1;
				nowDutyTime = 0;
				nowFlyTime = 0;
				continue;
			}

			int tmpDepTime, tmpArrTime;
			int tmpDay = Integer.valueOf(splitLine[5]);
			Date date1, date2;
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
			date1 = simpleDateFormat.parse(splitLine[8]);
			date2 = simpleDateFormat.parse(splitLine[10]);
			tmpDepTime = (tmpDay - 1) * 24 * 60 + date1.getHours() * 60 + date1.getMinutes();
			tmpArrTime = (tmpDay - 1) * 24 * 60 + date2.getHours() * 60 + date2.getMinutes();
			if (tmpArrTime < tmpDepTime)
				tmpArrTime += 24 * 60;

			if (newTod == 0) {
				nowArriveTime = tmpDepTime;
			}
			if (newTod == 1) {
				int tmpRestTime = tmpDepTime - nowArriveTime;
				if (tmpRestTime < 0)
					tmpRestTime += weekMin;
				nowArriveTime = tmpDepTime;
				if (tmpRestTime < restInterval) {
					System.err.println("rest interval is not enough!! Index:" + num);
					return false;
				}
			}
			newTod = 2;
			int tmp1 = tmpArrTime - nowArriveTime;
			int tmp2 = tmpArrTime - tmpDepTime;
			nowArriveTime = tmpArrTime;
			if (tmp1 < 0)
				tmp1 += weekMin;
			if (tmp2 < 0)
				tmp2 += weekMin;
			nowDutyTime += tmp1;
			nowAllDutyTime += tmp1;
			nowFlyTime += tmp2;
			if (nowDutyTime > dutyMin) {
				System.err.println("period of duty is over time!! Index:" + num);
				return false;
			}
			if (nowAllDutyTime > 4 * dayMin) {
				System.err.println("period of all duties is over time!! Index: " + num);
				return false;
			}
			if (nowFlyTime > flyMin) {
				System.err.println("period of flying time is over time!! Index: " + num);
				return false;
			}
		}
		return true;
	}
}
