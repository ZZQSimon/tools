package cn.com.easyerp.module.importer;

public abstract class CustomImportDetail extends CustomImportData {
    private static int current_rowid = 0;

    private int rowid;

    private String id;
    private String amount_H;
    private String home_amount;
    private String sale_order_detail_id;
    private String item_id;
    private String customer_item_id;
    private String quantity;
    private String unit;
    private String unit_price;
    private String amount_D;
    private String currency_D;
    private String trade_type;
    private String voucher_id;

    public CustomImportDetail() {
        this.rowid = ++current_rowid;
    }

    public static void reset() {
        current_rowid = 0;
    }

    public int getRowid() {
        return this.rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount_H() {
        return this.amount_H;
    }

    public void setAmount_H(String amount_H) {
        this.amount_H = amount_H;
    }

    public String getHome_amount() {
        return this.home_amount;
    }

    public void setHome_amount(String home_amount) {
        this.home_amount = home_amount;
    }

    public String getSale_order_detail_id() {
        return this.sale_order_detail_id;
    }

    public void setSale_order_detail_id(String sale_order_detail_id) {
        this.sale_order_detail_id = sale_order_detail_id;
    }

    public String getItem_id() {
        return this.item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getCustomer_item_id() {
        return this.customer_item_id;
    }

    public void setCustomer_item_id(String customer_item_id) {
        this.customer_item_id = customer_item_id;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit_price() {
        return this.unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getAmount_D() {
        return this.amount_D;
    }

    public void setAmount_D(String amount_D) {
        this.amount_D = amount_D;
    }

    public String getCurrency_D() {
        return this.currency_D;
    }

    public void setCurrency_D(String currency_D) {
        this.currency_D = currency_D;
    }

    public String getTrade_type() {
        return this.trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getVoucher_id() {
        return this.voucher_id;
    }

    public void setVoucher_id(String voucher_id) {
        this.voucher_id = voucher_id;
    }

    public abstract int customerItemIdExcelIndex();

    public int customerItemColorExcelIndex() {
        return -1;
    }

    public abstract int quantityExcelIndex();

    public abstract int voucherIdExcelIndex();
}
