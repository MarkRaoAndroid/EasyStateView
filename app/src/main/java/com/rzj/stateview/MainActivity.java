package com.rzj.stateview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.rzj.view.EasyStateView;
import com.rzj.view.R;

public class MainActivity extends AppCompatActivity {

    private com.rzj.view.EasyStateView  mStateView;
    private Button mBtn;
    private Spinner mSpinner;
    private RadioGroup mRadioAnimation;
    private int mStateId;
    private static final int YFH = 1;
    private static final int DLRB = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListener();
    }

    private void initListener() {
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStateView.setViewState(mStateId);
            }
        });
        mRadioAnimation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.use_anima){
                    mStateView.setUseAnim(true);
                }else {
                    mStateView.setUseAnim(false);
                }
            }
        });
        //适配器
        ArrayAdapter  arrAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.spinner));
        //设置样式
        arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        mStateId = EasyStateView.VIEW_EMPTY;
                        break;
                    case 1:
                        mStateId = EasyStateView.VIEW_ERROR_DATA;
                        break;
                    case 2:
                        mStateId = EasyStateView.VIEW_ERROR_NET;
                        break;
                    case 3:
                        mStateId = EasyStateView.VIEW_LOADING;
                        break;
                    case 4:
                        mStateId = EasyStateView.VIEW_CONTENT;
                        break;
                    case 5:
                        mStateId = YFH;
                        break;
                    case 6:
                        mStateId = DLRB;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinner.setAdapter(arrAdapter);
    }

    private void initViews() {
        mBtn = findViewById(R.id.btn_view_state);
        mSpinner = findViewById(R.id.spinner_state_view);
        mStateView = findViewById(R.id.state_view);
        mRadioAnimation = findViewById(R.id.radio_animation);
        mStateId = mStateView.getCurrentState();
        mStateView.addUserView(YFH, R.layout.state_yfh);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.state_dlrb, mStateView, false);
        mStateView.addUserView(DLRB, view);
    }
}
