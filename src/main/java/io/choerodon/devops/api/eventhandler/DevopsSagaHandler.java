package io.choerodon.devops.api.eventhandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.SagaDefinition;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.notify.NoticeSendDTO;
import io.choerodon.devops.api.dto.ApplicationDeployDTO;
import io.choerodon.devops.api.dto.ApplicationInstanceDTO;
import io.choerodon.devops.api.dto.PipelineWebHookDTO;
import io.choerodon.devops.api.dto.PushWebHookDTO;
import io.choerodon.devops.app.service.ApplicationInstanceService;
import io.choerodon.devops.app.service.ApplicationService;
import io.choerodon.devops.app.service.ApplicationTemplateService;
import io.choerodon.devops.app.service.DevopsEnvironmentService;
import io.choerodon.devops.app.service.DevopsGitService;
import io.choerodon.devops.app.service.DevopsGitlabPipelineService;
import io.choerodon.devops.app.service.PipelineService;
import io.choerodon.devops.domain.application.entity.ApplicationE;
import io.choerodon.devops.domain.application.entity.ApplicationTemplateE;
import io.choerodon.devops.domain.application.entity.DevopsEnvironmentE;
import io.choerodon.devops.domain.application.entity.PipelineStageRecordE;
import io.choerodon.devops.domain.application.entity.PipelineTaskRecordE;
import io.choerodon.devops.domain.application.event.DevOpsAppImportPayload;
import io.choerodon.devops.domain.application.event.DevOpsAppPayload;
import io.choerodon.devops.domain.application.event.DevOpsUserPayload;
import io.choerodon.devops.domain.application.event.GitlabProjectPayload;
import io.choerodon.devops.domain.application.repository.ApplicationRepository;
import io.choerodon.devops.domain.application.repository.ApplicationTemplateRepository;
import io.choerodon.devops.domain.application.repository.DevopsEnvironmentRepository;
import io.choerodon.devops.domain.application.repository.GitlabRepository;
import io.choerodon.devops.domain.application.repository.PipelineAppDeployRepository;
import io.choerodon.devops.domain.application.repository.PipelineRecordRepository;
import io.choerodon.devops.domain.application.repository.PipelineStageRecordRepository;
import io.choerodon.devops.domain.application.repository.PipelineTaskRecordRepository;
import io.choerodon.devops.domain.application.repository.WorkFlowRepository;
import io.choerodon.devops.domain.service.UpdateUserPermissionService;
import io.choerodon.devops.domain.service.impl.UpdateAppUserPermissionServiceImpl;
import io.choerodon.devops.infra.common.util.GitUserNameUtil;
import io.choerodon.devops.infra.common.util.enums.PipelineNoticeType;
import io.choerodon.devops.infra.common.util.enums.WorkFlowStatus;
import io.choerodon.devops.infra.feign.NotifyClient;


/**
 * Creator: Runge
 * Date: 2018/7/27
 * Time: 10:06
 * Description: Saga msg by DevOps self
 */
@Component
public class DevopsSagaHandler {
    private static final String TEMPLATE = "template";
    private static final String APPLICATION = "application";
    private static final String STATUS_FIN = "finished";
    private static final String STATUS_FAILED = "failed";
    private static final Logger LOGGER = LoggerFactory.getLogger(DevopsSagaHandler.class);

    private final Gson gson = new Gson();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final DevopsEnvironmentService devopsEnvironmentService;
    private final DevopsGitService devopsGitService;
    private final ApplicationTemplateService applicationTemplateService;
    private final ApplicationService applicationService;
    private final DevopsGitlabPipelineService devopsGitlabPipelineService;
    private final ApplicationRepository applicationRepository;
    private final ApplicationTemplateRepository applicationTemplateRepository;
    private final DevopsEnvironmentRepository devopsEnvironmentRepository;
    private final ApplicationInstanceService applicationInstanceService;
    private final GitlabRepository gitlabRepository;
    private final PipelineTaskRecordRepository taskRecordRepository;
    private final PipelineAppDeployRepository appDeployRepository;
    private final PipelineStageRecordRepository stageRecordRepository;
    private final PipelineService pipelineService;
    private PipelineRecordRepository pipelineRecordRepository;
    private WorkFlowRepository workFlowRepository;
    private NotifyClient notifyClient;

    @Autowired
    public DevopsSagaHandler(DevopsEnvironmentService devopsEnvironmentService,
                             DevopsGitService devopsGitService,
                             ApplicationTemplateService applicationTemplateService,
                             ApplicationService applicationService,
                             DevopsGitlabPipelineService devopsGitlabPipelineService,
                             ApplicationRepository applicationRepository,
                             ApplicationTemplateRepository applicationTemplateRepository,
                             DevopsEnvironmentRepository devopsEnvironmentRepository,
                             PipelineTaskRecordRepository taskRecordRepository,
                             PipelineAppDeployRepository appDeployRepository,
                             PipelineStageRecordRepository stageRecordRepository,
                             GitlabRepository gitlabRepository,
                             PipelineService pipelineService,
                             PipelineRecordRepository pipelineRecordRepository,
                             WorkFlowRepository workFlowRepository,
                             NotifyClient notifyClient,
                             ApplicationInstanceService applicationInstanceService) {
        this.devopsEnvironmentService = devopsEnvironmentService;
        this.devopsGitService = devopsGitService;
        this.applicationTemplateService = applicationTemplateService;
        this.applicationService = applicationService;
        this.devopsGitlabPipelineService = devopsGitlabPipelineService;
        this.applicationRepository = applicationRepository;
        this.applicationTemplateRepository = applicationTemplateRepository;
        this.devopsEnvironmentRepository = devopsEnvironmentRepository;
        this.taskRecordRepository = taskRecordRepository;
        this.appDeployRepository = appDeployRepository;
        this.gitlabRepository = gitlabRepository;
        this.pipelineService = pipelineService;
        this.stageRecordRepository = stageRecordRepository;
        this.applicationInstanceService = applicationInstanceService;
        this.pipelineRecordRepository = pipelineRecordRepository;
        this.workFlowRepository = workFlowRepository;
        this.notifyClient = notifyClient;
    }

    /**
     * devops创建环境
     */
    @SagaTask(code = "devopsCreateEnv",
            description = "devops创建环境",
            sagaCode = "devops-create-env",
            maxRetryCount = 3,
            seq = 1)
    public String devopsCreateEnv(String data) {
        GitlabProjectPayload gitlabProjectPayload = gson.fromJson(data, GitlabProjectPayload.class);
        try {
            devopsEnvironmentService.handleCreateEnvSaga(gitlabProjectPayload);
        } catch (Exception e) {
            devopsEnvironmentService.setEnvErrStatus(data, gitlabProjectPayload.getIamProjectId());
            throw e;
        }
        DevopsEnvironmentE devopsEnvironmentE = devopsEnvironmentRepository
                .queryByClusterIdAndCode(gitlabProjectPayload.getClusterId(), gitlabProjectPayload.getPath());
        if (devopsEnvironmentE.getFailed() != null && devopsEnvironmentE.getFailed()) {
            devopsEnvironmentE.initFailed(false);
            devopsEnvironmentRepository.update(devopsEnvironmentE);
        }
        return data;
    }

    /**
     * 环境创建失败
     */
    @SagaTask(code = "devopsCreateEnvError",
            description = "set  DevOps app status error",
            sagaCode = "devops-set-env-err",
            maxRetryCount = 3,
            seq = 1)
    public String setEnvErr(String data) {
        GitlabProjectPayload gitlabProjectPayload = gson.fromJson(data, GitlabProjectPayload.class);
        DevopsEnvironmentE devopsEnvironmentE = devopsEnvironmentRepository
                .queryByClusterIdAndCode(gitlabProjectPayload.getClusterId(), gitlabProjectPayload.getPath());
        devopsEnvironmentE.initFailed(true);
        devopsEnvironmentRepository.update(devopsEnvironmentE);
        return data;
    }

    /**
     * GitOps 事件处理
     */
    @SagaTask(code = "devopsGitOps",
            description = "gitops",
            sagaCode = "devops-sync-gitops",
            concurrentLimitNum = 1,
            maxRetryCount = 3,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.TYPE_AND_ID,
            seq = 1)
    public String gitops(String data) {
        PushWebHookDTO pushWebHookDTO = null;
        try {
            pushWebHookDTO = objectMapper.readValue(data, PushWebHookDTO.class);
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        devopsGitService.fileResourceSync(pushWebHookDTO);
        return data;
    }

    /**
     * GitOps 事件处理
     */
    @SagaTask(code = "devopsOperationGitlabProject",
            description = "devops create GitLab project",
            sagaCode = "devops-create-gitlab-project",
            maxRetryCount = 3,
            seq = 1)
    public String createApp(String data) {
        DevOpsAppPayload devOpsAppPayload = gson.fromJson(data, DevOpsAppPayload.class);
        if (devOpsAppPayload.getType().equals(APPLICATION)) {
            try {
                applicationService.operationApplication(devOpsAppPayload);
            } catch (Exception e) {
                applicationService.setAppErrStatus(data, devOpsAppPayload.getIamProjectId());
                throw e;
            }
        }
        return data;
    }

    /**
     * GitOps 事件处理
     */
    @SagaTask(code = "devopsCreateGitlabProject",
            description = "Devops从外部代码平台导入到gitlab项目",
            sagaCode = "devops-import-gitlab-project",
            maxRetryCount = 3,
            seq = 1)
    public String importApp(String data) {
        DevOpsAppImportPayload devOpsAppImportPayload = gson.fromJson(data, DevOpsAppImportPayload.class);
        if (devOpsAppImportPayload.getType().equals(APPLICATION)) {
            try {
                applicationService.operationApplicationImport(devOpsAppImportPayload);
            } catch (Exception e) {
                applicationService.setAppErrStatus(data, devOpsAppImportPayload.getIamProjectId());
                throw e;
            }
            ApplicationE applicationE = applicationRepository.query(devOpsAppImportPayload.getAppId());
            if (applicationE.getFailed() != null && applicationE.getFailed()) {
                applicationE.setFailed(false);
                if (1 != applicationRepository.update(applicationE)) {
                    LOGGER.error("update application set create success status error");
                }
            }
//            gitlabRepository.batchAddVariable(applicationE.getGitlabProjectE().getId(), TypeUtil.objToInteger(devOpsAppImportPayload.getGitlabUserId()),
//                    applicationService.setVariableDTO(applicationE.getHarborConfigE().getId(),applicationE.getChartConfigE().getId()));
        }
        return data;
    }

    /**
     * GitOps 用户权限分配处理
     */
    @SagaTask(code = "devopsUpdateGitlabUsers",
            description = "devops update gitlab users",
            sagaCode = "devops-update-gitlab-users",
            maxRetryCount = 3,
            seq = 1)
    public String updateGitlabUser(String data) {
        DevOpsUserPayload devOpsUserPayload = gson.fromJson(data, DevOpsUserPayload.class);
        try {
            UpdateUserPermissionService updateUserPermissionService = new UpdateAppUserPermissionServiceImpl();
            updateUserPermissionService
                    .updateUserPermission(devOpsUserPayload.getIamProjectId(), devOpsUserPayload.getAppId(),
                            devOpsUserPayload.getIamUserIds(), devOpsUserPayload.getOption());
        } catch (Exception e) {
            LOGGER.error("update gitlab users {} error", devOpsUserPayload.getIamUserIds());
            throw e;
        }
        return data;
    }

    /**
     * GitOps 应用创建失败处理
     */
    @SagaTask(code = "devopsCreateGitlabProjectErr",
            description = "set  DevOps app status error",
            sagaCode = "devops-create-app-fail",
            maxRetryCount = 3,
            seq = 1)
    public String setAppErr(String data) {
        DevOpsAppPayload devOpsAppPayload = gson.fromJson(data, DevOpsAppPayload.class);
        ApplicationE applicationE = applicationRepository.query(devOpsAppPayload.getAppId());
        applicationE.setFailed(true);
        if (1 != applicationRepository.update(applicationE)) {
            LOGGER.error("update application {} set create failed status error", applicationE.getCode());
        }
        return data;
    }

    /**
     * GitOps 应用模板创建失败处理
     */
    @SagaTask(code = "devopsCreateGitlabProjectTemplateErr",
            description = "set  DevOps app template status error",
            sagaCode = "devops-set-appTemplate-err",
            maxRetryCount = 3,
            seq = 1)
    public String setAppTemplateErr(String data) {
        DevOpsAppPayload devOpsAppPayload = gson.fromJson(data, DevOpsAppPayload.class);
        ApplicationTemplateE applicationTemplateE = applicationTemplateRepository.queryByCode(
                devOpsAppPayload.getOrganizationId(), devOpsAppPayload.getPath());
        applicationTemplateE.setFailed(true);
        applicationTemplateRepository.update(applicationTemplateE);
        return data;
    }

    /**
     * GitOps 模板事件处理
     */
    @SagaTask(code = "devopsOperationGitlabTemplateProject",
            description = "devops create GitLab template project",
            sagaCode = "devops-create-gitlab-template-project",
            maxRetryCount = 3,
            seq = 1)
    public String createTemplate(String data) {
        GitlabProjectPayload gitlabProjectEventDTO = gson.fromJson(data, GitlabProjectPayload.class);
        if (gitlabProjectEventDTO.getType().equals(TEMPLATE)) {
            try {
                applicationTemplateService.operationApplicationTemplate(gitlabProjectEventDTO);
            } catch (Exception e) {
                applicationTemplateService.setAppTemplateErrStatus(data, gitlabProjectEventDTO.getOrganizationId());
                throw e;
            }
            ApplicationTemplateE applicationTemplateE = applicationTemplateRepository.queryByCode(
                    gitlabProjectEventDTO.getOrganizationId(), gitlabProjectEventDTO.getPath());
            if (applicationTemplateE.getFailed() != null && applicationTemplateE.getFailed()) {
                applicationTemplateE.setFailed(false);
                applicationTemplateRepository.update(applicationTemplateE);
            }
        }
        return data;
    }

    /**
     * GitOps 事件处理
     */
    @SagaTask(code = "devopsGitlabPipeline",
            description = "gitlab-pipeline",
            sagaCode = "devops-gitlab-pipeline",
            maxRetryCount = 3,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.TYPE_AND_ID,
            seq = 1)
    public String gitlabPipeline(String data) {
        PipelineWebHookDTO pipelineWebHookDTO = null;
        try {
            pipelineWebHookDTO = objectMapper.readValue(data, PipelineWebHookDTO.class);
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        devopsGitlabPipelineService.handleCreate(pipelineWebHookDTO);
        return data;
    }

    @SagaTask(code = "devops-pipeline-create-instance",
            description = "devops pipeline instance",
            sagaCode = "devops-pipeline-auto-deploy-instance",
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.TYPE_AND_ID,
            maxRetryCount = 3,
            seq = 1)
    public void pipelineAutoDeployInstance(String data) {
        ApplicationDeployDTO applicationDeployDTO = gson.fromJson(data, ApplicationDeployDTO.class);
        Long taskRecordId = applicationDeployDTO.getRecordId();
        Long stageRecordId = taskRecordRepository.queryById(taskRecordId).getStageRecordId();
        PipelineStageRecordE stageRecordE = stageRecordRepository.queryById(stageRecordId);
        Long pipelineRecordId = stageRecordE.getPipelineRecordId();
        try {
            ApplicationInstanceDTO applicationInstanceDTO = applicationInstanceService.createOrUpdate(applicationDeployDTO);
            if (!pipelineRecordRepository.queryById(pipelineRecordId).getStatus().equals(WorkFlowStatus.FAILED.toValue()) || stageRecordE.getIsParallel() == 1) {
                PipelineTaskRecordE pipelineTaskRecordE = new PipelineTaskRecordE(applicationInstanceDTO.getId(), WorkFlowStatus.SUCCESS.toString());
                pipelineTaskRecordE.setId(applicationDeployDTO.getRecordId());
                taskRecordRepository.createOrUpdate(pipelineTaskRecordE);
                LOGGER.info("create pipeline auto deploy instance success");
            }
        } catch (Exception e) {
            PipelineTaskRecordE pipelineTaskRecordE = new PipelineTaskRecordE();
            pipelineTaskRecordE.setId(applicationDeployDTO.getRecordId());
            pipelineTaskRecordE.setStatus(WorkFlowStatus.FAILED.toValue());
            taskRecordRepository.createOrUpdate(pipelineTaskRecordE);
            pipelineService.updateStatus(pipelineRecordId, stageRecordId, WorkFlowStatus.FAILED.toValue(), e.getMessage());
            NoticeSendDTO.User user = new NoticeSendDTO.User();
            user.setEmail(GitUserNameUtil.getEmail());
            user.setId(GitUserNameUtil.getUserId().longValue());
            pipelineService.sendSiteMessage(pipelineRecordId, PipelineNoticeType.PIPELINEFAILED.toValue(), Collections.singletonList(user), new HashMap<>());
            LOGGER.error("error create pipeline auto deploy instance {}", e);
        }
    }

}
