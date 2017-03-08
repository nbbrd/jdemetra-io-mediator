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

import ec.tss.ITsProvider;
import ec.tss.tsproviders.IDataSourceProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = ITsProvider.class)
public final class MediatorProvider implements IDataSourceProvider {

    public static final String NAME = "MEDIATOR";

    @lombok.experimental.Delegate(types = IDataSourceProvider.class)
    private final InternalProvider delegate;

    public MediatorProvider() {
        this.delegate = new InternalProvider(LoggerFactory.getLogger(MediatorProvider.class), NAME);
    }

    @Nonnull
    public MediatorConnectionSupplier getConnectionSupplier() {
        return delegate.getConnectionSupplier();
    }

    public void setConnectionSupplier(@Nullable MediatorConnectionSupplier connectionSupplier) {
        delegate.setConnectionSupplier(connectionSupplier);
    }
}
