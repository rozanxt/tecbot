package zan.game.sound;

import java.io.File;
import java.net.URL;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;
import zan.game.resource.ResourceData;

/** Sound manager class */
public class SoundManager {
	private static final String LOGNAME = "SoundManager :: ";

	/** Sound resources directory */
	private final static String SND_DIR = "res/snd/";

	/** Sound system */
	private static SoundSystem sndSystem;

	/** Current background music */
	private static String backgroundMusic;

	/** Initialize sound manager */
	public static void init() {
		try {
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec("wav", CodecJOrbis.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
		} catch(SoundSystemException e) {
		    System.err.println(LOGNAME + "Error linking with sound plug-ins:\n " + e);
		}
		sndSystem = new SoundSystem();
	}

	/** Sound manager cleanup */
	public static void destroy() {
		sndSystem.cleanup();
	}

	/** Load sound from a resource data */
	public static void loadSoundData(ResourceData sndData) {
		if (sndData.isEmpty()) {
			System.err.println(LOGNAME + "Error loading sounds:\n no data found");
			return;
		}

		for (int i=0;i<sndData.getNumNodes();i++) {
			ResourceData node = sndData.getNode(i);
			if (!node.isEmpty()) {
				loadSound(node.getName(),
						  node.getValue("file"),
						  node.getBooleanValue("stream"),
						  node.getBooleanValue("loop"));
			}
		}
	}

	/** Load sound */
	private static void loadSound(String name, String fnm, boolean stream, boolean loop) {
		try {
			URL url = new File(SND_DIR + fnm).toURI().toURL();
			if (stream) {
				sndSystem.newStreamingSource(true, name, url, fnm, loop, 0f, 0f, 0f, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
			} else {
				sndSystem.newSource(true, name, url, fnm, loop, 0f, 0f, 0f, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
			}
		} catch(Exception e) {
			System.err.println(LOGNAME + "Error loading sound: " + SND_DIR + fnm);
		}
	}

	/** Set sound volume */
	public static void setVolume(String name, float volume) {
		sndSystem.setVolume(name, volume);
	}

	/** Play sound effect */
	public static void playSFX(String name) {
		if (sndSystem.playing(name)) sndSystem.stop(name);
		sndSystem.play(name);
	}

	/** Play background music */
	public static void playBGM(String name) {
		if (sndSystem.playing(backgroundMusic)) sndSystem.stop(backgroundMusic);
		backgroundMusic = name;
		sndSystem.play(backgroundMusic);
	}

	/** Unload sounds from a resource data */
	public static void unloadSoundData(ResourceData sndData) {
		if (sndData.isEmpty()) {
			System.err.println(LOGNAME + "Error unloading sounds: no data found");
			return;
		}

		for (int i=0;i<sndData.getNumNodes();i++) {
			ResourceData node = sndData.getNode(i);
			if (!node.isEmpty()) {
				unloadSound(node.getName());
			}
		}
	}

	/** Unload sound */
	private static void unloadSound(String name) {
		sndSystem.unloadSound(name);
	}

}
