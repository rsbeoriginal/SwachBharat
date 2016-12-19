package com.teamhack.swachbharat.Share;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamhack.swachbharat.Profile.User;
import com.teamhack.swachbharat.R;

/**
 * Created by Rishi on 19-12-2016.
 */

public class TaskDialog extends Dialog implements View.OnClickListener {

    private static final String SHARE_CHILD = "Share", STATUS = "status";
    static int flag=0;
    Button bt_ok,bt_cancel;
    Share share;
    Context context;
    ProgressDialog progressDialog;
    RadioGroup radiogroup;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ValueEventListener currentUserListener;

    public TaskDialog(Context context,Share share) {
        super(context);
        this.context=context;
        this.share=share;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Share");
        currentUserListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(share.getCreatedBy().uid.contentEquals(firebaseUser.getUid()))
                {
                    flag=1;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(currentUserListener);

        if(flag==0)
        {
            setContentView(R.layout.dialogue_task);
            bt_cancel= (Button) findViewById(R.id.bt_task_dialog_cancel);
            bt_ok= (Button) findViewById(R.id.bt_task_dialog_ok);
            radiogroup= (RadioGroup) findViewById(R.id.radiogroup_task);
            bt_ok.setOnClickListener(this);
            bt_cancel.setOnClickListener(this);
        }

        else if(flag==1)
        {
            setContentView(R.layout.dialogue_current_user_task);
            bt_cancel=(Button) findViewById(R.id.bt_current_user_task_dialog_cancel);
            bt_ok=(Button) findViewById(R.id.bt_current_user_task_dialog_ok);
            radiogroup= (RadioGroup) findViewById(R.id.radiogroup_current_user_task);
            bt_ok.setOnClickListener(this);
            bt_cancel.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_task_dialog_cancel:
                dismiss();
                break;
            case R.id.bt_task_dialog_ok:
                changeStatus();
                break;
        }
    }

    private void changeStatus() {

        if(flag==0)
        {
            if(getCategory().contentEquals(context.getResources().getString(R.string.radio_bt_take_task))){
                showProgressBar();
                FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                share.status="Taken";
                share.takenBy=new User();
                share.takenBy.uid=firebaseUser.getUid();
                share.takenBy.name=firebaseUser.getDisplayName();
                share.takenBy.email=firebaseUser.getEmail();
                DatabaseReference dataRef=FirebaseDatabase.getInstance().getReference().child(SHARE_CHILD).child(share.key);
                dataRef.setValue(share).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideProgressBar();
                        Toast.makeText(context, "Database updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                        Toast.makeText(context, "Failed to update the status", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            dismiss();
        }

        else if(flag==1)
        {
            if(getCategory().contentEquals(context.getResources().getString(R.string.radio_bt_delete_task)))
            {
                showProgressBar();
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(SHARE_CHILD);
                databaseReference.child(share.key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideProgressBar();
                        Toast.makeText(context, "Database updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                        Toast.makeText(context, "Failed to update the status", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            else if (getCategory().contentEquals(context.getResources().getString(R.string.radio_bt_completed)))
            {
                showProgressBar();
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(SHARE_CHILD);
                databaseReference.child(STATUS).setValue("Completed").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideProgressBar();
                        Toast.makeText(context, "Database updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                        Toast.makeText(context, "Failed to update the status", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            dismiss();
        }
    }

    private void hideProgressBar() {
        progressDialog.hide();
    }

    private void showProgressBar() {
        progressDialog=new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Posting . . .");
        progressDialog.show();
    }

    public String getCategory() {
        int id=radiogroup.getCheckedRadioButtonId();
        RadioButton r_bt= (RadioButton) findViewById(id);
        return r_bt.getText().toString();
    };

}
