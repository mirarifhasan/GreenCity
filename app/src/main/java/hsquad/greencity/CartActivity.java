package hsquad.greencity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hsquad.greencity.Common.Common;
import hsquad.greencity.Database.Database;
import hsquad.greencity.Model.MyResponse;
import hsquad.greencity.Model.Notification;
import hsquad.greencity.Model.Order;
import hsquad.greencity.Model.Request;
import hsquad.greencity.Model.Sender;
import hsquad.greencity.Model.Token;
import hsquad.greencity.Remote.APIService;
import hsquad.greencity.ViewHolder.CartAdapter;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    SharedPreferences sharePref;

    public TextView txtTotalPrice;
    Button btnPlace;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init Service (notification)
        mService = Common.getFCMService();
        
        //Init
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView)findViewById(R.id.total);
        btnPlace = (Button)findViewById(R.id.btnPlaceOrder);

        loadListPlantService();

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtTotalPrice.getText().equals("0.00"))
                    Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                else
                    showAlertDialog();
            }
        });

    }

    private void showAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address: ");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        final MaterialEditText edtAddress = (MaterialEditText)order_address_comment.findViewById(R.id.order_addressET);
        final MaterialEditText edtComment = (MaterialEditText)order_address_comment.findViewById(R.id.order_commentET);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Create new request
                Request request = new Request(
                        Common.currentUser.getphone(),
                        Common.currentUser.getname(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "0",
                        edtComment.getText().toString(),
                        cart
                        );

                //Submition to firebase
                //We will using System.CurrentMilli to key
                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number).setValue(request);

                //Delete cart
                new Database(getBaseContext()).cleanCart();

                sendNotificationOrder(order_number);

                finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                    Token serverToken = postSnapShot.getValue(Token.class);
                    
                    //Create new payload to send
                    Notification notification = new Notification("GS", "You have new order" + order_number);
                    Sender content = new Sender(serverToken.getToken(), notification);
                    
                    mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            //Only run when get result
                            if(response.code() == 200) {
                                if (response.body().success == 1) {
                                    Toast.makeText(CartActivity.this, "Thank you, Order placed", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else
                                    Toast.makeText(CartActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.e("ERROR", t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadListPlantService() {

        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calculate total price
        int total = 0;
        for(Order order:cart)
            total += (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en", "en");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total).substring(1));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());

        return true;
    }

    private void deleteCart(int position) {
        cart.remove(position);
        new Database(this).cleanCart();
        for(Order item:cart)
            new Database(this).addToCart(item);
        loadListPlantService();
    }
}
