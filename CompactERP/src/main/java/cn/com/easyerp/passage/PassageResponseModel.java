package cn.com.easyerp.passage;

import java.util.Date;
import java.util.List;

public class PassageResponseModel {
    private List<Date> dates;
    private List<PassageCatalogModel> catalogs;

    public PassageResponseModel(List<Date> dates, List<PassageCatalogModel> catalogs) {
        this.dates = dates;
        this.catalogs = catalogs;
    }

    public List<Date> getDates() {
        return this.dates;
    }

    public List<PassageCatalogModel> getCatalogs() {
        return this.catalogs;
    }
}
