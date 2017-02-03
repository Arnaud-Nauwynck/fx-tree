package fr.an.fxtree.impl.stdfunc.datasource;

import fr.an.fxtree.model.FxNode;

/**
 * default implementation of FxExternalDataSource, to keep value in memory (DefaultDataEntry)
 */
public class FxDefaultExternalDataSource extends FxExternalDataSource {

    // ------------------------------------------------------------------------

    public FxDefaultExternalDataSource() {
    }

    // ------------------------------------------------------------------------

    public void putDataValue(String dataId, FxNode value) {
        DefaultDataEntry dataEntry = (DefaultDataEntry) getOrRegisterDataEntry(dataId);
        dataEntry.setDataValue(value);
    }

    public FxNode getDataValue(String dataId) {
        DefaultDataEntry dataEntry = (DefaultDataEntry) getOrRegisterDataEntry(dataId);
        return dataEntry.getDataValue(); // unsafe.. do not modify return value (should return a copy / read-only wrapper)
    }

    // override abstract FxExternalDataSource
    // ------------------------------------------------------------------------

    @Override
    protected DataEntry createDataEntry(String dataId) {
        return new DefaultDataEntry(this, dataId);
    }

    @Override
    protected FxNode getDataValue(DataEntry dataEntry) {
        return ((DefaultDataEntry) dataEntry).getDataValue();
    }

    // ------------------------------------------------------------------------

    protected static class DefaultDataEntry extends DataEntry {
        protected FxNode dataValue;

        public DefaultDataEntry(FxExternalDataSource owner, String dataId) {
            super(owner, dataId);
        }

        public FxNode getDataValue() {
            return dataValue;
        }

        public void setDataValue(FxNode dataValue) {
            if (this.dataValue == dataValue) return;
            this.dataValue = dataValue;
            for(NodeDataOutput output : outputs) {
                output.writeDataValue(dataValue);
            }
        }
    }

}
