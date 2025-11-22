package com.mercado.plugins;

import com.mercado.annotations.PaymentMethod;
import com.mercado.core.PaymentStrategy;

@PaymentMethod("CARD")
public class CardPayment implements PaymentStrategy {
    @Override
    public void execute(double amount) {
        System.out.println(">>> [PLUGIN CARTAO] Conectando a operadora para cobrar R$" + amount);
    }
}
