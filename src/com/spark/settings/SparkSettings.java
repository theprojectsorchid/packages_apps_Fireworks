/*
 * Copyright (C) 2019-2022 Spark
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
package com.spark.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class SparkSettings extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.spark_settings);

        int mDashBoardStyle = getDashboardStyle();
        setPrefStyle(mDashBoardStyle);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

    private void setPrefStyle(int mDashBoardStyle) {
        final PreferenceScreen screen = getPreferenceScreen();
        final int count = screen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            final Preference preference = screen.getPreference(i);

            String key = preference.getKey();

            if (key == null) continue;

            if (key.equals("spark_logo")) {
                preference.setLayoutResource(R.layout.spark_logo);
                continue;
            }

            if (mDashBoardStyle == 5 && key.equals("key_theme_settings")) {
                preference.setLayoutResource(R.layout.dot_dashboard_preference_full_accent);
            } else if (mDashBoardStyle == 6 && key.equals("key_theme_settings")) {
                preference.setLayoutResource(R.layout.dot_dashboard_preference_full_accent_2);
            } else {
                preference.setLayoutResource(R.layout.dot_dashboard_preference_full);
            }
        }
    }

    private int getDashboardStyle() {
        return Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_DASHBOARD_STYLE, 6, UserHandle.USER_CURRENT);
    }

    /**
     * For Search.
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.spark_settings);
}
