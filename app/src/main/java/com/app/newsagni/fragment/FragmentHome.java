package com.app.newsagni.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.app.newsagni.ActivityMain;
import com.app.newsagni.ActivityPostDetails;
import com.app.newsagni.R;
import com.app.newsagni.adapter.AdapterPostList;
import com.app.newsagni.connection.API;
import com.app.newsagni.connection.RestAdapter;
import com.app.newsagni.connection.callbacks.CallbackListPost;
import com.app.newsagni.data.Constant;
import com.app.newsagni.model.Post;
import com.app.newsagni.utils.NetworkCheck;
import com.app.newsagni.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHome extends Fragment {

    private View root_view, parent_view;
    private RecyclerView recyclerView;
    private AdapterPostList mAdapter;
    private SwipeRefreshLayout swipe_refresh;
    private Call<CallbackListPost> callbackCall = null;

    private int post_total = 0;
    private int failed_page = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_home, null);
        parent_view = getActivity().findViewById(R.id.main_content);

        swipe_refresh = (SwipeRefreshLayout) root_view.findViewById(R.id.swipe_refresh_layout_home);
        recyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerViewHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterPostList(getActivity(), recyclerView, new ArrayList<Post>());
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterPostList.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Post obj, int position) {
                ActivityPostDetails.navigate((ActivityMain) getActivity(), v.findViewById(R.id.image), obj);
            }
        });

        // detect when scroll reach bottom
        mAdapter.setOnLoadMoreListener(new AdapterPostList.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (post_total > mAdapter.getItemCount() && current_page != 0) {
                    int next_page = current_page + 1;
                    requestAction(next_page);
                } else {
                    mAdapter.setLoaded();
                }
            }
        });

        // on swipe list
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
                mAdapter.resetListData();
                requestAction(1);
            }
        });

        requestAction(1);

        // get enabled controllers
        Tools.requestInfoApi(getActivity());

        return root_view;
    }

    private void displayApiResult(final List<Post> posts) {
        mAdapter.insertData(posts);
        swipeProgress(false);
        if (posts.size() == 0) {
            showNoItemView(true);
        }
    }

    private void requestListPostApi(final int page_no) {
        API api = RestAdapter.createAPI();
        callbackCall = api.getPostByPage(page_no, Constant.POST_PER_REQUEST);
        callbackCall.enqueue(new Callback<CallbackListPost>() {
            @Override
            public void onResponse(Call<CallbackListPost> call, Response<CallbackListPost> response) {
                CallbackListPost resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    post_total = resp.count_total;
                    displayApiResult(resp.posts);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<CallbackListPost> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(page_no);
            }

        });
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        mAdapter.setLoaded();
        swipeProgress(false);
        if (NetworkCheck.isConnect(getActivity())) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "");
        showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
        } else {
            mAdapter.setLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestListPostApi(page_no);
            }
        }, Constant.DELAY_TIME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = (View) root_view.findViewById(R.id.lyt_failed_home);
        ((TextView) root_view.findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) root_view.findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = (View) root_view.findViewById(R.id.lyt_no_item_home);
        ((TextView) root_view.findViewById(R.id.no_item_message)).setText(R.string.no_post);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
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

}

