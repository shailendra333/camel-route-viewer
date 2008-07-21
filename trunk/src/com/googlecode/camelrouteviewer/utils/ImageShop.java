package com.googlecode.camelrouteviewer.utils;

import java.io.File;
import java.net.URL;
import java.util.*;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.*;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.googlecode.camelrouteviewer.Activator;

public class ImageShop {
	private static ImageRegistry register = new ImageRegistry();

	private static Set keys = new HashSet();
	static {
		initial();
	}

	public static ImageDescriptor getDescriptor(String key) {
		ImageDescriptor image = register.getDescriptor(key);
		if (image == null) {
			image = ImageDescriptor.getMissingImageDescriptor();
		}
		return image;
	}

	public static Image get(String key) {
		Image image = register.get(key);
		if (image == null) {
			image = ImageDescriptor.getMissingImageDescriptor().createImage();
		}
		return image;
	}

	public static String[] getImageKey() {
		return (String[]) keys.toArray(new String[keys.size()]);
	}

	private static void initial() {
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		URL url = bundle.getEntry("icons");
		try {
			url = Platform.asLocalURL(url);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
		File file = new File(url.getPath());
		File[] images = file.listFiles();
		for (int i = 0; i < images.length; i++) {
			File f = images[i];
			if (!f.isFile()) {
				continue;
			}
			String name = f.getName();
			URL fullPathString = bundle.getEntry("icons/" + name);
			ImageDescriptor des = ImageDescriptor.createFromURL(fullPathString);
			register.put(name, des);
			keys.add(name);
		}
	}
}