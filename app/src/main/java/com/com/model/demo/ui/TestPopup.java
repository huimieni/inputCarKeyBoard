package com.com.model.demo.ui;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.com.model.demo.R;
import com.com.model.demo.databinding.PopupCarlicenceKbBinding;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.BottomPopupView;
import com.widget.carkeyboard.KeyboardInputController;


public class TestPopup extends BottomPopupView {
    PopupCarlicenceKbBinding mBinding;
    SimpleCallBack<String> callBack;
    String m = "";

    public TestPopup(@NonNull Context context) {
        super(context);
    }

    public TestPopup(@NonNull Context context, String m, SimpleCallBack<String> callBack) {
        super(context);
        this.m = m;
        this.callBack = callBack;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_carlicence_kb;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBinding = DataBindingUtil.bind(getPopupImplView());
        mBinding.cancel.setOnClickListener(v -> {
            dismiss();
        });
        if (!TextUtils.isEmpty(m)) {
            mBinding.carNo.updateNumber(m);
        }
        KeyboardInputController  mController = KeyboardInputController.with(mBinding.kbv, mBinding.carNo);
        mController.useDefaultMessageHandler();
        mBinding.kbv.getKeyboardEngine().setHideOKKey(true);
        mBinding.confirm.setOnClickListener(v -> {
            if (callBack != null) {
                if (mBinding.carNo.getNumber().length() >= 7) {
                    callBack.onResult(mBinding.carNo.getNumber());
                    dismiss();
                } else {

                }
            }

        });

        mBinding.carNo.setFocusToInput();
    }

    @Override
    protected int getMaxWidth() {
        return super.getMaxWidth();
    }

    @Override
    protected int getMaxHeight() {
        return super.getMaxHeight();
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return super.getPopupAnimator();
    }

    protected int getPopupWidth() {
        return 0;
    }

    protected int getPopupHeight() {
        return 0;
    }
}