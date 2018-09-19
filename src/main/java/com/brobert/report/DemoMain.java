/**
 *
 */
package com.brobert.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author brobert
 *
 */
public class DemoMain {
	public static void main(String[] args) {
		DemoRecord rec = new DemoRecord("Brendan", 24, 1241424, "Dummy bro");
		DemoRecord rec2 = new DemoRecord("Susie", 35, 4245353, "filled with data");
		DemoRecord rec3 = new DemoRecord("John", 35, 4245353, null);
		List<DemoRecord> recs = new ArrayList<>();
		recs.add(rec);
		recs.add(rec2);
		recs.add(rec3);

		Report<DemoRecord> report = new Report<>(recs, "Testing Record");
		report.writeReportToFile(new File("workbook.xlsx"));
	}
}
