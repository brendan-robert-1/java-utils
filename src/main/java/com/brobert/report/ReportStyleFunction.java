/**
 *
 */
package com.brobert.report;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author brobert
 *
 */
public @FunctionalInterface interface ReportStyleFunction {

	XSSFWorkbook style(XSSFWorkbook workbook);
}
