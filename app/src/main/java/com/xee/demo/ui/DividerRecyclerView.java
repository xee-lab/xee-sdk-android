/*
 * Copyright 2016 Eliocity
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xee.demo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xee.demo.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DividerRecyclerView extends RecyclerView.ItemDecoration {

    @IntDef({NORMAL, COVER, POSITION, NO_MARGIN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}

    public static final int NORMAL = 0;
    public static final int COVER = 1;
    public static final int POSITION = 2;
    public static final int NO_MARGIN = 3;

    private @DividerRecyclerView.Type int type = NO_MARGIN;

    private Drawable divider;

    public DividerRecyclerView(Context context, @Type int type) {
        this.divider = ContextCompat.getDrawable(context, R.drawable.recycler_view_divider);
        this.type = type;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        switch(type) {
            case NORMAL:
                left = parent.getPaddingLeft();
                right = parent.getWidth() - parent.getPaddingRight();
                break;
            case COVER:
                left = parent.getContext().getResources().getDimensionPixelSize(R.dimen.cell_square_cover_margin_horizontal) * 2
                        + parent.getContext().getResources().getDimensionPixelSize(R.dimen.cell_square_cover);
                right = parent.getWidth() - parent.getPaddingRight();
                break;
            case POSITION:
                left = parent.getContext().getResources().getDimensionPixelSize(R.dimen.cell_square_cover_margin_horizontal)
                        + parent.getContext().getResources().getDimensionPixelSize(R.dimen.cell_square_cover);
                right = parent.getWidth() - parent.getPaddingRight();
                break;
            case NO_MARGIN:
                left = 0;
                right = parent.getWidth();
                break;
        }

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}