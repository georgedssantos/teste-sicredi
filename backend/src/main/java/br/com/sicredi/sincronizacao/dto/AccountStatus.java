package br.com.sicredi.sincronizacao.dto;

public enum AccountStatus {
	
	PENDENTE("Pendente"),
	RECEBIDO("Recebido");
	
	private String description;
	

	AccountStatus(String string) {
		// TODO Auto-generated constructor stub
	}

	public String getDescription() {
		return description;
	}

	
}
