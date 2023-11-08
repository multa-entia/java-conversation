package ru.multa.entia.conversion.impl.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.multa.entia.conversion.api.ConversationItem;
import ru.multa.entia.conversion.api.pipeline.PipelineBoxHandler;
import ru.multa.entia.conversion.api.pipeline.PipelineBoxHandlerTask;
import ru.multa.entia.conversion.api.pipeline.PipelineSubscriber;
import ru.multa.entia.conversion.api.publisher.PublisherTask;
import ru.multa.entia.results.api.result.Result;
import ru.multa.entia.results.api.seed.Seed;
import ru.multa.entia.results.impl.result.DefaultResultBuilder;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class DefaultPipelineBoxHandler<T extends ConversationItem> implements PipelineBoxHandler<PublisherTask<T>> {
    @RequiredArgsConstructor
    @Getter
    public enum Code {
        TASK_IS_NULL("default-pipeline-box-handler.task-is-null"),
        INVALID_TASK_TYPE("default-pipeline-box-handler.invalid-task-type"),
        INVALID_ACTOR("default-pipeline-box-handler.invalid-actor"),
        INVALID_TASK_BOX("default-pipeline-box-handler.invalid-task-box"),
        INVALID_TASK_BOX_VALUE("default-pipeline-box-handler.invalid-task-box-value"),
        INVALID_TASK_SESSION_ID("default-pipeline-box-handler.invalid-session-id"),
        FAIL_GIVING("default-pipeline-box-handler.fail-giving");

        private final String value;
    };

    @Override
    public Result<Object> handle(PipelineBoxHandlerTask<PublisherTask<T>> task) {
        Code code = null;
        Object[] args = new Object[0];

        if (task == null) {
            code = Code.TASK_IS_NULL;
        } else if (!task.getClass().equals(DefaultPipelineBoxHandlerTask.class)) {
            code = Code.INVALID_TASK_TYPE;
        } else {
            DefaultPipelineBoxHandlerTask<T> castTask = (DefaultPipelineBoxHandlerTask<T>) task;

            if (castTask.box() == null){
                code = Code.INVALID_TASK_BOX;
            } else if (castTask.box().value() == null) {
                code = Code.INVALID_TASK_BOX_VALUE;
            } else if (castTask.sessionId() == null){
                code = Code.INVALID_TASK_SESSION_ID;
            } else {
                Map<UUID, PipelineSubscriber<PublisherTask<T>>> actor = castTask.actor();
                Lock __lock__ = Objects.requireNonNullElseGet(castTask.actorLock(), ReentrantLock::new);
                __lock__.lock();
                if (actor != null && !actor.isEmpty()){
                    ArrayList<Seed> seeds = new ArrayList<>();
                    for (Map.Entry<UUID, PipelineSubscriber<PublisherTask<T>>> entry : actor.entrySet()) {
                        Result<PublisherTask<T>> result = entry.getValue().give(castTask.box().value(), castTask.sessionId());
                        if (!result.ok()){
                            seeds.add(result.seed());
                        }
                    }

                    if (!seeds.isEmpty()){
                        code = Code.FAIL_GIVING;
                        args = new Object[]{seeds};
                    }
                }
                else {
                    code = Code.INVALID_ACTOR;
                }
                __lock__.unlock();
            }
        }

        return code == null
                ? DefaultResultBuilder.<Object>ok(null)
                : DefaultResultBuilder.<Object>fail(code.getValue(), args);
    }
}
