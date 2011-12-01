package biz.karms.urbangaming.pinboy;

import javax.microedition.location.Criteria;

/**
 * Static constants used for configuration.
 * 
 * @author Karm
 */
public class Constants {
	
	/**
	 * --------------------------<br>
	 * GPS related configuration<br>
	 * --------------------------<br>
	 */
	/**
	 * GPS data <b>retrieve interval</b> in seconds. Retrieve data every 1 second is ok
	 */
	public static final int		GPS_DATA_RETRIEVE_INTERVAL		= 2;
	public static final int		DEFAULT_GPS_DATA_SEND_INTERVAL	= 2;
	/**
	 * In meters. Default is javax.microedition.location.Criteria.NO_REQUIREMENT
	 *///10
	public static final int		HORIZONTAL_ACCURACY				= Criteria.NO_REQUIREMENT;
	/**
	 * In milliseconds.
	 *///2000
	public static final int		PREFERRED_RESPONSE_TIME			= Criteria.NO_REQUIREMENT;
	/**
	 * In seconds. After this time, an invalid Location is returned. -1 Means DEFAULT (don't touch this :-)
	 */
	public static final int		GPS_TIME_OUT					= -1;
	
	public static final int		GPS_LISTENER_IS_NULL			= 0;
	/**
	 * Prefix for the message format e.g.: <b>GPS, </b> INT(2) DOUBLE DOUBLE,
	 * where the two digits integer is the Pip Boy ID and Latitude and Longitude
	 * follow as double values.
	 */
	public static final String	GPS_MESSAGE_FORMAT_PREFIX		= "GPS ";
	/**
	 * Infix for the message format e.g.: <b>GPS, </b> INT(2) DOUBLE"infix"DOUBLE,
	 * where the two digits integer is the Pip Boy ID and Latitude and Longitude
	 * follow as double values.
	 */
	public static final String	GPS_MESSAGE_FORMAT_INFIX		= " ";
	/**
	 * Postfix for the message format e.g.: <b>GPS, </b> INT(2) DOUBLE DOUBLE"postfix",
	 * where the two digits integer is the Pip Boy ID and Latitude and Longitude
	 * follow as double values.
	 */
	public static final String	GPS_MESSAGE_FORMAT_POSTFIX		= "";
	/**
	 * Default value to be returned to the server in case we do not know our
	 * GPS location....
	 */
	public static final double	GPS_LATI_LONG_DEFAULT			= 666.666;
	
	/**
	 * ----------------------------------------<br>
	 * Server connection related configuration<br>
	 * ----------------------------------------<br>
	 */
	/**
	 * Urban Gaming <b>Server address</b> - preferably IP, possibly host address.
	 */
	//public static final String	SERVER_ADDRESS			= "127.0.0.1";
	public static final String	SERVER_ADDRESS				= "81.19.0.120";
	/**
	 * Urban Gaming <b>Server port</b>. Make sure the port is open and the server is accepting data.
	 */
	public static final String	PORT						= "8120";

	/**
	 * Command reading frequency in seconds
	 */
	public static final int	COMMAND_RETRIEVE_INTERVAL	= 5;

	/**
	 * ---------------------<br>
	 * System configuration<br>
	 * ---------------------<br>
	 */
	/**
	 * Unique <b>ID</b> that identifies this mobile client. Make sure the ID is unique among all clients.
	 * This is configured in the JAD file! Not here.
	 */
	private static String	pinBoyID						= "00";
	
	/**
	 * ---------------------<br>
	 * Audio player configuration<br>
	 * ---------------------<br>
	 */
	/**
	 * Default audio media format is WAV (<i>audio/x-wav</i>).<br>
	 * Note: Make sure the mobile device suports selected format.<br>
	 */
	public static final String	AUDIO_MP3					= "audio/mpeg";
	public static final String	AUDIO_WAV					= "audio/x-wav";
	public static final String	AUDIO_MIDI					= "audio/midi";
	/**
	 * Player will play the Background Music n-times. The default value is 100, so the background music will be played over and over again one hundred times.
	 */
	public static final int		BGM_LOOP					= 100;
	/**
	 * Player will play the Sound Effect n-times.
	 */
	public static final int		SFX_LOOP					= 1;
	/**
	 * BGM player default volume.
	 */
	public static final int		BGM_DEFAULT_VOLUME			= 100;
	/**
	 * SFX player default volume.
	 */
	public static final int		SFX_DEFAULT_VOLUME			= 100;
	/**
	 * ---------------------<br>
	 * Filesystem configuration<br>
	 * ---------------------<br>
	 */
	/**
	 * Default file root. This is a very device-dependent value.<br>
	 * Default expected directory structure is:<br>
	 * <ul>
	 * <li>/ugame/audio/</li>
	 * <li>/ugame/video/</li>
	 * <li>/ugame/pictures/</li>
	 * </ul>
	 */
	//Sonny Ericsson c702
	public static final String	DEFAULT_FILE_ROOT			= "file:///c:/other/pinboy/";
	//WTK Emulator
	//public static final String	DEFAULT_FILE_ROOT			= "file:///root1/ugame/";
	public static final String	DIRECTORY_AUDIO				= "audio/";
	public static final String	DIRECTORY_VIDEO				= "video/";
	public static final String	DIRECTORY_PICTURES			= "pictures/";
	
	/**
	 * -------------------------------<br>
	 * Command-receiver configuration<br>
	 * -------------------------------<br>
	 */
	
	/**
	 * Audio player OFF. No Music guys... 
	 */
	public static final String	OFF							= "OFF";
	/**
	 * Background music format is a one string line as follows:<br>
	 * <i>BGM FileName.FileExtension</i>
	 */
	public static final String	BGM							= "BGM";
	/**
	 * Background music volume format is a one string line as follows:<br>
	 * <i>VBG integer</i>
	 */
	public static final String	VBG							= "VBG";
	/**
	 * Sound effect format is a one string line as follows:<br>
	 * <i>SFX FileName.FileExtension</i>
	 */
	public static final String	SFX							= "SFX";
	/**
	 * Sound effect volume format is a one string line as follows:<br>
	 * <i>VFX integer</i>
	 */
	public static final String	VFX							= "VFX";
	/**
	 * Video format is a one string line as follows:<br>
	 * <i>VID FileName.FileExtension</i>
	 */
	public static final String	VID							= "VID";
	
	/**
	 * Vibration command is as follows:<br>
	 * <i>VIB integer</i>
	 * where integer is a number of milliseconds we should keep vibrating.
	 */
	public static final String	VIB							= "VIB";
	
	/**
	 * For how many <b>minutes</b> should the mobile client sleep.
	 * Usage:
	 * SLPXXNumber
	 * 
	 * Where "SLP" are chars, XX is two-digits reserved for a client' ID and "Number" is the number of minutes.
	 * 
	 * Example:
	 * 
	 * SLP02120
	 *  
	 * Means that client with ID 2 should fall asleep for 120 minutes.
	 */
	public static final String	SLEEP_COMMAND				= "SLP";
	/**
	 * DEFAULT_GPS_DATA_RETRIEVE_INTERVAL value.
	 * 
	 * Usage:
	 * RTCXXNumber
	 * 
	 * Where "RTC" are chars, XX is two-digits reserved for a client' ID and "Number" is the number of seconds.
	 */
	public static final String	RETRIEVE_TIME_COMMAND		= "RTC";
	
	/**
	 * Monitor thread sleep time in seconds
	 */
	public static final int MONITOR_THREAD_SLEEP_SECONDS = 40 + DEFAULT_GPS_DATA_SEND_INTERVAL;
	
	public static void setPinBoyID(String pinBoyID) {
		Constants.pinBoyID = pinBoyID;
	}
	
	public static String getPinBoyID() {
		return pinBoyID;
	}
	
}
