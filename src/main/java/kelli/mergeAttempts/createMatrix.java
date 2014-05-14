package kelli.mergeAttempts;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

@SuppressWarnings("unused")
public class createMatrix {
	private static HashMap<Integer, Integer> renumberMap = new HashMap<Integer, Integer>();
	private static int[][] matrix;
	/**
	 * make the adjacency matrix
	 * @param args
	 */
	public static void main(String[] args) {
		//		go through Friends_of_Friends, map IDs, put 1 in corresponding row/column
		
		setRenumberMap();
		setMatrix();
		writeMatrix();
		// OR make php do it, print the matrix to the screen, and copy the matrix to a text file. At least print
		// the table to the screen.  start there, i guess

	}
	/**
	 * setRenumberMap maps the values of the facebook unique identifiers to numbers 0 -> numberOfFriends (map.size) 
	 */
	@SuppressWarnings("deprecation")
	public static void setRenumberMap(){
	   int count = 0;
	   int id = 0;
	   try {
		  FileInputStream in = new FileInputStream("mapIDs.txt");
		  BufferedInputStream bis = null;
		  DataInputStream dis = null;
		  
		  // Here BufferedInputStream is added for fast reading.
		  bis = new BufferedInputStream(in);
		  dis = new DataInputStream(in);

		  // dis.available() returns 0 if the file does not have more lines.
		  while (dis.available() != 0) {
			 id = Integer.parseInt(dis.readLine());
			 renumberMap.put(id, count);
			 count++;
		  }
		  System.out.println("there are "+renumberMap.size()+" values in renumberMap");
		  // dispose all the resources after using them.
		  in.close();
		  bis.close();
		  dis.close();
	   } catch (Exception e){
		   System.out.println("Problem: "+e.getMessage());
	   }
		
	}
	@SuppressWarnings("deprecation")
	private static void setMatrix(){
	   int friendCount = renumberMap.size();
	   matrix = new int[friendCount][friendCount];
	   for (int row = 0; row < friendCount; row++){
		   for (int col = 0; col < friendCount; col++)
			   matrix[row][col] = 0;
	   }
	   int linesReadCount = 0;
	   try {
		  FileInputStream in = new FileInputStream("FriendOfFriends.txt");
		  DataInputStream dis = null;
		  
		  dis = new DataInputStream(in);
		  String FriendPair = null;
		  int friend1 = -1;
		  int friend2 = -1;
		  boolean skip = false;
		  int parsingSpace = -1;
		  // dis.available() returns 0 if the file does not have more lines.
		  while (dis.available() != 0) {
			 FriendPair = dis.readLine();
			 linesReadCount++;
			 parsingSpace = FriendPair.indexOf(' ');
			 friend1 = Integer.parseInt(FriendPair.substring(0, parsingSpace));
			 friend2 = Integer.parseInt(FriendPair.substring(parsingSpace+1));
			 if(renumberMap.containsKey(friend1))
				friend1 = renumberMap.get(friend1);
			 else skip = true;
			 if (!skip && renumberMap.containsKey(friend2))
				friend2 = renumberMap.get(friend2);
			 else skip = true;
			 if (!skip){
				matrix[friend1][friend2] = 1;
				matrix[friend2][friend1] = 1;
			 }
			 skip = false;
		  }

		  // dispose all the resources after using them.
		  in.close();
		  dis.close();
	   } catch (Exception e){
		   System.out.println("Problem in SetMatrix: "+e.getMessage());
		   System.out.println("lines read: "+linesReadCount);
		   System.exit(0);
	   }
	   System.out.println("exiting SetMatrix method");
	}
	private static void writeMatrix(){
	   System.out.println("entering writeMatrix method");
	   try {
		  FileOutputStream fos = new FileOutputStream("adjacencyMatrix.txt");
		  //OutputStreamWriter out = new OutputStreamWriter(fos);
	      //DataOutputStream out = new DataOutputStream(new
	       //       BufferedOutputStream(fos));
	      PrintWriter pw = new PrintWriter(new FileWriter("adjacencyMatrix.txt"));
		  int friendCount = renumberMap.size();
		  pw.println(friendCount);
		  //out.writeInt(friendCount);
		  for(int row = 0; row < friendCount; row++){
			 for (int col = 0; col < friendCount; col++){
				pw.print(matrix[row][col]);
				pw.print(' ');
				//out.writeInt(matrix[row][col]);
				//out.writeChar(' ');
			 }
			 pw.println();
		  }
		  System.out.println("matrix[1][1] = "+matrix[0][3]);
		  // dispose all the resources after using them.
//		  fos.close();
		  //out.close();
		  pw.close();
	   } catch (Exception e){
		   System.out.println("Problem in WriteMatrix: "+e.getMessage());
		   System.exit(0);
	   }
	   System.out.println("exiting writeMatrix method");
	}

}
