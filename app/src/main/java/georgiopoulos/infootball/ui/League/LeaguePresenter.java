/**
 *  Copyright 2017 georgiopoulos kyriakos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * I'm using try-with-resources, which closes the Realm instance at the end of the statement.
 * While this is normally only supported by Android 4.4 or later,
 * Retrolambda backport this to older versions.
 *
 * Nucleus It can automatically restart requests after a process restart and automatically
 * unsubscribe RxJava subscriptions during onDestroy.
 */
package georgiopoulos.infootball.ui.League;

import android.os.Bundle;
import javax.inject.Inject;

import georgiopoulos.infootball.data.local.LocalData;
import georgiopoulos.infootball.data.remote.api.ServerAPI;
import georgiopoulos.infootball.ui.Base.BasePresenter;
import icepick.State;
import rx.schedulers.Schedulers;
import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class LeaguePresenter extends BasePresenter<LeagueActivity>{

    private static final int REQUEST_TEAMS = 1;
    @Inject ServerAPI api;
    @Inject LocalData localData;
    @State String leagueId;

    public void request(String leagueId){
        this.leagueId = leagueId;

        restartableLatestCache(REQUEST_TEAMS,
                               ()->api.getLeagueTeams(leagueId).subscribeOn(Schedulers.io())
                                           .observeOn(Schedulers.computation())
                                           .map(teamsDetails -> localData.writeTeamDetailsToRealm(teamsDetails))
                                           .observeOn(mainThread()),
                               LeagueActivity::onTeams,
                               LeagueActivity::onNetworkError);

        start(REQUEST_TEAMS);
    }

}
