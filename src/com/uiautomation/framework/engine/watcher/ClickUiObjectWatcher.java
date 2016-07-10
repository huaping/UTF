package com.uiautomation.framework.engine.watcher;

import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.uiautomation.framework.utils.Constant;

public class ClickUiObjectWatcher extends SelectorWatcher {

	private UiSelector target = null;

    public ClickUiObjectWatcher(UiSelector[] conditions, UiSelector target) {
        super(conditions);
        this.target = target;
    }

	@Override
	public void action() {
		Log.d(Constant.LOG_TAG,"ClickUiObjectWatcher triggered!");
        if (target != null) {
            try {
                new UiObject(target).click();
            } catch (UiObjectNotFoundException e) {
                Log.d(Constant.LOG_TAG, e.getMessage());
            }
        }
    }

}
