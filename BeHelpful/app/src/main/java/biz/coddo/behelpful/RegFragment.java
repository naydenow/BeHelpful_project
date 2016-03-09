package biz.coddo.behelpful;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import biz.coddo.behelpful.ServerApi.ServerAsyncTask;
import biz.coddo.behelpful.ServerApi.ServerSendData;

public class RegFragment extends Fragment {

    private static final String TAG = "RegFragment";
    EditText userName, userPhone;
    TextView inputName, inputPhone;
    Button regButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.activity_start_reg, container, false);
        userName = (EditText) v.findViewById(R.id.userName);
        userPhone = (EditText) v.findViewById(R.id.userPhone);
        regButton = (Button) v.findViewById(R.id.regButton);
        inputName = (TextView) v.findViewById(R.id.inputName);
        inputPhone = (TextView) v.findViewById(R.id.inputPhone);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick");
                if (TextUtils.isEmpty(userName.getText().toString())) inputName.setTextColor(Color.RED);
                else {
                    inputName.setTextColor(Color.BLACK);
                    if (userPhone.getText().toString().length() < 11)
                        inputPhone.setTextColor(Color.RED);
                    else {
                        inputPhone.setTextColor(Color.BLACK);
                        Log.i(TAG, "Условия выполнены");
                        StartActivity.userPhone = userPhone.getText().toString();
                        regButton.setClickable(false);
                        AsyncTask<String, Void, Boolean> tsk = new ServerAsyncTask.SendRegistrationData();
                        tsk.execute(userName.getText().toString(), userPhone.getText().toString());
                        try {
                            if (tsk.get()) {
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.activity_start_reg, StartActivity.regConfirmFragment);
                                fragmentTransaction.commit();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            regButton.setClickable(true);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            regButton.setClickable(true);
                        }
                    }
                }
            }
        });
        return v;
    }
}
