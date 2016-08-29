package org.yndongyong.widget.webviewtemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewUtils;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DZYWebViewActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    
    private static final String LOAD_URL = "param1";

    private String mUrl ="https://www.baidu.com";


    public DZYWebViewActivity() {
        // Required empty public constructor
    }

    public Intent newInstance(Context context, String url) {
        Bundle args = new Bundle();
        args.putString(LOAD_URL, url);
        Intent intent = new Intent(context,DZYWebViewActivity.class);
        intent.putExtras(args);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzywebview_acitivity);
        setupToolbar();
        getSupportFragmentManager().beginTransaction().add(R.id.container, DZYWebViewFragment
                .newInstance(mUrl)).commit();
        
    }
    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

}
