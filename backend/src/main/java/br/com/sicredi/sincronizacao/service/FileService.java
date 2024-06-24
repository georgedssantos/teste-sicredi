package br.com.sicredi.sincronizacao.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import br.com.sicredi.sincronizacao.dto.AccountStatus;
import br.com.sicredi.sincronizacao.dto.ContaDTO;

@Service
public class FileService {
	
	  private final static String FILE_NAME = "arquivoTabelaDeContas.txt";
	
	  /**
	   * MÉTODO QUE LER TODAS AS LINHAS DO ARQUIVO CSV E RETORNA UMA LISTA
	   * UTILIZA O CSVREADER DA BIBLIOTECA OPENCSV PARA LER O ARQUIVO CSV.
	   * @param csvFile
	   * @return
	   */
	  public List<String[]> readCvsFile(String csvFile) {
		  
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
	  public File createTxtFile() {
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
	  public void writeTxtFile(File file, int line, ContaDTO contaDTO, AccountStatus accountStatus) {  
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
	  public void printAccountsTxtFile() {
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
