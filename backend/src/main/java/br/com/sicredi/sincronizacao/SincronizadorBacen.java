package br.com.sicredi.sincronizacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.sicredi.sincronizacao.service.SincronizacaoService;

@SpringBootApplication
public class SincronizadorBacen implements CommandLineRunner {
	
	private SincronizacaoService sincronizacaoService;
	
    @Autowired
    public SincronizadorBacen(SincronizacaoService sincronizacaoService) {
        this.sincronizacaoService = sincronizacaoService;
    }

	public static void main(String[] args) {
		SpringApplication.run(SincronizadorBacen.class, args);
		
	}
	
    @Override
    public void run(String... args) throws Exception {
		//O PROGRAMA ESPERA RECEBER UM ÚNICO ARGUMENTO QUE É O CAMINHO PARA O ARQUIVO CSV A SER LIDO.
        if (args.length != 1) {
            System.out.println("Favor utilizar o comando: java -jar SincronizadorBacen DATA.csv");
            return;
        }
        		                 
        this.sincronizacaoService.syncAccounts(args[0]);
    }

}
