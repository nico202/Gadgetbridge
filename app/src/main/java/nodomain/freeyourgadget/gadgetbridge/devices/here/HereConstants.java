package nodomain.freeyourgadget.gadgetbridge.devices.here;

/*
* @author Nicolò Balzarotti &lt;nicolo@nixo.xyz&gt;
*/

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport.BASE_UUID;

public final class HereConstants {
    public static final String BASE_HERE_UUID = "d973f2%s-b19e-11e2-9e96-0800200c9a66";

    public static final UUID UUID_CHARACTERISTIC_BATTERY = UUID.fromString(String.format(BASE_UUID, "2A19"));
    public static final UUID UUID_BATTERY_VALUE = UUID.fromString(String.format(BASE_UUID, "180F"));
    public static final UUID UUID_AUDIO_SETTINGS = UUID.fromString(String.format(BASE_HERE_UUID, "e0"));
    public static final UUID UUID_CHARACTERISTIC_INFO = UUID.fromString(String.format(BASE_UUID, "180A"));

    public static final UUID UUID_FW_VERSION = UUID.fromString(String.format(BASE_UUID, "2A26"));
    public static final UUID UUID_VOLUME = UUID.fromString(String.format(BASE_HERE_UUID, "e7"));
    // TODO: Put device in sleep mode, (byte)0x00 sleep, (byte)0x01 wake
    public static final UUID UUID_SLEEP = UUID.fromString(String.format(BASE_HERE_UUID, "eb"));
    public static final UUID UUID_EFFECTS = UUID.fromString(String.format(BASE_HERE_UUID, "e9"));
}
