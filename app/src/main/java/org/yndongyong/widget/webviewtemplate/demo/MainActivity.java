package org.yndongyong.widget.webviewtemplate.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.yndongyong.widget.webviewtemplate.DZYWebViewActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        
        findViewById(R.id.tv_open_web_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.baidu.com";
                Intent intent = DZYWebViewActivity.newInstance(MainActivity.this, url, "标题");
                startActivity(intent);
            }
        });
    }
}
