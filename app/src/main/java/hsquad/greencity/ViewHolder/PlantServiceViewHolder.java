package hsquad.greencity.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import hsquad.greencity.Interface.ItemClickListener;
import hsquad.greencity.R;


public class PlantServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView plant_Service_Name, plant_service_price;
    public ImageView plant_Service_Image;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public PlantServiceViewHolder(View itemView) {
        super(itemView);

        plant_Service_Name = (TextView)itemView.findViewById(R.id.plant_service_nameTV);
        plant_Service_Image = (ImageView)itemView.findViewById(R.id.plant_service_imageIV);
        plant_service_price = (TextView)itemView.findViewById(R.id.plant_service_priceTV);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
