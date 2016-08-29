package org.yndongyong.widget.webviewtemplate.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.yndongyong.widget.webviewtemplate.DZYWebViewFragment;

public class MainActivity extends AppCompatActivity {

    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url = "www.baidu.com";
        getSupportFragmentManager().beginTransaction().add(R.id.container, DZYWebViewFragment
                .newInstance(url)).commit();

    }
}
