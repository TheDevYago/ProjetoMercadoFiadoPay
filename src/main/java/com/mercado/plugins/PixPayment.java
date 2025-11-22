package com.mercado.plugins;

import com.mercado.annotations.AntiFraud;
import com.mercado.annotations.PaymentMethod;
import com.mercado.core.PaymentStrategy;

@PaymentMethod("PIX")
@AntiFraud(limit = 500.00) // Regra: Pix acima de 500 deve ser bloqueado
public class PixPayment implements PaymentStrategy {
    @Override
    public void execute(double amount) {
        System.out.println(">>> [PLUGIN PIX] Gerando QR Code para R$" + amount);
    }
}