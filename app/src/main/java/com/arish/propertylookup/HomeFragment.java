package com.arish.propertylookup;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.arish.propertylookup.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private LinearLayout searchLayout;

    private ExtendedFloatingActionButton postPropertyFAB;

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    private SwipeRefreshLayout homeSwipeRefreshLayout;

    private ArrayList<PropertyPostModelClass> postList;

    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        // if bottom navigation view is hidden, make it visible
        // bottom navigation view is set to be hidden in fragments like ViewFullPostFragment
        if(getActivity() != null)
            ((MainActivity)getActivity()).getNav().setVisibility(View.VISIBLE);

        postPropertyFAB = view.findViewById(R.id.post_property_fab);
        searchLayout = view.findViewById(R.id.search_layout);
        recyclerView = view.findViewById(R.id.home_posts_recyclerview);
        homeSwipeRefreshLayout = view.findViewById(R.id.home_swipe_refresh_layout);

        firebaseFirestore = FirebaseFirestore.getInstance();

        postList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new HomePostsListRecyclerAdapter(postList);
        recyclerView.setAdapter(adapter);

        postPropertyFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), AddPostActivity.class));
            }
        });

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), SearchPropertyActivity.class));
            }
        });

        // fetches data from firebase and passes it to the HomePostsListRecyclerAdapter
        populateRecyclerView();

        // again fetches data from firebase and passes it to the HomePostsListRecyclerAdapter on swipe refresh
        homeSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postList.clear();
                populateRecyclerView();
                homeSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void populateRecyclerView() {

        // add snapshot listener to the 'Posts' collection of firebase firestore
        firebaseFirestore
            .collection("Posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (documentSnapshots != null) {

                        // loop through all the documents of the collection
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                // make PropertyPostModelClass object from each document
                                PropertyPostModelClass obj = new PropertyPostModelClass(
                                        doc.getDocument().getId(),
                                        doc.getDocument().getString("propertyType"),
                                        doc.getDocument().getString("buyOrSell"),
                                        doc.getDocument().getString("rent"),
                                        (ArrayList<String>) doc.getDocument().get("images"),
                                        doc.getDocument().getString("hnoStreetName"),
                                        doc.getDocument().getString("locality"),
                                        doc.getDocument().getString("city"),
                                        doc.getDocument().getString("cityLowercase"),
                                        doc.getDocument().getDouble("price"),
                                        doc.getDocument().getDouble("age"),
                                        doc.getDocument().getString("otherDetails"),
                                        doc.getDocument().getString("userID"),
                                        doc.getDocument().getString("userName"),
                                        doc.getDocument().getString("userEmail"),
                                        doc.getDocument().getString("userMobile"),
                                        doc.getDocument().getString("userAddress")
                                );

                                // add object to the post list
                                postList.add(obj);

                                // notify adapter about changes
                                adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
    }
}