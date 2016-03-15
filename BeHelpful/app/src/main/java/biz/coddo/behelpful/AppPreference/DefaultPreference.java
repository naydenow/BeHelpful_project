package biz.coddo.behelpful.AppPreference;

import biz.coddo.behelpful.AppMap.AppMap;

public enum DefaultPreference {
    FIRST_TIME_LOADING_BOOLEAN ("firstTimeLoading", null, true, 0),
    USER_TOKEN_STRING ("userToken", null, false, 0),
    USER_ID_INT("userId", null, false,0),
    PLAY_SERVICES_AVAILABLE_BOOLEAN("gooPlayServices", null, true, 0),
    GMAP_INSTALLED_BOOLEAN("gMapInstall", null, true, 0),
    APP_MAP_INT("appMap", null, false, AppMap.MapList.GOOGLE_MAP.getMapId()),
    MARKER_UPGRADE_PERIOD_BACKGROUND_MIN_INT("backgroundUpdatePeriod", null, false, 15),
    MARKER_UPGRADE_PERIOD_ONRESUME_MIN_INT("resumeUpdatePeriod", null, false, 1),
    MARKER_UPGRADE_PERIOD_ONPAUSE_MIN_INT("pauseUpdatePeriod", null, false, 3),
    MARKER_VISIBLE_AREA_SIZE_KM_INT("visibleAreaSize", null, false, 80),
    MARKER_NOTIFICATION_AREA_RADIUS_INT("notificationAreaRadius", null, false, 3),
    MY_MARKER_ID_INT("myMarkerID", null, false, 0),
    USER_PHONE_STRING("userPhone", null, false, 0),
    REGISTRATION_TIME_LONG("regTime", null, false, 0),
    REGISTRATION_STOP_TODAY_BOOLEAN("regShotNumber", null, false, 0);


    private final String name;
    private final String stringValue;
    private final boolean booleanValue;
    private final int intValue;
    DefaultPreference(String name, String stringValue, boolean booleanValue, int intValue) {
        this.name = name;
        this.stringValue = stringValue;
        this.booleanValue = booleanValue;
        this.intValue = intValue;
    }

    public String getName() {
        return name;
    }

    public String getStringValue() {
        return stringValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public int getIntValue() {
        return intValue;
    }
}
