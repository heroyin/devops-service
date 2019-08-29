import React, { Fragment, useState, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import { Tooltip, Icon, Progress, Modal } from 'choerodon-ui/pro';
import { Spin } from 'choerodon-ui';
import _ from 'lodash';
import ReactCodeMirror from 'react-codemirror';
import Operation from './op-record';
import { useResourceStore } from '../../../../stores';
import { useInstanceStore } from '../stores';


import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/base16-dark.css';
import './index.less';

const logKey = Modal.key();
const logOptions = {
  theme: 'base16-dark',
  mode: 'textile',
  readOnly: true,
  lineNumbers: true,
  lineWrapping: true,
};

const Cases = observer(() => {
  const {
    prefixCls,
    intlPrefix,
  } = useResourceStore();
  const {
    intl: { formatMessage },
    casesDs,
  } = useInstanceStore();
  const [podTime, setPodTime] = useState('');
  const [expandKeys, setExpandKeys] = useState([]);
  const loading = casesDs.status === 'loading';

  const changeEvent = useCallback((data) => {
    setPodTime(data);
  }, []);

  /**
   * 展开更多
   * @param item
   */
  function showMore(item) {
    const data = [...expandKeys];
    const flag = _.includes(data, item);
    if (flag) {
      const index = _.indexOf(data, item);
      data.splice(index, 1);
    } else {
      data.push(item);
    }
    setExpandKeys(data);
  }

  function istEventDom(data) {
    const podEventVO = data.get('podEventVO');

    return _.map(podEventVO, ({ name, log, event, jobPodStatus }, index) => {
      const flag = _.includes(expandKeys, `${index}-${name}`);
      const eventData = { index, jobPodStatus, name, log, flag, event, intlPrefix, formatMessage, showMore };
      return <InstanceEvent {...eventData} />;
    });
  }


  function getContent() {
    const record = casesDs.data;
    if (record.length) {
      const currentPod = casesDs.find((data) => {
        const time = data.get('createTime');
        return time === podTime;
      });
      const data = currentPod || casesDs.get(0);
      return (
        <Fragment>
          <Operation
            handleClick={changeEvent}
            active={podTime}
          />
          <div className="cases-operation-content">
            {istEventDom(data)}
          </div>
        </Fragment>
      );
    }
    return <div className={`${prefixCls}-event-empty`}>
      <Icon type="info" className={`${prefixCls}-event-empty-icon`} />
      <span className={`${prefixCls}-event-empty-text`}>
        {formatMessage({ id: `${intlPrefix}.instance.cases.empty` })}
      </span>
    </div>;
  }

  return (
    <div className={`${prefixCls}-instance-cases`}>
      <Spin spinning={loading}>
        { getContent()}
      </Spin>
    </div>
  );
});

const InstanceEvent = ({ index, jobPodStatus, name, log, flag, event, intlPrefix, formatMessage, showMore }) => {
  function openLogDetail() {
    Modal.open({
      key: logKey,
      title: formatMessage({ id: 'container.log.header.title' }),
      drawer: true,
      okText: formatMessage({ id: 'close' }),
      okCancel: false,
      style: {
        width: 1000,
      },
      children: <div className="c7n-term-wrap"><ReactCodeMirror
        value={log}
        options={logOptions}
        className="c7n-log-editor"
      /></div>,
    });
  }
  return (
    <div key={index} className="operation-content-step">
      <div className="content-step-title">
        {jobPodStatus === 'running' ? (
          <Progress strokeWidth={10} width={13} type="loading" />
        ) : (
          <Icon
            type="wait_circle"
            className={`content-step-icon-${jobPodStatus}`}
          />
        )}
        <span className="content-step-title-text">{name}</span>
        {log && (
        <Tooltip  
          title={formatMessage({ id: `${intlPrefix}.instance.cases.log` })}
          placement="bottom"
        >
          <Icon type="find_in_page" onClick={openLogDetail} />
        </Tooltip>
        )}
      </div>
      <div className="content-step-des">
        <pre className={!flag ? 'content-step-des-hidden' : ''}>
          {event}
        </pre>
        {event && event.split('\n').length > 4 && (
        <a onClick={() => showMore(`${index}-${name}`)}>
          <FormattedMessage id={flag ? 'shrink' : 'expand'} />
        </a>
        )}
      </div>
    </div>);
};

export default Cases;
