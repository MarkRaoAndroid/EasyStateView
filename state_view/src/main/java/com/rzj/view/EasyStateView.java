package com.rzj.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.rzj.stateview.R;

public class EasyStateView extends FrameLayout {

    // 内容 View
    public static final int VIEW_CONTENT = 0;
    // 加载 View
    public static final int VIEW_LOADING = -1;
    // 数据异常( 数据异常指原本应该是有数据，但是服务器返回了错误的、不符合格式的数据 ) View
    public static final int VIEW_ERROR_DATA = -2;
    // 网络异常 View
    public static final int VIEW_ERROR_NET = -3;
    // 数据为空 View
    public static final int VIEW_EMPTY = -4;
    // View 的 Tag 标签值
    private static final int VIEW_TAG = -5;
    // 用来存放 View
    private SparseArray<View> mViews;
    // View Tag 对应的下标缓存
    // 是否使用过渡动画
    private boolean mUseAnim;
    // 是否处于动画中
    private boolean isAniming;
    // 当前显示的 ViewTag
    private int mCurrentState;
    private Context mContext;
    private StateViewListener mListener;
    // content View 是否被添加到队列
    private boolean isAddContent;

    public interface StateViewListener {
        void onStateChanged(int state);
    }

    public void setStateChangedListener(StateViewListener listener) {
        this.mListener = listener;
    }

    public EasyStateView(Context context) {
        this(context, null);
    }

    public EasyStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mViews = new SparseArray<>();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EasyStateView);
        mCurrentState = typedArray.getInt(R.styleable.EasyStateView_esv_viewState, VIEW_CONTENT);
        int emptyResId = typedArray.getResourceId(R.styleable.EasyStateView_esv_emptyView, VIEW_TAG);
        if (emptyResId != VIEW_TAG) {
            View view = LayoutInflater.from(getContext()).inflate(emptyResId, this, false);
            addViewToHash(view, VIEW_EMPTY);
            addViewInLayout(view, -1, view.getLayoutParams());
        }
        int errorDataResId = typedArray.getResourceId(R.styleable.EasyStateView_esv_errorDataView, VIEW_TAG);
        if (errorDataResId != VIEW_TAG) {
            View view = LayoutInflater.from(getContext()).inflate(errorDataResId, this, false);
            addViewToHash(view, VIEW_ERROR_DATA);
            addViewInLayout(view, -1, view.getLayoutParams());
        }
        int errorNetResId = typedArray.getResourceId(R.styleable.EasyStateView_esv_errorNetView, VIEW_TAG);
        if (errorNetResId != VIEW_TAG) {
            View view = LayoutInflater.from(getContext()).inflate(errorNetResId, this, false);
            addViewToHash(view, VIEW_ERROR_NET);
            addViewInLayout(view, -1, view.getLayoutParams());
        }
        int loadingResId = typedArray.getResourceId(R.styleable.EasyStateView_esv_loadingView, VIEW_TAG);
        if (loadingResId != VIEW_TAG) {
            View view = LayoutInflater.from(getContext()).inflate(loadingResId, this, false);
            addViewToHash(view, VIEW_LOADING);
            addViewInLayout(view, -1, view.getLayoutParams());
        }
        mUseAnim = typedArray.getBoolean(R.styleable.EasyStateView_esv_use_anim, true);
        typedArray.recycle();
    }

    @Override
    public void addView(View child) {
        addContentV(child);
        super.addView(child);
    }

    private boolean isContentView(View child) {
        if (!isAddContent && null != child
                && null == child.getTag()) {
            return true;
        }
        return false;
    }

    private void addContentV(View child) {
        if (isContentView(child)) {
            addViewToHash(child, VIEW_CONTENT);
            isAddContent = true;
        }
    }

    private void addViewToHash(View child, int viewTag) {
        child.setTag(viewTag);
        if (viewTag != mCurrentState) {
            child.setVisibility(GONE);
        }
        mViews.put(viewTag, child);
    }

    @Override
    public void addView(View child, int index) {
        addContentV(child);
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        addContentV(child);
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        addContentV(child);
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        addContentV(child);
        super.addView(child, width, height);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        addContentV(child);
        return super.addViewInLayout(child, index, params);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        addContentV(child);
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        int useAnim = 0;
        if (mUseAnim) {
            useAnim = 1;
        }
        return new SaveState(parcelable, mCurrentState, useAnim);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SaveState saveState = (SaveState) state;
        if (saveState.useAnim == 1) {
            mUseAnim = true;
        } else {
            mUseAnim = false;
        }
        // 因为应用方向改变触发重绘后，重新初始化读取的 ViewState 是不准确的，所以要隐藏掉
        if (saveState.viewState != mCurrentState) {
            getStateView(mCurrentState).setVisibility(GONE);
            setViewState(saveState.viewState);
        }
        super.onRestoreInstanceState(saveState.getSuperState());
    }

    private static class SaveState extends BaseSavedState {

        private int viewState;
        /**
         * 布尔值存储居然没有api，只能存储布尔数组，故改成 int 记录
         * 1 使用动画
         * 2 不使用动画
         */
        private int useAnim;

        private SaveState(Parcel source) {
            super(source);
            viewState = source.readInt();
        }

        private SaveState(Parcelable superState, int viewState, int useAnim) {
            super(superState);
            this.viewState = viewState;
            this.useAnim = useAnim;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(viewState);
            out.writeInt(useAnim);
        }

        public static final Parcelable.Creator<SaveState> CREATE = new Parcelable.Creator<SaveState>() {

            @Override
            public SaveState createFromParcel(Parcel source) {
                return new SaveState(source);
            }

            @Override
            public SaveState[] newArray(int size) {
                return new SaveState[size];
            }
        };
    }

    /**
     * 切换默认状态的 View
     * @param state
     */
    public void setViewState(int state) {
        if (state <= VIEW_TAG) {
            throw new RuntimeException("ViewState 不在目标范围");
        }
        if (isAniming) {
            return;
        }
        showViewAnim(state);
    }

    public void setUseAnim(boolean useAnim) {
        this.mUseAnim = useAnim;
    }

    private void showViewAnim(int state) {
        View showView = getStateView(state);
        if (null == showView || state == mCurrentState) {
            return;
        }
        isAniming = true;
        View currentView = getStateView(mCurrentState);
        if (mUseAnim) {
            showAlpha(state, showView, currentView);
        } else {
            currentView.setVisibility(GONE);
            if (showView.getAlpha() == 0) {
                showView.setAlpha(1f);
            }
            showView.setVisibility(VISIBLE);
            mCurrentState = state;
            if (null != mListener) {
                mListener.onStateChanged(state);
            }
            isAniming = false;
        }
    }

    private void showAlpha(final int state, final View showView,
                           final View currentView) {
        ObjectAnimator currentAnim = ObjectAnimator.ofFloat(currentView, "alpha", 1, 0);
        currentAnim.setDuration(250L);
        final ObjectAnimator showAnim = ObjectAnimator.ofFloat(showView, "alpha", 0, 1);
        showAnim.setDuration(250L);
        showAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (null != mListener) {
                    mListener.onStateChanged(state);
                }
                isAniming = false;
            }
        });
        currentAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                currentView.setVisibility(GONE);
                showView.setVisibility(VISIBLE);
                showAnim.start();
                mCurrentState = state;
            }
        });
        currentAnim.start();
    }

    public int getCurrentState() {
        return mCurrentState;
    }

    public View getStateView(int state) {
        if (state <= VIEW_TAG) {
            throw new RuntimeException("ViewState 不在目标范围");
        }
        return mViews.get(state);
    }

    public void addUserView(int state, int layId) {
        setUserDefView(state, null, layId);
    }

    public void addUserView(int state, View view) {
        setUserDefView(state, view, -1);
    }

    private void setUserDefView(int state, View view, int layId) {
        if (state <= 0) {
            throw new RuntimeException("自定义的 ViewState TAG 必须大于 0");
        }
        if (null == view && layId != -1) {
            view = LayoutInflater.from(mContext).inflate(layId, this, false);
        }
        addViewToHash(view, state);
        addViewInLayout(view, -1, view.getLayoutParams());
    }

}
