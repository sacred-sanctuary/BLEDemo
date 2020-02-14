/*
 * Copyright (C) 2020 Sacred Sanctuary Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.sacredsanctuary.bledemo.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Arrays;

import jp.sacredsanctuary.bledemo.R;
import jp.sacredsanctuary.bledemo.util.LogUtil;
import jp.sacredsanctuary.bledemo.util.PermissionUtils;
import jp.sacredsanctuary.bledemo.util.Preconditions;

/**
 * Activity that requests permissions needed for activities exported from file manager.
 */
public class RequestPermissionsActivity extends Activity {
    private static final String ClassName = RequestPermissionsActivity.class.getSimpleName();
    private static final String PREVIOUS_INTENT = "previous_intent";
    private static final int REQUEST_ALL_PERMISSIONS = 1;
    private Intent mPreviousIntent;
    private String[] mRequiresPermissions;

    public static boolean startPermissionActivity(Activity activity) {
        return startRequestPermissionActivity(activity, RequestPermissionsActivity.class);
    }

    protected static boolean startRequestPermissionActivity(Activity activity,
            Class<?> newActivityClass) {
        String[] list = PermissionUtils.getUnauthorizedPermissionList(activity.getBaseContext());
        LogUtil.V(ClassName,
                "startRequestPermissionActivity() [INF] list:" + Arrays.toString(list));
        if (!Preconditions.isEmpty(list)) {
            final Intent intent = new Intent(activity, newActivityClass);
            intent.putExtra(PREVIOUS_INTENT, activity.getIntent());
            activity.startActivity(intent);
            activity.finish();
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreviousIntent = (Intent) getIntent().getExtras().get(PREVIOUS_INTENT);
        mRequiresPermissions = PermissionUtils.getUnauthorizedPermissionList(getBaseContext());

        if (savedInstanceState == null) {
            requestPermissions();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantedResults) {
        if (permissions != null && permissions.length > 0
                && arePermissionsGranted(permissions, grantedResults)) {
            startPreviousActivity();
        } else {
            Toast.makeText(this, R.string.on_permission_read_file_manager,
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean arePermissionsGranted(String permissions[], int[] grantResult) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResult[i] != PackageManager.PERMISSION_GRANTED
                    && Arrays.asList(mRequiresPermissions).contains(permissions[i])) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        if (!Preconditions.isEmpty(mRequiresPermissions)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(mRequiresPermissions, REQUEST_ALL_PERMISSIONS);
            }
        } else {
            startPreviousActivity();
        }
    }

    private void startPreviousActivity() {
        mPreviousIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mPreviousIntent);
        finish();
    }
}
