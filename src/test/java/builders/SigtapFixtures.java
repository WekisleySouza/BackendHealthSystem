package builders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SigtapFixtures {
    public static void write(Path dir) throws IOException {
        Files.createDirectories(dir);
        String groups = "", subs = "", orgs = "", procs = "";
        for (String group : new String[]{"01", "02"}) {
            groups += group + String.format("%-100s", "Grupo " + group) + "202611\n";
            subs += group + "01" + String.format("%-100s", "Subgrupo") + "202611\n";
            orgs += group + "0101" + String.format("%-100s", "Forma") + "202611\n";
            char[] row = " ".repeat(336).toCharArray();
            put(row, 0, group + "01010001");
            put(row, 10, "Procedimento");
            put(row, 260, "1M");
            put(row, 274, "00000099");
            put(row, 330, "202611");
            procs += new String(row) + "\n";
        }
        Files.writeString(dir.resolve("tb_grupo.txt"), groups, StandardCharsets.ISO_8859_1);
        Files.writeString(dir.resolve("tb_sub_grupo.txt"), subs, StandardCharsets.ISO_8859_1);
        Files.writeString(dir.resolve("tb_forma_organizacao.txt"), orgs, StandardCharsets.ISO_8859_1);
        Files.writeString(dir.resolve("tb_procedimento.txt"), procs, StandardCharsets.ISO_8859_1);
    }
    private static void put(char[] row, int start, String value) {
        value.getChars(0, value.length(), row, start);
    }
}
