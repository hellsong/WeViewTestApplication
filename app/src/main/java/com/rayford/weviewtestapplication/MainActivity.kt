package com.rayford.weviewtestapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.rayford.weviewtestapplication.ui.theme.WeViewTestApplicationTheme
import java.io.InputStream
import java.util.concurrent.CountDownLatch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeViewTestApplicationTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            ) {
                LaunchNewWebViewPageButton()
            }
            WebPageScreen(
                "https://baidu.com"
            )
        }
    }
}

@Composable
fun LaunchNewWebViewPageButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Button(onClick = {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }) {
        Text(
            text = "launch new webveiw!",
            modifier = modifier
        )
    }
}

@Composable
fun WebPageScreen(url: String, modifier: Modifier = Modifier) {
    // Adding a WebView inside AndroidView
    AndroidView(factory = {
        WebView(it).apply {
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                    if (url?.startsWith("http") == true && url?.contains("png") == true) {
                        return WebResourceResponse("", "utf-8", object : InputStream() {
                            override fun read(): Int {
                                //Simulate a long time loading
                                val countDownLatch = CountDownLatch(1)
                                countDownLatch.await()
                                return 0
                            }

                        })
                    }
                    return super.shouldInterceptRequest(view, url)
                }
            }

            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            settings.javaScriptEnabled = true

            setCookie(context)

            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    }, modifier = modifier)
}

private fun setCookie(context: Context) {
    CookieSyncManager.createInstance(context)
    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptCookie(true)
    cookieManager.setCookie( "baidu.com", "test")
    CookieSyncManager.getInstance().sync()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeViewTestApplicationTheme {
        LaunchNewWebViewPageButton()
    }
}