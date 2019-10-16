package cn.com.easyerp.module.importer;

public class ImporterCustDaoModel {
    String out_warehouse;
    String exchangecurrency;
    String tax_type;
    String taxrate_id;

    public String getOut_warehouse() {
        return this.out_warehouse;
    }

    public void setOut_warehouse(String out_warehouse) {
        this.out_warehouse = out_warehouse;
    }

    public String getExchangecurrency() {
        return this.exchangecurrency;
    }

    public void setExchangecurrency(String exchangecurrency) {
        this.exchangecurrency = exchangecurrency;
    }

    public String getTax_type() {
        return this.tax_type;
    }

    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
    }

    public String getTaxrate_id() {
        return this.taxrate_id;
    }

    public void setTaxrate_id(String taxrate_id) {
        this.taxrate_id = taxrate_id;
    }
}
