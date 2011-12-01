package biz.karms.urbangaming.pinboy;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.location.LocationProvider;

public class ConnectionManager implements Runnable, CommandListener {
	private MainMIDlet				parent							= null;
	private Display					display							= null;
	private final StringItem		stringItemStatus				= new StringItem("Status:", "UNKNOWN");
	private final Form				form							= new Form("|== PIN BOY ==|");
	private final StringItem		stringItemID					= new StringItem("ID", ""+Constants.getPinBoyID());
	private final StringItem		stringItemCommand				= new StringItem("Cmd from server:", "unknown");
	private final Command			exitCommand						= new Command("Exit", Command.EXIT, 1);
	
	private boolean					connected						= false;
	private boolean					reading							= false;
	private boolean					writing							= false;
	private volatile long			lastMessageRead					= 0L;
	private volatile long			lastMessageWrite				= 0L;
	private volatile boolean		shouldWeSleep					= false;
	private boolean					needToWakeUp					= false;
	private volatile int			howManyMinutesToSleep			= 0;
	private int						dataSendFrequencySeconds		= Constants.DEFAULT_GPS_DATA_SEND_INTERVAL;
	private int						commandRetrieveFrequencySeconds	= Constants.COMMAND_RETRIEVE_INTERVAL;
	private int						monitorThreadSleepSeconds		= Constants.MONITOR_THREAD_SLEEP_SECONDS;

	private int 					outputReconnectCounter		= 1;
	private StreamConnection		connection					= null;
	private InputStream				inputStream					= null;
	private DataOutputStream		outputStream				= null;
	private GPSMessageWriter		gpsMessageWriter			= null;
	private CommandMessageReader	commandMessageReader		= null;
	private Thread					writer						= null;
	private Thread					reader						= null;
	private GPSListener				gpsListener					= null;
	private AudioPlayer				audioPlayer					= null;

	
	public ConnectionManager(MainMIDlet parent) {
		this.parent = parent;
		display = Display.getDisplay(parent);
		form.append(stringItemID);
		//Silent
		form.append(stringItemStatus);
		//Silent
		form.append(stringItemCommand);
		display.setCurrent(form);
	}
	
	public void start() {
		Thread t = new Thread(this);
		t.start();
	}
	
	private StreamConnection openConnection() {
		try {
			connected = true;
			return (StreamConnection) Connector.open("socket://" + parent.getTextFieldServerAddress() + ":" + parent.getTextFieldPort());
		} catch (Exception e) {
			connected = false;
			reading = false;
			writing = false;
		}
		return null;
	}
	
	private InputStream openInputStream() {
		try {
			reading = true;
			InputStream inputStream = connection.openInputStream();
			return inputStream;
		} catch (Exception e) {
			connected = false;
			reading = false;
			writing = false;
			//Debug
			////System.out.println("SHIT in openInputStream:" + e.toString());
		}
		return null;
	}
	
	private DataOutputStream openOutputStream() {
		try {
			writing = true;
			OutputStream inputStream = connection.openOutputStream();
			return new DataOutputStream(inputStream);
		} catch (IOException e) {
			connected = false;
			reading = false;
			writing = false;
			//Debug
			////System.out.println("SHIT in openOutputStream:" + e.toString());
		}
		return null;
	}
	
	public void run() {
		/*A lovely infinite loop :-* */
		while (true) {
			
			if(needToWakeUp) {
				shouldWeSleep = false;
				needToWakeUp = false;
				monitorThreadSleepSeconds = Constants.MONITOR_THREAD_SLEEP_SECONDS;
				gpsListener.startGPS();
			}
			
			try {
								
				if (gpsListener == null) {
					gpsListener = new GPSListener();
					gpsListener.startGPS();
				}
				if (audioPlayer == null) {
					audioPlayer = new AudioPlayer(parent.getTextFieldFileRoot());
				}
				if (!connected || connection == null) {
					if (connection != null) {
						connection.close();
					}
					connection = openConnection();
				}
				//Check whether the last message wasn't sent more than GPS_DATA_SEND_INTERVAL+60sec ago...
				if (!writing || outputStream == null || ((System.currentTimeMillis() - lastMessageWrite) > (dataSendFrequencySeconds * 1000 + 60000))) {
					if (gpsMessageWriter != null) {
						gpsMessageWriter.stop();
						gpsMessageWriter = null;
						writer = null;
					}
					if (outputStream != null) {
						outputStream.close();
					}
					outputStream = this.openOutputStream();
					gpsMessageWriter = new GPSMessageWriter();
					writer = new Thread(gpsMessageWriter);
					writer.start();
					// LOG SILENT
					stringItemStatus.setText("Output connections so far #"+outputReconnectCounter+".");
					outputReconnectCounter++;
				}
				// Check whether the last message wasn't read more than COMMAND_RETRIEVE_INTERVAL+60sec ago...
				if (!reading || inputStream == null || ((System.currentTimeMillis() - lastMessageRead) > (commandRetrieveFrequencySeconds * 1000 + 60000))) {
					if (commandMessageReader != null) {
						commandMessageReader.stop();
						commandMessageReader = null;
						reader = null;
					}
					if (inputStream != null) {
						inputStream.close();
					}
					inputStream = this.openInputStream();
					commandMessageReader = new CommandMessageReader();
					reader = new Thread(commandMessageReader);
					reader.start();
					// LOG
					// stringItemStatus.setText("Reading from socket started.");
				}
				//Debug
				// //System.out.println("LOG: Since last message written:" + (System.currentTimeMillis() - lastMessageWrite));
				// //System.out.println("LOG: Since last message read:" + (System.currentTimeMillis() - lastMessageRead));
				////System.out.println("Should we sleep:"+shouldWeSleep);
			
				if (shouldWeSleep) {
					//Debug
					////System.out.println("Gonna close connections...");
					closeConnections();
					gpsListener.stopGPS();
					playersOff();
					monitorThreadSleepSeconds = howManyMinutesToSleep*60;
					needToWakeUp = true;
				}
				//Debug
				////System.out.println("Gonna wait "+monitorThreadSleepSeconds+" seconds");
				
				Thread.sleep(monitorThreadSleepSeconds * 1000);
				
			} catch (Exception e) {
				
				/*System.out.println("SHIT in main monitoring thread:\n" +
						"\ngpsListener: " + gpsListener +
						"\nparent: " + parent +
						"\nconnected: " + connected +
						"\nconnection: " + connection +
						"\nwriting: " + writing +
						"\noutputStream: " + outputStream +
						"\ngpsMessageWriter: " + gpsMessageWriter +
						"\nwriter: " + writer +
						"\nreading: " + reading +
						"\ninputStream: " + inputStream +
										// "\ncommandMessageReader: " + commandMessageReader +
						"\nreader: " + reader + "" +
						"\n" + e.toString());*/
			}
		}
	}
	
	public void commandAction(Command c, Displayable arg1) {
		if ((c == Alert.DISMISS_COMMAND) || (c == exitCommand)) {
			closeConnections();
			parent.notifyDestroyed();
			parent.destroyApp(true);
		}
	}
	
	private class CommandMessageReader implements Runnable {
		private boolean	running	= true;

		public void run() {
			while (running) {
				try {
					if (inputStream.available() > 4) {
						
						int available = inputStream.available();
						byte[] bytes = new byte[available];
						inputStream.read(bytes, 0, available);

						String message = new String(bytes);
						//CMD0199
						//CMD - command, 01 - ID, 99 - value...
						String clientID = message.substring(3, 5);
							if (message.startsWith(Constants.SLEEP_COMMAND)) {
								if (Constants.getPinBoyID().equals(clientID) || clientID.equals("00")) {
									
								
								int sleepMinutes = Integer.parseInt(message.substring(5));
								howManyMinutesToSleep = sleepMinutes;
								shouldWeSleep = true;
								//Silent
								stringItemCommand.setText(Constants.SLEEP_COMMAND +" "+ clientID +" "+ message.substring(5));
								//Debug
								////System.out.println("Reader: Should we sleep:"+shouldWeSleep);
								}
							} else if (message.startsWith(Constants.RETRIEVE_TIME_COMMAND)) {
								if (Constants.getPinBoyID().equals(clientID) || clientID.equals("00")) {

								int retrieveInterval = Integer.parseInt(message.substring(5));
								dataSendFrequencySeconds = retrieveInterval;
								gpsListener.stopGPS();
								gpsListener.setGpsDataRetrieveInterval(retrieveInterval);
								gpsListener.startGPS();
								//Silent
								stringItemCommand.setText(Constants.RETRIEVE_TIME_COMMAND +" "+ clientID +" "+ message.substring(5));
								}
							} else if (message.startsWith(Constants.BGM)) {
								if (Constants.getPinBoyID().equals(clientID) || clientID.equals("00")) {

								audioPlayer.setCurrentFile(message.substring(5));
								if (audioPlayer.getBgmPlayerStatus() == AudioPlayer.PLAYER_PLAYING || audioPlayer.getBgmPlayerStatus() == AudioPlayer.PLAYER_PAUSED) {
									audioPlayer.closeBGMPlayer();
								}
								if (audioPlayer.getSfxPlayerStatus() == AudioPlayer.PLAYER_PLAYING) {
									audioPlayer.pauseSFXPlayer();
								}
								audioPlayer.openBGMPlayer();
								//Silent
								stringItemCommand.setText(Constants.BGM +" "+ clientID +" "+ message.substring(5));
								}
							} else if (message.startsWith(Constants.VBG)) {
								if (Constants.getPinBoyID().equals(clientID) || clientID.equals("00")) {

								stringItemCommand.setText(Constants.VBG +" "+ clientID +" "+ message.substring(5));
								}
							} else if (message.startsWith(Constants.SFX)) {
								if (Constants.getPinBoyID().equals(clientID) || clientID.equals("00")) {

								audioPlayer.setCurrentFile(message.substring(5));
								if (audioPlayer.getSfxPlayerStatus() == AudioPlayer.PLAYER_PLAYING || audioPlayer.getSfxPlayerStatus() == AudioPlayer.PLAYER_PAUSED) {
									audioPlayer.closeSFXPlayer();
								}
								if (audioPlayer.getBgmPlayerStatus() == AudioPlayer.PLAYER_PLAYING) {
									audioPlayer.pauseBGMPlayer();
								}
								audioPlayer.openSFXPlayer();
								//Silent
								stringItemCommand.setText(Constants.SFX +" "+ clientID +" "+ message.substring(5));
								}
							} else if (message.startsWith(Constants.VFX)) {
								if (Constants.getPinBoyID().equals(clientID) || clientID.equals("00")) {

								stringItemCommand.setText(Constants.VFX +" "+ clientID +" "+ message.substring(5));
								}
							} else if (message.startsWith(Constants.VID)) {
								if (Constants.getPinBoyID().equals(clientID) || clientID.equals("00")) {

								stringItemCommand.setText(Constants.VID +" "+ clientID +" "+ message.substring(5));
								}
							} else if (message.startsWith(Constants.VIB)) {
								if (Constants.getPinBoyID().equals(clientID) || clientID.equals("00")) {

								Display.getDisplay(parent).vibrate(Integer.parseInt(message.substring(5)));
								//Silent
								stringItemCommand.setText(Constants.VIB +" "+ clientID +" "+ message.substring(5));
								}
							} else if (message.startsWith(Constants.OFF)) {
								if (Constants.getPinBoyID().equals(clientID) || clientID.equals("00")) {

								playersOff();
								//Silent
								stringItemCommand.setText(Constants.OFF +" "+ clientID +" "+ message.substring(5));
								}
							} else {
								//stringItemCommand.setText("Err:[" + message + "]");
								stringItemCommand.setText("Erroneous message.");
							}
						
						}
					lastMessageRead = System.currentTimeMillis();
					Thread.sleep(commandRetrieveFrequencySeconds * 1000);
				} catch (Exception e) {
					reading = false;
					/*System.out.println("SHIT in CommandMessageReader thread:\n" +
							"\ngpsListener: " + gpsListener +
							"\nparent: " + parent +
							"\nconnected: " + connected +
							"\nconnection: " + connection +
							"\nwriting: " + writing +
							"\noutputStream: " + outputStream +
							"\ngpsMessageWriter: " + gpsMessageWriter +
							"\nwriter: " + writer +
							"\nreading: " + reading +
							"\ninputStream: " + inputStream +
							"\ncommandMessageReader: " + commandMessageReader +
							"\nreader: " + reader + "" +
							"\n" + e.toString());*/
				}
			}
		}
		
		public void stop() {
			this.running = false;
			reading = false;
		}
	}
	
	private class GPSMessageWriter implements Runnable {
		private boolean	running	= true;
		
		public void run() {
			while (running) {
				try {
		
					String message = null;
					int gpsListenerState = gpsListener.getState();
					if (gpsListener.getState() == LocationProvider.AVAILABLE && gpsListener.getLatitude() != Constants.GPS_LATI_LONG_DEFAULT && gpsListener.getLongitude() != Constants.GPS_LATI_LONG_DEFAULT) {
						message = 	Constants.GPS_MESSAGE_FORMAT_PREFIX + Constants.getPinBoyID() +" "+ gpsListener.getLatitude() + 
						Constants.GPS_MESSAGE_FORMAT_INFIX + gpsListener.getLongitude() + 
						Constants.GPS_MESSAGE_FORMAT_POSTFIX;
						//Silent
						stringItemStatus.setText("Lat:"+ gpsListener.getLatitude() +",Lon:"+ gpsListener.getLongitude());
					} else {
						//Silent
						switch (gpsListenerState) {
							case LocationProvider.OUT_OF_SERVICE:
								stringItemStatus.setText("OUT_OF_SERVICE");
								break;
							case LocationProvider.TEMPORARILY_UNAVAILABLE:
								stringItemStatus.setText("TEMPORARILY_UNAVAILABLE");
								break;
							case Constants.GPS_LISTENER_IS_NULL:
								stringItemStatus.setText("GPS_LISTENER_IS_NULL");
								break;								
							default:
								stringItemStatus.setText("UNKNOWN:"+gpsListenerState);
								break;
						}
						message = 	Constants.GPS_MESSAGE_FORMAT_PREFIX + Constants.getPinBoyID() +" "+ Constants.GPS_LATI_LONG_DEFAULT + 
						Constants.GPS_MESSAGE_FORMAT_INFIX + Constants.GPS_LATI_LONG_DEFAULT + 
						Constants.GPS_MESSAGE_FORMAT_POSTFIX;
					}
					
					byte[] bytes = new byte[message.getBytes().length];
					bytes = message.getBytes();
					outputStream.write(bytes);
					outputStream.flush();
					lastMessageWrite = System.currentTimeMillis();
					
					Thread.sleep(dataSendFrequencySeconds * 1000);
					
				} catch (Exception e) {
					writing = false;
					/*System.out.println("SHIT in GPSMessageWriter thread:\n" +
							"\ngpsListener: " + gpsListener +
							"\nparent: " + parent +
							"\nconnected: " + connected +
							"\nconnection: " + connection +
							"\nwriting: " + writing +
							"\noutputStream: " + outputStream +
							"\ngpsMessageWriter: " + gpsMessageWriter +
							"\nwriter: " + writer +
							"\nreading: " + reading +
							"\ninputStream: " + inputStream +
												// "\ncommandMessageReader: " + commandMessageReader +
							"\nreader: " + reader + "" +
							"\n" + e.toString());*/
				}
			}
		}
		
		public void stop() {
			this.running = false;
			writing = false;
		}
	}

	public void playersOff() {
		if(audioPlayer != null) {
			if (audioPlayer.getSfxPlayerStatus() == AudioPlayer.PLAYER_PLAYING || audioPlayer.getSfxPlayerStatus() == AudioPlayer.PLAYER_PAUSED) {
				audioPlayer.closeSFXPlayer();
			}
			if (audioPlayer.getBgmPlayerStatus() == AudioPlayer.PLAYER_PLAYING || audioPlayer.getBgmPlayerStatus() == AudioPlayer.PLAYER_PAUSED) {
				audioPlayer.closeBGMPlayer();
			}
		}
	}
	
	public void closeConnections() {
		try {
		if (gpsMessageWriter != null) {
			gpsMessageWriter.stop();
		}
		if (outputStream != null) {
			outputStream.close();
		}
		if (commandMessageReader != null) {
			commandMessageReader.stop();
		}
		if (inputStream != null) {
			inputStream.close();
		}
		if (connection != null) {
			connection.close();
			connected = false;
		}
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (Exception ex) {
			//System.out.println("Shit in ConnectionManager.closeConnections:"+ex.toString());
		}
	}
}
