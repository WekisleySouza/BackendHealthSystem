package com.project.healthsystem.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UtilsController {
    public static String asJson(Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
