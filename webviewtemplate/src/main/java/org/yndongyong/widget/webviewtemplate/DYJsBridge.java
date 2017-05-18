package org.yndongyong.widget.webviewtemplate;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 *
 * js 与 原生交互的方法 统一定义在此处
 *
 * 使用方法：
 *  window.android.HtmlcallJava()
 *  window.android.HtmlcallJava2("dong")
 *
 *  webView.load("javascript:JavacallHtml('')")
 *  webView.load("javascript:JavacallHtml2('dong')")
 *
 * Created by dongzhiyong on 2017/5/18.
 */
public class DYJsBridge {


    private WebView webView;
    public DYJsBridge(WebView webView) {
        this.webView = webView;
    }

    //给html提供的方法，js中：var str = window.android.HtmlcallJava();
    @JavascriptInterface
    public String HtmlcallJava() {
        return "Html call Java";
    }

    //给html提供的有参函数 ： window.jsObj.HtmlcallJava2("dong");
    @JavascriptInterface
    public String HtmlcallJava2(final String param) {
        return "Html call Java : " + param;
    }

    //Html给原生提供的函数
    @JavascriptInterface
    public void JavacallHtml() {
        webView.post(new Runnable() {
            @Override
            public void run() {
                //这里是调用方法
                webView.loadUrl("javascript: showFromHtml()");
            }
        });
    }

    //Html给原生提供的有参函数
    @JavascriptInterface
    public void JavacallHtml2(final String param) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript: showFromHtml2('dong')");
            }
        });
    }
}
