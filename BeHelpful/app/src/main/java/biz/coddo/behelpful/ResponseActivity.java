package biz.coddo.behelpful;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import biz.coddo.behelpful.Adapter.ResponseActivityAdapter;
import biz.coddo.behelpful.ServerApi.DTOUpdateService;
import biz.coddo.behelpful.Dialogs.ResponseClickDialog;
import biz.coddo.behelpful.Dialogs.ResponseDeleteAllDialog;

public class ResponseActivity extends AppCompatActivity {

    private static final String TAG = "ResponseActivity";
    private RecyclerView mRecyclerView;
    private ResponseActivityAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DBConnector dbConnector;
    private Toolbar toolbar;

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
        DTOUpdateService.setResponseActivityId(this);
        updateResponseList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DTOUpdateService.setResponseActivityId(null);
    }

    public void deleteAllResponse() {
        dbConnector.deleteAllResponses();
        updateResponseList();
    }

    public void deleteResponseById(int position) {
        dbConnector.deleteResponse(mAdapter.getResponseArrayList().get(position).getDbID());
        mAdapter.delResponseById(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void updateResponseList() {
        mAdapter.setResponseArrayList(dbConnector.getAllResponses());
        mAdapter.notifyDataSetChanged();
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
}
