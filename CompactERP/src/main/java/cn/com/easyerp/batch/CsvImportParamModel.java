package cn.com.easyerp.batch;

public class CsvImportParamModel {
    private String table;
    private String column = "csv_file";
    private String charset = "GBK";
    private char delimiter = ',';
    private char quote = '"';
    private String tmpId = null;

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public char getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    public char getQuote() {
        return this.quote;
    }

    public void setQuote(char quote) {
        this.quote = quote;
    }

    public String getTmpId() {
        return this.tmpId;
    }

    public void setTmpId(String tmpId) {
        this.tmpId = tmpId;
    }
}
