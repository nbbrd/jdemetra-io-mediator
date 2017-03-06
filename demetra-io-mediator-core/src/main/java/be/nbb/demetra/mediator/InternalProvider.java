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
import com.google.common.collect.ImmutableList;
import ec.tss.TsAsyncMode;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.utils.AbstractDataSourceProvider;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;

/**
 *
 * @author Philippe Charles
 */
final class InternalProvider extends AbstractDataSourceProvider<List<MediatorAlias>> {

    static final String VERSION = "20150814";
    private static final IParam<DataSource, String> X_NAME = Params.onString("", "x");
    private static final IParam<DataSet, String> Y_NAME = Params.onString("", "y");

    private MediatorConnectionSupplier connectionSupplier;

    InternalProvider(Logger logger, String name) {
        super(logger, name, TsAsyncMode.Once);
        this.connectionSupplier = Mediators.noOpSupplier();
    }

    @Nonnull
    public MediatorConnectionSupplier getConnectionSupplier() {
        return connectionSupplier;
    }

    public void setConnectionSupplier(@Nullable MediatorConnectionSupplier connectionSupplier) {
        this.connectionSupplier = connectionSupplier != null ? connectionSupplier : Mediators.noOpSupplier();
        updateDataSources();
    }

    private void updateDataSources() {
        try (MediatorConnection conn = connectionSupplier.getConnection()) {
            support.closeAll();
            DataSource.Builder b = DataSource.builder(getSource(), VERSION);
            for (String o : conn.getDataSourceNames()) {
                X_NAME.set(b, o);
                support.open(b.build());
            }
            clearCache();
        } catch (IOException ex) {
            logger.error("While loading datasources", ex);
        }
    }

    @Override
    protected List<MediatorAlias> loadFromDataSource(DataSource key) throws IOException {
        try (MediatorConnection conn = connectionSupplier.getConnection()) {
            return conn.get(X_NAME.get(key));
        }
    }

    @Override
    protected void fillCollection(TsCollectionInformation info, DataSource dataSource) throws IOException {
        DataSet.Builder b = DataSet.builder(dataSource, DataSet.Kind.SERIES);
        for (MediatorAlias o : loadFromDataSource(dataSource)) {
            Y_NAME.set(b, o.getDataSetName());
            TsInformation tsInformation = newTsInformation(b.build(), info.type);
            fill(o, tsInformation);
            info.items.add(tsInformation);
        }
    }

    @Override
    protected void fillCollection(TsCollectionInformation info, DataSet dataSet) throws IOException {
        throw new IOException();
    }

    @Override
    protected void fillSeries(TsInformation info, DataSet dataSet) throws IOException {
        for (MediatorAlias o : loadFromDataSource(dataSet.getDataSource())) {
            if (o.getDataSetName().equals(Y_NAME.get(dataSet))) {
                fill(o, info);
                return;
            }
        }
        info.invalidDataCause = "Series not found: '" + info.moniker.getId() + "'";
    }

    private void fill(MediatorAlias item, TsInformation info) {
        DataSet delegateDataSet = DataSet.uriParser().parse(item.getUri());
        if (delegateDataSet != null && delegateDataSet.getKind().equals(DataSet.Kind.SERIES)) {
            IDataSourceProvider delegateProvider = TsProviders.lookup(IDataSourceProvider.class, delegateDataSet).orNull();
            if (delegateProvider != null) {
                TsMoniker save = info.moniker;
                info.moniker = delegateProvider.toMoniker(delegateDataSet);
                delegateProvider.get(info);
                info.moniker = save;
            } else {
                info.invalidDataCause = "Provider '" + delegateDataSet.getDataSource().getProviderName() + "' not found";
            }
        } else {
            info.invalidDataCause = "Invalid URI";
        }
    }

    @Override
    public String getDisplayName() {
        return "Mediator";
    }

    @Override
    public List<DataSet> children(DataSource dataSource) throws IllegalArgumentException, IOException {
        ImmutableList.Builder<DataSet> result = ImmutableList.builder();
        DataSet.Builder b = DataSet.builder(dataSource, DataSet.Kind.SERIES);
        for (MediatorAlias o : loadFromDataSource(dataSource)) {
            Y_NAME.set(b, o.getDataSetName());
            result.add(b.build());
        }
        return result.build();
    }

    @Override
    public List<DataSet> children(DataSet parent) throws IllegalArgumentException, IOException {
        return Collections.emptyList();
    }

    @Override
    public String getDisplayName(DataSource dataSource) throws IllegalArgumentException {
        return X_NAME.get(dataSource);
    }

    @Override
    public String getDisplayName(DataSet dataSet) throws IllegalArgumentException {
        return X_NAME.get(dataSet.getDataSource()) + " \u25b6 " + Y_NAME.get(dataSet);
    }

    @Override
    public String getDisplayNodeName(DataSet dataSet) throws IllegalArgumentException {
        return Y_NAME.get(dataSet);
    }
}
