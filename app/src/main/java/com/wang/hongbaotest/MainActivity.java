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
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findViewById(R.id.bt_main_ShouShi).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Toast.makeText(MainActivity.this, "7.0及以上才能使用手势", Toast.LENGTH_SHORT).show();
                    return;
                }
                Path path = new Path();
                path.moveTo(400, 800);
                path.lineTo(10, 800);
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
