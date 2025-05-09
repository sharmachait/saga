package com.food.ordering.system.domain.entity;

public abstract class AggregateRoot<ID> extends BaseEntity<ID> {
    // is also an entity but a special kind, this class only marks other child classes as aggregate root
}
