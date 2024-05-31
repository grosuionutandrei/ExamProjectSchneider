package easv.bll.RegionLogic;

import easv.be.Country;
import easv.be.Region;
import easv.be.Team;
import easv.dal.regionDao.IRegionDAO;
import easv.dal.regionDao.RegionDAO;
import easv.exception.RateException;

import java.util.*;

public class RegionManager implements IRegionManager {

    private IRegionDAO regionDAO;

    public RegionManager() throws RateException {
        this.regionDAO = new RegionDAO();
    }

    /**
     * Adds a new region and associates it with a list of countries.
     *
     * @param region The region to add.
     * @param countries The list of countries to associate with the region.
     * @return The added region with its ID and countries set.
     */
    @Override
    public Region addRegion(Region region, List<Country> countries) throws RateException {
        int regionID = regionDAO.addRegion(region, countries);
        if (regionID > 0) {
            region.setId(regionID);
            region.setCountries(countries);
        }
        return region;
    }

    /**
     * Updates an existing region and its associated countries.
     *
     * @param region The region to update.
     * @param countries The updated list of countries to associate with the region.
     * @return The updated region with its countries set.
     */
    @Override
    public Region updateRegion(Region region, List<Country> countries) throws RateException {
        List<Country> existingCountries = region.getCountries();

        Set<Country> existingSet = new HashSet<>(existingCountries);
        Set<Country> newSet = new HashSet<>(countries);
        Set<Country> addedCountries = new HashSet<>(newSet);
        addedCountries.removeAll(existingSet);
        Set<Country> removedCountries = new HashSet<>(existingSet);
        removedCountries.removeAll(newSet);

        regionDAO.updateRegion(region, addedCountries.stream().toList(), removedCountries.stream().toList());
        region.setCountries(countries);
        return region;
    }

    /**
     * Deletes a region.
     *
     * @param region The region to delete.
     * @return true if the region was successfully deleted, false otherwise.
     */
    @Override
    public boolean deleteRegion(Region region) throws RateException {
        return regionDAO.deleteRegion(region);
    }

    /**
     * Filters regions based on a search filter.
     *
     * @param filter The search filter to apply.
     * @param allRegions The collection of all regions to filter.
     * @return A list of regions that match the filter criteria.
     */
    @Override
    public List<Region> performSearchRegionFilter(String filter, Collection<Region> allRegions) {
        String filterToLowerCase = filter.toLowerCase();
        return allRegions.stream().filter((region) -> {
            String name = region.getRegionName().toLowerCase();
            return name.contains(filterToLowerCase);
        }).toList();
    }


    /**
     * get region teams
     */
    @Override
    public List<Team> filterTeamsByRegion(Region region) {
        return region.getCountries().stream()
                .flatMap(country -> country.getTeams().stream())
                .toList();
    }
}
