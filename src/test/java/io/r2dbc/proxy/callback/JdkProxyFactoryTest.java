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
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.Wrapped;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class JdkProxyFactoryTest {

    private JdkProxyFactory proxyFactory;

    @BeforeEach
    void setUp() {
        this.proxyFactory = new JdkProxyFactory();
        this.proxyFactory.setProxyConfig(new ProxyConfig());
    }

    @Test
    void isProxy() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Batch<?> batch = mock(Batch.class);
        Statement<?> statement = mock(Statement.class);
        ConnectionInfo connectionInfo = new ConnectionInfo();
        String query = "query";

        Object result;

        result = this.proxyFactory.createProxyConnectionFactory(connectionFactory);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(result).isInstanceOf(Wrapped.class);
        assertThat(result).isNotInstanceOf(ConnectionHolder.class);

        result = this.proxyFactory.createProxyConnection(connection, connectionInfo);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(result).isInstanceOf(Wrapped.class);
        assertThat(result).isInstanceOf(ConnectionHolder.class);

        result = this.proxyFactory.createProxyBatch(batch, connectionInfo);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(result).isInstanceOf(Wrapped.class);
        assertThat(result).isInstanceOf(ConnectionHolder.class);

        result = this.proxyFactory.createProxyStatement(statement, query, connectionInfo);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(result).isInstanceOf(Wrapped.class);
        assertThat(result).isInstanceOf(ConnectionHolder.class);
    }

    @Test
    void testToString() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Batch<?> batch = mock(Batch.class);
        Statement<?> statement = mock(Statement.class);
        ConnectionInfo connectionInfo = new ConnectionInfo();
        String query = "query";

        String expected;
        Object result;

        result = this.proxyFactory.createProxyConnectionFactory(connectionFactory);
        expected = getExpectedToString(connectionFactory);
        assertThat(result.toString()).isEqualTo(expected);

        result = this.proxyFactory.createProxyConnection(connection, connectionInfo);
        expected = getExpectedToString(connection);
        assertThat(result.toString()).isEqualTo(expected);

        result = this.proxyFactory.createProxyBatch(batch, connectionInfo);
        expected = getExpectedToString(batch);
        assertThat(result.toString()).isEqualTo(expected);

        result = this.proxyFactory.createProxyStatement(statement, query, connectionInfo);
        expected = getExpectedToString(statement);
        assertThat(result.toString()).isEqualTo(expected);

    }

    private String getExpectedToString(Object target) {
        return target.getClass().getSimpleName() + "-proxy [" + target.toString() + "]";
    }
}
