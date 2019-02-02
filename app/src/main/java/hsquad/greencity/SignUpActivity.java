package hsquad.greencity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hsquad.greencity.Common.Common;
import hsquad.greencity.Model.User;

public class SignUpActivity extends AppCompatActivity {

    EditText mName, mPhone, mEmail, mPassword, mRePassword;
    Button mSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mName = (EditText)findViewById(R.id.nameET);
        mPhone = (EditText)findViewById(R.id.phoneET);
        mEmail = (EditText)findViewById(R.id.emailET);
        mPassword = (EditText)findViewById(R.id.passwordET);
        mRePassword = (EditText)findViewById(R.id.rePasswordET);
        mSignup = (Button)findViewById(R.id.signupBtn);

        //Initial Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Users List");


        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nameValue = mName.getText().toString().trim();
                final String phoneValue = mPhone.getText().toString().trim();
                final String emailValue = mEmail.getText().toString().trim();
                final String passwordValue = mPassword.getText().toString().trim();
                final String rePasswordValue = mRePassword.getText().toString().trim();

                if(Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                    mDialog.setMessage("Loading..");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            mDialog.dismiss();

                            //Check for any input empty or not
                            if (phoneValue.isEmpty() || emailValue.isEmpty() || nameValue.isEmpty() || passwordValue.isEmpty()) {

                                Toast.makeText(SignUpActivity.this, "Information Missing", Toast.LENGTH_SHORT).show();
                            }
                            //Check user already registered or not
                            else if (dataSnapshot.child(phoneValue).exists()) {

                                Toast.makeText(SignUpActivity.this, "Phone number already registered", Toast.LENGTH_SHORT).show();
                            }
                            //Two password is not same
                            else if (!rePasswordValue.equals(passwordValue)) {
                                Toast.makeText(SignUpActivity.this, "Password not matched!", Toast.LENGTH_SHORT).show();
                            }
                            //Log in codes
                            else {
                                User user = new User(emailValue, nameValue, passwordValue);
                                table_user.child(phoneValue).setValue(user);
                                Toast.makeText(SignUpActivity.this, "Sign up complete", Toast.LENGTH_SHORT).show();

                                Intent intente = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intente);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(SignUpActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
