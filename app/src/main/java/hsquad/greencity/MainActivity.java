package hsquad.greencity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hsquad.greencity.Common.Common;
import hsquad.greencity.Database.Database;
import hsquad.greencity.Model.User;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT =  1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intente = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intente);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
