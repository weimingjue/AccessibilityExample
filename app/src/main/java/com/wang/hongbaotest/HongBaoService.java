package com.wang.hongbaotest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
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
        AccessibilityNodeInfo biaoQingInfo = findFirst(AbstractTF.newContentDescription("表情", true));
        if (biaoQingInfo != null) {
            Utils.toast("找到wx的表情图标");//第一次运行可能会吐不出来
            Log.e(TAG, "onAccessibilityEvent: 找到wx的表情图标");//可以查看日志
            biaoQingInfo.recycle();
        }
    }

    @Override
    public void onInterrupt() {
        Utils.toast("(；′⌒`)\r\n红包功能被迫中断");
        mService = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.toast("%>_<%\r\n红包功能已关闭");
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
     * 点击该控件
     *
     * @return true表示点击成功
     */
    public static boolean clickView(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return true;
            } else {
                AccessibilityNodeInfo parent = nodeInfo.getParent();
                if (parent != null) {
                    boolean b = clickView(parent);
                    parent.recycle();
                    if (b) return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据getRootInActiveWindow查找包含当前text的控件
     *
     * @param containsText 只要内容包含就会找到（应该是根据drawText找的）
     */
    @Nullable
    public List<AccessibilityNodeInfo> findViewByContainsText(@NonNull String containsText) {
        AccessibilityNodeInfo info = getRootInActiveWindow();
        if (info == null) return null;
        List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByText(containsText);
        info.recycle();
        return list;
    }

    /**
     * 根据getRootInActiveWindow查找和当前text相等的控件
     *
     * @param equalsText 需要找的text
     */
    @Nullable
    public List<AccessibilityNodeInfo> findViewByEqualsText(@NonNull String equalsText) {
        List<AccessibilityNodeInfo> listOld = findViewByContainsText(equalsText);
        if (Utils.isEmptyArray(listOld)) {
            return null;
        }
        ArrayList<AccessibilityNodeInfo> listNew = new ArrayList<>();
        for (AccessibilityNodeInfo ani : listOld) {
            if (ani.getText() != null && equalsText.equals(ani.getText().toString())) {
                listNew.add(ani);
            } else {
                ani.recycle();
            }
        }
        return listNew;
    }

    /**
     * 根据getRootInActiveWindow查找当前id的控件
     *
     * @param pageName 被查找项目的包名:com.android.xxx
     * @param idName   id值:tv_main
     */
    @Nullable
    public AccessibilityNodeInfo findViewById(String pageName, String idName) {
        return findViewById(pageName + ":id/" + idName);
    }

    /**
     * 根据getRootInActiveWindow查找当前id的控件
     *
     * @param idfullName id全称:com.android.xxx:id/tv_main
     */
    @Nullable
    public AccessibilityNodeInfo findViewById(String idfullName) {
        List<AccessibilityNodeInfo> list = findViewByIdList(idfullName);
        return Utils.isEmptyArray(list) ? null : list.get(0);
    }

    /**
     * 根据getRootInActiveWindow查找当前id的控件集合(类似listview这种一个页面重复的id很多)
     *
     * @param idfullName id全称:com.android.xxx:id/tv_main
     */
    @Nullable
    public List<AccessibilityNodeInfo> findViewByIdList(String idfullName) {
        try {
            AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
            if (rootInfo == null) return null;
            List<AccessibilityNodeInfo> list = rootInfo.findAccessibilityNodeInfosByViewId(idfullName);
            rootInfo.recycle();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查找第一个匹配的控件
     *
     * @param tfs 匹配条件，多个AbstractTF是&&的关系，如：
     *            AbstractTF.newContentDescription("表情", true),AbstractTF.newClassName(AbstractTF.ST_IMAGEVIEW)
     *            表示描述内容是'表情'并且是imageview的控件
     */
    public AccessibilityNodeInfo findFirst(@NonNull AbstractTF... tfs) {
        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
        if (rootInfo == null) return null;

        AccessibilityNodeInfo firstInfo = findFirstRecursive(rootInfo, tfs);
        rootInfo.recycle();
        return firstInfo;
    }

    public static AccessibilityNodeInfo findFirstRecursive(AccessibilityNodeInfo parent, @NonNull AbstractTF... tfs) {
        if (parent == null) return null;
        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) continue;
            boolean isOk = true;
            for (AbstractTF tf : tfs) {
                if (!tf.checkOk(child)) {
                    isOk = false;
                    break;
                }
            }
            if (isOk) {
                return child;
            } else {
                AccessibilityNodeInfo childChild = findFirstRecursive(child, tfs);
                child.recycle();
                if (childChild != null) {
                    return childChild;
                }
            }
        }
        return null;
    }

    /**
     * 查找全部匹配的控件
     *
     * @param tfs 匹配条件，多个AbstractTF是&&的关系，如：
     *            AbstractTF.newContentDescription("表情", true),AbstractTF.newClassName(AbstractTF.ST_IMAGEVIEW)
     *            表示描述内容是'表情'并且是imageview的控件
     */
    public List<AccessibilityNodeInfo> findAll(@NonNull AbstractTF... tfs) {
        ArrayList<AccessibilityNodeInfo> list = new ArrayList<>();
        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
        if (rootInfo == null) return list;
        findAllRecursive(list, rootInfo, tfs);
        rootInfo.recycle();
        return list;
    }

    public static void findAllRecursive(List<AccessibilityNodeInfo> list, AccessibilityNodeInfo parent, @NonNull AbstractTF... tfs) {
        if (parent == null) return;
        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) continue;
            boolean isOk = true;
            for (AbstractTF tf : tfs) {
                if (!tf.checkOk(child)) {
                    isOk = false;
                    break;
                }
            }
            if (isOk) {
                list.add(child);
            } else {
                findAllRecursive(list, child, tfs);
                child.recycle();
            }
        }
    }

    /**
     * 立即发送移动的手势
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     *
     * @param path  移动路径
     * @param mills 持续总时间
     */
    @RequiresApi(24)
    public void dispatchGestureMove(Path path, int mills) {
        dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, mills)).build(), null, null);
    }

    /**
     * 点击指定位置
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     */
    @RequiresApi(24)
    public void dispatchGestureClick(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, 100)).build(), null, null);
    }

    /**
     * 由于太多,最好回收这些AccessibilityNodeInfo
     */
    public static void recycleAccessibilityNodeInfo(List<AccessibilityNodeInfo> listInfo) {
        if (Utils.isEmptyArray(listInfo)) return;

        for (AccessibilityNodeInfo info : listInfo) {
            info.recycle();
        }
    }
}