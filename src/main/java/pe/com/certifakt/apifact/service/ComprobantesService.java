package pe.com.certifakt.apifact.service;


import org.apache.poi.ss.usermodel.Workbook;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.dto.inter.DetailsPaymentInterDto;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterDto;
import pe.com.certifakt.apifact.model.ComprobantesEntity;
import pe.com.certifakt.apifact.model.GuiaRemisionEntity;
import pe.com.certifakt.apifact.model.OtherCpeEntity;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;
import pe.com.certifakt.apifact.security.UserPrincipal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ComprobantesService {

    Map<String, Object> getComprobantesByFilters(List<SearchCriteria> searchCriteriaList, Integer pageNumber, Integer perPage);

    Map<String, Object> getAllComprobantesByFilters(List<SearchCriteria> searchCriteriaList);

    PaymentVoucherEntity getComprobanteById(Long id);

    GuiaRemisionEntity getGuiaById(Long id);

    Integer getSiguienteNumeroComprobante(String tipoDocumento, String serie, String ruc);

    List<PaymentVoucherEntity> findComprobanteByNota(String numDocIdentReceptor, String serie, String rucEmisor);


    List<InfoEstadoSunat> getEstadoSunatByListaIds(List<Long> ids);

    List<InfoEstadoSunat> getEstadoSunatGuiaByListaIds(List<Long> ids);

    List<PaymentVoucherEntity> findComprobanteByAnticipo(String numDocIdentReceptor, String rucEmisor);

    List<GuiaRemisionEntity> searchGuiaRemision(Date filtroDesde, Date filtroHasta, String auth, String serie, Integer numero)
            throws Exception;

    byte[] exportExcelByFilters(UserPrincipal userResponse, String fechaEmisionDesde,
                                String fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor, String serie, Integer numero,
                                Integer estadoSunats);

    Workbook getAllComprobantesByFiltersQuery(UserPrincipal userResponse, String fechaEmisionDesde,
                                              String fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor, String serie, Integer numero) throws IOException;

    Map<String, Object> getComprobantesByFiltersQuery(UserResponse userResponse, String fechaEmisionDesde, String fechaEmisionHasta,
                                                      String tipoComprobante, String numDocIdentReceptor, String serie, Integer numero, Integer pageNumber,
                                                      Integer perPage);

    Map<String, Object> getComprobantesAllfByFiltersQuery(UserPrincipal user, String fechaEmisionDesde, String fechaEmisionHasta,
                                                          String tipoComprobante, String serie,boolean contador);

    void updateComprobantesByEstadoAnticipo(String identificadorDocumento);

    Map<String, Object> getComprobantesEstadoByFiltersQuery(UserPrincipal user, String filtroDesde, String filtroHasta,
                                                            String filtroTipoComprobante, String filtroRuc, String filtroSerie, Integer filtroNumero, Integer pageNumber,
                                                            Integer perPage, Integer estadoSunat);

    Map<String, Object> getGuiasEstadoByFiltersQuery(UserPrincipal user, String filtroDesde, String filtroHasta
                                                    , String filtroSerie, Integer filtroNumero, Integer pageNumber,
                                                            Integer perPage);

    Map<String, Object> getComprobantesDetallesByFiltersQuery(UserPrincipal user, Integer filtroIdPaymentVoucher);


    Map<String, Object> getPaymentsByDay(String ruc, int diff);

    Map<String, Object> getPaymentsByType(String ruc);

    Map<String, Object> getPaymentsByTypeMonth(String ruc);

    Map<String, Object> getPaymentsByTypeAndState(String ruc);

    Map<String, Object> getPaymentsByTypeAndStateMonth(String ruc);

    Map<String, Object> getPaymentsColumnLine(String ruc, int diff);

    Map<String, Object> getPaymentsColumnLineMonths(String ruc);

    Map<String, Object> getPaymentsByTypeAndDay(String ruc);

    Map<String, Object> getPaymentsByUserAndDay(String ruc, Integer diff);

    Map<String, Object> getPaymentsByUserAndMonth(String ruc);

    Map<String, Object> getPaymentsByMonth(String ruc);

    Map<String, Object> getCantidadComprobantesByCompany(UserPrincipal userPrincipal);

    OtherCpeEntity getOtherVoucherById(Long id);

    List<PaymentVoucherEntity> findComprobanteByCredito(String numDocIdentReceptor, String rucEmisor);

    PaymentVoucherInterDto getComprobantesEstadoByIdentificador(UserPrincipal userPrincipal, String indentificador);

    List<DetailsPaymentInterDto> getComprobanteDetailById(Integer idPayment);
}
