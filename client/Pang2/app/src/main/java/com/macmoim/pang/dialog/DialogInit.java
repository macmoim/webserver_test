package com.macmoim.pang.dialog;

import android.content.res.ColorStateList;
import android.content.res.Resources;
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
        } else if (builder.ListItems != null && builder.ListItems.length > 0 || builder.ListViewAdapter != null) {
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

        ////////////////////////////////////////////////////////////////////////////////////////////
        // declaration view id
        ////////////////////////////////////////////////////////////////////////////////////////////
        // title area : top
        Dialog.TitleFrameView = Dialog.vExtDialog.findViewById(R.id.titleFrame);
        Dialog.TitleIv = (ImageView) Dialog.vExtDialog.findViewById(R.id.icon);
        Dialog.TitleTv = (TextView) Dialog.vExtDialog.findViewById(R.id.title);
        // message area : middle
        Dialog.MessageSv = (ScrollView) Dialog.vExtDialog.findViewById(R.id.contentScrollView);
        Dialog.MessageTv = (TextView) Dialog.vExtDialog.findViewById(R.id.content);
        // button area : bottom
        Dialog.PositiveButton = (DialogButton) Dialog.vExtDialog.findViewById(R.id.positive);
        Dialog.PositiveButton.setVisibility(_Builder.PositiveText != null ? View.VISIBLE : View.GONE);
        Dialog.NegativeButton = (DialogButton) Dialog.vExtDialog.findViewById(R.id.negative);
        Dialog.NegativeButton.setVisibility(_Builder.NegativeText != null ? View.VISIBLE : View.GONE);
        // etc area : list, custom view, input, progress
        Dialog.ListItemView = (ListView) Dialog.vExtDialog.findViewById(R.id.ext_dialog_list_view);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // action
        ////////////////////////////////////////////////////////////////////////////////////////////
        // set ext dialog cancleable / touch set
        Dialog.setCancelable(_Builder.Cancelable);
        Dialog.setCanceledOnTouchOutside(_Builder.Cancelable);

        // dialog background color
        GradientDrawable _DialogBgGradient = new GradientDrawable();
        if (_Builder.DialogBgColor == -1) {
            final int _FB = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_background_color);
            _Builder.DialogBgColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_background_color, _FB);
        }
        _DialogBgGradient.setColor(_Builder.DialogBgColor);
        _DialogBgGradient.setCornerRadius(_Builder.BuilderContext.getResources().getDimension(R.dimen.ext_dialog_bg_corner_radius));
        Utils.SetBackground(Dialog.vExtDialog, _DialogBgGradient);

        // dialog divider color
        if (_Builder.DividerColor == -1) {
            final int _FB = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_divider_color);
            _Builder.DividerColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_divider_color, _FB);
        }
        Dialog.vExtDialog.SetDividerColor(_Builder.DividerColor);

        // title area : top
        if (Dialog.TitleFrameView != null) {
            if (_Builder.Title == null) {
                Dialog.TitleFrameView.setVisibility(View.GONE);
            } else {
                Dialog.TitleFrameView.setVisibility(View.VISIBLE);

                // title frame color
                final float _Radius = _Builder.BuilderContext.getResources().getDimension(R.dimen.ext_dialog_bg_corner_radius);
                final float[] _Radii = {_Radius, _Radius, _Radius, _Radius, 0, 0, 0, 0};
                GradientDrawable _TitleFrameGrdient = new GradientDrawable();
                if (_Builder.TitleFrameColor == -1) {
                    final int _FB = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_title_frame_color);
                    _Builder.TitleFrameColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_title_frame_color, _FB);
                }
                _TitleFrameGrdient.setCornerRadii(_Radii);
                _TitleFrameGrdient.setColor(_Builder.TitleFrameColor);
                Utils.SetBackground(Dialog.TitleFrameView, _TitleFrameGrdient);

                // title image view
                if (Dialog.TitleIv != null) {
                    if (_Builder.TitleIcon != null) {
                        Dialog.TitleIv.setVisibility(View.VISIBLE);
                        Dialog.TitleIv.setImageDrawable(_Builder.TitleIcon);
                    } else {
                        Dialog.TitleIv.setVisibility(View.GONE);
                    }
                }

                // title text view
                if (Dialog.TitleTv != null) {
                    Dialog.SetTypeFace(Dialog.TitleTv, _Builder.MediumFont);
                    // title text color
                    if (_Builder.TitleColor == -1) {
                        final int _FB = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_title_color);
                        _Builder.TitleColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_title_color, _FB);
                    }
                    Dialog.TitleTv.setTextColor(_Builder.TitleColor);

                    Dialog.TitleTv.setGravity(_Builder.TitleGravity.GetGravity());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        //noinspection ResourceType
                        Dialog.TitleTv.setTextAlignment(_Builder.TitleGravity.GetTextAlignment());
                    }
                    Dialog.TitleTv.setText(_Builder.Title);
                }
            }
        }

        // message area : medium
        // if (Dialog.MessageSv != null) {
        if (_Builder.MessageText == null) {
            if (Dialog.MessageSv != null) {
                Dialog.MessageSv.setVisibility(View.GONE);
            }

            if (Dialog.MessageTv != null) {
                Dialog.MessageTv.setVisibility(View.GONE);
            }
        } else {
            if (Dialog.MessageSv != null) {
                Dialog.MessageSv.setVisibility(View.VISIBLE);
            }

            if (Dialog.MessageTv != null) {
                Dialog.MessageTv.setVisibility(View.VISIBLE);

                Dialog.MessageTv.setMovementMethod(new LinkMovementMethod());
                Dialog.SetTypeFace(Dialog.MessageTv, _Builder.RegularFont);
                Dialog.MessageTv.setLineSpacing(0f, _Builder.MessageLineSpacing);

                // message text color
                if (_Builder.MessageColor == -1) {
                    final int _FB = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_message_color);
                    _Builder.MessageColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_message_color, _FB);
                }
                Dialog.MessageTv.setTextColor(_Builder.MessageColor);

                Dialog.MessageTv.setGravity(_Builder.MessageGravity.GetGravity());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    //noinspection ResourceType
                    Dialog.MessageTv.setTextAlignment(_Builder.MessageGravity.GetTextAlignment());
                }

                Dialog.MessageTv.setText(_Builder.MessageText);
            }
        }
        // }

        // button area : bottom
        Dialog.vExtDialog.SetButtonStackedGravity(_Builder.BtnStackedGravity);
        Dialog.vExtDialog.SetForceStack(_Builder.ForceStacking);

        if (_Builder.PositiveText != null) {
            DialogButton _PositiveTextView = Dialog.PositiveButton;
            Dialog.SetTypeFace(_PositiveTextView, _Builder.MediumFont);
            _PositiveTextView.SetAllCapsCompat(true);
            _PositiveTextView.setText(_Builder.PositiveText);
            // positive text color
            if (_Builder.PositiveColor == null) {
                final int _Id = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_btn_positive_color);
                final ColorStateList _FB = Utils.GetActionTextStateList(_Builder.BuilderContext, _Id);
                _Builder.PositiveColor = Utils.ResolveActionTextColorStateList(_Builder.BuilderContext, R.attr.ext_dialog_btn_positive_color, _FB);
            }
            _PositiveTextView.setTextColor(_Builder.PositiveColor);
            Dialog.PositiveButton.SetStackedSelector(Dialog.GetButtonSelector(DialogButtonAction.POSITIVE));
            Dialog.PositiveButton.SetDefaultSelector(Dialog.GetButtonSelector(DialogButtonAction.POSITIVE));
            Dialog.PositiveButton.setTag(DialogButtonAction.POSITIVE);
            Dialog.PositiveButton.setOnClickListener(Dialog);
            Dialog.PositiveButton.setVisibility(View.VISIBLE);
        }

        if (_Builder.NegativeText != null) {
            DialogButton _NnegativeTextView = Dialog.NegativeButton;
            Dialog.SetTypeFace(_NnegativeTextView, _Builder.MediumFont);
            _NnegativeTextView.SetAllCapsCompat(true);
            _NnegativeTextView.setText(_Builder.NegativeText);
            // negative button color
            if (_Builder.NegativeColor == null) {
                final int _Id = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_btn_negative_color);
                final ColorStateList _FB = Utils.GetActionTextStateList(_Builder.BuilderContext, _Id);
                _Builder.NegativeColor = Utils.ResolveActionTextColorStateList(_Builder.BuilderContext, R.attr.ext_dialog_btn_negative_color, _FB);
            }
            _NnegativeTextView.setTextColor(_Builder.NegativeColor);
            Dialog.NegativeButton.SetStackedSelector(Dialog.GetButtonSelector(DialogButtonAction.NEGATIVE));
            Dialog.NegativeButton.SetDefaultSelector(Dialog.GetButtonSelector(DialogButtonAction.NEGATIVE));
            Dialog.NegativeButton.setTag(DialogButtonAction.NEGATIVE);
            Dialog.NegativeButton.setOnClickListener(Dialog);
            Dialog.NegativeButton.setVisibility(View.VISIBLE);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // etc area : list, custom view, input, progress
        ////////////////////////////////////////////////////////////////////////////////////////////
        // widget color : list widget, progress widget, input widget
        if (_Builder.WidgetColor == -1) {
            final int _FB = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_widget_color);
            _Builder.WidgetColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_widget_color, _FB);
        }

        // list dialog
        if ((Dialog.ListItemView != null)
                && (_Builder.ListItems != null && _Builder.ListItems.length > 0 || _Builder.ListViewAdapter != null)) {
            Dialog.ListItemView.setVisibility(View.VISIBLE);
            // list item color
            if (_Builder.ListItemColor == -1) {
                final int _FB = Utils.ResolveColor(Dialog.getContext(), R.attr.ext_dialog_list_item_color);
                _Builder.ListItemColor = Utils.ResolveColor(_Builder.BuilderContext, R.attr.ext_dialog_list_item_color, _FB);
            }

            Dialog.ListItemView.setSelector(Dialog.GetListSelector());

            // No custom Adapter specified, setup the list with a ExtDialogAdapter.
            // Which supports regular lists and single/multi choice dialogs.
            if (_Builder.ListViewAdapter == null) {
                // Determine list type
                if (_Builder.ListCallBackSingleChoice != null) {
                    Dialog.mListType = ExtDialog.ListType.SINGLE;
                } else if (_Builder.ListCallBackMultiChoice != null) {
                    Dialog.mListType = ExtDialog.ListType.MULTI;
                    Dialog.SelectedIndicesList = new ArrayList<>();

                    if (_Builder.SelectedIndices != null) {
                        Dialog.SelectedIndicesList = new ArrayList<>(Arrays.asList(_Builder.SelectedIndices));
                    }
                } else {
                    Dialog.mListType = ExtDialog.ListType.REGULAR;
                }
                _Builder.ListViewAdapter = new TextListAdapter(Dialog, ExtDialog.ListType.GetLayoutForType(Dialog.mListType));
            }

            Dialog.InvalidateList();
            Dialog.CheckIfListInitScroll();
        }

        // progress dialog
        SetupProgressDialog(Dialog);

        if (_Builder.InputCallback != null && _Builder.PositiveText == null) {
            _Builder.PositiveText = _Builder.BuilderContext.getText(android.R.string.ok);
        }
        // input dialog
        SetupInputDialog(Dialog);

        // custom view
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

        ////////////////////////////////////////////////////////////////////////////////////////////
        // listener
        ////////////////////////////////////////////////////////////////////////////////////////////
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

        // dialog set content view
        Dialog.SetContentViewExt(Dialog.vExtDialog);
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
                Dialog.mProgress.setIndeterminateDrawable(new CircularProgressDrawable(_Builder.WidgetColor, _Builder.BuilderContext.getResources().getDimension(R.dimen.ext_dialog_progress_circle_border)));
                // Dialog.mProgress.setIndeterminateDrawable(_Builder.BuilderContext.getResources().getDrawable(R.drawable.ext_dialog_progress_circle_bg));
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
                    Dialog.mProgressLabel.setTextColor(_Builder.MessageColor);
                    Dialog.SetTypeFace(Dialog.mProgressLabel, _Builder.MediumFont);
                    Dialog.mProgressLabel.setText(_Builder.ProgressPercentFormat.format(0));
                }
                Dialog.mProgressMinMax = (TextView) Dialog.vExtDialog.findViewById(R.id.minMax);
                if (Dialog.mProgressMinMax != null) {
                    Dialog.mProgressMinMax.setTextColor(_Builder.MessageColor);
                    Dialog.SetTypeFace(Dialog.mProgressMinMax, _Builder.RegularFont);

                    if (_Builder.ShowMinMax) {
                        Dialog.mProgressMinMax.setVisibility(View.VISIBLE);
                        Dialog.mProgressMinMax.setText(String.format(_Builder.ProgressNumberFormat, 0, _Builder.ProgressMax));
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
        Dialog.InputEdText.setTextColor(_Builder.MessageColor);
        Dialog.InputEdText.setHintTextColor(Utils.AdjustAlpha(_Builder.MessageColor, 0.3f));
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