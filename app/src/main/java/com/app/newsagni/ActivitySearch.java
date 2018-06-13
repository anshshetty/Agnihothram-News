package com.app.newsagni;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.app.newsagni.adapter.AdapterPostList;
import com.app.newsagni.connection.API;
import com.app.newsagni.connection.RestAdapter;
import com.app.newsagni.connection.callbacks.CallbackListPost;
import com.app.newsagni.data.Constant;
import com.app.newsagni.model.Post;
import com.app.newsagni.utils.NetworkCheck;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySearch extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBar actionBar;
    private EditText et_search;
    private RecyclerView recyclerView;
    private AdapterPostList mAdapter;
    private ImageButton bt_clear;
    private ProgressBar progressBar;
    private View parent_view;
    private Call<CallbackListPost> callbackCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        parent_view = findViewById(android.R.id.content);

        initComponent();
        initToolbar();

    }

    private void initComponent() {
        et_search = (EditText) findViewById(R.id.et_search);
        bt_clear = (ImageButton) findViewById(R.id.bt_clear);
        bt_clear.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        et_search.addTextChangedListener(textWatcher);

        //set data and list adapter
        mAdapter = new AdapterPostList(this, recyclerView, new ArrayList<Post>());
        recyclerView.setAdapter(mAdapter);

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    searchAction();
                    return true;
                }
                return false;
            }
        });

        mAdapter.setOnItemClickListener(new AdapterPostList.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Post obj, int position) {
                ActivityPostDetails.navigate(ActivitySearch.this, v.findViewById(R.id.image), obj);
            }
        });

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence c, int i, int i1, int i2) {
            if (c.toString().trim().length() == 0) {
                bt_clear.setVisibility(View.GONE);
            } else {
                bt_clear.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void requestSearchApi(final String query) {
        API api = RestAdapter.createAPI();
        callbackCall = api.getSearchPosts(query, Constant.MAX_SEARCH_RESULT);
        callbackCall.enqueue(new Callback<CallbackListPost>() {
            @Override
            public void onResponse(Call<CallbackListPost> call, Response<CallbackListPost> response) {
                CallbackListPost resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    mAdapter.insertData(resp.posts);
                    if (resp.posts.size() == 0) showNotFoundView(true);
                } else {
                    onFailRequest();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CallbackListPost> call, Throwable t) {
                onFailRequest();
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    private void onFailRequest() {
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void searchAction() {
        showFailedView(false, "");
        showNotFoundView(false);
        final String query = et_search.getText().toString().trim();
        if (!query.equals("")) {
            mAdapter.resetListData();
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestSearchApi(query);
                }
            }, Constant.DELAY_TIME );
        } else {
            Toast.makeText(this, R.string.msg_search_input, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            Snackbar.make(parent_view, item.getTitle() + " clicked", Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = (View) findViewById(R.id.lyt_failed);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAction();
            }
        });
    }

    private void showNotFoundView(boolean show) {
        View lyt_no_item = (View) findViewById(R.id.lyt_no_item);
        ((TextView) findViewById(R.id.no_item_message)).setText(R.string.no_post_found);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

}
