package com.uiautomation.framework.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import com.android.uiautomator.core.Configurator;
import com.android.uiautomator.core.UiCollection;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.uiautomation.framework.engine.watcher.ClickUiObjectWatcher;
import com.uiautomation.framework.engine.watcher.PressKeysWatcher;
import com.uiautomation.framework.utils.CmdResult;
import com.uiautomation.framework.utils.Constant;

public class TestEngine implements ITestEngine {

	final public static String STORAGE_PATH = "/data/local/tmp/";

	private final HashSet<String> watchers = new HashSet<String>();

	private static final int DEVIATION = 10; // PIXEL DIVIATION

	public TestEngine(long waitTimeout) {
		Configurator.getInstance().setWaitForSelectorTimeout(waitTimeout);
	}


	@Override
	public DeviceInfo deviceInfo() {
		return DeviceInfo.getDeviceInfo();
	}

	@Override
	public boolean openApplication(String pkg, String cls) {
		String cmd = "am start -a android.intent.action.MAIN -W -n " + pkg
				+ "/" + cls;
		return executeCmd(cmd) == 0;
	}

	@Override
	public boolean enterText(String text, UiSelector uiSelector)
			throws UiObjectNotFoundException {
		return new UiObject(uiSelector).setText(text);
	}

	@Override
	public boolean clickText(String text) throws UiObjectNotFoundException {
		return new UiObject(new UiSelector().text(text)).click();
	}

	@Override
	public boolean clickResourceIdMatches(String id)
			throws UiObjectNotFoundException {
		return new UiObject(new UiSelector().resourceIdMatches(".*" + id))
				.click();
	}

	@Override
	public boolean clickResourceId(String id) throws UiObjectNotFoundException {
		return new UiObject(new UiSelector().resourceId(id)).click();
	}

	@Override
	public boolean clickClass(String clzName, int instance)
			throws UiObjectNotFoundException {
		return new UiObject(new UiSelector().className(clzName).instance(
				instance)).click();
	}

	@Override
	public boolean clickDescription(String discription)
			throws UiObjectNotFoundException {
		return new UiObject(new UiSelector().description(discription))
				.clickAndWaitForNewWindow();
	}

	@Override
	public boolean clickTextContains(String text)
			throws UiObjectNotFoundException {
		return new UiObject(new UiSelector().textContains(text))
				.clickAndWaitForNewWindow();
	}

	@Override
	public boolean click(UiSelector uiSelector)
			throws UiObjectNotFoundException {
		return new UiObject(uiSelector).clickAndWaitForNewWindow();
	}

	@Override
	public boolean click(UiObject obj) throws UiObjectNotFoundException {
		return obj.clickAndWaitForNewWindow();
	}

	@Override
	public boolean click(UiSelector uiSelector, String corner)
			throws UiObjectNotFoundException {

		if (corner == null) {
			corner = "center";
		}
		corner = corner.toLowerCase();
		if ("br".equals(corner) || "bottomright".equals(corner)) {
			return new UiObject(uiSelector).clickBottomRight();
		} else if (("tl".equals(corner) || "topleft".equals(corner))) {
			return new UiObject(uiSelector).clickTopLeft();
		} else if ("c".equals(corner) || "center".equals(corner)) {
			return new UiObject(uiSelector).click();
		}
		return false;
	}

	@Override
	public boolean click(int x, int y) {
		return UiDevice.getInstance().click(x, y);
	}

	@Override
	public boolean longClick(int x, int y) {
		return UiDevice.getInstance().swipe(x, y, x , y , 300);
	}

	@Override
	public boolean longClick(UiSelector uiSelector)
			throws UiObjectNotFoundException {
		//return new UiObject(uiSelector).longClick(); // this method doesn't work
		UiObject obj = new UiObject(uiSelector);
		return longClick(obj.getBounds().centerX(), obj.getBounds().centerY());
	}

	@Override
	public boolean longClick(UiSelector uiSelector, String corner)
			throws UiObjectNotFoundException {

		if (corner == null) {
			corner = "center";
		}
		corner = corner.toLowerCase();
		if ("br".equals(corner) || "bottomright".equals(corner)) {
			return new UiObject(uiSelector).longClickBottomRight();
		} else if (("tl".equals(corner) || "topleft".equals(corner))) {
			return new UiObject(uiSelector).longClickTopLeft();
		} else if ("c".equals(corner) || "center".equals(corner)) {
			return new UiObject(uiSelector).longClick();
		}
		return false;
	}

	@Override
	public boolean openNotification() {
		return UiDevice.getInstance().openNotification();
	}

	@Override
	public boolean openQuickSettings() {
		return UiDevice.getInstance().openQuickSettings();
	}

	@Override
	public boolean pressKeyCode(int keyCode) {
		return UiDevice.getInstance().pressKeyCode(keyCode);
	}

	@Override
	public boolean pressKeyCode(int keyCode, int metaState) {
		return UiDevice.getInstance().pressKeyCode(keyCode, metaState);
	}

	@Override
	public boolean pressKey(String key) throws RemoteException {
		boolean result;
		key = key.toLowerCase();
		if ("home".equals(key))
			result = UiDevice.getInstance().pressHome();
		else if ("back".equals(key))
			result = UiDevice.getInstance().pressBack();
		else if ("left".equals(key))
			result = UiDevice.getInstance().pressDPadLeft();
		else if ("right".equals(key))
			result = UiDevice.getInstance().pressDPadRight();
		else if ("up".equals(key))
			result = UiDevice.getInstance().pressDPadUp();
		else if ("down".equals(key))
			result = UiDevice.getInstance().pressDPadDown();
		else if ("center".equals(key))
			result = UiDevice.getInstance().pressDPadCenter();
		else if ("menu".equals(key))
			result = UiDevice.getInstance().pressMenu();
		else if ("search".equals(key))
			result = UiDevice.getInstance().pressSearch();
		else if ("enter".equals(key))
			result = UiDevice.getInstance().pressEnter();
		else if ("delete".equals(key) || "del".equals(key))
			result = UiDevice.getInstance().pressDelete();
		else if ("recent".equals(key))
			result = UiDevice.getInstance().pressRecentApps();
		else if ("volume_up".equals(key))
			result = UiDevice.getInstance().pressKeyCode(
					KeyEvent.KEYCODE_VOLUME_UP);
		else if ("volume_down".equals(key))
			result = UiDevice.getInstance().pressKeyCode(
					KeyEvent.KEYCODE_VOLUME_DOWN);
		else if ("volume_mute".equals(key))
			result = UiDevice.getInstance().pressKeyCode(
					KeyEvent.KEYCODE_VOLUME_MUTE);
		else if ("camera".equals(key))
			result = UiDevice.getInstance().pressKeyCode(
					KeyEvent.KEYCODE_CAMERA);
		else
			result = "power".equals(key)
					&& UiDevice.getInstance().pressKeyCode(
							KeyEvent.KEYCODE_POWER);

		return result;
	}

	@Override
	public int executeCmd(String cmdString) {

		Process proc = null;
		int returncode = -1;
		try {
			proc = Runtime.getRuntime().exec(cmdString);
			// the following is to prevent subprocess locked
			InputStream stderr = proc.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)  {
				Log.v(Constant.LOG_TAG, line);
			}
			synchronized (proc) {
				try {
					proc.wait(1000);
				} catch (Exception e) {
				}
			}
			returncode = proc.exitValue();
			if (proc != null) {
				proc.destroy();
			}
		} catch (Exception e) {
			Log.e(Constant.LOG_TAG,
					"Could not save because we were interrupted");
			if (proc != null) {
				proc.destroy();
			}
			e.printStackTrace();
		}
		return returncode;
	}

	@Override
	public boolean drag(int startX, int startY, int endX, int endY, int steps) {
		return UiDevice.getInstance().drag(startX, startY, endX, endY, steps);
	}

	@Override
	public boolean dragTo(UiSelector from, UiSelector to, int steps)
			throws UiObjectNotFoundException {
		return new UiObject(from).dragTo(new UiObject(to), steps);
	}

	@Override
	public boolean swipe(int startX, int startY, int endX, int endY, int steps) {
		return UiDevice.getInstance().swipe(startX, startY, endX, endY, steps);
	}

	@Override
	public boolean swipe(UiSelector uiSelector, String dir, int steps)
			throws UiObjectNotFoundException {
		dir = dir.toLowerCase();
		boolean result = false;
		if ("u".equals(dir) || "up".equals(dir)) {
			result = new UiObject(uiSelector).swipeUp(steps);
		} else if ("d".equals(dir) || "down".equals(dir)) {
			result = new UiObject(uiSelector).swipeDown(steps);
		} else if ("l".equals(dir) || "left".equals(dir)) {
			result = new UiObject(uiSelector).swipeLeft(steps);
		} else if ("r".equals(dir) || "right".equals(dir)) {
			result = new UiObject(uiSelector).swipeRight(steps);
		}

		return result;
	}

	@Override
	public String dumpWindow(String fileName, boolean compressed) {
		if (Build.VERSION.SDK_INT >= 18)
			UiDevice.getInstance().setCompressedLayoutHeirarchy(compressed);
		File parent = new File(Environment.getDataDirectory(), "local/tmp"); // Environment.getDataDirectory()
																				// return
																				// /data/local/tmp
																				// in
																				// android
																				// 4.3
																				// but
																				// not
																				// expected
																				// /data
		if (!parent.exists())
			parent.mkdirs();
		boolean return_value = false;
		if (fileName == null || fileName == "") {
			fileName = "dump.xml";
			return_value = true;
		}
		File dumpFile = new File(parent, fileName).getAbsoluteFile();
		UiDevice.getInstance().dumpWindowHierarchy(fileName);
		File f = new File(STORAGE_PATH, fileName); // It should be this one, but
													// in Android4.3, it is
													// "/data/local/tmp/local/tmp"......
		if (!f.exists())
			f = dumpFile;
		if (f.exists()) {
			if (return_value) {
				BufferedReader reader = null;
				try {
					StringBuilder sb = new StringBuilder();
					reader = new BufferedReader(new FileReader(f));
					char[] buffer = new char[4096];
					int len = 0;
					while ((len = reader.read(buffer)) != -1) {
						sb.append(new String(buffer, 0, len));
					}
					reader.close();
					reader = null;
					return sb.toString();
				} catch (IOException e) {
					Log.e(Constant.LOG_TAG, e.toString());
				} finally {
					if (reader != null) {
						try {
							reader.close();
							reader = null;
						} catch (IOException e1) {
						}
					}
				}
				return null;
			} else
				return f.getAbsolutePath();
		} else
			return null;
	}

	@Override
	public String screenshot(String filename, float scale, int quality) {
		File f = new File(STORAGE_PATH, filename);
		UiDevice.getInstance().takeScreenshot(f, scale, quality);
		if (f.exists())
			return f.getAbsolutePath();
		return null;
	}

	@Override
	public void freezeRotation(boolean freeze) throws RemoteException {
		if (freeze)
			UiDevice.getInstance().freezeRotation();
		else
			UiDevice.getInstance().unfreezeRotation();

	}

	@Override
	public void setOrientation(String dir) throws RemoteException {
		dir = dir.toLowerCase();
		if ("left".equals(dir) || "l".equals(dir))
			UiDevice.getInstance().setOrientationLeft();
		else if ("right".equals(dir) || "r".equals(dir))
			UiDevice.getInstance().setOrientationRight();
		else if ("natural".equals(dir) || "n".equals(dir))
			UiDevice.getInstance().setOrientationNatural();
	}

	@Override
	public void wakeUp() throws RemoteException {
		UiDevice.getInstance().wakeUp();
	}

	@Override
	public void sleep() throws RemoteException {
		UiDevice.getInstance().sleep();
	}

	@Override
	public boolean isScreenOn() throws RemoteException {
		return UiDevice.getInstance().isScreenOn();
	}

	@Override
	public void waitForIdle(long timeout) {
		UiDevice.getInstance().waitForIdle(timeout);
	}

	@Override
	public boolean waitForWindowUpdate(String packageName, long timeout) {
		return UiDevice.getInstance().waitForWindowUpdate(packageName, timeout);
	}

	@Override
	public boolean waitForExists(UiSelector uiSelector, long timeout) {
		return new UiObject(uiSelector).waitForExists(timeout);
	}

	@Override
	public boolean waitUntilGone(UiSelector uiSelector, long timeout) {
		return new UiObject(uiSelector).waitUntilGone(timeout);
	}


	/**
	 * Walk around to avoid backforward compatibility issue on uiautomator
	 * between api level 16/17.
	 */
	static void setAsVerticalList(UiScrollable obj) {
		@SuppressWarnings("rawtypes")
		Class noparams[] = {};
		Object nullparmas[] = {};
		try {
			Class.forName("com.android.uiautomator.core.UiScrollable")
					.getDeclaredMethod("setAsVerticalList", noparams)
					.invoke(obj, nullparmas);
		} catch (NoSuchMethodException e) {
			Log.d(Constant.LOG_TAG, e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.d(Constant.LOG_TAG, e.getMessage());
		} catch (InvocationTargetException e) {
			Log.d(Constant.LOG_TAG, e.getMessage());
		} catch (IllegalAccessException e) {
			Log.d(Constant.LOG_TAG, e.getMessage());
		}
	}

	/**
	 * Walk around to avoid backforward compatibility issue on uiautomator
	 * between api level 16/17.
	 */
	static void setAsHorizontalList(UiScrollable obj) {
		@SuppressWarnings("rawtypes")
		Class noparams[] = {};
		Object nullparmas[] = {};
		try {
			Class.forName("com.android.uiautomator.core.UiScrollable")
					.getDeclaredMethod("setAsHorizontalList", noparams)
					.invoke(obj, nullparmas);
		} catch (NoSuchMethodException e) {
			Log.d(Constant.LOG_TAG, e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.d(Constant.LOG_TAG, e.getMessage());
		} catch (InvocationTargetException e) {
			Log.d(Constant.LOG_TAG, e.getMessage());
		} catch (IllegalAccessException e) {
			Log.d(Constant.LOG_TAG, e.getMessage());
		}
	}

	@Override
	public boolean flingBackward(UiSelector obj, boolean isVertical)
			throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj);
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.flingBackward();
	}

	@Override
	public boolean flingForward(UiSelector obj, boolean isVertical)
			throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj);
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.flingForward();
	}

	@Override
	public boolean flingToBeginning(UiSelector obj, boolean isVertical,
			int maxSwipes) throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj);
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.flingToBeginning(maxSwipes);
	}

	@Override
	public boolean flingToEnd(UiSelector obj, boolean isVertical, int maxSwipes)
			throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj);
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.flingToEnd(maxSwipes);
	}

	@Override
	public boolean gesture(UiSelector obj, Point startPoint1,
			Point startPoint2, Point endPoint1, Point endPoint2, int steps) {
		return new UiObject(obj).performTwoPointerGesture(startPoint1,
				startPoint2, endPoint1, endPoint2, steps);
	}

	@Override
	public boolean scrollTo(UiSelector fromUiSelector, UiSelector toUiSelector,
			boolean isVertical) throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(fromUiSelector);
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.scrollIntoView(toUiSelector);

	}

	@Override
	public boolean scrollBackward(UiSelector obj, boolean isVertical, int steps)
			throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj);
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.scrollBackward(steps);
	}

	@Override
	public boolean scrollForward(UiSelector obj, boolean isVertical, int steps)
			throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj);
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.scrollForward(steps);
	}

	@Override
	public boolean scrollToBeginning(UiSelector obj, boolean isVertical,
			int maxSwipes, int steps) throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj);
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.scrollToBeginning(maxSwipes, steps);
	}

	@Override
	public boolean scrollToEnd(UiSelector obj, boolean isVertical,
			int maxSwipes, int steps) throws UiObjectNotFoundException {
		UiScrollable scrollable = new UiScrollable(obj);
		if (isVertical)
			setAsVerticalList(scrollable);
		else
			setAsHorizontalList(scrollable);
		return scrollable.scrollToEnd(maxSwipes, steps);
	}

	@Override
	public String getLastTraversedText() {
		return UiDevice.getInstance().getLastTraversedText();
	}

	@Override
	public void clearLastTraversedText() {
		UiDevice.getInstance().clearLastTraversedText();
	}

	@Override
	public boolean hasWatcherTriggered(String watcherName) {
		return UiDevice.getInstance().hasWatcherTriggered(watcherName);
	}

	@Override
	public boolean hasAnyWatcherTriggered() {
		return UiDevice.getInstance().hasAnyWatcherTriggered();
	}

	@Override
	public void registerClickUiObjectWatcher(String name,
			UiSelector[] conditions, UiSelector target) {
		synchronized (watchers) {
			if (watchers.contains(name)) {
				UiDevice.getInstance().removeWatcher(name);
				watchers.remove(name);
			}

			UiSelector[] selectors = new UiSelector[conditions.length];
			for (int i = 0; i < conditions.length; i++) {
				selectors[i] = conditions[i];
			}
			UiDevice.getInstance().registerWatcher(name,
					new ClickUiObjectWatcher(selectors, target));
			watchers.add(name);
		}

	}

	@Override
	public void registerPressKeyskWatcher(String name, UiSelector[] conditions,
			String[] keys) {
		synchronized (watchers) {
			if (watchers.contains(name)) {
				UiDevice.getInstance().removeWatcher(name);
				watchers.remove(name);
			}

			UiSelector[] selectors = new UiSelector[conditions.length];
			for (int i = 0; i < conditions.length; i++) {
				selectors[i] = conditions[i];
			}
			UiDevice.getInstance().registerWatcher(name,
					new PressKeysWatcher(selectors, keys));
			watchers.add(name);
		}
	}

	@Override
	public void removeWatcher(String name) {
		synchronized (watchers) {
			if (watchers.contains(name)) {
				UiDevice.getInstance().removeWatcher(name);
				watchers.remove(name);
			}
		}
	}

	@Override
	public void resetWatcherTriggers() {
		UiDevice.getInstance().resetWatcherTriggers();
	}

	@Override
	public void runWatchers() {
		UiDevice.getInstance().runWatchers();
	}

	@Override
	public String[] getWatchers() {
		synchronized (watchers) {
			return watchers.toArray(new String[watchers.size()]);
		}
	}

	@Override
	public String getText(UiSelector uiSelector)
			throws UiObjectNotFoundException {
		return new UiObject(uiSelector).getText();
	}

	@Override
	public String getText(String resourceId) throws UiObjectNotFoundException {
		UiObject obj = new UiObject(new UiSelector().resourceId(resourceId));
		return obj.getText();
	}

	@Override
	public void clearTextField(UiSelector obj) throws UiObjectNotFoundException {
		new UiObject(obj).clearTextField();
	}

	@Override
	public boolean clickAndWaitForNewWindow(UiSelector uiSelector, long timeout)
			throws UiObjectNotFoundException {
		return new UiObject(uiSelector).clickAndWaitForNewWindow(timeout);
	}

	@Override
	public boolean pinchIn(UiSelector uiSelector, int percent, int steps)
			throws UiObjectNotFoundException {
		return new UiObject(uiSelector).pinchIn(percent, steps);
	}

	@Override
	public boolean pinchOut(UiSelector uiSelector, int percent, int steps)
			throws UiObjectNotFoundException {
		return new UiObject(uiSelector).pinchOut(percent, steps);
	}

	@Override
	public UiObject childByText(UiSelector collection, UiSelector child,
			String text) throws UiObjectNotFoundException {
		UiObject obj;
		if (exists(collection) && new UiObject(collection).isScrollable()) {
			obj = new UiScrollable(collection).getChildByText(child, text);
		} else {
			obj = new UiCollection(collection).getChildByText(child, text);
		}
		return obj;
	}

	@Override
	public UiObject childByText(UiSelector collection, UiSelector child,
			String text, boolean allowScrollSearch)
			throws UiObjectNotFoundException {
		UiObject obj = new UiScrollable(collection).getChildByText(child, text,
				allowScrollSearch);
		return obj;
	}

	@Override
	public UiObject childByDescription(UiSelector collection, UiSelector child,
			String text) throws UiObjectNotFoundException {
		UiObject obj;
		if (exists(collection) && new UiObject(collection).isScrollable()) {
			obj = new UiScrollable(collection).getChildByDescription(child,
					text);
		} else {
			obj = new UiCollection(collection).getChildByDescription(child,
					text);
		}
		return obj;
	}

	@Override
	public UiObject childByDescription(UiSelector collection, UiSelector child,
			String text, boolean allowScrollSearch)
			throws UiObjectNotFoundException {
		UiObject obj = new UiScrollable(collection).getChildByDescription(
				child, text, allowScrollSearch);
		return obj;
	}

	@Override
	public UiObject childByInstance(UiSelector collection, UiSelector child,
			int instance) throws UiObjectNotFoundException {
		UiObject obj;
		if (exists(collection) && new UiObject(collection).isScrollable()) {
			obj = new UiScrollable(collection).getChildByInstance(child,
					instance);
		} else {
			obj = new UiCollection(collection).getChildByInstance(child,
					instance);
		}
		return obj;

	}

	@Override
	public UiObject getUiObject(UiSelector selector)
			throws UiObjectNotFoundException {
		return new UiObject(selector);
	}

	@Override
	public boolean exists(UiSelector obj) {
		return new UiObject(obj).exists();
	}

	@Override
	public boolean clickIfAvailable(UiSelector uiSelector, long timeout) throws UiObjectNotFoundException{
		if (waitForExists(uiSelector, timeout)) {
			return click(uiSelector);
		}
		return false;
	}


	@Override
	public CmdResult runCommand(String cmd) {
		CmdResult cmdResult = new CmdResult();
		cmdResult.returnValue = 255;
		cmdResult.outPuts = new ArrayList<String>();
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd);
			InputStream fis = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
				cmdResult.outPuts.add(line);
			cmdResult.returnValue = proc.waitFor();
			if (proc!=null) {
				proc.destroy();
			}
			return cmdResult;
		} catch (Exception e) {
			e.printStackTrace();
			return cmdResult;
		}
	}

	@Override
	public String runCmdAndVerify(String cmd, String strToVerify) {
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.indexOf(strToVerify) != -1)
					return line;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public boolean openApplication(String pkgName) {
		String cmd = "am start -a android.intent.action.MAIN  -c android.intent.category.LAUNCHER " + pkgName;
		return executeCmd(cmd) == 0;
	}


	@Override
	public UiObject waitObject(UiSelector uiSelector, long timeout) {
		UiObject object = new UiObject(uiSelector);
		if (object.waitForExists(timeout)) {
			return object;
		}
		return null;
	}


	@Override
	public boolean longClick(int x, int y, int time) {
		return UiDevice.getInstance().swipe(x, y, x , y , time/5);
	}


	private Bitmap getScreenShotBitmap() throws FileNotFoundException {
		File file = new File("/sdcard/screenshot.png");
		UiDevice.getInstance().takeScreenshot(file, 1.0f, 100);
		SystemClock.sleep(3000);
		FileInputStream fis = new FileInputStream("/sdcard/screenshot.png");
		Bitmap bitmap  = BitmapFactory.decodeStream(fis);
		return bitmap;
	}

	private int[] getPixels(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int[] pixels = new int[width * height];
        bm.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

	@Override
	public long getPixelsChecksum(int x, int y) throws Exception {
		Bitmap bitmap = getScreenShotBitmap();
		int width = bitmap.getWidth();
        int[] pixels = this.getPixels(bitmap);
        int index = y * width + x;
        long sum = 0;
        int from = index - DEVIATION - width;
        int to = index + DEVIATION - width;
        for (int i = from; i <= to; i++) {
            sum += pixels[i];
        }
        from = from + width;
        to = to + width;
        for (int i = from; i <= to; i++) {
            sum += pixels[i];
        }
        from = from + width;
        to = to + width;
        for (int i = from; i <= to; i++) {
            sum += pixels[i];
        }
        return sum;
	}

	@Override
	public int[] getCoordinatesWithPixelsChecksum(long checkSum) throws Exception {
		Bitmap bm = this.getScreenShotBitmap();
        int width = bm.getWidth();
        int height = bm.getHeight();
        int[] pixels = this.getPixels(bm);
        int x = 0;
        int y = 0;
        int index = DEVIATION + width;
        int sum = 0;
        for (y = 1; y < height - 1; y++) {
            for (x = DEVIATION; x < width - DEVIATION; x++) {
                sum = 0;
                int from = index - DEVIATION - width;
                int to = index + DEVIATION - width;
                for (int i = from; i <= to; i++) {
                    sum += pixels[i];
                }
                from = from + width;
                to = to + width;
                for (int i = from; i <= to; i++) {
                    sum += pixels[i];
                }
                from = from + width;
                to = to + width;
                for (int i = from; i <= to; i++) {
                    sum += pixels[i];
                }
                if (sum == checkSum) {
                    Log.d(Constant.LOG_TAG, "FIND " + pixels + " x=" + x + ",y=" + y);
                    return new int[] {
                            x, y
                    };
                }
                if (x == width - DEVIATION - 1) {
                    index = index + 2 * DEVIATION;
                }
                index++;
            }
        }
        return new int[] {
                -1, -1
        };
	}
}
