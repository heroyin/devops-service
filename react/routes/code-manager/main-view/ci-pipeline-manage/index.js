import React from 'react';
import { observer } from 'mobx-react-lite';
import { TabPage, Content, Header, Breadcrumb } from '@choerodon/boot';
import CodeManagerHeader from '../../header';
import CodeManagerToolBar from '../../tool-bar';  
import CodeManagerCiPipelineManage from '../../../ciPipelineManage';

import '../index.less';

const CiPipelineManage = observer(props => <TabPage>
  <CodeManagerToolBar name="CodeManagerCiPipelineManage" key="CodeManagerCiPipelineManage" />
  <CodeManagerHeader />
  <Content className="c7ncd-code-manager-content">
    <CodeManagerCiPipelineManage />
  </Content>
</TabPage>);

export default CiPipelineManage;