package com.wang.hongbaotest;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class HongBaoService extends AccessibilityService {
    private final String TAG = getClass().getName();

    public static HongBaoService mService;

    //初始化
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Utils.toast("O(∩_∩)O~~\r\n红包锁定中...");
        mService = this;
    }

    //实现辅助功能
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
        Utils.toast("(；′⌒`)\r\n红包功能被迫中断");
        mService = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 公共方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 辅助功能是否启动
     */
    public static boolean isStart() {
        return mService != null;
    }
}