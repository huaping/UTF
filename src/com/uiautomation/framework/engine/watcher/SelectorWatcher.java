package com.uiautomation.framework.engine.watcher;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.core.UiWatcher;

public abstract class SelectorWatcher implements UiWatcher {

	private UiSelector[] conditions = null;

	public SelectorWatcher(UiSelector[] conditions) {
		this.conditions = conditions;
	}

	@Override
	public boolean checkForCondition() {
		for (UiSelector s : conditions) {
			UiObject obj = new UiObject(s);
			if (!obj.exists())
				return false;
		}
		action();
		return true;
	}

	public abstract void action();
}
