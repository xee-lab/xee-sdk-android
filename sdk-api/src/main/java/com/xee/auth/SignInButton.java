/*
 * Copyright 2017 Eliocity
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

package com.xee.auth;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;

import com.xee.R;
import com.xee.api.Xee;

import java.lang.ref.WeakReference;

public final class SignInButton extends Button {

    private int mSizeIcon;
    private int mSizeNormal;
    private int mSizeLarge;
    private int mPaddingVertical;
    private int mPaddingHorizontal;
    private int mPaddingDrawable;
    private int mSize = -1;
    private int mTheme = -1;
    private Drawable mDrawable;
    private OnClickListener mListener;
    private ConnectionCallback mConnectionCallback;
    private WeakReference<Xee> mXeeWeakReference;

    public enum Theme {
        WHITE,
        GREY,
        GREEN
    }

    public enum Size {
        ICON,
        NORMAL,
        LARGE
    }

    public SignInButton(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SignInButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SignInButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SignInButton, defStyle, 0);
        mTheme = a.getInt(R.styleable.SignInButton_signInBtnTheme, Theme.GREY.ordinal());
        mSize = a.getInt(R.styleable.SignInButton_signInBtnSize, mSizeNormal);

        mSizeIcon = getRealDimen(R.dimen.sign_in_button_icon);
        mSizeNormal = getRealDimen(R.dimen.sign_in_button_normal);
        mSizeLarge = getRealDimen(R.dimen.sign_in_button_large);

        mPaddingHorizontal = getRealDimen(R.dimen.sign_in_padding_horizontal);
        mPaddingVertical = getRealDimen(R.dimen.sign_in_padding_vertical);
        mPaddingDrawable = getRealDimen(R.dimen.sign_in_padding_drawable);

        setBtnSize(context, mSize);
        setBtnTheme(mTheme);

        a.recycle();
    }

    /**
     * Set the button size
     *
     * @param size the button size {@link Size}
     */
    public void setBtnSize(@NonNull Size size) {
        switch (size) {
            case ICON:
                setBtnSize(getContext(), mSizeIcon);
                break;
            case NORMAL:
                setBtnSize(getContext(), mSizeNormal);
                break;
            case LARGE:
                setBtnSize(getContext(), mSizeLarge);
                break;
        }
    }

    /**
     * Set the button size
     *
     * @param size    the button size value
     * @param context the context
     */
    private void setBtnSize(final Context context, final int size) {
        mSize = size;
        final int minHeight = dpToPx(size) + dpToPx(mPaddingVertical * 2);
        if (size == mSizeIcon) {
            setText("");
            setBtnPadding(mPaddingHorizontal, mPaddingVertical, mPaddingHorizontal, mPaddingVertical);
            setHeight(minHeight);
            setCompoundDrawablePadding(dpToPx(0));
        } else if (size == mSizeLarge) {
            setText(context.getString(R.string.sign_in_with_xee));
            setBtnPadding(mPaddingHorizontal, mPaddingVertical, mPaddingHorizontal, mPaddingVertical);
            setHeight(minHeight);
            setCompoundDrawablePadding(dpToPx(mPaddingDrawable));
        } else {
            setText(context.getString(R.string.sign_in_with_xee));
            setBtnPadding(mPaddingHorizontal, mPaddingVertical, mPaddingHorizontal, mPaddingVertical);
            setHeight(minHeight);
            setCompoundDrawablePadding(dpToPx(mPaddingDrawable));
        }
    }

    /**
     * Set the button theme
     *
     * @param theme the button theme value
     */
    private void setBtnTheme(int theme) {
        mTheme = theme;
        switch (mTheme) {
            case 0:
                mDrawable = getContext().getResources().getDrawable(R.drawable.xee_auth_btn_logo_black, null);
                setBackgroundTintList(getContext().getResources().getColorStateList(R.color.sign_in_button_bg_white, null));
                setTextColor(ContextCompat.getColor(getContext(), R.color.sign_in_button_text_grey));
                break;
            case 1:
                mDrawable = getContext().getResources().getDrawable(R.drawable.xee_auth_btn_logo_white, null);
                setBackgroundTintList(getContext().getResources().getColorStateList(R.color.sign_in_button_bg_grey, null));
                setTextColor(ContextCompat.getColor(getContext(), R.color.sign_in_button_text_white));
                break;
            case 2:
                mDrawable = getContext().getResources().getDrawable(R.drawable.xee_auth_btn_logo_white, null);
                setBackgroundTintList(getContext().getResources().getColorStateList(R.color.sign_in_button_bg_green, null));
                setTextColor(ContextCompat.getColor(getContext(), R.color.sign_in_button_text_grey));
                break;
        }

        mDrawable.setBounds(0, 0, dpToPx(mSizeIcon), dpToPx(mSizeIcon));
        setCompoundDrawables(null, null, mDrawable, null);
    }

    /**
     * Set the button theme
     *
     * @param theme the button theme {@link Theme}
     */
    public void setBtnTheme(@NonNull Theme theme) {
        mTheme = theme.ordinal();
        setBtnTheme(mTheme);
    }

    /**
     * Set a click listener invoked when the button is clicked
     *
     * @param listener the listener
     */
    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

    /**
     * Set the sign in callback when the button is clicked
     *
     * @param xee                the {@link Xee} instance
     * @param connectionCallback the connection callback
     */
    public void setOnSignInClickResult(@NonNull Xee xee, @NonNull final ConnectionCallback connectionCallback) {
        mConnectionCallback = connectionCallback;
        mXeeWeakReference = new WeakReference<>(xee);
    }

    /**
     * Set the button padding
     *
     * @param left   the padding left
     * @param top    the padding top
     * @param right  the padding right
     * @param bottom the padding bottom
     */
    private void setBtnPadding(int left, int top, int right, int bottom) {
        setPadding(dpToPx(left), dpToPx(top), dpToPx(right), dpToPx(bottom));
    }

    /**
     * Get dimen based on current screen density
     *
     * @param dimen the dimen
     * @return
     */
    private int getRealDimen(@DimenRes int dimen) {
        final float scaleRatio = getResources().getDisplayMetrics().density;
        float dimenPix = getResources().getDimension(dimen);
        return (int) (dimenPix / scaleRatio);
    }

    /**
     * Class which allows to save and restore the button state
     */
    private static class SavedState extends BaseSavedState {
        int mmTheme = -1;
        int mmSize = -1;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mmTheme = in.readInt();
            mmSize = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mmTheme);
            out.writeInt(mmSize);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SignInButton.SavedState createFromParcel(Parcel in) {
                return new SignInButton.SavedState(in);
            }

            public SignInButton.SavedState[] newArray(int size) {
                return new SignInButton.SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mmTheme = mTheme;
        ss.mmSize = mSize;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mTheme = ss.mmTheme;
        mSize = ss.mmSize;
        setBtnSize(getContext(), mSize);
        setBtnTheme(mTheme);
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        super.setMinimumWidth(0);
    }

    @Override
    public int getMinWidth() {
        return 0;
    }

    @Override
    public void setMinWidth(int minpixels) {
        super.setMinWidth(0);
    }

    @Override
    public int getMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // detect if user is clicking outside
            Rect viewRect = new Rect();
            getGlobalVisibleRect(viewRect);
            if (viewRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                if (mListener != null) {
                    mListener.onClick(this);
                }
                if (mConnectionCallback != null) {
                    signIn();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (mListener != null) {
                mListener.onClick(this);
            }
            if (mConnectionCallback != null) {
                signIn();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Launch the sign in process and notify result
     */
    private void signIn() {
        if (mXeeWeakReference != null && mXeeWeakReference.get() != null) {
            mXeeWeakReference.get().connect(new ConnectionCallback() {
                @Override
                public void onError(@NonNull Throwable error) {
                    if (mConnectionCallback != null) {
                        mConnectionCallback.onError(error);
                    }
                }

                @Override
                public void onSuccess() {
                    if (mConnectionCallback != null) {
                        mConnectionCallback.onSuccess();
                    }
                }
            });
        }
    }

    /**
     * Convert do to px
     *
     * @param dp the dp to convert
     * @return
     */
    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}