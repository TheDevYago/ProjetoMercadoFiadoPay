package com.mercado.core;

import com.mercado.annotations.AntiFraud;
import com.mercado.annotations.PaymentMethod;
import java.util.HashMap;
import java.util.Map;

public class PluginLoader {
  // Simulamos o "scan" do pacote listando as classes conhecidas.
    // A Reflexão acontece ao LER as anotações dessas classes dinamicamente.
    private final Class<?>[] registeredClasses = {
        com.mercado.plugins.PixPayment.class,
        com.mercado.plugins.CardPayment.class
    };

    public Map<String, Class<?>> loadPlugins() {
        Map<String, Class<?>> plugins = new HashMap<>();
        System.out.println("[REFLEXAO] Escaneando plugins de pagamento...");
        
        for (Class<?> clazz : registeredClasses) {
            // Verifica se a classe tem a anotação @PaymentMethod
            if (clazz.isAnnotationPresent(PaymentMethod.class)) {
                PaymentMethod annotation = clazz.getAnnotation(PaymentMethod.class);
                System.out.println(" -> Encontrado: " + annotation.value());
                plugins.put(annotation.value(), clazz);
            }
        }
        return plugins;
    }

    public boolean checkAntiFraud(Class<?> clazz, double amount) {
        // Verifica se a classe tem regra de fraude
        if (clazz.isAnnotationPresent(AntiFraud.class)) {
            AntiFraud rule = clazz.getAnnotation(AntiFraud.class);
            if (amount > rule.limit()) {
                System.out.println("❌ [ANTIFRAUDE] BLOQUEADO! Valor R$" + amount + " maior que o limite de R$" + rule.limit());
                return false;
            }
        }
        return true;
    }
}