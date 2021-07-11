package com.masai.flairbnbapp.localdatabases;

import com.google.android.gms.maps.model.LatLng;
import com.masai.flairbnbapp.R;
import com.masai.flairbnbapp.models.CityModel;
import com.masai.flairbnbapp.models.LiveAnywhereModel;
import com.masai.flairbnbapp.models.ServiceListModel;
import com.masai.flairbnbapp.models.SubCategoryModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LocalKeys {

    public static final String KEY_IS_USER_LOGGED_IN = "DOES_USER_LOGGED_IN_1";
    public static final String KEY_USER_GOOGLE_ID = "GOOGLE_USER_ID_1";
    public static final String KEY_USER_LOGIN_TYPE = "KEYS_LOGIN_TYPE_USER";
    public static final String KEY_USER_CITY_LAT = "KEYS_CITY_OF_USER_LAT";
    public static final String KEY_USER_CITY_LONG = "KEYS_CITY_OF_USER_LONG";
    public static final String CHECK_IN_TIME = "KEYS_CHECK_IN_WITH_MILI";
    public static final String CHECK_OUT_TIME = "KEYS_CHECK_OUT_WITH_MILI";
    public static final String NUMBER_OF_DAYS = "NUMBER_OF_DAYS";

    public static LatLng my_location = null;


    public static ArrayList<SubCategoryModel> getHouseSubCategoryList() {
        ArrayList<SubCategoryModel> list = new ArrayList<SubCategoryModel>();
        list.add(new SubCategoryModel(
                "Residential home",
                "A home that may stand alone or have shared walls.",
                false));
        list.add(new SubCategoryModel(
                "Cabin",
                "A house made with natural materials like wood and built in a natural setting.",
                false));
        list.add(new SubCategoryModel(
                "Villa",
                "A luxury home that may have indoor-outdoor spaces, gardens, and pools.",
                false));
        list.add(new SubCategoryModel(
                "Townhouse",
                "A terraced house that may have shared walls and outdoor spaces.",
                false));
        list.add(new SubCategoryModel(
                "Cottage",
                "A cosy house built in a rural area or near a lake or beach.",
                false));
        list.add(new SubCategoryModel(
                "Bungalow",
                "A single-level house with a wide front porch and a sloping roof.",
                false));
        list.add(new SubCategoryModel(
                "Earth house",
                "A home built in the ground or made from materials like rammed earth.",
                false));
        list.add(new SubCategoryModel(
                "Houseboat",
                "A home that floats, which can be a boat used as a residence or a house.",
                false));
        list.add(new SubCategoryModel(
                "Hut",
                "A home made of wood or mud that may have a thatched straw roof.",
                false));
        list.add(new SubCategoryModel(
                "Farm stay",
                "A rural stay where guests may spend time with animals, hiking, or crafting.",
                false));
        list.add(new SubCategoryModel(
                "Dome house",
                "A home with a domed roof or spherical shape, such as a bubble home.",
                false));
        list.add(new SubCategoryModel(
                "Cycladic house",
                "A whitewashed house with a flat roof found in the Greek Cycladic islands.",
                false));
        list.add(new SubCategoryModel(
                "Chalet",
                "A wooden house with a sloped roof, popular for skiing or summer stays.",
                false));
        list.add(new SubCategoryModel(
                "Dammuso",
                "A stone house with a domed roof on the island of Pantelleria.",
                false));
        list.add(new SubCategoryModel(
                "Lighthouse",
                "A tower near water with a bright light that helps to guide ships.",
                false));
        list.add(new SubCategoryModel(
                "Shepherd’s hut",
                "A stand-alone house that's usually less than 400 square feet (37 square metres).",
                false));
        list.add(new SubCategoryModel(
                "Trullo",
                "A round, stone house with a cone-shaped roof, originating in Italy.",
                false));
        list.add(new SubCategoryModel(
                "Casa particular",
                "A private room in a home that feels like a bed and breakfast in Cuba.",
                false));
        list.add(new SubCategoryModel(
                "Pension",
                "A house with a barbecue and communal space in the countryside of Korea.",
                false));

        return list;
    }

    public static ArrayList<SubCategoryModel> getFlatSubCategoryList() {
        ArrayList<SubCategoryModel> list = new ArrayList<SubCategoryModel>();
        list.add(new SubCategoryModel(
                "Rental unit",
                "A rented place within a multi-unit residential building or complex.",
                false));
        list.add(new SubCategoryModel(
                "Apartment",
                "A place within a multi-unit building or complex owned by the residents.",
                false));
        list.add(new SubCategoryModel(
                "Loft",
                "An open-plan flat or apartment, which may have short interior walls.",
                false));
        list.add(new SubCategoryModel(
                "Serviced apartment",
                "An apartment with hotel-like amenities serviced by a professional management company.",
                false));
        list.add(new SubCategoryModel(
                "Casa particular",
                "A private room in a home that feels like a bed and breakfast in Cuba.",
                false));

        return list;
    }

    public static ArrayList<SubCategoryModel> getSecondarySubCategoryList() {
        ArrayList<SubCategoryModel> list = new ArrayList<SubCategoryModel>();
        list.add(new SubCategoryModel(
                "Guest house",
                "A carriage house or coach house that shares land with a main building.",
                false));
        list.add(new SubCategoryModel(
                "Guest suite",
                "A space with a private entrance inside of or attached to a larger structure.",
                false));
        list.add(new SubCategoryModel(
                "Farm stay",
                "A rural stay where guests may spend time with animals, hiking, or crafting.",
                false));
        return list;
    }

    public static ArrayList<SubCategoryModel> getUniqueSubCategoryList() {
        ArrayList<SubCategoryModel> list = new ArrayList<SubCategoryModel>();
        list.add(new SubCategoryModel(
                "Barn",
                "A converted space in a building used for grain, livestock, or farming.",
                false));
        list.add(new SubCategoryModel(
                "Boat",
                "A boat, sailing boat, or yacht moored during guest visits. Not a houseboat.",
                false));
        list.add(new SubCategoryModel(
                "Bus",
                "A converted multi-passenger vehicle with a creatively reimagined interior.",
                false));
        list.add(new SubCategoryModel(
                "Treehouse\n",
                "A place to stay built into the trunk or branches of a tree.",

                false));
        list.add(new SubCategoryModel(
                "Campsite\n",
                "An area of land where guests can set up a tent, yurt, motorhome, or tiny house.",

                false));
        list.add(new SubCategoryModel(
                "Castle\n",
                "A majestic, possibly historical building that may have towers and moats.",
                false));
        list.add(new SubCategoryModel(
                "Cave\n",
                "A natural underground formation in a hillside or cliff.",
                false));

        list.add(new SubCategoryModel(
                "Hut",
                "A home made of wood or mud that may have a thatched straw roof.",
                false));

        list.add(new SubCategoryModel(
                "Pension",
                "A house with a barbecue and communal space in the countryside of Korea.",
                false));
        return list;
    }

    public static ArrayList<ServiceListModel> getServiceList() {
        ArrayList<ServiceListModel> list = new ArrayList<>();
        list.add(new ServiceListModel(
                "Pool", R.drawable.ic_pool, false
        ));
        list.add(new ServiceListModel(
                "Hot tub", R.drawable.ic_hottub, false
        ));
        list.add(new ServiceListModel(
                "Patio", R.drawable.ic_patio, false
        ));
        list.add(new ServiceListModel(
                "BBQ grill", R.drawable.ic_bbq_grill, false
        ));
        list.add(new ServiceListModel(
                "Fire pit", R.drawable.ic_firepit, false
        ));
        list.add(new ServiceListModel(
                "Dining", R.drawable.ic_dining, false
        ));
        list.add(new ServiceListModel(
                "Exercise", R.drawable.ic_exercise, false
        ));
        list.add(new ServiceListModel(
                "Wifi", R.drawable.ic_wifi, false
        ));
        list.add(new ServiceListModel(
                "TV", R.drawable.ic_tv, false
        ));
        list.add(new ServiceListModel(
                "Kitchen", R.drawable.ic_kitchen, false
        ));
        list.add(new ServiceListModel(
                "Washing machine", R.drawable.ic_washingmachine, false
        ));
        list.add(new ServiceListModel(
                "Parking", R.drawable.ic_parking, false
        ));
        list.add(new ServiceListModel(
                "Air conditions", R.drawable.ic_ac, false
        ));
        list.add(new ServiceListModel(
                "Shower", R.drawable.ic_shower, false
        ));
        list.add(new ServiceListModel(
                "First aid kit", R.drawable.ic_firstaidkit, false
        ));
        list.add(new ServiceListModel(
                "Smoke alarm", R.drawable.ic_smokealarm, false
        ));
        list.add(new ServiceListModel(
                "Fire extinguisher", R.drawable.ic_fireextinguisher, false
        ));
        list.add(new ServiceListModel(
                "Room lock", R.drawable.ic_roomlock, false
        ));
        return list;
    }


    public static int getServiceIconIdByName(@NotNull String s) {
        switch (s) {
            case "Pool":
                return R.drawable.ic_pool;
            case "Hot tub":
                return R.drawable.ic_hottub;
            case "Patio":
                return R.drawable.ic_patio;
            case "BBQ grill":
                return R.drawable.ic_bbq_grill;
            case "Fire pit":
                return R.drawable.ic_firepit;
            case "Dining":
                return R.drawable.ic_dining;
            case "Exercise":
                return R.drawable.ic_exercise;
            case "Wifi":
                return R.drawable.ic_wifi;
            case "TV":
                return R.drawable.ic_tv;
            case "Kitchen":
                return R.drawable.ic_kitchen;
            case "Washing machine":
                return R.drawable.ic_washingmachine;
            case "Parking":
                return R.drawable.ic_parking;
            case "Air conditions":
                return R.drawable.ic_ac;
            case "Shower":
                return R.drawable.ic_shower;
            case "First aid kit":
                return R.drawable.ic_firstaidkit;
            case "Smoke alarm":
                return R.drawable.ic_smokealarm;
            case "Fire extinguisher":
                return R.drawable.ic_fireextinguisher;
            case "Room lock":
                return R.drawable.ic_roomlock;
            default:
                return R.drawable.ic_baseline_settings_24;
        }
    }


    public static ArrayList<CityModel> getAllCityModels() {
        ArrayList<CityModel> list = new ArrayList<>();
        list.add(new CityModel("Bengaluru", R.drawable.bengaluru));
        list.add(new CityModel("Kochi", R.drawable.kochi));
        list.add(new CityModel("Puducherry", R.drawable.puducherry));
        list.add(new CityModel("Canacona", R.drawable.canacona));
        list.add(new CityModel("Benaulim", R.drawable.benaulim));
        list.add(new CityModel("Majorda", R.drawable.majorda));
        return list;
    }

    public static ArrayList<LiveAnywhereModel> getAllLiveAnywhereModels() {
        ArrayList<LiveAnywhereModel> list = new ArrayList<>();
        list.add(new LiveAnywhereModel("Outdoor gateways", R.drawable.outdoor_gateways));
        list.add(new LiveAnywhereModel("Unique stays", R.drawable.unique_stays));
        list.add(new LiveAnywhereModel("Entire homes", R.drawable.entire_house));
        list.add(new LiveAnywhereModel("Pets allowed", R.drawable.pets_allowed));
        return list;
    }
}
