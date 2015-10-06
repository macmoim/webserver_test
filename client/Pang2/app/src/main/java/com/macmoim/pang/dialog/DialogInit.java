package com.macmoim.pang.dialog;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.macmoim.pang.R;
import com.macmoim.pang.dialog.base.DialogButton;
import com.macmoim.pang.dialog.typedef.DialogButtonAction;
import com.macmoim.pang.dialog.ui.CircularProgressDrawable;
import com.macmoim.pang.dialog.ui.TintHelper;
import com.macmoim.pang.dialog.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by P10452 on 2015-09-05.
 */
public class DialogInit {
    @StyleRes
    public static int GetTheme(@NonNull ExtDialog.Builder builder) {
        return R.style.ExtDialogStyle;
    }

    @LayoutRes
    public static int GetInflateLayout(ExtDialog.Builder builder) {
        if (builder.CustomViewType != null) {
            return R.layout.ext_dialog_type_custom;
        } else if (builder.ListItems != null && builder.ListItems.length > 0 || builder.Adapter != null) {
            return R.layout.ext_dialog_type_list;
        } else if (builder.ProgressBarType > -2) {
            return R.layout.ext_dialog_type_progress;
        } else if (builder.ProgressCircleType) {
            return R.layout.ext_dialog_type_progress_circle;
        } else if (builder.InputCallback != null) {
            return R.layout.ext_dialog_type_input;
        } else {
            return R.layout.ext_dialog_type_common;
        }
    }

    @UiThread
    public static void Init(final ExtDialog Dialog) {
        final ExtDialog.Builder _Builder = Dialog.mBuilder;

        Dialog.TitleTv = (TextView) Dialog.vExtDialog.findViewById(R.id.title);
        Dialog.TitleIconView = (ImageView) Dialog.vExtDialog.findViewById(R.id.icon);
        Dialog.TitleFrameView = Dialog.vExtDialog.findViewById(R.id.titleFrame);
        Dialog.ContentSv = (ScrollView) Dialog.vExtDialog.findViewById(R.id.contentScrollView);
        Dialog.ContentTv = (TextView) Dialog.vExtDialog.findViewById(R.id.content);
        Dialog.ListItemView = (ListView) Dialog.vExtDialog.findViewById(R.id.contentListView);
        Dialog.PositiveButton = (DialogButton) Dialog.vExtDialog.findViewById(R.id.positive);
        Dialog.PositiveButton.setVisibility(_Builder.PositiveText != null ? View.VISIBLE : View.GONE);
        Dialog.NegativeButton = (DialogButton) Dialog.vExtDialog.findViewById(R.id.negative);
        Dialog.NegativeButton.setVisibility(_Builder.NegativeText != null ? View.VISIBLE : View.GONE);

        Dialog.setCancelable(_Builder.Cancelable);
        Dialog.setCanceledOnTouchOutside(_Builder.Cancelable);

        // dialog background color
        GradientDrawable _GradientDrawble = new GradientDrawable();

        if (_Builder.DialogBgColor == -1) {
            final int _FB = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_background_color);
            _Builder.DialogBgColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_background_color, _FB);
        }

        _GradientDrawble.setColor(_Builder.DialogBgColor);
        _GradientDrawble.setCornerRadius(_Builder.BuilderContext.getResources().getDimension(R.dimen.ext_dialog_bg_corner_radius));
        Utils.SetBackground(Dialog.vExtDialog, _GradientDrawble);

        if (!_Builder.TitleColorSet) {
            final int _TitleColorFallback = Utils.ResolveColor(Dialog.getContext(), android.R.attr.textColorPrimary);
            _Builder.TitleColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_title_color, _TitleColorFallback);
        }
        if (!_Builder.ContentColorSet) {
            final int _ContentColorFallback = Utils.ResolveColor(Dialog.getContext(), android.R.attr.textColorSecondary);
            _Builder.ContentColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_content_color, _ContentColorFallback);
        }
        if (!_Builder.ListItemColorSet) {
            _Builder.ListItemColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_item_color, _Builder.ContentColor);
        }
        if (!_Builder.PositiveColorSet) {
            _Builder.PositiveColor = Dialog.getContext().getResources().getColorStateList(R.color.black_op100);
            _Builder.PositiveColor = Utils.ResolveActionTextColorStateList(_Builder.BuilderContext, R.attr.ext_dialog_btn_positive_color, _Builder.PositiveColor);
        }
        if (!_Builder.NegativeColorSet) {
            _Builder.NegativeColor = Dialog.getContext().getResources().getColorStateList(R.color.black_op100);
            _Builder.NegativeColor = Utils.ResolveActionTextColorStateList(_Builder.BuilderContext, R.attr.ext_dialog_btn_negative_color, _Builder.NegativeColor);
        }
        if (!_Builder.WidgetColorSet) {
            _Builder.WidgetColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_btn_widget_color, _Builder.WidgetColor);
        }
        if (_Builder.InputCallback != null && _Builder.PositiveText == null) {
            _Builder.PositiveText = _Builder.BuilderContext.getText(android.R.string.ok);
        }

        if (_Builder.TitleIcon != null) {
            Dialog.TitleIconView.setVisibility(View.VISIBLE);
            Dialog.TitleIconView.setImageDrawable(_Builder.TitleIcon);
        } else {
            Drawable d = Utils.ResolveDrawable(_Builder.BuilderContext, R.attr.ext_dialog_title_icon);
            if (d != null) {
                Dialog.TitleIconView.setVisibility(View.VISIBLE);
                Dialog.TitleIconView.setImageDrawable(d);
            } else {
                Dialog.TitleIconView.setVisibility(View.GONE);
            }
        }

        int _MaxIconSize = _Builder.MaxIconSize;
        if (_MaxIconSize == -1) {
            _MaxIconSize = Utils.ResolveDimension(_Builder.BuilderContext, R.attr.ext_dialog_title_icon_max_size);
        }
        if (_Builder.LimitIconToDefaultSize || Utils.ResolveBoolean(_Builder.BuilderContext, R.attr.ext_dialog_title_icon_limit_default_size)) {
            _MaxIconSize = _Builder.BuilderContext.getResources().getDimensionPixelSize(R.dimen.ext_dialog_title_frame_icon_max_size);
        }
        if (_MaxIconSize > -1) {
            Dialog.TitleIconView.setAdjustViewBounds(true);
            Dialog.TitleIconView.setMaxHeight(_MaxIconSize);
            Dialog.TitleIconView.setMaxWidth(_MaxIconSize);
            Dialog.TitleIconView.requestLayout();
        }

        if (Dialog.TitleTv != null) {
            Dialog.SetTypeFace(Dialog.TitleTv, _Builder.MediumFont);
            Dialog.TitleTv.setTextColor(_Builder.TitleColor);
            Dialog.TitleTv.setGravity(_Builder.TitleGravity.GetGravity());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //noinspection ResourceType
                Dialog.TitleTv.setTextAlignment(_Builder.TitleGravity.GetTextAlignment());
            }

            if (_Builder.Title == null) {
                Dialog.TitleFrameView.setVisibility(View.GONE);
            } else {
                Dialog.TitleTv.setText(_Builder.Title);
                Dialog.TitleFrameView.setVisibility(View.VISIBLE);

                if (_Builder.TitleFrameColor == -1) {
                    final int _FB = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_title_frame_color);
                    _Builder.TitleFrameColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_title_frame_color, _FB);
                }
                if (_Builder.TitleFrameColor != -1) {
                    GradientDrawable drawable = new GradientDrawable();
                    final float _Radius = _Builder.BuilderContext.getResources().getDimension(R.dimen.ext_dialog_bg_corner_radius);
                    float[] _Radii = {_Radius, _Radius, _Radius, _Radius, 0, 0, 0, 0};
                    drawable.setCornerRadii(_Radii);
                    drawable.setColor(_Builder.TitleFrameColor);
                    Utils.SetBackground(Dialog.TitleFrameView, drawable);
                }
            }
        }

        if (!_Builder.DividerColorSet) {
            final int dividerFallback = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_divider);
            _Builder.DividerColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_divider_color, dividerFallback);
        }
        Dialog.vExtDialog.SetDividerColor(_Builder.DividerColor);

        if (Dialog.ContentTv != null) {
            Dialog.ContentTv.setMovementMethod(new LinkMovementMethod());
            Dialog.SetTypeFace(Dialog.ContentTv, _Builder.RegularFont);
            Dialog.ContentTv.setLineSpacing(0f, _Builder.ContentLineSpacingMultiplier);
            if (_Builder.PositiveColor == null) {
                Dialog.ContentTv.setLinkTextColor(Utils.ResolveColor(Dialog.getContext(), android.R.attr.textColorPrimary));
            } else {
                Dialog.ContentTv.setLinkTextColor(_Builder.PositiveColor);
            }
            Dialog.ContentTv.setTextColor(_Builder.ContentColor);
            Dialog.ContentTv.setGravity(_Builder.ContentGravity.GetGravity());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //noinspection ResourceType
                Dialog.ContentTv.setTextAlignment(_Builder.ContentGravity.GetTextAlignment());
            }

            if (_Builder.Content != null) {
                Dialog.ContentTv.setText(_Builder.Content);
                Dialog.ContentTv.setVisibility(View.VISIBLE);
                if (Dialog.ContentSv != null) {
                    Dialog.ContentSv.setVisibility(View.VISIBLE);
                }
            } else {
                Dialog.ContentTv.setVisibility(View.GONE);
                if (Dialog.ContentSv != null) {
                    Dialog.ContentSv.setVisibility(View.GONE);
                }
            }
        }

        Dialog.vExtDialog.SetButtonStackedGravity(_Builder.BtnStackedGravity);
        Dialog.vExtDialog.SetForceStack(_Builder.ForceStacking);

        boolean _TtextAllCaps;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            _TtextAllCaps = Utils.ResolveBoolean(_Builder.BuilderContext, android.R.attr.textAllCaps, true);
            if (_TtextAllCaps) {
                _TtextAllCaps = Utils.ResolveBoolean(_Builder.BuilderContext, R.attr.textAllCaps, true);
            }
        } else {
            _TtextAllCaps = Utils.ResolveBoolean(_Builder.BuilderContext, R.attr.textAllCaps, true);
        }

        DialogButton _PositiveTextView = Dialog.PositiveButton;
        Dialog.SetTypeFace(_PositiveTextView, _Builder.MediumFont);
        _PositiveTextView.SetAllCapsCompat(_TtextAllCaps);
        _PositiveTextView.setText(_Builder.PositiveText);
        _PositiveTextView.setTextColor(_Builder.PositiveColor);
        Dialog.PositiveButton.SetStackedSelector(Dialog.GetButtonSelector(DialogButtonAction.POSITIVE, true));
        Dialog.PositiveButton.SetDefaultSelector(Dialog.GetButtonSelector(DialogButtonAction.POSITIVE, false));
        Dialog.PositiveButton.setTag(DialogButtonAction.POSITIVE);
        Dialog.PositiveButton.setOnClickListener(Dialog);
        Dialog.PositiveButton.setVisibility(View.VISIBLE);

        DialogButton _NnegativeTextView = Dialog.NegativeButton;
        Dialog.SetTypeFace(_NnegativeTextView, _Builder.MediumFont);
        _NnegativeTextView.SetAllCapsCompat(_TtextAllCaps);
        _NnegativeTextView.setText(_Builder.NegativeText);
        _NnegativeTextView.setTextColor(_Builder.NegativeColor);
        Dialog.NegativeButton.SetStackedSelector(Dialog.GetButtonSelector(DialogButtonAction.NEGATIVE, true));
        Dialog.NegativeButton.SetDefaultSelector(Dialog.GetButtonSelector(DialogButtonAction.NEGATIVE, false));
        Dialog.NegativeButton.setTag(DialogButtonAction.NEGATIVE);
        Dialog.NegativeButton.setOnClickListener(Dialog);
        Dialog.NegativeButton.setVisibility(View.VISIBLE);

        if (_Builder.ListCallBackMultiChoice != null) {
            Dialog.SelectedIndicesList = new ArrayList<>();
        }
        if (Dialog.ListItemView != null && (_Builder.ListItems != null && _Builder.ListItems.length > 0 || _Builder.Adapter != null)) {
            Dialog.ListItemView.setSelector(Dialog.GetListSelector());

            // No custom Adapter specified, setup the list with a ExtDialogAdapter.
            // Which supports regular lists and single/multi choice dialogs.
            if (_Builder.Adapter == null) {
                // Determine list type
                if (_Builder.ListCallBackSingleChoice != null) {
                    Dialog.mListType = ExtDialog.ListType.SINGLE;
                } else if (_Builder.ListCallBackMultiChoice != null) {
                    Dialog.mListType = ExtDialog.ListType.MULTI;
                    if (_Builder.SelectedIndices != null)
                        Dialog.SelectedIndicesList = new ArrayList<>(Arrays.asList(_Builder.SelectedIndices));
                } else {
                    Dialog.mListType = ExtDialog.ListType.REGULAR;
                }
                _Builder.Adapter = new TextListAdapter(Dialog,
                        ExtDialog.ListType.GetLayoutForType(Dialog.mListType));
            }
        }

        SetupProgressDialog(Dialog);
        SetupInputDialog(Dialog);

        if (_Builder.CustomViewType != null) {
            FrameLayout _Frame = (FrameLayout) Dialog.vExtDialog.findViewById(R.id.customViewFrame);
            Dialog.CustomViewFrame = _Frame;

            View _InnerView = _Builder.CustomViewType;
            if (_Builder.WrapCustomViewInScroll) {
                /* Apply the frame padding to the content, this allows the ScrollView to draw it's
                   over scroll glow without clipping */
                final Resources _Res = Dialog.getContext().getResources();
                final int _FramePadding = _Res.getDimensionPixelSize(R.dimen.ext_dialog_margin);
                final ScrollView _Sv = new ScrollView(Dialog.getContext());
                int _PaddingTop = _Res.getDimensionPixelSize(R.dimen.ext_dialog_content_padding_top);
                int _PpaddingBottom = _Res.getDimensionPixelSize(R.dimen.ext_dialog_content_padding_bottom);

                _Sv.setClipToPadding(false);
                if (_InnerView instanceof EditText) {
                    // Setting padding to an EditText causes visual errors, set it to the parent instead
                    _Sv.setPadding(_FramePadding, _PaddingTop, _FramePadding, _PpaddingBottom);
                } else {
                    // Setting padding to scroll view pushes the scroll bars out, don't do it if not necessary (like above)
                    _Sv.setPadding(0, _PaddingTop, 0, _PpaddingBottom);
                    _InnerView.setPadding(_FramePadding, 0, _FramePadding, 0);
                }
                _Sv.addView(_InnerView, new ScrollView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                _InnerView = _Sv;
            }
            _Frame.addView(_InnerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        if (_Builder.ShowListener != null) {
            Dialog.setOnShowListener(_Builder.ShowListener);
        }
        if (_Builder.CancelListener != null) {
            Dialog.setOnCancelListener(_Builder.CancelListener);
        }
        if (_Builder.DismissListener != null) {
            Dialog.setOnDismissListener(_Builder.DismissListener);
        }
        if (_Builder.KeyListener != null) {
            Dialog.setOnKeyListener(_Builder.KeyListener);
        }

        Dialog.SetOnShowListenerExt();

        Dialog.InvalidateList();
        Dialog.SetContentViewExt(Dialog.vExtDialog);
        Dialog.CheckIfListInitScroll();
    }

    private static void SetupProgressDialog(final ExtDialog Dialog) {
        final ExtDialog.Builder _Builder = Dialog.mBuilder;
        if (_Builder.ProgressCircleType || _Builder.ProgressBarType > -2) {
            Dialog.mProgress = (ProgressBar) Dialog.vExtDialog.findViewById(android.R.id.progress);

            if (Dialog.mProgress == null) {
                return;
            }

            if (_Builder.ProgressCircleType &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Dialog.mProgress.setIndeterminateDrawable(new CircularProgressDrawable(
                        _Builder.WidgetColor, _Builder.BuilderContext.getResources().getDimension(R.dimen.ext_dialog_circular_progress_border)));
                TintHelper.SetTint(Dialog.mProgress, _Builder.WidgetColor, true);
            } else {
                TintHelper.SetTint(Dialog.mProgress, _Builder.WidgetColor);
            }

            if (!_Builder.ProgressCircleType) {
                Dialog.mProgress.setIndeterminate(false);
                Dialog.mProgress.setProgress(0);
                Dialog.mProgress.setMax(_Builder.ProgressMax);
                Dialog.mProgressLabel = (TextView) Dialog.vExtDialog.findViewById(R.id.label);
                if (Dialog.mProgressLabel != null) {
                    Dialog.mProgressLabel.setTextColor(_Builder.ContentColor);
                    Dialog.SetTypeFace(Dialog.mProgressLabel, _Builder.MediumFont);
                    Dialog.mProgressLabel.setText(_Builder.ProgressPercentFormat.format(0));
                }
                Dialog.mProgressMinMax = (TextView) Dialog.vExtDialog.findViewById(R.id.minMax);
                if (Dialog.mProgressMinMax != null) {
                    Dialog.mProgressMinMax.setTextColor(_Builder.ContentColor);
                    Dialog.SetTypeFace(Dialog.mProgressMinMax, _Builder.RegularFont);

                    if (_Builder.ShowMinMax) {
                        Dialog.mProgressMinMax.setVisibility(View.VISIBLE);
                        Dialog.mProgressMinMax.setText(String.format(_Builder.ProgressNumberFormat,
                                0, _Builder.ProgressMax));
                        ViewGroup.MarginLayoutParams _Mp = (ViewGroup.MarginLayoutParams) Dialog.mProgress.getLayoutParams();
                        _Mp.leftMargin = 0;
                        _Mp.rightMargin = 0;
                    } else {
                        Dialog.mProgressMinMax.setVisibility(View.GONE);
                    }
                } else {
                    _Builder.ShowMinMax = false;
                }
            }
        }
    }

    private static void SetupInputDialog(final ExtDialog Dialog) {
        final ExtDialog.Builder _Builder = Dialog.mBuilder;

        Dialog.InputEdText = (EditText) Dialog.vExtDialog.findViewById(android.R.id.input);

        if (Dialog.InputEdText == null) {
            return;
        }

        Dialog.SetTypeFace(Dialog.InputEdText, _Builder.RegularFont);

        if (_Builder.InputPrefill != null) {
            Dialog.InputEdText.setText(_Builder.InputPrefill);
        }
        Dialog.SetInternalInputCallback();
        Dialog.InputEdText.setHint(_Builder.InputHint);
        Dialog.InputEdText.setSingleLine();
        Dialog.InputEdText.setTextColor(_Builder.ContentColor);
        Dialog.InputEdText.setHintTextColor(Utils.AdjustAlpha(_Builder.ContentColor, 0.3f));
        TintHelper.SetTint(Dialog.InputEdText, Dialog.mBuilder.WidgetColor);

        if (_Builder.InputType != -1) {
            Dialog.InputEdText.setInputType(_Builder.InputType);
            if ((_Builder.InputType & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                // If the flags contain TYPE_TEXT_VARIATION_PASSWORD, apply the password transformation method automatically
                Dialog.InputEdText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }

        Dialog.InputMinMaxTv = (TextView) Dialog.vExtDialog.findViewById(R.id.minMax);
        if (_Builder.InputMaxLength > -1) {
            Dialog.InvalidateInputMinMaxIndicator(Dialog.InputEdText.getText().toString().length(),
                    !_Builder.InputAllowEmpty);
        } else {
            Dialog.InputMinMaxTv.setVisibility(View.GONE);
            Dialog.InputMinMaxTv = null;
        }
    }
}