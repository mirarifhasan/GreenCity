package hsquad.greencity;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import hsquad.greencity.Common.Common;
import hsquad.greencity.Database.Database;
import hsquad.greencity.Model.Order;
import hsquad.greencity.Model.PlantService;

public class PlantServiceDetailActivity extends AppCompatActivity {

    TextView plant_service_name, plant_service_price, plant_service_description;
    ImageView plant_service_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String plant_serviceID = "1";
    String plant_price;

    FirebaseDatabase database;
    DatabaseReference plants_services;

    PlantService currentPlantService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_service_detail);

        //Firebase init
        database = FirebaseDatabase.getInstance();
        plants_services = database.getReference("Plant_Service");

        //Init View
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        plant_serviceID,
                        currentPlantService.getName(),
                        numberButton.getNumber(),
                        currentPlantService.getPrice(),
                        currentPlantService.getDiscount(),
                        currentPlantService.getImage()
                ));
                Toast.makeText(PlantServiceDetailActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });


        plant_service_description = (TextView)findViewById(R.id.plant_service_descriptionTV);
        plant_service_name = (TextView)findViewById(R.id.plant_service_nameTV);
        plant_service_price = (TextView)findViewById(R.id.plant_service_priceTV);
        plant_service_image = (ImageView)findViewById(R.id.img_plant_service);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Quantity X Plant price
        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                int quantity = (Integer.parseInt(numberButton.getNumber()));
                int totalPrice = quantity * (Integer.parseInt(plant_price));
                plant_service_price.setText(Integer.toString(totalPrice) + " TK");
            }
        });

        //Get plant or service id from Intent
        if(getIntent() != null)
            plant_serviceID = getIntent().getStringExtra("PlantServiceID");
        if(!plant_serviceID.isEmpty())
        {
            if(Common.isConnectedToInternet(getBaseContext()))
                getDetailPlantService(plant_serviceID);
            else {
                Toast.makeText(PlantServiceDetailActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


    private void getDetailPlantService(String plant_serviceID){
        plants_services.child(plant_serviceID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentPlantService = dataSnapshot.getValue(PlantService.class);

                //Set Image
                Picasso.with(getBaseContext()).load(currentPlantService.getImage()).into(plant_service_image);
                collapsingToolbarLayout.setTitle(currentPlantService.getName());
                plant_service_price.setText(currentPlantService.getPrice() + " TK");
                plant_service_name.setText(currentPlantService.getName());
                plant_service_description.setText(currentPlantService.getDescription());

                plant_price = currentPlantService.getPrice();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
