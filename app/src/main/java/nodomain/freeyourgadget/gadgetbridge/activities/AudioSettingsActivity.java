/*  Copyright (C) 2016-2017 Andreas Shimokawa, Carsten Pfeiffer

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

package nodomain.freeyourgadget.gadgetbridge.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.devices.here.HereConstants;
import nodomain.freeyourgadget.gadgetbridge.entities.AudioEffect;
import nodomain.freeyourgadget.gadgetbridge.entities.AudioEffectType;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;

public class AudioSettingsActivity extends AbstractGBActivity {
    private static final Logger LOG = LoggerFactory.getLogger(AudioSettingsActivity.class);
    private SeekBar seekBar;
    private TextView volume_text;

    // private boolean[] enabledEffects;

    private int volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_settings);
        LOG.debug("Create Audio Settings interface");

        // FIXME: read enabled effects too
        // FIXME: and EQ values XD
        seekBar = (SeekBar) findViewById(R.id.volume_seekbar);
        volume_text = (TextView) findViewById(R.id.volume_seekbar_volume);

        int startingVolume = 30; // FIXME: how do I read it?
        seekBar.setProgress(startingVolume);
        setdB(startingVolume);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
                // HERE's volume range is from 0xdb (-30dB on their app) to 0xff (+6dB)
                // 0xff - 0xdb = 36 -> seekbar max value
                // volume = seekbar + Min_volume (= 0xdb = 219)
                volume = position + 219;
                // LOG.debug("Volume = " + (byte)volume + " = " + volume + "= " + position);
                AudioEffect eff = new AudioEffect(AudioEffectType.VOLUME, volume);
                GBApplication.deviceService().onSetAudioProperty(eff);
                // Show the volume on the UI in dB (range -30;6)
                setdB(position);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // FIXME: we need to read the current value and display this.
        // right now, I'm just showing 0 dB, Called after changeListener sets the value on
        // the device too.

        Map<Integer, AudioEffectType> checkboxesIds = new HashMap<Integer, AudioEffectType>();
        checkboxesIds.put(R.id.audio_effect_echo, AudioEffectType.ECHO);
        checkboxesIds.put(R.id.audio_effect_bassboost, AudioEffectType.BASSBOOST);
        checkboxesIds.put(R.id.audio_effect_fuzz, AudioEffectType.FUZZ);
        checkboxesIds.put(R.id.audio_effect_flange, AudioEffectType.FLANGE);
        checkboxesIds.put(R.id.audio_effect_reverb, AudioEffectType.REVERB);
        checkboxesIds.put(R.id.audio_effect_noisemask, AudioEffectType.NOISEMASK);
        checkboxesIds.put(R.id.audio_effect_bitcrusher, AudioEffectType.BITCRUSHER);
        checkboxesIds.put(R.id.audio_effect_chorus, AudioEffectType.CHORUS);

        for (int id : checkboxesIds.keySet()) {
            final AudioEffectType effect = checkboxesIds.get(id);

            ((CheckedTextView) findViewById(id)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ((CheckedTextView) v).toggle();
                    LOG.info("Toggled " + effect.name());
                    applyEffect(new AudioEffect(effect, ((CheckedTextView) v).isChecked()));
                }
            });
        }
    }

    private void setdB(int volume) {
        volume_text.setText(" " + (volume - 30) + " dB");
    }

    void applyEffect(AudioEffect effect) {
        GBApplication.deviceService().onSetAudioProperty(effect);
    }
}
