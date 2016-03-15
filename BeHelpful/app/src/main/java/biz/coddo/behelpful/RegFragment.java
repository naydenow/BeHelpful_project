package biz.coddo.behelpful;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import biz.coddo.behelpful.ServerApi.ServerAsyncTask;
import biz.coddo.behelpful.ServerApi.ServerSendData;
import biz.coddo.behelpful.dialogs.AppProgressDialog;

public class RegFragment extends Fragment {

    private static final String TAG = "RegFragment";
    EditText userName, userPhone;
    Button regButton;
    StartActivity activity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        activity = (StartActivity) getActivity();
        View v = inflater.inflate(R.layout.reg_form, container, false);
        userName = (EditText) v.findViewById(R.id.userName);
        userPhone = (EditText) v.findViewById(R.id.userPhone);
        userPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        userPhone.append("+", 0, 1);
        regButton = (Button) v.findViewById(R.id.regButton);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration();
            }
        });

        return v;
    }

    public void registration() {
        if (validate()) {
            Log.i(TAG, "UserPhone " + StartActivity.userPhone);
            AppProgressDialog progressDialog = new AppProgressDialog(getActivity());
            regButton.setEnabled(false);
            AsyncTask<String, Void, Boolean> tsk = new ServerAsyncTask.SendRegistrationData();
            tsk.execute(userName.getText().toString(), StartActivity.userPhone);
            try {
                if (tsk.get()) {
                    progressDialog.cancel();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.activity_start_reg, StartActivity.regConfirmFragment);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getContext(), getResources().getText(R.string.reg_failed_try_again), Toast.LENGTH_LONG).show();
                    activity.regFailed();
                    progressDialog.cancel();
                    regButton.setEnabled(true);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                progressDialog.cancel();
                regButton.setEnabled(true);
            } catch (ExecutionException e) {
                e.printStackTrace();
                progressDialog.cancel();
                regButton.setEnabled(true);
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        String name = userName.getText().toString();
        String phone = userPhone.getText().toString().replaceAll("\\D", "");
        Log.i(TAG, "UserPhone on validate " + phone);
        if (name.isEmpty() || name.length() < 3) {
            userName.setError(getResources().getString(R.string.shot_user_name));
            valid = false;
        } else {
            userName.setError(null);
        }

        if (phone.isEmpty() || !Patterns.PHONE.matcher(phone).matches()) {
            userPhone.setError(getResources().getString(R.string.invalid_phone));
            valid = false;
        } else {
            StartActivity.userPhone = phone;
            if (!activity.isNewPhone()) {
                userPhone.setError(getResources().getString(R.string.get_other_phone));
            } else {
                userPhone.setError(null);
            }
        }

        return valid;
    }
}
