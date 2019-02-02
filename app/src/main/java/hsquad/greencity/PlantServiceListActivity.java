package hsquad.greencity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import hsquad.greencity.Common.Common;
import hsquad.greencity.Interface.ItemClickListener;
import hsquad.greencity.Model.PlantService;
import hsquad.greencity.ViewHolder.PlantServiceViewHolder;

public class PlantServiceListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference plantServiceList;
    FirebaseRecyclerAdapter<PlantService, PlantServiceViewHolder> adapter;

    String categoryID = "";

    //Search Functionality
    FirebaseRecyclerAdapter<PlantService, PlantServiceViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_service_list);

        //Firebase initial
        database = FirebaseDatabase.getInstance();
        plantServiceList = database.getReference("Plant_Service");

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Get intent here
                if(getIntent() != null){
                    categoryID = getIntent().getStringExtra("CategoryID");
                }
                if(!categoryID.isEmpty() && categoryID != null){
                    if(Common.isConnectedToInternet(getBaseContext()))
                        loadListPlantService(categoryID);
                    else {
                        Toast.makeText(PlantServiceListActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get intent here
                if(getIntent() != null){
                    categoryID = getIntent().getStringExtra("CategoryID");
                }
                if(!categoryID.isEmpty() && categoryID != null){
                    if(Common.isConnectedToInternet(getBaseContext()))
                        loadListPlantService(categoryID);
                    else {
                        Toast.makeText(PlantServiceListActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //Search
                materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
                materialSearchBar.setHint("Enter your needs");
                //materialSearchBar.setSpeechMode(false);  ->No need, because we already define in XML.
                loadSuggest();  // Write function to load Suggest form firebase.

                materialSearchBar.setCardViewElevation(10);

                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //Then user type their text, we will change suggest list.
                        List<String>suggest = new ArrayList<>();

                        for(String search:suggestList) //Loop in suggest list
                        {
                            if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggest.add(search);
                        }
                        materialSearchBar.setLastSuggestions(suggest);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        //Whien search bar is close, restore original adpter
                        if(!enabled)
                            recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        //When search finish, show result of search adapter
                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });
            }
        });


        recyclerView = (RecyclerView)findViewById(R.id.recycler_plant_service);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void startSearch(CharSequence text) {

        searchAdapter = new FirebaseRecyclerAdapter<PlantService, PlantServiceViewHolder>(PlantService.class, R.layout.plant_service_item,
                PlantServiceViewHolder.class, plantServiceList.orderByChild("name").equalTo(text.toString())) {
            @Override
            protected void populateViewHolder(PlantServiceViewHolder viewHolder, PlantService model, int position) {

                viewHolder.plant_Service_Name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.plant_Service_Image);

                final PlantService local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent plantServiceDetail = new Intent(PlantServiceListActivity.this, PlantServiceDetailActivity.class);
                        plantServiceDetail.putExtra("PlantServiceID", searchAdapter.getRef(position).getKey()); // Send Food Id to new activity
                        startActivity(plantServiceDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter); //Set adapter for recycler view is search result
    }

    private void loadSuggest() {
        plantServiceList.orderByChild("menuID").equalTo(categoryID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    PlantService item = postSnapshot.getValue(PlantService.class);
                    suggestList.add(item.getName());
                }
                materialSearchBar.setLastSuggestions(suggestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadListPlantService(String categoryID) {

        adapter = new FirebaseRecyclerAdapter<PlantService, PlantServiceViewHolder>(PlantService.class, R.layout.plant_service_item,
                PlantServiceViewHolder.class, plantServiceList.orderByChild("menuID").equalTo(categoryID)
                /*Like select * from Plant_Service menuId = categoryID*/ ) {
            @Override
            protected void populateViewHolder(PlantServiceViewHolder viewHolder, PlantService model, int position) {

                viewHolder.plant_Service_Name.setText(model.getName());
                viewHolder.plant_service_price.setText(String.format("Price: %s TK", model.getPrice()));
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.plant_Service_Image);

                final PlantService local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent plantServiceDetail = new Intent(PlantServiceListActivity.this, PlantServiceDetailActivity.class);
                        plantServiceDetail.putExtra("PlantServiceID", adapter.getRef(position).getKey()); // Send Food Id to new activity
                        startActivity(plantServiceDetail);
                    }
                });

            }
        };
        //set Adapter
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
