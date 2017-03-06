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
package internal.test;

import be.nbb.demetra.mediator.MediatorAlias;
import be.nbb.demetra.mediator.MediatorConnection;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Philippe Charles
 */
public final class FailingConnection implements MediatorConnection {

    public static final FailingConnection INSTANCE = new FailingConnection();

    private FailingConnection() {
    }

    @Override
    public List<String> getDataSourceNames() throws IOException {
        throw new IOException();
    }

    @Override
    public List<MediatorAlias> get(String dataSourceName) throws IOException {
        throw new IOException();
    }

    @Override
    public void close() throws IOException {
        throw new IOException();
    }
}
