package nodomain.freeyourgadget.gadgetbridge.devices.here;

/*
* @author Nicolò Balzarotti &lt;anothersms@gmail.com&gt;
*/

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;

import de.greenrobot.dao.query.QueryBuilder;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.charts.ChartsActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
//import nodomain.freeyourgadget.gadgetbridge.entities.HereHealthActivitySampleDao;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern; // Regex match name
import java.util.regex.Matcher; // Regex match name

import static nodomain.freeyourgadget.gadgetbridge.GBApplication.getContext;

public class HereCoordinator extends AbstractDeviceCoordinator {
    protected static final Logger LOG = LoggerFactory.getLogger(HereCoordinator.class);
    protected static Prefs prefs  = GBApplication.getPrefs();

    @NonNull
    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Collection<? extends ScanFilter> createBLEScanFilters() {
        ParcelUuid hpService = new ParcelUuid(HereConstants.UUID_SERVICE_HP);
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(hpService).build();
        return Collections.singletonList(filter);
    }

    @NonNull
    @Override
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        Pattern pat = Pattern.compile("H([LR])[A-F0-9]+|HERE-([LR])-[A-F0-9]+");
        Matcher mat = pat.matcher(name);
        // Identify if device is Left or Right
        if (mat.matches()) {
            if (mat.group(1) == "L") {
                return DeviceType.HEREL;
            } else if (mat.group(1) == "R") {
                return DeviceType.HERER;
            }
        }
        return DeviceType.UNKNOWN;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.HERER; //FIXME: SAVE device type
    }

    @Override
    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    @Override
    public Class<? extends Activity> getPrimaryActivity() {
        return ChartsActivity.class;
    }

    @Override
    public InstallHandler findInstallHandler(Uri uri, Context context) {
        return null;
    }

    @Override
    public boolean supportsActivityDataFetching() {
        return true;
    }

    @Override
    public boolean supportsActivityTracking() {
        return true;
    }

    @Override
    public boolean supportsScreenshots() {
        return false;
    }

    @Override
    public boolean supportsAlarmConfiguration() {
        return true;
    }

    @Override
    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    @Override
    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    @Override
    public int getTapString() {
        return R.string.tap_connected_device_for_activity;
    }

    @Override
    public String getManufacturer() {
        return "Zeblaze";
    }

    @Override
    public boolean supportsAppsManagement() {
        return false;
    }

    @Override
    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    @Override
    protected void deleteDevice(@NonNull GBDevice gbDevice, @NonNull Device device, @NonNull DaoSession session) throws GBException {
        Long deviceId = device.getId();
        QueryBuilder<?> qb = session.getHereHealthActivitySampleDao().queryBuilder();
        qb.where(HereHealthActivitySampleDao.Properties.DeviceId.eq(deviceId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public static byte getLanguage(String address) {
        String language = prefs.getString("language", "default");
        Locale locale;

        if (language.equals("default")) {
            locale = Locale.getDefault();
        } else {
            locale = new Locale(language);
        }

        if (locale.getLanguage().equals(new Locale("cn").getLanguage())){
            return HereConstants.ARG_LANGUAGE_CN;
        }else{
            return HereConstants.ARG_LANGUAGE_EN;
        }
    }

    public static byte getTimeMode(String address) {
        String tmode = prefs.getString(HereConstants.PREF_HPLUS_TIMEFORMAT, getContext().getString(R.string.p_timeformat_24h));

        if(tmode.equals(getContext().getString(R.string.p_timeformat_24h))) {
            return HereConstants.ARG_TIMEMODE_24H;
        }else{
            return HereConstants.ARG_TIMEMODE_12H;
        }

    }

    public static byte getUnit(String address) {
        String units = prefs.getString(HereConstants.PREF_HPLUS_UNIT, getContext().getString(R.string.p_unit_metric));

        if(units.equals(getContext().getString(R.string.p_unit_metric))){
            return HereConstants.ARG_UNIT_METRIC;
        }else{
            return HereConstants.ARG_UNIT_IMPERIAL;
        }
    }

    public static byte getUserWeight(String address) {
        ActivityUser activityUser = new ActivityUser();

        return (byte) (activityUser.getWeightKg() & 0xFF);
    }

    public static byte getUserHeight(String address) {
        ActivityUser activityUser = new ActivityUser();

        return (byte) (activityUser.getHeightCm() & 0xFF);
    }

    public static byte getUserAge(String address) {
        ActivityUser activityUser = new ActivityUser();

        return (byte) (activityUser.getAge() & 0xFF);
    }

    public static byte getUserGender(String address) {
        ActivityUser activityUser = new ActivityUser();

        if (activityUser.getGender() == ActivityUser.GENDER_MALE)
            return HereConstants.ARG_GENDER_MALE;

        return HereConstants.ARG_GENDER_FEMALE;
    }

    public static int getGoal(String address) {
        ActivityUser activityUser = new ActivityUser();

        return activityUser.getStepsGoal();
    }

    public static byte getScreenTime(String address) {
        return (byte) (prefs.getInt(HereConstants.PREF_HPLUS_SCREENTIME, 5) & 0xFF);
    }

    public static byte getAllDayHR(String address) {
        Boolean value = (prefs.getBoolean(HereConstants.PREF_HPLUS_ALLDAYHR, true));

        if(value){
            return HereConstants.ARG_HEARTRATE_ALLDAY_ON;
        }else{
            return HereConstants.ARG_HEARTRATE_ALLDAY_OFF;
        }
    }

    public static byte getSocial(String address) {
        //TODO: Figure what this is. Returning the default value

        return (byte) 255;
    }

    public static byte getUserWrist(String address) {
        String value = prefs.getString(HereConstants.PREF_HPLUS_WRIST, getContext().getString(R.string.left));

        if(value.equals(getContext().getString(R.string.left))){
            return HereConstants.ARG_WRIST_LEFT;
        }else{
            return HereConstants.ARG_WRIST_RIGHT;
        }
    }

    public static int getSITStartTime(String address) {
        return prefs.getInt(HereConstants.PREF_HPLUS_SIT_START_TIME, 0);
    }

    public static int getSITEndTime(String address) {
        return prefs.getInt(HereConstants.PREF_HPLUS_SIT_END_TIME, 0);
    }

}
