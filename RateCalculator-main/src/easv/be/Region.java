package easv.be;

import java.util.List;

public class Region {
    private String regionName;
    private int id;
    private List<Country> countries;


    @Override
    public String toString() {
        return  regionName;
    }

    public Region(String regionName) {
        this.regionName = regionName;
    }

    public Region(String regionName, int id) {
        this.regionName = regionName;
        this.id = id;
    }

    public Region(String regionName, int id,List<Country> countries) {
       this(regionName,id);
       this.countries=countries;
    }


    public void addCountryToRegion(Country country){
        this.countries.add(country);
    }

    public void removeCountriesFromRegion(Country country){
        this.countries.remove(country);
    }


    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }
}
