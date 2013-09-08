/*
 *   BroadcastService.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.application.broadcast;

import mobi.myseries.shared.Validate;
import android.content.Context;
import android.content.Intent;

@Deprecated
public class BroadcastService {
    private Context context;

    public BroadcastService(Context context) {
        Validate.isNonNull(context, "context");

        this.context = context;
    }

    public void broadcastSeenMarkup() {
        this.context.sendBroadcast(new Intent(BroadcastAction.SEEN_MARKUP));
    }

    public void broadcastUpdate() {
        this.context.sendBroadcast(new Intent(BroadcastAction.UPDATE));
    }

    public void broadcastAddition() {
        this.context.sendBroadcast(new Intent(BroadcastAction.ADDITION));
    }

    public void broadcastRemoval() {
        this.context.sendBroadcast(new Intent(BroadcastAction.REMOVAL));
    }
}
