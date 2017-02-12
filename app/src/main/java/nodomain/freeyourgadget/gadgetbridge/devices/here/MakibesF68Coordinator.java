package nodomain.freeyourgadget.gadgetbridge.devices.here;

/*
* @author Nicolò Balzarotti &lt;anothersms@gmail.com&gt;
*/

import android.support.annotation.NonNull;

import nodomain.freeyourgadget.gadgetbridge.devices.here.HereCoordinator;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

/**
 * Pseudo Coordinator for the Makibes F68, a sub type of the HPLUS devices
 */
public class MakibesF68Coordinator extends HereCoordinator {

    @NonNull
    @Override
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if(name != null && name.startsWith("SPORT")){
            return DeviceType.MAKIBESF68;
        }

        return DeviceType.UNKNOWN;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.MAKIBESF68;
    }

    @Override
    public String getManufacturer() {
        return "Makibes";
    }

}
