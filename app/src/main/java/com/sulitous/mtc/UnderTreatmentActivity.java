package com.sulitous.mtc;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class UnderTreatmentActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private ChildAdapter mChildAdapter;
    private ListenerRegistration registration;
    private FirebaseFirestore mRootRef;
    private int childType = 0;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_treatment);
        toolbar = (Toolbar) findViewById(R.id.myappbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("UnderTreatment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mUser =FirebaseAuth.getInstance().getCurrentUser();
        mRootRef = FirebaseFirestore.getInstance();
        RadioGroup mRadioGroup = findViewById(R.id.radio_childrenType);
        RecyclerView waitingList = findViewById(R.id.under_treatment_list);
        LinearLayoutManager manager = new LinearLayoutManager(UnderTreatmentActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        waitingList.setLayoutManager(manager);
        mChildAdapter = new ChildAdapter(UnderTreatmentActivity.this,mRootRef,1);
        mChildAdapter.getChildType(childType);
        waitingList.setAdapter(mChildAdapter);
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (i == R.id.radio_un_approved){
            childType = 1;
            refreshData();
        }else if (i == R.id.radio_under_treatment){
            childType = 2;
            refreshData();
        }else if (i == R.id.radio_treated){
            childType = 3;
            refreshData();
        }
    }

    private void refreshData() {
        if (registration != null){
            mChildAdapter.clear();
            registration.remove();
            mChildAdapter.getChildType(childType);
           /* SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_file_user), Context.MODE_PRIVATE);
            String UID = sharedPreferences.getString(getString(R.string.uid_key_shared),"");*/
            String uid = mUser.getUid();
            registration = mRootRef.collection("Treatment").document(uid).addSnapshotListener(mChildAdapter);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUser != null) {
            if (registration == null) {
                mChildAdapter.clear();

          /*  SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_file_user), Context.MODE_PRIVATE);
            String UID = sharedPreferences.getString(getString(R.string.uid_key_shared),"");*/
                String uid = mUser.getUid();
                registration = mRootRef.collection("Treatment").document(uid).addSnapshotListener(mChildAdapter);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mUser != null) {
            if (registration != null) {
                mChildAdapter.clear();
                registration.remove();
            }
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (mUser != null) {
            if (registration == null) {
                mChildAdapter.clear();
           /* SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_file_user), Context.MODE_PRIVATE);
            String UID = sharedPreferences.getString(getString(R.string.uid_key_shared),"");*/
                String uid = mUser.getUid();
                registration = mRootRef.collection("Treatment").document(uid).addSnapshotListener(mChildAdapter);
            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();

        if (registration != null){
            mChildAdapter.clear();
            registration.remove();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null){
            registration.remove();
        }
    }
}
