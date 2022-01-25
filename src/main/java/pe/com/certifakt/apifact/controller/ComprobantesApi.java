package pe.com.certifakt.apifact.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.mapper.ComprobantesMapper;
import pe.com.certifakt.apifact.model.ComprobantesEntity;
import pe.com.certifakt.apifact.model.GuiaRemisionEntity;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.ComprobantesService;

import pe.com.certifakt.apifact.service.ExcelService;

import pe.com.certifakt.apifact.service.ExcelService;


import pe.com.certifakt.apifact.service.impl.ReportServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ComprobantesApi {

    private final ComprobantesService comprobantesService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ComprobantesMapper comprobantesMapper;

    @Autowired
    private ReportServiceImpl reportService;

    @GetMapping("/obtenertodos")
    public List<ComprobantesEntity> obtenerTodos(){
        return comprobantesMapper.getAll();
    }

    @GetMapping("/comprobantes")
    public ResponseEntity<?> comprobantes(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            @RequestParam(name = "estadoSunat", required = false) Integer estadoSunat,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getComprobantesEstadoByFiltersQuery(userPrincipal, filtroDesde,
                filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat), HttpStatus.OK);
    }

    @GetMapping("/comprobanteDetail")
    public ResponseEntity<?> comprobanteDetail(
            @RequestParam(name = "idPayment", required = true) Integer idPayment,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getComprobanteDetailById(idPayment), HttpStatus.OK);
    }
    @GetMapping("/comprobanteIdentificador")
    public ResponseEntity<?> comprobantesIde(
            @RequestParam(name = "identificador", required = true) String indentificador,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getComprobantesEstadoByIdentificador(userPrincipal,
                indentificador), HttpStatus.OK);
    }


    @GetMapping("/cantidadComprobantes")
    public ResponseEntity<?> cantidadcomprobantes(
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getCantidadComprobantesByCompany(userPrincipal), HttpStatus.OK);
    }
    @GetMapping("/detalles")
    public ResponseEntity<?> detalles(
            @RequestParam Integer filtroIdPaymentVoucher,
            @CurrentUser UserPrincipal userPrincipal
    ){
        return new ResponseEntity<Object>(comprobantesService.getComprobantesDetallesByFiltersQuery(userPrincipal,filtroIdPaymentVoucher), HttpStatus.OK);
    }


    @GetMapping("/comprobantes-exportar-excel")
    public ResponseEntity<?> comprobantesExportarExcel(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "estadoSunat", required = false) Integer estadoSunat,
            @CurrentUser UserPrincipal userPrincipal) {

        byte[] file = comprobantesService.exportExcelByFilters(userPrincipal, filtroDesde,
                filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, estadoSunat);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "reporte-comprobantes.xlsx" + "\"")
                .body(file);
    }

    @GetMapping("/excels")
    public ResponseEntity<?> listarexcels(
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(excelService.getExcels(userPrincipal,pageNumber,perPage), HttpStatus.OK);
    }

    @GetMapping("/export/all")
    public ResponseEntity<InputStreamResource> exportExcelData(
            @CurrentUser UserPrincipal userPrincipal
    ) throws Exception{
        ByteArrayInputStream stream = reportService.exportAllData(userPrincipal.getRuc());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename=comprobantes.xls");

        return  ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }


    @GetMapping("/allcomprobantescsv")
    public ResponseEntity<?> allcomprobantesf(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "contador", required = false,defaultValue = "false") boolean contador,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getComprobantesAllfByFiltersQuery(userPrincipal, filtroDesde,
                filtroHasta, filtroTipoComprobante, filtroSerie,contador), HttpStatus.OK);
    }

    @GetMapping("/allcomprobantes")
    public ResponseEntity<?> allcomprobantes(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @CurrentUser UserPrincipal userPrincipal) throws IOException {

        return new ResponseEntity<Object>(comprobantesService.getAllComprobantesByFiltersQuery(userPrincipal, filtroDesde,
                filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero), HttpStatus.OK);
    }

    @GetMapping("/comprobantes-notas")
    public ResponseEntity<List<PaymentVoucherEntity>> comprobantesNota(
            @RequestParam(name = "filtroNumDoc", required = true) String filtroNumDoc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<List<PaymentVoucherEntity>>(comprobantesService.findComprobanteByNota(filtroNumDoc, filtroSerie, userPrincipal.getRuc()), HttpStatus.OK);
    }

    @GetMapping("/comprobantes-anticipo")
    public ResponseEntity<List<PaymentVoucherEntity>> comprobantesAnticipo(
            @RequestParam(name = "filtroNumDoc", required = true) String filtroNumDoc,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<List<PaymentVoucherEntity>>(comprobantesService.findComprobanteByAnticipo(filtroNumDoc, userPrincipal.getRuc()), HttpStatus.OK);
    }

    @GetMapping("/comprobantes-credito")
    public ResponseEntity<List<PaymentVoucherEntity>> comprobantesCredito(
            @RequestParam(name = "filtroNumDoc", required = true) String filtroNumDoc,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<List<PaymentVoucherEntity>>(comprobantesService.findComprobanteByCredito(filtroNumDoc, userPrincipal.getRuc()), HttpStatus.OK);
    }


    @GetMapping("/comprobanteById")
    public ResponseEntity<?> comprobanteById(
            @RequestParam(name = "id", required = true) Long id,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getComprobanteById(id), HttpStatus.OK);
    }


    @GetMapping("/siguienteNumero/{tipoDocumento}/{serie}")
    public ResponseEntity<?> ultimoComprobante(
            @PathVariable String tipoDocumento,
            @PathVariable String serie,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getSiguienteNumeroComprobante(tipoDocumento, serie, userPrincipal.getRuc()), HttpStatus.OK);
    }

    @PostMapping("/getEstadosSunat")
    public ResponseEntity<?> getEstadosSunat(@RequestBody List<Long> idsPaymentVouchers) {

        return new ResponseEntity<Object>(comprobantesService.getEstadoSunatByListaIds(idsPaymentVouchers), HttpStatus.OK);
    }

    @PostMapping("/getEstadosSunatGuias")
    public ResponseEntity<?> getEstadosSunatGuias(@RequestBody List<Long> idsGuiasRemision) {

        return new ResponseEntity<Object>(comprobantesService.getEstadoSunatByListaIds(idsGuiasRemision), HttpStatus.OK);
    }


    @RequestMapping(value = {"/update-anticipo-comprobante"}, method = RequestMethod.POST)
    @ResponseBody
    public void paymentVoucherUpdateAnticipo(HttpServletRequest request,
                                             HttpServletResponse response,
                                             @RequestParam(name = "identificadorDocumento", required = true) String identificadorDocumento,
                                             @CurrentUser UserPrincipal userPrincipal) throws Exception {
        comprobantesService.updateComprobantesByEstadoAnticipo(identificadorDocumento);
    }


    @RequestMapping(value = {"/guia-remision"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> searchGuiaRemision(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            @CurrentUser UserPrincipal userPrincipal) throws Exception {
        return new ResponseEntity<Object>(comprobantesService.getGuiasEstadoByFiltersQuery(userPrincipal, filtroDesde,
                filtroHasta, filtroSerie, filtroNumero, pageNumber, perPage), HttpStatus.OK);
    }

    /*@GetMapping("/comprobantes")
    public ResponseEntity<?> comprobantes(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            @RequestParam(name = "estadoSunat", required = false) Integer estadoSunat,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getComprobantesEstadoByFiltersQuery(userPrincipal, filtroDesde,
                filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat), HttpStatus.OK);
    }*/
    
    @GetMapping("/guiaById")
    public ResponseEntity<?> guiaById(
            @RequestParam(name = "id", required = true) Long id,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getGuiaById(id), HttpStatus.OK);
    }

    @GetMapping("/otherVoucherById")
    public ResponseEntity<?> otherVoucherById(
            @RequestParam(name = "id", required = true) Long id,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getOtherVoucherById(id), HttpStatus.OK);
    }



}
