package easv.bll.countryLogic;

import easv.be.Country;
import easv.be.Region;
import easv.be.Team;
import easv.dal.countryDao.CountryDao;
import easv.dal.countryDao.ICountryDao;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.sql.SQLException;
import java.util.*;

public class CountryLogic implements ICountryLogic {

    private ICountryDao countryDao;


    public CountryLogic() throws RateException {
        this.countryDao = new CountryDao();
    }



    /**
     * Extracts the countries to be used for the map view component.
     *
     * @param operationalCountries A map of countries keyed by their IDs.
     * @return A map of countries keyed by their names for the map view.
     */
    public Map<String, Country> getCountriesForMap(Map<Integer, Country> operationalCountries) {
        Map<String, Country> countriesForMap = new HashMap<>();
        operationalCountries.values().forEach(e -> {
            countriesForMap.put(e.getCountryName(), e);
        });
        return countriesForMap;
    }

    @Override
    public Country addCountry(Country country, List<Team> existingTeams, List<Team> newTeams, List<Team> teamsToUpdate) throws RateException {
        int countryID = countryDao.addCountry(country, existingTeams, newTeams, teamsToUpdate);
        if (countryID > 0) {
            country.setId(countryID);

            // Using a Set to compile all teams without duplicates
            Set<Team> allTeams = new HashSet<>();
            allTeams.addAll(existingTeams);
            allTeams.addAll(newTeams);
            allTeams.addAll(teamsToUpdate);

            List<Team> compiledTeams = new ArrayList<>(allTeams);

            country.setTeams(compiledTeams);
        }
        return country;
    }

    @Override
    public Country updateCountry(Country country, List<Team> currentTeams, List<Team> newTeams, List<Team> teamsToUpdate) throws RateException {
        List<Team> teamsBeforeUpdate = country.getTeams();

        // Uses a Set to track existing and current teams for comparison
        Set<Team> existingSet = new HashSet<>(teamsBeforeUpdate);
        Set<Team> currentTeamsSet = new HashSet<>(currentTeams);

        // Determine added teams
        Set<Team> addedTeams = new HashSet<>(currentTeamsSet);
        addedTeams.removeAll(existingSet);

        // Determine removed teams
        Set<Team> removedTeams = new HashSet<>(existingSet);
        removedTeams.removeAll(currentTeamsSet);
        removedTeams.removeIf(team -> teamsToUpdate.stream().anyMatch(updatedTeam -> updatedTeam.getId() == team.getId()));

        // Updates the country with added and removed teams
        countryDao.updateCountry(country, addedTeams.stream().toList(), removedTeams.stream().toList(), newTeams, teamsToUpdate);
        country.setTeams(currentTeams);
        return country;
    }

    @Override
    public boolean deleteCountry(Country country) throws RateException {
        return countryDao.deleteCountry(country);
    }

    @Override
    public List<Team> checkNewTeams(List<Team> teamsToCheck, Map<Integer, Team> teams){
        List<Team> newTeams = new ArrayList<>();
        for (Team team : teamsToCheck) {
            if (team != null && !teams.containsKey(team.getId())) {
                newTeams.add(team);
            }
        }
        return newTeams;
    }

    @Override
    public List<Team> checkExistingTeams(List<Team> teamsToCheck, Map<Integer, Team> teams) {
        List<Team> existingTeams = new ArrayList<>();
        for (Team team : teamsToCheck) {
            if (team != null) {
                Team existingTeam = teams.get(team.getId());
                if (teams.containsKey(team.getId()) && existingTeam.getTeamName().equals(team.getTeamName())) {
                    existingTeams.add(team);
                }
            }
        }
        return existingTeams;
    }

    @Override
    public List<Team> checkTeamsToUpdate(List<Team> teamsToCheck, Map<Integer, Team> teams){
        List<Team> updatedTeams = new ArrayList<>();
        for (Team team : teamsToCheck) {
            if (team != null) {
                if (teams.containsKey(team.getId())) {
                    Team existingTeam = teams.get(team.getId());
                    if (!existingTeam.getTeamName().equals(team.getTeamName())) {
                        updatedTeams.add(team);
                    }
                }
            }
        }
        return updatedTeams;
    }

    @Override
    public boolean deleteTeam(Team team) throws RateException {
        return countryDao.deleteTeam(team);
    }

    @Override
    public List<Country> performSearchCountryFilter(String filter, Collection<Country> values) {
        String filterToLowerCase = filter.toLowerCase();
        return values.stream().filter((country) -> {
            String name = country.getCountryName().toLowerCase();
            return name.contains(filterToLowerCase);
        }).toList();
    }

}
