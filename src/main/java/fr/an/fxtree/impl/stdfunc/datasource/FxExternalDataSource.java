package fr.an.fxtree.impl.stdfunc.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

/**
 * abstract class for updating destination FxNode(s) with externally stored values
 * 
 *  cf sub-class FxDefaultExternalDataSource
 *  
 *  <PRE>
 *     dest (source) Tree...                        dest (result) Tree
 *      {                                           {
 *        .. { "@fx-eval": .. dataId="key1" }         .. { "value1"..}
 *        .. { "@fx-eval": .. dataId="key2" }   ==>   .. { "value2"..}
 *        .. { "@fx-eval": .. dataId="key2" }         .. { "value2"..}
 *        ..                                          ..  /\   /\                
 *      }                                           }      |    |              
 *                                                          \    -----------             
 *                                                           --------------- \
 *    +----------------------+                                              \ |
 *    | FxExternalDataSource |     +--------------+                          ||
 *    |- dataEntries      -- | --> | DataEntry    |     +-----------------+  || 
 *    +----------------------+     | - outputs -- | --> | NodeDataOutput  |  /|
 *              /\                 +--------------+     | - dataWriter -- |-- /
 *              |                         /\            | - currDataValue-|--
 *              |                         |             +-----------------+
 *              |                         |
 *  +--------------------------+  +------------------+
 *  | DefaultExternalDataSource|  | DefaultDataEntry |
 *  +--------------------------+  | -dataValue    -- | --> { "value1" ..}
 *                                +------------------+                   
 *  </PRE>
 */
public abstract class FxExternalDataSource {

    private Map<String,DataEntry> dataEntries = new HashMap<>();
    
    // ------------------------------------------------------------------------

    public FxExternalDataSource() {
    }

    // ------------------------------------------------------------------------

    public void addDataOutput(String dataId, FxChildWriter dest) {
        DataEntry dataEntry = getOrRegisterDataEntry(dataId);
        dataEntry.addOutput(dest);
    }

    protected DataEntry getOrRegisterDataEntry(String dataId) {
        DataEntry dataEntry = dataEntries.get(dataId);
        if (dataEntry == null) {
            dataEntry = createDataEntry(dataId);
            dataEntries.put(dataId, dataEntry);
        }
        return dataEntry;
    }
    
    protected DataEntry createDataEntry(String dataId) {
        return new DataEntry(this, dataId);
    }
    
    protected abstract FxNode getDataValue(DataEntry dataEntry);
    
    
    // ------------------------------------------------------------------------

    protected static class DataEntry {
        
        private final FxExternalDataSource owner;
        private final String dataId;
        protected List<NodeDataOutput> outputs = new ArrayList<>();
        
        public DataEntry(FxExternalDataSource owner, String dataId) {
            this.owner = owner;
            this.dataId = dataId;
        }

        public String getDataId() {
            return dataId;
        }
        
        public void addOutput(FxChildWriter dest) {
            NodeDataOutput output = new NodeDataOutput(this, dest);
            outputs.add(output);
            FxNode dataValue = owner.getDataValue(this);
            output.writeDataValue(dataValue);
        }
        
    }
    
    
    protected static class NodeDataOutput {
        
        protected final DataEntry owner;
        protected FxChildWriter dataWriter;
        protected FxNode currDataValue;
        
        public NodeDataOutput(DataEntry owner, FxChildWriter dataWriter) {
            this.owner = owner;
            this.dataWriter = dataWriter;
        }
        
        public void writeDataValue(FxNode dataValue) {
            if (dataValue != null) {
                if (currDataValue != null) {
                    // TODO may use deep update visitor, instead of remove + deep copy  
                    dataWriter.remove();
                    currDataValue = null;
                }
                FxNodeCopyVisitor copyVisitor = new FxNodeCopyVisitor();
                currDataValue = dataValue.accept(copyVisitor, dataWriter);
            } else {
                if (currDataValue != null) {
                    dataWriter.remove();
                    currDataValue = null;
                }
            }
        }
    }
    
}
