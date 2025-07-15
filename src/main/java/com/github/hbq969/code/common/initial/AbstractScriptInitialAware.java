package com.github.hbq969.code.common.initial;

import com.github.hbq969.code.common.initial.event.ScriptInitialDoneEvent;
import com.github.hbq969.code.common.initial.event.TableCreateDoneEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScriptInitialAware implements ScriptInitialAware, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    private CountDownLatch tableCreateDoneLatch = new CountDownLatch(1);
    private CountDownLatch scriptInitialDoneLatch = new CountDownLatch(1);

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    @Override
    public void tableCreate() {
        try {
            tableCreate0();
        } finally {
            tableCreateDoneLatch.countDown();
            this.applicationEventPublisher.publishEvent(new TableCreateDoneEvent(this));
        }
    }


    protected void tableCreate0() {
    }

    @Override
    public void tableCreateDone(long timeout, TimeUnit unit) {
        try {
            if (timeout > 0)
                tableCreateDoneLatch.await(timeout, unit);
            else
                tableCreateDoneLatch.await();
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void scriptInitial() {
        try {
            scriptInitial0();
        } finally {
            scriptInitialDoneLatch.countDown();
            this.applicationEventPublisher.publishEvent(new ScriptInitialDoneEvent(this));
        }
    }

    protected void scriptInitial0() {
    }

    @Override
    public void scriptInitialDone(long timeout, TimeUnit unit) {
        try {
            if (timeout > 0)
                scriptInitialDoneLatch.await(timeout, unit);
            else
                scriptInitialDoneLatch.await();
        } catch (InterruptedException e) {
        }
    }
}
