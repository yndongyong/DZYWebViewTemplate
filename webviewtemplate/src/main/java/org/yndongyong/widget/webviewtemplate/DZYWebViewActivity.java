package org.yndongyong.widget.webviewtemplate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Shader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DZYWebViewActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProgressBar mProgressBar;

    private boolean mProgressVisible;

    private WebView mWebView;
    private TextView mErrorView;


    private static final String LOAD_URL = "param1";
    private static final String TITLE = "param2";

    private String mUrl = "https://www.baidu.com";
    private String mTitle;


    public DZYWebViewActivity() {
        // Required empty public constructor
    }

    public static Intent newInstance(Context context, String url, String title) {
        Bundle args = new Bundle();
        args.putString(LOAD_URL, url);
        args.putString(TITLE, title);
        Intent intent = new Intent(context, DZYWebViewActivity.class);
        intent.putExtras(args);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzywebview_acitivity);

        mUrl = getIntent().getStringExtra(LOAD_URL);
        mTitle = getIntent().getStringExtra(TITLE);

        mWebView = (WebView) findViewById(R.id.web);
        mErrorView = (TextView) findViewById(R.id.error);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        setupToolbar();
        setupWebView();
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (!TextUtils.isEmpty(mTitle)) {
            mToolbar.setTitle(mTitle);
        }
        setSupportActionBar(mToolbar);
    }

    private void setupWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        // NOTE: This gives double tap zooming.
        webSettings.setUseWideViewPort(true);
        mWebView.setWebChromeClient(new ChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mUrl);
    }

    private void setProgressVisible(boolean visible) {
        if (mProgressVisible != visible) {
            mProgressVisible = visible;
            setVisibleOrGone(mProgressBar, visible);
        }
    }

    private void setVisibleOrGone(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
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
        hideError();
        mWebView.reload();
    }

    private void hideError() {
        mErrorView.setVisibility(View.INVISIBLE);
        mErrorView.setText(null);
        mWebView.setVisibility(View.VISIBLE);
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

    private class ChromeClient extends WebChromeClient {

        // NOTE: WebView can be trying to show an AlertDialog after the activity is finished, which
        // will result in a WindowManager$BadTokenException.
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return DZYWebViewActivity.this.isFinishing() || super.onJsAlert(view, url, message,
                    result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return DZYWebViewActivity.this.isFinishing() || super.onJsConfirm(view, url, message,
                    result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                  JsPromptResult result) {
            return DZYWebViewActivity.this.isFinishing() || super.onJsPrompt(view, url, message,
                    defaultValue, result);
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return DZYWebViewActivity.this.isFinishing() || super.onJsBeforeUnload(view, url, message,
                    result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            setProgressVisible(newProgress != 100);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (title.length() > 10) {
                title = title.substring(0, 10);
                title += "...";
            }
            mToolbar.setTitle(title);
        }
    }

}
