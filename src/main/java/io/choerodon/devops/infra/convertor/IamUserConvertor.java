package io.choerodon.devops.infra.convertor;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.devops.api.vo.IamUserDTO;
import io.choerodon.devops.api.vo.iam.entity.iam.UserE;
<<<<<<< HEAD:src/main/java/io/choerodon/devops/infra/convertor/IamUserConvertor.java
import io.choerodon.devops.infra.dto.iam.UserDO;
=======
import io.choerodon.devops.infra.dataobject.iam.UserDO;
>>>>>>> [IMP] 修改AppControler重构:src/main/java/io/choerodon/devops/domain/application/convertor/IamUserConvertor.java
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Created by Zenger on 2018/4/16.
 */
@Service
public class IamUserConvertor implements ConvertorI<UserE, UserDO, IamUserDTO> {

    @Override
    public UserE doToEntity(UserDO dataObject) {
        UserE userE = new UserE();
        BeanUtils.copyProperties(dataObject, userE);
        return userE;
    }

    @Override
    public IamUserDTO entityToDto(UserE userE) {
        IamUserDTO userDTO = new IamUserDTO();
        BeanUtils.copyProperties(userE, userDTO);
        return userDTO;
    }
}
