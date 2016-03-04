package biz.coddo.behelpful;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import biz.coddo.behelpful.ServerApi.ServerSendData;

public class RegConfirmFragment extends Fragment {

    private static final String TAG = "RegFragment";
    EditText confirmKey;
    Button regButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.activity_start_reg_confirm, container, false);
        confirmKey = (EditText) v.findViewById(R.id.confirmKey);
        regButton = (Button) v.findViewById(R.id.confirmRegbutton);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmKey.getText().toString().length() == 4) {
                    TaskSendData tsk = new TaskSendData();
                    tsk.execute(confirmKey.getText().toString(), StartActivity.userPhone);
                }
            }
        });
        return v;
    }

    private class TaskSendData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            regButton.setClickable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            return ServerSendData.sendRegKey(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);
            JSONObject object = null;
            try {
                object = new JSONObject(aString);
                if (object.getString("responce").equals("true")) {
                    StartActivity.userId = object.getInt("id");
                    Log.i(TAG, "id: " + StartActivity.userId);
                    StartActivity.token = object.getString("token");
                    Log.i(TAG, "token: " + StartActivity.token);
                    StartActivity.registrationComplit(getContext());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
