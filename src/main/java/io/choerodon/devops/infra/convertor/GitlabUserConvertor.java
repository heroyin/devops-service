package io.choerodon.devops.infra.convertor;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.devops.api.vo.iam.entity.gitlab.GitlabUserE;
import io.choerodon.devops.infra.dto.gitlab.GitLabUserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

<<<<<<< HEAD
<<<<<<< HEAD:src/main/java/io/choerodon/devops/infra/convertor/GitlabUserConvertor.java
=======
import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.devops.api.vo.iam.entity.gitlab.GitlabUserE;
=======
<<<<<<< HEAD:src/main/java/io/choerodon/devops/domain/application/convertor/GitlabUserConvertor.java
>>>>>>> [IMP]修改后端结构
import io.choerodon.devops.infra.dataobject.gitlab.UserDO;

>>>>>>> [IMP] 修改AppControler重构:src/main/java/io/choerodon/devops/domain/application/convertor/GitlabUserConvertor.java
/**
 * Created by Zenger on 2018/3/29.
 */
@Component
public class GitlabUserConvertor implements ConvertorI<GitlabUserE, GitLabUserDTO, Object> {

    @Override
    public GitlabUserE doToEntity(GitLabUserDTO userDTO) {
        GitlabUserE gitlabUserE = new GitlabUserE();
        BeanUtils.copyProperties(userDTO, gitlabUserE);
        return gitlabUserE;
    }

    @Override
    public GitLabUserDTO entityToDo(GitlabUserE gitlabUserE) {
        GitLabUserDTO userDTO = new GitLabUserDTO();
        userDTO.setId(gitlabUserE.getId());
        userDTO.setUsername(gitlabUserE.getUsername());
        return userDTO;
    }
}
