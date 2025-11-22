package com.mercado.repository;

import com.mercado.dto.PaymentDTO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SalesRepository {
    private static final String DB_URL = "jdbc:h2:./mercado_db";
    private static final String USER = "sa";
    private static final String PASS = "";

    public SalesRepository() {
        createTable();
    }

    private void createTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            
            String sql = """
                CREATE TABLE IF NOT EXISTS vendas (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    loja VARCHAR(50),
                    valor DOUBLE,
                    metodo VARCHAR(20),
                    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            stmt.execute(sql);
            System.out.println("[DB] Tabela de vendas verificada.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(PaymentDTO payment) {
        String sql = "INSERT INTO vendas (loja, valor, metodo) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // CORREÇÃO AQUI: Ajustando para os novos campos do DTO
            pstmt.setString(1, "LOJA-01");       // Como removemos storeId, fixamos a loja
            pstmt.setDouble(2, payment.amount()); // Valor continua igual
            pstmt.setString(3, payment.method()); // Mudou de .type() para .method()
            
            pstmt.executeUpdate();
            System.out.println("💾 [DB] Venda salva localmente no H2.");
            
        } catch (SQLException e) {
            System.err.println("Erro ao salvar venda: " + e.getMessage());
        }
    }
    
    public void listAll() {
        String sql = "SELECT * FROM vendas";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n--- RELATORIO DE VENDAS LOCAIS (H2) ---");
            while(rs.next()) {
                System.out.printf("ID: %d | Metodo: %s | Valor: R$ %.2f%n", 
                    rs.getInt("id"), rs.getString("metodo"), rs.getDouble("valor"));
            }
            System.out.println("---------------------------------------\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}