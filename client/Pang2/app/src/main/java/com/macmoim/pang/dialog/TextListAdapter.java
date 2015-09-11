package com.macmoim.pang.dialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.macmoim.pang.dialog.typedef.GravityEnum;
import com.macmoim.pang.dialog.ui.TintHelper;
import com.macmoim.pang.R;

/**
 * Created by P10452 on 2015-09-05.
 */
public class TextListAdapter extends BaseAdapter {
    private final ExtDialog dialog;

    @LayoutRes
    private final int layout;

    private final GravityEnum itemGravity;
    public RadioButton mRadioButton;
    public boolean mInitRadio;

    public TextListAdapter(ExtDialog dialog, @LayoutRes int layout) {
        this.dialog = dialog;
        this.layout = layout;
        this.itemGravity = dialog.mBuilder.ListItemsGravity;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return dialog.mBuilder.ListItems != null ? dialog.mBuilder.ListItems.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return dialog.mBuilder.ListItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View getView(final int index, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(dialog.getContext()).inflate(layout, parent, false);
        }

        TextView _Tv = (TextView) view.findViewById(R.id.title);
        switch (dialog.mListType) {
            case SINGLE: {
                @SuppressLint("CutPasteId")
                RadioButton radio = (RadioButton) view.findViewById(R.id.control);
                boolean selected = dialog.mBuilder.SelectedIndex == index;
                TintHelper.SetTint(radio, dialog.mBuilder.WidgetColor);
                radio.setChecked(selected);
                if (selected && mInitRadio)
                    mRadioButton = radio;
                break;
            }
            case MULTI: {
                @SuppressLint("CutPasteId")
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.control);
                boolean selected = dialog.SelectedIndicesList.contains(index);
                TintHelper.SetTint(checkbox, dialog.mBuilder.WidgetColor);
                checkbox.setChecked(selected);
                break;
            }
        }
        _Tv.setText(dialog.mBuilder.ListItems[index]);
        _Tv.setTextColor(dialog.mBuilder.ListItemColor);
        dialog.SetTypeFace(_Tv, dialog.mBuilder.RegularFont);

        view.setTag(index + ":" + dialog.mBuilder.ListItems[index]);
        SetUpGravity((ViewGroup) view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup _Group = (ViewGroup) view;

            if (_Group.getChildCount() == 2) {
                // Remove circular selector from check boxes and radio buttons on Lollipop
                if (_Group.getChildAt(0) instanceof CompoundButton) {
                    _Group.getChildAt(0).setBackground(null);
                } else if (_Group.getChildAt(1) instanceof CompoundButton) {
                    _Group.getChildAt(1).setBackground(null);
                }
            }
        }

        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void SetUpGravity(ViewGroup view) {
        final LinearLayout _ItemRoot = (LinearLayout) view;
        final int _GravityInt = itemGravity.GetGravity();

        _ItemRoot.setGravity(_GravityInt | Gravity.CENTER_VERTICAL);

        if (view.getChildCount() == 2) {
            if (itemGravity == GravityEnum.END && !IsRTL() && view.getChildAt(0) instanceof CompoundButton) {
                CompoundButton _First = (CompoundButton) view.getChildAt(0);
                view.removeView(_First);

                TextView _Second = (TextView) view.getChildAt(0);
                view.removeView(_Second);
                _Second.setPadding(_Second.getPaddingRight(), _Second.getPaddingTop(),
                        _Second.getPaddingLeft(), _Second.getPaddingBottom());

                view.addView(_Second);
                view.addView(_First);
            } else if (itemGravity == GravityEnum.START && IsRTL() && view.getChildAt(1) instanceof CompoundButton) {
                CompoundButton _First = (CompoundButton) view.getChildAt(1);
                view.removeView(_First);

                TextView _Second = (TextView) view.getChildAt(0);
                view.removeView(_Second);
                _Second.setPadding(_Second.getPaddingRight(), _Second.getPaddingTop(),
                        _Second.getPaddingRight(), _Second.getPaddingBottom());

                view.addView(_First);
                view.addView(_Second);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean IsRTL() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false;
        }

        Configuration _Config = dialog.GetBuilder().GetContext().getResources().getConfiguration();
        return _Config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
}