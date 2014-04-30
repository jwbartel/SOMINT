package kelli.Display;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class Friends extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}

	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Friends");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        frame.add(new Friends());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
	
	/**
	 * This is the default constructor
	 */
	public Friends() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(550, 400);
		importDataFromFiles();
		FriendListTab flTab = new FriendListTab();
		this.add(flTab);

	}
	private List<FriendList> FriendLists;  //  @jve:decl-index=0:
	private 	void importDataFromFiles(){
		//TODO build hashtable of uid/name
		//TODO put this in a different file...link this info to display not load from display?
	   String listData = "sampleFriendLists.txt";
	   int linesReadCount = 0;
	   try {
		  DataInputStream in = new DataInputStream(new FileInputStream(listData));
		  String inputLine = null;
		  FriendLists = new ArrayList<FriendList>();
		  int uid;
		  // in.available() returns 0 if the file does not have more lines.
		  while (in.available() != 0) {
			 inputLine = in.readLine();
			 linesReadCount++;
			 FriendList currList = null;
			 while(!inputLine.contains("*")){
				 if(!inputLine.contains("Clique:")){
					uid = Integer.parseInt(inputLine);
					currList.add(new FriendInfo("", uid));
					if(in.available() != 0) inputLine = in.readLine();
				 } else {
					 String listName = inputLine.substring(8); //name after the ':'
					 currList = new FriendList(listName);
					 if (in.available() != 0) inputLine = in.readLine();
				 }
			 }
			 FriendLists.add(currList);
		  }
		  // dispose all the resources after using them.
		  in.close();
	   } catch (Exception e){
		   System.out.println("!!! CreateUIDGraph, line:"+linesReadCount+": "+e.getMessage());
	   }
	}

}
