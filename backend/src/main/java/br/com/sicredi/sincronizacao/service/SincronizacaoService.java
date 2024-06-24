package br.com.sicredi.sincronizacao.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sicredi.sincronizacao.dto.AccountStatus;
import br.com.sicredi.sincronizacao.dto.ContaDTO;
import br.com.sicredi.sincronizacao.timer.MeasuredExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SincronizacaoService {
		
  private BancoCentralService bancoCentralService;
  
  @Autowired
  private FileService fileService;
  
  @Autowired
  public SincronizacaoService(BancoCentralService bancoCentralService) {
      this.bancoCentralService = bancoCentralService;
  }
  
  
  @MeasuredExecutionTime
  public void syncAccounts(String csvFile) {	  

	  // CHAMADA DO MÉTODO PARA RECEBER A LISTA DE CONTAS
	  List<String[]> accounts = this.fileService.readCvsFile(csvFile);
	  
	  // CHAMADA DO MÉTODO PARA CRIAR O ARQUIVO
      File file = this.fileService.createTxtFile();
	  	  
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
	  this.fileService.printAccountsTxtFile();
	  
  }


  /**
   * MÉTODO QUE SALVA NO ARQUIVO AS CONTAS COM SEU RESPECTIVOS STATUS
   */
	private void saveAccount(File file, int line, ContaDTO contaDTO, boolean isAccountStatus) {
		// SE FOR VALIDO STATUS RECEBIDO, CASO CONTRARIO PENDENTE
		  if(isAccountStatus) {
			  this.fileService.writeTxtFile(file, line, contaDTO, AccountStatus.RECEBIDO);
		  }else {
			  this.fileService.writeTxtFile(file, line, contaDTO, AccountStatus.PENDENTE);
		  }
	}
  
}
