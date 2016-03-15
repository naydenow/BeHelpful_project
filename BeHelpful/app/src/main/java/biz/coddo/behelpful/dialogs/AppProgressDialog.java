package biz.coddo.behelpful.dialogs;

import android.app.ProgressDialog;
import android.content.Context;

import biz.coddo.behelpful.R;

public class AppProgressDialog {

    ProgressDialog progressDialog;

    public AppProgressDialog(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getResources().getString(R.string.wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void cancel(){
        progressDialog.cancel();
    }
}
