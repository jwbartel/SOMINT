package bus.images.printers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import bus.accounts.Account;

public class DefaultPrinter {
	
	static Pattern whitespace = Pattern.compile("\\s+");
	
	static Font font = new Font("monospaced", Font.PLAIN, Account.IMAGE_FONT_SIZE);
	static FontMetrics metrics;
	File file;
	
	public DefaultPrinter(String file){
		this.file = new File(file);
	}
	
	public DefaultPrinter(File file){
		this.file = file;
	}
	
	public void print(String loc) throws IOException{
		print(new File(loc));
	}
	
	public void print(File loc) throws IOException{
		int width = Account.IMAGE_WIDTH;
		int maxStringWidth = width - 2*Account.IMAGE_TEXT_V_OFFEST;
		
		BufferedImage image = new BufferedImage(width, Account.IMAGE_FONT_SIZE + Account.IMAGE_TEXT_H_OFFEST*2, BufferedImage.TYPE_INT_ARGB);//ImageIO.read(new File(Account.TEMPLATE_IMAGE_LOCATION));
		Graphics2D graphic = image.createGraphics();
		graphic.setFont(font);
		metrics = graphic.getFontMetrics();
		
		
		int hOffset = Account.IMAGE_TEXT_H_OFFEST;
		int vOffset = Account.IMAGE_TEXT_V_OFFEST + Account.IMAGE_FONT_SIZE;
		
		ArrayList<String> lineBuffer = new ArrayList<String>();
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = in.readLine();
		while(line != null){
			String lineReplacedTabs = line.replaceAll("\t", "        ");
			int lineWidth = metrics.stringWidth(lineReplacedTabs);
			
			if(lineWidth > maxStringWidth){
				Matcher matcher = whitespace.matcher(line);
				
				String lineStart = null;
				while(matcher.find()){
					String tempLineStart = line.substring(0, matcher.start());
					
					lineWidth = metrics.stringWidth(tempLineStart.replaceAll("\t", " "));
					if(lineWidth > maxStringWidth) break;
					lineStart = tempLineStart;
				}
				
				if(lineStart != null){
					line = line.substring(lineStart.length());
					if(line.startsWith(" ")) line = line.substring(1);
				}else{
					//TODO
				}
				lineBuffer.add(lineStart);
			}else{			
				lineBuffer.add(line);
			
				line = in.readLine();
			}
		}
		
		int height = 2*Account.IMAGE_FONT_SIZE + Account.IMAGE_TEXT_H_OFFEST*2;
		if(lineBuffer.size() > 0){
			height = 2*Account.IMAGE_TEXT_H_OFFEST + lineBuffer.size() * (Account.IMAGE_FONT_SIZE + Account.IMAGE_TEXT_H_OFFEST);
		}
		//TODO:empty line buffer and resize BufferedImage
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_555_RGB);//ImageIO.read(new File(Account.TEMPLATE_IMAGE_LOCATION));
		graphic = image.createGraphics();
		
		graphic.setColor(Color.WHITE);
		graphic.fillRect(0, 0, width, height);
		
		graphic.setColor(Color.BLACK);
		graphic.setFont(font);
		metrics = graphic.getFontMetrics();
		
		for(int i=0; i<lineBuffer.size(); i++){
			graphic.drawString(lineBuffer.get(i).replaceAll("\t", "        "), hOffset, vOffset);
			vOffset += Account.IMAGE_FONT_SIZE + Account.IMAGE_TEXT_V_OFFEST;
		}
		
		ImageIO.write(image, "png", loc);
		
		
	}
	
	public static void main(String[] args) throws IOException{
		DefaultPrinter imagePrinter = new DefaultPrinter("/home/bartizzi/Desktop/ex.txt");
		imagePrinter.print("/home/bartizzi/Desktop/temp.png");
	}
	
}
