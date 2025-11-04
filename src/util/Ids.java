package util;

import java.util.UUID;

/** ID 생성 유틸. */
public final class Ids {
    public static String newId(){
        return UUID.randomUUID().toString(); }
}
