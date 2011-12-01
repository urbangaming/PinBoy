package biz.karms.urbangaming.pinboy;

import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

/**
 * AudioPlayer handles all audio streams.
 * 
 * @author Karm
 */
public class AudioPlayer {
	
	/**
	 * There are two players available. SFX player is focused on playing short effects,<br>
	 * BGM player is focused on playing long background music.
	 */
	private Player			sfxPlayer			= null;
	private Player			bgmPlayer			= null;
	
	/**
	 * Volume setting.
	 */
	private int				bgmVolume			= Constants.BGM_DEFAULT_VOLUME;
	private int				sfxVolume			= Constants.SFX_DEFAULT_VOLUME;
	private VolumeControl	bgmVolumeControl	= null;
	private VolumeControl	sfxVolumeControl	= null;
	
	/**
	 * Source file access.
	 */
	private String			currentFile			= null;
	private InputStream		bgmIn				= null;
	private InputStream		sfxIn				= null;
	private FileConnection	bgmFC				= null;
	private FileConnection	sfxFC				= null;
	
	/**
	 * How many times should be the file repeated.
	 */
	private int				bgmLoop				= Constants.BGM_LOOP;
	private int				sfxLoop				= Constants.SFX_LOOP;
	
	/**
	 * Player status monitoring.
	 */
	private int				sfxPlayerStatus		= PLAYER_CLOSED;
	private int				bgmPlayerStatus		= PLAYER_CLOSED;
	public static final int	PLAYER_CLOSED		= 0;
	public static final int	PLAYER_PAUSED		= 1;
	public static final int	PLAYER_PLAYING		= 2;
	
	/**
	 * System attributes.
	 */
	private String			fileRoot			= null;
	
	/**
	 * @param connectionManager
	 *            is a parent thread
	 * @param fileRoot
	 *            root directory
	 */
	public AudioPlayer(String fileRoot) {
		this.fileRoot = fileRoot;
	}
	
	/**
	 * Open Background music player in a new thread
	 */
	public void openBGMPlayer() {
		Thread runner = new Thread() {
			
			public void run() {
				try {
					if (bgmIn != null) {
						bgmIn.close();
					}
					
					if (bgmFC != null) {
						bgmFC.close();
					}
					
					bgmFC = (FileConnection) Connector.open(fileRoot + Constants.DIRECTORY_AUDIO + currentFile, Connector.READ);
					bgmIn = bgmFC.openInputStream();
					
					bgmPlayer = Manager.createPlayer(bgmIn, Constants.AUDIO_MP3);
					bgmPlayer.addPlayerListener(new BGMListener(sfxPlayer, bgmPlayer, AudioPlayer.this));
					bgmPlayer.realize();
					bgmVolumeControl = (VolumeControl) bgmPlayer.getControl("VolumeControl");
					if (bgmVolumeControl != null) {
						bgmVolumeControl.setLevel(bgmVolume);
					}
					bgmPlayer.prefetch();
					if (bgmLoop > 1) {
						bgmPlayer.setLoopCount(bgmLoop);
					}
					bgmPlayer.start();
					bgmPlayerStatus = PLAYER_PLAYING;
				} catch (MediaException e) {
					// TODO:replace with alert window...
					//System.out.println("SHIT in openBGMPlayer thread, MediaException...:" + e.toString());
				} catch (Exception e) {
					// TODO:replace with alert window...
					//System.out.println("SHIT in openBGMPlayer thred:" + e.toString());
				}
			}
		};
		runner.start();
	}
	
	/**
	 * Open Sound effect player in a new thread
	 */
	public void openSFXPlayer() {
		Thread runner = new Thread() {
			
			public void run() {
				try {
					if (sfxIn != null) {
						sfxIn.close();
					}
					
					if (sfxFC != null) {
						sfxFC.close();
					}
					
					sfxFC = (FileConnection) Connector.open(fileRoot + Constants.DIRECTORY_AUDIO + currentFile, Connector.READ);
					sfxIn = sfxFC.openInputStream();
					
					sfxPlayer = Manager.createPlayer(sfxIn, Constants.AUDIO_MP3);
					sfxPlayer.addPlayerListener(new SFXListener(sfxPlayer, bgmPlayer, AudioPlayer.this));
					sfxPlayer.realize();
					sfxVolumeControl = (VolumeControl) sfxPlayer.getControl("VolumeControl");
					if (sfxVolumeControl != null) {
						sfxVolumeControl.setLevel(sfxVolume);
					}
					sfxPlayer.prefetch();
					if (sfxLoop > 1) {
						sfxPlayer.setLoopCount(sfxLoop);
					}
					sfxPlayer.start();
					sfxPlayerStatus = PLAYER_PLAYING;
				} catch (MediaException e) {
					//System.out.println("SHIT in openSFXPlayer thread, MediaException...:" + e.toString());
					// TODO:replace with alert window...
				} catch (Exception e) {
					//System.out.println("SHIT in openSFXPlayer thread:" + e.toString());
					// TODO:replace with alert window...
				}
			}
		};
		runner.start();
	}
	
	/**
	 * Pause BGM player in order to let the SFX player play...
	 */
	public void pauseBGMPlayer() {
		if (bgmPlayer != null) {
			try {
				bgmPlayer.stop();
				bgmPlayerStatus = PLAYER_PAUSED;
			} catch (MediaException e) {
				//System.out.println("SHIT in pauseBGMPlayer, MediaException...:" + e.toString());
			} catch (Exception e) {
				//System.out.println("SHIT in pauseBGMPlayer:" + e.toString());
			}
		}
	}
	
	/**
	 * Resume BGM player after SFX ends. This method is called by PlayerListener subclass.
	 */
	public void resumeBGMPlayer() {
		if (bgmPlayer != null) {
			try {
				bgmPlayer.start();
				bgmPlayerStatus = PLAYER_PLAYING;
			} catch (MediaException e) {
				//System.out.println("SHIT in resumeBGMPlayer, MediaException...:" + e.toString());
			} catch (Exception e) {
				//System.out.println("SHIT in resumeBGMPlayer:" + e.toString());
			}
		}
	}
	
	/**
	 * Close BGM player and release all resources.
	 */
	public void closeBGMPlayer() {
		if (bgmPlayer != null) {
			bgmPlayer.close();
			try {
				bgmIn.close();
			} catch (Exception e) {
				//System.out.println("SHIT in closeBGMPlayer:" + e.toString());
			}
			bgmPlayer = null;
			bgmIn = null;
			bgmPlayerStatus = PLAYER_CLOSED;
		}
	}
	
	/**
	 * Pause SFX player in order to let the BFX player play...
	 */
	public void pauseSFXPlayer() {
		if (sfxPlayer != null) {
			try {
				sfxPlayer.stop();
				sfxPlayerStatus = PLAYER_PAUSED;
			} catch (MediaException e) {
				//System.out.println("SHIT in pauseSFXPlayer, MediaException...:" + e.toString());
			} catch (Exception e) {
				//System.out.println("SHIT in pauseSFXPlayer:" + e.toString());
			}
		}
	}
	
	/**
	 * Resume SFX player after BGM ends. This method is called by PlayerListener subclass.
	 */
	public void resumeSFXPlayer() {
		if (sfxPlayer != null) {
			try {
				sfxPlayer.start();
				sfxPlayerStatus = PLAYER_PLAYING;
			} catch (MediaException e) {
				//System.out.println("SHIT in resumeSFXPlayer, MediaException...:" + e.toString());
			} catch (Exception e) {
				//System.out.println("SHIT in resumeSFXPlayer:" + e.toString());
			}
		}
	}
	
	/**
	 * Close SFX player and release all resources.
	 */
	public void closeSFXPlayer() {
		if (sfxPlayer != null) {
			sfxPlayer.close();
			try {
				sfxIn.close();
			} catch (Exception e) {
				//System.out.println("SHIT in closeSFXPlayer:" + e.toString());
			}
			sfxPlayer = null;
			sfxIn = null;
			sfxPlayerStatus = PLAYER_CLOSED;
		}
	}
	
	/**
	 * Current file must be set before opening any of the players.
	 * 
	 * @param currentFile
	 */
	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;
	}
	
	/**
	 * How many times should be the track repeated.
	 * 
	 * @param bgmLoop
	 */
	public void setBgmLoop(int bgmLoop) {
		this.bgmLoop = bgmLoop;
	}
	
	/**
	 * How many times should be the track repeated.
	 * 
	 * @param bgmLoop
	 */
	public void setSfxLoop(int sfxLoop) {
		this.sfxLoop = sfxLoop;
	}
	
	/**
	 * BGM Volume. Default value is 100 (max).
	 * 
	 * @param bgmVolume
	 */
	public void setBgmVolume(int bgmVolume) {
		this.bgmVolume = bgmVolume;
	}
	
	/**
	 * SFX Volume. Default value is 100 (max).
	 * 
	 * @param bgmVolume
	 */
	public void setSfxVolume(int sfxVolume) {
		this.sfxVolume = sfxVolume;
	}
	
	/**
	 * Player can be CLOSED,PLAYING or PAUSED.
	 * 
	 * @return current BGM player status.
	 */
	public int getBgmPlayerStatus() {
		return bgmPlayerStatus;
	}
	
	/**
	 * Player can be CLOSED, PLAYING or PAUSED.
	 * 
	 * @return current SFX player status.
	 */
	public int getSfxPlayerStatus() {
		return sfxPlayerStatus;
	}
	
	/**
	 * This class is monitoring SFX player events. e.g. the EndOfMedia event.
	 */
	private class SFXListener implements PlayerListener {
		
		private Player		sfxPlayer	= null;
		private Player		bgmPlayer	= null;
		private AudioPlayer	audioPlayer	= null;
		
		public SFXListener(Player sfxPlayer, Player bgmPlayer, AudioPlayer audioPlayer) {
			this.setSfxPlayer(sfxPlayer);
			this.setBgmPlayer(bgmPlayer);
			this.audioPlayer = audioPlayer;
		}
		
		/**
		 * If the SFX effect is finished, let's resume paused Background Music.
		 * 
		 * @param player
		 * @param event
		 * @param eventData
		 */
		public void playerUpdate(Player player, String event, Object eventData) {
			/**
			 * In this case is == correct because they are actually same instances...
			 */
			if (event == PlayerListener.END_OF_MEDIA && audioPlayer.bgmPlayerStatus == AudioPlayer.PLAYER_PAUSED) {
				audioPlayer.resumeBGMPlayer();
			}
		}
		
		public void setBgmPlayer(Player bgmPlayer) {
			this.bgmPlayer = bgmPlayer;
		}
		
		public Player getBgmPlayer() {
			return bgmPlayer;
		}
		
		public void setSfxPlayer(Player sfxPlayer) {
			this.sfxPlayer = sfxPlayer;
		}
		
		public Player getSfxPlayer() {
			return sfxPlayer;
		}
	}
	
	/**
	 * This class is monitoring BGM player events. e.g. the EndOfMedia event.
	 */
	private class BGMListener implements PlayerListener {
		
		private Player		sfxPlayer	= null;
		private Player		bgmPlayer	= null;
		private AudioPlayer	audioPlayer	= null;
		
		public BGMListener(Player sfxPlayer, Player bgmPlayer, AudioPlayer audioPlayer) {
			this.setSfxPlayer(sfxPlayer);
			this.setBgmPlayer(bgmPlayer);
			this.audioPlayer = audioPlayer;
		}
		
		/**
		 * If the BGM is finished, let's resume paused Sound Effect.
		 * 
		 * @param player
		 * @param event
		 * @param eventData
		 */
		public void playerUpdate(Player player, String event, Object eventData) {
			/**
			 * In this case is == correct because they are actually same instances...
			 */
			if (event == PlayerListener.END_OF_MEDIA && audioPlayer.sfxPlayerStatus == AudioPlayer.PLAYER_PAUSED) {
				audioPlayer.resumeSFXPlayer();
			}
		}
		
		public void setSfxPlayer(Player sfxPlayer) {
			this.sfxPlayer = sfxPlayer;
		}
		
		public Player getSfxPlayer() {
			return sfxPlayer;
		}
		
		public void setBgmPlayer(Player bgmPlayer) {
			this.bgmPlayer = bgmPlayer;
		}
		
		public Player getBgmPlayer() {
			return bgmPlayer;
		}
	}
}