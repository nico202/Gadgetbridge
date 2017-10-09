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

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.entities.AudioEffectType;

public class AudioSettingsActivity extends AbstractGBActivity {
    private static final Logger LOG = LoggerFactory.getLogger(AudioSettingsActivity.class);
    private SeekBar seekBar;
    private TextView volume_text;
    private CheckedTextView cbBassBoost;
    private CheckedTextView cbNoiseMask;

    // private boolean[] enabledEffects;

    private int volume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_settings);
    	LOG.debug("Create Audio Setttings interface");

        // All of this is because HERE can't handle more than 2 effects.
        // You _can_ enable them, but the device will not be able to manage them in realtime
        // and you'll get "Xruns". We prevent more than 2 simultaneous checks (like their app does)
        // But it's commented out until I'll read the one already enabled. Else this is useless :D
        /*enabledEffects = new boolean[] {
                false   // echo
                , false // reverb
                , false // noise mask
                , false // fuzz
                , false // flange
                , false // bass boost
        };*/

        seekBar = (SeekBar) findViewById(R.id.volume_seekbar);
        volume_text = (TextView) findViewById(R.id.volume_seekbar_volume);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
                // HERE's volume range is from 0xe3 (-22dB on their app) to 0xff (+6dB)
                // 0xff - 0xe3 = 28 -> seekbar max value
                // volume = seekbar + Min_volume (= 0xe3 = 227)
                volume = position + 227;
                // LOG.debug("Volume = " + (byte)volume + " = " + volume + "= " + position);
                GBApplication.deviceService().onSetAudioProperty(AudioEffectType.VOLUME.getKey(),
								 new float[] {volume});
                // Show the volume on the UI in dB (range -22;6)
                volume_text.setText(" " + (position - 22) + " dB");
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
        seekBar.setProgress(22);

        cbBassBoost = (CheckedTextView) findViewById(R.id.audio_effect_bassboost);

        cbBassBoost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
                LOG.info("Enabled bassBoost");
                applyEffect(AudioEffectType.BASSBOOST, ((CheckedTextView) v).isChecked());
            }
        });

        cbNoiseMask = (CheckedTextView) findViewById(R.id.audio_effect_noisemask);

        cbNoiseMask.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
                LOG.info("Enabled noisemask");
                applyEffect(AudioEffectType.NOISEMASK, ((CheckedTextView) v).isChecked());
            }
        });

        cbNoiseMask = (CheckedTextView) findViewById(R.id.audio_effect_echo);

        cbNoiseMask.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckedTextView) v).toggle();
                LOG.info("Enabled echo");
                applyEffect(AudioEffectType.ECHO, ((CheckedTextView) v).isChecked());
            }
        });
    }

    void applyEffect(AudioEffectType effect, boolean enable) {
        GBApplication.deviceService().onSetAudioProperty(
                effect.getId(),
                new float[] {
                        enable ? 1.0f : 0.0f,
                        // FIXME: add other params?
                });

    }
}
