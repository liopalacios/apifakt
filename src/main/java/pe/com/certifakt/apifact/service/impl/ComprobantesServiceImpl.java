package pe.com.certifakt.apifact.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.bean.InfoEstadoSunat;
import pe.com.certifakt.apifact.bean.SearchCriteria;
import pe.com.certifakt.apifact.bean.UserResponse;
import pe.com.certifakt.apifact.dao.ComprobantesDAO;

import pe.com.certifakt.apifact.dto.DetailsPaymentVoucherDto;
import pe.com.certifakt.apifact.dto.GuiaRemisionDto;
import pe.com.certifakt.apifact.dto.PaymentVoucherDto;

import pe.com.certifakt.apifact.dto.inter.DetailsPaymentInterDto;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterDto;
import pe.com.certifakt.apifact.dto.inter.UserInterDto;
import pe.com.certifakt.apifact.enums.EstadoComprobanteEnum;
import pe.com.certifakt.apifact.enums.EstadoSunatEnum;
import pe.com.certifakt.apifact.enums.EstadoComprobanteEnum;
import pe.com.certifakt.apifact.enums.EstadoSunatEnum;
import pe.com.certifakt.apifact.dto.PaymentVoucherDto;
import pe.com.certifakt.apifact.mapper.ComprobantesMapper;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.GuiaRemisionRepository;
import pe.com.certifakt.apifact.repository.OtherCpeRepository;
import pe.com.certifakt.apifact.repository.PaymentVoucherRepository;
import pe.com.certifakt.apifact.repository.UserRepository;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.ClientService;
import pe.com.certifakt.apifact.service.ComprobantesService;
import pe.com.certifakt.apifact.util.UtilDate;
import pe.com.certifakt.apifact.util.UtilExcel;
import pe.com.certifakt.apifact.validate.PaymentVoucherParamsInputValidate;

import javax.transaction.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.io.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ComprobantesServiceImpl implements ComprobantesService {

    @Autowired
    private ComprobantesDAO comprobantesDAO;

    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;

    @Autowired
    private OtherCpeRepository otherCpeRepository;

    @Autowired
    private GuiaRemisionRepository guiaRemisionRepository;

    @Autowired
    private ComprobantesMapper comprobantesMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientService clientService;

    HashMap<String,String> fieldsPayment = new HashMap<String, String>();
    HashMap<String,String> fieldsStates = new HashMap<String, String>();
    HashMap<String,Integer> fieldsDateTypeFact = new HashMap<String, Integer>();
    HashMap<String,Integer> fieldsDateTypeBol = new HashMap<String, Integer>();
    HashMap<String,Integer> fieldsDate = new HashMap<String, Integer>();
    HashMap<Integer,String> fieldsMonths = new HashMap<Integer, String>();
    HashMap<Integer,Integer> fieldsIntMonths = new HashMap<Integer, Integer>();
    @Override
    public Map<String, Object> getComprobantesByFilters(List<SearchCriteria> searchCriteriaList, Integer pageNumber,
                                                        Integer perPage) {
        return comprobantesDAO.buscarComprobantes(searchCriteriaList, pageNumber, perPage);
    }

    @Override
    public Map<String, Object> getAllComprobantesByFilters(List<SearchCriteria> searchCriteriaList) {
        return comprobantesDAO.buscarAllComprobantes(searchCriteriaList);
    }

    @Override
    public Map<String, Object> getComprobantesByFiltersQuery(UserResponse userResponse, String fechaEmisionDesde,
                                                             String fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor, String serie, Integer numero,
                                                             Integer pageNumber, Integer perPage) {
        UserInterDto usuario = userRepository.findByUsernameInter(userResponse.getUserName()).get();
        Integer idOficina = null;
        if (usuario.getIdOficina() != null) {
            idOficina = usuario.getIdOficina();
        }
        List<PaymentVoucherEntity> result = paymentVoucherRepository.findAllSerchForPage(userResponse.getRuc(),
                fechaEmisionDesde, fechaEmisionHasta, "%" + tipoComprobante + "%", "%" + numDocIdentReceptor + "%",
                "%" + serie + "%", numero, idOficina, new PageRequest((pageNumber - 1), perPage));
        return ImmutableMap.of("comprobantesList", result, "total",
                paymentVoucherRepository.findAllSerchCount(userResponse.getRuc(), fechaEmisionDesde, fechaEmisionHasta,
                        "%" + tipoComprobante + "%", "%" + numDocIdentReceptor + "%", "%" + serie + "%", numero, idOficina));
    }
    @Override
    public List<DetailsPaymentInterDto> getComprobanteDetailById(Integer idPayment) {
        return paymentVoucherRepository.findDetailsById(idPayment);
    }
    @Override
    public PaymentVoucherInterDto getComprobantesEstadoByIdentificador(UserPrincipal userPrincipal, String indentificador) {
        return paymentVoucherRepository.findByIdentificadorDocumentoName(indentificador);
    }



    @Override
    public Map<String, Object> getComprobantesAllfByFiltersQuery(UserPrincipal userResponse, String fechaEmisionDesde, String fechaEmisionHasta, String tipoComprobante,
                                                                 String serie,boolean contador) {
        User usuario = userRepository.findByUsername(userResponse.getUsername()).get();
        List<PaymentVoucherDto> list = new ArrayList<>();
        Integer idOficina = null;
        if (usuario.getOficina() != null) {
            idOficina = usuario.getOficina().getId();
        }
        List<PaymentVoucherInterDto> result;
        if (contador){
            result = paymentVoucherRepository.findAllSerchReportContador(userResponse.getRuc(),
                    fechaEmisionDesde, fechaEmisionHasta, "%" + tipoComprobante + "%",
                    "%" + serie + "%");
            return ImmutableMap.of("comprobantesList", result, "total", result.size());
        }else{
            result = paymentVoucherRepository.findAllSerchReport(userResponse.getRuc(),
                    fechaEmisionDesde, fechaEmisionHasta, "%" + tipoComprobante + "%",
                    "%" + serie + "%");
            list = getListOfResult(result);
            return ImmutableMap.of("comprobantesList", list, "total", result.size());
        }



    }

    private List<PaymentVoucherDto> getListOfResult(List<PaymentVoucherInterDto> result) {
        Long indice = 0l;
        List<PaymentVoucherDto> list = new ArrayList<>();
        PaymentVoucherDto voucherDto = null;
        DetailsPaymentVoucherDto details = null;
        for (PaymentVoucherInterDto dto: result ) {
            if (Long.compare(indice,dto.getId())==0){
                details = new DetailsPaymentVoucherDto();
                details.setCantidad(dto.getCantidad());
                details.setCodigoProducto(dto.getCodigoProducto());
                details.setDescripcion(dto.getDescripcion());
                details.setValorUnitario(dto.getValorUnitario());
                details.setValorVenta(dto.getValorVenta());
                details.setDescuento(dto.getDescuento());
                list.get(list.size()-1).getDetailsPaymentVouchers().add(details);
            }else {
                voucherDto = new PaymentVoucherDto();
                voucherDto.setIdPaymentVoucher(dto.getId());
                voucherDto.setFechaEmision(dto.getFechaEmision());
                voucherDto.setTipoComprobante(dto.getTipoComprobante());
                voucherDto.setSerie(dto.getSerie());
                voucherDto.setNumero(dto.getNumero());
                voucherDto.setTipoDocIdentReceptor(dto.getTipoDocIdentReceptor());
                voucherDto.setNumDocIdentReceptor(dto.getNumDocIdentReceptor());
                voucherDto.setDenominacionReceptor(dto.getDenominacionReceptor());
                voucherDto.setCodigoMoneda(dto.getCodigoMoneda());
                voucherDto.setTotalValorVentaOperacionGravada(dto.getTotalValorVentaOperacionGravada());
                voucherDto.setTotalValorVentaOperacionExonerada(dto.getTotalValorVentaOperacionExonerada());
                voucherDto.setTotalValorVentaOperacionInafecta(dto.getTotalValorVentaOperacionInafecta());
                voucherDto.setMontoImporteTotalVenta(dto.getMontoImporteTotalVenta());
                voucherDto.setEstado(dto.getEstado());
                voucherDto.setEstadoSunat(dto.getEstadoSunat());
                voucherDto.setMontoDescuentoGlobal(dto.getMontoDescuentoGlobal());
                voucherDto.setSumatoriaIGV(dto.getSumatoriaIGV());
                voucherDto.setDetailsPaymentVouchers(new ArrayList<>());

                details = new DetailsPaymentVoucherDto();
                details.setCantidad(dto.getCantidad());
                details.setCodigoProducto(dto.getCodigoProducto());
                details.setDescripcion(dto.getDescripcion());
                details.setValorUnitario(dto.getValorUnitario());
                details.setValorVenta(dto.getValorVenta());
                details.setDescuento(dto.getDescuento());

                voucherDto.getDetailsPaymentVouchers().add(details);

                list.add(voucherDto);
                indice = dto.getId();
            }
        }
    return list;

    }

    @Transactional
    @Override
    public void updateComprobantesByEstadoAnticipo(String identificadorDocumento) {
        paymentVoucherRepository.updateComprobantesByEstadoAnticipo(identificadorDocumento);
    }

    @Override
    public Map<String, Object> getComprobantesEstadoByFiltersQuery(UserPrincipal userResponse, String fechaEmisionDesde,
                                                                   String fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor, String serie, Integer numero,
                                                                   Integer pageNumber, Integer perPage, Integer estadoSunats) {
        UserInterDto usuario = userRepository.findByUsernameInter(userResponse.getUsername()).get();
        Integer idOficina = null;
        if (usuario.getIdOficina() != null) {
            idOficina = usuario.getIdOficina();
        }
        Integer numpagina = (pageNumber-1) * perPage;
        Date filtroDesde = UtilDate.stringToDate(fechaEmisionDesde, "dd-MM-yyyy");
        Date filtroHasta = UtilDate.stringToDate(fechaEmisionHasta, "dd-MM-yyyy");
        if (numero == null) numero = 0;
        if (idOficina == null) idOficina = 0;
        String estadoSunat = estadoSunats.toString();
        List<PaymentVoucherInterDto> result = paymentVoucherRepository.findAllAndEstadoSerchForPages(userResponse.getRuc(),
                filtroDesde, filtroHasta, "%" + tipoComprobante + "%", "%" + numDocIdentReceptor + "%",
                "%" + serie + "%", numero, idOficina, "%" + estadoSunat + "%",numpagina,perPage);
        Integer cantidad = paymentVoucherRepository.countAllAndEstadoSerchForPages(userResponse.getRuc(),
                filtroDesde, filtroHasta, "%" + tipoComprobante + "%", "%" + numDocIdentReceptor + "%",
                "%" + serie + "%", numero, idOficina, "%" + estadoSunat + "%");

        //--> AQUI COLOCAMOS LA CONVERSION

        return ImmutableMap.of("comprobantesList", result, "total", cantidad);
    }

    @Override
    public Map<String, Object> getComprobantesDetallesByFiltersQuery(UserPrincipal user, Integer filtroIdPaymentVoucher) {
        User usuario = userRepository.findByUsername(user.getUsername()).get();
        Integer idOficina = null;
        if (usuario.getOficina() != null) {
            idOficina = usuario.getOficina().getId();
        }
        if (idOficina == null) idOficina = 0;
        List<DetallesComprobantesEntity> result = comprobantesMapper.findAllItems(user.getRuc(), filtroIdPaymentVoucher, idOficina);
        log.info("result detalles: " + result);
        return ImmutableMap.of("detallesList", result, "totalDetalles", result.size());
    }

    @Override
    public Map<String, Object> getPaymentsByDay(String ruc, int diff) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.add(Calendar.DATE, - diff);
        simpleDateFormat.format(fechaEmisionCalendar.getTime());
        List<Object[]> objects = paymentVoucherRepository.getPaymentsByDay(ruc,fechaEmisionCalendar.getTime());
        List<ListPayments> listPayments = objectsToListpayment(objects, diff);
        return ImmutableMap.of("lista", listPayments,"fecini",simpleDateFormat.format(fechaEmisionCalendar.getTime()));
    }

    @Override
    public Map<String, Object> getPaymentsByType(String ruc) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Object[]> objects = paymentVoucherRepository.getPaymentsByType(ruc,simpleDateFormat.format(new Date()));
        List<ListPaymentsType> listPayments = objectsToListpaymentByType(objects);
        return ImmutableMap.of("lista", listPayments);
    }

    @Override
    public Map<String, Object> getPaymentsByTypeMonth(String ruc) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.set(Calendar.DAY_OF_MONTH,1);
        List<Object[]> objects = paymentVoucherRepository.getPaymentsByTypeMonth(ruc,fechaEmisionCalendar.getTime());
        List<ListPaymentsType> listPayments = objectsToListpaymentByType(objects);
        return ImmutableMap.of("lista", listPayments);
    }

    @Override
    public Map<String, Object> getPaymentsByTypeAndStateMonth(String ruc) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.set(Calendar.DAY_OF_MONTH,1);
        List<Object[]> objects = paymentVoucherRepository.getPaymentsByTypeAndStateMonth(ruc,fechaEmisionCalendar.getTime());
        List<ListPaymentsTypeState> listPayments = objectsToListpaymentByTypeState(objects);
        return ImmutableMap.of("lista", listPayments);
    }

    @Override
    public Map<String, Object> getPaymentsByTypeAndState(String ruc) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Object[]> objects = paymentVoucherRepository.getPaymentsByTypeAndState(ruc,simpleDateFormat.format(new Date()));
        List<ListPaymentsTypeState> listPayments = objectsToListpaymentByTypeState(objects);
        return ImmutableMap.of("lista", listPayments);
    }
    @Override
    public Map<String, Object> getPaymentsColumnLine(String ruc, int diff) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.add(Calendar.DATE, - diff);
        List<Object[]> objects = paymentVoucherRepository.getPaymentsByTypeWeek(ruc,fechaEmisionCalendar.getTime());
        Map<String, Object> listPayments = objectsToListpaymentColumnLine(objects, diff);
        return listPayments;
    }



    private Map<String, Object> objectsToListpaymentColumnLine(List<Object[]> objects,int diff) {
        List<ListPaymentsColumnLine> listPaymentsColumnLines = new ArrayList<>();
        ListPaymentsColumnLine paymentsColumnLine = null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaLlenarCalendar = Calendar.getInstance();
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.add(Calendar.DATE,- diff);
        fechaLlenarCalendar.add(Calendar.DATE, - diff);
        List<ListPayments> listPayments = new ArrayList<>();
        ListPayments payments = null;
        fieldsDateTypeFact.clear();
        fieldsDateTypeBol.clear();
        for (int i = 0; i < diff; i++) {
            fechaEmisionCalendar.add(Calendar.DATE, +1);
            fieldsDateTypeFact.put(simpleDateFormat.format(fechaEmisionCalendar.getTime()),0);
            fieldsDateTypeBol.put(simpleDateFormat.format(fechaEmisionCalendar.getTime()),0);
        }
        for (Object[] o : objects) {
            if((o[1].toString()).equals("01")){
                fieldsDateTypeFact.put(o[0].toString(),Integer.parseInt(o[2].toString()));
            }else if ((o[1].toString()).equals("03")){
                fieldsDateTypeBol.put(o[0].toString(),Integer.parseInt(o[2].toString()));
            }
        }
        int[] listFactura = new int[7];
        int[] listBoleta = new int[7];
        int[] listTotal = new int[7];
        int totFact = 0;
        int totBol = 0;
        for(int i = 0; i < diff; i++){
            fechaLlenarCalendar.add(Calendar.DATE, +1);
            listFactura[i] = fieldsDateTypeFact.get(simpleDateFormat.format(fechaLlenarCalendar.getTime()));
            listBoleta[i] = fieldsDateTypeBol.get(simpleDateFormat.format(fechaLlenarCalendar.getTime()));
            listTotal[i] = fieldsDateTypeFact.get(simpleDateFormat.format(fechaLlenarCalendar.getTime())) + fieldsDateTypeBol.get(simpleDateFormat.format(fechaLlenarCalendar.getTime()));
            totFact += listFactura[i];
            totBol += listBoleta[i];
        }
        paymentsColumnLine = new ListPaymentsColumnLine();
        paymentsColumnLine.setType("column");
        paymentsColumnLine.setName("Facturas");
        paymentsColumnLine.setData(listFactura);

        paymentsColumnLine = new ListPaymentsColumnLine();
        paymentsColumnLine.setType("column");
        paymentsColumnLine.setName("Boletas");
        paymentsColumnLine.setData(listBoleta);

        paymentsColumnLine = new ListPaymentsColumnLine();
        paymentsColumnLine.setType("column");
        paymentsColumnLine.setName("Total");
        paymentsColumnLine.setData(listTotal);

        listPaymentsColumnLines.add(paymentsColumnLine);

        return ImmutableMap.of("arrayf", listFactura,"arrayb",listBoleta,"arrayt",listTotal,"facturas",totFact, "boletas",totBol);
    }

    private List<ListPaymentsTypeState> objectsToListpaymentByTypeState(List<Object[]> objects) {
        List<ListPaymentsTypeState> list = new ArrayList<>();
        ListPaymentsTypeState typeState = null;
        String estado = "";
        int[] datas = new int[4];
        for (Object[] o : objects) {
            if(!estado.equals(o[0].toString())){
                if(typeState!=null)
                    list.add(typeState);

                typeState = new ListPaymentsTypeState();
                typeState.setName(fieldsStates.get(o[0].toString()));

                datas  = new int[4];
                for (int i=0; i < 4; i++){
                    datas[i] =0;
                }

                typeState.setData(datas);
                estado = o[0].toString();
            }

            //typeState.setName(fieldsPayment.get(o[0].toString()));
            if((o[1].toString()).equals("01")){
                typeState.getData()[0] = Integer.parseInt(o[2].toString());
            }else if((o[1].toString()).equals("03")){
                typeState.getData()[1] = Integer.parseInt(o[2].toString());
            }else if((o[1].toString()).equals("07")){
                typeState.getData()[2] = Integer.parseInt(o[2].toString());
            }else if((o[1].toString()).equals("08")){
                typeState.getData()[3] = Integer.parseInt(o[2].toString());
            }


        }
        list.add(typeState);
        return list;
    }

    @Override
    public Map<String, Object> getPaymentsByTypeAndDay(String ruc) {
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.add(Calendar.DATE, - 27);
        List<Object[]> objects = paymentVoucherRepository.getPaymentsByTypeAndDay(ruc,fechaEmisionCalendar.getTime());
        List<ListPaymentsByFechaAndType> listPayments = objectsToListpaymentByTypeAndDay(objects);
        return ImmutableMap.of("lista", listPayments);
    }

    @Override
    public Map<String, Object> getPaymentsByUserAndDay(String ruc, Integer diff) {
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.add(Calendar.DATE, - diff);
        List<Object[]> objects = paymentVoucherRepository.getPaymentsByUserAndDay(ruc,fechaEmisionCalendar.getTime());
        Map<String, Object> listPayments = objectsToListpaymentByUserAndDay(objects, diff);
        return listPayments;
    }

    @Override
    public Map<String, Object> getPaymentsByUserAndMonth(String ruc) {
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.set(Calendar.MONTH,0);
        fechaEmisionCalendar.set(Calendar.DAY_OF_MONTH,1);
        List<Object[]> objects = paymentVoucherRepository.getPaymentsByUserAndMonth(ruc,fechaEmisionCalendar.getTime());
        Map<String, Object> listPayments = objectsToListpaymentByUserAndMonth(objects);
        return listPayments;
    }

    private Map<String, Object> objectsToListpaymentByUserAndMonth(List<Object[]> objects) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaLlenarCalendar = Calendar.getInstance();
        ArrayList<String> listvendedor = new ArrayList<>();

        Calendar fechaEmisionCalendar = Calendar.getInstance();
        int mesact = fechaEmisionCalendar.get(Calendar.MONTH);
        int mesactlength = mesact + 1;

        String vendedor = "";
        int indexvend = 0;
        int[] arrayventas = new int[3];
        ArrayList<int[]> listarrayventas = new ArrayList<>();
        for (Object[] o : objects) {
            if(!(o[0].toString()).equals(vendedor)){
                if(vendedor != ""){

                    for (int y = 0; y < mesactlength; y++){
                        arrayventas = new int[3];
                        arrayventas[0] = indexvend;
                        arrayventas[1] = y;
                        arrayventas[2] = fieldsIntMonths.get(y);
                        listarrayventas.add(arrayventas);
                    }
                    indexvend ++;
                }
                fieldsIntMonths.clear();
                for(int p = 0; p < mesactlength; p++){
                    fieldsIntMonths.put(p,0);
                }
                fieldsIntMonths.put(Integer.parseInt(o[1].toString()) - 1,Integer.parseInt(o[2].toString()));

                listvendedor.add(o[0].toString());
                vendedor=o[0].toString();
            }else{
                fieldsIntMonths.put(Integer.parseInt(o[1].toString()) - 1,Integer.parseInt(o[2].toString()));
            }
        }

        if(vendedor != ""){
            for (int y = 0; y < mesactlength; y++){
                arrayventas = new int[3];
                arrayventas[0] = indexvend;
                arrayventas[1] = y;
                arrayventas[2] = fieldsIntMonths.get(y);
                listarrayventas.add(arrayventas);
            }
        }
        return ImmutableMap.of("listvendedor", listvendedor,"arrayventas",listarrayventas);
    }

    @Override
    public Map<String, Object> getPaymentsColumnLineMonths(String ruc) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.set(Calendar.MONTH,0);
        fechaEmisionCalendar.set(Calendar.DAY_OF_MONTH,1);
        List<Object[]> objects = paymentVoucherRepository
                .getPaymentsByTypeMonths(ruc,fechaEmisionCalendar.getTime());
        Map<String, Object> listPayments = objectsToListpaymentColumnLineMonths(objects);
        return listPayments;
    }

    private Map<String, Object> objectsToListpaymentColumnLineMonths(List<Object[]> objects) {
        List<ListPaymentsColumnLine> listPaymentsColumnLines = new ArrayList<>();
        ListPaymentsColumnLine paymentsColumnLine = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        int mesact = fechaEmisionCalendar.get(Calendar.MONTH);
        int mesactlength = mesact + 1;

        List<ListPayments> listPayments = new ArrayList<>();
        ListPayments payments = null;
        fieldsDateTypeFact.clear();
        fieldsDateTypeBol.clear();
        for (int i = 0; i < mesactlength; i++) {
            fechaEmisionCalendar.add(Calendar.DATE, +1);
            fieldsDateTypeFact.put(fieldsMonths.get(i+1),0);
            fieldsDateTypeBol.put(fieldsMonths.get(i+1),0);
        }
        for (Object[] o : objects) {
            if((o[1].toString()).equals("01")){
                fieldsDateTypeFact.put(fieldsMonths.get(Integer.parseInt(o[0].toString())),Integer.parseInt(o[2].toString()));
            }else if ((o[1].toString()).equals("03")){
                fieldsDateTypeBol.put(fieldsMonths.get(Integer.parseInt(o[0].toString())),Integer.parseInt(o[2].toString()));
            }
        }

        int[] listFactura = new int[mesactlength];
        int[] listBoleta = new int[mesactlength];
        int[] listTotal = new int[mesactlength];
        int totFact = 0;
        int totBol = 0;
        for(int i = 0; i < mesactlength; i++){
            //fechaLlenarCalendar.add(Calendar.DATE, +1);
            listFactura[i] = fieldsDateTypeFact.get(fieldsMonths.get(i +1));
            listBoleta[i] = fieldsDateTypeBol.get(fieldsMonths.get(i +1 ));
            listTotal[i] = fieldsDateTypeFact.get(fieldsMonths.get(i + 1)) + fieldsDateTypeBol.get(fieldsMonths.get(i + 1));
            totFact += listFactura[i];
            totBol += listBoleta[i];
        }
        paymentsColumnLine = new ListPaymentsColumnLine();
        paymentsColumnLine.setType("column");
        paymentsColumnLine.setName("Facturas");
        paymentsColumnLine.setData(listFactura);

        paymentsColumnLine = new ListPaymentsColumnLine();
        paymentsColumnLine.setType("column");
        paymentsColumnLine.setName("Boletas");
        paymentsColumnLine.setData(listBoleta);

        paymentsColumnLine = new ListPaymentsColumnLine();
        paymentsColumnLine.setType("column");
        paymentsColumnLine.setName("Total");
        paymentsColumnLine.setData(listTotal);

        listPaymentsColumnLines.add(paymentsColumnLine);

        return ImmutableMap.of("arrayf", listFactura,"arrayb",listBoleta,"arrayt",listTotal,"facturas",totFact, "boletas",totBol);

    }

    @Override
    public Map<String, Object> getPaymentsByMonth(String ruc) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.set(Calendar.MONTH,0);
        fechaEmisionCalendar.set(Calendar.DAY_OF_MONTH,1);
        List<Object[]> objects = paymentVoucherRepository
                .getPaymentsByMonth(ruc,fechaEmisionCalendar.getTime());
        List<ListPayments> listPayments = objectsToListpaymentMonth(objects);
        return ImmutableMap.of("lista", listPayments,"fecini",simpleDateFormat.format(fechaEmisionCalendar.getTime()));
    }



    private List<ListPayments> objectsToListpaymentMonth(List<Object[]> objects) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        int mes = fechaEmisionCalendar.get(Calendar.MONTH) + 1;
        List<ListPayments> listPayments = new ArrayList<>();
        ListPayments payments = null;
        for (int i = 0; i < mes; i++){
            payments = new ListPayments();
            payments.setFecha(fieldsMonths.get(i + 1));
            payments.setCantidad(0);

            for (Object[] o : objects) {
                if(Integer.parseInt(o[0].toString()) == i + 1) {
                    payments.setCantidad(Integer.parseInt(o[1].toString()));
                }
            }
            listPayments.add(payments);
        }
        return listPayments;
    }

    private Map<String, Object> objectsToListpaymentByUserAndDay(List<Object[]> objects, int diff) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaLlenarCalendar = Calendar.getInstance();
        ArrayList<String> listvendedor = new ArrayList<>();
        String vendedor = "";
        int indexvend = 0;
        int[] arrayventas = new int[3];
        ArrayList<int[]> listarrayventas = new ArrayList<>();
        for (Object[] o : objects) {
            if(!(o[0].toString()).equals(vendedor)){
                if(vendedor != ""){
                    fechaLlenarCalendar.add(Calendar.DATE, - diff);
                    for (int y = 0; y < diff; y++){
                        arrayventas = new int[3];
                        fechaLlenarCalendar.add(Calendar.DATE, +1);
                        //fieldsDate.put(simpleDateFormat.format(fechaLlenarCalendar.getTime()),0);
                        arrayventas[0] = indexvend;
                        arrayventas[1] = y;
                        arrayventas[2] = fieldsDate.get(simpleDateFormat.format(fechaLlenarCalendar.getTime()));
                        listarrayventas.add(arrayventas);
                    }
                    indexvend ++;
                }
                fechaLlenarCalendar.add(Calendar.DATE, - diff);
                fieldsDate.clear();
                for(int p = 0; p < diff; p++){
                    fechaLlenarCalendar.add(Calendar.DATE, +1);
                    fieldsDate.put(simpleDateFormat.format(fechaLlenarCalendar.getTime()),0);
                }
                fieldsDate.put(o[1].toString(),Integer.parseInt(o[2].toString()));
                listvendedor.add(o[0].toString());
                vendedor=o[0].toString();
            }else{
                fieldsDate.put(o[1].toString(),Integer.parseInt(o[2].toString()));
            }
            //fieldsDate.put(simpleDateFormat.format(fechaLlenarCalendar.getTime()),Integer.parseInt(o[0].toString()));
        }

        if(vendedor != ""){
            fechaLlenarCalendar.add(Calendar.DATE, - diff);
            for (int y = 0; y < diff; y++){
                arrayventas = new int[3];
                fechaLlenarCalendar.add(Calendar.DATE, +1);
                arrayventas[0] = indexvend;
                arrayventas[1] = y;
                arrayventas[2] = fieldsDate.get(simpleDateFormat.format(fechaLlenarCalendar.getTime()));
                listarrayventas.add(arrayventas);
            }
        }
        return ImmutableMap.of("listvendedor", listvendedor,"arrayventas",listarrayventas);
    }

    private List<ListPaymentsByFechaAndType> objectsToListpaymentByTypeAndDay(List<Object[]> objects) {
        List<ListPaymentsByFechaAndType> list = new ArrayList<>();
        ListPaymentsByFechaAndType payments = null;
        List<ListPaymentsType> listPaymentsTypes = null;
        ListPaymentsType paymentsType = null;
        String fecha = "";
        for (Object[] o : objects) {
            if(!fecha.equals(o[0].toString())){
                payments = new ListPaymentsByFechaAndType();
                payments.setFecha(o[0].toString());
                listPaymentsTypes = new ArrayList<>();
            }else {
                paymentsType = new ListPaymentsType();
                paymentsType.setName(o[1].toString());
                paymentsType.setY(Integer.parseInt(o[2].toString()));
                listPaymentsTypes.add(paymentsType);
            }
            list.add(payments);

        }
        return list;
    }

    public ComprobantesServiceImpl() {
        fieldsPayment.put("01","FACTURA");
        fieldsPayment.put("03","BOLETA");
        fieldsPayment.put("07","NOTA DE CREDITO");
        fieldsPayment.put("08","NOTA DE DEBITO");

        fieldsStates.put("01","PENDIENTE");
        fieldsStates.put("02","ACEPTADO");
        fieldsStates.put("05","RECHAZADO");
        fieldsStates.put("06","ERROR");
        fieldsStates.put("08","ANULADO");

        fieldsMonths.put(1,"ENERO");
        fieldsMonths.put(2,"FEBRERO");
        fieldsMonths.put(3,"MARZO");
        fieldsMonths.put(4,"ABRIL");
        fieldsMonths.put(5,"MAYO");
        fieldsMonths.put(6,"JUNIO");
        fieldsMonths.put(7,"JULIO");
        fieldsMonths.put(8,"AGOSTO");
        fieldsMonths.put(9,"SETIEMBRE");
        fieldsMonths.put(10,"OCTUBRE");
        fieldsMonths.put(11,"NOVIEMBRE");
        fieldsMonths.put(12,"DICIEMBRE");
    }

    private List<ListPaymentsType> objectsToListpaymentByType(List<Object[]> objects) {


        List<ListPaymentsType> listPayments = new ArrayList<>();
        ListPaymentsType payments = null;
        for (Object[] o : objects) {
            payments = new ListPaymentsType();
            payments.setName(fieldsPayment.get(o[0].toString()));
            payments.setY(Integer.parseInt(o[1].toString()));
            listPayments.add(payments);
        }
        return listPayments;
    }

    private List<ListPayments> objectsToListpayment(List<Object[]> objects, int diff) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.add(Calendar.DATE,- diff);
        List<ListPayments> listPayments = new ArrayList<>();
        ListPayments payments = null;
        for (int i = 0; i < diff; i++){
            payments = new ListPayments();
            fechaEmisionCalendar.add(Calendar.DATE, + 1);
            payments.setFecha(simpleDateFormat.format(fechaEmisionCalendar.getTime()));
            payments.setCantidad(0);
            for (Object[] o : objects) {
                if((o[0].toString()).equals(payments.getFecha())){
                    payments.setCantidad(Integer.parseInt(o[1].toString()));
                }
            }
            listPayments.add(payments);

        }

        return listPayments;

    }


    @Override
    public byte[] exportExcelByFilters(UserPrincipal userResponse, String fechaEmisionDesde,
                                       String fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor, String serie, Integer numero,
                                       Integer estadoSunats) {

        User usuario = userRepository.findByUsername(userResponse.getUsername()).get();
        Integer idOficina = null;
        if (usuario.getOficina() != null) {
            idOficina = usuario.getOficina().getId();
        }

        Date filtroDesde = UtilDate.stringToDate(fechaEmisionDesde, "dd-MM-yyyy");
        Date filtroHasta = UtilDate.stringToDate(fechaEmisionHasta, "dd-MM-yyyy");

        if (numero == null) numero = 0;
        if (idOficina == null) idOficina = 0;
        String estadoSunat = estadoSunats.toString();
        List<PaymentVoucherEntity> data = paymentVoucherRepository.findAllForExportExcel(userResponse.getRuc(),
                filtroDesde, filtroHasta, "%" + tipoComprobante + "%", "%" + numDocIdentReceptor + "%",
                "%" + serie + "%", numero, idOficina, "%" + estadoSunats + "%");

        List<List<String>> dataExcel = UtilExcel.convertListTicketsToListString(data);
        String[] columns = {"Fecha Emisión", "Tipo", "Número",
                "Tipo documento", "Num. Documento", "Receptor", "Moneda",
                "Gravada", "Exonerada", "Inafecta", "IGV", "Descuento total",
                "Monto Total", "Estado", "Estado Sunat", "Otros tributos",
                "Gratuita", "Detracción", "Imp. Detracción",
                "Comp. afectado","Codigo","Descripcion","Cantidad","Valor unidad","Valor venta",
                "Descuento","Condiciones","Deposito","Colegiado","Trasaccion","Operacion","Especialidad"
        };

        String nombreHoja = "Comprobantes";

        return UtilExcel.generateExcelFromList(nombreHoja, columns, dataExcel);
    }


    @Override
    public Map<String, Object> getCantidadComprobantesByCompany(UserPrincipal userPrincipal) {
        userPrincipal.getRuc();

        return ImmutableMap.of("total",
                paymentVoucherRepository.findAllByRucCount(userPrincipal.getRuc()));

    }

    @Override
    public Workbook getAllComprobantesByFiltersQuery(UserPrincipal userResponse, String fechaEmisionDesde,
                                                     String fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor, String serie,
                                                     Integer numero) throws IOException {
        if (tipoComprobante != null) {
            tipoComprobante = "%" + tipoComprobante + "%";
        }
        if (numDocIdentReceptor != null) {
            numDocIdentReceptor = "%" + numDocIdentReceptor + "%";
        }
        if (serie != null) {
            serie = "%" + serie + "%";
        }
        User usuario = userRepository.findByUsername(userResponse.getUsername()).get();
        Integer idOficina = null;
        if (usuario.getOficina() != null) {
            //idOficina = usuario.getOficina().getId();
        }
        List<PaymentVoucherEntity> result = paymentVoucherRepository.findAllSerch(userResponse.getRuc(),
                fechaEmisionDesde, fechaEmisionHasta, tipoComprobante, numDocIdentReceptor, serie, numero, idOficina);
				/*, "%" +  + "%",
				"%" + serie + "%", numero, idOficina);*/


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        sheet.setColumnWidth((short) 0, (short) ((50 * 8) / ((double) 1 / 20)));
        sheet.setColumnWidth((short) 1, (short) ((50 * 8) / ((double) 1 / 20)));
        workbook.setSheetName(0, "XSSFWorkbook example");
        Font font1 = workbook.createFont();
        font1.setFontHeightInPoints((short) 10);
        font1.setColor((short) 0xc); // make it blue
        font1.setBold(true);
        XSSFCellStyle cellStyle1 = (XSSFCellStyle) workbook.createCellStyle();
        cellStyle1.setFont(font1);

        Font font2 = workbook.createFont();
        font2.setFontHeightInPoints((short) 10);
        font2.setColor((short) Font.COLOR_NORMAL);
        XSSFCellStyle cellStyle2 = (XSSFCellStyle) workbook.createCellStyle();
        cellStyle2.setFont(font2);

        Row headerRow = sheet.createRow(0);
        Cell cell1 = headerRow.createCell(0);
        cell1.setCellValue("FECHA");
        cell1.setCellStyle(cellStyle1);
        Cell cell2 = headerRow.createCell(1);
        cell2.setCellValue("TIPO");
        cell2.setCellStyle(cellStyle1);
        Cell cell3 = headerRow.createCell(2);
        cell1.setCellValue("NUMERO");
        cell1.setCellStyle(cellStyle1);
        Cell cell4 = headerRow.createCell(3);
        cell2.setCellValue("DOCUMENTO");
        cell2.setCellStyle(cellStyle1);
        int rownum;
        Row row = null;
        Cell cell = null;

        for (rownum = (short) 1; rownum <= result.size(); rownum++) {
            row = sheet.createRow(rownum);
            cell = row.createCell(0);
            cell.setCellValue(result.get(rownum - 1).getFechaEmisionDate());
            cell.setCellStyle(cellStyle2);
            cell = row.createCell(1);
            cell.setCellValue(result.get(rownum - 1).getTipoComprobante());
            cell.setCellStyle(cellStyle2);
            row = sheet.createRow(rownum);
            cell = row.createCell(2);
            cell.setCellValue(result.get(rownum - 1).getSerie() + "-" + result.get(rownum - 1).getNumero());
            cell.setCellStyle(cellStyle2);
            cell = row.createCell(3);
            cell.setCellValue(result.get(rownum - 1).getTipoDocIdentReceptor());
            cell.setCellStyle(cellStyle2);
        }

        final String FILE_NAME = "./xssf_example.xlsx";
        FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
        return workbook;
        //return ImmutableMap.of("comprobantesList", result, "total", result.size());
    }


    @Override
    public Integer getSiguienteNumeroComprobante(String tipoDocumento, String serie, String ruc) {
        if (!tipoDocumento.equals("09")) {
            PaymentVoucherEntity ultimoComprobante = paymentVoucherRepository
                    .findFirst1ByTipoComprobanteAndSerieAndRucEmisorOrderByNumeroDesc(tipoDocumento, serie, ruc);
            if (ultimoComprobante != null) {
                return ultimoComprobante.getNumero() + 1;
            }
        } else {
            GuiaRemisionEntity guiaRemision = guiaRemisionRepository
                    .findFirst1BySerieAndNumeroDocumentoIdentidadRemitenteOrderByNumeroDesc(serie, ruc);
            if (guiaRemision != null) {
                return guiaRemision.getNumero() + 1;
            }
        }

        return 1;
    }

    @Override
    public PaymentVoucherEntity getComprobanteById(Long id) {
        PaymentVoucherEntity pv = paymentVoucherRepository.findById(id).get();

        return pv;
    }

    @Override
    public OtherCpeEntity getOtherVoucherById(Long id){
        System.out.println("Entro a editar other voucher");
        OtherCpeEntity oc = otherCpeRepository.findById(id).get();

        ObjectMapper mapper = new ObjectMapper();
        try{
            String json = mapper.writeValueAsString(oc);
            System.out.println("Other vocuher por id");
            System.out.println(json);
        }catch (Exception e){

        }

        return oc;
    }

    @Override
    public GuiaRemisionEntity getGuiaById(Long id) {
        GuiaRemisionEntity gr = guiaRemisionRepository.findById(id).get();

        ObjectMapper mapper = new ObjectMapper();
        try{
            String json = mapper.writeValueAsString(gr);
            System.out.println("Guia remision por id");
            System.out.println(json);
        }catch (Exception e){

        }


        return gr;
    }

    @Override
    public List<PaymentVoucherEntity> findComprobanteByNota(String numDocIdentReceptor, String serie,
                                                            String rucEmisor) {

        Calendar fechaHasta = Calendar.getInstance();
        Calendar fechaDesde = Calendar.getInstance();
        fechaDesde.add(Calendar.DATE, -15);

        List<String> tipoComprobante = new ArrayList<>();
        tipoComprobante.add("01");
        tipoComprobante.add("03");

        if (numDocIdentReceptor == null) {
            numDocIdentReceptor = "";
        }
        if (serie == null) {
            serie = "";
        }

        return paymentVoucherRepository
                .findAllByFechaEmisionDateBetweenAndTipoComprobanteInAndNumDocIdentReceptorAndSerieStartingWithAndRucEmisorOrderByNumDocIdentReceptorAscSerieAsc(
                        fechaDesde.getTime(), fechaHasta.getTime(), tipoComprobante, numDocIdentReceptor, serie,
                        rucEmisor);
    }

    @Override
    public List<PaymentVoucherEntity> findComprobanteByAnticipo(String numDocIdentReceptor, String rucEmisor) {

        List<String> tipoComprobante = new ArrayList<>();
        tipoComprobante.add("01");
        tipoComprobante.add("03");

        if (numDocIdentReceptor == null) {
            numDocIdentReceptor = "";
        }

        List<PaymentVoucherEntity> result = new ArrayList<>();
        List<PaymentVoucherEntity> list = paymentVoucherRepository
                .findAllByTipoComprobanteInAndNumDocIdentReceptorAndRucEmisorAndTipoOperacionAndEstadoOrderByNumDocIdentReceptor(
                        tipoComprobante, numDocIdentReceptor, rucEmisor, "04", "02");

        List<PaymentVoucherEntity> listDetracciones = paymentVoucherRepository
                .findAllByTipoComprobanteInAndNumDocIdentReceptorAndRucEmisorAndTipoOperacionAndEstadoOrderByNumDocIdentReceptor(
                        tipoComprobante, numDocIdentReceptor, rucEmisor, "1001", "02");


        for (PaymentVoucherEntity vouch : list) {
            if (vouch.getAnticipos().size() <= 0) {
                result.add(vouch);

            }
        }

        for (PaymentVoucherEntity vouch : listDetracciones) {

            if (vouch.getEstadoAnticipo() != null && vouch.getEstadoAnticipo() == 1) {
                if (vouch.getAnticipos().size() <= 0) {
                    result.add(vouch);
                }
            }
        }

        return result;
    }

    @Override
    public List<InfoEstadoSunat> getEstadoSunatByListaIds(List<Long> ids) {
        List<InfoEstadoSunat> respuesta = new ArrayList<>();
        List<PaymentVoucherEntity> comprobantes = paymentVoucherRepository.findByIdPaymentVoucherIn(ids);
        comprobantes.forEach(pv -> {
            respuesta.add(InfoEstadoSunat.builder().id(pv.getIdPaymentVoucher()).estado(pv.getEstado())
                    .estadoSunat(pv.getEstadoSunat()).build());
        });
        return respuesta;
    }

    @Override
    public List<InfoEstadoSunat> getEstadoSunatGuiaByListaIds(List<Long> ids) {
        List<InfoEstadoSunat> respuesta = new ArrayList<>();
        List<GuiaRemisionEntity> guias = guiaRemisionRepository.findByIdGuiaRemisionIn(ids);
        guias.forEach(guia -> {
            respuesta.add(InfoEstadoSunat.builder().id(guia.getIdGuiaRemision()).estado(guia.getEstado())
                    .estadoSunat(guia.getEstadoEnSunat()).build());
        });
        return respuesta;
    }


    @Override
    public List<GuiaRemisionEntity> searchGuiaRemision(Date filtroDesde, Date filtroHasta, String ruc, String serie,
                                                       Integer numero) throws Exception {

        Calendar fechaHasta = Calendar.getInstance();
        fechaHasta.setTime(filtroHasta);
        fechaHasta.add(Calendar.DATE, 1);

        return guiaRemisionRepository.findAllGuiaRemision(filtroDesde, fechaHasta.getTime(), ruc, serie, numero);
    }

    @Override
    public Map<String, Object> getGuiasEstadoByFiltersQuery(UserPrincipal userResponse, String fechaEmisionDesde,
                                                                   String fechaEmisionHasta, String serie, Integer numero,
                                                                   Integer pageNumber, Integer perPage) {
        User usuario = userRepository.findByUsername(userResponse.getUsername()).get();
        Integer idOficina = null;
        if (usuario.getOficina() != null) {
            idOficina = usuario.getOficina().getId();
        }

        Date filtroDesde = UtilDate.stringToDate(fechaEmisionDesde, "dd-MM-yyyy");
        Date filtroHasta = UtilDate.stringToDate(fechaEmisionHasta, "dd-MM-yyyy");

        if (numero == null) numero = 0;
        if (idOficina == null) idOficina = 0;
        Page<GuiaRemisionEntity> result = guiaRemisionRepository.findAllSerchForPages(userResponse.getRuc(),
                filtroDesde, filtroHasta,
                "%" + serie + "%", numero,idOficina, new PageRequest((pageNumber - 1), perPage));

        //--> AQUI COLOCAMOS LA CONVERSION

        return ImmutableMap.of("guiasList", result.map(GuiaRemisionDto::transformToDtoLite), "total", result.getTotalElements());
    }

    @Override
    public List<PaymentVoucherEntity> findComprobanteByCredito(String numDocIdentReceptor, String rucEmisor) {


        if (numDocIdentReceptor == null) {
            numDocIdentReceptor = "";
        }

        List<PaymentVoucherEntity> result = new ArrayList<>();
        List<PaymentVoucherEntity> list = paymentVoucherRepository
                .getPaymentVocuherByCredito(numDocIdentReceptor, rucEmisor);

        for (PaymentVoucherEntity vouch : list) {
            if (vouch.getAnticipos().size() <= 0) {
                result.add(vouch);

            }
        }

        return list;
    }




}
    @Data
    @NoArgsConstructor
    class ListPayments {
        String fecha;
        int cantidad;
    }
    @Data
    @NoArgsConstructor
    class ListPaymentsType {
        String name;
        int y;
    }
    @Data
    @NoArgsConstructor
    class ListPaymentsTypeState {
        String name;
        int[] data;
    }
    @Data
    @NoArgsConstructor
    class ListPaymentsColumnLine {
        String type;
        String name;
        int[] data;
    }
    @Data
    @NoArgsConstructor
    class ListPaymentsByFechaAndType {
        String fecha;
        List<ListPaymentsType> lista;
    }
    @Data
    @NoArgsConstructor
    class ListPaymentsByUserAndDate {
        String user;
        List<ListPayments> lista;
    }