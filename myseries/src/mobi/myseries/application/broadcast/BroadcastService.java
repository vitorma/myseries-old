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

import java.util.Collection;

import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.content.Intent;

public class BroadcastService implements SeriesFollowingListener {
    private Context context;

    // FIXME(Gabriel): Check whether this constructor is really necessary. It should be deprecated.
    public BroadcastService(Context context) {
        Validate.isNonNull(context, "context");

        this.context = context;
    }

    public BroadcastService(Context context, FollowSeriesService followSeriesService) {
        Validate.isNonNull(context, "context");
        Validate.isNonNull(followSeriesService, "followSeriesService");

        this.context = context;

        followSeriesService.register(this);
    }

    public void broadcastSeenMarkup() {
        this.context.sendBroadcast(new Intent(BroadcastAction.SEEN_MARKUP));
    }

    public void broadcastUpdate() {
        this.context.sendBroadcast(new Intent(BroadcastAction.UPDATE));
    }

    private void broadcastAddiction() {
        this.context.sendBroadcast(new Intent(BroadcastAction.ADDICTION));
    }

    private void broadcastRemoval() {
        this.context.sendBroadcast(new Intent(BroadcastAction.REMOVAL));
    }

    public void broadcastConfigurationChange() {
        this.context.sendBroadcast(new Intent(BroadcastAction.CONFIGURATION_CHANGE));
    }

    // SeriesFollowingListener

    @Override
    public void onFollowing(Series followedSeries) {
        this.broadcastAddiction();
    }

    @Override
    public void onStopFollowing(Series unfollowedSeries) {
        this.broadcastRemoval();
    }

    @Override
    public void onStopFollowingAll(Collection<Series> allUnfollowedSeries) {
        this.broadcastRemoval();
    }

    @Override
    public void onFollowingStart(Series seriesToFollow) {}

    @Override
    public void onFollowingFailure(Series series, Exception e) {}
}
