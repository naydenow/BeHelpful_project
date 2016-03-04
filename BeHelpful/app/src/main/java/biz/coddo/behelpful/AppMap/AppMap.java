package biz.coddo.behelpful.AppMap;


import android.content.Context;
import android.support.v4.app.Fragment;

import biz.coddo.behelpful.DTO.MarkerDTO;
import biz.coddo.behelpful.MainActivity;

public abstract class AppMap extends Fragment {

    onMarkerClickListener markerClickListener;

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
        int idIcon = getResources().getIdentifier("ic_emergency", "mipmap", MainActivity.PACKAGE_NAME);
        switch (id) {
            case 1:
                idIcon = getResources().getIdentifier("ic_emergency", "mipmap", MainActivity.PACKAGE_NAME);
                break;
            case 2:
                idIcon = getResources().getIdentifier("ic_accident", "mipmap", MainActivity.PACKAGE_NAME);
                break;
            case 3:
                idIcon = getResources().getIdentifier("ic_evacuation", "mipmap", MainActivity.PACKAGE_NAME);
                break;
            case 4:
                idIcon = getResources().getIdentifier("ic_repair", "mipmap", MainActivity.PACKAGE_NAME);
                break;
            case 5:
                idIcon = getResources().getIdentifier("ic_ready_to_help", "mipmap", MainActivity.PACKAGE_NAME);
                break;
        }
        return idIcon;
    }
}
