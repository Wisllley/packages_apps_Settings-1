/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.settings.dashboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.ProfileSelectDialog;
import com.android.settings.R;
import com.android.settings.Utils;

public class DashboardTileView extends FrameLayout implements View.OnClickListener {

    private static final int DEFAULT_COL_SPAN = 1;

    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mStatusTextView;
    private View mDivider;
    private int mDashTextSize = 14;

    private int mColSpan = DEFAULT_COL_SPAN;

    private DashboardTile mTile;

    public DashboardTileView(Context context) {
        this(context, null);
    }

    public DashboardTileView(Context context, AttributeSet attrs) {
        super(context, attrs);

        final View view = LayoutInflater.from(context).inflate(R.layout.dashboard_tile, this);

        mImageView = (ImageView) view.findViewById(R.id.icon);

        mTitleTextView = (TextView) view.findViewById(R.id.title);
        if (Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.DASHBOARD_TILEVIEW_DOUBLE_LINES, 0) == 1) {
        mTitleTextView.setSingleLine(false);
        } else {
        mTitleTextView.setSingleLine(true);
        }

        mStatusTextView = (TextView) view.findViewById(R.id.status);
        mDivider = view.findViewById(R.id.tile_divider);
        if (Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.DASHBOARD_TILEVIEW_DIVIDERS, 0) == 1) {
        mDivider.setVisibility(View.GONE);
        } else {
        mDivider.setVisibility(View.VISIBLE);
        }

        setOnClickListener(this);
        setBackgroundResource(R.drawable.dashboard_tile_background);
        mStatusTextView.setTextSize(Settings.System.getIntForUser(context.getContentResolver(),
                Settings.System.SETTINGS_TITLE_TEXT_SIZE, 14,
                UserHandle.USER_CURRENT));
        mTitleTextView.setTextSize(Settings.System.getIntForUser(context.getContentResolver(),
                Settings.System.SETTINGS_TITLE_TEXT_SIZE, 18,
                UserHandle.USER_CURRENT));
        setFocusable(true);
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    public TextView getStatusTextView() {
        return mStatusTextView;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setTile(DashboardTile tile) {
        mTile = tile;
    }

    public void setDividerVisibility(boolean visible) {
        mDivider.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    void setColumnSpan(int span) {
        mColSpan = span;
    }

    int getColumnSpan() {
        return mColSpan;
    }

    @Override
    public void onClick(View v) {
        if (mTile.fragment != null) {
            Utils.startWithFragment(getContext(), mTile.fragment, mTile.fragmentArguments, null, 0,
                    mTile.titleRes, mTile.getTitle(getResources()));
        } else if (mTile.intent != null) {
            int numUserHandles = mTile.userHandle.size();
            if (numUserHandles > 1) {
                ProfileSelectDialog.show(((Activity) getContext()).getFragmentManager(), mTile);
            } else if (numUserHandles == 1) {
                getContext().startActivityAsUser(mTile.intent, mTile.userHandle.get(0));
            } else {
                getContext().startActivity(mTile.intent);
            }
        }
    }
}
