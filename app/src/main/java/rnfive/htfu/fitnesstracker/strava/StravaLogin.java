package rnfive.htfu.fitnesstracker.strava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import rnfive.htfu.fitnesstracker.R;
import com.rn5.libstrava.authentication.model.AuthenticationType;

import androidx.appcompat.app.AppCompatActivity;

import static rnfive.htfu.fitnesstracker.MainActivity.bDarkMode;
import static com.rn5.libstrava.common.model.Constants.STRAVA_CODE;
import static com.rn5.libstrava.common.model.Constants.STRAVA_CLIENT_ID_STRING;

public class StravaLogin extends AppCompatActivity {

    private WebView webView;
    private static final String redirectUri = "http://localhost";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_strava_login);
        //Toolbar myToolbar = findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);
        //myToolbar.setTitle(getString(R.string.strava_auth));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator((bDarkMode?R.drawable.ic_arrow_back_white_24dp:R.drawable.ic_arrow_back_black_24dp));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.strava_login);
        }

        webView = findViewById(R.id.login_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        setWebViewClient();

        Uri uri = Uri.parse("https://www.strava.com/oauth/mobile/authorize")
                .buildUpon()
                .appendQueryParameter("client_id", STRAVA_CLIENT_ID_STRING)
                .appendQueryParameter("redirect_uri", redirectUri)
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("approval_prompt", "auto")
                .appendQueryParameter("scope", "activity:read_all")
                .build();

        webView.loadUrl(uri.toString());
    }

    private void setWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(Uri.parse(url)) || super.shouldOverrideUrlLoading(view, url);
            }

            //@TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                return handleUrl(uri) || super.shouldOverrideUrlLoading(view, request);
            }

            private boolean handleUrl(Uri uri) {
                if (uri.toString().startsWith(redirectUri)) {
                    String code = uri.getQueryParameter("code");
                    //String error = uri.getQueryParameter("error");
                    return makeResult(code);
                }
                return false;
            }

            private boolean makeResult(String code) {
                if (code != null && !code.isEmpty()) {

                    new StravaAuthenticationExecutor(AuthenticationType.AUTHENTICATE, null, code).run();
                    //StravaAuthenticationAsync stravaAuthentication = new StravaAuthenticationAsync(AuthenticationType.AUTHENTICATE,null);
                    //stravaAuthentication.execute(code);

                    Intent result = new Intent();
                    result.putExtra(STRAVA_CODE, code);
                    setResult(RESULT_OK, result);
                    finish();
                    return true;
                }
                finish();
                return false;
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }
}
