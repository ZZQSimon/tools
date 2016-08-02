package report;

import java.util.ArrayList;
import java.util.List;

/**
 * @createDate 2011-8-22
 * @author Simon Zhang
 * @version 1.0.0
 * @modifyAuthor 
 * @modifyDate
 */
public final class TitleObject {
	private String titleName;
	private List<String> titleChild=new ArrayList<String>();
	public String getTitleName() {
		return titleName;
	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	public List<String> getTitleChild() {
		return titleChild;
	}
	public void setTitleChild(List<String> titleChild) {
		this.titleChild = titleChild;
	}
	
}
