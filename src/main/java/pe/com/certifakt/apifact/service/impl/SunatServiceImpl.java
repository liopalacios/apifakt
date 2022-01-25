package pe.com.certifakt.apifact.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import pe.com.certifakt.apifact.bean.RazonSocial;
import pe.com.certifakt.apifact.dto.DniApiDTO;
import pe.com.certifakt.apifact.model.ParameterEntity;
import pe.com.certifakt.apifact.model.ReniecEntity;
import pe.com.certifakt.apifact.repository.ParameterRepository;
import pe.com.certifakt.apifact.repository.ReniecRepository;
import pe.com.certifakt.apifact.service.SunatService;
import pe.com.certifakt.apifact.util.Parameters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SunatServiceImpl implements SunatService {

    @Autowired
    private ReniecRepository reniecRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    @Value("${dni.reniec.url}")
    private String urlConsultaDni;

    @Value("${dni.reniec.nubeurl}")
    private String urlConsultaNubeRuc;

    @Value("${dni.reniec.apisperu}")
    private String apisperu;

    @Value("${dni.reniec.apisperuc}")
    private String apisperuc;

    @Value("${dni.reniec.tokenapisperu}")
    private String tokenapisperu;

    @Value("${dni.reniec.tokenmigo}")
    private String tokenmigo;

    @Value("${dni.reniec.nubetoken}")
    private String tokenube;

    @Override
    public RazonSocial findRazonSocialByRUC(String ruc) {


        RestTemplate restTemplate = new RestTemplate();
        RestTemplate restTemplateNube = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> requestnube = new HashMap<>();
        requestnube.put("token",tokenube);
        requestnube.put("ruc",ruc);

        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity <String> entityhttp = new HttpEntity<String>(headers);
        ResponseEntity<RazonSocial> response = null;
        RazonSocial razonSocial = null;

        System.out.println("SALIDA NUBEFACT RUC");
        try {

                response = restTemplate.exchange("https://dniruc.apisperu.com/api/v1/ruc/" +
                        ruc + "?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImxleXRlcnBhbGFjaW9zQGdtYWlsLmNvbSJ9.ZS_uptFpbXOlkOuN3bZcuqAOYjc57E95nbJ-yXxhuxw", HttpMethod.GET, entityhttp,RazonSocial.class);
                System.out.println(response);
                response.getBody().setNombre_o_razon_social(response.getBody().getRazonSocial());
                RazonSocial resp = response.getBody();
                resp.setDireccion_completa(response.getBody().getDireccion());
                resp.setSuccess(true);
                razonSocial = resp;


        } catch (HttpStatusCodeException e) {
            return new Gson().fromJson(e.getResponseBodyAsString(), RazonSocial.class);
        }



        log.info("resp: "+ razonSocial);

        return razonSocial;
    }

    @Override
    public String findNombreByDNI(String dni) {
        Optional<ReniecEntity> reniecEntity = reniecRepository.findById(dni);
        ReniecEntity entity = null;
        if(reniecEntity.isPresent())
            entity=reniecEntity.get();

        if(entity!=null)
            return entity.getNombres();

        String nombres = "";
        try {


            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity <String> entityhttp = new HttpEntity<String>(headers);




            ResponseEntity<DniApiDTO> response = restTemplate.exchange("https://dniruc.apisperu.com/api/v1/dni/" +
                    dni + "?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImxleXRlcnBhbGFjaW9zQGdtYWlsLmNvbSJ9.ZS_uptFpbXOlkOuN3bZcuqAOYjc57E95nbJ-yXxhuxw", HttpMethod.GET, entityhttp,DniApiDTO.class);

            System.out.println("APIS PERU RESPUESTA");
            System.out.println(response.toString());

            if(response.getStatusCodeValue()!=200 || response.getBody().getApellidoPaterno()==null){



            }else {
                if(response.getBody().getApellidoPaterno()==null){
                    System.out.println("guardar desde optimizeperu");

                }else {
                    System.out.println("guardar desde apisperu");
                    System.out.println(response);
                    nombres = response.getBody().getApellidoPaterno()+" "+response.getBody().getApellidoMaterno() + " "+
                            response.getBody().getNombres();
                    reniecRepository.save(ReniecEntity.builder().dni(dni).nombres(nombres).build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("No se encontro el dni ingresado: " + dni);
        }

        return nombres;
    }

}
