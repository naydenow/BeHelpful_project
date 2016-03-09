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
    MARKER_UPGRADE_PERIOD_ONRESUME_MIN_INT("backgroundUpdatePeriod", null, false, 1),
    MARKER_UPGRADE_PERIOD_ONPAUSE_MIN_INT("backgroundUpdatePeriod", null, false, 3),
    MARKER_VISIBLE_AREA_SIZE_KM_INT("visibleAreaSize", null, false, 80),
    MARKER_NOTIFICATION_AREA_RADIUS_INT("notificationAreaRadius", null, false, 3);


    private final String name;   // в килограммах
    private final String stringValue; // в метрах
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
