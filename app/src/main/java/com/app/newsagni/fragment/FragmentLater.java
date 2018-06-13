package com.app.newsagni.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.app.newsagni.ActivityMain;
import com.app.newsagni.ActivityPostDetails;
import com.app.newsagni.R;
import com.app.newsagni.adapter.AdapterPostList;
import com.app.newsagni.model.Post;
import com.app.newsagni.realm.RealmController;

public class FragmentLater extends Fragment {

    private View root_view, parent_view;
    private RecyclerView recyclerView;
    private AdapterPostList mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_later, null);
        parent_view = getActivity().findViewById(R.id.main_content);

        recyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerViewLater);
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
        return root_view;
    }

    @Override
    public void onResume() {
        showNoItemView(false);
        if(RealmController.with(this).getPostSize() > 0){
            displayData(RealmController.with(this).getPost());
        } else {
            showNoItemView(true);
        }
        super.onResume();
    }

    private void displayData(final List<Post> posts) {
        mAdapter.resetListData();
        mAdapter.insertData(posts);
        if (posts.size() == 0) {
            showNoItemView(true);
        }
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = (View) root_view.findViewById(R.id.lyt_no_item_later);
        ((TextView) root_view.findViewById(R.id.no_item_message)).setText(R.string.no_post);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }
}
