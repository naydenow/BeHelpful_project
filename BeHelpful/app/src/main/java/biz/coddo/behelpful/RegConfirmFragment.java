package biz.coddo.behelpful;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import biz.coddo.behelpful.ServerApi.ServerAsyncTask;
import biz.coddo.behelpful.dialogs.AppProgressDialog;
import biz.coddo.behelpful.dialogs.RegistrationStopDialog;

public class RegConfirmFragment extends Fragment {

    private static final String TAG = "RegFragment";
    EditText confirmCode;
    Button regButton;
    TextView changePhone;
    StartActivity activity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        activity = (StartActivity) getActivity();

        View v = inflater.inflate(R.layout.reg_confirm_form, container, false);
        confirmCode = (EditText) v.findViewById(R.id.input_confirm_code);
        regButton = (Button) v.findViewById(R.id.btn_signup);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) confirm();
            }
        });

        changePhone = (TextView) v.findViewById(R.id.change_phone);
        changePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhone();
            }
        });

        return v;
    }

    private void changePhone() {
        if (activity.isRegStopToday()) {
            RegistrationStopDialog dialog = new RegistrationStopDialog();
            dialog.show(activity.getFragmentManager(), "RegistrationStopDialog");
        } else {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.activity_start_reg, StartActivity.regFragment);
            fragmentTransaction.commit();
        }
    }

    private void confirm() {
        regButton.setClickable(false);
        AppProgressDialog progressDialog = new AppProgressDialog(getActivity());
        Log.i(TAG, "phone " + StartActivity.userPhone + " code " + confirmCode.getText().toString());
        AsyncTask<String, Void, String[]> tsk = new ServerAsyncTask.SendConfirmKey();
        tsk.execute(confirmCode.getText().toString(), StartActivity.userPhone);
        try {
            String[] str = tsk.get();
            if (str[0] != null && str[1] != null) {
                StartActivity.userId = Integer.parseInt(str[0]);
                Log.i(TAG, "id: " + StartActivity.userId);
                StartActivity.token = str[1];
                Log.i(TAG, "token: " + StartActivity.token);
                activity.registrationComplete();
            } else {
                Toast.makeText(getContext(), getResources().getText(R.string.reg_failed_try_again),
                        Toast.LENGTH_LONG).show();
                progressDialog.cancel();
                regButton.setClickable(true);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            progressDialog.cancel();
            regButton.setClickable(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
            progressDialog.cancel();
            regButton.setClickable(true);
        }
    }

    private boolean validate() {
        boolean valid = true;

        String confirmKey = confirmCode.getText().toString();

        if (confirmKey.isEmpty() || confirmKey.length() < 4) {
            confirmCode.setError(getResources().getString(R.string.length_confirm_key));
            valid = false;
        } else {
            confirmCode.setError(null);
        }
        return valid;
    }


}
