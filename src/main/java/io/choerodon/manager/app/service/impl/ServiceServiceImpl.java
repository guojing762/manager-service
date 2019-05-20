package io.choerodon.manager.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.manager.api.dto.ServiceDTO;
import io.choerodon.manager.api.dto.ServiceManagerDTO;
import io.choerodon.manager.app.service.ServiceService;
import io.choerodon.manager.domain.repository.ServiceRepository;
import io.choerodon.manager.infra.common.utils.ManualPageHelper;
import io.choerodon.manager.infra.dataobject.Sort;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author wuguokai
 */
@Service
public class ServiceServiceImpl implements ServiceService {

    private ServiceRepository serviceRepository;

    private DiscoveryClient discoveryClient;

    public ServiceServiceImpl(ServiceRepository serviceRepository, DiscoveryClient discoveryClient) {
        this.serviceRepository = serviceRepository;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public List<ServiceDTO> list(String param) {
        if (StringUtils.isEmpty(param)) {
            return ConvertHelper.convertList(serviceRepository.getAllService(), ServiceDTO.class);
        }
        return ConvertHelper.convertList(serviceRepository.selectServicesByFilter(param), ServiceDTO.class);
    }

    @Override
    public PageInfo<ServiceManagerDTO> pageManager(String serviceName, String params, int page, int size) {
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "name");
        Sort sort = new Sort(order);
        List<ServiceManagerDTO> serviceManagerDTOS = new ArrayList<>();
        discoveryClient.getServices().forEach(t -> serviceManagerDTOS
                .add(new ServiceManagerDTO(t, discoveryClient.getInstances(t).size())));

        Map<String, Object> map = new HashMap<>();
        map.put("serviceName", serviceName);
        map.put("params", params);
        return ManualPageHelper.postPage(serviceManagerDTOS, page,size,sort, map);
    }
}
