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
package be.nbb.demetra.mediator;

import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ui.tsproviders.CollectionNode;
import ec.nbdemetra.ui.tsproviders.DataSetNode;
import ec.nbdemetra.ui.tsproviders.SeriesNode;
import ec.tss.tsproviders.DataSet;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * @author Philippe Charles
 */
@ActionID(category = "Edit", id = "be.nbb.demetra.mediator.CopyUriAction")
@ActionRegistration(displayName = "#CTL_CopyUriAction", lazy = false)
@ActionReferences({
    @ActionReference(path = CollectionNode.ACTION_PATH, position = 1705, separatorBefore = 1700),
    @ActionReference(path = SeriesNode.ACTION_PATH, position = 1705, separatorBefore = 1700)
})
@Messages("CTL_CopyUriAction=Copy URI")
public final class CopyUriAction extends SingleNodeAction<DataSetNode> {

    public CopyUriAction() {
        super(DataSetNode.class);
    }

    @Override
    protected void performAction(DataSetNode activatedNode) {
        String uriAsString = DataSet.uriFormatter().formatAsString(activatedNode.getLookup().lookup(DataSet.class));
        getClipboard().setContents(new StringSelection(uriAsString), null);
    }

    @Override
    protected boolean enable(DataSetNode activatedNode) {
        return true;
    }

    @Override
    public String getName() {
        return Bundle.CTL_CopyUriAction();
    }

    private static Clipboard getClipboard() {
        return Toolkit.getDefaultToolkit().getSystemClipboard();
    }
}
