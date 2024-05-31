package easv.dal.countryDao;

import easv.be.Country;
import easv.be.Team;
import easv.exception.RateException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ICountryDao {


    Integer addCountry(Country country, List<Team> teams, List<Team> newTeams, List<Team> teamsToUpdate) throws RateException;

    void addNewTeamsToCountry(List<Integer> newTeamsIds, Integer countryID, Connection conn) throws RateException;

    void addTeamToCountry(Integer countryID, List<Team> teams, Connection conn) throws SQLException, RateException;

    List<Integer> addTeams(List<Team> teams, Connection conn) throws RateException;

    void updateCountry(Country country, List<Team> teamsToAdd, List<Team> teamsToRemove, List<Team> newTeams, List<Team> teamsToUpdate) throws RateException;

    boolean deleteCountry(Country country) throws RateException;

    boolean deleteTeam(Team team) throws RateException;
}
