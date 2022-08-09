package com.com.model.demo.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.com.model.demo.R;
import com.com.model.demo.app.base.BaseDBActivity;
import com.com.model.demo.databinding.ActivityMainTestBinding;
import com.lxj.xpopup.XPopup;

/**
 * 首页页面
 */
public class MainActivity extends BaseDBActivity<ActivityMainTestBinding> {

    @Override
    public int createLayoutId() {
        return R.layout.activity_main_test;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding.tvMain.setOnClickListener(v ->
                new XPopup.Builder(this)
                        .asCustom(new TestPopup(this, "车牌", s -> {
                            Log.e(TAG, s);
                            mBinding.tvMain.setText(s);
                        })).show());
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

}
