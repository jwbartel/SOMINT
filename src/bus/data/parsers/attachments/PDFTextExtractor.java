package bus.data.parsers.attachments;

import java.io.File;
import java.io.FileInputStream;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFTextExtractor {

	public static String extractFromPDF(File file){
		PDFParser parser = null;
		String parsedText = null;
		PDFTextStripper pdfStripper = null;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;
		
		try{
			parser = new PDFParser(new FileInputStream(file));
		} catch (Exception e) {
			System.out.println("Unable to open PDF Parser.");
			return null;
		}
		
		try{
			parser.parse();
			cosDoc = parser.getDocument();
			pdfStripper = new PDFTextStripper();
			pdDoc = new PDDocument(cosDoc);
			parsedText = pdfStripper.getText(pdDoc);
			pdDoc.close();
		}catch(NullPointerException e){
			e.printStackTrace();
			System.exit(0);
		}catch(Exception e){
			System.out.println("An exception occurred in parsing the PDF Document: "+file.getAbsolutePath());
			//e.printStackTrace();
			try{
					if (cosDoc != null) cosDoc.close();
					if (pdDoc != null) pdDoc.close();
				}catch(Exception e1){
				e.printStackTrace();
				
			}
			return null;
		}
		return parsedText;
	}
}
