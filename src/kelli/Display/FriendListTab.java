package kelli.Display;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class FriendListTab extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton joinButton = new JButton("join");
	private JButton splitButton = new JButton("split");
	/**
	 * This is the default constructor
	 */
	public FriendListTab() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		JPanel blankPanel = new JPanel();
		this.setSize(550, 200);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		joinButton.setText("join");
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.weightx = 0.5;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		this.add(joinButton,c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridy = 0;
		this.add(blankPanel,c);
		splitButton.setText("split");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.weightx = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		this.add(splitButton,c);
		FriendList fl = new FriendList("default");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		this.add(fl,c);
	}
}
