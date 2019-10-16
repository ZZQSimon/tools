package cn.com.easyerp.DeployTool.service;

public class Frontdata {
    private PassageMap passageMap;

    public PassageMap getPassageMap() {
        return this.passageMap;
    }

    private PassageRowMap passageRowMap;
    private RowFormula rowFormula;

    public void setPassageMap(PassageMap passageMap) {
        this.passageMap = passageMap;
    }

    public PassageRowMap getPassageRowMap() {
        return this.passageRowMap;
    }

    public void setPassageRowMap(PassageRowMap passageRowMap) {
        this.passageRowMap = passageRowMap;
    }

    public RowFormula getRowFormula() {
        return this.rowFormula;
    }

    public void setRowFormula(RowFormula rowFormula) {
        this.rowFormula = rowFormula;
    }
}
