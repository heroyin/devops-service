package io.choerodon.devops.infra.convertor;


import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.devops.api.vo.DevopsGitlabCommitDTO;
import io.choerodon.devops.api.vo.iam.entity.DevopsGitlabCommitE;
import io.choerodon.devops.infra.dto.DevopsGitlabCommitDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

<<<<<<< HEAD:src/main/java/io/choerodon/devops/infra/convertor/DevopsGitlabCommitConvertor.java
=======
import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.devops.api.vo.DevopsGitlabCommitDTO;
import io.choerodon.devops.api.vo.iam.entity.DevopsGitlabCommitE;
import io.choerodon.devops.infra.dataobject.DevopsGitlabCommitDO;
>>>>>>> [IMP] 修改AppControler重构:src/main/java/io/choerodon/devops/domain/application/convertor/DevopsGitlabCommitConvertor.java

@Component
public class DevopsGitlabCommitConvertor implements ConvertorI<DevopsGitlabCommitE, DevopsGitlabCommitDO, DevopsGitlabCommitDTO> {

    @Override
    public DevopsGitlabCommitE doToEntity(DevopsGitlabCommitDO devopsGitlabCommitDO) {
        DevopsGitlabCommitE devopsGitlabCommitE = new DevopsGitlabCommitE();
        BeanUtils.copyProperties(devopsGitlabCommitDO, devopsGitlabCommitE);
        return devopsGitlabCommitE;
    }

    @Override
    public DevopsGitlabCommitDO entityToDo(DevopsGitlabCommitE devopsGitlabCommitE) {
        DevopsGitlabCommitDO devopsGitlabCommitDO = new DevopsGitlabCommitDO();
        BeanUtils.copyProperties(devopsGitlabCommitE, devopsGitlabCommitDO);
        return devopsGitlabCommitDO;
    }

    @Override
    public DevopsGitlabCommitDTO entityToDto(DevopsGitlabCommitE devopsGitlabCommitE) {
        DevopsGitlabCommitDTO devopsGitlabCommitDTO = new DevopsGitlabCommitDTO();
        BeanUtils.copyProperties(devopsGitlabCommitE, devopsGitlabCommitDTO);
        return devopsGitlabCommitDTO;
    }
}
