[![OSL](https://gss2.bdstatic.com/-fo3dSag_xI4khGkpoWK1HF6hhy/baike/s%3D500/sign=f568d2e4f91fbe09185ec3145b610c30/5882b2b7d0a20cf4bf0e134d7d094b36adaf9982.jpg)](https://www.jianshu.com/u/a5102f480695)
#### 点击热巴进入我的博客

# EasyStateView

![Image text](https://upload-images.jianshu.io/upload_images/3027456-cbc32b9ffcd7f193.gif?imageMogr2/auto-orient/strip)
## 控件使用示例
```
  <com.rzj.view.EasyStateView
        android:id="@+id/state_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:esv_emptyView="@layout/state_empty"
        app:esv_errorDataView="@layout/state_data_error"
        app:esv_errorNetView="@layout/state_net_error"
        app:esv_loadingView="@layout/state_loading"
        app:esv_use_anim="true"
        app:esv_viewState="loading">

        <Button
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:text="内容" />

    </com.rzj.view.EasyStateView>
```
可以通过自定义属性来设置你的布局，中间放你的主题内容 View
还可以在代码里添加自定义的布局，这里控件内本身自定义了五种布局，通过 esv_viewState 来设置第一个显示在用户的 View
<br>1，内容 View 
<br>2，加载 View 
<br>3，数据异常( 数据异常指原本应该是有数据，但是服务器返回了错误的、不符合格式的数据 ) View
<br>4，网络异常 View
<br>5，数据为空 View
也可以监听 View 状态的变化
```
public class MainActivity extends AppCompatActivity implements EasyStateView.StateViewListener {

    private com.rzj.view.EasyStateView mStateView;
    
    private static final int YFH = 1;
    
    private static final int DLRB = 2;
    
    private int mStateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }


    private void initViews() {
        
        mStateView = findViewById(R.id.state_view);
        // 获取当前显示的 view 状态
        mStateId = mStateView.getCurrentState();
        // 代码动态添加 View , 注意 tag 值必须大于 0
        mStateView.addUserView(YFH, R.layout.state_yfh);
        
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.state_dlrb, mStateView, false);
        
        mStateView.addUserView(DLRB, view);
        // 监听 StateView 状态变化
        mStateView.setStateChangedListener(this);
    }

    @Override
    public void onStateChanged(int state) {
        Toast.makeText(MainActivity.this, String.valueOf(state), Toast.LENGTH_LONG).show();
    }
}
```
### 文档地址：<br>
### Control description address：<br>
https://www.jianshu.com/p/eaf71002e38f
