package br.com.alura.bytebank.domain.conta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

public class ContaDAO {
    
    private Connection conn;

    ContaDAO(Connection connection) {
        this.conn = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta){
        
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente);


        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email)" +
                "VALUES (?, ?, ?, ?, ?)";

        
        try {
            PreparedStatement prepareStatement = conn.prepareStatement(sql);
        
            prepareStatement.setInt(1, conta.getNumero());
            prepareStatement.setBigDecimal(2, BigDecimal.ZERO);
            prepareStatement.setString(3, dadosDaConta.dadosCliente().nome());
            prepareStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            prepareStatement.setString(5, dadosDaConta.dadosCliente().email());

            prepareStatement.execute();
            prepareStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
    }

    public Set<Conta> listar() {
        Set<Conta> contas = new HashSet<>();
        String sql = "SELECT * FROM conta";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()){
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                contas.add(new Conta(numero, new Cliente(new DadosCadastroCliente(nome, cpf, email))));
            }

            ps.close();
            resultSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contas;
    }

    public Conta listarPorNumero(Integer numero) {
        String sql = "SELECT * FROM conta WHERE numero = ?";

        PreparedStatement ps;
        ResultSet resultSet;
        Conta conta = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, numero);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Integer numeroRecuperado = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                DadosCadastroCliente dadosCadastroCliente =
                        new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);

                conta = new Conta(numeroRecuperado, cliente);
            }
            resultSet.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conta;
    }
}
