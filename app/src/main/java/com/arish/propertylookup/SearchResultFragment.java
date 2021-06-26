package com.arish.propertylookup;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arish.propertylookup.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchResultFragment extends Fragment {

    private Toolbar toolbar;

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    private ArrayList<PropertyPostModelClass> postList;

    private FirebaseFirestore firebaseFirestore;

    public SearchResultFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_search_result, container, false);

        toolbar = view.findViewById(R.id.search_result_toolbar);

        // set title to 'No Property Available', will change if database returns some properties
        toolbar.setTitle("No Property Available");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        // on toolbar's back button press, remove this fragment
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) v.getContext()).getSupportFragmentManager().popBackStack();
            }
        });

        recyclerView = view.findViewById(R.id.search_result_recycler_view);

        firebaseFirestore = FirebaseFirestore.getInstance();

        postList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new SearchResultListRecyclerAdapter(postList);
        recyclerView.setAdapter(adapter);

        Bundle bundle = getArguments();

        String location = bundle.getString("location");
        Double minPrice = bundle.getDouble("minPrice");
        Double maxPrice = bundle.getDouble("maxPrice");
        String propertyType = bundle.getString("propertyType");

        populateRecyclerView(location, minPrice, maxPrice, propertyType);

        return view;
    }

    private void populateRecyclerView(String location, Double minPrice, Double maxPrice, String propertytype) {

        Query query = firebaseFirestore
                .collection("Posts");

        if(!location.equals("")) {
            query = query.whereEqualTo("cityLowercase", location.toLowerCase());
        }

        if(minPrice != -1) {
            query = query.whereGreaterThanOrEqualTo("price", minPrice);
        }

        if(maxPrice != -1) {
            query = query.whereLessThanOrEqualTo("price", maxPrice);
        }

        if(!propertytype.equals("All")) {
            query = query.whereEqualTo("propertyType", propertytype);
        }

        query.orderBy("price").orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (documentSnapshots != null) {

                            int num_results = documentSnapshots.size();
                            if(num_results == 1) {
                                toolbar.setTitle("1 Property");
                            } else {
                                toolbar.setTitle(num_results + " Properties");
                            }

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getDocument().get("images") != null) {
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
                                    postList.add(obj);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
//                        toolbar.setTitle( "0 Search Results Found");
                    }

                });

    }

}