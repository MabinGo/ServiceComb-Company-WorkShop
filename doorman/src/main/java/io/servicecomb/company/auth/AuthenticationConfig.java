/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicecomb.company.auth;

import com.google.common.collect.Lists;
import com.huawei.apm.thrift.TDiscoveryInfo;
import com.huawei.paas.cse.tracing.apm.MetaDataMgr;
import com.huawei.paas.cse.tracing.apm.TracingUtil;
import com.huawei.paas.cse.tracing.apm.sender.DataSender;
import com.huawei.paas.cse.tracing.apm.sender.FileDataSender;
import com.huawei.paas.cse.tracing.apm.sender.NamedPipeDataSender;
import com.huawei.paas.cse.tracing.apm.span.IDGenerator;
import io.servicecomb.company.auth.domain.UserRepository;
import io.servicecomb.serviceregistry.RegistryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AuthenticationConfig {

  private static final int SECONDS_OF_A_DAY = 24 * 60 * 60;

  @Bean
  @Autowired
  AuthenticationService authenticationService(
      TokenStore tokenStore,
      UserRepository repository,
      TDiscoveryInfo discoveryInfo,
      DataSender dataSender) {

    return new AuthenticationServiceImpl(tokenStore, repository, discoveryInfo, dataSender);
  }

  @Bean
  TokenStore tokenStore(@Value("${company.auth.secret:someSecretKey}") String secretKey) {
    return new JwtTokenStore(secretKey, SECONDS_OF_A_DAY);
  }

  @Autowired
  @Bean
  TDiscoveryInfo createTdiscoveryInfo(@Value("${spring.datasource.url}") String url){

    String tierName = "company";
    String ips = url.substring(url.indexOf("//") + 1, url.lastIndexOf(":"));
    TDiscoveryInfo info = new TDiscoveryInfo();
    info.setHostname(RegistryUtils.getPublishHostName());
    info.setIp(RegistryUtils.getPublishAddress());
    String resourceId = IDGenerator.generateIDUsingMD5(MetaDataMgr.projectId + "|" + MetaDataMgr.namespace + "|" + MetaDataMgr.tier + "|" + "MYSQL");
    info.setAgentId(resourceId);
    info.setAppName(MetaDataMgr.applicationName);
    info.setClusterKey("");
    info.setServiceType("MYSQL");
    info.setDisplayName(TracingUtil.INSTANCE.getTracingName());
    info.setInstanceName(TracingUtil.INSTANCE.getTracingName());
    info.setProjectId(MetaDataMgr.projectId);
    info.setPodId(MetaDataMgr.podId);
    info.setCollectorId(resourceId);
    info.setAppId(MetaDataMgr.appId);
    info.setPorts(Lists.newArrayList(3306));
    info.setIps(Lists.newArrayList(ips));
    info.setTier(tierName);
    info.setNamespaceName(MetaDataMgr.namespace);
    info.setCreated(System.currentTimeMillis());
    info.setUpdated(System.currentTimeMillis());
    info.setDeleted(0L);
    return info;
  }



  @Bean
  DataSender createDataSender(){
    if(com.huawei.paas.cse.tracing.apm.Configuration.INSTANCE.apmDataSendMode() == 0){
      return new NamedPipeDataSender(com.huawei.paas.cse.tracing.apm.Configuration.INSTANCE.getDiscoveryNamedPipePath(), 1);
    }else{
      return FileDataSender.DISC_DATA_SENDER;
    }
  }

}
