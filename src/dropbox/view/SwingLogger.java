package dropbox.view;

import javax.swing.JTextArea;

import bus.tools.LogWriter;

public class SwingLogger implements LogWriter{

	JTextArea log;
	
	public SwingLogger(JTextArea log){
		this.log = log;
	}
	
	@Override
	public void println(String message) {
		log.setText(log.getText()+message+"\n");
	}
	
	@Override
	public void print(String message) {
		log.setText(log.getText()+message);
	}

}
