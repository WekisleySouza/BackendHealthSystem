package com.project.healthsystem.controller.common;

import com.project.healthsystem.exceptions.AccessDeniedException;

public class ControllerAuxFunctions {

    public static String getTokenFrom(String authHeader){
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }
        throw new AccessDeniedException("Acesso negado!");
    }
}
