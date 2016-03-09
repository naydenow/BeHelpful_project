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
    private String mNumber, mUser;
    public static OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        ResponseActivityAdapter.listener = listener;
    }

    public ResponseActivityAdapter(ArrayList<ResponseDTO> responseArrayList) {
        this.responseArrayList = responseArrayList;
    }

    @Override
    public ResponseActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.response_recycler_item, parent, false);
        mUser = parent.getContext().getResources().getString(R.string.user);
        mNumber = parent.getContext().getResources().getString(R.string.phone_number);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResponseDTO response = responseArrayList.get(position);
        String mUserName = mUser + " " + response.getUserName();
        holder.userName.setText(mUserName);
        String mUserPhone = mNumber + " +" + response.getUserPhone();
        holder.userPhone.setText(mUserPhone);
        String[] date = response.getDate().split(" ");
        holder.date.setText(date[0]);
        holder.time.setText(date[1].substring(0, date[1].length() - 3));

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

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ResponseActivityAdapter.listener != null)
                        ResponseActivityAdapter.listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }
}
