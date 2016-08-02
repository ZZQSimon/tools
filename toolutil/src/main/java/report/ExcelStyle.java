package report;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.BoldStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;
import jxl.write.WritableFont.FontName;

/**
 * @createDate 2011-8-22
 * @author Simon Zhang
 * @version 1.0.0
 * @modifyAuthor 
 * @modifyDate
 */
public final class ExcelStyle {

	/**
	 * 标题高度
	 * 
	 */
	public final static int HEAD_HEIGHT = 1000;

	/**
	 * 第二行日期高度
	 * 
	 */
	public final static int DATE_HEIGHT = 500;
	/**
	 * 单位行高度
	 */
	public final static int UNIT_HEIGHT = 300;
	/**
	 * 列名行高度
	 */
	public final static int COLUMNNAME_HEIGHT = 300;
	/**
	 * 内容高度
	 */
	public final static int BODY_HEIGHT = 400;
	/**
	 * 父列名高度
	 */
	public final static int COLUMNNAME_PARENT=300;
	/**
	 * 子列名高度
	 */
	public final static int COLUMNNAME_CHILD=300;

	/**
	 * 标题样式
	 * 
	 * @return
	 */
	public static WritableCellFormat getHeadStyle() {
		return setBoldTemplate(WritableFont.TIMES, 20, null,
				Alignment.CENTRE, VerticalAlignment.BOTTOM, Border.ALL,
				BorderLineStyle.NONE, Colour.WHITE);

	}

	/**
	 * 列名称样式
	 * 
	 * @return
	 */

	public static WritableCellFormat getColumnNameStyle() {
		// 字体为TIMES，字号16，加粗显示
		WritableFont font = new WritableFont(WritableFont.TIMES, 10,
				WritableFont.BOLD);
		WritableCellFormat format = new WritableCellFormat(font);

		try {
			// 把水平对齐方式指定为居中
			format.setAlignment(jxl.format.Alignment.CENTRE);

			// 把垂直对齐方式指定为居中
			format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			// 设置顶部
			format.setBorder(jxl.format.Border.TOP, BorderLineStyle.HAIR,
					jxl.format.Colour.WHITE);
			// 设置左边
			format.setBorder(jxl.format.Border.LEFT, BorderLineStyle.HAIR,
					jxl.format.Colour.WHITE);
			// 设置右边
			format.setBorder(jxl.format.Border.RIGHT, BorderLineStyle.HAIR,
					jxl.format.Colour.WHITE);
			// 设置底部
			format.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.DASHED,
					jxl.format.Colour.WHITE);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return format;

	}

	/**
	 * 列名称样式
	 * 
	 * @return
	 */

	public static WritableCellFormat getColumnParentNameStyle() {
		// 字体为TIMES，字号16，加粗显示
		WritableFont font = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.BOLD);
		WritableCellFormat format = new WritableCellFormat(font);

		try {
			// 把水平对齐方式指定为居中
			format.setAlignment(jxl.format.Alignment.CENTRE);

			// 把垂直对齐方式指定为居中
			format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			// 设置顶部
			format.setBorder(jxl.format.Border.TOP, BorderLineStyle.HAIR,
					jxl.format.Colour.WHITE);
			// 设置左边
			format.setBorder(jxl.format.Border.LEFT, BorderLineStyle.HAIR,
					jxl.format.Colour.WHITE);
			// 设置右边
			format.setBorder(jxl.format.Border.RIGHT, BorderLineStyle.HAIR,
					jxl.format.Colour.WHITE);
			// 设置底部
			format.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.THIN,
					jxl.format.Colour.BLACK);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return format;

	}
	/**
	 * 列名称样式
	 * 
	 * @return
	 */

	public static WritableCellFormat getColumnChildNameStyle() {
		// 字体为TIMES，字号16，加粗显示
		WritableFont font = new WritableFont(WritableFont.TIMES, 10,
				WritableFont.BOLD);
		WritableCellFormat format = new WritableCellFormat(font);

		try {
			// 把水平对齐方式指定为居中
			format.setAlignment(jxl.format.Alignment.CENTRE);

			// 把垂直对齐方式指定为居中
			format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			// 设置顶部
			format.setBorder(jxl.format.Border.TOP, BorderLineStyle.HAIR,
					jxl.format.Colour.WHITE);
			// 设置左边
			format.setBorder(jxl.format.Border.LEFT, BorderLineStyle.HAIR,
					jxl.format.Colour.WHITE);
			// 设置右边
			format.setBorder(jxl.format.Border.RIGHT, BorderLineStyle.HAIR,
					jxl.format.Colour.WHITE);
			// 设置底部
			format.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.DASHED,
					jxl.format.Colour.WHITE);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return format;

	}
	/**
	 * 内容样式
	 * 
	 * @return
	 */
	public static WritableCellFormat getBodyStyle() {
		return setThinTemplate(WritableFont.TIMES, 9, null,
				Alignment.CENTRE, VerticalAlignment.CENTRE,
				Border.ALL,BorderLineStyle.HAIR, Colour.WHITE);
	}

	/**
	 * 第二行日期样式
	 * 
	 * @return
	 */
	public static WritableCellFormat getDateStyle() {

		return setThinTemplate(WritableFont.TIMES, 9, null,
				Alignment.CENTRE, VerticalAlignment.CENTRE,
				Border.ALL,BorderLineStyle.HAIR, Colour.WHITE);

	}

	/**
	 * 单位行样式：“单位：元”
	 * 
	 * @return
	 */
	public static WritableCellFormat getUnitStyle() {
		return setThinTemplate(WritableFont.TIMES, 9, null,
				Alignment.RIGHT, VerticalAlignment.CENTRE,
				Border.ALL,BorderLineStyle.HAIR, Colour.WHITE);

	}
	/**
	 * 单位行样式：“单位名称”
	 * 
	 * @return
	 */
	public static WritableCellFormat getUnitNameStyle() {
		return setThinTemplate(WritableFont.TIMES, 9, null,
				Alignment.RIGHT, VerticalAlignment.CENTRE,
				Border.ALL,BorderLineStyle.HAIR, Colour.WHITE);

	}
	/**
	 * 单位行样式：“单位名称内容”
	 * 
	 * @return
	 */
	public static WritableCellFormat getUnitNameContentStyle() {
		return setThinTemplate(WritableFont.TIMES, 9, null,
				Alignment.LEFT, VerticalAlignment.CENTRE,
				Border.ALL,BorderLineStyle.HAIR, Colour.WHITE);

	}
	/**
	 * 
	 * @param fn
	 *            字体名称
	 * @param ps
	 *            字体大小
	 * @param bs
	 *            设置粗体
	 * @param a
	 *            设置水平对其方式
	 * @param va
	 *            设置垂直对其方式
	 * @param b
	 *            设置表格的范围，left，right，top，bottom，all
	 * @param ls
	 *            表格边框的样式
	 * @param c
	 *            表格边框的颜色
	 * @return
	 * @throws Exception
	 */
	public static WritableCellFormat setBoldTemplate(FontName fn, int ps,
			BoldStyle bs, Alignment a, VerticalAlignment va, Border b,
			BorderLineStyle ls, Colour c) {
		WritableFont font = new WritableFont(fn, ps, WritableFont.BOLD);
		WritableCellFormat format = new WritableCellFormat(font);
		try {
			format.setAlignment(a);
			format.setVerticalAlignment(va);
			format.setBorder(b, ls, c);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return format;
	}

	/**
	 * 
	 * @param fn
	 *            字体名称
	 * @param ps
	 *            字体大小
	 * @param bs
	 *            设置粗体
	 * @param a
	 *            设置水平对其方式
	 * @param va
	 *            设置垂直对其方式
	 * @param b
	 *            设置表格的范围，left，right，top，bottom，all
	 * @param ls
	 *            表格边框的样式
	 * @param c
	 *            表格边框的颜色
	 * @return
	 * @throws Exception
	 */
	public static WritableCellFormat setThinTemplate(FontName fn, int ps,
			BoldStyle bs, Alignment a, VerticalAlignment va, Border b,
			BorderLineStyle ls, Colour c) {
		WritableFont font = new WritableFont(fn, ps, WritableFont.NO_BOLD);
		WritableCellFormat format = new WritableCellFormat(font);
		try {
			format.setAlignment(a);
			format.setVerticalAlignment(va);
			format.setBorder(b, ls, c);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return format;
	}
}
