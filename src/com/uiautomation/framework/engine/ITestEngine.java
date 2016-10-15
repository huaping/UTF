package com.uiautomation.framework.engine;


import android.graphics.Point;
import android.os.RemoteException;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;

public interface ITestEngine {
	
	DeviceInfo deviceInfo();
	
	boolean openApplication(String pkg, String cls);

	boolean enterText(String text, UiSelector uiSelector) throws UiObjectNotFoundException;

	boolean clickText(String text) throws UiObjectNotFoundException;

	boolean clickResourceIdMatches(String id) throws UiObjectNotFoundException;

	boolean clickResourceId(String id) throws UiObjectNotFoundException;

	boolean clickClass(String clzName, int instance) throws UiObjectNotFoundException;

	boolean clickDescription(String discription) throws UiObjectNotFoundException;

	boolean clickTextContains(String text) throws UiObjectNotFoundException;

	boolean click(UiSelector uiSelector) throws UiObjectNotFoundException;

	boolean click(UiObject obj) throws UiObjectNotFoundException;

	boolean click(UiSelector uiSelector, String corner) throws UiObjectNotFoundException;

	boolean click(int x, int y);

	boolean longClick(int x, int y);

	boolean longClick(UiSelector uiSelector) throws UiObjectNotFoundException;

	boolean longClick(UiSelector uiSelector, String corner) throws UiObjectNotFoundException;

	boolean openNotification();

	boolean openQuickSettings();

	boolean pressKeyCode(int keyCode);
	
	boolean pressKeyCode(int keyCode, int metaState);

	boolean pressKey(String key) throws RemoteException;

	int executeCmd(String cmdString);

	boolean drag(int startX, int startY, int endX, int endY, int steps);

	boolean dragTo(UiSelector from, UiSelector to, int steps)throws UiObjectNotFoundException;


    boolean swipe(int startX, int startY, int endX, int endY, int steps);

	boolean swipe(UiSelector uiSelector, String dir, int steps) throws UiObjectNotFoundException;

	String dumpWindow(String fileName, boolean compressed);

	String screenshot(String fileName, float scale, int quality);

	void freezeRotation(boolean freeze) throws RemoteException;

	void setOrientation(String oritationString) throws RemoteException;

	void wakeUp() throws RemoteException;

	void sleep()throws RemoteException;

	boolean isScreenOn()throws RemoteException;

	void waitForIdle(long timeout);

	boolean waitForWindowUpdate(String packageName, long timeout);

	boolean waitForExists(UiSelector uiSelector, long timeout);

	boolean waitUntilGone(UiSelector obj, long timeout);


	boolean flingBackward(UiSelector obj, boolean isVertical) throws UiObjectNotFoundException;

	boolean flingForward(UiSelector obj, boolean isVertical) throws UiObjectNotFoundException;

	boolean flingToBeginning(UiSelector obj, boolean isVertical, int maxSwipes) throws UiObjectNotFoundException;

	boolean flingToEnd(UiSelector obj, boolean isVertical, int maxSwipes) throws UiObjectNotFoundException;

	boolean gesture(UiSelector obj, Point startPoint1, Point startPoint2,
			Point endPoint1, Point endPoint2, int steps);


	boolean scrollTo(UiSelector fromUiSelector, UiSelector toUiSelector,
			boolean isVertical) throws UiObjectNotFoundException;

	boolean scrollBackward(UiSelector obj, boolean isVertical, int steps) throws UiObjectNotFoundException;

	boolean scrollForward(UiSelector obj, boolean isVertical, int steps) throws UiObjectNotFoundException;

	boolean scrollToBeginning(UiSelector obj, boolean isVertical,
			int maxSwipes, int steps) throws UiObjectNotFoundException;

	boolean scrollToEnd(UiSelector obj, boolean isVertical, int maxSwipes,
			int steps) throws UiObjectNotFoundException;

	void clearTextField(UiSelector uiSelector) throws UiObjectNotFoundException;

	String getLastTraversedText();

	void clearLastTraversedText();

	boolean hasWatcherTriggered(String watcherName);

	boolean hasAnyWatcherTriggered();

	void registerClickUiObjectWatcher(String name, UiSelector[] conditions, UiSelector target);
	
	void registerPressKeyskWatcher(String name, UiSelector[] conditions, String[] keys);
	
	void removeWatcher(String name);
	void resetWatcherTriggers();
	
	void runWatchers();
	
	String[] getWatchers();
	
	String getText(UiSelector uiSelector) throws UiObjectNotFoundException;
	
	String getText(String resourceId) throws UiObjectNotFoundException;
	

	boolean clickAndWaitForNewWindow(UiSelector uiSelector, long timeout) throws UiObjectNotFoundException;

	boolean pinchIn(UiSelector uiSelector, int percent, int steps) throws UiObjectNotFoundException;

	boolean pinchOut(UiSelector uiSelector, int percent, int steps) throws UiObjectNotFoundException;

	UiObject childByText(UiSelector collection, UiSelector child, String text) throws UiObjectNotFoundException;

	UiObject childByText(UiSelector collection, UiSelector child, String text,
			boolean allowScrollSearch) throws UiObjectNotFoundException;

	UiObject childByDescription(UiSelector collection, UiSelector child,
			String text) throws UiObjectNotFoundException;

	UiObject childByDescription(UiSelector collection, UiSelector child,
			String text, boolean allowScrollSearch) throws UiObjectNotFoundException;

	UiObject childByInstance(UiSelector collection, UiSelector child,
			int instance) throws UiObjectNotFoundException;

	UiObject getUiObject(UiSelector selector) throws UiObjectNotFoundException;
	
	boolean exists(UiSelector obj);
	
	boolean clickIfAvailable(UiSelector uiSelector, long timeout) throws UiObjectNotFoundException;
	
}