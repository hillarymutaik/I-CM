package com.musicind.dukamoja.errors;

import java.util.List;
import java.util.Map;

public class ApiError {
    String message;
    Map<String, List<String>> errors;

    public String getMessage() {
        return message;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
