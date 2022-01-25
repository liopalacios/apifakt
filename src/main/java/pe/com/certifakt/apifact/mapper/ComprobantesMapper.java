package pe.com.certifakt.apifact.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import pe.com.certifakt.apifact.model.ComprobantesEntity;
import pe.com.certifakt.apifact.model.DetallesComprobantesEntity;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;

import java.util.Date;
import java.util.List;

@Mapper
public interface ComprobantesMapper {

    @Select("SELECT * FROM payment_voucher p "
            + "left join branch_offices b on p.oficina_id = b.id ")
    List<ComprobantesEntity> getAll();

    @Select("SELECT p.* FROM payment_voucher p "
            + "left join branch_offices b on p.oficina_id = b.id "
            + "where p.ruc_emisor = #{rucEmisor} and p.fecha_emision_date between #{fechaEmisionDesde} and #{fechaEmisionHasta} "
            + "and (#{tipoComprobante} is null or p.tipo_comprobante like #{tipoComprobante}) and (#{numDocIdentReceptor} is null or p.num_doc_ident_receptor like #{numDocIdentReceptor}) "
            + "and (#{serie} is null or p.serie like #{serie}) and (#{numero} = 0 or p.numero = #{numero}) and (#{idOficina} = 0 or b.id = #{idOficina}) "
            + "and p.estado like #{estadoSunat} "
            + "order by p.fecha_emision_date desc, p.numero desc")
    @Results(value = {
            @Result(property = "id_payment_voucher",column = "id_payment_voucher"),
            @Result(property = "fecha_emision",column = "fecha_emision"),
            @Result(property = "detailsPaymentVouchers",column = "id_payment_voucher",javaType = List.class, many = @Many(select = "findAllItems"))
    })
    public List<ComprobantesEntity> findAllAndEstadoSerchForPages(String rucEmisor, Date fechaEmisionDesde, Date fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor,
                                                           String serie, Integer numero, Integer idOficina, String estadoSunat, Pageable page);
    @Select("Select dp.id_details_payment,dp.descripcion_producto,dp.cod_unid_medida,dp.cantidad,dp.precio_venta_unit,dp.descuento,dp.valor_venta,dp.afectacion_igv from details_payment_voucher dp "
            + "where dp.id_payment_voucher = #{idPaymentVoucher}")
    @Results(value = {
            @Result(property = "id_details_payment",column = "id_details_payment"),
            @Result(property = "descripcion_producto",column = "descripcion_producto")
    })
    List<DetallesComprobantesEntity> findAllItems(String rucEmisor, Integer idPaymentVoucher, Integer idOficina);


}
