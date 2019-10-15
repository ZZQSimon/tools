package cn.com.easyerp.clearI18N;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClearI18N extends JFrame implements ActionListener {
    private static final long serialVersionUID = 2246102659827094960L;
    private StringBuffer jsI18ns = new StringBuffer();
    private List<String> likeI18ns = new ArrayList<>();

    JButton exec = null;
    JTextField way = null;
    JTextArea jsI18N = null;
    JPanel jp1;
    JPanel jp2;
    JPanel jp3 = null;

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "确定") {
            String area = this.way.getText();
            iteratorPath(area);
            String i18nSQL = i18nSQL();
            this.jsI18N.setText(i18nSQL);
        }
    }

    private String i18nSQL() {
        String sql = "SELECT * FROM c_international WHERE NOT EXISTS  (SELECT 1 FROM c_auto_gen WHERE c_auto_gen.international_id = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_column WHERE c_column.international_id = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_dictionary WHERE c_dictionary.id_international = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_dictionary WHERE c_dictionary.key_international = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_flow_event WHERE c_flow_event.international_id = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_menu WHERE c_menu.international_id = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_report WHERE c_report.international_id = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_table WHERE c_table.international_id = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_table_action WHERE c_table_action.action_name_international = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_table_action_event WHERE c_table_action_event.event_name = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_table_action_prerequistie WHERE c_table_action_prerequistie.violate_msg_international_id = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_table_check WHERE c_table_check.error_msg_id = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_time_task WHERE c_time_task.international_id = c_international.international_id) AND NOT EXISTS (SELECT 1 FROM c_url WHERE c_url.name = c_international.international_id)";
        String inStr = this.jsI18ns.toString();
        if (inStr.length() > 1)
            sql = sql + " AND c_international.international_id NOT IN (" + inStr.substring(1, inStr.length()) + ")";
        if (this.likeI18ns != null)
            for (int i = 0; i < this.likeI18ns.size(); i++) {
                if (this.likeI18ns.get(i) != null && ((String) this.likeI18ns.get(i)).length() > 0) {
                    sql = sql + " AND c_international.international_id NOT LIKE '%" + (String) this.likeI18ns.get(i)
                            + "%' ";
                }
            }
        return sql;
    }

    public ClearI18N() {
        this.exec = new JButton("确定");
        this.way = new JTextField(30);
        this.jsI18N = new JTextArea(50, 50);
        this.exec.addActionListener(this);
        this.jp1 = new JPanel();
        this.jp2 = new JPanel();
        this.jp3 = new JPanel();
        this.jp1.add(this.way);
        this.jp2.add(this.exec);
        this.jp2.add(this.jsI18N);
        add(this.jp1);
        add(this.jp2);
        add(this.jp3);
        // TODO
        // setLayout(new GridLayout(5, true));
        setLayout(new GridLayout(5, 5));
        setTitle("鏌ヨ鍥介檯鍖�");
        setSize(600, 400);
        setLocation(800, 400);
        setDefaultCloseOperation(3);
        setVisible(true);
        setResizable(true);
    }

    private String iteratorPath(String dir) {
        File or = new File(dir);
        File[] files = or.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    matchI18N(file);
                } else if (file.isDirectory()) {
                    iteratorPath(file.getAbsolutePath());
                }
            }
        }
        return null;
    }

    private String matchI18N(File file) {
        String fileName = file.getName();
        if (fileName.indexOf(".js") != -1) {
            matchJsFile(file);
        } else if (fileName.indexOf(".java") != -1) {
            matchJavaFile(file);
        } else if (fileName.indexOf(".vm") != -1) {
            matchVMFile(file);
        }
        return null;
    }

    private String matchJsFile(File file) {
        try {
            String result = "";
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                result = result + line;
            }
            String reg = "msg\\([\\'|\\\"](.*?)[\\'|\\\"]?\\)";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(result);
            while (m.find()) {
                String value = m.group(1);
                if (value.indexOf("'") > 1) {
                    this.likeI18ns.add(value.substring(0, value.indexOf("'") - 1));
                    continue;
                }
                this.jsI18ns.append(",'" + value + "'");
            }
        } catch (Exception e) {
        }
        return null;
    }

    private String matchJavaFile(File file) {
        matchStr(file);
        return null;
    }

    private String matchVMFile(File file) {
        try {
            String result = "";
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                result = result + line;
            }
            String reg = "dx.msg\\([\\\"|'](.*?)[\\\"|']?\\)";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(result);
            while (m.find()) {
                String value = m.group(1);
                if (value.indexOf(",") > 1) {
                    this.likeI18ns.add(value.substring(0, value.indexOf(",") - 1));
                    continue;
                }
                if (value.indexOf("'") > 1) {
                    this.likeI18ns.add(value.substring(0, value.indexOf("'") - 1));
                    continue;
                }
                this.jsI18ns.append(",'" + value + "'");
            }
        } catch (Exception e) {

        }
        return null;
    }

    private void matchStr(File file) {
        try {
            String result = "";
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                result = result + line;
            }
            String reg = "\\\"(.*?)\\\"";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(result);
            while (m.find()) {
                String value = m.group(1);
                this.likeI18ns.add(value);
            }
        } catch (Exception e) {
        }
    }

    private void matchApplication(File file) {
        try {
            String result = "";
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                result = result + line;
            }
            String reg = "ApplicationException\\(\\\"(.*?)\\\"?\\)";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(result);
            while (m.find()) {
                String value = m.group(1);
                if (value.indexOf("\"") > 1) {
                    this.likeI18ns.add(value.substring(0, value.indexOf("\"") - 1));
                    continue;
                }
                this.jsI18ns.append(",'" + value + "'");
            }
        } catch (Exception e) {
        }
    }

    private void matchActionResult(File file) {
        try {
            String result = "";
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                result = result + line;
            }
            String reg = "new ActionResult\\((true|false)\\s*\\,\\s*\\\"(.*?)\\\"?\\)";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(result);
            while (m.find()) {
                String value2 = m.group(2);
                this.jsI18ns.append(",'" + value2 + "'");
            }
        } catch (Exception e) {
        }
    }

    private void matchError(File file) {
        try {
            String result = "";
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                result = result + line;
            }
            String reg = "error\\(\\\"(.*?)\\\"?\\)";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(result);
            while (m.find()) {
                String value = m.group(1);
                if (value.indexOf("\"") > 1) {
                    this.likeI18ns.add(value.substring(0, value.indexOf("\"") - 1));
                    continue;
                }
                this.jsI18ns.append(",'" + value + "'");
            }
        } catch (Exception e) {
        }
    }

    private void matchMessageText(File file) {
        try {
            String result = "";
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                result = result + line;
            }
            String reg = "getMessageText\\(\\\"(.*?)\\\"?\\)";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(result);
            while (m.find()) {
                String value = m.group(1);
                if (value.indexOf("\"") > 1) {
                    this.likeI18ns.add(value.substring(0, value.indexOf("\"") - 1));
                    continue;
                }
                this.jsI18ns.append(",'" + value + "'");
            }
        } catch (Exception e) {
        }
    }

    private void matchFormat(File file) {
        try {
            String result = "";
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                result = result + line;
            }
            String reg = "format\\(\\\"(.*?)\\\"?\\)";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(result);
            while (m.find()) {
                String value = m.group(1);
                if (value.indexOf("'") > 1) {
                    this.likeI18ns.add(value.substring(0, value.indexOf("\"") - 1));
                    continue;
                }
                this.jsI18ns.append(",'" + value + "'");
            }
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        Properties prop = System.getProperties();
        System.out.println("\n褰撳墠鐢ㄦ埛鍚�:" + prop.getProperty("user.name"));
        System.out.println("\n褰撳墠绯荤粺:" + prop.getProperty("os.name"));
        String str = "'${status}'!='5' && dxf.date(${scheduled_date})<dxf.date()";
        String reg = "dxf.([a-zA-Z]*)\\(\\\\?['\"]?(.*?)\\\\?['\"]?\\)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String aaa = matcher.group(0);
            String bbb = matcher.group(1);
        }
        String aaa = "1";
    }
}