package nlu.dacn.dacn_backend.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import nlu.dacn.dacn_backend.dto.request.LaptopDTO;
import nlu.dacn.dacn_backend.exception.ServiceException;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static List<String> jsonToListString(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        try {
            List<String> result = gson.fromJson(jsonString, type);
            return result != null ? result: new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static LaptopDTO jsonToLaptopDTO(String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        try {
            LaptopDTO laptopDTO = gson.fromJson(jsonObject, LaptopDTO.class);
            return laptopDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể chuyển đổi chuỗi json");
        }
    }
}