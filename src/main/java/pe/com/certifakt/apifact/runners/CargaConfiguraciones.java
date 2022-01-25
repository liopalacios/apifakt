package pe.com.certifakt.apifact.runners;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class CargaConfiguraciones implements CommandLineRunner {


    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ParameterRepository parameterRepository;
    @Autowired
    private BranchOfficeRepository branchOfficeRepository;
    @Autowired
    private SerieRepository serieRepository;

    @Value("${apifact.isProduction}")
    private Boolean isProduction;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... strings) throws Exception {

        List<Authority> authorities = authorityRepository.findAll();
        if (authorities.isEmpty()) {
            authorities = authorityRepository.saveAll(Arrays.asList(Authority.builder().name(AuthorityName.ROLE_ADMIN).build(),
                    Authority.builder().name(AuthorityName.ROLE_USER).build()));
        }

        CompanyEntity companyEntity = companyRepository.findByRuc("20293093297");
        if (companyEntity == null) {
            companyEntity = companyRepository.save(CompanyEntity.builder()
                    .bucket("20293093297")
                    .direccion("Dean Valvidia 148, San Isidro")
                    .estado("A")
                    .fechaCreacion(Timestamp.valueOf(LocalDateTime.now()))
                    .nombreComercial("Certicom SAC")
                    .razonSocial("Certicom SAC")
                    .ruc("20293093297")
                    .pais("Peru")
                    .build());
        }

        Optional<User> userOptional = userRepository.findByUsername("admin");
        User user = null;
        if (!userOptional.isPresent()) {
            user = new User();
            user.setCompany(companyEntity);
            user.setAuthorities(authorities);
            user.setEnabled(true);
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("s0p0rt32018"));
            user.setEstado(true);
            user.setFullName("Franjo Kurtovic");

            user = userRepository.save(user);
        } else {
            user = userOptional.get();
            user.setEnabled(true);
            user.setEstado(true);
            user = userRepository.save(user);
        }
        if (!isProduction) {
            log.info("No estamos en produccion, CREANDO EMPRESA DEMO");
            //EMPRESA DEMO
            CompanyEntity companyDemo = companyRepository.findByRuc("20204040303");
            if (companyDemo == null) {
                companyDemo = companyRepository.save(CompanyEntity.builder()
                        .bucket("20204040303")
                        .direccion("Dean Valvidia 148, San Isidro")
                        .estado("A")
                        .fechaCreacion(Timestamp.valueOf(LocalDateTime.now()))
                        .nombreComercial("Mi Empresa SAC")
                        .razonSocial("Mi Empresa SAC")
                        .ruc("20204040303")
                        .telefono("015234747")
                        .email("contacto@miempresa.com")
                        .pais("Peru")
                        .build());
            }

            Optional<User> userDemoOptional = userRepository.findByUsername("demo@certifakt.com.pe");
            User userDemo = null;
            if (!userDemoOptional.isPresent()) {
                userDemo = new User();
                userDemo.setCompany(companyDemo);
                userDemo.setAuthorities(authorities);
                userDemo.setEnabled(true);
                userDemo.setUsername("demo@certifakt.com.pe");
                userDemo.setPassword(passwordEncoder.encode("demo"));
                userDemo.setEstado(true);
                userDemo.setFullName("Mi Usuario Demo");

                userDemo = userRepository.save(userDemo);
            } else {
                userDemo = userDemoOptional.get();
                userDemo.setEnabled(true);
                userDemo.setEstado(true);
                userDemo.setFullName("Mi Usuario Demo");
                userDemo = userRepository.save(userDemo);
            }

            List<BranchOfficeEntity> oficinasDemos = branchOfficeRepository.findAllByCompanyId(companyDemo.getId());
            BranchOfficeEntity oficinademo = null;
            if (oficinasDemos.isEmpty()) {
                oficinademo = new BranchOfficeEntity();
                oficinademo.setCompany(companyDemo);
                oficinademo.setNombreCorto("Local Principal");
                oficinademo.setDireccion("Av. Dean Valdivia 148");
                oficinademo.setEstado(true);

                oficinademo = branchOfficeRepository.save(oficinademo);

                List<SerieEntity> seriesDemo = serieRepository.findAllByOficinaId(oficinademo.getId());
                if (!seriesDemo.isEmpty()) {
                    serieRepository.deleteAll(seriesDemo);
                }
                seriesDemo = Arrays.asList(
                        SerieEntity.builder().serie("F001").tipoDocumento("01").build(),
                        SerieEntity.builder().serie("B001").tipoDocumento("03").build()
                );
                oficinademo.addSeries(seriesDemo);
                oficinademo = branchOfficeRepository.save(oficinademo);

            } else {
                oficinademo = oficinasDemos.get(0);
            }

            userDemo.setOficina(oficinademo);
            userRepository.save(userDemo);

        }


        ParameterEntity VALOR_IGV = parameterRepository.findByName("VALOR_IGV");
        if (VALOR_IGV == null)
            parameterRepository.save(ParameterEntity.builder().name("VALOR_IGV").status(true).value("0.18").build());

        ParameterEntity RANGO_DIAS_BAJA_DOCUMENTOS = parameterRepository.findByName("RANGO_DIAS_BAJA_DOCUMENTOS");
        if (RANGO_DIAS_BAJA_DOCUMENTOS == null)
            parameterRepository.save(ParameterEntity.builder().name("RANGO_DIAS_BAJA_DOCUMENTOS").status(true).value("7").build());

        ParameterEntity RANGO_DIAS_RESUMEN_BOLETAS = parameterRepository.findByName("RANGO_DIAS_RESUMEN_BOLETAS");
        if (RANGO_DIAS_RESUMEN_BOLETAS == null)
            parameterRepository.save(ParameterEntity.builder().name("RANGO_DIAS_RESUMEN_BOLETAS").status(true).value("7").build());

        ParameterEntity TOKEN_CONSULTA_RUC = parameterRepository.findByName("TOKEN_CONSULTA_RUC");
        if (TOKEN_CONSULTA_RUC == null)
            parameterRepository.save(ParameterEntity.builder().name("TOKEN_CONSULTA_RUC").status(true).value("a6b1c4bc-d047-4e5f-9b0e-4e5af02a151f-ece1efae-97e7-42c2-893d-9d683ffb37f5").build());


        ParameterEntity URL_CONSULTA_RUC = parameterRepository.findByName("URL_CONSULTA_RUC");
        if (URL_CONSULTA_RUC == null)
            parameterRepository.save(ParameterEntity.builder().name("URL_CONSULTA_RUC").status(true).value("https://ruc.com.pe/api/v1/ruc").build());


    }
}
