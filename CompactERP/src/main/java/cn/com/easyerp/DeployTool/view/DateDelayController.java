package cn.com.easyerp.DeployTool.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.DateDelayDao;
import cn.com.easyerp.DeployTool.service.DateDelayMap;
import cn.com.easyerp.DeployTool.service.Passage;
import cn.com.easyerp.DeployTool.service.PassageRow;
import cn.com.easyerp.DeployTool.service.PassageRowFormula;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/datedelay" })
public class DateDelayController extends FormViewControllerBase {
    @Autowired
    private DateDelayDao dateDelayDao;

    @RequestMapping({ "/datedelay.view" })
    public ModelAndView datedelay(@RequestBody DateDelayRequestModel request) {
        DateDelayModel model = new DateDelayModel(request.getParent());
        return buildModelAndView(model);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping({ "/selectDateDelay.do" })
    @ResponseBody
    public ActionResult selectDateDelay() {
        DateDelayMap dateDelayMap = new DateDelayMap();
        try {
            Map<String, Passage> passageMap = new HashMap<String, Passage>();
            for (Passage passage : this.dateDelayDao.selectPassage()) {
                passageMap.put(passage.getId(), passage);
            }
            List<PassageRow> passageRowList = this.dateDelayDao.selectPassageRow();
            Map<String, List<PassageRow>> passageRowMap = new HashMap<String, List<PassageRow>>();
            if (passageRowList.size() > 0) {
                for (PassageRow passageRow : passageRowList) {
                    if (passageRowMap.get(passageRow.getPassage_id()) == null
                            || ((List) passageRowMap.get(passageRow.getPassage_id())).size() == 0) {
                        passageRowMap.put(passageRow.getPassage_id(), new ArrayList<>());
                    }
                    ((List) passageRowMap.get(passageRow.getPassage_id())).add(passageRow);
                }
            }
            List<PassageRowFormula> passageRowFormulaList = this.dateDelayDao.selectPassageRowFormula();
            Map<String, List<PassageRowFormula>> passageRowFormulaMap = new HashMap<String, List<PassageRowFormula>>();
            if (passageRowFormulaList.size() > 0) {
                for (PassageRowFormula passageRowFormula : passageRowFormulaList) {
                    if (passageRowFormulaMap.get(passageRowFormula.getPassage_row_id()) == null
                            || ((List) passageRowFormulaMap.get(passageRowFormula.getPassage_row_id())).size() == 0) {
                        passageRowFormulaMap.put(passageRowFormula.getPassage_row_id(), new ArrayList<>());
                    }
                    ((List) passageRowFormulaMap.get(passageRowFormula.getPassage_row_id())).add(passageRowFormula);
                }
            }
            dateDelayMap.setPassageMap(passageMap);
            dateDelayMap.setPassageRowMap(passageRowMap);
            dateDelayMap.setPassageRowFormulaMap(passageRowFormulaMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ActionResult(true, dateDelayMap);
    }

    @RequestMapping({ "/saveDelay.do" })
    @ResponseBody
    @Transactional
    public ActionResult saveDelay(@RequestBody DateDelayRequestModel frontdata) throws Exception {
        Boolean flag = Boolean.valueOf(true);

        List<Passage> passageList = new ArrayList<Passage>();
        if (frontdata.getFrontdata().getPassageMap().getInserted().size() > 0) {
            for (Passage passage : frontdata.getFrontdata().getPassageMap().getInserted()) {
                Passage passage2 = new Passage();
                passage2.setId(passage.getId());
                passage2.setDisp_cols(passage.getDisp_cols());
                passage2.setFilter_sql(passage.getFilter_sql());
                passage2.setFilter_tables(passage.getFilter_tables());
                passage2.setGroup_cols(passage.getGroup_cols());
                passage2.setHas_append(passage.getHas_append());
                passage2.setMain_table(passage.getMain_table());
                passage2.setName(passage.getName());
                passage2.setType(passage.getType());
                passageList.add(passage2);
            }
            this.dateDelayDao.addDateDelay(passageList);
        } else if (frontdata.getFrontdata().getPassageMap().getUpdated().size() > 0) {
            for (Passage passage : frontdata.getFrontdata().getPassageMap().getUpdated()) {
                Passage passage2 = new Passage();
                passage2.setId(passage.getId());
                passage2.setDisp_cols(passage.getDisp_cols());
                passage2.setFilter_sql(passage.getFilter_sql());
                passage2.setFilter_tables(passage.getFilter_tables());
                passage2.setGroup_cols(passage.getGroup_cols());
                passage2.setHas_append(passage.getHas_append());
                passage2.setMain_table(passage.getMain_table());
                passage2.setName(passage.getName());
                passage2.setType(passage.getType());
                passageList.add(passage2);
            }
            this.dateDelayDao.updDateDelay(passageList);
        } else if (frontdata.getFrontdata().getPassageMap().getDeleted().size() > 0) {
            for (Passage passage : frontdata.getFrontdata().getPassageMap().getDeleted()) {
                Passage passage2 = new Passage();
                passage2.setId(passage.getId());
                passageList.add(passage2);
            }
            this.dateDelayDao.delDateDelay(passageList);
        }

        List<PassageRow> passageRowList = new ArrayList<PassageRow>();
        if (frontdata.getFrontdata().getPassageRowMap().getInserted().size() > 0) {
            for (PassageRow passageRow : frontdata.getFrontdata().getPassageRowMap().getInserted()) {
                PassageRow row = new PassageRow();
                row.setDecimal_digit(passageRow.getDecimal_digit());
                row.setDetail_sql(passageRow.getDetail_sql());
                row.setDisp_name_key(passageRow.getDisp_name_key());
                row.setEdit_cond(passageRow.getEdit_cond());
                row.setEdit_part_bg_color(passageRow.getEdit_part_bg_color());
                row.setEdit_part_fg_color(passageRow.getEdit_part_fg_color());
                row.setId(passageRow.getId());
                row.setModule(passageRow.getModule());
                row.setPassage_id(passageRow.getPassage_id());
                row.setTotal_col(passageRow.getTotal_col());
                row.setTotal_row(passageRow.getTotal_row());
                row.setUnedit_part_bg_color(passageRow.getUnedit_part_bg_color());
                row.setUnedit_part_fg_color(passageRow.getUnedit_part_fg_color());
                row.setUpd_statement(passageRow.getUpd_statement());
                passageRowList.add(row);
            }
            this.dateDelayDao.addPassageRow(passageRowList);
        } else if (frontdata.getFrontdata().getPassageRowMap().getUpdated().size() > 0) {
            for (PassageRow passageRow : frontdata.getFrontdata().getPassageRowMap().getUpdated()) {
                PassageRow row = new PassageRow();
                row.setDecimal_digit(passageRow.getDecimal_digit());
                row.setDetail_sql(passageRow.getDetail_sql());
                row.setDisp_name_key(passageRow.getDisp_name_key());
                row.setEdit_cond(passageRow.getEdit_cond());
                row.setEdit_part_bg_color(passageRow.getEdit_part_bg_color());
                row.setEdit_part_fg_color(passageRow.getEdit_part_fg_color());
                row.setId(passageRow.getId());
                row.setModule(passageRow.getModule());
                row.setPassage_id(passageRow.getPassage_id());
                row.setTotal_col(passageRow.getTotal_col());
                row.setTotal_row(passageRow.getTotal_row());
                row.setUnedit_part_bg_color(passageRow.getUnedit_part_bg_color());
                row.setUnedit_part_fg_color(passageRow.getUnedit_part_fg_color());
                row.setUpd_statement(passageRow.getUpd_statement());
                passageRowList.add(row);
            }
            this.dateDelayDao.updPassageRow(passageRowList);
        } else if (frontdata.getFrontdata().getPassageRowMap().getDeleted().size() > 0) {
            for (PassageRow passageRow : frontdata.getFrontdata().getPassageRowMap().getDeleted()) {
                PassageRow row = new PassageRow();
                row.setPassage_id(passageRow.getPassage_id());
                passageRowList.add(row);
            }
            this.dateDelayDao.delPassageRow(passageRowList);
        }

        List<PassageRowFormula> passageRowFormulaList = new ArrayList<PassageRowFormula>();
        if (frontdata.getFrontdata().getRowFormula().getInserted().size() > 0) {
            for (PassageRowFormula passageRowFormula : frontdata.getFrontdata().getRowFormula().getInserted()) {
                PassageRowFormula rowFormula = new PassageRowFormula();
                rowFormula.setId(passageRowFormula.getId());
                rowFormula.setPassage_row_id(passageRowFormula.getPassage_row_id());
                rowFormula.setLevel(passageRowFormula.getLevel());
                rowFormula.setCond(passageRowFormula.getCond());
                rowFormula.setFormula(passageRowFormula.getFormula());
                rowFormula.setSql(passageRowFormula.getSql());
                rowFormula.setType(passageRowFormula.getType());
                rowFormula.setModule(passageRowFormula.getModule());
                passageRowFormulaList.add(rowFormula);
            }
            this.dateDelayDao.addPassageRowFormula(passageRowFormulaList);
        } else if (frontdata.getFrontdata().getRowFormula().getUpdated().size() > 0) {
            for (PassageRowFormula passageRowFormula : frontdata.getFrontdata().getRowFormula().getUpdated()) {
                PassageRowFormula rowFormula = new PassageRowFormula();
                rowFormula.setId(passageRowFormula.getId());
                rowFormula.setPassage_row_id(passageRowFormula.getPassage_row_id());
                rowFormula.setLevel(passageRowFormula.getLevel());
                rowFormula.setCond(passageRowFormula.getCond());
                rowFormula.setFormula(passageRowFormula.getFormula());
                rowFormula.setSql(passageRowFormula.getSql());
                rowFormula.setType(passageRowFormula.getType());
                rowFormula.setModule(passageRowFormula.getModule());
                passageRowFormulaList.add(rowFormula);
            }
            this.dateDelayDao.updPassageRowFormula(passageRowFormulaList);
        } else if (frontdata.getFrontdata().getRowFormula().getDeleted().size() > 0) {
            for (PassageRowFormula passageRowFormula : frontdata.getFrontdata().getRowFormula().getDeleted()) {
                PassageRowFormula rowFormula = new PassageRowFormula();
                rowFormula.setPassage_row_id(passageRowFormula.getPassage_row_id());
                passageRowFormulaList.add(rowFormula);
            }
            this.dateDelayDao.delPassageRowFormula(passageRowFormulaList);
        }
        return new ActionResult(true, flag);
    }
}
