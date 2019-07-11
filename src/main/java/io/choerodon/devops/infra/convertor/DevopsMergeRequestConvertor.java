package io.choerodon.devops.infra.convertor;

import java.util.Date;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.devops.api.vo.DevopsMergeRequestDTO;
import io.choerodon.devops.api.vo.iam.entity.DevopsMergeRequestE;
import io.choerodon.devops.infra.dto.DevopsMergeRequestDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

<<<<<<< HEAD:src/main/java/io/choerodon/devops/infra/convertor/DevopsMergeRequestConvertor.java
=======
import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.devops.api.vo.DevopsMergeRequestDTO;
import io.choerodon.devops.api.vo.iam.entity.DevopsMergeRequestE;
import io.choerodon.devops.infra.dataobject.DevopsMergeRequestDO;
>>>>>>> [IMP] 修改AppControler重构:src/main/java/io/choerodon/devops/domain/application/convertor/DevopsMergeRequestConvertor.java

@Component
public class DevopsMergeRequestConvertor implements ConvertorI<DevopsMergeRequestE, DevopsMergeRequestDO, DevopsMergeRequestDTO> {

    @Override
    public DevopsMergeRequestDTO entityToDto(DevopsMergeRequestE entity) {
        DevopsMergeRequestDTO devopsMergeRequestDTO = new DevopsMergeRequestDTO();


        BeanUtils.copyProperties(entity, devopsMergeRequestDTO);

        return devopsMergeRequestDTO;
    }


    @Override
    public DevopsMergeRequestE dtoToEntity(DevopsMergeRequestDTO devopsMergeRequestDTO) {
        DevopsMergeRequestE devopsMergeRequestE = new DevopsMergeRequestE();
        devopsMergeRequestE.setProjectId(devopsMergeRequestDTO.getProject().getId());
        devopsMergeRequestE.setGitlabMergeRequestId(devopsMergeRequestDTO.getObjectAttributes().getIid());
        String sourceBranch = devopsMergeRequestDTO.getObjectAttributes().getSourceBranch();
        devopsMergeRequestE.setSourceBranch(sourceBranch);
        String targetBranch = devopsMergeRequestDTO.getObjectAttributes().getTargetBranch();
        devopsMergeRequestE.setTargetBranch(targetBranch);
        Long authorId = devopsMergeRequestDTO.getObjectAttributes().getAuthorId();
        devopsMergeRequestE.setAuthorId(authorId);
        devopsMergeRequestE.setAssigneeId(devopsMergeRequestDTO.getObjectAttributes().getAssigneeId());
        String state = devopsMergeRequestDTO.getObjectAttributes().getState();
        devopsMergeRequestE.setState(state);
        String title = devopsMergeRequestDTO.getObjectAttributes().getTitle();
        devopsMergeRequestE.setTitle(title);
        Date createTime = devopsMergeRequestDTO.getObjectAttributes().getCreatedAt();
        devopsMergeRequestE.setCreatedAt(createTime);
        Date updateTime = devopsMergeRequestDTO.getObjectAttributes().getUpdatedAt();
        devopsMergeRequestE.setUpdatedAt(updateTime);
        return devopsMergeRequestE;
    }

    @Override
    public DevopsMergeRequestDO entityToDo(DevopsMergeRequestE entity) {
        DevopsMergeRequestDO devopsMergeRequestDO = new DevopsMergeRequestDO();
        BeanUtils.copyProperties(entity, devopsMergeRequestDO);
        return devopsMergeRequestDO;
    }

    @Override
    public DevopsMergeRequestE doToEntity(DevopsMergeRequestDO dataObject) {
        DevopsMergeRequestE devopsMergeRequestE = new DevopsMergeRequestE();
        BeanUtils.copyProperties(dataObject, devopsMergeRequestE);
        return devopsMergeRequestE;
    }
}
