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

import ec.tstoolkit.design.VisibleForTesting;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
public abstract class MediatorConnection implements AutoCloseable {

    @Nonnull
    abstract public List<String> getDataSourceNames() throws IOException;

    @Nonnull
    abstract public List<MediatorAlias> get(@Nonnull String dataSourceName) throws IOException;

    @Override
    public void close() throws IOException {
    }

    @Nonnull
    public static MediatorConnection noOp() {
        return NoOpConnection.INSTANCE;
    }

    @Nonnull
    @VisibleForTesting
    public static MediatorConnection failing() {
        return FailingConnection.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final class NoOpConnection extends MediatorConnection {

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
    }

    private static final class FailingConnection extends MediatorConnection {

        public static final FailingConnection INSTANCE = new FailingConnection();

        private FailingConnection() {
            // singleton
        }

        @Override
        public List<String> getDataSourceNames() throws IOException {
            throw new IOException();
        }

        @Override
        public List<MediatorAlias> get(String dataSourceName) throws IOException {
            throw new IOException();
        }
    }
    //</editor-fold>
}
