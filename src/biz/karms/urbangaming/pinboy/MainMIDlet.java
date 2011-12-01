package biz.karms.urbangaming.pinboy;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

/**
 * Application entry point.
 * 
 * @author Karm
 */
public class MainMIDlet extends MIDlet implements CommandListener {
	
	private final Display		display;
	private final Form			form						= new Form("|== PIN BOY ==|");
	private final StringItem	stringItemSettings;
	private final TextField		textFieldFileRoot		= new TextField("File root:", Constants.DEFAULT_FILE_ROOT, 40, TextField.ANY);
	private final TextField		textFieldServerAddress	= new TextField("Server:", Constants.SERVER_ADDRESS, 20, TextField.ANY);
	private final TextField		textFieldPort			= new TextField("Port:", Constants.PORT, 6, TextField.ANY);
	private final Command		exitCommand				= new Command("Exit", Command.EXIT, 2);
	private final Command		startCommand			= new Command("Start", Command.ITEM, 1);
	private boolean				isPaused;
	private ConnectionManager	connectionManager		= null;
	
	/**
	 * MainMIDlet - set all visible components
	 */
	public MainMIDlet() {
		/**
		 * Load PIN BOY ID
		 */
		Constants.setPinBoyID(this.getAppProperty("pinboy-id"));
		display = Display.getDisplay(this);
		
		/**
		 * Let's display some environment settings.
		 */
		stringItemSettings = new StringItem(
				"Settings:",
				"\nMobile client ID:"
				+ Constants.getPinBoyID()
				+ "\nMicro Config:"
				+ System.getProperty("microedition.configuration")
				+ "\nMicro Profile:"
				+ System.getProperty("microedition.profiles"));
		form.append(textFieldServerAddress);
		form.append(textFieldPort);
		form.append(stringItemSettings);
		form.append(textFieldFileRoot);
		form.addCommand(exitCommand);
		form.addCommand(startCommand);
		form.setCommandListener(this);
		display.setCurrent(form);
	}
	
	public boolean isPaused() {
		return isPaused;
	}
	
	public void startApp() {
		isPaused = false;
	}
	
	public void pauseApp() {
		isPaused = true;
	}
	
	public void destroyApp(boolean unconditional) {
		if (connectionManager != null) {
			connectionManager.closeConnections();
		}
	}
	
	/**
	 * Perform actions
	 * 
	 * @param c
	 *            catch command
	 * @param s
	 *            Displayable component
	 */
	public void commandAction(Command c, Displayable s) {
		if (c == exitCommand) {
			destroyApp(true);
			notifyDestroyed();
		} else if (c == startCommand) {
				connectionManager = new ConnectionManager(this);
				connectionManager.start();
		} else {
			throw new IllegalArgumentException("Illegal command.");
		}
	}
	
	public String getTextFieldPort() {
		return textFieldPort.getString();
	}
	
	public String getTextFieldServerAddress() {
		return textFieldServerAddress.getString();
	}
	
	public String getTextFieldFileRoot() {
		return textFieldFileRoot.getString();
	}
}