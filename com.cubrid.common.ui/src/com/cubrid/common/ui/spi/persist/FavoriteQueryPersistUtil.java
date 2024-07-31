package com.cubrid.common.ui.spi.persist;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.dialog.AddQueryToFavoriteDialog;
import com.cubrid.common.ui.common.dialog.SelectWorkspaceDialog;
import com.cubrid.common.ui.query.control.BatchRunComposite;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;

public class FavoriteQueryPersistUtil {
    private static FavoriteQueryPersistUtil instance = null;
    private static final String LIST_ID = "BatchRunList";
    private final List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

    private FavoriteQueryPersistUtil() {
        loadBatchRunList();
    }

    public static FavoriteQueryPersistUtil getInstance() {
        if (instance == null) {
            instance = new FavoriteQueryPersistUtil();
            instance.loadBatchRunList();
        }
        return instance;
    }

    public void loadBatchRunList() {
        IXMLMemento memento = PersistUtils.getXMLMemento(BatchRunComposite.ID, LIST_ID);
        if (memento == null) {
            return;
        }
        try {
            listData.clear();
            for (IXMLMemento xmlMemento : memento.getChildren("BatchRun")) {
                Map<String, String> data = new HashMap<String, String>();
                data.put("0", "");
                data.put("1", xmlMemento.getString("filename"));
                data.put("2", xmlMemento.getString("memo"));
                data.put("3", xmlMemento.getString("regdate"));
                data.put("4", xmlMemento.getString("directory"));
                data.put("5", xmlMemento.getString("charset"));
                data.put("6", xmlMemento.getString("managed"));
                listData.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveBatchRunList() {
        XMLMemento memento = XMLMemento.createWriteRoot("BatchRunList");
        for (Iterator<Map<String, String>> it = listData.iterator(); it.hasNext(); ) {
            IXMLMemento batchRunXMLMemento = memento.createChild("BatchRun");
            Map<String, String> data = it.next();
            batchRunXMLMemento.putString("filename", StringUtil.nvl(data.get("1")));
            batchRunXMLMemento.putString("memo", StringUtil.nvl(data.get("2")));
            batchRunXMLMemento.putString("regdate", StringUtil.nvl(data.get("3")));
            batchRunXMLMemento.putString("directory", StringUtil.nvl(data.get("4")));
            batchRunXMLMemento.putString("charset", StringUtil.nvl(data.get("5")));
            batchRunXMLMemento.putString("managed", StringUtil.nvl(data.get("6")));
        }
        PersistUtils.saveXMLMemento(BatchRunComposite.ID, LIST_ID, memento);
    }

    public void addFavorite(
            String basepath, String filename, String memo, String charset, boolean managed) {
        String datetime = DateUtil.getDatetimeString(new Date().getTime(), "yyyy-MM-dd HH:mm");
        Map<String, String> data = new HashMap<String, String>();
        data.put("1", filename);
        data.put("2", memo);
        data.put("3", datetime);
        data.put("4", basepath);
        data.put("5", charset);
        data.put("6", managed ? "Y" : "N");
        listData.add(data);
        saveBatchRunList();
    }

    public void addFavoriteQuery(String sql) {
        String charset = StringUtil.getDefaultCharset();
        String basepath =
                SelectWorkspaceDialog.getLastSetWorkspaceDirectory() + File.separator + "sql";
        AddQueryToFavoriteDialog dialog =
                new AddQueryToFavoriteDialog(
                        Display.getDefault().getActiveShell(), basepath, sql, charset);
        if (dialog.open() != IDialogConstants.OK_ID) {
            return;
        }

        addFavorite(dialog.getBasepath(), dialog.getFilename(), dialog.getMemo(), charset, true);
    }

    public boolean remove(int index, boolean removeWithFile) {
        if (index >= listData.size()) {
            return false;
        }
        if (removeWithFile) {
            Map<String, String> item = listData.get(index);
            String filepath = item.get("4") + File.separator + item.get("1");
            FileUtil.delete(filepath);
        }
        listData.remove(index);
        return true;
    }

    public List<Map<String, String>> getListData() {
        return listData;
    }
}
