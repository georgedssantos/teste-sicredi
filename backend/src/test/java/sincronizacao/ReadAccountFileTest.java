package sincronizacao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import br.com.sicredi.sincronizacao.dto.ContaDTO;

/**
 * CLASSE DE TESTES UNITÁRIOS. PARA FUNCIONAR O ARQUIVO DATA.CSV DEVE ESTAR NO MESMO DIRETÓRIO DO PROGRAMA JAVA
 */
public class ReadAccountFileTest {
    private List<String[]> csvData;
    
    String csvFile = "src/test/resources/DATA.csv";
    
    @BeforeEach
    public void setUp() throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            csvData = reader.readAll();
        }
    }

    @Test
    @DisplayName("LER DADOS DO ARQUIVO CSV")
    public void testaLeituraDoArquivoCSVComSucesso() {
        for (String[] account : csvData) {
            String agencia = account[0];
            String conta = account[1];
            String saldo = account[2];

            ContaDTO contaDTO = new ContaDTO(agencia, conta, saldo);

            assertEquals(agencia, contaDTO.agencia());
            assertEquals(conta, contaDTO.conta());
            assertEquals(saldo, contaDTO.saldo());
        }
    }
}

