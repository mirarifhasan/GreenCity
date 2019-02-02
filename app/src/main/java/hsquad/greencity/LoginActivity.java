package hsquad.greencity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import hsquad.greencity.Common.Common;
import hsquad.greencity.Database.Database;
import hsquad.greencity.Model.User;

public class LoginActivity extends AppCompatActivity {

    Button mLogin;
    EditText mPhone, mPassword;
    TextView mForgetPassword, mSignUp;

    SharedPreferences sharePref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogin = (Button)findViewById(R.id.loginBtn);
        mPhone = (EditText)findViewById(R.id.phoneET);
        mPassword = (EditText)findViewById(R.id.passwordET);
        mForgetPassword = (TextView)findViewById(R.id.forgetPasswordTV);
        mSignUp = (TextView)findViewById(R.id.signupBtn);

        //Initial Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Users List");


        //Shared Preference
        sharePref = getSharedPreferences("userInfoRef", Context.MODE_PRIVATE);
        if(!sharePref.getString("phoneRef", "").equals(""))
        {
            String phn = sharePref.getString("phoneRef", "");
            String pwd = sharePref.getString("passwordRef", "");
            logIn(phn, pwd);
        }


        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intente = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intente);
            }
        });

        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intente = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intente);
            }
        });


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String phoneValue = mPhone.getText().toString().trim();
                final String passwordValue = mPassword.getText().toString().trim();

                if(Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                    mDialog.setMessage("Connecting..");
                    mDialog.show();

                    new Database(getBaseContext()).cleanCart(); //Removing previous cart data

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //Check user exist or not
                            if (phoneValue.isEmpty() || passwordValue.isEmpty()) {
                                mDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Input Misssing", Toast.LENGTH_SHORT).show();
                            }
                            else if (dataSnapshot.child(phoneValue).exists()) {

                                //Get User Information
                                mDialog.dismiss();
                                User user = dataSnapshot.child(phoneValue).getValue(User.class);

                                if (user.getpassword().equals(passwordValue)) {
                                    Toast.makeText(LoginActivity.this, "Sign in successful.", Toast.LENGTH_SHORT).show();

                                    //Shared Preference
                                    editor = sharePref.edit();
                                    editor.putString("phoneRef", phoneValue);
                                    editor.putString("passwordRef", passwordValue);
                                    editor.apply();

                                    user.setphone(phoneValue);
                                    Intent intente = new Intent(LoginActivity.this, HomeActivity.class);
                                    Common.currentUser = user;
                                    startActivity(intente);
                                    finish();
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                    mPassword.setText("");
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Phone number is not registered", Toast.LENGTH_SHORT).show();
                                mPassword.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void logIn(final String phn, final String pwd) {

        //Initial Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Users List");


        if(Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setMessage("Connecting..");
            mDialog.show();

            new Database(getBaseContext()).cleanCart(); //Removing previous cart data

            table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(phn).exists()) {
                        //Get User Information
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phn).getValue(User.class);

                        if (user.getpassword().equals(pwd)) {
                            Toast.makeText(LoginActivity.this, "Sign in successful.", Toast.LENGTH_SHORT).show();

                            user.setphone(phn);
                            Intent intente = new Intent(LoginActivity.this, HomeActivity.class);
                            Common.currentUser = user;
                            startActivity(intente);
                            finish();

                            table_user.removeEventListener(this);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                            mPassword.setText("");
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Phone number is not registered", Toast.LENGTH_SHORT).show();
                        mPassword.setText("");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Toast.makeText(LoginActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
