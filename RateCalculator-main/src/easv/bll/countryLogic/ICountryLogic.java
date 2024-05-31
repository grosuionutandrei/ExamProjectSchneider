package easv.bll.countryLogic;

import easv.be.Country;
import easv.be.Region;
import easv.be.Team;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface ICountryLogic {



    /**
     * extract the countries to be used for the  map view component
     */
    Map<String, Country> getCountriesForMap(Map<Integer, Country> operationalCountries);

    Country addCountry(Country country, List<Team> teams, List<Team> newTeams, List<Team> teamsToUpdate) throws RateException;

    Country updateCountry(Country country, List<Team> teams, List<Team> newTeams, List<Team> teamsToUpdate) throws RateException;

    boolean deleteCountry(Country country) throws RateException;

    List<Team> checkNewTeams(List<Team> teamsToCheck, Map<Integer, Team> teams);

    List<Team> checkExistingTeams(List<Team> teamsToCheck, Map<Integer, Team> teams);

    List<Team> checkTeamsToUpdate(List<Team> teamsToCheck, Map<Integer, Team> teams);

    boolean deleteTeam(Team team) throws RateException;

    List<Country> performSearchCountryFilter(String filter, Collection<Country> values);
}
