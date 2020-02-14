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
/**
 * @file LogUtil.java
 * @note <br>
 * VERBOSE: adb shell setprop log.tag.BleDemo VERBOSE<br>
 * DEBUG:   adb shell setprop log.tag.BleDemo DEBUG<br>
 * INFO:    adb shell setprop log.tag.BleDemo INFO
 */
package jp.sacredsanctuary.bledemo.util;

import android.util.Log;

public class LogUtil {
    private final static String TAG = "BleDemo";
    private final static boolean VERBOSE = Log.isLoggable(TAG, Log.VERBOSE);
    private final static boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);

    public static void V(String ClassName, String log) {
        if (VERBOSE) {
            Log.v(TAG, "[" + ClassName + "] " + log);
        }
    }

    public static void D(String ClassName, String log) {
        if (VERBOSE) {
            Log.v(TAG, "[" + ClassName + "] " + log);
        }
    }

    public static void I(String ClassName, String log) {
        if (VERBOSE) {
            Log.v(TAG, "[" + ClassName + "] " + log);
        }
    }

    public static void W(String ClassName, String log) {
        if (VERBOSE) {
            Log.v(TAG, "[" + ClassName + "] " + log);
        }
    }

    public static void E(String ClassName, String log) {
        if (DEBUG) {
            Log.e(TAG, "[" + ClassName + "] " + log);
        }
    }
}
