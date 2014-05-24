package data.parsers.attachments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hslf.exceptions.CorruptPowerPointFileException;
import org.apache.poi.hslf.exceptions.OldPowerPointFormatException;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xslf.XSLFSlideShow;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;

public class MSOfficeTextExtractor{
	
	
	public static String extractFromWord(File file) throws IOException{
		try{
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
			HWPFDocument doc = new HWPFDocument(fs);
			WordExtractor extractor = new WordExtractor(doc);
			return extractor.getText();
		}catch(IllegalStateException e){
			return null;
		}catch(IOException e){
			String message = e.getMessage();
			if(message.indexOf("Invalid header signature;")==0 || message.indexOf("Cannot remove block")==0 || message.indexOf("Unable to read entire header;")==0){
				return null;
			}
			throw e;
		}catch(NullPointerException e){
			return null;
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public static String extractFromWord2007(File file) throws FileNotFoundException, IOException{
		XWPFDocument doc = new XWPFDocument(new FileInputStream(file));
		XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
		return extractor.getText();
	}
	
	public static String extractFromPowerPoint(File file) throws IOException{
		try{
			PowerPointExtractor extractor = new PowerPointExtractor(new FileInputStream(file));
			return extractor.getText();
		}catch(OldPowerPointFormatException e){
			return null;
		}catch(CorruptPowerPointFileException e){
			return null;
		}catch(FileNotFoundException e){
			return null;
		}catch(IOException e){
			String message = e.getMessage();
			if(message.indexOf("Invalid header signature;")==0 || message.indexOf("Cannot remove block")==0 || message.indexOf("Unable to read entire header;")==0){
				return null;
			}
			throw e;
		}
	}
	
	public static String extractFromPowerpoint2007(File file) throws XmlException, IOException, OpenXML4JException{
		XSLFSlideShow slideshow = new XSLFSlideShow(file.getAbsolutePath());
		XSLFPowerPointExtractor extractor = new XSLFPowerPointExtractor(slideshow);
		return extractor.getText();
	}
	
	public static String extractFromExcel(File file) throws IOException{
		try{
			StringBuffer returnBuffer = new StringBuffer();
			FileInputStream in = new FileInputStream(file);
			POIFSFileSystem fs = new POIFSFileSystem();
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			for(int i=0; i<wb.getNumberOfSheets(); i++){
				
				HSSFSheet sheet = wb.getSheetAt(i);
				
				
				Iterator<Row> rows = sheet.rowIterator();
				while(rows.hasNext()){
					Row row = rows.next();
					
					Iterator<Cell> cells = row.cellIterator();
					while(cells.hasNext()){
						Cell cell = cells.next();
						
						//int type = cell.getCellType();
						String value = "";
					
						try{
							value = cell.getStringCellValue();
						}catch(IllegalStateException e){
							try{
								value = ""+cell.getNumericCellValue();
							}catch(IllegalStateException e1){
								try{
									FormulaError error = FormulaError.forInt(cell.getErrorCellValue());
									if(error == FormulaError.DIV0){
										value = "#DIV/0!";
									}else if(error == FormulaError.NA){
										value = "#NA!";
									}else if(error == FormulaError.NAME){
										value = "#NAME!";
									}else if(error == FormulaError.NULL){
										value = "#NULL!";
									}else if(error == FormulaError.NUM){
										value = "#NUM!";
									}else if(error == FormulaError.REF){
										value = "#REF!";
									}else if(error == FormulaError.VALUE){
										value = "#VALUE!";
									}else{
										value = "";
									}
								}catch(IllegalArgumentException e2){
									value = "";
								}catch(IllegalStateException e2){
									value = ""+cell.getBooleanCellValue();
								}
							}
						}
					
						returnBuffer.append(value);
					
						if(cells.hasNext()){
							returnBuffer.append(" ");
						}
					}
					returnBuffer.append("\n");
				}
		
			}
			in.close();
			return returnBuffer.toString();
		}catch(IllegalArgumentException e){
			return null;
		}catch(IOException e){
			System.out.println(file.getAbsolutePath());
			throw e;
		}catch(RuntimeException e){
			System.out.println(file.getAbsolutePath());
			throw e;			
		}
	}

	public static String extractFromExcel2007(File file) throws IOException{
		StringBuffer returnBuffer = new StringBuffer();
		
		XSSFWorkbook wb = new XSSFWorkbook(file.getAbsolutePath());
		for(int i=0; i<wb.getNumberOfSheets(); i++){
			XSSFSheet sheet = wb.getSheetAt(i);
			
			Iterator<Row> rows = sheet.rowIterator();
			while(rows.hasNext()){
				Row row = rows.next();
				
				Iterator<Cell> cells = row.cellIterator();
				while(cells.hasNext()){
					XSSFCell cell = (XSSFCell) cells.next();
					//System.out.println(""+XSSFCell.CELL_TYPE_BLANK+","+XSSFCell.CELL_TYPE_BOOLEAN+","+XSSFCell.CELL_TYPE_ERROR+","+XSSFCell.CELL_TYPE_FORMULA+","+XSSFCell.CELL_TYPE_NUMERIC+","+XSSFCell.CELL_TYPE_STRING);
					//System.out.println(cell.getCellType());
					if(cell.getCellType() == XSSFCell.CELL_TYPE_ERROR){
						returnBuffer.append(cell.getErrorCellString());
					}else try{
						returnBuffer.append(cell.getStringCellValue());
					}catch (IllegalStateException e){
						try{
							returnBuffer.append(""+cell.getNumericCellValue());
						}catch(NumberFormatException nfe){
							returnBuffer.append(cell.getErrorCellString());
						}
					}
					if(cells.hasNext()){
						returnBuffer.append(" ");
					}
				}
				returnBuffer.append("\n");
			}
		}
		
		return returnBuffer.toString();
	}
}