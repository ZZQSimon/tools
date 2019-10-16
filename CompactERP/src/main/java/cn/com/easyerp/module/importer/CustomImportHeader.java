package cn.com.easyerp.module.importer;

public class CustomImportHeader extends CustomImportData {
    protected String id;
    protected String import_date;
    protected String order_date;
    protected String customer_id;
    protected String receiver_id;
    protected String request_id;
    protected String receive_indicate_date;
    protected String classes;
    protected String deliver_date;
    protected String receive_time;
    protected String delivery_no;
    protected String receive_warehouse;
    protected String out_warehouse;
    protected String status;
    protected String currency_H;
    protected String exchange_rate;
    protected String exchange_type;
    protected String cre_user;
    protected String tax;
    protected String tax_type;
    private double amount = 0.0D;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCurrency_H() {
        return this.currency_H;
    }

    public void setCurrency_H(String currency_H) {
        this.currency_H = currency_H;
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

    public String getCre_user() {
        return this.cre_user;
    }

    public void setCre_user(String cre_user) {
        this.cre_user = cre_user;
    }

    public String getTax_type() {
        return this.tax_type;
    }

    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
    }

    public String getTax() {
        return this.tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public double getAmount() {
        return this.amount;
    }

    public void addAmount(double amount) {
        this.amount += amount;
    }
}
