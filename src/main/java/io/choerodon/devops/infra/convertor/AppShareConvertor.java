package io.choerodon.devops.infra.convertor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.devops.api.vo.ApplicationReleasingDTO;
import io.choerodon.devops.api.vo.iam.entity.DevopsAppShareE;
<<<<<<< HEAD:src/main/java/io/choerodon/devops/infra/convertor/AppShareConvertor.java

import io.choerodon.devops.infra.dto.DevopsAppShareDO;
=======
import io.choerodon.devops.infra.dataobject.DevopsAppShareDO;
>>>>>>> [IMP] 修改AppControler重构:src/main/java/io/choerodon/devops/domain/application/convertor/AppShareConvertor.java

/**
 * Created by ernst on 2018/5/12.
 */
@Component
public class AppShareConvertor implements ConvertorI<DevopsAppShareE, DevopsAppShareDO, ApplicationReleasingDTO> {

    @Override
    public DevopsAppShareDO entityToDo(DevopsAppShareE applicationMarketE) {
        DevopsAppShareDO devopsAppMarketDO = new DevopsAppShareDO();
        BeanUtils.copyProperties(applicationMarketE, devopsAppMarketDO);
        if (applicationMarketE.getApplicationE() != null) {
            devopsAppMarketDO.setAppId(applicationMarketE.getApplicationE().getId());
        }
        return devopsAppMarketDO;
    }

    @Override
    public ApplicationReleasingDTO entityToDto(DevopsAppShareE entity) {
        ApplicationReleasingDTO applicationReleasingDTO = new ApplicationReleasingDTO();
        BeanUtils.copyProperties(entity, applicationReleasingDTO);
        applicationReleasingDTO.setAppId(entity.getApplicationE().getId());
        applicationReleasingDTO.setLastUpdatedDate(entity.getMarketUpdatedDate());
        return applicationReleasingDTO;
    }

    @Override
    public DevopsAppShareE doToEntity(DevopsAppShareDO devopsAppMarketDO) {
        DevopsAppShareE applicationMarketE = new DevopsAppShareE();
        BeanUtils.copyProperties(devopsAppMarketDO, applicationMarketE);
        applicationMarketE.initApplicationEById(devopsAppMarketDO.getAppId());
        return applicationMarketE;
    }

    @Override
    public DevopsAppShareDO dtoToDo(ApplicationReleasingDTO dto) {
        DevopsAppShareDO appMarketDO = new DevopsAppShareDO();
        BeanUtils.copyProperties(dto, appMarketDO);
        return appMarketDO;
    }
}
