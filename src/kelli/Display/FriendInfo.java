package kelli.Display;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class FriendInfo extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel nameLabel = null;
	private JPanel friendPic = null;  //TODO: make it a picture instead
	private int uid;
//eventually also have a picture...
	/**
	 * This is the default constructor
	 */
	public FriendInfo(String name, int uid) {
		super();
		initialize(name, uid);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize(String name, int id) {
		friendPic = new JPanel();
		friendPic.setBorder(new LineBorder(Color.cyan));
		nameLabel = new JLabel();
		nameLabel.setText(name);
		uid = id;
		nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.setSize(50, 70);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(friendPic, null);
		this.add(nameLabel, null);
	}

}  //  @jve:decl-index=0:visual-constraint="49,32"
