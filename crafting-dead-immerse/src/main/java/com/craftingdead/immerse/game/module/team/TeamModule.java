/*
 * Crafting Dead
 * Copyright (C) 2021  NexusNode LTD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.craftingdead.immerse.game.module.team;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.craftingdead.core.network.Synched;
import com.craftingdead.immerse.game.module.Module;
import com.craftingdead.immerse.game.module.ModuleType;
import com.craftingdead.immerse.game.module.ModuleTypes;
import net.minecraft.network.PacketBuffer;

public class TeamModule<T extends Enum<T> & Team> implements Module, Synched {

  private final Class<T> teamType;
  private final Map<T, TeamInstance<T>> teams;

  protected final Map<UUID, T> playerTeams = new HashMap<>();
  protected final Map<UUID, T> dirtyPlayerTeams = new HashMap<>();

  public TeamModule(Class<T> teamType) {
    this.teamType = teamType;
    this.teams = new EnumMap<>(teamType);
  }

  public void registerTeam(T team) {
    this.teams.put(team, new TeamInstance<>(team));
  }

  public TeamInstance<T> getSmallestTeam() {
    TeamInstance<T> smallestTeam = null;
    for (TeamInstance<T> team : this.teams.values()) {
      if (smallestTeam == null || team.getMembers().size() < smallestTeam.getMembers().size()) {
        smallestTeam = team;
      }
    }
    return smallestTeam;
  }

  public TeamInstance<T> getTeamInstance(T team) {
    return this.teams.get(team);
  }

  public Optional<T> getPlayerTeam(UUID playerId) {
    return Optional.ofNullable(this.playerTeams.get(playerId));
  }

  @Override
  public ModuleType getType() {
    return ModuleTypes.TEAM.get();
  }

  @Override
  public void encode(PacketBuffer out, boolean writeAll) {
    for (Map.Entry<T, TeamInstance<T>> entry : this.teams.entrySet()) {
      if (entry.getValue().requiresSync()) {
        out.writeVarInt(entry.getKey().ordinal());
        entry.getValue().encode(out, false);
      }
    }
    // End marker
    out.writeVarInt(-1);

    boolean writePlayerTeams = writeAll || !this.dirtyPlayerTeams.isEmpty();
    out.writeBoolean(writePlayerTeams);
    if (writePlayerTeams) {
      out.writeBoolean(writeAll); // Clear player teams flag
      Map<UUID, T> playerTeamsToSend = writeAll ? this.playerTeams : this.dirtyPlayerTeams;
      out.writeVarInt(playerTeamsToSend.size());
      for (Map.Entry<UUID, T> entry : playerTeamsToSend.entrySet()) {
        out.writeUUID(entry.getKey());
        if (entry.getValue() == null) {
          out.writeBoolean(true);
        } else {
          out.writeBoolean(false);
          out.writeEnum(entry.getValue());
        }
      }
    }

    if (!writeAll) {
      this.dirtyPlayerTeams.clear();
    }
  }

  private TeamInstance<T> getExpectedTeamInstance(T team) {
    TeamInstance<T> teamInstance = this.teams.get(team);
    if (teamInstance == null) {
      throw new IllegalStateException("Expected team '" + team.toString() + "' to be present");
    }
    return teamInstance;
  }

  @Override
  public void decode(PacketBuffer in) {
    int i;
    while ((i = in.readVarInt()) != -1) {
      T team = this.teamType.getEnumConstants()[i];
      this.getExpectedTeamInstance(team).decode(in);
    }

    if (in.readBoolean()) {
      if (in.readBoolean()) {
        this.playerTeams.clear();
      }

      int playerTeamsSize = in.readVarInt();
      for (int j = 0; j < playerTeamsSize; j++) {
        UUID playerId = in.readUUID();
        if (in.readBoolean()) {
          T team = this.playerTeams.remove(playerId);
          if (team != null) {
            TeamInstance<T> teamInstance = this.getTeamInstance(team);
            // Team may have been deleted so don't expect it to be present.
            if (teamInstance != null) {
              teamInstance.removeMember(playerId);
            }
          }
        } else {
          T team = in.readEnum(this.teamType);
          T oldTeam = this.playerTeams.put(playerId, team);
          if (oldTeam != null) {
            this.getExpectedTeamInstance(oldTeam).removeMember(playerId);
          }
          this.getExpectedTeamInstance(team).addMember(playerId);
        }
      }
    }
  }

  @Override
  public boolean requiresSync() {
    return this.teams.values().stream().anyMatch(TeamInstance::requiresSync)
        || !this.dirtyPlayerTeams.isEmpty();
  }
}
