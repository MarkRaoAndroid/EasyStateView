package com.rzj.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
    // 用来存放自定义状态 View
    private ArrayMap<Integer, View> mViews;
    private View mEmptyView;
    private View mErrorDataView;
    private View mErrorNetView;
    private View mLoadingView;
    private View mCurrentView;
    private View mContentView;
    // 是否使用默认的过渡动画
    private boolean isAnimation;
    // 当前显示的 ViewType
    private int mCurrentType;
    private Context mContext;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VIEW_CONTENT, VIEW_LOADING, VIEW_ERROR_DATA, VIEW_ERROR_NET, VIEW_EMPTY})
    public @interface ViewState {
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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EasyStateView);
        mCurrentType = typedArray.getInt(R.styleable.EasyStateView_esv_viewState, VIEW_CONTENT);
        Log.e("init", getChildCount() +"  "+ mCurrentType);
        if(getChildCount() > 0){
            mContentView = getChildAt(0);
            if(mCurrentType != VIEW_CONTENT){
                mContentView.setVisibility(GONE);
            }else {
                mCurrentView = mContentView;
            }
        }
        int emptyResId = typedArray.getResourceId(R.styleable.EasyStateView_esv_emptyView, -1);
        if (emptyResId != -1) {
            mEmptyView = LayoutInflater.from(getContext()).inflate(emptyResId, this, false);
            if (mCurrentType != VIEW_EMPTY) {
                mEmptyView.setVisibility(GONE);
            } else {
                mCurrentView = mEmptyView;
            }
            addView(mEmptyView,mEmptyView.getLayoutParams());
        }
        int errorDataResId = typedArray.getResourceId(R.styleable.EasyStateView_esv_errorDataView, -1);
        if (errorDataResId != -1) {
            mErrorDataView = LayoutInflater.from(getContext()).inflate(errorDataResId, this, false);
            if (mCurrentType != VIEW_ERROR_DATA) {
                mErrorDataView.setVisibility(GONE);
            } else {
                mCurrentView = mErrorDataView;
            }
            addView(mErrorDataView, mErrorDataView.getLayoutParams());
        }
        int errorNetResId = typedArray.getResourceId(R.styleable.EasyStateView_esv_errorNetView, -1);
        if (errorNetResId != -1) {
            mErrorNetView = LayoutInflater.from(getContext()).inflate(errorNetResId, this, false);
            if (mCurrentType != VIEW_ERROR_NET) {
                mErrorNetView.setVisibility(GONE);
            } else {
                mCurrentView = mErrorNetView;
            }
            addView(mErrorNetView, mErrorNetView.getLayoutParams());
        }
        int loadingResId = typedArray.getResourceId(R.styleable.EasyStateView_esv_loadingView, -1);
        if (loadingResId != -1) {
            mLoadingView = LayoutInflater.from(getContext()).inflate(loadingResId, this, false);
            if (mCurrentType != VIEW_LOADING) {
                mLoadingView.setVisibility(GONE);
            } else {
                mCurrentView = mLoadingView;
            }
            addView(mLoadingView, mLoadingView.getLayoutParams());
        }
        isAnimation = typedArray.getBoolean(R.styleable.EasyStateView_esv_set_animation, true);
        typedArray.recycle();
    }

    /**
     * 切换默认状态的 View
     * @param state
     */
    public void setViewState(@ViewState int state) {
        switch (state) {
            case VIEW_CONTENT:
                showViewAnimator(mContentView);
                break;
            case VIEW_EMPTY:
                showViewAnimator(mEmptyView);
                break;
            case VIEW_ERROR_DATA:
                showViewAnimator(mErrorDataView);
                break;
            case VIEW_ERROR_NET:
                showViewAnimator(mErrorNetView);
                break;
            case VIEW_LOADING:
                showViewAnimator(mLoadingView);
                break;
        }
    }

    public void showViewAnimator(final View showView) {
        if(null == showView || null == mCurrentView
                || showView == mContentView){
            return;
        }
        ObjectAnimator currentAnim = ObjectAnimator.ofFloat(mCurrentView, "alpha", 1, 0);
        currentAnim.setDuration(300);
        currentAnim.start();
        currentAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator showAnim = ObjectAnimator.ofFloat(showView, "alpha", 0, 1);
                showAnim.setDuration(300);
                animation.start();
                mCurrentView = showView;
            }
        });
    }

    public void setUserDefViewState(int state){
        if (state <= 0) {
            throw new RuntimeException("自定义的 ViewState TAG 必须大于 0");
        }
        if(null != mViews){
            showViewAnimator(mViews.get(state));
        }
    }

    public View getUserDefView(int state){
        if (state <= 0) {
            throw new RuntimeException("自定义的 ViewState TAG 必须大于 0");
        }
        if(null != mViews && mViews.size() > 0){
            return mViews.get(state);
        }
        return null;
    }

    public void setUserDefView(int state, int layId) {
        setUserDefView(state,null, layId);
    }
    public void setUserDefView(int state, View view) {
        setUserDefView(state,view, -1);
    }

    private void setUserDefView(int state, View view, int layId) {
        if (state <= 0) {
            throw new RuntimeException("自定义的 ViewState TAG 必须大于 0");
        }
        if(null == view && layId != -1){
            view = LayoutInflater.from(mContext).inflate(layId, this, false);
        }
        if(null == mViews){
            mViews = new ArrayMap();
        }
        mViews.put(state, view);
    }

}
