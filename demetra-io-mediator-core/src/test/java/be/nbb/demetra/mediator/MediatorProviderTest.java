/*
 * Copyright 2015 National Bank of Belgium
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
package be.nbb.demetra.mediator;

import be.nbb.demetra.mediator.MediatorConnection;
import be.nbb.demetra.mediator.MediatorAlias;
import be.nbb.demetra.mediator.MediatorConnectionSupplier;
import be.nbb.demetra.mediator.MediatorProvider;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class MediatorProviderTest {

    //<editor-fold defaultstate="collapsed" desc="Resources">
    static final class FakeConnection extends MediatorConnection {

        @Override
        public List<String> getDataSourceNames() throws IOException {
            return Arrays.asList("Fake");
        }

        @Override
        public List<MediatorAlias> get(String dataSourceName) throws IOException {
            switch (dataSourceName) {
                case "Fake":
                    return Arrays.asList(new MediatorAlias("Fake", "hello", ""));
                default:
                    return Collections.emptyList();
            }
        }
    }

    static final class FakeConnectionSupplier extends MediatorConnectionSupplier {

        @Override
        public MediatorConnection getConnection() throws IOException {
            return new FakeConnection();
        }
    }

    static final DataSource FAKE_DATASOURCE = DataSource.builder(MediatorProvider.NAME, MediatorProvider.VERSION).put("x", "Fake").build();
    static final DataSet FAKE_DATASET = DataSet.builder(FAKE_DATASOURCE, DataSet.Kind.SERIES).put("y", "hello").build();
    //</editor-fold>

    @Test
    public void testAll() throws IllegalArgumentException, IOException {
        MediatorProvider provider = new MediatorProvider();

        assertThat(provider.getDataSources()).isEmpty();

        provider.setConnectionSupplier(new FakeConnectionSupplier());
        assertThat(provider.getDataSources()).containsExactly(FAKE_DATASOURCE);
        assertThat(provider.children(FAKE_DATASOURCE)).containsExactly(FAKE_DATASET);

        provider.setConnectionSupplier(null);
        assertThat(provider.getDataSources()).isEmpty();
    }
}
