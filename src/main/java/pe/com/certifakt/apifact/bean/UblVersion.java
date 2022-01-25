package pe.com.certifakt.apifact.bean;


import java.util.Arrays;
import java.util.List;

public interface UblVersion {

    //VERSIONRES
    String TWODOTZERO = "2.0";
    String TWODOTONE = "2.1";


    List<String> UBL_VERSIONS = Arrays.asList(TWODOTZERO,TWODOTONE);
}
