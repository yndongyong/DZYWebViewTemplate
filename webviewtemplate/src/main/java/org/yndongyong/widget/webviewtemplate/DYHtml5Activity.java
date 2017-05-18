package org.yndongyong.widget.webviewtemplate;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 一个加载Html5网页的Activity模板
 * 内部已经打开js的支持
 * 通过 newInstance 方法 调用
 *
 * 需要使用如下的权限
 * //<uses-permission android:name="android.permission.INTERNET"/>
 //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
 //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
 */
public class DYHtml5Activity extends AppCompatActivity {

    private Toolbar mToolbar;
    private WebView mWebView;

    private static final String KEY_PARAM_URL = "KEY_PARAM_URL";
    private static final String KEY_PARAM_TITLE = "KEY_PARAM_TITLE";
    private static final String KEY_PARAM_SHOW_HTML_TITLE = "KEY_PARAM_SHOW_HTML_TITLE";

    private String mUrl;
    private String mTitle;
    private boolean mShowHtmlTitle = false;


    public DYHtml5Activity() {
        // Required empty public constructor
    }

    /**
     *
     * @param context
     * @param url  the url load
     * @param title
     * @param showHtmlTitle
     * @return
     */
    public static Intent newInstance(Context context, String url, String title,boolean showHtmlTitle) {
        Bundle args = new Bundle();
        args.putString(KEY_PARAM_URL, url);
        args.putString(KEY_PARAM_TITLE, title);
        args.putBoolean(KEY_PARAM_SHOW_HTML_TITLE,showHtmlTitle);
        Intent intent = new Intent(context, DYHtml5Activity.class);
        intent.putExtras(args);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html5_acitivity);

        LinearLayout layout = (LinearLayout) findViewById(R.id.ll_root);

        mWebView = new WebView(getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(layoutParams);
        layout.addView(mWebView);

        setupWindowAnimations();

        mUrl = getIntent().getStringExtra(KEY_PARAM_URL);
        mTitle = getIntent().getStringExtra(KEY_PARAM_TITLE);
        mShowHtmlTitle = getIntent().getBooleanExtra(KEY_PARAM_SHOW_HTML_TITLE,false);

        setupToolbar();
        setupWebViewParams();
    }

    @TargetApi(21)
    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.BOTTOM);
        slide.setDuration(200);

        Window window = getWindow();
        window.setEnterTransition(slide);
        window.setExitTransition(slide);
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (!TextUtils.isEmpty(mTitle)) {
            mToolbar.setTitle(mTitle);
        }
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     *
     */
    @SuppressLint("JavascriptInterface")
    private void setupWebViewParams() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        //打开js的支持，因为少写这行代码少赚1000块钱！！！  加上注解 @JavascriptInterface
        webSettings.setJavaScriptEnabled(true);
        // NOTE: This gives double tap zooming.
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setLoadsImagesAutomatically(true);

        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下 。然后 复写 WebChromeClient的onCreateWindow方法
        webSettings.setSupportMultipleWindows(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);


        //h5保存数据
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);


        //android 5.0 解决HTTPS和http或者加载的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.addJavascriptInterface(new DYJsBridge(mWebView), "android");
        mWebView.setWebChromeClient(new CustomChromeClient());
        mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.loadUrl(mUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
            return true;
        } else if (i == R.id.action_reload) {
            reloadWebView();
            return true;
        } else if (i == R.id.action_copy_url) {
            copyUrl();
            return true;
        } else if (i == R.id.action_open_in_browser) {
            openInBrowser();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected void reloadWebView() {
        mWebView.reload();
    }

    private void copyUrl() {
        String url = mWebView.getUrl();
        if (!TextUtils.isEmpty(url)) {
            copyText(mWebView.getTitle(), url, this);
        } else {
            Toast.makeText(this, "链接为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void openInBrowser() {
        String url = mWebView.getUrl();
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            Intent intent = new  Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            
        } else {
            Toast.makeText(this, "链接为空", Toast.LENGTH_SHORT).show();
        }
    }

    private static ClipboardManager getClipboardManager(Context context) {
        return (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public static void copyText(CharSequence label, CharSequence text, Context context) {
        ClipData clipData = ClipData.newPlainText(label, text);
        getClipboardManager(context).setPrimaryClip(clipData);
        Toast.makeText(context, text+"\n已复制到剪贴板", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.destroy();
            mWebView = null;
        }
    }

    /**
     * 辅助webview处理加载js对话框、网页title、网页加载进度的。
     */
    private class CustomChromeClient extends WebChromeClient {

        // NOTE: WebView can be trying to show an AlertDialog after the activity is finished, which
        // will result in a WindowManager$BadTokenException.
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return DYHtml5Activity.this.isFinishing() || super.onJsAlert(view, url, message,
                    result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return DYHtml5Activity.this.isFinishing() || super.onJsConfirm(view, url, message,
                    result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                  JsPromptResult result) {
            return DYHtml5Activity.this.isFinishing() || super.onJsPrompt(view, url, message,
                    defaultValue, result);
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return DYHtml5Activity.this.isFinishing() || super.onJsBeforeUnload(view, url, message,
                    result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            // TODO: 2017/5/18 处理进度
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (!mShowHtmlTitle) return;
            if (title.length() > 10) {
                title = title.substring(0, 10);
                title += "...";
            }
            mToolbar.setTitle(title);
        }

        //多窗口window
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            return true;
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            //参数2：支持h5定位；
            //参数3：是否希望内核记住ff
            callback.invoke(origin,true,false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    /**
     * 辅助webview处理请求事件、通知
     */
    private class  CustomWebViewClient extends  WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //不使用外部的浏览器打开url
            mWebView.loadUrl(url);
            return true;
        }

    }

}
