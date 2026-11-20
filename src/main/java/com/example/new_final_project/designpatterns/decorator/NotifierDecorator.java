package com.example.new_final_project.designpatterns.decorator;

import com.example.new_final_project.model.Customer;
import com.example.new_final_project.model.Order;

// Base decorator: wraps another Notifier and passes the call on to it
public abstract class NotifierDecorator implements Notifier {

    private final Notifier delegate;

    protected NotifierDecorator(Notifier delegate) {
        this.delegate = delegate;
    }

    @Override
    public void send(Customer customer, Order order, String message) {
        delegate.send(customer, order, message);
    }
}