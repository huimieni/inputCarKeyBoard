package com.widget.carkeyboard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.qingsong.car.keyboard.R;

public class InputView extends ConstraintLayout {
    private static final String NAME_SPACE = "http://schemas.android.com/apk/res/android";
    private static final String TAG = InputView.class.getName();
    private TextView energyLabel;
    private LinearLayout dotContainer;

    private static final String KEY_INIT_NUMBER = "pwk.keyboard.key:init.number";

    private final HashMap<String, Object> mKeyMap = new HashMap<>();

    private final Set<OnFieldViewSelectedListener> mOnFieldViewSelectedListeners = new HashSet<>(4);

    private final FieldViewGroup mFieldViewGroup;

    /**
     * 点击选中输入框时，只可以从左到右顺序输入，不可隔位
     */
    private final OnClickListener mOnFieldViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final TextView field = mFieldViewGroup.getFirstEmptyField();
            final ClickMeta clickMeta = getClickMeta(field);
            Log.d(TAG, "设置点击信息: " + clickMeta);
            setFieldViewSelected(field);
            // 触发选中事件
            for (OnFieldViewSelectedListener listener : mOnFieldViewSelectedListeners) {
                listener.onSelectedAt(clickMeta.clickIndex);
            }
        }
    };

    /**
     * 主动设置焦点
     */
    public void setFocusToInput() {
        final TextView field = mFieldViewGroup.getFirstEmptyField();
        final ClickMeta clickMeta = getClickMeta(field);
        Log.d(TAG, "设置点击信息: " + clickMeta);
        setFieldViewSelected(field);
        // 触发选中事件
        for (OnFieldViewSelectedListener listener : mOnFieldViewSelectedListeners) {
            listener.onSelectedAt(clickMeta.clickIndex);
        }
    }

    @Nullable
    private Drawable mSelectedDrawable;
    @Nullable
    private Drawable mUnSelectedDrawable;
    @Nullable
    private Drawable mProvinceSelectedDrawable;
    @Nullable
    private Drawable mProvinceUnSelectedDrawable;
    @Nullable
    private Drawable mEnergySelectedDrawable;
    @Nullable
    private Drawable mEnergyUnSelectedDrawable;

    private float padding;
    private int labelVisible;
    private int dotVisible;

    public InputView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.pwkInputStyle);
    }

    public InputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.pwk_input_view, this);
        int backGround = attrs.getAttributeResourceValue(NAME_SPACE, "background", -1);
        //background包括color和Drawable,这里分开取值
        if (backGround != -1) {
            setBackgroundResource(backGround);
        } else {
            setBackgroundColor(Color.parseColor("#00000000"));
        }
        mFieldViewGroup = new FieldViewGroup() {
            @Override
            protected TextView findViewById(int id) {
                return InputView.this.findViewById(id);
            }
        };
        energyLabel = findViewById(R.id.tvEnergyLabel);
        energyLabel.setTextColor(Color.WHITE);
        energyLabel.setBackgroundResource(R.drawable.ic_car_energy_bg_selected_top);
        dotContainer = findViewById(R.id.llDot);
        onInited(context, attrs, defStyleAttr);
    }

    private void onInited(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InputView, defStyleAttr, 0);
        final float textSize = ta.getDimension(R.styleable.InputView_pwkInputTextSize, 9);
        int textColor = ta.getColor(R.styleable.InputView_pwkInputTextColor, Color.parseColor("#333333"));
        int provinceTextColor = ta.getColor(R.styleable.InputView_pwkProvinceTextColor, textColor);
        int energyTextColor = ta.getColor(R.styleable.InputView_pwkEnergyTextColor, textColor);
        //
        mSelectedDrawable = ta.getDrawable(R.styleable.InputView_pwkItemSelectedDrawable);
        mUnSelectedDrawable = ta.getDrawable(R.styleable.InputView_pwkItemUnSelectedDrawable);
        //
        mProvinceSelectedDrawable = ta.getDrawable(R.styleable.InputView_pwkProvinceSelectedDrawable);
        mProvinceUnSelectedDrawable = ta.getDrawable(R.styleable.InputView_pwkProvinceUnSelectedDrawable);
        //
        mEnergySelectedDrawable = ta.getDrawable(R.styleable.InputView_pwkEnergySelectedDrawable);
        mEnergyUnSelectedDrawable = ta.getDrawable(R.styleable.InputView_pwkEnergyUnSelectedDrawable);
        padding = ta.getDimension(R.styleable.InputView_pwkItemPadding, 0);
        labelVisible = ta.getInt(R.styleable.InputView_pwkEnergyLabelVisible, 0);
        dotVisible = ta.getInt(R.styleable.InputView_pwkDotVisible, 0);
        ta.recycle();
        //
        if (dotVisible == 0) {
            dotContainer.setVisibility(GONE);
        } else {
            dotContainer.setVisibility(VISIBLE);
        }
        //
        if (labelVisible == 0) {
            energyLabel.setVisibility(GONE);
        } else if (labelVisible == 1) {
            energyLabel.setVisibility(VISIBLE);
        }
        //
        mFieldViewGroup.setupAllFieldsTextSize(textSize);
        mFieldViewGroup.setupAllFieldsOnClickListener(mOnFieldViewClickListener);
        mFieldViewGroup.changeTo8Fields();
        //
        mFieldViewGroup.setHorizontalPadding(padding);
        //设置字体颜色
        mFieldViewGroup.setTextColor(textColor);
        mFieldViewGroup.setProvinceTextColor(provinceTextColor);
        mFieldViewGroup.setEnergyTextColor(energyTextColor);
    }

    /**
     * 设置文本字符到当前选中的输入框
     *
     * @param text 文本字符
     */
    public void updateSelectedCharAndSelectNext(final String text) {
        final TextView selected = mFieldViewGroup.getFirstSelectedFieldOrNull();
        if (selected != null) {
            selected.setText(text);
            performNextFieldViewBy(selected);
        }
    }

    /**
     * 从最后一位开始删除
     */
    public void removeLastCharOfNumber() {
        final TextView last = mFieldViewGroup.getLastFilledFieldOrNull();
        if (last != null) {
            last.setText(null);
            performFieldViewSetToSelected(last);
        }
    }

    /**
     * @return 返回当前输入组件是否为完成状态
     */
    public boolean isCompleted() {
        // 所有显示的输入框都被填充了车牌号码，即输入完成状态
        return mFieldViewGroup.isAllFieldsFilled();
    }

    /**
     * 返回当前车牌号码是否被修改过。
     * 与通过 updateNumber 方法设置的车牌号码来对比。
     *
     * @return 是否修改过
     */
    public boolean isNumberChanged() {
        final String current = getNumber();
        return !current.equals(String.valueOf(mKeyMap.get(KEY_INIT_NUMBER)));
    }

    /**
     * 更新/重置车牌号码
     *
     * @param number 车牌号码
     */
    public void updateNumber(String number) {
        // 初始化车牌
        mKeyMap.put(KEY_INIT_NUMBER, number);
        mFieldViewGroup.setTextToFields(number);
    }

    /**
     * 获取当前已输入的车牌号码
     *
     * @return 车牌号码
     */
    public String getNumber() {
        return mFieldViewGroup.getText();
    }

    /**
     * 选中第一个输入框
     */
    public void performFirstFieldView() {
        performFieldViewSetToSelected(mFieldViewGroup.getFieldAt(0));
    }

    /**
     * 选中最后一个可等待输入的输入框。
     * 如果全部为空，则选中第1个输入框。
     */
    public void performLastPendingFieldView() {
        final TextView field = mFieldViewGroup.getLastFilledFieldOrNull();
        if (field != null) {
            performNextFieldViewBy(field);
        } else {
            performFieldViewSetToSelected(mFieldViewGroup.getFieldAt(0));
        }
    }

    /**
     * 选中下一个输入框。
     * 如果当前输入框是空字符，则重新触发当前输入框的点击事件。
     */
    public void performNextFieldView() {
        final ClickMeta meta = getClickMeta(null);
        if (meta.selectedIndex >= 0) {
            final TextView current = mFieldViewGroup.getFieldAt(meta.selectedIndex);
            if (!TextUtils.isEmpty(current.getText())) {
                performNextFieldViewBy(current);
            } else {
                performFieldViewSetToSelected(current);
            }
        }
    }

    /**
     * 重新触发当前输入框选中状态
     */
    public void rePerformCurrentFieldView() {
        final ClickMeta clickMeta = getClickMeta(null);
        if (clickMeta.selectedIndex >= 0) {
            performFieldViewSetToSelected(mFieldViewGroup.getFieldAt(clickMeta.selectedIndex));
        }
    }

    /**
     * 设置第8位输入框显示状态
     *
     * @param setToShow8thField 是否显示
     */
    public void set8thVisibility(boolean setToShow8thField) {
        final boolean changed;
        if (setToShow8thField) {
            changed = mFieldViewGroup.changeTo8Fields();
        } else {
            changed = mFieldViewGroup.changeTo7Fields();
        }
        if (changed) {
            final TextView field = mFieldViewGroup.getFirstEmptyField();
            if (field != null) {
                Log.d(TAG, "[@@ FieldChanged @@] FirstEmpty.tag: " + field.getTag());
                setFieldViewSelected(field);
            }
        }
    }

    /**
     * 是否最后一位被选中状态。
     *
     * @return 是否选中
     */
    public boolean isLastFieldViewSelected() {
        return mFieldViewGroup.getLastField().isSelected();
    }

    public InputView addOnFieldViewSelectedListener(OnFieldViewSelectedListener listener) {
        mOnFieldViewSelectedListeners.add(listener);
        return this;
    }

    private void performFieldViewSetToSelected(TextView target) {
        Log.d(TAG, "[== FastPerform ==] Btn.text: " + target.getText());
        // target.performClick();
        // 自动触发的，不要使用Android内部处理，太慢了。
        mOnFieldViewClickListener.onClick(target);
        setFieldViewSelected(target);
    }

    private void performNextFieldViewBy(TextView current) {
        final int nextIndex = mFieldViewGroup.getNextIndexOfField(current);
        Log.d(TAG, "[>> NextPerform >>] Next.Btn.idx: " + nextIndex);
        performFieldViewSetToSelected(mFieldViewGroup.getFieldAt(nextIndex));
    }

    private void setFieldViewSelected(TextView target) {
        for (TextView btn : mFieldViewGroup.getAvailableFields()) {
            btn.setSelected((btn == target));
        }
        invalidate();
    }

    private ClickMeta getClickMeta(TextView clicked) {
        int selectedIndex = -1;
        int currentIndex = -1;
        final TextView[] fields = mFieldViewGroup.getAvailableFields();
        for (int i = 0; i < fields.length; i++) {
            final TextView field = fields[i];
            if (currentIndex < 0 && field == clicked) {
                currentIndex = i;
            }
            if (selectedIndex < 0 && field.isSelected()) {
                selectedIndex = i;
            }
        }
        return new ClickMeta(selectedIndex, currentIndex);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        invalidateSelectedDrawable(canvas);
    }

    private void invalidateSelectedDrawable(Canvas canvas) {
        if (mSelectedDrawable == null) {
            return;
        }

        final int count = mFieldViewGroup.getCount();
        View selected;
        for (int i = 0; i < count; i++) {
            selected = mFieldViewGroup.getFieldAt(i);

            if (selected.getVisibility() == View.VISIBLE && selected.isSelected()) {
                selected.setBackground(mSelectedDrawable);
            } else {
                selected.setBackground(mUnSelectedDrawable);
            }
            //province
            if (selected == mFieldViewGroup.getFieldAt(0)) {
                if (selected.getVisibility() == View.VISIBLE && selected.isSelected()) {
                    if (mProvinceSelectedDrawable != null) {
                        selected.setBackground(mProvinceSelectedDrawable);
                    }
                } else {
                    if (mProvinceUnSelectedDrawable != null) {
                        selected.setBackground(mProvinceUnSelectedDrawable);
                    }
                }
            }

            //energy
            if (selected == mFieldViewGroup.getFieldAt(7)) {
                if (selected.getVisibility() == View.VISIBLE && selected.isSelected()) {
                    if (mEnergySelectedDrawable != null) {
                        selected.setBackground(mEnergySelectedDrawable);
                    }
                } else {
                    if (mEnergyUnSelectedDrawable != null) {
                        selected.setBackground(mEnergyUnSelectedDrawable);
                    }
                }
            }

        }
    }

    //////

    public interface OnFieldViewSelectedListener {

        void onSelectedAt(int index);
    }

    //////////

    private static class ClickMeta {

        /**
         * 当前输入框已选中的序号
         */
        final int selectedIndex;

        /**
         * 当前点击的输入框序号
         */
        final int clickIndex;

        private ClickMeta(int selectedIndex, int currentIndex) {
            this.selectedIndex = selectedIndex;
            this.clickIndex = currentIndex;
        }

        @Override
        public String toString() {
            return "ClickMeta{" +
                    "selectedIndex=" + selectedIndex +
                    ", clickIndex=" + clickIndex +
                    '}';
        }
    }

}
