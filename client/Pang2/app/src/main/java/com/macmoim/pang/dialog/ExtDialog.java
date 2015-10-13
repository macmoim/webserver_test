package com.macmoim.pang.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.macmoim.pang.R;
import com.macmoim.pang.dialog.base.DialogButton;
import com.macmoim.pang.dialog.base.RootLayout;
import com.macmoim.pang.dialog.typedef.DialogButtonAction;
import com.macmoim.pang.dialog.typedef.GravityEnum;
import com.macmoim.pang.dialog.typedef.TypeFaceHelper;
import com.macmoim.pang.dialog.ui.TintHelper;
import com.macmoim.pang.dialog.util.Utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by P10452 on 2015-09-05.
 */
public class ExtDialog extends DialogBase implements View.OnClickListener, AdapterView.OnItemClickListener {
    protected final Builder mBuilder;

    protected View TitleFrameView = null;
    protected ImageView TitleIv = null;
    protected TextView TitleTv = null;

    protected ScrollView MessageSv = null;
    protected TextView MessageTv = null;

    protected DialogButton PositiveButton = null;
    protected DialogButton NegativeButton = null;

    protected ListView ListItemView = null;
    protected ListType mListType = null;
    protected List<Integer> SelectedIndicesList = null; // multi

    protected ProgressBar mProgress = null;
    protected TextView mProgressLabel = null;
    protected TextView mProgressMinMax = null;

    protected EditText InputEdText = null;
    protected TextView InputMinMaxTv = null;

    protected FrameLayout CustomViewFrame = null;

    public final Builder GetBuilder() {
        return mBuilder;
    }

    @SuppressLint("InflateParams")
    protected ExtDialog(Builder builder) {
        super(builder.BuilderContext, DialogInit.GetTheme(builder));
        mHandler = new Handler();
        mBuilder = builder;

        final LayoutInflater _Inflater = LayoutInflater.from(builder.BuilderContext);
        vExtDialog = (RootLayout) _Inflater.inflate(DialogInit.GetInflateLayout(builder), null);
        DialogInit.Init(this);
    }

    public final void SetTypeFace(TextView Target, Typeface Type) {
        if (Type == null) {
            return;
        }

        int _Flags = Target.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG;
        Target.setPaintFlags(_Flags);
        Target.setTypeface(Type);
    }

    protected final void CheckIfListInitScroll() {
        if (ListItemView == null) {
            return;
        }

        ListItemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation
                    ListItemView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    ListItemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                if (mListType == ListType.SINGLE || mListType == ListType.MULTI) {
                    int _SelectedIndex;
                    if (mListType == ListType.SINGLE) {
                        if (mBuilder.SelectedIndex < 0) {
                            return;
                        }
                        _SelectedIndex = mBuilder.SelectedIndex;
                    } else {
                        if (mBuilder.SelectedIndices == null || mBuilder.SelectedIndices.length == 0) {
                            return;
                        }
                        List<Integer> _IndicesList = Arrays.asList(mBuilder.SelectedIndices);
                        Collections.sort(_IndicesList);
                        _SelectedIndex = _IndicesList.get(0);
                    }
                    if (ListItemView.getLastVisiblePosition() < _SelectedIndex) {
                        final int _TotalVisible = ListItemView.getLastVisiblePosition() - ListItemView.getFirstVisiblePosition();
                        // Scroll so that the selected index appears in the middle (vertically) of the listView
                        int _ScrollIndex = _SelectedIndex - (_TotalVisible / 2);
                        if (_ScrollIndex < 0) {
                            _ScrollIndex = 0;
                        }

                        final int _Index = _ScrollIndex;
                        ListItemView.post(new Runnable() {
                            @Override
                            public void run() {
                                ListItemView.requestFocus();
                                ListItemView.setSelection(_Index);
                            }
                        });
                    }
                }
            }
        });
    }

    protected final void InvalidateList() {
        if (ListItemView == null) {
            return;
        } else if ((mBuilder.ListItems == null || mBuilder.ListItems.length == 0) && (mBuilder.ListViewAdapter == null)) {
            return;
        }

        // set up list with adapter
        ListItemView.setAdapter(mBuilder.ListViewAdapter);

        if (mListType != null || mBuilder.ListCallbackCustom != null) {
            ListItemView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mBuilder.ListCallbackCustom != null) {
            // Custom Adapter
            CharSequence _Text = null;
            if (view instanceof TextView) {
                _Text = ((TextView) view).getText();
            }
            mBuilder.ListCallbackCustom.OnSelection(this, view, position, _Text);
        } else if (mListType == null || mListType == ListType.REGULAR) {
            // Default Adapter, non choice mode
            if (mBuilder.AutoDismiss) {
                // If auto dismiss is enabled, dismiss the dialog when a list item is selected
                dismiss();
            }
            mBuilder.ListCallBack.OnSelection(this, view, position, mBuilder.ListItems[position]);
        } else {
            // Default Adapter, choice mode
            if (mListType == ListType.MULTI) {
                final boolean _ShouldBeChecked = !SelectedIndicesList.contains(Integer.valueOf(position));
                final CheckBox _Cb = (CheckBox) view.findViewById(R.id.control);
                if (_ShouldBeChecked) {
                    // Add the selection to the states first so the callback includes it (when AlwaysCallMultiChoiceCallback)
                    SelectedIndicesList.add(position);
                    if (mBuilder.AlwaysCallMultiChoiceCallback) {
                        // If the checkbox wasn't previously selected, and the callback returns true, add it to the states and check it
                        if (SendMultichoiceCallback()) {
                            _Cb.setChecked(true);
                        } else {
                            // The callback cancelled selection, remove it from the states
                            SelectedIndicesList.remove(Integer.valueOf(position));
                        }
                    } else {
                        // The callback was not used to check if selection is allowed, just select it
                        _Cb.setChecked(true);
                    }
                } else {
                    // The checkbox was unchecked
                    SelectedIndicesList.remove(Integer.valueOf(position));
                    _Cb.setChecked(false);
                    if (mBuilder.AlwaysCallMultiChoiceCallback)
                        SendMultichoiceCallback();
                }
            } else if (mListType == ListType.SINGLE) {
                boolean _AllowSelection = true;
                final TextListAdapter _Adapter = (TextListAdapter) mBuilder.ListViewAdapter;
                final RadioButton _Rb = (RadioButton) view.findViewById(R.id.control);

                if (mBuilder.AutoDismiss && mBuilder.PositiveText == null) {
                    // If auto dismiss is enabled, and no action button is visible to approve the selection, dismiss the dialog
                    dismiss();
                    // Don't allow the selection to be updated since the dialog is being dismissed anyways
                    _AllowSelection = false;
                    // Update selected index and send callback
                    mBuilder.SelectedIndex = position;
                    SendSingleChoiceCallback(view);
                } else if (mBuilder.AlwaysCallSingleChoiceCallback) {
                    int oldSelected = mBuilder.SelectedIndex;
                    // Temporarily set the new index so the callback uses the right one
                    mBuilder.SelectedIndex = position;
                    // Only allow the radio button to be checked if the callback returns true
                    _AllowSelection = SendSingleChoiceCallback(view);
                    // Restore the old selected index, so the state is updated below
                    mBuilder.SelectedIndex = oldSelected;
                }
                // Update the checked states
                if (_AllowSelection && mBuilder.SelectedIndex != position) {
                    mBuilder.SelectedIndex = position;
                    // Uncheck the previously selected radio button
                    if (_Adapter.mRadioButton == null) {
                        _Adapter.mInitRadio = true;
                        _Adapter.notifyDataSetChanged();
                    }
                    if (_Adapter.mRadioButton != null)
                        _Adapter.mRadioButton.setChecked(false);
                    // Check the newly selected radio button
                    _Rb.setChecked(true);
                    _Adapter.mRadioButton = _Rb;
                }
            }
        }
    }

    public static class NotImplementedException extends Error {
        public NotImplementedException(@SuppressWarnings("SameParameterValue") String message) {
            super(message);
        }
    }

    public static class DialogException extends WindowManager.BadTokenException {
        public DialogException(@SuppressWarnings("SameParameterValue") String message) {
            super(message);
        }
    }

    protected final Drawable GetListSelector() {
        if (mBuilder.ListSelector != -1) {
            return ResourcesCompat.getDrawable(mBuilder.BuilderContext.getResources(), mBuilder.ListSelector, null);
        }

        final Drawable d = Utils.ResolveDrawable(mBuilder.BuilderContext, R.attr.ext_dialog_list_selector);

        if (d != null) {
            return d;
        }

        return Utils.ResolveDrawable(getContext(), R.attr.ext_dialog_list_selector);
    }

    protected Drawable GetButtonSelector(DialogButtonAction which) {
        if (mBuilder.ForceStacking) {
            if (mBuilder.BtnSelectorStacked != -1) {
                return ResourcesCompat.getDrawable(mBuilder.BuilderContext.getResources(), mBuilder.BtnSelectorStacked, null);
            }
            final Drawable d = Utils.ResolveDrawable(mBuilder.BuilderContext, R.attr.ext_dialog_btn_stacked_selector);
            if (d != null) {
                return d;
            }
            return Utils.ResolveDrawable(getContext(), R.attr.ext_dialog_btn_stacked_selector);
        } else {
            switch (which) {
                default: {
                    if (mBuilder.BtnSelectorPositive != -1) {
                        return ResourcesCompat.getDrawable(mBuilder.BuilderContext.getResources(), mBuilder.BtnSelectorPositive, null);
                    }
                    final Drawable d = Utils.ResolveDrawable(mBuilder.BuilderContext, R.attr.ext_dialog_btn_positive_selector);
                    if (d != null) {
                        return d;
                    }
                    return Utils.ResolveDrawable(getContext(), R.attr.ext_dialog_btn_positive_selector);
                }
                case NEGATIVE: {
                    if (mBuilder.BtnSelectorNegative != -1) {
                        return ResourcesCompat.getDrawable(mBuilder.BuilderContext.getResources(), mBuilder.BtnSelectorNegative, null);
                    }
                    final Drawable d = Utils.ResolveDrawable(mBuilder.BuilderContext, R.attr.ext_dialog_btn_negative_selector);
                    if (d != null) {
                        return d;
                    }
                    return Utils.ResolveDrawable(getContext(), R.attr.ext_dialog_btn_negative_selector);
                }
            }
        }
    }

    private boolean SendSingleChoiceCallback(View v) {
        CharSequence _Text = null;
        if (mBuilder.SelectedIndex >= 0) {
            _Text = mBuilder.ListItems[mBuilder.SelectedIndex];
        }
        return mBuilder.ListCallBackSingleChoice.OnSelection(this, v, mBuilder.SelectedIndex, _Text);
    }

    private boolean SendMultichoiceCallback() {
        Collections.sort(SelectedIndicesList); // make sure the indicies are in order
        List<CharSequence> _SelectedTitles = new ArrayList<>();
        for (Integer i : SelectedIndicesList) {
            _SelectedTitles.add(mBuilder.ListItems[i]);
        }
        return mBuilder.ListCallBackMultiChoice.OnSelection(this,
                SelectedIndicesList.toArray(new Integer[SelectedIndicesList.size()]),
                _SelectedTitles.toArray(new CharSequence[_SelectedTitles.size()]));
    }

    @Override
    public final void onClick(View v) {
        DialogButtonAction tag = (DialogButtonAction) v.getTag();
        switch (tag) {
            case POSITIVE: {
                if (mBuilder.ButtonCallBack != null) {
                    mBuilder.ButtonCallBack.OnAny(this);
                    mBuilder.ButtonCallBack.OnPositive(this);
                }
                if (mBuilder.ListCallBackSingleChoice != null) {
                    SendSingleChoiceCallback(v);
                }
                if (mBuilder.ListCallBackMultiChoice != null) {
                    SendMultichoiceCallback();
                }
                if (mBuilder.InputCallback != null && InputEdText != null && !mBuilder.AlwaysCallInputCallback) {
                    mBuilder.InputCallback.OnInput(this, InputEdText.getText());
                }
                if (mBuilder.AutoDismiss) {
                    dismiss();
                }
                break;
            }
            case NEGATIVE: {
                if (mBuilder.ButtonCallBack != null) {
                    mBuilder.ButtonCallBack.OnAny(this);
                    mBuilder.ButtonCallBack.OnNegative(this);
                }
                if (mBuilder.AutoDismiss) {
                    dismiss();
                }
                break;
            }
        }
    }

    public static class Builder {
        protected final Context BuilderContext;
        // set font
        protected Typeface MediumFont = null;
        protected Typeface RegularFont = null;
        // set dialog bg, divider
        protected int DialogBgColor = -1;
        protected int DividerColor = -1;
        // title area : top
        protected CharSequence Title = null;
        protected int TitleFrameColor = -1;
        protected Drawable TitleIcon = null;
        protected int TitleColor = -1;
        protected GravityEnum TitleGravity = GravityEnum.START;
        // message area : middle
        protected CharSequence MessageText = null;
        protected int MessageColor = -1;
        protected float MessageLineSpacing = 1.2f;
        protected GravityEnum MessageGravity = GravityEnum.START;
        // button area :down
        protected boolean ForceStacking = false;
        protected GravityEnum BtnStackedGravity = GravityEnum.END;
        protected CharSequence PositiveText = null;
        protected ColorStateList PositiveColor = null;
        protected CharSequence NegativeText = null;
        protected ColorStateList NegativeColor = null;
        @DrawableRes
        protected int BtnSelectorStacked = -1;
        @DrawableRes
        protected int BtnSelectorPositive = -1;
        @DrawableRes
        protected int BtnSelectorNegative = -1;
        protected ButtonCallback ButtonCallBack = null;
        // etc area : list, custom view, input, progress
        protected int WidgetColor = -1;
        // list view
        protected CharSequence[] ListItems = null;
        protected int ListItemColor = -1;
        protected GravityEnum ListItemsGravity = GravityEnum.START;
        protected ListAdapter ListViewAdapter = null;
        @DrawableRes
        protected int ListSelector = -1;
        protected ListCallback ListCallBack = null;
        protected ListCallbackSingleChoice ListCallBackSingleChoice = null;
        protected int SelectedIndex = -1;
        protected boolean AlwaysCallSingleChoiceCallback = false;
        protected ListCallbackMultiChoice ListCallBackMultiChoice = null;
        protected Integer[] SelectedIndices = null;
        protected boolean AlwaysCallMultiChoiceCallback = false;
        protected ListCallback ListCallbackCustom = null;
        // progress
        protected boolean ProgressCircleType = false;
        protected int ProgressBarType = -2;
        protected int ProgressMax = 0;
        protected boolean ShowMinMax = false;
        protected String ProgressNumberFormat = null;
        protected NumberFormat ProgressPercentFormat = null;
        // input
        protected CharSequence InputPrefill = null;
        protected CharSequence InputHint = null;
        protected InputCallback InputCallback = null;
        protected boolean InputAllowEmpty = false;
        protected int InputType = -1;
        protected boolean AlwaysCallInputCallback = false;
        protected int InputMaxLength = -1;
        protected int InputMaxLengthErrorColor = 0;
        // custum view
        protected View CustomViewType = null;
        protected boolean WrapCustomViewInScroll = false;
        // listener
        protected DialogInterface.OnShowListener ShowListener = null;
        protected DialogInterface.OnCancelListener CancelListener = null;
        protected DialogInterface.OnDismissListener DismissListener = null;
        protected DialogInterface.OnKeyListener KeyListener = null;

        protected boolean Cancelable = true;
        protected boolean AutoDismiss = true;

        public final Context GetContext() {
            return BuilderContext;
        }

        public Builder(@NonNull Context context) {
            this.BuilderContext = context;

            // font set
            final String _MediumFont = Utils.ResolveString(context, R.attr.ext_dialog_medium_font);
            final String _RegularFont = Utils.ResolveString(context, R.attr.ext_dialog_regular_font);
            TypeFace(_MediumFont, _RegularFont);

            if (this.MediumFont == null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.MediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL);
                    } else {
                        this.MediumFont = Typeface.create("sans-serif", Typeface.BOLD);
                    }
                } catch (Exception ignored) {

                }
            }
            if (this.RegularFont == null) {
                try {
                    this.RegularFont = Typeface.create("sans-serif", Typeface.NORMAL);
                } catch (Exception ignored) {

                }
            }

            this.ProgressPercentFormat = NumberFormat.getPercentInstance();
            this.ProgressNumberFormat = "%1d/%2d";

            this.ListItemsGravity = Utils.ResolveGravityEnum(context, R.attr.ext_dialog_items_gravity, this.ListItemsGravity);
        }

        public Builder TypeFace(@Nullable Typeface Medium, @Nullable Typeface Regular) {
            this.MediumFont = Medium;
            this.RegularFont = Regular;
            return this;
        }

        public Builder TypeFace(@Nullable String Medium, @Nullable String Regular) {
            if (Medium != null) {
                this.MediumFont = TypeFaceHelper.Get(this.BuilderContext, Medium);
                if (this.MediumFont == null) {
                    throw new IllegalArgumentException("no font asset found for " + Medium);
                }
            }
            if (Regular != null) {
                this.RegularFont = TypeFaceHelper.Get(this.BuilderContext, Regular);
                if (this.RegularFont == null) {
                    throw new IllegalArgumentException("no font asset found for " + Regular);
                }
            }
            return this;
        }

        public Builder SetCancelable(boolean Cancelable) {
            this.Cancelable = Cancelable;
            return this;
        }

        public Builder DialogBgColor(@ColorInt int Color) {
            this.DialogBgColor = Color;
            return this;
        }

        public Builder DialogBgColorRes(@ColorRes int ColorRes) {
            return DialogBgColor(this.BuilderContext.getResources().getColor(ColorRes));
        }

        public Builder DialogBgColorAttr(@AttrRes int ColorAttr) {
            return DialogBgColor(Utils.ResolveColor(this.BuilderContext, ColorAttr));
        }

        public Builder DividerColor(@ColorInt int Color) {
            this.DividerColor = Color;
            return this;
        }

        public Builder DividerColorRes(@ColorRes int ColorRes) {
            return DividerColor(this.BuilderContext.getResources().getColor(ColorRes));
        }

        public Builder DividerColorAttr(@AttrRes int ColorAttr) {
            return DividerColor(Utils.ResolveColor(this.BuilderContext, ColorAttr));
        }

        public Builder SetTitle(@StringRes int TitleRes) {
            SetTitle(this.BuilderContext.getText(TitleRes));
            return this;
        }

        public Builder SetTitle(@NonNull CharSequence Title) {
            this.Title = Title;
            return this;
        }

        public Builder TitleFrameColor(@ColorInt int Color) {
            this.TitleFrameColor = Color;
            return this;
        }

        public Builder TitleFrameColorRes(@ColorRes int ColorRes) {
            return TitleFrameColor(this.BuilderContext.getResources().getColor(ColorRes));
        }

        public Builder TitleColor(@ColorInt int Color) {
            this.TitleColor = Color;
            return this;
        }

        public Builder TitleColorRes(@ColorRes int ColorRes) {
            TitleColor(this.BuilderContext.getResources().getColor(ColorRes));
            return this;
        }

        public Builder TitleColorAttr(@AttrRes int ColorAttr) {
            TitleColor(Utils.ResolveColor(this.BuilderContext, ColorAttr));
            return this;
        }

        public Builder TitleIcon(@NonNull Drawable Icon) {
            if (Icon != null) {
                this.TitleIcon = Icon;
            }
            return this;
        }

        public Builder TitleIconRes(@DrawableRes int IconRes) {
            if (IconRes != -1) {
                this.TitleIcon = ResourcesCompat.getDrawable(BuilderContext.getResources(), IconRes, null);
            }
            return this;
        }

        public Builder SetMessage(@StringRes int MessageRes) {
            SetMessage(this.BuilderContext.getText(MessageRes));
            return this;
        }

        public Builder SetMessage(@NonNull CharSequence MessageString) {
            if (this.CustomViewType != null) {
                throw new IllegalStateException("you cannot set message string when you're using a custom view.");
            }
            this.MessageText = MessageString;
            return this;
        }

        public Builder SetMessage(@StringRes int MessageRes, Object... FormatArgs) {
            SetMessage(this.BuilderContext.getString(MessageRes, FormatArgs));
            return this;
        }

        public Builder MessageColor(@ColorInt int Color) {
            this.MessageColor = Color;
            return this;
        }

        public Builder MessageColorRes(@ColorRes int ColorRes) {
            MessageColor(this.BuilderContext.getResources().getColor(ColorRes));
            return this;
        }

        public Builder MessageColorAttr(@AttrRes int ColorAttr) {
            MessageColor(Utils.ResolveColor(this.BuilderContext, ColorAttr));
            return this;
        }

        public Builder ForceStacked(boolean Stacked) {
            this.ForceStacking = Stacked;
            return this;
        }

        public Builder SetNegativeButton(@StringRes int StringRes) {
            return SetNegativeButton(this.BuilderContext.getText(StringRes));
        }

        public Builder SetNegativeButton(@NonNull CharSequence String) {
            this.NegativeText = String;
            return this;
        }

        public Builder NegativeColor(@ColorInt int Color) {
            return NegativeColor(Utils.GetActionTextStateList(BuilderContext, Color));
        }

        public Builder NegativeColorRes(@ColorRes int ColorRes) {
            return NegativeColor(Utils.GetActionTextColorStateList(this.BuilderContext, ColorRes));
        }

        public Builder NegativeColorAttr(@AttrRes int ColorAttr) {
            return NegativeColor(Utils.ResolveActionTextColorStateList(this.BuilderContext, ColorAttr, null));
        }

        public Builder NegativeColor(ColorStateList ColorStateList) {
            this.NegativeColor = ColorStateList;
            return this;
        }

        public Builder SetPositiveButton(@StringRes int StringRes) {
            SetPositiveButton(this.BuilderContext.getText(StringRes));
            return this;
        }

        public Builder SetPositiveButton(@NonNull CharSequence String) {
            this.PositiveText = String;
            return this;
        }

        public Builder PositiveTextColor(@ColorInt int Color) {
            return PositiveTextColor(Utils.GetActionTextStateList(BuilderContext, Color));
        }

        public Builder PositiveColorRes(@ColorRes int ColorRes) {
            return PositiveTextColor(Utils.GetActionTextColorStateList(this.BuilderContext, ColorRes));
        }

        public Builder PositiveColorAttr(@AttrRes int ColorAttr) {
            return PositiveTextColor(Utils.ResolveActionTextColorStateList(this.BuilderContext, ColorAttr, null));
        }

        public Builder PositiveTextColor(ColorStateList ColorStateList) {
            this.PositiveColor = ColorStateList;
            return this;
        }

        public Builder CallBack(@NonNull ButtonCallback CB) {
            this.ButtonCallBack = CB;
            return this;
        }

        public Builder BtnSelectorStacked(@DrawableRes int SelectorRes) {
            this.BtnSelectorStacked = SelectorRes;
            return this;
        }

        public Builder BtnSelector(@DrawableRes int SelectorRes, @NonNull DialogButtonAction Which) {
            switch (Which) {
                case POSITIVE: {
                    this.BtnSelectorPositive = SelectorRes;
                    break;
                }
                case NEGATIVE: {
                    this.BtnSelectorNegative = SelectorRes;
                    break;
                }
            }
            return this;
        }

        public Builder WidgetColor(@ColorInt int Color) {
            this.WidgetColor = Color;
            return this;
        }

        public Builder WidgetColorRes(@ColorRes int ColorRes) {
            return WidgetColor(this.BuilderContext.getResources().getColor(ColorRes));
        }

        public Builder WidgetColorAttr(@AttrRes int ColorAttr) {
            return WidgetColorRes(Utils.ResolveColor(this.BuilderContext, ColorAttr));
        }

        public Builder ListItems(@ArrayRes int ItemsRes) {
            ListItems(this.BuilderContext.getResources().getTextArray(ItemsRes));
            return this;
        }

        public Builder ListItems(@NonNull CharSequence[] Items) {
            if (this.CustomViewType != null) {
                throw new IllegalStateException("cannot set ListItems() when you're using a custom view.");
            }
            this.ListItems = Items;
            return this;
        }

        public Builder ListItemColor(@ColorInt int Color) {
            this.ListItemColor = Color;
            return this;
        }

        public Builder ListItemColorRes(@ColorRes int ColorRes) {
            return ListItemColor(this.BuilderContext.getResources().getColor(ColorRes));
        }

        public Builder ListItemColorAttr(@AttrRes int ColorAttr) {
            return ListItemColor(Utils.ResolveColor(this.BuilderContext, ColorAttr));
        }

        public Builder ListViewAdapter(@NonNull ListAdapter Adapter, @Nullable ListCallback CB) {
            if (this.CustomViewType != null) {
                throw new IllegalStateException("cannot set Adapter() when you're using a custom view.");
            }

            this.ListViewAdapter = Adapter;
            this.ListCallbackCustom = CB;
            return this;
        }

        public Builder ListItemsCallback(@NonNull ListCallback CB) {
            this.ListCallBack = CB;
            this.ListCallBackSingleChoice = null;
            this.ListCallBackMultiChoice = null;
            return this;
        }

        public Builder ListItemsCallbackSingleChoice(int SelectedIdx, @NonNull ListCallbackSingleChoice CB) {
            this.SelectedIndex = SelectedIdx;
            this.ListCallBack = null;
            this.ListCallBackSingleChoice = CB;
            this.ListCallBackMultiChoice = null;
            return this;
        }

        public Builder AlwaysCallSingleChoiceCallback() {
            this.AlwaysCallSingleChoiceCallback = true;
            return this;
        }

        public Builder ListItemsCallbackMultiChoice(@Nullable Integer[] SelectedIdxes, @NonNull ListCallbackMultiChoice CB) {
            this.SelectedIndices = SelectedIdxes;
            this.ListCallBack = null;
            this.ListCallBackSingleChoice = null;
            this.ListCallBackMultiChoice = CB;
            return this;
        }

        public Builder AlwaysCallMultiChoiceCallback() {
            this.AlwaysCallMultiChoiceCallback = true;
            return this;
        }

        public Builder ListSelector(@DrawableRes int SelectorRes) {
            this.ListSelector = SelectorRes;
            return this;
        }

        public Builder Progress(boolean Circle, int Max) {
            if (this.CustomViewType != null)
                throw new IllegalStateException("You cannot set ProgressBarType() when you're using a custom view.");
            if (Circle) {
                this.ProgressCircleType = true;
                this.ProgressBarType = -2;
            } else {
                this.ProgressCircleType = false;
                this.ProgressBarType = -1;
                this.ProgressMax = Max;
            }
            return this;
        }

        public Builder Progress(boolean Circle, int Max, boolean ShowMinMax) {
            this.ShowMinMax = ShowMinMax;
            return Progress(Circle, Max);
        }

        public Builder ProgressNumberFormat(@NonNull String String) {
            this.ProgressNumberFormat = String;
            return this;
        }

        public Builder ProgressPercentFormat(@NonNull NumberFormat Number) {
            this.ProgressPercentFormat = Number;
            return this;
        }

        public Builder AutoDismiss(boolean dismiss) {
            this.AutoDismiss = dismiss;
            return this;
        }

        public Builder ShowListener(@NonNull DialogInterface.OnShowListener listener) {
            this.ShowListener = listener;
            return this;
        }

        public Builder DismissListener(@NonNull DialogInterface.OnDismissListener listener) {
            this.DismissListener = listener;
            return this;
        }

        public Builder CancelListener(@NonNull DialogInterface.OnCancelListener listener) {
            this.CancelListener = listener;
            return this;
        }

        public Builder KeyListener(@NonNull DialogInterface.OnKeyListener listener) {
            this.KeyListener = listener;
            return this;
        }

        public Builder Input(@Nullable CharSequence hint, @Nullable CharSequence prefill, boolean allowEmptyInput, @NonNull InputCallback callback) {
            if (this.CustomViewType != null) {
                throw new IllegalStateException("cannot set content() when you're using a custom view.");
            }
            this.InputCallback = callback;
            this.InputHint = hint;
            this.InputPrefill = prefill;
            this.InputAllowEmpty = allowEmptyInput;
            return this;
        }

        public Builder Input(@Nullable CharSequence hint, @Nullable CharSequence prefill, @NonNull InputCallback callback) {
            return Input(hint, prefill, true, callback);
        }

        public Builder Input(@StringRes int hint, @StringRes int prefill, boolean allowEmptyInput, @NonNull InputCallback callback) {
            return Input(hint == 0 ? null : BuilderContext.getText(hint), prefill == 0 ? null : BuilderContext.getText(prefill), allowEmptyInput, callback);
        }

        public Builder Input(@StringRes int hint, @StringRes int prefill, @NonNull InputCallback callback) {
            return Input(hint, prefill, true, callback);
        }

        public Builder InputType(int type) {
            this.InputType = type;
            return this;
        }

        public Builder InputMaxLength(int maxLength) {
            return InputMaxLength(maxLength, 0);
        }

        public Builder InputMaxLength(int MaxLength, int ErrorColor) {
            if (MaxLength < 1) {
                throw new IllegalArgumentException("Max length for input dialogs cannot be less than 1.");
            }
            this.InputMaxLength = MaxLength;
            if (ErrorColor == 0) {
                InputMaxLengthErrorColor = BuilderContext.getResources().getColor(R.color.ext_dialog_edit_text_error);
            } else {
                this.InputMaxLengthErrorColor = ErrorColor;
            }
            return this;
        }

        public Builder InputMaxLengthRes(int maxLength, @ColorRes int errorColor) {
            return InputMaxLength(maxLength, BuilderContext.getResources().getColor(errorColor));
        }

        public Builder AlwaysCallInputCallback() {
            this.AlwaysCallInputCallback = true;
            return this;
        }

        public Builder CustomView(@LayoutRes int layoutRes, boolean wrapInScrollView) {
            LayoutInflater li = LayoutInflater.from(this.BuilderContext);
            return CustomView(li.inflate(layoutRes, null), wrapInScrollView);
        }

        public Builder CustomView(@NonNull View view, boolean wrapInScrollView) {
            if (this.MessageText != null) {
                throw new IllegalStateException("cannot use CustomViewType() when you have content set.");
            } else if (this.ListItems != null) {
                throw new IllegalStateException("cannot use CustomViewType() when you have ListItems set.");
            } else if (this.InputCallback != null) {
                throw new IllegalStateException("cannot use CustomViewType() with an input dialog");
            } else if (this.ProgressBarType > -2 || this.ProgressCircleType) {
                throw new IllegalStateException("cannot use CustomViewType() with a ProgressBarType dialog");
            }

            if (view.getParent() != null && view.getParent() instanceof ViewGroup) {
                ((ViewGroup) view.getParent()).removeView(view);
            }

            this.CustomViewType = view;
            this.WrapCustomViewInScroll = wrapInScrollView;
            return this;
        }

        @UiThread
        public ExtDialog Build() {
            return new ExtDialog(this);
        }

        @UiThread
        public ExtDialog Show() {
            ExtDialog _Dialog = Build();
            _Dialog.show();
            return _Dialog;
        }
    }

    @Override
    @UiThread
    public void show() {
        try {
            super.show();
        } catch (WindowManager.BadTokenException e) {
            throw new DialogException("Bad window token, you cannot show a dialog before an Activity is created or after it's hidden.");
        }
    }

    public final View GetActionButton(@NonNull DialogButtonAction which) {
        switch (which) {
            default: {
                return vExtDialog.findViewById(R.id.positive);
            }
            case NEGATIVE: {
                return vExtDialog.findViewById(R.id.negative);
            }
        }
    }

    @Nullable
    public final EditText GetInputEditText() {
        return InputEdText;
    }

    @UiThread
    public final void SetActionButton(@NonNull final DialogButtonAction which, final CharSequence title) {
        switch (which) {
            default:
                mBuilder.PositiveText = title;
                PositiveButton.setText(title);
                PositiveButton.setVisibility(title == null ? View.GONE : View.VISIBLE);
                break;
            case NEGATIVE:
                mBuilder.NegativeText = title;
                NegativeButton.setText(title);
                NegativeButton.setVisibility(title == null ? View.GONE : View.VISIBLE);
                break;
        }
    }

    public final void SetActionButton(DialogButtonAction which, @StringRes int titleRes) {
        SetActionButton(which, getContext().getText(titleRes));
    }

    public final boolean HasActionButtons() {
        return NumberOfActionButtons() > 0;
    }

    public final int NumberOfActionButtons() {
        int _Num = 0;

        if (mBuilder.PositiveText != null && PositiveButton.getVisibility() == View.VISIBLE) {
            _Num++;
        }

        if (mBuilder.NegativeText != null && NegativeButton.getVisibility() == View.VISIBLE) {
            _Num++;
        }

        return _Num;
    }

    @UiThread
    @Override
    public final void setTitle(@NonNull CharSequence newTitle) {
        TitleTv.setText(newTitle);
    }

    @UiThread
    @Override
    public final void setTitle(@StringRes int newTitleRes) {
        setTitle(mBuilder.BuilderContext.getString(newTitleRes));
    }

    @UiThread
    public final void setTitle(@StringRes int newTitleRes, @Nullable Object... formatArgs) {
        setTitle(mBuilder.BuilderContext.getString(newTitleRes, formatArgs));
    }

    @UiThread
    public void setIcon(@DrawableRes final int Id) {
        TitleIv.setImageResource(Id);
        TitleIv.setVisibility(Id != 0 ? View.VISIBLE : View.GONE);
    }

    @UiThread
    public void setIcon(final Drawable D) {
        TitleIv.setImageDrawable(D);
        TitleIv.setVisibility(D != null ? View.VISIBLE : View.GONE);
    }

    @UiThread
    public void setIconAttribute(@AttrRes int AttrId) {
        Drawable d = Utils.ResolveDrawable(mBuilder.BuilderContext, AttrId);
        setIcon(d);
    }

    @UiThread
    public final void setContent(CharSequence Content) {
        MessageTv.setText(Content);
        MessageTv.setVisibility(TextUtils.isEmpty(Content) ? View.GONE : View.VISIBLE);
    }

    @UiThread
    public final void setContent(@StringRes int Res) {
        setContent(mBuilder.BuilderContext.getString(Res));
    }

    @UiThread
    public final void setContent(@StringRes int newContentRes, @Nullable Object... formatArgs) {
        setContent(mBuilder.BuilderContext.getString(newContentRes, formatArgs));
    }

    @Deprecated
    public void setMessage(CharSequence message) {
        setContent(message);
    }

    @UiThread
    public final void SetItems(CharSequence[] items) {
        if (mBuilder.ListViewAdapter == null) {
            throw new IllegalStateException("This ExtDialog instance does not yet have an adapter set to it.cannot use setItems().");
        }
        mBuilder.ListItems = items;

        if (mBuilder.ListViewAdapter instanceof TextListAdapter) {
            mBuilder.ListViewAdapter = new TextListAdapter(this, ListType.GetLayoutForType(mListType));
        } else {
            throw new IllegalStateException("When using a custom adapter, setItems() cannot be used. set ListItems through the adapter instead.");
        }
        ListItemView.setAdapter(mBuilder.ListViewAdapter);
    }

    public final int GetCurrentProgress() {
        if (mProgress == null) {
            return -1;
        }
        return mProgress.getProgress();
    }

    public final void IncrementProgress(final int by) {
        SetProgress(GetCurrentProgress() + by);
    }

    private Handler mHandler;

    public final void SetProgress(final int progress) {
        if (mBuilder.ProgressBarType <= -2) {
            throw new IllegalStateException("cannot use SetProgress() on this dialog.");
        }
        mProgress.setProgress(progress);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgressLabel != null) {
//                    final int percentage = (int) (((float) getCurrentProgress() / (float) getMaxProgress()) * 100f);
                    mProgressLabel.setText(mBuilder.ProgressPercentFormat.format(
                            (float) GetCurrentProgress() / (float) GetMaxProgress()));
                }
                if (mProgressMinMax != null) {
                    mProgressMinMax.setText(String.format(mBuilder.ProgressNumberFormat,
                            GetCurrentProgress(), GetMaxProgress()));
                }
            }
        });
    }

    public final void SetMaxProgress(final int max) {
        if (mBuilder.ProgressBarType <= -2) {
            throw new IllegalStateException("cannot use SetMaxProgress() on this dialog.");
        }
        mProgress.setMax(max);
    }

    public final int GetMaxProgress() {
        if (mProgress == null) {
            return -1;
        }
        return mProgress.getMax();
    }

    public final void SetProgressPercentFormat(NumberFormat format) {
        mBuilder.ProgressPercentFormat = format;
        SetProgress(GetCurrentProgress()); // invalidates display
    }

    public final void SetProgressNumberFormat(String format) {
        mBuilder.ProgressNumberFormat = format;
        SetProgress(GetCurrentProgress()); // invalidates display
    }

    public final boolean IsCancelled() {
        return !isShowing();
    }

    public int GetSelectedIndex() {
        if (mBuilder.ListCallBackSingleChoice != null) {
            return mBuilder.SelectedIndex;
        } else {
            return -1;
        }
    }

    @Nullable
    public Integer[] GetSelectedIndices() {
        if (mBuilder.ListCallBackMultiChoice != null) {
            return SelectedIndicesList.toArray(new Integer[SelectedIndicesList.size()]);
        } else {
            return null;
        }
    }

    @UiThread
    public void SetSelectedIndex(int index) {
        mBuilder.SelectedIndex = index;
        if (mBuilder.ListViewAdapter != null && mBuilder.ListViewAdapter instanceof TextListAdapter) {
            ((TextListAdapter) mBuilder.ListViewAdapter).notifyDataSetChanged();
        } else {
            throw new IllegalStateException("only use SetSelectedIndex() with the default adapter implementation.");
        }
    }

    @UiThread
    public void SetSelectedIndices(@NonNull Integer[] indices) {
        mBuilder.SelectedIndices = indices;
        SelectedIndicesList = new ArrayList<>(Arrays.asList(indices));
        if (mBuilder.ListViewAdapter != null && mBuilder.ListViewAdapter instanceof TextListAdapter) {
            ((TextListAdapter) mBuilder.ListViewAdapter).notifyDataSetChanged();
        } else {
            throw new IllegalStateException("only use SetSelectedIndices() with the default adapter implementation.");
        }
    }

    public void ClearSelectedIndices() {
        if (SelectedIndicesList == null) {
            throw new IllegalStateException("only use ClearSelectedIndicies() with multi choice list dialogs.");
        }
        mBuilder.SelectedIndices = null;
        SelectedIndicesList.clear();
        if (mBuilder.ListViewAdapter != null && mBuilder.ListViewAdapter instanceof TextListAdapter) {
            ((TextListAdapter) mBuilder.ListViewAdapter).notifyDataSetChanged();
        } else {
            throw new IllegalStateException("only use ClearSelectedIndicies() with the default adapter implementation.");
        }
    }

    @Override
    public final void onShow(DialogInterface dialog) {
        if (InputEdText != null) {
            Utils.ShowKeyboard(this, mBuilder);
            if (InputEdText.getText().length() > 0)
                InputEdText.setSelection(InputEdText.getText().length());
        }
        super.onShow(dialog);
    }

    protected void SetInternalInputCallback() {
        if (InputEdText == null) {
            return;
        }

        InputEdText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final int _Length = s.toString().length();
                boolean _EmptyDisabled = false;
                if (!mBuilder.InputAllowEmpty) {
                    _EmptyDisabled = _Length == 0;
                    final View positiveAb = GetActionButton(DialogButtonAction.POSITIVE);
                    positiveAb.setEnabled(!_EmptyDisabled);
                }
                InvalidateInputMinMaxIndicator(_Length, _EmptyDisabled);
                if (mBuilder.AlwaysCallInputCallback) {
                    mBuilder.InputCallback.OnInput(ExtDialog.this, s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    protected void InvalidateInputMinMaxIndicator(int currentLength, boolean emptyDisabled) {
        if (InputMinMaxTv != null) {
            InputMinMaxTv.setText(currentLength + "/" + mBuilder.InputMaxLength);

            final boolean _IsDisabled = (emptyDisabled && currentLength == 0) || currentLength > mBuilder.InputMaxLength;
            final int _ColorText = _IsDisabled ? mBuilder.InputMaxLengthErrorColor : mBuilder.MessageColor;
            final int _ColorWidget = _IsDisabled ? mBuilder.InputMaxLengthErrorColor : mBuilder.WidgetColor;
            InputMinMaxTv.setTextColor(_ColorText);
            TintHelper.SetTint(InputEdText, _ColorWidget);

            final View _PositiveAb = GetActionButton(DialogButtonAction.POSITIVE);
            _PositiveAb.setEnabled(!_IsDisabled);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (InputEdText != null) {
            Utils.HideKeyboard(this, mBuilder);
        }
    }

    protected enum ListType {
        REGULAR, SINGLE, MULTI;

        public static int GetLayoutForType(ListType type) {
            switch (type) {
                case REGULAR:
                    return R.layout.ext_dialog_section_list_item;
                case SINGLE:
                    return R.layout.ext_dialog_section_list_item_singlechoice;
                case MULTI:
                    return R.layout.ext_dialog_section_list_item_multichoice;
                default:
                    throw new IllegalArgumentException("not a valid list type");
            }
        }
    }

    public interface ListCallback {
        void OnSelection(ExtDialog dialog, View itemView, int which, CharSequence text);
    }

    public interface ListCallbackSingleChoice {
        boolean OnSelection(ExtDialog dialog, View itemView, int which, CharSequence text);
    }

    public interface ListCallbackMultiChoice {
        boolean OnSelection(ExtDialog dialog, Integer[] which, CharSequence[] text);
    }

    public static abstract class ButtonCallback {
        public void OnAny(ExtDialog dialog) {
        }

        public void OnPositive(ExtDialog dialog) {
        }

        public void OnNegative(ExtDialog dialog) {
        }

        public ButtonCallback() {
            super();
        }

        @Override
        protected final Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public final boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        protected final void finalize() throws Throwable {
            super.finalize();
        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

        @Override
        public final String toString() {
            return super.toString();
        }
    }

    public interface InputCallback {
        void OnInput(ExtDialog dialog, CharSequence input);
    }
}