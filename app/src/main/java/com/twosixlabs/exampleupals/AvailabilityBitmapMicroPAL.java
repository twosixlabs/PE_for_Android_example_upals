/*
 * Copyright © 2020 by Raytheon BBN Technologies Corp.
 *
 * This material is based upon work supported by DARPA and AFRL under Contract No. FA8750-16-C-0006.
 *
 * The Government has unlimited rights to use, modify, reproduce, release, perform, display, or
 * disclose computer software or computer software documentation marked with this legend.
 * Any reproduction of technical data, computer software, or portions thereof marked with
 * this legend must also reproduce this marking.
 *
 * DISCLAIMER LANGUAGE
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing permissions and limitations
 * under the License.
 *
 */

package com.twosixlabs.exampleupals;

import android.os.Bundle;
import android.privatedata.DataRequest;
import android.pal.item.ListItem;
import android.pal.item.calendar.CalendarEventItem;
import android.privatedata.MicroPALProviderService;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityBitmapMicroPAL extends MicroPALProviderService<ListItem<CalendarEventItem>> {
    private static final String TAG = AvailabilityBitmapMicroPAL.class.getName();
    private static final String DESCRIPTION = "Generates an availability bitmap showing busy/available times";

    public enum Resolution{
        DAY,
        HOUR,
        HALF_HOUR,
        QUARTER_HOUR,
        MINUTE,
    };

    public static final Resolution DEFAULT_RESOLUTION = Resolution.QUARTER_HOUR;
    public static final String PARAM_KEY_RESOLUTION = "resolution";
    public static final String RESULT_KEY_WINDOW_STEP_SIZE_MILLIS = "window_step_millis";
    public static final String RESULT_KEY_WINDOW_START_MILLIS = "window_start_millis";
    public static final String RESULT_KEY_WINDOW_END_MILLIS = "window_end_millis";
    public static final String RESULT_KEY_BITMAP = "bitmap";
    public static final String RESULT_KEY_BITMAP_STRING = "bitmap_string";
    public static final String RESULT_KEY_EVENTS_FOUND = "events_found";

    public AvailabilityBitmapMicroPAL() {
        super(DataRequest.DataType.CALENDAR);
    }

    @Override
    public Bundle onReceive(ListItem<CalendarEventItem> calendarData, Bundle params) {
        Bundle result = new Bundle();
        ArrayList<CalendarEventItem> events = calendarData.getStoredItems();

        if(!events.isEmpty()) {
            long windowSizeMillis = getWindowSizeMillis(params);
            result.putLong(RESULT_KEY_WINDOW_STEP_SIZE_MILLIS, windowSizeMillis);

            // Compute the start and end times for the bitmap
            long[] startEnd = getStartEnd(events);

            long firstStart = startEnd[0];
            long lastEnd = startEnd[1];

            long firstPastStep = firstStart % windowSizeMillis;
            long lastUntilStep = windowSizeMillis - (lastEnd % windowSizeMillis);

            long windowStart = firstStart - firstPastStep;
            long windowEnd = lastEnd + lastUntilStep;
            result.putLong(RESULT_KEY_WINDOW_START_MILLIS, windowStart);
            result.putLong(RESULT_KEY_WINDOW_END_MILLIS, windowEnd);

            // Fill out the bitmap
            int bins = (int) ((windowEnd - windowStart) / windowSizeMillis);
            byte[] bitmap = new byte[bins];
            for(int n = 0; n < bitmap.length; n++) {
                bitmap[n] = 0;
            }

            for(CalendarEventItem event : events) {
                long eventStart = event.getStartTime();
                long eventEnd = event.getEndTime();

                int firstIndex = (int) ((eventStart - windowStart) / windowSizeMillis);
                int lastIndex = (int) ((eventEnd - windowStart) / windowSizeMillis);

                for(int n = firstIndex; n <= lastIndex; n++) {
                    bitmap[n] = 1;
                }
            }

            result.putByteArray(RESULT_KEY_BITMAP, bitmap);
            result.putString(RESULT_KEY_BITMAP_STRING, bitmapToString(bitmap));
            result.putBoolean(RESULT_KEY_EVENTS_FOUND, true);

        } else {
            Log.w(TAG, "Received empty calendar");
            result.putLong(RESULT_KEY_WINDOW_STEP_SIZE_MILLIS, 1000l);
            result.putLong(RESULT_KEY_WINDOW_START_MILLIS, 0l);
            result.putLong(RESULT_KEY_WINDOW_END_MILLIS, 0l);

            byte[] bitmap = {0};
            result.putByteArray(RESULT_KEY_BITMAP, bitmap);
            result.putString(RESULT_KEY_BITMAP_STRING, bitmapToString(bitmap));
            result.putBoolean(RESULT_KEY_EVENTS_FOUND, false);
        }

        return result;
    }

    private long getWindowSizeMillis(Bundle params) {
        Resolution rez = DEFAULT_RESOLUTION;
        if(params != null) {
            String rezParam = params.getString(PARAM_KEY_RESOLUTION, "");
            try {
                rez = Resolution.valueOf(rezParam);
            } catch(IllegalArgumentException e) {
                Log.w(TAG, "Received invalid resolution value " + rezParam);
                Log.w(TAG, "Resolution value must be one of " + Resolution.values());
                Log.w(TAG, "Setting resolution to default value" + DEFAULT_RESOLUTION.name());
            }
        }

        long windowSizeMillis = 0l;
        switch(rez) {
            case DAY:
                windowSizeMillis = 1000l * 60 * 60 * 24;
                break;

            case HOUR:
                windowSizeMillis = 1000l * 60 * 60;
                break;

            case HALF_HOUR:
                windowSizeMillis = 1000l * 60 * 30;
                break;

            case QUARTER_HOUR:
                windowSizeMillis = 1000l * 60 * 15;
                break;

            case MINUTE:
                windowSizeMillis = 1000l * 60;
                break;
        }

        return windowSizeMillis;
    }

    private long[] getStartEnd(List<CalendarEventItem> events) {
        long[] startEnd = {0l, 0l};
        if(!events.isEmpty()) {
            long firstStart = Long.MAX_VALUE;
            long lastEnd = 0l;

            for(CalendarEventItem event : events) {
                if(event.getStartTime() < firstStart) {
                    firstStart = event.getStartTime();
                }

                if(event.getEndTime() > lastEnd) {
                    lastEnd = event.getEndTime();
                }
            }

            startEnd[0] = firstStart;
            startEnd[1] = lastEnd;
        }

        return  startEnd;
    }

    private String bitmapToString(byte[] bitmap) {
        StringBuffer buffer = new StringBuffer();
        buffer.append('{');

        for(byte value : bitmap) {
            if(value == 0) {
                buffer.append("0,");
            } else {
                buffer.append("1,");
            }
        }

        buffer.append('}');

        return buffer.toString();
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
