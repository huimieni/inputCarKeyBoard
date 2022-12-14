package com.widget.carkeyboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Window;

import androidx.annotation.ColorInt;

import com.widget.carkeyboard.engine.KeyboardEngine;
import com.widget.carkeyboard.view.InputView;
import com.widget.carkeyboard.view.KeyboardView;

public class PopupKeyboard {

    private final KeyboardView mKeyboardView;

    private KeyboardInputController mController;

    private boolean isDialog = false;

    public PopupKeyboard(Context context) {
        mKeyboardView = new KeyboardView(context);
    }

    public PopupKeyboard(Context context, @ColorInt int bubbleTextColor, ColorStateList okKeyBackgroundColor) {
        mKeyboardView = new KeyboardView(context);
        mKeyboardView.setBubbleTextColor(bubbleTextColor);
        mKeyboardView.setOkKeyTintColor(okKeyBackgroundColor);
    }

    public KeyboardView getKeyboardView() {
        return mKeyboardView;
    }

    public void attach(InputView inputView, final Activity activity) {
        isDialog = false;
        attach(inputView, activity.getWindow());
    }

    public void attach(InputView inputView, final Dialog dialog) {
        isDialog = true;
        attach(inputView, dialog.getWindow());
    }

    private void attach(InputView inputView, final Window window) {
        if (mController == null) {
            mController = KeyboardInputController
                    .with(mKeyboardView, inputView);
            mController.useDefaultMessageHandler();

            inputView.addOnFieldViewSelectedListener(new InputView.OnFieldViewSelectedListener() {
                @Override
                public void onSelectedAt(int index) {
                    show(window);
                }
            });
        }
    }

    public KeyboardInputController getController() {
        return checkAttachedController();
    }

    public KeyboardEngine getKeyboardEngine() {
        return mKeyboardView.getKeyboardEngine();
    }

    public void show(Activity activity) {
        show(activity.getWindow());
    }

    public void show(Window window) {
        checkAttachedController();
        PopupHelper.showToWindow(window, mKeyboardView, isDialog);
    }

    public void dismiss(Activity activity) {
        dismiss(activity.getWindow());
    }

    public void dismiss(Window window) {
        checkAttachedController();
        PopupHelper.dismissFromWindow(window);
    }

    public boolean isShown() {
        return mKeyboardView.isShown();
    }

    private KeyboardInputController checkAttachedController() {
        if (mController == null) {
            throw new IllegalStateException("Try attach() first");
        }
        return mController;
    }

}
