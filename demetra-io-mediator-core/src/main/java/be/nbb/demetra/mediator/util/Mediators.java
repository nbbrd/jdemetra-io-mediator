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
package be.nbb.demetra.mediator.util;

import be.nbb.demetra.mediator.MediatorAlias;
import be.nbb.demetra.mediator.MediatorConnection;
import be.nbb.demetra.mediator.MediatorConnectionSupplier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class Mediators {

    @Nonnull
    public MediatorConnectionSupplier noOpSupplier() {
        return NoOpSupplier.INSTANCE;
    }

    @Nonnull
    public MediatorConnection noOpConnection() {
        return NoOpConnection.INSTANCE;
    }

    @Nonnull
    public MediatorConnectionSupplier supplierOf(@Nonnull Iterable<MediatorAlias> data) {
        return new SupplierImpl(Objects.requireNonNull(asMap(data)));
    }

    @Nonnull
    public MediatorConnection connectionOf(@Nonnull Iterable<MediatorAlias> data) {
        return new ConnectionImpl(Objects.requireNonNull(asMap(data)));
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static SortedMap<String, List<MediatorAlias>> asMap(Iterable<MediatorAlias> data) {
        SortedMap<String, List<MediatorAlias>> result = new TreeMap<>();
        for (MediatorAlias o : data) {
            List<MediatorAlias> list = result.get(o.getDataSourceName());
            if (list == null) {
                list = new ArrayList<>();
                result.put(o.getDataSourceName(), list);
            }
            list.add(o);
        }
        return result;
    }

    private static final class SupplierImpl implements MediatorConnectionSupplier {

        private final SortedMap<String, List<MediatorAlias>> data;

        SupplierImpl(SortedMap<String, List<MediatorAlias>> data) {
            this.data = data;
        }

        @Override
        public MediatorConnection getConnection() throws IOException {
            return new ConnectionImpl(data);
        }
    }

    private static final class ConnectionImpl implements MediatorConnection {

        private final SortedMap<String, List<MediatorAlias>> data;

        ConnectionImpl(SortedMap<String, List<MediatorAlias>> data) {
            this.data = data;
        }

        @Override
        public List<String> getDataSourceNames() throws IOException {
            return new ArrayList<>(data.keySet());
        }

        @Override
        public List<MediatorAlias> get(String dataSourceName) throws IOException {
            List<MediatorAlias> result = data.get(dataSourceName);
            if (result == null) {
                throw new IOException("Cannot find '" + dataSourceName + "'");
            }
            return Collections.unmodifiableList(result);
        }

        @Override
        public void close() throws IOException {
            // do nothing
        }
    }

    private static final class NoOpSupplier implements MediatorConnectionSupplier {

        public static final NoOpSupplier INSTANCE = new NoOpSupplier();

        private NoOpSupplier() {
            // singleton
        }

        @Override
        public MediatorConnection getConnection() throws IOException {
            return NoOpConnection.INSTANCE;
        }
    }

    private static final class NoOpConnection implements MediatorConnection {

        public static final NoOpConnection INSTANCE = new NoOpConnection();

        private NoOpConnection() {
            // singleton
        }

        @Override
        public List<String> getDataSourceNames() throws IOException {
            return Collections.emptyList();
        }

        @Override
        public List<MediatorAlias> get(String dataSourceName) throws IOException {
            return Collections.emptyList();
        }

        @Override
        public void close() throws IOException {
            // do nothing
        }
    }
    //</editor-fold>
}
