package biz.coddo.behelpful.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import biz.coddo.behelpful.DTO.ResponseDTO;
import biz.coddo.behelpful.R;

public class ResponseActivityAdapter extends RecyclerView.Adapter<ResponseActivityAdapter.ViewHolder> {

    private ArrayList<ResponseDTO> responseArrayList;

    public ResponseActivityAdapter(ArrayList<ResponseDTO> responseArrayList) {
        this.responseArrayList = responseArrayList;
    }

    @Override
    public ResponseActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.response_recycler_item, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResponseDTO response = responseArrayList.get(position);
        holder.userName.setText(R.string.user + response.getUserName());
        holder.userPhone.setText(R.string.phone_number + response.getUserPhone());
        String[] date = response.getDate().split(" ");
        holder.date.setText(date[0]);
        holder.time.setText(date[1].substring(0, date[1].length() - 2));

    }

    @Override
    public int getItemCount() {
        return responseArrayList.size();
    }

    public void setResponseArrayList(ArrayList<ResponseDTO> responseArrayList) {
        this.responseArrayList = responseArrayList;
    }

    public void delResponseById(int id) {
        responseArrayList.remove(id);
    }

    public ArrayList<ResponseDTO> getResponseArrayList() {
        return responseArrayList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userPhone, date, time;

        public ViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.response_user_name);
            userPhone = (TextView) v.findViewById(R.id.response_phone_number);
            date = (TextView) v.findViewById(R.id.response_date);
            time = (TextView) v.findViewById(R.id.response_time);
        }

    }
}
