package biz.coddo.behelpful.AppMap;


import android.content.Context;
import android.support.v4.app.Fragment;

import biz.coddo.behelpful.DTO.MarkerDTO;
import biz.coddo.behelpful.R;


public abstract class AppMap extends Fragment {

    onMarkerClickListener markerClickListener;

    public static AppMap getMap(int appMapInt){
        AppMap appMap = null;
        switch (appMapInt) {
            case 0:
                appMap = new AppMapGoogleMap();
                break;
        }
        return appMap;
    }

    public static int getAppMapContainer(int appMapInt){
        int container = R.id.content_main;
        if (appMapInt == MapList.GOOGLE_MAP.getMapId())
                container = R.id.content_main;
        return container;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        markerClickListener = (onMarkerClickListener) getActivity();
    }

    public abstract void setCameraOnMyLocation();

    public abstract void removeMark(int markerID);

    public abstract void addMark(MarkerDTO marker);

    public abstract void addAllMarker();

    //This interface return to MainActivity userId of clicked marker.
    //Put "markerClickListener.onMarkerClick(int Id)" at your AppMap onMarkerClick method
    public interface onMarkerClickListener{
        void onMarkerClick(int id);
    }

    int setIconByID(int id) {
        int idIcon = R.mipmap.ic_accident_marker;
        switch (id) {
            case 1:
                idIcon = R.mipmap.ic_emergency_marker;
                break;
            case 2:
                idIcon = R.mipmap.ic_accident_marker;
                break;
            case 3:
                idIcon = R.mipmap.ic_evacuation_marker;
                break;
            case 4:
                idIcon = R.mipmap.ic_repair_marker;
                break;
            case 5:
                idIcon = R.mipmap.ic_ready_marker;
                break;
        }
        return idIcon;
    }

    public enum MapList {
        GOOGLE_MAP (0);

        private int mapId;
        MapList(int mapId){
            this.mapId = mapId;
        }

        public int getMapId() {
            return mapId;
        }
    }
}
