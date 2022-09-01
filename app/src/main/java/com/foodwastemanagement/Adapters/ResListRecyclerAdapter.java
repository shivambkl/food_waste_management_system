package com.foodwastemanagement.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodwastemanagement.Model.User_Accounts;
import com.foodwastemanagement.R;
import com.foodwastemanagement.ResturentHistory;

import java.util.List;

public class ResListRecyclerAdapter extends RecyclerView.Adapter<ResListRecyclerAdapter.View_Holder>{
    List<User_Accounts> datalist;
    Context context;
    public ResListRecyclerAdapter(List<User_Accounts> datalist, Context context) {
        this.datalist = datalist;
        this.context = context;
    }
    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent , int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.res_item_list, parent,false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }
    @Override
    public void onBindViewHolder(final View_Holder holder, final int i) {

        final String strName = datalist.get(i).getFirstName();
        final String strResName = datalist.get(i).getLastName();
        final String strPassword = datalist.get(i).getPassword();
        final String strMobile = datalist.get(i).getMobile();
        final String strEmail = datalist.get(i).getEmail();
        final String strAddress= datalist.get(i).getAddress();
        final String UserID = datalist.get(i).getUsername();
        final String strImage = datalist.get(i).getUserImage();
        final String strUType = datalist.get(i).getUType();

        holder.tvTitle.setText(strResName);
        holder.tvSubTitle.setText(strAddress);
        holder.tvResMobile.setText(strMobile);
        if(!strImage.equals("")){
            Glide.with(context)
                    .load(strImage)
                    .into(holder.imgResturent);
        }

        holder.Name.setText(strName);

        holder.tvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ResturentHistory.class);
                intent.putExtra("utype",strUType);
                intent.putExtra("userid",UserID);
                Log.d("#33","resid"+UserID);
                context.startActivity(intent);

            }
        });
    }
    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {
        protected TextView Name,tvTitle,tvSubTitle,tvResMobile,tvHistory;
        LinearLayout Linear;
        ImageView imgEvent,imgResturent,imgResWhatsApp;

        public View_Holder(View view) {
            super(view);
            this.Name = (TextView) view.findViewById(R.id.tv_res_contact_name);
            this.tvTitle = (TextView) view.findViewById(R.id.tv_res_title);
            this.tvSubTitle = (TextView) view.findViewById(R.id.tv_res_subtitle);
            this.tvResMobile = (TextView) view.findViewById(R.id.tv_res_mobile);
            this.tvHistory = (TextView) view.findViewById(R.id.tv_history);
            this.imgEvent= (ImageView) view.findViewById(R.id.img_event);
            this.imgResturent= (ImageView) view.findViewById(R.id.img_res);
            this.imgResWhatsApp= (ImageView) view.findViewById(R.id.img_res_whatsapp);
            this.Linear = (LinearLayout) view.findViewById(R.id.linearclick1);
        }
    }


}
