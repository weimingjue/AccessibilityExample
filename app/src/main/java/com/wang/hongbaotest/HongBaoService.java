package com.wang.hongbaotest;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

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
        List<AccessibilityNodeInfo> listBiaoQing = findViewByContentDescription("表情");
        if (!Utils.isEmptyArray(listBiaoQing)) {
            Utils.toast("找到wx的表情图标");
        }
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

    /**
     * 此方法效率相对较低,建议使用之后保存id然后根据id进行查找
     */
    public List<AccessibilityNodeInfo> findViewByContentDescription(String contentDescription) {
        ArrayList<AccessibilityNodeInfo> list = new ArrayList<>();
        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
        if (rootInfo == null) return list;
        findViewByContentDescription(list, rootInfo, contentDescription);
        rootInfo.recycle();
        return list;
    }

    /**
     * 此方法效率相对较低,建议使用之后保存id然后根据id进行查找
     */
    public static void findViewByContentDescription(List<AccessibilityNodeInfo> list, AccessibilityNodeInfo parent, String contentDescription) {
        if (parent == null) return;
        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) continue;
            CharSequence cd = child.getContentDescription();
            if (cd != null && contentDescription.equals(cd.toString())) {
                list.add(child);
            } else {
                findViewByContentDescription(list, child, contentDescription);
                child.recycle();
            }
        }
    }
}