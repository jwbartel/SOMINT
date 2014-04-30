package dropbox.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;

import bus.tools.Logger;
import dropbox.converter.EmailToDropboxTranslator;
import dropbox.converter.ImapException;

public class DefaultConversionView implements ConversionView{
	
	static final int TEXT_FIELD_WIDTH = 200;
	static final int TEXT_FIELD_HEIGHT = 30;
	static final int LOG_WIDTH = 270;
	
	EmailToDropboxTranslator translator = new EmailToDropboxTranslator();
	
	JFrame frame;

	
	JLabel imapTitle;
	JLabel hostLabel;
	JLabel addressLabel;
	JLabel passwordLabel;
	
	JTextField host;
	JTextField address;
	JPasswordField password;
	JButton login;
	
	JLabel logLabel;
	JTextArea log;
	JScrollPane logPane;
	

	JLabel folderTitle;
	JScrollPane folderPane;
	JButton translate;
	
	FolderSelector folderSelector;
	
	public DefaultConversionView(){
		frame = createInitialFrame();
	}
	
	
	public JFrame createInitialFrame(){
				
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		frame = new JFrame("Email to Dropbox Translator");
		
		

		initializeWidgets();
		layoutWidgets();
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(600,350);
		
		address.requestFocus();
		
		return frame;
	}
	
	private void layoutWidgets(){
		
		JPanel container= new JPanel();
		frame.add(container);

		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(imapTitle)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(hostLabel)
							.addComponent(addressLabel)
							.addComponent(passwordLabel)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(host, TEXT_FIELD_WIDTH, TEXT_FIELD_WIDTH, TEXT_FIELD_WIDTH)
							.addComponent(address, TEXT_FIELD_WIDTH, TEXT_FIELD_WIDTH, TEXT_FIELD_WIDTH)
							.addComponent(password, TEXT_FIELD_WIDTH, TEXT_FIELD_WIDTH, TEXT_FIELD_WIDTH)
							.addComponent(login)
						)
					)
					.addComponent(logLabel)
					.addComponent(logPane, LOG_WIDTH, LOG_WIDTH, LOG_WIDTH)
				)
				.addGap(15)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(folderTitle)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(folderPane)
						.addComponent(translate)
					)
				)
			);
		layout.setVerticalGroup(layout.createSequentialGroup()

				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(imapTitle)
					.addComponent(folderTitle)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(hostLabel, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT)
							.addComponent(host, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(addressLabel, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT)
							.addComponent(address, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(passwordLabel, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT)
								.addComponent(password, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT, TEXT_FIELD_HEIGHT)
						)
						.addComponent(login)
						.addComponent(logLabel)
						.addComponent(logPane)
					)
					.addGroup(layout.createSequentialGroup()
						.addComponent(folderPane)
						.addComponent(translate)
					)
				)
			);
	}
	
	private void initializeWidgets(){
		
		initializeCredentialsWidgets();
		initializeLogWidgets();
		initializeFolderWidgets();
	}
	
	private void initializeCredentialsWidgets(){		
		imapTitle = new JLabel("IMAP Credentials");
		hostLabel = new JLabel("IMAP Host");
		addressLabel = new JLabel("Email Address");
		passwordLabel = new JLabel("Password");
		
		host = new JTextField();
		address = new JTextField();
		password = new JPasswordField();
		login = new JButton("Connect");
		
		host.setText("imap.gmail.com");
		host.addActionListener(new LoginListener(this, host, address, password));
		address.addActionListener(new LoginListener(this, host, address, password));
		password.addActionListener(new LoginListener(this, host, address, password));
		login.addActionListener(new LoginListener(this, host, address, password));
	}
	
	private void initializeLogWidgets(){
		
		logLabel = new JLabel("Log");
		log = new JTextArea();
		logPane = new JScrollPane(log);
		
		log.setBackground(frame.getBackground());
		Font logFont = log.getFont();
		log.setFont(new Font(logFont.getFamily(), logFont.getStyle(), 12));
		log.setDisabledTextColor(Color.BLACK);
		log.setEnabled(false);
		Logger.setWriter(new SwingLogger(log));
		
	}
	
	private void initializeFolderWidgets(){

		folderTitle = new JLabel("Select email folder(s) to translate");
		folderPane = new JScrollPane();
		translate = new JButton("Translate");
		
		folderPane.setViewportView(new JLabel("Log in to load folders"));
		translate.setEnabled(false);
		translate.addActionListener(new TranslateListener(this, translator));
	}
	
	public void addFolders(DefaultMutableTreeNode parent, Folder[] folders) throws MessagingException{
		
		for(int i=0; i<folders.length; i++){
			
			Folder folder = folders[i];
			Folder[] children = folder.list();
			DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(folder.getFullName(), true);
			parent.add(folderNode);
			addFolders(folderNode, children);
		}
	}
	
	@Override
	public void connect(String host, String email, String password){
		translator.setHost(host);
		translator.setEmail(email);
		translator.setPassword(password);
		try {
			shouldAllowTranslation(false);
			Logger.log("Connecting to imap...");
			translator.connect();
			Logger.logln("Complete.");
			
			loadFolders();
			
		} catch (ImapException e) {
			Logger.logln("Failure: "+e.getMessage());
		} catch (MessagingException e) {
			Logger.logln("Failure: "+e.getMessage());
		}
	}
	
	public void loadFolders(){

		Logger.log("Retrieving folders...");
		try {
			Folder[] folders = translator.getFolders();
			if(folders != null){
				Logger.logln("Found "+folders.length+" folders.");
				showFolders(folders);
			}else{
				errorMessage("You need to connect before loading folders");
			}
		} catch (MessagingException e) {
			Logger.logln("Error: "+e.getMessage());
		}
	}

	@Override
	public void showFolders(Folder[] folders) {
		
		DefaultMutableTreeNode top;
		if(folders == null){
			top = new DefaultMutableTreeNode("No folders found");
		}else{
			top = new DefaultMutableTreeNode();
			try {
				addFolders(top, folders);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		
		JTree tree = new JTree(top);
		folderSelector = new FolderSelector(this, tree);
		tree.addTreeSelectionListener(folderSelector);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		
		folderPane.setViewportView(tree);
		folderPane.updateUI();
	}

	@Override
	public Folder[] getSelectedFolders(){
		if(folderSelector == null){
			return new Folder[0];
		}
		
		ArrayList<String> folderNames = folderSelector.getSelectedFolderNames();
		try {
			return translator.getFolders(folderNames);
		} catch (MessagingException e) {
			Logger.logln("Error loading selected folders: "+e.getMessage());
			return new Folder[0];
		}
	}
	
	@Override
	public void errorMessage(String message){
		JOptionPane.showMessageDialog(frame, message);
	}
	
	
	JLabel translationProgressLabel = new JLabel();
	@Override
	public void displayTranslation(ArrayList<Message> messages){
		if(messages.size() == 0) return;
		shouldAllowTranslation(false);
		
		JFrame translationProgress = new JFrame("Translation Progress");
		translationProgress.setLayout(new BorderLayout());
		
		JPanel progressPanel = new JPanel(new GridLayout(2,1,5,5));		
		progressPanel.add(translationProgressLabel);
		JProgressBar progressBar = new JProgressBar(0, messages.size()-1);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressPanel.add(progressBar);
		
		translationProgress.add(progressPanel, BorderLayout.CENTER);
		translationProgress.add(new JPanel(), BorderLayout.NORTH);
		translationProgress.add(new JPanel(), BorderLayout.SOUTH);
		translationProgress.add(new JPanel(), BorderLayout.EAST);
		translationProgress.add(new JPanel(), BorderLayout.WEST);
		
		translationProgress.setSize(300, 110);
		translationProgress.setVisible(true);
		
		Thread thread = new Thread(new EmailThreadSorter(translator, this, progressBar, messages));
		thread.start();
	}

	@Override
	public void progressMessage(String message) {
		if(translationProgressLabel != null)
			translationProgressLabel.setText(message);
	}

	@Override
	public void shouldAllowTranslation(boolean allowed) {
		translate.setEnabled(allowed);
	}
	
	public static void main(String[] args){
		DefaultConversionView view = new DefaultConversionView();
	}
}

