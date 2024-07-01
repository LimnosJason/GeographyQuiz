package com.example.geographyquiz;

import java.util.ArrayList;
import java.util.Iterator;

public class CountryDataProvider {
    //Get a list with all country names and flags
    //Used in quiz activity
    public static ArrayList<Country> getCountriesFullList() {
        ArrayList<Country> countryList= new ArrayList();
        countryList.add(new Country("Canada", R.drawable.canada));
        countryList.add(new Country("China", R.drawable.china));
        countryList.add(new Country("Greece", R.drawable.greece));
        countryList.add(new Country("Italy", R.drawable.italy));
        countryList.add(new Country("Madagascar", R.drawable.madagascar));
        countryList.add(new Country("Monaco", R.drawable.monaco));
        countryList.add(new Country("Peru", R.drawable.peru));
        countryList.add(new Country("Philippines", R.drawable.philippines));
        countryList.add(new Country("Poland", R.drawable.poland));
        countryList.add(new Country("Slovakia", R.drawable.slovakia));
        countryList.add(new Country("Slovenia", R.drawable.slovenia));
        countryList.add(new Country("Spain", R.drawable.spain));
        countryList.add(new Country("Sweden", R.drawable.sweden));
        countryList.add(new Country("The Netherlands", R.drawable.the_netherlands));
        countryList.add(new Country("Tonga", R.drawable.tonga));

        return countryList;
    }
    //Get a list with all country names and country outlines
    public static ArrayList<Country> getCountriesOutlineList() {
        ArrayList<Country> countryList= new ArrayList();
        countryList.add(new Country("Canada", R.drawable.canada_outline));
        countryList.add(new Country("China", R.drawable.china_outline));
        countryList.add(new Country("Greece", R.drawable.greece_outline));
        countryList.add(new Country("Italy", R.drawable.italy_outline));
        countryList.add(new Country("Madagascar", R.drawable.madagascar_outline));
        countryList.add(new Country("Monaco", R.drawable.monaco_outline));
        countryList.add(new Country("Peru", R.drawable.peru_outline));
        countryList.add(new Country("Philippines", R.drawable.philippines_outline));
        countryList.add(new Country("Poland", R.drawable.poland_outline));
        countryList.add(new Country("Slovakia", R.drawable.slovakia_outline));
        countryList.add(new Country("Slovenia", R.drawable.slovenia_outline));
        countryList.add(new Country("Spain", R.drawable.spain_outline));
        countryList.add(new Country("Sweden", R.drawable.sweden_outline));
        countryList.add(new Country("The Netherlands", R.drawable.the_netherlands_outline));
        countryList.add(new Country("Tonga", R.drawable.tonga_outline));

        return countryList;
    }
    //Get a list with all country names
    public static ArrayList<Country> getCountriesList() {
        ArrayList<Country> countryList= new ArrayList();
        countryList.add(new Country("Canada"));
        countryList.add(new Country("China"));
        countryList.add(new Country("Greece"));
        countryList.add(new Country("Italy"));
        countryList.add(new Country("Madagascar"));
        countryList.add(new Country("Monaco"));
        countryList.add(new Country("Peru"));
        countryList.add(new Country("Philippines"));
        countryList.add(new Country("Poland"));
        countryList.add(new Country("Slovakia"));
        countryList.add(new Country("Slovenia"));
        countryList.add(new Country("Spain"));
        countryList.add(new Country("Sweden"));
        countryList.add(new Country("The Netherlands"));
        countryList.add(new Country("Tonga"));
        return countryList;
    }
    //Get a list with the countries that match the search of the player
    public static ArrayList<Country> getSearchedCountries(ArrayList<Country> countriesList,String searchValue){
        Iterator<Country> it = countriesList.iterator();
        while (it.hasNext()) {
            if (!it.next().getName().toLowerCase().contains(searchValue.toLowerCase())) {
                it.remove();
            }
        }
        return countriesList;
    }
}
