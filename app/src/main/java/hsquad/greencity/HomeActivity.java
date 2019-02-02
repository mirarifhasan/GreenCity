package hsquad.greencity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import hsquad.greencity.Common.Common;
import hsquad.greencity.Database.Database;
import hsquad.greencity.Interface.ItemClickListener;
import hsquad.greencity.Model.Category;
import hsquad.greencity.Model.Token;
import hsquad.greencity.ViewHolder.MenuViewHolder;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference table_category;

    TextView textFullName;

    RecyclerView recyler_menu;
    RecyclerView.LayoutManager layoutManager;

    SharedPreferences sharePref;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    CounterFab fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
                );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext()))
                    loadMenu();
                else{
                    Toast.makeText(getBaseContext(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Default, load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext()))
                    loadMenu();
                else{
                    Toast.makeText(getBaseContext(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


        //Initial Firebase
        database = FirebaseDatabase.getInstance();
        table_category = database.getReference("Category");

        fab = (CounterFab) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(cartIntent);
            }
        });
        fab.setCount(new Database(this).getCountCart());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set name for user
        View headerView = navigationView.getHeaderView(0);
        textFullName = (TextView)headerView.findViewById(R.id.fullNameTV);
        textFullName.setText(Common.currentUser.getname());


        //Load Menu
        recyler_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        recyler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyler_menu.setLayoutManager(layoutManager);


        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());
    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, false); // false becauser, this token send from client app
        tokens.child(Common.currentUser.getphone()).setValue(data);
    }

    private void loadMenu() {

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, table_category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);

                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Get Category Id and Send to new Activity
                        Intent plantServiceList = new Intent(HomeActivity.this, PlantServiceListActivity.class);
                        //Because category is key, so we get key of this item
                        plantServiceList.putExtra("CategoryID", adapter.getRef(position).getKey());
                        startActivity(plantServiceList);
                    }
                });

            }
        };
        recyler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.refresh)
            loadMenu();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(cartIntent);
        }
        else if (id == R.id.nav_help) {
            Intent orderIntent = new Intent(HomeActivity.this, HelpActivity.class);
            startActivity(orderIntent);
        }
        else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(HomeActivity.this, OrderStatus.class);
            startActivity(orderIntent);
        }
        else if(id == R.id.nav_changePassword){
            showChangePasswordDialog();
        }
        else if (id == R.id.nav_log_out) {

            Intent navLogOut = new Intent(HomeActivity.this, LoginActivity.class);
            sharePref = getSharedPreferences("userInfoRef", Context.MODE_PRIVATE);
            sharePref.edit().putString("phoneRef", "").apply();
            sharePref.edit().putString("passRef", "").apply();
            startActivity(navLogOut);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layoyt_pwd = inflater.inflate(R.layout.change_password_layout, null);

        final EditText mOldPass = (EditText)layoyt_pwd.findViewById(R.id.oldPasswordET);
        final EditText mNewPass = (EditText)layoyt_pwd.findViewById(R.id.newPasswordET);
        final EditText mReNewPass = (EditText)layoyt_pwd.findViewById(R.id.reNewPasswordET);

        alertDialog.setView(layoyt_pwd);

        //Button
        alertDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Change pass here
                final android.app.AlertDialog waitingDialog = new SpotsDialog(HomeActivity.this);
                waitingDialog.show();

                if(mOldPass.getText().toString().equals(Common.currentUser.getpassword())){

                    if(mNewPass.getText().toString().equals(mReNewPass.getText().toString())){
                        Map<String , Object>passwordUpdate = new HashMap<>();
                        passwordUpdate.put("password", mNewPass.getText().toString());

                        //Make update
                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("Users List");
                        user.child(Common.currentUser.getphone()).updateChildren(passwordUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                Toast.makeText(HomeActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        waitingDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "New password doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    waitingDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "Wrong old password", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }
}
