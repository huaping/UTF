package com.uiautomation.framework;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Point;
import android.os.RemoteException;
import android.util.Log;

import com.android.uiautomator.core.Configurator;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.uiautomation.framework.engine.DeviceInfo;
import com.uiautomation.framework.engine.ITestEngine;
import com.uiautomation.framework.engine.TestEngine;
import com.uiautomation.framework.utils.Constant;

public class UiAutoTestCase extends UiAutomatorTestCase implements ITestEngine{

	private ITestEngine te;
	
	private boolean mDebug = false;
	
	private String mTag;
	
	public UiAutoTestCase() {
		this(Configurator.getInstance().getWaitForSelectorTimeout(),
				Constant.LOG_TAG, false);
	}

	public UiAutoTestCase(long waitForTimeout, String tag, boolean debug){
		te = new TestEngine(waitForTimeout);
		this.mTag = tag;
		this.mDebug = debug;
	}
	
	public void logMessage(String log) {
        if(mDebug){
            System.out.println(getName() + ": " + log);
        }
        Log.d(mTag, getName() + ": " + log);
    }
	
	@Override
    protected void setUp() throws Exception {
        logMessage("Case starting...");
        super.setUp();
    }
	
	private String getCurrentTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime.toString();
	}
	/**
     * Run in end of each test case, this case is for capturing screenshot when running parameter is "1"
     * Example: {@code adb shell uiautomator xxxx.jar -c XXXXTest -e screenshot 1}
     */
    @Override
    protected void tearDown() throws Exception {
        logMessage("Case end");
        
        String fileName = getName() + "_" + getCurrentTime() + ".png";
        if (getParams().getString("screenshot") != null)
            if (getParams().getString("screenshot").contentEquals("1")) {
                screenshot(fileName, 0.5f, 80);
            }
        super.tearDown();
    }
    
    /**
     * Overide of runTest of testcase, to test try times a gain when failure
     * setUp and tearDown will don't run again after failure, so openApp should be in test
     * This method also catch screenshot when failure.
     * -e retrytimes 1
     * screenshot path is /data/local/tmp/TestClassName/testxxx.png
     */
    @Override
    protected void runTest() throws Throwable {
    	String testMethodName = getClass().getName() + "." + getName();
    	Log.v(mTag, "Begin to run " + testMethodName + ".");
        int retryTimes = 0;
        boolean firstTime = true;
        
		String fileName = "/data/local/tmp/" + Constant.LOGS_PATH
				+ File.separator + getName() + "_" + getCurrentTime();
		String logFile = fileName + "_logcat.txt";
		String pngFile = fileName + ".png";
        Process process = null;
        File f = new File(logFile);
        try {
            if (!f.getParentFile().exists()) {
            	process = Runtime.getRuntime().exec("mkdir -p "+
                        f.getParentFile().getAbsolutePath());
                       process.waitFor();
                if (process != null) {
					process.destroy();
				}
            }
        } catch(Exception e) {
            Log.e(mTag, "Unkown Exception, filename: " + logFile);
            e.printStackTrace();
        }
        
        if(getParams().getString("logcat") == "true"){
        	final String command=String.format("logcat -d -v time -f %s *:V -s", logFile);
            Runtime.getRuntime().exec(command).waitFor();
        }

        if(getParams().getString("retrytimes") != null){
            retryTimes = Integer.parseInt(getParams().getString("retrytimes"));
        }

        while(retryTimes >= 0){
            try{
                if(!firstTime){
                    for(int i = 0; i < 5; i++) pressKey("back");
                    setUp();
                }
                firstTime = false;
                super.runTest();
                break;
            }catch (Throwable e){
                if(retryTimes >= 1){
                    retryTimes--;
                    continue;
                } else {
                    System.out.println("runTest() throws an exception");
                    UiDevice.getInstance().takeScreenshot(new File(pngFile), 0.5f, 80);
                    throw e;
                }
            }
        }
        Log.v(mTag, testMethodName + " run finished.");
    }
    
    
	@Override
	/**
     * Get the device info.
     * @return device info.
     */
	public DeviceInfo deviceInfo() {
		return te.deviceInfo();
	}

	/**
	 * Open Application with package name and class
	 * @param pkg Package name of the target
	 * @param cls Activity class name of the target
	 */
	@Override
	public boolean openApplication(String pkg, String cls) {
		return te.openApplication(pkg, cls);
	}

	/**
     * Sets the text in an editable field, after clearing the field's content. The UiSelector selector of this object must reference a UI element that is editable. When you call this method, the method first simulates a click() on editable field to set focus. The method then clears the field's contents and injects your specified text into the field. If you want to capture the original contents of the field, call getText() first. You can then modify the text and use this method to update the field.
     *
     * @param text  the text to be set.
     * @param uiSelector the target uiselector
     * @return true if operation is successful
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean enterText(String text, UiSelector uiSelector)
			throws UiObjectNotFoundException {
		return te.enterText(text, uiSelector);
	}

	/**
     * Performs a click at text.
     *
     * @param text the target text object.
     * @return true id successful else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean clickText(String text) throws UiObjectNotFoundException {
		return te.clickText(text);
	}

	/**
     * Performs a click at resource id matches rex
     *
     * @param id of the target resource id .
     * @return true id successful else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean clickResourceIdMatches(String id)
			throws UiObjectNotFoundException {
		return te.clickResourceIdMatches(id);
	}
	
	/**
     * Performs a click at resource id 
     * 
     * @param id of the target resource id .
     * @return true id successful else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean clickResourceId(String id) throws UiObjectNotFoundException {
		return te.clickResourceId(id);
	}
	
	
	/**
     * Performs a click at Class name 
     *
     * @param clzName of the target
     * @param instance of the target on the screen
     * @return true id successful else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean clickClass(String clzName, int instance)
			throws UiObjectNotFoundException {
		return te.clickClass(clzName, instance);
	}

	/**
     * Performs a click at description
     *
     * @param discription of the target description .
     * @return true id successful else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean clickDescription(String discription)
			throws UiObjectNotFoundException {
		return te.clickDescription(discription);
	}

	/**
     * Performs a click at text contains
     *
     * @param text is part of the target 
     * @return true id successful else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean clickTextContains(String text)
			throws UiObjectNotFoundException {
		return te.clickTextContains(text);
	}

	/**
     * Performs a click at the center of the visible bounds of the UI element represented by this UiObject.
     *
     * @param uiSelector the target ui selector.
     * @return true id successful else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean click(UiSelector uiSelector)
			throws UiObjectNotFoundException {
		return te.click(uiSelector);
	}

	/**
     * Performs a click at the center of the visible bounds of the UI element represented by this UiObject.
     *
     * @param obj the target ui object.
     * @return true id successful else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean click(UiObject obj) throws UiObjectNotFoundException {
		return te.click(obj);
	}
	
	/**
     * Long clicks bottom and right corner of the UI element
     *
     * @param uiSelector    the target ui selector.
     * @param corner "br"/"bottomright" means BottomRight, "tl"/"topleft" means TopLeft, "center" means Center.
     * @return true if operation was successful
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean click(UiSelector uiSelector, String corner)
			throws UiObjectNotFoundException {
		return te.click(uiSelector, corner);
	}

    /**
     * Perform a click at arbitrary coordinates specified by the user.
     *
     * @param x coordinate
     * @param y coordinate
     * @return true if the click succeeded else false
     */
	@Override
	public boolean click(int x, int y) {
		return te.click(x, y);
	}

    /**
     * Perform a long click at arbitrary coordinates specified by the user.
     *
     * @param x coordinate
     * @param y coordinate
     * @return true if the click succeeded else false
     */
	@Override
	public boolean longClick(int x, int y) {
		return te.longClick(x, y);
	}


    /**
     * Long clicks the center of the visible bounds of the UI element
     *
     * @param uiSelector the target ui selector.
     * @return true if operation was successful
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean longClick(UiSelector uiSelector)
			throws UiObjectNotFoundException {
		return te.longClick(uiSelector);
	}

	/**
     * Long clicks bottom and right corner of the UI element
     *
     * @param uiSelector    the target ui selector.
     * @param corner "br"/"bottomright" means BottomRight, "tl"/"topleft" means TopLeft, "center" means Center.
     * @return true if operation was successful
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean longClick(UiSelector uiSelector, String corner)
			throws UiObjectNotFoundException {
		return te.longClick(uiSelector, corner);
	}

	/**
     * Opens the notification shade.
     *
     * @return true if successful, else return false
     * @throws com.github.uiautomatorstub.NotImplementedException
     *
     */
	@Override
	public boolean openNotification() {
		return te.openNotification();
	}

	  /**
     * Opens the Quick Settings shade.
     *
     * @return true if successful, else return false
     * @throws com.github.uiautomatorstub.NotImplementedException
     *
     */
	@Override
	public boolean openQuickSettings() {
		return te.openQuickSettings();
	}

	/**
     * Simulates a short press using a key code. See KeyEvent.
     *
     * @param keyCode the key code of the event.
     * @return true if successful, else return false
     */
	@Override
	public boolean pressKeyCode(int keyCode) {
		return te.pressKeyCode(keyCode);
	}

	/**
     * Simulates a short press using a key code. See KeyEvent.
     *
     * @param keyCode   the key code of the event.
     * @param metaState an integer in which each bit set to 1 represents a pressed meta key
     * @return true if successful, else return false
     */
	@Override
	public boolean pressKeyCode(int keyCode, int metaState) {
		return te.pressKeyCode(keyCode, metaState);
	}


    /**
     * Simulates a short press using key name.
     *
     * @param key possible key name is home, back, left, right, up, down, center, menu, search, enter, delete(or del), recent(recent apps), volume_up, volume_down, volume_mute, camera, power
     * @return true if successful, else return false
     * @throws android.os.RemoteException
     */
	@Override
	public boolean pressKey(String key) throws RemoteException {
		return te.pressKey(key);
	}

	/**
	 * Execute command on shell
	 * @param cmdString command to be executed
	 * @return 0 is successful, other is failed
	 */
	@Override
	public int executeCmd(String cmdString) {
		return te.executeCmd(cmdString);
	}

	 /**
     * Performs a swipe from one coordinate to another coordinate. You can control the smoothness and speed of the swipe by specifying the number of steps. Each step execution is throttled to 5 milliseconds per step, so for a 100 steps, the swipe will take around 0.5 seconds to complete.
     *
     * @param startX X-axis value for the starting coordinate
     * @param startY Y-axis value for the starting coordinate
     * @param endX   X-axis value for the ending coordinate
     * @param endY   Y-axis value for the ending coordinate
     * @param steps  is the number of steps for the swipe action
     * @return true if swipe is performed, false if the operation fails or the coordinates are invalid
     */
	@Override
	public boolean drag(int startX, int startY, int endX, int endY, int steps) {
		return te.drag(startX, startY, endX, endY, steps);
	}



    /**
     * Drags this object to a destination UiObject. The number of steps specified in your input parameter can influence the drag speed, and varying speeds may impact the results. Consider evaluating different speeds when using this method in your tests.
     *
     * @param from     the ui selector to be dragged.
     * @param to       the ui selector to be dragged to.
     * @param steps   usually 40 steps. You can increase or decrease the steps to change the speed.
     * @return true if successful
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     *
     */
	@Override
	public boolean dragTo(UiSelector from, UiSelector to, int steps)
			throws UiObjectNotFoundException {
		return te.dragTo(from, to, steps);
	}
	
	 /**
     * Performs a swipe from one coordinate to another using the number of steps to determine smoothness and speed. Each step execution is throttled to 5ms per step. So for a 100 steps, the swipe will take about 1/2 second to complete.
     *
     * @param startX X-axis value for the starting coordinate
     * @param startY Y-axis value for the starting coordinate
     * @param endX   X-axis value for the ending coordinate
     * @param endY   Y-axis value for the ending coordinate
     * @param steps  is the number of move steps sent to the system
     * @return false if the operation fails or the coordinates are invalid
     */
	@Override
	public boolean swipe(int startX, int startY, int endX, int endY, int steps) {
		return te.swipe(startX, startY, endX, endY, steps);
	}


	 /**
     * Performs the swipe up/down/left/right action on the UiObject
     *
     * @param uiSelector   the target ui selector.
     * @param dir   "u"/"up", "d"/"down", "l"/"left", "r"/"right"
     * @param steps indicates the number of injected move steps into the system. Steps are injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
     * @return true of successful
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean swipe(UiSelector uiSelector, String dir, int steps)
			throws UiObjectNotFoundException {
		return te.swipe(uiSelector, dir, steps);
	}

	/**
     * Helper method used for debugging to dump the current window's layout hierarchy. The file root location is /data/local/tmp
     * @param fileName   the filename to be stored.
     * @param compressed use compressed layout hierarchy or not using setCompressedLayoutHeirarchy method. Ignore the parameter in case the API level lt 18.
     * @return the absolute path name of dumped file.
     */
	@Override
	public String dumpWindow(String fileName, boolean compressed) {
		return te.dumpWindow(fileName, compressed);
	}

	/**
     * Take a screenshot of current window and store it as PNG The screenshot is adjusted per screen rotation
     *
     * @param fileName  where the PNG should be written to
     * @param scale     scale the screenshot down if needed; 1.0f for original size
     * @param quality   quality of the PNG compression; range: 0-100
     * @return the file name of the screenshot. null if failed.
     *
     */
	@Override
	public String screenshot(String fileName, float scale, int quality) {
		return te.screenshot(fileName, scale, quality);
	}

	/**
     * Disables the sensors and freezes the device rotation at its current rotation state, or enable it.
     *
     * @param freeze true to freeze the rotation, false to unfreeze the rotation.
     * @throws android.os.RemoteException
     */
	@Override
	public void freezeRotation(boolean freeze) throws RemoteException {
		te.freezeRotation(freeze);		
	}

	 /**
     * Simulates orienting the device to the left/right/natural and also freezes rotation by disabling the sensors.
     *
     * @param oritationString Left or l, Right or r, Natural or n, case insensitive
     * @throws android.os.RemoteException
     *
     */
	@Override
	public void setOrientation(String oritationString) throws RemoteException {
		te.setOrientation(oritationString);
	}

	/**
     * This method simulates pressing the power button if the screen is OFF else it does nothing if the screen is already ON. If the screen was OFF and it just got turned ON, this method will insert a 500ms delay to allow the device time to wake up and accept input.
     *
     * @throws android.os.RemoteException
     */
	@Override
	public void wakeUp() throws RemoteException {
		te.wakeUp();
	}

    /**
     * This method simply presses the power button if the screen is ON else it does nothing if the screen is already OFF.
     *
     * @throws android.os.RemoteException
     */
	@Override
	public void sleep() throws RemoteException {
		te.sleep();
	}

	/**
     * Checks the power manager if the screen is ON.
     *
     * @return true if the screen is ON else false
     * @throws android.os.RemoteException
     */
	@Override
	public boolean isScreenOn() throws RemoteException {
		return te.isScreenOn();
	}

	/**
     * Waits for the current application to idle.
     *
     * @param timeout in milliseconds
     */
	@Override
	public void waitForIdle(long timeout) {
		te.waitForIdle(timeout);
	}

    /**
     * Waits for a window content update event to occur. If a package name for the window is specified, but the current window does not have the same package name, the function returns immediately.
     *
     * @param packageName the specified window package name (can be null). If null, a window update from any front-end window will end the wait.
     * @param timeout     the timeout for the wait
     * @return true if a window update occurred, false if timeout has elapsed or if the current window does not have the specified package name
     */
	@Override
	public boolean waitForWindowUpdate(String packageName, long timeout) {
		return te.waitForWindowUpdate(packageName, timeout);
	}
	
	/**
     * Waits a specified length of time for a view to become visible. This method waits until the view becomes visible on the display, or until the timeout has elapsed. You can use this method in situations where the content that you want to select is not immediately displayed.
     *
     * @param uiSelector     the target ui selector
     * @param timeout time to wait (in milliseconds)
     * @return true if the view is displayed, else false if timeout elapsed while waiting
     */
	@Override
	public boolean waitForExists(UiSelector uiSelector, long timeout) {
		return te.waitForExists(uiSelector, timeout);
	}
	
    /**
     * Waits a specified length of time for a view to become undetectable. This method waits until a view is no longer matchable, or until the timeout has elapsed. A view becomes undetectable when the UiSelector of the object is unable to find a match because the element has either changed its state or is no longer displayed. You can use this method when attempting to wait for some long operation to compete, such as downloading a large file or connecting to a remote server.
     *
     * @param obj     the target ui selector
     * @param timeout time to wait (in milliseconds)
     * @return true if the element is gone before timeout elapsed, else false if timeout elapsed but a matching element is still found.
     */
	@Override
	public boolean waitUntilGone(UiSelector obj, long timeout) {
		return te.waitUntilGone(obj, timeout);
	}

	
	/**
     * Performs a backwards fling action with the default number of fling steps (5). If the swipe direction is set to vertical, then the swipe will be performed from top to bottom. If the swipe direction is set to horizontal, then the swipes will be performed from left to right. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @return true if scrolled, and false if can't scroll anymore
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean flingBackward(UiSelector obj, boolean isVertical)
			throws UiObjectNotFoundException {
		return te.flingBackward(obj, isVertical);
	}
	 /**
     * Performs a forward fling with the default number of fling steps (5). If the swipe direction is set to vertical, then the swipes will be performed from bottom to top. If the swipe direction is set to horizontal, then the swipes will be performed from right to left. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     * @param obj        the selector of the scrollable selector
     * @param isVertical vertical or horizontal
     * @return true if scrolled, and false if can't scroll anymore
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean flingForward(UiSelector obj, boolean isVertical)
			throws UiObjectNotFoundException {
		return te.flingForward(obj, isVertical);
	}

	 /**
     * Performs a fling gesture to reach the beginning of a scrollable layout element. The beginning can be at the top-most edge in the case of vertical controls, or the left-most edge for horizontal controls. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     *
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @param maxSwipes  max swipes to achieve beginning.
     * @return true on scrolled, else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean flingToBeginning(UiSelector obj, boolean isVertical,
			int maxSwipes) throws UiObjectNotFoundException {
		return te.flingToBeginning(obj, isVertical, maxSwipes);
	}

	/**
     * Performs a fling gesture to reach the end of a scrollable layout element. The end can be at the bottom-most edge in the case of vertical controls, or the right-most edge for horizontal controls. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     *
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @param maxSwipes  max swipes to achieve end.
     * @return true on scrolled, else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean flingToEnd(UiSelector obj, boolean isVertical, int maxSwipes)
			throws UiObjectNotFoundException {
		return te.flingToEnd(obj, isVertical, maxSwipes);
	}

	/**
     * Generates a two-pointer gesture with arbitrary starting and ending points.
     *
     * @param obj         the target ui object. ??
     * @param startPoint1 start point of pointer 1
     * @param startPoint2 start point of pointer 2
     * @param endPoint1   end point of pointer 1
     * @param endPoint2   end point of pointer 2
     * @param steps       the number of steps for the gesture. Steps are injected about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
     * @return true if all touch events for this gesture are injected successfully, false otherwise
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean gesture(UiSelector obj, Point startPoint1,
			Point startPoint2, Point endPoint1, Point endPoint2, int steps) {
		return te.gesture(obj, startPoint1, startPoint2, endPoint1, endPoint2, steps);
	}

	 /**
     * Perform a scroll forward action to move through the scrollable layout element until a visible item that matches the selector is found.
     *
     * @param fromUiSelector        the selector of the scrollable object
     * @param toUiSelector  the item matches the selector to be found.
     * @param isVertical vertical or horizontal
     * @return true on scrolled, else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean scrollTo(UiSelector fromUiSelector, UiSelector toUiSelector,
			boolean isVertical) throws UiObjectNotFoundException {
		return te.scrollTo(fromUiSelector, toUiSelector, isVertical);
	}

	/**
     * Performs a backward scroll. If the swipe direction is set to vertical, then the swipes will be performed from top to bottom. If the swipe direction is set to horizontal, then the swipes will be performed from left to right. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     *
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @param steps      number of steps. Use this to control the speed of the scroll action.
     * @return true if scrolled, false if can't scroll anymore
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean scrollBackward(UiSelector obj, boolean isVertical, int steps)
			throws UiObjectNotFoundException {
		return te.scrollBackward(obj, isVertical, steps);
	}

	/**
     * Performs a forward scroll with the default number of scroll steps (55). If the swipe direction is set to vertical, then the swipes will be performed from bottom to top. If the swipe direction is set to horizontal, then the swipes will be performed from right to left. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     *
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @param steps      number of steps. Use this to control the speed of the scroll action.
     * @return true on scrolled, else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean scrollForward(UiSelector obj, boolean isVertical, int steps)
			throws UiObjectNotFoundException {
		return te.scrollForward(obj, isVertical, steps);
	}

	/**
     * Scrolls to the beginning of a scrollable layout element. The beginning can be at the top-most edge in the case of vertical controls, or the left-most edge for horizontal controls. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     *
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @param maxSwipes  max swipes to be performed.
     * @param steps      use steps to control the speed, so that it may be a scroll, or fling
     * @return true on scrolled else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean scrollToBeginning(UiSelector obj, boolean isVertical,
			int maxSwipes, int steps) throws UiObjectNotFoundException {
		return te.scrollToBeginning(obj, isVertical, maxSwipes, steps);
	}

	/**
     * Scrolls to the end of a scrollable layout element. The end can be at the bottom-most edge in the case of vertical controls, or the right-most edge for horizontal controls. Make sure to take into account devices configured with right-to-left languages like Arabic and Hebrew.
     *
     * @param obj        the selector of the scrollable object
     * @param isVertical vertical or horizontal
     * @param maxSwipes  max swipes to be performed.
     * @param steps      use steps to control the speed, so that it may be a scroll, or fling
     * @return true on scrolled, else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean scrollToEnd(UiSelector obj, boolean isVertical,
			int maxSwipes, int steps) throws UiObjectNotFoundException {
		return te.scrollToEnd(obj, isVertical, maxSwipes, steps);
	}

	/**
     * Clears the existing text contents in an editable field. The UiSelector of this object must reference a UI element that is editable. When you call this method, the method first sets focus at the start edge of the field. The method then simulates a long-press to select the existing text, and deletes the selected text. If a "Select-All" option is displayed, the method will automatically attempt to use it to ensure full text selection. Note that it is possible that not all the text in the field is selected; for example, if the text contains separators such as spaces, slashes, at symbol etc. Also, not all editable fields support the long-press functionality.
     *
     * @param uiSelector the selector of the UiObject.
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public void clearTextField(UiSelector uiSelector)
			throws UiObjectNotFoundException {
		te.clearTextField(uiSelector);
	}

	/**
     * Retrieves the text from the last UI traversal event received.
     *
     * @return the text from the last UI traversal event received.
     */
	@Override
	public String getLastTraversedText() {
		return te.getLastTraversedText();
	}

    /**
     * Clears the text from the last UI traversal event.
     */
	@Override
	public void clearLastTraversedText() {
		te.clearLastTraversedText();
	}

	/**
     * Checks if a specific registered UiWatcher has triggered. See registerWatcher(String, UiWatcher). If a UiWatcher runs and its checkForCondition() call returned true, then the UiWatcher is considered triggered. This is helpful if a watcher is detecting errors from ANR or crash dialogs and the test needs to know if a UiWatcher has been triggered.
     *
     * @param watcherName the name of registered watcher.
     * @return true if triggered else false
     */
	@Override
	public boolean hasWatcherTriggered(String watcherName) {
		return te.hasWatcherTriggered(watcherName);
	}

	/**
     * Checks if any registered UiWatcher have triggered.
     *
     * @return true if any UiWatcher have triggered else false.
     */
	@Override
	public boolean hasAnyWatcherTriggered() {
		return te.hasAnyWatcherTriggered();
	}

	/**
     * Register a ClickUiObjectWatcher
     *
     * @param name       Watcher name
     * @param conditions If all UiObject in the conditions match, the watcher should be triggered.
     * @param target     The target UiObject should be clicked if all conditions match.
     */
	@Override
	public void registerClickUiObjectWatcher(String name,
			UiSelector[] conditions, UiSelector target) {
		te.registerClickUiObjectWatcher(name, conditions, target);
	}

	/**
     * Register a PressKeysWatcher
     *
     * @param name       Watcher name
     * @param conditions If all UiObject in the conditions match, the watcher should be triggered.
     * @param keys       All keys will be pressed in sequence.
     */
	@Override
	public void registerPressKeyskWatcher(String name, UiSelector[] conditions,
			String[] keys) {
		te.registerPressKeyskWatcher(name, conditions, keys);
	}

	/**
     * Removes a previously registered UiWatcher.
     *
     * @param name Watcher name
     */
	@Override
	public void removeWatcher(String name) {
		te.removeWatcher(name);
	}

	 /**
     * Resets a UiWatcher that has been triggered. If a UiWatcher runs and its checkForCondition() call returned true, then the UiWatcher is considered triggered.
     */
	@Override
	public void resetWatcherTriggers() {
		te.resetWatcherTriggers();
	}

	/**
     * Force to run all watchers.
     */
	@Override
	public void runWatchers() {
		te.runWatchers();
	}

	/**
     * Get all registered UiWatchers
     *
     * @return UiWatcher names
     */
	@Override
	public String[] getWatchers() {
		return te.getWatchers();
	}

    /**
     * Reads the text property of the UI element
     *
     * @param uiSelector the selector of the UiObject.
     * @return text value of the current node represented by this UiObject
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public String getText(UiSelector uiSelector)
			throws UiObjectNotFoundException {
		return te.getText(uiSelector);
	}

	/**
     * Reads the text property of the resource ID object
     *
     * @param resourceId the resourceid of the UiObject.
     * @return text value of the current node represented by this UiObject
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public String getText(String resourceId) throws UiObjectNotFoundException {
		return te.getText(resourceId);
	}

	/**
     * Performs a click at the center of the visible bounds of the UI element represented by this UiObject and waits for window transitions. This method differ from click() only in that this method waits for a a new window transition as a result of the click. Some examples of a window transition:
     * - launching a new activity
     * - bringing up a pop-up menu
     * - bringing up a dialog
     *
     * @param uiSelector     the target ui object.
     * @param timeout timeout before giving up on waiting for a new window
     * @return true if the event was triggered, else false
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean clickAndWaitForNewWindow(UiSelector uiSelector, long timeout)
			throws UiObjectNotFoundException {
		return te.clickAndWaitForNewWindow(uiSelector, timeout);
	}

	 /**
     * Performs a two-pointer gesture, where each pointer moves diagonally toward the other, from the edges to the center of this UiObject .
     *
     * @param uiSelector     the target ui uiSelector.
     * @param percent percentage of the object's diagonal length for the pinch gesture
     * @param steps   the number of steps for the gesture. Steps are injected about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
     * @return true if all touch events for this gesture are injected successfully, false otherwise
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public boolean pinchIn(UiSelector uiSelector, int percent, int steps)
			throws UiObjectNotFoundException {
		return te.pinchIn(uiSelector, percent, steps);
	}

	/**
     * Performs a two-pointer gesture, where each pointer moves diagonally opposite across the other, from the center out towards the edges of the this UiObject.
     *
     * @param uiSelector     the target ui uiSelector.
     * @param percent percentage of the object's diagonal length for the pinch gesture
     * @param steps   the number of steps for the gesture. Steps are injected about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
     * @return true if all touch events for this gesture are injected successfully, false otherwise
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     */
	@Override
	public boolean pinchOut(UiSelector uiSelector, int percent, int steps)
			throws UiObjectNotFoundException {
		return te.pinchOut(uiSelector, percent, steps);
	}

	/**
     * Searches for child UI element within the constraints of this UiSelector selector. It looks for any child matching the childPattern argument that has a child UI element anywhere within its sub hierarchy that has a text attribute equal to text. The returned UiObject will point at the childPattern instance that matched the search and not at the identifying child element that matched the text attribute.
     *
     * @param collection Selector of UiCollection or UiScrollable.
     * @param text       String of the identifying child contents of of the childPattern
     * @param child      UiSelector selector of the child pattern to match and return
     * @return A string ID represent the returned UiObject.
     */
	@Override
	public UiObject childByText(UiSelector collection, UiSelector child,
			String text) throws UiObjectNotFoundException {
		return te.childByText(collection, child, text);
	}

	/**
     * Searches for child UI element within the constraints of this UiSelector selector. It looks for any child matching the childPattern argument that has a child UI element anywhere within its sub hierarchy that has a text attribute equal to text. The returned UiObject will point at the childPattern instance that matched the search and not at the identifying child element that matched the text attribute.
     *
     * @param collection Selector of UiCollection or UiScrollable.
     * @param text       String of the identifying child contents of of the childPattern
     * @param child      UiSelector selector of the child pattern to match and return
     * @param allowScrollSearch    allow scroll search
     * @return A string ID represent the returned UiObject.
     */
	@Override
	public UiObject childByText(UiSelector collection, UiSelector child,
			String text, boolean allowScrollSearch)
			throws UiObjectNotFoundException {
		return te.childByText(collection, child, text, allowScrollSearch);
	}

	 /**
     * Searches for child UI element within the constraints of this UiSelector selector. It looks for any child matching the childPattern argument that has a child UI element anywhere within its sub hierarchy that has content-description text. The returned UiObject will point at the childPattern instance that matched the search and not at the identifying child element that matched the content description.
     *
     * @param collection Selector of UiCollection or UiScrollable
     * @param child      UiSelector selector of the child pattern to match and return
     * @param text       String of the identifying child contents of of the childPattern
     * @return A string ID represent the returned UiObject.
     */
	@Override
	public UiObject childByDescription(UiSelector collection, UiSelector child,
			String text) throws UiObjectNotFoundException {
		return te.childByDescription(collection, child, text);
	}

	@Override
	public UiObject childByDescription(UiSelector collection, UiSelector child,
			String text, boolean allowScrollSearch)
			throws UiObjectNotFoundException {
		return te.childByDescription(collection, child, text, allowScrollSearch);
	}

	
	/**
     * Searches for child UI element within the constraints of this UiSelector. It looks for any child matching the childPattern argument that has a child UI element anywhere within its sub hierarchy that is at the instance specified. The operation is performed only on the visible items and no scrolling is performed in this case.
     *
     * @param collection Selector of UiCollection or UiScrollable
     * @param child      UiSelector selector of the child pattern to match and return
     * @param instance   int the desired matched instance of this childPattern
     * @return A string ID represent the returned UiObject.
     */
	@Override
	public UiObject childByInstance(UiSelector collection, UiSelector child,
			int instance) throws UiObjectNotFoundException {
		return te.childByInstance(collection, child, instance);
	}

	 /**
     * Get a new UiObject from the selector.
     *
     * @param selector Selector of the UiObject
     * @return A string ID represent the returned UiObject.
     * @throws com.android.uiautomator.core.UiObjectNotFoundException
     *
     */
	@Override
	public UiObject getUiObject(UiSelector selector)
			throws UiObjectNotFoundException {
		return te.getUiObject(selector);
	}

	/**
	 * Check UiSelector obj exists on screen in 0 second
	 * @param obj the UiSelector to check
	 * @return  true is existing
	 */
	@Override
	public boolean exists(UiSelector obj) {
		return te.exists(obj);
	}
	/**
	 * Click uiseletor if it's available in timeout
	 */
	@Override
	public boolean clickIfAvailable(UiSelector uiSelector, long timeout) throws UiObjectNotFoundException{
		return te.clickIfAvailable(uiSelector, timeout);
	}

}
