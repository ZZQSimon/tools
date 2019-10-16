package cn.com.easyerp.module.importer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.AutoKeyService;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.module.Module;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.module.dao.ImporterDao;
import cn.com.easyerp.storage.StorageService;

@Controller
@Module("importer")
@RequestMapping({ "/import" })
public class ImporterController extends FormViewControllerBase {
    public static final SimpleDateFormat dateOnlySDF;
    public static final SimpleDateFormat dateOnlySDF2;
    public static final SimpleDateFormat monthOnlySDF;
    public static final SimpleDateFormat timeOnlyFormat;
    public static final SimpleDateFormat timeOnlySDF1;
    public static final SimpleDateFormat timeOnlySDF2;
    public static final Set<String> skipHdrColNames;
    public static final Set<String> skipDtlColNames;
    private static final String tableNameHeader = "t_sale_order";
    private static final String keyColHeader = "id";
    private static final String tableNameDetail = "t_sale_order_detail";
    private static final String keyColDetail = "sale_order_detail_id";
    // private static final int dateOnly = 1;
    // private static final int timeOnly = 2;
    // private static final int monthOnly = 3;
    // private static final int HondaType1 = 1;
    // private static final int HondaType2 = 2;
    // private static final int nissanType = 3;
    // private static final int toyotaType = 4;
    private static final Map<Integer, String> SHEET_NAME_MAP;
    @Autowired
    private DataService dataService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ImporterDao importerDao;
    @Autowired
    private AutoKeyService autoKeyService;

    @RequestMapping({ "/import.view" })
    public ModelAndView view(@RequestBody final ImporterRequestModel<?> request) {
        final List<ColumnDescribe> columnList = new ArrayList<ColumnDescribe>();
        final Map<String, ColumnDescribe> keyColMap = new HashMap<String, ColumnDescribe>();
        TableDescribe table = this.dataService.getTableDesc(tableNameHeader);
        for (final ColumnDescribe column : table.getColumns()) {
            if (!ImporterController.skipHdrColNames.contains(column.getColumn_name())) {
                if (keyColHeader.equals(column.getColumn_name())) {
                    keyColMap.put(tableNameHeader, column);
                } else {
                    columnList.add(column);
                }
            }
        }
        table = this.dataService.getTableDesc(tableNameDetail);
        for (final ColumnDescribe column : table.getColumns()) {
            if (!ImporterController.skipDtlColNames.contains(column.getColumn_name())) {
                if (keyColDetail.equals(column.getColumn_name())) {
                    keyColMap.put(tableNameDetail, column);
                } else {
                    columnList.add(column);
                }
            }
        }
        final int typeId = request.getTypeId();
        final String customerId = this.importerDao.selectCustomerId(typeId);
        final ImporterFormModel form = new ImporterFormModel(columnList, keyColMap, customerId, typeId);
        return this.buildModelAndView((FormModelBase) form);
    }

    @SuppressWarnings("resource")
    @ResponseBody
    @RequestMapping({ "/import.do" })
    public ActionResult importExcel(@RequestBody final ImporterRequestModel<?> request) throws IOException {
        final String id = request.getId();
        final ImporterFormModel form = (ImporterFormModel) ViewService.fetchFormModel(id);
        final int typeId = form.getTypeId();
        try (final Workbook wb = (Workbook) new HSSFWorkbook(
                this.storageService.getUploadFile(request.getFileId()).getInputStream());) {
            final Sheet sheet = wb.getSheet((String) ImporterController.SHEET_NAME_MAP.get(typeId));
            if (sheet == null || sheet.getLastRowNum() < 1) {
                return new ActionResult(false,
                        (Object) this.dataService.getMessageText("data not exists", new Object[0]));
            }
            CustomImportDetail.reset();
            CustomImportResult result = null;
            switch (typeId) {
            case 1: {
                result = this.parseHonda1(form.getCustomerId(), sheet);
                break;
            }
            case 3: {
                result = this.getNissanDatas(form, sheet);
                break;
            }
            case 2: {
                result = this.getHonda2Datas(form, sheet);
                break;
            }
            case 4: {
                boolean errFlg = false;
                Row row = sheet.getRow(1);
                String objDate = this.getExcelCellValue(row.getCell(0));
                if (objDate == null || objDate.trim().length() != 6) {
                    errFlg = true;
                }
                if (!errFlg) {
                    try {
                        Integer.parseInt(objDate);
                    } catch (NumberFormatException e) {
                        errFlg = true;
                    }
                }
                if (!errFlg) {
                    final int month = Integer.parseInt(objDate.substring(4, 6));
                    if (month < 1 || month > 12) {
                        errFlg = true;
                    }
                }
                if (errFlg) {
                    return new ActionResult(true,
                            (Object) this.dataService.getMessageText("receive_indicate_date error", 1));
                }
                final String objDate_bf = objDate;
                for (int rowSize = sheet.getLastRowNum(), i = 2; i <= rowSize; ++i) {
                    row = sheet.getRow(i);
                    objDate = this.getExcelCellValue(row.getCell(0));
                    if (!objDate_bf.equals(objDate)) {
                        return new ActionResult(true, dataService.getMessageText("receive_indicate_date diff", i));
                    }
                }
                result = this.getToyotaTypeDatas(form, sheet);
                break;
            }
            }
            form.setResult(result);
            return new ActionResult(true, (Object) result);
        }
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/save.do" })
    public ActionResult saveExcel(@RequestBody final ImporterRequestModel<?> request) {
        final String id = request.getId();
        final ImporterFormModel form = (ImporterFormModel) ViewService.fetchFormModel(id);
        final CustomImportResult result = form.getResult();
        final int typeId = form.getTypeId();
        final List<ImportRowModel> dataList = result.getDataList();
        String hd_OrderId = null;
        ImportRowModel bf_model = new ImportRowModel();
        boolean isSame = true;
        if (typeId == 1) {
            return this.saveHonda1Data(form.getResult(), form.getCustomerId());
        }
        for (final ImportRowModel model : dataList) {
            switch (typeId) {
            case 2: {
                String existId;
                try {
                    existId = this.importerDao.voucherIdIsExist(model.getVoucher_id(), form.getCustomerId());
                } catch (Exception e) {
                    existId = null;
                }
                if (existId != null) {
                    this.importerDao.updateTSaleOrderDetail(model.getItem_id(), model.getCustomer_item_id(),
                            model.getTrade_type(), Double.parseDouble(model.getQuantity()),
                            Double.parseDouble(model.getUnit_price()), Double.parseDouble(model.getAmount_D()),
                            model.getCurrency_D(), Double.parseDouble(model.getTax()), model.getUnit(),
                            model.getTax_type(), model.getVoucher_id(), model.getCre_user(), existId);
                    final double sumAmount = this.importerDao.selectSumAmount(existId);
                    this.importerDao.updateHdrAmount(existId, sumAmount, form.getCustomerId());
                    continue;
                }
                if (typeId == 1) {
                    isSame = model.isSameOrderHonda1(bf_model.getDeliver_date(), bf_model.getReceive_time(),
                            bf_model.getReceiver_id());
                    break;
                }
                if (typeId == 2) {
                    isSame = model.isSameOrderHonda2(bf_model.getReceive_indicate_date(), bf_model.getReceive_time(),
                            bf_model.getReceive_warehouse());
                    break;
                }
                break;
            }
            case 3: {
                isSame = model.isSameOrderNissan(bf_model.getOrder_date(), bf_model.getReceive_indicate_date(),
                        bf_model.getReceive_time(), bf_model.getReceive_warehouse());
                break;
            }
            case 4: {
                isSame = model.isSameOrderToyota(bf_model.getReceive_indicate_date(), bf_model.getReceive_warehouse());
                break;
            }
            }
            if (!isSame && hd_OrderId != null) {
                final double sumAmount2 = this.importerDao.selectSumAmount(hd_OrderId);
                this.importerDao.updateHdrAmount(hd_OrderId, sumAmount2, form.getCustomerId());
            }
            if (!isSame) {
                hd_OrderId = this.autoKeyService.update(this.dataService.getTableDesc(tableNameHeader), null);
                this.importerDao.insertTSaleOrder(hd_OrderId, model.getImport_date(), model.getOrder_date(),
                        model.getCustomer_id(), model.getReceiver_id(), model.getRequest_id(),
                        model.getReceive_indicate_date(), model.getClasses(), model.getDeliver_date(),
                        model.getReceive_time().replace(":", ""), model.getDelivery_no(), model.getReceive_warehouse(),
                        model.getOut_warehouse(), model.getStatus(), model.getCurrency_H(),
                        Double.parseDouble(model.getExchange_rate()), model.getExchange_type(), 0.0, 0.0,
                        model.getCre_user());
            }
            final Map<String, Object> data = new HashMap<String, Object>();
            data.put(keyColHeader, hd_OrderId);
            model.setSale_order_detail_id(
                    this.autoKeyService.update(this.dataService.getTableDesc(tableNameDetail), data));
            this.importerDao.insertTSaleOrderDetail(hd_OrderId, model.getSale_order_detail_id(), model.getItem_id(),
                    model.getCustomer_item_id(), model.getTrade_type(), Double.parseDouble(model.getQuantity()),
                    Double.parseDouble(model.getUnit_price()), Double.parseDouble(model.getAmount_D()),
                    model.getCurrency_D(), Double.parseDouble(model.getTax()), model.getUnit(), model.getTax_type(),
                    model.getVoucher_id(), model.getCre_user());
            bf_model = model;
        }
        if (hd_OrderId != null) {
            final double sumAmount3 = this.importerDao.selectSumAmount(hd_OrderId);
            this.importerDao.updateHdrAmount(hd_OrderId, sumAmount3, form.getCustomerId());
        }
        return new ActionResult(true, (Object) this.dataService.getMessageText("import_success_msg", new Object[0]));
    }

    private ActionResult saveHonda1Data(final CustomImportResult data, final String customerId) {
        final Map<String, Object> param = new HashMap<String, Object>();
        final Set<String> voucherCache = new HashSet<String>();
        for (final CustomImportDetail detail : data.getDetails()) {
            final String key = Common.makeMapKey(detail.getVoucher_id(), customerId, new String[0]);
            if (!voucherCache.contains(key)) {
                if (this.importerDao.voucherIdIsExist(detail.getVoucher_id(), customerId) != null) {
                    throw new ApplicationException(
                            this.dataService.getMessageText("voucher exists", detail.getVoucher_id()));
                }
                voucherCache.add(key);
            }
            final CustomImportHeader h = data.getHeaders().get(detail.getVoucher_id());
            String order_id;
            if (h.getId() == null) {
                h.setId(order_id = this.autoKeyService.update(this.dataService.getTableDesc(tableNameHeader)));
            } else {
                order_id = h.getId();
            }
            param.put(keyColHeader, order_id);
            detail.setSale_order_detail_id(
                    this.autoKeyService.update(this.dataService.getTableDesc(tableNameDetail), param));
            final double amount = Double.parseDouble(detail.getAmount_D());
            this.importerDao.insertTSaleOrderDetail(order_id, detail.getSale_order_detail_id(), detail.getItem_id(),
                    detail.getCustomer_item_id(), detail.getTrade_type(), Double.parseDouble(detail.getQuantity()),
                    Double.parseDouble(detail.getUnit_price()), amount, detail.getCurrency_D(),
                    Double.parseDouble(h.getTax()), detail.getUnit(), h.getTax_type(), detail.getVoucher_id(),
                    h.getCre_user());
            h.addAmount(amount);
        }
        for (final CustomImportHeader header : data.getHeaders().values()) {
            this.importerDao.insertTSaleOrder(header.getId(), header.getImport_date(), header.getOrder_date(),
                    header.getCustomer_id(), header.getReceiver_id(), header.getRequest_id(),
                    header.getReceive_indicate_date(), header.getClasses(), header.getDeliver_date(),
                    header.getReceive_time().replace(":", ""), header.getDelivery_no(), header.getReceive_warehouse(),
                    header.getOut_warehouse(), header.getStatus(), header.getCurrency_H(),
                    Double.parseDouble(header.getExchange_rate()), header.getExchange_type(), 0.0, header.getAmount(),
                    header.getCre_user());
        }
        return Common.ActionOk;
    }

    private CustomImportResult parseHonda1(final String customerId, final Sheet sheet) throws IOException {
        final int rowSize = sheet.getLastRowNum();
        final CustomImportResult result = new CustomImportResult();
        final Map<String, CustomImportHeader> headers = new HashMap<String, CustomImportHeader>();
        final List<CustomImportDetail> details = new ArrayList<CustomImportDetail>();
        final Map<String, Boolean> receiverIdCache = new HashMap<String, Boolean>();
        final DataCache cache = new DataCache();
        for (int i = 1; i <= rowSize; ++i) {
            final CustomImportDetail detail = new Honda1ImportDetail();
            final Row row = sheet.getRow(i);
            final String voucher_id = row.getCell(11).getStringCellValue();
            detail.setVoucher_id(voucher_id);
            CustomImportHeader header = headers.get(voucher_id);
            if (header == null) {
                header = new CustomImportHeader();
                header.setDeliver_date(this.getStringDate(row.getCell(9), header, ImporterController.dateOnlySDF2, 8));
                String receive_time = this.getString(row.getCell(10), header, 9, 6);
                if (receive_time.length() == 5) {
                    receive_time = '0' + receive_time;
                }
                receive_time = receive_time.substring(0, 4);
                header.setReceive_time(receive_time);
                if (Common.notDate(receive_time, ImporterController.timeOnlyFormat)) {
                    header.addError(9);
                }
                final String receiver_id = this.getString(row.getCell(13), header, 4, 20);
                header.setReceiver_id(receiver_id);
                if (!receiverIdCache.containsKey(receiver_id)) {
                    receiverIdCache.put(receiver_id, this.importerDao.receiverIdIsExist(receiver_id) == 1);
                }
                if (!receiverIdCache.get(receiver_id)) {
                    header.addError(4);
                }
                header.setReceive_indicate_date(ImporterController.dateOnlySDF.format(new Date()));
                this.setHeaderData(header, customerId, cache);
                header.setOrder_date(header.getImport_date());
                headers.put(voucher_id, header);
            }
            this.setDetailData(header, detail, row, cache);
            details.add(detail);
        }
        result.setDetails(details);
        result.setHeaders(headers);
        return result;
    }

    private void setDetailData(final CustomImportHeader header, final CustomImportDetail detail, final Row row,
            final DataCache cache) {
        String customer_item_id = this.getExcelCellValue(row.getCell(detail.customerItemIdExcelIndex()));
        if (detail.customerItemColorExcelIndex() >= 0) {
            String customer_item_color = this.getExcelCellValue(row.getCell(detail.customerItemColorExcelIndex()));
            if (customer_item_id == null) {
                customer_item_id = "";
            }
            if (customer_item_color == null) {
                customer_item_color = "";
            }
            customer_item_id = customer_item_id + " " + customer_item_color;
        }
        detail.setCustomer_item_id(customer_item_id);
        if (Common.isBlank(customer_item_id) || customer_item_id.length() > 50) {
            detail.addError(17).addError(18);
        } else {
            final String key = Common.makeMapKey(detail.getCustomer_item_id(), header.getCustomer_id(), new String[0]);
            String item_id;
            if (!cache.itemIdCache.containsKey(key)) {
                item_id = this.importerDao.selectItemId(detail.getCustomer_item_id(), header.getCustomer_id());
                cache.itemIdCache.put(key, item_id);
            } else {
                item_id = cache.itemIdCache.get(key);
            }
            detail.setItem_id(item_id);
            if (item_id == null) {
                detail.addError(17);
            }
        }
        detail.setTrade_type("1");
        final String quantity = this.getExcelCellValue(row.getCell(detail.quantityExcelIndex()));
        detail.setQuantity(quantity);
        if (Common.notNumber(quantity)) {
            detail.addError(20);
        }
        try {
            final String key = Common.makeMapKey(header.getCustomer_id(), detail.getItem_id(),
                    new String[] { header.getImport_date() });
            double unit_price;
            if (!cache.unitPriceCache.containsKey(key)) {
                unit_price = this.importerDao.selectUnitPrice(header.getCustomer_id(), detail.getItem_id(),
                        header.getImport_date());
                cache.unitPriceCache.put(key, unit_price);
            } else {
                unit_price = cache.unitPriceCache.get(key);
            }
            detail.setUnit_price(String.valueOf(unit_price));
        } catch (Exception e) {
            detail.addError(21);
        }
        try {
            final double d_quantity = Double.parseDouble(detail.getQuantity());
            final double d_price = Double.parseDouble(detail.getUnit_price());
            final double d_amount = d_quantity * d_price;
            detail.setAmount_D(String.valueOf(d_amount));
        } catch (Exception e) {
            detail.addError(22);
        }
        detail.setCurrency_D(header.getCurrency_H());
        String unit;
        if (!cache.unitCache.containsKey(detail.getItem_id())) {
            unit = this.importerDao.selectBaseUnit(detail.getItem_id());
            cache.unitCache.put(detail.getItem_id(), unit);
        } else {
            unit = cache.unitCache.get(detail.getItem_id());
        }
        if (unit != null) {
            detail.setUnit(unit);
        } else {
            detail.addError(25);
        }
        if (detail.voucherIdExcelIndex() >= 0) {
            final String voucher_id = this.getExcelCellValue(row.getCell(detail.voucherIdExcelIndex()));
            detail.setVoucher_id(voucher_id);
            if (Common.isBlank(voucher_id) || voucher_id.length() > 20) {
                detail.addError(27);
            }
        }
    }

    private void setHeaderData(final CustomImportHeader header, final String customerId, final DataCache cache) {
        header.setImport_date(ImporterController.dateOnlySDF.format(new Date()));
        header.setCustomer_id(customerId);
        if (header.getReceiver_id() == null) {
            header.setReceiver_id(customerId);
        }
        header.setRequest_id(customerId);
        if (header.getDeliver_date() == null) {
            final String key = Common.makeMapKey(header.getReceive_indicate_date(), customerId, new String[0]);
            String deliver_date;
            if (!cache.deliverDateCache.containsKey(key)) {
                deliver_date = this.importerDao.selectDeliverDate(header.getReceive_indicate_date(), customerId);
                cache.deliverDateCache.put(key, deliver_date);
            } else {
                deliver_date = cache.deliverDateCache.get(key);
            }
            header.setDeliver_date(deliver_date);
            if (deliver_date == null) {
                header.addError(8);
            }
        }
        header.setStatus("0");
        ImporterCustDaoModel customer;
        if (!cache.customerCache.containsKey(customerId)) {
            customer = this.importerDao.selectCustomerDetail(customerId);
            cache.customerCache.put(customerId, customer);
        } else {
            customer = cache.customerCache.get(customerId);
        }
        if (customer != null) {
            header.setOut_warehouse(customer.getOut_warehouse());
            header.setCurrency_H(customer.getExchangecurrency());
            header.setTax_type(customer.getTax_type());
            double tax;
            if (!cache.taxCache.containsKey(customer.getTaxrate_id())) {
                tax = this.importerDao.selectTaxrate(customer.getTaxrate_id());
                cache.taxCache.put(customer.getTaxrate_id(), tax);
            } else {
                tax = cache.taxCache.get(customer.getTaxrate_id());
            }
            header.setTax(String.valueOf(tax));
        } else {
            header.addError(12).addError(14).addError(24).addError(26);
        }
        ImporterExcgDaoModel excgModel;
        if (!cache.excangeCache.containsKey(header.getCurrency_H())) {
            excgModel = this.importerDao.selectExchangeDetail(header.getCurrency_H());
            cache.excangeCache.put(header.getCurrency_H(), excgModel);
        } else {
            excgModel = cache.excangeCache.get(header.getCurrency_H());
        }
        if (excgModel != null) {
            header.setExchange_rate(String.valueOf(excgModel.getExchange_rate()));
            header.setExchange_type(excgModel.getExchange_type());
        } else {
            header.addError(15).addError(15);
        }
        header.setCre_user(customerId);
    }

    private String getStringDate(final Cell cell, final CustomImportData data, final SimpleDateFormat format,
            final int index) {
        final String date = cell.getStringCellValue();
        try {
            format.parse(date);
        } catch (Exception e) {
            data.addError(index);
        }
        return date;
    }

    private String getString(final Cell cell, final CustomImportData data, final int index, final int maxLength) {
        return this.getString(cell, data, index, maxLength, true);
    }

    private String getString(final Cell cell, final CustomImportData data, final int index, final int maxLength,
            final boolean noEmpty) {
        final String str = cell.getStringCellValue().trim();
        if (Common.isBlank(str)) {
            if (noEmpty) {
                data.addError(index);
            }
        } else if (str.length() > maxLength) {
            data.addError(index);
        }
        return str;
    }

    private CustomImportResult getHonda2Datas(final ImporterFormModel form, final Sheet sheet) throws IOException {
        final CustomImportResult result = new CustomImportResult();
        final int rowSize = sheet.getLastRowNum();
        final List<ImportRowModel> valueList = new ArrayList<ImportRowModel>();
        final List<String> statusList = new ArrayList<String>();
        ImportRowModel bfModel = null;
        StringBuilder sbHdrErrColInxs = null;
        final Set<String> hdrReqSet = new HashSet<String>();
        hdrReqSet.add("deliver_date");
        hdrReqSet.add("customer");
        hdrReqSet.add("exchange");
        hdrReqSet.add("receiver_id");
        for (int i = 1; i <= rowSize; ++i) {
            final ImportRowModel record = new ImportRowModel();
            final Row row = sheet.getRow(i);
            record.setRowid(String.valueOf(i));
            final StringBuilder sbExcelErrInx = new StringBuilder();
            this.getReceive_indicate_date_xls(row, record, sbExcelErrInx, 8);
            this.getReceive_time_xls(row, record, sbExcelErrInx, 9, 2);
            this.getReceive_warehouse_xls(row, record, sbExcelErrInx, 2);
            if (bfModel != null && record.isSameOrderHonda2(bfModel.getReceive_indicate_date(),
                    bfModel.getReceive_time(), bfModel.getReceive_warehouse())) {
                this.setSameData(record, bfModel);
            } else {
                sbHdrErrColInxs = this.selectHeaderData(record, hdrReqSet, sbExcelErrInx.toString(),
                        form.getCustomerId());
            }
            final StringBuilder sbDtlErrColInxs = new StringBuilder(sbHdrErrColInxs.toString());
            final int[] xlsDtlIdx = { 4, 0, 14, 15 };
            this.selectDetailData(record, sbDtlErrColInxs, row, xlsDtlIdx);
            bfModel = record;
            valueList.add(record);
            String sErrColInxs = null;
            final StringBuilder sb = new StringBuilder(sbHdrErrColInxs.toString()).append(sbDtlErrColInxs.toString());
            if (sb.length() > 0) {
                sErrColInxs = sb.substring(0, sb.length() - 1);
            }
            statusList.add(sErrColInxs);
        }
        result.setDataList(valueList);
        result.setStatus(statusList);
        form.setResult(result);
        return result;
    }

    private CustomImportResult getNissanDatas(final ImporterFormModel form, final Sheet sheet) throws IOException {
        final CustomImportResult result = new CustomImportResult();
        final int rowSize = sheet.getLastRowNum();
        final List<ImportRowModel> valueList = new ArrayList<ImportRowModel>();
        final List<String> statusList = new ArrayList<String>();
        ImportRowModel bfModel = null;
        StringBuilder sbHdrErrColInxs = null;
        final Set<String> hdrReqSet = new HashSet<String>();
        hdrReqSet.add("deliver_date");
        hdrReqSet.add("customer");
        hdrReqSet.add("exchange");
        hdrReqSet.add("receiver_id");
        for (int i = 1; i <= rowSize; ++i) {
            final ImportRowModel record = new ImportRowModel();
            final Row row = sheet.getRow(i);
            record.setRowid(String.valueOf(i));
            final StringBuilder sbExcelErrInx = new StringBuilder();
            this.getOrder_date_xls(row, record, sbExcelErrInx, 27);
            this.getReceive_indicate_date_xls(row, record, sbExcelErrInx, 7);
            this.getReceive_time_xls(row, record, sbExcelErrInx, 7, 1);
            this.getReceive_warehouse_xls(row, record, sbExcelErrInx, 8);
            if (bfModel != null && record.isSameOrderNissan(bfModel.getOrder_date(), bfModel.getReceive_indicate_date(),
                    bfModel.getReceive_time(), bfModel.getReceive_warehouse())) {
                this.setSameData(record, bfModel);
            } else {
                sbHdrErrColInxs = this.selectHeaderData(record, hdrReqSet, sbExcelErrInx.toString(),
                        form.getCustomerId());
            }
            final StringBuilder sbDtlErrColInxs = new StringBuilder(sbHdrErrColInxs.toString());
            final int[] xlsDtlIdx = { 1, 0, 10, 0 };
            this.selectDetailData(record, sbDtlErrColInxs, row, xlsDtlIdx);
            bfModel = record;
            valueList.add(record);
            String sErrColInxs = null;
            if (sbDtlErrColInxs.length() > 0) {
                sErrColInxs = sbDtlErrColInxs.substring(0, sbDtlErrColInxs.length() - 1);
            }
            statusList.add(sErrColInxs);
        }
        result.setDataList(valueList);
        result.setStatus(statusList);
        form.setResult(result);
        return result;
    }

    private CustomImportResult getToyotaTypeDatas(final ImporterFormModel form, final Sheet sheet) throws IOException {
        final CustomImportResult result = new CustomImportResult();
        final int rowSize = sheet.getLastRowNum();
        final List<ImportRowModel> valueList = new ArrayList<ImportRowModel>();
        final List<String> statusList = new ArrayList<String>();
        ImportRowModel bfModel = null;
        StringBuilder sbHdrErrColInxs = null;
        final Set<String> hdrReqSet = new HashSet<String>();
        hdrReqSet.add("deliver_date");
        hdrReqSet.add("customer");
        hdrReqSet.add("exchange");
        hdrReqSet.add("receiver_id");
        Row row = sheet.getRow(1);
        String receive_indicate_month = this.getExcelCellValue(row.getCell(0));
        int year = Integer.parseInt(receive_indicate_month.substring(0, 4));
        int mon = Integer.parseInt(receive_indicate_month.substring(4, 6));
        final int stColNum = 18;
        final int lastDay = this.getLastDay(year, mon);
        int rownum = 1;
        for (int col = 1; col <= lastDay; ++col) {
            for (int i = 1; i <= rowSize; ++i) {
                row = sheet.getRow(i);
                final String quantity = this.getExcelCellValue(row.getCell(stColNum + col));
                if (!"".equals(quantity.replace("0", ""))) {
                    final ImportRowModel record = new ImportRowModel();
                    record.setRowid(String.valueOf(rownum++));
                    final StringBuilder sbExcelErrInx = new StringBuilder();
                    String receive_indicate_day = String.valueOf(col);
                    if (receive_indicate_day.length() == 1) {
                        receive_indicate_day = "0" + receive_indicate_day;
                    }
                    record.setReceive_indicate_date(receive_indicate_month + receive_indicate_day);
                    this.getReceive_warehouse2_xls(row, record, sbExcelErrInx, 145, 146);
                    if (bfModel != null && record.isSameOrderToyota(bfModel.getReceive_indicate_date(),
                            bfModel.getReceive_warehouse())) {
                        this.setSameData(record, bfModel);
                    } else {
                        sbHdrErrColInxs = this.selectHeaderData(record, hdrReqSet, sbExcelErrInx.toString(),
                                form.getCustomerId());
                    }
                    final StringBuilder sbDtlErrColInxs = new StringBuilder(sbHdrErrColInxs.toString());
                    final int[] xlsDtlIdx = { 7, 0, stColNum + col, 0 };
                    this.selectDetailData(record, sbDtlErrColInxs, row, xlsDtlIdx);
                    bfModel = record;
                    valueList.add(record);
                    String sErrColInxs = null;
                    if (sbDtlErrColInxs.length() > 0) {
                        sErrColInxs = sbDtlErrColInxs.substring(0, sbDtlErrColInxs.length() - 1);
                    }
                    statusList.add(sErrColInxs);
                }
            }
        }
        if (mon == 12) {
            ++year;
            mon = 1;
        } else {
            ++mon;
        }
        final int nextStColNum = 49;
        final int nextLastDay = this.getLastDay(year, mon);
        if (mon < 10) {
            receive_indicate_month = String.valueOf(year) + "0" + String.valueOf(mon);
        } else {
            receive_indicate_month = String.valueOf(year) + String.valueOf(mon);
        }
        for (int col2 = 1; col2 <= nextLastDay; ++col2) {
            for (int j = 1; j <= rowSize; ++j) {
                row = sheet.getRow(j);
                final String quantity2 = this.getExcelCellValue(row.getCell(nextStColNum + col2));
                if (!"".equals(quantity2.replace("0", ""))) {
                    final ImportRowModel record2 = new ImportRowModel();
                    record2.setRowid(String.valueOf(rownum++));
                    final StringBuilder sbExcelErrInx = new StringBuilder();
                    String receive_indicate_day2 = String.valueOf(col2);
                    if (receive_indicate_day2.length() == 1) {
                        receive_indicate_day2 = "0" + receive_indicate_day2;
                    }
                    record2.setReceive_indicate_date(receive_indicate_month + receive_indicate_day2);
                    this.getReceive_warehouse2_xls(row, record2, sbExcelErrInx, 145, 146);
                    if (bfModel != null && record2.isSameOrderToyota(bfModel.getReceive_indicate_date(),
                            bfModel.getReceive_warehouse())) {
                        this.setSameData(record2, bfModel);
                    } else {
                        sbHdrErrColInxs = this.selectHeaderData(record2, hdrReqSet, sbExcelErrInx.toString(),
                                form.getCustomerId());
                    }
                    final StringBuilder sbDtlErrColInxs = new StringBuilder(sbHdrErrColInxs.toString());
                    final int[] xlsDtlIdx2 = { 7, nextStColNum + col2, 0 };
                    this.selectDetailData(record2, sbDtlErrColInxs, row, xlsDtlIdx2);
                    bfModel = record2;
                    valueList.add(record2);
                    String sErrColInxs2 = null;
                    if (sbDtlErrColInxs.length() > 0) {
                        sErrColInxs2 = sbDtlErrColInxs.substring(0, sbDtlErrColInxs.length() - 1);
                    }
                    statusList.add(sErrColInxs2);
                }
            }
        }
        result.setDataList(valueList);
        result.setStatus(statusList);
        form.setResult(result);
        return result;
    }

    private void setSameData(final ImportRowModel record, final ImportRowModel bfModel) {
        record.setImport_date(bfModel.getImport_date());
        record.setOrder_date(bfModel.getOrder_date());
        record.setCustomer_id(bfModel.getCustomer_id());
        record.setReceiver_id(bfModel.getReceiver_id());
        record.setRequest_id(bfModel.getRequest_id());
        record.setReceive_indicate_date(bfModel.getReceive_indicate_date());
        record.setClasses(bfModel.getClasses());
        record.setDeliver_date(bfModel.getDeliver_date());
        record.setReceive_time(bfModel.getReceive_time());
        record.setDelivery_no(bfModel.getDelivery_no());
        record.setReceive_warehouse(bfModel.getReceive_warehouse());
        record.setOut_warehouse(bfModel.getOut_warehouse());
        record.setStatus(bfModel.getStatus());
        record.setCurrency_H(bfModel.getCurrency_H());
        record.setExchange_rate(bfModel.getExchange_rate());
        record.setExchange_type(bfModel.getExchange_type());
        record.setCre_user(bfModel.getCre_user());
        record.setTax_type(bfModel.getTax_type());
        record.setTax(bfModel.getTax());
    }

    private void selectDetailData(final ImportRowModel record, final StringBuilder sbDtlErrColInxs, final Row row,
            final int[] xlsDtlidx) {
        String customer_item_id = this.getExcelCellValue(row.getCell(xlsDtlidx[0]));
        if (xlsDtlidx[1] != 0) {
            String customer_item_id_color = this.getExcelCellValue(row.getCell(xlsDtlidx[1]));
            if (customer_item_id == null) {
                customer_item_id = "";
            }
            if (customer_item_id_color == null) {
                customer_item_id_color = "";
            }
            customer_item_id = customer_item_id + " " + customer_item_id_color;
        }
        record.setCustomer_item_id(customer_item_id);
        if (customer_item_id == null || "".equals(customer_item_id.trim()) || customer_item_id.length() > 50) {
            sbDtlErrColInxs.append("17,18,");
        } else {
            final String item_id = this.importerDao.selectItemId(record.getCustomer_item_id(), record.getCustomer_id());
            record.setItem_id(item_id);
            if (item_id == null) {
                sbDtlErrColInxs.append("17,");
            }
        }
        record.setTrade_type("1");
        final String quantity = this.getExcelCellValue(row.getCell(xlsDtlidx[2]));
        record.setQuantity(quantity);
        try {
            Double.parseDouble(quantity);
        } catch (Exception e) {
            sbDtlErrColInxs.append("20,");
        }
        try {
            final double unit_price = this.importerDao.selectUnitPrice(record.getCustomer_id(), record.getItem_id(),
                    record.getImport_date());
            record.setUnit_price(String.valueOf(unit_price));
        } catch (Exception e) {
            sbDtlErrColInxs.append("21,");
        }
        try {
            final double d_quantity = Double.parseDouble(record.getQuantity());
            final double d_price = Double.parseDouble(record.getUnit_price());
            final double d_amount = d_quantity * d_price;
            record.setAmount_D(String.valueOf(d_amount));
        } catch (Exception e) {
            sbDtlErrColInxs.append("22,");
        }
        record.setCurrency_D(record.getCurrency_H());
        final String unit = this.importerDao.selectBaseUnit(record.getItem_id());
        if (unit != null) {
            record.setUnit(unit);
        } else {
            sbDtlErrColInxs.append("25,");
        }
        if (xlsDtlidx[3] != 0) {
            final String voucher_id = this.getExcelCellValue(row.getCell(xlsDtlidx[3]));
            record.setVoucher_id(voucher_id);
            if (voucher_id == null || "".equals(voucher_id.trim()) || voucher_id.length() > 20) {
                sbDtlErrColInxs.append("27,");
            }
        }
    }

    private StringBuilder selectHeaderData(final ImportRowModel record, final Set<String> hdrReqSet,
            final String excelErrInxs, final String customerId) {
        final StringBuilder sbHdrErrColInxs = new StringBuilder(excelErrInxs);
        record.setImport_date(ImporterController.dateOnlySDF.format(new Date()));
        record.setCustomer_id(customerId);
        if (hdrReqSet.contains("receiver_id")) {
            record.setReceiver_id(customerId);
        }
        record.setRequest_id(customerId);
        if (hdrReqSet.contains("deliver_date")) {
            String deliver_date;
            try {
                deliver_date = this.importerDao.selectDeliverDate(record.getReceive_indicate_date(), customerId);
                record.setDeliver_date(deliver_date);
            } catch (Exception e) {
                deliver_date = null;
            }
            if (deliver_date == null) {
                sbHdrErrColInxs.append("8,");
            }
        }
        record.setStatus("0");
        if (hdrReqSet.contains("customer")) {
            final ImporterCustDaoModel custModel = this.importerDao.selectCustomerDetail(customerId);
            if (custModel != null) {
                record.setOut_warehouse(custModel.getOut_warehouse());
                record.setCurrency_H(custModel.getExchangecurrency());
                record.setTax_type(custModel.getTax_type());
            } else {
                sbHdrErrColInxs.append("12,14,26,");
            }
            try {
                final double tax = this.importerDao.selectTaxrate(custModel.getTaxrate_id());
                record.setTax(String.valueOf(tax));
            } catch (Exception e2) {
                sbHdrErrColInxs.append("24,");
            }
        }
        if (hdrReqSet.contains("exchange")) {
            final ImporterExcgDaoModel excgModel = this.importerDao.selectExchangeDetail(record.getCurrency_H());
            if (excgModel != null) {
                record.setExchange_rate(String.valueOf(excgModel.getExchange_rate()));
                record.setExchange_type(excgModel.getExchange_type());
            } else {
                sbHdrErrColInxs.append("15,16,");
            }
        }
        record.setCre_user(customerId);
        return sbHdrErrColInxs;
    }

    private String getExcelCellDate(final Cell cell, final int dateType) {
        String strValue = null;
        try {
            switch (dateType) {
            case 1: {
                strValue = ImporterController.dateOnlySDF.format(cell.getDateCellValue());
                break;
            }
            case 2: {
                strValue = ImporterController.timeOnlyFormat.format(cell.getDateCellValue());
                break;
            }
            case 3: {
                strValue = ImporterController.monthOnlySDF.format(cell.getDateCellValue());
                break;
            }
            }
        } catch (Exception e) {
            strValue = this.getExcelCellValue(cell);
        }
        return strValue;
    }

    private String getExcelCellValue(final Cell cell) {
        String strValue = null;
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
        case STRING: {
            strValue = cell.getStringCellValue().trim();
            break;
        }
        case NUMERIC: {
            strValue = "" + cell.getNumericCellValue();
            break;
        }
        case FORMULA: {
            strValue = cell.getCellFormula();
            break;
        }
        case ERROR: {
            strValue = "" + cell.getErrorCellValue();
            break;
        }
        case BOOLEAN: {
            strValue = "" + cell.getBooleanCellValue();
            break;
        }
        default: {
            strValue = "";
            break;
        }
        }
        return strValue;
    }

    @SuppressWarnings("unused")
    private void getDeliver_date_xls(final Row row, final ImportRowModel record, final StringBuilder sbExcelErrInx,
            final int indx) {
        final String deliver_date = row.getCell(indx).getStringCellValue();
        record.setDeliver_date(deliver_date);
        try {
            ImporterController.dateOnlySDF2.parse(deliver_date);
        } catch (Exception e) {
            sbExcelErrInx.append("8,");
        }
    }

    private void getReceive_time_xls(final Row row, final ImportRowModel record, final StringBuilder sbExcelErrInx,
            final int index, final int flg) {
        String receive_time = null;
        switch (flg) {
        case 1: {
            receive_time = this.getExcelCellDate(row.getCell(index), 2);
            break;
        }
        case 2: {
            receive_time = this.getExcelCellValue(row.getCell(index)).replace(":", "");
            break;
        }
        case 3: {
            receive_time = this.getExcelCellValue(row.getCell(index));
            record.setReceive_time(receive_time);
            try {
                if (flg == 2) {
                    ImporterController.timeOnlySDF2.parse(receive_time);
                } else {
                    ImporterController.timeOnlyFormat.parse(receive_time.substring(0, 4));
                }
            } catch (Exception e) {
                sbExcelErrInx.append("9,");
            }
            break;
        }
        }
        try {
            ImporterController.timeOnlyFormat.parse(receive_time);
        } catch (Exception e) {
            sbExcelErrInx.append("9,");
        }
        record.setReceive_time(receive_time);
    }

    private void getReceive_warehouse_xls(final Row row, final ImportRowModel record, final StringBuilder sbExcelErrInx,
            final int indx) {
        final String receive_warehouse = this.getExcelCellValue(row.getCell(indx));
        if (receive_warehouse == null || "".equals(receive_warehouse.trim()) || receive_warehouse.length() > 20) {
            sbExcelErrInx.append("11,");
        } else {
            record.setReceive_warehouse(receive_warehouse);
        }
    }

    @SuppressWarnings("unused")
    private void getReceiver_id_xls(final Row row, final ImportRowModel record, final StringBuilder sbExcelErrInx,
            final int indx) {
        final String receiver_id = this.getExcelCellValue(row.getCell(indx));
        if (receiver_id == null || "".equals(receiver_id.trim()) || receiver_id.length() > 20) {
            sbExcelErrInx.append("4,");
        } else {
            final int num = this.importerDao.receiverIdIsExist(receiver_id);
            if (num != 1) {
                sbExcelErrInx.append("4,");
            }
        }
        record.setReceiver_id(receiver_id);
    }

    private void getReceive_warehouse2_xls(final Row row, final ImportRowModel record,
            final StringBuilder sbExcelErrInx, final int indx1, final int indx2) {
        final String receive_warehouse_bef = this.getExcelCellValue(row.getCell(indx1));
        final String receive_warehouse_aft = this.getExcelCellValue(row.getCell(indx2));
        if (receive_warehouse_bef == null || "".equals(receive_warehouse_bef.trim()) || receive_warehouse_aft == null
                || "".equals(receive_warehouse_aft.trim())
                || receive_warehouse_bef.length() + receive_warehouse_aft.length() > 19) {
            sbExcelErrInx.append("11,");
        } else {
            record.setReceive_warehouse(receive_warehouse_bef + "/" + receive_warehouse_aft);
        }
    }

    private void getReceive_indicate_date_xls(final Row row, final ImportRowModel record,
            final StringBuilder sbExcelErrInx, final int indx) {
        final String receive_indicate_date = this.getExcelCellDate(row.getCell(indx), 1);
        record.setReceive_indicate_date(receive_indicate_date);
        try {
            ImporterController.dateOnlySDF.parse(receive_indicate_date);
        } catch (Exception e) {
            sbExcelErrInx.append("6,");
        }
    }

    private void getOrder_date_xls(final Row row, final ImportRowModel record, final StringBuilder sbExcelErrInx,
            final int indx) {
        final String order_date = this.getExcelCellDate(row.getCell(indx), 1);
        record.setOrder_date(order_date);
        try {
            ImporterController.dateOnlySDF.parse(order_date);
        } catch (Exception e) {
            sbExcelErrInx.append("2,");
        }
    }

    private int getLastDay(final int year, final int month) {
        final int day = 1;
        final Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return cal.getActualMaximum(5);
    }

    static {
        dateOnlySDF = new SimpleDateFormat("yyyy-MM-dd");
        dateOnlySDF2 = new SimpleDateFormat("yyyyMMdd");
        monthOnlySDF = new SimpleDateFormat("yyyy-MM");
        timeOnlyFormat = new SimpleDateFormat("HHmm");
        timeOnlySDF1 = new SimpleDateFormat("HH:mm:ss");
        timeOnlySDF2 = new SimpleDateFormat("HH:mm");
        skipHdrColNames = new HashSet<String>();
        skipDtlColNames = new HashSet<String>();
        ImporterController.skipHdrColNames.add("amount");
        ImporterController.skipHdrColNames.add("home_amount");
        ImporterController.skipHdrColNames.add("cre_user");
        ImporterController.skipHdrColNames.add("cre_date");
        ImporterController.skipHdrColNames.add("upd_user");
        ImporterController.skipHdrColNames.add("upd_date");
        ImporterController.skipDtlColNames.add("id");
        ImporterController.skipDtlColNames.add("cre_user");
        ImporterController.skipDtlColNames.add("cre_date");
        ImporterController.skipDtlColNames.add("upd_user");
        ImporterController.skipDtlColNames.add("upd_date");
        ImporterController.dateOnlySDF.setLenient(false);
        ImporterController.dateOnlySDF2.setLenient(false);
        ImporterController.monthOnlySDF.setLenient(false);
        ImporterController.timeOnlySDF1.setLenient(false);
        ImporterController.timeOnlySDF2.setLenient(false);
        ImporterController.timeOnlyFormat.setLenient(false);
        (SHEET_NAME_MAP = new HashMap<Integer, String>()).put(1, "\u53d6\u8d27\u7269\u6d41\u8fd0\u8f93\u6e05\u5355");
        ImporterController.SHEET_NAME_MAP.put(2, "\u65e5\u660e\u7ec6");
        ImporterController.SHEET_NAME_MAP.put(3, "\u5230\u8d27\u6307\u793a(\u65e5\u4ea7\uff09");
        ImporterController.SHEET_NAME_MAP.put(4, "sheet1");
    }

    private static class DataCache {
        private Map<String, String> deliverDateCache;
        private Map<String, ImporterCustDaoModel> customerCache;
        private Map<String, Double> taxCache;
        private Map<String, ImporterExcgDaoModel> excangeCache;
        private Map<String, String> itemIdCache;
        private Map<String, Double> unitPriceCache;
        private Map<String, String> unitCache;

        private DataCache() {
            this.deliverDateCache = new HashMap<String, String>();
            this.customerCache = new HashMap<String, ImporterCustDaoModel>();
            this.taxCache = new HashMap<String, Double>();
            this.excangeCache = new HashMap<String, ImporterExcgDaoModel>();
            this.itemIdCache = new HashMap<String, String>();
            this.unitPriceCache = new HashMap<String, Double>();
            this.unitCache = new HashMap<String, String>();
        }
    }
}
