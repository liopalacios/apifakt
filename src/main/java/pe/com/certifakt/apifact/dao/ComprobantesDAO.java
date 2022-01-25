package pe.com.certifakt.apifact.dao;

import pe.com.certifakt.apifact.bean.SearchCriteria;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;

import java.util.List;
import java.util.Map;

public interface ComprobantesDAO {


    Map<String, Object> buscarComprobantes(List<SearchCriteria> searchCriteriaList, Integer pageNumber, Integer perPage);

    List<PaymentVoucherEntity> buscarComprobantes(List<SearchCriteria> searchCriteriaList);

    Map<String, Object> buscarAllComprobantes(List<SearchCriteria> searchCriteriaList);
}
