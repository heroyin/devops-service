package io.choerodon.devops.infra.mapper;

import io.choerodon.devops.domain.application.entity.DevopsEnvMessageE;
import io.choerodon.devops.infra.dataobject.DevopsEnvApplicationDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DevopsEnvApplicationMapper extends Mapper<DevopsEnvApplicationDO> {

    /**
     * 通过环境Id查询所有应用Id
     * @param envId 环境Id
     * @return List 应用Id
     */
    List<Long> queryAppByEnvId(@Param("envId") Long envId);

    /**
     * 通过环境Id查询所有应用Id
     * @param envId 环境Id
     * @param appId 应用Id
     * @return List 环境资源信息
     */
    List<DevopsEnvMessageE> listResourceByEnvAndApp(@Param("envId")Long envId, @Param("appId")Long appId);
}