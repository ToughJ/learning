package airline_aug;

import java.io.*;
import java.text.DecimalFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class txt2excel {

	static String headings[] = { "值勤日", "航段编号", "飞机编号", "起飞地点", "降落地点", "起飞日", "起飞时间", "降落日", "降落时间", "周次" };
	static String weekdays[] = { "周一", "周二", "周三", "周四", "周五", "周六", "周日", "周一" };
	static int ToD_num = 0;
	static String[] namelist = new String[400];
	static int tournum =0;
	static int schNum = 198;

	private static void createHeaderRow(Sheet sheet, int rowCount, String[] splitted) {

		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 15);
		cellStyle.setFont(font);

		// first heading row
		Row row = sheet.createRow(rowCount);
		Cell cellTitle = row.createCell(0);

		cellTitle.setCellStyle(cellStyle);
		cellTitle.setCellValue("值勤期");

		Cell cellAuthor = row.createCell(1);
		cellAuthor.setCellStyle(cellStyle);
		cellAuthor.setCellValue(ToD_num);

		tournum = Integer.valueOf(splitted[2]);
		// second heading row

		CellStyle cellStyle1 = sheet.getWorkbook().createCellStyle();
		Font font1 = sheet.getWorkbook().createFont();
		font1.setBold(true);
		font1.setFontHeightInPoints((short) 11);
		cellStyle1.setFont(font1);
		row = sheet.createRow(rowCount + 1);

		for (int i = 0; i < headings.length; i++) {
			cellTitle = row.createCell(i);
			cellTitle.setCellStyle(cellStyle1);
			cellTitle.setCellValue(headings[i]);
		}
	}

	public static void main(String[] args) throws IOException {

		// for (String arg : args) {
		// if (arg.startsWith("-file=")) {
		// txtFile = arg.split("=")[1];
		// }
		// }
		String nameFile = "./airline-new-data/namelist.txt";
		BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(nameFile), "UTF-8"));
		String l = b.readLine();
		int in = 0;
		while ((l = b.readLine()) != null) {
			String[] sl = l.split("\\s+");
			namelist[in++] = sl[1];
		}
		b.close();

		for (int fileID = 0; fileID < schNum; fileID++) {
			String txtFile = "./airline-new-data/used_Schedule/NO." + fileID + ".txt";
			System.out.printf("reading from %s.\n", txtFile);
			String excelFile = "./airline-new-data/used_PILOT/PILOT" + String.format("%03d", fileID) + ".xlsx";
			System.out.printf("writing to %s.\n", excelFile);
			
			ToD_num = 0;

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("周计划");
			
			Row row = sheet.createRow(0);
			Cell cell = row.createCell(0);
			cell.setCellValue(namelist[fileID]);

			int width = 12; // Where width is number of characters
			sheet.setDefaultColumnWidth(width);

			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(txtFile), "UTF-8"));) {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				int rowCount = 1;

				String stCity = null;
				int fw = 0;
				int ld = -1;
				String ld_time = null;
				int day = 1;
				int we = 0;
				int start = -1;
				int end = -1;
				while (line != null) {

					sb.append(line);

					String[] splitted = line.split("\\s+");
					if (splitted[0].equals("tod_num")) { // heading
						createHeaderRow(sheet, rowCount, splitted);
						rowCount += 2;
						day = 0;
						if (ld ==-1)day++;
						start = -1;
						ToD_num++;
					} else if (splitted[0].equals("from")) {
						fw = -1;
					} else {
						row = sheet.createRow(rowCount++);

						System.out.println(splitted.length);
						for (String temp : splitted) {
							System.out.println(temp);
						}
						assert splitted.length == 14 || splitted.length == 3;
						if (splitted.length == 14) {
							we = Integer.valueOf(splitted[9]);
							int columnCount = 0;
							int last[] = { 1, 4, 5, 7, 9, 11, 13 };

							int ed = fw * 24 * 60 * 7 + (we - 1) * 24 * 60
									+ Integer.valueOf(splitted[13].split(":")[0]) * 60
									+ Integer.valueOf(splitted[13].split(":")[1]);
							int st = fw * 24 * 60 * 7 + (we - 1) * 24 * 60
									+ Integer.valueOf(splitted[11].split(":")[0]) * 60
									+ Integer.valueOf(splitted[11].split(":")[1]);

							if (ld != -1 && st < ld) {
								fw++;
								st += 24 * 60 * 7;
								ed += 24 * 60 * 7;
							}
							if (start == -1) {
								start = st;
								stCity = splitted[5];
							}
							end = ed;
							if (ld != -1 && st - ld >= 10 * 60) {
								day++;
							}

							cell = row.createCell(columnCount++);
							cell.setCellValue(day);
							for (int i = 0; i < splitted.length; i++) {

								if (i == 1 || i == 4 || i == 5 || i == 7 || i == 11) {
									cell = row.createCell(columnCount++);
									cell.setCellValue(splitted[i]);
								} else if (i == 13) {

									if (ed < st) {
										we++;
										ed += 24 * 60;
										if (we == 8) {
											if (fw == -1) {
												fw++;
												we = 1;
											}
										}
									}

									cell = row.createCell(columnCount++);
									cell.setCellValue(weekdays[we - 1]);
									cell = row.createCell(columnCount++);
									cell.setCellValue(splitted[i]);
								} else if (i == 9) {

									cell = row.createCell(columnCount++);
									cell.setCellValue(weekdays[we - 1]);
								}
							}
							ld = ed;
							ld_time = splitted[13];
							if (fw == -1) {
								cell = row.createCell(columnCount++);
								cell.setCellValue("上周");
							} else if (fw == 0) {
								cell = row.createCell(columnCount++);
								CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
								cellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());

								cellStyle.setFillPattern(FillPatternType.THICK_FORWARD_DIAG);
								cell.setCellStyle(cellStyle);
								cell.setCellValue("本周");

							} else if (fw == 1) {
								cell = row.createCell(columnCount++);
								cell.setCellValue("下周");
							}
							if (we == 8)
								fw++;

						} else if (splitted.length == 3) {
							cell = row.createCell(0);
							cell.setCellValue("休息");
							cell = row.createCell(1);
							cell.setCellValue(weekdays[we - 1]);
							cell = row.createCell(2);
							cell.setCellValue(ld_time);
							cell = row.createCell(3);
							cell.setCellValue("-->");
							int newwe = we + 2;
							if (newwe > 7) {
								newwe -= 7;
								cell = row.createCell(4);
								cell.setCellValue("下" + weekdays[newwe - 1]);
							} else {
								cell = row.createCell(4);
								cell.setCellValue(weekdays[newwe - 1]);
							}
							cell = row.createCell(5);
							cell.setCellValue(ld_time);

							row = sheet.createRow(rowCount++);
							cell = row.createCell(0);
							cell.setCellValue("总值勤期时间：");
							cell = row.createCell(1);
							cell.setCellValue((end - start) / 60 + "小时");
							Cell cellPrice = row.createCell(2);
							cellPrice.setCellValue("环代号");

							DecimalFormat df=new DecimalFormat("0000");
							String str2=df.format(tournum);
							Cell cellExtra = row.createCell(3);
							cellExtra.setCellValue(stCity + str2);

						}
					}
					sb.append(System.lineSeparator()); // add "\n" when
														// system.out is needed
					line = br.readLine();
				}
				String everything = sb.toString();
				System.out.println(everything);
			}

			try (FileOutputStream outputStream = new FileOutputStream(excelFile)) {
				workbook.write(outputStream);
			}
		}

	}
}