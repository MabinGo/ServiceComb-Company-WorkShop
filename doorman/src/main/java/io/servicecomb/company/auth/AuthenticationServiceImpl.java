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

import com.huawei.apm.thrift.TDiscoveryInfo;
import com.huawei.paas.cse.tracing.apm.MetaDataMgr;
import com.huawei.paas.cse.tracing.apm.kpi.KpiManager;
import com.huawei.paas.cse.tracing.apm.kpi.KpiMessage;
import com.huawei.paas.cse.tracing.apm.sender.DataSender;
import com.huawei.paas.cse.tracing.apm.seralize.TThriftSerializer;
import io.servicecomb.company.auth.domain.User;
import io.servicecomb.company.auth.domain.UserRepository;

class AuthenticationServiceImpl implements AuthenticationService {

  private final TokenStore tokenStore;
  private final UserRepository userRepository;
  private final TDiscoveryInfo tDiscoveryInfo;
  private final DataSender sender;
  private long startTime = System.currentTimeMillis();
  AuthenticationServiceImpl(
      TokenStore tokenStore,
      UserRepository userRepository,
      TDiscoveryInfo discoveryInfo,
      DataSender sender) {
    this.tokenStore = tokenStore;
    this.userRepository = userRepository;
    this.tDiscoveryInfo = discoveryInfo;
    this.sender = sender;
    try{
      sender.send(new TThriftSerializer().serialize(tDiscoveryInfo));
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }


  @Override
  public String authenticate(String username, String password) {
    if(System.currentTimeMillis() - startTime > 600 * 1000){
      try{
        sender.send(new TThriftSerializer().serialize(tDiscoveryInfo));
        startTime = System.currentTimeMillis();
      }catch (Exception xe){
        xe.printStackTrace();
      }
    }
    KpiMessage kpiMessage = before();
    after(kpiMessage);
//    if (user == null) {
    //    User user = userRepository.findByUsernameAndPassword(username, password);
//
//      throw new UnauthorizedAccessException("No user matches username " + username + " and password");
//    }

    return tokenStore.generate(username);
  }

  private KpiMessage before(){
    KpiMessage message = new KpiMessage(MetaDataMgr.resourceId, tDiscoveryInfo.getAgentId(), KpiManager.instance().getContext().getTxType());
    message.setStartTime(System.currentTimeMillis());
    return message;
  }

  private void after(KpiMessage message) {
    message.setEndTime(System.currentTimeMillis() + 3);
    message.setSuccess(true);
    KpiManager.instance().addKpiMessage(message);
  }

  @Override
  public String validate(String token) {
    try {
      return tokenStore.parse(token);
    } catch (TokenException e) {
      throw new UnauthorizedAccessException("No user matches such a token " + token, e);
    }
  }
}
