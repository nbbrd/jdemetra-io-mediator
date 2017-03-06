/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.nbb.demetra.mediator;

import be.nbb.demetra.mediator.file.FileMediatorConnectionSupplier;
import com.google.common.base.Optional;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsMoniker;
import ec.tss.datatransfer.TssTransferHandler;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tss.datatransfer.impl.LocalObjectTssTransferHandler;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.TsProviders;
import ec.util.grid.swing.XTable;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.ext.FontAwesomeUtils;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.COPY;
import lombok.Builder;
import lombok.Data;
import org.jfree.ui.ExtensionFileFilter;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.swing.etable.ETableColumnModel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//be.nbb.demetra.mediator//Mediator//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "MediatorTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "be.nbb.demetra.mediator.MediatorTopComponent")
@ActionReference(path = "Menu/Tools" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_MediatorAction"
)
@Messages({
    "CTL_MediatorAction=Mediator alias editor",
    "CTL_MediatorTopComponent=Mediator alias editor",
    "HINT_MediatorTopComponent=This is a Mediator window"
})
public final class MediatorTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager mgr = new ExplorerManager();
    private final JFileChooser fileChooser;

    public MediatorTopComponent() {
        initComponents();
        setName(Bundle.CTL_MediatorTopComponent());
        setToolTipText(Bundle.HINT_MediatorTopComponent());
        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));

        setIcon(loadButton, FontAwesome.FA_FILE_TEXT_O);
        setIcon(storeButton, FontAwesome.FA_FLOPPY_O);
        setIcon(checkButton, FontAwesome.FA_CHECK_SQUARE_O).setEnabled(false);
        setIcon(addButton, FontAwesome.FA_PLUS).setEnabled(false);
        setIcon(removeButton, FontAwesome.FA_MINUS).setEnabled(false);
        setIcon(moveUpButton, FontAwesome.FA_CARET_UP).setEnabled(false);;
        setIcon(moveDownButton, FontAwesome.FA_CARET_DOWN).setEnabled(false);

        fileChooser = new FileChooserBuilder(MediatorTopComponent.class)
                .setFileFilter(new ExtensionFileFilter("XML file", "xml"))
                .createFileChooser();

        mgr.setRootContext(new AliasRootNode(new Index.ArrayChildren()));
        mgr.addVetoableChangeListener(new VetoableChangeListener() {
            @Override
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    Node[] nodes = (Node[]) evt.getNewValue();
//                    removeButton.setEnabled(nodes.length > 0);
//                    Index.ArrayChildren children = (Index.ArrayChildren) mgr.getRootContext().getChildren();
//                    moveUpButton.setEnabled(nodes.length == 1 && children.indexOf(nodes[0]) != 0);
//                    moveDownButton.setEnabled(nodes.length == 1 && children.indexOf(nodes[0]) != children.getNodesCount() - 1);
                }
            }
        });

        outlineView1.setTransferHandler(new UriTransferHandler());
        outlineView1.getOutline().setRootVisible(false);
        outlineView1.setPropertyColumns("uri", "Uri");
        ETableColumnModel columnModel = (ETableColumnModel) outlineView1.getOutline().getColumnModel();
        columnModel.getColumn(0).setHeaderValue("Alias");
//        columnModel.setColumnHidden(columnModel.getColumn(0), true);
        XTable.setWidthAsPercentages(outlineView1.getOutline(), .25, .75);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        outlineView1 = new org.openide.explorer.view.OutlineView();
        jToolBar1 = new javax.swing.JToolBar();
        loadButton = new javax.swing.JButton();
        storeButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        checkButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());
        add(outlineView1, java.awt.BorderLayout.CENTER);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(loadButton, org.openide.util.NbBundle.getMessage(MediatorTopComponent.class, "MediatorTopComponent.loadButton.text")); // NOI18N
        loadButton.setFocusable(false);
        loadButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(loadButton);

        org.openide.awt.Mnemonics.setLocalizedText(storeButton, org.openide.util.NbBundle.getMessage(MediatorTopComponent.class, "MediatorTopComponent.storeButton.text")); // NOI18N
        storeButton.setFocusable(false);
        storeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        storeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        storeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                storeButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(storeButton);
        jToolBar1.add(jSeparator1);

        org.openide.awt.Mnemonics.setLocalizedText(checkButton, org.openide.util.NbBundle.getMessage(MediatorTopComponent.class, "MediatorTopComponent.checkButton.text")); // NOI18N
        checkButton.setFocusable(false);
        checkButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        checkButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(checkButton);
        jToolBar1.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(MediatorTopComponent.class, "MediatorTopComponent.addButton.text")); // NOI18N
        addButton.setFocusable(false);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(addButton);

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(MediatorTopComponent.class, "MediatorTopComponent.removeButton.text")); // NOI18N
        removeButton.setFocusable(false);
        removeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(removeButton);

        org.openide.awt.Mnemonics.setLocalizedText(moveUpButton, org.openide.util.NbBundle.getMessage(MediatorTopComponent.class, "MediatorTopComponent.moveUpButton.text")); // NOI18N
        moveUpButton.setFocusable(false);
        moveUpButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveUpButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(moveUpButton);

        org.openide.awt.Mnemonics.setLocalizedText(moveDownButton, org.openide.util.NbBundle.getMessage(MediatorTopComponent.class, "MediatorTopComponent.moveDownButton.text")); // NOI18N
        moveDownButton.setFocusable(false);
        moveDownButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveDownButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(moveDownButton);

        add(jToolBar1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (InputStream stream = Files.newInputStream(fileChooser.getSelectedFile().toPath())) {
                Index.ArrayChildren children = new Index.ArrayChildren();
                for (MediatorAlias o : FileMediatorConnectionSupplier.loadXml(stream)) {
                    children.add(new Node[]{new AliasNode(new AliasBean(o.getDataSourceName(), o.getDataSetName(), o.getUri()))});
                }
                mgr.setRootContext(new AliasRootNode(children));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_loadButtonActionPerformed

    private void storeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_storeButtonActionPerformed
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (OutputStream stream = Files.newOutputStream(fileChooser.getSelectedFile().toPath())) {
                List<MediatorAlias> tmp = new ArrayList<>();
                for (Node o : mgr.getRootContext().getChildren().getNodes()) {
                    AliasBean bean = o.getLookup().lookup(AliasBean.class);
                    tmp.add(new MediatorAlias(bean.getDataSourceName(), bean.getDataSetName(), bean.getUri()));
                }
                FileMediatorConnectionSupplier.storeXml(stream, tmp);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_storeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton checkButton;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton loadButton;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private org.openide.explorer.view.OutlineView outlineView1;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton storeButton;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    void writeProperties(java.util.Properties p) {
    }

    void readProperties(java.util.Properties p) {
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    private static JButton setIcon(JButton button, FontAwesome fa) {
        button.setText("");
        button.setIcon(FontAwesomeUtils.getIcon(fa, BeanInfo.ICON_MONO_16x16));
        return button;
    }

    private static String toUri(TsMoniker moniker) {
        Optional<IDataSourceProvider> provider = TsProviders.lookup(IDataSourceProvider.class, moniker);
        if (provider.isPresent()) {
            DataSet dataSet = provider.get().toDataSet(moniker);
            return DataSet.uriFormatter().formatAsString(dataSet);
        }
        return null;
    }

    private final class UriTransferHandler extends TransferHandler {

        @Nullable
        private TsCollection peekCollection(TransferHandler.TransferSupport support) {
            TssTransferHandler localObjectHandler = Lookup.getDefault().lookup(LocalObjectTssTransferHandler.class);
            if (localObjectHandler != null) {
                DataFlavor dataFlavor = localObjectHandler.getDataFlavor();
                if (support.isDataFlavorSupported(dataFlavor)) {
                    try {
                        Object data = support.getTransferable().getTransferData(dataFlavor);
                        if (localObjectHandler.canImportTsCollection(data)) {
                            return localObjectHandler.importTsCollection(data);
                        }
                    } catch (UnsupportedFlavorException | IOException ex) {
                    }
                    return null;
                }
            }
            return null;
        }

        private boolean mayChangeContent(TransferHandler.TransferSupport support) {
            TsCollection newContent = peekCollection(support);
            if (newContent != null) {
                for (Ts o : newContent) {
                    if (isValid(o)) {
                        return true; // YES
                    }
                }
                return false; // NO
            }
            return true; // MAYBE
        }

        private boolean isValid(Ts o) {
            return !o.getMoniker().isAnonymous() && !MediatorProvider.NAME.equals(o.getMoniker().getSource());
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            boolean result = TssTransferSupport.getDefault().canImport(support.getDataFlavors())
                    && mayChangeContent(support);
            if (result && support.isDrop()) {
                support.setDropAction(COPY);
            }
            return result;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            TsCollection col = TssTransferSupport.getDefault().toTsCollection(support.getTransferable());
            if (col != null) {
                if (!col.isEmpty()) {
                    Index.ArrayChildren children = (Index.ArrayChildren) mgr.getRootContext().getChildren();
                    List<Node> nodes = new ArrayList<>();
                    AliasBean.AliasBeanBuilder b = new AliasBean.AliasBeanBuilder().dataSourceName("Default");
                    for (Ts o : col) {
                        if (isValid(o)) {
                            nodes.add(new AliasNode(b.dataSetName(o.getName()).uri(toUri(o.getMoniker())).build()));
                        }
                    }
                    children.add(nodes.toArray(new Node[0]));
                }
                return true;
            }
            return false;
        }
    }

    private static final class AliasRootNode extends AbstractNode {

        public AliasRootNode(Index.ArrayChildren children) {
            super(children);
        }
    }

    //https://github.com/akullpp/awesome-java
    private static final class AliasNode extends AbstractNode implements PropertyChangeListener {

        public AliasNode(AliasBean item) {
            super(Children.LEAF, Lookups.singleton(item));
            setDisplayName(item.getDataSourceName() + " \u25b6 " + item.getDataSetName());
            item.addPropertyChangeListener(WeakListeners.propertyChange(this, item));
        }

        @Override
        public Image getIcon(int type) {
            return FontAwesomeUtils.getImage(FontAwesome.FA_LINE_CHART, type);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        protected Sheet createSheet() {
            AliasBean bean = getLookup().lookup(AliasBean.class);
            Sheet result = super.createSheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();
            b.with(String.class).select(bean, "dataSourceName").display("DataSource name").add();
            b.with(String.class).select(bean, "dataSetName").display("DataSet name").add();
            b.with(String.class).select(bean, "uri").display("URI").add();
            result.put(b.build());
            return result;
        }

        @Override
        public boolean canCut() {
            return true;
        }

        @Override
        public boolean canCopy() {
            return true;
        }

        @Override
        public boolean canDestroy() {
            return true;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            AliasBean bean = getLookup().lookup(AliasBean.class);
            switch (evt.getPropertyName()) {
                case "dataSourceName":
                    setDisplayName(bean.dataSourceName + " > " + bean.dataSetName);
                    break;
            }
        }
    }

    @Data
    @Builder
    public static final class AliasBean {

        private String dataSourceName;
        private String dataSetName;
        private String uri;

        private transient final PropertyChangeSupport support = new PropertyChangeSupport(this);

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            support.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            support.removePropertyChangeListener(listener);
        }

        public void setDataSourceName(String dataSourceName) {
            String old = this.dataSourceName;
            this.dataSourceName = dataSourceName;
            support.firePropertyChange("dataSourceName", old, this.dataSourceName);
        }

        public void setDataSetName(String dataSetName) {
            String old = this.dataSetName;
            this.dataSetName = dataSetName;
            support.firePropertyChange("dataSetName", old, this.dataSetName);
        }

        public void setUri(String uri) {
            String old = this.uri;
            this.uri = uri;
            support.firePropertyChange("uri", old, this.uri);
        }
    }
}
