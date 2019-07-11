package io.choerodon.devops.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.devops.api.vo.iam.entity.DevopsProjectVO;
import io.choerodon.devops.api.vo.iam.entity.UserAttrE;
import io.choerodon.devops.app.eventhandler.payload.OrganizationEventPayload;
import io.choerodon.devops.app.service.GitlabUserService;
import io.choerodon.devops.app.service.OrganizationService;
import io.choerodon.devops.domain.application.repository.UserAttrRepository;
import io.choerodon.devops.infra.enums.Visibility;
import io.choerodon.devops.infra.util.TypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private static final String TEMPLATE = "template";

    @Autowired
    private GitlabRepository gitlabRepository;
    @Autowired
    private UserAttrRepository userAttrRepository;
    @Autowired
    private GitlabUserService gitlabUserService;

    @Override
    public void create(OrganizationEventPayload organizationEventPayload) {
        UserAttrE userAttrE = userAttrRepository.queryById(organizationEventPayload.getUserId());
        if (userAttrE == null) {
            throw new CommonException("gitlab user not related to iam user");
        }
        DevopsProjectVO devopsProjectENew = new DevopsProjectVO();
        devopsProjectENew.initName(organizationEventPayload.getCode() + "_" + TEMPLATE);
        devopsProjectENew.initPath(organizationEventPayload.getCode() + "_" + TEMPLATE);
        devopsProjectENew.initVisibility(Visibility.PUBLIC);
        gitlabRepository.createGroup(devopsProjectENew, TypeUtil.objToInteger(userAttrE.getGitlabUserId()));
    }
}
