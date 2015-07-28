package com.macmoim.pang.richeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.macmoim.pang.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Copyright (C) 2015 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class RichViewer extends RichEditor {



    public RichViewer(Context context) {
        this(context, null);
    }

    public RichViewer(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
        setEnabled(false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public RichViewer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



}