package biz.coddo.behelpful.DTO;

public class ResponseDTO {
    private String userName, userPhone, date;
    private boolean showed;
    private int dbID;

    public ResponseDTO(String userName, String userPhone, String date){
        this.userName = userName;
        this.userPhone = userPhone;
        this.date = date;
        this.showed = false;
    }

    public void setDbID(int dbID) {
        this.dbID = dbID;
    }

    public int getDbID() {
        return dbID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getDate() {
        return date;
    }

    public boolean isShowed() {
        return showed;
    }

    public void setShowed() {
        this.showed = true;
    }
}
