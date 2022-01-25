package pe.com.certifakt.apifact.util;

import java.util.UUID;

public class UUIDGen {
	public static String generate(){
	    return UUID.randomUUID().toString().replace("-", "");
	}
}
