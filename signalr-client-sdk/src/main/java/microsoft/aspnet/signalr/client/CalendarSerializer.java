/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package microsoft.aspnet.signalr.client;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class CalendarSerializer {

    public Calendar deserialize(String element) throws IOException {
        Date date = LoganSquare.parse(element, Date.class);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar;
    }

    public String serialize(Calendar calendar) throws IOException {
        return LoganSquare.serialize(DateSerializer.serialize(calendar.getTime()));
    }

}
