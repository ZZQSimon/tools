package cn.com.easyerp.framework.i18n;

/**
 * 请引用下面常量时，根据注释中的旧key查找，并核对一下国际化内容后再使用。
 */
public interface I18N {

    public String SUCCESS = "success";
    public String FAILED = "failed";
    public String UNKNOWNCODE = "unknowncode";

    public String INFO_BACKUP_BEFORETRANS = "info.backup.beforetrans";
    public String INFO_TRANSFER_FAILED = "info.transfer.failed";
    public String INFO_BACKUP_FAILED_TRANS = "info.backup.failed.trans";

    public String ERROR_BACKUP_BEFORETRANS = "error.backup.beforetrans";
    public String ERROR_BACKUP_FAILED_BEFORETRANS = "error.backup.failed.beforetrans";
    public String ERROR_BACKUP_FAILED_TRANS = "error.backup.failed.trans";
    public String ERROR_CONVER_XMLREADER = "error.conver.xmlreader";
    public String ERROR_CONVER_FILENOTFOUND = "error.conver.filenotfound";
    public String ERROR_CONVER_IO = "error.conver.io";
    public String ERROR_CONVER_JAXB = "error.conver.jaxb";
    public String ERROR_DB_GETPO = "error.db.getpo";
    public String ERROR_DB_INSERT = "error.db.insert";
    public String ERROR_EBCDIC_FILENOTFOUND = "error.ebcdic.filenotfound";
    public String ERROR_EBCDIC_IO = "error.ebcdic.io";
    public String ERROR_EBCDIC_WRITE = "error.ebcdic.write";
    public String ERROR_EBCDICTXT_LENGTH = "error.ebcdictxt.length";
    public String ERROR_ENCODING_UNSUPPORTED = "error.encoding.unsupported";
    public String ERROR_FORMAT_XMLDATE = "error.format.xmldate";
    public String ERROR_FORMAT_XMLSHIPMENTID = "error.format.xmlshipmentid";
    public String ERROR_INVOKE_ILLEGALACCESS = "error.invoke.illegalaccess";
    public String ERROR_INVOKE_INVOCATIONTARGET = "error.invoke.invocationtarget";
    public String ERROR_INVOKE_NOSUCHMETHOD = "error.invoke.nosuchmethod";
    public String ERROR_PARAM_FILECREATE = "error.param.filecreate";
    public String ERROR_PARAM_FILEPATH = "error.param.filepath";
    public String ERROR_PO_CONTENT_EMPTY = "error.po.content.empty";
    public String ERROR_POPOA_ASN_NODATA = "error.popoa.asn.nodata";
    public String ERROR_READ_FAILED_TRANS = "error.read.failed.trans";
    public String ERROR_STOCK_FILENOTFOUND = "error.stock.filenotfound";
    public String ERROR_XML_CONTENT = "error.xml.content";
    public String ERROR_XML_FILENOTFOUND = "error.xml.filenotfound";
    public String ERROR_XML_TRANS = "error.xml.trans";

    public String ERROR_MAIL_SENT_FAILED = "error.mail.sent.failed";
    public String ERROR_DATE_J2W05 = "error.date.j2w05";
    public String ERROR_DATE_J2W11 = "error.date.j2w11";
    public String ERROR_TASK_UNCATCH = "error.task.uncatch";
    public String ERROR_XML_ASNXML_PONO_NOT8 = "error.xml.asnxml.pono.not8";

    public String ERROR_ERR01 = "error.err01";
    public String ERROR_SFTP01 = "error.sftp01";
    public String ERROR_DOWNLOAD01 = "error.download01";
    public String ERROR_DOWNLOAD02 = "error.download02";
    public String ERROR_DOWNLOAD03 = "error.download03";
    public String ERROR_FAILLOG01 = "error.faillog01";
    public String ERROR_FAILLOG02 = "error.faillog02";
    public String ERROR_FAILLOG03 = "error.faillog03";
    public String ERROR_DELETE01 = "error.delete01";
    public String ERROR_LISTFILES01 = "error.listfiles01";
    public String ERROR_SFTPCD01 = "error.sftpcd01";
    public String ERROR_SFTPCD02 = "error.sftpcd02";
    public String ERROR_FTPCD01 = "error.ftpcd01";
    public String ERROR_COPYFILE01 = "error.copyfile01";
    public String ERROR_DISCONNECT01 = "error.disconnect01";
    public String ERROR_FTP01 = "error.ftp01";
    public String ERROR_FTP02 = "error.ftp02";
    public String ERROR_FTP03 = "error.ftp03";
    public String ERROR_FTP04 = "error.ftp04";
    public String ERROR_FTP05 = "error.ftp05";
    public String ERROR_FTP06 = "error.ftp06";
    public String ERROR_UPLOAD01 = "error.upload01";
    public String ERROR_UPLOAD02 = "error.upload02";

    public String INFO_INFO001 = "info.info001";
    public String INFO_SFTP01 = "info.sftp01";
    public String INFO_DOWNLOAD01 = "info.download01";
    public String INFO_DELETE01 = "info.delete01";
    public String INFO_DELETE02 = "info.delete02";
    public String INFO_DELETE03 = "info.delete03";
    public String INFO_LOG01 = "info.log01";
    public String INFO_LOG02 = "info.log02";
    public String INFO_LOG03 = "info.log03";
    public String INFO_LOG04 = "info.log04";
    public String INFO_LOG05 = "info.log05";
    public String INFO_LOG06 = "info.log06";
    public String INFO_LOG07 = "info.log07";
    public String INFO_LOG08 = "info.log08";
    public String INFO_LOG09 = "info.log09";
    public String INFO_LOG10 = "info.log10";
    public String INFO_LOG11 = "info.log11";
    public String INFO_LOG12 = "info.log12";
    public String INFO_LOG13 = "info.log13";
    public String INFO_LOG14 = "info.log14";
    public String INFO_LOG15 = "info.log15";
    public String INFO_LOG16 = "info.log16";
    public String INFO_LOG17 = "info.log17";
    public String INFO_LOG18 = "info.log18";
    public String INFO_LOG19 = "info.log19";
    public String INFO_LOG191 = "info.log191";
    public String INFO_LOG20 = "info.log20";
    public String INFO_LOG21 = "info.log21";
    public String INFO_LOG22 = "info.log22";
    public String INFO_LOG23 = "info.log23";
    public String INFO_MAILTITLE01 = "info.mailtitle01";
    public String INFO_MAILTITLE02 = "info.mailtitle02";
    public String INFO_MAILTITLE03 = "info.mailtitle03";
    public String INFO_MAILTITLE04 = "info.mailtitle04";
    public String INFO_MAILTITLE05 = "info.mailtitle05";
    public String INFO_MAILTITLE06 = "info.mailtitle06";
    public String INFO_MAILTITLE07 = "info.mailtitle07";
    public String INFO_MAILTITLE08 = "info.mailtitle08";
    public String INFO_MAILTITLE09 = "info.mailtitle09";
    public String INFO_MAILTITLE10 = "info.mailtitle10";
    public String INFO_MAILTITLE11 = "info.mailtitle11";
    public String INFO_MAILTITLE12 = "info.mailtitle12";

    public String ERROR_ASN_NOPO_NOLINES = "error.asn.nopo.noline";
    public String ERROR_ASN_NOLINES = "error.asn.noline";
    public String ERROR_ASN_NOPO = "error.asn.nopo";

}
