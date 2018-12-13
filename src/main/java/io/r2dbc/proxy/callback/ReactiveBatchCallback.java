/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.proxy.callback;

import io.r2dbc.proxy.core.ConnectionInfo;
import io.r2dbc.proxy.core.ExecutionType;
import io.r2dbc.proxy.core.QueryExecutionInfo;
import io.r2dbc.proxy.core.QueryInfo;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Result;
import org.reactivestreams.Publisher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Proxy callback for {@link Batch}.
 *
 * @author Tadaya Tsuyukubo
 */
public class ReactiveBatchCallback extends CallbackSupport {

    private Batch<?> batch;

    private ConnectionInfo connectionInfo;

    private List<String> queries = new ArrayList<>();

    public ReactiveBatchCallback(Batch<?> batch, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        super(proxyConfig);
        this.batch = batch;
        this.connectionInfo = connectionInfo;
    }

    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();

        if ("unwrap".equals(methodName)) {
            return this.batch;
        } else if ("getOriginalConnection".equals(methodName)) {
            return this.connectionInfo.getOriginalConnection();
        }

        Object result = proceedExecution(method, this.batch, args, this.proxyConfig.getListeners(), this.connectionInfo, null, null);

        if ("add".equals(methodName)) {
            this.queries.add((String) args[0]);
        } else if ("execute".equals(methodName)) {

            List<QueryInfo> queryInfoList = this.queries.stream()
                .map(QueryInfo::new)
                .collect(toList());

            QueryExecutionInfo execInfo = new QueryExecutionInfo();
            execInfo.setType(ExecutionType.BATCH);
            execInfo.setQueries(queryInfoList);
            execInfo.setBatchSize(this.queries.size());
            execInfo.setMethod(method);
            execInfo.setMethodArgs(args);
            execInfo.setConnectionInfo(this.connectionInfo);

            // API defines "execute()" returns a publisher
            Publisher<? extends Result> publisher = (Publisher<? extends Result>) result;

            return interceptQueryExecution(publisher, execInfo);
        }

        return result;
    }
}
