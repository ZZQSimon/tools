package cn.com.easyerp.module.importer;

public class ImportRowModel {
    private String rowid;
    private String id;
    private String import_date;
    private String order_date;
    private String customer_id;
    private String receiver_id;
    private String request_id;
    private String receive_indicate_date;
    private String classes;
    private String deliver_date;
    private String receive_time;
    private String delivery_no;
    private String receive_warehouse;
    private String out_warehouse;
    private String status;
    private String currency_H;
    private String exchange_rate;
    private String exchange_type;
    private String amount_H;
    private String home_amount;
    private String cre_user;
    private String sale_order_detail_id;
    private String item_id;
    private String customer_item_id;
    private String quantity;
    private String unit;
    private String unit_price;
    private String amount_D;
    private String currency_D;
    private String tax;
    private String tax_type;
    private String trade_type;
    private String voucher_id;

    public String getRowid() {
        return this.rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    public String getImport_date() {
        return this.import_date;
    }

    public void setImport_date(String import_date) {
        this.import_date = import_date;
    }

    public String getOrder_date() {
        return this.order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getCustomer_id() {
        return this.customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getReceiver_id() {
        return this.receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getRequest_id() {
        return this.request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getReceive_indicate_date() {
        return this.receive_indicate_date;
    }

    public void setReceive_indicate_date(String receive_indicate_date) {
        this.receive_indicate_date = receive_indicate_date;
    }

    public String getClasses() {
        return this.classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getDeliver_date() {
        return this.deliver_date;
    }

    public void setDeliver_date(String deliver_date) {
        this.deliver_date = deliver_date;
    }

    public String getReceive_time() {
        return this.receive_time;
    }

    public void setReceive_time(String receive_time) {
        this.receive_time = receive_time;
    }

    public String getDelivery_no() {
        return this.delivery_no;
    }

    public void setDelivery_no(String delivery_no) {
        this.delivery_no = delivery_no;
    }

    public String getReceive_warehouse() {
        return this.receive_warehouse;
    }

    public void setReceive_warehouse(String receive_warehouse) {
        this.receive_warehouse = receive_warehouse;
    }

    public String getOut_warehouse() {
        return this.out_warehouse;
    }

    public void setOut_warehouse(String out_warehouse) {
        this.out_warehouse = out_warehouse;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExchange_rate() {
        return this.exchange_rate;
    }

    public void setExchange_rate(String exchange_rate) {
        this.exchange_rate = exchange_rate;
    }

    public String getExchange_type() {
        return this.exchange_type;
    }

    public void setExchange_type(String exchange_type) {
        this.exchange_type = exchange_type;
    }

    public String getHome_amount() {
        return this.home_amount;
    }

    public void setHome_amount(String home_amount) {
        this.home_amount = home_amount;
    }

    public String getCre_user() {
        return this.cre_user;
    }

    public void setCre_user(String cre_user) {
        this.cre_user = cre_user;
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

    public String getTrade_type() {
        return this.trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit_price() {
        return this.unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getTax() {
        return this.tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTax_type() {
        return this.tax_type;
    }

    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
    }

    public String getCurrency_H() {
        return this.currency_H;
    }

    public void setCurrency_H(String currency_H) {
        this.currency_H = currency_H;
    }

    public String getAmount_H() {
        return this.amount_H;
    }

    public void setAmount_H(String amount_H) {
        this.amount_H = amount_H;
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

    public String getSale_order_detail_id() {
        return this.sale_order_detail_id;
    }

    public void setSale_order_detail_id(String sale_order_detail_id) {
        this.sale_order_detail_id = sale_order_detail_id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVoucher_id() {
        return this.voucher_id;
    }

    public void setVoucher_id(String voucher_id) {
        this.voucher_id = voucher_id;
    }

    public boolean isSameOrderNissan(String order_date, String receive_indicate_date, String receive_time,
            String receive_warehouse) {
        if (this.order_date == null || this.receive_indicate_date == null || this.receive_time == null
                || this.receive_warehouse == null) {
            return false;
        }
        if (!this.order_date.equals(order_date) || !this.receive_indicate_date.equals(receive_indicate_date)
                || !this.receive_time.equals(receive_time) || !this.receive_warehouse.equals(receive_warehouse))
            return false;
        return true;
    }

    public boolean isSameOrderToyota(String receive_indicate_date, String receive_warehouse) {
        if (this.receive_indicate_date == null || this.receive_warehouse == null) {
            return false;
        }
        if (!this.receive_indicate_date.equals(receive_indicate_date)
                || !this.receive_warehouse.equals(receive_warehouse))
            return false;
        return true;
    }

    public boolean isSameOrderHonda1(String deliver_date, String receive_time, String receiver_id) {
        if (this.deliver_date == null || this.receive_time == null || this.receiver_id == null) {
            return false;
        }
        if (!this.deliver_date.equals(deliver_date) || !this.receive_time.equals(receive_time)
                || !this.receiver_id.equals(receiver_id)) {
            return false;
        }
        return true;
    }

    public boolean isSameOrderHonda2(String receive_indicate_date, String receive_time, String receive_warehouse) {
        if (this.receive_indicate_date == null || this.receive_time == null || this.receive_warehouse == null) {
            return false;
        }
        if (!this.receive_indicate_date.equals(receive_indicate_date) || !this.receive_time.equals(receive_time)
                || !this.receive_warehouse.equals(receive_warehouse)) {
            return false;
        }
        return true;
    }
}
