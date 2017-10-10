/*  Copyright (C) 2015-2017 Andreas Shimokawa, Carsten Pfeiffer, Daniele
    Gobbetti, João Paulo Barraca, protomors, Quallenauge, Sami Alaoui

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge.model;

import android.support.annotation.DrawableRes;

import nodomain.freeyourgadget.gadgetbridge.R;

/**
 * For every supported device, a device type constant must exist.
 *
 * Note: they key of every constant is stored in the DB, so it is fixed forever,
 * and may not be changed.
 */
public enum DeviceType {
    UNKNOWN(-1, R.drawable.ic_device_default, R.drawable.ic_device_default_disabled),
    PEBBLE(1, R.drawable.ic_device_pebble, R.drawable.ic_device_pebble_disabled),
    MIBAND(10, R.drawable.ic_device_miband, R.drawable.ic_device_miband_disabled),
    MIBAND2(11, R.drawable.ic_device_miband, R.drawable.ic_device_miband_disabled),
    AMAZFITBIP(12, R.drawable.ic_device_hplus, R.drawable.ic_device_hplus_disabled),
    VIBRATISSIMO(20, R.drawable.ic_device_lovetoy, R.drawable.ic_device_lovetoy_disabled),
    LIVEVIEW(30, R.drawable.ic_device_default, R.drawable.ic_device_default_disabled),
    HPLUS(40, R.drawable.ic_device_hplus, R.drawable.ic_device_hplus_disabled),
    MAKIBESF68(41, R.drawable.ic_device_hplus, R.drawable.ic_device_hplus_disabled),
    EXRIZUK8(42, R.drawable.ic_device_hplus, R.drawable.ic_device_hplus_disabled),
    NO1F1(50, R.drawable.ic_device_hplus, R.drawable.ic_device_hplus_disabled),
    TECLASTH30(60, R.drawable.ic_device_h30_h10, R.drawable.ic_device_h30_h10_disabled),
    // Single device made of L + R (71, 71)
    // but each earbut can be controlled independently
    HERE(70, R.drawable.ic_device_default, R.drawable.ic_device_default_disabled),
    // HERER(71, R.drawable.ic_device_here, R.drawable.ic_device_here_disabled),
    // HEREL(72, R.drawable.ic_device_here, R.drawable.ic_device_here_disabled),
    TEST(1000, R.drawable.ic_device_default, R.drawable.ic_device_default_disabled);

    private final int key;
    @DrawableRes
    private final int defaultIcon;
    @DrawableRes
    private final int disabledIcon;

    DeviceType(int key, int defaultIcon, int disabledIcon) {
        this.key = key;
        this.defaultIcon = defaultIcon;
        this.disabledIcon = disabledIcon;
    }

    public int getKey() {
        return key;
    }

    public boolean isSupported() {
        return this != UNKNOWN;
    }

    public static DeviceType fromKey(int key) {
        for (DeviceType type : values()) {
            if (type.key == key) {
                return type;
            }
        }
        return DeviceType.UNKNOWN;
    }

    @DrawableRes
    public int getIcon() {
        return defaultIcon;
    }

    @DrawableRes
    public int getDisabledIcon() {
        return disabledIcon;
    }
}
