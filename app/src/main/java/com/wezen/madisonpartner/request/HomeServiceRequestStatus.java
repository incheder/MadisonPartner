package com.wezen.madisonpartner.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eder on 4/13/15.
 */
public enum HomeServiceRequestStatus {
    ENVIADO(0),
    ASIGNADO(1),
    CANCELADO(2),
    COMPLETO(3);

    private final int value;
    private static Map<Integer,HomeServiceRequestStatus> map = new HashMap<>();
    static {
        for(HomeServiceRequestStatus bType : HomeServiceRequestStatus.values()){
            map.put(bType.value,bType);
        }
    }

    private HomeServiceRequestStatus(int value) {
        this.value = value;
    }

    public static HomeServiceRequestStatus valueOf(int value){
        return  map.get(value);
    }

    public int getValue(){
        return value;
    }




}
