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
package io.servicecomb.company.worker;

import io.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * {@link FibonacciRestEndpoint} provides the rest implementation of {@link FibonacciEndpoint}.
 * The rest endpoint is accessed by /fibonacci/term?n={value} with HTTP GET.
 */
@RestSchema(schemaId = "fibonacciRestEndpoint")
@RequestMapping("/fibonacci")
@Controller
public class FibonacciRestEndpoint implements FibonacciEndpoint {

  private final FibonacciService fibonacciService;

  @Autowired
  FibonacciRestEndpoint(FibonacciService fibonacciService) {
    this.fibonacciService = fibonacciService;
  }

  @Override
  @RequestMapping(value = "/term", method = RequestMethod.GET)
  @ResponseBody
  public long term(int n) throws InterruptedException {
    return fibonacciService.term(n);
  }
}
