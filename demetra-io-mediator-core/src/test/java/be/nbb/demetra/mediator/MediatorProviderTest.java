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

import be.nbb.demetra.mediator.util.Mediators;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import java.io.IOException;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class MediatorProviderTest {

    static final DataSource FAKE_DATASOURCE = DataSource.builder(MediatorProvider.NAME, InternalProvider.VERSION).put("x", "Fake").build();
    static final DataSet FAKE_DATASET = DataSet.builder(FAKE_DATASOURCE, DataSet.Kind.SERIES).put("y", "hello").build();

    @Test
    public void testAll() throws IllegalArgumentException, IOException {
        MediatorProvider p = new MediatorProvider();

        assertThat(p.getDataSources()).isEmpty();

        p.setConnectionSupplier(Mediators.supplierOf(Arrays.asList(new MediatorAlias("Fake", "hello", ""))));
        assertThat(p.getDataSources()).containsExactly(FAKE_DATASOURCE);
        assertThat(p.children(FAKE_DATASOURCE)).containsExactly(FAKE_DATASET);

        p.setConnectionSupplier(null);
        assertThat(p.getDataSources()).isEmpty();
    }
}
