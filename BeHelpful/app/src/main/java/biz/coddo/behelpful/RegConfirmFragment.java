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

import java.util.concurrent.ExecutionException;

import biz.coddo.behelpful.ServerApi.ServerAsyncTask;

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
                    regButton.setClickable(false);
                    AsyncTask<String, Void, String[]> tsk = new ServerAsyncTask.SendConfirmKey();
                    tsk.execute(confirmKey.getText().toString(), StartActivity.userPhone);
                    try {
                        String[] str = tsk.get();
                        if (str[0] != null && str[1] != null){
                            StartActivity.userId = Integer.parseInt(str[0]);
                            Log.i(TAG, "id: " + StartActivity.userId);
                            StartActivity.token = str[1];
                            Log.i(TAG, "token: " + StartActivity.token);
                            StartActivity activity = (StartActivity) getActivity();
                            activity.registrationComplete();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return v;
    }
}
