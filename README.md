# VirtualCoach: Android app

The mobile app for the VirtualCoach app that handles the connection with the MetaMotioR sensor via BLE technology.

## How to use Android WebView

Create the WebView and setup the url:

```java
  final String PUBLIC_URL = "https://flex-your-muscle.netlify.com/";

  private WebView mWebView = null;

  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mWebView = (WebView) findViewById(R.id.webview);
      mWebView.loadUrl(PUBLIC_URL);

      WebSettings webSettings = mWebView.getSettings();
      webSettings.setJavaScriptEnabled(true);
  }
```

Add the Android permission to access the Internet on `AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

When the sample is available call the magical js function

```java
double x = something;
double y = something;
double z = something;
double w = something;

mWebView.evaluateJavascript("javascript: " + "updateQuaternion(" + x + "," + y + "," + z + "," + w + ")", null);
```
