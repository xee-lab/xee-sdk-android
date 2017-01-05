package com.xee.auth;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;

import com.xee.R;
import com.xee.api.Xee;

import java.lang.ref.WeakReference;

public final class SignInButton extends Button {

    private static final int TEXT_WHITE = 0xFFFFFFFF;
    private static final int TEXT_GREY = 0xFF474747;

    private static final int SIZE_ICON = 24;
    private static final int SIZE_NORMAL = 42;
    private static final int SIZE_LARGE = 56;

    private static final int PADDING_VERTICAL = 8;
    private static final int PADDING_HORIZONTAL = 16;
    private static final int PADDING_DRAWABLE = 8;

    private Drawable mDrawable;
    private int mSize = -1;
    private int mTheme = -1;
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
        mSize = a.getInt(R.styleable.SignInButton_signInBtnSize, SIZE_NORMAL);

        setBtnSize(context, mSize);
        setBtnTheme(mTheme);

        a.recycle();
    }

    /**
     * Set the button size
     * @param size the button size {@link Size}
     */
    public void setBtnSize(Size size) {
        switch (size) {
            case ICON:
                setBtnSize(getContext(), SIZE_ICON);
                break;
            case NORMAL:
                setBtnSize(getContext(), SIZE_NORMAL);
                break;
            case LARGE:
                setBtnSize(getContext(), SIZE_LARGE);
                break;
        }
    }

    /**
     * Set the button size
     * @param size the button size value
     * @param context the context
     */
    private void setBtnSize(final Context context, final int size) {
        mSize = size;
        final int minHeight = dpToPx(size) + dpToPx(PADDING_VERTICAL * 2);
        switch (size) {
            case SIZE_ICON:
                setText("");
                setBtnPadding(PADDING_HORIZONTAL, PADDING_VERTICAL, PADDING_HORIZONTAL, PADDING_VERTICAL);
                setHeight(minHeight);
                setCompoundDrawablePadding(dpToPx(0));
                break;
            case SIZE_NORMAL:
                setText(context.getString(R.string.sign_in_with_xee));
                setBtnPadding(PADDING_HORIZONTAL, PADDING_VERTICAL, PADDING_HORIZONTAL, PADDING_VERTICAL);
                setHeight(minHeight);
                setCompoundDrawablePadding(dpToPx(PADDING_DRAWABLE));
                break;
            default:
            case SIZE_LARGE:
                setText(context.getString(R.string.sign_in_with_xee));
                setBtnPadding(PADDING_HORIZONTAL, PADDING_VERTICAL, PADDING_HORIZONTAL, PADDING_VERTICAL);
                setHeight(minHeight);
                setCompoundDrawablePadding(dpToPx(PADDING_DRAWABLE));
                break;
        }
    }

    /**
     * Set the button theme
     * @param theme the button theme value
     */
    private void setBtnTheme(int theme) {
        mTheme = theme;
        switch (mTheme) {
            case 0:
                mDrawable = getContext().getResources().getDrawable(R.drawable.xee_auth_btn_logo_black, null);
                setBackgroundTintList(getContext().getResources().getColorStateList(R.color.sign_in_button_bg_white, null));
                setTextColor(TEXT_GREY);
                break;
            case 1:
                mDrawable = getContext().getResources().getDrawable(R.drawable.xee_auth_btn_logo_white, null);
                setBackgroundTintList(getContext().getResources().getColorStateList(R.color.sign_in_button_bg_grey, null));
                setTextColor(TEXT_WHITE);
                break;
            case 2:
                mDrawable = getContext().getResources().getDrawable(R.drawable.xee_auth_btn_logo_white, null);
                setBackgroundTintList(getContext().getResources().getColorStateList(R.color.sign_in_button_bg_green, null));
                setTextColor(TEXT_GREY);
                break;
        }

        mDrawable.setBounds(0, 0, dpToPx(SIZE_ICON), dpToPx(SIZE_ICON));
        setCompoundDrawables(null, null, mDrawable, null);
    }

    /**
     * Set the button theme
     * @param theme the button theme {@link Theme}
     */
    public void setBtnTheme(Theme theme) {
        mTheme = theme.ordinal();
        switch (mTheme) {
            case 0:
                setBtnTheme(Theme.WHITE.ordinal());
                break;
            case 1:
                setBtnTheme(Theme.GREY.ordinal());
                break;
            case 2:
                setBtnTheme(Theme.GREEN.ordinal());
                break;
        }
    }

    /**
     * Set a click listener invoked when the button is clicked
     * @param listener the listener
     */
    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

    /**
     * Set the sign in callback when the button is clicked
     * @param xee the {@link Xee} instance
     * @param connectionCallback the connection callback
     */
    public void setOnSignInClickResult(@NonNull Xee xee, @NonNull final ConnectionCallback connectionCallback) {
        mConnectionCallback = connectionCallback;
        mXeeWeakReference = new WeakReference<>(xee);
    }

    /**
     * Set the button padding
     * @param left the padding left
     * @param top the padding top
     * @param right the padding right
     * @param bottom the padding bottom
     */
    private void setBtnPadding(int left, int top, int right, int bottom) {
        setPadding(dpToPx(left), dpToPx(top), dpToPx(right), dpToPx(bottom));
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
        if(event.getAction() == MotionEvent.ACTION_UP) {
            // detect if user is clicking outside
            Rect viewRect = new Rect();
            getGlobalVisibleRect(viewRect);
            if (viewRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                if(mListener != null) {
                    mListener.onClick(this);
                }
                if(mConnectionCallback != null) {
                    signIn();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_UP && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if(mListener != null) {
                mListener.onClick(this);
            }
            if(mConnectionCallback != null) {
                signIn();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Launch the sign in process and notify result
     */
    private void signIn() {
        if(mXeeWeakReference != null && mXeeWeakReference.get() != null) {
            mXeeWeakReference.get().connect(new ConnectionCallback() {
                @Override
                public void onError(@NonNull Throwable error) {
                    if(mConnectionCallback != null) {
                        mConnectionCallback.onError(error);
                    }
                }

                @Override
                public void onSuccess() {
                    if(mConnectionCallback != null) {
                        mConnectionCallback.onSuccess();
                    }
                }
            });
        }
    }

    /**
     * Convert do to px
     * @param dp the dp to convert
     * @return
     */
    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}