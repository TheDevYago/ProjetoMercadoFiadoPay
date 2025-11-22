package com.mercado;

import com.mercado.client.FiadoPayClient;
import com.mercado.core.PaymentStrategy;
import com.mercado.core.PluginLoader;
import com.mercado.dto.PaymentDTO;
import com.mercado.repository.SalesRepository; // Novo import
import com.mercado.service.AuthService;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("=== MERCADO FIADOPAY (COM DB H2) ===");

        // 1. INICIALIZA BANCO DE DADOS
        SalesRepository db = new SalesRepository();

        // 2. INICIA THREADS
        AuthService authService = new AuthService();
        authService.startTokenRefresh();

        // 3. CARREGA PLUGINS
        PluginLoader loader = new PluginLoader();
        Map<String, Class<?>> paymentMethods = loader.loadPlugins();

        // 4. PREPARA CLIENTE
        FiadoPayClient client = new FiadoPayClient();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            Thread.sleep(500);
            System.out.println("\n---------------------------------------");
            System.out.println("Opcoes: " + paymentMethods.keySet() + " ou 'RELATORIO'");
            System.out.print("Digite o comando (ou 'SAIR'): ");
            String command = scanner.nextLine().toUpperCase().trim();

            if (command.equals("SAIR")) break;
            
            // Opção extra para ver o banco funcionando no vídeo
            if (command.equals("RELATORIO")) {
                db.listAll();
                continue;
            }

            if (paymentMethods.containsKey(command)) {
                System.out.print("Valor da compra: ");
                try {
                    double amount = Double.parseDouble(scanner.nextLine());
                    Class<?> pluginClass = paymentMethods.get(command);

                    if (loader.checkAntiFraud(pluginClass, amount)) {
                        // Processa Logica Local
                        PaymentStrategy strategy = (PaymentStrategy) pluginClass.getDeclaredConstructor().newInstance();
                        strategy.execute(amount);

                        // Cria DTO
                        PaymentDTO dto = new PaymentDTO(command, "BRL", amount, 1, "ORDER -" + System.currentTimeMillis());
                        
                        // 1. Envia para API (Nuvem)
                        client.sendPayment(dto, authService.getToken());
                        
                        // 2. Salva no Banco Local (Requisito H2/Reconciliação)
                        db.save(dto);
                        
                    } 
                } catch (Exception e) {
                    System.out.println("Erro no processo: " + e.getMessage());
                }
            } else {
                System.out.println("⚠️ Comando invalido.");
            }
        }
        
        authService.stop();
        scanner.close();
        System.out.println("Sistema encerrado.");
    }
}