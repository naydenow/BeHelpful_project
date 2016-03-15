package biz.coddo.behelpful;

import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import biz.coddo.behelpful.Adapter.ResponseActivityAdapter;
import biz.coddo.behelpful.DTO.ResponseDTO;
import biz.coddo.behelpful.Notification.MarkerNotification;
import biz.coddo.behelpful.Notification.ResponseNotification;
import biz.coddo.behelpful.dialogs.ResponseClickDialog;
import biz.coddo.behelpful.dialogs.ResponseDeleteAllDialog;

public class ResponseActivity extends AppCompatActivity {

    private static final String TAG = "ResponseActivity";
    private RecyclerView mRecyclerView;
    private ResponseActivityAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DBConnector dbConnector;
    private Toolbar toolbar;
    private static boolean responseActivityStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_response);

        initToolbar();

        initRecyclerViewDBConnectorAndSetAdapter();

    }

    private void initRecyclerViewDBConnectorAndSetAdapter() {

        mRecyclerView = (RecyclerView) findViewById(R.id.response_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        dbConnector = new DBConnector(this);

        mAdapter = new ResponseActivityAdapter(dbConnector.getAllResponses());
        DTOUpdateService.setIsChangeResponseFalse();

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(getOnItemClickListener());
    }

    private ResponseActivityAdapter.OnItemClickListener getOnItemClickListener(){
        return new ResponseActivityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.i(TAG, "onResponseClick");
                Bundle args = new Bundle();
                args.putInt("id", position);
                DialogFragment responseClickDialog = new ResponseClickDialog();
                responseClickDialog.setArguments(args);
                responseClickDialog.show(getFragmentManager(), "ResponseClickDialog");
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        responseActivityStart = true;
        DTOUpdateService.newResponseNotifyCount = 0;
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ResponseNotification.NOTIFY_ID);
        updateResponseList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        responseActivityStart = false;
    }

    public void deleteAllResponse() {
        dbConnector.deleteAllResponses();
        mAdapter.setResponseArrayList(new ArrayList<ResponseDTO>());
        mAdapter.notifyDataSetChanged();
    }

    public void deleteResponseById(int position) {
        dbConnector.deleteResponse(mAdapter.getResponseArrayList().get(position).getDbID());
        mAdapter.delResponseById(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void updateResponseList() {
        int oldLength = mAdapter.getItemCount();
        mAdapter.setResponseArrayList(dbConnector.getAllResponses());
        int newLength = mAdapter.getItemCount();
        for (int i = newLength - oldLength -1; i >= 0; i--)
        mAdapter.notifyItemInserted(i);
        DTOUpdateService.setIsChangeResponseFalse();
    }

    public ResponseActivityAdapter getAdapter() {
        return mAdapter;
    }

    private void initToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle(R.string.responses);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_response, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_responses_del_all:
                DialogFragment responseDeleteAll = new ResponseDeleteAllDialog();
                responseDeleteAll.show(getFragmentManager(), "ResponseDeleteAllDialog");
                break;
        }
        return true;
    }

    public static boolean isResponseActivityStart() {
        return responseActivityStart;
    }
}
