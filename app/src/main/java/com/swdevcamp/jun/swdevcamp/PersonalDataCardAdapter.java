package com.swdevcamp.jun.swdevcamp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PersonalDataCardAdapter extends RecyclerView.Adapter<PersonalDataCardAdapter.ViewHolder> {

    Context context;
    int design;
    ArrayList<DataCardItem> items;

    public PersonalDataCardAdapter(Context context, ArrayList<DataCardItem> items, int design) {
        this.context = context;
        this.design = design;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_cardview,null);
        return new ViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DataCardItem item = items.get(position);
        Drawable drawable = context.getResources().getDrawable(item.getImage());
        holder.preview.setBackground(drawable);
        holder.title.setText(item.getTitle());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailActivity.class);
                intent.putExtra("image",item.getImage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
        ImageView preview;
        TextView title;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.preview);
            title = itemView.findViewById(R.id.title);
            cardView = itemView.findViewById(R.id.data_card);
        }
    }
}
