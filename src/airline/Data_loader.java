package airline;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class Data_loader {
	public ArrayList<Air_section> arrSec=new ArrayList<>();
	public ArrayList<String> arrCity = new ArrayList<>();

    public Data_loader() {
    }
    
    public void loadNodeInfo(String url) throws FileNotFoundException, IOException, ParseException{
    	url = "C:/learning part/operationResearchh/code/learning/airline-data/" + url;
        BufferedReader br = new BufferedReader(new java.io.FileReader(url));
        String line;
        String[] splitLine;
        line = br.readLine();
        ArrayList<Air_section> tempArrSec=new ArrayList<>();
        while ((line = br.readLine()) != null) {
            splitLine=line.split("\\s+");
            Air_section tempSec = new Air_section();
            tempSec.setIndex(Integer.valueOf(splitLine[0]));
            tempSec.setDay(Integer.valueOf(splitLine[1]));
            String de = splitLine[2],ar = splitLine[3];
            int deInt,arInt;
            if (!arrCity.contains(de))
            	arrCity.add(de);
            deInt = arrCity.indexOf(de);
            if (!arrCity.contains(ar))
            	arrCity.add(ar);
            arInt = arrCity.indexOf(ar);
            tempSec.setDeparture(deInt);
            tempSec.setArrival(arInt);
            Date date1,date2;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            date1 = simpleDateFormat.parse(splitLine[4]);
            date2 = simpleDateFormat.parse(splitLine[5]);
            int min1,min2;
            min1 = new Long(date1.getTime()).intValue()/(1000*60)+(8*60);
            min2 = new Long(date2.getTime()).intValue()/(1000*60)+(8*60);
            if (min1 <= 3*60) min1 += 24*60;  // 凌晨三点以前算作前一天的
            if (min2 <= 3*60) min2 += 24*60;  // 凌晨三点以前算作前一天的
            tempSec.setDepartureTime(min1);
            tempSec.setArrivalTime(min2);
            tempArrSec.add(tempSec);
        }
        arrSec.clear();
        arrSec.addAll(tempArrSec);
        br.close();
    }

    public void testPrint(){
    	System.out.println("check CitySet");
    	for (String city : arrCity) 
    		System.out.println(arrCity.indexOf(city) + " : " + city);
    	System.out.println("----"+"check arrSec ------");
    	for (Air_section sec : arrSec) {
    		System.out.println(sec.index + " : " + sec.day + "  " + arrCity.get(sec.departure) + " --> "+
    				arrCity.get(sec.arrival) + "  "  + sec.tDep + "  " +  sec.tArr);
    	}
    	
    }

    // 测试 date 中文字符的用法
//    public static void main(String[] args) throws ParseException{
//    	String str = "测试字符转换 a beautiful girl"; //默认环境，已是UTF-8编码  
//        try {  
//            String strGBK = URLEncoder.encode(str, "GBK");  
//            System.out.println(strGBK);  
//            String strUTF8 = URLDecoder.decode(str, "UTF-8");  
//            System.out.println(strUTF8);  
//        } catch (UnsupportedEncodingException e) {  
//            e.printStackTrace();  
//        }  
//    }
//    	String tmp = "15:01",t2= "15:00";
//    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
//    	Date date = simpleDateFormat.parse(tmp);
//    	long ts = date.getTime();
//    	String res = String.valueOf(ts);
//    	date = simpleDateFormat.parse(t2);
//    	long tss = date.getTime();
//    	System.out.println(tss-ts);
//    	System.out.println(res);
//    	long lt = new Long(res);
//        Date date1 = new Date(0);
//        res = simpleDateFormat.format(date1);
//        System.out.println(res);
//        System.out.println("---");
//        Date d1 = simpleDateFormat.parse(tmp);
//        Date d2 = simpleDateFormat.parse(t2);
//        System.out.println(d1);
//        System.out.println(d2);
//        long diff = d1.getTime() - d2.getTime();
//        long time = diff/(60*1000);
//        int ti = new Long(time).intValue();
//        System.out.println(ti);
//        System.out.println(diff);
//        Date date2 = new Date(time);
//        System.out.println(ts/1000/60/60);
//    }

}