package com.project.healthsystem.service.datasus;

import com.project.healthsystem.exceptions.InvalidDataException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;

@Component
public class CnesCsvReader {
    public enum Catalog {
        ESTABLISHMENTS(new String[]{"CO_UNIDADE","CO_CNES","NU_CNPJ_MANTENEDORA","NO_RAZAO_SOCIAL",
            "NO_FANTASIA","CO_CEP","NU_TELEFONE","NO_EMAIL","NU_CPF","NU_CNPJ","ENDERECO"}, 831904622L),
        PROFESSIONALS(new String[]{"name","cns","cityName","cbo","cboDescription","cnpj","cnes",
            "estabilishmentName","legalNatureCode","legalNatureDescription","sus","resident",
            "preceptor","management","estabilishmentBond","employerBond","bondDetails"}, 831904623L);
        private final String[] header;
        private final long lockId;
        Catalog(String[] header, long lockId) { this.header = header; this.lockId = lockId; }
        public long lockId() { return lockId; }
    }

    private static final CSVFormat FORMAT = CSVFormat.RFC4180.builder()
        .setDelimiter(';').setIgnoreEmptyLines(true).get();

    public long validate(Path file, Catalog catalog) {
        return read(file, catalog, fields -> {});
    }

    // Record numbers count the header as record 1; quoted newlines do not create extra records.
    public long read(Path file, Catalog catalog, Consumer<String[]> consumer) {
        long rows = 0, lastRecord = 0, invalidRows = 0;
        List<String> problems = new ArrayList<>();
        BitSet establishmentCodes = new BitSet();
        try (PushbackReader reader = new PushbackReader(new BufferedReader(new InputStreamReader(
                Files.newInputStream(file), StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT))), 1)) {
            int first = reader.read();
            if (first != -1 && first != '\uFEFF') reader.unread(first);
            try (var parser = FORMAT.parse(reader)) {
                var iterator = parser.iterator();
                if (!iterator.hasNext()) throw new InvalidDataException("Arquivo CNES vazio.");
                CSVRecord header = iterator.next();
                lastRecord = header.getRecordNumber();
                if (!Arrays.equals(values(header), catalog.header)) {
                    throw new InvalidDataException("Cabeçalho CNES inválido para " + catalog
                        + ". Colunas esperadas, na ordem: " + String.join(";", catalog.header));
                }
                while (iterator.hasNext()) {
                    CSVRecord record = iterator.next();
                    lastRecord = record.getRecordNumber();
                    String[] fields = values(record);
                    if (fields.length == 1 && fields[0].isBlank()) continue;
                    rows++;
                    String error = validateFields(fields, catalog, establishmentCodes);
                    if (error != null) {
                        invalidRows++;
                        if (problems.size() < 20) problems.add("Registro " + lastRecord + ": " + error);
                    } else {
                        consumer.accept(fields);
                    }
                }
            }
        } catch (IOException | UncheckedIOException e) {
            throw new InvalidDataException("Não foi possível ler o CSV CNES próximo ao registro "
                + (lastRecord + 1) + ". Verifique UTF-8, aspas e a estrutura do arquivo.");
        }
        if (invalidRows > 0) {
            throw new InvalidDataException("CSV CNES com " + invalidRows + " registro(s) inválido(s). "
                + String.join(" | ", problems) + (invalidRows > 20 ? " | Exibidos os primeiros 20 erros." : ""));
        }
        if (rows == 0) throw new InvalidDataException("Arquivo CNES sem registros de dados.");
        return rows;
    }

    private static String[] values(CSVRecord record) {
        String[] result = new String[record.size()];
        for (int i = 0; i < result.length; i++) result[i] = record.get(i).trim();
        return result;
    }

    private static String validateFields(String[] fields, Catalog catalog, BitSet codes) {
        if (fields.length != catalog.header.length) {
            return "esperadas " + catalog.header.length + " colunas; encontradas " + fields.length + ".";
        }
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].codePointCount(0, fields[i].length()) > 255) {
                return "campo " + catalog.header[i] + " excede 255 caracteres.";
            }
            if (fields[i].indexOf('\u0000') >= 0 || fields[i].indexOf('\uFFFD') >= 0) {
                return "campo " + catalog.header[i] + " contém caractere inválido.";
            }
        }
        if (catalog == Catalog.ESTABLISHMENTS) {
            if (!fields[1].matches("[0-9]{7}")) return "CO_CNES deve conter sete dígitos.";
            int code = Integer.parseInt(fields[1]);
            if (codes.get(code)) return "CO_CNES duplicado no arquivo.";
            codes.set(code);
        } else {
            if (fields[0].isBlank()) return "name não pode estar vazio.";
            if (!fields[1].matches("[0-9]{15}")) return "cns deve conter quinze dígitos.";
            if (!fields[3].matches("[A-Za-z0-9]{6}")) return "cbo deve conter seis caracteres alfanuméricos.";
            if (!fields[6].matches("[0-9]{7}")) return "cnes deve conter sete dígitos.";
            for (int i = 10; i <= 12; i++) {
                if (!fields[i].equalsIgnoreCase("S") && !fields[i].equalsIgnoreCase("N")) {
                    return catalog.header[i] + " deve ser S ou N.";
                }
            }
        }
        return null;
    }
}
