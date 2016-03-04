package biz.coddo.behelpful;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import biz.coddo.behelpful.Adapter.ResponseActivityAdapter;
import biz.coddo.behelpful.ServerApi.MarkerUpdateService;

public class ResponseActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ResponseActivityAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DBConnector dbConnector;
    private static final int CM_DELETE_ID = 0;
    private static final int CM_DELETE_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_response);

        mRecyclerView = (RecyclerView) findViewById(R.id.response_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        dbConnector = new DBConnector(this);

        mAdapter = new ResponseActivityAdapter(dbConnector.getAllResponses());
        MarkerUpdateService.setIsChangeResponseFalse();
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MarkerUpdateService.setResponseActivityId(this);
        updateResponseList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MarkerUpdateService.setResponseActivityId(null);
        if (MarkerUpdateService.isChangeResponse())
            updateResponseList();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_response);
        menu.add(0, CM_DELETE_ALL, 0, R.string.delete_all_response);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CM_DELETE_ID:
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                deleteResponseById(acmi.position);
                break;
            case CM_DELETE_ALL:
                deleteAllResponse();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteAllResponse() {
        dbConnector.deleteAllResponses();
        updateResponseList();
    }

    private void deleteResponseById(int position) {
        dbConnector.deleteResponse(mAdapter.getResponseArrayList().get(position).getDbID());
        mAdapter.delResponseById(position);
        mAdapter.notifyDataSetChanged();
    }

    public void updateResponseList() {
        mAdapter.setResponseArrayList(dbConnector.getAllResponses());
        mAdapter.notifyDataSetChanged();
        MarkerUpdateService.setIsChangeResponseFalse();
    }
}
