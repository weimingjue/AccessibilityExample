package com.wang.hongbaotest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        findViewById(R.id.bt_main_ShouShi).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Toast.makeText(MainActivity.this, "7.0及以上才能使用手势", Toast.LENGTH_SHORT).show();
                    return;
                }
                Path path = new Path();
                path.moveTo(displayMetrics.widthPixels / 2, displayMetrics.heightPixels * 2 / 3);//从屏幕的2/3处开始滑动
                path.lineTo(10, displayMetrics.heightPixels * 2 / 3);
                final GestureDescription.StrokeDescription sd = new GestureDescription.StrokeDescription(path, 0, 500);
                HongBaoService.mService.dispatchGesture(new GestureDescription.Builder().addStroke(sd).build(), new AccessibilityService.GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        super.onCompleted(gestureDescription);
                        Toast.makeText(MainActivity.this, "手势成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(GestureDescription gestureDescription) {
                        super.onCancelled(gestureDescription);
                        Toast.makeText(MainActivity.this, "手势失败，请重启手机再试", Toast.LENGTH_SHORT).show();
                    }
                }, null);
            }
        });
        findViewById(R.id.bt_main_DianJi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessibilityNodeInfo ces = HongBaoService.mService.findFirst(AbstractTF.newText("测试控件", true));
                if (ces == null) {
                    Utils.toast("找测试控件失败");
                    return;
                }
                HongBaoService.clickView(ces);
            }
        });
        final View viewCes = findViewById(R.id.bt_main_CeShi);
        findViewById(R.id.bt_main_ChangAn).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Toast.makeText(MainActivity.this, "7.0及以上才能使用手势", Toast.LENGTH_SHORT).show();
                    return;
                }
                int[] absXY = new int[2];
                viewCes.getLocationOnScreen(absXY);
//                HongBaoService.mService.dispatchGestureClick(absXY[0],absXY[1]);//这是用手势来点击
                Path path = new Path();
                path.moveTo(absXY[0], absXY[1]);
                HongBaoService.mService.dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                        (path, 0, 800)).build(), new AccessibilityService.GestureResultCallback() {

                    @Override
                    public void onCancelled(GestureDescription gestureDescription) {
                        super.onCancelled(gestureDescription);
                        Toast.makeText(MainActivity.this, "手势失败，请重启手机再试", Toast.LENGTH_SHORT).show();
                    }
                }, null);
            }
        });
        findViewById(R.id.bt_main_FanHui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HongBaoService.mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        });

        viewCes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toast("'测试控件'被点击了");
            }
        });
        viewCes.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.toast("'测试控件'被长按了");
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!HongBaoService.isStart()) {
            try {
                this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                this.startActivity(new Intent(Settings.ACTION_SETTINGS));
                e.printStackTrace();
            }
        }
    }
}
