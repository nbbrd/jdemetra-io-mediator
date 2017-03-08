/*
 * Copyright 2017 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package be.nbb.demetra.mediator.file;

import be.nbb.demetra.mediator.MediatorConnection;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class FileMediatorConnectionSupplierTest {

    @Test
    public void test() throws IOException {
        FileMediatorConnectionSupplier supplier = new FileMediatorConnectionSupplier(ALIASES);

        assertThat(supplier.getFile()).isEqualTo(ALIASES);
        try (MediatorConnection conn = supplier.getConnection()) {
            assertThat(conn.getDataSourceNames()).containsExactly("First", "Second");
        }
    }

    public static final Path ALIASES = getFile(FileMediatorConnectionSupplierTest.class.getResource("/Aliases.xml"));

    private static Path getFile(URL url) {
        try {
            return Paths.get(url.toURI());
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
