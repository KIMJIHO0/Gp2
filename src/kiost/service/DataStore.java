package kiost.service;

import kiost.model.TeamSide;

public class DataStore {
   private TeamSide team;

    public TeamSide getTeam() {
        return team;
    }

    public void setTeam(TeamSide team) {
        this.team = team;
    }
}
