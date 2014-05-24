package data.preprocess.oldprecomputeExtractor.newsgroups;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.mail.MessagingException;

import data.parsers.AddressParser;
import data.preprocess.old.precomputeExtractor.PrecomputesFileBuilder;

public class NewsgroupPrecomputesFileBuilder extends PrecomputesFileBuilder {

	
	public void writeEmailAddresses(File dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		try {
			AddressParser parser = new AddressParser();
			String[] headers = message.getHeader("from");
			if(headers != null){
				parser.add(headers[0]);
			}
			out.write("FROM:");
			out.newLine();
			for(String address: removeDuplicates(parser.getAddressesInArrayList())){
				out.write("\t"+address);
				out.newLine();
			}
			out.newLine();
		} catch (MessagingException e) {}
		
		try {
			AddressParser parser = new AddressParser();
			String[] headers = message.getHeader("newsgroups");
			if(headers != null){
				parser.add(headers[0]);
			}
			out.write("Newsgroups:");
			out.newLine();
			for(String address: removeDuplicates(parser.getAddressesInArrayList())){
				out.write("\t"+address);
				out.newLine();
			}
			out.newLine();
		} catch (MessagingException e) {}
		
		try {
			AddressParser parser = new AddressParser();
			String[] headers = message.getHeader("to");
			if(headers != null){
				parser.add(headers[0]);
			}
			out.write("TO:");
			out.newLine();
			for(String address: removeDuplicates(parser.getAddressesInArrayList())){
				out.write("\t"+address);
				out.newLine();
			}
			out.newLine();
		} catch (MessagingException e) {}
		
		try {
			AddressParser parser = new AddressParser();
			String[] headers = message.getHeader("cc");
			if(headers != null){
				parser.add(headers[0]);
			}
			out.write("CC:");
			out.newLine();
			for(String address: removeDuplicates(parser.getAddressesInArrayList())){
				out.write("\t"+address);
				out.newLine();
			}
			out.newLine();
		} catch (MessagingException e) {}
		
		try {
			AddressParser parser = new AddressParser();
			String[] headers = message.getHeader("bcc");
			if(headers != null){
				parser.add(headers[0]);
			}
			out.write("BCC:");
			out.newLine();
			for(String address: removeDuplicates(parser.getAddressesInArrayList())){
				out.write("\t"+address);
				out.newLine();
			}
			out.newLine();
		} catch (MessagingException e) {}
		
		out.flush();
		out.close();
	}
}
