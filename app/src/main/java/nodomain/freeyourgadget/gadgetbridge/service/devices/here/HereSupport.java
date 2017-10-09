/*  Copyright (C) 2017 Niclò Balzarotti

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
package nodomain.freeyourgadget.gadgetbridge.service.devices.here;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.here.HereConstants;
import nodomain.freeyourgadget.gadgetbridge.entities.AudioEffect;
import nodomain.freeyourgadget.gadgetbridge.entities.AudioEffectType;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;

public class HereSupport extends AbstractBTLEDeviceSupport {

    private static final Logger LOG = LoggerFactory.getLogger(HereSupport.class);

    public BluetoothGattCharacteristic batteryCharacteristic = null;
    public BluetoothGattCharacteristic infoCharacteristic = null;
    public BluetoothGattCharacteristic fwCharacteristic = null;
    public BluetoothGattCharacteristic volumeCharacteristic = null;
    public BluetoothGattCharacteristic effectCharacteristic = null;
    private final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();
    private final GBDeviceEventBatteryInfo batteryCmd = new GBDeviceEventBatteryInfo();

    public HereSupport() {
        super(LOG);
        addSupportedService(HereConstants.UUID_BATTERY_VALUE);
        addSupportedService(HereConstants.UUID_CHARACTERISTIC_INFO);
        addSupportedService(HereConstants.UUID_AUDIO_SETTINGS);
    }

    @Override
    protected TransactionBuilder initializeDevice(TransactionBuilder builder) {
        LOG.info("Initializing");

        gbDevice.setState(GBDevice.State.INITIALIZING);
        gbDevice.sendDeviceUpdateIntent(getContext());

        batteryCharacteristic = getCharacteristic(HereConstants.UUID_CHARACTERISTIC_BATTERY);
        infoCharacteristic = getCharacteristic(HereConstants.UUID_CHARACTERISTIC_INFO);
        fwCharacteristic = getCharacteristic(HereConstants.UUID_CHARACTERISTIC_INFO);
        volumeCharacteristic = getCharacteristic(HereConstants.UUID_VOLUME);
        effectCharacteristic = getCharacteristic(HereConstants.UUID_EFFECTS);

        builder.setGattCallback(this);
        builder.notify(batteryCharacteristic, true);
        builder.read(fwCharacteristic);
        syncSettings(builder);

        // fw = builder.read(infoCharacteristic);

        gbDevice.setState(GBDevice.State.INITIALIZED);
        gbDevice.sendDeviceUpdateIntent(getContext());
	requestCurrentVolume();

        LOG.info("Initialization Done");

        return builder;
    }

    @Override
    public boolean onCharacteristicChanged(BluetoothGatt gatt,
                                           BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);

        UUID characteristicUUID = characteristic.getUuid();

        byte[] data = characteristic.getValue();
        if (data.length == 0)
            return true;

        if (HereConstants.UUID_CHARACTERISTIC_BATTERY.equals(characteristicUUID)) {
            LOG.info("Battery is: " + String.format("%s", data[0]));
            batteryCmd.level = ((short) data[0]);
            handleGBDeviceEvent(batteryCmd);
            return true;
        } else if (HereConstants.UUID_FW_VERSION.equals(characteristicUUID)) {
            LOG.info("Device info: " + (short) data[0]);
            LOG.info("Device info: " + data);

            versionCmd.fwVersion = String.format("%s", data);
            handleGBDeviceEvent(versionCmd);
            return true;
        } else {
            return true;
        }
    }

    private void syncDateAndTime(TransactionBuilder builder) {
    }

    private void syncSettings(TransactionBuilder builder) {
    }

    private void showNotification(byte icon, String title, String message) {
    }

    @Override
    public boolean useAutoConnect() {
        return true;
    }

    @Override
    public void onNotification(NotificationSpec notificationSpec) {
    }

    @Override
    public void onDeleteNotification(int id) {

    }

    @Override
    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
    }

    @Override
    public void onSetTime() {
    }

    @Override
    public void onSetCallState(CallSpec callSpec) {
    }

    @Override
    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {

    }

    @Override
    public void onSetAudioProperty(AudioEffect effect) {
        TransactionBuilder builder = createTransactionBuilder("SetAudio");
        BluetoothGattCharacteristic characteristic;
        if (effect.getType() == AudioEffectType.VOLUME) {
            characteristic = volumeCharacteristic;
        } else {
            characteristic = effectCharacteristic;
        }
        builder.write(characteristic, effect.toByteMessage());
        builder.queue(getQueue());
    }

    @Override
    public void onSetMusicState(MusicStateSpec stateSpec) {
    }

    @Override
    public void onSetMusicInfo(MusicSpec musicSpec) {

    }

    @Override
    public void onEnableRealtimeSteps(boolean enable) {
    }

    @Override
    public void onInstallApp(Uri uri) {

    }

    @Override
    public void onAppInfoReq() {

    }

    @Override
    public void onAppStart(UUID uuid, boolean start) {

    }

    @Override
    public void onAppDelete(UUID uuid) {

    }

    @Override
    public void onAppConfiguration(UUID appUuid, String config) {

    }

    @Override
    public void onAppReorder(UUID[] uuids) {

    }

    @Override
    public void onFetchActivityData() {

    }

    @Override
    public void onReboot() {
    }

    @Override
    public void onHeartRateTest() {
    }

    @Override
    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
    }

    @Override
    public void onFindDevice(boolean start) {
    }

    @Override
    public void onSetConstantVibration(int integer) {

    }

    @Override
    public void onScreenshotReq() {

    }

    @Override
    public void onEnableHeartRateSleepSupport(boolean enable) {

    }

    @Override
    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {

    }

    @Override
    public void onDeleteCalendarEvent(byte type, long id) {

    }

    @Override
    public void onSendConfiguration(String config) {

    }

    @Override
    public void onTestNewFunction() {

    }

    @Override
    public void onSendWeather(WeatherSpec weatherSpec) {

    }

    // TODO: USE THIS
    // private byte[] effectMessage(int id, int padding, float [] params) {};

    public int requestCurrentVolume() {
        TransactionBuilder builder = createTransactionBuilder("getAudio");
        builder.read(volumeCharacteristic);
        LOG.debug(builder.toString());
        return 1;
    }

    private byte[] createMessage(AudioEffectType effect) {
        byte [] message;
        AudioEffect eff = new AudioEffect(effect, true); // default params!
        message = eff.toByteMessage();
        return message;
    }
}
