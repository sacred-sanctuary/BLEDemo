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
package jp.sacredsanctuary.bledemo.bluetooth;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.sacredsanctuary.bledemo.util.LogUtil;

/**
 * Helper class for BLE scan callback.
 */
public class BleScanCallback extends ScanCallback {
    private static final String ClassName = BleScanCallback.class.getSimpleName();
    private Set<ScanResult> mResults = new HashSet<>();
    private List<ScanResult> mBatchScanResults = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
            LogUtil.V(ClassName, "onScanResult() [INF] result:" + result);
            mResults.add(result);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        // In case onBatchScanResults are called due to buffer full, we want to collect all
        // scan results.
        mBatchScanResults.addAll(results);
    }

    /**
     * Clear regular and batch scan results.
     */
    synchronized void clear() {
        mResults.clear();
        mBatchScanResults.clear();
    }

    /**
     * Return regular BLE scan results accumulated so far.
     */
    synchronized Set<ScanResult> getScanResults() {
        return Collections.unmodifiableSet(mResults);
    }

    /**
     * Return batch scan results.
     */
    synchronized public List<ScanResult> getBatchScanResults() {
        return Collections.unmodifiableList(mBatchScanResults);
    }
}