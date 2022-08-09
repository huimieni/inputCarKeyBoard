package com.com.model.demo.app.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseDBActivity<DB extends ViewDataBinding>
        extends AppCompatActivity {
    public DB mBinding;
    public String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, createLayoutId());
        initView();
        initData(savedInstanceState);
    }


    public abstract int createLayoutId();


    public abstract void initView();


    public abstract void initData(@Nullable Bundle savedInstanceState);

}
