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

import android.net.Uri;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple static methods to be called at the start of your own methods to verify
 * correct arguments and state.
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is not null.
     *
     * @param reference an object reference
     * @return {@code true} if {@code reference} is not null, {@code false} otherwise.
     */
    public static <T> boolean checkNotNull(T reference) {
        return reference != null;
    }

    /**
     * Returns whether this {@code String} contains no elements.
     *
     * @param str the string to be examined
     * @return {@code true} if this {@code String} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns whether this {@code CharSequence} contains no elements.
     *
     * @param str the string to be examined
     * @return {@code true} if this {@code CharSequence} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns whether this {@code Uri} contains no elements.
     *
     * @param uri the Uri to be examined
     * @return {@code true} if this {@code Uri} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(Uri uri) {
        return uri == null || Uri.EMPTY.equals(uri);
    }

    /**
     * Returns whether this {@code Map} contains no elements.
     *
     * @param map the Map object to be examined
     * @return {@code true} if this {@code Map} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Returns whether this {@code Set} contains no elements.
     *
     * @param set the Set object to be examined
     * @return {@code true} if this {@code Set} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(Set<?> set) {
        return set == null || set.isEmpty();
    }

    /**
     * Returns whether this {@code List} contains no elements.
     *
     * @param list the List object to be examined
     * @return {@code true} if this {@code List} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * Returns whether this contains no elements.
     *
     * @param array the any array to be examined
     * @return {@code true} if this {@code array} has no elements, {@code false} otherwise.
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether this contains no elements.
     *
     * @param array the int array to be examined
     * @return {@code true} if this {@code array} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether this contains no elements.
     *
     * @param array the long array to be examined
     * @return {@code true} if this {@code array} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether this contains no elements.
     *
     * @param array the byte array to be examined
     * @return {@code true} if this {@code array} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether this contains no elements.
     *
     * @param array the boolean array to be examined
     * @return {@code true} if this {@code array} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Returns whether this contains no elements.
     *
     * @param reference the any object to be examined
     * @return {@code true} if this {@code reference} has no elements, {@code false} otherwise.
     */
    public static <T> boolean isEmpty(T reference) {
        return reference == null;
    }
}
