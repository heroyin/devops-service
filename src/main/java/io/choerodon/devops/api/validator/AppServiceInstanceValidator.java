package io.choerodon.devops.api.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.devops.api.vo.AppServiceDeployVO;
import io.choerodon.devops.api.vo.DevopsServiceReqVO;

/**
 * Created by n!Ck
 * Date: 2018/11/20
 * Time: 9:51
 * Description:
 */
@Component
public class AppServiceInstanceValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppServiceInstanceValidator.class);
    //appServiceInstance name
    private static final String NAME_PATTERN = "[a-z]([-a-z0-9]*[a-z0-9])?";
    @Autowired
    private Validator validator;

    private AppServiceInstanceValidator() {
    }


    public static void checkName(String name) {
        if (!Pattern.matches(NAME_PATTERN, name)) {
            throw new CommonException("error.app.instance.name.notMatch");
        }
    }

    /**
     * 校验批量部署的请求参数
     *
     * @param appServiceDeployVOS 批量部署的信息
     */
    public void validateBatchDeployment(List<AppServiceDeployVO> appServiceDeployVOS) {
        if (appServiceDeployVOS.isEmpty()) {
            throw new CommonException("error.request.instance.empty");
        }
        List<String> instanceCodes = new ArrayList<>();
        List<String> serviceNames = new ArrayList<>();
        List<String> ingressNames = new ArrayList<>();
        // 校验实例
        for (AppServiceDeployVO appServiceDeployVO : appServiceDeployVOS) {
            Set<ConstraintViolation<AppServiceDeployVO>> set = validator.validate(appServiceDeployVO);
            if (!CollectionUtils.isEmpty(set)) {
                for (ConstraintViolation<AppServiceDeployVO> cv : set) {
                    LOGGER.info("App-service-validator: invalid instance. the message is {}", cv.getMessageTemplate());
                    throw new CommonException(cv.getMessageTemplate());
                }
            }
            if (instanceCodes.contains(appServiceDeployVO.getInstanceName())) {
                throw new CommonException("error.app.service.name.duplicated.in.list", appServiceDeployVO.getInstanceName());
            }
            instanceCodes.add(appServiceDeployVO.getInstanceName());

            // 校验网络
            if (appServiceDeployVO.getDevopsServiceReqVO() != null) {
                Set<ConstraintViolation<DevopsServiceReqVO>> serviceSet = validator.validate(appServiceDeployVO.getDevopsServiceReqVO());
                if (!CollectionUtils.isEmpty(serviceSet)) {
                    for (ConstraintViolation<DevopsServiceReqVO> cv : serviceSet) {
                        LOGGER.info("App-service-validator: invalid service. the message is {}", cv.getMessageTemplate());
                        throw new CommonException(cv.getMessageTemplate());
                    }
                }
                if (serviceNames.contains(appServiceDeployVO.getDevopsServiceReqVO().getName())) {
                    throw new CommonException("error.service.name.duplicated.in.list", appServiceDeployVO.getDevopsServiceReqVO().getName());
                }
                serviceNames.add(appServiceDeployVO.getDevopsServiceReqVO().getName());
            }

            if (appServiceDeployVO.getDevopsIngressVO() != null) {
                if (ingressNames.contains(appServiceDeployVO.getDevopsIngressVO().getName())) {
                    throw new CommonException("error.ingress.name.duplicated.in.list", appServiceDeployVO.getDevopsIngressVO().getName());
                }
                ingressNames.add(appServiceDeployVO.getDevopsIngressVO().getName());
                DevopsIngressValidator.checkVOForBatchDeployment(appServiceDeployVO.getDevopsIngressVO());
            }
        }
    }
}
