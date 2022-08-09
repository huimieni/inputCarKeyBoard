package com.com.model.demo.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.com.model.demo.R;
import com.com.model.demo.app.base.BaseDBActivity;
import com.com.model.demo.databinding.ActivityWelcomeBinding;


public class WelcomeActivity extends BaseDBActivity<ActivityWelcomeBinding> {

    @Override
    public int createLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void initView() {
        mBinding.getRoot().postDelayed(this::into, 1000);
    }


    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
    }

    private void into() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
