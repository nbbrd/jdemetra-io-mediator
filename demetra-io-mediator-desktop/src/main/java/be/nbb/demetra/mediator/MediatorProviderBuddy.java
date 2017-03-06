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

import be.nbb.demetra.mediator.file.FileMediatorConnectionSupplier;
import com.google.common.base.Converter;
import com.google.common.base.Optional;
import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.tsproviders.AbstractDataSourceProviderBuddy;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.ext.FontAwesomeUtils;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nullable;
import javax.swing.JFileChooser;
import lombok.Data;
import org.jfree.ui.ExtensionFileFilter;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = IDataSourceProviderBuddy.class)
public class MediatorProviderBuddy extends AbstractDataSourceProviderBuddy implements IConfigurable {

    private final Configurator<MediatorProviderBuddy> configurator;

    public MediatorProviderBuddy() {
        this.configurator = createConfigurator();
    }

    @Override
    public String getProviderName() {
        return MediatorProvider.NAME;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return FontAwesomeUtils.getImage(FontAwesome.FA_RETWEET, type);
    }

    @Override
    public Image getIcon(DataSet dataSet, int type, boolean opened) {
        return FontAwesomeUtils.getImage(FontAwesome.FA_LINE_CHART, type);
    }

    @Override
    public Image getIcon(TsMoniker moniker, int type, boolean opened) {
        return FontAwesomeUtils.getImage(FontAwesome.FA_LINE_CHART, type);
    }

    @Override
    public Config getConfig() {
        return configurator.getConfig(this);
    }

    @Override
    public void setConfig(Config config) throws IllegalArgumentException {
        configurator.setConfig(this, config);
    }

    @Override
    public Config editConfig(Config config) throws IllegalArgumentException {
        return configurator.editConfig(config);
    }

    private Path getFile() {
        Optional<MediatorProvider> provider = TsProviders.lookup(MediatorProvider.class, MediatorProvider.NAME);
        if (provider.isPresent()) {
            MediatorConnectionSupplier connectionSupplier = provider.get().getConnectionSupplier();
            if (connectionSupplier instanceof FileMediatorConnectionSupplier) {
                return ((FileMediatorConnectionSupplier) connectionSupplier).getFile();
            }
        }
        return null;
    }

    private void setFile(Path file) {
        if (file != null) {
            Optional<MediatorProvider> provider = TsProviders.lookup(MediatorProvider.class, MediatorProvider.NAME);
            if (provider.isPresent()) {
                provider.get().setConnectionSupplier(new FileMediatorConnectionSupplier(file));
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Configuration">
    private static Configurator<MediatorProviderBuddy> createConfigurator() {
        return new BuddyConfigHandler().toConfigurator(new BuddyConfigConverter(), new BuddyConfigEditor());
    }

    @Data
    public static final class BuddyConfig {

        @Nullable
        private Path file;
    }

    private static final class BuddyConfigHandler extends BeanHandler<BuddyConfig, MediatorProviderBuddy> {

        @Override
        public BuddyConfig loadBean(MediatorProviderBuddy resource) {
            BuddyConfig result = new BuddyConfig();
            result.setFile(resource.getFile());
            return result;
        }

        @Override
        public void storeBean(MediatorProviderBuddy resource, BuddyConfig bean) {
            resource.setFile(bean.getFile());
        }
    }

    private static final class BuddyConfigConverter extends Converter<BuddyConfig, Config> {

        private final IParam<Config, String> file = Params.onString("", "file");

        @Override
        protected Config doForward(BuddyConfig a) {
            Config.Builder result = Config.builder(BuddyConfig.class.getName(), "INSTANCE", "20150814");
            Path tmp = a.getFile();
            if (tmp != null) {
                file.set(result, tmp.toString());
            }
            return result.build();
        }

        @Override
        protected BuddyConfig doBackward(Config b) {
            BuddyConfig result = new BuddyConfig();
            String tmp = file.get(b);
            if (!tmp.isEmpty()) {
                result.setFile(Paths.get(tmp));
            }
            return result;
        }
    }

    private static final class BuddyConfigEditor implements IBeanEditor {

        private final JFileChooser fileChooser;

        public BuddyConfigEditor() {
            this.fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new ExtensionFileFilter("XML file", "xml"));
        }

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                ((BuddyConfig) bean).setFile(fileChooser.getSelectedFile().toPath());
                return true;
            }
            return false;
        }
    }
    //</editor-fold>
}
