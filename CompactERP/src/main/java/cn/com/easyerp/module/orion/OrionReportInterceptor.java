package cn.com.easyerp.module.orion;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import org.hibernate.type.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.zxing.EncodeHintType;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.chart.ReportModel;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.filter.FilterRequestModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.module.dao.OrionModuleDao;
import cn.com.easyerp.report.ReportFormRequestModel;
import cn.com.easyerp.report.ReportServiceDefault;
import net.glxn.qrgen.javase.QRCode;

@Service("orion-report")
public class OrionReportInterceptor extends ReportServiceDefault<OrionReportParam>
{
	private static final TypeReference<OrionReportParam> OrionReportModelRef = new TypeReference<OrionReportParam>() {
    };
    @Autowired
    private OrionModuleDao orionModuleDao;
    
    @Override
    public void init(final ReportModel<OrionReportParam> report) {
        super.init(report);
        report.setParam((OrionReportParam) Common.fromJson(report.getService_param(), (TypeReference)OrionReportInterceptor.OrionReportModelRef));
    }
    
    private PageFormat getMinimumMarginPageFormat(final PrinterJob printJob) {
        final PageFormat pf = printJob.defaultPage();
        final Paper paper = (Paper)pf.getPaper().clone();
        final double margin = 5.0;
        paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2.0, paper.getHeight() - margin * 2.0);
        pf.setPaper(paper);
        return pf;
    }
    
    @Override
    public ActionResult print(final ReportModel<OrionReportParam> report, final String where, final AuthDetails user, final ReportFormRequestModel request) throws IOException {
        ActionResult result;
        if (!((OrionReportParam)report.getParam()).isLabelOnly()) {
            result = super.print(report, where, user, request);
            if (!result.isSuccess()) {
                return result;
            }
        }
        else {
            result = Common.ActionOk;
        }
        this.printLabel(request, (OrionReportParam)report.getParam());
        return result;
    }
    
    private void printLabel(final ReportFormRequestModel request, final OrionReportParam param) {
        String where = null;
        final FilterRequestModel filter = request.getFilter();
        List<OrderDetailModel> list;
        if ("t_order".equals(filter.getTableName())) {
            where = " order_id in (" + Common.join((Collection)filter.getFilters().get("id").getValue()) + ")";
            list = (List<OrderDetailModel>)this.orionModuleDao.getOrderDetailList(where);
        }
        else {
            final List<String> filters = (List<String>)filter.getFilters().get("order_id,id").getValue();
            for (final String values : filters) {
                if (where != null) {
                    where += " or ";
                }
                else {
                    where = "(";
                }
                final String[] value = values.split(",");
                where = where + "(order_id = '" + value[0] + "' and id = '" + value[1] + "')";
            }
            where += ')';
            list = (List<OrderDetailModel>)this.orionModuleDao.getOrderDetailViewList(where);
        }
        if (list.size() == 0) {
            return;
        }
        final PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new LabelPrinter((List)list, param), this.getMinimumMarginPageFormat(printJob));
        try {
            printJob.print();
        }
        catch (PrinterException e) {
            throw new ApplicationException((Throwable)e);
        }
    }
    
    private static class LabelPrinter implements Printable
    {
        private List<OrderDetailModel> list;
        private OrionReportParam param;
        
        private LabelPrinter(final List<OrderDetailModel> list, final OrionReportParam param) {
            this.list = list;
            this.param = param;
        }
        
        private BufferedImage loadImage(final byte[] img) {
            try {
                return ImageIO.read(new ByteArrayInputStream(img));
            }
            catch (IOException e) {
                throw new ApplicationException((Throwable)e);
            }
        }
        
        @Override
        public int print(final Graphics g, final PageFormat pf, final int index) throws PrinterException {
            if (index >= this.list.size()) {
                return 1;
            }
            final Graphics2D g2 = (Graphics2D)g;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            final OrderDetailModel detail = this.list.get(index);
            final byte[] img = QRCode.from(detail.export(this.param)).withHint(EncodeHintType.MARGIN, (Object)0).withSize(this.param.getLabelSize(), this.param.getLabelSize()).to(ImageType.PNG).stream().toByteArray();
            final int left = 90;
            int h = 10;
            final int lh = 15;
            g.drawString("\u88fd\u756a " + detail.getManufacture_id(), left, h);
            g.drawString("\u88c5\u7f6e " + detail.getDevice_id(), left + 110, h);
            h += lh;
            g.drawString("\u56f3\u756a/\u578b\u756a", left, h);
            final String item_id = detail.getItem_id();
            g.drawString(item_id.substring(0, Math.min(15, item_id.length())), left + 60, h);
            h += lh;
            if (item_id.length() > 15) {
                g.drawString(item_id.substring(15), left + 60, h);
            }
            g.drawImage(this.loadImage(img), left + 120, h, null);
            h += lh;
            g.drawString("\u6b63", left, h);
            g.drawString(String.valueOf(detail.getPositive().intValue()), left + 20, h);
            g.drawString("\u6570\u91cf", left + 60, h);
            g.drawString(String.valueOf(detail.getNumber().intValue()), left + 90, h);
            h += lh;
            g.drawString("\u53cd", left, h);
            g.drawString(String.valueOf(detail.getNegative().intValue()), left + 20, h);
            h += lh;
            g.drawString("No." + detail.getIssue_no(), left, h);
            h += lh;
            g.drawString(String.format("%05d", index + 1), left, h);
            return 0;
        }
    }
}
