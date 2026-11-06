package com.project.healthsystem.unit.service;

import builders.CnesFixtures;
import com.project.healthsystem.service.datasus.CnesCsvReader;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.model.datasus_data.cnes.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static builders.CnesFixtures.*;
import static com.project.healthsystem.service.datasus.CnesCsvReader.Catalog.*;
import static org.junit.jupiter.api.Assertions.*;

class CnesCsvReaderTest {
    @TempDir Path temp;
    CnesCsvReader reader = new CnesCsvReader();

    Path csv(String text) throws Exception {
        return Files.writeString(temp.resolve("input.csv"), text, StandardCharsets.UTF_8);
    }

    @Test void quotedFieldsBomAndNewlinesArePreserved() throws Exception {
        var path = csv("\uFEFF" + EST_HEADER + "\r\n"
            + "unit;0012345;;\"Razão; \"\"Centro\"\"\";Unidade;00123456;;;;;\"Rua\nNúmero 1\"\r\n");
        List<Estabilishment> rows = new ArrayList<>();
        assertEquals(1, reader.read(path, ESTABLISHMENTS, fields -> rows.add(Estabilishment.fromFields(fields))));
        assertEquals("0012345", rows.get(0).getCnesCode());
        assertEquals("Razão; \"Centro\"", rows.get(0).getLegalName());
        assertEquals("Rua\nNúmero 1", rows.get(0).getAddress());
        assertEquals("", rows.get(0).getCnpj());
    }

    @Test void multipleBondsAlphanumericCboAndIndicatorsArePreserved() throws Exception {
        Path path = csv(PROF_HEADER + "\n" + professional("000000000000001", "0012345")
            + "\n" + professional("000000000000001", "0012346"));
        List<CnesProfessional> rows = new ArrayList<>();
        assertEquals(2, reader.read(path, PROFESSIONALS, fields -> rows.add(CnesProfessional.fromFields(fields))));
        assertEquals("2231F9", rows.get(0).getCbo());
        assertEquals("000000000000001", rows.get(0).getCns());
        assertTrue(rows.get(0).getSus());
        assertFalse(rows.get(0).getResident());
        assertTrue(rows.get(0).getPreceptor());
        assertEquals("D", rows.get(0).getManagement());
    }

    @Test void reportsAllInvalidCnsRecordsWithoutEchoingPersonalData() throws Exception {
        Path path = csv(PROF_HEADER + "\n" + professional("1234","0012345")
            + "\n" + professional("5678","0012346"));
        String message = assertThrows(InvalidDataException.class, () -> reader.validate(path, PROFESSIONALS)).getMessage();
        assertTrue(message.contains("2 registro(s)"));
        assertTrue(message.contains("Registro 2"));
        assertTrue(message.contains("Registro 3"));
        assertFalse(message.contains("1234"));
        assertFalse(message.contains("Pessoa"));
    }

    @ParameterizedTest @ValueSource(strings = {"", "X", "true"})
    void rejectsUnknownBoolean(String value) throws Exception {
        Path path = csv(PROF_HEADER + "\n" + professional("000000000000001","0012345").replace(";s;N;S;", ";" + value + ";N;S;"));
        assertThrows(InvalidDataException.class, () -> reader.validate(path, PROFESSIONALS));
    }

    @Test void rejectsDuplicateEstablishmentCodes() throws Exception {
        Path path = csv(EST_HEADER + "\n" + establishment("0012345") + "\n" + establishment("0012345"));
        assertTrue(assertThrows(InvalidDataException.class, () -> reader.validate(path, ESTABLISHMENTS))
            .getMessage().contains("duplicado"));
    }

    @Test void rejectsEmptyHeaderOnlyMissingHeaderAndWrongCatalog() throws Exception {
        for (String content : List.of("", EST_HEADER, establishment("0012345"), PROF_HEADER)) {
            Path path = csv(content);
            assertThrows(InvalidDataException.class, () -> reader.validate(path, ESTABLISHMENTS));
        }
    }

    @Test void rejectsWrongColumnsAndMalformedQuotes() throws Exception {
        for (String row : List.of("a;b", establishment("0012345") + ";extra", "\"unclosed")) {
            Path path = csv(EST_HEADER + "\n" + row);
            assertThrows(InvalidDataException.class, () -> reader.validate(path, ESTABLISHMENTS));
        }
    }

    @Test void rejectsInvalidUtf8RatherThanReplacingCharacters() throws Exception {
        Path path = temp.resolve("bad.csv");
        byte[] prefix = (EST_HEADER + "\n").getBytes(StandardCharsets.UTF_8);
        byte[] content = Arrays.copyOf(prefix, prefix.length + 2);
        content[prefix.length] = (byte) 0xC3;
        content[prefix.length + 1] = (byte) 0x28;
        Files.write(path, content);
        assertThrows(InvalidDataException.class, () -> reader.validate(path, ESTABLISHMENTS));
    }

    @Test void optionalNamesAndContactsAreAllowedButInvalidCnesIsNot() throws Exception {
        Path path = csv(EST_HEADER + "\n" + establishment("0012345").replace("Razão;Unidade",";"));
        assertEquals(1, reader.validate(path, ESTABLISHMENTS));
        path = csv(EST_HEADER + "\n" + establishment("123"));
        Path invalid = path;
        assertThrows(InvalidDataException.class, () -> reader.validate(invalid, ESTABLISHMENTS));
    }

    @Test void errorListIsBoundedAndLongFieldsRejected() throws Exception {
        Path path = csv(EST_HEADER + "\n" + ("x".repeat(256) + ";1;;;;;;;;;\n").repeat(30));
        String message = assertThrows(InvalidDataException.class, () -> reader.validate(path, ESTABLISHMENTS)).getMessage();
        assertTrue(message.contains("30 registro(s)"));
        assertTrue(message.contains("primeiros 20"));
    }
}
