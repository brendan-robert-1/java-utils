/**
 *
 */
package com.brobert.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author brobert
 *
 */
public class Report<T> {

	private Collection<T> records;

	private String reportName = "Report";

	private ReportStyleFunction defaultStyleFx = new DefaultStyleFunction();



	public Report(Collection<T> records) {
		this.records = records;
	}



	public Report(Collection<T> records, String reportName) {
		//validate length is not 0
		this.records = records;
		this.reportName = reportName;
	}



	public XSSFWorkbook getXSSFReport(ReportStyleFunction fx) {
		return fx.style(getXSSFReport());
	}



	public XSSFWorkbook getXSSFReport() {
		XSSFWorkbook report = new XSSFWorkbook();
		XSSFSheet sheet = report.createSheet(reportName);
		if (records != null && records.isEmpty() == false) {
			T first = records.iterator().next();
			List<String> headers = this.getOrderdFieldNames(first);
			applyHeaders(sheet, headers);
			int rowCount = 1;

			for (T t : records) {
				List<String> fields = getValuesAsString(t);
				Row row = sheet.createRow(rowCount++);
				addCells(row, fields);
			}
			defaultStyleFx.style(report);
		}
		return report;
	}




	private void applyHeaders(XSSFSheet sheet, List<String> headers) {
		Row row = sheet.createRow(0);
		int count = 0;
		for (String header : headers) {
			Cell cell = row.createCell(count++);
			cell.setCellValue(header);
		}
	}



	private void addCells(Row row, List<String> values) {
		int column = 0;
		for (String s : values) {
			Cell cell = row.createCell(column++);
			cell.setCellValue(s);
		}
	}



	private LinkedList<String> getValuesAsString(T t) {
		LinkedList<String> recordValues = new LinkedList<>();
		for (Field field : t.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(RecordElement.class)) {
				RecordElement el = field.getAnnotation(RecordElement.class);
				if (!el.include()) {
					continue;
				}
			}
			try {
				String value = "";
				Object obj = PropertyUtils.getProperty(t, field.getName());
				if (obj != null) {
					value = obj.toString();
				}
				recordValues.add(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return recordValues;
	}




	private List<String> getOrderdFieldNames(T t) {
		List<String> names = new ArrayList<>();
		for (Field field : t.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			String fieldName = "Unknown";
			if (field.isAnnotationPresent(RecordElement.class)) {
				RecordElement el = field.getAnnotation(RecordElement.class);
				if (!el.include()) {
					continue;
				}
				fieldName = el.name();
			} else {
				LinkedList<String> camelCaseSplit = splitCamelCaseString(field.getName());
				fieldName = listToDelimitedString(camelCaseSplit, " ");
			}
			names.add(fieldName);
		}
		return names;
	}



	private static LinkedList<String> splitCamelCaseString(String s) {
		LinkedList<String> result = new LinkedList<String>();
		for (String w : s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
			String str = upperFirstChar(w);
			result.add(str);
		}
		return result;
	}



	private static String listToDelimitedString(List<String> strings, String delimiter) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (String s : strings) {
			sb.append(s);
			if (++count != strings.size()) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}



	private static String upperFirstChar(String s) {
		String lower = s.toLowerCase();
		return lower.substring(0, 1).toUpperCase() + lower.substring(1);
	}



	public void writeReportToFile(XSSFWorkbook workbook, File file) {
		writeReportToFile(workbook, new DefaultStyleFunction(), file);
	}



	public void writeReportToFile(XSSFWorkbook workbook, ReportStyleFunction style, File file) {
		try {
			OutputStream os = new FileOutputStream(file);
			workbook.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void writeReportToFile(ReportStyleFunction style, File file) {
		XSSFWorkbook workbook = getXSSFReport();
		try {
			OutputStream os = new FileOutputStream(file);
			workbook.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void writeReportToFile(File file) {
		writeReportToFile(new DefaultStyleFunction(), file);
	}
}
