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
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
public abstract class MediatorConnectionSupplier {

    @Nonnull
    abstract public MediatorConnection getConnection() throws IOException;

    @Nonnull
    public static MediatorConnectionSupplier noOp() {
        return new NoOpSupplier();
    }

    @Nonnull
    @VisibleForTesting
    public static MediatorConnectionSupplier failing() {
        return new FailingSupplier();
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final class NoOpSupplier extends MediatorConnectionSupplier {

        public static final NoOpSupplier INSTANCE = new NoOpSupplier();

        private NoOpSupplier() {
            // singleton
        }

        @Override
        public MediatorConnection getConnection() throws IOException {
            return MediatorConnection.noOp();
        }
    }

    private static final class FailingSupplier extends MediatorConnectionSupplier {

        public static final FailingSupplier INSTANCE = new FailingSupplier();

        private FailingSupplier() {
            // singleton
        }

        @Override
        public MediatorConnection getConnection() throws IOException {
            throw new IOException();
        }
    }
    //</editor-fold>
}
