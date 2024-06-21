package br.com.sicredi.sincronizacao.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import br.com.sicredi.sincronizacao.dto.AccountStatus;
import br.com.sicredi.sincronizacao.dto.ContaDTO;
import br.com.sicredi.sincronizacao.timer.MeasuredExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SincronizacaoService {
	
  private final static String FILE_NAME = "arquivoTabelaDeContas.txt";
	
  private BancoCentralService bancoCentralService;
  
  @Autowired
  public SincronizacaoService(BancoCentralService bancoCentralService) {
      this.bancoCentralService = bancoCentralService;
  }

  @MeasuredExecutionTime
  public void syncAccounts(String csvFile) {	  

	  // CHAMADA DO MÉTODO PARA RECEBER A LISTA DE CONTAS
	  List<String[]> accounts = readFile(csvFile);
	  
	  // CHAMADA DO MÉTODO PARA CRIAR O ARQUIVO
      File file = createFile();
	  	  
	  int line = 0;
	  // INTERAÇÃO PARA ATUALIZAR CADA CONTA LIDA DO ARQUIVO  
	  for (String[] account : accounts) {
		  
		  String agencia = account[0];
		  String conta = account[1];
		  String saldo = account[2];
		  	  
		  // CONTA LINHAS
		  ++line;
		  		  
		  // ENVIANDO OS DADOS DE CADA UMA DAS CONTAS A PARTIR DA SEGUNDA LINHA, ISTO É, APOS O CABEÇALHO 
		  if(line>1) {
			  
			  // PREENCHE OBJETO CONTA
			  ContaDTO contaDTO = new ContaDTO(agencia, conta, saldo);
			  
			  // ENVIA DADOS DA CONTA PARA O BANCO CENTRAL
			  boolean isAccountStatus = this.bancoCentralService.atualizaConta(contaDTO);
			  
			 // SALVA DADOS DA CONTA
			  saveAccount(file, line, contaDTO, isAccountStatus);
			  
		  }
	  }
	  
	  // IMPRIMINDO NO CONSOLE
	  printAccounts();
	  
  }


  /**
   * MÉTODO QUE SALVA NO ARQUIVO AS CONTAS COM SEU RESPECTIVOS STATUS
   */
	private void saveAccount(File file, int line, ContaDTO contaDTO, boolean isAccountStatus) {
		// SE FOR VALIDO STATUS RECEBIDO, CASO CONTRARIO PENDENTE
		  if(isAccountStatus) {
			  writeFile(file, line, contaDTO, AccountStatus.RECEBIDO);
		  }else {
			  writeFile(file, line, contaDTO, AccountStatus.PENDENTE);
		  }
	}
  

  /**
   * MÉTODO QUE LER TODAS AS LINHAS DO ARQUIVO CSV E RETORNA UMA LISTA
   * UTILIZA O CSVREADER DA BIBLIOTECA OPENCSV PARA LER O ARQUIVO CSV.
   * @param csvFile
   * @return
   */
  private List<String[]> readFile(String csvFile) {
	  
	  try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {		  
		  return reader.readAll();
		  
	  } catch (IOException | CsvException e) {
		  throw new IllegalArgumentException();
	  }
  }
  

  /**
   * MÉTODO CRIAR ARQUIVO COM CABEÇALHO DA TABELA
   * @return
   */
  private File createFile() {
	  // CRIA ARQUIVO
	  File file = new File(FILE_NAME);
	  try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
    	  
          // ESCREVENDO CABEÇALHO DA TABELA
			writer.write("Linha\t\tAgência\t\tConta\t\tSaldo\t\tStatus");
			writer.newLine();

      } catch (IOException e) {
    	  System.err.println("Erro ao criar arquivo: " + e.getMessage());
	} 
	  
	  return file;
  }
  
  /**
   * MÉTODO ESCREVER ARQUIVO CRIADO
   * @param file
   * @param line
   * @param contaDTO
   * @param accountStatus
   */
  private void writeFile(File file, int line, ContaDTO contaDTO, AccountStatus accountStatus) {  
      // VALIDA ARQUIVO
      if (file != null && file.exists()) {
    	  try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
    		  
    		// ESCREVENDO NO ARQUIVO
    		  writer.write(line+ "\t\t\t" + contaDTO.agencia() + "\t\t"  + contaDTO.conta()+ "\t\t"  + contaDTO.saldo()+ "\t\t"  + accountStatus);
    		  writer.newLine();
    		  
    	  } catch (IOException e) {
              System.err.println("Erro ao salvar dados no arquivo: " + e.getMessage());
          }
    	  
      }
	  
  }
  
  /**
   * MÉTODO IMPRIMIR CONTAS SALVAS
   */
  private void printAccounts() {
	  System.out.println("Conteúdo do arquivo " + FILE_NAME + ":");
      try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
          String line;
          while ((line = reader.readLine()) != null) {
              System.out.println(line);
          }
      } catch (IOException e) {
          System.err.println("Erro ao ler dados do arquivo: " + e.getMessage());
      }
  }

}
