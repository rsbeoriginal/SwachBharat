package com.teamhack.swachbharat.Social;


import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamhack.swachbharat.PermissionUtils;
import com.teamhack.swachbharat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    RecyclerView rv_feed;
    SocialAdapter socialAdapter;
    List<Social> feedList;
    LinearLayoutManager linearLayoutManager;
    private DatabaseReference databaseReference;
    ValueEventListener feedListener;
    final static String SOCIAL_CHILD="Social";
    ProgressDialog progressDialog;
    SocialDialog socialDialog;

    public SocialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_social, container, false);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading . . .");
        progressDialog.show();
        rv_feed= (RecyclerView) v.findViewById(R.id.recyclerView);
        feedList=new ArrayList<>();
        socialAdapter = new SocialAdapter(getActivity(),feedList);
        linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rv_feed.setAdapter(socialAdapter);
        rv_feed.setLayoutManager(linearLayoutManager);
        databaseReference= FirebaseDatabase.getInstance().getReference().child(SOCIAL_CHILD);
        feedListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                feedList.clear();
                for(DataSnapshot feedSnapshot:dataSnapshot.getChildren()){
                    feedList.add(feedSnapshot.getValue(Social.class));
                    socialAdapter.notifyDataSetChanged();
                }
                progressDialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.hide();
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.addValueEventListener(feedListener);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab_social);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socialDialog = new SocialDialog(getActivity());
                socialDialog.show();
            }
        });
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            socialDialog = new SocialDialog(getActivity());
            socialDialog.show();
            socialDialog.enableMyLocation();
        } else {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseReference.removeEventListener(feedListener);
    }
}
