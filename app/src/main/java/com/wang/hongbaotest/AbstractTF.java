package com.wang.hongbaotest;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTF<T> {

    /**
     * 是包含还必须相等;
     */
    protected final boolean mIsEquals;
    protected final T mCheckData;

    private AbstractTF(@NonNull T checkData, boolean isEquals) {
        mCheckData = checkData;
        mIsEquals = isEquals;
    }

    public abstract boolean checkOk(AccessibilityNodeInfo thisInfo);

    /**
     * 找id，就是findAccessibilityNodeInfosByViewId方法
     * 和找text一样效率最高，如果能找到，尽量使用这个
     */
    private static class IdTF extends AbstractTF<String> implements IdTextTF {
        private IdTF(@NonNull String idFullName) {
            super(idFullName, true);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            return true;//此处不需要实现
        }

        @Nullable
        @Override
        public AccessibilityNodeInfo findFirst(AccessibilityNodeInfo root) {
            List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByViewId(mCheckData);
            if (Utils.isEmptyArray(list)) {
                return null;
            }
            for (int i = 1; i < list.size(); i++) {//其他的均回收
                list.get(i).recycle();
            }
            return list.get(0);
        }

        @Nullable
        @Override
        public List<AccessibilityNodeInfo> findAll(AccessibilityNodeInfo root) {
            return root.findAccessibilityNodeInfosByViewId(mCheckData);
        }
    }

    /**
     * 普通text，就是findAccessibilityNodeInfosByText方法
     * 和找id一样效率最高，如果能找到，尽量使用这个
     */
    private static class TextTF extends AbstractTF<String> implements IdTextTF {
        private TextTF(@NonNull String text, boolean isEquals) {
            super(text, isEquals);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            return true;//此处不需要实现
        }

        @Nullable
        @Override
        public AccessibilityNodeInfo findFirst(AccessibilityNodeInfo root) {
            List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByText(mCheckData);
            if (Utils.isEmptyArray(list)) {
                return null;
            }
            if (mIsEquals) {
                AccessibilityNodeInfo returnInfo = null;
                for (AccessibilityNodeInfo info : list) {
                    if (info.getText() != null && mCheckData.equals(info.getText().toString())) {
                        returnInfo = info;
                    } else {
                        info.recycle();
                    }
                }
                return returnInfo;
            } else {
                return list.get(0);
            }
        }

        @Nullable
        @Override
        public List<AccessibilityNodeInfo> findAll(AccessibilityNodeInfo root) {
            List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByText(mCheckData);
            if (Utils.isEmptyArray(list)) {
                return null;
            }
            if (mIsEquals) {
                ArrayList<AccessibilityNodeInfo> listNew = new ArrayList<>();
                for (AccessibilityNodeInfo info : list) {
                    if (info.getText() != null && mCheckData.equals(info.getText().toString())) {
                        listNew.add(info);
                    } else {
                        info.recycle();
                    }
                }
                return listNew;
            } else {
                return list;
            }
        }
    }

    /**
     * 类似uc浏览器，有text值但无法直接根据text来找到
     */
    private static class WebTextTF extends AbstractTF<String> {
        private WebTextTF(@NonNull String checkString, boolean isEquals) {
            super(checkString, isEquals);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            CharSequence text = thisInfo.getText();
            if (mIsEquals) {
                return text != null && text.toString().equals(mCheckData);
            } else {
                return text != null && text.toString().contains(mCheckData);
            }
        }
    }

    /**
     * 找ContentDescription字段
     */
    private static class ContentDescriptionTF extends AbstractTF<String> {
        private ContentDescriptionTF(@NonNull String checkString, boolean isEquals) {
            super(checkString, isEquals);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            CharSequence text = thisInfo.getContentDescription();
            if (mIsEquals) {
                return text != null && text.toString().equals(mCheckData);
            } else {
                return text != null && text.toString().contains(mCheckData);
            }
        }
    }

    /**
     * 找ClassName匹配
     */
    private static class ClassNameTF extends AbstractTF<String> {
        public ClassNameTF(@NonNull String checkString, boolean isEquals) {
            super(checkString, isEquals);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            if (mIsEquals) {
                return thisInfo.getClassName().toString().equals(mCheckData);
            } else {
                return thisInfo.getClassName().toString().contains(mCheckData);
            }
        }
    }

    /**
     * 在某个区域内的控件
     */
    private static class RectTF extends AbstractTF<Rect> {
        public RectTF(@NonNull Rect rect) {
            super(rect, true);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            thisInfo.getBoundsInScreen(mRecycleRect);
            return mCheckData.contains(mRecycleRect);
        }
    }

    public interface IdTextTF {
        @Nullable
        AccessibilityNodeInfo findFirst(AccessibilityNodeInfo root);

        @Nullable
        List<AccessibilityNodeInfo> findAll(AccessibilityNodeInfo root);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 创建方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Rect mRecycleRect = new Rect();

    public static final String ST_VIEW = "android.view.View",
            ST_TEXTVIEW = "android.widget.TextView",
            ST_IMAGEVIEW = "android.widget.ImageView",
            ST_BUTTON = "android.widget.Button",
            ST_IMAGEBUTTON = "android.widget.ImageButton",
            ST_EDITTEXT = "android.widget.EditText",
            ST_LISTVIEW = "android.widget.ListView",
            ST_LINEARLAYOUT = "android.widget.LinearLayout",
            ST_VIEWGROUP = "android.view.ViewGroup",
            ST_SYSTEMUI = "com.android.systemui";

    /**
     * 找id，就是findAccessibilityNodeInfosByViewId方法
     * 和找text一样效率最高，如果能找到，尽量使用这个
     *
     * @param pageName 被查找项目的包名:com.android.xxx
     * @param idName   id值:tv_main
     */
    public static AbstractTF newId(String pageName, String idName) {
        return newId(pageName + ":id/" + idName);
    }

    /**
     * @param idfullName id全称:com.android.xxx:id/tv_main
     */
    public static AbstractTF newId(@NonNull String idfullName) {
        return new IdTF(idfullName);
    }

    /**
     * 普通text，就是findAccessibilityNodeInfosByText方法
     * 和找id一样效率最高，如果能找到，尽量使用这个
     */
    public static AbstractTF newText(@NonNull String text, boolean isEquals) {
        return new TextTF(text, isEquals);
    }

    /**
     * 类似uc浏览器，有text值但无法直接根据text来找到
     */
    public static AbstractTF newWebText(@NonNull String webText, boolean isEquals) {
        return new WebTextTF(webText, isEquals);
    }

    /**
     * 找ContentDescription字段
     */
    public static AbstractTF newContentDescription(@NonNull String cd, boolean isEquals) {
        return new ContentDescriptionTF(cd, isEquals);
    }

    /**
     * 找ClassName匹配
     */
    public static AbstractTF newClassName(@NonNull String className) {
        return new ClassNameTF(className, true);
    }

    public static AbstractTF newClassName(@NonNull String className, boolean isEquals) {
        return new ClassNameTF(className, isEquals);
    }

    /**
     * 在某个区域内的控件
     */
    public static AbstractTF newRect(@NonNull Rect rect) {
        return new RectTF(rect);
    }
}