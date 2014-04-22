package zan.game.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLUtil;

/** Icon loader class */
public class IconLoader {
	private static final String LOGNAME = "IconLoader :: ";
	
	/** Icon resources directory */
	private static final String ICO_DIR = "res/ico/";
	
	/** @return Usable icon data */
	public static ByteBuffer[] loadIcon(String fnm) {
		BufferedImage iconImage = null;
		ByteBuffer[] imageBuffer = null;
		
		try {
			iconImage = ImageIO.read(new File(ICO_DIR + fnm));
			switch (LWJGLUtil.getPlatform()) {
				case LWJGLUtil.PLATFORM_WINDOWS:
					imageBuffer = new ByteBuffer[3];
					imageBuffer[0] = loadInstance(iconImage, iconImage.getWidth());
					imageBuffer[1] = loadInstance(iconImage, 16);
					imageBuffer[2] = loadInstance(iconImage, 32);
					break;
				case LWJGLUtil.PLATFORM_MACOSX:
					imageBuffer = new ByteBuffer[1];
					imageBuffer[0] = loadInstance(iconImage, 128);
					break;
				case LWJGLUtil.PLATFORM_LINUX:
					imageBuffer = new ByteBuffer[1];
					imageBuffer[0] = loadInstance(iconImage, 32);
					break;
			}
		} catch (IOException e) {
			System.err.println(LOGNAME + "Error loading icon for " + ICO_DIR + fnm + ":\n " + e); 
		}
		
		return imageBuffer;
	}
	
	/** @return Icon instance */
	private static ByteBuffer loadInstance(BufferedImage image, int dimension) {
		BufferedImage scaledIcon = new BufferedImage(dimension, dimension, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g = scaledIcon.createGraphics();
		float ratio = getIconRatio(image, scaledIcon);
		float width = image.getWidth() * ratio;
		float height = image.getHeight() * ratio;
		g.drawImage(image, (int)((scaledIcon.getWidth() - width) / 2), (int)((scaledIcon.getHeight() - height) / 2), (int)(width), (int)(height), null);
		g.dispose();
		
		return convertToByteBuffer(scaledIcon);
	}
	
	/** @return Icon ratio */
	private static float getIconRatio(BufferedImage src, BufferedImage icon) {
		float ratio = 1;
		if (src.getWidth() > icon.getWidth()) ratio = (float) (icon.getWidth()) / src.getWidth();
		else ratio = (int) (icon.getWidth() / src.getWidth());
		if (src.getHeight() > icon.getHeight()) {
			float r2 = (float) (icon.getHeight()) / src.getHeight();
			if (r2 < ratio) ratio = r2;
		} else {
			float r2 = (int) (icon.getHeight() / src.getHeight());
			if (r2 < ratio) ratio = r2;
		}
		return ratio;
	}
	
	/** Convert BufferedImage to ByteBuffer */
	public static ByteBuffer convertToByteBuffer(BufferedImage image) {
		byte[] buffer = new byte[image.getWidth() * image.getHeight() * 4];
		int counter = 0;
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				int colorSpace = image.getRGB(j, i);
				buffer[counter + 0] = (byte) ((colorSpace << 8) >> 24);
				buffer[counter + 1] = (byte) ((colorSpace << 16) >> 24);
				buffer[counter + 2] = (byte) ((colorSpace << 24) >> 24);
				buffer[counter + 3] = (byte) (colorSpace >> 24);
				counter += 4;
			}
		}
		return ByteBuffer.wrap(buffer);
	}
	
}
