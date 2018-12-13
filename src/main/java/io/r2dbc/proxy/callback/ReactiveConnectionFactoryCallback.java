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
import io.r2dbc.proxy.core.MethodExecutionInfo;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;

import java.lang.reflect.Method;
import java.util.function.BiFunction;

/**
 * Proxy callback for {@link ConnectionFactory}.
 *
 * @author Tadaya Tsuyukubo
 */
public class ReactiveConnectionFactoryCallback extends CallbackSupport {

    private ConnectionFactory connectionFactory;

    public ReactiveConnectionFactoryCallback(ConnectionFactory connectionFactory, ProxyConfig proxyConfig) {
        super(proxyConfig);
        this.connectionFactory = connectionFactory;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();

        if ("unwrap".equals(methodName)) {
            return this.connectionFactory;
        }

        BiFunction<Object, MethodExecutionInfo, Object> onMap = null;

        if ("create".equals(methodName)) {

            // callback for creating connection proxy
            onMap = (resultObj, executionInfo) -> {
                executionInfo.setResult(resultObj);

                Connection connection = (Connection) resultObj;  // original connection
                String connectionId = this.proxyConfig.getConnectionIdManager().getId(connection);

                ConnectionInfo connectionInfo = new ConnectionInfo();
                connectionInfo.setConnectionId(connectionId);
                connectionInfo.setClosed(false);
                connectionInfo.setOriginalConnection(connection);

                executionInfo.setConnectionInfo(connectionInfo);

                Connection proxyConnection = proxyConfig.getProxyFactory().createProxyConnection(connection, connectionInfo);

                return proxyConnection;
            };

        }

        Object result = proceedExecution(method, this.connectionFactory, args, this.proxyConfig.getListeners(), null, onMap, null);
        return result;
    }

}
