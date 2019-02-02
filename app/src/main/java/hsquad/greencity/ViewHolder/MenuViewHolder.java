package hsquad.greencity.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import hsquad.greencity.Interface.ItemClickListener;
import hsquad.greencity.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView){
        super(itemView);

        txtMenuName = (TextView)itemView.findViewById(R.id.menu_nameTV);
        imageView = (ImageView)itemView.findViewById(R.id.menu_imageIV);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view){
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
