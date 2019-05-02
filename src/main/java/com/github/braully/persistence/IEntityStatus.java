package com.github.braully.persistence;

/**
 *
 * @author Braully Rocha da Silva
 */
public interface IEntityStatus extends IEntity {

    public Status getStatus();

    public void setStatus(Status status);

    default public boolean isBlocked() {
        return getStatus() == Status.BLOCKED;
    }

    default public boolean isActive() {
        return getStatus() == Status.ACTIVE;
    }

    default public void setActive(boolean bStatus) {
        if (bStatus) {
            activate();
        } else {
            block();
        }
    }

    default public void block() {
        setStatus(Status.BLOCKED);
    }

    default public void activate() {
        setStatus(Status.ACTIVE);
    }

    default public void toggleBlock() {
        if (isActive()) {
            block();
        } else {
            activate();
        }
    }
}
