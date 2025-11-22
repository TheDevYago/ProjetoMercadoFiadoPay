package com.mercado.core;

public interface PaymentStrategy {
    void execute(double amount);
}