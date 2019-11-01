package com.wang.hongbaotest;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.accessibility.AccessibilityNodeInfo;

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
     * 类似uc浏览器，有text值但无法直接根据text来找到
     */
    private static class WebTextTF extends AbstractTF<String> {
        private WebTextTF(String checkString, boolean isEquals) {
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
        private ContentDescriptionTF(String checkString, boolean isEquals) {
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
        public ClassNameTF(String checkString, boolean isEquals) {
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
        public RectTF(Rect rect) {
            super(rect, true);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            thisInfo.getBoundsInScreen(mRecycleRect);
            return mCheckData.contains(mRecycleRect);
        }
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
     * 类似uc浏览器，有text值但无法直接根据text来找到
     */
    public static AbstractTF newWebText(String checkString, boolean isEquals) {
        return new WebTextTF(checkString, isEquals);
    }

    /**
     * 找ContentDescription字段
     */
    public static AbstractTF newContentDescription(String checkString, boolean isEquals) {
        return new ContentDescriptionTF(checkString, isEquals);
    }

    /**
     * 找ClassName匹配
     */
    public static AbstractTF newClassName(String checkString) {
        return new ClassNameTF(checkString, true);
    }

    public static AbstractTF newClassName(String checkString, boolean isEquals) {
        return new ClassNameTF(checkString, isEquals);
    }

    /**
     * 在某个区域内的控件
     */
    public static AbstractTF newRect(Rect rect) {
        return new RectTF(rect);
    }
}