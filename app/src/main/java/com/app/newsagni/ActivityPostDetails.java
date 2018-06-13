package com.app.newsagni;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.newsagni.adapter.AdapterComments;
import com.app.newsagni.connection.API;
import com.app.newsagni.connection.RestAdapter;
import com.app.newsagni.connection.callbacks.CallbackDetailsPost;
import com.app.newsagni.data.AppConfig;
import com.app.newsagni.data.Constant;
import com.app.newsagni.data.SharedPref;
import com.app.newsagni.model.Comment;
import com.app.newsagni.model.Post;
import com.app.newsagni.realm.RealmController;
import com.app.newsagni.utils.NetworkCheck;
import com.app.newsagni.utils.Tools;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPostDetails extends AppCompatActivity {

    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";
    public static final String EXTRA_NOTIF = "key.EXTRA_NOTIF";

    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionView, Post obj) {
        Intent intent = new Intent(activity, ActivityPostDetails.class);
        intent.putExtra(EXTRA_OBJC, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, EXTRA_OBJC);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private Toolbar toolbar;
    private ActionBar actionBar;
    private View parent_view;
    private MenuItem read_later_menu;
    private SwipeRefreshLayout swipe_refresh;

    // extra obj
    private Post post;
    private boolean from_notif;

    private SharedPref sharedPref;
    private boolean flag_read_later;
    private Call<CallbackDetailsPost> callbackCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        parent_view = findViewById(android.R.id.content);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        sharedPref = new SharedPref(this);

        // animation transition
        ViewCompat.setTransitionName(findViewById(R.id.image), EXTRA_OBJC);

        // get extra object
        post = (Post) getIntent().getSerializableExtra(EXTRA_OBJC);
        from_notif = getIntent().getBooleanExtra(EXTRA_NOTIF, false);
        initToolbar();

        displayPostData(true);
        prepareAds();

        if (post.isDraft()) requestAction();

        // on swipe
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAction();
            }
        });

        // get enabled controllers
        Tools.requestInfoApi(this);

        // analytics tracking
       // ThisApplication.getInstance().trackScreenView("View post : "+post.title_plain);

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
    }

    private void requestDetailsPostApi() {
        API api = RestAdapter.createAPI();
        callbackCall = api.getPostDetialsById(post.id);
        callbackCall.enqueue(new Callback<CallbackDetailsPost>() {
            @Override
            public void onResponse(Call<CallbackDetailsPost> call, Response<CallbackDetailsPost> response) {
                CallbackDetailsPost resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    post = resp.post;
                    displayPostData(false);
                    swipeProgress(false);
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackDetailsPost> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest();
            }

        });
    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestDetailsPostApi();
            }
        }, Constant.DELAY_TIME_MEDIUM);
    }

    private void onFailRequest() {
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void displayPostData(boolean is_draft) {
        ((TextView) findViewById(R.id.title)).setText(Html.fromHtml(post.title));

        WebView webview = (WebView) findViewById(R.id.content);
        String html_data = "<style>img{max-width:100%;height:auto;} iframe{width:100%;}</style> ";
        html_data += post.content;
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings();
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadData(html_data, "text/html; charset=UTF-8", null);
        // disable scroll on touch
        webview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        ((TextView) findViewById(R.id.date)).setText(Tools.getFormatedDate(post.date));
        ((TextView) findViewById(R.id.comment)).setText(post.comment_count + "");
        ((TextView) findViewById(R.id.tv_comment)).setText(getString(R.string.show_tv_comments) + " (" + post.comment_count + ")");
        ((TextView) findViewById(R.id.category)).setText(Html.fromHtml(Tools.getCategoryTxt(post.categories)));
        Tools.displayImageThumbnail(this, post, ((ImageView) findViewById(R.id.image)));

        if(is_draft){
           return;
        }
        // when show comments click
        (findViewById(R.id.bt_show_comment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.comments.size() <= 0) {
                    Snackbar.make(parent_view, R.string.post_have_no_comment, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                dialogShowComments(post.comments);
            }
        });

        // when post comments click
        (findViewById(R.id.bt_send_comment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppConfig.MUST_REGISTER_TO_COMMENT) {
                    Intent i = new Intent(ActivityPostDetails.this, ActivityWebView.class);

                    i.putExtra(EXTRA_OBJC, post);
                    startActivity(i);
                } else {
                    Tools.dialogCommentNeedLogin(ActivityPostDetails.this, post.url);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            onBackPressed();
        } else if (item_id == R.id.action_share) {
            Tools.methodShare(ActivityPostDetails.this, post);
        } else if (item_id == R.id.action_later) {
            if (post.isDraft()) {
                Snackbar.make(parent_view, R.string.cannot_add_to_read_later, Snackbar.LENGTH_SHORT).show();
                return true;
            }
            String str;
            if (flag_read_later) {
                RealmController.with(this).deletePost(post.id);
                str = getString(R.string.remove_from_msg);
            } else {
                RealmController.with(this).savePost(post);
                str = getString(R.string.added_to_msg);
            }
            Snackbar.make(parent_view, "Post " + str + " Read Later", Snackbar.LENGTH_SHORT).show();
            refreshReadLaterMenu();
        } else if (item_id == R.id.action_browser) {
            Tools.directLinkToBrowser(this, post.url);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_post_details, menu);
        read_later_menu = menu.findItem(R.id.action_later);
        refreshReadLaterMenu();
        return true;
    }

    private void dialogShowComments(List<Comment> items) {

        final Dialog dialog = new Dialog(ActivityPostDetails.this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_comments);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        AdapterComments mAdapter = new AdapterComments(this, items);
        recyclerView.setAdapter(mAdapter);

        ((ImageView) dialog.findViewById(R.id.img_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void refreshReadLaterMenu() {
        flag_read_later = RealmController.with(this).getPost(post.id) != null;
        Drawable drawable = read_later_menu.getIcon();
        if (flag_read_later) {
            drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        } else {
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void prepareAds() {
        if (AppConfig.ENABLE_ADSENSE && NetworkCheck.isConnect(getApplicationContext())) {
            AdView mAdView = (AdView) findViewById(R.id.ad_view);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        } else {
            ((RelativeLayout) findViewById(R.id.banner_layout)).setVisibility(View.GONE);
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = (View) findViewById(R.id.lyt_failed);
        View lyt_main_content = (View) findViewById(R.id.lyt_main_content);

        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_main_content.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_main_content.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction();
            }
        });
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            return;
        }
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(show);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(from_notif) {
            startActivity(new Intent(getApplicationContext(), ActivityMain.class));
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
