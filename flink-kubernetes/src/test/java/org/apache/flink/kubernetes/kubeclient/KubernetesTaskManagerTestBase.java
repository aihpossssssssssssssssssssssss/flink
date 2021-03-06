/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.kubernetes.kubeclient;

import org.apache.flink.configuration.MemorySize;
import org.apache.flink.configuration.ResourceManagerOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.kubernetes.KubernetesTestBase;
import org.apache.flink.kubernetes.kubeclient.parameters.KubernetesTaskManagerParameters;
import org.apache.flink.runtime.clusterframework.ContaineredTaskManagerParameters;
import org.apache.flink.runtime.clusterframework.TaskExecutorProcessSpec;
import org.apache.flink.runtime.clusterframework.TaskExecutorProcessUtils;

import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * Base test class for the TaskManager side.
 */
public class KubernetesTaskManagerTestBase extends KubernetesTestBase {

	protected static final int RPC_PORT = 12345;

	protected static final String POD_NAME = "taskmanager-pod-1";
	private static final String DYNAMIC_PROPERTIES = "";

	protected static final int TOTAL_PROCESS_MEMORY = 1024;
	protected static final double TASK_MANAGER_CPU = 2.0;

	protected final Map<String, String> customizedEnvs = new HashMap<String, String>() {
		{
			put("key1", "value1");
			put("key2", "value2");
		}
	};

	protected TaskExecutorProcessSpec taskExecutorProcessSpec;

	protected ContaineredTaskManagerParameters containeredTaskManagerParameters;

	protected KubernetesTaskManagerParameters kubernetesTaskManagerParameters;

	protected FlinkPod baseFlinkPod = new FlinkPod.Builder().build();

	@Before
	public void setup() throws Exception {
		super.setup();

		flinkConfig.set(TaskManagerOptions.RPC_PORT, String.valueOf(RPC_PORT));
		flinkConfig.set(TaskManagerOptions.CPU_CORES, TASK_MANAGER_CPU);
		flinkConfig.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse(TOTAL_PROCESS_MEMORY + "m"));
		customizedEnvs.forEach((k, v) ->
				flinkConfig.setString(ResourceManagerOptions.CONTAINERIZED_TASK_MANAGER_ENV_PREFIX + k, v));

		taskExecutorProcessSpec = TaskExecutorProcessUtils.processSpecFromConfig(flinkConfig);
		containeredTaskManagerParameters = ContaineredTaskManagerParameters.create(flinkConfig, taskExecutorProcessSpec,
				flinkConfig.getInteger(TaskManagerOptions.NUM_TASK_SLOTS));
		kubernetesTaskManagerParameters = new KubernetesTaskManagerParameters(
				flinkConfig,
				POD_NAME,
				TOTAL_PROCESS_MEMORY,
				DYNAMIC_PROPERTIES,
				containeredTaskManagerParameters);
	}
}
