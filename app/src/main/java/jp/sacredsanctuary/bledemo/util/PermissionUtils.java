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
package jp.sacredsanctuary.bledemo.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class to handle permissions.
 */
public class PermissionUtils {

    /**
     * Gets the list of unauthorized permissions listed in manifest.
     *
     * @param context A context of the current app
     * @return Returns the list of unauthorized permissions listed in manifest.
     */
    public static String[] getUnauthorizedPermissionList(Context context) {
        List<String> list = new ArrayList<>();
        String[] permissionList = getPermissionList(context);
        boolean ret;
        if (Preconditions.isEmpty(permissionList)) return null;
        for (String permission : permissionList) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permission.equals(("android.permission.SYSTEM_ALERT_WINDOW"))) {
                    ret = true;
                } else {
                    ret = context.checkSelfPermission(permission)
                            == PackageManager.PERMISSION_GRANTED;
                }
            } else {
                ret = context.getPackageManager().checkPermission(permission,
                        context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
            }
            if (!ret) list.add(permission);
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Returns the list of permissions listed in manifest.
     *
     * @param context A Context object used to access application manifest.
     * @return Returns the list of permissions listed in manifest.
     */
    private static String[] getPermissionList(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return packageInfo.requestedPermissions;
    }
}
