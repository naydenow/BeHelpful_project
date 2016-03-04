package biz.coddo.behelpful.DTO;

public class MarkerDTO {

    public double lat, lng;
    public int userId, markerId, markerType;
    public boolean respondOnMarker = false;
    public String date;

    public MarkerDTO(int userId, int markerId, int markerType, double lat, double lng){
        this.userId = userId;
        this.markerId = markerId;
        this.markerType = markerType;
        this.lat = lat;
        this.lng = lng;
        this.respondOnMarker = false;
    }
    private MarkerDTO(){};

    @Override
    public String toString() {
        return "userId: " + userId + " markerId: " + markerId + " markerType: " + markerType
                + " lat: " + lat + " lng: " + lng;
    }
}
