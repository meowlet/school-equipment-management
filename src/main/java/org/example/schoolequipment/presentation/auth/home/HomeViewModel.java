package org.example.schoolequipment.presentation.auth.home;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.schoolequipment.api.API;
import org.example.schoolequipment.model.Equipment;
import org.example.schoolequipment.util.HttpRequestHelper;
import java.lang.reflect.Type;
import java.util.List;

public class HomeViewModel {
    public HomeState state;
    public API api;
    public List<Equipment> equipments;

    public HomeViewModel() {
        state = new HomeState();
        api = new API();
    }

public void fetchEquipments() {
    HttpRequestHelper.HttpResponse response = api.fetchEquipments();
    if (response.getStatusCode() == 200) {
        Gson gson = new Gson();
        Type equipmentListType = new TypeToken<List<Equipment>>(){}.getType();
        List<Equipment> equipmentList = gson.fromJson(response.getBody(), equipmentListType);
        state.equipments.clear();
        state.equipments.addAll(equipmentList);
    } else {
        state.error.set(response.getBody());
    }
}
}
