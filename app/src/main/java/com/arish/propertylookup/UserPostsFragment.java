package com.arish.propertylookup;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arish.propertylookup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserPostsFragment extends Fragment {

    private TextView noPropertyPostedTV;

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    private ArrayList<PropertyPostModelClass> postList;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public UserPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_posts, container, false);

        Toolbar toolbar = view.findViewById(R.id.user_posts_toolbar);

        toolbar.setTitle("Your Posts");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getNav().setSelectedItemId(R.id.home);
                getFragmentManager().beginTransaction().replace(R.id.main_frame_layout,
                        new HomeFragment()).commit();
            }
        });

        // hide bottom nav
        if(getActivity() != null)
            ((MainActivity)getActivity()).getNav().setVisibility(View.VISIBLE);

        noPropertyPostedTV = view.findViewById(R.id.no_property_posted_text_view);

        recyclerView = view.findViewById(R.id.user_posts_recycler_view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        postList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new UserPostsListRecyclerAdapter(postList);
        recyclerView.setAdapter(adapter);

        // fetch data from firebase and passes it to the HomePostsListRecyclerAdapter
        firebaseFirestore
                .collection("Posts")
                .whereEqualTo("userID", firebaseAuth.getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (documentSnapshots != null) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                // loop through all the documents of the collection belonging to signed in user
                                if (doc.getDocument().get("images") != null) {

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

                            recyclerView.setVisibility(View.VISIBLE);
                            noPropertyPostedTV.setVisibility(View.GONE);

                        }
                    }

                });

        return view;
    }
}