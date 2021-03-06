package edu.uta.groceryplanner;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uta.groceryplanner.adapters.BuyListAdapter;
import edu.uta.groceryplanner.beans.ListBean;


/**
 * A simple {@link Fragment} subclass.
 */
public class DraftFragment extends Fragment implements View.OnClickListener{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListBean> beanList;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton mainFab,personalFab,groupFab,createGroupFab;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    DatabaseReference draftListRef;
    public DraftFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        draftListRef = FirebaseDatabase.getInstance().getReference("draftList");
        if(firebaseAuth==null){
            startActivity(new Intent(getContext(),LoginActivity.class));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_draft, container, false);
        recyclerView=view.findViewById(R.id.draftRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        beanList=new ArrayList<ListBean>();
        mainFab = view.findViewById(R.id.mainFab);
        personalFab = view.findViewById(R.id.personalFab);
        groupFab = view.findViewById(R.id.groupFab);
        createGroupFab=view.findViewById(R.id.createGroupFab);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_backward);
        mainFab.setOnClickListener(this);
        personalFab.setOnClickListener(this);
        groupFab.setOnClickListener(this);
        createGroupFab.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.mainFab:
                animateFAB();
                break;
            case R.id.personalFab:
                startActivity(new Intent(getContext(),PersonalListActivity.class));
                break;
            case R.id.groupFab:
                break;
            case R.id.createGroupFab:
                break;
        }
    }
    public void animateFAB() {

        if (isFabOpen) {
            mainFab.startAnimation(rotate_backward);
            personalFab.startAnimation(fab_close);
            groupFab.startAnimation(fab_close);
            createGroupFab.startAnimation(fab_close);
            personalFab.setClickable(false);
            groupFab.setClickable(false);
            createGroupFab.setClickable(false);
            isFabOpen = false;
        } else {
            mainFab.startAnimation(rotate_forward);
            personalFab.startAnimation(fab_open);
            groupFab.startAnimation(fab_open);
            createGroupFab.startAnimation(fab_open);
            personalFab.setClickable(true);
            groupFab.setClickable(true);
            createGroupFab.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public void onStart() {
        draftListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                beanList.clear();
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()){
                    ListBean listBean=dataSnap.getValue(ListBean.class);
                    beanList.add(listBean);
                }
                adapter=new BuyListAdapter(beanList,getContext(),firebaseAuth);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        super.onStart();
    }
}
