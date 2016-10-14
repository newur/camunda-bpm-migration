package org.camunda.bpm.migration.plan.step.variable.strategy;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.test.ProcessV1;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

@Deployment(resources = ProcessV1.BPMN_FILE)
public class ReadProcessVariableTest extends AbstractStrategyTest {

	private ReadProcessVariable strategy = new ReadProcessVariable();

	@Test
	public void read_should_read_process_variable() {
		Optional<TypedValue> value = strategy.read(context, "FOO");

		assertThat(value).isNotNull();
		assertThat(value.isPresent());
		assertThat(value.get().getValue()).isEqualTo("BAR");
		assertThat(value.get().getType()).isEqualTo(ValueType.STRING);
	}

	@Test
	public void read_should_not_change_var_on_read() {
		strategy.read(context, "FOO");

		Map<String, Object> variables = runtimeService().getVariables(context.getProcessInstanceId());
		assertThat(variables).hasSize(1).containsEntry("FOO","BAR");
	}

	@Test
	public void remove_should_remove_process_variable() {
		strategy.remove(context,"FOO");

		Map<String, Object> variables = runtimeService().getVariables(context.getProcessInstanceId());
		assertThat(variables).isEmpty();
	}

	@Test
	public void remove_should_not_remove_task_variable() {
		strategy.remove(context,"FOO");

		Map<String, Object> variables = taskService().getVariablesLocal(taskId);
		assertThat(variables).hasSize(1);
	}
}
