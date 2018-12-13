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

package io.r2dbc.proxy.listener;

import io.r2dbc.proxy.core.MethodExecutionInfo;
import io.r2dbc.proxy.core.QueryExecutionInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Delegate to multiple of {@link ProxyExecutionListener ProxyExecutionListeners}.
 *
 * @author Tadaya Tsuyukubo
 */
public class CompositeProxyExecutionListener implements ProxyExecutionListener {

    private List<ProxyExecutionListener> listeners = new ArrayList<>();

    public CompositeProxyExecutionListener(ProxyExecutionListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    @Override
    public void beforeMethod(MethodExecutionInfo executionInfo) {
        this.listeners.forEach(listener -> listener.beforeMethod(executionInfo));
    }

    @Override
    public void afterMethod(MethodExecutionInfo executionInfo) {
        this.listeners.forEach(listener -> listener.afterMethod(executionInfo));
    }

    @Override
    public void beforeQuery(QueryExecutionInfo execInfo) {
        this.listeners.forEach(listener -> listener.beforeQuery(execInfo));
    }

    @Override
    public void afterQuery(QueryExecutionInfo execInfo) {
        this.listeners.forEach(listener -> listener.afterQuery(execInfo));
    }

    @Override
    public void eachQueryResult(QueryExecutionInfo execInfo) {
        this.listeners.forEach(listener -> listener.eachQueryResult(execInfo));
    }

    public boolean add(ProxyExecutionListener listener) {
        return this.listeners.add(listener);
    }

    public boolean addAll(Collection<ProxyExecutionListener> listeners) {
        return this.listeners.addAll(listeners);
    }

    public List<ProxyExecutionListener> getListeners() {
        return this.listeners;
    }

}
