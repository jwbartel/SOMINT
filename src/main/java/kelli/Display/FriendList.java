package kelli.Display;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class FriendList extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel listName = new JLabel("friendList");
	private JCheckBox joinBox = new JCheckBox();
	private JCheckBox splitBox = new JCheckBox();

	/**
	 * This is the default constructor
	 */
	public FriendList(String listName) {
		super();
		initialize(listName);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize(String name) {
		this.setSize(500, 200);
		//try flow layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new EmptyBorder(10, 5, 10, 5));
		listName.setText(name);
		this.add(joinBox);
		this.add(listName);
		FriendInfo mickey = new FriendInfo("mickey", 1);
		this.add(mickey);
		FriendInfo donald = new FriendInfo("donald",2);
		this.add(donald);
		this.add(splitBox);
	}

}
