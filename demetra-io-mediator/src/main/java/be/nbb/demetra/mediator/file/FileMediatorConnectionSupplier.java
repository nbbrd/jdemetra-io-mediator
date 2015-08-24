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
package be.nbb.demetra.mediator.file;

import be.nbb.demetra.mediator.MediatorConnection;
import be.nbb.demetra.mediator.MediatorConnectionSupplier;
import be.nbb.demetra.mediator.MediatorAlias;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Philippe Charles
 */
public final class FileMediatorConnectionSupplier extends MediatorConnectionSupplier {

    private final Path file;

    public FileMediatorConnectionSupplier(@Nonnull Path file) {
        this.file = file;
    }

    @Nonnull
    public Path getFile() {
        return file;
    }

    @Override
    public MediatorConnection getConnection() throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {
            return fromXml(stream);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static MediatorConnection fromXml(InputStream stream) throws IOException {
        ArrayListMultimap<String, MediatorAlias> data = ArrayListMultimap.create();
        for (MediatorAlias o : loadXml(stream)) {
            data.put(o.getDataSourceName(), o);
        }
        return fromMultimap(data);
    }

    private static MediatorConnection fromMultimap(final ListMultimap<String, MediatorAlias> data) {
        return new MediatorConnection() {
            @Override
            public List<String> getDataSourceNames() {
                return new ArrayList<>(data.keySet());
            }

            @Override
            public List<MediatorAlias> get(String dataSourceName) {
                return data.get(dataSourceName);
            }
        };
    }

    public static List<MediatorAlias> loadXml(InputStream stream) throws IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(ItemsBean.class);
            List<MediatorAlias> result = new ArrayList<>();
            ItemsBean tmp = (ItemsBean) context.createUnmarshaller().unmarshal(stream);
            if (tmp.item != null) {
                for (ItemBean o : tmp.item) {
                    result.add(new MediatorAlias(o.dataSourceName, o.dataSetName, o.uri));
                }
            }
            return result;
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
    }

    public static void storeXml(OutputStream stream, List<MediatorAlias> items) throws IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(ItemsBean.class);
            ItemsBean result = new ItemsBean();
            result.item = new ItemBean[items.size()];
            for (int i = 0; i < result.item.length; i++) {
                ItemBean o = new ItemBean();
                o.dataSourceName = items.get(i).getDataSourceName();
                o.dataSetName = items.get(i).getDataSetName();
                o.uri = items.get(i).getUri();
                result.item[i] = o;
            }
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(result, stream);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
    }

    @XmlRootElement(name = "items")
    public static final class ItemsBean {

        public ItemBean[] item;
    }

    public static final class ItemBean {

        @XmlAttribute
        public String dataSourceName;
        @XmlAttribute
        public String dataSetName;
        @XmlAttribute
        public String uri;
    }
    //</editor-fold>
}
